package neutra1.linter.rules.impl.file;

import java.util.List;

import neutra1.linter.ffi.LycheeWrapper;
import neutra1.linter.models.records.LinkInfo;
import neutra1.linter.models.records.Violation;
import neutra1.linter.rules.IFileRule;
import neutra1.linter.rules.LinkRule;

public class Rule11 extends LinkRule implements IFileRule {

    private final String RULE_ID = "MADR11"; 
    

    public Rule11() {
        super();
    }

    @Override
    public int getRuleNumber(){
        return 11;
    }

    @Override
    public void check(){
        List<LinkInfo> linkInfolist = traverser.getLinkInfoList();
        List<String> badUrls = LycheeWrapper.getBadLinks(linkInfolist);
        List<LinkInfo> badLinkInfoList = linkInfolist.stream().filter(linkInfo -> badUrls.contains(linkInfo.url())).toList();
        for (LinkInfo badLinkInfo : badLinkInfoList) {
            String description = "Invalid external link detected: " + badLinkInfo.url();
            reporter.report(new Violation(RULE_ID, description, badLinkInfo.startLineNumber()));
        }
    }

}
