# Zona patches

Morphe patch source for FreeZona World (`free.zona`). Loaded into Morph as a GitHub source;
Morph reads `patches-bundle.json` from this directory and downloads the `.mpp` from the
`download_url`.

## Build

```
./gradlew :patches:buildAndroid --offline
```

Output: `patches/build/libs/patches-<version>.mpp`. Bump `version` in `gradle.properties` and
mirror it in `patches-bundle.json` (`version`, `download_url` tag, `created_at`, `description`).

`patches-bundle.json` is parsed strictly by Morph: `created_at` must be a `LocalDateTime`
pattern without a zone designator (`2026-10-14T00:00:00`). A trailing `Z` makes Morph report
"could not load patch bundle" with no further detail.

The `morphe-patcher` and `morphe-patches-library` checkouts under `C:\Dev\Morphe` are pulled in
with `includeBuild`, so they override every version pin in `gradle/libs.versions.toml`. When a
build fails on an API that should exist, check those checkouts first.

## Reverse engineering anchors (3.0.75, versionCode 1075)

Obfuscated with R8; the useful classes are two-letter packages. Each decompiled class carries a
`SourceDebugExtension` SMAP with the original Kotlin file name, which is how these were mapped.

- `Hg.p` = `ru/zona/app/stream/movie/StreamMultilevelFilterFactoryWithProblemAware`. Builds the
  stream-selection filter chain. `Hg.p.a()` has two branches: with no remembered selection it
  passes the default ladder to both slots of `Hg.q`, with a remembered selection it passes
  `customFilters` (remembered resolution + voice-over) as the strict chain and
  `customFilters+DEFAULT_FILTERS` as the scoring chain.
- `Hg.q(strict, scoring)` — `Ch.d.b()` is the strict BEST chain (applies every filter as a hard
  predicate and stops, returning an empty list, as soon as candidates run out), `Ch.d.a()` is the
  scored chain (`Ch.c` multiplies each filter score by a boost, and the remembered values are
  boosted by the number of custom filters, which outranks the preferred-quality ladder).
- `Hg.o` = ladder built from the `Preferred quality` setting, in order: `Hg.r` (stream is not a
  trailer), `Hg.g` (stream language equals `ru`), `Hg.m(down2, up2)`, `Hg.m(down2, up1)`,
  `Hg.m(down1, up1)`, `Hg.m(preferred, preferred)`, `Hg.b` (`rc.I.s`, an unlabelled string field
  that must be empty). `Hg.m` is a resolution range predicate
  scoring 1.0 or 0.0, `Hg.s` a voice-over name predicate. `Ge.b.c` is the resolution number of a
  quality (`Ge.c.a` steps up, `Ge.c.b` steps down, both clamped), so the ladder scores a stream
  that is exactly the preferred quality 4 points, one step away 3, and the voice-over alone is
  worth 7 — in the scored chain the voice-over always outranks the quality.
- Because `Ch.d.b()` ANDs every predicate, putting the whole ladder into the strict chain makes it
  miss often (a 1080p stream with a subtitle track, or a non-`ru` label, is ruled out), and a miss
  costs a wait for all sources or the 10 second `Fg.o.f6080m` timeout before the scored chain
  picks the voice-over instead. Keep the strict chain to the two things the user asked for.
- `Fg.s$a.a(String)` turns a resolution string into `ResolutionKey`; its regex needs three or four
  digits, so fewer (an empty value, or `SD` = 0) yields `ResolutionKey(0)`, which is what makes
  `Hg.p.a()` skip the resolution filter altogether.
- `Fg.o.<init>` is the single place in the whole app that reads the remembered resolution
  (`Kd.i.c(J)`), and the `Fg.f$c` it builds there is used both for the filter chain and for the
  highlighted row of the quality sheet (`Fg.f$b.c`, read by `oe.d`/`oe.g.b`). `Ge.b` is the third
  parameter of the same constructor, so one insertion there moves the whole preference from the
  hand-picked value to the setting.
- `Fg.o` = the stream selection service. `Fg.g`/`Fg.o.i()`/`Fg.o.j()` show the degradation path:
  a strict miss falls back to the scored chain once all sources are processed or on timeout, so a
  strict chain that is too narrow delays the first frame instead of breaking playback.
