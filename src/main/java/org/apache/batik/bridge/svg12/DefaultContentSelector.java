// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge.svg12;

import org.w3c.dom.Node;
import java.util.ArrayList;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.apache.batik.anim.dom.XBLOMContentElement;

public class DefaultContentSelector extends AbstractContentSelector
{
    protected SelectedNodes selectedContent;
    
    public DefaultContentSelector(final ContentManager cm, final XBLOMContentElement content, final Element bound) {
        super(cm, content, bound);
    }
    
    @Override
    public NodeList getSelectedContent() {
        if (this.selectedContent == null) {
            this.selectedContent = new SelectedNodes();
        }
        return this.selectedContent;
    }
    
    @Override
    boolean update() {
        if (this.selectedContent == null) {
            this.selectedContent = new SelectedNodes();
            return true;
        }
        return this.selectedContent.update();
    }
    
    protected class SelectedNodes implements NodeList
    {
        protected ArrayList nodes;
        
        public SelectedNodes() {
            this.nodes = new ArrayList(10);
            this.update();
        }
        
        protected boolean update() {
            final ArrayList oldNodes = (ArrayList)this.nodes.clone();
            this.nodes.clear();
            for (Node n = DefaultContentSelector.this.boundElement.getFirstChild(); n != null; n = n.getNextSibling()) {
                if (!DefaultContentSelector.this.isSelected(n)) {
                    this.nodes.add(n);
                }
            }
            final int nodesSize = this.nodes.size();
            if (oldNodes.size() != nodesSize) {
                return true;
            }
            for (int i = 0; i < nodesSize; ++i) {
                if (oldNodes.get(i) != this.nodes.get(i)) {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public Node item(final int index) {
            if (index < 0 || index >= this.nodes.size()) {
                return null;
            }
            return this.nodes.get(index);
        }
        
        @Override
        public int getLength() {
            return this.nodes.size();
        }
    }
}
