// 
// Decompiled by Procyon v0.5.36
// 

package game.util.end;

import game.functions.ints.IntFunction;
import game.types.play.RoleType;
import util.BaseLudeme;

public class Score extends BaseLudeme
{
    final RoleType role;
    final IntFunction score;
    
    public Score(final RoleType role, final IntFunction score) {
        this.role = role;
        this.score = score;
    }
    
    public RoleType role() {
        return this.role;
    }
    
    public IntFunction score() {
        return this.score;
    }
}
