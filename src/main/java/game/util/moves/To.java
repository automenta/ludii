// 
// Decompiled by Procyon v0.5.36
// 

package game.util.moves;

import annotations.Name;
import annotations.Opt;
import annotations.Or;
import game.functions.booleans.BooleanFunction;
import game.functions.intArray.state.Rotations;
import game.functions.ints.IntFunction;
import game.functions.region.RegionFunction;
import game.rules.play.moves.nonDecision.effect.Apply;
import game.types.board.SiteType;
import util.BaseLudeme;

public class To extends BaseLudeme
{
    private final IntFunction loc;
    private final RegionFunction region;
    private final IntFunction level;
    private final BooleanFunction cond;
    private final Rotations rotations;
    private final SiteType type;
    private final Apply effect;
    
    public To(@Opt final SiteType type, @Opt @Or final RegionFunction region, @Opt @Or final IntFunction loc, @Opt @Name final IntFunction level, @Opt final Rotations rotations, @Opt @Name final BooleanFunction If, @Opt final Apply effect) {
        this.loc = ((region != null) ? null : ((loc == null) ? game.functions.ints.context.To.construct() : loc));
        this.region = region;
        this.level = level;
        this.cond = If;
        this.rotations = rotations;
        this.type = type;
        this.effect = effect;
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
    
    public Rotations rotations() {
        return this.rotations;
    }
    
    public Apply effect() {
        return this.effect;
    }
}
