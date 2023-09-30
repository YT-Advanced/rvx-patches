package app.revanced.patches.music.ads.music.patch

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patches.music.utils.litho.patch.LithoFilterPatch
import app.revanced.patches.music.utils.settings.resource.patch.SettingsPatch
import app.revanced.patches.shared.patch.ads.AbstractAdsPatch
import app.revanced.util.enum.CategoryType
import app.revanced.util.integrations.Constants.MUSIC_ADS_PATH

@Patch(
    name = "Hide music ads",
    description = "Hides ads before playing a music.",
    compatiblePackages = [
        CompatiblePackage(
            "com.google.android.apps.youtube.music",
            [
                "6.15.52",
                "6.20.51",
                "6.21.51"
            ]
        )
    ],
    dependencies = [
        LithoFilterPatch::class,
        SettingsPatch::class
    ]
)
@Suppress("unused")
object MusicAdsPatch : AbstractAdsPatch(
    "$MUSIC_ADS_PATH/HideMusicAdsPatch;->hideMusicAds()Z"
) {
    private const val FILTER_CLASS_DESCRIPTOR =
        "$MUSIC_ADS_PATH/AdsFilter;"

    override fun execute(context: BytecodeContext) {
        super.execute(context)

        SettingsPatch.addMusicPreference(CategoryType.ADS, "revanced_hide_music_ads", "true")

        LithoFilterPatch.addFilter(FILTER_CLASS_DESCRIPTOR)

    }
}
