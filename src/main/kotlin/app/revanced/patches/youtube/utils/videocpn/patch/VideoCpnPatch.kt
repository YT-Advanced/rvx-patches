package app.revanced.patches.youtube.utils.videocpn.patch

import app.revanced.extensions.exception
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.patches.youtube.utils.videocpn.fingerprint.OrganicPlaybackContextModelFingerprint

object VideoCpnPatch : BytecodePatch(
    setOf(OrganicPlaybackContextModelFingerprint)
) {
    private lateinit var insertMethod: MutableMethod

    override fun execute(context: BytecodeContext) {

        insertMethod = OrganicPlaybackContextModelFingerprint.result?.mutableMethod
            ?: throw OrganicPlaybackContextModelFingerprint.exception

    }

    fun injectCall(
        methodDescriptor: String
    ) {
        insertMethod.addInstructions(
            2,
            "invoke-static {p1,p2}, $methodDescriptor"
        )
    }
}

