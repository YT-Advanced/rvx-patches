package app.revanced.patches.music.general.branding.icon.patch

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.util.resources.IconHelper.customIconMusic
import app.revanced.util.resources.IconHelper.customIconMusicAdditional

@Patch(
    name = "Custom branding icon MMT",
    description = "Changes the YouTube Music launcher icon to MMT."
    use = false
)
@Suppress("unused")
object CustomBrandingIconMMTPatch : ResourcePatch {
    override fun execute(context: ResourceContext) {

        context.customIconMusic("mmt")
        context.customIconMusicAdditional("mmt")

    }

}
