// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.List;
import java.lang.ref.SoftReference;
import java.awt.geom.Point2D;
import org.apache.batik.dom.util.DOMUtilities;
import org.apache.batik.dom.events.DOMMouseEvent;
import org.apache.batik.gvt.GraphicsNode;
import java.awt.Point;
import org.apache.batik.gvt.event.GraphicsNodeMouseEvent;
import org.w3c.dom.views.AbstractView;
import org.apache.batik.dom.events.DOMKeyEvent;
import org.w3c.dom.events.DocumentEvent;
import org.apache.batik.gvt.event.GraphicsNodeKeyEvent;
import org.w3c.dom.Element;
import org.w3c.dom.events.Event;
import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;
import org.w3c.dom.events.EventListener;
import org.apache.batik.gvt.event.EventDispatcher;
import org.w3c.dom.events.EventTarget;
import org.apache.batik.dom.events.NodeEventTarget;
import org.apache.batik.gvt.event.GraphicsNodeKeyListener;
import org.apache.batik.gvt.event.GraphicsNodeMouseListener;
import org.w3c.dom.Document;
import java.text.AttributedCharacterIterator;
import org.apache.batik.util.SVGConstants;

public abstract class BridgeEventSupport implements SVGConstants
{
    public static final AttributedCharacterIterator.Attribute TEXT_COMPOUND_ID;
    
    protected BridgeEventSupport() {
    }
    
    public static void addGVTListener(final BridgeContext ctx, final Document doc) {
        final UserAgent ua = ctx.getUserAgent();
        if (ua != null) {
            final EventDispatcher dispatcher = ua.getEventDispatcher();
            if (dispatcher != null) {
                final Listener listener = new Listener(ctx, ua);
                dispatcher.addGraphicsNodeMouseListener(listener);
                dispatcher.addGraphicsNodeKeyListener(listener);
                final EventListener l = new GVTUnloadListener(dispatcher, listener);
                final NodeEventTarget target = (NodeEventTarget)doc;
                target.addEventListenerNS("http://www.w3.org/2001/xml-events", "SVGUnload", l, false, null);
                storeEventListenerNS(ctx, target, "http://www.w3.org/2001/xml-events", "SVGUnload", l, false);
            }
        }
    }
    
    protected static void storeEventListener(final BridgeContext ctx, final EventTarget e, final String t, final EventListener l, final boolean c) {
        ctx.storeEventListener(e, t, l, c);
    }
    
    protected static void storeEventListenerNS(final BridgeContext ctx, final EventTarget e, final String n, final String t, final EventListener l, final boolean c) {
        ctx.storeEventListenerNS(e, n, t, l, c);
    }
    
    static {
        TEXT_COMPOUND_ID = GVTAttributedCharacterIterator.TextAttribute.TEXT_COMPOUND_ID;
    }
    
    protected static class GVTUnloadListener implements EventListener
    {
        protected EventDispatcher dispatcher;
        protected Listener listener;
        
        public GVTUnloadListener(final EventDispatcher dispatcher, final Listener listener) {
            this.dispatcher = dispatcher;
            this.listener = listener;
        }
        
        @Override
        public void handleEvent(final Event evt) {
            this.dispatcher.removeGraphicsNodeMouseListener(this.listener);
            this.dispatcher.removeGraphicsNodeKeyListener(this.listener);
            final NodeEventTarget et = (NodeEventTarget)evt.getTarget();
            et.removeEventListenerNS("http://www.w3.org/2001/xml-events", "SVGUnload", this, false);
        }
    }
    
    protected static class Listener implements GraphicsNodeMouseListener, GraphicsNodeKeyListener
    {
        protected BridgeContext context;
        protected UserAgent ua;
        protected Element lastTargetElement;
        protected boolean isDown;
        
        public Listener(final BridgeContext ctx, final UserAgent u) {
            this.context = ctx;
            this.ua = u;
        }
        
        @Override
        public void keyPressed(final GraphicsNodeKeyEvent evt) {
            if (!this.isDown) {
                this.isDown = true;
                this.dispatchKeyEvent("keydown", evt);
            }
            if (evt.getKeyChar() == '\uffff') {
                this.dispatchKeyEvent("keypress", evt);
            }
        }
        
        @Override
        public void keyReleased(final GraphicsNodeKeyEvent evt) {
            this.dispatchKeyEvent("keyup", evt);
            this.isDown = false;
        }
        
        @Override
        public void keyTyped(final GraphicsNodeKeyEvent evt) {
            this.dispatchKeyEvent("keypress", evt);
        }
        
        protected void dispatchKeyEvent(final String eventType, final GraphicsNodeKeyEvent evt) {
            final FocusManager fmgr = this.context.getFocusManager();
            if (fmgr == null) {
                return;
            }
            Element targetElement = (Element)fmgr.getCurrentEventTarget();
            if (targetElement == null) {
                targetElement = this.context.getDocument().getDocumentElement();
            }
            final DocumentEvent d = (DocumentEvent)targetElement.getOwnerDocument();
            final DOMKeyEvent keyEvt = (DOMKeyEvent)d.createEvent("KeyEvents");
            keyEvt.initKeyEvent(eventType, true, true, evt.isControlDown(), evt.isAltDown(), evt.isShiftDown(), evt.isMetaDown(), this.mapKeyCode(evt.getKeyCode()), evt.getKeyChar(), null);
            try {
                ((EventTarget)targetElement).dispatchEvent(keyEvt);
            }
            catch (RuntimeException e) {
                this.ua.displayError(e);
            }
        }
        
