// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.piece.families;

import annotations.Hide;
import metadata.graphics.GraphicsItem;

@Hide
public class PieceFamilies implements GraphicsItem
{
    private final String[] pieceFamilies;
    
    public PieceFamilies(final String[] pieceFamilies) {
        this.pieceFamilies = pieceFamilies;
    }
    
    public String[] pieceFamilies() {
        return this.pieceFamilies;
    }
}
