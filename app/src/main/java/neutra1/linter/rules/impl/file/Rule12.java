package neutra1.linter.rules.impl.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import neutra1.linter.core.ASTTraverser;
import neutra1.linter.helper.LintContext;
import neutra1.linter.models.enums.LinkType;
import neutra1.linter.models.records.LinkInfo;
import neutra1.linter.models.records.Violation;
import neutra1.linter.rules.IFileRule;
import neutra1.linter.rules.LinkRule;

public class Rule12 extends LinkRule implements IFileRule {

    private final String RULE_ID_A = "MADR12a";
    private final String RULE_ID_B = "MADR12b";
    private final String RULE_ID_C = "MADR12c";
    private final String RULE_ID_D = "MADR12d";

    public Rule12(){
        super();
    }

    @Override
    public int getRuleNumber(){
        return 12;
    }

    @Override
    public void check(){
        Map<String, Integer> badLocalPaths = new HashMap<>();
        Map<String, Integer> absolutePaths = new HashMap<>();
        Map<String, Integer> badAnchorLinks = new HashMap<>();
        Map<String, Integer> badRootRelativePaths = new HashMap<>();
        List<LinkInfo> localLinks = traverser.getLinkInfoList().stream().filter(linkInfo -> linkInfo.linkType() == LinkType.LOCAL).toList();
        for (LinkInfo localLink : localLinks) {
            String url = localLink.url();
            int startLineNumber = localLink.startLineNumber();
            if (isAbsolutePath(url)){
                absolutePaths.put(url, startLineNumber);
            }
            else if (isAnchorLink(url)){
                checkAnchorLink(url, startLineNumber, badLocalPaths, badAnchorLinks);
            }
            else if (isRootRelativeLink(url)){
                String urlRelativized = url.substring(1);
                Path resolved = Paths.get(LintContext.PROJECT_ROOT).resolve(urlRelativized);
                if (!Files.exists(resolved)){
                    badRootRelativePaths.put(url, startLineNumber);
                }
            }
            else {
                try{
                    boolean exists = pathExists(url);
                    if (!exists){
                        badLocalPaths.put(url, startLineNumber);
                    }
                }
                catch (Exception e){
                    badLocalPaths.put(url, startLineNumber);
                }
            }
        }
        buildDescription("Invalid local path detected: ", badLocalPaths, RULE_ID_A);
        buildDescription("Invalid anchor link detected: ", badAnchorLinks, RULE_ID_B);
        buildDescription("Invalid root relative path detected: ", badRootRelativePaths, RULE_ID_C);
        buildDescription("Non-renderable absolute path detected:", absolutePaths, RULE_ID_D);    
    } 

    /**
     * Checks a link with a fragment.
     * A fragment alone refers to a heading of the linted file.
     * A fragment after a path refers to a heading of that file, which must exist.
     * Fragments into files other than Markdown are not checked.
     */
    private void checkAnchorLink(String url, int lineNumber, Map<String, Integer> badLocalPaths, Map<String, Integer> badAnchorLinks){
        int hashIndex = url.indexOf('#');
        String pathPart = url.substring(0, hashIndex);
        String fragment = url.substring(hashIndex + 1);
        List<String> slugList;
        if (pathPart.isEmpty()){
            slugList = traverser.getHeadingSlugList();
        }
        else {
            Path target;
            try {
                target = resolveLocalPath(pathPart);
            }
            catch (InvalidPathException e){
                badLocalPaths.put(url, lineNumber);
                return;
            }
            if (!Files.exists(target)){
                badLocalPaths.put(url, lineNumber);
                return;
            }
            if (!Files.isRegularFile(target) || !isMarkdownFile(target)){
                return;
            }
            try {
                slugList = ASTTraverser.headingSlugsOf(Files.readString(target));
            }
            catch (IOException e){
                badLocalPaths.put(url, lineNumber);
                return;
            }
        }
        if (!slugList.contains(fragment)){
            badAnchorLinks.put(url, lineNumber);
        }
    }

    private boolean isMarkdownFile(Path path){
        String name = path.getFileName().toString().toLowerCase();
        return name.endsWith(".md") || name.endsWith(".markdown");
    }

    private void buildDescription(String foreword, Map<String, Integer> brokenLinks, String ruleId){
        if (brokenLinks.isEmpty()){
            return;
        }
        brokenLinks.forEach((url, lineNumber) -> {
            String desc = foreword + url;
            reporter.report(new Violation(ruleId, desc, lineNumber));
        });
    }
}
