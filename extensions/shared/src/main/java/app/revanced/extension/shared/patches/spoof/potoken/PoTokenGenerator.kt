package app.revanced.extension.shared.patches.spoof.potoken

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.webkit.CookieManager
import app.revanced.extension.shared.utils.Logger.printDebug
import app.revanced.extension.shared.utils.Utils.getActivity
import kotlinx.coroutines.runBlocking

class PoTokenGenerator {
    private val supportsWebView by lazy { runCatching { CookieManager.getInstance() }.isSuccess }

    private object PoTokenGenLock
    private var visitorData: String? = null
    private var webPoTokenGenerator: PoTokenWebView? = null

    fun getPoToken(
        videoId: String, 
        mActivity: Activity? = getActivity(), 
        forceCreate: Boolean = false
    ): String {
        if (!supportsWebView) {
            throw PoTokenException("WebView is not supported. Cannot obtain PoToken")
        }

        val (poTokenGenerator, hasBeenRecreated) = synchronized(PoTokenGenLock) {
            val shouldRecreate = forceCreate || webPoTokenGenerator == null || webPoTokenGenerator!!.isExpired()

            if (shouldRecreate) {
                runBlocking {
                    // close the current webPoTokenGenerator on the main thread
                    webPoTokenGenerator?.let { Handler(Looper.getMainLooper()).post { it.close() } }

                    // create a new poTokenGenerator with Application Context
                    webPoTokenGenerator = PoTokenWebView.newPoTokenGenerator(mActivity!!.getApplicationContext())
                }
            }

            return@synchronized Pair(
                webPoTokenGenerator!!,
                shouldRecreate
            )
        }

        val playerPot = try {
            runBlocking {
                poTokenGenerator.generatePoToken(videoId)
            }
        } catch (throwable: Throwable) {
            if (hasBeenRecreated) {
                // the poTokenGenerator has just been recreated (and possibly this is already the
                // second time we try), so there is likely nothing we can do
                throw throwable
            } else {
                // retry, this time recreating the [webPoTokenGenerator] from scratch;
                // this might happen for example if the app goes in the background and the WebView
                // content is lost
                printDebug { "Failed to obtain poToken, retrying" }
                return getPoToken(videoId = videoId, mActivity = mActivity, forceCreate = true)
            }
        }

        printDebug { "PoToken for video $videoId: $playerPot" }

        return playerPot
    }
}