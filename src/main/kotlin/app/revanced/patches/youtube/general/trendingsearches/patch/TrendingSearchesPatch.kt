package app.revanced.patches.youtube.general.trendingsearches.patch

import app.revanced.extensions.exception
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patches.youtube.general.trendingsearches.fingerprints.SearchBarEntryFingerprint
import app.revanced.patches.youtube.utils.resourceid.patch.SharedResourceIdPatch
import app.revanced.patches.youtube.utils.resourceid.patch.SharedResourceIdPatch.YtOutlineArrowTimeBlack
import app.revanced.patches.youtube.utils.resourceid.patch.SharedResourceIdPatch.YtOutlineFireBlack
import app.revanced.patches.youtube.utils.resourceid.patch.SharedResourceIdPatch.YtOutlineSearchBlack
import app.revanced.patches.youtube.utils.settings.resource.patch.SettingsPatch
import app.revanced.util.bytecode.getWideLiteralIndex
import app.revanced.util.integrations.Constants.GENERAL
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction

@Patch(
    name = "Hide trending searches",
    description = "Hide trending searches in the search bar.",
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
        SettingsPatch::class,
        SharedResourceIdPatch::class
    ]
)
@Suppress("unused")
object TrendingSearchesPatch : BytecodePatch(
    setOf(SearchBarEntryFingerprint)
) {
    override fun execute(context: BytecodeContext) {

        SearchBarEntryFingerprint.result?.let {
            it.mutableMethod.apply {
                SearchTerm.values()
                    .map { searchTerm -> getWideLiteralIndex(searchTerm.resourceId) to searchTerm.value }
                    .sortedBy { searchTerm -> searchTerm.first }
                    .reversed()
                    .forEach { (index, value) ->
                        val freeRegister = getInstruction<OneRegisterInstruction>(index).registerA
                        val viewRegister =
                            getInstruction<TwoRegisterInstruction>(index - 1).registerA

                        addInstructions(
                            index, """
                                const/4 v$freeRegister, $value
                                invoke-static {v$viewRegister, v$freeRegister}, $GENERAL->hideTrendingSearches(Landroid/widget/ImageView;Z)V
                                """
                        )
                    }
            }
        } ?: throw SearchBarEntryFingerprint.exception

        /**
         * Add settings
         */
        SettingsPatch.addPreference(
            arrayOf(
                "PREFERENCE: GENERAL_SETTINGS",
                "SETTINGS: HIDE_TRENDING_SEARCHES"
            )
        )

        SettingsPatch.updatePatchStatus("hide-trending-searches")

    }

    private enum class SearchTerm(val resourceId: Long, val value: Int) {
        HISTORY(YtOutlineArrowTimeBlack, 0),
        SEARCH(YtOutlineSearchBlack, 0),
        TRENDING(YtOutlineFireBlack, 1)
    }
}