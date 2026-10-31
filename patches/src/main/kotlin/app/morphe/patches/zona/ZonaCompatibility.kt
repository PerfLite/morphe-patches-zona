package app.morphe.patches.zona

import app.morphe.patcher.patch.ApkFileType
import app.morphe.patcher.patch.AppTarget
import app.morphe.patcher.patch.Compatibility

val OFFICIAL_ZONA_COMPATIBILITY = Compatibility(
    name = "Zona",
    packageName = "mobi.zona",
    apkFileType = ApkFileType.APK_REQUIRED,
    appIconColor = 0x202124,
    signatures = setOf("e85c1eb707f4c7987e51886a8b8eee159c4256aabfcca179626cd04aac4aa989"),
    targets = listOf(
        AppTarget(
            version = "3.0.77",
            versionCode = 1077,
            minSdk = 21,
            description = "Official Zona APK",
        ),
    ),
)

val FREE_ZONA_COMPATIBILITY = Compatibility(
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
            description = "APK: https://github.com/julay123/mobi.zona/releases",
        ),
        AppTarget(
            version = "3.0.76",
            versionCode = 1076,
            minSdk = 21,
            description = "APK: https://github.com/julay123/mobi.zona/releases",
        ),
    ),
)

val ZONA_COMPATIBILITY = arrayOf(OFFICIAL_ZONA_COMPATIBILITY, FREE_ZONA_COMPATIBILITY)
