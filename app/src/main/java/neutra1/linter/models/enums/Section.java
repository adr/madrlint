package neutra1.linter.models.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import lombok.Getter;

@Getter
public enum Section {
    
    CONTEXT(List.of("Context and Problem Statement", "Context and Problem", "Context"), 2, null, SectionRequirement.MANDATORY, 1),
    CONSIDERED_OPTIONS(List.of("Considered Options", "Considered Alternatives", "Explored Options", "Explored Alternatives"), 2, null, SectionRequirement.MANDATORY, 3),
    DECISION_OUTCOME(List.of("Decision Outcome"), 2, null, SectionRequirement.MANDATORY, 4),

    PROS_AND_CONS(List.of("Pros and Cons of the Options"), 2, null, SectionRequirement.OPTIONAL, 5),
    MORE_INFORMATION(List.of("More Information", "Information"), 2, null, SectionRequirement.OPTIONAL, 6),
    DECISION_DRIVERS(List.of("Decision Drivers"), 2, null, SectionRequirement.OPTIONAL, 2),
    CONSEQUENCES(List.of("Consequences", "Positive Consequences", "Negative Consequences"), 3, Section.DECISION_OUTCOME.getPermittedTitles(), SectionRequirement.OPTIONAL),
    CONFIRMATION(List.of("Confirmation"), 3, Section.DECISION_OUTCOME.getPermittedTitles(), SectionRequirement.OPTIONAL);

    private final List<String> permittedTitles;
    private final int permittedHeadingLevel;
    private final List<String> permittedParentHeadings;
    private final SectionRequirement requirement;
    private final int canonicalOrder;

    private static final int NO_ORDER = -1;

    Section(List<String> permittedTitles, int permittedHeadingLevel, List<String> permittedParentHeadings, SectionRequirement requirement, int canonicalOrder) {
        this.permittedTitles = permittedTitles;
        this.permittedHeadingLevel = permittedHeadingLevel;
        this.permittedParentHeadings = permittedParentHeadings;
        this.requirement = requirement;
        this.canonicalOrder = canonicalOrder;
    }

    Section(List<String> permittedTitles, int permittedHeadingLevel, List<String> permittedParentHeadings, SectionRequirement requirement) {
        this.permittedTitles = permittedTitles;
        this.permittedHeadingLevel = permittedHeadingLevel;
        this.permittedParentHeadings = permittedParentHeadings;
        this.requirement = requirement;
        this.canonicalOrder = NO_ORDER;
    }

    public boolean matches(String title) {
        List<String> permittedTitlesLowerCase = permittedTitles.stream().map(name -> name.toLowerCase()).toList();
        return permittedTitlesLowerCase.contains(title.toLowerCase());
    }

    public static List<Section> getSectionsBasedOnRequirement(SectionRequirement requirement){
        List<Section> sections = Arrays.asList(Section.values());
        return sections.stream().filter(section -> section.requirement == requirement).toList();
    }
}
