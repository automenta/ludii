// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.w3c.dom.Node;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.w3c.dom.svg.SVGTests;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;

public class SVGSwitchElementBridge extends SVGGElementBridge
{
    protected Element selectedChild;
    
    @Override
    public String getLocalName() {
        return "switch";
    }
    
    @Override
    public Bridge getInstance() {
        return new SVGSwitchElementBridge();
    }
    
    @Override
    public GraphicsNode createGraphicsNode(final BridgeContext ctx, final Element e) {
        GraphicsNode refNode = null;
        final GVTBuilder builder = ctx.getGVTBuilder();
        this.selectedChild = null;
        for (Node n = e.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() == 1) {
                final Element ref = (Element)n;
                if (n instanceof SVGTests && SVGUtilities.matchUserAgent(ref, ctx.getUserAgent())) {
                    this.selectedChild = ref;
                    refNode = builder.build(ctx, ref);
                    break;
                }
            }
        }
        if (refNode == null) {
            return null;
        }
        final CompositeGraphicsNode group = (CompositeGraphicsNode)super.createGraphicsNode(ctx, e);
        if (group == null) {
            return null;
        }
        group.add(refNode);
        return group;
    }
    
    @Override
    public boolean isComposite() {
        return false;
    }
    
    @Override
    public void dispose() {
        this.selectedChild = null;
        super.dispose();
    }
    
    @Override
    protected void handleElementAdded(final CompositeGraphicsNode gn, final Node parent, final Element childElt) {
        for (Node n = childElt.getPreviousSibling(); n != null; n = n.getPreviousSibling()) {
            if (n == childElt) {
                return;
            }
        }
        if (childElt instanceof SVGTests && SVGUtilities.matchUserAgent(childElt, this.ctx.getUserAgent())) {
            if (this.selectedChild != null) {
                gn.remove(0);
                AbstractGraphicsNodeBridge.disposeTree(this.selectedChild);
            }
            this.selectedChild = childElt;
            final GVTBuilder builder = this.ctx.getGVTBuilder();
            final GraphicsNode refNode = builder.build(this.ctx, childElt);
            if (refNode != null) {
                gn.add(refNode);
            }
        }
    }
    
    protected void handleChildElementRemoved(final Element e) {
        final CompositeGraphicsNode gn = (CompositeGraphicsNode)this.node;
        if (this.selectedChild == e) {
            gn.remove(0);
            AbstractGraphicsNodeBridge.disposeTree(this.selectedChild);
            this.selectedChild = null;
            GraphicsNode refNode = null;
            final GVTBuilder builder = this.ctx.getGVTBuilder();
            for (Node n = e.getNextSibling(); n != null; n = n.getNextSibling()) {
                if (n.getNodeType() == 1) {
                    final Element ref = (Element)n;
                    if (n instanceof SVGTests && SVGUtilities.matchUserAgent(ref, this.ctx.getUserAgent())) {
                        refNode = builder.build(this.ctx, ref);
                        this.selectedChild = ref;
                        break;
                    }
                }
            }
            if (refNode != null) {
                gn.add(refNode);
            }
        }
    }
}
