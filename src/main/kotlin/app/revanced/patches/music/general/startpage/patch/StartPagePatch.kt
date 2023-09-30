package app.revanced.patches.music.general.startpage.patch

import app.revanced.extensions.exception
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.extensions.InstructionExtensions.removeInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patches.music.general.startpage.fingerprints.ColdStartUpFingerprint
import app.revanced.patches.music.utils.intenthook.patch.IntentHookPatch
import app.revanced.patches.music.utils.settings.resource.patch.SettingsPatch
import app.revanced.patches.music.utils.settings.resource.patch.SettingsPatch.Companion.contexts
import app.revanced.util.enum.CategoryType
import app.revanced.util.integrations.Constants.MUSIC_GENERAL
import app.revanced.util.resources.ResourceUtils.copyXmlNode
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction

@Patch(
    name = "Start page",
    description = "Set the default start page.",
    compatiblePackages = [
        CompatiblePackage(
            "com.google.android.apps.youtube.music",
            [
                "6.15.52",
                "6.20.51",
                "6.21.51"
            ]
        )
    ]
    dependencies = [
        IntentHookPatch::class,
        SettingsPatch::class
    ]
)
@Suppress("unused")
object StartPagePatch : BytecodePatch(
    setOf(ColdStartUpFingerprint)
) {
    override fun execute(context: BytecodeContext) {

        ColdStartUpFingerprint.result?.let {
            it.mutableMethod.apply {
                val targetIndex = it.scanResult.patternScanResult!!.endIndex
                val targetRegister = getInstruction<OneRegisterInstruction>(targetIndex).registerA

                addInstructions(
                    targetIndex + 1, """
                        invoke-static {v$targetRegister}, $MUSIC_GENERAL->setStartPage(Ljava/lang/String;)Ljava/lang/String;
                        move-result-object v$targetRegister
                        return-object v$targetRegister
                        """
                )
                removeInstruction(targetIndex)
            }
        } ?: throw ColdStartUpFingerprint.exception

        /**
         * Copy arrays
         */
        contexts.copyXmlNode("music/startpage/host", "values/arrays.xml", "resources")

        SettingsPatch.addMusicPreferenceWithIntent(
            CategoryType.GENERAL,
            "revanced_start_page"
        )

    }
}
