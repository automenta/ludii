// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom;

import org.apache.batik.dom.util.XMLSupport;
import org.w3c.dom.Element;
import org.apache.batik.xml.XMLUtilities;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public abstract class AbstractText extends AbstractCharacterData implements Text
{
    @Override
    public Text splitText(final int offset) throws DOMException {
        if (this.isReadonly()) {
            throw this.createDOMException((short)7, "readonly.node", new Object[] { this.getNodeType(), this.getNodeName() });
        }
        final String v = this.getNodeValue();
        if (offset < 0 || offset >= v.length()) {
            throw this.createDOMException((short)1, "offset", new Object[] { offset });
        }
        final Node n = this.getParentNode();
        if (n == null) {
            throw this.createDOMException((short)1, "need.parent", new Object[0]);
        }
        final String t1 = v.substring(offset);
        final Text t2 = this.createTextNode(t1);
        final Node ns = this.getNextSibling();
        if (ns != null) {
            n.insertBefore(t2, ns);
        }
        else {
            n.appendChild(t2);
        }
        this.setNodeValue(v.substring(0, offset));
        return t2;
    }
    
    protected Node getPreviousLogicallyAdjacentTextNode(final Node n) {
        Node p = n.getPreviousSibling();
        for (Node parent = n.getParentNode(); p == null && parent != null && parent.getNodeType() == 5; p = parent, parent = p.getParentNode(), p = p.getPreviousSibling()) {}
        while (p != null && p.getNodeType() == 5) {
            p = p.getLastChild();
        }
        if (p == null) {
            return null;
        }
        final int nt = p.getNodeType();
        if (nt == 3 || nt == 4) {
            return p;
        }
        return null;
    }
    
    protected Node getNextLogicallyAdjacentTextNode(final Node n) {
        Node p = n.getNextSibling();
        for (Node parent = n.getParentNode(); p == null && parent != null && parent.getNodeType() == 5; p = parent, parent = p.getParentNode(), p = p.getNextSibling()) {}
        while (p != null && p.getNodeType() == 5) {
            p = p.getFirstChild();
        }
        if (p == null) {
            return null;
        }
        final int nt = p.getNodeType();
        if (nt == 3 || nt == 4) {
            return p;
        }
        return null;
    }
    
    @Override
    public String getWholeText() {
        final StringBuffer sb = new StringBuffer();
        for (Node n = this; n != null; n = this.getPreviousLogicallyAdjacentTextNode(n)) {
            sb.insert(0, n.getNodeValue());
        }
        for (Node n = this.getNextLogicallyAdjacentTextNode(this); n != null; n = this.getNextLogicallyAdjacentTextNode(n)) {
            sb.append(n.getNodeValue());
        }
        return sb.toString();
    }
    
    @Override
    public boolean isElementContentWhitespace() {
        for (int len = this.nodeValue.length(), i = 0; i < len; ++i) {
            if (!XMLUtilities.isXMLSpace(this.nodeValue.charAt(i))) {
                return false;
            }
        }
        final Node p = this.getParentNode();
        if (p.getNodeType() == 1) {
            final String sp = XMLSupport.getXMLSpace((Element)p);
            return !sp.equals("preserve");
        }
        return true;
    }
    
    @Override
    public Text replaceWholeText(final String s) throws DOMException {
        for (Node n = this.getPreviousLogicallyAdjacentTextNode(this); n != null; n = this.getPreviousLogicallyAdjacentTextNode(n)) {
            final AbstractNode an = (AbstractNode)n;
            if (an.isReadonly()) {
                throw this.createDOMException((short)7, "readonly.node", new Object[] { n.getNodeType(), n.getNodeName() });
            }
        }
        for (Node n = this.getNextLogicallyAdjacentTextNode(this); n != null; n = this.getNextLogicallyAdjacentTextNode(n)) {
            final AbstractNode an = (AbstractNode)n;
            if (an.isReadonly()) {
                throw this.createDOMException((short)7, "readonly.node", new Object[] { n.getNodeType(), n.getNodeName() });
            }
        }
        final Node parent = this.getParentNode();
        for (Node n2 = this.getPreviousLogicallyAdjacentTextNode(this); n2 != null; n2 = this.getPreviousLogicallyAdjacentTextNode(n2)) {
            parent.removeChild(n2);
        }
        for (Node n2 = this.getNextLogicallyAdjacentTextNode(this); n2 != null; n2 = this.getNextLogicallyAdjacentTextNode(n2)) {
            parent.removeChild(n2);
        }
        if (this.isReadonly()) {
            final Text t = this.createTextNode(s);
            parent.replaceChild(t, this);
            return t;
        }
        this.setNodeValue(s);
        return this;
    }
    
    @Override
    public String getTextContent() {
        if (this.isElementContentWhitespace()) {
            return "";
        }
        return this.getNodeValue();
    }
    
    protected abstract Text createTextNode(final String p0);
}
