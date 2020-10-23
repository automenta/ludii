// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.region.sites.player;

import annotations.Hide;
import annotations.Opt;
import annotations.Or;
import annotations.Or2;
import game.Game;
import game.equipment.container.board.Track;
import game.functions.ints.IntFunction;
import game.functions.ints.board.Id;
import game.functions.region.BaseRegionFunction;
import game.types.play.RoleType;
import game.util.equipment.Region;
import game.util.moves.Player;
import gnu.trove.list.array.TIntArrayList;
import util.Context;

@Hide
public final class SitesTrack extends BaseRegionFunction
{
    private static final long serialVersionUID = 1L;
    private Region precomputedRegion;
    private final IntFunction pid;
    private final String name;
    
    public SitesTrack(@Or @Opt final Player pid, @Or @Opt final RoleType role, @Or2 @Opt final String name) {
        this.precomputedRegion = null;
        this.pid = ((role != null) ? new Id(null, role) : ((pid != null) ? pid.index() : null));
        this.name = ((name == null) ? "" : name);
    }
    
    @Override
    public Region eval(final Context context) {
        if (this.precomputedRegion != null) {
            return this.precomputedRegion;
        }
        Track track = null;
        if (context.track() != -1) {
            final int index = context.track();
            if (index >= 0 && index < context.tracks().size()) {
                track = context.tracks().get(index);
            }
        }
        else {
            final int playerId = (this.pid != null) ? this.pid.eval(context) : 0;
            for (final Track t : context.tracks()) {
                if (this.name != null) {
                    if (t.name().contains(this.name)) {
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
        }
        if (track == null) {
            return new Region();
        }
        final TIntArrayList sites = new TIntArrayList();
        for (int i = 0; i < track.elems().length; ++i) {
            sites.add(track.elems()[i].site);
        }
        return new Region(sites.toArray());
    }
    
    @Override
    public boolean isStatic() {
        return this.pid != null && this.pid.isStatic();
    }
    
    @Override
    public String toString() {
        return "Track()";
    }
    
    @Override
    public long gameFlags(final Game game) {
        long flags = 0L;
        if (this.pid != null) {
            flags = this.pid.gameFlags(game);
        }
        return flags;
    }
    
    @Override
    public void preprocess(final Game game) {
        if (this.pid != null) {
            this.pid.preprocess(game);
        }
        if (this.isStatic()) {
            this.precomputedRegion = this.eval(new Context(game, null));
        }
    }
}
