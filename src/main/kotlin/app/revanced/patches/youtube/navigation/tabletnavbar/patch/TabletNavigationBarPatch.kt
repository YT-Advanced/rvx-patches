package app.revanced.patches.youtube.navigation.tabletnavbar.patch

import app.revanced.extensions.exception
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprintResult
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patches.youtube.navigation.tabletnavbar.fingerprints.PivotBarChangedFingerprint
import app.revanced.patches.youtube.navigation.tabletnavbar.fingerprints.PivotBarStyleFingerprint
import app.revanced.patches.youtube.utils.settings.resource.patch.SettingsPatch
import app.revanced.util.integrations.Constants.NAVIGATION
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction

@Patch(
    name = "Enable tablet navigation bar",
    description = "Enables the tablet navigation bar.",
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
    dependencies = [SettingsPatch::class]
)
@Suppress("unused")
object TabletNavigationBarPatch : BytecodePatch(
    setOf(
        PivotBarChangedFingerprint,
        PivotBarStyleFingerprint
    )
) {
    override fun execute(context: BytecodeContext) {

        arrayOf(
            PivotBarChangedFingerprint,
            PivotBarStyleFingerprint
        ).forEach {
            it.result?.insertHook() ?: throw it.exception
        }

        /**
         * Add settings
         */
        SettingsPatch.addPreference(
            arrayOf(
                "PREFERENCE: NAVIGATION_SETTINGS",
                "SETTINGS: ENABLE_TABLET_NAVIGATION_BAR"
            )
        )

        SettingsPatch.updatePatchStatus("enable-tablet-navigation-bar")

    }

    companion object {
        private fun MethodFingerprintResult.insertHook() {
            val targetIndex = scanResult.patternScanResult!!.startIndex + 1
            val register =
                mutableMethod.getInstruction<OneRegisterInstruction>(targetIndex).registerA

            mutableMethod.addInstructions(
                targetIndex + 1, """
                    invoke-static {v$register}, $NAVIGATION->enableTabletNavBar(Z)Z
                    move-result v$register
                    """
            )
        }
    }
}
