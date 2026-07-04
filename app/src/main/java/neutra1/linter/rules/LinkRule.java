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
        Path madrPath = Paths.get(LintContext.INTERNAL_PATH);
        Path containingDir = madrPath.getParent();
        Path resolvedPath = containingDir.resolve(urlText).normalize();
        return Files.exists(resolvedPath);    
    }
    
}
