package neutra1.linter.rules.impl.file;

import neutra1.linter.models.enums.MandatorySection;
import neutra1.linter.models.enums.OptionalSection;
import neutra1.linter.models.records.HeadingInfo;
import neutra1.linter.models.records.Violation;
import neutra1.linter.rules.HeadingRule;
import neutra1.linter.rules.IFileRule;

public class Rule07 extends HeadingRule implements IFileRule {

    private final String RULE_ID_A = "MADR07a";
    private final String RULE_ID_B = "MADR07b";

    public Rule07(){
        super();
    }

    @Override
    public int getRuleNumber(){
        return 7;
    }

    @Override
    public void check(){
        HeadingInfo decisionOutcome = getHeadingInfoByText(MandatorySection.DECISION_OUTCOME.getPermittedTitles(), true);
        if (decisionOutcome == null){
            return;
        }
        String subsequenceDecisionOutcome = decisionOutcome.getBodyUnderHeading(true);
        HeadingInfo consequences = getHeadingInfoByText(OptionalSection.CONSEQUENCES.getPermittedTitles(), true);
        HeadingInfo confirmation = getHeadingInfoByText(OptionalSection.CONFIRMATION.getPermittedTitles(), true);
        reportFalseParenthood(RULE_ID_A, subsequenceDecisionOutcome, consequences);
        reportFalseParenthood(RULE_ID_B, subsequenceDecisionOutcome, confirmation);
    }

    private void reportFalseParenthood(String ruleId, String parentText, HeadingInfo childHeading){
        if (childHeading == null){
            return;
        }
        String rawTextChildHeading = childHeading.rawText();
        if (!parentText.contains(rawTextChildHeading)){
            String description;
            if (ruleId.equals(this.RULE_ID_A)){
                description = "Expected Consequences to be a H3 heading under Decision Outcome";
            }
            else {
                description = "Expected Confirmation to be a H3 heading under Decision Outcome";
            }
            reporter.report(new Violation(ruleId, description, childHeading.startLineNumber()));
        }  
    }
}
