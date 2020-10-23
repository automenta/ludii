// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.show.line;

import annotations.Hide;
import annotations.Name;
import annotations.Opt;
import metadata.graphics.GraphicsItem;
import metadata.graphics.util.colour.Colour;

@Hide
public class ShowLine implements GraphicsItem
{
    private final Integer[][] lines;
    private final float scale;
    private final Colour colour;
    private final Float[] curve;
    
    public ShowLine(final Integer[][] lines, @Opt final Colour colour, @Opt @Name final Float scale, @Opt @Name final Float[] curve) {
        this.lines = lines;
        this.colour = colour;
        this.scale = ((scale == null) ? 1.0f : scale);
        this.curve = curve;
    }
    
    public Integer[][] lines() {
        return this.lines;
    }
    
    public float scale() {
        return this.scale;
    }
    
    public Colour colour() {
        return this.colour;
    }
    
    public Float[] curve() {
        return this.curve;
    }
}
