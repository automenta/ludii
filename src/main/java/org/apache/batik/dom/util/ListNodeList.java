// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.util;

import org.w3c.dom.Node;
import java.util.List;
import org.w3c.dom.NodeList;

public class ListNodeList implements NodeList
{
    protected List list;
    
    public ListNodeList(final List list) {
        this.list = list;
    }
    
    @Override
    public Node item(final int index) {
        if (index < 0 || index > this.list.size()) {
            return null;
        }
        return this.list.get(index);
    }
    
    @Override
    public int getLength() {
        return this.list.size();
    }
}
