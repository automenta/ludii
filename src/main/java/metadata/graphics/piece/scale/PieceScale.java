// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.piece.scale;

import annotations.Hide;
import annotations.Opt;
import game.types.play.RoleType;
import metadata.graphics.GraphicsItem;

@Hide
public class PieceScale implements GraphicsItem
{
    private final RoleType roleType;
    private final String pieceName;
    private final double scale;
    
    public PieceScale(@Opt final RoleType roleType, @Opt final String pieceName, final Float scale) {
        this.roleType = roleType;
        this.pieceName = pieceName;
        this.scale = scale;
    }
    
    public RoleType roleType() {
        return this.roleType;
    }
    
    public String pieceName() {
        return this.pieceName;
    }
    
    public double scale() {
        return this.scale;
    }
}
