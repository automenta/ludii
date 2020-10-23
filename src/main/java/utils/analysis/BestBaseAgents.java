// 
// Decompiled by Procyon v0.5.36
// 

package utils.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class BestBaseAgents
{
    private final Map<String, Entry> entries;
    
    public static BestBaseAgents loadData() {
        final Map<String, Entry> entries = new HashMap<>();
        final File file = new File("../AI/resources/Analysis/BestBaseAgents.csv");
        try (final BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                final String[] lineSplit = line.split(Pattern.quote(","));
                entries.put(lineSplit[0], new Entry(lineSplit[0], lineSplit[1], Float.parseFloat(lineSplit[2]), lineSplit[3], Long.parseLong(lineSplit[4])));
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return new BestBaseAgents(entries);
    }
    
    private BestBaseAgents(final Map<String, Entry> entries) {
        this.entries = entries;
    }
    
    public Entry getEntry(final String cleanGameName) {
        return this.entries.get(cleanGameName);
    }
    
    public Set<String> keySet() {
        return this.entries.keySet();
    }
    
    public static class Entry
    {
        private final String cleanGameName;
        private final String topAgent;
        private final float topScore;
        private final String bestHeuristics;
        private final long lastEvaluated;
        
        protected Entry(final String cleanGameName, final String topAgent, final float topScore, final String bestHeuristics, final long lastEvaluated) {
            this.cleanGameName = cleanGameName;
            this.topAgent = topAgent;
            this.topScore = topScore;
            this.bestHeuristics = bestHeuristics;
            this.lastEvaluated = lastEvaluated;
        }
        
        public String cleanGameName() {
            return this.cleanGameName;
        }
        
        public String topAgent() {
            return this.topAgent;
        }
        
        public float topScore() {
            return this.topScore;
        }
        
        public String bestHeuristics() {
            return this.bestHeuristics;
        }
        
        public long lastEvaluated() {
            return this.lastEvaluated;
        }
    }
}
