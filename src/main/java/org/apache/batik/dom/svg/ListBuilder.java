// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.svg;

import java.util.ArrayList;
import java.util.List;

public class ListBuilder implements ListHandler
{
    private final AbstractSVGList abstractSVGList;
    protected List list;
    
    public ListBuilder(final AbstractSVGList abstractSVGList) {
        this.abstractSVGList = abstractSVGList;
    }
    
    public List getList() {
        return this.list;
    }
    
    @Override
    public void startList() {
        this.list = new ArrayList();
    }
    
    @Override
    public void item(final SVGItem item) {
        item.setParent(this.abstractSVGList);
        this.list.add(item);
    }
    
    @Override
    public void endList() {
    }
}
