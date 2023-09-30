package app.revanced.patches.youtube.general.personalinformation.patch

import app.revanced.extensions.exception
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patches.youtube.general.personalinformation.fingerprints.AccountSwitcherAccessibilityLabelFingerprint
import app.revanced.patches.youtube.utils.resourceid.patch.SharedResourceIdPatch
import app.revanced.patches.youtube.utils.settings.resource.patch.SettingsPatch
import app.revanced.util.integrations.Constants.GENERAL
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction

@Patch(
    name = "Hide handle",
    description = "Hides the handle in the account switcher.",
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
        SettingsPatch::class,
        SharedResourceIdPatch::class
    ]
)
@Suppress("unused")
object HideEmailAddressPatch : BytecodePatch(
    setOf(AccountSwitcherAccessibilityLabelFingerprint)
) {
    override fun execute(context: BytecodeContext) {

        AccountSwitcherAccessibilityLabelFingerprint.result?.let {
            it.mutableMethod.apply {
                val targetIndex = it.scanResult.patternScanResult!!.endIndex - 2
                val targetRegister = getInstruction<OneRegisterInstruction>(targetIndex).registerA

                addInstructions(
                    targetIndex + 2, """
                        invoke-static {v$targetRegister}, $GENERAL->hideEmailAddress(I)I
                        move-result v$targetRegister
                        """
                )
            }
        } ?: throw AccountSwitcherAccessibilityLabelFingerprint.exception

        /**
         * Add settings
         */
        SettingsPatch.addPreference(
            arrayOf(
                "PREFERENCE: GENERAL_SETTINGS",
                "SETTINGS: HIDE_EMAIL_ADDRESS"
            )
        )

        SettingsPatch.updatePatchStatus("hide-handle")

    }
}
