// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.region.sites.distance;

import annotations.Hide;
import annotations.Name;
import annotations.Opt;
import game.Game;
import game.functions.ints.IntFunction;
import game.functions.region.BaseRegionFunction;
import game.types.board.RelationType;
import game.types.board.SiteType;
import game.util.equipment.Region;
import gnu.trove.list.array.TIntArrayList;
import topology.TopologyElement;
import util.Context;

import java.util.List;

@Hide
public final class SitesDistance extends BaseRegionFunction
{
    private static final long serialVersionUID = 1L;
    private Region precomputedRegion;
    private final RelationType relation;
    private final IntFunction fromFn;
    private final IntFunction distanceFn;
    
    public SitesDistance(@Opt final SiteType type, @Opt final RelationType relation, @Name final IntFunction from, final IntFunction distance) {
        this.precomputedRegion = null;
        this.fromFn = from;
        this.distanceFn = distance;
        this.type = type;
        this.relation = ((relation == null) ? RelationType.Adjacent : relation);
    }
    
    @Override
    public Region eval(final Context context) {
        if (this.precomputedRegion != null) {
            return this.precomputedRegion;
        }
        final int from = this.fromFn.eval(context);
        final int distance = this.distanceFn.eval(context);
        final SiteType realType = (this.type != null) ? this.type : context.game().board().defaultSite();
        final TIntArrayList sites = new TIntArrayList();
        final List<? extends TopologyElement> elements = context.topology().getGraphElements(realType);
        if (from < 0 || from >= elements.size()) {
            return new Region(sites.toArray());
        }
        final TopologyElement element = elements.get(from);
        if (distance < 0 || distance >= element.sitesAtDistance().size()) {
            return new Region(sites.toArray());
        }
        final List<TopologyElement> elementsAtDistance = element.sitesAtDistance().get(distance);
        for (final TopologyElement elementAtDistance : elementsAtDistance) {
            sites.add(elementAtDistance.index());
        }
        return new Region(sites.toArray());
    }
    
    @Override
    public boolean isStatic() {
        return this.fromFn.isStatic() && this.distanceFn.isStatic();
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = this.fromFn.gameFlags(game) | this.distanceFn.gameFlags(game);
        switch (this.relation) {
            case Adjacent: {
                gameFlags |= 0x400000000L;
                break;
            }
            case All: {
                gameFlags |= 0x4000000000L;
                break;
            }
            case Diagonal: {
                gameFlags |= 0x1000000000L;
                break;
            }
            case OffDiagonal: {
                gameFlags |= 0x2000000000L;
                break;
            }
            case Orthogonal: {
                gameFlags |= 0x800000000L;
                break;
            }
        }
        gameFlags |= SiteType.stateFlags(this.type);
        return gameFlags;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.type = SiteType.use(this.type, game);
        this.fromFn.preprocess(game);
        this.distanceFn.preprocess(game);
        if (this.isStatic()) {
            this.precomputedRegion = this.eval(new Context(game, null));
        }
    }
}
