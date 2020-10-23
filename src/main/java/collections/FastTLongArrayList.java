/*
 * Decompiled with CFR 0.150.
 */
package collections;

import gnu.trove.list.array.TLongArrayList;

public final class FastTLongArrayList
extends TLongArrayList {
    private static final long[] EMPTY_ELEMENTDATA = new long[0];

    public FastTLongArrayList() {
    }

    public FastTLongArrayList(FastTLongArrayList other) {
        this.no_entry_value = -99L;
        int length = other.size();
        if (length > 0) {
            this._data = new long[length];
            System.arraycopy(other._data, 0, this._data, 0, length);
            this._pos = length;
        } else {
            this._data = EMPTY_ELEMENTDATA;
            this._pos = 0;
        }
    }
}

