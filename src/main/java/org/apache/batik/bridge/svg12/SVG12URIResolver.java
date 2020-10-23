// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge.svg12;

import org.w3c.dom.NodeList;
import org.apache.batik.dom.xbl.XBLShadowTreeElement;
import org.apache.batik.dom.xbl.NodeXBL;
import org.w3c.dom.Node;
import org.apache.batik.dom.AbstractNode;
import org.w3c.dom.Element;
import org.apache.batik.bridge.DocumentLoader;
import org.w3c.dom.svg.SVGDocument;
import org.apache.batik.bridge.URIResolver;

public class SVG12URIResolver extends URIResolver
{
    public SVG12URIResolver(final SVGDocument doc, final DocumentLoader dl) {
        super(doc, dl);
    }
    
    @Override
    protected String getRefererBaseURI(final Element ref) {
        final AbstractNode aref = (AbstractNode)ref;
        if (aref.getXblBoundElement() != null) {
            return null;
        }
        return aref.getBaseURI();
    }
    
    @Override
    protected Node getNodeByFragment(final String frag, final Element ref) {
        final NodeXBL refx = (NodeXBL)ref;
        final NodeXBL boundElt = (NodeXBL)refx.getXblBoundElement();
        if (boundElt != null) {
            final XBLShadowTreeElement shadow = (XBLShadowTreeElement)boundElt.getXblShadowTree();
            Node n = shadow.getElementById(frag);
            if (n != null) {
                return n;
            }
            final NodeList nl = refx.getXblDefinitions();
            for (int i = 0; i < nl.getLength(); ++i) {
                n = nl.item(i).getOwnerDocument().getElementById(frag);
                if (n != null) {
                    return n;
                }
            }
        }
        return super.getNodeByFragment(frag, ref);
    }
}
