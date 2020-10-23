// 
// Decompiled by Procyon v0.5.36
// 

package util.action.others;

import util.Context;
import util.action.Action;
import util.action.ActionType;
import util.action.BaseAction;

public final class ActionNextInstance extends BaseAction
{
    private static final long serialVersionUID = 1L;
    
    public ActionNextInstance() {
    }
    
    public ActionNextInstance(final String detailedString) {
        assert detailedString.startsWith("[NextInstance:");
        final String strDecision = Action.extractData(detailedString, "decision");
        this.decision = (!strDecision.isEmpty() && Boolean.parseBoolean(strDecision));
    }
    
    @Override
    public Action apply(final Context context, final boolean store) {
        return this;
    }
    
    @Override
    public String toTrialFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        sb.append("[NextInstance:");
        if (this.decision) {
            sb.append("decision=" + this.decision);
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
        if (!(obj instanceof ActionNextInstance)) {
            return false;
        }
        final ActionNextInstance other = (ActionNextInstance)obj;
        return this.decision == other.decision;
    }
    
    @Override
    public String toTurnFormat(final Context context) {
        return "Next Game";
    }
    
    @Override
    public String toMoveFormat(final Context context) {
        return "(Next Game)";
    }
    
    @Override
    public String getDescription() {
        return "NextInstance";
    }
    
    @Override
    public boolean containsNextInstance() {
        return true;
    }
    
    @Override
    public ActionType actionType() {
        return ActionType.NextInstance;
    }
}
