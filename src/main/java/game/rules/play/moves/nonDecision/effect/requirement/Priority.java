// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect.requirement;

import annotations.Opt;
import game.Game;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.rules.play.moves.nonDecision.effect.Effect;
import game.rules.play.moves.nonDecision.effect.Then;
import util.Context;

public final class Priority extends Effect
{
    private static final long serialVersionUID = 1L;
    private final Moves[] list;
    
    public Priority(final Moves[] list, @Opt final Then then) {
        super(then);
        this.list = list;
    }
    
    public Priority(final Moves list1, final Moves list2, @Opt final Then then) {
        super(then);
        this.list = new Moves[] { list1, list2 };
    }
    
    @Override
    public Moves eval(final Context context) {
        for (final Moves move : this.list) {
            final Moves l = move.eval(context);
            if (!l.moves().isEmpty()) {
                if (this.then() != null) {
                    for (int j = 0; j < l.moves().size(); ++j) {
                        l.moves().get(j).then().add(this.then().moves());
                    }
                }
                return l;
            }
        }
        return new BaseMoves(super.then());
    }
    
    @Override
    public boolean canMove(final Context context) {
        for (final Moves moves : this.list) {
            if (moves.canMove(context)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = super.gameFlags(game);
        for (final Moves moves : this.list) {
            gameFlags |= moves.gameFlags(game);
        }
        return gameFlags;
    }
    
    @Override
    public boolean isStatic() {
        boolean isStatic = true;
        for (final Moves moves : this.list) {
            isStatic = (isStatic && moves.isStatic());
        }
        return isStatic;
    }
    
    @Override
    public void preprocess(final Game game) {
        super.preprocess(game);
        for (final Moves moves : this.list) {
            moves.preprocess(game);
        }
    }
    
    public Moves[] list() {
        return this.list;
    }
    
    @Override
    public String toEnglish() {
        return "Priority";
    }
}
