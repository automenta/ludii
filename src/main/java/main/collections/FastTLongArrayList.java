// 
// Decompiled by Procyon v0.5.36
// 

package main.collections;

import gnu.trove.list.array.TLongArrayList;

public final class FastTLongArrayList extends TLongArrayList
{
    private static final long[] EMPTY_ELEMENTDATA;
    
    public FastTLongArrayList() {
    }
    
    public FastTLongArrayList(final FastTLongArrayList other) {
        this.no_entry_value = -99L;
        final int length = other.size();
        if (length > 0) {
            this._data = new long[length];
            System.arraycopy(other._data, 0, this._data, 0, length);
            this._pos = length;
        }
        else {
            this._data = FastTLongArrayList.EMPTY_ELEMENTDATA;
            this._pos = 0;
        }
    }
    
    static {
        EMPTY_ELEMENTDATA = new long[0];
    }
}
