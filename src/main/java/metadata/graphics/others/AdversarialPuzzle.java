// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.others;

import annotations.Opt;
import metadata.graphics.GraphicsItem;

public class AdversarialPuzzle implements GraphicsItem
{
    private final boolean adversarialPuzzle;
    
    public AdversarialPuzzle(@Opt final Boolean adversarialPuzzle) {
        this.adversarialPuzzle = (adversarialPuzzle == null || adversarialPuzzle);
    }
    
    public boolean adversarialPuzzle() {
        return this.adversarialPuzzle;
    }
}
