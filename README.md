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
| [Install as a separate app](#install-as-a-separate-app) | Installs the patched app under its own package name beside the original one, so the original keeps updating on its own. |

**Where to get the app:** the patches target the FreeZona clone published by
[julay123/mobi.zona](https://github.com/julay123/mobi.zona) — [latest
release](https://github.com/julay123/mobi.zona/releases/latest) carries the three variants (RU,
World, original server set) for every Zona version. All three share the package `free.zona`, so they
replace each other; pick one and keep it as the copy that updates itself.

**Supported app version:** FreeZona `3.0.75` (versionCode `1075`). This is the only version the
patches declare compatibility with, so other versions are unsupported and untested.

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

### Install as a separate app

Off by default. A patched app is signed by Morphe, not by the app's own key, so it can never receive
the app's own updates. This patch gives the patched copy its own package name, permissions and
provider authorities, so both live on the phone at the same time: the original keeps updating
silently, and the patched one — under the name you choose — is your daily app.

When the original updates, patch it again (Morphe takes the running APK from the phone) and install
over the patched copy: same key, same package, so its history and settings survive.

**Setup, once:**

1. Download the APK you want from [julay123/mobi.zona — latest release](https://github.com/julay123/mobi.zona/releases/latest)
   and install it. Pick one variant (RU, World or the original server set) and stay with it: all three
   share the package `free.zona` and replace each other.
2. Leave that install **unpatched**. It is the donor: it keeps updating itself silently, and it is the
   source for every future patch.
3. In Morphe add this patch source (**GitHub** → `PerfLite/morphe-patches-zona`), select the donor app
   and tick **Install as a separate app** together with the patches you want.
4. Fill in the two options: a unique package name (for example `free.zona.ru.android`) and an app name
   you can tell apart on the home screen (for example `FreeZonaRu`).
5. Patch and install the result. A second app appears — that is the one you actually use; its history,
   bookmarks and settings are its own and are not shared with the donor.
6. When the donor updates, repeat steps 3–5. The patched app is replaced over itself, so nothing in it
   is lost.

Do not uninstall the donor and do not patch it: without it there is nothing left to update from, and
you would be back to hunting for a fresh APK.

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

Morphe signs every patched APK with its own key (`CN=Morphe`), created once on the first patch and kept
in Morphe's private storage. FreeZona is signed with the public AOSP `testkey` (`CN=Android`) and updates
itself silently, so a patched copy never receives that update, and a patched build never installs over
the original: Android identifies an app by its package name and accepts an update only from the same
signing key.

Two ways to live with it:

- **Recommended: [Install as a separate app](#install-as-a-separate-app)** — the patched copy takes its
  own package name and installs beside the original, which goes on updating. See the setup steps above.
- Without it, every new Zona version costs an uninstall of the patched app and its settings with it.

Re-signing with the app's own key is technically possible — Morphe can import a keystore built from that
public AOSP `testkey` pair — but it keeps one keystore for everything it patches, so the change would
apply to your other patched apps as well and give them nothing in return.

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
| **Install as a separate app** | Ставит пропатченную копию под собственным именем пакета рядом с исходной, чтобы исходная продолжала обновляться сама. |

**Где брать приложение:** патчи рассчитаны на FreeZona из
[julay123/mobi.zona](https://github.com/julay123/mobi.zona) — в [последнем релизе](https://github.com/julay123/mobi.zona/releases/latest)
лежат три варианта (RU, World и исходный набор серверов) под каждую версию Zona. Пакет у всех трёх
один — `free.zona`, поэтому они заменяют друг друга: выбери один и держи как обновляемый.

**Поддерживаемая версия:** FreeZona `3.0.75` (versionCode `1075`). Это единственная версия,
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

### Install as a separate app

По умолчанию выключен. Пропатченное приложение подписывает ключом Morphe, а не ключом приложения,
поэтому собственные обновления ему не приходят. Патч даёт пропатченной копии своё имя пакета, свои
разрешения и свои authority у провайдеров, так что оба приложения живут на телефоне одновременно:
исходное молча обновляется, а пропатченное — под тем именем, которое ты укажешь, — и есть рабочий
вариант.

Когда исходное обновилось, перепатчи его (Morphe берёт работающий APK прямо с телефона) и поставь
поверх пропатченного: пакет и подпись те же, поэтому история и настройки сохраняются.

**Как собрать один раз:**

1. Скачай нужный APK из [julay123/mobi.zona — последний релиз](https://github.com/julay123/mobi.zona/releases/latest)
   и поставь его. Выбери один вариант (RU, World или исходный набор серверов) и держись его: у всех
   трёх пакет `free.zona`, они заменяют друг друга.
2. Эту установку **не патчь** — это донор. Он молча обновляется сам и служит источником для каждого
   следующего патча.
3. В Morphe добавь источник патчей (**GitHub** → `PerfLite/morphe-patches-zona`), выбери приложение
   донора и отметь **Install as a separate app** вместе с нужными патчами.
4. Заполни две опции: уникальное имя пакета (например `free.zona.ru.android`) и имя приложения, чтобы
   не путать их на рабочем столе (например `FreeZonaRu`).
5. Запусти патчинг и поставь результат — появится второе приложение, вот им и пользуешься. Его история,
   закладки и настройки принадлежат только ему и с донором не общие.
6. Когда донор обновится, повтори пункты 3–5. Пропатченное заменит само себя, ничего внутри не потеряется.

Донора не удаляй и не патчь: без него нечем обновляться, и придётся снова искать свежий APK на стороне.

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

Morphe подписывает каждый пропатченный APK своим ключом (`CN=Morphe`): он создаётся один раз при первом
патче и хранится в приватном каталоге Morphe. FreeZona подписана публичным AOSP `testkey` (`CN=Android`)
и обновляется молча сама, поэтому пропатченной копии это обновление не приходит, а пропатченный APK не
встаёт поверх официального: Android считает приложение по имени пакета, а обновление принимает только от
того же ключа подписи.

Жить с этим можно двумя способами:

- **Рекомендуем [Install as a separate app](#install-as-a-separate-app)** — пропатченная копия берёт себе
  другое имя пакета и ставится рядом с оригиналом, который продолжает обновляться. Шаги выше.
- Без него каждая новая версия Zona стоит удаления пропатченного приложения — вместе с его настройками.

Переподписать приложение его же ключом технически можно: Morphe умеет импортировать keystore, собранный
из этой публичной пары AOSP `testkey`. Но keystore у Morphe один на все патчи, то есть подпись поменяется
у всех будущих сборок, а остальным приложениям (Google, VK, Telegram) это ничего не даст взамен.

## ⚖️ Лицензия

GPL-3.0 — см. [LICENSE](LICENSE) и [NOTICE](NOTICE).

Проект не связан с Zona. Патчите только те копии приложения, которыми вам разрешено пользоваться;
патчи предоставляются «как есть», без каких-либо гарантий.
