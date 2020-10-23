// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.context;

import annotations.Name;
import annotations.Opt;
import game.Game;
import game.functions.ints.BaseIntFunction;
import game.functions.ints.IntFunction;
import game.types.board.SiteType;
import util.Context;

public final class Hint extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    final SiteType type;
    final IntFunction siteFn;
    
    public Hint(@Opt final SiteType type, @Name @Opt final IntFunction at) {
        this.type = type;
        this.siteFn = at;
    }
    
    @Override
    public int eval(final Context context) {
        if (this.siteFn == null) {
            return context.hint();
        }
        final int site = this.siteFn.eval(context);
        Integer[][] regions = null;
        Integer[] hints = null;
        switch (this.type) {
            case Edge: {
                regions = context.game().equipment().edgesWithHints();
                hints = context.game().equipment().edgeHints();
                break;
            }
            case Vertex: {
                regions = context.game().equipment().verticesWithHints();
                hints = context.game().equipment().vertexHints();
                break;
            }
            case Cell: {
                regions = context.game().equipment().cellsWithHints();
                hints = context.game().equipment().cellHints();
                break;
            }
        }
        if (regions == null || hints == null) {
            return -1;
        }
        for (int i = 0; i < Math.min(hints.length, regions.length); ++i) {
            if (regions[i][0] == site) {
                return hints[i];
            }
        }
        return -1;
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long flags = 0L;
        if (this.siteFn != null) {
            flags |= this.siteFn.gameFlags(game);
        }
        if (this.type != null) {
            if (this.type.equals(SiteType.Edge) || this.type.equals(SiteType.Vertex)) {
                flags |= 0x800000L;
            }
            if (this.type.equals(SiteType.Edge)) {
                flags |= 0x4000000L;
            }
            if (this.type.equals(SiteType.Vertex)) {
                flags |= 0x1000000L;
            }
            if (this.type.equals(SiteType.Cell)) {
                flags |= 0x2000000L;
            }
        }
        else if (game.board().defaultSite() == SiteType.Vertex) {
            flags |= 0x1000000L;
        }
        else {
            flags |= 0x2000000L;
        }
        return flags;
    }
    
    @Override
    public void preprocess(final Game game) {
        if (this.siteFn != null) {
            this.siteFn.preprocess(game);
        }
    }
    
    @Override
    public String toString() {
        return "Hint()";
    }
    
    @Override
    public boolean isHint() {
        return true;
    }
}
