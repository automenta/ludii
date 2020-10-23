// 
// Decompiled by Procyon v0.5.36
// 

package game.util.moves;

import annotations.Name;
import annotations.Opt;
import game.functions.booleans.BooleanFunction;
import game.functions.ints.IntFunction;
import game.functions.range.RangeFunction;
import game.rules.play.moves.nonDecision.effect.Apply;
import util.BaseLudeme;

public class Between extends BaseLudeme
{
    private final IntFunction trail;
    private final BooleanFunction cond;
    private final IntFunction before;
    private final RangeFunction range;
    private final IntFunction after;
    private final Apply effect;
    
    public Between(@Opt @Name final IntFunction before, @Opt final RangeFunction range, @Opt @Name final IntFunction after, @Opt @Name final BooleanFunction If, @Opt @Name final IntFunction trail, @Opt final Apply effect) {
        this.trail = trail;
        this.cond = If;
        this.before = before;
        this.range = range;
        this.after = after;
        this.effect = effect;
    }
    
    public IntFunction trail() {
        return this.trail;
    }
    
    public Apply effect() {
        return this.effect;
    }
    
    public IntFunction before() {
        return this.before;
    }
    
    public RangeFunction range() {
        return this.range;
    }
    
    public IntFunction after() {
        return this.after;
    }
    
    public BooleanFunction condition() {
        return this.cond;
    }
}
