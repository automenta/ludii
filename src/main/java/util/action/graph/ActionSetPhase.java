// 
// Decompiled by Procyon v0.5.36
// 

package util.action.graph;

import game.types.board.SiteType;
import main.SettingsGeneral;
import util.Context;
import util.action.Action;
import util.action.BaseAction;

public final class ActionSetPhase extends BaseAction
{
    private static final long serialVersionUID = 1L;
    private final int to;
    private final int phase;
    private SiteType type;
    
    public ActionSetPhase(final SiteType type, final int to, final int phase) {
        this.type = type;
        this.to = to;
        this.phase = phase;
    }
    
    public ActionSetPhase(final String detailedString) {
        assert detailedString.startsWith("[SetPhase:");
        final String strType = Action.extractData(detailedString, "type");
        this.type = (strType.isEmpty() ? null : SiteType.valueOf(strType));
        final String strTo = Action.extractData(detailedString, "to");
        this.to = Integer.parseInt(strTo);
        final String strPhase = Action.extractData(detailedString, "phase");
        this.phase = Integer.parseInt(strPhase);
        final String strDecision = Action.extractData(detailedString, "decision");
        this.decision = (!strDecision.isEmpty() && Boolean.parseBoolean(strDecision));
    }
    
    @Override
    public Action apply(final Context context, final boolean store) {
        this.type = ((this.type == null) ? context.board().defaultSite() : this.type);
        context.topology().getGraphElements(this.type).get(this.to).setPhase(this.phase);
        return this;
    }
    
    @Override
    public String toTrialFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        sb.append("[SetPhase:");
        if (this.type != null || (context != null && this.type != context.board().defaultSite())) {
            sb.append("type=").append(this.type);
            sb.append(",to=").append(this.to);
        }
        else {
            sb.append("to=").append(this.to);
        }
        sb.append(",phase=").append(this.phase);
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
        if (!(obj instanceof ActionSetPhase)) {
            return false;
        }
        final ActionSetPhase other = (ActionSetPhase)obj;
        return this.decision == other.decision && this.to == other.to && this.phase == other.phase;
    }
    
    @Override
    public String getDescription() {
        return "SetPhase";
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
            sb.append(this.type).append(" ").append(newTo);
        }
        else {
            sb.append(newTo);
        }
        sb.append("=%").append(this.phase);
        return sb.toString();
    }
    
    @Override
    public String toMoveFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        sb.append("(Phase at ");
        String newTo = String.valueOf(this.to);
        if (SettingsGeneral.isMoveCoord()) {
            final int cid = (this.type == SiteType.Cell || (this.type == null && context.board().defaultSite() == SiteType.Cell)) ? context.containerId()[this.to] : 0;
            if (cid == 0) {
                final SiteType realType = (this.type != null) ? this.type : context.board().defaultSite();
                newTo = context.game().equipment().containers()[cid].topology().getGraphElements(realType).get(this.to).label();
            }
        }
        if (this.type != null && this.type != context.board().defaultSite()) {
            sb.append(this.type).append(" ").append(newTo);
        }
        else {
            sb.append(newTo);
        }
        sb.append(" = ").append(this.phase).append(")");
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
    public int to() {
        return this.to;
    }
}
