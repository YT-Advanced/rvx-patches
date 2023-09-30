package app.revanced.patches.reddit.layout.premiumicon.patch

import app.revanced.extensions.exception
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patches.reddit.layout.premiumicon.fingerprints.PremiumIconFingerprint

@Patch(
    name = "Premium icon",
    compatiblePackages = [CompatiblePackage("com.reddit.frontpage")]
    description = "Unlocks premium icons."
)
@Suppress("unused")
object PremiumIconPatch : BytecodePatch(
    setOf(PremiumIconFingerprint)
) {
    override fun execute(context: BytecodeContext) {

        PremiumIconFingerprint.result?.let {
            it.mutableMethod.apply {
                addInstructions(
                    0, """
                        const/4 v0, 0x1
                        return v0
                        """
                )
            }
        } ?: throw PremiumIconFingerprint.exception

    }
}
