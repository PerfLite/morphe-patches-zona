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
- `Hg.o` = ladder built from the `Preferred quality` setting (`r`, `g`, four `m` resolution-range
  filters, `b`); `Hg.m` is a resolution range predicate, `Hg.s` a voice-over name predicate.
- `Fg.s$a.a(String)` turns the remembered resolution string into `ResolutionKey`; an empty or
  null string yields `ResolutionKey(0)`, which is what makes `Hg.p.a()` skip the remembered
  resolution filter.
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

## Release

Publish the tag and the release asset only after the user has validated the patched app on
device; `patches-bundle.json` on `main` is what Morph serves to every install. Release notes on
GitHub Releases are written in two languages (English and Russian), following the structure of
`PerfLite/morphe-patches` (English section with compare links, followed by `---`, then Russian
section). The `description` field in `patches-bundle.json` is written in Russian.
