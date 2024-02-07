package app.revanced.patches.youtube.misc.ambientmode.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction

object PowerSaveModeNewFingerprint : MethodFingerprint(
    returnType = "V",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.FINAL,
    parameters = listOf("Ljava/lang/Object;"),
    opcodes = listOf(
        Opcode.IGET,
        Opcode.CONST_4,
        Opcode.PACKED_SWITCH,
        Opcode.CHECK_CAST
    ),
    customFingerprint = custom@{ methodDef, _ ->
        if (methodDef.name != "accept")
            return@custom false

        val instructions = methodDef.implementation?.instructions!!

        var count = 0
        for (instruction in instructions) {
            if (instruction.opcode != Opcode.INVOKE_VIRTUAL)
                continue

            val invokeInstruction = instruction as ReferenceInstruction
            if (!invokeInstruction.reference.toString().endsWith("Landroid/os/PowerManager;->isPowerSaveMode()Z"))
                continue

            count++
        }
        count == 1
    }
)