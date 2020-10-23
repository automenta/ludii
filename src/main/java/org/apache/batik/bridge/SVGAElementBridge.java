// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.apache.batik.anim.dom.SVGOMAnimationElement;
import org.apache.batik.anim.dom.SVGOMDocument;
import org.apache.batik.util.ParsedURL;
import org.apache.batik.anim.dom.SVGOMAElement;
import java.util.Iterator;
import java.util.List;
import org.w3c.dom.svg.SVGAElement;
import org.apache.batik.dom.events.AbstractEvent;
import org.w3c.dom.events.Event;
import java.awt.Cursor;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.EventListener;
import org.apache.batik.dom.events.NodeEventTarget;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;

public class SVGAElementBridge extends SVGGElementBridge
{
    protected AnchorListener al;
    protected CursorMouseOverListener bl;
    protected CursorMouseOutListener cl;
    
    @Override
    public String getLocalName() {
        return "a";
    }
    
    @Override
    public Bridge getInstance() {
        return new SVGAElementBridge();
    }
    
    @Override
    public void buildGraphicsNode(final BridgeContext ctx, final Element e, final GraphicsNode node) {
        super.buildGraphicsNode(ctx, e, node);
        if (ctx.isInteractive()) {
            final NodeEventTarget target = (NodeEventTarget)e;
            final CursorHolder ch = new CursorHolder(CursorManager.DEFAULT_CURSOR);
            target.addEventListenerNS("http://www.w3.org/2001/xml-events", "click", this.al = new AnchorListener(ctx.getUserAgent(), ch), false, null);
            ctx.storeEventListenerNS(target, "http://www.w3.org/2001/xml-events", "click", this.al, false);
            target.addEventListenerNS("http://www.w3.org/2001/xml-events", "mouseover", this.bl = new CursorMouseOverListener(ctx.getUserAgent(), ch), false, null);
            ctx.storeEventListenerNS(target, "http://www.w3.org/2001/xml-events", "mouseover", this.bl, false);
            target.addEventListenerNS("http://www.w3.org/2001/xml-events", "mouseout", this.cl = new CursorMouseOutListener(ctx.getUserAgent(), ch), false, null);
            ctx.storeEventListenerNS(target, "http://www.w3.org/2001/xml-events", "mouseout", this.cl, false);
        }
    }
    
