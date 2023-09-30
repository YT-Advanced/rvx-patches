package app.revanced.patches.youtube.player.playerbuttonbg.patch

import app.revanced.extensions.exception
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patches.youtube.player.playerbuttonbg.fingerprints.PlayerPatchFingerprint
import app.revanced.patches.youtube.utils.playerbutton.patch.PlayerButtonHookPatch
import app.revanced.patches.youtube.utils.settings.resource.patch.SettingsPatch
import app.revanced.util.integrations.Constants.INTEGRATIONS_PATH

@Patch(
    name = "Hide player button background",
    description = "Hide player button background.",
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
        PlayerButtonHookPatch::class,
        SettingsPatch::class
    ]
)
@Suppress("unused")
object HidePlayerButtonBackgroundPatch : BytecodePatch(
    setOf(PlayerPatchFingerprint)
) {
    override fun execute(context: BytecodeContext) {

        PlayerPatchFingerprint.result?.mutableMethod?.addInstruction(
            0,
            "invoke-static {p0}, " +
                    "$INTEGRATIONS_PATH/utils/ResourceHelper;->" +
                    "hidePlayerButtonBackground(Landroid/view/View;)V"
        ) ?: throw PlayerPatchFingerprint.exception

        /**
         * Add settings
         */
        SettingsPatch.addPreference(
            arrayOf(
                "PREFERENCE: PLAYER_SETTINGS",
                "SETTINGS: HIDE_PLAYER_BUTTON_BACKGROUND"
            )
        )

        SettingsPatch.updatePatchStatus("hide-player-button-background")

    }
}
