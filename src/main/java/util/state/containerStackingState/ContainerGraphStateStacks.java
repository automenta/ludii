// 
// Decompiled by Procyon v0.5.36
// 

package util.state.containerStackingState;

import game.Game;
import game.equipment.container.Container;
import game.types.board.SiteType;
import game.util.equipment.Region;
import topology.Vertex;
import util.state.State;
import util.zhash.*;

import java.util.Arrays;

public class ContainerGraphStateStacks extends ContainerStateStacks
{
    private static final long serialVersionUID = 1L;
    private final HashedChunkStack[] chunkStacksVertex;
    private final HashedChunkSet[] hiddenVertex;
    private final HashedBitSet playableVertex;
    private final HashedChunkStack[] chunkStacksEdge;
    private final HashedChunkSet[] hiddenEdge;
    private final HashedBitSet playableEdge;
    private final Region emptyEdge;
    private final Region emptyVertex;
    private final long[][][] chunkStacksWhatVertexHash;
    private final long[][][] chunkStacksWhoVertexHash;
    private final long[][][] chunkStacksStateVertexHash;
    private final long[][][] chunkStacksRotationVertexHash;
    private final long[][] chunkStacksSizeVertexHash;
    private final long[][][] chunkStacksWhatEdgeHash;
    private final long[][][] chunkStacksWhoEdgeHash;
    private final long[][][] chunkStacksStateEdgeHash;
    private final long[][][] chunkStacksRotationEdgeHash;
    private final long[][] chunkStacksSizeEdgeHash;
    private final boolean hiddenVertexInfo;
    private final boolean hiddenEdgeInfo;
    
    public ContainerGraphStateStacks(final ZobristHashGenerator generator, final Game game, final Container container, final int type) {
        super(generator, game, container, type);
        final int numEdges = game.board().topology().edges().size();
        final int numVertice = game.board().topology().vertices().size();
        this.chunkStacksVertex = new HashedChunkStack[numVertice];
        this.chunkStacksEdge = new HashedChunkStack[numEdges];
        final int maxValWhat = this.numComponents;
        final int maxValWho = this.numPlayers + 1;
        final int maxValState = this.numStates;
        final int maxValRotation = this.numRotation;
        this.chunkStacksWhatVertexHash = ZobristHashUtilities.getSequence(generator, numVertice, 32, maxValWhat + 1);
        this.chunkStacksWhoVertexHash = ZobristHashUtilities.getSequence(generator, numVertice, 32, maxValWho + 1);
        this.chunkStacksStateVertexHash = ZobristHashUtilities.getSequence(generator, numVertice, 32, maxValState + 1);
        this.chunkStacksRotationVertexHash = ZobristHashUtilities.getSequence(generator, numVertice, 32, maxValRotation + 1);
        this.chunkStacksSizeVertexHash = ZobristHashUtilities.getSequence(generator, numVertice, 32);
        this.chunkStacksWhatEdgeHash = ZobristHashUtilities.getSequence(generator, numEdges, 32, maxValWhat + 1);
        this.chunkStacksWhoEdgeHash = ZobristHashUtilities.getSequence(generator, numEdges, 32, maxValWho + 1);
        this.chunkStacksStateEdgeHash = ZobristHashUtilities.getSequence(generator, numEdges, 32, maxValState + 1);
        this.chunkStacksRotationEdgeHash = ZobristHashUtilities.getSequence(generator, numEdges, 32, maxValRotation + 1);
        this.chunkStacksSizeEdgeHash = ZobristHashUtilities.getSequence(generator, numEdges, 32);
        if ((game.gameFlags() & 0x8L) == 0x0L) {
            this.hiddenVertex = null;
            this.hiddenVertexInfo = false;
            this.hiddenEdge = null;
            this.hiddenEdgeInfo = false;
        }
        else {
            this.hiddenVertex = new HashedChunkSet[numVertice];
            this.hiddenVertexInfo = true;
            this.hiddenEdge = new HashedChunkSet[numEdges];
            this.hiddenEdgeInfo = true;
        }
        if (!game.isBoardless()) {
            this.playableVertex = null;
            this.playableEdge = null;
        }
        else {
            this.playableVertex = new HashedBitSet(generator, numVertice);
            this.playableEdge = new HashedBitSet(generator, numEdges);
        }
        this.emptyEdge = new Region(numEdges);
        this.emptyVertex = new Region(numVertice);
    }
    
