// 
// Decompiled by Procyon v0.5.36
// 

package util.action.die;

import game.equipment.container.other.Dice;
import game.types.board.SiteType;
import util.Context;
import util.action.Action;
import util.action.ActionType;
import util.action.BaseAction;
import util.state.containerState.ContainerState;

public final class ActionUpdateDice extends BaseAction
{
    private static final long serialVersionUID = 1L;
    private final int site;
    private final int newState;
    
    public ActionUpdateDice(final int site, final int newState) {
        this.site = site;
        this.newState = newState;
    }
    
    public ActionUpdateDice(final String detailedString) {
        assert detailedString.startsWith("[SetStateAndUpdateDice:");
        final String strSite = Action.extractData(detailedString, "site");
        this.site = Integer.parseInt(strSite);
        final String strState = Action.extractData(detailedString, "state");
        this.newState = Integer.parseInt(strState);
        final String strDecision = Action.extractData(detailedString, "decision");
        this.decision = (!strDecision.isEmpty() && Boolean.parseBoolean(strDecision));
    }
    
    @Override
    public Action apply(final Context context, final boolean store) {
        final int cid = context.containerId()[this.site];
        final ContainerState state = context.state().containerStates()[cid];
        state.setSite(context.state(), this.site, -1, -1, -1, this.newState, -1, -1, SiteType.Cell);
        if (context.containers()[cid].isDice()) {
            final Dice dice = (Dice)context.containers()[cid];
            int indexDice = 0;
            for (int i = 0; i < context.game().handDice().size(); ++i) {
                final Dice d = context.game().handDice().get(i);
                if (d.index() == dice.index()) {
                    indexDice = i;
                    break;
                }
            }
            final int from = context.sitesFrom()[cid];
            final int what = state.whatCell(this.site);
            final int dieIndex = this.site - from;
            context.state().currentDice()[indexDice][dieIndex] = context.components()[what].getFaces()[this.newState];
        }
        return this;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + (this.decision ? 1231 : 1237);
        result = 31 * result + this.site;
        result = 31 * result + this.newState;
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ActionUpdateDice)) {
            return false;
        }
        final ActionUpdateDice other = (ActionUpdateDice)obj;
        return this.decision == other.decision && this.site == other.site && this.newState == other.newState;
    }
    
    @Override
    public String toTrialFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        sb.append("[SetStateAndUpdateDice:");
        sb.append("site=" + this.site);
        sb.append(",state=" + this.newState);
        if (this.decision) {
            sb.append(",decision=" + this.decision);
        }
        sb.append(']');
        return sb.toString();
    }
    
    @Override
    public String getDescription() {
        return "SetStateAndUpdateDice";
    }
    
    @Override
    public String toTurnFormat(final Context context) {
        return "Die " + this.site + "=" + this.newState;
    }
    
    @Override
    public String toMoveFormat(final Context context) {
        return "(Die at " + this.site + " state=" + this.newState + ")";
    }
    
    @Override
    public int from() {
        return this.site;
    }
    
    @Override
    public int to() {
        return this.site;
    }
    
    @Override
    public int state() {
        return this.newState;
    }
    
    @Override
    public int who() {
        return this.newState;
    }
    
    @Override
    public ActionType actionType() {
        return ActionType.SetStateAndUpdateDice;
    }
}
