package app.revanced.patches.music.layout.branding.name.patch

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.OptionsContainer
import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.patch.PatchOption
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.annotation.CompatiblePackage

@Patch(
    name = "Custom branding Music name",
    description = "Rename the YouTube Music app to the name specified in options.json.",
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
    dependencies = [RemoveElementsPatch::class]
)
@Suppress("unused")
object CustomBrandingNamePatch : ResourcePatch {
    override fun execute(context: ResourceContext) {

        val longName = MusicLongName
            ?: throw PatchException("Invalid app name.")

        val shortName = MusicShortName
            ?: throw PatchException("Invalid app name.")

        context.xmlEditor["res/values/strings.xml"].use { editor ->
            val document = editor.file

            mapOf(
                "app_name" to longName,
                "app_launcher_name" to shortName
            ).forEach { (k, v) ->
                val stringElement = document.createElement("string")

                stringElement.setAttribute("name", k)
                stringElement.textContent = v

                document.getElementsByTagName("resources").item(0).appendChild(stringElement)
            }
        }

    }

    companion object : OptionsContainer() {
        var MusicLongName: String? by option(
            PatchOption.StringOption(
                key = "MusicLongName",
                default = "ReVanced Extended Music",
                title = "Application Name of YouTube Music",
                description = "The name of the YouTube Music it will show on your notification panel."
            )
        )
        var MusicShortName: String? by option(
            PatchOption.StringOption(
                key = "MusicShortName",
                default = "RVX Music",
                title = "Application Name of YouTube Music",
                description = "The name of the YouTube Music it will show on your home screen."
            )
        )
    }
}
