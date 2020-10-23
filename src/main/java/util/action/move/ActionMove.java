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

import java.util.List;

public final class ActionMove extends BaseAction
{
    private static final long serialVersionUID = 1L;
    private final SiteType typeFrom;
    private final int from;
    private int levelFrom;
    private final SiteType typeTo;
    private final int to;
    private final int levelTo;
    private final int state;
    private final int rotation;
    private final boolean onStacking;
    
    public ActionMove(final SiteType typeFrom, final int from, final int levelFrom, final SiteType typeTo, final int to, final int levelTo, final int state, final int rotation, final boolean onStacking) {
        this.typeFrom = typeFrom;
        this.from = from;
        this.levelFrom = levelFrom;
        this.typeTo = typeTo;
        this.to = to;
        this.levelTo = levelTo;
        this.state = state;
        this.rotation = rotation;
        this.onStacking = onStacking;
    }
    
    public ActionMove(final String detailedString) {
        assert detailedString.startsWith("[Move:");
        final String strTypeFrom = Action.extractData(detailedString, "typeFrom");
        this.typeFrom = (strTypeFrom.isEmpty() ? null : SiteType.valueOf(strTypeFrom));
        final String strFrom = Action.extractData(detailedString, "from");
        this.from = Integer.parseInt(strFrom);
        final String strLevelFrom = Action.extractData(detailedString, "levelFrom");
        this.levelFrom = (strLevelFrom.isEmpty() ? -1 : Integer.parseInt(strLevelFrom));
        final String strTypeTo = Action.extractData(detailedString, "typeTo");
        this.typeTo = (strTypeTo.isEmpty() ? null : SiteType.valueOf(strTypeTo));
        final String strTo = Action.extractData(detailedString, "to");
        this.to = Integer.parseInt(strTo);
        final String strLevelTo = Action.extractData(detailedString, "levelTo");
        this.levelTo = (strLevelTo.isEmpty() ? -1 : Integer.parseInt(strLevelTo));
        final String strState = Action.extractData(detailedString, "state");
        this.state = (strState.isEmpty() ? -1 : Integer.parseInt(strState));
        final String strRotation = Action.extractData(detailedString, "rotation");
        this.rotation = (strRotation.isEmpty() ? -1 : Integer.parseInt(strRotation));
        final String strStack = Action.extractData(detailedString, "stack");
        this.onStacking = (!strStack.isEmpty() && Boolean.parseBoolean(strStack));
        final String strDecision = Action.extractData(detailedString, "decision");
        this.decision = (!strDecision.isEmpty() && Boolean.parseBoolean(strDecision));
    }
    
