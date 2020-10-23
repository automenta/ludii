// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.apache.batik.util.DoublyIndexedTable;

public class AttributeInitializer
{
    protected String[] keys;
    protected int length;
    protected DoublyIndexedTable values;
    
    public AttributeInitializer(final int capacity) {
        this.values = new DoublyIndexedTable();
        this.keys = new String[capacity * 3];
    }
    
    public void addAttribute(final String ns, final String prefix, final String ln, final String val) {
        final int len = this.keys.length;
        if (this.length == len) {
            final String[] t = new String[len * 2];
            System.arraycopy(this.keys, 0, t, 0, len);
            this.keys = t;
        }
        this.keys[this.length++] = ns;
        this.keys[this.length++] = prefix;
        this.keys[this.length++] = ln;
        this.values.put(ns, ln, val);
    }
    
    public void initializeAttributes(final AbstractElement elt) {
        for (int i = this.length - 1; i >= 2; i -= 3) {
            this.resetAttribute(elt, this.keys[i - 2], this.keys[i - 1], this.keys[i]);
        }
    }
    
    public boolean resetAttribute(final AbstractElement elt, final String ns, final String prefix, String ln) {
        final String val = (String)this.values.get(ns, ln);
        if (val == null) {
            return false;
        }
        if (prefix != null) {
            ln = prefix + ':' + ln;
        }
        elt.setUnspecifiedAttribute(ns, ln, val);
        return true;
    }
}
