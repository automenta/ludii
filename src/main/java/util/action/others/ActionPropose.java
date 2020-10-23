// 
// Decompiled by Procyon v0.5.36
// 

package util.action.others;

import util.Context;
import util.action.Action;
import util.action.ActionType;
import util.action.BaseAction;

public final class ActionPropose extends BaseAction
{
    private static final long serialVersionUID = 1L;
    private final String proposition;
    
    public ActionPropose(final String proposition, final boolean useless) {
        this.proposition = proposition;
    }
    
    public ActionPropose(final String detailedString) {
        assert detailedString.startsWith("[Propose:");
        final String strProposition = Action.extractData(detailedString, "proposition");
        this.proposition = strProposition;
        final String strDecision = Action.extractData(detailedString, "decision");
        this.decision = (!strDecision.isEmpty() && Boolean.parseBoolean(strDecision));
    }
    
    @Override
    public Action apply(final Context context, final boolean store) {
        context.state().propositions().add(this.proposition);
        return this;
    }
    
    @Override
    public String toTrialFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        sb.append("[Propose:");
        sb.append("proposition=").append(this.proposition);
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
        result = 31 * result + this.proposition.hashCode();
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ActionPropose)) {
            return false;
        }
        final ActionPropose other = (ActionPropose)obj;
        return this.decision == other.decision && this.proposition.equals(other.proposition);
    }
    
    @Override
    public String toTurnFormat(final Context context) {
        return "Propose \"" + this.proposition + "\"";
    }
    
    @Override
    public String toMoveFormat(final Context context) {
        return "(Propose \"" + this.proposition + "\")";
    }
    
    @Override
    public String getDescription() {
        return "Propose";
    }
    
    @Override
    public String proposition() {
        return this.proposition;
    }
    
    @Override
    public boolean isOtherMove() {
        return true;
    }
    
    @Override
    public ActionType actionType() {
        return ActionType.Propose;
    }
}
