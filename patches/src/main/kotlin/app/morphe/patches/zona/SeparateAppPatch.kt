package app.morphe.patches.zona

import app.morphe.patcher.patch.resourcePatch
import app.morphe.patcher.patch.stringOption
import org.w3c.dom.Element

/**
 * Everything the app derives from its own package name at runtime: the permissions it declares for
 * itself, and the authorities of its five providers.
 */
private fun renameScopedName(original: String, replacement: String, name: String): String? = when {
    name.startsWith("$original.") -> name.replaceFirst(original, replacement)
    else -> null
}

@Suppress("unused")
val separateAppPatch = resourcePatch(
    name = "Install as a separate app",
    description = "Installs the patched app beside the original one under its own package name and app name, so the original keeps updating on its own.",
    default = false,
) {
    compatibleWith(ZONA_COMPATIBILITY)

    val packageNameOption = stringOption(
        key = "packageName",
        default = "free.zona.morphe",
        title = "Package name",
        description = "Package name of the separate install. Must be unique among the installed copies.",
        required = true,
    ) { it == null || it.matches(Regex("^[a-z]\\w*(\\.[a-z]\\w*)+$")) }

    val appNameOption = stringOption(
        key = "appName",
        default = "FreeZona Morphe",
        title = "App name",
        description = "Name to tell the separate install apart from the original one.",
        required = true,
    )

    val iconColorOption = stringOption(
        key = "iconColor",
        default = "#FF2B1E3F",
        title = "Icon background color",
        description = "ARGB color of the launcher icon background, e.g. #FF2B1E3F. The app's own mark is kept. Leave empty to keep the original icon.",
        required = false,
    ) { it == null || it.isBlank() || it.matches(Regex("^#([0-9A-Fa-f]{6}|[0-9A-Fa-f]{8})$")) }

    finalize {
        val originalPackageName = packageMetadata.packageName
        val packageName = packageNameOption.value!!

        document("AndroidManifest.xml").use { document ->
            document.documentElement.setAttribute("package", packageName)

            val usesPermissions = document.getElementsByTagName("uses-permission")
            val permissions = document.getElementsByTagName("permission")

            for (i in 0 until permissions.length) {
                val permission = permissions.item(i) as Element
                val oldName = permission.getAttribute("android:name")
                val newName = renameScopedName(originalPackageName, packageName, oldName) ?: continue

                permission.setAttribute("android:name", newName)

                for (j in 0 until usesPermissions.length) {
                    val usesPermission = usesPermissions.item(j) as Element
                    if (usesPermission.getAttribute("android:name") == oldName) {
                        usesPermission.setAttribute("android:name", newName)
                    }
                }
            }

            val providers = document.getElementsByTagName("provider")
            for (i in 0 until providers.length) {
                val provider = providers.item(i) as Element
                val authorities = provider.getAttribute("android:authorities").split(';')
                val renamed = authorities.map {
                    renameScopedName(originalPackageName, packageName, it) ?: "${packageName}_$it"
                }
                provider.setAttribute("android:authorities", renamed.joinToString(";"))
            }

            val appName = appNameOption.value!!
            val applications = document.getElementsByTagName("application")
            for (i in 0 until applications.length) {
                (applications.item(i) as Element).setAttribute("android:label", appName)
            }
        }

        // The adaptive launcher icon paints `color/zona_background` behind the app's mark. That color
        // belongs to the app, so the clone gets its own: point the icon at a color drawable instead
        // and recolor that one.
        val iconColor = iconColorOption.value
        val iconBackground = "res/drawable/ic_launcher_background.xml"
        if (!iconColor.isNullOrBlank() && iconBackground in listApkEntries()) {
            document(iconBackground).use { document ->
                (document.documentElement as Element).setAttribute("android:color", iconColor)
            }

            for (icon in listApkEntries("res/mipmap-anydpi-v26/")) {
                document(icon).use { document ->
                    val backgrounds = document.getElementsByTagName("background")
                    for (i in 0 until backgrounds.length) {
                        (backgrounds.item(i) as Element)
                            .setAttribute("android:drawable", "@drawable/ic_launcher_background")
                    }
                }
            }
        }
    }
}
