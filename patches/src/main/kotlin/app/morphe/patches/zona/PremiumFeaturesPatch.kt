package app.morphe.patches.zona

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.extensions.InstructionExtensions.replaceInstruction
import app.morphe.patcher.patch.bytecodePatch
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction
import com.android.tools.smali.dexlib2.iface.reference.FieldReference

private object OrderFingerprint : Fingerprint(
    strings = listOf("Order(id=", ", isPaid="),
)

private object UserFingerprint : Fingerprint(
    strings = listOf(", isPremium=", ", premiumStartDate=", ", premiumEndDate="),
)

@Suppress("unused")
val premiumFeaturesPatch = bytecodePatch(
    name = "Premium features",
    description = "Activates premium VIP status, marks orders and subscription as active, and enables client-side premium features.",
    default = true,
) {
    compatibleWith(*ZONA_COMPATIBILITY)

    execute {
        // 1. Force Order / Subscription validity checks to always return true (active subscription).
        OrderFingerprint.matchOrNull()?.classDef?.let { orderClass ->
            val orderType = orderClass.type
            classDefForEach { classDef ->
                val hasOrderMethod = classDef.methods.any { method ->
                    val params = method.parameterTypes.map { it.toString() }
                    method.returnType == "Z" && (params == listOf(orderType, "J") || params == listOf(orderType))
                }
                if (hasOrderMethod) {
                    mutableClassDefBy(classDef).methods.forEach { method ->
                        val params = method.parameterTypes.map { it.toString() }
                        if (method.returnType == "Z" && (params == listOf(orderType, "J") || params == listOf(orderType))) {
                            method.apply {
                                replaceInstruction(0, "const/4 v0, 1")
                                addInstruction(1, "return v0")
                            }
                        }
                    }
                }
            }
        }

        // 2. Force User data class to always have isPremium = true in all constructors.
        UserFingerprint.matchOrNull()?.classDef?.let { userClass ->
            val premiumField = userClass.fields.firstOrNull { it.type == "Z" }
            if (premiumField != null) {
                val userMutableClass = mutableClassDefBy(userClass)
                userMutableClass.methods.forEach { method ->
                    if (method.name == "<init>") {
                        val instructions = method.implementation?.instructions ?: return@forEach
                        for (index in instructions.indices.reversed()) {
                            val instruction = instructions[index]
                            if (instruction.opcode == Opcode.IPUT_BOOLEAN &&
                                instruction is TwoRegisterInstruction &&
                                instruction is ReferenceInstruction
                            ) {
                                val fieldRef = instruction.reference as? FieldReference
                                if (fieldRef?.name == premiumField.name) {
                                    method.addInstruction(index, "const/4 v${instruction.registerA}, 1")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