    public ContainerGraphStateStacks(final ContainerGraphStateStacks other) {
        super(other);
        this.playableVertex = ((other.playableVertex == null) ? null : other.playableVertex.clone());
        this.playableEdge = ((other.playableEdge == null) ? null : other.playableEdge.clone());
        if (other.hiddenVertex == null) {
            this.hiddenVertex = null;
        }
        else {
            this.hiddenVertex = new HashedChunkSet[other.hiddenVertex.length];
            for (int i = 0; i < this.hiddenVertex.length; ++i) {
                if (other.hiddenVertex[i] != null) {
                    this.hiddenVertex[i] = other.hiddenVertex[i].clone();
                }
            }
        }
        if (other.hiddenEdge == null) {
            this.hiddenEdge = null;
        }
        else {
            this.hiddenEdge = new HashedChunkSet[other.hiddenEdge.length];
            for (int i = 0; i < this.hiddenEdge.length; ++i) {
                if (other.hiddenEdge[i] != null) {
                    this.hiddenEdge[i] = other.hiddenEdge[i].clone();
                }
            }
        }
        if (other.chunkStacksVertex == null) {
            this.chunkStacksVertex = null;
        }
        else {
            this.chunkStacksVertex = new HashedChunkStack[other.chunkStacksVertex.length];
            for (int i = 0; i < other.chunkStacksVertex.length; ++i) {
                final HashedChunkStack otherChunkStack = other.chunkStacksVertex[i];
                if (otherChunkStack != null) {
                    this.chunkStacksVertex[i] = otherChunkStack.clone();
                }
            }
        }
        if (other.chunkStacksEdge == null) {
            this.chunkStacksEdge = null;
        }
        else {
            this.chunkStacksEdge = new HashedChunkStack[other.chunkStacksEdge.length];
            for (int i = 0; i < other.chunkStacksEdge.length; ++i) {
                final HashedChunkStack otherChunkStack = other.chunkStacksEdge[i];
                if (otherChunkStack != null) {
                    this.chunkStacksEdge[i] = otherChunkStack.clone();
                }
            }
        }
        this.chunkStacksWhatVertexHash = other.chunkStacksWhatVertexHash;
        this.chunkStacksWhoVertexHash = other.chunkStacksWhoVertexHash;
        this.chunkStacksStateVertexHash = other.chunkStacksStateVertexHash;
        this.chunkStacksRotationVertexHash = other.chunkStacksRotationVertexHash;
        this.chunkStacksSizeVertexHash = other.chunkStacksSizeVertexHash;
        this.hiddenVertexInfo = other.hiddenVertexInfo;
        this.chunkStacksWhatEdgeHash = other.chunkStacksWhatEdgeHash;
        this.chunkStacksWhoEdgeHash = other.chunkStacksWhoEdgeHash;
        this.chunkStacksStateEdgeHash = other.chunkStacksStateEdgeHash;
        this.chunkStacksRotationEdgeHash = other.chunkStacksRotationEdgeHash;
        this.chunkStacksSizeEdgeHash = other.chunkStacksSizeEdgeHash;
        this.hiddenEdgeInfo = other.hiddenEdgeInfo;
        this.emptyEdge = new Region(other.emptyEdge);
        this.emptyVertex = new Region(other.emptyVertex);
    }
    
    @Override
    protected long calcCanonicalHash(final int[] siteRemap, final int[] edgeRemap, final int[] vertexRemap, final int[] playerRemap, final boolean whoOnly) {
        if (this.offset != 0) {
            return 0L;
        }
        long hash = super.calcCanonicalHash(siteRemap, edgeRemap, vertexRemap, playerRemap, whoOnly);
        hash ^= calcCanonicalHashOverSites(this.chunkStacksVertex, siteRemap, this.chunkStacksWhatVertexHash, this.chunkStacksWhoVertexHash, this.chunkStacksStateVertexHash, this.chunkStacksRotationVertexHash, this.chunkStacksSizeVertexHash, whoOnly);
        hash ^= calcCanonicalHashOverSites(this.chunkStacksEdge, siteRemap, this.chunkStacksWhatEdgeHash, this.chunkStacksWhoEdgeHash, this.chunkStacksStateEdgeHash, this.chunkStacksRotationEdgeHash, this.chunkStacksSizeEdgeHash, whoOnly);
        return hash;
    }
    
