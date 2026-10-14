package app.morphe.patches.zona

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.extensions.InstructionExtensions.getInstruction
import app.morphe.patcher.methodCall
import app.morphe.patcher.patch.bytecodePatch
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.iface.instruction.FiveRegisterInstruction

/**
 * Stream selection service, which reads the resolution and the voice-over chosen by hand last
 * time out of the app settings memory and turns them into the stream filters to apply.
 */
private object StreamSelectionFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.CONSTRUCTOR),
    parameters = listOf("LFg/n;", "LKd/i;", "LGe/b;", "LFg/h;", "LFg/p;", "Lhe/w0;", "Lda/M;"),
    filters = listOf(
        // Turns the remembered resolution into a filter.
        methodCall(
            definingClass = "LFg/s\$a;",
            name = "a",
            parameters = listOf("Ljava/lang/String;"),
            returnType = "LFg/s;",
        ),
        // The same constructor receives the quality from the app settings.
        methodCall(
            definingClass = "LHg/o;",
            name = "<init>",
            parameters = listOf("LGe/b;", "LFg/f\$c;"),
            returnType = "V",
        ),
    ),
)

@Suppress("unused")
val preferredQualityPriorityPatch = bytecodePatch(
    name = "Preferred quality priority",
    description = "Plays at the quality from \"Preferred quality\" instead of the last quality chosen by hand, and keeps the chosen voice-over.",
    default = true,
) {
    compatibleWith(ZONA_COMPATIBILITY)

    execute {
        // The remembered resolution is the only thing the strict filter chain demands from a
        // stream, and the quality sheet highlights it. Read the quality from the settings instead,
        // which makes the chain require that quality and the voice-over chosen by hand, while
        // leaving the remaining quality ladder to rank the streams instead of ruling them out.
        StreamSelectionFingerprint.method.apply {
            val parseResolution = StreamSelectionFingerprint.instructionMatches[0]
            val qualityArgument = StreamSelectionFingerprint.instructionMatches[1]

            // A call with a single argument keeps that argument in the first listed register.
            val register = parseResolution.getInstruction<FiveRegisterInstruction>().registerC
            val qualityRegister = qualityArgument.getInstruction<FiveRegisterInstruction>().registerD

            // The quality is the resolution number itself, and the parser ignores anything below
            // three digits, so "Auto" or the lowest quality leaves the ladder untouched.
            addInstruction(parseResolution.index, "iget v$register, v$qualityRegister, LGe/b;->c:I")
            addInstruction(
                parseResolution.index + 1,
                "invoke-static {v$register}, Ljava/lang/String;->valueOf(I)Ljava/lang/String;",
            )
            addInstruction(parseResolution.index + 2, "move-result-object v$register")
        }
    }
}
