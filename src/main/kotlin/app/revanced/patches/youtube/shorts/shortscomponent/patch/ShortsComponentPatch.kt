package app.revanced.patches.youtube.shorts.shortscomponent.patch

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patches.youtube.shorts.shortsnavigationbar.patch.ShortsNavigationBarPatch
import app.revanced.patches.youtube.utils.litho.patch.LithoFilterPatch
import app.revanced.patches.youtube.utils.navbarindex.patch.NavBarIndexHookPatch
import app.revanced.patches.youtube.utils.resourceid.patch.SharedResourceIdPatch
import app.revanced.patches.youtube.utils.settings.resource.patch.SettingsPatch
import app.revanced.util.integrations.Constants.PATCHES_PATH

@Patch(
    name = "Hide shorts components",
    description = "Hides other Shorts components.",
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
    ],
    dependencies = [
        LithoFilterPatch::class,
        NavBarIndexHookPatch::class,
        SettingsPatch::class,
        SharedResourceIdPatch::class,
        ShortsCommentButtonPatch::class,
        ShortsDislikeButtonPatch::class,
        ShortsInfoPanelPatch::class,
        ShortsLikeButtonPatch::class,
        ShortsNavigationBarPatch::class,
        ShortsPaidPromotionBannerPatch::class,
        ShortsPivotButtonPatch::class,
        ShortsRemixButtonPatch::class,
        ShortsShareButtonPatch::class,
        ShortsSubscriptionsButtonPatch::class,
        ShortsToolBarPatch::class
    ]
)
@Suppress("unused")
object ShortsComponentPatch : BytecodePatch() {
    override fun execute(context: BytecodeContext) {

        LithoFilterPatch.addFilter("$PATCHES_PATH/ads/ShortsFilter;")

        /**
         * Add settings
         */
        SettingsPatch.addPreference(
            arrayOf(
                "PREFERENCE: SHORTS_SETTINGS",
                "SETTINGS: HIDE_SHORTS_SHELF",
                "SETTINGS: SHORTS_PLAYER_PARENT",
                "SETTINGS: HIDE_SHORTS_COMPONENTS"
            )
        )

        SettingsPatch.updatePatchStatus("hide-shorts-component")

    }
}
