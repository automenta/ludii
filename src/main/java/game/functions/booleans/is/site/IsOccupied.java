// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans.is.site;

import annotations.Hide;
import annotations.Opt;
import game.Game;
import game.functions.booleans.BaseBooleanFunction;
import game.functions.ints.IntFunction;
import game.types.board.SiteType;
import util.Context;
import util.state.containerState.ContainerState;

@Hide
public final class IsOccupied extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    private SiteType type;
    private final IntFunction siteFn;
    
    public IsOccupied(@Opt final SiteType type, final IntFunction site) {
        this.type = type;
        this.siteFn = site;
    }
    
    @Override
    public boolean eval(final Context context) {
        final int site = this.siteFn.eval(context);
        final ContainerState cs = context.containerState(context.containerId()[site]);
        return cs.what(site, this.type) != 0;
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = this.siteFn.gameFlags(game);
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
        return gameFlags;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.type = SiteType.use(this.type, game);
        this.siteFn.preprocess(game);
    }
}
