// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.player.colour;

import annotations.Hide;
import game.types.play.RoleType;
import metadata.graphics.GraphicsItem;
import metadata.graphics.util.colour.Colour;

@Hide
public class PlayerColour implements GraphicsItem
{
    private final RoleType roleType;
    private final Colour colour;
    
    public PlayerColour(final RoleType roleType, final Colour colour) {
        this.roleType = roleType;
        this.colour = colour;
    }
    
    public RoleType roleType() {
        return this.roleType;
    }
    
    public Colour colour() {
        return this.colour;
    }
}
