# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Android interval timer app (sections × train time × rest time) published to Google Play for both phone and Wear OS. Package name `dev.lucianosantos.intervaltimer`.

## Build / Test commands

Uses Gradle wrapper. JDK 17 is expected locally; CI uses JDK 17 for build/deploy and JDK 19 for the unit-test job.

- Unit tests + coverage (matches CI): `./gradlew testDebugUnitTest jacocoTestReport`
- Single test class: `./gradlew :core:testDebugUnitTest --tests "dev.lucianosantos.intervaltimer.core.viewmodels.SettingsViewModelTest"`
- Single test method: append `.methodName` to `--tests`.
- Release bundle for a target: `./gradlew :mobile:bundleRelease` or `./gradlew :wear:bundleRelease` (requires `keystore.properties` at repo root and `mobile/keystore.jks` / `wear/keystore.jks`; without these, release signing is silently skipped).
- Assemble debug APKs: `./gradlew :mobile:assembleDebug :wear:assembleDebug`.

Tests live only in `:core` — `:mobile` and `:wear` have `src/{test,androidTest}` directories but no test sources. CI test/coverage reporting only reads from `core/build/test-results/...`.

## Module architecture

Three Gradle modules, flat (no `:app`):

- `:core` — Android **library**, houses all shared domain logic. The interesting classes live here, not in the UI modules.
  - `core.service.CountDownTimerService` is an **abstract `LifecycleService`** — the actual timer state machine (`CountDownTimer` + `CountDownTimerHelper` + `AlertUserHelper`) runs here, exposed as `StateFlow`s via the `ICountDownTimerService` interface. `:mobile` and `:wear` each provide a thin concrete subclass (`CountDownTimerServiceMobile`, `CountDownTimerServiceWear`) that supplies an `OngoingActivityWrapper` (Wear-only behavior) and the `MainActivity` class reference for notification pending intents.
  - `core.data.TimerSettingsRepositoryImpl` persists settings. `SettingsViewModel` lives in core and is reused by both form factors.
  - `core.utils.AlarmManagerHelper` + `WakeReceiver` exist because a long-running countdown can be killed by Doze; the service schedules a wake alarm `remaining - 5s` out whenever `timerState` changes to keep the CPU alive through rest/train transitions. Touching the service lifecycle or timer flows without re-scheduling this alarm will reintroduce the bug.
  - Broadcast actions `INTERVAL_TIMER_ACTION_{PAUSE,RESUME,STOP,RESTART}` are the API surface for notification action buttons — defined as companion constants on `CountDownTimerService` and received by an internal `BroadcastReceiver` registered with `RECEIVER_EXPORTED`.
- `:mobile` — Android **application**, Compose + Material3. Uses Koin for DI (`Module.kt` → `appModule`, started in `MainApplication`). Integrates RevenueCat (subscriptions), AdMob (banner + interstitial), Play Billing, Firebase Analytics/Crashlytics.
- `:wear` — Android **application**, Wear Compose + `wear-ongoing` for ongoing activity notifications. Koin is started in `wear/.../MainApplication.kt` with a minimal module that binds `ITimerSettingsRepository` only — it exists because `core.CountDownTimerService` does `by inject()` for the repo and would crash on service construction otherwise. Everything else on wear (settings, viewmodels) is still instantiated directly at composition time (e.g. `TimerSettingsRepositoryImpl(LocalContext.current)` in the wear nav host); don't expand the wear Koin module unless you have a reason.
  - `foregroundServiceType="mediaPlayback"` on the service requires `FOREGROUND_SERVICE_MEDIA_PLAYBACK` in the wear manifest on API 34+; without it `startForeground` throws `SecurityException` when the UI unbinds.
  - `OngoingActivityWrapperImpl.allowForegroundService()` must stay `true` — it gates whether `CountDownTimerService.onUnbind()` promotes to foreground or stops the timer. Flipping it to false reintroduces the "timer dies when screen turns off" bug.

Both apps share `applicationId = "dev.lucianosantos.intervaltimer"` with the namespace `dev.lucianosantos.intervaltimer` — mobile and wear are published as separate tracks of the same Play Console listing.

## Version catalog & build-logic

- `build-logic/` is a **git submodule** (see `.gitmodules`) and is included as a composite build. `./gradlew` will fail to resolve plugins/versions without it — run `git submodule update --init --recursive` after a fresh clone.
- Version catalog lives at `build-logic/libs.versions.toml` (not the usual `gradle/libs.versions.toml`) — `settings.gradle.kts` wires it explicitly. Add dependencies and plugin aliases there, not in module `build.gradle.kts` files.
- `build-logic/convention/` contains Now-in-Android-style convention plugins (`nowinandroid.android.application`, etc.). **They are not currently applied** by any module — all three modules apply plugins directly via `libs.plugins.*`. Treat the convention plugins as unused scaffolding unless you're intentionally migrating.

## Versioning & release

- `scripts/set_app_version.py <target>` reads the latest git tag as `versionName` and synthesizes `versionCode = {tag}{GITHUB_RUN_NUMBER:04d}{10|30}` (mobile=10, wear=30). It rewrites `mobile/build.gradle.kts` or `wear/build.gradle.kts` in place by inserting lines immediately after `defaultConfig {` — don't hand-edit `versionName`/`versionCode` there, and be aware that local runs of this script will modify the file.
- Release flow is `.github/workflows/build_n_deploy_all.yml` triggered by pushing a numeric tag (`[0-9]+`). Builds mobile then wear sequentially via a strategy matrix with `max-parallel: 1`, uploads each `.aab` to Play Store Internal Testing via Fastlane (`fastlane/Fastfile` lanes `internal_mobile`, `internal_wear`, `production`). Secrets required: `PLAY_CONFIG_JSON`, `ANDROID_KEYSTORE_FILE` (base64), `KEYSTORE_KEY_ALIAS`, `KEYSTORE_KEY_PASSWORD`, `KEYSTORE_STORE_PASSWORD`, `GH_SSH_KEY` (for the submodule).
- `core/build.gradle.kts` enables `enableUnitTestCoverage = true` on debug — that's how `jacocoTestReport` exists without explicit plugin wiring. Coverage is uploaded to Codecov from CI.
