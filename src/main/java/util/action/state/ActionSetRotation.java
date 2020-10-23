// 
// Decompiled by Procyon v0.5.36
// 

package util.action.state;

import game.types.board.SiteType;
import main.SettingsGeneral;
import util.Context;
import util.action.Action;
import util.action.BaseAction;
import util.state.containerState.ContainerState;

public final class ActionSetRotation extends BaseAction
{
    private static final long serialVersionUID = 1L;
    private final int to;
    private final int rotation;
    private SiteType type;
    
    public ActionSetRotation(final SiteType type, final int to, final int rotation) {
        this.to = to;
        this.rotation = rotation;
        this.type = type;
    }
    
    public ActionSetRotation(final String detailedString) {
        assert detailedString.startsWith("[SetRotation:");
        final String strType = Action.extractData(detailedString, "type");
        this.type = (strType.isEmpty() ? null : SiteType.valueOf(strType));
        final String strTo = Action.extractData(detailedString, "to");
        this.to = Integer.parseInt(strTo);
        final String strRotation = Action.extractData(detailedString, "rotation");
        this.rotation = Integer.parseInt(strRotation);
        final String strDecision = Action.extractData(detailedString, "decision");
        this.decision = (!strDecision.isEmpty() && Boolean.parseBoolean(strDecision));
    }
    
    @Override
    public Action apply(final Context context, final boolean store) {
        this.type = ((this.type == null) ? context.board().defaultSite() : this.type);
        final ContainerState state = context.state().containerStates()[context.containerId()[this.to]];
        state.setSite(context.state(), this.to, -1, -1, -1, -1, this.rotation, -1, this.type);
        return this;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + (this.decision ? 1231 : 1237);
        result = 31 * result + this.to;
        result = 31 * result + this.rotation;
        result = 31 * result + ((this.type == null) ? 0 : this.type.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ActionSetRotation)) {
            return false;
        }
        final ActionSetRotation other = (ActionSetRotation)obj;
        return this.decision == other.decision && this.to == other.to && this.rotation == other.rotation && this.type == other.type;
    }
    
    @Override
    public String toTrialFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        sb.append("[SetRotation:");
        if (this.type != null || (context != null && this.type != context.board().defaultSite())) {
            sb.append("type=" + this.type);
            sb.append(",to=" + this.to);
        }
        else {
            sb.append("to=" + this.to);
        }
        sb.append(",rotation=" + this.rotation);
        if (this.decision) {
            sb.append(",decision=" + this.decision);
        }
        sb.append(']');
        return sb.toString();
    }
    
    @Override
    public String getDescription() {
        return "SetRotation";
    }
    
    @Override
    public String toTurnFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        String newTo = this.to + "";
        if (SettingsGeneral.isMoveCoord()) {
            final int cid = (this.type == SiteType.Cell || (this.type == null && context.board().defaultSite() == SiteType.Cell)) ? context.containerId()[this.to] : 0;
            if (cid == 0) {
                final SiteType realType = (this.type != null) ? this.type : context.board().defaultSite();
                newTo = context.game().equipment().containers()[cid].topology().getGraphElements(realType).get(this.to).label();
            }
        }
        if (this.type != null && !this.type.equals(context.board().defaultSite())) {
            sb.append(this.type + " " + newTo);
        }
        else {
            sb.append(newTo);
        }
        sb.append(" r" + this.rotation);
        return sb.toString();
    }
    
    @Override
    public String toMoveFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        sb.append("(Rotation ");
        String newTo = this.to + "";
        if (SettingsGeneral.isMoveCoord()) {
            final int cid = (this.type == SiteType.Cell || (this.type == null && context.board().defaultSite() == SiteType.Cell)) ? context.containerId()[this.to] : 0;
            if (cid == 0) {
                final SiteType realType = (this.type != null) ? this.type : context.board().defaultSite();
                newTo = context.game().equipment().containers()[cid].topology().getGraphElements(realType).get(this.to).label();
            }
        }
        if (this.type != null && !this.type.equals(context.board().defaultSite())) {
            sb.append(this.type + " " + newTo);
        }
        else {
            sb.append(newTo);
        }
        sb.append(" = " + this.rotation);
        sb.append(')');
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
    public int rotation() {
        return this.rotation;
    }
    
    @Override
    public int from() {
        return this.to;
    }
    
    @Override
    public int to() {
        return this.to;
    }
    
    @Override
    public int state() {
        return this.rotation;
    }
    
    @Override
    public int who() {
        return this.rotation;
    }
}
