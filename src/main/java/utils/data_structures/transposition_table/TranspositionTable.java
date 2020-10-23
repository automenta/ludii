// 
// Decompiled by Procyon v0.5.36
// 

package utils.data_structures.transposition_table;

import java.lang.reflect.Array;

public class TranspositionTable<E>
{
    private final int numBitsPrimaryCode;
    private final int maxNumEntries;
    private E[] table;
    private final Class<E> dataType;
    
    public TranspositionTable(final Class<E> dataType, final int numBitsPrimaryCode) {
        this.numBitsPrimaryCode = numBitsPrimaryCode;
        this.maxNumEntries = 1 << numBitsPrimaryCode;
        this.table = (E[])Array.newInstance(dataType, this.maxNumEntries);
        this.dataType = dataType;
    }
    
    public void clear() {
        this.table = (E[])Array.newInstance(this.dataType, this.maxNumEntries);
    }
    
    public E retrieve(final long fullHash) {
        return this.table[(int)(fullHash >>> 64 - this.numBitsPrimaryCode)];
    }
    
    public void store(final E e, final long fullHash) {
        this.table[(int)(fullHash >>> 64 - this.numBitsPrimaryCode)] = e;
    }
}
