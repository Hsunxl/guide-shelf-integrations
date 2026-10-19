# Validation — 2026-10-20

- Java 21: successful Gradle builds against original Guide Shelf 1.3.6 and 1.3.7 using NeoForge 21.1.169 APIs; final build also passed with NeoForge **21.1.259** and Guide Shelf **1.3.7**, matching the current pack's NeoForge version. Runtime metadata respects Guide Shelf's minimum NeoForge 21.1.218 requirement.
- Policy tests: original entry precedence, duplicate suppression, stable IDs, ignored-ID filtering after merge, invalid wiki IDs, empty providers.
- SHA-512 verified official Guide Shelf **1.3.6 and 1.3.7** against Modrinth metadata; binaries remain in ignored local-libs, never packaged.
- `tools/check_api.py`: both original versions have exactly one `ArrayList.stream()` merge point followed by the original ignored-ID filter, and the expected opener descriptor.
- ABI checked: installed Oracle **1.4.0**, GuideME **21.1.19**, installed Ponder library. Oracle's second `openScreen` argument is `ResourceLocation`, not String; reset fields and parent-screen opener were checked.
- Optional providers are loaded reflectively only after the corresponding mod ID is present.

Still required: actual client Mixin application, click-through, return navigation, pinned entries and provider present/absent UI matrix. Static ABI checks and pure tests are not a substitute for these runtime UI tests. No existing mod jar, configuration, keybind or player save was replaced during development.

No upstream PR exists; see UPSTREAM.md for investigation and contribution prerequisites.