    private static long calcCanonicalHashOverSites(final HashedChunkStack[] stack, final int[] siteRemap, final long[][][] whatHash, final long[][][] whoHash, final long[][][] stateHash, final long[][][] rotationHash, final long[][] sizeHash, final boolean whoOnly) {
        long hash = 0L;
        for (int pos = 0; pos < stack.length && pos < siteRemap.length; ++pos) {
            final int newPos = siteRemap[pos];
            if (stack[pos] != null) {
                hash ^= stack[pos].remapHashTo(whatHash[newPos], whoHash[newPos], stateHash[newPos], rotationHash[newPos], sizeHash[newPos], whoOnly);
            }
        }
        return hash;
    }
    
    @Override
    public void reset(final State trialState, final Game game) {
        super.reset(trialState, game);
        final int numEdges = (game.board().defaultSite() == SiteType.Cell) ? game.board().topology().edges().size() : game.board().topology().edges().size();
        final int numVertices = (game.board().defaultSite() == SiteType.Cell) ? game.board().topology().vertices().size() : game.board().topology().vertices().size();
        for (final HashedChunkStack set : this.chunkStacksVertex) {
            if (set != null) {
                trialState.updateStateHash(set.calcHash());
            }
        }
        Arrays.fill(this.chunkStacksVertex, null);
        if (this.hiddenVertex != null) {
            for (final HashedChunkSet set2 : this.hiddenVertex) {
                if (set2 != null) {
                    set2.clear(trialState);
                }
            }
        }
        super.reset(trialState, game);
        for (final HashedChunkStack set : this.chunkStacksEdge) {
            if (set != null) {
                trialState.updateStateHash(set.calcHash());
            }
        }
        Arrays.fill(this.chunkStacksEdge, null);
        if (this.hiddenEdge != null) {
            for (final HashedChunkSet set2 : this.hiddenEdge) {
                if (set2 != null) {
                    set2.clear(trialState);
                }
            }
        }
        this.emptyEdge.set(numEdges);
        this.emptyVertex.set(numVertices);
    }
    
    private void verifyPresentVertex(final int site) {
        if (this.chunkStacksVertex[site] != null) {
            return;
        }
        this.chunkStacksVertex[site] = new HashedChunkStack(this.numComponents, this.numPlayers, this.numStates, this.numRotation, this.type, this.hiddenVertexInfo, this.chunkStacksWhatVertexHash[site], this.chunkStacksWhoVertexHash[site], this.chunkStacksStateVertexHash[site], this.chunkStacksRotationVertexHash[site], this.chunkStacksSizeVertexHash[site]);
    }
    
    private void verifyPresentEdge(final int site) {
        if (this.chunkStacksEdge[site] != null) {
            return;
        }
        this.chunkStacksEdge[site] = new HashedChunkStack(this.numComponents, this.numPlayers, this.numStates, this.numRotation, this.type, this.hiddenEdgeInfo, this.chunkStacksWhatEdgeHash[site], this.chunkStacksWhoEdgeHash[site], this.chunkStacksStateEdgeHash[site], this.chunkStacksRotationEdgeHash[site], this.chunkStacksSizeEdgeHash[site]);
    }
    
    private void checkPlayableVertex(final State trialState, final int site) {
        if (this.isOccupiedVertex(site)) {
            this.setPlayableVertex(trialState, site, false);
            return;
        }
        final Vertex v = this.container().topology().vertices().get(site);
        for (final Vertex vNbors : v.adjacent()) {
            if (this.isOccupiedVertex(vNbors.index())) {
                this.setPlayableVertex(trialState, site, true);
                return;
            }
        }
        this.setPlayable(trialState, site, false);
    }
    
    public void setPlayableVertex(final State trialState, final int site, final boolean on) {
        this.playableVertex.set(trialState, site, on);
    }
    
    public boolean isOccupiedVertex(final int site) {
        return this.chunkStacksVertex[site] != null && this.chunkStacksVertex[site].what() != 0;
    }
    
