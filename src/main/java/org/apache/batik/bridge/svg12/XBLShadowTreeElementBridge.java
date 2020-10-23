// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge.svg12;

import org.apache.batik.bridge.GVTBuilder;
import org.w3c.dom.Node;
import org.w3c.dom.events.MutationEvent;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.bridge.SVGUtilities;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.AbstractGraphicsNodeBridge;

public class XBLShadowTreeElementBridge extends AbstractGraphicsNodeBridge
{
    @Override
    public String getLocalName() {
        return "shadowTree";
    }
    
    @Override
    public String getNamespaceURI() {
        return "http://www.w3.org/2004/xbl";
    }
    
    @Override
    public Bridge getInstance() {
        return new XBLShadowTreeElementBridge();
    }
    
    @Override
    public GraphicsNode createGraphicsNode(final BridgeContext ctx, final Element e) {
        if (!SVGUtilities.matchUserAgent(e, ctx.getUserAgent())) {
            return null;
        }
        final CompositeGraphicsNode cgn = new CompositeGraphicsNode();
        this.associateSVGContext(ctx, e, cgn);
        return cgn;
    }
    
    @Override
    protected GraphicsNode instantiateGraphicsNode() {
        return null;
    }
    
    @Override
    public void buildGraphicsNode(final BridgeContext ctx, final Element e, final GraphicsNode node) {
        this.initializeDynamicSupport(ctx, e, node);
    }
    
    @Override
    public boolean getDisplay(final Element e) {
        return true;
    }
    
    @Override
    public boolean isComposite() {
        return true;
    }
    
    @Override
    public void handleDOMNodeInsertedEvent(final MutationEvent evt) {
        if (evt.getTarget() instanceof Element) {
            this.handleElementAdded((CompositeGraphicsNode)this.node, this.e, (Element)evt.getTarget());
        }
    }
    
    public void handleElementAdded(final CompositeGraphicsNode gn, final Node parent, final Element childElt) {
        final GVTBuilder builder = this.ctx.getGVTBuilder();
        final GraphicsNode childNode = builder.build(this.ctx, childElt);
        if (childNode == null) {
            return;
        }
        int idx = -1;
        for (Node ps = childElt.getPreviousSibling(); ps != null; ps = ps.getPreviousSibling()) {
            if (ps.getNodeType() == 1) {
                final Element pse = (Element)ps;
                GraphicsNode psgn;
                for (psgn = this.ctx.getGraphicsNode(pse); psgn != null && psgn.getParent() != gn; psgn = psgn.getParent()) {}
                if (psgn != null) {
                    idx = gn.indexOf(psgn);
                    if (idx != -1) {
                        break;
                    }
                }
            }
        }
        ++idx;
        gn.add(idx, childNode);
    }
}
