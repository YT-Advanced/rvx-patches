package app.revanced.patches.youtube.layout.doubletapbackground.patch

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patches.youtube.utils.settings.resource.patch.SettingsPatch

@Patch(
    name = "Hide double tap overlay filter",
    description = "Hides the double tap dark filter layer.",
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
    dependencies = [SettingsPatch::class],
    use = false
)
@Suppress("unused")
object DoubleTapOverlayBackgroundPatch : ResourcePatch {
    override fun execute(context: ResourceContext) {
        context.xmlEditor[RESOURCE_FILE_PATH].use {
            it.file.getElementsByTagName("merge").item(0).childNodes.apply {
                val attributes = arrayOf("height", "width")
                for (i in 1 until length) {
                    val view = item(i)
                    if (
                        view.hasAttributes() &&
                        view.attributes.getNamedItem("android:id").nodeValue.endsWith("tap_bloom_view")
                    ) {
                        attributes.forEach { attribute ->
                            view.attributes.getNamedItem("android:layout_$attribute").nodeValue =
                                "0.0dip"
                        }
                    }
                    if (
                        view.hasAttributes() &&
                        view.attributes.getNamedItem("android:id").nodeValue.endsWith("dark_background")
                    ) {
                        view.attributes.getNamedItem("android:src").nodeValue =
                            "@color/full_transparent"
                        break
                    }
                }
            }
        }

        SettingsPatch.updatePatchStatus("hide-double-tap-overlay-filter")

    }

    private companion object {
        const val RESOURCE_FILE_PATH = "res/layout/quick_seek_overlay.xml"
    }
}