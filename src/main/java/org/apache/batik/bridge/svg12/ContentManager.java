// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge.svg12;

import org.w3c.dom.events.EventTarget;
import org.w3c.dom.Attr;
import org.w3c.dom.events.MutationEvent;
import org.w3c.dom.events.Event;
import java.util.Set;
import java.util.Collection;
import java.util.HashSet;
import javax.swing.event.EventListenerList;
import org.apache.batik.anim.dom.XBLOMContentElement;
import java.util.Iterator;
import org.apache.batik.dom.events.NodeEventTarget;
import org.w3c.dom.NodeList;
import java.util.Map;
import org.apache.batik.dom.AbstractNode;
import org.w3c.dom.events.EventListener;
import org.apache.batik.anim.dom.XBLEventSupport;
import org.apache.batik.dom.xbl.XBLManager;
import org.w3c.dom.Node;
import java.util.LinkedList;
import java.util.HashMap;
import org.w3c.dom.Element;
import org.apache.batik.anim.dom.XBLOMShadowTreeElement;

public class ContentManager
{
    protected XBLOMShadowTreeElement shadowTree;
    protected Element boundElement;
    protected DefaultXBLManager xblManager;
    protected HashMap selectors;
    protected HashMap selectedNodes;
    protected LinkedList contentElementList;
    protected Node removedNode;
    protected HashMap listeners;
    protected ContentElementDOMAttrModifiedEventListener contentElementDomAttrModifiedEventListener;
    protected DOMAttrModifiedEventListener domAttrModifiedEventListener;
    protected DOMNodeInsertedEventListener domNodeInsertedEventListener;
    protected DOMNodeRemovedEventListener domNodeRemovedEventListener;
    protected DOMSubtreeModifiedEventListener domSubtreeModifiedEventListener;
    protected ShadowTreeNodeInsertedListener shadowTreeNodeInsertedListener;
    protected ShadowTreeNodeRemovedListener shadowTreeNodeRemovedListener;
    protected ShadowTreeSubtreeModifiedListener shadowTreeSubtreeModifiedListener;
    
    public ContentManager(final XBLOMShadowTreeElement s, final XBLManager xm) {
        this.selectors = new HashMap();
        this.selectedNodes = new HashMap();
        this.contentElementList = new LinkedList();
        this.listeners = new HashMap();
        this.shadowTree = s;
        (this.xblManager = (DefaultXBLManager)xm).setContentManager(s, this);
        this.boundElement = this.xblManager.getXblBoundElement(s);
        this.contentElementDomAttrModifiedEventListener = new ContentElementDOMAttrModifiedEventListener();
        XBLEventSupport es = (XBLEventSupport)this.shadowTree.initializeEventSupport();
        this.shadowTreeNodeInsertedListener = new ShadowTreeNodeInsertedListener();
        this.shadowTreeNodeRemovedListener = new ShadowTreeNodeRemovedListener();
        this.shadowTreeSubtreeModifiedListener = new ShadowTreeSubtreeModifiedListener();
        es.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeInserted", this.shadowTreeNodeInsertedListener, true);
        es.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeRemoved", this.shadowTreeNodeRemovedListener, true);
        es.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMSubtreeModified", this.shadowTreeSubtreeModifiedListener, true);
        es = (XBLEventSupport)((AbstractNode)this.boundElement).initializeEventSupport();
        this.domAttrModifiedEventListener = new DOMAttrModifiedEventListener();
        this.domNodeInsertedEventListener = new DOMNodeInsertedEventListener();
        this.domNodeRemovedEventListener = new DOMNodeRemovedEventListener();
        this.domSubtreeModifiedEventListener = new DOMSubtreeModifiedEventListener();
        es.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMAttrModified", this.domAttrModifiedEventListener, true);
        es.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeInserted", this.domNodeInsertedEventListener, true);
        es.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeRemoved", this.domNodeRemovedEventListener, true);
        es.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMSubtreeModified", this.domSubtreeModifiedEventListener, false);
        this.update(true);
    }
    
