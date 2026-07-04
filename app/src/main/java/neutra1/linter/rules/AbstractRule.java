package neutra1.linter.rules;

import neutra1.linter.core.ASTTraverser;
import neutra1.linter.core.Reporter;

public abstract class AbstractRule {

    protected final ASTTraverser traverser = ASTTraverser.getASTTraverserInstance();
    protected final Reporter reporter = Reporter.getReporterInstance();
    
    public AbstractRule(){}

    public abstract void check();

    public abstract int getRuleNumber();

}
