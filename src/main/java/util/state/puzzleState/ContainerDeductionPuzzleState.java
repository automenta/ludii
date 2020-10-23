// 
// Decompiled by Procyon v0.5.36
// 

package util.state.puzzleState;

import game.Game;
import game.equipment.container.Container;
import game.types.board.SiteType;
import main.collections.ChunkSet;
import main.math.BitTwiddling;
import util.state.State;
import util.state.containerState.ContainerState;
import util.symmetry.SymmetryValidator;
import util.zhash.ZobristHashGenerator;

import java.util.BitSet;

public class ContainerDeductionPuzzleState extends BaseContainerStateDeductionPuzzles
{
    private static final long serialVersionUID = 1L;
    protected int nbValuesVert;
    protected ChunkSet verts;
    protected ChunkSet edges;
    protected int nbValuesEdge;
    protected ChunkSet cells;
    protected int nbValuesCell;
    
    public ContainerDeductionPuzzleState(final ZobristHashGenerator generator, final Game game, final Container container) {
        super(game, container, game.equipment().totalDefaultSites());
        this.nbValuesVert = 1;
        this.nbValuesEdge = 1;
        this.nbValuesCell = 1;
        final int numEdges = game.board().topology().edges().size();
        final int numFaces = game.board().topology().cells().size();
        final int numVertices = game.board().topology().vertices().size();
        if (game.isDeductionPuzzle()) {
            this.nbValuesVert = game.board().vertexRange().max() + 1;
        }
        else {
            this.nbValuesVert = 1;
        }
        if (game.isDeductionPuzzle()) {
            this.nbValuesEdge = game.board().edgeRange().max() + 1;
        }
        else {
            this.nbValuesEdge = 1;
        }
        if (game.isDeductionPuzzle()) {
            this.nbValuesCell = game.board().cellRange().max() + 1;
        }
        else {
            this.nbValuesCell = 1;
        }
        if (this.nbValuesVert <= 31 && this.nbValuesEdge <= 31 && this.nbValuesCell <= 31) {
            final int chunkSize = BitTwiddling.nextPowerOf2(this.nbValuesVert);
            this.verts = new ChunkSet(chunkSize, numVertices);
            for (int var = 0; var < numVertices; ++var) {
                this.verts.setNBits(var, this.nbValuesVert, true);
            }
            final int chunkSizeEdge = BitTwiddling.nextPowerOf2(this.nbValuesEdge);
            this.edges = new ChunkSet(chunkSizeEdge, numEdges);
            for (int var2 = 0; var2 < numEdges; ++var2) {
                this.edges.setNBits(var2, this.nbValuesEdge, true);
            }
            final int chunkSizeFace = BitTwiddling.nextPowerOf2(this.nbValuesCell);
            this.cells = new ChunkSet(chunkSizeFace, numFaces);
            for (int i = 0; i < numFaces; ++i) {
                this.cells.setNBits(i, this.nbValuesCell, true);
            }
        }
    }
    
    public ContainerDeductionPuzzleState(final ContainerDeductionPuzzleState other) {
        super(other);
        this.nbValuesVert = 1;
        this.nbValuesEdge = 1;
        this.nbValuesCell = 1;
        this.nbValuesVert = other.nbValuesVert;
        this.nbValuesEdge = other.nbValuesEdge;
        this.nbValuesCell = other.nbValuesCell;
        this.verts = ((other.verts == null) ? null : other.verts.clone());
        this.edges = ((other.edges == null) ? null : other.edges.clone());
        this.cells = ((other.cells == null) ? null : other.cells.clone());
    }
    
    @Override
    public void reset(final State trialState, final Game game) {
        super.reset(trialState, game);
        if (this.verts != null && this.edges != null && this.cells != null) {
            final int numEdges = game.board().topology().edges().size();
            final int numCells = game.board().topology().cells().size();
            final int numVertices = game.board().topology().vertices().size();
            this.verts.clear();
            for (int var = 0; var < numVertices; ++var) {
                this.verts.setNBits(var, this.nbValuesVert, true);
            }
            this.edges.clear();
            for (int var = 0; var < numEdges; ++var) {
                this.edges.setNBits(var, this.nbValuesEdge, true);
            }
            this.cells.clear();
            for (int i = 0; i < numCells; ++i) {
                this.cells.setNBits(i, this.nbValuesCell, true);
            }
        }
    }
    
    @Override
    public int remove(final State state, final int site, final SiteType type) {
        return 0;
    }
    
