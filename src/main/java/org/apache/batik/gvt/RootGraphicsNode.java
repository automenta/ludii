// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt;

import org.apache.batik.gvt.event.GraphicsNodeChangeListener;
import java.util.LinkedList;
import java.util.List;

public class RootGraphicsNode extends CompositeGraphicsNode
{
    List treeGraphicsNodeChangeListeners;
    
    public RootGraphicsNode() {
        this.treeGraphicsNodeChangeListeners = null;
    }
    
    @Override
    public RootGraphicsNode getRoot() {
        return this;
    }
    
    public List getTreeGraphicsNodeChangeListeners() {
        if (this.treeGraphicsNodeChangeListeners == null) {
            this.treeGraphicsNodeChangeListeners = new LinkedList();
        }
        return this.treeGraphicsNodeChangeListeners;
    }
    
    public void addTreeGraphicsNodeChangeListener(final GraphicsNodeChangeListener l) {
        this.getTreeGraphicsNodeChangeListeners().add(l);
    }
    
    public void removeTreeGraphicsNodeChangeListener(final GraphicsNodeChangeListener l) {
        this.getTreeGraphicsNodeChangeListeners().remove(l);
    }
}
