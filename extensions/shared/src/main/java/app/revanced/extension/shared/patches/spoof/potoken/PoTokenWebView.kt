package app.revanced.extension.shared.patches.spoof.potoken

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.annotation.MainThread
import app.revanced.extension.shared.requests.Requester.parseStringAndDisconnect
import app.revanced.extension.shared.requests.Route.CompiledRoute
import app.revanced.extension.shared.utils.Logger
import app.revanced.extension.shared.utils.ResourceUtils.openRawResource
import kotlinx.coroutines.*
import java.nio.charset.StandardCharsets
import java.time.Instant
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

// import androidx.webkit.WebSettingsCompat
// import androidx.webkit.WebViewFeature

class PoTokenWebView private constructor(
    context: Context,
    private val generatorContinuation: Continuation<PoTokenWebView>
) {
    private val webView = WebView(context)
    private val poTokenContinuations = mutableMapOf<String, Continuation<String>>()
    private lateinit var expirationInstant: Instant

    //region Initialization
    init {
        val webViewSettings = webView.settings

        // Uncomment these line in case the application has to support Android 5-7
        // if (WebViewFeature.isFeatureSupported(WebViewFeature.SAFE_BROWSING_ENABLE)) {
        //     WebSettingsCompat.setSafeBrowsingEnabled(webViewSettings, false)
        // }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            webViewSettings.safeBrowsingEnabled = false
        }
        //noinspection SetJavaScriptEnabled we want to use JavaScript!
        webViewSettings.javaScriptEnabled = true
        webViewSettings.userAgentString = USER_AGENT
        webViewSettings.blockNetworkLoads = true // the WebView does not need internet access

        // so that we can run async functions and get back the result
        webView.addJavascriptInterface(this, JS_INTERFACE)
     }

    /**
     * Must be called right after instantiating [PoTokenWebView] to perform the actual
     * initialization. This will asynchronously go through all the steps needed to load BotGuard,
     * run it, and obtain an `integrityToken`.
     */
    private fun loadHtmlAndObtainBotguard() {
        Logger.printDebug { "loadHtmlAndObtainBotguard() called" }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val html = openRawResource("po_token").bufferedReader().use { it.readText() }
                withContext(Dispatchers.Main) {
                    webView.loadDataWithBaseURL(
                        "https://www.youtube.com",
                        html.replaceFirst(
                            "</script>",
                            // calls downloadAndRunBotguard() when the page has finished loading
                            "\n$JS_INTERFACE.downloadAndRunBotguard()</script>"
                        ),
                        "text/html",
                        "utf-8",
                        null,
                    )
                }
            } catch (e: Exception) {
                onInitializationError(e)
            }
        }
    }

    /**
     * Called during initialization by the JavaScript snippet appended to the HTML page content in
     * [loadHtmlAndObtainBotguard] after the WebView content has been loaded.
     */
    @JavascriptInterface
    fun downloadAndRunBotguard() {
        Logger.printDebug { "downloadAndRunBotguard() called" }

        CoroutineScope(Dispatchers.IO).launch {
            val responseBody = makeBotguardServiceRequest(
                PoTokenRoutes.CREATE_CHALLENGE, 
                "[ \"$REQUEST_KEY\" ]"
            )
            val parsedChallengeData = parseChallengeData(responseBody)
            withContext(Dispatchers.Main) {
                webView.evaluateJavascript(
                    """try {
                             data = $parsedChallengeData
                             runBotGuard(data).then(function (result) {
                                 this.webPoSignalOutput = result.webPoSignalOutput
                                 $JS_INTERFACE.onRunBotguardResult(result.botguardResponse)
                             }, function (error) {
                                 $JS_INTERFACE.onJsInitializationError(error + "\n" + error.stack)
                             })
                         } catch (error) {
                             $JS_INTERFACE.onJsInitializationError(error + "\n" + error.stack)
                         }""",
                    null
                )
            }
        }
    }

    /**
     * Called during initialization by the JavaScript snippets from either
     * [downloadAndRunBotguard] or [onRunBotguardResult].
     */
    @JavascriptInterface
    fun onJsInitializationError(error: String) {
        Logger.printException { "Initialization error from JavaScript: $error" }

        onInitializationError(PoTokenException(error))
    }

    /**
     * Called during initialization by the JavaScript snippet from [downloadAndRunBotguard] after
     * obtaining the BotGuard execution output [botguardResponse].
     */
    @JavascriptInterface
    @TargetApi(26)
    fun onRunBotguardResult(botguardResponse: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = makeBotguardServiceRequest(
                PoTokenRoutes.GENERATE_INTEGRITY_TOKEN, 
                "[ \"$REQUEST_KEY\", \"$botguardResponse\" ]"
            )
            val (integrityToken, expirationTimeInSeconds) = parseIntegrityTokenData(response)

            // leave 10 minutes of margin just to be sure
            expirationInstant = Instant.now().plusSeconds(expirationTimeInSeconds - 600)

            withContext(Dispatchers.Main) {
                webView.evaluateJavascript(
                    "this.integrityToken = $integrityToken"
                ) {
                    Logger.printDebug { "initialization finished, expiration=${expirationTimeInSeconds}s" }

                    generatorContinuation.resume(this@PoTokenWebView)
                }
            }
        }
    }
    //endregion

    //region Obtaining poTokens
    suspend fun generatePoToken(identifier: String): String {
        Logger.printDebug { "generatePoToken() called with identifier $identifier" }

        return suspendCancellableCoroutine { continuation ->
            poTokenContinuations[identifier] = continuation
            val u8Identifier = stringToU8(identifier)

            Handler(Looper.getMainLooper()).post {
                webView.evaluateJavascript(
                    """try {
                        identifier = "$identifier"
                        u8Identifier = $u8Identifier
                        poTokenU8 = obtainPoToken(webPoSignalOutput, integrityToken, u8Identifier)
                        poTokenU8String = ""
                        for (i = 0; i < poTokenU8.length; i++) {
                            if (i != 0) poTokenU8String += ","
                            poTokenU8String += poTokenU8[i]
                        }
                        $JS_INTERFACE.onObtainPoTokenResult(identifier, poTokenU8String)
                    } catch (error) {
                        $JS_INTERFACE.onObtainPoTokenError(identifier, error + "\n" + error.stack)
                    }""",
                ) {}
            }
        }
    }

    /**
     * Called by the JavaScript snippet from [generatePoToken] when an error occurs in calling the
     * JavaScript `obtainPoToken()` function.
     */
    @JavascriptInterface
    fun onObtainPoTokenError(identifier: String, error: String) {
        Logger.printException { "obtainPoToken error from JavaScript: $error" }

        poTokenContinuations.remove(identifier)?.resumeWithException(PoTokenException(error))
    }

    /**
     * Called by the JavaScript snippet from [generatePoToken] with the original identifier and the
     * result of the JavaScript `obtainPoToken()` function.
     */
    @JavascriptInterface
    fun onObtainPoTokenResult(identifier: String, poTokenU8: String) {
        Logger.printDebug { "Generated poToken (before decoding): identifier=$identifier poTokenU8=$poTokenU8" }

        val poToken = try {
            u8ToBase64(poTokenU8)
        } catch (t: Throwable) {
            poTokenContinuations.remove(identifier)?.resumeWithException(t)
            return
        }

        Logger.printDebug { "Generated poToken: identifier=$identifier poToken=$poToken" }
        poTokenContinuations.remove(identifier)?.resume(poToken)
    }

    @TargetApi(26)
    fun isExpired(): Boolean {
        return Instant.now().isAfter(expirationInstant)
    }
    //endregion

    //region Utils
    /**
     * Makes a POST request with the given [route] and [data].
     * This is supposed to be used only during initialization. Returns the response body
     * as a String if the response is successful.
     */
    private suspend fun makeBotguardServiceRequest(route: CompiledRoute, data: String): String =
        withContext(Dispatchers.IO) {
            botguardRequest(route, data).getOrThrow()
        }

    /**
     * Handles any error happening during initialization, releasing resources and sending the error
     * to [generatorContinuation].
     */
    private fun onInitializationError(error: Throwable) {
        CoroutineScope(Dispatchers.Main).launch {
            close()
            generatorContinuation.resumeWithException(error)
        }
    }

    /**
     * Releases all [webView] resources.
     */
    @MainThread
    fun close() {
        webView.clearHistory()
        // clears RAM cache and disk cache (globally for all WebViews)
        webView.clearCache(true)

        // ensures that the WebView isn't doing anything when destroying it
        webView.loadUrl("about:blank")

        webView.onPause()
        webView.removeAllViews()
        webView.destroy()
    }
    //endregion

    companion object {
        private const val USER_AGENT = "Mozilla/5.0 (iPad; CPU OS 16_7_10 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.6 Mobile/15E148 Safari/604.1,gzip(gfe)"
        private const val REQUEST_KEY = "O43z0dpjhgX20SCx4KAo"
        private const val JS_INTERFACE = "PoTokenWebView"

        private fun botguardRequest(
            route: CompiledRoute,
            data: String
        ): Result<String> = runCatching {
            val connection = PoTokenRoutes.getBotGuardConnectionFromRoute(route)

            val body = data.toByteArray(StandardCharsets.UTF_8)
            connection.setFixedLengthStreamingMode(body.size)
            connection.outputStream.use { os ->
                os.write(body)
            }

            connection.connect()
            return@runCatching parseStringAndDisconnect(connection)
        }

        suspend fun newPoTokenGenerator(context: Context): PoTokenWebView {
            return suspendCancellableCoroutine { continuation ->
                Handler(Looper.getMainLooper()).post {
                    val poTokenWebView = PoTokenWebView(context, continuation)
                    poTokenWebView.loadHtmlAndObtainBotguard()
                }
            }
        }


    }
}


class PoTokenException(message: String) : Exception(message)