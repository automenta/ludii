// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans.is.player;

import annotations.Hide;
import annotations.Or;
import game.Game;
import game.functions.booleans.BaseBooleanFunction;
import game.functions.ints.IntFunction;
import game.functions.ints.board.Id;
import game.types.play.RoleType;
import util.Context;

@Hide
public final class IsPrev extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction who;
    
    public IsPrev(@Or final IntFunction who, @Or final RoleType role) {
        this.who = ((role != null) ? new Id(null, role) : who);
    }
    
    @Override
    public boolean eval(final Context context) {
        return this.who.eval(context) == context.state().prev();
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        return this.who.gameFlags(game);
    }
    
    @Override
    public void preprocess(final Game game) {
        this.who.preprocess(game);
    }
}
