// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.traversal;

import java.util.Iterator;
import org.w3c.dom.DOMException;
import java.util.LinkedList;
import org.w3c.dom.traversal.NodeIterator;
import org.w3c.dom.traversal.TreeWalker;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.Node;
import org.apache.batik.dom.AbstractDocument;
import java.util.List;

public class TraversalSupport
{
    protected List iterators;
    
    public static TreeWalker createTreeWalker(final AbstractDocument doc, final Node root, final int whatToShow, final NodeFilter filter, final boolean entityReferenceExpansion) {
        if (root == null) {
            throw doc.createDOMException((short)9, "null.root", null);
        }
        return new DOMTreeWalker(root, whatToShow, filter, entityReferenceExpansion);
    }
    
    public NodeIterator createNodeIterator(final AbstractDocument doc, final Node root, final int whatToShow, final NodeFilter filter, final boolean entityReferenceExpansion) throws DOMException {
        if (root == null) {
            throw doc.createDOMException((short)9, "null.root", null);
        }
        final NodeIterator result = new DOMNodeIterator(doc, root, whatToShow, filter, entityReferenceExpansion);
        if (this.iterators == null) {
            this.iterators = new LinkedList();
        }
        this.iterators.add(result);
        return result;
    }
    
    public void nodeToBeRemoved(final Node removedNode) {
        if (this.iterators != null) {
            for (final Object iterator : this.iterators) {
                ((DOMNodeIterator)iterator).nodeToBeRemoved(removedNode);
            }
        }
    }
    
    public void detachNodeIterator(final NodeIterator it) {
        this.iterators.remove(it);
    }
}
