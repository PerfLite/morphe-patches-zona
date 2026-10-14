<div align="center">
<picture>
  <source width="420px" media="(prefers-color-scheme: dark)"
    srcset="https://raw.githubusercontent.com/MorpheApp/.github/refs/heads/main/profile/assets/morphe-wordmark/morphe_wordmark_dark.svg" />
  <img width="420px"
    src="https://raw.githubusercontent.com/MorpheApp/.github/refs/heads/main/profile/assets/morphe-wordmark/morphe_wordmark_light.svg"
    alt="Morphe wordmark" />
</picture>

[![Website](https://img.shields.io/badge/Website-morphe.software-gray?style=for-the-badge)](https://morphe.software/)
[![MorpheApp](https://img.shields.io/badge/GitHub-MorpheApp-gray?style=for-the-badge&logo=github)](https://github.com/MorpheApp)
[![Target app](https://img.shields.io/badge/FreeZona_World-3.0.75-green?style=for-the-badge)](#-patches)
[![License](https://img.shields.io/badge/License-GPL--3.0-blue?style=for-the-badge)](LICENSE)

&nbsp;
[![Download Morphe](https://raw.githubusercontent.com/MorpheApp/.github/refs/heads/main/profile/assets/download-morphe.svg)](https://morphe.software/)
&nbsp;
</div>

# 🧩 Zona patches

A [Morphe](https://morphe.software/) patch source for **FreeZona World** (`free.zona`).
[Morphe](https://github.com/MorpheApp) is based on the prior work of
[ReVanced](https://github.com/ReVanced).

## 🩹 Patches

| Patch | Description |
|-------|-------------|
| [Force landscape player](#force-landscape-player) | Switches to landscape orientation when movie or series playback starts. |
| [Preferred quality priority](#preferred-quality-priority) | Plays at the quality from the "Preferred quality" setting instead of the last quality chosen by hand, and keeps the chosen voice-over. |

**Supported app version:** FreeZona World `3.0.75` (versionCode `1075`). This is the only version
the patches declare compatibility with, so other versions are unsupported and untested.

### Force landscape player

Rotates the player to landscape as soon as playback of a movie or an episode starts, so you don't
have to turn the phone yourself.

### Preferred quality priority

Zona normally plays the stream you picked by hand last time, ignoring the "Preferred quality"
setting. This patch makes the player use the quality from the setting and keep the voice-over you
chose.

> [!WARNING]
> Works at less than 100%. When a title has no stream at the chosen quality with the chosen
> voice-over (older series usually have no 1080p), the player takes the closest available option,
> and the first switch to such an episode can take a few seconds while the sources load.

## ⬇️ Usage

1. Install [Morphe](https://morphe.software/).
2. Add a patch source: **GitHub** → `PerfLite/morphe-patches-zona`.
3. Select the FreeZona World APK, tick the patches you need and patch.

Morphe reads [`patches-bundle.json`](patches-bundle.json) from this repository and downloads the
`.mpp` from the latest release.

## 🧱 Build

```
./gradlew :patches:buildAndroid --offline
```

Output: `patches/build/libs/patches-<version>.mpp`. When releasing, bump `version` in
`gradle.properties` and mirror it in `patches-bundle.json` (`version`, `download_url` tag,
`created_at`, `description`), then publish the tag with the `.mpp` attached.

## 🔑 Signing and Zona updates

Morphe signs every patched APK with its own key (`CN=Morphe`), created once on the first patch and
kept in Morphe's private storage. The official FreeZona World APK is signed with the public AOSP
`testkey` (`CN=Android`), so an in-app Zona update cannot install over a patched install, and a
patched install cannot update an official one. This is normal for patched apps and has nothing to do
with these patches: when Zona releases a new version, uninstall the old one, patch the new APK and
install it — Zona's own settings are lost in the process.

To keep the in-app updater working instead, import a keystore built from that AOSP `testkey` pair
(Settings → System → Import keystore) **before** patching, and Morphe will sign with the same key the
official APKs use. Then official updates and re-patched builds install over each other with data
kept, and after an update you only re-patch — Morphe takes the running APK from the phone. Use alias
and key password `Morphe` and leave the keystore password empty; those are the values Morphe tries
automatically.

> [!CAUTION]
> Morphe keeps one keystore for everything it patches, so importing changes the signature of all your
> future patches, not only Zona. Other apps are signed by their own vendors (Google, VK, Telegram),
> so the AOSP key gives them nothing back — while the next patch of any of them will refuse to install
> over the currently patched copy until you uninstall it. If you patch more than Zona, keep the
> default key in your main Morphe and import the AOSP keystore only into a separate Morphe copy used
> for Zona (for example inside Samsung Secure Folder), which keeps its own keystore.

## ⚖️ License

GPL-3.0 — see [LICENSE](LICENSE) and [NOTICE](NOTICE).

This project is not affiliated with Zona. Patch only copies of the app you are allowed to use; the
patches are provided "as is", without warranty of any kind.

---

# 🧩 Патчи для Zona

Источник патчей для [Morphe](https://morphe.software/) к **FreeZona World** (`free.zona`).
Сам [Morphe](https://github.com/MorpheApp) вырос из [ReVanced](https://github.com/ReVanced).

## 🩹 Список патчей

| Патч | Описание |
|------|----------|
| **Force landscape player** | Переключает плеер в альбомный режим, когда начинается воспроизведение фильма или серии. |
| **Preferred quality priority** | Воспроизводит качество из настройки «Предпочтительное качество», а не то, что последний раз выбирали руками, и сохраняет выбранную озвучку. |

**Поддерживаемая версия:** FreeZona World `3.0.75` (versionCode `1075`). Это единственная версия,
для которой патчи объявляют совместимость, поэтому остальные не поддерживаются и не проверены.

### Force landscape player

Поворачивает плеер горизонтально в момент старта воспроизведения, поэтому не нужно крутить телефон
руками.

### Preferred quality priority

Обычно Zona играет тот поток, который вы последний раз выбрали вручную, и игнорирует настройку
«Предпочтительное качество». Патч возвращает приоритет настройке и сохраняет выбранную озвучку.

> [!WARNING]
> Работает не на 100%. Если у тайтла нет потока выбранного качества с выбранной озвучкой (у старых
> сериалов чаще всего нет 1080p), плеер берёт ближайший доступный вариант, а первое переключение на
> такую серию может занять несколько секунд — пока источники прогрузятся.

## ⬇️ Установка

1. Установите [Morphe](https://morphe.software/).
2. Добавьте источник патчей: **GitHub** → `PerfLite/morphe-patches-zona`.
3. Выберите APK FreeZona World, отметьте нужные патчи и запустите патчинг.

Morphe читает [`patches-bundle.json`](patches-bundle.json) из этого репозитория и скачивает `.mpp`
из последнего релиза.

## 🧱 Сборка

```
./gradlew :patches:buildAndroid --offline
```

На выходе: `patches/build/libs/patches-<version>.mpp`. При релизе поднимите `version` в
`gradle.properties` и зеркальте её в `patches-bundle.json` (`version`, тег в `download_url`,
`created_at`, `description`), после чего опубликуйте тег с приложенным `.mpp`.

## 🔑 Подпись и обновления Zona

Morphe подписывает каждый пропатченный APK своим ключом (`CN=Morphe`): он создаётся один раз при
первом патче и хранится в приватном каталоге Morphe. Официальный FreeZona World подписан публичным
AOSP `testkey` (`CN=Android`), поэтому официальное обновление не ставится поверх пропатченного, а
пропатченный — поверх официального. Это обычная ситуация для любых пропатченных приложений и к нашим
патчам отношения не имеет: вышла новая Zona → удаляем старую, патчим новый APK, ставим; настройки самой
Zona при этом теряются.

Если нужен работающий апдейт внутри приложения, — импортируй keystore, собранный из этой пары AOSP
`testkey` (Settings → System → Import keystore) **до** патча, и Morphe будет подписывать тем же ключом,
что и официальные сборки. Тогда официальные обновления и перепатченные сборки ставятся друг поверх
друга без потери данных, а после обновления остаётся только перепатчить: Morphe берёт работающий APK
прямо с телефона. Алиас и пароль ключа — `Morphe`, пароль хранилища оставь пустым: именно эти значения
Morphe подставляет сам.

> [!CAUTION]
> Keystore у Morphe один на все патчи, поэтому импорт меняет подпись всех будущих сборок, а не только
> Zona. Остальные приложения подписаны своими вендорами (Google, VK, Telegram), так что AOSP-ключ им
> ничего не даёт взамен — зато следующий патч любого из них не встанет поверх уже пропатченного, пока
> его не удалишь. Если патчишь не только Zona, держи в основном Morphe свой ключ, а AOSP keystore
> импортируй только в отдельную копию Morphe для Zona (например в Samsung Secure Folder) — у неё свой
> keystore.

## ⚖️ Лицензия

GPL-3.0 — см. [LICENSE](LICENSE) и [NOTICE](NOTICE).

Проект не связан с Zona. Патчите только те копии приложения, которыми вам разрешено пользоваться;
патчи предоставляются «как есть», без каких-либо гарантий.
