package app.revanced.patches.youtube.layout.theme.patch

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patches.youtube.layout.theme.patch.GeneralThemePatch.isMonetPatchIncluded
import app.revanced.patches.youtube.utils.settings.resource.patch.SettingsPatch
import app.revanced.util.resources.ResourceHelper.updatePatchStatusTheme
import org.w3c.dom.Element
import app.revanced.patcher.patch.options.types.StringPatchOption.Companion.stringPatchOption

@Patch(
    name = "Theme",
    description = "Change the app's theme to the values specified in options.json.",
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
        GeneralThemePatch::class,
        SettingsPatch::class
    ]
)
@Suppress("unused")
object ThemePatch : ResourcePatch {
    var darkThemeBackgroundColor by stringPatchOption(
        key = "darkThemeBackgroundColor",
        default = "@android:color/black",
        title = "Background color for the dark theme",
        description = "The background color of the dark theme. Can be a hex color or a resource reference."
    )

    override fun execute(context: ResourceContext) {

        arrayOf("values", "values-v31").forEach { context.setTheme(it) }

        val currentTheme = if (isMonetPatchIncluded) "mix" else "amoled"

        context.updatePatchStatusTheme(currentTheme)

    }

    private fun ResourceContext.setTheme(valuesPath: String) {
        val darkThemeColor = darkThemeBackgroundColor
            ?: throw PatchException("Invalid color.")

        this.xmlEditor["res/$valuesPath/colors.xml"].use { editor ->
            val resourcesNode = editor.file.getElementsByTagName("resources").item(0) as Element

            for (i in 0 until resourcesNode.childNodes.length) {
                val node = resourcesNode.childNodes.item(i) as? Element ?: continue

                node.textContent = when (node.getAttribute("name")) {
                    "yt_black0", "yt_black1", "yt_black1_opacity95", "yt_black1_opacity98", "yt_black2", "yt_black3",
                    "yt_black4", "yt_status_bar_background_dark", "material_grey_850" -> darkThemeColor

                    else -> continue
                }
            }
        }
    }
}
