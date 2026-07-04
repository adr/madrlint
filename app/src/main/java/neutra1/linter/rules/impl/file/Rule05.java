package neutra1.linter.rules.impl.file;

import java.util.Comparator;
import java.util.List;

import neutra1.linter.models.records.HeadingInfo;
import neutra1.linter.models.records.Violation;
import neutra1.linter.rules.HeadingRule;
import neutra1.linter.rules.IFileRule;

public class Rule05 extends HeadingRule implements IFileRule {

    private final String RULE_ID = "MADR05";

    public Rule05(){
        super();
    }

    @Override
    public int getRuleNumber(){
        return 5;
    }

    @Override
    public void check(){
        List<HeadingInfo> headingList = traverser.getHeadingInfoList();
        int headingLevelOneCount = (int) headingList.stream().filter(headingInfo -> headingInfo.level() == 1).count();
        if (headingLevelOneCount > 1){
            List<HeadingInfo> headingsLevelOne = headingList.stream().
                filter(headingInfo -> headingInfo.level() == 1).
                sorted(Comparator.comparingInt(HeadingInfo::startLineNumber)).toList();
            List<HeadingInfo> violatingHeadings = headingsLevelOne.subList(1, headingsLevelOne.size());
            for (HeadingInfo heading : violatingHeadings){
                int startLineNumber = heading.startLineNumber();
                StringBuilder desc = new StringBuilder("Unexpected H1 heading: " + heading.text());
                reporter.report(new Violation(RULE_ID, desc.toString(), startLineNumber));      
            }
        }
    }
}