    @Override
    public int remove(final State state, final int site, final int level, final SiteType type) {
        return this.remove(state, site, type);
    }
    
    @Override
    public ContainerState deepClone() {
        return new ContainerDeductionPuzzleState(this);
    }
    
    @Override
    public int numberEdge(final int var) {
        for (int i = 0; i < this.nbValuesEdge; ++i) {
            if (this.bitEdge(var, i)) {
                return i;
            }
        }
        return 0;
    }
    
    @Override
    public boolean isResolvedVerts(final int var) {
        return this.verts.isResolved(var);
    }
    
    @Override
    public int whatVertex(final int var) {
        return this.verts.resolvedTo(var);
    }
    
    @Override
    public void resetVariable(final SiteType type, final int var, final int numValues) {
        switch (type) {
            case Vertex: {
                this.verts.setNBits(var, numValues, true);
                break;
            }
            case Edge: {
                this.edges.setNBits(var, numValues, true);
                break;
            }
            case Cell: {
                this.cells.setNBits(var, numValues, true);
                break;
            }
            default: {
                this.verts.setNBits(var, numValues, true);
                break;
            }
        }
    }
    
    @Override
    public BitSet values(final SiteType type, final int var) {
        final BitSet bs = new BitSet();
        switch (type) {
            case Vertex: {
                final int values = this.verts.getChunk(var);
                for (int n = 0; n < this.nbValuesVert; ++n) {
                    if ((values & 1 << n) != 0x0) {
                        bs.set(n, true);
                    }
                }
                break;
            }
            case Edge: {
                final int values = this.edges.getChunk(var);
                for (int n = 0; n < this.nbValuesEdge; ++n) {
                    if ((values & 1 << n) != 0x0) {
                        bs.set(n, true);
                    }
                }
                break;
            }
            case Cell: {
                final int values = this.cells.getChunk(var);
                for (int n = 0; n < this.nbValuesCell; ++n) {
                    if ((values & 1 << n) != 0x0) {
                        bs.set(n, true);
                    }
                }
                break;
            }
            default: {
                final int values = this.cells.getChunk(var);
                for (int n = 0; n < this.nbValuesCell; ++n) {
                    if ((values & 1 << n) != 0x0) {
                        bs.set(n, true);
                    }
                }
                break;
            }
        }
        return bs;
    }
    
    @Override
    public boolean bitVert(final int var, final int value) {
        return this.verts.getBit(var, value) == 1;
    }
    
    @Override
    public void setVert(final int var, final int value) {
        this.verts.resolveToBit(var, value);
    }
    
    @Override
    public void toggleVerts(final int var, final int value) {
        this.verts.toggleBit(var, value);
    }
    
    @Override
    public boolean isResolvedEdges(final int var) {
        return this.edges.isResolved(var);
    }
    
    @Override
    public int whatEdge(final int var) {
        return this.edges.resolvedTo(var);
    }
    
    @Override
    public boolean bitEdge(final int var, final int value) {
        return this.edges.getBit(var, value) == 1;
    }
    
    @Override
    public void setEdge(final int var, final int value) {
        this.edges.resolveToBit(var, value);
    }
    
    @Override
    public void toggleEdges(final int var, final int value) {
        this.edges.toggleBit(var, value);
    }
    
    @Override
    public boolean bitCell(final int var, final int value) {
        return this.cells.getBit(var, value) == 1;
    }
    
    @Override
    public void setCell(final int var, final int value) {
        this.cells.resolveToBit(var, value);
    }
    
    @Override
    public void toggleCells(final int var, final int value) {
        this.cells.toggleBit(var, value);
    }
    
    @Override
    public boolean isResolvedCell(final int var) {
        return this.cells.isResolved(var);
    }
    
    @Override
    public int whatCell(final int var) {
        return this.cells.resolvedTo(var);
    }
    
    @Override
    public void setPlayable(final State trialState, final int site, final boolean on) {
    }
    
    @Override
    public int sizeStackCell(final int var) {
        if (this.cells.isResolved(var)) {
            return 1;
        }
        return 0;
    }
    
    @Override
    public int sizeStackEdge(final int var) {
        if (this.edges.isResolved(var)) {
            return 1;
        }
        return 0;
    }
    
    @Override
    public int sizeStackVertex(final int var) {
        if (this.verts.isResolved(var)) {
            return 1;
        }
        return 0;
    }
    
    @Override
    public int stateEdge(final int site) {
        return 0;
    }
    
