// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans.is.component;

import annotations.Hide;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.functions.booleans.BaseBooleanFunction;
import game.functions.ints.IntFunction;
import game.functions.ints.last.LastTo;
import game.functions.region.RegionFunction;
import game.functions.region.sites.Sites;
import game.types.board.SiteType;
import game.util.equipment.Region;
import gnu.trove.list.array.TIntArrayList;
import util.Context;

@Hide
public final class IsWithin extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction pieceId;
    private final RegionFunction region;
    private SiteType type;
    
    public IsWithin(final IntFunction pieceId, @Opt final SiteType type, @Or final IntFunction locn, @Or final RegionFunction region) {
        this.pieceId = pieceId;
        if (region != null) {
            this.region = region;
        }
        else if (locn != null) {
            this.region = Sites.construct(new IntFunction[] { locn });
        }
        else {
            this.region = Sites.construct(new IntFunction[] { new LastTo(null) });
        }
        this.type = type;
    }
    
    @Override
    public final boolean eval(final Context context) {
        final int pid = this.pieceId.eval(context);
        final int owner = context.components()[pid].owner();
        final Region sites = this.region.eval(context);
        final TIntArrayList owned = context.state().owned().sites(owner, pid);
        for (int i = 0; i < owned.size(); ++i) {
            final int location = owned.getQuick(i);
            if (sites.bitSet().get(location)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "IsWithin(" + this.pieceId + "," + this.region + ")";
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = this.pieceId.gameFlags(game) | this.region.gameFlags(game);
        if (this.type != null) {
            if (this.type == SiteType.Edge || this.type == SiteType.Vertex) {
                gameFlags |= 0x800000L;
            }
            if (this.type == SiteType.Edge) {
                gameFlags |= 0x4000000L;
            }
            if (this.type == SiteType.Vertex) {
                gameFlags |= 0x1000000L;
            }
            if (this.type == SiteType.Cell) {
                gameFlags |= 0x2000000L;
            }
        }
        else if (game.board().defaultSite() == SiteType.Vertex) {
            gameFlags |= 0x1000000L;
        }
        else {
            gameFlags |= 0x2000000L;
        }
        gameFlags |= this.region.gameFlags(game);
        return gameFlags;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.type = SiteType.use(this.type, game);
        this.pieceId.preprocess(game);
        this.region.preprocess(game);
    }
}
