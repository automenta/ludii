// 
// Decompiled by Procyon v0.5.36
// 

package game.util.equipment;

import annotations.Opt;
import util.BaseLudeme;

public class Hint extends BaseLudeme
{
    private final Integer hint;
    private final Integer[] region;
    
    public Hint(final Integer[] region, @Opt final Integer hint) {
        this.region = region;
        this.hint = ((hint == null) ? Integer.valueOf(0) : hint);
    }
    
    public Hint(final Integer site, @Opt final Integer hint) {
        this.hint = ((hint == null) ? Integer.valueOf(0) : hint);
        (this.region = new Integer[1])[0] = site;
    }
    
    public Integer hint() {
        return this.hint;
    }
    
    public Integer[] region() {
        return this.region;
    }
}
