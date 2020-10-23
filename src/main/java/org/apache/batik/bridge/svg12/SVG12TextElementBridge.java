// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge.svg12;

import org.apache.batik.dom.events.EventSupport;
import org.w3c.dom.events.Event;
import org.w3c.dom.Element;
import org.w3c.dom.events.MutationEvent;
import org.apache.batik.dom.xbl.NodeXBL;
import org.w3c.dom.Node;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.EventListener;
import org.apache.batik.anim.dom.XBLEventSupport;
import org.apache.batik.dom.AbstractNode;
import org.apache.batik.dom.events.NodeEventTarget;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.SVGTextElementBridge;

public class SVG12TextElementBridge extends SVGTextElementBridge implements SVG12BridgeUpdateHandler
{
    @Override
    public Bridge getInstance() {
        return new SVG12TextElementBridge();
    }
    
    @Override
    protected void addTextEventListeners(final BridgeContext ctx, final NodeEventTarget e) {
        if (this.childNodeRemovedEventListener == null) {
            this.childNodeRemovedEventListener = new DOMChildNodeRemovedEventListener();
        }
        if (this.subtreeModifiedEventListener == null) {
            this.subtreeModifiedEventListener = new DOMSubtreeModifiedEventListener();
        }
        final SVG12BridgeContext ctx2 = (SVG12BridgeContext)ctx;
        final AbstractNode n = (AbstractNode)e;
        final XBLEventSupport evtSupport = (XBLEventSupport)n.initializeEventSupport();
        evtSupport.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeRemoved", this.childNodeRemovedEventListener, true);
        ctx2.storeImplementationEventListenerNS(e, "http://www.w3.org/2001/xml-events", "DOMNodeRemoved", this.childNodeRemovedEventListener, true);
        evtSupport.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMSubtreeModified", this.subtreeModifiedEventListener, false);
        ctx2.storeImplementationEventListenerNS(e, "http://www.w3.org/2001/xml-events", "DOMSubtreeModified", this.subtreeModifiedEventListener, false);
    }
    
    @Override
    protected void removeTextEventListeners(final BridgeContext ctx, final NodeEventTarget e) {
        final AbstractNode n = (AbstractNode)e;
        final XBLEventSupport evtSupport = (XBLEventSupport)n.initializeEventSupport();
        evtSupport.removeImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeRemoved", this.childNodeRemovedEventListener, true);
        evtSupport.removeImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMSubtreeModified", this.subtreeModifiedEventListener, false);
    }
    
    @Override
    protected Node getFirstChild(final Node n) {
        return ((NodeXBL)n).getXblFirstChild();
    }
    
    @Override
    protected Node getNextSibling(final Node n) {
        return ((NodeXBL)n).getXblNextSibling();
    }
    
    @Override
    protected Node getParentNode(final Node n) {
        return ((NodeXBL)n).getXblParentNode();
    }
    
    @Override
    public void handleDOMCharacterDataModified(final MutationEvent evt) {
        final Node childNode = (Node)evt.getTarget();
        if (this.isParentDisplayed(childNode)) {
            if (this.getParentNode(childNode) != childNode.getParentNode()) {
                this.computeLaidoutText(this.ctx, this.e, this.node);
            }
            else {
                this.laidoutText = null;
            }
        }
    }
    
    @Override
    public void handleBindingEvent(final Element bindableElement, final Element shadowTree) {
    }
    
    @Override
    public void handleContentSelectionChangedEvent(final ContentSelectionChangedEvent csce) {
        this.computeLaidoutText(this.ctx, this.e, this.node);
    }
    
    protected class DOMChildNodeRemovedEventListener extends SVGTextElementBridge.DOMChildNodeRemovedEventListener
    {
        @Override
        public void handleEvent(final Event evt) {
            super.handleEvent(EventSupport.getUltimateOriginalEvent(evt));
        }
    }
    
    protected class DOMSubtreeModifiedEventListener extends SVGTextElementBridge.DOMSubtreeModifiedEventListener
    {
        @Override
        public void handleEvent(final Event evt) {
            super.handleEvent(EventSupport.getUltimateOriginalEvent(evt));
        }
    }
}
