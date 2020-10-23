// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.apache.batik.dom.xbl.ShadowTreeEvent;
import org.w3c.dom.events.MutationEvent;
import org.w3c.dom.NodeList;
import org.apache.batik.dom.xbl.NodeXBL;
import org.w3c.dom.events.EventException;
import org.w3c.dom.Node;
import java.util.Collection;
import java.util.HashSet;
import org.apache.batik.dom.events.AbstractEvent;
import org.w3c.dom.events.Event;
import org.apache.batik.dom.events.NodeEventTarget;
import org.w3c.dom.events.EventListener;
import org.apache.batik.dom.AbstractNode;
import org.apache.batik.dom.events.EventListenerList;
import java.util.HashMap;
import org.apache.batik.dom.events.EventSupport;

public class XBLEventSupport extends EventSupport
{
    protected HashMap<String, EventListenerList> capturingImplementationListeners;
    protected HashMap<String, EventListenerList> bubblingImplementationListeners;
    protected static HashMap<String, String> eventTypeAliases;
    
    public XBLEventSupport(final AbstractNode n) {
        super(n);
    }
    
    @Override
    public void addEventListenerNS(final String namespaceURI, final String type, final EventListener listener, final boolean useCapture, final Object group) {
        super.addEventListenerNS(namespaceURI, type, listener, useCapture, group);
        if (namespaceURI == null || namespaceURI.equals("http://www.w3.org/2001/xml-events")) {
            final String alias = XBLEventSupport.eventTypeAliases.get(type);
            if (alias != null) {
                super.addEventListenerNS(namespaceURI, alias, listener, useCapture, group);
            }
        }
    }
    
    @Override
    public void removeEventListenerNS(final String namespaceURI, final String type, final EventListener listener, final boolean useCapture) {
        super.removeEventListenerNS(namespaceURI, type, listener, useCapture);
        if (namespaceURI == null || namespaceURI.equals("http://www.w3.org/2001/xml-events")) {
            final String alias = XBLEventSupport.eventTypeAliases.get(type);
            if (alias != null) {
                super.removeEventListenerNS(namespaceURI, alias, listener, useCapture);
            }
        }
    }
    
    public void addImplementationEventListenerNS(final String namespaceURI, final String type, final EventListener listener, final boolean useCapture) {
        HashMap<String, EventListenerList> listeners;
        if (useCapture) {
            if (this.capturingImplementationListeners == null) {
                this.capturingImplementationListeners = new HashMap<String, EventListenerList>();
            }
            listeners = this.capturingImplementationListeners;
        }
        else {
            if (this.bubblingImplementationListeners == null) {
                this.bubblingImplementationListeners = new HashMap<String, EventListenerList>();
            }
            listeners = this.bubblingImplementationListeners;
        }
        EventListenerList list = listeners.get(type);
        if (list == null) {
            list = new EventListenerList();
            listeners.put(type, list);
        }
        list.addListener(namespaceURI, null, listener);
    }
    
    public void removeImplementationEventListenerNS(final String namespaceURI, final String type, final EventListener listener, final boolean useCapture) {
        final HashMap<String, EventListenerList> listeners = useCapture ? this.capturingImplementationListeners : this.bubblingImplementationListeners;
        if (listeners == null) {
            return;
        }
        final EventListenerList list = listeners.get(type);
        if (list == null) {
            return;
        }
        list.removeListener(namespaceURI, listener);
        if (list.size() == 0) {
            listeners.remove(type);
        }
    }
    
    @Override
    public void moveEventListeners(final EventSupport other) {
        super.moveEventListeners(other);
        final XBLEventSupport es = (XBLEventSupport)other;
        es.capturingImplementationListeners = this.capturingImplementationListeners;
        es.bubblingImplementationListeners = this.bubblingImplementationListeners;
        this.capturingImplementationListeners = null;
        this.bubblingImplementationListeners = null;
    }
    
