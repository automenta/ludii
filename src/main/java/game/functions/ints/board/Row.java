// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.board;

import annotations.Name;
import annotations.Opt;
import game.Game;
import game.functions.ints.BaseIntFunction;
import game.functions.ints.IntFunction;
import game.types.board.SiteType;
import topology.TopologyElement;
import util.Context;

import java.util.List;

public final class Row extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction site;
    private SiteType type;
    private int precomputedValue;
    
    public Row(@Opt final SiteType type, @Name final IntFunction of) {
        this.precomputedValue = -1;
        this.site = of;
        this.type = type;
    }
    
    @Override
    public int eval(final Context context) {
        if (this.precomputedValue != -1) {
            return this.precomputedValue;
        }
        final int index = this.site.eval(context);
        if (index < 0) {
            return -1;
        }
        final SiteType realType = (this.type != null) ? this.type : context.game().board().defaultSite();
        final List<? extends TopologyElement> elements = context.topology().getGraphElements(realType);
        if (index >= elements.size()) {
            return -1;
        }
        return elements.get(index).row();
    }
    
    @Override
    public boolean isStatic() {
        return this.site.isStatic();
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = this.site.gameFlags(game);
        gameFlags |= SiteType.stateFlags(this.type);
        return gameFlags;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.type = SiteType.use(this.type, game);
        if (this.site != null) {
            this.site.preprocess(game);
        }
        if (this.isStatic()) {
            this.precomputedValue = this.eval(new Context(game, null));
        }
    }
}
