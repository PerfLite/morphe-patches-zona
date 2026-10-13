package app.morphe.patches.zona

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.extensions.InstructionExtensions.getInstruction
import app.morphe.patcher.extensions.InstructionExtensions.replaceInstruction
import app.morphe.patcher.methodCall
import app.morphe.patcher.patch.ApkFileType
import app.morphe.patcher.patch.AppTarget
import app.morphe.patcher.patch.Compatibility
import app.morphe.patcher.patch.bytecodePatch
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.iface.instruction.FiveRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.NarrowLiteralInstruction
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction

private val ZONA_COMPATIBILITY = Compatibility(
    name = "FreeZona World",
    packageName = "free.zona",
    apkFileType = ApkFileType.APK_REQUIRED,
    appIconColor = 0x202124,
    signatures = setOf("a40da80a59d170caa950cf15c18c454d47a39b26989d8b640ecd745ba71bf5dc"),
    targets = listOf(
        AppTarget(
            version = "3.0.75",
            versionCode = 1075,
            minSdk = 21,
        )
    ),
)

private object SetSensorLandscapeFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "V",
    parameters = listOf(),
    strings = listOf("set SENSOR_LANDSCAPE orientation"),
    filters = listOf(
        methodCall(
            definingClass = "Landroid/app/Activity;",
            name = "setRequestedOrientation",
            parameters = listOf("I"),
            returnType = "V",
        )
    ),
)

@Suppress("unused")
val forceLandscapePlayerPatch = bytecodePatch(
    name = "Force landscape player",
    description = "Switches to landscape orientation when movie or series playback starts.",
    default = true,
) {
    compatibleWith(ZONA_COMPATIBILITY)

    execute {
        SetSensorLandscapeFingerprint.method.apply {
            val setterIndex = SetSensorLandscapeFingerprint.instructionMatches.single().index
            val setterInstruction = getInstruction<FiveRegisterInstruction>(setterIndex)
            val literalIndex = (setterIndex - 1 downTo 0).first { index ->
                val instruction = implementation!!.instructions[index]
                instruction is NarrowLiteralInstruction &&
                    instruction is OneRegisterInstruction &&
                    instruction.narrowLiteral == -1 &&
                    instruction.registerA == setterInstruction.registerD
            }
            val orientationRegister = getInstruction<OneRegisterInstruction>(literalIndex).registerA

            replaceInstruction(literalIndex, "const/4 v$orientationRegister, 0x6")
        }
    }
}
