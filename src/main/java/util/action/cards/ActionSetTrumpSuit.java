// 
// Decompiled by Procyon v0.5.36
// 

package util.action.cards;

import game.types.component.SuitType;
import util.Context;
import util.action.Action;
import util.action.ActionType;
import util.action.BaseAction;

public class ActionSetTrumpSuit extends BaseAction
{
    private static final long serialVersionUID = 1L;
    private final int trumpSuit;
    
    public ActionSetTrumpSuit(final int trumpSuit) {
        this.trumpSuit = trumpSuit;
    }
    
    public ActionSetTrumpSuit(final String detailedString) {
        assert detailedString.startsWith("[SetTrumpSuit:");
        final String strTrumpSuit = Action.extractData(detailedString, "trumpSuit");
        this.trumpSuit = Integer.parseInt(strTrumpSuit);
        final String strDecision = Action.extractData(detailedString, "decision");
        this.decision = (!strDecision.isEmpty() && Boolean.parseBoolean(strDecision));
    }
    
    @Override
    public Action apply(final Context context, final boolean store) {
        context.state().setTrumpSuit(this.trumpSuit);
        return this;
    }
    
    @Override
    public String toTrialFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        sb.append("[SetTrumpSuit:");
        sb.append("trumpSuit=").append(this.trumpSuit);
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
        result = 31 * result + this.trumpSuit;
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ActionSetTrumpSuit)) {
            return false;
        }
        final ActionSetTrumpSuit other = (ActionSetTrumpSuit)obj;
        return this.trumpSuit == other.trumpSuit && this.decision == other.decision;
    }
    
    @Override
    public String toTurnFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        sb.append("TrumpSuit = ").append(SuitType.values()[this.trumpSuit]);
        return sb.toString();
    }
    
    @Override
    public String getDescription() {
        return "SetTrumpSuit";
    }
    
    @Override
    public String toMoveFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        sb.append("(TrumpSuit = ").append(SuitType.values()[this.trumpSuit]).append(")");
        return sb.toString();
    }
    
    @Override
    public boolean isOtherMove() {
        return true;
    }
    
    @Override
    public ActionType actionType() {
        return ActionType.SetTrumpSuit;
    }
    
    @Override
    public int what() {
        return this.trumpSuit;
    }
}
