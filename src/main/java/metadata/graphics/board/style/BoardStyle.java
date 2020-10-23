// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.board.style;

import annotations.Hide;
import metadata.graphics.GraphicsItem;
import metadata.graphics.util.ContainerStyleType;

@Hide
public class BoardStyle implements GraphicsItem
{
    private final ContainerStyleType containerStyleType;
    private final boolean onlyEdges;
    
    public BoardStyle(final ContainerStyleType containerStyleType, final boolean onlyEdges) {
        this.containerStyleType = containerStyleType;
        this.onlyEdges = onlyEdges;
    }
    
    public ContainerStyleType containerStyleType() {
        return this.containerStyleType;
    }
    
    public boolean onlyEdges() {
        return this.onlyEdges;
    }
}
