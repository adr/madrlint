package neutra1.linter.rules.impl.directory;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import neutra1.linter.helper.LintContext;
import neutra1.linter.models.records.Violation;
import neutra1.linter.rules.IDirectoryRule;
import neutra1.linter.rules.NamingRule;

public class Rule41 extends NamingRule implements IDirectoryRule {

    private final String RULE_ID_A = "MADR41a";
    private final String RULE_ID_B = "MADR41b";

    public Rule41(){
        super();
    }

    @Override
    public int getRuleNumber(){
        return 41;
    }

    @Override
    public void check(){
        if (validMadrNames.isEmpty()){
            return;
        }
        List<Integer> madrIds = validMadrNames.stream().map(
            pathString -> extractMadrId(pathString))
            .sorted().toList();
        int smallestId = madrIds.get(0);
        List<String> smallestMadrs = validMadrNames.stream().filter(madr -> extractMadrId(madr) == smallestId).toList();
        String smallestMadr = smallestMadrs.get(0);
        if (smallestId >= 2){
            String formattedId = String.format("%04d", smallestId);
            String description = "Expected the smallest MADR Id to be either 0000 or 0001. Found " + formattedId;
            reporter.report(new Violation(RULE_ID_A, description, 1, Optional.of(smallestMadr)));
        }
        List<DisconnectedMadrPair> disconnectedMadrPairList = new ArrayList<>();
        for (int i = 0; i < madrIds.size() - 1; i++){
            int diff = madrIds.get(i+1) - madrIds.get(i);
            if (diff > 1){
                String smallerMadr = Paths.get(validMadrNames.get(i)).toString();
                String biggerMadr = Paths.get(validMadrNames.get(i+1)).toString();
                String expectedMadr = LintContext.USER_PATH + File.separator + String.format("%04d", madrIds.get(i) + 1) + "-*.md";
                disconnectedMadrPairList.add(new DisconnectedMadrPair(smallerMadr, biggerMadr, expectedMadr));
            }
        }
        if (!disconnectedMadrPairList.isEmpty()){
            for (DisconnectedMadrPair disconnectedMadrPair : disconnectedMadrPairList){
                String expectedMadr = disconnectedMadrPair.expectedMadr();
                String biggerMadr = disconnectedMadrPair.biggerMadr();
                String smallerMadr = disconnectedMadrPair.smallerMadr();
                StringBuilder desc = new StringBuilder("Expected " + expectedMadr + " after " + smallerMadr);
                reporter.report(new Violation(RULE_ID_B, desc.toString(), 1, Optional.of(biggerMadr)));
            }
        }
    }

    private int extractMadrId(String path) {
        return Integer.parseInt(Paths.get(path).getFileName().toString().substring(0 ,4));
    }
    
    private record DisconnectedMadrPair(String smallerMadr, String biggerMadr, String expectedMadr){}

}
