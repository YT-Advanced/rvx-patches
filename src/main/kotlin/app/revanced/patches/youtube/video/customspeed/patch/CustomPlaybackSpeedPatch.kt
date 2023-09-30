package app.revanced.patches.youtube.video.customspeed.patch

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patches.shared.patch.customspeed.AbstractCustomPlaybackSpeedPatch
import app.revanced.patches.youtube.flyoutpanel.oldspeedlayout.patch.OldSpeedLayoutPatch
import app.revanced.patches.youtube.utils.settings.resource.patch.SettingsPatch
import app.revanced.util.integrations.Constants.VIDEO_PATH

@Patch(
    name = "Custom playback speed",
    description = "Adds more playback speed options.",
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
        OldSpeedLayoutPatch::class,
        SettingsPatch::class
    ]
)
@Suppress("unused")
object CustomPlaybackSpeedPatch : AbstractCustomPlaybackSpeedPatch(
    "$VIDEO_PATH/CustomPlaybackSpeedPatch;",
    10.0f
) {
    override fun execute(context: BytecodeContext) {
        super.execute(context)

        /**
         * Add settings
         */
        SettingsPatch.addPreference(
            arrayOf(
                "PREFERENCE: VIDEO_SETTINGS",
                "SETTINGS: CUSTOM_PLAYBACK_SPEED"
            )
        )

        SettingsPatch.updatePatchStatus("custom-playback-speed")
    }
}
