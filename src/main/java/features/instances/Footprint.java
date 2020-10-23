// 
// Decompiled by Procyon v0.5.36
// 

package features.instances;

import main.collections.ChunkSet;

public final class Footprint
{
    protected final ChunkSet emptyCell;
    protected final ChunkSet emptyVertex;
    protected final ChunkSet emptyEdge;
    protected final ChunkSet whoCell;
    protected final ChunkSet whoVertex;
    protected final ChunkSet whoEdge;
    protected final ChunkSet whatCell;
    protected final ChunkSet whatVertex;
    protected final ChunkSet whatEdge;
    
    public Footprint(final ChunkSet emptyCell, final ChunkSet emptyVertex, final ChunkSet emptyEdge, final ChunkSet whoCell, final ChunkSet whoVertex, final ChunkSet whoEdge, final ChunkSet whatCell, final ChunkSet whatVertex, final ChunkSet whatEdge) {
        this.emptyCell = emptyCell;
        this.emptyVertex = emptyVertex;
        this.emptyEdge = emptyEdge;
        this.whoCell = whoCell;
        this.whoVertex = whoVertex;
        this.whoEdge = whoEdge;
        this.whatCell = whatCell;
        this.whatVertex = whatVertex;
        this.whatEdge = whatEdge;
    }
    
    public final ChunkSet emptyCell() {
        return this.emptyCell;
    }
    
    public final ChunkSet emptyVertex() {
        return this.emptyVertex;
    }
    
    public final ChunkSet emptyEdge() {
        return this.emptyEdge;
    }
    
    public final ChunkSet whoCell() {
        return this.whoCell;
    }
    
    public final ChunkSet whoVertex() {
        return this.whoVertex;
    }
    
    public final ChunkSet whoEdge() {
        return this.whoEdge;
    }
    
    public final ChunkSet whatCell() {
        return this.whatCell;
    }
    
    public final ChunkSet whatVertex() {
        return this.whatVertex;
    }
    
    public final ChunkSet whatEdge() {
        return this.whatEdge;
    }
}
