// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.piece.ground;

import annotations.Hide;
import annotations.Name;
import annotations.Opt;
import game.types.play.RoleType;
import metadata.graphics.GraphicsItem;
import metadata.graphics.util.colour.Colour;

@Hide
public class PieceForeground implements GraphicsItem
{
    private final RoleType roleType;
    private final String pieceName;
    private final Integer state;
    private final String foreground;
    private final Colour fillColour;
    private final Colour edgeColour;
    private final float scale;
    
    public PieceForeground(@Opt final RoleType roleType, @Opt final String pieceName, @Opt @Name final Integer state, @Name final String foreground, @Opt @Name final Colour fillColour, @Opt @Name final Colour edgeColour, @Opt @Name final Float scale) {
        this.roleType = roleType;
        this.pieceName = pieceName;
        this.state = state;
        this.foreground = foreground;
        this.fillColour = fillColour;
        this.edgeColour = edgeColour;
        this.scale = ((scale == null) ? 1.0f : scale);
    }
    
    public RoleType roleType() {
        return this.roleType;
    }
    
    public String pieceName() {
        return this.pieceName;
    }
    
    public Integer state() {
        return this.state;
    }
    
    public String foreground() {
        return this.foreground;
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
}
