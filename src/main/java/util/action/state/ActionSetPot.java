// 
// Decompiled by Procyon v0.5.36
// 

package util.action.state;

import util.Context;
import util.action.Action;
import util.action.ActionType;
import util.action.BaseAction;

public final class ActionSetPot extends BaseAction
{
    private static final long serialVersionUID = 1L;
    private final int pot;
    
    public ActionSetPot(final int pot) {
        this.pot = pot;
    }
    
    public ActionSetPot(final String detailedString) {
        assert detailedString.startsWith("[SetPot:");
        final String strBet = Action.extractData(detailedString, "pot");
        this.pot = Integer.parseInt(strBet);
        final String strDecision = Action.extractData(detailedString, "decision");
        this.decision = (!strDecision.isEmpty() && Boolean.parseBoolean(strDecision));
    }
    
    @Override
    public Action apply(final Context context, final boolean store) {
        context.state().setPot(this.pot);
        return this;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + (this.decision ? 1231 : 1237);
        result = 31 * result + this.pot;
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ActionSetPot)) {
            return false;
        }
        final ActionSetPot other = (ActionSetPot)obj;
        return this.decision == other.decision && this.pot == other.pot;
    }
    
    @Override
    public String toTrialFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        sb.append("[SetPot:");
        sb.append("pot=" + this.pot);
        if (this.decision) {
            sb.append(",decision=" + this.decision);
        }
        sb.append(']');
        return sb.toString();
    }
    
    @Override
    public String getDescription() {
        return "Pot";
    }
    
    @Override
    public String toTurnFormat(final Context context) {
        return "Pot  $" + this.pot;
    }
    
    @Override
    public String toMoveFormat(final Context context) {
        return "(Pot = " + this.pot + ")";
    }
    
    @Override
    public boolean isOtherMove() {
        return true;
    }
    
    @Override
    public int count() {
        return this.pot;
    }
    
    @Override
    public ActionType actionType() {
        return ActionType.Bet;
    }
}
