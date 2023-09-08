package app.revanced.patches.youtube.shorts.shortscomponent.patch

import app.revanced.extensions.exception
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint.Companion.resolve
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patches.youtube.shorts.shortscomponent.fingerprints.ShortsSubscriptionsFingerprint
import app.revanced.patches.youtube.utils.resourceid.patch.SharedResourceIdPatch.Companion.ReelPlayerFooter
import app.revanced.patches.youtube.utils.resourceid.patch.SharedResourceIdPatch.Companion.ReelPlayerPausedStateButton
import app.revanced.util.bytecode.getWideLiteralIndex
import app.revanced.util.integrations.Constants.SHORTS
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction
import com.android.tools.smali.dexlib2.iface.reference.FieldReference

class ShortsSubscriptionsButtonPatch : BytecodePatch(
    listOf(
        ShortsSubscriptionsFingerprint
    )
) {
    override fun execute(context: BytecodeContext) {
        ShortsSubscriptionsFingerprint.result?.let {
            it.mutableMethod.apply {
                val insertIndex = getWideLiteralIndex(ReelPlayerPausedStateButton) + 2
                val insertRegister = getInstruction<OneRegisterInstruction>(insertIndex).registerA

                addInstruction(
                    insertIndex + 1,
                    "invoke-static {v$insertRegister}, $SHORTS->hideShortsPlayerSubscriptionsButton(Landroid/view/View;)V"
                )
            }
        } ?: throw ShortsSubscriptionsFingerprint.exception

        }

    }
