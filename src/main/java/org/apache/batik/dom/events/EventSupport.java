// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.events;

import org.apache.batik.dom.AbstractDocument;
import java.util.Iterator;
import java.util.List;
import org.w3c.dom.events.EventException;
import java.util.Collection;
import java.util.HashSet;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.apache.batik.dom.AbstractNode;
import java.util.HashMap;

public class EventSupport
{
    protected HashMap<String, EventListenerList> capturingListeners;
    protected HashMap<String, EventListenerList> bubblingListeners;
    protected AbstractNode node;
    
    public EventSupport(final AbstractNode n) {
        this.node = n;
    }
    
    public void addEventListener(final String type, final EventListener listener, final boolean useCapture) {
        this.addEventListenerNS(null, type, listener, useCapture, null);
    }
    
    public void addEventListenerNS(final String namespaceURI, final String type, final EventListener listener, final boolean useCapture, final Object group) {
        HashMap<String, EventListenerList> listeners;
        if (useCapture) {
            if (this.capturingListeners == null) {
                this.capturingListeners = new HashMap<String, EventListenerList>();
            }
            listeners = this.capturingListeners;
        }
        else {
            if (this.bubblingListeners == null) {
                this.bubblingListeners = new HashMap<String, EventListenerList>();
            }
            listeners = this.bubblingListeners;
        }
        EventListenerList list = listeners.get(type);
        if (list == null) {
            list = new EventListenerList();
            listeners.put(type, list);
        }
        list.addListener(namespaceURI, group, listener);
    }
    
    public void removeEventListener(final String type, final EventListener listener, final boolean useCapture) {
        this.removeEventListenerNS(null, type, listener, useCapture);
    }
    
    public void removeEventListenerNS(final String namespaceURI, final String type, final EventListener listener, final boolean useCapture) {
        HashMap<String, EventListenerList> listeners;
        if (useCapture) {
            listeners = this.capturingListeners;
        }
        else {
            listeners = this.bubblingListeners;
        }
        if (listeners == null) {
            return;
        }
        final EventListenerList list = listeners.get(type);
        if (list != null) {
            list.removeListener(namespaceURI, listener);
            if (list.size() == 0) {
                listeners.remove(type);
            }
        }
    }
    
