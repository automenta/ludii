// 
// Decompiled by Procyon v0.5.36
// 

package util.state.containerState;

import game.Game;
import game.equipment.container.Container;
import game.types.board.SiteType;
import game.util.directions.AbsoluteDirection;
import game.util.equipment.Region;
import util.Sites;
import util.UnionInfoD;
import util.state.State;
import util.symmetry.SymmetryType;
import util.symmetry.SymmetryUtils;
import util.symmetry.SymmetryValidator;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

public abstract class BaseContainerState implements ContainerState
{
    private static final long serialVersionUID = 1L;
    private transient Container container;
    private transient String nameFromFile;
    private final Map<Long, Long> canonicalHashLookup;
    private UnionInfoD[] unionInfoAdjacent;
    private UnionInfoD[] unionInfoBlockingAdjacent;
    private UnionInfoD[] unionInfoOrthogonal;
    private UnionInfoD[] unionInfoBlockingOrthogonal;
    protected final Region empty;
    protected final int offset;
    
    public BaseContainerState(final Game game, final Container container, final int numSites) {
        this.nameFromFile = null;
        this.unionInfoAdjacent = null;
        this.unionInfoBlockingAdjacent = null;
        this.unionInfoOrthogonal = null;
        this.unionInfoBlockingOrthogonal = null;
        this.container = container;
        this.empty = new Region(numSites);
        this.offset = game.equipment().sitesFrom()[container.index()];
        this.canonicalHashLookup = new HashMap<>();
    }
    
    public BaseContainerState(final BaseContainerState other) {
        this.nameFromFile = null;
        this.unionInfoAdjacent = null;
        this.unionInfoBlockingAdjacent = null;
        this.unionInfoOrthogonal = null;
        this.unionInfoBlockingOrthogonal = null;
        this.container = other.container;
        this.empty = new Region(other.empty);
        if (other.unionInfoAdjacent != null) {
            this.unionInfoAdjacent = new UnionInfoD[other.unionInfoAdjacent.length];
            for (int i = 1; i < other.unionInfoAdjacent.length; ++i) {
                this.unionInfoAdjacent[i] = new UnionInfoD(other.unionInfoAdjacent[i]);
            }
        }
        if (other.unionInfoBlockingAdjacent != null) {
            this.unionInfoBlockingAdjacent = new UnionInfoD[other.unionInfoBlockingAdjacent.length];
            for (int i = 1; i < other.unionInfoBlockingAdjacent.length; ++i) {
                this.unionInfoBlockingAdjacent[i] = new UnionInfoD(other.unionInfoBlockingAdjacent[i]);
            }
        }
        if (other.unionInfoOrthogonal != null) {
            this.unionInfoOrthogonal = new UnionInfoD[other.unionInfoOrthogonal.length];
            for (int i = 1; i < other.unionInfoOrthogonal.length; ++i) {
                this.unionInfoOrthogonal[i] = new UnionInfoD(other.unionInfoOrthogonal[i]);
            }
        }
        if (other.unionInfoBlockingOrthogonal != null) {
            this.unionInfoBlockingOrthogonal = new UnionInfoD[other.unionInfoBlockingOrthogonal.length];
            for (int i = 1; i < other.unionInfoBlockingOrthogonal.length; ++i) {
                this.unionInfoBlockingOrthogonal[i] = new UnionInfoD(other.unionInfoBlockingOrthogonal[i]);
            }
        }
        this.offset = other.offset;
        this.canonicalHashLookup = other.canonicalHashLookup;
    }
    
