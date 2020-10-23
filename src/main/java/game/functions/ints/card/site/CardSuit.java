// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.card.site;

import annotations.Hide;
import annotations.Opt;
import game.Game;
import game.equipment.component.Component;
import game.functions.ints.BaseIntFunction;
import game.functions.ints.IntFunction;
import game.types.board.SiteType;
import util.Context;
import util.state.containerState.ContainerState;

@Hide
public final class CardSuit extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction siteFn;
    private final IntFunction levelFn;
    
    public CardSuit(final IntFunction site, @Opt final IntFunction level) {
        this.siteFn = site;
        this.levelFn = (level);
    }
    
    @Override
    public int eval(final Context context) {
        final int site = this.siteFn.eval(context);
        final int cid = context.containerId()[site];
        final ContainerState cs = context.containerState(cid);
        final int what = (this.levelFn != null) ? cs.what(site, this.levelFn.eval(context), SiteType.Cell) : cs.what(site, SiteType.Cell);
        if (what < 1) {
            return -1;
        }
        final Component component = context.components()[what];
        if (!component.isCard()) {
            return -1;
        }
        return component.suit();
    }
    
    @Override
    public boolean isStatic() {
        return this.siteFn.isStatic() && (this.levelFn == null || this.levelFn.isStatic());
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = 0x2008L | this.siteFn.gameFlags(game);
        if (this.levelFn != null) {
            gameFlags |= this.levelFn.gameFlags(game);
        }
        return gameFlags;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.siteFn.preprocess(game);
        if (this.levelFn != null) {
            this.levelFn.preprocess(game);
        }
    }
}
