// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine;

import org.w3c.dom.events.MutationEvent;
import org.w3c.dom.events.Event;
import org.w3c.css.sac.CSSException;
import org.w3c.dom.Attr;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.SelectorList;
import org.apache.batik.css.engine.sac.ExtendedSelector;
import org.apache.batik.css.engine.value.ComputedValue;
import org.apache.batik.css.engine.value.InheritValue;
import java.util.Iterator;
import org.w3c.dom.NamedNodeMap;
import org.w3c.css.sac.DocumentHandler;
import org.w3c.css.sac.ConditionFactory;
import org.apache.batik.css.engine.sac.CSSSelectorFactory;
import org.apache.batik.css.engine.value.Value;
import org.w3c.css.sac.LexicalUnit;
import java.util.ArrayList;
import org.w3c.dom.DOMException;
import org.w3c.dom.events.EventTarget;
import java.util.HashSet;
import java.util.Collections;
import java.util.LinkedList;
import org.w3c.dom.Element;
import org.apache.batik.css.engine.sac.CSSConditionFactory;
import org.w3c.dom.Node;
import org.w3c.dom.events.EventListener;
import java.util.Set;
import java.util.List;
import org.w3c.css.sac.SACMediaList;
import org.apache.batik.css.parser.ExtendedParser;
import org.apache.batik.css.engine.value.ShorthandManager;
import org.apache.batik.css.engine.value.ValueManager;
import org.apache.batik.util.ParsedURL;
import org.w3c.dom.Document;

public abstract class CSSEngine
{
    protected CSSEngineUserAgent userAgent;
    protected CSSContext cssContext;
    protected Document document;
    protected ParsedURL documentURI;
    protected boolean isCSSNavigableDocument;
    protected StringIntMap indexes;
    protected StringIntMap shorthandIndexes;
    protected ValueManager[] valueManagers;
    protected ShorthandManager[] shorthandManagers;
    protected ExtendedParser parser;
    protected String[] pseudoElementNames;
    protected int fontSizeIndex;
    protected int lineHeightIndex;
    protected int colorIndex;
    protected StyleSheet userAgentStyleSheet;
    protected StyleSheet userStyleSheet;
    protected SACMediaList media;
    protected List styleSheetNodes;
    protected List fontFaces;
    protected String styleNamespaceURI;
    protected String styleLocalName;
    protected String classNamespaceURI;
    protected String classLocalName;
    protected Set nonCSSPresentationalHints;
    protected String nonCSSPresentationalHintsNamespaceURI;
    protected StyleDeclarationDocumentHandler styleDeclarationDocumentHandler;
    protected StyleDeclarationUpdateHandler styleDeclarationUpdateHandler;
    protected StyleSheetDocumentHandler styleSheetDocumentHandler;
    protected StyleDeclarationBuilder styleDeclarationBuilder;
    protected CSSStylableElement element;
    protected ParsedURL cssBaseURI;
    protected String alternateStyleSheet;
    protected CSSNavigableDocumentHandler cssNavigableDocumentListener;
    protected EventListener domAttrModifiedListener;
    protected EventListener domNodeInsertedListener;
    protected EventListener domNodeRemovedListener;
    protected EventListener domSubtreeModifiedListener;
    protected EventListener domCharacterDataModifiedListener;
    protected boolean styleSheetRemoved;
    protected Node removedStylableElementSibling;
    protected List listeners;
    protected Set selectorAttributes;
    protected final int[] ALL_PROPERTIES;
    protected CSSConditionFactory cssConditionFactory;
    protected static final CSSEngineListener[] LISTENER_ARRAY;
    
    public static Node getCSSParentNode(final Node n) {
        if (n instanceof CSSNavigableNode) {
            return ((CSSNavigableNode)n).getCSSParentNode();
        }
        return n.getParentNode();
    }
    
    protected static Node getCSSFirstChild(final Node n) {
        if (n instanceof CSSNavigableNode) {
            return ((CSSNavigableNode)n).getCSSFirstChild();
        }
        return n.getFirstChild();
    }
    
    protected static Node getCSSNextSibling(final Node n) {
        if (n instanceof CSSNavigableNode) {
            return ((CSSNavigableNode)n).getCSSNextSibling();
        }
        return n.getNextSibling();
    }
    
    protected static Node getCSSPreviousSibling(final Node n) {
        if (n instanceof CSSNavigableNode) {
            return ((CSSNavigableNode)n).getCSSPreviousSibling();
        }
        return n.getPreviousSibling();
    }
    
    public static CSSStylableElement getParentCSSStylableElement(final Element elt) {
        for (Node n = getCSSParentNode(elt); n != null; n = getCSSParentNode(n)) {
            if (n instanceof CSSStylableElement) {
                return (CSSStylableElement)n;
            }
        }
        return null;
    }
    
    protected CSSEngine(final Document doc, final ParsedURL uri, final ExtendedParser p, final ValueManager[] vm, final ShorthandManager[] sm, final String[] pe, final String sns, final String sln, final String cns, final String cln, final boolean hints, final String hintsNS, final CSSContext ctx) {
        this.fontSizeIndex = -1;
        this.lineHeightIndex = -1;
        this.colorIndex = -1;
        this.fontFaces = new LinkedList();
        this.styleDeclarationDocumentHandler = new StyleDeclarationDocumentHandler();
        this.styleSheetDocumentHandler = new StyleSheetDocumentHandler();
        this.styleDeclarationBuilder = new StyleDeclarationBuilder();
        this.listeners = Collections.synchronizedList(new LinkedList<Object>());
        this.document = doc;
        this.documentURI = uri;
        this.parser = p;
        this.pseudoElementNames = pe;
        this.styleNamespaceURI = sns;
        this.styleLocalName = sln;
        this.classNamespaceURI = cns;
        this.classLocalName = cln;
        this.cssContext = ctx;
        this.isCSSNavigableDocument = (doc instanceof CSSNavigableDocument);
        this.cssConditionFactory = new CSSConditionFactory(cns, cln, null, "id");
        int len = vm.length;
        this.indexes = new StringIntMap(len);
        this.valueManagers = vm;
        for (int i = len - 1; i >= 0; --i) {
            final String pn = vm[i].getPropertyName();
            this.indexes.put(pn, i);
            if (this.fontSizeIndex == -1 && pn.equals("font-size")) {
                this.fontSizeIndex = i;
            }
            if (this.lineHeightIndex == -1 && pn.equals("line-height")) {
                this.lineHeightIndex = i;
            }
            if (this.colorIndex == -1 && pn.equals("color")) {
                this.colorIndex = i;
            }
        }
        len = sm.length;
        this.shorthandIndexes = new StringIntMap(len);
        this.shorthandManagers = sm;
        for (int i = len - 1; i >= 0; --i) {
            this.shorthandIndexes.put(sm[i].getPropertyName(), i);
        }
        if (hints) {
            this.nonCSSPresentationalHints = new HashSet(vm.length + sm.length);
            this.nonCSSPresentationalHintsNamespaceURI = hintsNS;
            len = vm.length;
            for (int i = 0; i < len; ++i) {
                final String pn = vm[i].getPropertyName();
                this.nonCSSPresentationalHints.add(pn);
            }
            len = sm.length;
            for (int i = 0; i < len; ++i) {
                final String pn = sm[i].getPropertyName();
                this.nonCSSPresentationalHints.add(pn);
            }
        }
        if (this.cssContext.isDynamic() && this.document instanceof EventTarget) {
            this.addEventListeners((EventTarget)this.document);
            this.styleDeclarationUpdateHandler = new StyleDeclarationUpdateHandler();
        }
        this.ALL_PROPERTIES = new int[this.getNumberOfProperties()];
        for (int i = this.getNumberOfProperties() - 1; i >= 0; --i) {
            this.ALL_PROPERTIES[i] = i;
        }
    }
    