    private void checkPlayableEdge(final State trialState, final int site) {
        if (this.isOccupiedEdge(site)) {
            this.setPlayableEdge(trialState, site, false);
            return;
        }
        this.setPlayable(trialState, site, false);
    }
    
    public void setPlayableEdge(final State trialState, final int site, final boolean on) {
        this.playableEdge.set(trialState, site, on);
    }
    
    public boolean isOccupiedEdge(final int site) {
        return this.chunkStacksEdge[site] != null && this.chunkStacksEdge[site].what() != 0;
    }
    
    @Override
    public void setSite(final State trialState, final int site, final int whoVal, final int whatVal, final int countVal, final int stateVal, final int rotationVal, final int valueVal, final SiteType type) {
        if (type == SiteType.Cell) {
            super.setSite(trialState, site, whoVal, whatVal, countVal, stateVal, rotationVal, valueVal, type);
        }
        else if (type == SiteType.Vertex) {
            this.verifyPresentVertex(site);
            final boolean wasEmpty = this.isEmpty(site, SiteType.Vertex);
            if (whoVal != -1) {
                this.chunkStacksVertex[site].setWho(trialState, whoVal);
            }
            if (whatVal != -1) {
                this.chunkStacksVertex[site].setWhat(trialState, whatVal);
            }
            if (stateVal != -1) {
                this.chunkStacksVertex[site].setState(trialState, stateVal);
            }
            if (rotationVal != -1) {
                this.chunkStacksVertex[site].setRotation(trialState, rotationVal);
            }
            final boolean isEmpty = this.isEmpty(site, SiteType.Vertex);
            if (wasEmpty == isEmpty) {
                return;
            }
            if (isEmpty) {
                this.addToEmpty(site, type);
                if (this.playableVertex != null) {
                    this.checkPlayableVertex(trialState, site - this.offset);
                    final Vertex v = this.container().topology().vertices().get(site);
                    for (final Vertex vNbors : v.adjacent()) {
                        this.checkPlayableVertex(trialState, vNbors.index());
                    }
                }
            }
            else {
                this.removeFromEmpty(site, type);
                if (this.playableVertex != null) {
                    this.setPlayableVertex(trialState, site, false);
                    final Vertex v = this.container().topology().vertices().get(site);
                    for (final Vertex vNbors : v.adjacent()) {
                        if (!this.isOccupiedVertex(vNbors.index())) {
                            this.setPlayableVertex(trialState, vNbors.index(), true);
                        }
                    }
                }
            }
        }
        else if (type == SiteType.Edge) {
            this.verifyPresentEdge(site);
            final boolean wasEmpty = this.isEmpty(site, SiteType.Edge);
            if (whoVal != -1) {
                this.chunkStacksEdge[site].setWho(trialState, whoVal);
            }
            if (whatVal != -1) {
                this.chunkStacksEdge[site].setWhat(trialState, whatVal);
            }
            if (stateVal != -1) {
                this.chunkStacksEdge[site].setState(trialState, stateVal);
            }
            if (rotationVal != -1) {
                this.chunkStacksEdge[site].setRotation(trialState, rotationVal);
            }
            final boolean isEmpty = this.isEmpty(site, SiteType.Edge);
            if (wasEmpty == isEmpty) {
                return;
            }
            if (isEmpty) {
                this.addToEmpty(site, type);
                if (this.playableEdge != null) {
                    this.checkPlayableEdge(trialState, site - this.offset);
                }
            }
            else {
                this.removeFromEmpty(site, type);
                if (this.playableEdge != null) {
                    this.setPlayableEdge(trialState, site, false);
                }
            }
        }
    }
    
    @Override
    public void addItemVertex(final State trialState, final int site, final int whatValue, final int whoId, final Game game) {
        this.verifyPresentVertex(site);
        this.chunkStacksVertex[site].incrementSize(trialState);
        this.chunkStacksVertex[site].setWhat(trialState, whatValue);
        this.chunkStacksVertex[site].setWho(trialState, whoId);
    }
    
    @Override
    public void insertVertex(final State trialState, final int site, final int level, final int whatValue, final int whoId, final Game game) {
    }
    
