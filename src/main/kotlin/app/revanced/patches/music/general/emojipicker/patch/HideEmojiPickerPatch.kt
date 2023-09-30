package app.revanced.patches.music.general.emojipicker.patch

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patches.music.utils.litho.patch.LithoFilterPatch
import app.revanced.patches.music.utils.settings.resource.patch.SettingsPatch
import app.revanced.util.enum.CategoryType
import app.revanced.util.integrations.Constants.MUSIC_ADS_PATH

@Patch(
    name = "Hide emoji picker",
    description = "Hides emoji picker at the comments box.",
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
        LithoFilterPatch::class,
        SettingsPatch::class
    ]
)
@Suppress("unused")
object HideEmojiPickerPatch : BytecodePatch() {
    override fun execute(context: BytecodeContext) {

        SettingsPatch.addMusicPreference(
            CategoryType.GENERAL,
            "revanced_hide_emoji_picker",
            "false"
        )

        LithoFilterPatch.addFilter(FILTER_CLASS_DESCRIPTOR)

    }

    private companion object {
        private const val FILTER_CLASS_DESCRIPTOR =
            "$MUSIC_ADS_PATH/EmojiPickerFilter;"
    }
}
