// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.no.Boolean;

import annotations.Hide;
import annotations.Opt;
import metadata.graphics.GraphicsItem;

@Hide
public class NoDicePips implements GraphicsItem
{
    private final boolean noDicePips;
    
    public NoDicePips(@Opt final Boolean noDicePips) {
        this.noDicePips = (noDicePips == null || noDicePips);
    }
    
    public boolean noDicePips() {
        return this.noDicePips;
    }
}
