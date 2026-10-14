package app.morphe.patches.zona

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.extensions.InstructionExtensions.getInstruction
import app.morphe.patcher.extensions.InstructionExtensions.replaceInstruction
import app.morphe.patcher.methodCall
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.string
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.iface.instruction.FiveRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction

/**
 * Stream selection service, which reads the resolution and the voice-over chosen by hand last
 * time out of the app settings memory.
 */
private object StreamSelectionFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.CONSTRUCTOR),
    parameters = listOf("LFg/n;", "LKd/i;", "LGe/b;", "LFg/h;", "LFg/p;", "Lhe/w0;", "Lda/M;"),
    filters = listOf(
        methodCall(
            definingClass = "LFg/s\$a;",
            name = "a",
            parameters = listOf("Ljava/lang/String;"),
            returnType = "LFg/s;",
        )
    ),
)

/**
 * Stream filter factory, which builds the strict filter chain used to pick the stream to play.
 */
private object StreamFilterChainFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "LCh/b;",
    parameters = listOf(),
    filters = listOf(
        string("customFilters+DEFAULT_FILTERS"),
        methodCall(
            definingClass = "LHg/q;",
            name = "<init>",
            parameters = listOf("LCh/d;", "LCh/d;"),
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
        // The remembered resolution is turned into a filter which outweighs the preferred quality
        // setting, so drop it by reading the resolution memory as if it were empty.
        StreamSelectionFingerprint.method.apply {
            val parseResolution = StreamSelectionFingerprint.instructionMatches.single()
            // The single argument invoke keeps its argument in the first listed register.
            val register = when (val instruction = parseResolution.instruction) {
                is FiveRegisterInstruction -> instruction.registerC
                else -> (instruction as OneRegisterInstruction).registerA
            }

            addInstruction(parseResolution.index, "const/4 v$register, 0x0")
        }

        // The strict chain only checks the remembered values, leaving the quality to the order the
        // sources come in. Make it apply the quality ladder as well.
        StreamFilterChainFingerprint.method.apply {
            val filterChain = StreamFilterChainFingerprint.instructionMatches[1]
            val arguments = filterChain.getInstruction<FiveRegisterInstruction>()

            replaceInstruction(
                filterChain.index,
                "invoke-direct {" +
                    "v${arguments.registerC}, " +
                    "v${arguments.registerE}, " +
                    "v${arguments.registerE}" +
                    "}, LHg/q;-><init>(LCh/d;LCh/d;)V",
            )
        }
    }
}
