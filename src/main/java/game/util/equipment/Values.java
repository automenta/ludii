// 
// Decompiled by Procyon v0.5.36
// 

package game.util.equipment;

import game.functions.range.Range;
import game.types.board.SiteType;
import util.BaseLudeme;

public class Values extends BaseLudeme
{
    private final SiteType type;
    private final Range range;
    
    public Values(final SiteType type, final Range range) {
        this.range = range;
        this.type = type;
    }
    
    public SiteType type() {
        return this.type;
    }
    
    public Range range() {
        return this.range;
    }
}