    @Override
    public Action apply(final Context context, final boolean store) {
        final OnTrackIndices onTrackIndices = context.state().onTrackIndices();
        final int contIdA = context.containerId()[this.from];
        final int contIdB = context.containerId()[this.to];
        final boolean requiresStack = context.game().isStacking();
        if (!requiresStack) {
            final ContainerState csA = context.state().containerStates()[contIdA];
            final ContainerState csB = context.state().containerStates()[contIdB];
            if (csA.what(this.from, this.typeFrom) == 0 && csA.count(this.from, this.typeFrom) == 0) {
                return this;
            }
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
            final int count = csA.count(this.from, this.typeFrom);
            int currentStateA = -1;
            int currentRotationA = -1;
            Component piece = null;
            currentStateA = ((csA.what(this.from, this.typeFrom) == 0) ? -1 : csA.state(this.from, this.typeFrom));
            currentRotationA = csA.rotation(this.from, this.typeFrom);
            if (count == 1) {
                csA.remove(context.state(), this.from, this.typeFrom);
                if (what != 0) {
                    piece = context.components()[what];
                    final int owner = piece.owner();
                    context.state().owned().remove(owner, what, this.from);
                }
                if (piece != null && piece.isLargePiece()) {
                    final Component largePiece = piece;
                    final TIntArrayList locs = largePiece.locs(context, this.from, currentStateA, context.topology());
                    for (int i = 0; i < locs.size(); ++i) {
                        csA.addToEmpty(locs.getQuick(i), SiteType.Cell);
                        csA.setCount(context.state(), locs.getQuick(i), 0);
                    }
                    if (largePiece.isDomino() && context.containerId()[this.from] == 0) {
                        for (int i = 0; i < 4; ++i) {
                            csA.setValue(context.state(), locs.getQuick(i), largePiece.getValue());
                        }
                        for (int i = 4; i < 8; ++i) {
                            csA.setValue(context.state(), locs.getQuick(i), largePiece.getValue2());
                        }
                    }
                }
            }
            else {
                csA.setSite(context.state(), this.from, -1, -1, count - 1, -1, -1, context.game().usesLineOfPlay() ? 1 : -1, this.typeFrom);
            }
            if (currentStateA != -1 && this.state == -1) {
                csB.setSite(context.state(), this.to, -1, -1, -1, currentStateA, -1, context.game().usesLineOfPlay() ? 1 : -1, this.typeTo);
            }
            else if (this.state != -1) {
                csB.setSite(context.state(), this.to, -1, -1, -1, this.state, -1, context.game().usesLineOfPlay() ? 1 : -1, this.typeTo);
            }
            if (currentRotationA != -1 && this.rotation == -1) {
                csB.setSite(context.state(), this.to, -1, -1, -1, -1, currentRotationA, context.game().usesLineOfPlay() ? 1 : -1, this.typeTo);
            }
            else if (this.rotation != -1) {
                csB.setSite(context.state(), this.to, -1, -1, -1, -1, this.state, context.game().usesLineOfPlay() ? 1 : -1, this.typeTo);
            }
            final int who = (what < 1) ? 0 : context.components()[what].owner();
            if (csB.what(this.to, this.typeTo) != 0 && csB.what(this.to, this.typeTo) != what) {
                final Component pieceToRemove = context.components()[csB.what(this.to, this.typeTo)];
                final int owner2 = pieceToRemove.owner();
                context.state().owned().remove(owner2, csB.what(this.to, this.typeTo), this.to);
            }
            if (csB.what(this.to, this.typeTo) == what && csB.count(this.to, this.typeTo) > 0) {
                csB.setSite(context.state(), this.to, -1, -1, context.game().requiresCount() ? (csB.count(this.to, this.typeTo) + 1) : 1, -1, -1, context.game().usesLineOfPlay() ? 1 : -1, this.typeTo);
            }
            else {
                csB.setSite(context.state(), this.to, who, what, 1, -1, -1, context.game().usesLineOfPlay() ? 1 : -1, this.typeTo);
            }
            if (what != 0 && csB.count(this.to, this.typeTo) == 1) {
                piece = context.components()[what];
                final int owner3 = piece.owner();
                context.state().owned().add(owner3, what, this.to, this.typeTo);
            }
            if (piece != null && piece.isLargePiece()) {
                final Component largePiece2 = piece;
                final TIntArrayList locs2 = largePiece2.locs(context, this.to, this.state, context.topology());
                for (int j = 0; j < locs2.size(); ++j) {
                    csB.removeFromEmpty(locs2.getQuick(j), SiteType.Cell);
                    csB.setCount(context.state(), locs2.getQuick(j), context.game().usesLineOfPlay() ? piece.index() : 1);
                }
                if (context.game().usesLineOfPlay() && context.containerId()[this.to] == 0) {
                    for (int j = 0; j < 4; ++j) {
                        csB.setValue(context.state(), locs2.getQuick(j), largePiece2.getValue());
                    }
                    for (int j = 4; j < 8; ++j) {
                        csB.setValue(context.state(), locs2.getQuick(j), largePiece2.getValue2());
                    }
                    for (int j = 0; j < context.containers()[0].numSites(); ++j) {
                        csB.setPlayable(context.state(), j, false);
                    }
                    for (int j = 0; j < context.containers()[0].numSites(); ++j) {
                        if (csB.what(j, this.typeTo) != 0) {
                            final Component currentComponent = context.components()[csB.what(j, this.typeTo)];
                            final int currentState = csB.state(j, this.typeTo);
                            final TIntArrayList locsToUpdate = largePiece2.locs(context, j, currentState, context.topology());
                            BaseAction.lineOfPlayDominoes(context, locsToUpdate.getQuick(0), locsToUpdate.getQuick(1), this.getDirnDomino(0, currentState), false, true);
                            BaseAction.lineOfPlayDominoes(context, locsToUpdate.getQuick(7), locsToUpdate.getQuick(6), this.getDirnDomino(2, currentState), false, false);
                            if (currentComponent.isDoubleDomino()) {
                                BaseAction.lineOfPlayDominoes(context, locsToUpdate.getQuick(2), locsToUpdate.getQuick(5), this.getDirnDomino(1, currentState), true, true);
                                BaseAction.lineOfPlayDominoes(context, locsToUpdate.getQuick(3), locsToUpdate.getQuick(4), this.getDirnDomino(3, currentState), true, true);
                            }
                        }
                    }
                }
            }
            this.updateOnTrackIndices(what, onTrackIndices, context.board().tracks());
            if (csB.isEmpty(this.to, this.typeTo)) {
                throw new RuntimeException("Did not expect locationB to be empty at site locnB=" + this.to + "(who, what,count,state)=(" + csB.who(this.to, this.typeTo) + "," + csB.what(this.to, this.typeTo) + "," + csB.count(this.to, this.typeTo) + "," + csB.state(this.to, this.typeTo) + "," + csB.state(this.to, this.typeTo) + ")");
            }
        }
        else {
            if (this.from == this.to) {
                return this;
            }
            final ContainerState containerA = context.state().containerStates()[contIdA];
            final ContainerState containerB = context.state().containerStates()[contIdB];
            if (this.onStacking) {
                final int sizeStackA = containerA.sizeStack(this.from, this.typeFrom);
                for (int slevel = 0; slevel < containerA.sizeStack(this.from, this.typeFrom); ++slevel) {
                    if (this.levelTo == -1) {
                        containerB.addItemGeneric(context.state(), this.to, containerA.what(this.from, slevel, this.typeFrom), containerA.who(this.from, slevel, this.typeFrom), containerA.state(this.from, slevel, this.typeFrom), containerA.rotation(this.from, slevel, this.typeFrom), context.game(), this.typeTo);
                    }
                    else {
                        containerB.insert(context.state(), this.to, this.levelTo, containerA.what(this.from, slevel, this.typeFrom), containerA.who(this.from, slevel, this.typeFrom), context.game());
                    }
                }
                for (int level = 0; level < containerA.sizeStack(this.from, this.typeFrom); ++level) {
                    final int whatA = containerA.what(this.from, level, this.typeFrom);
                    if (whatA != 0) {
                        final Component pieceA = context.components()[whatA];
                        final int ownerA = pieceA.owner();
                        if (ownerA != 0) {
                            context.state().owned().remove(ownerA, whatA, this.from);
                        }
                    }
                }
                containerA.removeStackGeneric(context.state(), this.from, this.typeFrom);
                containerA.addToEmpty(this.from, this.typeFrom);
                containerB.removeFromEmpty(this.to, this.typeTo);
                for (int level = containerB.sizeStack(this.to, this.typeTo) - sizeStackA; level < containerB.sizeStack(this.to, this.typeTo); ++level) {
                    if (level >= 0) {
                        final int whatB = containerB.what(this.to, level, this.typeTo);
                        if (whatB != 0) {
                            final Component pieceB = context.components()[whatB];
                            final int ownerB = pieceB.owner();
                            if (ownerB != 0) {
                                context.state().owned().add(ownerB, whatB, this.to, level, this.typeTo);
                            }
                        }
                    }
                }
            }
            else if (this.levelFrom == -1) {
                final int what = containerA.what(this.from, this.typeFrom);
                containerA.remove(context.state(), this.from, this.typeFrom);
                if (containerA.sizeStack(this.from, this.typeFrom) == 0) {
                    containerA.addToEmpty(this.from, this.typeFrom);
                }
                final int who2 = (what < 1) ? 0 : context.components()[what].owner();
                if (context.game().hasCard()) {
                    final boolean[] masked = new boolean[context.players().size() - 1];
                    for (int pid = 1; pid < context.players().size(); ++pid) {
                        masked[pid - 1] = containerA.isMasked(this.from, pid, this.typeFrom);
                    }
                    if (this.levelTo == -1) {
                        containerB.addItemGeneric(context.state(), this.to, what, who2, context.game(), masked, true, this.typeTo);
                    }
                    else {
                        containerB.insert(context.state(), this.to, this.levelTo, what, who2, context.game());
                    }
                }
                else if (this.levelTo == -1) {
                    containerB.addItemGeneric(context.state(), this.to, what, who2, context.game(), this.typeTo);
                }
                else {
                    containerB.insert(context.state(), this.to, this.levelTo, what, who2, context.game());
                }
                if (containerB.sizeStack(this.to, this.typeTo) != 0) {
                    containerB.removeFromEmpty(this.to, this.typeTo);
                }
                Component pieceA2 = null;
                int ownerA2 = 0;
                if (what != 0) {
                    pieceA2 = context.components()[what];
                    ownerA2 = pieceA2.owner();
                    if (ownerA2 != 0) {
                        context.state().owned().add(ownerA2, what, this.to, containerB.sizeStack(this.to, this.typeTo) - 1, this.typeTo);
                        context.state().owned().remove(ownerA2, what, this.from, containerA.sizeStack(this.from, this.typeFrom));
                    }
                }
                this.updateOnTrackIndices(what, onTrackIndices, context.board().tracks());
            }
            else {
                final int what = containerA.what(this.from, this.levelFrom, this.typeFrom);
                containerA.remove(context.state(), this.from, this.levelFrom, this.typeFrom);
                if (containerA.sizeStack(this.from, this.typeFrom) == 0) {
                    containerA.addToEmpty(this.from, this.typeFrom);
                }
                final int who2 = (what < 1) ? 0 : context.components()[what].owner();
                if (context.game().hasCard()) {
                    final boolean[] masked = new boolean[context.players().size() - 1];
                    for (int pid = 1; pid < context.players().size(); ++pid) {
                        masked[pid - 1] = containerA.isMasked(this.from, this.levelFrom, pid, this.typeFrom);
                    }
                    containerB.addItemGeneric(context.state(), this.to, what, who2, context.game(), masked, true, this.typeTo);
                    if (containerB.sizeStack(this.to, this.typeTo) != 0) {
                        containerB.removeFromEmpty(this.to, this.typeTo);
                    }
                    Component pieceA = null;
                    int ownerA = 0;
                    if (what != 0) {
                        pieceA = context.components()[what];
                        ownerA = pieceA.owner();
                        if (ownerA != 0) {
                            context.state().owned().add(ownerA, what, this.to, containerB.sizeStack(this.to, this.typeTo) - 1, this.typeTo);
                            context.state().owned().remove(ownerA, what, this.from, this.levelFrom);
                        }
                    }
                }
                else if (this.levelTo == -1) {
                    containerB.addItemGeneric(context.state(), this.to, what, who2, context.game(), this.typeTo);
                    if (containerB.sizeStack(this.to, this.typeTo) != 0) {
                        containerB.removeFromEmpty(this.to, this.typeTo);
                    }
                    Component pieceA2 = null;
                    int ownerA2 = 0;
                    if (what != 0) {
                        pieceA2 = context.components()[what];
                        ownerA2 = pieceA2.owner();
                        if (ownerA2 != 0) {
                            context.state().owned().add(ownerA2, what, this.to, containerB.sizeStack(this.to, this.typeTo) - 1, this.typeTo);
                            context.state().owned().remove(ownerA2, what, this.from, this.levelFrom);
                        }
                    }
                }
                else {
                    final int sizeStack = containerB.sizeStack(this.to, this.typeTo);
                    for (int k = sizeStack - 1; k >= this.levelTo; --k) {
                        final int owner4 = containerB.who(this.to, k, this.typeTo);
                        final int piece2 = containerB.what(this.to, k, this.typeTo);
                        context.state().owned().remove(owner4, piece2, this.to, k);
                        context.state().owned().add(owner4, piece2, this.to, k + 1, this.typeTo);
                    }
                    containerB.insert(context.state(), this.to, this.levelTo, what, who2, context.game());
                    final Component piece3 = context.components()[what];
                    final int owner4 = piece3.owner();
                    context.state().owned().add(owner4, what, this.to, this.levelTo, this.typeTo);
                    if (containerB.sizeStack(this.to, this.typeTo) != 0) {
                        containerB.removeFromEmpty(this.to, this.typeTo);
                    }
                    Component pieceA3 = null;
                    int ownerA3 = 0;
                    if (what != 0) {
                        pieceA3 = context.components()[what];
                        ownerA3 = pieceA3.owner();
                        if (ownerA3 != 0) {
                            context.state().owned().remove(ownerA3, what, this.from, this.levelFrom);
                        }
                    }
                }
                this.updateOnTrackIndices(what, onTrackIndices, context.board().tracks());
            }
        }
        return this;
    }
    
