package app.revanced.patches.reddit.utils.settings.resource.patch

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patches.reddit.utils.integrations.patch.IntegrationsPatch
import app.revanced.patches.reddit.utils.settings.bytecode.patch.SettingsBytecodePatch
import kotlin.io.path.exists

@Patch(
    name = "Reddit settings",
    compatiblePackages = [CompatiblePackage("com.reddit.frontpage")],
    description = "Adds ReVanced settings to Reddit.",
    dependencies = [
        IntegrationsPatch::class,
        SettingsBytecodePatch::class
    ]
)
@Suppress("unused")
object SettingsPatch : ResourcePatch() {
    override fun execute(context: ResourceContext) {

        /**
         * Replace settings icon and label
         */
        arrayOf("preferences", "preferences_logged_in").forEach { targetXML ->
            val resDirectory = context["res"]
            val targetXml = resDirectory.resolve("xml").resolve("$targetXML.xml").toPath()

            if (!targetXml.exists())
                throw PatchException("The preferences can not be found.")

            val preference = context["res/xml/$targetXML.xml"]

            preference.writeText(
                preference.readText()
                    .replace(
                        "\"@drawable/icon_text_post\" android:title=\"@string/label_acknowledgements\"",
                        "\"@drawable/icon_beta_planet\" android:title=\"ReVanced Extended\""
                    )
            )
        }

    }
}