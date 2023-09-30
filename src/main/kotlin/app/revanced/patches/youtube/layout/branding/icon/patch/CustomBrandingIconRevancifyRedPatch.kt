package app.revanced.patches.youtube.layout.branding.icon.patch

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patches.youtube.utils.settings.resource.patch.SettingsPatch
import app.revanced.util.resources.IconHelper.customIcon
import app.revanced.util.resources.ResourceHelper.updatePatchStatusIcon

@Patch(
    name = "Custom branding icon Revancify red",
    description = "Changes the YouTube launcher icon to Revancify Red.",
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
    dependencies = [SettingsPatch::class],
    use = false
)
@Suppress("unused")
object CustomBrandingIconRevancifyRedPatch : ResourcePatch() {
    override fun execute(context: ResourceContext) {

        context.customIcon("revancify-red")
        context.updatePatchStatusIcon("revancify_red")

    }
}
