// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect;

import annotations.Name;
import annotations.Opt;
import game.Game;
import game.functions.booleans.BooleanFunction;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.rules.play.moves.nonDecision.NonDecision;
import util.Context;

public final class Apply extends Moves
{
    private static final long serialVersionUID = 1L;
    final BooleanFunction cond;
    final NonDecision effect;
    
    public Apply(@Opt @Name final BooleanFunction If, @Opt final NonDecision effect) {
        super(null);
        this.cond = If;
        this.effect = effect;
    }
    
    @Override
    public Moves eval(final Context context) {
        if (this.cond == null || this.cond.eval(context)) {
            final Moves moves = this.effect.eval(context);
            return moves;
        }
        return new BaseMoves(super.then());
    }
    
    @Override
    public boolean canMove(final Context context) {
        return (this.cond == null || this.cond.eval(context)) && this.effect.canMove(context);
    }
    
    @Override
    public boolean canMoveTo(final Context context, final int target) {
        return (this.cond == null || this.cond.eval(context)) && this.effect.canMoveTo(context, target);
    }
    
    @Override
    public long gameFlags(final Game game) {
        long flags = super.gameFlags(game);
        if (this.cond != null) {
            flags |= this.cond.gameFlags(game);
        }
        if (this.effect != null) {
            flags |= this.effect.gameFlags(game);
        }
        return flags;
    }
    
    @Override
    public boolean isStatic() {
        return (this.cond == null || this.cond.isStatic()) && (this.effect == null || this.effect.isStatic());
    }
    
    @Override
    public void preprocess(final Game game) {
        super.preprocess(game);
        if (this.cond != null) {
            this.cond.preprocess(game);
        }
        if (this.effect != null) {
            this.effect.preprocess(game);
        }
    }
    
    public BooleanFunction condition() {
        return this.cond;
    }
    
    public Moves effect() {
        return this.effect;
    }
    
    @Override
    public String toEnglish() {
        return "Effect";
    }
}
