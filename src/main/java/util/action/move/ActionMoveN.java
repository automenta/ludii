// 
// Decompiled by Procyon v0.5.36
// 

package util.action.move;

import game.equipment.container.board.Track;
import game.types.board.SiteType;
import gnu.trove.list.array.TIntArrayList;
import main.SettingsGeneral;
import util.Context;
import util.action.Action;
import util.action.BaseAction;
import util.state.containerState.ContainerState;
import util.state.onTrack.OnTrackIndices;

public final class ActionMoveN extends BaseAction
{
    private static final long serialVersionUID = 1L;
    private final SiteType typeFrom;
    private final int from;
    private final SiteType typeTo;
    private final int to;
    private final int count;
    
    public ActionMoveN(final SiteType typeFrom, final int from, final SiteType typeTo, final int to, final int count) {
        this.from = from;
        this.to = to;
        this.count = count;
        this.typeFrom = typeFrom;
        this.typeTo = typeTo;
    }
    
    public ActionMoveN(final String detailedString) {
        assert detailedString.startsWith("[Move:");
        final String strTypeFrom = Action.extractData(detailedString, "typeFrom");
        this.typeFrom = (strTypeFrom.isEmpty() ? null : SiteType.valueOf(strTypeFrom));
        final String strFrom = Action.extractData(detailedString, "from");
        this.from = Integer.parseInt(strFrom);
        final String strTypeTo = Action.extractData(detailedString, "typeTo");
        this.typeTo = (strTypeTo.isEmpty() ? null : SiteType.valueOf(strTypeTo));
        final String strTo = Action.extractData(detailedString, "to");
        this.to = Integer.parseInt(strTo);
        final String strCount = Action.extractData(detailedString, "count");
        this.count = Integer.parseInt(strCount);
        final String strDecision = Action.extractData(detailedString, "decision");
        this.decision = (!strDecision.isEmpty() && Boolean.parseBoolean(strDecision));
    }
    
    @Override
    public Action apply(final Context context, final boolean store) {
        final int contIdA = context.containerId()[this.from];
        final int contIdB = context.containerId()[this.to];
        final ContainerState csA = context.state().containerStates()[contIdA];
        final ContainerState csB = context.state().containerStates()[contIdB];
        final int what = csA.what(this.from, this.typeFrom);
        final int who = (what < 1) ? 0 : context.components()[what].owner();
        if (csA.count(this.from, this.typeFrom) - this.count <= 0) {
            csA.remove(context.state(), this.from, this.typeFrom);
        }
        else {
            csA.setSite(context.state(), this.from, -1, -1, csA.count(this.from, this.typeFrom) - this.count, -1, -1, -1, this.typeFrom);
        }
        if (csB.count(this.to, this.typeTo) == 0) {
            csB.setSite(context.state(), this.to, who, what, this.count, -1, -1, -1, this.typeTo);
        }
        else if (csB.what(this.to, this.typeTo) == what) {
            csB.setSite(context.state(), this.to, -1, -1, csB.count(this.to, this.typeTo) + this.count, -1, -1, -1, this.typeTo);
        }
        if (what != 0 && who != 0) {
            context.state().owned().remove(who, what, this.from);
            context.state().owned().add(who, what, this.to, this.typeTo);
        }
        final OnTrackIndices onTrackIndices = context.state().onTrackIndices();
        if (what != 0 && onTrackIndices != null) {
            for (final Track track : context.board().tracks()) {
                final int trackIdx = track.trackIdx();
                final TIntArrayList indicesLocA = onTrackIndices.locToIndex(trackIdx, this.from);
                int k = 0;
                while (k < indicesLocA.size()) {
                    final int indexA = indicesLocA.getQuick(k);
                    final int countAtIndex = onTrackIndices.whats(trackIdx, what, indicesLocA.getQuick(k));
                    if (countAtIndex > 0) {
                        onTrackIndices.remove(trackIdx, what, this.count, indexA);
                        final TIntArrayList newWhatIndice = onTrackIndices.locToIndexFrom(trackIdx, this.to, indexA);
                        if (!newWhatIndice.isEmpty()) {
                            onTrackIndices.add(trackIdx, what, this.count, newWhatIndice.getQuick(0));
                            break;
                        }
                        final TIntArrayList newWhatIndiceIfNotAfter = onTrackIndices.locToIndex(trackIdx, this.to);
                        if (!newWhatIndiceIfNotAfter.isEmpty()) {
                            onTrackIndices.add(trackIdx, what, this.count, newWhatIndiceIfNotAfter.getQuick(0));
                        }
                        break;
                    }
                    else {
                        ++k;
                    }
                }
                if (indicesLocA.isEmpty()) {
                    final TIntArrayList indicesLocB = onTrackIndices.locToIndex(trackIdx, this.to);
                    if (indicesLocB.isEmpty()) {
                        continue;
                    }
                    onTrackIndices.add(trackIdx, what, 1, indicesLocB.getQuick(0));
                }
            }
        }
        return this;
    }
    
    @Override
    public String toTrialFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        sb.append("[Move:");
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
        if (!(obj instanceof ActionMoveN)) {
            return false;
        }
        final ActionMoveN other = (ActionMoveN)obj;
        return this.count == other.count && this.decision == other.decision && this.from == other.from && this.to == other.to && this.typeFrom == other.typeFrom && this.typeTo == other.typeTo;
    }
    
    @Override
    public String getDescription() {
        return "Move";
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
        if (this.count > 1) {
            sb.append("x" + this.count);
        }
        return sb.toString();
    }
    
    @Override
    public String toMoveFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        sb.append("(Move ");
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
        if (this.count > 1) {
            sb.append("x" + this.count);
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
        return this.count;
    }
}
