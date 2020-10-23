// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.size.largePiece;

import annotations.Hide;
import annotations.Name;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.equipment.component.Component;
import game.functions.ints.BaseIntFunction;
import game.functions.ints.IntFunction;
import game.functions.region.RegionFunction;
import game.functions.region.sites.custom.SitesCustom;
import game.types.board.SiteType;
import gnu.trove.list.array.TIntArrayList;
import util.Context;
import util.state.containerState.ContainerState;

@Hide
public final class SizeLargePiece extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    private final RegionFunction region;
    private SiteType type;
    
    public SizeLargePiece(@Opt final SiteType type, @Or @Name final RegionFunction in, @Or @Name final IntFunction at) {
        this.type = type;
        this.region = ((in != null) ? in : new SitesCustom(new IntFunction[] { at }));
    }
    
    @Override
    public int eval(final Context context) {
        int count = 0;
        final SiteType realType = (this.type != null) ? this.type : context.game().board().defaultSite();
        final int[] sites = this.region.eval(context).sites();
        for (int i = 0; i < sites.length; ++i) {
            final int site = sites[i];
            final int cid = realType.equals(SiteType.Cell) ? context.containerId()[site] : 0;
            final ContainerState cs = context.containerState(cid);
            final int what = cs.what(site, realType);
            if (what != 0) {
                final Component component = context.components()[what];
                if (component.isLargePiece()) {
                    final TIntArrayList locs = component.locs(context, context.topology().centre(realType).get(0).index(), 0, context.topology());
                    count += locs.size();
                }
                else {
                    ++count;
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
    public long gameFlags(final Game game) {
        long flags = this.region.gameFlags(game);
        flags |= SiteType.stateFlags(this.type);
        return flags;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.type = SiteType.use(this.type, game);
        this.region.preprocess(game);
    }
}
