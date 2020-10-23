// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect;

import game.rules.play.moves.Moves;
import game.rules.play.moves.nonDecision.NonDecision;
import util.Context;

public abstract class Effect extends NonDecision
{
    private static final long serialVersionUID = 1L;
    
    public Effect(final Then then) {
        super(then);
    }
    
    @Override
    public Moves eval(final Context context) {
        return null;
    }
}
