// 
// Decompiled by Procyon v0.5.36
// 

package supplementary.bgg;

import java.util.ArrayList;
import java.util.List;

public class User
{
    private final String name;
    private final List<Rating> ratings;
    private double match;
    
    public User(final String name) {
        this.ratings = new ArrayList<>();
        this.match = 0.0;
        this.name = name;
    }
    
    public String name() {
        return this.name;
    }
    
    public List<Rating> ratings() {
        return this.ratings;
    }
    
    public void add(final Rating rating) {
        this.ratings.add(rating);
    }
    
    public double match() {
        return this.match;
    }
    
    public void setMatch(final double value) {
        this.match = value;
    }
}
