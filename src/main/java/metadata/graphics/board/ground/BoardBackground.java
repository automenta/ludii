// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.board.ground;

import annotations.Hide;
import annotations.Name;
import annotations.Opt;
import metadata.graphics.GraphicsItem;
import metadata.graphics.util.colour.Colour;

@Hide
public class BoardBackground implements GraphicsItem
{
    private final String background;
    private final Colour fillColour;
    private final Colour edgeColour;
    private final float scale;
    private final int rotation;
    private final float offsetX;
    private final float offsetY;
    
    public BoardBackground(@Name final String background, @Opt @Name final Colour fillColour, @Opt @Name final Colour edgeColour, @Opt @Name final Float scale, @Opt @Name final Integer rotation, @Opt @Name final Float offsetX, @Opt @Name final Float offsetY) {
        this.background = background;
        this.fillColour = fillColour;
        this.edgeColour = edgeColour;
        this.scale = ((scale == null) ? 1.0f : scale);
        this.rotation = ((rotation == null) ? 0 : rotation);
        this.offsetX = ((offsetX == null) ? 0.0f : offsetX);
        this.offsetY = ((offsetY == null) ? 0.0f : offsetY);
    }
    
    public String background() {
        return this.background;
    }
    
    public Colour fillColour() {
        return this.fillColour;
    }
    
    public Colour edgeColour() {
        return this.edgeColour;
    }
    
    public float scale() {
        return this.scale;
    }
    
    public int rotation() {
        return this.rotation;
    }
    
    public float offsetX() {
        return this.offsetX;
    }
    
    public float offsetY() {
        return this.offsetY;
    }
}