    protected void addEventListeners(final EventTarget doc) {
        if (this.isCSSNavigableDocument) {
            this.cssNavigableDocumentListener = new CSSNavigableDocumentHandler();
            final CSSNavigableDocument cnd = (CSSNavigableDocument)doc;
            cnd.addCSSNavigableDocumentListener(this.cssNavigableDocumentListener);
        }
        else {
            doc.addEventListener("DOMAttrModified", this.domAttrModifiedListener = new DOMAttrModifiedListener(), false);
            doc.addEventListener("DOMNodeInserted", this.domNodeInsertedListener = new DOMNodeInsertedListener(), false);
            doc.addEventListener("DOMNodeRemoved", this.domNodeRemovedListener = new DOMNodeRemovedListener(), false);
            doc.addEventListener("DOMSubtreeModified", this.domSubtreeModifiedListener = new DOMSubtreeModifiedListener(), false);
            doc.addEventListener("DOMCharacterDataModified", this.domCharacterDataModifiedListener = new DOMCharacterDataModifiedListener(), false);
        }
    }
    
    protected void removeEventListeners(final EventTarget doc) {
        if (this.isCSSNavigableDocument) {
            final CSSNavigableDocument cnd = (CSSNavigableDocument)doc;
            cnd.removeCSSNavigableDocumentListener(this.cssNavigableDocumentListener);
        }
        else {
            doc.removeEventListener("DOMAttrModified", this.domAttrModifiedListener, false);
            doc.removeEventListener("DOMNodeInserted", this.domNodeInsertedListener, false);
            doc.removeEventListener("DOMNodeRemoved", this.domNodeRemovedListener, false);
            doc.removeEventListener("DOMSubtreeModified", this.domSubtreeModifiedListener, false);
            doc.removeEventListener("DOMCharacterDataModified", this.domCharacterDataModifiedListener, false);
        }
    }
    
    public void dispose() {
        this.setCSSEngineUserAgent(null);
        this.disposeStyleMaps(this.document.getDocumentElement());
        if (this.document instanceof EventTarget) {
            this.removeEventListeners((EventTarget)this.document);
        }
    }
    
    protected void disposeStyleMaps(final Node node) {
        if (node instanceof CSSStylableElement) {
            ((CSSStylableElement)node).setComputedStyleMap(null, null);
        }
        for (Node n = getCSSFirstChild(node); n != null; n = getCSSNextSibling(n)) {
            if (n.getNodeType() == 1) {
                this.disposeStyleMaps(n);
            }
        }
    }
    
    public CSSContext getCSSContext() {
        return this.cssContext;
    }
    
    public Document getDocument() {
        return this.document;
    }
    
    public int getFontSizeIndex() {
        return this.fontSizeIndex;
    }
    
    public int getLineHeightIndex() {
        return this.lineHeightIndex;
    }
    
    public int getColorIndex() {
        return this.colorIndex;
    }
    
    public int getNumberOfProperties() {
        return this.valueManagers.length;
    }
    
    public int getPropertyIndex(final String name) {
        return this.indexes.get(name);
    }
    
    public int getShorthandIndex(final String name) {
        return this.shorthandIndexes.get(name);
    }
    
    public String getPropertyName(final int idx) {
        return this.valueManagers[idx].getPropertyName();
    }
    
    public void setCSSEngineUserAgent(final CSSEngineUserAgent userAgent) {
        this.userAgent = userAgent;
    }
    
    public CSSEngineUserAgent getCSSEngineUserAgent() {
        return this.userAgent;
    }
    
    public void setUserAgentStyleSheet(final StyleSheet ss) {
        this.userAgentStyleSheet = ss;
    }
    
    public void setUserStyleSheet(final StyleSheet ss) {
        this.userStyleSheet = ss;
    }
    
    public ValueManager[] getValueManagers() {
        return this.valueManagers;
    }
    
    public ShorthandManager[] getShorthandManagers() {
        return this.shorthandManagers;
    }
    
    public List getFontFaces() {
        return this.fontFaces;
    }
    
    public void setMedia(final String str) {
        try {
            this.media = this.parser.parseMedia(str);
        }
        catch (Exception e) {
            String m = e.getMessage();
            if (m == null) {
                m = "";
            }
            final String s = Messages.formatMessage("media.error", new Object[] { str, m });
            throw new DOMException((short)12, s);
        }
    }
    
    public void setAlternateStyleSheet(final String str) {
        this.alternateStyleSheet = str;
    }
    
    public void importCascadedStyleMaps(final Element src, final CSSEngine srceng, final Element dest) {
        if (src instanceof CSSStylableElement) {
            final CSSStylableElement csrc = (CSSStylableElement)src;
            final CSSStylableElement cdest = (CSSStylableElement)dest;
            StyleMap sm = srceng.getCascadedStyleMap(csrc, null);
            sm.setFixedCascadedStyle(true);
            cdest.setComputedStyleMap(null, sm);
            if (this.pseudoElementNames != null) {
                final int len = this.pseudoElementNames.length;
                for (final String pe : this.pseudoElementNames) {
                    sm = srceng.getCascadedStyleMap(csrc, pe);
                    cdest.setComputedStyleMap(pe, sm);
                }
            }
        }
        for (Node dn = getCSSFirstChild(dest), sn = getCSSFirstChild(src); dn != null; dn = getCSSNextSibling(dn), sn = getCSSNextSibling(sn)) {
            if (sn.getNodeType() == 1) {
                this.importCascadedStyleMaps((Element)sn, srceng, (Element)dn);
            }
        }
    }
    
    public ParsedURL getCSSBaseURI() {
        if (this.cssBaseURI == null) {
            this.cssBaseURI = this.element.getCSSBase();
        }
        return this.cssBaseURI;
    }
    