    @Override
    public boolean dispatchEvent(final NodeEventTarget target, final Event evt) throws EventException {
        if (evt == null) {
            return false;
        }
        if (!(evt instanceof AbstractEvent)) {
            throw this.createEventException((short)9, "unsupported.event", new Object[0]);
        }
        final AbstractEvent e = (AbstractEvent)evt;
        final String type = e.getType();
        if (type == null || type.length() == 0) {
            throw this.createEventException((short)0, "unspecified.event", new Object[0]);
        }
        this.setTarget(e, target);
        this.stopPropagation(e, false);
        this.stopImmediatePropagation(e, false);
        this.preventDefault(e, false);
        final NodeEventTarget[] ancestors = this.getAncestors(target);
        final int bubbleLimit = e.getBubbleLimit();
        int minAncestor = 0;
        if (this.isSingleScopeEvent(e)) {
            final AbstractNode targetNode = (AbstractNode)target;
            final Node boundElement = targetNode.getXblBoundElement();
            if (boundElement != null) {
                for (minAncestor = ancestors.length; minAncestor > 0; --minAncestor) {
                    final AbstractNode ancestorNode = (AbstractNode)ancestors[minAncestor - 1];
                    if (ancestorNode.getXblBoundElement() != boundElement) {
                        break;
                    }
                }
            }
        }
        else if (bubbleLimit != 0) {
            minAncestor = ancestors.length - bubbleLimit + 1;
            if (minAncestor < 0) {
                minAncestor = 0;
            }
        }
        final AbstractEvent[] es = this.getRetargettedEvents(target, ancestors, e);
        boolean preventDefault = false;
        final HashSet stoppedGroups = new HashSet();
        final HashSet toBeStoppedGroups = new HashSet();
        for (int i = 0; i < minAncestor; ++i) {
            final NodeEventTarget node = ancestors[i];
            this.setCurrentTarget(es[i], node);
            this.setEventPhase(es[i], (short)1);
            this.fireImplementationEventListeners(node, es[i], true);
        }
        for (int i = minAncestor; i < ancestors.length; ++i) {
            final NodeEventTarget node = ancestors[i];
            this.setCurrentTarget(es[i], node);
            this.setEventPhase(es[i], (short)1);
            this.fireImplementationEventListeners(node, es[i], true);
            this.fireEventListeners(node, es[i], true, stoppedGroups, toBeStoppedGroups);
            this.fireHandlerGroupEventListeners(node, es[i], true, stoppedGroups, toBeStoppedGroups);
            preventDefault = (preventDefault || es[i].getDefaultPrevented());
            stoppedGroups.addAll(toBeStoppedGroups);
            toBeStoppedGroups.clear();
        }
        this.setEventPhase(e, (short)2);
        this.setCurrentTarget(e, target);
        this.fireImplementationEventListeners(target, e, false);
        this.fireEventListeners(target, e, false, stoppedGroups, toBeStoppedGroups);
        this.fireHandlerGroupEventListeners(this.node, e, false, stoppedGroups, toBeStoppedGroups);
        stoppedGroups.addAll(toBeStoppedGroups);
        toBeStoppedGroups.clear();
        preventDefault = (preventDefault || e.getDefaultPrevented());
        if (e.getBubbles()) {
            for (int i = ancestors.length - 1; i >= minAncestor; --i) {
                final NodeEventTarget node = ancestors[i];
                this.setCurrentTarget(es[i], node);
                this.setEventPhase(es[i], (short)3);
                this.fireImplementationEventListeners(node, es[i], false);
                this.fireEventListeners(node, es[i], false, stoppedGroups, toBeStoppedGroups);
                this.fireHandlerGroupEventListeners(node, es[i], false, stoppedGroups, toBeStoppedGroups);
                preventDefault = (preventDefault || es[i].getDefaultPrevented());
                stoppedGroups.addAll(toBeStoppedGroups);
                toBeStoppedGroups.clear();
            }
            for (int i = minAncestor - 1; i >= 0; --i) {
                final NodeEventTarget node = ancestors[i];
                this.setCurrentTarget(es[i], node);
                this.setEventPhase(es[i], (short)3);
                this.fireImplementationEventListeners(node, es[i], false);
                preventDefault = (preventDefault || es[i].getDefaultPrevented());
            }
        }
        if (!preventDefault) {
            this.runDefaultActions(e);
        }
        return preventDefault;
    }
    
