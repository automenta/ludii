// 
// Decompiled by Procyon v0.5.36
// 

package game.util.graph;

public class ItemScore implements Comparable<ItemScore>
{
    private final int id;
    private final double score;
    
    public ItemScore(final int id, final double score) {
        this.id = id;
        this.score = score;
    }
    
    public int id() {
        return this.id;
    }
    
    public double score() {
        return this.score;
    }
    
    @Override
    public int compareTo(final ItemScore other) {
        return (this.score == other.score) ? 0 : ((this.score < other.score) ? -1 : 1);
    }
}
