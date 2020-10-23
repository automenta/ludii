// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.region.sites.player;

import annotations.Hide;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.equipment.other.Regions;
import game.functions.ints.IntFunction;
import game.functions.ints.board.Id;
import game.functions.region.BaseRegionFunction;
import game.functions.region.RegionFunction;
import game.types.play.RoleType;
import game.util.equipment.Region;
import game.util.moves.Player;
import gnu.trove.list.array.TIntArrayList;
import util.Context;

import java.util.ArrayList;
import java.util.List;

@Hide
public final class SitesEquipmentRegion extends BaseRegionFunction
{
    private static final long serialVersionUID = 1L;
    private Region precomputedRegion;
    private Region[] precomputedPerPlayer;
    private List<Regions>[] regionsPerPlayer;
    private final IntFunction index;
    private final String name;
    
    public SitesEquipmentRegion(@Or @Opt final Player player, @Or @Opt final RoleType role, @Opt final String name) {
        this.precomputedRegion = null;
        this.precomputedPerPlayer = null;
        this.regionsPerPlayer = null;
        this.index = ((role != null) ? new Id(null, role) : ((player != null) ? player.index() : null));
        this.name = ((name == null) ? "" : name);
    }
    
    @Override
    public Region eval(final Context context) {
        if (this.precomputedRegion != null) {
            return this.precomputedRegion;
        }
        final int who = (this.index != null) ? this.index.eval(context) : 0;
        if (this.precomputedPerPlayer != null) {
            return this.precomputedPerPlayer[who];
        }
        return this.computeForWho(context, who);
    }
    
    @Override
    public boolean contains(final Context context, final int location) {
        if (this.precomputedRegion != null || this.precomputedPerPlayer != null) {
            return super.contains(context, location);
        }
        final int who = (this.index != null) ? this.index.eval(context) : 0;
        for (final Regions region : this.regionsPerPlayer[who]) {
            if (region.contains(context, location)) {
                return true;
            }
        }
        return false;
    }
    
    private Region computeForWho(final Context context, final int player) {
        final List<TIntArrayList> siteLists = new ArrayList<>();
        int totalNumSites = 0;
        for (final Regions region : this.regionsPerPlayer[player]) {
            final TIntArrayList wrapped = TIntArrayList.wrap(region.eval(context));
            siteLists.add(wrapped);
            totalNumSites += wrapped.size();
        }
        final int[] sites = new int[totalNumSites];
        int startIdx = 0;
        for (final TIntArrayList wrapped2 : siteLists) {
            wrapped2.toArray(sites, 0, startIdx, wrapped2.size());
            startIdx += wrapped2.size();
        }
        return new Region(sites);
    }
    
    @Override
    public boolean isStatic() {
        return this.index == null || this.index.isStatic();
    }
    
    @Override
    public String toString() {
        return "EquipmentRegion()";
    }
    
    @Override
    public long gameFlags(final Game game) {
        long flags = 0L;
        if (this.index != null) {
            flags = this.index.gameFlags(game);
        }
        return flags;
    }
    
    @Override
    public void preprocess(final Game game) {
        if (this.regionsPerPlayer != null) {
            return;
        }
        if (this.index != null) {
            this.index.preprocess(game);
            final boolean whoStatic = this.index.isStatic();
            boolean regionsStatic = true;
            final Regions[] regions = game.equipment().regions();
            this.regionsPerPlayer = (List<Regions>[])new List[game.players().count() + 2];
            for (int p = 0; p < this.regionsPerPlayer.length; ++p) {
                this.regionsPerPlayer[p] = new ArrayList<>();
            }
            for (final Regions region : regions) {
                if (region.name().contains(this.name)) {
                    this.regionsPerPlayer[region.owner()].add(region);
                    region.preprocess(game);
                }
            }
            for (final Regions region : regions) {
                if (region.region() != null) {
                    for (final RegionFunction regionFunction : region.region()) {
                        regionsStatic &= regionFunction.isStatic();
                    }
                }
            }
            if (whoStatic && regionsStatic) {
                this.precomputedRegion = this.eval(new Context(game, null));
            }
            else if (regionsStatic) {
                this.precomputedPerPlayer = new Region[game.players().count() + 2];
                for (int p = 0; p < this.precomputedPerPlayer.length; ++p) {
                    this.precomputedPerPlayer[p] = this.computeForWho(new Context(game, null), p);
                }
            }
        }
        else {
            final Regions[] regions2 = game.equipment().regions();
            (this.regionsPerPlayer = (List<Regions>[])new List[1])[0] = new ArrayList<>();
            for (final Regions region2 : regions2) {
                if (region2.name().equals(this.name)) {
                    this.regionsPerPlayer[0].add(region2);
                    region2.preprocess(game);
                }
            }
        }
        if (this.index == null && this.isStatic()) {
            this.precomputedRegion = this.eval(new Context(game, null));
        }
    }
}
