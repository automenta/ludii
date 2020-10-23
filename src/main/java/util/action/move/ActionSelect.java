// 
// Decompiled by Procyon v0.5.36
// 

package util.action.move;

import game.types.board.SiteType;
import main.SettingsGeneral;
import util.Context;
import util.action.Action;
import util.action.ActionType;
import util.action.BaseAction;

public final class ActionSelect extends BaseAction
{
    private static final long serialVersionUID = 1L;
    private final int from;
    private final int to;
    private final int levelFrom;
    private final int levelTo;
    private final SiteType typeFrom;
    private final SiteType typeTo;
    
    public ActionSelect(final SiteType type, final int from, final int levelFrom, final int to, final int levelTo) {
        this.from = from;
        this.to = to;
        this.typeFrom = type;
        this.typeTo = type;
        this.levelFrom = levelFrom;
        this.levelTo = levelTo;
    }
    
    public ActionSelect(final String detailedString) {
        assert detailedString.startsWith("[Select:");
        final String strTypeFrom = Action.extractData(detailedString, "typeFrom");
        this.typeFrom = (strTypeFrom.isEmpty() ? null : SiteType.valueOf(strTypeFrom));
        final String strFrom = Action.extractData(detailedString, "from");
        this.from = (strFrom.isEmpty() ? -1 : Integer.parseInt(strFrom));
        final String strLevelFrom = Action.extractData(detailedString, "levelFrom");
        this.levelFrom = (strLevelFrom.isEmpty() ? -1 : Integer.parseInt(strLevelFrom));
        final String strTypeTo = Action.extractData(detailedString, "typeTo");
        this.typeTo = (strTypeTo.isEmpty() ? this.typeFrom : SiteType.valueOf(strTypeTo));
        final String strTo = Action.extractData(detailedString, "to");
        this.to = (strTo.isEmpty() ? -1 : Integer.parseInt(strTo));
        final String strLevelTo = Action.extractData(detailedString, "levelTo");
        this.levelTo = (strLevelTo.isEmpty() ? -1 : Integer.parseInt(strLevelTo));
        final String strDecision = Action.extractData(detailedString, "decision");
        this.decision = (!strDecision.isEmpty() && Boolean.parseBoolean(strDecision));
    }
    
    @Override
    public Action apply(final Context context, final boolean store) {
        return this;
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
        if (!(obj instanceof ActionSelect)) {
            return false;
        }
        final ActionSelect other = (ActionSelect)obj;
        return this.decision == other.decision && this.from == other.from && this.to == other.to && this.typeFrom == other.typeFrom && this.typeTo == other.typeTo;
    }
    
    @Override
    public String toTrialFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        sb.append("[Select:");
        if (this.typeFrom != null || (context != null && this.typeFrom != context.board().defaultSite())) {
            sb.append("typeFrom=" + this.typeFrom);
            sb.append(",from=" + this.from);
        }
        else {
            sb.append("from=" + this.from);
        }
        if (this.levelFrom != -1) {
            sb.append(",levelFrom=" + this.levelFrom);
        }
        if (this.to != -1) {
            if (this.typeTo != null) {
                sb.append(",typeTo=" + this.typeTo);
            }
            sb.append(",to=" + this.to);
            if (this.levelTo != -1) {
                sb.append(",levelTo=" + this.levelTo);
            }
        }
        if (this.decision) {
            sb.append(",decision=" + this.decision);
        }
        sb.append(']');
        return sb.toString();
    }
    
    @Override
    public String getDescription() {
        return "Select";
    }
    
    @Override
    public String toTurnFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        sb.append("*");
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
            sb.append("/" + this.levelFrom);
        }
        if (this.to != -1) {
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
                sb.append("/" + this.levelTo);
            }
        }
        return sb.toString();
    }
    
    @Override
    public String toMoveFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        sb.append("(Select ");
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
        if (this.to != -1) {
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
        }
        sb.append(')');
        return sb.toString();
    }
    
    @Override
    public int from() {
        return this.from;
    }
    
    @Override
    public int to() {
        return (this.to == -1) ? this.from : this.to;
    }
    
    @Override
    public int levelFrom() {
        return (this.levelFrom == -1) ? 0 : this.levelFrom;
    }
    
    @Override
    public int levelTo() {
        return (this.levelTo == -1) ? 0 : this.levelTo;
    }
    
    @Override
    public SiteType fromType() {
        return this.typeFrom;
    }
    
    @Override
    public SiteType toType() {
        return this.typeFrom;
    }
    
    @Override
    public ActionType actionType() {
        return ActionType.Select;
    }
}