        protected final int mapKeyCode(final int keyCode) {
            switch (keyCode) {
                case 10: {
                    return 13;
                }
                case 262: {
                    return 0;
                }
                case 263: {
                    return 0;
                }
                default: {
                    return keyCode;
                }
            }
        }
        
        @Override
        public void mouseClicked(final GraphicsNodeMouseEvent evt) {
            this.dispatchMouseEvent("click", evt, true);
        }
        
        @Override
        public void mousePressed(final GraphicsNodeMouseEvent evt) {
            this.dispatchMouseEvent("mousedown", evt, true);
        }
        
        @Override
        public void mouseReleased(final GraphicsNodeMouseEvent evt) {
            this.dispatchMouseEvent("mouseup", evt, true);
        }
        
        @Override
        public void mouseEntered(final GraphicsNodeMouseEvent evt) {
            final Point clientXY = evt.getClientPoint();
            final GraphicsNode node = evt.getGraphicsNode();
            final Element targetElement = this.getEventTarget(node, evt.getPoint2D());
            final Element relatedElement = this.getRelatedElement(evt);
            this.dispatchMouseEvent("mouseover", targetElement, relatedElement, clientXY, evt, true);
        }
        
        @Override
        public void mouseExited(final GraphicsNodeMouseEvent evt) {
            final Point clientXY = evt.getClientPoint();
            final GraphicsNode node = evt.getRelatedNode();
            final Element targetElement = this.getEventTarget(node, evt.getPoint2D());
            if (this.lastTargetElement != null) {
                this.dispatchMouseEvent("mouseout", this.lastTargetElement, targetElement, clientXY, evt, true);
                this.lastTargetElement = null;
            }
        }
        
        @Override
        public void mouseDragged(final GraphicsNodeMouseEvent evt) {
            this.dispatchMouseEvent("mousemove", evt, false);
        }
        
        @Override
        public void mouseMoved(final GraphicsNodeMouseEvent evt) {
            final Point clientXY = evt.getClientPoint();
            final GraphicsNode node = evt.getGraphicsNode();
            final Element targetElement = this.getEventTarget(node, evt.getPoint2D());
            final Element holdLTE = this.lastTargetElement;
            if (holdLTE != targetElement) {
                if (holdLTE != null) {
                    this.dispatchMouseEvent("mouseout", holdLTE, targetElement, clientXY, evt, true);
                }
                if (targetElement != null) {
                    this.dispatchMouseEvent("mouseover", targetElement, holdLTE, clientXY, evt, true);
                }
            }
            this.dispatchMouseEvent("mousemove", targetElement, null, clientXY, evt, false);
        }
        
        protected void dispatchMouseEvent(final String eventType, final GraphicsNodeMouseEvent evt, final boolean cancelable) {
            final Point clientXY = evt.getClientPoint();
            final GraphicsNode node = evt.getGraphicsNode();
            final Element targetElement = this.getEventTarget(node, evt.getPoint2D());
            final Element relatedElement = this.getRelatedElement(evt);
            this.dispatchMouseEvent(eventType, targetElement, relatedElement, clientXY, evt, cancelable);
        }
        
        protected void dispatchMouseEvent(final String eventType, final Element targetElement, final Element relatedElement, final Point clientXY, final GraphicsNodeMouseEvent evt, final boolean cancelable) {
            if (targetElement == null) {
                return;
            }
            final Point screenXY = evt.getScreenPoint();
            final DocumentEvent d = (DocumentEvent)targetElement.getOwnerDocument();
            final DOMMouseEvent mouseEvt = (DOMMouseEvent)d.createEvent("MouseEvents");
            final String modifiers = DOMUtilities.getModifiersList(evt.getLockState(), evt.getModifiers());
            mouseEvt.initMouseEventNS("http://www.w3.org/2001/xml-events", eventType, true, cancelable, null, evt.getClickCount(), screenXY.x, screenXY.y, clientXY.x, clientXY.y, (short)(evt.getButton() - 1), (EventTarget)relatedElement, modifiers);
            try {
                ((EventTarget)targetElement).dispatchEvent(mouseEvt);
            }
            catch (RuntimeException e) {
                this.ua.displayError(e);
            }
            finally {
                this.lastTargetElement = targetElement;
            }
        }
        
        protected Element getRelatedElement(final GraphicsNodeMouseEvent evt) {
            final GraphicsNode relatedNode = evt.getRelatedNode();
            Element relatedElement = null;
            if (relatedNode != null) {
                relatedElement = this.context.getElement(relatedNode);
            }
            return relatedElement;
        }
        
        protected Element getEventTarget(final GraphicsNode node, final Point2D pt) {
            final Element target = this.context.getElement(node);
            if (target != null && node instanceof TextNode) {
                final TextNode textNode = (TextNode)node;
                final List list = textNode.getTextRuns();
                if (list != null) {
                    final float x = (float)pt.getX();
                    final float y = (float)pt.getY();
                    for (final Object aList : list) {
                        final StrokingTextPainter.TextRun run = (StrokingTextPainter.TextRun)aList;
                        final AttributedCharacterIterator aci = run.getACI();
                        final TextSpanLayout layout = run.getLayout();
                        final TextHit textHit = layout.hitTestChar(x, y);
                        final Rectangle2D bounds = layout.getBounds2D();
                        if (textHit != null && bounds != null && bounds.contains(x, y)) {
                            final SoftReference sr = (SoftReference)aci.getAttribute(BridgeEventSupport.TEXT_COMPOUND_ID);
                            final Object delimiter = sr.get();
                            if (delimiter instanceof Element) {
                                return (Element)delimiter;
                            }
                            continue;
                        }
                    }
                }
            }
            return target;
        }
    }
}
