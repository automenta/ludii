// 
// Decompiled by Procyon v0.5.36
// 

package util.action.state;

import game.equipment.component.Component;
import game.types.board.SiteType;
import main.SettingsGeneral;
import util.Context;
import util.action.Action;
import util.action.BaseAction;
import util.state.containerState.ContainerState;

public class ActionSetCount extends BaseAction
{
    private static final long serialVersionUID = 1L;
    private final int what;
    private final int to;
    private final int count;
    private SiteType type;
    
    public ActionSetCount(final SiteType type, final int to, final int what, final int count) {
        this.to = to;
        this.count = count;
        this.type = type;
        this.what = what;
    }
    
    public ActionSetCount(final String detailedString) {
        assert detailedString.startsWith("[SetCount:");
        final String strType = Action.extractData(detailedString, "type");
        this.type = (strType.isEmpty() ? null : SiteType.valueOf(strType));
        final String strTo = Action.extractData(detailedString, "to");
        this.to = Integer.parseInt(strTo);
        final String strWhat = Action.extractData(detailedString, "what");
        this.what = Integer.parseInt(strWhat);
        final String strCount = Action.extractData(detailedString, "count");
        this.count = Integer.parseInt(strCount);
        final String strDecision = Action.extractData(detailedString, "decision");
        this.decision = (!strDecision.isEmpty() && Boolean.parseBoolean(strDecision));
    }
    
    @Override
    public Action apply(final Context context, final boolean store) {
        this.type = ((this.type == null) ? context.board().defaultSite() : this.type);
        if (this.to >= context.board().topology().getGraphElements(this.type).size()) {
            this.type = SiteType.Cell;
        }
        final int contID = (this.type == SiteType.Cell) ? context.containerId()[this.to] : 0;
        final ContainerState sc = context.state().containerStates()[contID];
        if (this.what != 0 && sc.count(this.to, this.type) == 0) {
            final Component piece = context.components()[this.what];
            final int owner = piece.owner();
            context.state().owned().add(owner, this.what, this.to, this.type);
        }
        sc.setSite(context.state(), this.to, -1, this.what, this.count, -1, -1, -1, this.type);
        return this;
    }
    
    @Override
    public String toTrialFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        sb.append("[SetCount:");
        if (this.type != null || (context != null && this.type != context.board().defaultSite())) {
            sb.append("type=" + this.type);
            sb.append(",to=" + this.to);
        }
        else {
            sb.append("to=" + this.to);
        }
        sb.append(",what=" + this.what);
        sb.append(",count=" + this.count);
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
        result = 31 * result + this.count;
        result = 31 * result + (this.decision ? 1231 : 1237);
        result = 31 * result + this.to;
        result = 31 * result + ((this.type == null) ? 0 : this.type.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ActionSetCount)) {
            return false;
        }
        final ActionSetCount other = (ActionSetCount)obj;
        return this.decision == other.decision && this.to == other.to && this.count == other.count && this.type == other.type;
    }
    
    @Override
    public String getDescription() {
        return "SetCount";
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
        sb.append("+");
        if (this.what > 0 && this.what < context.components().length) {
            sb.append(context.components()[this.what].name());
            if (this.count > 1) {
                sb.append("x" + this.count);
            }
        }
        return sb.toString();
    }
    
    @Override
    public String toMoveFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        sb.append("(Add ");
        if (this.what > 0 && this.what < context.components().length) {
            sb.append(context.components()[this.what].name());
            if (this.count > 1) {
                sb.append("x" + this.count);
            }
        }
        String newTo = String.valueOf(this.to);
        if (SettingsGeneral.isMoveCoord()) {
            final int cid = (this.type == SiteType.Cell || (this.type == null && context.board().defaultSite() == SiteType.Cell)) ? context.containerId()[this.to] : 0;
            if (cid == 0) {
                final SiteType realType = (this.type != null) ? this.type : context.board().defaultSite();
                newTo = context.game().equipment().containers()[cid].topology().getGraphElements(realType).get(this.to).label();
            }
        }
        if (this.type != null && this.type != context.board().defaultSite()) {
            sb.append(" to " + this.type + " " + newTo);
        }
        else {
            sb.append(" to " + newTo);
        }
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
    public int from() {
        return this.to;
    }
    
    @Override
    public int to() {
        return this.to;
    }
    
    @Override
    public int what() {
        return this.what;
    }
    
    @Override
    public int count() {
        return this.count;
    }
}
