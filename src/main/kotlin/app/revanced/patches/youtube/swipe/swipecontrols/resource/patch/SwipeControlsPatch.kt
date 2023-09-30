package app.revanced.patches.youtube.swipe.swipecontrols.resource.patch

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patches.youtube.swipe.hdrbrightness.patch.HDRBrightnessPatch
import app.revanced.patches.youtube.swipe.swipecontrols.bytecode.patch.SwipeControlsBytecodePatch
import app.revanced.patches.youtube.utils.settings.resource.patch.SettingsPatch
import app.revanced.util.resources.ResourceUtils
import app.revanced.util.resources.ResourceUtils.copyResources

@Patch(
    name = "Swipe controls",
    description = "Adds volume and brightness swipe controls.",
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
        HDRBrightnessPatch::class,
        SettingsPatch::class,
        SwipeControlsBytecodePatch::class
    ]
)
@Suppress("unused")
object SwipeControlsPatch : ResourcePatch() {
    override fun execute(context: ResourceContext) {

        /**
         * Add settings
         */
        SettingsPatch.addPreference(
            arrayOf(
                "PREFERENCE: SWIPE_SETTINGS",
                "SETTINGS: SWIPE_CONTROLS"
            )
        )

        SettingsPatch.updatePatchStatus("swipe-controls")

        context.copyResources(
            "youtube/swipecontrols",
            ResourceUtils.ResourceGroup(
                "drawable",
                "ic_sc_brightness_auto.xml",
                "ic_sc_brightness_manual.xml",
                "ic_sc_volume_mute.xml",
                "ic_sc_volume_normal.xml"
            )
        )
    }
}
