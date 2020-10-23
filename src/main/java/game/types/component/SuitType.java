// 
// Decompiled by Procyon v0.5.36
// 

package game.types.component;

import metadata.graphics.GraphicsItem;

public enum SuitType implements GraphicsItem
{
    Clubs(1), 
    Spades(2), 
    Diamonds(3), 
    Hearts(4);
    
    public final int value;
    
    SuitType(final int value) {
        this.value = value;
    }
}
