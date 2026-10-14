package app.morphe.patches.zona

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.extensions.InstructionExtensions.replaceInstruction
import app.morphe.patcher.patch.bytecodePatch
import com.android.tools.smali.dexlib2.AccessFlags

private object ZonaAnalyticsFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    strings = listOf("MOVIE_PLAYER_RESULT_SUCCESS"),
    returnType = "V",
)

private object ClickHouseSenderFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    strings = listOf("Get url for send event is failed"),
    returnType = "Ljava/lang/Object;",
)

private object AppMetricaActivateFingerprint : Fingerprint(
    definingClass = "Lio/appmetrica/analytics/AppMetrica;",
    name = "activate",
    parameters = listOf("Landroid/content/Context;", "Lio/appmetrica/analytics/AppMetricaConfig;"),
    returnType = "V",
)

private object AppMetricaActivateReporterFingerprint : Fingerprint(
    definingClass = "Lio/appmetrica/analytics/AppMetrica;",
    name = "activateReporter",
    parameters = listOf("Landroid/content/Context;", "Lio/appmetrica/analytics/ReporterConfig;"),
    returnType = "V",
)

private object AppMetricaSetDataSendingEnabledFingerprint : Fingerprint(
    definingClass = "Lio/appmetrica/analytics/AppMetrica;",
    name = "setDataSendingEnabled",
    parameters = listOf("Z"),
    returnType = "V",
)

private object AppMetricaLibraryAdapterActivateFingerprint : Fingerprint(
    definingClass = "Lio/appmetrica/analytics/AppMetricaLibraryAdapter;",
    name = "activate",
    parameters = listOf("Landroid/content/Context;", "Lio/appmetrica/analytics/AppMetricaLibraryAdapterConfig;"),
    returnType = "V",
)

private object AppMetricaLibraryAdapterReportEventFingerprint : Fingerprint(
    definingClass = "Lio/appmetrica/analytics/AppMetricaLibraryAdapter;",
    name = "reportEvent",
    parameters = listOf("Ljava/lang/String;", "Ljava/lang/String;", "Ljava/lang/String;"),
    returnType = "V",
)

private object AppMetricaLibraryAdapterSubscribeFingerprint : Fingerprint(
    definingClass = "Lio/appmetrica/analytics/AppMetricaLibraryAdapter;",
    name = "subscribeForAutoCollectedData",
    parameters = listOf("Landroid/content/Context;", "Ljava/lang/String;"),
    returnType = "V",
)

@Suppress("unused")
val disableAnalyticsPatch = bytecodePatch(
    name = "Disable analytics",
    description = "Disables internal event tracking, ClickHouse telemetry, crash reporting, and AppMetrica SDK to enhance privacy and battery life.",
    default = true,
) {
    compatibleWith(ZONA_COMPATIBILITY)

    execute {
        // 1. Neutralize Zona's internal analytics dispatcher (Lic/b)
        ZonaAnalyticsFingerprint.classDef.methods.forEach { method ->
            val params = method.parameterTypes.map { it.toString() }
            if (method.returnType == "V" && params.size in 2..3 && params.last() == "Ljava/util/Map;") {
                method.apply {
                    replaceInstruction(0, "return-void")
                }
            }
        }

        // 2. Neutralize ClickHouse HTTP sender (Ljc/c)
        ClickHouseSenderFingerprint.classDef.methods.forEach { method ->
            if (method.returnType == "Ljava/lang/Object;" && method.parameterTypes.size == 3) {
                method.apply {
                    replaceInstruction(0, "sget-object v0, Lkotlin/Unit;->INSTANCE:Lkotlin/Unit;")
                    addInstruction(1, "return-object v0")
                }
            }
        }

        // 3. Neutralize AppMetrica SDK initialization and data reporting
        listOf(
            AppMetricaActivateFingerprint,
            AppMetricaActivateReporterFingerprint,
            AppMetricaSetDataSendingEnabledFingerprint,
            AppMetricaLibraryAdapterActivateFingerprint,
            AppMetricaLibraryAdapterReportEventFingerprint,
            AppMetricaLibraryAdapterSubscribeFingerprint,
        ).forEach { fingerprint ->
            fingerprint.matchOrNull()?.method?.apply {
                replaceInstruction(0, "return-void")
            }
        }
    }
}