    @Override
    public void addItemVertex(final State trialState, final int site, final int whatValue, final int whoId, final int stateVal, final int rotationVal, final Game game) {
        this.verifyPresentVertex(site);
        this.chunkStacksVertex[site].incrementSize(trialState);
        this.chunkStacksVertex[site].setWhat(trialState, whatValue);
        this.chunkStacksVertex[site].setWho(trialState, whoId);
        this.chunkStacksVertex[site].setState(trialState, stateVal);
        this.chunkStacksVertex[site].setRotation(trialState, stateVal);
    }
    
    @Override
    public void addItemVertex(final State trialState, final int site, final int whatValue, final int whoId, final Game game, final boolean[] hiddenValues, final boolean masked) {
        this.verifyPresentVertex(site);
        this.chunkStacksVertex[site].incrementSize(trialState);
        this.chunkStacksVertex[site].setWhat(trialState, whatValue);
        this.chunkStacksVertex[site].setWho(trialState, whoId);
        if (hiddenValues != null) {
            for (int i = 0; i < hiddenValues.length; ++i) {
                if (hiddenValues[i]) {
                    if (masked) {
                        this.setMaskedVertex(trialState, site, i);
                    }
                    else {
                        this.setInvisibleVertex(trialState, site, i);
                    }
                }
                else {
                    this.setVisibleVertex(trialState, site, i);
                }
            }
        }
    }
    
    @Override
    public void removeStackVertex(final State trialState, final int site) {
        if (this.chunkStacksVertex[site] == null) {
            return;
        }
        trialState.updateStateHash(this.chunkStacksVertex[site].calcHash());
        this.chunkStacksVertex[site] = null;
    }
    
    @Override
    public void addItemEdge(final State trialState, final int site, final int whatValue, final int whoId, final Game game) {
        this.verifyPresentEdge(site);
        this.chunkStacksEdge[site].incrementSize(trialState);
        this.chunkStacksEdge[site].setWhat(trialState, whatValue);
        this.chunkStacksEdge[site].setWho(trialState, whoId);
    }
    
    @Override
    public void insertEdge(final State trialState, final int site, final int level, final int whatValue, final int whoId, final Game game) {
    }
    
    @Override
    public void addItemEdge(final State trialState, final int site, final int whatValue, final int whoId, final int stateVal, final int rotationVal, final Game game) {
        this.verifyPresentEdge(site);
        this.chunkStacksEdge[site].incrementSize(trialState);
        this.chunkStacksEdge[site].setWhat(trialState, whatValue);
        this.chunkStacksEdge[site].setWho(trialState, whoId);
        this.chunkStacksEdge[site].setState(trialState, stateVal);
        this.chunkStacksEdge[site].setRotation(trialState, stateVal);
    }
    
    @Override
    public void addItemEdge(final State trialState, final int site, final int whatValue, final int whoId, final Game game, final boolean[] hiddenValues, final boolean masked) {
        this.verifyPresentEdge(site);
        this.chunkStacksEdge[site].incrementSize(trialState);
        this.chunkStacksEdge[site].setWhat(trialState, whatValue);
        this.chunkStacksEdge[site].setWho(trialState, whoId);
        if (hiddenValues != null) {
            for (int i = 0; i < hiddenValues.length; ++i) {
                if (hiddenValues[i]) {
                    if (masked) {
                        this.setMaskedEdge(trialState, site, i);
                    }
                    else {
                        this.setInvisibleEdge(trialState, site, i);
                    }
                }
                else {
                    this.setVisibleEdge(trialState, site, i);
                }
            }
        }
    }
    
    @Override
    public void removeStackEdge(final State trialState, final int site) {
        if (this.chunkStacksEdge[site] == null) {
            return;
        }
        trialState.updateStateHash(this.chunkStacksEdge[site].calcHash());
        this.chunkStacksEdge[site] = null;
    }
    
    @Override
    public int whoVertex(final int site) {
        if (this.chunkStacksVertex[site] == null) {
            return 0;
        }
        return this.chunkStacksVertex[site].who();
    }
    
    @Override
    public int whoVertex(final int site, final int level) {
        if (this.chunkStacksVertex[site] == null) {
            return 0;
        }
        return this.chunkStacksVertex[site].who(level);
    }
    