    public StyleMap getCascadedStyleMap(final CSSStylableElement elt, final String pseudo) {
        final int props = this.getNumberOfProperties();
        final StyleMap result = new StyleMap(props);
        if (this.userAgentStyleSheet != null) {
            final ArrayList rules = new ArrayList();
            this.addMatchingRules(rules, this.userAgentStyleSheet, elt, pseudo);
            this.addRules(elt, pseudo, result, rules, (short)0);
        }
        if (this.userStyleSheet != null) {
            final ArrayList rules = new ArrayList();
            this.addMatchingRules(rules, this.userStyleSheet, elt, pseudo);
            this.addRules(elt, pseudo, result, rules, (short)8192);
        }
        this.element = elt;
        try {
            if (this.nonCSSPresentationalHints != null) {
                final ShorthandManager.PropertyHandler ph = new ShorthandManager.PropertyHandler() {
                    @Override
                    public void property(final String pname, final LexicalUnit lu, final boolean important) {
                        int idx = CSSEngine.this.getPropertyIndex(pname);
                        if (idx != -1) {
                            final ValueManager vm = CSSEngine.this.valueManagers[idx];
                            final Value v = vm.createValue(lu, CSSEngine.this);
                            CSSEngine.this.putAuthorProperty(result, idx, v, important, (short)16384);
                            return;
                        }
                        idx = CSSEngine.this.getShorthandIndex(pname);
                        if (idx == -1) {
                            return;
                        }
                        CSSEngine.this.shorthandManagers[idx].setValues(CSSEngine.this, this, lu, important);
                    }
                };
                final NamedNodeMap attrs = elt.getAttributes();
                for (int len = attrs.getLength(), i = 0; i < len; ++i) {
                    final Node attr = attrs.item(i);
                    final String an = attr.getNodeName();
                    if (this.nonCSSPresentationalHints.contains(an)) {
                        try {
                            final LexicalUnit lu = this.parser.parsePropertyValue(attr.getNodeValue());
                            ph.property(an, lu, false);
                        }
                        catch (Exception e) {
                            String m = e.getMessage();
                            if (m == null) {
                                m = "";
                            }
                            final String u = (this.documentURI == null) ? "<unknown>" : this.documentURI.toString();
                            final String s = Messages.formatMessage("property.syntax.error.at", new Object[] { u, an, attr.getNodeValue(), m });
                            final DOMException de = new DOMException((short)12, s);
                            if (this.userAgent == null) {
                                throw de;
                            }
                            this.userAgent.displayError(de);
                        }
                    }
                }
            }
            final CSSEngine eng = this.cssContext.getCSSEngineForElement(elt);
            final List snodes = eng.getStyleSheetNodes();
            final int slen = snodes.size();
            if (slen > 0) {
                final ArrayList rules2 = new ArrayList();
                for (final Object snode : snodes) {
                    final CSSStyleSheetNode ssn = (CSSStyleSheetNode)snode;
                    final StyleSheet ss = ssn.getCSSStyleSheet();
                    if (ss != null && (!ss.isAlternate() || ss.getTitle() == null || ss.getTitle().equals(this.alternateStyleSheet)) && this.mediaMatch(ss.getMedia())) {
                        this.addMatchingRules(rules2, ss, elt, pseudo);
                    }
                }
                this.addRules(elt, pseudo, result, rules2, (short)24576);
            }
            if (this.styleLocalName != null) {
                final String style = elt.getAttributeNS(this.styleNamespaceURI, this.styleLocalName);
                if (style.length() > 0) {
                    try {
                        this.parser.setSelectorFactory(CSSSelectorFactory.INSTANCE);
                        this.parser.setConditionFactory(this.cssConditionFactory);
                        this.styleDeclarationDocumentHandler.styleMap = result;
                        this.parser.setDocumentHandler(this.styleDeclarationDocumentHandler);
                        this.parser.parseStyleDeclaration(style);
                        this.styleDeclarationDocumentHandler.styleMap = null;
                    }
                    catch (Exception e2) {
                        String j = e2.getMessage();
                        if (j == null) {
                            j = e2.getClass().getName();
                        }
                        final String u2 = (this.documentURI == null) ? "<unknown>" : this.documentURI.toString();
                        final String s2 = Messages.formatMessage("style.syntax.error.at", new Object[] { u2, this.styleLocalName, style, j });
                        final DOMException de2 = new DOMException((short)12, s2);
                        if (this.userAgent == null) {
                            throw de2;
                        }
                        this.userAgent.displayError(de2);
                    }
                }
            }
            final StyleDeclarationProvider p = elt.getOverrideStyleDeclarationProvider();
            if (p != null) {
                final StyleDeclaration over = p.getStyleDeclaration();
                if (over != null) {
                    for (int ol = over.size(), k = 0; k < ol; ++k) {
                        final int idx = over.getIndex(k);
                        final Value value = over.getValue(k);
                        final boolean important = over.getPriority(k);
                        if (!result.isImportant(idx) || important) {
                            result.putValue(idx, value);
                            result.putImportant(idx, important);
                            result.putOrigin(idx, (short)(-24576));
                        }
                    }
                }
            }
        }
        finally {
            this.element = null;
            this.cssBaseURI = null;
        }
        return result;
    }
    
    public Value getComputedStyle(final CSSStylableElement elt, final String pseudo, final int propidx) {
        StyleMap sm = elt.getComputedStyleMap(pseudo);
        if (sm == null) {
            sm = this.getCascadedStyleMap(elt, pseudo);
            elt.setComputedStyleMap(pseudo, sm);
        }
        final Value value = sm.getValue(propidx);
        if (sm.isComputed(propidx)) {
            return value;
        }
        Value result = value;
        final ValueManager vm = this.valueManagers[propidx];
        final CSSStylableElement p = getParentCSSStylableElement(elt);
        if (value == null) {
            if (p == null || !vm.isInheritedProperty()) {
                result = vm.getDefaultValue();
            }
        }
        else if (p != null && value == InheritValue.INSTANCE) {
            result = null;
        }
        if (result == null) {
            result = this.getComputedStyle(p, null, propidx);
            sm.putParentRelative(propidx, true);
            sm.putInherited(propidx, true);
        }
        else {
            result = vm.computeValue(elt, pseudo, this, propidx, sm, result);
        }
        if (value == null) {
            sm.putValue(propidx, result);
            sm.putNullCascaded(propidx, true);
        }
        else if (result != value) {
            final ComputedValue cv = new ComputedValue(value);
            cv.setComputedValue(result);
            sm.putValue(propidx, cv);
            result = cv;
        }
        sm.putComputed(propidx, true);
        return result;
    }
    
    public List getStyleSheetNodes() {
        if (this.styleSheetNodes == null) {
            this.styleSheetNodes = new ArrayList();
            this.selectorAttributes = new HashSet();
            this.findStyleSheetNodes(this.document);
            final int len = this.styleSheetNodes.size();
            for (final Object styleSheetNode : this.styleSheetNodes) {
                final CSSStyleSheetNode ssn = (CSSStyleSheetNode)styleSheetNode;
                final StyleSheet ss = ssn.getCSSStyleSheet();
                if (ss != null) {
                    this.findSelectorAttributes(this.selectorAttributes, ss);
                }
            }
        }
        return this.styleSheetNodes;
    }
    
    protected void findStyleSheetNodes(final Node n) {
        if (n instanceof CSSStyleSheetNode) {
            this.styleSheetNodes.add(n);
        }
        for (Node nd = getCSSFirstChild(n); nd != null; nd = getCSSNextSibling(nd)) {
            this.findStyleSheetNodes(nd);
        }
    }
    
    protected void findSelectorAttributes(final Set attrs, final StyleSheet ss) {
        for (int len = ss.getSize(), i = 0; i < len; ++i) {
            final Rule r = ss.getRule(i);
            switch (r.getType()) {
                case 0: {
                    final StyleRule style = (StyleRule)r;
                    final SelectorList sl = style.getSelectorList();
                    for (int slen = sl.getLength(), j = 0; j < slen; ++j) {
                        final ExtendedSelector s = (ExtendedSelector)sl.item(j);
                        s.fillAttributeSet(attrs);
                    }
                    break;
                }
                case 1:
                case 2: {
                    final MediaRule mr = (MediaRule)r;
                    if (this.mediaMatch(mr.getMediaList())) {
                        this.findSelectorAttributes(attrs, mr);
                        break;
                    }
                    break;
                }
            }
        }
    }
    
    public void setMainProperties(final CSSStylableElement elt, final MainPropertyReceiver dst, final String pname, final String value, final boolean important) {
        try {
            this.element = elt;
            final LexicalUnit lu = this.parser.parsePropertyValue(value);
            final ShorthandManager.PropertyHandler ph = new ShorthandManager.PropertyHandler() {
                @Override
                public void property(final String pname, final LexicalUnit lu, final boolean important) {
                    int idx = CSSEngine.this.getPropertyIndex(pname);
                    if (idx != -1) {
                        final ValueManager vm = CSSEngine.this.valueManagers[idx];
                        final Value v = vm.createValue(lu, CSSEngine.this);
                        dst.setMainProperty(pname, v, important);
                        return;
                    }
                    idx = CSSEngine.this.getShorthandIndex(pname);
                    if (idx == -1) {
                        return;
                    }
                    CSSEngine.this.shorthandManagers[idx].setValues(CSSEngine.this, this, lu, important);
                }
            };
            ph.property(pname, lu, important);
        }
        catch (Exception e) {
            String m = e.getMessage();
            if (m == null) {
                m = "";
            }
            final String u = (this.documentURI == null) ? "<unknown>" : this.documentURI.toString();
            final String s = Messages.formatMessage("property.syntax.error.at", new Object[] { u, pname, value, m });
            final DOMException de = new DOMException((short)12, s);
            if (this.userAgent == null) {
                throw de;
            }
            this.userAgent.displayError(de);
        }
        finally {
            this.element = null;
            this.cssBaseURI = null;
        }
    }
    
