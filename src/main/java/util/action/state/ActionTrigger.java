// 
// Decompiled by Procyon v0.5.36
// 

package util.action.state;

import util.Context;
import util.action.Action;
import util.action.BaseAction;

public final class ActionTrigger extends BaseAction
{
    private static final long serialVersionUID = 1L;
    private final int player;
    private final String event;
    
    public ActionTrigger(final String event, final int player) {
        this.player = player;
        this.event = event;
    }
    
    public ActionTrigger(final String detailedString) {
        assert detailedString.startsWith("[Trigger:");
        final String strPlayer = Action.extractData(detailedString, "player");
        this.player = Integer.parseInt(strPlayer);
        final String strEvent = Action.extractData(detailedString, "event");
        this.event = strEvent;
        final String strDecision = Action.extractData(detailedString, "decision");
        this.decision = (!strDecision.isEmpty() && Boolean.parseBoolean(strDecision));
    }
    
    @Override
    public Action apply(final Context context, final boolean store) {
        context.state().triggers(this.player, true);
        return this;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + (this.decision ? 1231 : 1237);
        result = 31 * result + this.player;
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ActionTrigger)) {
            return false;
        }
        final ActionTrigger other = (ActionTrigger)obj;
        return this.decision == other.decision && this.player == other.player && this.event.equals(other.event);
    }
    
    @Override
    public String toTrialFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        sb.append("[Trigger:");
        sb.append("event=").append(this.event);
        sb.append(",player=").append(this.player);
        if (this.decision) {
            sb.append(",decision=").append(this.decision);
        }
        sb.append(']');
        return sb.toString();
    }
    
    @Override
    public String getDescription() {
        return "Trigger";
    }
    
    @Override
    public String toTurnFormat(final Context context) {
        return "Trigger " + this.event + " P" + this.player;
    }
    
    @Override
    public String toMoveFormat(final Context context) {
        return "(Trigger " + this.event + " P" + this.player + ")";
    }
    
    @Override
    public int who() {
        return this.player;
    }
    
    @Override
    public int what() {
        return this.player;
    }
}
