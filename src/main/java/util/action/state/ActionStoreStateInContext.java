// 
// Decompiled by Procyon v0.5.36
// 

package util.action.state;

import util.Context;
import util.action.Action;
import util.action.BaseAction;

public class ActionStoreStateInContext extends BaseAction
{
    private static final long serialVersionUID = 1L;
    
    public ActionStoreStateInContext() {
    }
    
    public ActionStoreStateInContext(final String detailedString) {
        assert detailedString.startsWith("[StoreStateInContext:");
        final String strDecision = Action.extractData(detailedString, "decision");
        this.decision = (!strDecision.isEmpty() && Boolean.parseBoolean(strDecision));
    }
    
    @Override
    public Action apply(final Context context, final boolean store) {
        context.state().storeCurrentState(context.state());
        return this;
    }
    
    @Override
    public String toTrialFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        sb.append("[StoreStateInContext:");
        if (this.decision) {
            sb.append("decision=" + this.decision);
        }
        sb.append(']');
        return sb.toString();
    }
    
    @Override
    public int hashCode() {
        final int result = 1;
        return 1;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof ActionStoreStateInContext;
    }
    
    @Override
    public String getDescription() {
        return "StoreStateInContext";
    }
    
    @Override
    public String toTurnFormat(final Context context) {
        return "Store State";
    }
    
    @Override
    public String toMoveFormat(final Context context) {
        return "(Store State)";
    }
}
