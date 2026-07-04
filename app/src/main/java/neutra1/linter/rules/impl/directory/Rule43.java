package neutra1.linter.rules.impl.directory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import neutra1.linter.models.records.Violation;
import neutra1.linter.rules.IDirectoryRule;
import neutra1.linter.rules.NamingRule;

public class Rule43 extends NamingRule implements IDirectoryRule {

    private final String RULE_ID = "MADR43";

    public Rule43(){super();}

    @Override
    public int getRuleNumber(){
        return 43;
    }

    @Override
    public void check(){
        Map<String, List<Integer>> madrIdToIndicesMap = new HashMap<>();
        Map<String, List<Integer>> duplicateMap = new HashMap<>();
        List<Path> pathList = validMadrNames.stream().map(str -> Paths.get(str)).toList();
        List<String> fileNames = pathList.stream().map(path -> path.getFileName().toString()).toList();
        List<String> madrIds = fileNames.stream().map(name -> name.split("-")[0]).toList();
        for (int i = 0; i < madrIds.size(); i++){
            String currentMadrId = madrIds.get(i);
            madrIdToIndicesMap.computeIfAbsent(currentMadrId, j -> new ArrayList<>()).add(i);
        }
        madrIdToIndicesMap.forEach((key, value) -> {
            if (value.size() > 1){
                duplicateMap.put(key, value);
            }
        });
        if (!duplicateMap.isEmpty()){
            List<String> keys = Arrays.asList(duplicateMap.keySet().toArray(new String[0]));
            Collections.sort(keys);
            for (int i = 0; i < keys.size(); i++){
                String currentId = keys.get(i);
                List<Integer> currentIndices = duplicateMap.get(currentId);
                int duplicateCount = currentIndices.size();
                for (int j = 0; j < duplicateCount; j++){
                    StringBuilder desc = new StringBuilder("Duplicate ID " + "'" + currentId + "'. " + "Conflicting with " + (duplicateCount - 1) + " other files");
                    reporter.report(new Violation(RULE_ID, desc.toString(), 1, Optional.of(validMadrNames.get(currentIndices.get(j))))); 
                }
            }
        }
    }
}
