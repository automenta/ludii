// 
// Decompiled by Procyon v0.5.36
// 

package util.state.containerStackingState;

import game.Game;
import game.equipment.container.Container;
import game.types.board.SiteType;
import util.state.State;
import util.state.containerState.BaseContainerState;

public abstract class BaseContainerStateStacking extends BaseContainerState
{
    private static final long serialVersionUID = 1L;
    
    public BaseContainerStateStacking(final Game game, final Container container, final int numSites) {
        super(game, container, numSites);
    }
    
    public BaseContainerStateStacking(final BaseContainerStateStacking other) {
        super(other);
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
            return this.sizeStackEdge(site);
        }
        return this.sizeStackVertex(site);
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
    public boolean isEmpty(final int site, final SiteType type) {
        if (type == SiteType.Cell || this.container().index() != 0 || type == null) {
            return this.isEmptyCell(site);
        }
        if (type == SiteType.Edge) {
            return this.isEmptyEdge(site);
        }
        return this.isEmptyVertex(site);
    }
    
    @Override
    public void addItemGeneric(final State trialState, final int site, final int whatValue, final int whoId, final Game game, final SiteType graphElementType) {
        if (graphElementType == SiteType.Cell || this.container().index() != 0 || graphElementType == null) {
            this.addItem(trialState, site, whatValue, whoId, game);
        }
        else if (graphElementType == SiteType.Vertex) {
            this.addItemVertex(trialState, site, whatValue, whoId, game);
        }
        else {
            this.addItemEdge(trialState, site, whatValue, whoId, game);
        }
    }
    
    @Override
    public void addItemGeneric(final State trialState, final int site, final int what, final int who, final int stateVal, final int rotationVal, final Game game, final SiteType graphElementType) {
        if (graphElementType == SiteType.Cell || this.container().index() != 0 || graphElementType == null) {
            this.addItem(trialState, site, what, who, stateVal, rotationVal, game);
        }
        else if (graphElementType == SiteType.Vertex) {
            this.addItemVertex(trialState, site, what, who, stateVal, rotationVal, game);
        }
        else {
            this.addItemEdge(trialState, site, what, who, stateVal, rotationVal, game);
        }
    }
    
    @Override
    public void addItemGeneric(final State trialState, final int site, final int whatValue, final int whoId, final Game game, final boolean[] hiddenValues, final boolean masked, final SiteType graphElementType) {
        if (graphElementType == SiteType.Cell || this.container().index() != 0 || graphElementType == null) {
            this.addItem(trialState, site, whatValue, whoId, game, hiddenValues, masked);
        }
        else if (graphElementType == SiteType.Vertex) {
            this.addItemVertex(trialState, site, whatValue, whoId, game, hiddenValues, masked);
        }
        else {
            this.addItemEdge(trialState, site, whatValue, whoId, game, hiddenValues, masked);
        }
    }
    
    @Override
    public void removeStackGeneric(final State trialState, final int site, final SiteType graphElementType) {
        if (graphElementType == SiteType.Cell || this.container().index() != 0 || graphElementType == null) {
            this.removeStack(trialState, site);
        }
        else if (graphElementType == SiteType.Vertex) {
            this.removeStackVertex(trialState, site);
        }
        else {
            this.removeStackEdge(trialState, site);
        }
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
}
