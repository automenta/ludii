// 
// Decompiled by Procyon v0.5.36
// 

package util.state.containerState;

import game.Game;
import game.equipment.container.Container;
import game.types.board.SiteType;
import game.util.equipment.Region;
import collections.ChunkSet;
import util.state.State;
import util.zhash.HashedChunkSet;
import util.zhash.ZobristHashGenerator;

public class ContainerGraphState extends ContainerFlatState
{
    private static final long serialVersionUID = 1L;
    protected final HashedChunkSet whoEdge;
    protected final HashedChunkSet whoVertex;
    protected final HashedChunkSet whatEdge;
    protected final HashedChunkSet whatVertex;
    protected final HashedChunkSet countEdge;
    protected final HashedChunkSet countVertex;
    protected final HashedChunkSet stateEdge;
    protected final HashedChunkSet stateVertex;
    protected final HashedChunkSet rotationEdge;
    protected final HashedChunkSet rotationVertex;
    protected HashedChunkSet[] hiddenEdge;
    protected HashedChunkSet[] hiddenVertex;
    private final Region emptyEdge;
    private final Region emptyVertex;
    
    public ContainerGraphState(final ZobristHashGenerator generator, final Game game, final Container container, final int maxWhatVal, final int maxStateVal, final int maxCountVal, final int maxRotationVal) {
        super(generator, game, container, container.numSites(), maxWhatVal, maxStateVal, maxCountVal, maxRotationVal);
        final int numEdges = game.board().topology().edges().size();
        final int numVertices = game.board().topology().vertices().size();
        final int numPlayers = game.players().count();
        if ((game.gameFlags() & 0x8L) == 0x0L) {
            this.hiddenEdge = null;
            this.hiddenVertex = null;
        }
        else {
            final int numHiddenState = 3;
            if (!game.isVertexGame()) {}
            this.hiddenVertex = new HashedChunkSet[numPlayers + 1];
            for (int i = 1; i < numPlayers + 1; ++i) {
                this.hiddenVertex[i] = new HashedChunkSet(generator, 3, numVertices);
            }
            if (game.isEdgeGame()) {
                this.hiddenEdge = new HashedChunkSet[numPlayers + 1];
                for (int i = 1; i < numPlayers + 1; ++i) {
                    this.hiddenEdge[i] = new HashedChunkSet(generator, 3, numEdges);
                }
            }
            else {
                this.hiddenEdge = null;
            }
        }
        if (!game.isVertexGame()) {}
        this.whoVertex = new HashedChunkSet(generator, numPlayers + 1, numVertices);
        this.whatVertex = ((maxWhatVal > 0) ? new HashedChunkSet(generator, maxWhatVal, numVertices) : null);
        this.countVertex = ((maxCountVal > 0) ? new HashedChunkSet(generator, maxCountVal, numVertices) : null);
        this.stateVertex = ((maxStateVal > 0) ? new HashedChunkSet(generator, maxStateVal, numVertices) : null);
        this.rotationVertex = ((maxRotationVal > 0) ? new HashedChunkSet(generator, maxRotationVal, numVertices) : null);
        this.emptyVertex = new Region(numVertices);
        if (!game.isEdgeGame()) {}
        this.whoEdge = new HashedChunkSet(generator, numPlayers + 1, numEdges);
        this.whatEdge = ((maxWhatVal > 0) ? new HashedChunkSet(generator, maxWhatVal, numEdges) : null);
        this.rotationEdge = ((maxRotationVal > 0) ? new HashedChunkSet(generator, maxRotationVal, numEdges) : null);
        this.countEdge = ((maxCountVal > 0) ? new HashedChunkSet(generator, maxCountVal, numEdges) : null);
        this.stateEdge = ((maxStateVal > 0) ? new HashedChunkSet(generator, maxStateVal, numEdges) : null);
        this.emptyEdge = new Region(numEdges);
    }
    
