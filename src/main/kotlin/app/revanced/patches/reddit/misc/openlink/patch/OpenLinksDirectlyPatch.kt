package app.revanced.patches.reddit.misc.openlink.patch

import app.revanced.extensions.exception
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patches.reddit.misc.openlink.fingerprints.ScreenNavigatorFingerprint
import app.revanced.patches.reddit.utils.settings.bytecode.patch.SettingsBytecodePatch.updateSettingsStatus
import app.revanced.patches.reddit.utils.settings.resource.patch.SettingsPatch

@Patch(
    name = "Open links directly",
    compatiblePackages = [CompatiblePackage("com.reddit.frontpage")],
    description = "Skips over redirection URLs to external links.",
    dependencies = [SettingsPatch::class]
)
@Suppress("unused")
object OpenLinksDirectlyPatch : BytecodePatch(
    setOf(ScreenNavigatorFingerprint)
) {
    private const val INTEGRATIONS_METHOD_DESCRIPTOR =
        "Lapp/revanced/reddit/patches/OpenLinksDirectlyPatch;" +
                "->parseRedirectUri(Landroid/net/Uri;)Landroid/net/Uri;"

    override fun execute(context: BytecodeContext) {
        ScreenNavigatorFingerprint.result?.let {
            it.mutableMethod.apply {
                addInstructions(
                    0, """
                        invoke-static {p2}, $INTEGRATIONS_METHOD_DESCRIPTOR
                        move-result-object p2
                        """
                )
            }
        } ?: throw ScreenNavigatorFingerprint.exception

        updateSettingsStatus("OpenLinksDirectly")

    }
}