    @Override
    public int whatVertex(final int site) {
        if (this.chunkStacksVertex[site] == null) {
            return 0;
        }
        return this.chunkStacksVertex[site].what();
    }
    
    @Override
    public int whatVertex(final int site, final int level) {
        if (this.chunkStacksVertex[site] == null) {
            return 0;
        }
        return this.chunkStacksVertex[site].what(level);
    }
    
    @Override
    public int stateVertex(final int site) {
        if (this.chunkStacksVertex[site] == null) {
            return 0;
        }
        return this.chunkStacksVertex[site].state();
    }
    
    @Override
    public int stateVertex(final int site, final int level) {
        if (this.chunkStacksVertex[site] == null) {
            return 0;
        }
        return this.chunkStacksVertex[site].state(level);
    }
    
    @Override
    public int rotationVertex(final int site) {
        if (this.chunkStacksVertex[site] == null) {
            return 0;
        }
        return this.chunkStacksVertex[site].rotation();
    }
    
    @Override
    public int rotationVertex(final int site, final int level) {
        if (this.chunkStacksVertex[site] == null) {
            return 0;
        }
        return this.chunkStacksVertex[site].rotation(level);
    }
    
    @Override
    public boolean isInvisibleVertex(final int site, final int who) {
        return this.chunkStacksVertex[site] != null && this.chunkStacksVertex[site].isInvisible(who);
    }
    
    @Override
    public boolean isInvisibleVertex(final int site, final int level, final int who) {
        return this.chunkStacksVertex[site] != null && this.chunkStacksVertex[site].isInvisible(who, level);
    }
    
    @Override
    public boolean isVisibleVertex(final int site, final int who) {
        return this.chunkStacksVertex[site] != null && this.chunkStacksVertex[site].isVisible(who);
    }
    
    @Override
    public boolean isVisibleVertex(final int site, final int level, final int who) {
        return this.chunkStacksVertex[site] != null && this.chunkStacksVertex[site].isVisible(who, level);
    }
    
    @Override
    public boolean isMaskedVertex(final int site, final int who) {
        return this.chunkStacksVertex[site] != null && this.chunkStacksVertex[site].isMasked(who);
    }
    
    @Override
    public boolean isMaskedVertex(final int site, final int level, final int who) {
        return this.chunkStacksVertex[site] != null && this.chunkStacksVertex[site].isMasked(who, level);
    }
    
    @Override
    public void setInvisibleVertex(final State trialState, final int site, final int who) {
        if (this.hiddenVertexInfo) {
            this.chunkStacksVertex[site].setInvisible(trialState, who);
        }
    }
    
    @Override
    public void setInvisibleVertex(final State trialState, final int site, final int level, final int who) {
        if (this.hiddenVertexInfo) {
            this.chunkStacksVertex[site].setInvisible(trialState, who, level);
        }
    }
    
    @Override
    public void setMaskedVertex(final State trialState, final int site, final int who) {
        if (this.hiddenVertexInfo) {
            this.chunkStacksVertex[site].setMasked(trialState, who);
        }
    }
    
    @Override
    public void setMaskedVertex(final State trialState, final int site, final int level, final int who) {
        if (this.hiddenVertexInfo) {
            this.chunkStacksVertex[site].setMasked(trialState, who, level);
        }
    }
    
    @Override
    public void setVisibleVertex(final State trialState, final int site, final int who) {
        if (this.hiddenVertexInfo) {
            this.chunkStacksVertex[site].setVisible(trialState, who);
        }
    }
    
    @Override
    public void setVisibleVertex(final State trialState, final int site, final int level, final int who) {
        if (this.hiddenVertexInfo) {
            this.chunkStacksVertex[site].setVisible(trialState, who, level);
        }
    }
    
    @Override
    public int sizeStackVertex(final int site) {
        if (this.chunkStacksVertex[site] == null) {
            return 0;
        }
        return this.chunkStacksVertex[site].size();
    }
    
    @Override
    public int whoEdge(final int site) {
        if (this.chunkStacksEdge[site] == null) {
            return 0;
        }
        return this.chunkStacksEdge[site].who();
    }
    