    public void dispose() {
        this.xblManager.setContentManager(this.shadowTree, null);
        for (final Map.Entry e : this.selectedNodes.entrySet()) {
            final NodeList nl = e.getValue();
            for (int j = 0; j < nl.getLength(); ++j) {
                final Node n = nl.item(j);
                this.xblManager.getRecord(n).contentElement = null;
            }
        }
        for (final NodeEventTarget n2 : this.contentElementList) {
            n2.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMAttrModified", this.contentElementDomAttrModifiedEventListener, false);
        }
        this.contentElementList.clear();
        this.selectedNodes.clear();
        final XBLEventSupport es = (XBLEventSupport)((AbstractNode)this.boundElement).getEventSupport();
        es.removeImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMAttrModified", this.domAttrModifiedEventListener, true);
        es.removeImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeInserted", this.domNodeInsertedEventListener, true);
        es.removeImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeRemoved", this.domNodeRemovedEventListener, true);
        es.removeImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMSubtreeModified", this.domSubtreeModifiedEventListener, false);
    }
    
    public NodeList getSelectedContent(final XBLOMContentElement e) {
        return this.selectedNodes.get(e);
    }
    
    protected XBLOMContentElement getContentElement(final Node n) {
        return this.xblManager.getXblContentElement(n);
    }
    
    public void addContentSelectionChangedListener(final XBLOMContentElement e, final ContentSelectionChangedListener l) {
        EventListenerList ll = this.listeners.get(e);
        if (ll == null) {
            ll = new EventListenerList();
            this.listeners.put(e, ll);
        }
        ll.add(ContentSelectionChangedListener.class, l);
    }
    
    public void removeContentSelectionChangedListener(final XBLOMContentElement e, final ContentSelectionChangedListener l) {
        final EventListenerList ll = this.listeners.get(e);
        if (ll != null) {
            ll.remove(ContentSelectionChangedListener.class, l);
        }
    }
    
    protected void dispatchContentSelectionChangedEvent(final XBLOMContentElement e) {
        this.xblManager.invalidateChildNodes(e.getXblParentNode());
        final ContentSelectionChangedEvent evt = new ContentSelectionChangedEvent(e);
        final EventListenerList ll = this.listeners.get(e);
        if (ll != null) {
            final Object[] ls = ll.getListenerList();
            for (int i = ls.length - 2; i >= 0; i -= 2) {
                final ContentSelectionChangedListener l = (ContentSelectionChangedListener)ls[i + 1];
                l.contentSelectionChanged(evt);
            }
        }
        final Object[] ls = this.xblManager.getContentSelectionChangedListeners();
        for (int i = ls.length - 2; i >= 0; i -= 2) {
            final ContentSelectionChangedListener l = (ContentSelectionChangedListener)ls[i + 1];
            l.contentSelectionChanged(evt);
        }
    }
    
    protected void update(final boolean first) {
        final HashSet previouslySelectedNodes = new HashSet();
        for (final Map.Entry e : this.selectedNodes.entrySet()) {
            final NodeList nl = e.getValue();
            for (int j = 0; j < nl.getLength(); ++j) {
                final Node n = nl.item(j);
                this.xblManager.getRecord(n).contentElement = null;
                previouslySelectedNodes.add(n);
            }
        }
        for (final NodeEventTarget n2 : this.contentElementList) {
            n2.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMAttrModified", this.contentElementDomAttrModifiedEventListener, false);
        }
        this.contentElementList.clear();
        this.selectedNodes.clear();
        boolean updated = false;
        for (Node n3 = this.shadowTree.getFirstChild(); n3 != null; n3 = n3.getNextSibling()) {
            if (this.update(first, n3)) {
                updated = true;
            }
        }
        if (updated) {
            final HashSet newlySelectedNodes = new HashSet();
            for (final Map.Entry e2 : this.selectedNodes.entrySet()) {
                final NodeList nl2 = e2.getValue();
                for (int k = 0; k < nl2.getLength(); ++k) {
                    final Node n4 = nl2.item(k);
                    newlySelectedNodes.add(n4);
                }
            }
            final HashSet removed = new HashSet();
            removed.addAll(previouslySelectedNodes);
            removed.removeAll(newlySelectedNodes);
            final HashSet added = new HashSet();
            added.addAll(newlySelectedNodes);
            added.removeAll(previouslySelectedNodes);
            if (!first) {
                this.xblManager.shadowTreeSelectedContentChanged(removed, added);
            }
        }
    }
    
