// 
// Decompiled by Procyon v0.5.36
// 

package main.collections;

import gnu.trove.list.array.TIntArrayList;

public final class FastTIntArrayList extends TIntArrayList
{
    private static final int[] EMPTY_ELEMENTDATA;
    
    public FastTIntArrayList() {
    }
    
    public FastTIntArrayList(final FastTIntArrayList other) {
        this.no_entry_value = -99;
        final int length = other.size();
        if (length > 0) {
            this._data = new int[length];
            System.arraycopy(other._data, 0, this._data, 0, length);
            this._pos = length;
        }
        else {
            this._data = FastTIntArrayList.EMPTY_ELEMENTDATA;
            this._pos = 0;
        }
    }
    
    static {
        EMPTY_ELEMENTDATA = new int[0];
    }
}