    @Override
    public int whoEdge(final int site, final int level) {
        if (this.chunkStacksEdge[site] == null) {
            return 0;
        }
        return this.chunkStacksEdge[site].who(level);
    }
    
    @Override
    public int whatEdge(final int site) {
        if (this.chunkStacksEdge[site] == null) {
            return 0;
        }
        return this.chunkStacksEdge[site].what();
    }
    
    @Override
    public int whatEdge(final int site, final int level) {
        if (this.chunkStacksEdge[site] == null) {
            return 0;
        }
        return this.chunkStacksEdge[site].what(level);
    }
    
    @Override
    public int stateEdge(final int site) {
        if (this.chunkStacksEdge[site] == null) {
            return 0;
        }
        return this.chunkStacksEdge[site].state();
    }
    
    @Override
    public int stateEdge(final int site, final int level) {
        if (this.chunkStacksEdge[site] == null) {
            return 0;
        }
        return this.chunkStacksEdge[site].state(level);
    }
    
    @Override
    public int rotationEdge(final int site) {
        if (this.chunkStacksEdge[site] == null) {
            return 0;
        }
        return this.chunkStacksEdge[site].rotation();
    }
    
    @Override
    public int rotationEdge(final int site, final int level) {
        if (this.chunkStacksEdge[site] == null) {
            return 0;
        }
        return this.chunkStacksEdge[site].rotation(level);
    }
    
    @Override
    public boolean isInvisibleEdge(final int site, final int who) {
        return this.chunkStacksEdge[site] != null && this.chunkStacksEdge[site].isInvisible(who);
    }
    
    @Override
    public boolean isInvisibleEdge(final int site, final int level, final int who) {
        return this.chunkStacksEdge[site] != null && this.chunkStacksEdge[site].isInvisible(who, level);
    }
    
    @Override
    public boolean isVisibleEdge(final int site, final int who) {
        return this.chunkStacksEdge[site] != null && this.chunkStacksEdge[site].isVisible(who);
    }
    
    @Override
    public boolean isVisibleEdge(final int site, final int level, final int who) {
        return this.chunkStacksEdge[site] != null && this.chunkStacksEdge[site].isVisible(who, level);
    }
    
    @Override
    public boolean isMaskedEdge(final int site, final int who) {
        return this.chunkStacksEdge[site] != null && this.chunkStacksEdge[site].isMasked(who);
    }
    
    @Override
    public boolean isMaskedEdge(final int site, final int level, final int who) {
        return this.chunkStacksEdge[site] != null && this.chunkStacksEdge[site].isMasked(who, level);
    }
    
    @Override
    public void setInvisibleEdge(final State trialState, final int site, final int who) {
        if (this.hiddenEdgeInfo) {
            this.chunkStacksEdge[site].setInvisible(trialState, who);
        }
    }
    
    @Override
    public void setInvisibleEdge(final State trialState, final int site, final int level, final int who) {
        if (this.hiddenEdgeInfo) {
            this.chunkStacksEdge[site].setInvisible(trialState, who, level);
        }
    }
    
    @Override
    public void setMaskedEdge(final State trialState, final int site, final int who) {
        if (this.hiddenEdgeInfo) {
            this.chunkStacksEdge[site].setMasked(trialState, who);
        }
    }
    
    @Override
    public void setMaskedEdge(final State trialState, final int site, final int level, final int who) {
        if (this.hiddenEdgeInfo) {
            this.chunkStacksEdge[site].setMasked(trialState, who, level);
        }
    }
    
    @Override
    public void setVisibleEdge(final State trialState, final int site, final int who) {
        if (this.hiddenEdgeInfo) {
            this.chunkStacksEdge[site].setVisible(trialState, who);
        }
    }
    
    @Override
    public void setVisibleEdge(final State trialState, final int site, final int level, final int who) {
        if (this.hiddenEdgeInfo) {
            this.chunkStacksEdge[site].setVisible(trialState, who, level);
        }
    }
    
    @Override
    public int sizeStackEdge(final int site) {
        if (this.chunkStacksEdge[site] == null) {
            return 0;
        }
        return this.chunkStacksEdge[site].size();
    }
    
