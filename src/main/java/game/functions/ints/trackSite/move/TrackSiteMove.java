// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.trackSite.move;

import annotations.Hide;
import annotations.Name;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.equipment.container.board.Track;
import game.functions.ints.BaseIntFunction;
import game.functions.ints.IntFunction;
import game.functions.ints.board.Id;
import game.functions.ints.context.From;
import game.functions.ints.state.Mover;
import game.types.play.RoleType;
import game.util.moves.Player;
import gnu.trove.list.array.TIntArrayList;
import util.Context;
import util.state.containerState.ContainerState;
import util.state.onTrack.OnTrackIndices;

@Hide
public final class TrackSiteMove extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction currentLocation;
    private final IntFunction steps;
    private final IntFunction player;
    private final String name;
    private final Track preComputedTrack;
    
    public TrackSiteMove(@Opt @Name final IntFunction from, @Opt @Or final RoleType role, @Opt @Or final Player player, @Opt @Or final String name, @Name final IntFunction steps) {
        this.preComputedTrack = null;
        int numNonNull = 0;
        if (player != null) {
            ++numNonNull;
        }
        if (name != null) {
            ++numNonNull;
        }
        if (role != null) {
            ++numNonNull;
        }
        if (numNonNull > 1) {
            throw new IllegalArgumentException("Zero or one Or parameter can be non-null.");
        }
        this.player = ((player == null && role == null) ? new Mover() : ((role != null) ? new Id(null, role) : player.index()));
        this.steps = steps;
        this.currentLocation = ((from == null) ? new From(null) : from);
        this.name = name;
    }
    
    @Override
    public int eval(final Context context) {
        final int playerId = this.player.eval(context);
        Track track = this.preComputedTrack;
        if (this.name != null) {
            for (final Track t : context.game().board().tracks()) {
                if (t.name().contains(this.name) && t.owner() == playerId) {
                    track = t;
                    break;
                }
            }
        }
        if (track == null && this.name != null) {
            for (final Track t : context.game().board().tracks()) {
                if (t.name().contains(this.name)) {
                    track = t;
                    break;
                }
            }
        }
        if (track == null) {
            final Track[] tracks = context.board().ownedTracks(playerId);
            if (tracks.length != 0) {
                track = tracks[0];
            }
            else {
                final Track[] tracksWithNoOwner = context.board().ownedTracks(0);
                if (tracksWithNoOwner.length != 0) {
                    track = tracksWithNoOwner[0];
                }
            }
        }
        if (track == null) {
            return -1;
        }
        int i = track.elems().length;
        if (this.currentLocation == null) {
            i = -1;
        }
        else {
            final int currentLoc = this.currentLocation.eval(context);
            if (!track.islooped() && context.game().hasInternalLoopInTrack()) {
                if (currentLoc < 0) {
                    return -1;
                }
                final ContainerState cs = context.containerState(context.containerId()[currentLoc]);
                int what = cs.what(currentLoc, context.board().defaultSite());
                for (int sizeStack = cs.sizeStack(currentLoc, context.board().defaultSite()), lvl = 0; lvl < sizeStack; ++lvl) {
                    final int who = cs.who(currentLoc, lvl, context.board().defaultSite());
                    if (who == playerId) {
                        what = cs.what(currentLoc, lvl, context.board().defaultSite());
                        break;
                    }
                }
                if (what != 0) {
                    final OnTrackIndices onTrackIndices = context.state().onTrackIndices();
                    final int trackIdx = track.trackIdx();
                    final TIntArrayList locsToIndex = onTrackIndices.locToIndex(trackIdx, currentLoc);
                    for (int j = 0; j < locsToIndex.size(); ++j) {
                        final int index = locsToIndex.getQuick(j);
                        final int count = onTrackIndices.whats(trackIdx, what, index);
                        if (count > 0) {
                            i = index;
                            break;
                        }
                    }
                }
                else {
                    for (i = 0; i < track.elems().length; ++i) {
                        if (track.elems()[i].site == currentLoc) {
                            break;
                        }
                    }
                }
            }
            else {
                for (i = 0; i < track.elems().length; ++i) {
                    if (track.elems()[i].site == currentLoc) {
                        break;
                    }
                }
            }
        }
        i += this.steps.eval(context);
        if (i < track.elems().length) {
            return track.elems()[i].site;
        }
        if (track.elems()[track.elems().length - 1].next == -1) {
            return -1;
        }
        i -= track.elems().length;
        if (i == 0) {
            return track.elems()[track.elems().length - 1].next;
        }
        return track.elems()[i - 1].next;
    }
    
    @Override
    public boolean isStatic() {
        boolean isStatic = this.player.isStatic();
        if (this.steps != null) {
            isStatic = (isStatic && this.steps.isStatic());
        }
        if (this.currentLocation != null) {
            isStatic = (isStatic && this.currentLocation.isStatic());
        }
        return isStatic;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = this.player.gameFlags(game);
        if (this.steps != null) {
            gameFlags |= this.steps.gameFlags(game);
        }
        if (this.currentLocation != null) {
            gameFlags |= this.currentLocation.gameFlags(game);
        }
        return gameFlags;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.player.preprocess(game);
        if (this.steps != null) {
            this.steps.preprocess(game);
        }
        if (this.currentLocation != null) {
            this.currentLocation.preprocess(game);
        }
    }
}
