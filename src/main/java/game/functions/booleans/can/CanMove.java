// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans.can;

import annotations.Hide;
import game.Game;
import game.functions.booleans.BaseBooleanFunction;
import game.rules.play.moves.Moves;
import util.Context;

@Hide
public class CanMove extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    private final Moves moves;
    
    public CanMove(final Moves moves) {
        this.moves = moves;
    }
    
    @Override
    public boolean eval(final Context context) {
        if (context.game().requiresVisited()) {
            final Context newContext = new Context(context);
            final int from = newContext.trial().lastMove().fromNonDecision();
            final int to = newContext.trial().lastMove().toNonDecision();
            newContext.state().visit(from);
            newContext.state().visit(to);
            return this.moves.canMove(newContext);
        }
        return this.moves.canMove(context);
    }
    
    @Override
    public long gameFlags(final Game game) {
        return this.moves.gameFlags(game);
    }
    
    @Override
    public boolean isStatic() {
        return this.moves.isStatic();
    }
    
    @Override
    public void preprocess(final Game game) {
        this.moves.preprocess(game);
    }
}