    @Override
    public int remove(final State state, final int site, final SiteType graphElement) {
        if (graphElement == SiteType.Cell) {
            super.remove(state, site, graphElement);
        }
        else if (graphElement == SiteType.Vertex) {
            if (this.chunkStacksVertex[site] == null) {
                return 0;
            }
            final int componentRemove = this.chunkStacksVertex[site].what();
            this.chunkStacksVertex[site].setWhat(state, 0);
            this.chunkStacksVertex[site].setWho(state, 0);
            this.chunkStacksVertex[site].setState(state, 0);
            this.chunkStacksVertex[site].setRotation(state, 0);
            this.chunkStacksVertex[site].decrementSize(state);
            return componentRemove;
        }
        if (this.chunkStacksEdge[site] == null) {
            return 0;
        }
        final int componentRemove = this.chunkStacksEdge[site].what();
        this.chunkStacksEdge[site].setWhat(state, 0);
        this.chunkStacksEdge[site].setWho(state, 0);
        this.chunkStacksEdge[site].setState(state, 0);
        this.chunkStacksEdge[site].setRotation(state, 0);
        this.chunkStacksEdge[site].decrementSize(state);
        return componentRemove;
    }
    
    @Override
    public int remove(final State state, final int site, final int level, final SiteType graphElement) {
        if (graphElement == SiteType.Cell) {
            super.remove(state, site, level, graphElement);
        }
        else if (graphElement == SiteType.Vertex) {
            if (this.chunkStacksVertex[site] == null) {
                return 0;
            }
            final int componentRemove = this.chunkStacksVertex[site].what(level);
            for (int i = level; i < this.sizeStack(site, graphElement) - 1; ++i) {
                this.chunkStacksVertex[site].setWhat(state, this.chunkStacksVertex[site].what(i + 1), i);
                this.chunkStacksVertex[site].setWho(state, this.chunkStacksVertex[site].who(i + 1), i);
                this.chunkStacksVertex[site].setState(state, this.chunkStacksVertex[site].state(i + 1), i);
                this.chunkStacksVertex[site].setRotation(state, this.chunkStacksVertex[site].rotation(i + 1), i);
            }
            this.chunkStacksVertex[site].setWhat(state, 0);
            this.chunkStacksVertex[site].setWho(state, 0);
            this.chunkStacksVertex[site].decrementSize(state);
            return componentRemove;
        }
        if (this.chunkStacksEdge[site] == null) {
            return 0;
        }
        final int componentRemove = this.chunkStacksEdge[site].what(level);
        for (int i = level; i < this.sizeStack(site, graphElement) - 1; ++i) {
            this.chunkStacksEdge[site].setWhat(state, this.chunkStacksEdge[site].what(i + 1), i);
            this.chunkStacksEdge[site].setWho(state, this.chunkStacksEdge[site].who(i + 1), i);
            this.chunkStacksEdge[site].setState(state, this.chunkStacksEdge[site].state(i + 1), i);
            this.chunkStacksEdge[site].setRotation(state, this.chunkStacksEdge[site].rotation(i + 1), i);
        }
        this.chunkStacksEdge[site].setWhat(state, 0);
        this.chunkStacksEdge[site].setWho(state, 0);
        this.chunkStacksEdge[site].decrementSize(state);
        return componentRemove;
    }
    
    @Override
    public void addToEmpty(final int site, final SiteType graphType) {
        if (graphType == SiteType.Cell) {
            this.empty.add(site - this.offset);
        }
        else if (graphType == SiteType.Edge) {
            this.emptyEdge.add(site);
        }
        else {
            this.emptyVertex.add(site);
        }
    }
    
    @Override
    public void removeFromEmpty(final int site, final SiteType graphType) {
        if (graphType == SiteType.Cell) {
            this.empty.remove(site - this.offset);
        }
        else if (graphType == SiteType.Edge) {
            this.emptyEdge.remove(site);
        }
        else {
            this.emptyVertex.remove(site);
        }
    }
    
    @Override
    public ContainerStateStacks deepClone() {
        return new ContainerGraphStateStacks(this);
    }
    
    @Override
    public Region emptyRegion(final SiteType graphType) {
        if (graphType == SiteType.Cell) {
            return this.empty;
        }
        if (graphType == SiteType.Edge) {
            return this.emptyEdge;
        }
        return this.emptyVertex;
    }
}
