package neutra1.linter.rules.impl.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
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
        File lychee = null;
        try {
            lychee = extractBinary("lychee.exe");
        } catch (Exception e) {
            System.out.println("lychee executable not found. Checks for Rule 08 will not run");
            return;
        }
        List<LinkInfo> externalLinkList = traverser.getLinkInfoList();
        if (externalLinkList.isEmpty()) return;
        try {
            ProcessBuilder pb = new ProcessBuilder(
                lychee.getAbsolutePath(),
                "-f", "json",
                "-qq",
                "-" 
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream(), StandardCharsets.US_ASCII))) {
                for (LinkInfo link : externalLinkList) {
                    writer.write(link.url());
                    writer.newLine();
                }
                writer.flush();
            } 
            StringBuilder lycheeOutputSb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.US_ASCII))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lycheeOutputSb.append(line);
                }
            }
            int exitCode = process.waitFor();
            if (lycheeOutputSb.isEmpty()) {
                System.out.println("Lychee returned no output. Exit code: " + exitCode);
                return;
            }
            Gson gson = new Gson();
            String lycheeOutput = lycheeOutputSb.toString();
            FailedLinkReport failedLinkReport = gson.fromJson(lycheeOutput.substring(lycheeOutput.indexOf("{")), FailedLinkReport.class);
            if (failedLinkReport != null && !failedLinkReport.error_map.isEmpty()) {
                List<String> failedUrls = new ArrayList<>();
                for (Map.Entry<String, List<FailedLink>> entry : failedLinkReport.error_map.entrySet()) {
                    List<String> errors = entry.getValue().stream().map(error -> error.url()).toList();
                    failedUrls.addAll(errors);
                }
                List<LinkInfo> failedLinkInfos = externalLinkList.stream().filter(linkInfo -> failedUrls.contains(linkInfo.url())).toList();
                failedLinkInfos.stream().forEach(link -> {
                    int lineNumber = link.startLineNumber();
                    StringBuilder desc = new StringBuilder("Non-reachable link detected: " + link.url());
                    reporter.report(new Violation(RULE_ID, desc.toString(), lineNumber));
                });
            }
        } catch (IOException e) {
            System.out.println("IO Error: " + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("Process interrupted");
        }
    }

    private File extractBinary(String binaryName) throws IOException{
        InputStream inputStream = Rule11.class.getResourceAsStream("/" + binaryName);
        if (inputStream == null){
            System.out.println("Executable not found in Resources.");
            throw new IOException();
        }
        File temp = File.createTempFile(binaryName, null);
        temp.deleteOnExit();
        Files.copy(inputStream, temp.toPath(), StandardCopyOption.REPLACE_EXISTING);
        temp.setExecutable(true);
        return temp;
    }

    private record FailedLink(String url){}

    private record FailedLinkReport (Map<String, List<FailedLink>> error_map){}
}
