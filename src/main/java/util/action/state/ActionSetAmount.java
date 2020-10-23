// 
// Decompiled by Procyon v0.5.36
// 

package util.action.state;

import util.Context;
import util.action.Action;
import util.action.ActionType;
import util.action.BaseAction;

public final class ActionSetAmount extends BaseAction
{
    private static final long serialVersionUID = 1L;
    private final int player;
    private final int amount;
    
    public ActionSetAmount(final int player, final int amount) {
        this.player = player;
        this.amount = amount;
    }
    
    public ActionSetAmount(final String detailedString) {
        assert detailedString.startsWith("[SetAmount:");
        final String strPlayer = Action.extractData(detailedString, "player");
        this.player = Integer.parseInt(strPlayer);
        final String strAmount = Action.extractData(detailedString, "amount");
        this.amount = Integer.parseInt(strAmount);
        final String strDecision = Action.extractData(detailedString, "decision");
        this.decision = (!strDecision.isEmpty() && Boolean.parseBoolean(strDecision));
    }
    
    @Override
    public Action apply(final Context context, final boolean store) {
        context.state().setAmount(this.player, this.amount);
        return this;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + (this.decision ? 1231 : 1237);
        result = 31 * result + this.player;
        result = 31 * result + this.amount;
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ActionSetAmount)) {
            return false;
        }
        final ActionSetAmount other = (ActionSetAmount)obj;
        return this.decision == other.decision && this.amount == other.amount && this.player == other.player;
    }
    
    @Override
    public String toTrialFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        sb.append("[SetAmount:");
        sb.append("player=").append(this.player);
        sb.append(",amount=").append(this.amount);
        if (this.decision) {
            sb.append(",decision=").append(this.decision);
        }
        sb.append(']');
        return sb.toString();
    }
    
    @Override
    public String getDescription() {
        return "Amount";
    }
    
    @Override
    public String toTurnFormat(final Context context) {
        return "P" + this.player + "=$" + this.amount;
    }
    
    @Override
    public String toMoveFormat(final Context context) {
        return "(Amount P" + this.player + " = " + this.amount + ")";
    }
    
    @Override
    public int who() {
        return this.player;
    }
    
    @Override
    public ActionType actionType() {
        return ActionType.SetAmount;
    }
}
