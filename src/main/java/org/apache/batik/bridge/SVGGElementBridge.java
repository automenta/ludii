// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.w3c.dom.Node;
import org.w3c.dom.events.MutationEvent;
import java.awt.geom.Rectangle2D;
import java.awt.RenderingHints;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;

public class SVGGElementBridge extends AbstractGraphicsNodeBridge
{
    @Override
    public String getLocalName() {
        return "g";
    }
    
    @Override
    public Bridge getInstance() {
        return new SVGGElementBridge();
    }
    
    @Override
    public GraphicsNode createGraphicsNode(final BridgeContext ctx, final Element e) {
        final CompositeGraphicsNode gn = (CompositeGraphicsNode)super.createGraphicsNode(ctx, e);
        if (gn == null) {
            return null;
        }
        this.associateSVGContext(ctx, e, gn);
        RenderingHints hints = null;
        hints = CSSUtilities.convertColorRendering(e, hints);
        if (hints != null) {
            gn.setRenderingHints(hints);
        }
        final Rectangle2D r = CSSUtilities.convertEnableBackground(e);
        if (r != null) {
            gn.setBackgroundEnable(r);
        }
        return gn;
    }
    
    @Override
    protected GraphicsNode instantiateGraphicsNode() {
        return new CompositeGraphicsNode();
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
        else {
            super.handleDOMNodeInsertedEvent(evt);
        }
    }
    
    protected void handleElementAdded(final CompositeGraphicsNode gn, final Node parent, final Element childElt) {
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
