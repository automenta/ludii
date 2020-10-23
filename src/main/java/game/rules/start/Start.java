// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.start;

import annotations.Or;
import game.types.board.SiteType;
import util.BaseLudeme;
import util.Context;
import util.Move;
import util.action.BaseAction;
import util.action.move.ActionAdd;

import java.io.Serializable;

public class Start extends BaseLudeme implements Serializable
{
    private static final long serialVersionUID = 1L;
    private final StartRule[] rules;
    
    public Start(@Or final StartRule[] rules, @Or final StartRule rule) {
        int numNonNull = 0;
        if (rules != null) {
            ++numNonNull;
        }
        if (rule != null) {
            ++numNonNull;
        }
        if (numNonNull != 1) {
            throw new IllegalArgumentException("Exactly one Or parameter must be non-null.");
        }
        if (rules != null) {
            this.rules = rules;
        }
        else {
            (this.rules = new StartRule[1])[0] = rule;
        }
    }
    
    public StartRule[] rules() {
        return this.rules;
    }
    
    public void eval(final Context context) {
        for (final StartRule rule : this.rules) {
            rule.eval(context);
        }
    }
    
    public static void placePieces(final Context context, final int locn, final int what, final int count, final int state, final int rotation, final boolean isStack, final boolean[] isInvisible, final boolean[] isMasked, final SiteType type) {
        if (isStack) {
            final BaseAction actionAtomic = new ActionAdd(type, locn, what, 1, -1, -1, isInvisible, isMasked, Boolean.TRUE);
            actionAtomic.apply(context, true);
            context.trial().moves().add(new Move(actionAtomic));
        }
        else {
            final BaseAction actionAtomic = new ActionAdd(type, locn, what, count, state, rotation, isInvisible, isMasked, null);
            actionAtomic.apply(context, true);
            context.trial().moves().add(new Move(actionAtomic));
        }
        context.trial().addInitPlacement();
    }
}
