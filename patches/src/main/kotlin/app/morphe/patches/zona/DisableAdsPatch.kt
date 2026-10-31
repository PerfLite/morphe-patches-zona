package app.morphe.patches.zona

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.extensions.InstructionExtensions.replaceInstruction
import app.morphe.patcher.patch.bytecodePatch
import com.android.tools.smali.dexlib2.AccessFlags

private object PlayerInstreamAdServiceFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    strings = listOf("Can't get ads for player ", "vast_url_with_params"),
    returnType = "Ljava/lang/Object;",
)

private object MovieGetAdsModelFingerprint : Fingerprint(
    definingClass = "Lmobi/zona/data/model/Movie;",
    name = "getAdsModel",
    returnType = "Lmobi/zona/data/model/Ads;",
)

private object YandexAdsProviderFingerprint : Fingerprint(
    definingClass = "Lcom/yandex/mobile/ads/core/initializer/MobileAdsInitializeProvider;",
    name = "onCreate",
    returnType = "Z",
)

@Suppress("unused")
val disableAdsPatch = bytecodePatch(
    name = "Disable ads",
    description = "Removes instream video pre-roll ads in the player, eliminates ad banners from the movie catalog, and neutralizes the Yandex Mobile Ads SDK.",
    default = true,
) {
    compatibleWith(*ZONA_COMPATIBILITY)

    execute {
        // 1. Cut off pre-roll instream video ads before movie playback starts.
        PlayerInstreamAdServiceFingerprint.matchOrNull()?.method?.apply {
            replaceInstruction(0, "sget-object v0, Lkotlin/collections/EmptyList;->INSTANCE:Lkotlin/collections/EmptyList;")
            addInstruction(1, "return-object v0")
        }

        // 2. Eliminate ad cards and banners from the movies/series grid.
        MovieGetAdsModelFingerprint.matchOrNull()?.method?.apply {
            replaceInstruction(0, "const/4 v0, 0")
            addInstruction(1, "return-object v0")
        }

        // 3. Prevent Yandex Mobile Ads SDK initialization in ContentProvider.
        YandexAdsProviderFingerprint.matchOrNull()?.method?.apply {
            replaceInstruction(0, "const/4 v0, 1")
            addInstruction(1, "return v0")
        }
    }
}
