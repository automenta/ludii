// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.count.stepsOnTrack;

import annotations.Hide;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.equipment.container.board.Track;
import game.functions.ints.BaseIntFunction;
import game.functions.ints.IntFunction;
import game.functions.ints.board.Id;
import game.functions.ints.state.Mover;
import game.types.play.RoleType;
import game.util.moves.Player;
import gnu.trove.list.array.TIntArrayList;
import util.Context;
import util.state.containerState.ContainerState;
import util.state.onTrack.OnTrackIndices;

@Hide
public final class CountStepsOnTrack extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction site1Fn;
    private final IntFunction site2Fn;
    private final IntFunction player;
    private final String name;
    private final Track preComputedTrack;
    
    public CountStepsOnTrack(@Opt @Or final RoleType role, @Opt @Or final Player player, @Opt @Or final String name, final IntFunction site1, final IntFunction site2) {
        this.preComputedTrack = null;
        this.player = ((player == null && role == null) ? new Mover() : ((role != null) ? new Id(null, role) : player.index()));
        this.site1Fn = site1;
        this.site2Fn = site2;
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
        final int site1 = this.site1Fn.eval(context);
        final int site2 = this.site2Fn.eval(context);
        final int currentLoc = site1;
        int i = track.elems().length;
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
        int count2 = 0;
        while (i < track.elems().length) {
            if (track.elems()[i].site == site2) {
                return count2;
            }
            ++count2;
            ++i;
        }
        return -1;
    }
    
    @Override
    public boolean isStatic() {
        return this.player.isStatic() && this.site1Fn.isStatic() && this.site2Fn.isStatic();
    }
    
    @Override
    public String toString() {
        return "CountStepsOnTrack()";
    }
    
    @Override
    public long gameFlags(final Game game) {
        final long gameFlags = this.site1Fn.gameFlags(game) | this.site2Fn.gameFlags(game) | this.player.gameFlags(game);
        return gameFlags;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.player.preprocess(game);
        this.site1Fn.preprocess(game);
        this.site2Fn.preprocess(game);
    }
}
