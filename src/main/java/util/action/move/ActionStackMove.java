// 
// Decompiled by Procyon v0.5.36
// 

package util.action.move;

import game.types.board.SiteType;
import main.SettingsGeneral;
import util.Context;
import util.action.Action;
import util.action.BaseAction;
import util.state.containerState.ContainerState;

public final class ActionStackMove extends BaseAction
{
    private static final long serialVersionUID = 1L;
    private final SiteType typeFrom;
    private final int from;
    private int levelFrom;
    private final SiteType typeTo;
    private final int to;
    private int levelTo;
    private final int numLevel;
    
    public ActionStackMove(final SiteType typeFrom, final int from, final SiteType typeTo, final int to, final int numLevel) {
        this.levelFrom = 0;
        this.levelTo = 0;
        this.from = from;
        this.to = to;
        this.numLevel = numLevel;
        this.typeFrom = typeFrom;
        this.typeTo = typeTo;
    }
    
    public ActionStackMove(final String detailedString) {
        this.levelFrom = 0;
        this.levelTo = 0;
        assert detailedString.startsWith("[StackMove:");
        final String strTypeFrom = Action.extractData(detailedString, "typeFrom");
        this.typeFrom = (strTypeFrom.isEmpty() ? null : SiteType.valueOf(strTypeFrom));
        final String strFrom = Action.extractData(detailedString, "from");
        this.from = Integer.parseInt(strFrom);
        final String strTypeTo = Action.extractData(detailedString, "typeTo");
        this.typeTo = (strTypeTo.isEmpty() ? null : SiteType.valueOf(strTypeTo));
        final String strTo = Action.extractData(detailedString, "to");
        this.to = Integer.parseInt(strTo);
        final String strNumLevel = Action.extractData(detailedString, "numLevel");
        this.numLevel = Integer.parseInt(strNumLevel);
        final String strDecision = Action.extractData(detailedString, "decision");
        this.decision = (!strDecision.isEmpty() && Boolean.parseBoolean(strDecision));
    }
    
    @Override
    public Action apply(final Context context, final boolean store) {
        final int contIdA = context.containerId()[this.from];
        final int contIdB = context.containerId()[this.to];
        final ContainerState csA = context.state().containerStates()[contIdA];
        final ContainerState csB = context.state().containerStates()[contIdB];
        final int sizeStackA = csA.sizeStack(this.from, this.typeFrom);
        final int what = csA.what(this.from, this.typeFrom);
        if (what == 0 || sizeStackA < this.numLevel) {
            return this;
        }
        final int[] movedElement = new int[this.numLevel];
        final int[] ownerElement = new int[this.numLevel];
        for (int i = 0; i < this.numLevel; ++i) {
            final int whatTop = csA.what(this.from, this.typeFrom);
            movedElement[i] = whatTop;
            final int whoTop = csA.who(this.from, this.typeFrom);
            ownerElement[i] = whoTop;
            final int topLevel = csA.sizeStack(this.from, this.typeFrom) - 1;
            csA.remove(context.state(), this.from, this.typeFrom);
            context.state().owned().remove(whoTop, what, this.from, topLevel);
        }
        if (csA.sizeStack(this.from, this.typeFrom) == 0) {
            csA.addToEmpty(this.from, this.typeFrom);
        }
        for (int i = movedElement.length - 1; i >= 0; --i) {
            csB.addItemGeneric(context.state(), this.to, movedElement[i], ownerElement[i], context.game(), this.typeTo);
            context.state().owned().add(ownerElement[i], movedElement[i], this.to, csB.sizeStack(this.to, this.typeTo) - 1, this.typeTo);
        }
        return this;
    }
    
