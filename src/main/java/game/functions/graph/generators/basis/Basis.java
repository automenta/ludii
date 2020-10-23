// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.graph.generators.basis;

import game.functions.graph.BaseGraphFunction;
import game.types.board.SiteType;
import game.util.graph.Graph;
import util.Context;

public abstract class Basis extends BaseGraphFunction
{
    private static final long serialVersionUID = 1L;
    
    @Override
    public Graph eval(final Context context, final SiteType siteType) {
        return null;
    }
    
    @Override
    public String toEnglish() {
        return "<Shape>";
    }
}
