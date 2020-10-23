// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge.svg12;

import org.w3c.dom.Node;
import java.util.ArrayList;
import org.w3c.dom.NodeList;
import javax.xml.transform.TransformerException;
import org.apache.batik.dom.AbstractDocument;
import org.apache.xml.utils.PrefixResolver;
import javax.xml.transform.SourceLocator;
import org.w3c.dom.Element;
import org.apache.batik.anim.dom.XBLOMContentElement;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPath;

public class XPathPatternContentSelector extends AbstractContentSelector
{
    protected NSPrefixResolver prefixResolver;
    protected XPath xpath;
    protected XPathContext context;
    protected SelectedNodes selectedContent;
    protected String expression;
    
    public XPathPatternContentSelector(final ContentManager cm, final XBLOMContentElement content, final Element bound, final String selector) {
        super(cm, content, bound);
        this.prefixResolver = new NSPrefixResolver();
        this.expression = selector;
        this.parse();
    }
    
    protected void parse() {
        this.context = new XPathContext();
        try {
            this.xpath = new XPath(this.expression, (SourceLocator)null, (PrefixResolver)this.prefixResolver, 1);
        }
        catch (TransformerException te) {
            final AbstractDocument doc = (AbstractDocument)this.contentElement.getOwnerDocument();
            throw doc.createXPathException((short)51, "xpath.invalid.expression", new Object[] { this.expression, te.getMessage() });
        }
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
        this.parse();
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
            for (Node n = XPathPatternContentSelector.this.boundElement.getFirstChild(); n != null; n = n.getNextSibling()) {
                this.update(n);
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
        
        protected boolean descendantSelected(Node n) {
            for (n = n.getFirstChild(); n != null; n = n.getNextSibling()) {
                if (XPathPatternContentSelector.this.isSelected(n) || this.descendantSelected(n)) {
                    return true;
                }
            }
            return false;
        }
        
        protected void update(Node n) {
            if (!XPathPatternContentSelector.this.isSelected(n)) {
                try {
                    final double matchScore = XPathPatternContentSelector.this.xpath.execute(XPathPatternContentSelector.this.context, n, (PrefixResolver)XPathPatternContentSelector.this.prefixResolver).num();
                    if (matchScore != Double.NEGATIVE_INFINITY) {
                        if (!this.descendantSelected(n)) {
                            this.nodes.add(n);
                        }
                    }
                    else {
                        for (n = n.getFirstChild(); n != null; n = n.getNextSibling()) {
                            this.update(n);
                        }
                    }
                }
                catch (TransformerException te) {
                    final AbstractDocument doc = (AbstractDocument)XPathPatternContentSelector.this.contentElement.getOwnerDocument();
                    throw doc.createXPathException((short)51, "xpath.error", new Object[] { XPathPatternContentSelector.this.expression, te.getMessage() });
                }
            }
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
    
    protected class NSPrefixResolver implements PrefixResolver
    {
        public String getBaseIdentifier() {
            return null;
        }
        
        public String getNamespaceForPrefix(final String prefix) {
            return XPathPatternContentSelector.this.contentElement.lookupNamespaceURI(prefix);
        }
        
        public String getNamespaceForPrefix(final String prefix, final Node context) {
            return XPathPatternContentSelector.this.contentElement.lookupNamespaceURI(prefix);
        }
        
        public boolean handlesNullPrefixes() {
            return false;
        }
    }
}