    private ContainerGraphState(final ContainerGraphState other) {
        super(other);
        if (other.hiddenEdge != null) {
            this.hiddenEdge = new HashedChunkSet[other.hiddenEdge.length];
            for (int i = 1; i < other.hiddenEdge.length; ++i) {
                this.hiddenEdge[i] = ((other.hiddenEdge[i] == null) ? null : other.hiddenEdge[i].clone());
            }
        }
        if (other.hiddenVertex != null) {
            this.hiddenVertex = new HashedChunkSet[other.hiddenVertex.length];
            for (int i = 1; i < other.hiddenVertex.length; ++i) {
                this.hiddenVertex[i] = ((other.hiddenVertex[i] == null) ? null : other.hiddenVertex[i].clone());
            }
        }
        this.whoEdge = ((other.whoEdge == null) ? null : other.whoEdge.clone());
        this.whoVertex = ((other.whoVertex == null) ? null : other.whoVertex.clone());
        this.whatEdge = ((other.whatEdge == null) ? null : other.whatEdge.clone());
        this.whatVertex = ((other.whatVertex == null) ? null : other.whatVertex.clone());
        this.countEdge = ((other.countEdge == null) ? null : other.countEdge.clone());
        this.countVertex = ((other.countVertex == null) ? null : other.countVertex.clone());
        this.stateEdge = ((other.stateEdge == null) ? null : other.stateEdge.clone());
        this.stateVertex = ((other.stateVertex == null) ? null : other.stateVertex.clone());
        this.rotationEdge = ((other.rotationEdge == null) ? null : other.rotationEdge.clone());
        this.rotationVertex = ((other.rotationVertex == null) ? null : other.rotationVertex.clone());
        this.emptyEdge = ((other.emptyEdge == null) ? null : new Region(other.emptyEdge));
        this.emptyVertex = ((other.emptyVertex == null) ? null : new Region(other.emptyVertex));
    }
    
    @Override
    public ContainerGraphState deepClone() {
        return new ContainerGraphState(this);
    }
    
    @Override
    public void reset(final State trialState, final Game game) {
        final int numEdges = (game.board().defaultSite() == SiteType.Cell) ? game.board().topology().edges().size() : game.board().topology().edges().size();
        final int numVertices = (game.board().defaultSite() == SiteType.Cell) ? game.board().topology().vertices().size() : game.board().topology().vertices().size();
        super.reset(trialState, game);
        if (this.whoEdge != null) {
            this.whoEdge.clear(trialState);
        }
        if (this.whoVertex != null) {
            this.whoVertex.clear(trialState);
        }
        if (this.whatEdge != null) {
            this.whatEdge.clear(trialState);
        }
        if (this.whatVertex != null) {
            this.whatVertex.clear(trialState);
        }
        if (this.countEdge != null) {
            this.countEdge.clear(trialState);
        }
        if (this.countVertex != null) {
            this.countVertex.clear(trialState);
        }
        if (this.stateEdge != null) {
            this.stateEdge.clear(trialState);
        }
        if (this.stateVertex != null) {
            this.stateVertex.clear(trialState);
        }
        if (this.rotationEdge != null) {
            this.rotationEdge.clear(trialState);
        }
        if (this.rotationVertex != null) {
            this.rotationVertex.clear(trialState);
        }
        if (this.emptyEdge != null) {
            this.emptyEdge.set(numEdges);
        }
        if (this.emptyVertex != null) {
            this.emptyVertex.set(numVertices);
        }
    }
    
