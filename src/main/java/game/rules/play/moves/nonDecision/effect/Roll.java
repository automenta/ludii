// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect;

import annotations.Opt;
import game.Game;
import game.equipment.container.other.Dice;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.types.board.SiteType;
import util.Context;
import util.Move;
import util.action.Action;
import util.action.die.ActionSetDiceAllEqual;
import util.action.die.ActionUpdateDice;

import java.util.ArrayList;
import java.util.List;

public final class Roll extends Effect
{
    private static final long serialVersionUID = 1L;
    
    public Roll(@Opt final Then then) {
        super(then);
    }
    
    @Override
    public Moves eval(final Context context) {
        final Moves moves = new BaseMoves(super.then());
        boolean allEqual = true;
        int valueToCompare = -1;
        final List<Action> actions = new ArrayList<>();
        for (final Dice dice : context.game().handDice()) {
            for (int loc = context.sitesFrom()[dice.index()]; loc < context.sitesFrom()[dice.index()] + dice.numLocs(); ++loc) {
                final int what = context.containerState(dice.index()).what(loc, SiteType.Cell);
                final int newValue = context.components()[what].roll(context);
                if (valueToCompare == -1) {
                    valueToCompare = newValue;
                }
                else if (valueToCompare != newValue) {
                    allEqual = false;
                }
                actions.add(new ActionUpdateDice(loc, newValue));
            }
        }
        actions.add(new ActionSetDiceAllEqual(allEqual));
        moves.moves().add(new Move(actions));
        if (this.then() != null) {
            for (int j = 0; j < moves.moves().size(); ++j) {
                moves.moves().get(j).then().add(this.then().moves());
            }
        }
        return moves;
    }
    
    @Override
    public boolean canMoveTo(final Context context, final int target) {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0x42L | super.gameFlags(game);
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
        return "Roll";
    }
}
