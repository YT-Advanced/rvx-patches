package app.revanced.patches.music.misc.translations.patch

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patches.music.utils.settings.resource.patch.SettingsPatch
import app.revanced.util.resources.ResourceHelper.addTranslations

@Patch(
    name = "Translations",
    description = "Add Crowdin translations for YouTube Music.",
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
    dependencies = [SettingsPatch::class]
)
@Suppress("unused")
object TranslationsPatch : ResourcePatch {
    // Language list
    val LANGUAGE_LIST = arrayOf(
        "az-rAZ",
        "be-rBY",
        "bn",
        "cs-rCZ",
        "de-rDE",
        "el-rGR",
        "es-rES",
        "fr-rFR",
        "hi-rIN",
        "hu-rHU",
        "id-rID",
        "in",
        "it-rIT",
        "ja-rJP",
        "ko-rKR",
        "nl-rNL",
        "pl-rPL",
        "pt-rBR",
        "ru-rRU",
        "th-rTH",
        "tr-rTR",
        "uk-rUA",
        "vi-rVN",
        "zh-rCN",
        "zh-rTW"
    )

    // Add translations
    override fun execute(context: ResourceContext) {
        context.addTranslations("music", LANGUAGE_LIST)
    }
}
