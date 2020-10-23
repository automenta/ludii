// 
// Decompiled by Procyon v0.5.36
// 

package game.util.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Bucket
{
    private final List<ItemScore> items;
    private double total;
    
    public Bucket() {
        this.items = new ArrayList<>();
        this.total = 0.0;
    }
    
    public List<ItemScore> items() {
        return Collections.unmodifiableList(this.items);
    }
    
    public double mean() {
        return this.items.isEmpty() ? 0.0 : (this.total / this.items.size());
    }
    
    public void addItem(final ItemScore item) {
        this.items.add(item);
        this.total += item.score();
    }
}