    public Value parsePropertyValue(final CSSStylableElement elt, final String prop, final String value) {
        final int idx = this.getPropertyIndex(prop);
        if (idx == -1) {
            return null;
        }
        final ValueManager vm = this.valueManagers[idx];
        try {
            this.element = elt;
            final LexicalUnit lu = this.parser.parsePropertyValue(value);
            return vm.createValue(lu, this);
        }
        catch (Exception e) {
            String m = e.getMessage();
            if (m == null) {
                m = "";
            }
            final String u = (this.documentURI == null) ? "<unknown>" : this.documentURI.toString();
            final String s = Messages.formatMessage("property.syntax.error.at", new Object[] { u, prop, value, m });
            final DOMException de = new DOMException((short)12, s);
            if (this.userAgent == null) {
                throw de;
            }
            this.userAgent.displayError(de);
        }
        finally {
            this.element = null;
            this.cssBaseURI = null;
        }
        return vm.getDefaultValue();
    }
    
    public StyleDeclaration parseStyleDeclaration(final CSSStylableElement elt, final String value) {
        this.styleDeclarationBuilder.styleDeclaration = new StyleDeclaration();
        try {
            this.element = elt;
            this.parser.setSelectorFactory(CSSSelectorFactory.INSTANCE);
            this.parser.setConditionFactory(this.cssConditionFactory);
            this.parser.setDocumentHandler(this.styleDeclarationBuilder);
            this.parser.parseStyleDeclaration(value);
        }
        catch (Exception e) {
            String m = e.getMessage();
            if (m == null) {
                m = "";
            }
            final String u = (this.documentURI == null) ? "<unknown>" : this.documentURI.toString();
            final String s = Messages.formatMessage("syntax.error.at", new Object[] { u, m });
            final DOMException de = new DOMException((short)12, s);
            if (this.userAgent == null) {
                throw de;
            }
            this.userAgent.displayError(de);
        }
        finally {
            this.element = null;
            this.cssBaseURI = null;
        }
        return this.styleDeclarationBuilder.styleDeclaration;
    }
    
    public StyleSheet parseStyleSheet(final ParsedURL uri, final String media) throws DOMException {
        final StyleSheet ss = new StyleSheet();
        try {
            ss.setMedia(this.parser.parseMedia(media));
        }
        catch (Exception e) {
            String m = e.getMessage();
            if (m == null) {
                m = "";
            }
            final String u = (this.documentURI == null) ? "<unknown>" : this.documentURI.toString();
            final String s = Messages.formatMessage("syntax.error.at", new Object[] { u, m });
            final DOMException de = new DOMException((short)12, s);
            if (this.userAgent == null) {
                throw de;
            }
            this.userAgent.displayError(de);
            return ss;
        }
        this.parseStyleSheet(ss, uri);
        return ss;
    }
    
    public StyleSheet parseStyleSheet(final InputSource is, final ParsedURL uri, final String media) throws DOMException {
        final StyleSheet ss = new StyleSheet();
        try {
            ss.setMedia(this.parser.parseMedia(media));
            this.parseStyleSheet(ss, is, uri);
        }
        catch (Exception e) {
            String m = e.getMessage();
            if (m == null) {
                m = "";
            }
            final String u = (this.documentURI == null) ? "<unknown>" : this.documentURI.toString();
            final String s = Messages.formatMessage("syntax.error.at", new Object[] { u, m });
            final DOMException de = new DOMException((short)12, s);
            if (this.userAgent == null) {
                throw de;
            }
            this.userAgent.displayError(de);
        }
        return ss;
    }
    
    public void parseStyleSheet(final StyleSheet ss, final ParsedURL uri) throws DOMException {
        if (uri != null) {
            try {
                this.cssContext.checkLoadExternalResource(uri, this.documentURI);
                this.parseStyleSheet(ss, new InputSource(uri.toString()), uri);
            }
            catch (SecurityException e) {
                throw e;
            }
            catch (Exception e2) {
                String m = e2.getMessage();
                if (m == null) {
                    m = e2.getClass().getName();
                }
                final String s = Messages.formatMessage("syntax.error.at", new Object[] { uri.toString(), m });
                final DOMException de = new DOMException((short)12, s);
                if (this.userAgent == null) {
                    throw de;
                }
                this.userAgent.displayError(de);
            }
            return;
        }
        final String s2 = Messages.formatMessage("syntax.error.at", new Object[] { "Null Document reference", "" });
        final DOMException de2 = new DOMException((short)12, s2);
        if (this.userAgent == null) {
            throw de2;
        }
        this.userAgent.displayError(de2);
    }
    
    public StyleSheet parseStyleSheet(final String rules, final ParsedURL uri, final String media) throws DOMException {
        final StyleSheet ss = new StyleSheet();
        try {
            ss.setMedia(this.parser.parseMedia(media));
        }
        catch (Exception e) {
            String m = e.getMessage();
            if (m == null) {
                m = "";
            }
            final String u = (this.documentURI == null) ? "<unknown>" : this.documentURI.toString();
            final String s = Messages.formatMessage("syntax.error.at", new Object[] { u, m });
            final DOMException de = new DOMException((short)12, s);
            if (this.userAgent == null) {
                throw de;
            }
            this.userAgent.displayError(de);
            return ss;
        }
        this.parseStyleSheet(ss, rules, uri);
        return ss;
    }
    
    public void parseStyleSheet(final StyleSheet ss, final String rules, final ParsedURL uri) throws DOMException {
        try {
            this.parseStyleSheet(ss, new InputSource(new StringReader(rules)), uri);
        }
        catch (Exception e) {
            String m = e.getMessage();
            if (m == null) {
                m = "";
            }
            final String s = Messages.formatMessage("stylesheet.syntax.error", new Object[] { uri.toString(), rules, m });
            final DOMException de = new DOMException((short)12, s);
            if (this.userAgent == null) {
                throw de;
            }
            this.userAgent.displayError(de);
        }
    }
    
    protected void parseStyleSheet(final StyleSheet ss, final InputSource is, final ParsedURL uri) throws IOException {
        this.parser.setSelectorFactory(CSSSelectorFactory.INSTANCE);
        this.parser.setConditionFactory(this.cssConditionFactory);
        try {
            this.cssBaseURI = uri;
            this.styleSheetDocumentHandler.styleSheet = ss;
            this.parser.setDocumentHandler(this.styleSheetDocumentHandler);
            this.parser.parseStyleSheet(is);
            for (int len = ss.getSize(), i = 0; i < len; ++i) {
                final Rule r = ss.getRule(i);
                if (r.getType() != 2) {
                    break;
                }
                final ImportRule ir = (ImportRule)r;
                this.parseStyleSheet(ir, ir.getURI());
            }
        }
        finally {
            this.cssBaseURI = null;
        }
    }
    
    protected void putAuthorProperty(final StyleMap dest, final int idx, final Value sval, final boolean imp, final short origin) {
        final Value dval = dest.getValue(idx);
        final short dorg = dest.getOrigin(idx);
        final boolean dimp = dest.isImportant(idx);
        boolean cond = dval == null;
        if (!cond) {
            switch (dorg) {
                case 8192: {
                    cond = !dimp;
                    break;
                }
                case 24576: {
                    cond = (!dimp || imp);
                    break;
                }
                case -24576: {
                    cond = false;
                    break;
                }
                default: {
                    cond = true;
                    break;
                }
            }
        }
        if (cond) {
            dest.putValue(idx, sval);
            dest.putImportant(idx, imp);
            dest.putOrigin(idx, origin);
        }
    }
    
