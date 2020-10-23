// 
// Decompiled by Procyon v0.5.36
// 

package util;

import annotations.Hide;
import game.rules.play.moves.Moves;

@Hide
public final class MoveUtilities
{
    private MoveUtilities() {
    }
    
    public static void chainRuleCrossProduct(final Context context, final Moves ourActions, final Moves nextRule, final Move currentAction, final boolean prepend) {
        if (nextRule == null) {
            if (currentAction != null) {
                ourActions.moves().add(currentAction);
            }
            return;
        }
        final Moves generated = nextRule.eval(context);
        if (currentAction == null) {
            ourActions.moves().addAll(generated.moves());
        }
        else {
            assert generated.moves().isEmpty();
            ourActions.moves().add(currentAction);
        }
    }
    
    public static Move chainRuleWithAction(final Context context, final Moves nextRule, final Move currentAction, final boolean prepend, final boolean decision) {
        if (nextRule == null) {
            return currentAction;
        }
        final Moves generated = nextRule.eval(context);
        if (generated.moves().isEmpty()) {
            return currentAction;
        }
        if (generated.moves().size() > 1) {
            if (!decision) {
                for (final Move m : generated.moves()) {
                    m.setFromNonDecision(m.actions().get(0).from());
                    m.setToNonDecision(m.actions().get(0).to());
                    m.setDecision(false);
                }
            }
            return prepend ? new Move(generated.moves(), currentAction) : new Move(currentAction, generated.moves());
        }
        final Move i = generated.moves().get(0);
        if (!decision) {
            i.setDecision(false);
        }
        if (currentAction == null) {
            return i;
        }
        return prepend ? new Move(i, currentAction) : new Move(currentAction, generated.moves().get(0));
    }
}
