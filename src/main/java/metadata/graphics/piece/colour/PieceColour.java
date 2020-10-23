// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.piece.colour;

import annotations.Hide;
import annotations.Name;
import annotations.Opt;
import game.types.play.RoleType;
import metadata.graphics.GraphicsItem;
import metadata.graphics.util.colour.Colour;

@Hide
public class PieceColour implements GraphicsItem
{
    private final RoleType roleType;
    private final String pieceName;
    private final Integer state;
    private final Colour fillColour;
    private final Colour strokeColour;
    private final Colour secondaryColour;
    
    public PieceColour(@Opt final RoleType roleType, @Opt final String pieceName, @Opt @Name final Integer state, @Opt @Name final Colour fillColour, @Opt @Name final Colour strokeColour, @Opt @Name final Colour secondaryColour) {
        this.roleType = roleType;
        this.pieceName = pieceName;
        this.state = state;
        this.fillColour = fillColour;
        this.strokeColour = strokeColour;
        this.secondaryColour = secondaryColour;
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
    
    public Colour fillColour() {
        return this.fillColour;
    }
    
    public Colour strokeColour() {
        return this.strokeColour;
    }
    
    public Colour secondaryColour() {
        return this.secondaryColour;
    }
}
