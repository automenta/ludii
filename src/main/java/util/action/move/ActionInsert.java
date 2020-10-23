// 
// Decompiled by Procyon v0.5.36
// 

package util.action.move;

import game.equipment.component.Component;
import game.equipment.container.board.Track;
import game.types.board.SiteType;
import gnu.trove.list.array.TIntArrayList;
import main.SettingsGeneral;
import util.Context;
import util.action.Action;
import util.action.BaseAction;
import util.state.containerState.ContainerState;
import util.state.onTrack.OnTrackIndices;

public final class ActionInsert extends BaseAction
{
    private static final long serialVersionUID = 1L;
    private SiteType type;
    private final int to;
    private final int level;
    private final int what;
    private final int state;
    
    public ActionInsert(final SiteType type, final int to, final int level, final int what, final int state) {
        this.type = type;
        this.to = to;
        this.level = level;
        this.what = what;
        this.state = state;
    }
    
    public ActionInsert(final String detailedString) {
        assert detailedString.startsWith("[Insert:");
        final String strType = Action.extractData(detailedString, "type");
        this.type = (strType.isEmpty() ? null : SiteType.valueOf(strType));
        final String strTo = Action.extractData(detailedString, "to");
        this.to = Integer.parseInt(strTo);
        final String strLevel = Action.extractData(detailedString, "level");
        this.level = Integer.parseInt(strLevel);
        final String strWhat = Action.extractData(detailedString, "what");
        this.what = Integer.parseInt(strWhat);
        final String strState = Action.extractData(detailedString, "state");
        this.state = (strState.isEmpty() ? -1 : Integer.parseInt(strState));
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
        final ContainerState container = context.state().containerStates()[contID];
        final int who = (this.what < 1) ? 0 : context.components()[this.what].owner();
        final int sizeStack = container.sizeStack(this.to, this.type);
        if (this.level == sizeStack) {
            container.addItem(context.state(), this.to, this.what, who, context.game());
            container.removeFromEmpty(this.to, this.type);
            if (this.what != 0) {
                final Component piece = context.components()[this.what];
                final int owner = piece.owner();
                context.state().owned().add(owner, this.what, this.to, container.sizeStack(this.to, this.type) - 1, this.type);
            }
        }
        else {
            for (int i = sizeStack - 1; i >= this.level; --i) {
                final int owner = container.who(this.to, i, this.type);
                final int piece2 = container.what(this.to, i, this.type);
                context.state().owned().remove(owner, piece2, this.to, i);
                context.state().owned().add(owner, piece2, this.to, i + 1, this.type);
            }
            container.insert(context.state(), this.to, this.level, this.what, who, context.game());
            final Component piece = context.components()[this.what];
            final int owner = piece.owner();
            context.state().owned().add(owner, this.what, this.to, this.level, this.type);
        }
        final OnTrackIndices onTrackIndices = context.state().onTrackIndices();
        if (onTrackIndices != null) {
            for (final Track track : context.board().tracks()) {
                final int trackIdx = track.trackIdx();
                final TIntArrayList indices = onTrackIndices.locToIndex(trackIdx, this.to);
                if (!indices.isEmpty()) {
                    onTrackIndices.add(trackIdx, this.what, 1, indices.getQuick(0));
                }
            }
        }
        return this;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + (this.decision ? 1231 : 1237);
        result = 31 * result + this.to;
        result = 31 * result + this.level;
        result = 31 * result + this.state;
        result = 31 * result + this.what;
        result = 31 * result + ((this.type == null) ? 0 : this.type.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ActionInsert)) {
            return false;
        }
        final ActionInsert other = (ActionInsert)obj;
        return this.decision == other.decision && this.to == other.to && this.level == other.level && this.state == other.state && this.what == other.what && this.type == other.type;
    }
    
    @Override
    public String toTrialFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        sb.append("[Insert:");
        if (this.type != null || (context != null && this.type != context.board().defaultSite())) {
            sb.append("type=").append(this.type);
            sb.append(",to=").append(this.to);
        }
        else {
            sb.append("to=").append(this.to);
        }
        sb.append(",level=").append(this.level);
        sb.append(",what=").append(this.what);
        if (this.state != -1) {
            sb.append(",state=").append(this.state);
        }
        if (this.decision) {
            sb.append(",decision=").append(this.decision);
        }
        sb.append(']');
        return sb.toString();
    }
    
    @Override
    public String getDescription() {
        return "Insert";
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
        if (this.level != -1) {
            sb.append("/").append(this.level);
        }
        sb.append("^");
        if (this.what > 0 && this.what < context.components().length) {
            sb.append(context.components()[this.what].name());
        }
        if (this.state != -1) {
            sb.append("=").append(this.state);
        }
        return sb.toString();
    }
    
    @Override
    public String toMoveFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        sb.append("(Insert ");
        if (this.what > 0 && this.what < context.components().length) {
            sb.append(context.components()[this.what].name());
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
            sb.append(" to ").append(this.type).append(" ").append(newTo);
        }
        else {
            sb.append(" to ").append(newTo);
        }
        sb.append("/").append(this.level);
        if (this.state != -1) {
            sb.append(" state=").append(this.state);
        }
        sb.append(')');
        return sb.toString();
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
    public int levelFrom() {
        return this.level;
    }
    
    @Override
    public int levelTo() {
        return this.level;
    }
    
    @Override
    public int what() {
        return this.what;
    }
    
    @Override
    public int state() {
        return this.state;
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
