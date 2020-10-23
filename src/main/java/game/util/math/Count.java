// 
// Decompiled by Procyon v0.5.36
// 

package game.util.math;

import game.functions.ints.IntFunction;
import util.BaseLudeme;

public class Count extends BaseLudeme
{
    final String item;
    final IntFunction count;
    
    public Count(final String item, final IntFunction count) {
        this.item = item;
        this.count = count;
    }
    
    public String item() {
        return this.item;
    }
    
    public IntFunction count() {
        return this.count;
    }
}
