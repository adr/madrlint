package neutra1.linter.rules.impl.file;

import neutra1.linter.models.enums.Section;
import neutra1.linter.models.records.HeadingInfo;
import neutra1.linter.models.records.Violation;
import neutra1.linter.rules.HeadingRule;
import neutra1.linter.rules.IFileRule;

public class Rule06 extends HeadingRule implements IFileRule {

    private final String RULE_ID = "MADR06";

    public Rule06(){
        super();
    }

    @Override
    public int getRuleNumber(){
        return 6;
    }

    @Override
    public void check(){
        HeadingInfo context = getHeadingInfoByText(Section.CONTEXT.getPermittedTitles(), true);
        HeadingInfo consideredOptions = getHeadingInfoByText(Section.CONSIDERED_OPTIONS.getPermittedTitles(), true);
        HeadingInfo decisionOutcome = getHeadingInfoByText(Section.DECISION_OUTCOME.getPermittedTitles(), true);
        HeadingInfo prosAndCons = getHeadingInfoByText(Section.PROS_AND_CONS.getPermittedTitles(), true);
        HeadingInfo moreInformation = getHeadingInfoByText(Section.MORE_INFORMATION.getPermittedTitles(), true);
        HeadingInfo decisionDrivers = getHeadingInfoByText(Section.DECISION_DRIVERS.getPermittedTitles(), true);
        HeadingInfo consequences = getHeadingInfoByText(Section.CONSEQUENCES.getPermittedTitles(), true);
        HeadingInfo confirmation = getHeadingInfoByText(Section.CONFIRMATION.getPermittedTitles(), true);

        reportBadHeadingLevel(context, Section.CONTEXT.getPermittedHeadingLevel());
        reportBadHeadingLevel(consideredOptions, Section.CONSIDERED_OPTIONS.getPermittedHeadingLevel());
        reportBadHeadingLevel(decisionOutcome, Section.DECISION_OUTCOME.getPermittedHeadingLevel());
        reportBadHeadingLevel(prosAndCons, Section.PROS_AND_CONS.getPermittedHeadingLevel());
        reportBadHeadingLevel(moreInformation, Section.MORE_INFORMATION.getPermittedHeadingLevel());
        reportBadHeadingLevel(decisionDrivers, Section.DECISION_DRIVERS.getPermittedHeadingLevel());
        reportBadHeadingLevel(consequences, Section.CONSEQUENCES.getPermittedHeadingLevel());
        reportBadHeadingLevel(confirmation, Section.CONFIRMATION.getPermittedHeadingLevel());
    }

    private void reportBadHeadingLevel(HeadingInfo headingInfo, int permittedHeadingLevel){
        if (headingInfo == null){
            return;
        }
        int actualLevel = headingInfo.level();
        if (actualLevel != permittedHeadingLevel){
            String heading = headingInfo.text();
            StringBuilder desc = new StringBuilder();
            desc.append("Expected level " + permittedHeadingLevel + " for heading " + heading + ". Actual heading level found: " + actualLevel);
            reporter.report(new Violation(RULE_ID, desc.toString(), headingInfo.startLineNumber()));
        }
    }
}
