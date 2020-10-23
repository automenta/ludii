// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom;

import java.util.ListIterator;
import org.apache.batik.util.Service;
import org.apache.batik.xml.XMLUtilities;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Document;
import org.apache.batik.dom.util.DOMUtilities;
import org.w3c.dom.Element;
import org.w3c.dom.css.ViewCSS;
import org.apache.batik.css.parser.ExtendedParser;
import org.apache.batik.css.parser.ExtendedParserWrapper;
import java.lang.reflect.InvocationTargetException;
import org.w3c.dom.DOMException;
import org.w3c.css.sac.Parser;
import org.apache.batik.util.XMLResourceDescriptor;
import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.CSSContext;
import org.apache.batik.css.engine.value.ShorthandManager;
import java.util.LinkedList;
import org.apache.batik.css.engine.value.ValueManager;
import java.util.Iterator;
import java.util.List;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.css.DOMImplementationCSS;

public abstract class ExtensibleDOMImplementation extends AbstractDOMImplementation implements DOMImplementationCSS, StyleSheetFactory
{
    protected DoublyIndexedTable customFactories;
    protected List customValueManagers;
    protected List customShorthandManagers;
    protected static List extensions;
    
    public ExtensibleDOMImplementation() {
        for (final Object o : getDomExtensions()) {
            final DomExtension de = (DomExtension)o;
            de.registerTags(this);
        }
    }
    
    public void registerCustomElementFactory(final String namespaceURI, final String localName, final ElementFactory factory) {
        if (this.customFactories == null) {
            this.customFactories = new DoublyIndexedTable();
        }
        this.customFactories.put(namespaceURI, localName, factory);
    }
    
    public void registerCustomCSSValueManager(final ValueManager vm) {
        if (this.customValueManagers == null) {
            this.customValueManagers = new LinkedList();
        }
        this.customValueManagers.add(vm);
    }
    
    public void registerCustomCSSShorthandManager(final ShorthandManager sm) {
        if (this.customShorthandManagers == null) {
            this.customShorthandManagers = new LinkedList();
        }
        this.customShorthandManagers.add(sm);
    }
    
    public CSSEngine createCSSEngine(final AbstractStylableDocument doc, final CSSContext ctx) {
        final String pn = XMLResourceDescriptor.getCSSParserClassName();
        Parser p;
        try {
            p = (Parser)Class.forName(pn).getDeclaredConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
        }
        catch (ClassNotFoundException e) {
            throw new DOMException((short)15, this.formatMessage("css.parser.class", new Object[] { pn }));
        }
        catch (InstantiationException e2) {
            throw new DOMException((short)15, this.formatMessage("css.parser.creation", new Object[] { pn }));
        }
        catch (IllegalAccessException e3) {
            throw new DOMException((short)15, this.formatMessage("css.parser.access", new Object[] { pn }));
        }
        catch (NoSuchMethodException e4) {
            throw new DOMException((short)15, this.formatMessage("css.parser.access", new Object[] { pn }));
        }
        catch (InvocationTargetException e5) {
            throw new DOMException((short)15, this.formatMessage("css.parser.access", new Object[] { pn }));
        }
        final ExtendedParser ep = ExtendedParserWrapper.wrap(p);
        ValueManager[] vms;
        if (this.customValueManagers == null) {
            vms = new ValueManager[0];
        }
        else {
            vms = new ValueManager[this.customValueManagers.size()];
            final Iterator it = this.customValueManagers.iterator();
            int i = 0;
            while (it.hasNext()) {
                vms[i++] = it.next();
            }
        }
        ShorthandManager[] sms;
        if (this.customShorthandManagers == null) {
            sms = new ShorthandManager[0];
        }
        else {
            sms = new ShorthandManager[this.customShorthandManagers.size()];
            final Iterator it2 = this.customShorthandManagers.iterator();
            int j = 0;
            while (it2.hasNext()) {
                sms[j++] = it2.next();
            }
        }
        final CSSEngine result = this.createCSSEngine(doc, ctx, ep, vms, sms);
        doc.setCSSEngine(result);
        return result;
    }
    
    public abstract CSSEngine createCSSEngine(final AbstractStylableDocument p0, final CSSContext p1, final ExtendedParser p2, final ValueManager[] p3, final ShorthandManager[] p4);
    
    public abstract ViewCSS createViewCSS(final AbstractStylableDocument p0);
    
    public Element createElementNS(final AbstractDocument document, String namespaceURI, final String qualifiedName) {
        if (namespaceURI != null && namespaceURI.length() == 0) {
            namespaceURI = null;
        }
        if (namespaceURI == null) {
            return new GenericElement(qualifiedName.intern(), document);
        }
        if (this.customFactories != null) {
            final String name = DOMUtilities.getLocalName(qualifiedName);
            final ElementFactory cef = (ElementFactory)this.customFactories.get(namespaceURI, name);
            if (cef != null) {
                return cef.create(DOMUtilities.getPrefix(qualifiedName), document);
            }
        }
        return new GenericElementNS(namespaceURI.intern(), qualifiedName.intern(), document);
    }
    
    @Override
    public DocumentType createDocumentType(String qualifiedName, final String publicId, final String systemId) {
        if (qualifiedName == null) {
            qualifiedName = "";
        }
        final int test = XMLUtilities.testXMLQName(qualifiedName);
        if ((test & 0x1) == 0x0) {
            throw new DOMException((short)5, this.formatMessage("xml.name", new Object[] { qualifiedName }));
        }
        if ((test & 0x2) == 0x0) {
            throw new DOMException((short)5, this.formatMessage("invalid.qname", new Object[] { qualifiedName }));
        }
        return new GenericDocumentType(qualifiedName, publicId, systemId);
    }
    
    protected static synchronized List getDomExtensions() {
        if (ExtensibleDOMImplementation.extensions != null) {
            return ExtensibleDOMImplementation.extensions;
        }
        ExtensibleDOMImplementation.extensions = new LinkedList();
        final Iterator iter = Service.providers(DomExtension.class);
    Label_0027:
        while (iter.hasNext()) {
            final DomExtension de = iter.next();
            final float priority = de.getPriority();
            final ListIterator li = ExtensibleDOMImplementation.extensions.listIterator();
            while (true) {
                while (li.hasNext()) {
                    final DomExtension lde = li.next();
                    if (lde.getPriority() > priority) {
                        li.previous();
                        li.add(de);
                        continue Label_0027;
                    }
                }
                li.add(de);
                continue;
            }
        }
        return ExtensibleDOMImplementation.extensions;
    }
    
    static {
        ExtensibleDOMImplementation.extensions = null;
    }
    
    public interface ElementFactory
    {
        Element create(final String p0, final Document p1);
    }
}
