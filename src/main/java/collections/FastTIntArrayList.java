/*
 * Decompiled with CFR 0.150.
 */
package collections;

import gnu.trove.list.array.TIntArrayList;

public final class FastTIntArrayList
extends TIntArrayList {
    private static final int[] EMPTY_ELEMENTDATA = new int[0];

    public FastTIntArrayList() {
    }

    public FastTIntArrayList(FastTIntArrayList other) {
        this.no_entry_value = -99;
        int length = other.size();
        if (length > 0) {
            this._data = new int[length];
            System.arraycopy(other._data, 0, this._data, 0, length);
            this._pos = length;
        } else {
            this._data = EMPTY_ELEMENTDATA;
            this._pos = 0;
        }
    }
}

