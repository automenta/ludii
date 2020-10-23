// 
// Decompiled by Procyon v0.5.36
// 

package supplementary.bgg;

import java.util.ArrayList;
import java.util.List;

public class BggGame
{
    private final int index;
    private final int bggId;
    private final String name;
    private final String date;
    private final String[] details;
    private final List<Rating> ratings;
    
    public BggGame(final int index, final int bggId, final String name, final String date, final String[] details) {
        this.ratings = new ArrayList<>();
        this.index = index;
        this.bggId = bggId;
        this.name = name;
        this.date = date;
        this.details = details;
    }
    
    public int index() {
        return this.index;
    }
    
    public int bggId() {
        return this.bggId;
    }
    
    public String name() {
        return this.name;
    }
    
    public String date() {
        return this.date;
    }
    
    public String[] details() {
        return this.details;
    }
    
    public List<Rating> ratings() {
        return this.ratings;
    }
    
    public void add(final Rating rating) {
        this.ratings.add(rating);
    }
}
