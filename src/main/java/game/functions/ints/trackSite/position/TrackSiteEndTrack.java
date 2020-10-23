// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.trackSite.position;

import annotations.Hide;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.equipment.container.board.Track;
import game.functions.ints.BaseIntFunction;
import game.functions.ints.IntFunction;
import game.functions.ints.board.Id;
import game.types.play.RoleType;
import game.util.moves.Player;
import util.Context;

@Hide
public final class TrackSiteEndTrack extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    private final String name;
    private final IntFunction pidFn;
    private int precomputedValue;
    
    public TrackSiteEndTrack(@Or @Opt final Player player, @Or @Opt final RoleType role, @Opt final String name) {
        this.precomputedValue = -1;
        this.name = name;
        this.pidFn = ((player != null) ? player.index() : ((role != null) ? new Id(null, role) : null));
    }
    
    @Override
    public int eval(final Context context) {
        if (this.precomputedValue != -1) {
            return this.precomputedValue;
        }
        final int playerId = (this.pidFn != null) ? this.pidFn.eval(context) : 0;
        Track track = null;
        for (final Track t : context.tracks()) {
            if (this.name != null && playerId == 0) {
                if (t.name().contains(this.name)) {
                    track = t;
                    break;
                }
                continue;
            }
            else if (this.name != null) {
                if (this.name != null && t.name().contains(this.name) && t.owner() == playerId) {
                    track = t;
                    break;
                }
                continue;
            }
            else {
                if (t.owner() == playerId || t.owner() == 0) {
                    track = t;
                    break;
                }
                continue;
            }
        }
        if (track == null) {
            if (context.game().board().tracks().size() == 0) {
                return -1;
            }
            track = context.game().board().tracks().get(0);
        }
        if (track.elems().length == 0) {
            return -1;
        }
        return track.elems()[track.elems().length - 1].site;
    }
    
    @Override
    public boolean isStatic() {
        return this.pidFn == null || this.pidFn.isStatic();
    }
    
    @Override
    public long gameFlags(final Game game) {
        long flags = 0L;
        if (this.pidFn != null) {
            flags |= this.pidFn.gameFlags(game);
        }
        return flags;
    }
    
    @Override
    public void preprocess(final Game game) {
        if (this.pidFn != null) {
            this.pidFn.preprocess(game);
        }
        if (this.isStatic()) {
            this.precomputedValue = this.eval(new Context(game, null));
        }
    }
    
    @Override
    public String toString() {
        return "";
    }
}
