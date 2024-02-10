package app.revanced.patches.youtube.shorts.outlinebutton

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patcher.patch.options.PatchOption.PatchExtensions.booleanPatchOption
import app.revanced.patches.youtube.utils.settings.SettingsPatch
import app.revanced.util.ResourceGroup
import app.revanced.util.copyResources

@Patch(
    name = "Shorts overlay buttons",
    description = "Apply the new icons to the action buttons of the Shorts player.",
    dependencies = [SettingsPatch::class],
    compatiblePackages = [
        CompatiblePackage(
            "com.google.android.youtube",
            [
                "18.25.40",
                "18.27.36",
                "18.29.38",
                "18.30.37",
                "18.31.40",
                "18.32.39",
                "18.33.40",
                "18.34.38",
                "18.35.36",
                "18.36.39",
                "18.37.36",
                "18.38.44",
                "18.39.41",
                "18.40.34",
                "18.41.39",
                "18.42.41",
                "18.43.45",
                "18.44.41",
                "18.45.43",
                "18.46.45",
                "18.48.39",
                "18.49.37",
                "19.01.34",
                "19.02.39",
                "19.03.36",
                "19.04.37"
            ]
        )
    ],
    use = false
)
@Suppress("unused")
object ShortsOverlayButtonsPatch : ResourcePatch() {
    private val OutlineIcon by booleanPatchOption(
        key = "OutlineIcon",
        default = false,
        title = "Outline icons",
        description = "Apply the outline icon",
        required = true
    )

    private val OutlineCircleIcon by booleanPatchOption(
        key = "OutlineCircleIcon",
        default = false,
        title = "Outline circled icons",
        description = "Apply the outline circled icon",
        required = true
    )

    override fun execute(context: ResourceContext) {

        if (OutlineIcon == true) {
            arrayOf(
                "xxxhdpi",
                "xxhdpi",
                "xhdpi",
                "hdpi",
                "mdpi"
            ).forEach { dpi ->
                context.copyResources(
                    "youtube/shorts/outline",
                    ResourceGroup(
                        "drawable-$dpi",
                        "ic_right_dislike_on_32c.webp",
                        "ic_right_like_on_32c.webp"
                    )
                )
            }

            arrayOf(
                // Shorts outline icons for older versions of YouTube
                ResourceGroup(
                    "drawable",
                    "ic_right_comment_32c.xml",
                    "ic_right_dislike_off_32c.xml",
                    "ic_right_like_off_32c.xml",
                    "ic_right_share_32c.xml"
                ),

                ResourceGroup(
                    "drawable-xxhdpi",
                    "ic_remix_filled_white_24.webp", // for older versions only
                    "ic_remix_filled_white_shadowed.webp",
                    "ic_right_comment_shadowed.webp",
                    "ic_right_dislike_off_shadowed.webp",
                    "ic_right_dislike_on_shadowed.webp",
                    "ic_right_like_off_shadowed.webp",
                    "ic_right_like_on_shadowed.webp",
                    "ic_right_share_shadowed.webp"
                )
            ).forEach { resourceGroup ->
                context.copyResources("youtube/shorts/outline", resourceGroup)
            }
        } else if (OutlineCircleIcon == true) {
            arrayOf(
                "xxxhdpi",
                "xxhdpi",
                "xhdpi",
                "hdpi",
                "mdpi"
            ).forEach { dpi ->
                context.copyResources(
                    "youtube/shorts/outline",
                    ResourceGroup(
                        "drawable-$dpi",
                        "ic_right_dislike_on_32c.webp",
                        "ic_right_like_on_32c.webp"
                    )
                )
            }

            arrayOf(
                // Shorts outline icons for older versions of YouTube (not circled)
                ResourceGroup(
                    "drawable",
                    "ic_right_comment_32c.xml",
                    "ic_right_dislike_off_32c.xml",
                    "ic_right_like_off_32c.xml",
                    "ic_right_share_32c.xml"
                ),

                ResourceGroup(
                    "drawable-xxhdpi",
                    "ic_remix_filled_white_24.webp", // for older versions only
                    "ic_remix_filled_white_shadowed.webp",
                    "ic_right_comment_shadowed.webp",
                    "ic_right_dislike_off_shadowed.webp",
                    "ic_right_dislike_on_shadowed.webp",
                    "ic_right_like_off_shadowed.webp",
                    "ic_right_like_on_shadowed.webp",
                    "ic_right_share_shadowed.webp"
                )
            ).forEach { resourceGroup ->
                context.copyResources("youtube/shorts/outlinecircle", resourceGroup)
            }
        } else {
            arrayOf(
                ResourceGroup(
                    "drawable-xxhdpi",
                    "ic_remix_filled_white_shadowed.webp",
                    "ic_right_comment_shadowed.webp",
                    "ic_right_dislike_off_shadowed.webp",
                    "ic_right_dislike_on_shadowed.webp",
                    "ic_right_like_off_shadowed.webp",
                    "ic_right_like_on_shadowed.webp",
                    "ic_right_share_shadowed.webp"
                )
            ).forEach { resourceGroup ->
                context.copyResources("youtube/shorts/default", resourceGroup)
            }
        }

        SettingsPatch.updatePatchStatus("Shorts overlay buttons")

    }
}