    protected void fireHandlerGroupEventListeners(NodeEventTarget node, final AbstractEvent e, final boolean useCapture, final HashSet stoppedGroups, final HashSet toBeStoppedGroups) {
        final NodeList defs = ((NodeXBL)node).getXblDefinitions();
        for (int j = 0; j < defs.getLength(); ++j) {
            Node n;
            for (n = defs.item(j).getFirstChild(); n != null && !(n instanceof XBLOMHandlerGroupElement); n = n.getNextSibling()) {}
            if (n != null) {
                node = (NodeEventTarget)n;
                final String type = e.getType();
                final EventSupport support = node.getEventSupport();
                if (support != null) {
                    final EventListenerList list = support.getEventListeners(type, useCapture);
                    if (list == null) {
                        return;
                    }
                    final EventListenerList.Entry[] listeners = list.getEventListeners();
                    this.fireEventListeners(node, e, listeners, stoppedGroups, toBeStoppedGroups);
                }
            }
        }
    }
    
    protected boolean isSingleScopeEvent(final Event evt) {
        return evt instanceof MutationEvent || evt instanceof ShadowTreeEvent;
    }
    
    protected AbstractEvent[] getRetargettedEvents(final NodeEventTarget target, final NodeEventTarget[] ancestors, final AbstractEvent e) {
        final boolean singleScope = this.isSingleScopeEvent(e);
        final AbstractNode targetNode = (AbstractNode)target;
        final AbstractEvent[] es = new AbstractEvent[ancestors.length];
        if (ancestors.length > 0) {
            int index = ancestors.length - 1;
            Node boundElement = targetNode.getXblBoundElement();
            AbstractNode ancestorNode = (AbstractNode)ancestors[index];
            if (!singleScope && ancestorNode.getXblBoundElement() != boundElement) {
                es[index] = this.retargetEvent(e, ancestors[index]);
            }
            else {
                es[index] = e;
            }
            while (--index >= 0) {
                ancestorNode = (AbstractNode)ancestors[index + 1];
                boundElement = ancestorNode.getXblBoundElement();
                final AbstractNode nextAncestorNode = (AbstractNode)ancestors[index];
                final Node nextBoundElement = nextAncestorNode.getXblBoundElement();
                if (!singleScope && nextBoundElement != boundElement) {
                    es[index] = this.retargetEvent(es[index + 1], ancestors[index]);
                }
                else {
                    es[index] = es[index + 1];
                }
            }
        }
        return es;
    }
    
    protected AbstractEvent retargetEvent(final AbstractEvent e, final NodeEventTarget target) {
        final AbstractEvent clonedEvent = e.cloneEvent();
        this.setTarget(clonedEvent, target);
        return clonedEvent;
    }
    
    public EventListenerList getImplementationEventListeners(final String type, final boolean useCapture) {
        final HashMap<String, EventListenerList> listeners = useCapture ? this.capturingImplementationListeners : this.bubblingImplementationListeners;
        return (listeners != null) ? listeners.get(type) : null;
    }
    
    protected void fireImplementationEventListeners(final NodeEventTarget node, final AbstractEvent e, final boolean useCapture) {
        final String type = e.getType();
        final XBLEventSupport support = (XBLEventSupport)node.getEventSupport();
        if (support == null) {
            return;
        }
        final EventListenerList list = support.getImplementationEventListeners(type, useCapture);
        if (list == null) {
            return;
        }
        final EventListenerList.Entry[] listeners = list.getEventListeners();
        this.fireEventListeners(node, e, listeners, null, null);
    }
    
    static {
        (XBLEventSupport.eventTypeAliases = new HashMap<String, String>()).put("SVGLoad", "load");
        XBLEventSupport.eventTypeAliases.put("SVGUnoad", "unload");
        XBLEventSupport.eventTypeAliases.put("SVGAbort", "abort");
        XBLEventSupport.eventTypeAliases.put("SVGError", "error");
        XBLEventSupport.eventTypeAliases.put("SVGResize", "resize");
        XBLEventSupport.eventTypeAliases.put("SVGScroll", "scroll");
        XBLEventSupport.eventTypeAliases.put("SVGZoom", "zoom");
    }
}
