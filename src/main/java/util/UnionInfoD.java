// 
// Decompiled by Procyon v0.5.36
// 

package util;

import main.Constants;

import java.io.Serializable;
import java.util.Arrays;
import java.util.BitSet;

public class UnionInfoD implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected final int[] parent;
    protected final BitSet[] itemsList;
    protected final BitSet[] itemWithOrthoNeighbors;
    protected final int totalsize;
    
    public UnionInfoD(final int totalElements, final int numberOfPlayers, final boolean blocking) {
        this.totalsize = totalElements;
        this.parent = new int[totalElements];
        this.itemsList = new BitSet[totalElements];
        this.itemWithOrthoNeighbors = new BitSet[totalElements];
        if (blocking) {
            (this.itemsList[0] = new BitSet(this.totalsize)).set(0, totalElements);
        }
        else {
            for (int i = 0; i < totalElements; ++i) {
                this.parent[i] = Constants.UNUSED;
            }
        }
    }
    
    public UnionInfoD(final UnionInfoD other) {
        this.totalsize = other.totalsize;
        this.parent = Arrays.copyOf(other.parent, other.parent.length);
        this.itemsList = new BitSet[other.itemsList.length];
        for (int i = 0; i < other.itemsList.length; ++i) {
            if (other.itemsList[i] != null) {
                this.itemsList[i] = (BitSet)other.itemsList[i].clone();
            }
        }
        if (other.itemWithOrthoNeighbors != null) {
            this.itemWithOrthoNeighbors = new BitSet[other.itemWithOrthoNeighbors.length];
            for (int i = 0; i < other.itemWithOrthoNeighbors.length; ++i) {
                if (other.itemWithOrthoNeighbors[i] != null) {
                    this.itemWithOrthoNeighbors[i] = (BitSet)other.itemWithOrthoNeighbors[i].clone();
                }
            }
        }
        else {
            this.itemWithOrthoNeighbors = null;
        }
    }
    
    public void setParent(final int childIndex, final int parentIndex) {
        this.parent[childIndex] = parentIndex;
    }
    
    public void clearParent(final int childIndex) {
        this.parent[childIndex] = Constants.UNUSED;
    }
    
    public int getParent(final int childIndex) {
        return this.parent[childIndex];
    }
    
    public BitSet getItemsList(final int parentIndex) {
        if (this.itemsList[parentIndex] == null) {
            this.itemsList[parentIndex] = new BitSet(this.totalsize);
        }
        return this.itemsList[parentIndex];
    }
    
    public void clearItemsList(final int parentIndex) {
        this.itemsList[parentIndex] = null;
    }
    
    public boolean isSameGroup(final int parentIndex, final int childIndex) {
        return this.itemsList[parentIndex] != null && this.itemsList[parentIndex].get(childIndex);
    }
    
    public void setItem(final int parentIndex, final int childIndex) {
        if (this.itemsList[parentIndex] == null) {
            this.itemsList[parentIndex] = new BitSet(this.totalsize);
        }
        this.itemsList[parentIndex].set(childIndex);
    }
    
    public void mergeItemsLists(final int parentIndex1, final int parentIndex2) {
        this.getItemsList(parentIndex1).or(this.getItemsList(parentIndex2));
        this.itemsList[parentIndex2] = null;
    }
    
    public int getGroupSize(final int parentIndex) {
        if (this.itemsList[parentIndex] == null) {
            return 0;
        }
        return this.itemsList[parentIndex].cardinality();
    }
    
    public BitSet getAllItemWithOrthoNeighbors(final int parentIndex) {
        if (this.itemWithOrthoNeighbors[parentIndex] == null) {
            this.itemWithOrthoNeighbors[parentIndex] = new BitSet(this.totalsize);
        }
        return this.itemWithOrthoNeighbors[parentIndex];
    }
    
    public void clearAllitemWithOrthoNeighbors(final int parentIndex) {
        this.itemWithOrthoNeighbors[parentIndex] = null;
    }
    
    public void setItemWithOrthoNeighbors(final int parentIndex, final int childIndex) {
        if (this.itemWithOrthoNeighbors[parentIndex] == null) {
            this.itemWithOrthoNeighbors[parentIndex] = new BitSet(this.totalsize);
        }
        this.itemWithOrthoNeighbors[parentIndex].set(childIndex);
    }
    
    public void mergeItemWithOrthoNeighbors(final int parentIndex1, final int parentIndex2) {
        this.getAllItemWithOrthoNeighbors(parentIndex1).or(this.getAllItemWithOrthoNeighbors(parentIndex2));
        this.itemWithOrthoNeighbors[parentIndex2] = null;
    }
}
