// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.no.Boolean;

import annotations.Hide;
import annotations.Opt;
import metadata.graphics.GraphicsItem;

@Hide
public class NoCurves implements GraphicsItem
{
    private final boolean straightRingLines;
    
    public NoCurves(@Opt final Boolean straightRingLines) {
        this.straightRingLines = (straightRingLines == null || straightRingLines);
    }
    
    public boolean straightRingLines() {
        return this.straightRingLines;
    }
}
