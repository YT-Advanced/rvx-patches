package app.revanced.patches.music.misc.debugging.patch

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patches.music.utils.settings.resource.patch.SettingsPatch
import app.revanced.util.enum.CategoryType

@Patch(
    name = "Enable debug logging",
    description = "Adds debugging options.",
    compatiblePackages = [
        CompatiblePackage(
            "com.google.android.apps.youtube.music",
            [
                "6.15.52",
                "6.20.51",
                "6.21.51"
            ]
        )
    ],
    dependencies = [SettingsPatch::class],
    use = false
)
@Suppress("unused")
object DebuggingPatch : ResourcePatch {
    override fun execute(context: ResourceContext) {

        SettingsPatch.addMusicPreference(
            CategoryType.MISC,
            "revanced_enable_debug_logging",
            "false"
        )

    }
}