// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.card.site;

import annotations.Hide;
import annotations.Opt;
import game.Game;
import game.equipment.component.Component;
import game.functions.ints.BaseIntFunction;
import game.functions.ints.IntConstant;
import game.functions.ints.IntFunction;
import util.Context;
import util.state.containerState.ContainerState;

@Hide
public final class CardRank extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction siteFn;
    private final IntFunction levelFn;
    
    public CardRank(final IntFunction site, @Opt final IntFunction level) {
        this.siteFn = site;
        this.levelFn = ((level == null) ? new IntConstant(0) : level);
    }
    
    @Override
    public int eval(final Context context) {
        final int site = this.siteFn.eval(context);
        final int level = this.levelFn.eval(context);
        final int cid = context.containerId()[site];
        final ContainerState cs = context.containerState(cid);
        final int what = cs.whatCell(site, level);
        if (what < 1) {
            return -1;
        }
        final Component component = context.components()[what];
        if (!component.isCard()) {
            return -1;
        }
        return component.rank();
    }
    
    @Override
    public boolean isStatic() {
        return this.siteFn.isStatic() && this.levelFn.isStatic();
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0x2008L | this.siteFn.gameFlags(game) | this.levelFn.gameFlags(game);
    }
    
    @Override
    public void preprocess(final Game game) {
        this.siteFn.preprocess(game);
        this.levelFn.preprocess(game);
    }
}
