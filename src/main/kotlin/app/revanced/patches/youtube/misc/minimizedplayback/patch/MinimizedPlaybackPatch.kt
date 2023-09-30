package app.revanced.patches.youtube.misc.minimizedplayback.patch

import app.revanced.extensions.exception
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.patches.youtube.misc.minimizedplayback.fingerprints.KidsMinimizedPlaybackPolicyControllerFingerprint
import app.revanced.patches.youtube.misc.minimizedplayback.fingerprints.MinimizedPlaybackManagerFingerprint
import app.revanced.patches.youtube.misc.minimizedplayback.fingerprints.MinimizedPlaybackSettingsFingerprint
import app.revanced.patches.youtube.utils.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.utils.playertype.patch.PlayerTypeHookPatch
import app.revanced.patches.youtube.utils.resourceid.patch.SharedResourceIdPatch
import app.revanced.util.integrations.Constants.MISC_PATH
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

@Patch(
    name = "Enable minimized playback",
    description = "Enables minimized and background playback.",
    compatiblePackages = [
        CompatiblePackage(
            "com.google.android.youtube",
            [
                "18.22.37",
                "18.23.36",
                "18.24.37",
                "18.25.40",
                "18.27.36",
                "18.29.38",
                "18.30.37",
                "18.31.40",
                "18.32.39"
            ]
        )
    ]
    dependencies = [
        PlayerTypeHookPatch::class,
        IntegrationsPatch::class,
        SharedResourceIdPatch::class
    ]
)
@Suppress("unused")
object MinimizedPlaybackPatch : BytecodePatch(
    setOf(
        KidsMinimizedPlaybackPolicyControllerFingerprint,
        MinimizedPlaybackManagerFingerprint,
        MinimizedPlaybackSettingsFingerprint
    )
) {
    override fun execute(context: BytecodeContext) {
        val methods = arrayOf(
            KidsMinimizedPlaybackPolicyControllerFingerprint,
            MinimizedPlaybackManagerFingerprint,
            MinimizedPlaybackSettingsFingerprint
        ).map {
            it.result?.mutableMethod ?: throw it.exception
        }

        methods[0].hookKidsMiniPlayer()
        methods[1].hookMinimizedPlaybackManager()
        methods[2].hookMinimizedPlaybackSettings(context)

    }

    private companion object {
        const val INTEGRATIONS_METHOD_REFERENCE =
            "$MISC_PATH/MinimizedPlaybackPatch;->isPlaybackNotShort()Z"

        fun MutableMethod.hookKidsMiniPlayer() {
            addInstruction(
                0,
                "return-void"
            )
        }

        fun MutableMethod.hookMinimizedPlaybackManager() {
            addInstructions(
                0, """
                    invoke-static {}, $INTEGRATIONS_METHOD_REFERENCE
                    move-result v0
                    return v0
                    """
            )
        }

        fun MutableMethod.hookMinimizedPlaybackSettings(
            context: BytecodeContext
        ) {
            val booleanCalls = implementation!!.instructions.withIndex()
                .filter { ((it.value as? ReferenceInstruction)?.reference as? MethodReference)?.returnType == "Z" }

            val booleanIndex = booleanCalls.elementAt(1).index
            val booleanMethod =
                context.toMethodWalker(this)
                    .nextMethod(booleanIndex, true)
                    .getMethod() as MutableMethod

            booleanMethod.addInstructions(
                0, """
                    const/4 v0, 0x1
                    return v0
                    """
            )
        }
    }
}
