package app.revanced.patches.music.misc.tastebuilder.patch

import app.revanced.extensions.exception
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patches.music.misc.tastebuilder.fingerprints.TasteBuilderConstructorFingerprint
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction

@Patch(
    name = "Hide taste builder",
    description = "Hides the \"Tell us which artists you like\" card from homepage."
)
@Suppress("unused")
object TasteBuilderPatch : BytecodePatch(
    setOf(TasteBuilderConstructorFingerprint)
) {
    override fun execute(context: BytecodeContext) {
        TasteBuilderConstructorFingerprint.result?.let {
            it.mutableMethod.apply {
                val insertIndex = it.scanResult.patternScanResult!!.endIndex - 8
                val register = getInstruction<TwoRegisterInstruction>(insertIndex).registerA

                addInstructions(
                    insertIndex, """
                        const/16 v1, 0x8
                        invoke-virtual {v$register, v1}, Landroid/view/View;->setVisibility(I)V
                        """
                )
            }
        } ?: throw TasteBuilderConstructorFingerprint.exception

    }
}
