package app.revanced.extension.shared.patches.spoof.potoken

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.webkit.CookieManager
import app.revanced.extension.shared.patches.client.YouTubeWebClient
import app.revanced.extension.shared.patches.spoof.potoken.PoTokenResult
import app.revanced.extension.shared.patches.spoof.requests.PlayerRoutes
import app.revanced.extension.shared.requests.Requester
import app.revanced.extension.shared.requests.Route.CompiledRoute
import app.revanced.extension.shared.utils.Logger
import kotlinx.coroutines.runBlocking
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.SocketTimeoutException

class PoTokenGenerator {
    private val supportsWebView by lazy { runCatching { CookieManager.getInstance() }.isSuccess }

    private object PoTokenGenLock
    private var visitorData: String? = null
    private var streamingPot: String? = null
    private var poTokenGenerator: PoTokenWebView? = null

    private fun sendVisitorDataRequest(): JSONObject? {
        val startTime = System.currentTimeMillis()
        val clientType = YouTubeWebClient.ClientType.MWEB
        val clientTypeName = clientType.name
        Logger.printDebug { "Fetching visitor data using client: $clientTypeName" }

        try {
            val connection = PlayerRoutes.getPlayerResponseConnectionFromRoute(
                PlayerRoutes.GET_VISITOR_DATA,
                clientType
            )
            val requestBody = PlayerRoutes.createVisitorDataInnertubeBody(clientType)

            connection.setFixedLengthStreamingMode(requestBody.size)
            connection.outputStream.write(requestBody)

            val responseCode = connection.responseCode
            if (responseCode == 200) return Requester.parseJSONObject(connection)
            handleConnectionError(
                (clientTypeName + " not available with response code: "
                        + responseCode + " message: " + connection.responseMessage),
                null
            )
        } catch (ex: SocketTimeoutException) {
            handleConnectionError("Connection timeout", ex)
        } catch (ex: IOException) {
            handleConnectionError("Network error", ex)
        } catch (ex: Exception) {
            Logger.printException({ "sendWebRequest failed" }, ex)
        } finally {
            Logger.printDebug { "Fetch visitor data " + " took: " + (System.currentTimeMillis() - startTime) + "ms" }
        }

        return null
    }

    private fun parseVisitorData(visitorDataJson: JSONObject) : String? {
        try {
            return visitorDataJson.getJSONObject("responseContext").getString("visitorData")   
        } catch (ex: JSONException) {
            Logger.printException(
                { "Fetch failed while processing response data for response: $visitorDataJson" },
                ex
            )
        }

        return null
    }

    private fun fetchVisitorData(): String? {
        val visitorDataJson = sendVisitorDataRequest()
        if (visitorDataJson != null) {
            return parseVisitorData(visitorDataJson)
        }

        return null
    }

    fun getPoTokenResult(activity: Activity, forceCreate: Boolean = false): PoTokenResult? {
        if (!supportsWebView) {
            Logger.printDebug { "WebView is not supported. Cannot obtain PoToken" }
            return null
        }
        synchronized(PoTokenGenLock) {
            val shouldRecreate = forceCreate || poTokenGenerator == null || poTokenGenerator!!.isExpired()

            if (shouldRecreate) {
                visitorData = fetchVisitorData() ?: throw PoTokenException("Visitor data is null")

                runBlocking {
                    // close the current poTokenGenerator on the main thread
                    poTokenGenerator?.let { Handler(Looper.getMainLooper()).post { it.close() } }

                    // create a new poTokenGenerator
                    poTokenGenerator = PoTokenWebView.newPoTokenGenerator(activity)

                    // The streaming poToken needs to be generated exactly once before generating
                    // any other (player) tokens.
                    streamingPot = poTokenGenerator!!.generatePoToken(visitorData!!)
                }
            }
            Logger.printDebug { "PoToken: $streamingPot\nVisitor Data: $visitorData" }

            return PoTokenResult(visitorData!!, streamingPot!!)
        }
    }

    private fun handleConnectionError(toastMessage: String, ex: Exception?) {
        Logger.printInfo({ toastMessage }, ex)
    }
}