    @Override
    protected long calcCanonicalHash(final int[] siteRemap, final int[] edgeRemap, final int[] vertexRemap, final int[] playerRemap, final boolean whoOnly) {
        long hash = 0L;
        if (siteRemap != null && siteRemap.length > 0) {
            if (this.who != null) {
                hash ^= this.who.calculateHashAfterRemap(siteRemap, playerRemap);
            }
            if (!whoOnly) {
                if (this.what != null) {
                    hash ^= this.what.calculateHashAfterRemap(siteRemap, null);
                }
                if (this.playable != null) {
                    hash ^= this.playable.calculateHashAfterRemap(siteRemap, false);
                }
                if (this.count != null) {
                    hash ^= this.count.calculateHashAfterRemap(siteRemap, null);
                }
                if (this.state != null) {
                    hash ^= this.state.calculateHashAfterRemap(siteRemap, null);
                }
                if (this.rotation != null) {
                    hash ^= this.rotation.calculateHashAfterRemap(siteRemap, null);
                }
                if (this.value != null) {
                    hash ^= this.value.calculateHashAfterRemap(siteRemap, null);
                }
                if (this.hidden != null) {
                    for (int whom = 1; whom < this.hidden.length; ++whom) {
                        hash ^= this.hidden[whom].calculateHashAfterRemap(siteRemap, playerRemap);
                    }
                }
            }
        }
        if (edgeRemap != null && edgeRemap.length > 0) {
            if (this.whoEdge != null) {
                hash ^= this.whoEdge.calculateHashAfterRemap(edgeRemap, playerRemap);
            }
            if (!whoOnly) {
                if (this.whatEdge != null) {
                    hash ^= this.whatEdge.calculateHashAfterRemap(edgeRemap, null);
                }
                if (this.countEdge != null) {
                    hash ^= this.countEdge.calculateHashAfterRemap(edgeRemap, null);
                }
                if (this.stateEdge != null) {
                    hash ^= this.stateEdge.calculateHashAfterRemap(edgeRemap, null);
                }
                if (this.rotationEdge != null) {
                    hash ^= this.rotationEdge.calculateHashAfterRemap(edgeRemap, null);
                }
                if (this.hiddenEdge != null) {
                    for (int whom = 1; whom < this.hidden.length; ++whom) {
                        hash ^= this.hiddenEdge[whom].calculateHashAfterRemap(edgeRemap, null);
                    }
                }
            }
        }
        if (vertexRemap != null && vertexRemap.length > 0) {
            if (this.whoVertex != null) {
                hash ^= this.whoVertex.calculateHashAfterRemap(vertexRemap, playerRemap);
            }
            if (!whoOnly) {
                if (this.whatVertex != null) {
                    hash ^= this.whatVertex.calculateHashAfterRemap(vertexRemap, null);
                }
                if (this.countVertex != null) {
                    hash ^= this.countVertex.calculateHashAfterRemap(vertexRemap, null);
                }
                if (this.stateVertex != null) {
                    hash ^= this.stateVertex.calculateHashAfterRemap(vertexRemap, null);
                }
                if (this.rotationVertex != null) {
                    hash ^= this.rotationVertex.calculateHashAfterRemap(vertexRemap, null);
                }
                if (this.hiddenVertex != null) {
                    for (int whom = 1; whom < this.hidden.length; ++whom) {
                        hash ^= this.hiddenVertex[whom].calculateHashAfterRemap(vertexRemap, null);
                    }
                }
            }
        }
        return hash;
    }
    
    @Override
    public int whoEdge(final int edge) {
        return this.whoEdge.getChunk(edge);
    }
    
    @Override
    public int whoVertex(final int vertex) {
        return this.whoVertex.getChunk(vertex);
    }
    
    @Override
    public int whatEdge(final int edge) {
        if (this.whatEdge == null) {
            return this.whoEdge(edge);
        }
        return this.whatEdge.getChunk(edge);
    }
    
    @Override
    public int stateEdge(final int edge) {
        if (this.stateEdge == null) {
            return 0;
        }
        return this.stateEdge.getChunk(edge);
    }
    
    @Override
    public int rotationEdge(final int edge) {
        if (this.rotationEdge == null) {
            return 0;
        }
        return this.rotationEdge.getChunk(edge);
    }
    
    @Override
    public int countEdge(final int edge) {
        if (this.countEdge != null) {
            return this.countEdge.getChunk(edge);
        }
        if (this.whoEdge.getChunk(edge) != 0 || (this.whatEdge != null && this.whatEdge.getChunk(edge) != 0)) {
            return 1;
        }
        return 0;
    }
    
    @Override
    public int whatVertex(final int vertex) {
        if (this.whatVertex == null) {
            return this.whoVertex(vertex);
        }
        return this.whatVertex.getChunk(vertex);
    }
    
    @Override
    public int stateVertex(final int vertex) {
        if (this.stateVertex == null) {
            return 0;
        }
        return this.stateVertex.getChunk(vertex);
    }
    
    @Override
    public int rotationVertex(final int vertex) {
        if (this.rotationVertex == null) {
            return 0;
        }
        return this.rotationVertex.getChunk(vertex);
    }
    
    @Override
    public int countVertex(final int vertex) {
        if (this.countVertex != null) {
            return this.countVertex.getChunk(vertex);
        }
        if (this.whoVertex.getChunk(vertex) != 0 || (this.whatVertex != null && this.whatVertex.getChunk(vertex) != 0)) {
            return 1;
        }
        return 0;
    }
    
    public boolean isOccupied(final int site, final SiteType type) {
        if (type == SiteType.Cell) {
            return this.countCell(site) != 0;
        }
        if (type == SiteType.Edge) {
            return this.countEdge(site) != 0;
        }
        return this.countVertex(site) != 0;
    }
    
