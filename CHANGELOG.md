# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Fixed

- madrlint now exits with code `1` when violations are found. [#24](https://github.com/adr/madrlint/pull/24)
- Link checking no longer silently ignores failed client requests. [#23](https://github.com/adr/madrlint/pull/23)

## [1.1.0] - 2026-09-27

### Added

- Rule checking that template-defined headings appear in the correct order. [#22](https://github.com/adr/madrlint/pull/22)
- `--output-format` option with `errorformat` and `github-actions`. [#7](https://github.com/adr/madrlint/pull/7)
- `--no-warn` accepts rule IDs (e.g., `MADR01`) in addition to rule numbers. [#8](https://github.com/adr/madrlint/pull/8)
- Support for running with JBang. [#2](https://github.com/adr/madrlint/pull/2)
- Native-image binary builds. [#3](https://github.com/adr/madrlint/pull/3)

### Changed

- Renamed project from madr-linter to madrlint. [#16](https://github.com/adr/madrlint/pull/16)
- Reworked link checking (rule 11). [#15](https://github.com/adr/madrlint/pull/15)
- Built jar is named `madrlint.jar`. [#19](https://github.com/adr/madrlint/pull/19)

[Unreleased]: https://github.com/adr/madrlint/compare/v1.1.0...HEAD
[1.1.0]: https://github.com/adr/madrlint/releases/tag/v1.1.0
