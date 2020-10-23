// 
// Decompiled by Procyon v0.5.36
// 

package util.action.hidden;

import game.types.board.SiteType;
import main.SettingsGeneral;
import util.Context;
import util.action.Action;
import util.action.ActionType;
import util.action.BaseAction;
import util.state.containerState.ContainerState;

public final class ActionSetVisible extends BaseAction
{
    private static final long serialVersionUID = 1L;
    private final int to;
    private final int level;
    private final int who;
    private SiteType type;
    
    public ActionSetVisible(final SiteType type, final int to, final int level, final int who) {
        this.who = who;
        this.to = to;
        this.level = level;
        this.type = type;
    }
    
    public ActionSetVisible(final String detailedString) {
        assert detailedString.startsWith("[SetVisible:");
        final String strType = Action.extractData(detailedString, "type");
        this.type = (strType.isEmpty() ? null : SiteType.valueOf(strType));
        final String strTo = Action.extractData(detailedString, "to");
        this.to = Integer.parseInt(strTo);
        final String strWho = Action.extractData(detailedString, "who");
        this.who = Integer.parseInt(strWho);
        final String strLevel = Action.extractData(detailedString, "level");
        this.level = (strLevel.isEmpty() ? -1 : Integer.parseInt(strLevel));
        final String strDecision = Action.extractData(detailedString, "decision");
        this.decision = (!strDecision.isEmpty() && Boolean.parseBoolean(strDecision));
    }
    
    @Override
    public Action apply(final Context context, final boolean store) {
        this.type = ((this.type == null) ? context.board().defaultSite() : this.type);
        final ContainerState cs = context.state().containerStates()[context.containerId()[this.to]];
        if (this.level != -1) {
            cs.setVisibleCell(context.state(), this.to, this.level, this.who);
        }
        else {
            cs.setVisible(context.state(), this.to, this.who, this.type);
        }
        return this;
    }
    
    @Override
    public String toTrialFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        sb.append("[SetVisible:");
        if (this.type != null || (context != null && this.type != context.board().defaultSite())) {
            sb.append("type=" + this.type);
            sb.append(",to=" + this.to);
        }
        else {
            sb.append("to=" + this.to);
        }
        if (this.level != -1) {
            sb.append(",level=" + this.level);
        }
        sb.append(",who=" + this.who);
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
        result = 31 * result + this.to;
        result = 31 * result + this.who;
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ActionSetVisible)) {
            return false;
        }
        final ActionSetVisible other = (ActionSetVisible)obj;
        return this.decision == other.decision && this.to == other.to && this.who == other.who && this.type == other.type;
    }
    
    @Override
    public String getDescription() {
        return "SetVisible";
    }
    
    @Override
    public String toTurnFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        String newTo = String.valueOf(this.to);
        if (SettingsGeneral.isMoveCoord()) {
            final int cid = (this.type == SiteType.Cell || (this.type == null && context.board().defaultSite() == SiteType.Cell)) ? context.containerId()[this.to] : 0;
            if (cid == 0) {
                final SiteType realType = (this.type != null) ? this.type : context.board().defaultSite();
                newTo = context.game().equipment().containers()[cid].topology().getGraphElements(realType).get(this.to).label();
            }
        }
        if (this.type != null && this.type != context.board().defaultSite()) {
            sb.append(this.type + " " + newTo);
        }
        else {
            sb.append(newTo);
        }
        if (this.level != -1) {
            sb.append("/" + this.level);
        }
        sb.append(" show:P" + this.who);
        return sb.toString();
    }
    
    @Override
    public String toMoveFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        sb.append("(Set Visible at ");
        String newTo = String.valueOf(this.to);
        if (SettingsGeneral.isMoveCoord()) {
            final int cid = (this.type == SiteType.Cell || (this.type == null && context.board().defaultSite() == SiteType.Cell)) ? context.containerId()[this.to] : 0;
            if (cid == 0) {
                final SiteType realType = (this.type != null) ? this.type : context.board().defaultSite();
                newTo = context.game().equipment().containers()[cid].topology().getGraphElements(realType).get(this.to).label();
            }
        }
        if (this.type != null && this.type != context.board().defaultSite()) {
            sb.append(this.type + " " + newTo);
        }
        else {
            sb.append(newTo);
        }
        if (this.level != -1) {
            sb.append("/" + this.level);
        }
        sb.append(" for P" + this.who + ")");
        return sb.toString();
    }
    
    @Override
    public SiteType fromType() {
        return this.type;
    }
    
    @Override
    public SiteType toType() {
        return this.type;
    }
    
    @Override
    public int from() {
        return this.to;
    }
    
    @Override
    public int who() {
        return this.who;
    }
    
    @Override
    public int to() {
        return this.to;
    }
    
    @Override
    public int levelFrom() {
        return (this.level == -1) ? 0 : this.level;
    }
    
    @Override
    public int levelTo() {
        return (this.level == -1) ? 0 : this.level;
    }
    
    @Override
    public ActionType actionType() {
        return ActionType.SetVisible;
    }
}
