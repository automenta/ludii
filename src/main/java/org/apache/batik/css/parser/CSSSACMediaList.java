// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.parser;

import org.w3c.css.sac.SACMediaList;

public class CSSSACMediaList implements SACMediaList
{
    protected String[] list;
    protected int length;
    
    public CSSSACMediaList() {
        this.list = new String[3];
    }
    
    @Override
    public int getLength() {
        return this.length;
    }
    
    @Override
    public String item(final int index) {
        if (index < 0 || index >= this.length) {
            return null;
        }
        return this.list[index];
    }
    
    public void append(final String item) {
        if (this.length == this.list.length) {
            final String[] tmp = this.list;
            System.arraycopy(tmp, 0, this.list = new String[1 + this.list.length + this.list.length / 2], 0, tmp.length);
        }
        this.list[this.length++] = item;
    }
}
