package neutra1.linter.rules.impl.file;

import java.util.List;

import com.vladsch.flexmark.util.ast.Node;

import neutra1.linter.models.enums.DecisionOutcomeElements;
import neutra1.linter.models.enums.MandatorySection;
import neutra1.linter.models.records.HeadingInfo;
import neutra1.linter.models.records.Violation;
import neutra1.linter.rules.IFileRule;
import neutra1.linter.rules.SectionRule;

public class Rule03 extends SectionRule implements IFileRule {

    private final String RULE_ID_A = "MADR03a";
    private final String RULE_ID_B = "MADR03b";

    public Rule03(){super();}

    @Override
    public int getRuleNumber(){
        return 3;
    }

    @Override
    public void check() {
        HeadingInfo decisionOutcome = getHeadingInfoByText(MandatorySection.DECISION_OUTCOME.getPermittedTitles());
        if (decisionOutcome == null){
            return;
        }
        Node nodeDecisionOutcome = findNodeByKeywords(decisionOutcome.body(), DecisionOutcomeElements.CHOSEN_OPTION.getKeywords());
        if (nodeDecisionOutcome == null) {
            String description = "Expected statement of chosen option inside Decision Outcome. Expected format: 'Chosen option: <chosen option>, because <rationale>'";
            int lineNumber = decisionOutcome.startLineNumber();
            reporter.report(new Violation(RULE_ID_A, description, lineNumber));
            return;
        }
        Node firstNodeDecisionOutcome = decisionOutcome.body().get(0);
        String firstNodeText = firstNodeDecisionOutcome.getChars().toString();
        List<String> firstNodeLines = List.of(firstNodeText.split("\n"));
        String firstLine = firstNodeLines.get(0);
        if (!DecisionOutcomeElements.CHOSEN_OPTION.matches(firstLine, true)) {
            String description = "Decision Outcome section must start with statement of chosen option";
            int lineNumber = getLineNumberByContent(decisionOutcome,
                DecisionOutcomeElements.CHOSEN_OPTION.getKeywords());
            reporter.report(new Violation(RULE_ID_B, description, lineNumber));  
        }
    }
}



