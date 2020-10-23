// 
// Decompiled by Procyon v0.5.36
// 

package util.action.move;

import game.equipment.component.Component;
import game.types.board.SiteType;
import main.SettingsGeneral;
import util.Context;
import util.action.Action;
import util.action.BaseAction;
import util.state.containerState.ContainerState;

public final class ActionCopy extends BaseAction
{
    private static final long serialVersionUID = 1L;
    private final SiteType typeFrom;
    private final int from;
    private final SiteType typeTo;
    private final int to;
    
    public ActionCopy(final SiteType typeFrom, final int from, final SiteType typeTo, final int to) {
        this.from = from;
        this.to = to;
        this.typeFrom = typeFrom;
        this.typeTo = typeTo;
    }
    
    public ActionCopy(final String detailedString) {
        assert detailedString.startsWith("[Copy:");
        final String strTypeFrom = Action.extractData(detailedString, "typeFrom");
        this.typeFrom = (strTypeFrom.isEmpty() ? null : SiteType.valueOf(strTypeFrom));
        final String strFrom = Action.extractData(detailedString, "from");
        this.from = Integer.parseInt(strFrom);
        final String strTypeTo = Action.extractData(detailedString, "typeTo");
        this.typeTo = (strTypeTo.isEmpty() ? null : SiteType.valueOf(strTypeTo));
        final String strTo = Action.extractData(detailedString, "to");
        this.to = Integer.parseInt(strTo);
        final String strDecision = Action.extractData(detailedString, "decision");
        this.decision = (!strDecision.isEmpty() && Boolean.parseBoolean(strDecision));
    }
    
    @Override
    public Action apply(final Context context, final boolean store) {
        final int contIdA = context.containerId()[this.from];
        final int contIdB = context.containerId()[this.to];
        final ContainerState csA = context.state().containerStates()[contIdA];
        final ContainerState csB = context.state().containerStates()[contIdB];
        if (context.game().hiddenInformation()) {
            for (int index = 1; index < context.players().size(); ++index) {
                if (csA.isInvisible(this.from, index, this.typeFrom)) {
                    csB.setInvisibleCell(context.state(), this.to, index);
                }
                else if (csA.isMasked(this.from, index, this.typeFrom)) {
                    csB.setMaskedCell(context.state(), this.to, index);
                }
                else {
                    csB.setVisibleCell(context.state(), this.to, index);
                }
            }
            if (csA.count(this.from, this.typeFrom) == 1) {
                csA.setInvisibleCell(context.state(), this.from, context.state().mover());
            }
        }
        final int what = csA.what(this.from, this.typeFrom);
        int currentStateA = -1;
        int currentRotationA = -1;
        Component piece = null;
        currentStateA = ((csA.what(this.from, this.typeFrom) == 0) ? -1 : csA.state(this.from, this.typeFrom));
        currentRotationA = csA.rotation(this.to, this.typeFrom);
        if (currentStateA != -1) {
            csB.setSite(context.state(), this.to, -1, -1, -1, currentStateA, -1, context.game().usesLineOfPlay() ? 1 : -1, this.typeTo);
        }
        if (currentRotationA != -1) {
            csB.setSite(context.state(), this.to, -1, -1, -1, -1, currentRotationA, context.game().usesLineOfPlay() ? 1 : -1, this.typeTo);
        }
        final int who = (what < 1) ? 0 : context.components()[what].owner();
        if (csB.what(this.to, this.typeTo) != 0 && csB.what(this.to, this.typeTo) != what) {
            final Component pieceToRemove = context.components()[csB.what(this.to, this.typeTo)];
            final int owner = pieceToRemove.owner();
            context.state().owned().remove(owner, csB.what(this.to, this.typeTo), this.to);
        }
        if (csB.what(this.to, this.typeTo) == what && csB.count(this.to, this.typeTo) > 0) {
            csB.setSite(context.state(), this.to, -1, -1, context.game().requiresCount() ? (csB.count(this.to, this.typeTo) + 1) : 1, -1, -1, context.game().usesLineOfPlay() ? 1 : -1, this.typeTo);
        }
        else {
            csB.setSite(context.state(), this.to, who, what, 1, -1, -1, context.game().usesLineOfPlay() ? 1 : -1, this.typeTo);
        }
        if (what != 0 && csB.count(this.to, this.typeTo) == 1) {
            piece = context.components()[what];
            final int owner2 = piece.owner();
            context.state().owned().add(owner2, what, this.to, this.typeTo);
        }
        return this;
    }
    
