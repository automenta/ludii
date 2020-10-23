// 
// Decompiled by Procyon v0.5.36
// 

package util.action.die;

import game.equipment.container.other.Dice;
import util.Context;
import util.action.Action;
import util.action.ActionType;
import util.action.BaseAction;

public final class ActionSetDiceAllEqual extends BaseAction
{
    private static final long serialVersionUID = 1L;
    private final boolean value;
    
    public ActionSetDiceAllEqual(final boolean value) {
        this.value = value;
    }
    
    public ActionSetDiceAllEqual(final String detailedString) {
        assert detailedString.startsWith("[SetDiceAllEqual:");
        final String strValue = Action.extractData(detailedString, "value");
        this.value = Boolean.parseBoolean(strValue);
        final String strDecision = Action.extractData(detailedString, "decision");
        this.decision = (!strDecision.isEmpty() && Boolean.parseBoolean(strDecision));
    }
    
    @Override
    public Action apply(final Context context, final boolean store) {
        context.state().setDiceAllEqual(this.value);
        for (int i = 0; i < context.game().handDice().size(); ++i) {
            final Dice dice = context.game().handDice().get(i);
            final int siteFrom = context.sitesFrom()[dice.index()];
            final int siteTo = context.sitesFrom()[dice.index()] + dice.numSites();
            int sum = 0;
            for (int site = siteFrom; site < siteTo; ++site) {
                sum += context.state().currentDice()[i][site - siteFrom];
            }
            context.state().sumDice()[i] = sum;
        }
        return this;
    }
    
    @Override
    public String toTrialFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        sb.append("[SetDiceAllEqual:");
        sb.append("value=" + this.value);
        if (this.decision) {
            sb.append(",decision=" + this.decision);
        }
        sb.append(']');
        return sb.toString();
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + (this.decision ? 1231 : 1237);
        result = 31 * result + (this.value ? 1231 : 1237);
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ActionSetDiceAllEqual)) {
            return false;
        }
        final ActionSetDiceAllEqual other = (ActionSetDiceAllEqual)obj;
        return this.decision == other.decision && this.value == other.value;
    }
    
    @Override
    public String getDescription() {
        return "SetDiceAllEqual";
    }
    
    @Override
    public String toTurnFormat(final Context context) {
        if (this.value) {
            return "Dice Equal";
        }
        return "Dice Not Equal";
    }
    
    @Override
    public String toMoveFormat(final Context context) {
        if (this.value) {
            return "(Dice Equal)";
        }
        return "(Dice Not Equal)";
    }
    
    @Override
    public ActionType actionType() {
        return ActionType.SetDiceAllEqual;
    }
}
