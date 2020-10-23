// 
// Decompiled by Procyon v0.5.36
// 

package util;

import java.io.Serializable;
import java.util.Arrays;
import java.util.BitSet;

public class UnionInfo implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected final int[] parent;
    protected final BitSet[] itemsList;
    protected final int totalsize;
    
    public UnionInfo(final int totalElements) {
        this.totalsize = totalElements;
        this.parent = new int[totalElements];
        this.itemsList = new BitSet[totalElements];
        for (int i = 0; i < totalElements; ++i) {
            this.parent[i] = i;
            this.itemsList[i] = null;
        }
    }
    
    public UnionInfo(final UnionInfo other) {
        this.totalsize = other.totalsize;
        this.parent = Arrays.copyOf(other.parent, other.parent.length);
        this.itemsList = new BitSet[other.itemsList.length];
        for (int i = 0; i < other.itemsList.length; ++i) {
            if (other.itemsList[i] != null) {
                this.itemsList[i] = (BitSet)other.itemsList[i].clone();
            }
        }
    }
    
    public void setParent(final int childIndex, final int parentIndex) {
        this.parent[childIndex] = parentIndex;
    }
    
    public int getParent(final int childIndex) {
        return this.parent[childIndex];
    }
    
    public BitSet getItemsList(final int parentIndex) {
        return this.itemsList[parentIndex];
    }
    
    public void setItem(final int parentIndex, final int childIndex) {
        (this.itemsList[parentIndex] = new BitSet(this.totalsize)).set(childIndex);
    }
    
    public void mergeItemsLists(final int parentIndex1, final int parentIndex2) {
        this.itemsList[parentIndex1].or(this.itemsList[parentIndex2]);
        this.itemsList[parentIndex2].clear();
    }
    
    public boolean isSameGroup(final int parentIndex, final int childIndex) {
        return this.itemsList[parentIndex] != null && this.itemsList[parentIndex].get(childIndex);
    }
    
    public int getGroupSize(final int parentIndex) {
        if (this.itemsList[parentIndex] == null) {
            return 0;
        }
        return this.itemsList[parentIndex].cardinality();
    }
}
