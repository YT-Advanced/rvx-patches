package app.revanced.patches.youtube.misc.translations.patch

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patches.youtube.utils.settings.resource.patch.SettingsPatch
import app.revanced.util.resources.ResourceHelper.addTranslations

@Patch(
    name = "Translations",
    description = "Add Crowdin translations for YouTube.",
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
    dependencies = [SettingsPatch::class]
)
@Suppress("unused")
object TranslationsPatch : ResourcePatch {
    override fun execute(context: ResourceContext) {

        context.addTranslations("youtube", LANGUAGE_LIST)

        SettingsPatch.updatePatchStatus("translations")

    }

    private companion object {
        val LANGUAGE_LIST = arrayOf(
            "ar",
            "bg-rBG",
            "bn",
            "de-rDE",
            "el-rGR",
            "es-rES",
            "fi-rFI",
            "fr-rFR",
            "hu-rHU",
            "id-rID",
            "in",
            "it-rIT",
            "ja-rJP",
            "ko-rKR",
            "pl-rPL",
            "pt-rBR",
            "ru-rRU",
            "tr-rTR",
            "uk-rUA",
            "vi-rVN",
            "zh-rCN",
            "zh-rTW"
        )
    }
}
