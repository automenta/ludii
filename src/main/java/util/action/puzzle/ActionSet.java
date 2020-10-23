// 
// Decompiled by Procyon v0.5.36
// 

package util.action.puzzle;

import game.types.board.SiteType;
import main.SettingsGeneral;
import util.Context;
import util.action.Action;
import util.action.BaseAction;
import util.state.containerState.ContainerState;
import util.state.puzzleState.ContainerDeductionPuzzleState;

public class ActionSet extends BaseAction
{
    private static final long serialVersionUID = 1L;
    private SiteType type;
    private final int var;
    private final int value;
    
    public ActionSet(final SiteType type, final int var, final int what) {
        this.var = var;
        this.value = what;
        this.type = type;
    }
    
    public ActionSet(final String detailedString) {
        assert detailedString.startsWith("[Set:");
        final String strType = Action.extractData(detailedString, "type");
        this.type = (strType.isEmpty() ? null : SiteType.valueOf(strType));
        final String strTo = Action.extractData(detailedString, "var");
        this.var = Integer.parseInt(strTo);
        final String strValue = Action.extractData(detailedString, "value");
        this.value = Integer.parseInt(strValue);
        final String strDecision = Action.extractData(detailedString, "decision");
        this.decision = (!strDecision.isEmpty() && Boolean.parseBoolean(strDecision));
    }
    
    @Override
    public Action apply(final Context context, final boolean store) {
        this.type = ((this.type == null) ? context.board().defaultSite() : this.type);
        final int contID = context.containerId()[0];
        final ContainerState sc = context.state().containerStates()[contID];
        final ContainerDeductionPuzzleState ps = (ContainerDeductionPuzzleState)sc;
        if (this.type == SiteType.Vertex) {
            ps.setVert(this.var, this.value);
        }
        else if (this.type == SiteType.Edge) {
            ps.setEdge(this.var, this.value);
        }
        else {
            ps.setCell(this.var, this.value);
        }
        return this;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + (this.decision ? 1231 : 1237);
        result = 31 * result + this.var;
        result = 31 * result + this.value;
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ActionSet)) {
            return false;
        }
        final ActionSet other = (ActionSet)obj;
        return this.var == other.var && this.value == other.value && this.type == other.type;
    }
    
    @Override
    public String toTrialFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        sb.append("[Set:");
        if (this.type != null || (context != null && this.type != context.board().defaultSite())) {
            sb.append("type=").append(this.type);
            sb.append(",var=").append(this.var);
        }
        else {
            sb.append("var").append(this.var);
        }
        sb.append(",value=").append(this.value);
        if (this.decision) {
            sb.append(",decision=").append(this.decision);
        }
        sb.append(']');
        return sb.toString();
    }
    
    @Override
    public String getDescription() {
        return "Set";
    }
    
    @Override
    public String toTurnFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        String newTo = String.valueOf(this.var);
        if (SettingsGeneral.isMoveCoord()) {
            final int cid = (this.type == SiteType.Cell || (this.type == null && context.board().defaultSite() == SiteType.Cell)) ? context.containerId()[this.var] : 0;
            if (cid == 0) {
                final SiteType realType = (this.type != null) ? this.type : context.board().defaultSite();
                newTo = context.game().equipment().containers()[cid].topology().getGraphElements(realType).get(this.var).label();
            }
        }
        if (this.type != null && this.type != context.board().defaultSite()) {
            sb.append(this.type).append(" ").append(newTo);
        }
        else {
            sb.append(newTo);
        }
        sb.append("=").append(this.value);
        return sb.toString();
    }
    
    @Override
    public String toMoveFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        sb.append("(");
        String newTo = String.valueOf(this.var);
        if (SettingsGeneral.isMoveCoord()) {
            final int cid = (this.type == SiteType.Cell || (this.type == null && context.board().defaultSite() == SiteType.Cell)) ? context.containerId()[this.var] : 0;
            if (cid == 0) {
                final SiteType realType = (this.type != null) ? this.type : context.board().defaultSite();
                newTo = context.game().equipment().containers()[cid].topology().getGraphElements(realType).get(this.var).label();
            }
        }
        if (this.type != null && this.type != context.board().defaultSite()) {
            sb.append(this.type).append(" ").append(newTo);
        }
        else {
            sb.append(newTo);
        }
        sb.append(" = ").append(this.value);
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
        return this.var;
    }
    
    @Override
    public int to() {
        return this.var;
    }
    
    @Override
    public int what() {
        return this.value;
    }
    
    @Override
    public int count() {
        return 1;
    }
}