- The remembered hand-picked selection lives in `Bd.v`, RAM only, a single global record, written
  from `p147he.C3931u.invoke` when the quality sheet is used (`p260ne.b.a(Ge.b)`). The
  `rc.I.q`/`rc.I.r` track indexes seen there are not the mechanism that changes the voice-over.

## Inline smali gotchas

- Method references in inline smali have no colon before the prototype:
  `LHg/q;-><init>(LCh/d;LCh/d;)V`. The `.` and `:` forms in dexdump output are display only.
- A single-argument `invoke-static {v2}` is dex format 35c, i.e. `FiveRegisterInstruction`, whose
  listed registers are `registerC`, `registerD`, `registerE` — there is no `registerA`.
  `ThreeRegisterInstruction` is format 23x and does not apply to invokes.
- `replaceInstruction(index, smali)` compiles the string but inserts only its FIRST instruction,
  and `replaceInstructions(index, smali)` deletes as many existing instructions as the new list
  has. To add several instructions while keeping the anchored call, loop `addInstruction(index + n)`
  instead. A silently dropped `invoke` leaves a stray `move-result-object` and an uninitialized
  register, which the no-op dex verifier does not catch.

## Signing and app updates

The distributed FreeZona World APK is signed with the **AOSP `testkey`** pair
(`build/make/target/product/security/testkey.pk8` / `.x509.pem`, cert SHA-256
`a40da80a59d170caa950cf15c18c454d47a39b26989d8b640ecd745ba71bf5dc`, the value
`ZonaCompatibility.signatures` expects, valid until 2035-07-17). That is why its in-app "обновить"
button installs updates in place.

Morph signs its output with its own persistent key (`CN=Morphe`, BKS at `signing/morphe.keystore`
in its private dir), so a patched install can never receive an official update. To keep the
in-app updater working, sign with the AOSP testkey instead: build a BKS from that pair with
`ApkSigner.newKeyStore(...)` and `store(out, null)` (no store password), alias and key password
both `Morphe` — those are in Morph's auto-try lists (`ImportExportViewModel.kt:645-646`), so
Settings → System → Import keystore takes the file in one tap without asking credentials.
Verified: such a keystore loads through `ApkSigner.readKeyStore(stream, null)`, and
`ApkUtils.signApk` with it produces an APK that `apksigner` reports as v1+v2+v3 with
`CN=Android` and the matching digest.

Switching to it costs one final uninstall of the patched app (signature changes from `CN=Morphe`).
After that official → patched and patched → official both install as updates with app data kept.
The keystore is deliberately not committed here (the key pair is public AOSP material either way).

**Not the shipped answer.** He rejected keystore swapping: Morph keeps one global keystore, so it would
change the signature of every app he patches while helping only Zona. The solution that went into
`separateAppPatch` ("Install as a separate app", v1.2.0) is a package rename instead — the patched copy
gets its own package, permissions and provider authorities and installs beside the self-updating
original, which stays the donor. Prefer that framing in docs and issues; keep the testkey recipe above
as background, not as a recommendation.

## Release

Publish the tag and the release asset only after the user has validated the patched app on
device; `patches-bundle.json` on `main` is what Morph serves to every install. Upload the exact
`.mpp` he validated — do not rebuild after validation, since a recompile changes the asset even
when the patch logic is unchanged. Verify the published asset with `gh release view --json assets`
(`digest` must equal `sha256sum` of the local file) and that `download_url` in
`patches-bundle.json` matches the tag. On device the applied patch shows up as a small extra dex
(`classes5.dex` with `LFg/o;` and `LCd/f;`), and `dumpsys package free.zona` `lastUpdateTime`
confirms the re-patch. Morph strips `META-INF/*.SF|*.RSA` and re-signs in the APK Signing Block,
so the absence of v1 signature files is normal. Release notes on
GitHub Releases are written in two languages (English and Russian), following the structure of
`PerfLite/morphe-patches` (English section with compare links, followed by `---`, then Russian
section). The `description` field in `patches-bundle.json` is written in Russian.
