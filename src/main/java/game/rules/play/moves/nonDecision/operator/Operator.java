// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.operator;

import game.rules.play.moves.Moves;
import game.rules.play.moves.nonDecision.NonDecision;
import game.rules.play.moves.nonDecision.effect.Then;
import util.Context;

public abstract class Operator extends NonDecision
{
    private static final long serialVersionUID = 1L;
    
    public Operator(final Then then) {
        super(then);
    }
    
    @Override
    public Moves eval(final Context context) {
        return null;
    }
}
