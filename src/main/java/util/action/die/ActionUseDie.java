// 
// Decompiled by Procyon v0.5.36
// 

package util.action.die;

import util.Context;
import util.action.Action;
import util.action.ActionType;
import util.action.BaseAction;

public class ActionUseDie extends BaseAction
{
    private static final long serialVersionUID = 1L;
    private final int indexHandDice;
    private final int indexDie;
    private final int site;
    
    public ActionUseDie(final int indexHandDice, final int indexDie, final int toIndex) {
        this.indexHandDice = indexHandDice;
        this.indexDie = indexDie;
        this.site = toIndex;
    }
    
    public ActionUseDie(final String detailedString) {
        assert detailedString.startsWith("[UseDie:");
        final String strIndexDie = Action.extractData(detailedString, "indexDie");
        this.indexDie = Integer.parseInt(strIndexDie);
        final String strIndexHandDice = Action.extractData(detailedString, "indexHandDice");
        this.indexHandDice = Integer.parseInt(strIndexHandDice);
        final String strSite = Action.extractData(detailedString, "site");
        this.site = Integer.parseInt(strSite);
        final String strDecision = Action.extractData(detailedString, "decision");
        this.decision = (!strDecision.isEmpty() && Boolean.parseBoolean(strDecision));
    }
    
    @Override
    public Action apply(final Context context, final boolean store) {
        context.state().updateCurrentDice(0, this.indexDie, this.indexHandDice);
        return this;
    }
    
    @Override
    public String toTrialFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        sb.append("[UseDie:");
        sb.append("indexHandDice=" + this.indexHandDice);
        sb.append(",indexDie=" + this.indexDie);
        sb.append(",site=" + this.site);
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
        result = 31 * result + this.indexHandDice;
        result = 31 * result + this.indexDie;
        result = 31 * result + this.site;
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ActionUseDie)) {
            return false;
        }
        final ActionUseDie other = (ActionUseDie)obj;
        return this.indexHandDice == other.indexHandDice && this.indexDie == other.indexDie && this.site == other.site && this.decision == other.decision;
    }
    
    @Override
    public String getDescription() {
        return "UseDie";
    }
    
    @Override
    public String toTurnFormat(final Context context) {
        return "Die " + this.site;
    }
    
    @Override
    public String toMoveFormat(final Context context) {
        return "(Die at " + this.site + " is used)";
    }
    
    @Override
    public int from() {
        return this.site;
    }
    
    @Override
    public int to() {
        return this.site;
    }
    
    public int indexHandDice() {
        return this.indexHandDice;
    }
    
    public int indexDie() {
        return this.indexDie;
    }
    
    @Override
    public ActionType actionType() {
        return ActionType.UseDie;
    }
}
