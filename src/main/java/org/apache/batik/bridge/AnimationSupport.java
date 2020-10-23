// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.apache.batik.dom.svg.IdContainer;
import org.apache.batik.dom.svg.SVGOMUseShadowRoot;
import org.w3c.dom.Element;
import org.apache.batik.anim.dom.SVGOMAnimationElement;
import org.apache.batik.anim.timing.TimedElement;
import org.w3c.dom.events.Event;
import org.w3c.dom.views.AbstractView;
import org.apache.batik.dom.events.DOMTimeEvent;
import org.w3c.dom.Node;
import org.w3c.dom.events.DocumentEvent;
import java.util.Calendar;
import org.w3c.dom.events.EventTarget;

public abstract class AnimationSupport
{
    public static void fireTimeEvent(final EventTarget target, final String eventType, final Calendar time, final int detail) {
        final DocumentEvent de = (DocumentEvent)((Node)target).getOwnerDocument();
        final DOMTimeEvent evt = (DOMTimeEvent)de.createEvent("TimeEvent");
        evt.initTimeEventNS("http://www.w3.org/2001/xml-events", eventType, null, detail);
        evt.setTimestamp(time.getTime().getTime());
        target.dispatchEvent(evt);
    }
    
    public static TimedElement getTimedElementById(final String id, final Node n) {
        final Element e = getElementById(id, n);
        if (e instanceof SVGOMAnimationElement) {
            final SVGAnimationElementBridge b = (SVGAnimationElementBridge)((SVGOMAnimationElement)e).getSVGContext();
            return b.getTimedElement();
        }
        return null;
    }
    
    public static EventTarget getEventTargetById(final String id, final Node n) {
        return (EventTarget)getElementById(id, n);
    }
    
    protected static Element getElementById(final String id, Node n) {
        Node p = n.getParentNode();
        while (p != null) {
            n = p;
            if (n instanceof SVGOMUseShadowRoot) {
                p = ((SVGOMUseShadowRoot)n).getCSSParentNode();
            }
            else {
                p = n.getParentNode();
            }
        }
        if (n instanceof IdContainer) {
            return ((IdContainer)n).getElementById(id);
        }
        return null;
    }
}
