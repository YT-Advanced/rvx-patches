package app.revanced.patches.music.video.customspeed.patch

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patches.music.utils.intenthook.patch.IntentHookPatch
import app.revanced.patches.music.utils.settings.resource.patch.SettingsPatch
import app.revanced.patches.shared.patch.customspeed.AbstractCustomPlaybackSpeedPatch
import app.revanced.util.enum.CategoryType
import app.revanced.util.integrations.Constants.MUSIC_VIDEO_PATH

@Patch(
    name = "Custom playback speed",
    description = "Adds more playback speed options.",
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
    dependencies = [IntentHookPatch::class]
)
@Suppress("unused")
object CustomPlaybackSpeedPatch : AbstractCustomPlaybackSpeedPatch(
    "$MUSIC_VIDEO_PATH/CustomPlaybackSpeedPatch;",
    3.0f
) {
    override fun execute(context: BytecodeContext) {
        super.execute(context)

        SettingsPatch.addMusicPreferenceWithIntent(
            CategoryType.VIDEO,
            "revanced_custom_playback_speeds"
        )

    }
}
