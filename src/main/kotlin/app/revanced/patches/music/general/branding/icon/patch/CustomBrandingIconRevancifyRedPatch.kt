package app.revanced.patches.music.general.branding.icon.patch

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.util.resources.IconHelper.customIconMusic

@Patch(
    name = "Custom branding icon Revancify red",
    description = "Changes the YouTube Music launcher icon to Revancify Red."
    use = false
)
@Suppress("unused")
object CustomBrandingIconRevancifyRedPatch : ResourcePatch {
    override fun execute(context: ResourceContext) {

        context.customIconMusic("revancify-red")

    }

}
