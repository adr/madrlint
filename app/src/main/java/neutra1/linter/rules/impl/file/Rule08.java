package neutra1.linter.rules.impl.file;

import neutra1.linter.models.records.HeadingInfo;
import neutra1.linter.rules.HeadingRule;
import neutra1.linter.rules.IFileRule;

public class Rule08 extends HeadingRule implements IFileRule{
    
    @Override 
    public int getRuleNumber(){
        return 8;
    }

    @Override 
    public void check(){
        
    }
}
