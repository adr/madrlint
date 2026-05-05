package neutra1.linter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import neutra1.linter.core.ASTTraverser;
import neutra1.linter.core.Reporter;
import neutra1.linter.helper.IgnoreFileHandler;
import neutra1.linter.helper.LintContext;
import neutra1.linter.rules.AbstractRule;
import neutra1.linter.rules.IFileRule;
import neutra1.linter.rules.impl.file.Rule01;
import neutra1.linter.rules.impl.file.Rule02;
import neutra1.linter.rules.impl.file.Rule03;
import neutra1.linter.rules.impl.file.Rule04;
import neutra1.linter.rules.impl.file.Rule05;
import neutra1.linter.rules.impl.file.Rule11;
import neutra1.linter.rules.impl.file.Rule06;
import neutra1.linter.rules.impl.file.Rule07;
import neutra1.linter.rules.impl.file.Rule21;
import neutra1.linter.rules.impl.file.Rule31;
import neutra1.linter.rules.impl.file.Rule12;
import neutra1.linter.rules.IDirectoryRule;
import neutra1.linter.rules.impl.directory.Rule41;
import neutra1.linter.rules.impl.directory.Rule42;
import neutra1.linter.rules.impl.directory.Rule43;
import neutra1.linter.rules.impl.directory.Rule44;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(
    name = "madrlint",
    description = "Lint MADR files",
    mixinStandardHelpOptions = true,
    customSynopsis = "madrlint [-hOqV] [-n <ruleNumber>[,ruleNumber...]>] [-o <outputFile>] <madrFile>",
    version="1.0.0"
)
public class Main implements Runnable {

    private final String RESET = "\u001B[0m";
    private final String RED   = "\u001B[31m";
    private final Path currentDir = Paths.get(System.getProperty("user.dir"));

    @Parameters(index = "0", description = "Path to MADR document.")
    private String userPath;
    @Option(names = {"--out", "-o"}, description = "Output the diagnostics to a file. If that file does not exist, it will be created.")
    private String outputFile;
    @Option(names = {"-O", "--override"}, description = "If the given output file already exists, it will be overwritten.")
    private boolean override;
    @Option(names = {"-q", "--quiet"}, description = "Information not relevant to the lint results will be suppressed.")
    boolean quietMode;
    @Option(names = {"-n", "--no-warn"}, description = "Disable warnings for certain rules. They can either be declared separately(e.g -n1 -n2) or chained together separated by comma(e.g -n1,2)", split = ",")
    private Set<Integer> disabledRules = new HashSet<>();
    @Option(names = {"-r", "--root"}, description = "Set the root directory of the project. Defaults to current working directory if not specified.")
    private String root;
    @Override
    public void run(){ 
        System.setProperty("jdk.httpclient.maxstreams", "200");
        String internalPath = currentDir.resolve(userPath).toString();
        LintContext.WORKING_DIR = currentDir.toString();
        LintContext.INTERNAL_PATH = internalPath;
        LintContext.USER_PATH = userPath;
        LintContext.PROJECT_ROOT = (root == null) ? currentDir.toString() : currentDir.resolve(Paths.get(root)).toString();
        IgnoreFileHandler ignoreFileHandler = LintContext.getIgnoreFileHandler();
        ASTTraverser astTraverser = ASTTraverser.getASTTraverserInstance();
        Reporter reporter = Reporter.getReporterInstance();
        List<AbstractRule> rules = List.of(
            new Rule01(),
            new Rule02(),
            new Rule03(),
            new Rule04(),
            new Rule05(),
            new Rule06(),
            new Rule07(),
            new Rule11(),
            new Rule12(),
            new Rule21(),
            new Rule31(),
            new Rule41(),
            new Rule42(),
            new Rule43(),
            new Rule44()
        );
        
        if (Files.isRegularFile(Paths.get(internalPath))){
            if (ignoreFileHandler.isIgnored(internalPath)){
                System.out.println(userPath +" was not linted due to its presence inside .madrlintignore.");
                System.exit(0);
            }
            try {
                astTraverser.traverse(readFile(internalPath));
                rules = rules.stream().filter(rule -> rule instanceof IFileRule).toList();
            }
            catch (IOException ioException){
                System.out.println(RED + "Error: unable to read input file " + userPath + RESET);
                System.exit(1);
            }
        }
        else if (Files.isDirectory(Paths.get(internalPath))){
            rules = rules.stream().filter(rule -> rule instanceof IDirectoryRule).toList(); 
        }
        else {
            System.out.println(RED + "Error: Path " + userPath + " does not exist." + RESET);
            System.exit(1);
        }
        // astTraverser.getOutput().toString().lines().forEach(System.out::println);
        rules.stream().filter(rule -> !disabledRules.contains(rule.getRuleNumber())).forEach(rule -> rule.check());
        int disabledRuleCount = disabledRules.size();
        int totalRuleCount = rules.size();
        int disabledRelevantRuleCount = rules.stream().filter(rule -> disabledRules.contains(rule.getRuleNumber())).toList().size();
        if (outputFile == null){  
            reporter.outputDiagnostics(disabledRuleCount, disabledRelevantRuleCount, totalRuleCount, quietMode);
        } 
        else {
            reporter.outputDiagnostics(outputFile, disabledRuleCount, disabledRelevantRuleCount, totalRuleCount, override, quietMode);
        }
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            args = new String[] {"-h"};
        }
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }

    private String readFile(String filePath) throws IOException{
        Path path = Paths.get(filePath);
        if (!path.isAbsolute()){
            path = currentDir.resolve(filePath);
        }
        String content = new String(Files.readAllBytes(path), StandardCharsets.US_ASCII);
        return content;
    }
    
}