    public void moveEventListeners(final EventSupport other) {
        other.capturingListeners = this.capturingListeners;
        other.bubblingListeners = this.bubblingListeners;
        this.capturingListeners = null;
        this.bubblingListeners = null;
    }
    
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
        e.setTarget(target);
        e.stopPropagation(false);
        e.stopImmediatePropagation(false);
        e.preventDefault(false);
        final NodeEventTarget[] ancestors = this.getAncestors(target);
        e.setEventPhase((short)1);
        final HashSet stoppedGroups = new HashSet();
        final HashSet toBeStoppedGroups = new HashSet();
        for (final NodeEventTarget node : ancestors) {
            e.setCurrentTarget(node);
            this.fireEventListeners(node, e, true, stoppedGroups, toBeStoppedGroups);
            stoppedGroups.addAll(toBeStoppedGroups);
            toBeStoppedGroups.clear();
        }
        e.setEventPhase((short)2);
        e.setCurrentTarget(target);
        this.fireEventListeners(target, e, false, stoppedGroups, toBeStoppedGroups);
        stoppedGroups.addAll(toBeStoppedGroups);
        toBeStoppedGroups.clear();
        if (e.getBubbles()) {
            e.setEventPhase((short)3);
            for (int i = ancestors.length - 1; i >= 0; --i) {
                final NodeEventTarget node2 = ancestors[i];
                e.setCurrentTarget(node2);
                this.fireEventListeners(node2, e, false, stoppedGroups, toBeStoppedGroups);
                stoppedGroups.addAll(toBeStoppedGroups);
                toBeStoppedGroups.clear();
            }
        }
        if (!e.getDefaultPrevented()) {
            this.runDefaultActions(e);
        }
        return e.getDefaultPrevented();
    }
    
    protected void runDefaultActions(final AbstractEvent e) {
        final List runables = e.getDefaultActions();
        if (runables != null) {
            for (final Object runable : runables) {
                final Runnable r = (Runnable)runable;
                r.run();
            }
        }
    }
    
    protected void fireEventListeners(final NodeEventTarget node, final AbstractEvent e, final EventListenerList.Entry[] listeners, final HashSet stoppedGroups, final HashSet toBeStoppedGroups) {
        if (listeners == null) {
            return;
        }
        final String eventNS = e.getNamespaceURI();
        for (final EventListenerList.Entry listener : listeners) {
            try {
                final String listenerNS = listener.getNamespaceURI();
                if (listenerNS == null || eventNS == null || listenerNS.equals(eventNS)) {
                    final Object group = listener.getGroup();
                    if (stoppedGroups == null || !stoppedGroups.contains(group)) {
                        listener.getListener().handleEvent(e);
                        if (e.getStopImmediatePropagation()) {
                            if (stoppedGroups != null) {
                                stoppedGroups.add(group);
                            }
                            e.stopImmediatePropagation(false);
                        }
                        else if (e.getStopPropagation()) {
                            if (toBeStoppedGroups != null) {
                                toBeStoppedGroups.add(group);
                            }
                            e.stopPropagation(false);
                        }
                    }
                }
            }
            catch (ThreadDeath td) {
                throw td;
            }
            catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }
    
    protected void fireEventListeners(final NodeEventTarget node, final AbstractEvent e, final boolean useCapture, final HashSet stoppedGroups, final HashSet toBeStoppedGroups) {
        final String type = e.getType();
        final EventSupport support = node.getEventSupport();
        if (support == null) {
            return;
        }
        final EventListenerList list = support.getEventListeners(type, useCapture);
        if (list == null) {
            return;
        }
        final EventListenerList.Entry[] listeners = list.getEventListeners();
        this.fireEventListeners(node, e, listeners, stoppedGroups, toBeStoppedGroups);
    }
    
    protected NodeEventTarget[] getAncestors(NodeEventTarget node) {
        node = node.getParentNodeEventTarget();
        int nancestors = 0;
        for (NodeEventTarget n = node; n != null; n = n.getParentNodeEventTarget(), ++nancestors) {}
        final NodeEventTarget[] ancestors = new NodeEventTarget[nancestors];
        for (int i = nancestors - 1; i >= 0; --i, node = node.getParentNodeEventTarget()) {
            ancestors[i] = node;
        }
        return ancestors;
    }
    
    public boolean hasEventListenerNS(final String namespaceURI, final String type) {
        if (this.capturingListeners != null) {
            final EventListenerList ell = this.capturingListeners.get(type);
            if (ell != null && ell.hasEventListener(namespaceURI)) {
                return true;
            }
        }
        if (this.bubblingListeners != null) {
            final EventListenerList ell = this.capturingListeners.get(type);
            if (ell != null) {
                return ell.hasEventListener(namespaceURI);
            }
        }
        return false;
    }
    
    public EventListenerList getEventListeners(final String type, final boolean useCapture) {
        final HashMap<String, EventListenerList> listeners = useCapture ? this.capturingListeners : this.bubblingListeners;
        if (listeners == null) {
            return null;
        }
        return listeners.get(type);
    }
    
    protected EventException createEventException(final short code, final String key, final Object[] args) {
        try {
            final AbstractDocument doc = (AbstractDocument)this.node.getOwnerDocument();
            return new EventException(code, doc.formatMessage(key, args));
        }
        catch (Exception e) {
            return new EventException(code, key);
        }
    }
    
    protected void setTarget(final AbstractEvent e, final NodeEventTarget target) {
        e.setTarget(target);
    }
    
    protected void stopPropagation(final AbstractEvent e, final boolean b) {
        e.stopPropagation(b);
    }
    
    protected void stopImmediatePropagation(final AbstractEvent e, final boolean b) {
        e.stopImmediatePropagation(b);
    }
    
    protected void preventDefault(final AbstractEvent e, final boolean b) {
        e.preventDefault(b);
    }
    
    protected void setCurrentTarget(final AbstractEvent e, final NodeEventTarget target) {
        e.setCurrentTarget(target);
    }
    
    protected void setEventPhase(final AbstractEvent e, final short phase) {
        e.setEventPhase(phase);
    }
    
    public static Event getUltimateOriginalEvent(final Event evt) {
        AbstractEvent e = (AbstractEvent)evt;
        while (true) {
            final AbstractEvent origEvt = (AbstractEvent)e.getOriginalEvent();
            if (origEvt == null) {
                break;
            }
            e = origEvt;
        }
        return e;
    }
}