    public void updateOnTrackIndices(final int what, final OnTrackIndices onTrackIndices, final List<Track> tracks) {
        if (what != 0 && onTrackIndices != null) {
            for (final Track track : tracks) {
                final int trackIdx = track.trackIdx();
                final TIntArrayList indicesLocA = onTrackIndices.locToIndex(trackIdx, this.from);
                int k = 0;
                while (k < indicesLocA.size()) {
                    final int indexA = indicesLocA.getQuick(k);
                    final int countAtIndex = onTrackIndices.whats(trackIdx, what, indicesLocA.getQuick(k));
                    if (countAtIndex > 0) {
                        onTrackIndices.remove(trackIdx, what, 1, indexA);
                        final TIntArrayList newWhatIndice = onTrackIndices.locToIndexFrom(trackIdx, this.to, indexA);
                        if (!newWhatIndice.isEmpty()) {
                            onTrackIndices.add(trackIdx, what, 1, newWhatIndice.getQuick(0));
                            break;
                        }
                        final TIntArrayList newWhatIndiceIfNotAfter = onTrackIndices.locToIndex(trackIdx, this.to);
                        if (!newWhatIndiceIfNotAfter.isEmpty()) {
                            onTrackIndices.add(trackIdx, what, 1, newWhatIndiceIfNotAfter.getQuick(0));
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
                    onTrackIndices.add(trackIdx, what, 1, indicesLocB.get(0));
                }
            }
        }
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
        if (this.levelFrom != -1) {
            sb.append(",levelFrom=" + this.levelFrom);
        }
        if (this.typeTo != null || (context != null && this.typeTo != context.board().defaultSite())) {
            sb.append(",typeTo=" + this.typeTo);
        }
        sb.append(",to=" + this.to);
        if (this.levelTo != -1) {
            sb.append(",levelTo=" + this.levelTo);
        }
        if (this.state != -1) {
            sb.append(",state=" + this.state);
        }
        if (this.rotation != -1) {
            sb.append(",rotation=" + this.rotation);
        }
        if (this.onStacking) {
            sb.append(",stack=" + this.onStacking);
        }
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
        result = 31 * result + this.levelFrom;
        result = 31 * result + this.to;
        result = 31 * result + this.levelTo;
        result = 31 * result + this.state;
        result = 31 * result + this.rotation;
        result = 31 * result + (this.onStacking ? 1231 : 1237);
        result = 31 * result + ((this.typeFrom == null) ? 0 : this.typeFrom.hashCode());
        result = 31 * result + ((this.typeTo == null) ? 0 : this.typeTo.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ActionMove)) {
            return false;
        }
        final ActionMove other = (ActionMove)obj;
        return this.decision == other.decision && this.from == other.from && this.levelFrom == other.levelFrom && this.to == other.to && this.levelTo == other.levelTo && this.state == other.state && this.rotation == other.rotation && this.onStacking == other.onStacking && this.typeFrom == other.typeFrom && this.typeTo == other.typeTo;
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
        if (this.levelFrom != -1 && context.game().isStacking()) {
            sb.append("/" + this.levelFrom);
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
        if (this.levelTo != -1) {
            sb.append("/" + this.levelTo);
        }
        if (this.state != -1) {
            sb.append("=" + this.state);
        }
        if (this.rotation != -1) {
            sb.append(" r" + this.rotation);
        }
        if (this.onStacking) {
            sb.append(" ^");
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
        if (this.levelFrom != -1) {
            sb.append("/" + this.levelFrom);
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
        if (this.levelTo != -1) {
            sb.append("/" + this.levelTo);
        }
        if (this.state != -1) {
            sb.append(" state=" + this.state);
        }
        if (this.rotation != -1) {
            sb.append(" rotation=" + this.rotation);
        }
        if (this.onStacking) {
            sb.append(" stack=" + this.onStacking);
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
    public int levelFrom() {
        return (this.levelFrom == -1) ? 0 : this.levelFrom;
    }
    
    @Override
    public int levelTo() {
        return (this.levelTo == -1) ? 0 : this.levelTo;
    }
    
    @Override
    public int state() {
        return this.state;
    }
    
    @Override
    public int rotation() {
        return this.rotation;
    }
    
    @Override
    public int count() {
        return 1;
    }
    
    @Override
    public boolean isStacking() {
        return this.onStacking;
    }
    
    @Override
    public void setLevelFrom(final int levelA) {
        this.levelFrom = levelA;
    }
}
