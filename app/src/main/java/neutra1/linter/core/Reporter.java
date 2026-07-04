package neutra1.linter.core;

import java.util.List;

import neutra1.linter.helper.LintContext;
import neutra1.linter.models.records.Violation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;

public class Reporter {
    
    private List<Violation> violationList;
    private static Reporter reporter = null;

    private Reporter() {
        this.violationList = new ArrayList<>();
    }

    public static Reporter getReporterInstance() {
        if (reporter == null) {
            reporter = new Reporter();
        }
        return reporter;
    }

    public void report(Violation violation) {
        violationList.add(violation);
    }

    public void outputDiagnostics(int disabledRuleCount, int disabledRelevantRuleCount, int totalRuleCount, boolean quietMode) {
        StringBuilder diagnosis = getDiagnosis();
        System.out.println(diagnosis.toString());
        if (!quietMode){
            printInformation(totalRuleCount, disabledRelevantRuleCount, disabledRuleCount);
        }
    }

    public void outputDiagnostics(String outputFile, int disabledRuleCount, int disabledRelevantRuleCount, int totalRuleCount, boolean override, boolean quietMode){
        StringBuilder diagnosis = getDiagnosis();
        Path currentDir = Paths.get(System.getProperty("user.dir"));
        Path outputPath = Paths.get(outputFile);
        if (!outputPath.isAbsolute()){
            outputPath = currentDir.resolve(outputPath);
        }
        try{
            if (override){
                Files.writeString(outputPath, diagnosis.toString(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            }
            else {
                Files.writeString(outputPath, diagnosis.toString());
            }
        }
        catch (IOException e){
            System.out.println("WARNING: writing to " + outputPath.toString() + " not successful." + "\n" +
                                "Output defaults to stdout. You can add flag \"--override\" to overwrite it.\n" + 
                                "If this message still shows up after adding that flag, check path validity and/or write access.\n");
            outputDiagnostics(disabledRuleCount, disabledRelevantRuleCount, totalRuleCount, quietMode);
        }
        if (!quietMode){
            printInformation(totalRuleCount, disabledRelevantRuleCount, disabledRuleCount);
        }
    }

    private StringBuilder getDiagnosis() {
        StringBuilder diagnosis = new StringBuilder();
        violationList.sort(Comparator.comparingInt(Violation::lineNumber));
        for (Violation v : violationList) {
            if (v.representativeMadr().isEmpty()){
                diagnosis.append(LintContext.USER_PATH + ":" + v.lineNumber() + " " + "[" + v.ruleId() + "]" + " " + v.description() + "\n");
            }
            else {
                diagnosis.append(v.representativeMadr().get() + ":" + v.lineNumber() + " " + "[" + v.ruleId() + "]" + " " + v.description() + "\n");
            }
        }
        return diagnosis;
    }

    private void printInformation(int totalRuleCount, int disabledRelevantRuleCount, int disabledRuleCount){
        StringBuilder info = new StringBuilder();
        int violationCount = violationList.size();
        int checkedRuleCount = totalRuleCount - disabledRelevantRuleCount;
        info.append("madrlint - Open-sourced under MIT License.\n");
        info.append("See https://github.com/Neutra1l/madr-linter for documentation and how to contribute.\n");
        info.append(violationCount + " violations detected from checks for " + 
                    checkedRuleCount + " rules " + "(" + disabledRuleCount + 
                    " user-suppressed rules, " + disabledRelevantRuleCount + " took effect).");
        System.out.println(info.toString());
    }
}
