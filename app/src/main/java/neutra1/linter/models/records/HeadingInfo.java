package neutra1.linter.models.records;

import java.util.List;
import java.util.Map;

import com.vladsch.flexmark.util.ast.Node;

import neutra1.linter.core.ASTTraverser;

public record HeadingInfo(String text, String rawText, String anchorRefId, int level, 
                          int startLineNumber, List<Node> body, List<Node> bodyWithSubsections, Map<String, List<Node>> subHeadingBodyMap) {

    public String getBodyUnderHeading(boolean includeSubsections){
        StringBuilder sb = new StringBuilder();
        List<Node> body = (includeSubsections) ? this.bodyWithSubsections : this.body;
        for (Node node : body){
            String current = node.getChars().toString();
            sb.append(current);
        }
        return sb.toString();
    }

    public HeadingInfo findNextSameLevelHeading(){
        ASTTraverser traverser = ASTTraverser.getASTTraverserInstance();
        int targetLevel = this.level();
        List<HeadingInfo> headingInfoListSameLevel = traverser.getHeadingInfoList().stream().filter
                                        (headingInfo -> headingInfo.level() == targetLevel).toList();
        for (int i = 0; i < headingInfoListSameLevel.size(); i++){
            HeadingInfo current = headingInfoListSameLevel.get(i);
            if (current.equals(this)){
                if (i == headingInfoListSameLevel.size() - 1){
                    return null;
                }
                else {
                    return headingInfoListSameLevel.get(i + 1);
                }
            }
        }
        return null;
    }
}
