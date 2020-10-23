// 
// Decompiled by Procyon v0.5.36
// 

package features.instances;

import game.types.board.SiteType;
import util.state.State;

public interface BitwiseTest
{
    boolean matches(final State p0);
    
    boolean hasNoTests();
    
    boolean onlyRequiresSingleMustEmpty();
    
    boolean onlyRequiresSingleMustWho();
    
    boolean onlyRequiresSingleMustWhat();
    
    SiteType graphElementType();
}
