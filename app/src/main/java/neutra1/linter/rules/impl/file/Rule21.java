package neutra1.linter.rules.impl.file;

import java.util.List;

import com.github.sbaudoin.yamllint.LintProblem;

import neutra1.linter.models.records.MetadataInfo;
import neutra1.linter.models.records.Violation;
import neutra1.linter.rules.IFileRule;
import neutra1.linter.rules.MetadataRule;

public class Rule21 extends MetadataRule implements IFileRule {
    
    private final String RULE_ID = "MADR21";

    public Rule21(){
        super();
    }

    @Override
    public int getRuleNumber(){
        return 21;
    }

    @Override
    public void check(){
        List<MetadataInfo> metadataInfoList = traverser.getMetadataInfoList();
        if (metadataInfoList.size() == 0){
            return;
        }
        int startLineNumber = metadataInfoList.get(0).startLineNumber();
        List<LintProblem> problems = metadataInfoList.get(0).problems();
        if (problems.size() == 0){
            return;
        }
        for (int i = 0; i < problems.size(); i++){
            StringBuilder descBuilder = new StringBuilder();
            String current = problems.get(i).toString();
            String[] parts = current.split(":", 3);
            int line = Integer.parseInt(parts[0]) + startLineNumber;
            String desc = capitalize(parts[2]);
            descBuilder.append("YAML front matter: " + desc);
            reporter.report(new Violation(RULE_ID, descBuilder.toString(), line));
        }
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

}