    protected boolean update(final boolean first, final Node n) {
        boolean updated = false;
        for (Node m = n.getFirstChild(); m != null; m = m.getNextSibling()) {
            if (this.update(first, m)) {
                updated = true;
            }
        }
        if (n instanceof XBLOMContentElement) {
            this.contentElementList.add(n);
            final XBLOMContentElement e = (XBLOMContentElement)n;
            e.addEventListenerNS("http://www.w3.org/2001/xml-events", "DOMAttrModified", this.contentElementDomAttrModifiedEventListener, false, null);
            AbstractContentSelector s = this.selectors.get(n);
            boolean changed;
            if (s == null) {
                if (e.hasAttributeNS(null, "includes")) {
                    final String lang = this.getContentSelectorLanguage(e);
                    final String selector = e.getAttributeNS(null, "includes");
                    s = AbstractContentSelector.createSelector(lang, this, e, this.boundElement, selector);
                }
                else {
                    s = new DefaultContentSelector(this, e, this.boundElement);
                }
                this.selectors.put(n, s);
                changed = true;
            }
            else {
                changed = s.update();
            }
            final NodeList selectedContent = s.getSelectedContent();
            this.selectedNodes.put(n, selectedContent);
            for (int i = 0; i < selectedContent.getLength(); ++i) {
                final Node j = selectedContent.item(i);
                this.xblManager.getRecord(j).contentElement = e;
            }
            if (changed) {
                updated = true;
                this.dispatchContentSelectionChangedEvent(e);
            }
        }
        return updated;
    }
    
    protected String getContentSelectorLanguage(final Element e) {
        String lang = e.getAttributeNS("http://xml.apache.org/batik/ext", "selectorLanguage");
        if (lang.length() != 0) {
            return lang;
        }
        lang = e.getOwnerDocument().getDocumentElement().getAttributeNS("http://xml.apache.org/batik/ext", "selectorLanguage");
        if (lang.length() != 0) {
            return lang;
        }
        return null;
    }
    
    protected class ContentElementDOMAttrModifiedEventListener implements EventListener
    {
        @Override
        public void handleEvent(final Event evt) {
            final MutationEvent me = (MutationEvent)evt;
            final Attr a = (Attr)me.getRelatedNode();
            final Element e = (Element)evt.getTarget();
            if (e instanceof XBLOMContentElement) {
                final String ans = a.getNamespaceURI();
                String aln = a.getLocalName();
                if (aln == null) {
                    aln = a.getNodeName();
                }
                if ((ans == null && "includes".equals(aln)) || ("http://xml.apache.org/batik/ext".equals(ans) && "selectorLanguage".equals(aln))) {
                    ContentManager.this.selectors.remove(e);
                    ContentManager.this.update(false);
                }
            }
        }
    }
    
    protected class DOMAttrModifiedEventListener implements EventListener
    {
        @Override
        public void handleEvent(final Event evt) {
            if (evt.getTarget() != ContentManager.this.boundElement) {
                ContentManager.this.update(false);
            }
        }
    }
    
    protected class DOMNodeInsertedEventListener implements EventListener
    {
        @Override
        public void handleEvent(final Event evt) {
            ContentManager.this.update(false);
        }
    }
    
    protected class DOMNodeRemovedEventListener implements EventListener
    {
        @Override
        public void handleEvent(final Event evt) {
            ContentManager.this.removedNode = (Node)evt.getTarget();
        }
    }
    
    protected class DOMSubtreeModifiedEventListener implements EventListener
    {
        @Override
        public void handleEvent(final Event evt) {
            if (ContentManager.this.removedNode != null) {
                ContentManager.this.removedNode = null;
                ContentManager.this.update(false);
            }
        }
    }
    
    protected class ShadowTreeNodeInsertedListener implements EventListener
    {
        @Override
        public void handleEvent(final Event evt) {
            if (evt.getTarget() instanceof XBLOMContentElement) {
                ContentManager.this.update(false);
            }
        }
    }
    
    protected class ShadowTreeNodeRemovedListener implements EventListener
    {
        @Override
        public void handleEvent(final Event evt) {
            final EventTarget target = evt.getTarget();
            if (target instanceof XBLOMContentElement) {
                ContentManager.this.removedNode = (Node)evt.getTarget();
            }
        }
    }
    
    protected class ShadowTreeSubtreeModifiedListener implements EventListener
    {
        @Override
        public void handleEvent(final Event evt) {
            if (ContentManager.this.removedNode != null) {
                ContentManager.this.removedNode = null;
                ContentManager.this.update(false);
            }
        }
    }
}
