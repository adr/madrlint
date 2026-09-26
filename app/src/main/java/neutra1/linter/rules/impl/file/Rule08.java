package neutra1.linter.rules.impl.file;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import neutra1.linter.models.enums.Section;
import neutra1.linter.models.records.HeadingInfo;
import neutra1.linter.models.records.Violation;
import neutra1.linter.rules.HeadingRule;
import neutra1.linter.rules.IFileRule;

public class Rule08 extends HeadingRule implements IFileRule{
   
    private final String RULE_ID_A = "MADR08a";
    private final String RULE_ID_B = "MADR08b";

    @Override 
    public int getRuleNumber(){
        return 8;
    }

    @Override 
    public void check(){
        List<Section> templateDefinedSections = Arrays.asList(Section.values());
        List<Section> relevantSections = templateDefinedSections.stream().
            filter(section -> section.getPermittedHeadingLevel() == 2).
            sorted(Comparator.comparingInt(Section::getCanonicalOrder)).
            toList();
        List<PresentSection> presentSections = relevantSections.stream()
            .map(section -> {
                HeadingInfo info = getHeadingInfoByText(section.getPermittedTitles(), true);
                return info != null ? new PresentSection(section, info) : null;
            })
            .filter(Objects::nonNull)
            .toList();
        if (presentSections.size() < 2) { 
            return;
        }
        List<HeadingInfo> headingList = traverser.getHeadingInfoList();
        HeadingInfo title = headingList.stream().filter(headingInfo -> headingInfo.level() == 1).findFirst().orElse(null);
        if (title != null){
            boolean titleAfterOtherHeadings = traverser.getHeadingInfoList().stream().anyMatch(headingInfo -> title.startLineNumber() > headingInfo.startLineNumber());
            if (titleAfterOtherHeadings){
                String description = "Title should be at the top of the file";
                reporter.report(new Violation(RULE_ID_A, description, title.startLineNumber()));
            }
        }
        int numPresentSections = presentSections.size();
        int[] dp = new int[numPresentSections];
        int[] prev = new int[numPresentSections];
        Arrays.fill(dp, 1);
        Arrays.fill(prev, -1);

        int maxLen = 0;
        int bestEnd = 0;

        for (int i = 0; i < numPresentSections; i++) {
            int currentLine = presentSections.get(i).headingInfo().startLineNumber(); 
            for (int j = 0; j < i; j++) {
                int prevLine = presentSections.get(j).headingInfo().startLineNumber();
                if (prevLine < currentLine) {
                    if (dp[j] + 1 > dp[i]) {
                        dp[i] = dp[j] + 1;
                        prev[i] = j;
                    }
                }
            }
            if (dp[i] > maxLen) {
                maxLen = dp[i];
                bestEnd = i;
            }
        }

        Set<Integer> validIndices = new HashSet<>();
        int curr = bestEnd;
        while (curr != -1) {
            validIndices.add(curr);
            curr = prev[curr];
        }

        for (int i = 0; i < numPresentSections; i++) {
        if (!validIndices.contains(i)) {
            PresentSection misplaced = presentSections.get(i);
            String message = String.format(
                "Heading '%s' is out of order according to the template.",
                misplaced.headingInfo().text()
                );
            reporter.report(new Violation(RULE_ID_B, message, misplaced.headingInfo.startLineNumber()));
            }
        }

    }

    private record PresentSection(Section section, HeadingInfo headingInfo){}
}
