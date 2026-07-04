package neutra1.linter.rules.impl.directory;

import neutra1.linter.rules.NamingRule;
import neutra1.linter.rules.IDirectoryRule;

public class Rule44 extends NamingRule implements IDirectoryRule {
    
    private final String RULE_ID = "MADR44";

    @Override
    public void check(){
        reportMadrsWithNamingViolations();
    }

    @Override
    public int getRuleNumber(){
        return 44;
    }

    private void reportMadrsWithNamingViolations(){
        String openingMessage = "Invalid filename. Expected format: 'XXXX-brief-description.md'";
        this.report(madrsWithNamingViolations, RULE_ID, openingMessage);
    }
}
