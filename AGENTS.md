# AGENTS.md

Notes for coding agents working on madrlint. User-facing documentation is in [README.md](README.md).

## Build and run

- JDK 25, Gradle wrapper: `./gradlew build`, `./gradlew shadowJar` (produces `app/build/libs/app-all.jar`).
- Run: `java -jar app/build/libs/app-all.jar [options] <madrFile|dir>`, or with `./gradlew run --args="..."`.
- Rule 11 (link checking) calls a Rust library (`app/native/rust`, built with `cargo build --release`) through FFM.
  Without it, disable the rule locally: `-n 11`.
- `madr-samples/` has sample MADRs, some intentionally broken, for manual checks.

## Code layout

- Entry point: `app/src/main/java/neutra1/linter/Main.java` (picocli; exit code `1` when violations are found).
- Rules: `rules/impl/file/RuleNN.java` (`IFileRule`, one MADR file) and `rules/impl/directory/RuleNN.java` (`IDirectoryRule`, an ADR directory).
  A new rule must be registered in the list in `Main.call()`.
- Violations go to the `Reporter` singleton.

## Keep in sync

- `madrlint.java` (JBang launcher) lists every source file (`//SOURCES`) and dependency (`//DEPS`).
  Adding a source file or dependency means updating it; the JBang workflow verifies it compiles.
- `CHANGELOG.md` follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).
  Add user-facing changes under `[Unreleased]`, linking the PR. CI validates it with [heylogs](https://github.com/nbbrd/heylogs).
- Markdown is linted by markdownlint-cli2 (`.markdownlint-cli2.jsonc`).