    @Override
    public String toTrialFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        sb.append("[StackMove:");
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
        sb.append(",numLevel=" + this.numLevel);
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
        result = 31 * result + this.numLevel;
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
        if (!(obj instanceof ActionStackMove)) {
            return false;
        }
        final ActionStackMove other = (ActionStackMove)obj;
        return this.numLevel == other.numLevel && this.decision == other.decision && this.from == other.from && this.to == other.to && this.typeFrom == other.typeFrom && this.typeTo == other.typeTo;
    }
    
    @Override
    public String getDescription() {
        return "StackMove";
    }
    
    @Override
    public String toTurnFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        String newFrom = this.from + "";
        if (SettingsGeneral.isMoveCoord()) {
            final int cid = (this.typeFrom == SiteType.Cell || (this.typeFrom == null && context.board().defaultSite() == SiteType.Cell)) ? context.containerId()[this.from] : 0;
            if (cid == 0) {
                final SiteType realType = (this.typeFrom != null) ? this.typeFrom : context.board().defaultSite();
                newFrom = context.game().equipment().containers()[cid].topology().getGraphElements(realType).get(this.from).label();
            }
        }
        if (this.typeFrom != null && !this.typeFrom.equals(context.board().defaultSite())) {
            sb.append(this.typeFrom + " " + newFrom);
        }
        else {
            sb.append(newFrom);
        }
        if (this.levelFrom != -1) {
            sb.append(":" + this.levelFrom);
        }
        String newTo = this.to + "";
        if (SettingsGeneral.isMoveCoord()) {
            final int cid2 = (this.typeTo == SiteType.Cell || (this.typeTo == null && context.board().defaultSite() == SiteType.Cell)) ? context.containerId()[this.to] : 0;
            if (cid2 == 0) {
                final SiteType realType2 = (this.typeTo != null) ? this.typeTo : context.board().defaultSite();
                newTo = context.game().equipment().containers()[cid2].topology().getGraphElements(realType2).get(this.to).label();
            }
        }
        if (this.typeTo != null && !this.typeTo.equals(context.board().defaultSite())) {
            sb.append("-" + this.typeTo + " " + newTo);
        }
        else {
            sb.append("-" + newTo);
        }
        if (this.levelTo != -1) {
            sb.append(":" + this.levelTo);
        }
        sb.append("^" + this.numLevel);
        return sb.toString();
    }
    
    @Override
    public String toMoveFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        sb.append("(Move ");
        String newFrom = this.from + "";
        if (SettingsGeneral.isMoveCoord()) {
            final int cid = (this.typeFrom == SiteType.Cell || (this.typeFrom == null && context.board().defaultSite() == SiteType.Cell)) ? context.containerId()[this.from] : 0;
            if (cid == 0) {
                final SiteType realType = (this.typeFrom != null) ? this.typeFrom : context.board().defaultSite();
                newFrom = context.game().equipment().containers()[cid].topology().getGraphElements(realType).get(this.from).label();
            }
        }
        if (this.typeFrom != null && this.typeTo != null && (!this.typeFrom.equals(context.board().defaultSite()) || !this.typeFrom.equals(this.typeTo))) {
            sb.append(this.typeFrom + " " + newFrom);
        }
        else {
            sb.append(newFrom);
        }
        if (this.levelFrom != -1) {
            sb.append("/" + this.levelFrom);
        }
        String newTo = this.to + "";
        if (SettingsGeneral.isMoveCoord()) {
            final int cid2 = (this.typeTo == SiteType.Cell || (this.typeTo == null && context.board().defaultSite() == SiteType.Cell)) ? context.containerId()[this.to] : 0;
            if (cid2 == 0) {
                final SiteType realType2 = (this.typeTo != null) ? this.typeTo : context.board().defaultSite();
                newTo = context.game().equipment().containers()[cid2].topology().getGraphElements(realType2).get(this.to).label();
            }
        }
        if (this.typeFrom != null && this.typeTo != null && (!this.typeTo.equals(context.board().defaultSite()) || !this.typeFrom.equals(this.typeTo))) {
            sb.append(" - " + this.typeTo + " " + newTo);
        }
        else {
            sb.append("-" + newTo);
        }
        if (this.levelTo != -1) {
            sb.append("/" + this.levelTo);
        }
        sb.append(" numLevel=" + this.numLevel);
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
    public int levelFrom() {
        return this.levelFrom;
    }
    
    @Override
    public int levelTo() {
        return this.levelTo;
    }
    
    @Override
    public void setLevelFrom(final int levelA) {
        this.levelFrom = levelA;
    }
    
    @Override
    public void setLevelTo(final int levelB) {
        this.levelTo = levelB;
    }
}
