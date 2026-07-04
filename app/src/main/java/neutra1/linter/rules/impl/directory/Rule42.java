package neutra1.linter.rules.impl.directory;

import neutra1.linter.rules.IDirectoryRule;
import neutra1.linter.rules.NamingRule;

public class Rule42 extends NamingRule implements IDirectoryRule {

    private final String RULE_ID = "MADR42";
   
    public Rule42(){
        super();
    }

    @Override
    public int getRuleNumber(){
        return 42;
    }

    @Override
    public void check() {
        reportNonMarkdownFiles();
    }

    private void reportNonMarkdownFiles(){
        StringBuilder openingMessage = new StringBuilder("Non-MADR found inside ADR directory");
        this.report(nonMarkdownFiles, RULE_ID, openingMessage.toString());
    }
}
