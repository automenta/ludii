// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.match;

import game.Game;
import game.functions.ints.BaseIntFunction;
import game.functions.ints.IntFunction;
import game.functions.ints.board.Id;
import game.types.play.RoleType;
import util.Context;

public final class MatchScore extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction idPlayerFn;
    
    public MatchScore(final RoleType role) {
        this.idPlayerFn = new Id(null, role);
    }
    
    @Override
    public int eval(final Context context) {
        final int pid = this.idPlayerFn.eval(context);
        if (context.parentContext() != null) {
            return context.parentContext().score(pid);
        }
        if (context.isAMatch()) {
            return context.score(pid);
        }
        return -1;
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
    
    @Override
    public String toString() {
        return "MatchScore()";
    }
}