    protected void addMatchingRules(final List rules, final StyleSheet ss, final Element elt, final String pseudo) {
        for (int len = ss.getSize(), i = 0; i < len; ++i) {
            final Rule r = ss.getRule(i);
            switch (r.getType()) {
                case 0: {
                    final StyleRule style = (StyleRule)r;
                    final SelectorList sl = style.getSelectorList();
                    for (int slen = sl.getLength(), j = 0; j < slen; ++j) {
                        final ExtendedSelector s = (ExtendedSelector)sl.item(j);
                        if (s.match(elt, pseudo)) {
                            rules.add(style);
                        }
                    }
                    break;
                }
                case 1:
                case 2: {
                    final MediaRule mr = (MediaRule)r;
                    if (this.mediaMatch(mr.getMediaList())) {
                        this.addMatchingRules(rules, mr, elt, pseudo);
                        break;
                    }
                    break;
                }
            }
        }
    }
    
    protected void addRules(final Element elt, final String pseudo, final StyleMap sm, final ArrayList rules, final short origin) {
        this.sortRules(rules, elt, pseudo);
        final int rlen = rules.size();
        if (origin == 24576) {
            for (final Object rule : rules) {
                final StyleRule sr = (StyleRule)rule;
                final StyleDeclaration sd = sr.getStyleDeclaration();
                for (int len = sd.size(), i = 0; i < len; ++i) {
                    this.putAuthorProperty(sm, sd.getIndex(i), sd.getValue(i), sd.getPriority(i), origin);
                }
            }
        }
        else {
            for (final Object rule : rules) {
                final StyleRule sr = (StyleRule)rule;
                final StyleDeclaration sd = sr.getStyleDeclaration();
                for (int len = sd.size(), i = 0; i < len; ++i) {
                    final int idx = sd.getIndex(i);
                    sm.putValue(idx, sd.getValue(i));
                    sm.putImportant(idx, sd.getPriority(i));
                    sm.putOrigin(idx, origin);
                }
            }
        }
    }
    
    protected void sortRules(final ArrayList rules, final Element elt, final String pseudo) {
        final int len = rules.size();
        final int[] specificities = new int[len];
        for (int i = 0; i < len; ++i) {
            final StyleRule r = rules.get(i);
            final SelectorList sl = r.getSelectorList();
            int spec = 0;
            for (int slen = sl.getLength(), k = 0; k < slen; ++k) {
                final ExtendedSelector s = (ExtendedSelector)sl.item(k);
                if (s.match(elt, pseudo)) {
                    final int sp = s.getSpecificity();
                    if (sp > spec) {
                        spec = sp;
                    }
                }
            }
            specificities[i] = spec;
        }
        for (int i = 1; i < len; ++i) {
            final Object rule = rules.get(i);
            int spec2;
            int j;
            for (spec2 = specificities[i], j = i - 1; j >= 0 && specificities[j] > spec2; --j) {
                rules.set(j + 1, rules.get(j));
                specificities[j + 1] = specificities[j];
            }
            rules.set(j + 1, rule);
            specificities[j + 1] = spec2;
        }
    }
    
