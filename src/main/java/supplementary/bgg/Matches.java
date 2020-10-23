// 
// Decompiled by Procyon v0.5.36
// 

package supplementary.bgg;

import java.util.ArrayList;
import java.util.List;

public class Matches
{
    private final BggGame game;
    private final List<Double> scores;
    private double score;
    private int numberMatches;
    
    public Matches(final BggGame game) {
        this.scores = new ArrayList<>();
        this.score = 0.0;
        this.numberMatches = 0;
        this.game = game;
    }
    
    public BggGame game() {
        return this.game;
    }
    
    public List<Double> scores() {
        return this.scores;
    }
    
    public double score() {
        return this.score;
    }
    
    public void add(final double value) {
        this.scores.add(value);
        this.score += value;
    }
    
    public void setScore(final double value) {
        this.score = value;
    }
    
    public void normalise() {
        if (this.scores.isEmpty()) {
            this.score = 0.0;
        }
        else {
            this.score /= this.scores.size();
        }
    }
    
    public int getNumberMatches() {
        return this.numberMatches;
    }
    
    public void setNumberMatches(final int numberMatches) {
        this.numberMatches = numberMatches;
    }
}
