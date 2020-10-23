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

public class ActionReset extends BaseAction
{
    private static final long serialVersionUID = 1L;
    private SiteType type;
    private final int var;
    private final int max;
    
    public ActionReset(final SiteType type, final int var, final int whatMax) {
        this.type = type;
        this.var = var;
        this.max = whatMax;
    }
    
    public ActionReset(final String detailedString) {
        assert detailedString.startsWith("[Reset:");
        final String strType = Action.extractData(detailedString, "type");
        this.type = (strType.isEmpty() ? null : SiteType.valueOf(strType));
        final String strTo = Action.extractData(detailedString, "var");
        this.var = Integer.parseInt(strTo);
        final String strMax = Action.extractData(detailedString, "max");
        this.max = Integer.parseInt(strMax);
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
            ps.resetVariable(SiteType.Vertex, this.var, this.max);
        }
        if (this.type == SiteType.Edge) {
            ps.resetVariable(SiteType.Edge, this.var, this.max);
        }
        if (this.type == SiteType.Cell) {
            ps.resetVariable(SiteType.Cell, this.var, this.max);
        }
        return this;
    }
    
    @Override
    public String toTrialFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        sb.append("[Reset:");
        if (this.type != null || (context != null && this.type != context.board().defaultSite())) {
            sb.append("type=" + this.type);
            sb.append(",var=" + this.var);
        }
        else {
            sb.append("var=" + this.var);
        }
        sb.append(",max=" + this.max);
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
        result = 31 * result + this.var;
        result = 31 * result + this.max;
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ActionReset)) {
            return false;
        }
        final ActionReset other = (ActionReset)obj;
        return this.decision == other.decision && this.var == other.var && this.max == other.max && this.type == other.type;
    }
    
    @Override
    public String getDescription() {
        return "Reset";
    }
    
    @Override
    public String toTurnFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        sb.append("Reset ");
        String newTo = String.valueOf(this.var);
        if (SettingsGeneral.isMoveCoord()) {
            final int cid = (this.type == SiteType.Cell || (this.type == null && context.board().defaultSite() == SiteType.Cell)) ? context.containerId()[this.var] : 0;
            if (cid == 0) {
                final SiteType realType = (this.type != null) ? this.type : context.board().defaultSite();
                newTo = context.game().equipment().containers()[cid].topology().getGraphElements(realType).get(this.var).label();
            }
        }
        if (this.type != null && this.type != context.board().defaultSite()) {
            sb.append(this.type + " " + newTo);
        }
        else {
            sb.append(newTo);
        }
        return sb.toString();
    }
    
    @Override
    public String toMoveFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        sb.append("(Reset ");
        String newTo = String.valueOf(this.var);
        if (SettingsGeneral.isMoveCoord()) {
            final int cid = (this.type == SiteType.Cell || (this.type == null && context.board().defaultSite() == SiteType.Cell)) ? context.containerId()[this.var] : 0;
            if (cid == 0) {
                final SiteType realType = (this.type != null) ? this.type : context.board().defaultSite();
                newTo = context.game().equipment().containers()[cid].topology().getGraphElements(realType).get(this.var).label();
            }
        }
        if (this.type != null && this.type != context.board().defaultSite()) {
            sb.append(this.type + " " + newTo);
        }
        else {
            sb.append(newTo);
        }
        sb.append(')');
        return sb.toString();
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
    public int count() {
        return 1;
    }
    
    @Override
    public SiteType fromType() {
        return this.type;
    }
    
    @Override
    public SiteType toType() {
        return this.type;
    }
}
