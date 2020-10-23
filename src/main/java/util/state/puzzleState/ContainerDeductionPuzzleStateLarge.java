// 
// Decompiled by Procyon v0.5.36
// 

package util.state.puzzleState;

import game.Game;
import game.equipment.container.Container;
import game.types.board.SiteType;
import util.state.State;
import util.state.containerState.ContainerState;
import util.zhash.ZobristHashGenerator;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class ContainerDeductionPuzzleStateLarge extends ContainerDeductionPuzzleState
{
    private static final long serialVersionUID = 1L;
    protected final List<BitSet> verticeList;
    protected final List<BitSet> edgesList;
    protected final List<BitSet> cellsList;
    
    public ContainerDeductionPuzzleStateLarge(final ZobristHashGenerator generator, final Game game, final Container container) {
        super(generator, game, container);
        final int numEdges = game.board().topology().edges().size();
        final int numCells = game.board().topology().cells().size();
        final int numVertices = game.board().topology().vertices().size();
        if (game.isDeductionPuzzle()) {
            this.nbValuesVert = game.board().vertexRange().max() - game.board().vertexRange().min() + 1;
        }
        else {
            this.nbValuesVert = 1;
        }
        if (game.isDeductionPuzzle()) {
            this.nbValuesEdge = game.board().edgeRange().max() - game.board().edgeRange().min() + 1;
        }
        else {
            this.nbValuesEdge = 1;
        }
        if (game.isDeductionPuzzle()) {
            this.nbValuesCell = game.board().cellRange().max() - game.board().cellRange().min() + 1;
        }
        else {
            this.nbValuesCell = 1;
        }
        this.verticeList = new ArrayList<>();
        for (int var = 0; var < numVertices; ++var) {
            final BitSet values = new BitSet(this.nbValuesVert);
            values.set(0, this.nbValuesVert, true);
            this.verticeList.add(values);
        }
        this.edgesList = new ArrayList<>();
        for (int var = 0; var < numEdges; ++var) {
            final BitSet values = new BitSet(this.nbValuesEdge);
            values.set(0, this.nbValuesEdge, true);
            this.edgesList.add(values);
        }
        this.cellsList = new ArrayList<>();
        for (int var = 0; var < numCells; ++var) {
            final BitSet values = new BitSet(this.nbValuesCell);
            values.set(0, this.nbValuesCell, true);
            this.cellsList.add(values);
        }
    }
    
    public ContainerDeductionPuzzleStateLarge(final ContainerDeductionPuzzleStateLarge other) {
        super(other);
        this.nbValuesVert = other.nbValuesVert;
        this.nbValuesEdge = other.nbValuesEdge;
        this.nbValuesCell = other.nbValuesCell;
        if (other.verticeList == null) {
            this.verticeList = null;
        }
        else {
            this.verticeList = new ArrayList<>();
            for (final BitSet bs : other.verticeList) {
                this.verticeList.add((BitSet)bs.clone());
            }
        }
        if (other.edgesList == null) {
            this.edgesList = null;
        }
        else {
            this.edgesList = new ArrayList<>();
            for (final BitSet bs : other.edgesList) {
                this.edgesList.add((BitSet)bs.clone());
            }
        }
        if (other.cellsList == null) {
            this.cellsList = null;
        }
        else {
            this.cellsList = new ArrayList<>();
            for (final BitSet bs : other.cellsList) {
                this.cellsList.add((BitSet)bs.clone());
            }
        }
    }
    
    @Override
    public void reset(final State trialState, final Game game) {
        super.reset(trialState, game);
        if (this.verticeList != null) {
            for (final BitSet bs : this.verticeList) {
                bs.set(0, this.nbValuesVert, true);
            }
        }
        if (this.edgesList != null) {
            for (final BitSet bs : this.edgesList) {
                bs.set(0, this.nbValuesEdge, true);
            }
        }
        if (this.cellsList != null) {
            for (final BitSet bs : this.cellsList) {
                bs.set(0, this.nbValuesCell, true);
            }
        }
    }
    
    @Override
    public int remove(final State state, final int site, final SiteType type) {
        return 0;
    }
    
    @Override
    public ContainerState deepClone() {
        return new ContainerDeductionPuzzleStateLarge(this);
    }
    
    @Override
    public String nameFromFile() {
        return null;
    }
    
    @Override
    public int whoCell(final int site) {
        return 0;
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
        return this.verticeList.get(var - this.offset).cardinality() == 1;
    }
    
    @Override
    public int whatVertex(final int var) {
        final BitSet bs = this.verticeList.get(var - this.offset);
        final int firstOnBit = bs.nextSetBit(0);
        if (firstOnBit == -1) {
            System.out.println("** Unexpected empty variable.");
            return 0;
        }
        if (bs.nextSetBit(firstOnBit + 1) == -1) {
            return firstOnBit;
        }
        return 0;
    }
    
    @Override
    public void resetVariable(final SiteType type, final int var, final int numValues) {
        switch (type) {
            case Vertex: {
                this.verticeList.get(var - this.offset).set(0, this.nbValuesVert, true);
                break;
            }
            case Edge: {
                this.edgesList.get(var).set(0, this.nbValuesEdge, true);
                break;
            }
            case Cell: {
                this.cellsList.get(var).set(0, this.nbValuesCell, true);
                break;
            }
            default: {
                this.verticeList.get(var - this.offset).set(0, this.nbValuesVert, true);
                break;
            }
        }
    }
    
    @Override
    public BitSet values(final SiteType type, final int var) {
        switch (type) {
            case Vertex: {
                return this.verticeList.get(var - this.offset);
            }
            case Edge: {
                return this.edgesList.get(var);
            }
            case Cell: {
                return this.cellsList.get(var);
            }
            default: {
                return this.verticeList.get(var - this.offset);
            }
        }
    }
    
    @Override
    public boolean bitVert(final int var, final int value) {
        return this.verticeList.get(var - this.offset).get(value);
    }
    
    @Override
    public void setVert(final int var, final int value) {
        final BitSet bs = this.verticeList.get(var - this.offset);
        bs.clear();
        bs.set(value, true);
    }
    
    @Override
    public void toggleVerts(final int var, final int value) {
        this.verticeList.get(var - this.offset).flip(value);
    }
    
    @Override
    public boolean isResolvedEdges(final int var) {
        return this.edgesList.get(var).cardinality() == 1;
    }
    
    @Override
    public int whatEdge(final int var) {
        final BitSet bs = this.edgesList.get(var);
        final int firstOnBit = bs.nextSetBit(0);
        if (firstOnBit == -1) {
            System.out.println("** Unexpected empty variable.");
            return 0;
        }
        if (bs.nextSetBit(firstOnBit + 1) == -1) {
            return firstOnBit;
        }
        return 0;
    }
    
    @Override
    public boolean bitEdge(final int var, final int value) {
        return this.edgesList.get(var).get(value);
    }
    
    @Override
    public void setEdge(final int var, final int value) {
        final BitSet bs = this.edgesList.get(var);
        bs.clear();
        bs.set(value, true);
    }
    
    @Override
    public void toggleEdges(final int var, final int value) {
        this.edgesList.get(var).flip(value);
    }
    
    @Override
    public boolean bitCell(final int var, final int value) {
        return this.cellsList.get(var).get(value);
    }
    
    @Override
    public void setCell(final int var, final int value) {
        final BitSet bs = this.cellsList.get(var);
        bs.clear();
        bs.set(value, true);
    }
    
    @Override
    public void toggleCells(final int var, final int value) {
        this.cellsList.get(var).flip(value);
    }
    
    @Override
    public boolean isResolvedCell(final int var) {
        return this.cellsList.get(var).cardinality() == 1;
    }
    
    @Override
    public int whatCell(final int var) {
        final BitSet bs = this.cellsList.get(var);
        final int firstOnBit = bs.nextSetBit(0);
        if (firstOnBit == -1) {
            System.out.println("** Unexpected empty variable.");
            return 0;
        }
        if (bs.nextSetBit(firstOnBit + 1) == -1) {
            return firstOnBit;
        }
        return 0;
    }
}
