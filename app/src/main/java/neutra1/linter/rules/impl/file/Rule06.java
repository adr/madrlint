package neutra1.linter.rules.impl.file;

import neutra1.linter.models.enums.MandatorySection;
import neutra1.linter.models.enums.OptionalSection;
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
        HeadingInfo context = getHeadingInfoByText(MandatorySection.CONTEXT.getPermittedTitles(), true);
        HeadingInfo consideredOptions = getHeadingInfoByText(MandatorySection.CONSIDERED_OPTIONS.getPermittedTitles(), true);
        HeadingInfo decisionOutcome = getHeadingInfoByText(MandatorySection.DECISION_OUTCOME.getPermittedTitles(), true);
        HeadingInfo prosAndCons = getHeadingInfoByText(OptionalSection.PROS_AND_CONS.getPermittedTitles(), true);
        HeadingInfo moreInformation = getHeadingInfoByText(OptionalSection.MORE_INFORMATION.getPermittedTitles(), true);
        HeadingInfo decisionDrivers = getHeadingInfoByText(OptionalSection.DECISION_DRIVERS.getPermittedTitles(), true);
        HeadingInfo consequences = getHeadingInfoByText(OptionalSection.CONSEQUENCES.getPermittedTitles(), true);
        HeadingInfo confirmation = getHeadingInfoByText(OptionalSection.CONFIRMATION.getPermittedTitles(), true);

        reportBadHeadingLevel(context, MandatorySection.CONTEXT.getPermittedHeadingLevel());
        reportBadHeadingLevel(consideredOptions, MandatorySection.CONSIDERED_OPTIONS.getPermittedHeadingLevel());
        reportBadHeadingLevel(decisionOutcome, MandatorySection.DECISION_OUTCOME.getPermittedHeadingLevel());
        reportBadHeadingLevel(prosAndCons, OptionalSection.PROS_AND_CONS.getPermittedHeadingLevel());
        reportBadHeadingLevel(moreInformation, OptionalSection.MORE_INFORMATION.getPermittedHeadingLevel());
        reportBadHeadingLevel(decisionDrivers, OptionalSection.DECISION_DRIVERS.getPermittedHeadingLevel());
        reportBadHeadingLevel(consequences, OptionalSection.CONSEQUENCES.getPermittedHeadingLevel());
        reportBadHeadingLevel(confirmation, OptionalSection.CONFIRMATION.getPermittedHeadingLevel());
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