    public void deepCopy(final State trialState, final BaseContainerState other) {
        this.container = other.container;
        this.empty.set(other.empty);
        if (other.unionInfoAdjacent != null) {
            this.unionInfoAdjacent = new UnionInfoD[other.unionInfoAdjacent.length];
            for (int i = 1; i < other.unionInfoAdjacent.length; ++i) {
                this.unionInfoAdjacent[i] = new UnionInfoD(other.unionInfoAdjacent[i]);
            }
        }
        if (other.unionInfoBlockingAdjacent != null) {
            this.unionInfoBlockingAdjacent = new UnionInfoD[other.unionInfoBlockingAdjacent.length];
            for (int i = 1; i < other.unionInfoBlockingAdjacent.length; ++i) {
                this.unionInfoBlockingAdjacent[i] = new UnionInfoD(other.unionInfoBlockingAdjacent[i]);
            }
        }
        if (other.unionInfoOrthogonal != null) {
            this.unionInfoOrthogonal = new UnionInfoD[other.unionInfoOrthogonal.length];
            for (int i = 1; i < other.unionInfoOrthogonal.length; ++i) {
                this.unionInfoOrthogonal[i] = new UnionInfoD(other.unionInfoOrthogonal[i]);
            }
        }
        if (other.unionInfoBlockingOrthogonal != null) {
            this.unionInfoBlockingOrthogonal = new UnionInfoD[other.unionInfoBlockingOrthogonal.length];
            for (int i = 1; i < other.unionInfoBlockingOrthogonal.length; ++i) {
                this.unionInfoBlockingOrthogonal[i] = new UnionInfoD(other.unionInfoBlockingOrthogonal[i]);
            }
        }
    }
    
    @Override
    public void reset(final State trialState, final Game game) {
        final int numSites = this.container.numSites();
        this.empty.set(numSites);
    }
    
    @Override
    public String nameFromFile() {
        return this.nameFromFile;
    }
    
    @Override
    public Container container() {
        return this.container;
    }
    
    @Override
    public void setContainer(final Container cont) {
        this.container = cont;
    }
    
    @Override
    public Sites emptySites() {
        return new Sites(this.empty.sites());
    }
    
    @Override
    public int numEmpty() {
        return this.empty.count();
    }
    
    @Override
    public boolean isEmpty(final int site, final SiteType type) {
        if (type == null || type == SiteType.Cell || this.container().index() != 0) {
            return this.isEmptyCell(site);
        }
        if (type == SiteType.Edge) {
            return this.isEmptyEdge(site);
        }
        return this.isEmptyVertex(site);
    }
    
    @Override
    public boolean isEmptyVertex(final int vertex) {
        return true;
    }
    
    @Override
    public boolean isEmptyEdge(final int edge) {
        return true;
    }
    
    @Override
    public boolean isEmptyCell(final int site) {
        return this.empty.contains(site - this.offset);
    }
    
    @Override
    public Region emptyRegion(final SiteType type) {
        return this.empty;
    }
    
    @Override
    public void addToEmpty(final int site) {
        this.empty.add(site - this.offset);
    }
    
    @Override
    public void removeFromEmpty(final int site) {
        this.empty.remove(site - this.offset);
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeUTF(this.container.name());
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.nameFromFile = in.readUTF();
    }
    
    @Override
    public UnionInfoD[] unionInfoAdjacent() {
        return this.unionInfoAdjacent;
    }
    
    @Override
    public UnionInfoD[] unionInfoBlockingAdjacent() {
        return this.unionInfoBlockingAdjacent;
    }
    
    @Override
    public UnionInfoD[] unionInfoOrthogonal() {
        return this.unionInfoOrthogonal;
    }
    
    @Override
    public UnionInfoD[] unionInfoBlockingOrthogonal() {
        return this.unionInfoBlockingOrthogonal;
    }
    
    @Override
    public UnionInfoD[] unionInfo(final AbsoluteDirection dir) {
        switch (dir) {
            case Adjacent -> {
                return this.unionInfoAdjacent;
            }
            case Orthogonal -> {
                return this.unionInfoOrthogonal;
            }
            default -> {
                throw new IllegalArgumentException("BaseContainerState::unionInfo() only supports Adjacent or Orthogonal!");
            }
        }
    }
    
