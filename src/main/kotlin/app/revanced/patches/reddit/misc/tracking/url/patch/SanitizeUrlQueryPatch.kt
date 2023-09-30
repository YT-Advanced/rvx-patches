package app.revanced.patches.reddit.misc.tracking.url.patch

import app.revanced.extensions.exception
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.util.smali.ExternalLabel
import app.revanced.patches.reddit.misc.tracking.url.fingerprints.ShareLinkFormatterFingerprint
import app.revanced.patches.reddit.utils.settings.bytecode.patch.SettingsBytecodePatch.updateSettingsStatus
import app.revanced.patches.reddit.utils.settings.resource.patch.SettingsPatch

@Patch(
    name = "Sanitize sharing links",
    compatiblePackages = [CompatiblePackage("com.reddit.frontpage")],
    description = "Removes (tracking) query parameters from the URLs when sharing links.",
    dependencies = [SettingsPatch::class]
)
@Suppress("unused")
object SanitizeUrlQueryPatch : BytecodePatch(
    setOf(ShareLinkFormatterFingerprint)
) {
    private const val SANITIZE_METHOD_DESCRIPTOR =
        "Lapp/revanced/reddit/patches/SanitizeUrlQueryPatch;" +
                "->stripQueryParameters()Z"

    override fun execute(context: BytecodeContext) {
        ShareLinkFormatterFingerprint.result?.let { result ->
            result.mutableMethod.apply {

                addInstructionsWithLabels(
                    0,
                    """
                        invoke-static {}, $SANITIZE_METHOD_DESCRIPTOR
                        move-result v0
                        if-eqz v0, :off
                        return-object p0
                        """, ExternalLabel("off", getInstruction(0))
                )
            }
        } ?: throw ShareLinkFormatterFingerprint.exception

        updateSettingsStatus("SanitizeUrlQuery")

    }
}