    protected boolean mediaMatch(final SACMediaList ml) {
        if (this.media == null || ml == null || this.media.getLength() == 0 || ml.getLength() == 0) {
            return true;
        }
        for (int i = 0; i < ml.getLength(); ++i) {
            if (ml.item(i).equalsIgnoreCase("all")) {
                return true;
            }
            for (int j = 0; j < this.media.getLength(); ++j) {
                if (this.media.item(j).equalsIgnoreCase("all") || ml.item(i).equalsIgnoreCase(this.media.item(j))) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public void addCSSEngineListener(final CSSEngineListener l) {
        this.listeners.add(l);
    }
    
    public void removeCSSEngineListener(final CSSEngineListener l) {
        this.listeners.remove(l);
    }
    
    protected void firePropertiesChangedEvent(final Element target, final int[] props) {
        final CSSEngineListener[] ll = this.listeners.toArray(CSSEngine.LISTENER_ARRAY);
        final int len = ll.length;
        if (len > 0) {
            final CSSEngineEvent evt = new CSSEngineEvent(this, target, props);
            for (final CSSEngineListener aLl : ll) {
                aLl.propertiesChanged(evt);
            }
        }
    }
    
    protected void inlineStyleAttributeUpdated(final CSSStylableElement elt, final StyleMap style, final short attrChange, final String prevValue, final String newValue) {
        final boolean[] updated = this.styleDeclarationUpdateHandler.updatedProperties;
        for (int i = this.getNumberOfProperties() - 1; i >= 0; --i) {
            updated[i] = false;
        }
        switch (attrChange) {
            case 1:
            case 2: {
                if (newValue.length() > 0) {
                    this.element = elt;
                    try {
                        this.parser.setSelectorFactory(CSSSelectorFactory.INSTANCE);
                        this.parser.setConditionFactory(this.cssConditionFactory);
                        this.styleDeclarationUpdateHandler.styleMap = style;
                        this.parser.setDocumentHandler(this.styleDeclarationUpdateHandler);
                        this.parser.parseStyleDeclaration(newValue);
                        this.styleDeclarationUpdateHandler.styleMap = null;
                    }
                    catch (Exception e) {
                        String m = e.getMessage();
                        if (m == null) {
                            m = "";
                        }
                        final String u = (this.documentURI == null) ? "<unknown>" : this.documentURI.toString();
                        final String s = Messages.formatMessage("style.syntax.error.at", new Object[] { u, this.styleLocalName, newValue, m });
                        final DOMException de = new DOMException((short)12, s);
                        if (this.userAgent == null) {
                            throw de;
                        }
                        this.userAgent.displayError(de);
                    }
                    finally {
                        this.element = null;
                        this.cssBaseURI = null;
                    }
                }
            }
            case 3: {
                boolean removed = false;
                if (prevValue != null && prevValue.length() > 0) {
                    for (int j = this.getNumberOfProperties() - 1; j >= 0; --j) {
                        if (style.isComputed(j) && !updated[j]) {
                            final short origin = style.getOrigin(j);
                            if (origin >= -32768) {
                                removed = true;
                                updated[j] = true;
                            }
                        }
                    }
                }
                if (removed) {
                    this.invalidateProperties(elt, null, updated, true);
                }
                else {
                    int count = 0;
                    final boolean fs = this.fontSizeIndex != -1 && updated[this.fontSizeIndex];
                    final boolean lh = this.lineHeightIndex != -1 && updated[this.lineHeightIndex];
                    final boolean cl = this.colorIndex != -1 && updated[this.colorIndex];
                    for (int k = this.getNumberOfProperties() - 1; k >= 0; --k) {
                        if (updated[k]) {
                            ++count;
                        }
                        else if ((fs && style.isFontSizeRelative(k)) || (lh && style.isLineHeightRelative(k)) || (cl && style.isColorRelative(k))) {
                            updated[k] = true;
                            clearComputedValue(style, k);
                            ++count;
                        }
                    }
                    if (count > 0) {
                        final int[] props = new int[count];
                        count = 0;
                        for (int l = this.getNumberOfProperties() - 1; l >= 0; --l) {
                            if (updated[l]) {
                                props[count++] = l;
                            }
                        }
                        this.invalidateProperties(elt, props, null, true);
                    }
                }
            }
            default: {
                throw new IllegalStateException("Invalid attrChangeType");
            }
        }
    }
    
    private static void clearComputedValue(final StyleMap style, final int n) {
        if (style.isNullCascaded(n)) {
            style.putValue(n, null);
        }
        else {
            Value v = style.getValue(n);
            if (v instanceof ComputedValue) {
                final ComputedValue cv = (ComputedValue)v;
                v = cv.getCascadedValue();
                style.putValue(n, v);
            }
        }
        style.putComputed(n, false);
    }
    
    protected void invalidateProperties(final Node node, final int[] properties, final boolean[] updated, final boolean recascade) {
        if (!(node instanceof CSSStylableElement)) {
            return;
        }
        final CSSStylableElement elt = (CSSStylableElement)node;
        final StyleMap style = elt.getComputedStyleMap(null);
        if (style == null) {
            return;
        }
        final boolean[] diffs = new boolean[this.getNumberOfProperties()];
        if (updated != null) {
            System.arraycopy(updated, 0, diffs, 0, updated.length);
        }
        if (properties != null) {
            for (final int property : properties) {
                diffs[property] = true;
            }
        }
        int count = 0;
        if (!recascade) {
            for (final boolean diff : diffs) {
                if (diff) {
                    ++count;
                }
            }
        }
        else {
            final StyleMap newStyle = this.getCascadedStyleMap(elt, null);
            elt.setComputedStyleMap(null, newStyle);
            for (int i = 0; i < diffs.length; ++i) {
                if (diffs[i]) {
                    ++count;
                }
                else {
                    final Value nv = newStyle.getValue(i);
                    Value ov = null;
                    if (!style.isNullCascaded(i)) {
                        ov = style.getValue(i);
                        if (ov instanceof ComputedValue) {
                            ov = ((ComputedValue)ov).getCascadedValue();
                        }
                    }
                    if (nv != ov) {
                        if (nv != null && ov != null) {
                            if (nv.equals(ov)) {
                                continue;
                            }
                            final String ovCssText = ov.getCssText();
                            final String nvCssText = nv.getCssText();
                            if (nvCssText == ovCssText) {
                                continue;
                            }
                            if (nvCssText != null && nvCssText.equals(ovCssText)) {
                                continue;
                            }
                        }
                        ++count;
                        diffs[i] = true;
                    }
                }
            }
        }
        int[] props = null;
        if (count != 0) {
            props = new int[count];
            count = 0;
            for (int i = 0; i < diffs.length; ++i) {
                if (diffs[i]) {
                    props[count++] = i;
                }
            }
        }
        this.propagateChanges(elt, props, recascade);
    }
    
    protected void propagateChanges(final Node node, int[] props, final boolean recascade) {
        if (!(node instanceof CSSStylableElement)) {
            return;
        }
        final CSSStylableElement elt = (CSSStylableElement)node;
        final StyleMap style = elt.getComputedStyleMap(null);
        if (style != null) {
            final boolean[] updated = this.styleDeclarationUpdateHandler.updatedProperties;
            for (int i = this.getNumberOfProperties() - 1; i >= 0; --i) {
                updated[i] = false;
            }
            if (props != null) {
                for (int i = props.length - 1; i >= 0; --i) {
                    final int idx = props[i];
                    updated[idx] = true;
                }
            }
            final boolean fs = this.fontSizeIndex != -1 && updated[this.fontSizeIndex];
            final boolean lh = this.lineHeightIndex != -1 && updated[this.lineHeightIndex];
            final boolean cl = this.colorIndex != -1 && updated[this.colorIndex];
            int count = 0;
            for (int j = this.getNumberOfProperties() - 1; j >= 0; --j) {
                if (updated[j]) {
                    ++count;
                }
                else if ((fs && style.isFontSizeRelative(j)) || (lh && style.isLineHeightRelative(j)) || (cl && style.isColorRelative(j))) {
                    updated[j] = true;
                    clearComputedValue(style, j);
                    ++count;
                }
            }
            if (count == 0) {
                props = null;
            }
            else {
                props = new int[count];
                count = 0;
                for (int j = this.getNumberOfProperties() - 1; j >= 0; --j) {
                    if (updated[j]) {
                        props[count++] = j;
                    }
                }
                this.firePropertiesChangedEvent(elt, props);
            }
        }
        int[] inherited;
        if ((inherited = props) != null) {
            int count2 = 0;
            for (int k = 0; k < props.length; ++k) {
                final ValueManager vm = this.valueManagers[props[k]];
                if (vm.isInheritedProperty()) {
                    ++count2;
                }
                else {
                    props[k] = -1;
                }
            }
            if (count2 == 0) {
                inherited = null;
            }
            else {
                inherited = new int[count2];
                count2 = 0;
                for (final int prop : props) {
                    if (prop != -1) {
                        inherited[count2++] = prop;
                    }
                }
            }
        }
        for (Node n = getCSSFirstChild(node); n != null; n = getCSSNextSibling(n)) {
            if (n.getNodeType() == 1) {
                this.invalidateProperties(n, inherited, null, recascade);
            }
        }
    }
    
    protected void nonCSSPresentationalHintUpdated(final CSSStylableElement elt, final StyleMap style, final String property, final short attrChange, final String newValue) {
        final int idx = this.getPropertyIndex(property);
        if (style.isImportant(idx)) {
            return;
        }
        if (style.getOrigin(idx) >= 24576) {
            return;
        }
        switch (attrChange) {
            case 1:
            case 2: {
                this.element = elt;
                try {
                    final LexicalUnit lu = this.parser.parsePropertyValue(newValue);
                    final ValueManager vm = this.valueManagers[idx];
                    final Value v = vm.createValue(lu, this);
                    style.putMask(idx, (short)0);
                    style.putValue(idx, v);
                    style.putOrigin(idx, (short)16384);
                }
                catch (Exception e) {
                    String m = e.getMessage();
                    if (m == null) {
                        m = "";
                    }
                    final String u = (this.documentURI == null) ? "<unknown>" : this.documentURI.toString();
                    final String s = Messages.formatMessage("property.syntax.error.at", new Object[] { u, property, newValue, m });
                    final DOMException de = new DOMException((short)12, s);
                    if (this.userAgent == null) {
                        throw de;
                    }
                    this.userAgent.displayError(de);
                }
                finally {
                    this.element = null;
                    this.cssBaseURI = null;
                }
                break;
            }
            case 3: {
                final int[] invalid = { idx };
                this.invalidateProperties(elt, invalid, null, true);
                return;
            }
        }
        final boolean[] updated = this.styleDeclarationUpdateHandler.updatedProperties;
        for (int i = this.getNumberOfProperties() - 1; i >= 0; --i) {
            updated[i] = false;
        }
        updated[idx] = true;
        final boolean fs = idx == this.fontSizeIndex;
        final boolean lh = idx == this.lineHeightIndex;
        final boolean cl = idx == this.colorIndex;
        int count = 0;
        for (int j = this.getNumberOfProperties() - 1; j >= 0; --j) {
            if (updated[j]) {
                ++count;
            }
            else if ((fs && style.isFontSizeRelative(j)) || (lh && style.isLineHeightRelative(j)) || (cl && style.isColorRelative(j))) {
                updated[j] = true;
                clearComputedValue(style, j);
                ++count;
            }
        }
        final int[] props = new int[count];
        count = 0;
        for (int k = this.getNumberOfProperties() - 1; k >= 0; --k) {
            if (updated[k]) {
                props[count++] = k;
            }
        }
        this.invalidateProperties(elt, props, null, true);
    }
    
    protected boolean hasStyleSheetNode(Node n) {
        if (n instanceof CSSStyleSheetNode) {
            return true;
        }
        for (n = getCSSFirstChild(n); n != null; n = getCSSNextSibling(n)) {
            if (this.hasStyleSheetNode(n)) {
                return true;
            }
        }
        return false;
    }
    
    protected void handleAttrModified(final Element e, final Attr attr, final short attrChange, final String prevValue, final String newValue) {
        if (!(e instanceof CSSStylableElement)) {
            return;
        }
        if (newValue.equals(prevValue)) {
            return;
        }
        final String attrNS = attr.getNamespaceURI();
        final String name = (attrNS == null) ? attr.getNodeName() : attr.getLocalName();
        final CSSStylableElement elt = (CSSStylableElement)e;
        final StyleMap style = elt.getComputedStyleMap(null);
        if (style != null) {
            if ((attrNS == this.styleNamespaceURI || (attrNS != null && attrNS.equals(this.styleNamespaceURI))) && name.equals(this.styleLocalName)) {
                this.inlineStyleAttributeUpdated(elt, style, attrChange, prevValue, newValue);
                return;
            }
            if (this.nonCSSPresentationalHints != null && (attrNS == this.nonCSSPresentationalHintsNamespaceURI || (attrNS != null && attrNS.equals(this.nonCSSPresentationalHintsNamespaceURI))) && this.nonCSSPresentationalHints.contains(name)) {
                this.nonCSSPresentationalHintUpdated(elt, style, name, attrChange, newValue);
                return;
            }
        }
        if (this.selectorAttributes != null && this.selectorAttributes.contains(name)) {
            this.invalidateProperties(elt, null, null, true);
            for (Node n = getCSSNextSibling(elt); n != null; n = getCSSNextSibling(n)) {
                this.invalidateProperties(n, null, null, true);
            }
        }
    }
    
    protected void handleNodeInserted(Node n) {
        if (this.hasStyleSheetNode(n)) {
            this.styleSheetNodes = null;
            this.invalidateProperties(this.document.getDocumentElement(), null, null, true);
        }
        else if (n instanceof CSSStylableElement) {
            for (n = getCSSNextSibling(n); n != null; n = getCSSNextSibling(n)) {
                this.invalidateProperties(n, null, null, true);
            }
        }
    }
    
    protected void handleNodeRemoved(final Node n) {
        if (this.hasStyleSheetNode(n)) {
            this.styleSheetRemoved = true;
        }
        else if (n instanceof CSSStylableElement) {
            this.removedStylableElementSibling = getCSSNextSibling(n);
        }
        this.disposeStyleMaps(n);
    }
    
    protected void handleSubtreeModified(final Node ignored) {
        if (this.styleSheetRemoved) {
            this.styleSheetRemoved = false;
            this.styleSheetNodes = null;
            this.invalidateProperties(this.document.getDocumentElement(), null, null, true);
        }
        else if (this.removedStylableElementSibling != null) {
            for (Node n = this.removedStylableElementSibling; n != null; n = getCSSNextSibling(n)) {
                this.invalidateProperties(n, null, null, true);
            }
            this.removedStylableElementSibling = null;
        }
    }
    
    protected void handleCharacterDataModified(final Node n) {
        if (getCSSParentNode(n) instanceof CSSStyleSheetNode) {
            this.styleSheetNodes = null;
            this.invalidateProperties(this.document.getDocumentElement(), null, null, true);
        }
    }
    
    static {
        LISTENER_ARRAY = new CSSEngineListener[0];
    }
    
    protected class StyleDeclarationDocumentHandler extends DocumentAdapter implements ShorthandManager.PropertyHandler
    {
        public StyleMap styleMap;
        
        @Override
        public void property(final String name, final LexicalUnit value, final boolean important) throws CSSException {
            int i = CSSEngine.this.getPropertyIndex(name);
            if (i == -1) {
                i = CSSEngine.this.getShorthandIndex(name);
                if (i == -1) {
                    return;
                }
                CSSEngine.this.shorthandManagers[i].setValues(CSSEngine.this, this, value, important);
            }
            else {
                final Value v = CSSEngine.this.valueManagers[i].createValue(value, CSSEngine.this);
                CSSEngine.this.putAuthorProperty(this.styleMap, i, v, important, (short)(-32768));
            }
        }
    }
    
    protected class StyleDeclarationBuilder extends DocumentAdapter implements ShorthandManager.PropertyHandler
    {
        public StyleDeclaration styleDeclaration;
        
        @Override
        public void property(final String name, final LexicalUnit value, final boolean important) throws CSSException {
            int i = CSSEngine.this.getPropertyIndex(name);
            if (i == -1) {
                i = CSSEngine.this.getShorthandIndex(name);
                if (i == -1) {
                    return;
                }
                CSSEngine.this.shorthandManagers[i].setValues(CSSEngine.this, this, value, important);
            }
            else {
                final Value v = CSSEngine.this.valueManagers[i].createValue(value, CSSEngine.this);
                this.styleDeclaration.append(v, i, important);
            }
        }
    }
    
    protected class StyleSheetDocumentHandler extends DocumentAdapter implements ShorthandManager.PropertyHandler
    {
        public StyleSheet styleSheet;
        protected StyleRule styleRule;
        protected StyleDeclaration styleDeclaration;
        
        @Override
        public void startDocument(final InputSource source) throws CSSException {
        }
        
        @Override
        public void endDocument(final InputSource source) throws CSSException {
        }
        
        @Override
        public void ignorableAtRule(final String atRule) throws CSSException {
        }
        
        @Override
        public void importStyle(final String uri, final SACMediaList media, final String defaultNamespaceURI) throws CSSException {
            final ImportRule ir = new ImportRule();
            ir.setMediaList(media);
            ir.setParent(this.styleSheet);
            final ParsedURL base = CSSEngine.this.getCSSBaseURI();
            ParsedURL url;
            if (base == null) {
                url = new ParsedURL(uri);
            }
            else {
                url = new ParsedURL(base, uri);
            }
            ir.setURI(url);
            this.styleSheet.append(ir);
        }
        
        @Override
        public void startMedia(final SACMediaList media) throws CSSException {
            final MediaRule mr = new MediaRule();
            mr.setMediaList(media);
            mr.setParent(this.styleSheet);
            this.styleSheet.append(mr);
            this.styleSheet = mr;
        }
        
        @Override
        public void endMedia(final SACMediaList media) throws CSSException {
            this.styleSheet = this.styleSheet.getParent();
        }
        
        @Override
        public void startPage(final String name, final String pseudo_page) throws CSSException {
        }
        
        @Override
        public void endPage(final String name, final String pseudo_page) throws CSSException {
        }
        
        @Override
        public void startFontFace() throws CSSException {
            this.styleDeclaration = new StyleDeclaration();
        }
        
        @Override
        public void endFontFace() throws CSSException {
            final StyleMap sm = new StyleMap(CSSEngine.this.getNumberOfProperties());
            for (int len = this.styleDeclaration.size(), i = 0; i < len; ++i) {
                final int idx = this.styleDeclaration.getIndex(i);
                sm.putValue(idx, this.styleDeclaration.getValue(i));
                sm.putImportant(idx, this.styleDeclaration.getPriority(i));
                sm.putOrigin(idx, (short)24576);
            }
            this.styleDeclaration = null;
            final int pidx = CSSEngine.this.getPropertyIndex("font-family");
            final Value fontFamily = sm.getValue(pidx);
            if (fontFamily == null) {
                return;
            }
            final ParsedURL base = CSSEngine.this.getCSSBaseURI();
            CSSEngine.this.fontFaces.add(new FontFaceRule(sm, base));
        }
        
        @Override
        public void startSelector(final SelectorList selectors) throws CSSException {
            (this.styleRule = new StyleRule()).setSelectorList(selectors);
            this.styleDeclaration = new StyleDeclaration();
            this.styleRule.setStyleDeclaration(this.styleDeclaration);
            this.styleSheet.append(this.styleRule);
        }
        
        @Override
        public void endSelector(final SelectorList selectors) throws CSSException {
            this.styleRule = null;
            this.styleDeclaration = null;
        }
        
        @Override
        public void property(final String name, final LexicalUnit value, final boolean important) throws CSSException {
            int i = CSSEngine.this.getPropertyIndex(name);
            if (i == -1) {
                i = CSSEngine.this.getShorthandIndex(name);
                if (i == -1) {
                    return;
                }
                CSSEngine.this.shorthandManagers[i].setValues(CSSEngine.this, this, value, important);
            }
            else {
                final Value v = CSSEngine.this.valueManagers[i].createValue(value, CSSEngine.this);
                this.styleDeclaration.append(v, i, important);
            }
        }
    }
    
    protected static class DocumentAdapter implements DocumentHandler
    {
        @Override
        public void startDocument(final InputSource source) {
            this.throwUnsupportedEx();
        }
        
        @Override
        public void endDocument(final InputSource source) {
            this.throwUnsupportedEx();
        }
        
        @Override
        public void comment(final String text) {
        }
        
        @Override
        public void ignorableAtRule(final String atRule) {
            this.throwUnsupportedEx();
        }
        
        @Override
        public void namespaceDeclaration(final String prefix, final String uri) {
            this.throwUnsupportedEx();
        }
        
        @Override
        public void importStyle(final String uri, final SACMediaList media, final String defaultNamespaceURI) {
            this.throwUnsupportedEx();
        }
        
        @Override
        public void startMedia(final SACMediaList media) {
            this.throwUnsupportedEx();
        }
        
        @Override
        public void endMedia(final SACMediaList media) {
            this.throwUnsupportedEx();
        }
        
        @Override
        public void startPage(final String name, final String pseudo_page) {
            this.throwUnsupportedEx();
        }
        
        @Override
        public void endPage(final String name, final String pseudo_page) {
            this.throwUnsupportedEx();
        }
        
        @Override
        public void startFontFace() {
            this.throwUnsupportedEx();
        }
        
        @Override
        public void endFontFace() {
            this.throwUnsupportedEx();
        }
        
        @Override
        public void startSelector(final SelectorList selectors) {
            this.throwUnsupportedEx();
        }
        
        @Override
        public void endSelector(final SelectorList selectors) {
            this.throwUnsupportedEx();
        }
        
        @Override
        public void property(final String name, final LexicalUnit value, final boolean important) {
            this.throwUnsupportedEx();
        }
        
        private void throwUnsupportedEx() {
            throw new UnsupportedOperationException("you try to use an empty method in Adapter-class");
        }
    }
    
    protected class StyleDeclarationUpdateHandler extends DocumentAdapter implements ShorthandManager.PropertyHandler
    {
        public StyleMap styleMap;
        public boolean[] updatedProperties;
        
        protected StyleDeclarationUpdateHandler() {
            this.updatedProperties = new boolean[CSSEngine.this.getNumberOfProperties()];
        }
        
        @Override
        public void property(final String name, final LexicalUnit value, final boolean important) throws CSSException {
            int i = CSSEngine.this.getPropertyIndex(name);
            if (i == -1) {
                i = CSSEngine.this.getShorthandIndex(name);
                if (i == -1) {
                    return;
                }
                CSSEngine.this.shorthandManagers[i].setValues(CSSEngine.this, this, value, important);
            }
            else {
                if (this.styleMap.isImportant(i)) {
                    return;
                }
                this.updatedProperties[i] = true;
                final Value v = CSSEngine.this.valueManagers[i].createValue(value, CSSEngine.this);
                this.styleMap.putMask(i, (short)0);
                this.styleMap.putValue(i, v);
                this.styleMap.putOrigin(i, (short)(-32768));
            }
        }
    }
    
    protected class CSSNavigableDocumentHandler implements CSSNavigableDocumentListener, MainPropertyReceiver
    {
        protected boolean[] mainPropertiesChanged;
        protected StyleDeclaration declaration;
        
        @Override
        public void nodeInserted(final Node newNode) {
            CSSEngine.this.handleNodeInserted(newNode);
        }
        
        @Override
        public void nodeToBeRemoved(final Node oldNode) {
            CSSEngine.this.handleNodeRemoved(oldNode);
        }
        
        @Override
        public void subtreeModified(final Node rootOfModifications) {
            CSSEngine.this.handleSubtreeModified(rootOfModifications);
        }
        
        @Override
        public void characterDataModified(final Node text) {
            CSSEngine.this.handleCharacterDataModified(text);
        }
        
        @Override
        public void attrModified(final Element e, final Attr attr, final short attrChange, final String prevValue, final String newValue) {
            CSSEngine.this.handleAttrModified(e, attr, attrChange, prevValue, newValue);
        }
        
        @Override
        public void overrideStyleTextChanged(final CSSStylableElement elt, final String text) {
            final StyleDeclarationProvider p = elt.getOverrideStyleDeclarationProvider();
            StyleDeclaration declaration = p.getStyleDeclaration();
            int ds = declaration.size();
            final boolean[] updated = new boolean[CSSEngine.this.getNumberOfProperties()];
            for (int i = 0; i < ds; ++i) {
                updated[declaration.getIndex(i)] = true;
            }
            declaration = CSSEngine.this.parseStyleDeclaration(elt, text);
            p.setStyleDeclaration(declaration);
            ds = declaration.size();
            for (int i = 0; i < ds; ++i) {
                updated[declaration.getIndex(i)] = true;
            }
            CSSEngine.this.invalidateProperties(elt, null, updated, true);
        }
        
        @Override
        public void overrideStylePropertyRemoved(final CSSStylableElement elt, final String name) {
            final StyleDeclarationProvider p = elt.getOverrideStyleDeclarationProvider();
            final StyleDeclaration declaration = p.getStyleDeclaration();
            final int idx = CSSEngine.this.getPropertyIndex(name);
            final int ds = declaration.size();
            int i = 0;
            while (i < ds) {
                if (idx == declaration.getIndex(i)) {
                    declaration.remove(i);
                    final StyleMap style = elt.getComputedStyleMap(null);
                    if (style != null && style.getOrigin(idx) == -24576) {
                        CSSEngine.this.invalidateProperties(elt, new int[] { idx }, null, true);
                        break;
                    }
                    break;
                }
                else {
                    ++i;
                }
            }
        }
        
        @Override
        public void overrideStylePropertyChanged(final CSSStylableElement elt, final String name, final String val, final String prio) {
            final boolean important = prio != null && prio.length() != 0;
            final StyleDeclarationProvider p = elt.getOverrideStyleDeclarationProvider();
            this.declaration = p.getStyleDeclaration();
            CSSEngine.this.setMainProperties(elt, this, name, val, important);
            this.declaration = null;
            CSSEngine.this.invalidateProperties(elt, null, this.mainPropertiesChanged, true);
        }
        
        @Override
        public void setMainProperty(final String name, final Value v, final boolean important) {
            final int idx = CSSEngine.this.getPropertyIndex(name);
            if (idx == -1) {
                return;
            }
            int i;
            for (i = 0; i < this.declaration.size() && idx != this.declaration.getIndex(i); ++i) {}
            if (i < this.declaration.size()) {
                this.declaration.put(i, v, idx, important);
            }
            else {
                this.declaration.append(v, idx, important);
            }
        }
    }
    
    protected class DOMNodeInsertedListener implements EventListener
    {
        @Override
        public void handleEvent(final Event evt) {
            CSSEngine.this.handleNodeInserted((Node)evt.getTarget());
        }
    }
    
    protected class DOMNodeRemovedListener implements EventListener
    {
        @Override
        public void handleEvent(final Event evt) {
            CSSEngine.this.handleNodeRemoved((Node)evt.getTarget());
        }
    }
    
    protected class DOMSubtreeModifiedListener implements EventListener
    {
        @Override
        public void handleEvent(final Event evt) {
            CSSEngine.this.handleSubtreeModified((Node)evt.getTarget());
        }
    }
    
    protected class DOMCharacterDataModifiedListener implements EventListener
    {
        @Override
        public void handleEvent(final Event evt) {
            CSSEngine.this.handleCharacterDataModified((Node)evt.getTarget());
        }
    }
    
    protected class DOMAttrModifiedListener implements EventListener
    {
        @Override
        public void handleEvent(final Event evt) {
            final MutationEvent mevt = (MutationEvent)evt;
            CSSEngine.this.handleAttrModified((Element)evt.getTarget(), (Attr)mevt.getRelatedNode(), mevt.getAttrChange(), mevt.getPrevValue(), mevt.getNewValue());
        }
    }
    
    public interface MainPropertyReceiver
    {
        void setMainProperty(final String p0, final Value p1, final boolean p2);
    }
}
