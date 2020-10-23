// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.no.Boolean;

import annotations.Hide;
import annotations.Opt;
import metadata.graphics.GraphicsItem;

@Hide
public class NoAnimation implements GraphicsItem
{
    private final boolean noAnimation;
    
    public NoAnimation(@Opt final Boolean noAnimation) {
        this.noAnimation = (noAnimation == null || noAnimation);
    }
    
    public boolean noAnimation() {
        return this.noAnimation;
    }
}