    @Override
    public void addToEmpty(final int site, final SiteType type) {
        if (type == SiteType.Cell) {
            this.empty.add(site - this.offset);
        }
        else if (type == SiteType.Edge) {
            this.emptyEdge.add(site);
        }
        else {
            this.emptyVertex.add(site);
        }
    }
    
    @Override
    public void removeFromEmpty(final int site, final SiteType type) {
        if (type == SiteType.Cell) {
            this.empty.remove(site - this.offset);
        }
        else if (type == SiteType.Edge) {
            this.emptyEdge.remove(site);
        }
        else {
            this.emptyVertex.remove(site);
        }
    }
    
    @Override
    public void setSite(final State trialState, final int site, final int whoVal, final int whatVal, final int countVal, final int stateVal, final int rotationVal, final int valueVal, final SiteType type) {
        if (type == SiteType.Cell || this.container().index() != 0) {
            super.setSite(trialState, site, whoVal, whatVal, countVal, stateVal, rotationVal, valueVal, type);
        }
        else if (type == SiteType.Edge) {
            final boolean wasEmpty = !this.isOccupied(site, type);
            if (whoVal != -1) {
                this.whoEdge.setChunk(trialState, site, whoVal);
            }
            if (whatVal != -1) {
                this.defaultIfNull(this.whatEdge).setChunk(trialState, site, whatVal);
            }
            if (countVal != -1) {
                if (this.countEdge != null) {
                    this.countEdge.setChunk(trialState, site, Math.max(countVal, 0));
                }
                else if (this.countEdge == null && countVal > 1) {
                    throw new UnsupportedOperationException("This game does not support counts, but a count > 1 has been set. countVal=" + countVal);
                }
            }
            if (stateVal != -1) {
                if (this.stateEdge != null) {
                    this.stateEdge.setChunk(trialState, site, stateVal);
                }
                else if (stateVal != 0) {
                    throw new UnsupportedOperationException("This game does not support states, but a state has been set. stateVal=" + stateVal);
                }
            }
            if (rotationVal != -1) {
                if (this.rotationEdge != null) {
                    this.rotationEdge.setChunk(trialState, site, rotationVal);
                }
                else if (rotationVal != 0) {
                    throw new UnsupportedOperationException("This game does not support rotations, but a rotation has been set. rotationVal=" + rotationVal);
                }
            }
            final boolean isEmpty = !this.isOccupied(site, type);
            if (wasEmpty == isEmpty) {
                return;
            }
            if (isEmpty) {
                this.addToEmpty(site, type);
            }
            else {
                this.removeFromEmpty(site, type);
            }
        }
        else if (type == SiteType.Vertex) {
            final boolean wasEmpty = !this.isOccupied(site, type);
            if (whoVal != -1) {
                this.whoVertex.setChunk(trialState, site, whoVal);
            }
            if (whatVal != -1) {
                this.defaultIfNull(this.whatVertex).setChunk(trialState, site, whatVal);
            }
            if (countVal != -1) {
                if (this.countVertex != null) {
                    this.countVertex.setChunk(trialState, site, Math.max(countVal, 0));
                }
                else if (this.countVertex == null && countVal > 1) {
                    throw new UnsupportedOperationException("This game does not support counts, but a count > 1 has been set. countVal=" + countVal);
                }
            }
            if (stateVal != -1) {
                if (this.stateVertex != null) {
                    this.stateVertex.setChunk(trialState, site, stateVal);
                }
                else if (stateVal != 0) {
                    throw new UnsupportedOperationException("This game does not support states, but a state has been set. stateVal=" + stateVal);
                }
            }
            if (rotationVal != -1) {
                if (this.rotationVertex != null) {
                    this.rotationVertex.setChunk(trialState, site, rotationVal);
                }
                else if (rotationVal != 0) {
                    throw new UnsupportedOperationException("This game does not support rotations, but a rotation has been set. rotationVal=" + rotationVal);
                }
            }
            final boolean isEmpty = !this.isOccupied(site, type);
            if (wasEmpty == isEmpty) {
                return;
            }
            if (isEmpty) {
                this.addToEmpty(site, type);
            }
            else {
                this.removeFromEmpty(site, type);
            }
        }
    }
    
    @Override
    public Region emptyRegion(final SiteType type) {
        if (type == SiteType.Cell) {
            return this.empty;
        }
        if (type == SiteType.Edge) {
            return this.emptyEdge;
        }
        return this.emptyVertex;
    }
    