    @Override
    public int rotationEdge(final int site) {
        return 0;
    }
    
    @Override
    public boolean isInvisibleEdge(final int site, final int who) {
        return false;
    }
    
    @Override
    public boolean isVisibleEdge(final int site, final int who) {
        return true;
    }
    
    @Override
    public boolean isMaskedEdge(final int site, final int who) {
        return false;
    }
    
    @Override
    public void setInvisibleEdge(final State state, final int site, final int who) {
    }
    
    @Override
    public void setVisibleEdge(final State state, final int site, final int who) {
    }
    
    @Override
    public void setMaskedEdge(final State state, final int site, final int who) {
    }
    
    @Override
    public int stateVertex(final int site) {
        return 0;
    }
    
    @Override
    public int rotationVertex(final int site) {
        return 0;
    }
    
    @Override
    public boolean isInvisibleVertex(final int site, final int who) {
        return false;
    }
    
    @Override
    public boolean isVisibleVertex(final int site, final int who) {
        return true;
    }
    
    @Override
    public boolean isMaskedVertex(final int site, final int who) {
        return false;
    }
    
    @Override
    public void setInvisibleVertex(final State state, final int site, final int who) {
    }
    
    @Override
    public void setVisibleVertex(final State state, final int site, final int who) {
    }
    
    @Override
    public void setMaskedVertex(final State state, final int site, final int who) {
    }
    
    @Override
    public int value(final int site) {
        return 0;
    }
    
    @Override
    public void setValue(final State trialState, final int site, final int valueVal) {
    }
    
    @Override
    public void setCount(final State trialState, final int site, final int countVal) {
    }
    
    @Override
    public void addItem(final State trialState, final int site, final int what, final int who, final Game game) {
    }
    
    @Override
    public void insert(final State trialState, final int site, final int level, final int what, final int who, final Game game) {
    }
    
    @Override
    public void addItem(final State trialState, final int site, final int what, final int who, final int stateVal, final int rotationVal, final Game game) {
    }
    
    @Override
    public void addItem(final State trialState, final int site, final int what, final int who, final Game game, final boolean[] hidden, final boolean masked) {
    }
    
    @Override
    public void removeStack(final State state, final int site) {
    }
    
    @Override
    public int whatCell(final int site, final int level) {
        return this.whatCell(site);
    }
    
    @Override
    public int stateCell(final int site, final int level) {
        return this.stateCell(site);
    }
    
    @Override
    public int rotationCell(final int site, final int level) {
        return this.rotationCell(site);
    }
    
    @Override
    public boolean isInvisibleCell(final int site, final int level, final int who) {
        return this.isInvisibleCell(site, who);
    }
    
    @Override
    public boolean isVisibleCell(final int site, final int level, final int who) {
        return this.isVisibleCell(site, who);
    }
    
    @Override
    public boolean isMaskedCell(final int site, final int level, final int who) {
        return this.isMaskedCell(site, who);
    }
    
    @Override
    public void setInvisibleCell(final State trialState, final int site, final int level, final int who) {
        this.setInvisibleCell(trialState, site, who);
    }
    
    @Override
    public void setVisibleCell(final State trialState, final int site, final int level, final int who) {
        this.setVisibleCell(trialState, site, who);
    }
    
    @Override
    public void setMaskedCell(final State trialState, final int site, final int level, final int who) {
        this.setMaskedCell(trialState, site, who);
    }
    
    @Override
    public int remove(final State state, final int site, final int level) {
        return this.remove(state, site, SiteType.Cell);
    }
    
    @Override
    public void addToEmpty(final int site, final SiteType graphType) {
        this.addToEmpty(site);
    }
    
    @Override
    public void removeFromEmpty(final int site, final SiteType graphType) {
        this.removeFromEmpty(site);
    }
    
    @Override
    public void setSite(final State trialState, final int site, final int level, final int whoVal, final int whatVal, final int countVal, final int stateVal, final int rotationVal, final int valueVal) {
    }
    
    @Override
    public long canonicalHash(final SymmetryValidator validator, final State state, final boolean whoOnly) {
        return 0L;
    }
    
    @Override
    public void setVisible(final State trialState, final int site, final int who, final SiteType graphElementType) {
    }
    
    @Override
    public void setInvisible(final State trialState, final int site, final int who, final SiteType graphElementType) {
    }
    
    @Override
    public void setMasked(final State trialState, final int site, final int who, final SiteType graphElementType) {
    }
}
