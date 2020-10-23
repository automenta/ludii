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

public class BestStartingHeuristics
{
    private final Map<String, Entry> entries;
    
    public static BestStartingHeuristics loadData() {
        final Map<String, Entry> entries = new HashMap<>();
        final File file = new File("../AI/resources/Analysis/BestStartingHeuristics.csv");
        try (final BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                final String[] lineSplit = line.split(Pattern.quote(","));
                entries.put(lineSplit[0], new Entry(lineSplit[0], lineSplit[1], Float.parseFloat(lineSplit[2]), Long.parseLong(lineSplit[3])));
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return new BestStartingHeuristics(entries);
    }
    
    private BestStartingHeuristics(final Map<String, Entry> entries) {
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
        private final String topHeuristic;
        private final float topScore;
        private final long lastEvaluated;
        
        protected Entry(final String cleanGameName, final String topHeuristic, final float topScore, final long lastEvaluated) {
            this.cleanGameName = cleanGameName;
            this.topHeuristic = topHeuristic;
            this.topScore = topScore;
            this.lastEvaluated = lastEvaluated;
        }
        
        public String cleanGameName() {
            return this.cleanGameName;
        }
        
        public String topHeuristic() {
            return this.topHeuristic;
        }
        
        public float topScore() {
            return this.topScore;
        }
        
        public long lastEvaluated() {
            return this.lastEvaluated;
        }
    }
}
