package neutra1.linter.rules;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import neutra1.linter.helper.LintContext;

public abstract class LinkRule extends AbstractRule{

    protected final String ruleType = "Link Rule";

    public LinkRule(){
        super();
    }

    protected boolean isRootRelativeLink(String url){
        return url.startsWith("/");
    }

    protected boolean isAnchorLink(String url) {
        try {
            URI uri = new URI(url);
            if (uri.getFragment() != null && uri.getScheme() == null){
                 return true;
            } 
            else {
                return false;
            }
        }
        catch (Exception e) {
            return false;
        }
    }

    protected boolean isAbsolutePath(String path){
        try {
            return Paths.get(path).isAbsolute();
        } 
        catch (Exception e) {
            return false;
        }
    }

    protected boolean pathExists(String urlText) throws InvalidPathException {
        return Files.exists(resolveLocalPath(urlText));
    }

    /**
     * Resolves a local link target: a root-relative path against the project root,
     * any other path against the directory of the linted file.
     */
    protected Path resolveLocalPath(String urlText) throws InvalidPathException {
        if (isRootRelativeLink(urlText)) {
            return Paths.get(LintContext.PROJECT_ROOT).resolve(urlText.substring(1)).normalize();
        }
        Path madrPath = Paths.get(LintContext.INTERNAL_PATH);
        Path containingDir = madrPath.getParent();
        return containingDir.resolve(urlText).normalize();
    }
    
}
