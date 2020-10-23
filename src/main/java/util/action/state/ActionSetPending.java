// 
// Decompiled by Procyon v0.5.36
// 

package util.action.state;

import util.Context;
import util.action.Action;
import util.action.BaseAction;

public final class ActionSetPending extends BaseAction
{
    private static final long serialVersionUID = 1L;
    final int value;
    
    public ActionSetPending(final int value) {
        this.value = value;
    }
    
    public ActionSetPending(final String detailedString) {
        assert detailedString.startsWith("[SetPending:");
        final String strValue = Action.extractData(detailedString, "value");
        this.value = (strValue.isEmpty() ? -1 : Integer.parseInt(strValue));
        final String strDecision = Action.extractData(detailedString, "decision");
        this.decision = (!strDecision.isEmpty() && Boolean.parseBoolean(strDecision));
    }
    
    @Override
    public Action apply(final Context context, final boolean store) {
        context.state().setPending(this.value);
        return this;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + (this.decision ? 1231 : 1237);
        result = 31 * result + this.value;
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ActionSetPending)) {
            return false;
        }
        final ActionSetPending other = (ActionSetPending)obj;
        return this.decision == other.decision && this.value == other.value;
    }
    
    @Override
    public String toTrialFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        sb.append("[SetPending:");
        if (this.value != -1) {
            sb.append("value=").append(this.value);
        }
        if (this.decision) {
            sb.append(",decision=").append(this.decision);
        }
        sb.append(']');
        return sb.toString();
    }
    
    @Override
    public String getDescription() {
        return "SetPending";
    }
    
    @Override
    public String toTurnFormat(final Context context) {
        if (this.value != -1) {
            return "Pending=" + this.value;
        }
        return "Set Pending";
    }
    
    @Override
    public String toMoveFormat(final Context context) {
        if (this.value != -1) {
            return "(Pending = " + this.value + ")";
        }
        return "(Pending)";
    }
}
