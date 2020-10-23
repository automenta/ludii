// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.no.Boolean;

import annotations.Hide;
import annotations.Opt;
import metadata.graphics.GraphicsItem;

@Hide
public class NoHandScale implements GraphicsItem
{
    private final boolean noHandScale;
    
    public NoHandScale(@Opt final Boolean noHandScale) {
        this.noHandScale = (noHandScale == null || noHandScale);
    }
    
    public boolean noHandScale() {
        return this.noHandScale;
    }
}