    @Override
    public boolean isEmptyVertex(final int vertex) {
        return this.emptyVertex.contains(vertex - this.offset);
    }
    
    @Override
    public boolean isEmptyEdge(final int edge) {
        return this.emptyEdge.contains(edge - this.offset);
    }
    
    @Override
    public int whoVertex(final int site, final int level) {
        return this.whoVertex(site);
    }
    
    @Override
    public int whatVertex(final int site, final int level) {
        return this.whatVertex(site);
    }
    
    @Override
    public int stateVertex(final int site, final int level) {
        return this.stateVertex(site);
    }
    
    @Override
    public int rotationVertex(final int site, final int level) {
        return this.rotationVertex(site);
    }
    
    @Override
    public boolean isInvisibleVertex(final int site, final int whoId) {
        return this.hiddenVertex != null && this.hiddenVertex[whoId].getChunk(site) == 2;
    }
    
    @Override
    public boolean isMaskedVertex(final int site, final int whoH) {
        return this.hiddenVertex != null && this.hiddenVertex[whoH].getChunk(site) == 1;
    }
    
    @Override
    public boolean isVisibleVertex(final int site, final int whoH) {
        return this.hiddenVertex == null || this.hiddenVertex[whoH].getChunk(site - this.offset) == 0;
    }
    
    @Override
    public boolean isInvisibleVertex(final int site, final int level, final int whoId) {
        return this.isInvisibleVertex(site, whoId);
    }
    
    @Override
    public boolean isVisibleVertex(final int site, final int level, final int whoId) {
        return this.isVisibleVertex(site, whoId);
    }
    
    @Override
    public boolean isMaskedVertex(final int site, final int level, final int whoId) {
        return this.isMaskedVertex(site, whoId);
    }
    
    @Override
    public void setInvisibleVertex(final State trialState, final int site, final int who) {
        if (this.hiddenVertex == null) {
            throw new RuntimeException("Tried to set invisible information a vertex in game with no hidden state.");
        }
        this.hiddenVertex[who].setChunk(trialState, site, 2);
    }
    
    @Override
    public void setVisibleVertex(final State trialState, final int site, final int who) {
        if (this.hiddenVertex == null) {
            throw new RuntimeException("Tried to set visible information a vertex in game with no hidden state.");
        }
        this.hiddenVertex[who].setChunk(trialState, site, 0);
    }
    
    @Override
    public void setMaskedVertex(final State trialState, final int site, final int who) {
        if (this.hiddenVertex == null) {
            throw new RuntimeException("Tried to set masked information a vertex in game with no hidden state.");
        }
        this.hiddenVertex[who].setChunk(trialState, site, 1);
    }
    
    @Override
    public void setInvisibleVertex(final State trialState, final int site, final int level, final int who) {
        this.setInvisibleVertex(trialState, site, who);
    }
    
    @Override
    public void setMaskedVertex(final State trialState, final int site, final int level, final int who) {
        this.setMaskedVertex(trialState, site, who);
    }
    
    @Override
    public void setVisibleVertex(final State trialState, final int site, final int level, final int who) {
        this.setVisibleVertex(trialState, site, who);
    }
    
    @Override
    public int whoEdge(final int site, final int level) {
        return this.whoEdge(site);
    }
    
    @Override
    public int whatEdge(final int site, final int level) {
        return this.whatEdge(site);
    }
    
    @Override
    public int stateEdge(final int site, final int level) {
        return this.stateEdge(site);
    }
    
    @Override
    public int rotationEdge(final int site, final int level) {
        return this.rotationEdge(site);
    }
    
    @Override
    public boolean isInvisibleEdge(final int site, final int whoId) {
        return this.hiddenEdge != null && this.hiddenEdge[whoId].getChunk(site) == 2;
    }
    
    @Override
    public boolean isMaskedEdge(final int site, final int whoH) {
        return this.hiddenEdge != null && this.hiddenEdge[whoH].getChunk(site) == 1;
    }
    
    @Override
    public boolean isVisibleEdge(final int site, final int whoH) {
        return this.hiddenEdge == null || this.hiddenEdge[whoH].getChunk(site - this.offset) == 0;
    }
    
    @Override
    public boolean isInvisibleEdge(final int site, final int level, final int whoId) {
        return this.isInvisibleEdge(site, whoId);
    }
    
