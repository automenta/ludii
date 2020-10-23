// 
// Decompiled by Procyon v0.5.36
// 

package game.util.moves;

import annotations.Name;
import annotations.Opt;
import annotations.Or;
import game.functions.booleans.BooleanFunction;
import game.functions.ints.IntFunction;
import game.functions.region.RegionFunction;
import game.types.board.SiteType;
import util.BaseLudeme;

public class From extends BaseLudeme
{
    private final IntFunction loc;
    private final RegionFunction region;
    private final IntFunction level;
    private final BooleanFunction cond;
    private final SiteType type;
    
    public From(@Opt final SiteType type, @Opt @Or final RegionFunction region, @Opt @Or final IntFunction loc, @Opt @Name final IntFunction level, @Opt @Name final BooleanFunction If) {
        this.loc = ((region != null) ? null : ((loc == null) ? new game.functions.ints.context.From(null) : loc));
        this.region = region;
        this.level = level;
        this.cond = If;
        this.type = type;
    }
    
    public IntFunction loc() {
        return this.loc;
    }
    
    public RegionFunction region() {
        return this.region;
    }
    
    public IntFunction level() {
        return this.level;
    }
    
    public BooleanFunction cond() {
        return this.cond;
    }
    
    public SiteType type() {
        return this.type;
    }
}
