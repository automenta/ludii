// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.player;

import game.types.play.RoleType;
import metadata.graphics.GraphicsItem;
import metadata.graphics.player.colour.PlayerColour;
import metadata.graphics.util.colour.Colour;

public class Player implements GraphicsItem
{
    public static GraphicsItem construct(final PlayerColourType playerType, final RoleType roleType, final Colour colour) {
        switch (playerType) {
            case Colour -> {
                return new PlayerColour(roleType, colour);
            }
            default -> {
                throw new IllegalArgumentException("Player(): A PlayerColourType is not implemented.");
            }
        }
    }
    
    private Player() {
    }
}
