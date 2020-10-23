// 
// Decompiled by Procyon v0.5.36
// 

package util.action.state;

import util.Context;
import util.action.Action;
import util.action.BaseAction;

public final class ActionSetTemp extends BaseAction
{
    private static final long serialVersionUID = 1L;
    private final int temp;
    
    public ActionSetTemp(final int temp) {
        this.temp = temp;
    }
    
    public ActionSetTemp(final String detailedString) {
        assert detailedString.startsWith("[SetTemp:");
        final String strTemp = Action.extractData(detailedString, "temp");
        this.temp = Integer.parseInt(strTemp);
        final String strDecision = Action.extractData(detailedString, "decision");
        this.decision = (!strDecision.isEmpty() && Boolean.parseBoolean(strDecision));
    }
    
    @Override
    public Action apply(final Context context, final boolean store) {
        context.state().setTemp(this.temp);
        return this;
    }
    
    @Override
    public String toTrialFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        sb.append("[SetTemp:");
        sb.append("temp=").append(this.temp);
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
        result = 31 * result + this.temp;
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ActionSetTemp)) {
            return false;
        }
        final ActionSetTemp other = (ActionSetTemp)obj;
        return this.decision == other.decision && this.temp == other.temp;
    }
    
    @Override
    public String getDescription() {
        return "SetTemp";
    }
    
    @Override
    public String toTurnFormat(final Context context) {
        return "Temp=" + this.temp;
    }
    
    @Override
    public String toMoveFormat(final Context context) {
        return "(Temp = " + this.temp + ")";
    }
}
