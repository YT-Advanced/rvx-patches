package app.revanced.patches.youtube.misc.debugging.patch

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patches.youtube.utils.settings.resource.patch.SettingsPatch

@Patch(
    name = "Enable debug logging",
    description = "Adds debugging options.",
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
object DebuggingPatch : ResourcePatch {
    override fun execute(context: ResourceContext) {

        /**
         * Add settings
         */
        SettingsPatch.addPreference(
            arrayOf(
                "SETTINGS: ENABLE_DEBUG_LOGGING"
            )
        )

        SettingsPatch.updatePatchStatus("enable-debug-logging")


    }
}