    @Override
    public String toTrialFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        sb.append("[Copy:");
        if (this.typeFrom != null || (context != null && this.typeFrom != context.board().defaultSite())) {
            sb.append("typeFrom=" + this.typeFrom);
            sb.append(",from=" + this.from);
        }
        else {
            sb.append("from=" + this.from);
        }
        if (this.typeTo != null || (context != null && this.typeTo != context.board().defaultSite())) {
            sb.append(",typeTo=" + this.typeTo);
        }
        sb.append(",to=" + this.to);
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
        result = 31 * result + this.from;
        result = 31 * result + this.to;
        result = 31 * result + ((this.typeFrom == null) ? 0 : this.typeFrom.hashCode());
        result = 31 * result + ((this.typeTo == null) ? 0 : this.typeTo.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ActionCopy)) {
            return false;
        }
        final ActionCopy other = (ActionCopy)obj;
        return this.decision == other.decision && this.from == other.from && this.to == other.to && this.typeFrom == other.typeFrom && this.typeTo == other.typeTo;
    }
    
    @Override
    public String getDescription() {
        return "Copy";
    }
    
    @Override
    public String toTurnFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        String newFrom = String.valueOf(this.from);
        if (SettingsGeneral.isMoveCoord()) {
            final int cid = (this.typeFrom == SiteType.Cell || (this.typeFrom == null && context.board().defaultSite() == SiteType.Cell)) ? context.containerId()[this.from] : 0;
            if (cid == 0) {
                final SiteType realType = (this.typeFrom != null) ? this.typeFrom : context.board().defaultSite();
                newFrom = context.game().equipment().containers()[cid].topology().getGraphElements(realType).get(this.from).label();
            }
        }
        if (this.typeFrom != null && this.typeFrom != context.board().defaultSite()) {
            sb.append(this.typeFrom + " " + newFrom);
        }
        else {
            sb.append(newFrom);
        }
        String newTo = String.valueOf(this.to);
        if (SettingsGeneral.isMoveCoord()) {
            final int cid2 = (this.typeTo == SiteType.Cell || (this.typeTo == null && context.board().defaultSite() == SiteType.Cell)) ? context.containerId()[this.to] : 0;
            if (cid2 == 0) {
                final SiteType realType2 = (this.typeTo != null) ? this.typeTo : context.board().defaultSite();
                newTo = context.game().equipment().containers()[cid2].topology().getGraphElements(realType2).get(this.to).label();
            }
        }
        if (this.typeTo != null && this.typeTo != context.board().defaultSite()) {
            sb.append("-" + this.typeTo + " " + newTo);
        }
        else {
            sb.append("-" + newTo);
        }
        sb.append(" (Copy)");
        return sb.toString();
    }
    
    @Override
    public String toMoveFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        sb.append("(Copy ");
        String newFrom = String.valueOf(this.from);
        if (SettingsGeneral.isMoveCoord()) {
            final int cid = (this.typeFrom == SiteType.Cell || (this.typeFrom == null && context.board().defaultSite() == SiteType.Cell)) ? context.containerId()[this.from] : 0;
            if (cid == 0) {
                final SiteType realType = (this.typeFrom != null) ? this.typeFrom : context.board().defaultSite();
                newFrom = context.game().equipment().containers()[cid].topology().getGraphElements(realType).get(this.from).label();
            }
        }
        if (this.typeFrom != null && this.typeTo != null && (this.typeFrom != context.board().defaultSite() || this.typeFrom != this.typeTo)) {
            sb.append(this.typeFrom + " " + newFrom);
        }
        else {
            sb.append(newFrom);
        }
        String newTo = String.valueOf(this.to);
        if (SettingsGeneral.isMoveCoord()) {
            final int cid2 = (this.typeTo == SiteType.Cell || (this.typeTo == null && context.board().defaultSite() == SiteType.Cell)) ? context.containerId()[this.to] : 0;
            if (cid2 == 0) {
                final SiteType realType2 = (this.typeTo != null) ? this.typeTo : context.board().defaultSite();
                newTo = context.game().equipment().containers()[cid2].topology().getGraphElements(realType2).get(this.to).label();
            }
        }
        if (this.typeFrom != null && this.typeTo != null && (this.typeTo != context.board().defaultSite() || this.typeFrom != this.typeTo)) {
            sb.append(" - " + this.typeTo + " " + newTo);
        }
        else {
            sb.append("-" + newTo);
        }
        sb.append(')');
        return sb.toString();
    }
    
    @Override
    public SiteType fromType() {
        return this.typeFrom;
    }
    
    @Override
    public SiteType toType() {
        return this.typeTo;
    }
    
    @Override
    public int from() {
        return this.from;
    }
    
    @Override
    public int to() {
        return this.to;
    }
    
    @Override
    public int count() {
        return 1;
    }
}
