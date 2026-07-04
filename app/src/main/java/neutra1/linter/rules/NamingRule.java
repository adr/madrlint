package neutra1.linter.rules;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import neutra1.linter.helper.IgnoreFileHandler;
import neutra1.linter.helper.LintContext;
import neutra1.linter.models.records.Violation;

public abstract class NamingRule extends AbstractRule{
    
    protected final String ruleType = "Naming Rule";
    protected IgnoreFileHandler ignoreFileHandler;
    private final String MADR_FILE_NAMING_REGEX = "^\\d{4}-.+\\.md$";
    protected List<String> nonMarkdownFiles;
    protected List<String> madrsWithNamingViolations;
    protected List<String> validMadrNames;
    
    public NamingRule(){
        super();
        ignoreFileHandler = LintContext.getIgnoreFileHandler();
        nonMarkdownFiles = new ArrayList<>();
        madrsWithNamingViolations = new ArrayList<>();
        validMadrNames = new ArrayList<>();
        if (Files.isDirectory(Paths.get(LintContext.INTERNAL_PATH))){
            classifyFilesInMadrFolder();
        }
    }

    private List<Path> getAllFilesInMadrFolder(){
        String madrPath = LintContext.INTERNAL_PATH;
        Path parentFolder = Path.of(madrPath);
        DirectoryStream<Path> directoryStream = null;
        List<Path> paths = null;
        try {
            directoryStream = Files.newDirectoryStream(parentFolder);
            paths = new ArrayList<>();
            for (Path path : directoryStream){
                if(Files.isDirectory(path)){
                    continue;
                }
                Path fileName = path.getFileName();
                Path pathName = Paths.get(LintContext.USER_PATH, fileName.toString());
                paths.add(pathName);
            }
        }
        catch(IOException e){
            System.err.println("Error reading directory: " + madrPath + "\n." + 
            "Check for adherence to MADR naming conventions was not performed.");
        }
        finally{
            if (directoryStream != null){
                try {
                    directoryStream.close();
                } catch (IOException e) {
                    System.out.println("Error closing directory stream: " + e.getMessage());
                }
            }
        }
        return paths;
    }

    private void classifyFilesInMadrFolder(){
        List<Path> paths = getAllFilesInMadrFolder();
        if (paths == null){
            return;
        }
        Pattern pattern = Pattern.compile(MADR_FILE_NAMING_REGEX);
        for (Path filePath : paths) {
            if (ignoreFileHandler.isIgnored(filePath)){
                continue;
            }
            String fileName = filePath.getFileName().toString();
            String path = filePath.toString();
            if (!fileName.contains(".md")) {
                this.nonMarkdownFiles.add(path);
            }
            else if (!pattern.matcher(fileName).matches()) {
                this.madrsWithNamingViolations.add(path);
            }
            else {
                this.validMadrNames.add(path);
            }
        }
    }

    protected void report(List<String> files, String ruleId, String desc) {
        if (files.isEmpty()) {
            return;
        }
        files.stream().forEach(file -> {
            reporter.report(new Violation(ruleId, desc, 1, Optional.of(file)));
        });
    }
}
