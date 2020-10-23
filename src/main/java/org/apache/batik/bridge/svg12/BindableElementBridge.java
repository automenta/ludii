// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge.svg12;

import org.w3c.dom.events.MutationEvent;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.ScriptingEnvironment;
import org.apache.batik.bridge.UpdateManager;
import org.w3c.dom.Node;
import org.apache.batik.anim.dom.BindableElement;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.bridge.SVGUtilities;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.AbstractGraphicsNodeBridge;

public class BindableElementBridge extends AbstractGraphicsNodeBridge implements SVG12BridgeUpdateHandler
{
    @Override
    public String getNamespaceURI() {
        return "*";
    }
    
    @Override
    public String getLocalName() {
        return "*";
    }
    
    @Override
    public Bridge getInstance() {
        return new BindableElementBridge();
    }
    
    @Override
    public GraphicsNode createGraphicsNode(final BridgeContext ctx, final Element e) {
        if (!SVGUtilities.matchUserAgent(e, ctx.getUserAgent())) {
            return null;
        }
        final CompositeGraphicsNode gn = this.buildCompositeGraphicsNode(ctx, e, null);
        return gn;
    }
    
    public CompositeGraphicsNode buildCompositeGraphicsNode(final BridgeContext ctx, final Element e, CompositeGraphicsNode gn) {
        final BindableElement be = (BindableElement)e;
        final Element shadowTree = be.getXblShadowTree();
        final UpdateManager um = ctx.getUpdateManager();
        final ScriptingEnvironment se = (um == null) ? null : um.getScriptingEnvironment();
        if (se != null && shadowTree != null) {
            se.addScriptingListeners(shadowTree);
        }
        if (gn == null) {
            gn = new CompositeGraphicsNode();
            this.associateSVGContext(ctx, e, gn);
        }
        else {
            for (int s = gn.size(), i = 0; i < s; ++i) {
                gn.remove(0);
            }
        }
        final GVTBuilder builder = ctx.getGVTBuilder();
        if (shadowTree != null) {
            final GraphicsNode shadowNode = builder.build(ctx, shadowTree);
            if (shadowNode != null) {
                gn.add(shadowNode);
            }
        }
        else {
            for (Node m = e.getFirstChild(); m != null; m = m.getNextSibling()) {
                if (m.getNodeType() == 1) {
                    final GraphicsNode n = builder.build(ctx, (Element)m);
                    if (n != null) {
                        gn.add(n);
                    }
                }
            }
        }
        return gn;
    }
    
    @Override
    public void dispose() {
        final BindableElement be = (BindableElement)this.e;
        if (be != null && be.getCSSFirstChild() != null) {
            AbstractGraphicsNodeBridge.disposeTree(be.getCSSFirstChild());
        }
        super.dispose();
    }
    
    @Override
    protected GraphicsNode instantiateGraphicsNode() {
        return null;
    }
    
    @Override
    public boolean isComposite() {
        return false;
    }
    
    @Override
    public void buildGraphicsNode(final BridgeContext ctx, final Element e, final GraphicsNode node) {
        this.initializeDynamicSupport(ctx, e, node);
    }
    
    @Override
    public void handleDOMNodeInsertedEvent(final MutationEvent evt) {
        final BindableElement be = (BindableElement)this.e;
        final Element shadowTree = be.getXblShadowTree();
        if (shadowTree == null && evt.getTarget() instanceof Element) {
            this.handleElementAdded((CompositeGraphicsNode)this.node, this.e, (Element)evt.getTarget());
        }
    }
    
    @Override
    public void handleBindingEvent(final Element bindableElement, final Element shadowTree) {
        final CompositeGraphicsNode gn = this.node.getParent();
        gn.remove(this.node);
        AbstractGraphicsNodeBridge.disposeTree(this.e);
        this.handleElementAdded(gn, this.e.getParentNode(), this.e);
    }
    
    @Override
    public void handleContentSelectionChangedEvent(final ContentSelectionChangedEvent csce) {
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
