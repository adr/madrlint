package neutra1.linter.models.records;

import java.util.Optional;

public record Violation(String ruleId, String description, int lineNumber, Optional<String> representativeMadr) {
    
    public Violation(String ruleId, String description, int lineNumber){
        this(ruleId, description, lineNumber, Optional.empty());
    }
    
    public int getRuleNumber(){
        return Integer.parseInt(ruleId.substring(4, 6));
    }

}
