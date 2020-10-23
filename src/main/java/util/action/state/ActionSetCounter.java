// 
// Decompiled by Procyon v0.5.36
// 

package util.action.state;

import util.Context;
import util.action.Action;
import util.action.BaseAction;

public final class ActionSetCounter extends BaseAction
{
    private static final long serialVersionUID = 1L;
    private final int counter;
    
    public ActionSetCounter(final int counter) {
        this.counter = counter;
    }
    
    public ActionSetCounter(final String detailedString) {
        assert detailedString.startsWith("[SetCounter:");
        final String strCounter = Action.extractData(detailedString, "counter");
        this.counter = Integer.parseInt(strCounter);
        final String strDecision = Action.extractData(detailedString, "decision");
        this.decision = (!strDecision.isEmpty() && Boolean.parseBoolean(strDecision));
    }
    
    @Override
    public Action apply(final Context context, final boolean store) {
        context.state().setCounter(this.counter);
        return this;
    }
    
    @Override
    public String toTrialFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        sb.append("[SetCounter:");
        sb.append("counter=").append(this.counter);
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
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ActionSetCounter)) {
            return false;
        }
        final ActionSetCounter other = (ActionSetCounter)obj;
        return this.decision == other.decision && this.counter == other.counter;
    }
    
    @Override
    public String getDescription() {
        return "SetCounter";
    }
    
    @Override
    public String toTurnFormat(final Context context) {
        return "Counter=" + this.counter;
    }
    
    @Override
    public String toMoveFormat(final Context context) {
        return "(Counter = " + this.counter + ")";
    }
}
