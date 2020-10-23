// 
// Decompiled by Procyon v0.5.36
// 

package util.action.others;

import util.Context;
import util.action.Action;
import util.action.ActionType;
import util.action.BaseAction;

public final class ActionSetValueOfPlayer extends BaseAction
{
    private static final long serialVersionUID = 1L;
    private final int player;
    private final int value;
    
    public ActionSetValueOfPlayer(final int player, final int value) {
        this.player = player;
        this.value = value;
    }
    
    public ActionSetValueOfPlayer(final String detailedString) {
        assert detailedString.startsWith("[SetValueOfPlayer:");
        final String strPlayer = Action.extractData(detailedString, "player");
        this.player = Integer.parseInt(strPlayer);
        final String strValue = Action.extractData(detailedString, "value");
        this.value = Integer.parseInt(strValue);
        final String strDecision = Action.extractData(detailedString, "decision");
        this.decision = (!strDecision.isEmpty() && Boolean.parseBoolean(strDecision));
    }
    
    @Override
    public Action apply(final Context context, final boolean store) {
        context.state().setValueForPlayer(this.player, this.value);
        return this;
    }
    
    @Override
    public String toTrialFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        sb.append("[SetValueOfPlayer:");
        sb.append("player=").append(this.player);
        sb.append(",value=").append(this.value);
        if (this.decision) {
            sb.append(",decision=").append(this.decision);
        }
        sb.append(']');
        return sb.toString();
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + (this.decision ? 1231 : 1237);
        result = 31 * result + this.player;
        result = 31 * result + this.value;
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ActionSetValueOfPlayer)) {
            return false;
        }
        final ActionSetValueOfPlayer other = (ActionSetValueOfPlayer)obj;
        return this.decision == other.decision && this.player == other.player && this.value == other.value;
    }
    
    @Override
    public String toTurnFormat(final Context context) {
        return "P" + this.player + " value=" + this.value;
    }
    
    @Override
    public String toMoveFormat(final Context context) {
        return "(value P" + this.player + "=" + this.value + ")";
    }
    
    @Override
    public String getDescription() {
        return "SetValueOfPlayer";
    }
    
    @Override
    public ActionType actionType() {
        return ActionType.SetValueOfPlayer;
    }
}