    @Override
    public void dispose() {
        final NodeEventTarget target = (NodeEventTarget)this.e;
        if (this.al != null) {
            target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "click", this.al, false);
            this.al = null;
        }
        if (this.bl != null) {
            target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "mouseover", this.bl, false);
            this.bl = null;
        }
        if (this.cl != null) {
            target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "mouseout", this.cl, false);
            this.cl = null;
        }
        super.dispose();
    }
    
    @Override
    public boolean isComposite() {
        return true;
    }
    
    public static class CursorHolder
    {
        Cursor cursor;
        
        public CursorHolder(final Cursor c) {
            this.cursor = null;
            this.cursor = c;
        }
        
        public void holdCursor(final Cursor c) {
            this.cursor = c;
        }
        
        public Cursor getCursor() {
            return this.cursor;
        }
    }
    
    public static class AnchorListener implements EventListener
    {
        protected UserAgent userAgent;
        protected CursorHolder holder;
        
        public AnchorListener(final UserAgent ua, final CursorHolder ch) {
            this.userAgent = ua;
            this.holder = ch;
        }
        
        @Override
        public void handleEvent(final Event evt) {
            if (!(evt instanceof AbstractEvent)) {
                return;
            }
            final AbstractEvent ae = (AbstractEvent)evt;
            final List l = ae.getDefaultActions();
            if (l != null) {
                for (final Object o : l) {
                    if (o instanceof AnchorDefaultActionable) {
                        return;
                    }
                }
            }
            final SVGAElement elt = (SVGAElement)evt.getCurrentTarget();
            ae.addDefaultAction(new AnchorDefaultActionable(elt, this.userAgent, this.holder));
        }
    }
    
    public static class AnchorDefaultActionable implements Runnable
    {
        protected SVGOMAElement elt;
        protected UserAgent userAgent;
        protected CursorHolder holder;
        
        public AnchorDefaultActionable(final SVGAElement e, final UserAgent ua, final CursorHolder ch) {
            this.elt = (SVGOMAElement)e;
            this.userAgent = ua;
            this.holder = ch;
        }
        
        @Override
        public void run() {
            this.userAgent.setSVGCursor(this.holder.getCursor());
            final String href = this.elt.getHref().getAnimVal();
            final ParsedURL purl = new ParsedURL(this.elt.getBaseURI(), href);
            final SVGOMDocument doc = (SVGOMDocument)this.elt.getOwnerDocument();
            final ParsedURL durl = doc.getParsedURL();
            if (purl.sameFile(durl)) {
                final String frag = purl.getRef();
                if (frag != null && frag.length() != 0) {
                    final Element refElt = doc.getElementById(frag);
                    if (refElt instanceof SVGOMAnimationElement) {
                        final SVGOMAnimationElement aelt = (SVGOMAnimationElement)refElt;
                        final float t = aelt.getHyperlinkBeginTime();
                        if (Float.isNaN(t)) {
                            aelt.beginElement();
                        }
                        else {
                            doc.getRootElement().setCurrentTime(t);
                        }
                        return;
                    }
                }
            }
            this.userAgent.openLink(this.elt);
        }
    }
    
    public static class CursorMouseOverListener implements EventListener
    {
        protected UserAgent userAgent;
        protected CursorHolder holder;
        
        public CursorMouseOverListener(final UserAgent ua, final CursorHolder ch) {
            this.userAgent = ua;
            this.holder = ch;
        }
        
        @Override
        public void handleEvent(final Event evt) {
            if (!(evt instanceof AbstractEvent)) {
                return;
            }
            final AbstractEvent ae = (AbstractEvent)evt;
            final List l = ae.getDefaultActions();
            if (l != null) {
                for (final Object o : l) {
                    if (o instanceof MouseOverDefaultActionable) {
                        return;
                    }
                }
            }
            final Element target = (Element)ae.getTarget();
            final SVGAElement elt = (SVGAElement)ae.getCurrentTarget();
            ae.addDefaultAction(new MouseOverDefaultActionable(target, elt, this.userAgent, this.holder));
        }
    }
    
    public static class MouseOverDefaultActionable implements Runnable
    {
        protected Element target;
        protected SVGAElement elt;
        protected UserAgent userAgent;
        protected CursorHolder holder;
        
        public MouseOverDefaultActionable(final Element t, final SVGAElement e, final UserAgent ua, final CursorHolder ch) {
            this.target = t;
            this.elt = e;
            this.userAgent = ua;
            this.holder = ch;
        }
        
        @Override
        public void run() {
            if (CSSUtilities.isAutoCursor(this.target)) {
                this.holder.holdCursor(CursorManager.DEFAULT_CURSOR);
                this.userAgent.setSVGCursor(CursorManager.ANCHOR_CURSOR);
            }
            if (this.elt != null) {
                final String href = this.elt.getHref().getAnimVal();
                this.userAgent.displayMessage(href);
            }
        }
    }
    
    public static class CursorMouseOutListener implements EventListener
    {
        protected UserAgent userAgent;
        protected CursorHolder holder;
        
        public CursorMouseOutListener(final UserAgent ua, final CursorHolder ch) {
            this.userAgent = ua;
            this.holder = ch;
        }
        
        @Override
        public void handleEvent(final Event evt) {
            if (!(evt instanceof AbstractEvent)) {
                return;
            }
            final AbstractEvent ae = (AbstractEvent)evt;
            final List l = ae.getDefaultActions();
            if (l != null) {
                for (final Object o : l) {
                    if (o instanceof MouseOutDefaultActionable) {
                        return;
                    }
                }
            }
            final SVGAElement elt = (SVGAElement)evt.getCurrentTarget();
            ae.addDefaultAction(new MouseOutDefaultActionable(elt, this.userAgent, this.holder));
        }
    }
    
    public static class MouseOutDefaultActionable implements Runnable
    {
        protected SVGAElement elt;
        protected UserAgent userAgent;
        protected CursorHolder holder;
        
        public MouseOutDefaultActionable(final SVGAElement e, final UserAgent ua, final CursorHolder ch) {
            this.elt = e;
            this.userAgent = ua;
            this.holder = ch;
        }
        
        @Override
        public void run() {
            if (this.elt != null) {
                this.userAgent.displayMessage("");
            }
        }
    }
}
