// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.others;

import game.types.component.SuitType;
import metadata.graphics.GraphicsItem;

public class SuitRanking implements GraphicsItem
{
    private final SuitType[] suitRanking;
    
    public SuitRanking(final SuitType[] suitRanking) {
        this.suitRanking = suitRanking;
    }
    
    public SuitType[] suitRanking() {
        return this.suitRanking;
    }
}
