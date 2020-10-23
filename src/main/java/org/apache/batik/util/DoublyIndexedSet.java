// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.util;

public class DoublyIndexedSet
{
    protected DoublyIndexedTable table;
    protected static Object value;
    
    public DoublyIndexedSet() {
        this.table = new DoublyIndexedTable();
    }
    
    public int size() {
        return this.table.size();
    }
    
    public void add(final Object o1, final Object o2) {
        this.table.put(o1, o2, DoublyIndexedSet.value);
    }
    
    public void remove(final Object o1, final Object o2) {
        this.table.remove(o1, o2);
    }
    
    public boolean contains(final Object o1, final Object o2) {
        return this.table.get(o1, o2) != null;
    }
    
    public void clear() {
        this.table.clear();
    }
    
    static {
        DoublyIndexedSet.value = new Object();
    }
}
