// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.size.site;

import annotations.Hide;
import annotations.Name;
import annotations.Opt;
import annotations.Or2;
import game.Game;
import game.functions.ints.BaseIntFunction;
import game.functions.ints.IntFunction;
import game.functions.ints.last.LastTo;
import game.functions.region.RegionFunction;
import game.functions.region.sites.Sites;
import game.types.board.SiteType;
import util.Context;
import util.state.containerStackingState.BaseContainerStateStacking;

@Hide
public final class SizeStack extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    private final RegionFunction region;
    private SiteType type;
    
    public SizeStack(@Opt final SiteType type, @Opt @Or2 @Name final RegionFunction in, @Opt @Or2 @Name final IntFunction at) {
        this.region = ((in != null) ? in : ((at != null) ? Sites.construct(new IntFunction[] { at }) : Sites.construct(new IntFunction[] { new LastTo(null) })));
        this.type = type;
    }
    
    @Override
    public int eval(final Context context) {
        int count = 0;
        final int[] sites2;
        final int[] sites = sites2 = this.region.eval(context).sites();
        for (final int site : sites2) {
            final BaseContainerStateStacking state = (BaseContainerStateStacking)context.state().containerStates()[context.containerId()[site]];
            count += state.sizeStack(site, this.type);
        }
        return count;
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public String toString() {
        return "Stack()";
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = this.region.gameFlags(game) | 0x10L;
        gameFlags |= SiteType.stateFlags(this.type);
        return gameFlags;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.type = SiteType.use(this.type, game);
        this.region.preprocess(game);
    }
}