    @Override
    public boolean isVisibleEdge(final int site, final int level, final int whoId) {
        return this.isVisibleEdge(site, whoId);
    }
    
    @Override
    public boolean isMaskedEdge(final int site, final int level, final int whoId) {
        return this.isMaskedEdge(site, whoId);
    }
    
    @Override
    public void setInvisibleEdge(final State trialState, final int site, final int who) {
        if (this.hiddenEdge == null) {
            throw new RuntimeException("Tried to set invisible information a edge in game with no hidden state.");
        }
        this.hiddenEdge[who].setChunk(trialState, site, 2);
    }
    
    @Override
    public void setVisibleEdge(final State trialState, final int site, final int who) {
        if (this.hiddenEdge == null) {
            throw new RuntimeException("Tried to set visible information a edge in game with no hidden state.");
        }
        this.hiddenEdge[who].setChunk(trialState, site, 0);
    }
    
    @Override
    public void setMaskedEdge(final State trialState, final int site, final int who) {
        if (this.hiddenEdge == null) {
            throw new RuntimeException("Tried to set masked information a edge in game with no hidden state.");
        }
        this.hiddenEdge[who].setChunk(trialState, site, 1);
    }
    
    @Override
    public void setInvisibleEdge(final State trialState, final int site, final int level, final int who) {
        this.setInvisibleEdge(trialState, site, who);
    }
    
    @Override
    public void setMaskedEdge(final State trialState, final int site, final int level, final int who) {
        this.setMaskedEdge(trialState, site, who);
    }
    
    @Override
    public void setVisibleEdge(final State trialState, final int site, final int level, final int who) {
        this.setVisibleEdge(trialState, site, who);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ContainerState type = " + this.getClass() + "\n");
        if (this.empty != null) {
            sb.append("emptyVertex = " + this.empty.bitSet().toChunkString() + "\n");
        }
        if (this.emptyEdge != null) {
            sb.append("emptyEdge = " + this.emptyEdge.bitSet().toChunkString() + "\n");
        }
        if (this.emptyVertex != null) {
            sb.append("emptyVertex = " + this.emptyVertex.bitSet().toChunkString() + "\n");
        }
        if (this.who != null) {
            sb.append("Who = " + this.who.internalStateCopy().toChunkString() + "\n");
        }
        if (this.whoEdge != null) {
            sb.append("whoEdge = " + this.whoEdge.internalStateCopy().toChunkString() + "\n");
        }
        if (this.whoVertex != null) {
            sb.append("whoVertex = " + this.whoVertex.internalStateCopy().toChunkString() + "\n");
        }
        if (this.what != null) {
            sb.append("What" + this.what.internalStateCopy().toChunkString() + "\n");
        }
        if (this.whatEdge != null) {
            sb.append("whatEdge = " + this.whatEdge.internalStateCopy().toChunkString() + "\n");
        }
        if (this.whatVertex != null) {
            sb.append("whatVertex = " + this.whatVertex.internalStateCopy().toChunkString() + "\n");
        }
        if (this.state != null) {
            sb.append("State = " + this.state.internalStateCopy().toChunkString() + "\n");
        }
        if (this.rotation != null) {
            sb.append("Rotation = " + this.rotation.internalStateCopy().toChunkString() + "\n");
        }
        if (this.value != null) {
            sb.append("value = " + this.value.internalStateCopy().toChunkString() + "\n");
        }
        if (this.count != null) {
            sb.append("Count = " + this.count.internalStateCopy().toChunkString() + "\n");
        }
        if (this.playable != null) {
            sb.append("Playable = " + this.playable.internalStateCopy().toString() + "\n");
        }
        if (this.hidden != null) {
            for (int i = 1; i < this.hidden.length; ++i) {
                sb.append("Hidden for  player " + i + " = " + this.hidden[i].internalStateCopy().toChunkString() + "\n");
            }
        }
        return sb.toString();
    }
    
    @Override
    public ChunkSet emptyChunkSetVertex() {
        return this.emptyVertex.bitSet();
    }
    
    @Override
    public ChunkSet emptyChunkSetEdge() {
        return this.emptyEdge.bitSet();
    }
    
    @Override
    public int numChunksWhoVertex() {
        return this.whoVertex.numChunks();
    }
    
    @Override
    public int numChunksWhoEdge() {
        return this.whoEdge.numChunks();
    }
    