    @Override
    public UnionInfoD[] unionInfoBlocking(final AbsoluteDirection dir) {
        switch (dir) {
            case Adjacent -> {
                return this.unionInfoBlockingAdjacent;
            }
            case Orthogonal -> {
                return this.unionInfoBlockingOrthogonal;
            }
            default -> {
                throw new IllegalArgumentException("BaseContainerState::unionInfoBlocking() only supports Adjacent or Orthogonal!");
            }
        }
    }
    
    @Override
    public void setPlayable(final State trialState, final int site, final boolean on) {
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + this.empty.hashCode();
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof BaseContainerState)) {
            return false;
        }
        final BaseContainerState other = (BaseContainerState)obj;
        return this.empty.equals(other.empty);
    }
    
    @Override
    public void setVisible(final State trialState, final int site, final int who, final SiteType graphElementType) {
        if (graphElementType == SiteType.Cell || this.container().index() != 0 || graphElementType == null) {
            this.setVisibleCell(trialState, site, who);
        }
        else if (graphElementType == SiteType.Edge) {
            this.setVisibleEdge(trialState, site, who);
        }
        else {
            this.setVisibleVertex(trialState, site, who);
        }
    }
    
    @Override
    public void setInvisible(final State trialState, final int site, final int who, final SiteType graphElementType) {
        if (graphElementType == SiteType.Cell || this.container().index() != 0 || graphElementType == null) {
            this.setInvisibleCell(trialState, site, who);
        }
        else if (graphElementType == SiteType.Edge) {
            this.setInvisibleEdge(trialState, site, who);
        }
        else {
            this.setInvisibleVertex(trialState, site, who);
        }
    }
    
    @Override
    public void setMasked(final State trialState, final int site, final int who, final SiteType graphElementType) {
        if (graphElementType == SiteType.Cell || this.container().index() != 0 || graphElementType == null) {
            this.setMaskedCell(trialState, site, who);
        }
        else if (graphElementType == SiteType.Edge) {
            this.setMaskedEdge(trialState, site, who);
        }
        else {
            this.setMaskedVertex(trialState, site, who);
        }
    }
    
    @Override
    public int what(final int site, final SiteType graphElementType) {
        if (graphElementType == SiteType.Cell || this.container().index() != 0 || graphElementType == null) {
            return this.whatCell(site);
        }
        if (graphElementType == SiteType.Edge) {
            return this.whatEdge(site);
        }
        return this.whatVertex(site);
    }
    
    @Override
    public int who(final int site, final SiteType graphElementType) {
        if (graphElementType == SiteType.Cell || this.container().index() != 0 || graphElementType == null) {
            return this.whoCell(site);
        }
        if (graphElementType == SiteType.Edge) {
            return this.whoEdge(site);
        }
        return this.whoVertex(site);
    }
    
    @Override
    public int count(final int site, final SiteType graphElementType) {
        if (graphElementType == SiteType.Cell || this.container().index() != 0 || graphElementType == null) {
            return this.countCell(site);
        }
        if (graphElementType == SiteType.Edge) {
            return this.countEdge(site);
        }
        return this.countVertex(site);
    }
    
    @Override
    public int sizeStack(final int site, final SiteType graphElementType) {
        if (graphElementType == SiteType.Cell || this.container().index() != 0 || graphElementType == null) {
            return this.sizeStackCell(site);
        }
        if (graphElementType == SiteType.Edge) {
            return (this.whatEdge(site) != 0) ? 1 : 0;
        }
        return (this.whatVertex(site) != 0) ? 1 : 0;
    }
    
    @Override
    public boolean isInvisible(final int site, final int owner, final SiteType graphElementType) {
        if (graphElementType == SiteType.Cell || this.container().index() != 0 || graphElementType == null) {
            return this.isInvisibleCell(site, owner);
        }
        if (graphElementType == SiteType.Edge) {
            return this.isInvisibleEdge(site, owner);
        }
        return this.isInvisibleVertex(site, owner);
    }
    
    @Override
    public boolean isVisible(final int site, final int owner, final SiteType graphElementType) {
        if (graphElementType == SiteType.Cell || this.container().index() != 0 || graphElementType == null) {
            return this.isVisibleCell(site, owner);
        }
        if (graphElementType == SiteType.Edge) {
            return this.isVisibleEdge(site, owner);
        }
        return this.isVisibleVertex(site, owner);
    }
    
    @Override
    public boolean isMasked(final int site, final int owner, final SiteType graphElementType) {
        if (graphElementType == SiteType.Cell || this.container().index() != 0 || graphElementType == null) {
            return this.isMaskedCell(site, owner);
        }
        if (graphElementType == SiteType.Edge) {
            return this.isMaskedEdge(site, owner);
        }
        return this.isMaskedVertex(site, owner);
    }
    
    @Override
    public int state(final int site, final SiteType graphElementType) {
        if (graphElementType == SiteType.Cell || this.container().index() != 0 || graphElementType == null) {
            return this.stateCell(site);
        }
        if (graphElementType == SiteType.Edge) {
            return this.stateEdge(site);
        }
        return this.stateVertex(site);
    }
    
    @Override
    public int rotation(final int site, final SiteType graphElementType) {
        if (graphElementType == SiteType.Cell || this.container().index() != 0 || graphElementType == null) {
            return this.rotationCell(site);
        }
        if (graphElementType == SiteType.Edge) {
            return this.rotationEdge(site);
        }
        return this.rotationVertex(site);
    }
    
    @Override
    public int what(final int site, final int level, final SiteType graphElementType) {
        if (graphElementType == SiteType.Cell || this.container().index() != 0 || graphElementType == null) {
            return this.whatCell(site, level);
        }
        if (graphElementType == SiteType.Edge) {
            return this.whatEdge(site, level);
        }
        return this.whatVertex(site, level);
    }
    
    @Override
    public int who(final int site, final int level, final SiteType graphElementType) {
        if (graphElementType == SiteType.Cell || this.container().index() != 0 || graphElementType == null) {
            return this.whoCell(site, level);
        }
        if (graphElementType == SiteType.Edge) {
            return this.whoEdge(site, level);
        }
        return this.whoVertex(site, level);
    }
    
    @Override
    public boolean isInvisible(final int site, final int level, final int owner, final SiteType graphElementType) {
        if (graphElementType == SiteType.Cell || this.container().index() != 0 || graphElementType == null) {
            return this.isInvisibleCell(site, level, owner);
        }
        if (graphElementType == SiteType.Edge) {
            return this.isInvisibleEdge(site, owner);
        }
        return this.isInvisibleVertex(site, owner);
    }
    
    @Override
    public boolean isVisible(final int site, final int level, final int owner, final SiteType graphElementType) {
        if (graphElementType == SiteType.Cell || this.container().index() != 0 || graphElementType == null) {
            return this.isVisibleCell(site, level, owner);
        }
        if (graphElementType == SiteType.Edge) {
            return this.isVisibleEdge(site, level, owner);
        }
        return this.isVisibleVertex(site, level, owner);
    }
    
    @Override
    public boolean isMasked(final int site, final int level, final int owner, final SiteType graphElementType) {
        if (graphElementType == SiteType.Cell || this.container().index() != 0 || graphElementType == null) {
            return this.isMaskedCell(site, level, owner);
        }
        if (graphElementType == SiteType.Edge) {
            return this.isMaskedEdge(site, level, owner);
        }
        return this.isMaskedVertex(site, level, owner);
    }
    
    @Override
    public int state(final int site, final int level, final SiteType graphElementType) {
        if (graphElementType == SiteType.Cell || this.container().index() != 0 || graphElementType == null) {
            return this.stateCell(site, level);
        }
        if (graphElementType == SiteType.Edge) {
            return this.stateEdge(site, level);
        }
        return this.stateVertex(site, level);
    }
    
    @Override
    public int rotation(final int site, final int level, final SiteType graphElementType) {
        if (graphElementType == SiteType.Cell || this.container().index() != 0 || graphElementType == null) {
            return this.rotationCell(site, level);
        }
        if (graphElementType == SiteType.Edge) {
            return this.rotationEdge(site, level);
        }
        return this.rotationVertex(site, level);
    }
    
    @Override
    public void set(final int var, final int value, final SiteType type) {
    }
    
    @Override
    public boolean bit(final int index, final int value, final SiteType type) {
        return true;
    }
    
    @Override
    public boolean isResolvedEdges(final int site) {
        return false;
    }
    
    @Override
    public boolean isResolvedCell(final int site) {
        return false;
    }
    
    @Override
    public boolean isResolvedVerts(final int site) {
        return false;
    }
    
    @Override
    public boolean isResolved(final int site, final SiteType type) {
        return false;
    }
    
    @Override
    public final long canonicalHash(final SymmetryValidator validator, final State gameState, final boolean whoOnly) {
        if (this.container().topology().cellRotationSymmetries() == null) {
            Container.createSymmetries(this.container().topology());
        }
        final List<Long> allHashes = new ArrayList<>();
        final int[][] cellRotates = this.container().topology().cellRotationSymmetries();
        final int[][] cellReflects = this.container().topology().cellReflectionSymmetries();
        final int[][] edgeRotates = this.container().topology().edgeRotationSymmetries();
        final int[][] edgeReflects = this.container().topology().edgeReflectionSymmetries();
        final int[][] vertexRotates = this.container().topology().vertexRotationSymmetries();
        final int[][] vertexReflects = this.container().topology().vertexReflectionSymmetries();
        final int[][] playerPermutations = SymmetryUtils.playerPermutations(gameState.numPlayers());
        long canonicalHash = Long.MAX_VALUE;
        for (int playerIdx = 0; playerIdx < playerPermutations.length; ++playerIdx) {
            if (validator.isValid(SymmetryType.SUBSTITUTIONS, playerIdx, playerPermutations.length)) {
                for (int rotateIdx = 0; rotateIdx < cellRotates.length; ++rotateIdx) {
                    if (validator.isValid(SymmetryType.ROTATIONS, rotateIdx, cellRotates.length)) {
                        final long hash = this.calcCanonicalHash(cellRotates[rotateIdx], edgeRotates[rotateIdx], vertexRotates[rotateIdx], playerPermutations[playerIdx], whoOnly);
                        canonicalHash = Math.min(canonicalHash, hash);
                        final Long key = hash;
                        allHashes.add(key);
                        for (int reflectIdx = 0; reflectIdx < cellReflects.length; ++reflectIdx) {
                            if (validator.isValid(SymmetryType.REFLECTIONS, reflectIdx, cellReflects.length)) {
                                final int[] siteRemap = SymmetryUtils.combine(cellReflects[reflectIdx], cellRotates[rotateIdx]);
                                final int[] edgeRemap = SymmetryUtils.combine(edgeReflects[reflectIdx], edgeRotates[rotateIdx]);
                                final int[] vertexRemap = SymmetryUtils.combine(vertexReflects[reflectIdx], vertexRotates[rotateIdx]);
                                final long hash2 = this.calcCanonicalHash(siteRemap, edgeRemap, vertexRemap, playerPermutations[playerIdx], whoOnly);
                                canonicalHash = Math.min(canonicalHash, hash2);
                                allHashes.add(hash2);
                            }
                        }
                    }
                }
            }
        }
        final Long smallest = canonicalHash;
        for (final Long key2 : allHashes) {
            this.canonicalHashLookup.put(key2, smallest);
        }
        return canonicalHash;
    }
    
    protected abstract long calcCanonicalHash(final int[] siteRemap, final int[] edgeRemap, final int[] vertexRemap, final int[] playerRemap, final boolean whoOnly);
    
    @Override
    public BitSet values(final SiteType type, final int var) {
        return new BitSet();
    }
}
