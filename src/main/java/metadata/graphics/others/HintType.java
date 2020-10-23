// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.others;

import metadata.graphics.GraphicsItem;
import metadata.graphics.util.PuzzleHintType;

public class HintType implements GraphicsItem
{
    private final PuzzleHintType hintType;
    
    public HintType(final PuzzleHintType hintType) {
        this.hintType = hintType;
    }
    
    public PuzzleHintType hintType() {
        return this.hintType;
    }
}
