// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.parser;

import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SelectorList;

public class CSSSelectorList implements SelectorList
{
    protected Selector[] list;
    protected int length;
    
    public CSSSelectorList() {
        this.list = new Selector[3];
    }
    
    @Override
    public int getLength() {
        return this.length;
    }
    
    @Override
    public Selector item(final int index) {
        if (index < 0 || index >= this.length) {
            return null;
        }
        return this.list[index];
    }
    
    public void append(final Selector item) {
        if (this.length == this.list.length) {
            final Selector[] tmp = this.list;
            System.arraycopy(tmp, 0, this.list = new Selector[1 + this.list.length + this.list.length / 2], 0, tmp.length);
        }
        this.list[this.length++] = item;
    }
}
