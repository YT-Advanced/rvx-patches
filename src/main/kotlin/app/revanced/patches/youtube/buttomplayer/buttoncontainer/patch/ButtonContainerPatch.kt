package app.revanced.patches.youtube.buttomplayer.buttoncontainer.patch

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patches.youtube.utils.litho.patch.LithoFilterPatch
import app.revanced.patches.youtube.utils.settings.resource.patch.SettingsPatch
import app.revanced.util.integrations.Constants.PATCHES_PATH

@Patch(
    name = "Hide button container",
    description = "Adds the options to hide action buttons under a video.",
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
        LithoFilterPatch::class,
        SettingsPatch::class
    ]
)
@Suppress("unused")
object ButtonContainerPatch : ResourcePatch {
    override fun execute(context: ResourceContext) {

        LithoFilterPatch.addFilter("$PATCHES_PATH/ads/ButtonsFilter;")

        /**
         * Add settings
         */
        SettingsPatch.addPreference(
            arrayOf(
                "PREFERENCE: BOTTOM_PLAYER_SETTINGS",
                "SETTINGS: BUTTON_CONTAINER"
            )
        )

        SettingsPatch.updatePatchStatus("hide-button-container")

    }
}
