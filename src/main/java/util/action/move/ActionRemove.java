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
import util.action.ActionType;
import util.action.BaseAction;
import util.state.containerStackingState.BaseContainerStateStacking;
import util.state.containerState.ContainerState;
import util.state.onTrack.OnTrackIndices;

public final class ActionRemove extends BaseAction
{
    private static final long serialVersionUID = 1L;
    private final int to;
    private final boolean applied;
    private SiteType type;
    
    public ActionRemove(final SiteType type, final int to, final boolean applied) {
        this.to = to;
        this.applied = applied;
        this.type = type;
    }
    
    public ActionRemove(final String detailedString) {
        assert detailedString.startsWith("[Remove:");
        final String strType = Action.extractData(detailedString, "type");
        this.type = (strType.isEmpty() ? null : SiteType.valueOf(strType));
        final String strTo = Action.extractData(detailedString, "to");
        this.to = Integer.parseInt(strTo);
        final String strApplied = Action.extractData(detailedString, "applied");
        this.applied = (strApplied.isEmpty() || Boolean.parseBoolean(strApplied));
        final String strDecision = Action.extractData(detailedString, "decision");
        this.decision = (!strDecision.isEmpty() && Boolean.parseBoolean(strDecision));
    }
    
    @Override
    public Action apply(final Context context, final boolean store) {
        this.type = ((this.type == null) ? context.board().defaultSite() : this.type);
        final int contID = (this.type == SiteType.Cell) ? context.containerId()[this.to] : 0;
        final int site = this.to;
        if (!this.applied) {
            context.state().setPieceToRemove(site);
            return this;
        }
        final ContainerState cs = context.state().containerStates()[contID];
        final int pieceIdx = cs.remove(context.state(), site, this.type);
        if (context.game().isStacking()) {
            final BaseContainerStateStacking csStack = (BaseContainerStateStacking)cs;
            if (pieceIdx > 0) {
                final Component piece = context.components()[pieceIdx];
                final int owner = piece.owner();
                context.state().owned().remove(owner, pieceIdx, site, csStack.sizeStack(site, this.type));
            }
            if (csStack.sizeStack(site, this.type) == 0) {
                csStack.addToEmpty(site, this.type);
            }
        }
        else if (pieceIdx > 0) {
            final Component piece2 = context.components()[pieceIdx];
            final int owner2 = piece2.owner();
            context.state().owned().remove(owner2, pieceIdx, site);
        }
        if (pieceIdx > 0) {
            final OnTrackIndices onTrackIndices = context.state().onTrackIndices();
            if (onTrackIndices != null) {
                for (final Track track : context.board().tracks()) {
                    final int trackIdx = track.trackIdx();
                    final TIntArrayList indices = onTrackIndices.locToIndex(trackIdx, site);
                    for (int i = 0; i < indices.size(); ++i) {
                        onTrackIndices.remove(trackIdx, pieceIdx, 1, indices.getQuick(i));
                    }
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
        result = 31 * result + (this.applied ? 1231 : 1237);
        result = 31 * result + this.to;
        result = 31 * result + ((this.type == null) ? 0 : this.type.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ActionRemove)) {
            return false;
        }
        final ActionRemove other = (ActionRemove)obj;
        return this.decision == other.decision && this.applied == other.applied && this.to == other.to && this.type == other.type;
    }
    
    @Override
    public String toTrialFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        sb.append("[Remove:");
        if (this.type != null || (context != null && this.type != context.board().defaultSite())) {
            sb.append("type=" + this.type);
            sb.append(",to=" + this.to);
        }
        else {
            sb.append("to=" + this.to);
        }
        if (!this.applied) {
            sb.append(",applied=" + this.applied);
        }
        if (this.decision) {
            sb.append(",decision=" + this.decision);
        }
        sb.append(']');
        return sb.toString();
    }
    
    @Override
    public String getDescription() {
        return "Remove";
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
        sb.append("-");
        if (!this.applied) {
            sb.append("...");
        }
        return sb.toString();
    }
    
    @Override
    public String toMoveFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        sb.append("(Remove ");
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
        if (!this.applied) {
            sb.append(" applied = false");
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
    public ActionType actionType() {
        return ActionType.Remove;
    }
}
