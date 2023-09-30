package app.revanced.patches.youtube.buttomplayer.comment.patch

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patches.youtube.ads.general.bytecode.patch.GeneralAdsBytecodePatch
import app.revanced.patches.youtube.utils.litho.patch.LithoFilterPatch
import app.revanced.patches.youtube.utils.settings.resource.patch.SettingsPatch
import app.revanced.util.integrations.Constants.PATCHES_PATH

@Patch(
    name = "Hide comment component",
    description = "Hides components related to comments.",
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
        GeneralAdsBytecodePatch::class,
        SettingsPatch::class
    ]
)
@Suppress("unused")
object CommentComponentPatch : ResourcePatch() {
    override fun execute(context: ResourceContext) {
        LithoFilterPatch.addFilter("$PATCHES_PATH/ads/CommentsFilter;")
        LithoFilterPatch.addFilter("$PATCHES_PATH/ads/CommentsPreviewDotsFilter;")

        /**
         * Add settings
         */
        SettingsPatch.addPreference(
            arrayOf(
                "PREFERENCE: BOTTOM_PLAYER_SETTINGS",
                "SETTINGS: COMMENT_COMPONENTS"
            )
        )

        SettingsPatch.updatePatchStatus("hide-comment-component")

    }
}
