// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.no.Boolean;

import annotations.Hide;
import annotations.Opt;
import metadata.graphics.GraphicsItem;

@Hide
public class NoMaskedColour implements GraphicsItem
{
    private final boolean noMaskedColour;
    
    public NoMaskedColour(@Opt final Boolean noMaskedColour) {
        this.noMaskedColour = (noMaskedColour == null || noMaskedColour);
    }
    
    public boolean noMaskedColour() {
        return this.noMaskedColour;
    }
}
