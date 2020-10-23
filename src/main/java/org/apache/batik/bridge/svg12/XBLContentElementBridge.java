// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge.svg12;

import org.w3c.dom.NodeList;
import org.apache.batik.bridge.GVTBuilder;
import org.w3c.dom.Node;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.anim.dom.XBLOMContentElement;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.AbstractGraphicsNodeBridge;

public class XBLContentElementBridge extends AbstractGraphicsNodeBridge
{
    protected ContentChangedListener contentChangedListener;
    protected ContentManager contentManager;
    
    @Override
    public String getLocalName() {
        return "content";
    }
    
    @Override
    public String getNamespaceURI() {
        return "http://www.w3.org/2004/xbl";
    }
    
    @Override
    public Bridge getInstance() {
        return new XBLContentElementBridge();
    }
    
    @Override
    public GraphicsNode createGraphicsNode(final BridgeContext ctx, final Element e) {
        final CompositeGraphicsNode gn = this.buildCompositeGraphicsNode(ctx, e, null);
        return gn;
    }
    
    public CompositeGraphicsNode buildCompositeGraphicsNode(final BridgeContext ctx, final Element e, CompositeGraphicsNode cgn) {
        final XBLOMContentElement content = (XBLOMContentElement)e;
        final AbstractDocument doc = (AbstractDocument)e.getOwnerDocument();
        final DefaultXBLManager xm = (DefaultXBLManager)doc.getXBLManager();
        this.contentManager = xm.getContentManager(e);
        if (cgn == null) {
            cgn = new CompositeGraphicsNode();
            this.associateSVGContext(ctx, e, cgn);
        }
        else {
            for (int s = cgn.size(), i = 0; i < s; ++i) {
                cgn.remove(0);
            }
        }
        final GVTBuilder builder = ctx.getGVTBuilder();
        final NodeList nl = this.contentManager.getSelectedContent(content);
        if (nl != null) {
            for (int j = 0; j < nl.getLength(); ++j) {
                final Node n = nl.item(j);
                if (n.getNodeType() == 1) {
                    final GraphicsNode gn = builder.build(ctx, (Element)n);
                    if (gn != null) {
                        cgn.add(gn);
                    }
                }
            }
        }
        if (ctx.isDynamic() && this.contentChangedListener == null) {
            this.contentChangedListener = new ContentChangedListener();
            this.contentManager.addContentSelectionChangedListener(content, this.contentChangedListener);
        }
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
        return false;
    }
    
    @Override
    public void dispose() {
        super.dispose();
        if (this.contentChangedListener != null) {
            this.contentManager.removeContentSelectionChangedListener((XBLOMContentElement)this.e, this.contentChangedListener);
        }
    }
    
    protected class ContentChangedListener implements ContentSelectionChangedListener
    {
        @Override
        public void contentSelectionChanged(final ContentSelectionChangedEvent csce) {
            XBLContentElementBridge.this.buildCompositeGraphicsNode(XBLContentElementBridge.this.ctx, XBLContentElementBridge.this.e, (CompositeGraphicsNode)XBLContentElementBridge.this.node);
        }
    }
}
