# madr-linter

A Java-based linter for Markdown Architectural Decision Records (MADR).
Learn more about MADR in the [MADR template primer](https://www.ozimmer.ch/practices/2022/11/22/MADRTemplatePrimer.html).

## Run with JBang

With [JBang](https://www.jbang.dev/) installed (requires JRE 21 or newer), the
linter can be run directly from this repository without cloning:

```shell
jbang madrlint@adr/madrlint <madrFile>
```

Or install it as a local command:

```shell
jbang app install madrlint@adr/madrlint
madrlint <madrFile>
```

## How-to-test

1. Build and grab dependencies with: `.\gradlew build`
2. Test the tool with: `.\gradlew run --args="[Options] <madrFile>"`

### Arguments

```text
<madrFile>                     Path to the Markdown Architectural Decision Record
                               (MADR) file to lint.
```

### Options

```text
-h, --help                     Show help message and exit.
-V, --version                  Print version information and exit.
-n, --no-warn <disabledRules>  Disable warnings for certain rules, given as rule
                               number or rule ID (e.g -n1 or -nMADR01).
                               They can either be declared separately(e.g -n1 -n2) or 
                               chained together separated by comma(e.g -n1,2).            
-o, --out <outputFile>         Output the diagnostics to a file. If that file does
                               not exist, it will be created.
-O, --override                 If the given output file already exists, it will be
                               overwritten.
--output-format <format>       Format of the diagnostics. Valid values: errorformat,
                               github-actions. Defaults to errorformat.
-q, --quiet                    Information not relevant to the lint results will be
                               suppressed.
```

## Rules

Violations are reported with a rule ID such as `MADR01a`: the rule number, optionally followed by a letter identifying the specific check within that rule.
To disable a rule, pass its ID or number to `-n`/`--no-warn` (e.g., `-n MADR01` or `-n 1` disables all `MADR01` checks).

### Structural integrity

| ID       | Description                                                                                                                                                                           |
|----------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `MADR01` | The mandatory sections must be present: Title (`a`), Context and Problem Statement (`b`), Considered Options (`c`), and Decision Outcome (`d`).                                       |
| `MADR02` | No section may be empty.                                                                                                                                                              |
| `MADR03` | The chosen option must be stated inside Decision Outcome (`a`), and it must come first in that section (`b`). Expected format: `Chosen option: <chosen option>, because <rationale>`. |
| `MADR04` | The statement of the chosen option must be followed by a rationale: `Chosen option: <chosen option>, because <rationale>`.                                                            |
| `MADR05` | Only the title may have heading level 1.                                                                                                                                              |
| `MADR06` | Headings must have the levels given in the MADR template.                                                                                                                             |
| `MADR07` | Consequences (`a`) and Confirmation (`b`), if present, must be level-3 headings under Decision Outcome.                                                                               |

### Link validity

| ID       | Description                                                               |
|----------|---------------------------------------------------------------------------|
| `MADR11` | Internet links must be valid.                                             |
| `MADR12` | Local links (anchor links, local paths to resources, etc.) must be valid. |

### Syntactic validity

| ID       | Description                                                      |
|----------|------------------------------------------------------------------|
| `MADR21` | Metadata content must have proper syntax adhering to YAML rules. |

### Stylistic neutrality

| ID       | Description                                   |
|----------|-----------------------------------------------|
| `MADR31` | Asterisks (`*`) must be used as list markers. |

### Organizational soundness

| ID       | Description                                                                                                              |
|----------|--------------------------------------------------------------------------------------------------------------------------|
| `MADR41` | The numbering of ADRs within the ADR directory must start with either `0000` or `0001` (`a`) and feature no skips (`b`). |
| `MADR42` | No 'foreign' files may be present inside the ADR directory.                                                              |
| `MADR43` | Numberings must not collide between MADRs in the same folder.                                                            |
| `MADR44` | MADRs must follow the naming scheme `NNNN-brief-description.md`.                                                         |

The rules are based on the [MADR template](https://github.com/adr/madr/blob/develop/template/adr-template.md).
