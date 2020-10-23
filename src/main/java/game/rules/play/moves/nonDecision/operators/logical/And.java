// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.operators.logical;

import annotations.Opt;
import game.Game;
import game.rules.play.moves.Moves;
import game.rules.play.moves.nonDecision.effect.Then;
import game.rules.play.moves.nonDecision.operator.Operator;
import util.Context;

public final class And extends Operator
{
    private static final long serialVersionUID = 1L;
    
    public static Moves construct(final Moves listA, final Moves listB, @Opt final Then then) {
        return new Or(listA, listB, then);
    }
    
    public static Moves construct(final Moves[] list, @Opt final Then then) {
        return new Or(list, then);
    }
    
    private And() {
        super(null);
    }
    
    @Override
    public Moves eval(final Context context) {
        throw new UnsupportedOperationException("And.eval(): Should never be called directly.");
    }
    
    @Override
    public boolean canMoveTo(final Context context, final int target) {
        throw new UnsupportedOperationException("And.canMoveTo(): Should never be called directly.");
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0L;
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
}
