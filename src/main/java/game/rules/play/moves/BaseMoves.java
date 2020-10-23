// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves;

import annotations.Hide;
import annotations.Opt;
import game.Game;
import game.rules.play.moves.nonDecision.effect.Then;
import util.Context;

@Hide
public class BaseMoves extends Moves
{
    private static final long serialVersionUID = 1L;
    
    public BaseMoves(@Opt final Then then) {
        super(then);
    }
    
    @Override
    public Moves eval(final Context context) {
        return this;
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
        super.preprocess(game);
    }
    
    @Override
    public String toEnglish() {
        return "<BaseMoves>";
    }
}
