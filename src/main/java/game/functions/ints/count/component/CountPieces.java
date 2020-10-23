// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.count.component;

import annotations.Hide;
import annotations.Name;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.functions.ints.BaseIntFunction;
import game.functions.ints.IntFunction;
import game.functions.ints.board.Id;
import game.functions.region.RegionFunction;
import game.types.board.SiteType;
import game.types.play.RoleType;
import gnu.trove.list.array.TIntArrayList;
import util.Context;
import util.locations.Location;
import util.state.containerState.ContainerState;

import java.util.ArrayList;
import java.util.List;

@Hide
public final class CountPieces extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    private SiteType type;
    private final IntFunction whoFn;
    private final String name;
    private final RoleType role;
    private final RegionFunction whereFn;
    
    public CountPieces(@Opt final SiteType type, @Opt @Or final RoleType role, @Opt @Or @Name final IntFunction of, @Opt final String name, @Opt @Name final RegionFunction in) {
        this.type = type;
        this.whoFn = ((of != null) ? of : ((role != null) ? new Id(null, role) : new Id(null, RoleType.Shared)));
        this.name = name;
        this.role = role;
        this.whereFn = in;
    }
    
    @Override
    public int eval(final Context context) {
        if (this.name != null && this.name.equals("Bag")) {
            return context.state().remainingDominoes().size();
        }
        int count = 0;
        if (this.role == RoleType.All) {
            final List<Location> locsOwned = new ArrayList<>();
            for (int pid = 1; pid <= context.game().players().count(); ++pid) {
                final List<? extends Location>[] owned = context.state().owned().positions(pid);
                for (final List<? extends Location> locations : owned) {
                    for (Location location : locations) {
                        locsOwned.add(location);
                    }
                }
            }
            for (Location location : locsOwned) {
                final int site = location.site();
                final SiteType typeSite = location.siteType();
                final ContainerState cs = context.containerState(context.containerId()[site]);
                count += cs.count(site, typeSite);
            }
            return count;
        }
        final TIntArrayList sitesOwned = new TIntArrayList();
        final int whoId = this.whoFn.eval(context);
        if (this.whereFn != null) {
            final int[] sites2;
            final int[] sites = sites2 = this.whereFn.eval(context).sites();
            for (final int site2 : sites2) {
                final ContainerState cs2 = context.containerState(context.containerId()[site2]);
                final int who = cs2.who(site2, this.type);
                if (who == whoId) {
                    ++count;
                }
            }
            return count;
        }
        if (this.name != null) {
            final ContainerState cs3 = context.containerState(0);
            final int sitesFrom = context.sitesFrom()[0];
            for (int sitesTo = sitesFrom + context.containers()[0].numSites(), site3 = sitesFrom; site3 < sitesTo; ++site3) {
                final int what = cs3.what(site3, this.type);
                if (what != 0 && context.components()[what].name().equals(this.name)) {
                    ++count;
                }
            }
            return count;
        }
        for (int pid2 = 1; pid2 <= context.players().size(); ++pid2) {
            if (whoId == context.players().size() || pid2 == whoId) {
                final TIntArrayList ownedSites = context.state().owned().sites(pid2);
                for (int l = 0; l < ownedSites.size(); ++l) {
                    sitesOwned.add(ownedSites.getQuick(l));
                }
            }
        }
        if (!context.game().isStacking()) {
            for (int m = 0; m < sitesOwned.size(); ++m) {
                final int site4 = sitesOwned.get(m);
                final ContainerState cs = context.containerState(context.containerId()[site4]);
                count += cs.count(site4, this.type);
            }
        }
        else {
            for (int m = 0; m < sitesOwned.size(); ++m) {
                final int site4 = sitesOwned.get(m);
                final ContainerState cs = context.containerState(context.containerId()[site4]);
                for (int sizeStack = cs.sizeStack(site4, this.type), lvl = 0; lvl < sizeStack; ++lvl) {
                    final int who2 = cs.who(site4, lvl, this.type);
                    if (whoId == who2) {
                        ++count;
                    }
                }
            }
        }
        return count;
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public String toString() {
        return "Pieces()";
    }
    
    @Override
    public long gameFlags(final Game game) {
        if (this.name != null && this.name.equals("Bag")) {
            return 134234112L;
        }
        long gameFlags = this.whoFn.gameFlags(game);
        if (this.whereFn != null) {
            gameFlags |= this.whereFn.gameFlags(game);
        }
        gameFlags |= SiteType.stateFlags(this.type);
        return gameFlags;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.type = SiteType.use(this.type, game);
        this.whoFn.preprocess(game);
        if (this.whereFn != null) {
            this.whereFn.preprocess(game);
        }
    }
}
