package neutra1.linter.rules.impl.file;

import java.util.List;

import com.vladsch.flexmark.ast.BulletListItem;

import neutra1.linter.models.records.BulletListItemInfo;
import neutra1.linter.models.records.Violation;
import neutra1.linter.rules.IFileRule;
import neutra1.linter.rules.SectionRule;

public class Rule31 extends SectionRule implements IFileRule {
    
    private final String RULE_ID = "MADR31";

    public Rule31(){
        super();
    }

    @Override
    public int getRuleNumber(){
        return 31;
    }

    @Override
    public void check(){
        List<BulletListItemInfo> bulletListInfoList = traverser.getBulletListInfoList();
        bulletListInfoList.stream().forEach(bulletListInfo ->  {
            BulletListItem item = bulletListInfo.item();
            String openingMarker = item.getOpeningMarker().toString();
            if (!openingMarker.equals("*")){
                StringBuilder desc = new StringBuilder("Expected '*' as list marker. Actual: " + openingMarker);
                int lineNumber = item.getStartLineNumber() + 1;
                reporter.report(new Violation(RULE_ID, desc.toString(), lineNumber));
            }
        });
    }
    
}
