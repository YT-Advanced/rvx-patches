package app.revanced.patches.youtube.seekbar.timestamps.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

object TimeCounterFingerprint : MethodFingerprint(
    accessFlags = AccessFlags.PUBLIC or AccessFlags.FINAL,
    parameters = listOf(),
    returnType = "V",
    opcodes = listOf(
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT_WIDE,
        Opcode.INVOKE_STATIC,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.INVOKE_INTERFACE,
        Opcode.RETURN_VOID,
        Opcode.IGET_BOOLEAN,
        Opcode.IF_EQZ,
        Opcode.IGET_OBJECT,
        Opcode.IGET_WIDE,
        Opcode.IGET_WIDE,
        Opcode.SUB_LONG_2ADDR,
        Opcode.INVOKE_STATIC,
        Opcode.MOVE_RESULT_OBJECT,
        null, //iget-wide or iget-object
        Opcode.IGET_WIDE
    ),
    customFingerprint = { _, classDef ->
        // On older devices this fingerprint resolves very slowly.
        // Speed this up by checking for the number of methods.
        classDef.methods.count() == 14 || classDef.methods.count() == 15 || classDef.methods.count() == 17
    }
)