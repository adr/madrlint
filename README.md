# madr-linter

A Java-based linter for Markdown Architectural Decision Records (MADR).
Learn more about MADR [here](https://www.ozimmer.ch/practices/2022/11/22/MADRTemplatePrimer.html).

## Run with JBang

With [JBang](https://www.jbang.dev/) installed (requires JRE 21 or newer), the
linter can be run directly from this repository without cloning:

```
jbang madrlint@Neutra1l/madrlint <madrFile>
```

Or install it as a local command:

```
jbang app install madrlint@Neutra1l/madrlint
madrlint <madrFile>
```

## How-to-test

1. Build and grab dependencies with: `.\gradlew build`
2. Test the tool with: `.\gradlew run --args="[Options] <madrFile>"`

**Arguments**
```
<madrFile>                     Path to the Markdown Architectural Decision Record
                               (MADR) file to lint.
```
**Options**

```
-h, --help                     Show help message and exit.
-V, --version                  Print version information and exit.
-n, --no-warn <disabledRules>  Disable warnings for certain rules. 
                               They can either be declared separately(e.g -n1 -n2) or 
                               chained together separated by comma(e.g -n1,2).            
-o, --out <outputFile>         Output the diagnostics to a file. If that file does
                               not exist, it will be created.
-O, --override                 If the given output file already exists, it will be
                               overwritten.
-q, --quiet                    Information not relevant to the lint results will be
                               suppressed.
```
## Rules

### Structural integrity

```
01. Mandatory sections: Title (a), Context and Problem Statement (b),
    Considered Options (c), Decision Outcome (d) must be present.
02. No sections may be empty.
03. Chosen option must be mentioned first (b) inside Decision Outcome (a). Expected format: 'Chosen option: <chosen option>, because <rationale>'.
04. Statement of chosen option must be followed by rationale in this format 'Chosen option: <chosen option>, because <rationale>'.
05. Only the title is allowed to have heading level 1.
06. Headings must have levels conforming to template's
07. Consequences (a) and Confirmation (b), if present, must be H3 headings under Decision Outcome
```

### Link validity

```
11. Internet links must be valid.
12. Local links (Anchor links, local paths to resources, etc) must be valid.
```

### Syntactic validity

```
21. Metadata content must have proper syntax adhering to YAML rules.
```

### Stylistic neutrality

```
31. Asterisks (*) must be used as list markers.
```

### Organizational soundness

```
41. The numberings of ADRs within the ADR directory must start with either 0000 or 
0001 (a) and feature no skips (b).
42. No 'foreign' files may be present inside ADR directory.
43. No collisions of numberings between MADRs in the same folder.
44. MADRs must follow the naming scheme: 'XXXX-brief-description.md'
```
The rules marked with * are rules for which checks are yet to be implemented.

See [here](https://github.com/adr/madr/blob/develop/template/adr-template.md) for the MADR template on which the rules are based.