    @Override
    public int chunkSizeWhoVertex() {
        return this.whoVertex.chunkSize();
    }
    
    @Override
    public int chunkSizeWhoEdge() {
        return this.whoEdge.chunkSize();
    }
    
    @Override
    public int numChunksWhatVertex() {
        return (this.whatVertex != null) ? this.whatVertex.numChunks() : this.whoVertex.numChunks();
    }
    
    @Override
    public int numChunksWhatEdge() {
        return (this.whatEdge != null) ? this.whatEdge.numChunks() : this.whoEdge.numChunks();
    }
    
    @Override
    public int chunkSizeWhatVertex() {
        return (this.whatVertex != null) ? this.whatVertex.chunkSize() : this.whoVertex.chunkSize();
    }
    
    @Override
    public int chunkSizeWhatEdge() {
        return (this.whatEdge != null) ? this.whatEdge.chunkSize() : this.whoEdge.chunkSize();
    }
    
    @Override
    public boolean matchesWhoVertex(final ChunkSet mask, final ChunkSet pattern) {
        return this.whoVertex.matches(mask, pattern);
    }
    
    @Override
    public boolean matchesWhoEdge(final ChunkSet mask, final ChunkSet pattern) {
        return this.whoEdge.matches(mask, pattern);
    }
    
    @Override
    public boolean violatesNotWhoVertex(final ChunkSet mask, final ChunkSet pattern) {
        return this.whoVertex.violatesNot(mask, pattern);
    }
    
    @Override
    public boolean violatesNotWhoEdge(final ChunkSet mask, final ChunkSet pattern) {
        return this.whoEdge.violatesNot(mask, pattern);
    }
    
    @Override
    public boolean violatesNotWhoVertex(final ChunkSet mask, final ChunkSet pattern, final int startWord) {
        return this.whoVertex.violatesNot(mask, pattern, startWord);
    }
    
    @Override
    public boolean violatesNotWhoEdge(final ChunkSet mask, final ChunkSet pattern, final int startWord) {
        return this.whoEdge.violatesNot(mask, pattern, startWord);
    }
    
    @Override
    public boolean matchesWhatVertex(final ChunkSet mask, final ChunkSet pattern) {
        return (this.whatVertex != null) ? this.whatVertex.matches(mask, pattern) : this.whoVertex.matches(mask, pattern);
    }
    
    @Override
    public boolean matchesWhatEdge(final ChunkSet mask, final ChunkSet pattern) {
        return (this.whatEdge != null) ? this.whatEdge.matches(mask, pattern) : this.whoEdge.matches(mask, pattern);
    }
    
    @Override
    public boolean violatesNotWhatVertex(final ChunkSet mask, final ChunkSet pattern) {
        return (this.whatVertex != null) ? this.whatVertex.violatesNot(mask, pattern) : this.whoVertex.violatesNot(mask, pattern);
    }
    
    @Override
    public boolean violatesNotWhatEdge(final ChunkSet mask, final ChunkSet pattern) {
        return (this.whatEdge != null) ? this.whatEdge.violatesNot(mask, pattern) : this.whoEdge.violatesNot(mask, pattern);
    }
    
    @Override
    public boolean violatesNotWhatVertex(final ChunkSet mask, final ChunkSet pattern, final int startWord) {
        return (this.whatVertex != null) ? this.whatVertex.violatesNot(mask, pattern, startWord) : this.whoVertex.violatesNot(mask, pattern, startWord);
    }
    
    @Override
    public boolean violatesNotWhatEdge(final ChunkSet mask, final ChunkSet pattern, final int startWord) {
        return (this.whatEdge != null) ? this.whatEdge.violatesNot(mask, pattern, startWord) : this.whoEdge.violatesNot(mask, pattern, startWord);
    }
    
    @Override
    public ChunkSet cloneWhoVertex() {
        return this.whoVertex.internalStateCopy();
    }
    
    @Override
    public ChunkSet cloneWhoEdge() {
        return this.whoEdge.internalStateCopy();
    }
    
    @Override
    public ChunkSet cloneWhatVertex() {
        return (this.whatVertex != null) ? this.whatVertex.internalStateCopy() : this.whoVertex.internalStateCopy();
    }
    
    @Override
    public ChunkSet cloneWhatEdge() {
        return (this.whatEdge != null) ? this.whatEdge.internalStateCopy() : this.whoEdge.internalStateCopy();
    }
}
