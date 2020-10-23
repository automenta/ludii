// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.w3c.dom.events.Event;
import java.util.HashSet;
import java.util.Set;
import org.apache.batik.gvt.font.GVTGlyphMetrics;
import java.awt.geom.AffineTransform;
import org.apache.batik.gvt.font.GVTGlyphVector;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import org.apache.batik.css.engine.value.ListValue;
import java.awt.AlphaComposite;
import org.apache.batik.css.engine.StyleMap;
import org.apache.batik.css.engine.CSSStylableElement;
import org.apache.batik.anim.dom.SVGOMAnimatedEnumeration;
import org.apache.batik.anim.dom.AbstractSVGAnimatedLength;
import java.awt.Color;
import org.w3c.dom.svg.SVGTextContentElement;
import org.apache.batik.gvt.font.GVTFont;
import org.apache.batik.gvt.font.GVTFontFamily;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.gvt.font.UnresolvedFontFamily;
import java.util.ArrayList;
import java.lang.ref.SoftReference;
import java.util.List;
import org.w3c.dom.svg.SVGNumberList;
import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;
import org.apache.batik.anim.dom.SVGOMAnimatedNumberList;
import org.w3c.dom.svg.SVGTextPositioningElement;
import java.util.Iterator;
import org.apache.batik.dom.util.XLinkSupport;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import org.apache.batik.dom.util.XMLSupport;
import java.util.Map;
import org.apache.batik.gvt.text.TextPath;
import org.apache.batik.css.engine.CSSEngineEvent;
import org.apache.batik.anim.dom.AnimatedLiveAttributeValue;
import org.apache.batik.gvt.text.TextPaintInfo;
import org.w3c.dom.events.MutationEvent;
import org.apache.batik.dom.svg.SVGContext;
import org.apache.batik.anim.dom.SVGOMElement;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.EventListener;
import org.apache.batik.dom.events.NodeEventTarget;
import org.w3c.dom.svg.SVGLengthList;
import org.apache.batik.dom.svg.LiveAttributeException;
import org.apache.batik.anim.dom.SVGOMAnimatedLengthList;
import org.apache.batik.anim.dom.SVGOMTextPositioningElement;
import java.awt.geom.Point2D;
import java.awt.RenderingHints;
import org.w3c.dom.Node;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;
import java.util.WeakHashMap;
import java.text.AttributedString;
import java.text.AttributedCharacterIterator;
import org.apache.batik.dom.svg.SVGTextContent;

public class SVGTextElementBridge extends AbstractGraphicsNodeBridge implements SVGTextContent
{
    protected static final Integer ZERO;
    public static final AttributedCharacterIterator.Attribute TEXT_COMPOUND_DELIMITER;
    public static final AttributedCharacterIterator.Attribute TEXT_COMPOUND_ID;
    public static final AttributedCharacterIterator.Attribute PAINT_INFO;
    public static final AttributedCharacterIterator.Attribute ALT_GLYPH_HANDLER;
    public static final AttributedCharacterIterator.Attribute TEXTPATH;
    public static final AttributedCharacterIterator.Attribute ANCHOR_TYPE;
    public static final AttributedCharacterIterator.Attribute GVT_FONT_FAMILIES;
    public static final AttributedCharacterIterator.Attribute GVT_FONTS;
    public static final AttributedCharacterIterator.Attribute BASELINE_SHIFT;
    protected AttributedString laidoutText;
    protected WeakHashMap elemTPI;
    protected boolean usingComplexSVGFont;
    protected DOMChildNodeRemovedEventListener childNodeRemovedEventListener;
    protected DOMSubtreeModifiedEventListener subtreeModifiedEventListener;
    private boolean hasNewACI;
    private Element cssProceedElement;
    protected int endLimit;
    
    public SVGTextElementBridge() {
        this.elemTPI = new WeakHashMap();
        this.usingComplexSVGFont = false;
    }
    
    @Override
    public String getLocalName() {
        return "text";
    }
    
    @Override
    public Bridge getInstance() {
        return new SVGTextElementBridge();
    }
    
    protected TextNode getTextNode() {
        return (TextNode)this.node;
    }
    
    @Override
    public GraphicsNode createGraphicsNode(final BridgeContext ctx, final Element e) {
        final TextNode node = (TextNode)super.createGraphicsNode(ctx, e);
        if (node == null) {
            return null;
        }
        this.associateSVGContext(ctx, e, node);
        for (Node child = this.getFirstChild(e); child != null; child = this.getNextSibling(child)) {
            if (child.getNodeType() == 1) {
                this.addContextToChild(ctx, (Element)child);
            }
        }
        if (ctx.getTextPainter() != null) {
            node.setTextPainter(ctx.getTextPainter());
        }
        RenderingHints hints = null;
        hints = CSSUtilities.convertColorRendering(e, hints);
        hints = CSSUtilities.convertTextRendering(e, hints);
        if (hints != null) {
            node.setRenderingHints(hints);
        }
        node.setLocation(this.getLocation(ctx, e));
        return node;
    }
    
    @Override
    protected GraphicsNode instantiateGraphicsNode() {
        return new TextNode();
    }
    
    protected Point2D getLocation(final BridgeContext ctx, final Element e) {
        try {
            final SVGOMTextPositioningElement te = (SVGOMTextPositioningElement)e;
            final SVGOMAnimatedLengthList _x = (SVGOMAnimatedLengthList)te.getX();
            _x.check();
            final SVGLengthList xs = _x.getAnimVal();
            float x = 0.0f;
            if (xs.getNumberOfItems() > 0) {
                x = xs.getItem(0).getValue();
            }
            final SVGOMAnimatedLengthList _y = (SVGOMAnimatedLengthList)te.getY();
            _y.check();
            final SVGLengthList ys = _y.getAnimVal();
            float y = 0.0f;
            if (ys.getNumberOfItems() > 0) {
                y = ys.getItem(0).getValue();
            }
            return new Point2D.Float(x, y);
        }
        catch (LiveAttributeException ex) {
            throw new BridgeException(ctx, ex);
        }
    }
    
    protected boolean isTextElement(final Element e) {
        if (!"http://www.w3.org/2000/svg".equals(e.getNamespaceURI())) {
            return false;
        }
        final String nodeName = e.getLocalName();
        return nodeName.equals("text") || nodeName.equals("tspan") || nodeName.equals("altGlyph") || nodeName.equals("a") || nodeName.equals("textPath") || nodeName.equals("tref");
    }
    
    protected boolean isTextChild(final Element e) {
        if (!"http://www.w3.org/2000/svg".equals(e.getNamespaceURI())) {
            return false;
        }
        final String nodeName = e.getLocalName();
        return nodeName.equals("tspan") || nodeName.equals("altGlyph") || nodeName.equals("a") || nodeName.equals("textPath") || nodeName.equals("tref");
    }
    
    @Override
    public void buildGraphicsNode(final BridgeContext ctx, final Element e, final GraphicsNode node) {
        e.normalize();
        this.computeLaidoutText(ctx, e, node);
        node.setComposite(CSSUtilities.convertOpacity(e));
        node.setFilter(CSSUtilities.convertFilter(e, node, ctx));
        node.setMask(CSSUtilities.convertMask(e, node, ctx));
        node.setClip(CSSUtilities.convertClipPath(e, node, ctx));
        node.setPointerEventType(CSSUtilities.convertPointerEvents(e));
        this.initializeDynamicSupport(ctx, e, node);
        if (!ctx.isDynamic()) {
            this.elemTPI.clear();
        }
    }
    
    @Override
    public boolean isComposite() {
        return false;
    }
    
    protected Node getFirstChild(final Node n) {
        return n.getFirstChild();
    }
    
    protected Node getNextSibling(final Node n) {
        return n.getNextSibling();
    }
    
    protected Node getParentNode(final Node n) {
        return n.getParentNode();
    }
    
    @Override
    protected void initializeDynamicSupport(final BridgeContext ctx, final Element e, final GraphicsNode node) {
        super.initializeDynamicSupport(ctx, e, node);
        if (ctx.isDynamic()) {
            this.addTextEventListeners(ctx, (NodeEventTarget)e);
        }
    }
    
    protected void addTextEventListeners(final BridgeContext ctx, final NodeEventTarget e) {
        if (this.childNodeRemovedEventListener == null) {
            this.childNodeRemovedEventListener = new DOMChildNodeRemovedEventListener();
        }
        if (this.subtreeModifiedEventListener == null) {
            this.subtreeModifiedEventListener = new DOMSubtreeModifiedEventListener();
        }
        e.addEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeRemoved", this.childNodeRemovedEventListener, true, null);
        ctx.storeEventListenerNS(e, "http://www.w3.org/2001/xml-events", "DOMNodeRemoved", this.childNodeRemovedEventListener, true);
        e.addEventListenerNS("http://www.w3.org/2001/xml-events", "DOMSubtreeModified", this.subtreeModifiedEventListener, false, null);
        ctx.storeEventListenerNS(e, "http://www.w3.org/2001/xml-events", "DOMSubtreeModified", this.subtreeModifiedEventListener, false);
    }
    
    protected void removeTextEventListeners(final BridgeContext ctx, final NodeEventTarget e) {
        e.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeRemoved", this.childNodeRemovedEventListener, true);
        e.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMSubtreeModified", this.subtreeModifiedEventListener, false);
    }
    
    @Override
    public void dispose() {
        this.removeTextEventListeners(this.ctx, (NodeEventTarget)this.e);
        super.dispose();
    }
    
    protected void addContextToChild(final BridgeContext ctx, final Element e) {
        if ("http://www.w3.org/2000/svg".equals(e.getNamespaceURI())) {
            if (e.getLocalName().equals("tspan")) {
                ((SVGOMElement)e).setSVGContext(new TspanBridge(ctx, this, e));
            }
            else if (e.getLocalName().equals("textPath")) {
                ((SVGOMElement)e).setSVGContext(new TextPathBridge(ctx, this, e));
            }
            else if (e.getLocalName().equals("tref")) {
                ((SVGOMElement)e).setSVGContext(new TRefBridge(ctx, this, e));
            }
        }
        for (Node child = this.getFirstChild(e); child != null; child = this.getNextSibling(child)) {
            if (child.getNodeType() == 1) {
                this.addContextToChild(ctx, (Element)child);
            }
        }
    }
    
    protected void removeContextFromChild(final BridgeContext ctx, final Element e) {
        if ("http://www.w3.org/2000/svg".equals(e.getNamespaceURI())) {
            if (e.getLocalName().equals("tspan")) {
                ((AbstractTextChildBridgeUpdateHandler)((SVGOMElement)e).getSVGContext()).dispose();
            }
            else if (e.getLocalName().equals("textPath")) {
                ((AbstractTextChildBridgeUpdateHandler)((SVGOMElement)e).getSVGContext()).dispose();
            }
            else if (e.getLocalName().equals("tref")) {
                ((AbstractTextChildBridgeUpdateHandler)((SVGOMElement)e).getSVGContext()).dispose();
            }
        }
        for (Node child = this.getFirstChild(e); child != null; child = this.getNextSibling(child)) {
            if (child.getNodeType() == 1) {
                this.removeContextFromChild(ctx, (Element)child);
            }
        }
    }
    
    @Override
    public void handleDOMNodeInsertedEvent(final MutationEvent evt) {
        final Node childNode = (Node)evt.getTarget();
        switch (childNode.getNodeType()) {
            case 3:
            case 4: {
                this.laidoutText = null;
                break;
            }
            case 1: {
                final Element childElement = (Element)childNode;
                if (this.isTextChild(childElement)) {
                    this.addContextToChild(this.ctx, childElement);
                    this.laidoutText = null;
                    break;
                }
                break;
            }
        }
        if (this.laidoutText == null) {
            this.computeLaidoutText(this.ctx, this.e, this.getTextNode());
        }
    }
    
    public void handleDOMChildNodeRemovedEvent(final MutationEvent evt) {
        final Node childNode = (Node)evt.getTarget();
        switch (childNode.getNodeType()) {
            case 3:
            case 4: {
                if (this.isParentDisplayed(childNode)) {
                    this.laidoutText = null;
                    break;
                }
                break;
            }
            case 1: {
                final Element childElt = (Element)childNode;
                if (this.isTextChild(childElt)) {
                    this.laidoutText = null;
                    this.removeContextFromChild(this.ctx, childElt);
                    break;
                }
                break;
            }
        }
    }
    
    public void handleDOMSubtreeModifiedEvent(final MutationEvent evt) {
        if (this.laidoutText == null) {
            this.computeLaidoutText(this.ctx, this.e, this.getTextNode());
        }
    }
    
    @Override
    public void handleDOMCharacterDataModified(final MutationEvent evt) {
        final Node childNode = (Node)evt.getTarget();
        if (this.isParentDisplayed(childNode)) {
            this.laidoutText = null;
        }
    }
    
    protected boolean isParentDisplayed(final Node childNode) {
        final Node parentNode = this.getParentNode(childNode);
        return this.isTextElement((Element)parentNode);
    }
    
    protected void computeLaidoutText(final BridgeContext ctx, final Element e, final GraphicsNode node) {
        final TextNode tn = (TextNode)node;
        this.elemTPI.clear();
        final AttributedString as = this.buildAttributedString(ctx, e);
        if (as == null) {
            tn.setAttributedCharacterIterator(null);
            return;
        }
        this.addGlyphPositionAttributes(as, e, ctx);
        if (ctx.isDynamic()) {
            this.laidoutText = new AttributedString(as.getIterator());
        }
        tn.setAttributedCharacterIterator(as.getIterator());
        final TextPaintInfo pi = new TextPaintInfo();
        this.setBaseTextPaintInfo(pi, e, node, ctx);
        this.setDecorationTextPaintInfo(pi, e);
        this.addPaintAttributes(as, e, tn, pi, ctx);
        if (this.usingComplexSVGFont) {
            tn.setAttributedCharacterIterator(as.getIterator());
        }
        if (ctx.isDynamic()) {
            this.checkBBoxChange();
        }
    }
    
    @Override
    public void handleAnimatedAttributeChanged(final AnimatedLiveAttributeValue alav) {
        if (alav.getNamespaceURI() == null) {
            final String ln = alav.getLocalName();
            if (ln.equals("x") || ln.equals("y") || ln.equals("dx") || ln.equals("dy") || ln.equals("rotate") || ln.equals("textLength") || ln.equals("lengthAdjust")) {
                final char c = ln.charAt(0);
                if (c == 'x' || c == 'y') {
                    this.getTextNode().setLocation(this.getLocation(this.ctx, this.e));
                }
                this.computeLaidoutText(this.ctx, this.e, this.getTextNode());
                return;
            }
        }
        super.handleAnimatedAttributeChanged(alav);
    }
    
    @Override
    public void handleCSSEngineEvent(final CSSEngineEvent evt) {
        this.hasNewACI = false;
        final int[] arr$;
        final int[] properties = arr$ = evt.getProperties();
        for (final int property : arr$) {
            switch (property) {
                case 1:
                case 11:
                case 12:
                case 21:
                case 22:
                case 24:
                case 25:
                case 27:
                case 28:
                case 29:
                case 31:
                case 32:
                case 53:
                case 56:
                case 58:
                case 59: {
                    if (!this.hasNewACI) {
                        this.hasNewACI = true;
                        this.computeLaidoutText(this.ctx, this.e, this.getTextNode());
                        break;
                    }
                    break;
                }
            }
        }
        this.cssProceedElement = evt.getElement();
        super.handleCSSEngineEvent(evt);
        this.cssProceedElement = null;
    }
    
    @Override
    protected void handleCSSPropertyChanged(final int property) {
        switch (property) {
            case 15:
            case 16:
            case 45:
            case 46:
            case 47:
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 54: {
                this.rebuildACI();
                break;
            }
            case 57: {
                this.rebuildACI();
                super.handleCSSPropertyChanged(property);
                break;
            }
            case 55: {
                RenderingHints hints = this.node.getRenderingHints();
                hints = CSSUtilities.convertTextRendering(this.e, hints);
                if (hints != null) {
                    this.node.setRenderingHints(hints);
                    break;
                }
                break;
            }
            case 9: {
                RenderingHints hints = this.node.getRenderingHints();
                hints = CSSUtilities.convertColorRendering(this.e, hints);
                if (hints != null) {
                    this.node.setRenderingHints(hints);
                    break;
                }
                break;
            }
            default: {
                super.handleCSSPropertyChanged(property);
                break;
            }
        }
    }
    
    protected void rebuildACI() {
        if (this.hasNewACI) {
            return;
        }
        final TextNode textNode = this.getTextNode();
        if (textNode.getAttributedCharacterIterator() == null) {
            return;
        }
        TextPaintInfo pi;
        TextPaintInfo oldPI;
        if (this.cssProceedElement == this.e) {
            pi = new TextPaintInfo();
            this.setBaseTextPaintInfo(pi, this.e, this.node, this.ctx);
            this.setDecorationTextPaintInfo(pi, this.e);
            oldPI = this.elemTPI.get(this.e);
        }
        else {
            final TextPaintInfo parentPI = this.getParentTextPaintInfo(this.cssProceedElement);
            pi = this.getTextPaintInfo(this.cssProceedElement, textNode, parentPI, this.ctx);
            oldPI = this.elemTPI.get(this.cssProceedElement);
        }
        if (oldPI == null) {
            return;
        }
        textNode.swapTextPaintInfo(pi, oldPI);
        if (this.usingComplexSVGFont) {
            textNode.setAttributedCharacterIterator(textNode.getAttributedCharacterIterator());
        }
    }
    
    int getElementStartIndex(final Element element) {
        final TextPaintInfo tpi = this.elemTPI.get(element);
        if (tpi == null) {
            return -1;
        }
        return tpi.startChar;
    }
    
    int getElementEndIndex(final Element element) {
        final TextPaintInfo tpi = this.elemTPI.get(element);
        if (tpi == null) {
            return -1;
        }
        return tpi.endChar;
    }
    
    protected AttributedString buildAttributedString(final BridgeContext ctx, final Element element) {
        final AttributedStringBuffer asb = new AttributedStringBuffer();
        this.fillAttributedStringBuffer(ctx, element, true, null, null, null, asb);
        return asb.toAttributedString();
    }
    
    protected void fillAttributedStringBuffer(final BridgeContext ctx, final Element element, final boolean top, final TextPath textPath, final Integer bidiLevel, Map initialAttributes, final AttributedStringBuffer asb) {
        if (!SVGUtilities.matchUserAgent(element, ctx.getUserAgent()) || !CSSUtilities.convertDisplay(element)) {
            return;
        }
        String s = XMLSupport.getXMLSpace(element);
        final boolean preserve = s.equals("preserve");
        Element nodeElement = element;
        final int elementStartChar = asb.length();
        if (top) {
            this.endLimit = 0;
        }
        if (preserve) {
            this.endLimit = asb.length();
        }
        final Map map = (initialAttributes == null) ? new HashMap() : new HashMap(initialAttributes);
        initialAttributes = this.getAttributeMap(ctx, element, textPath, bidiLevel, map);
        final Object o = map.get(TextAttribute.BIDI_EMBEDDING);
        Integer subBidiLevel = bidiLevel;
        if (o != null) {
            subBidiLevel = (Integer)o;
        }
        for (Node n = this.getFirstChild(element); n != null; n = this.getNextSibling(n)) {
            final boolean prevEndsWithSpace = !preserve && (asb.length() == 0 || asb.getLastChar() == 32);
            switch (n.getNodeType()) {
                case 1: {
                    if (!"http://www.w3.org/2000/svg".equals(n.getNamespaceURI())) {
                        break;
                    }
                    nodeElement = (Element)n;
                    final String ln = n.getLocalName();
                    if (ln.equals("tspan") || ln.equals("altGlyph")) {
                        final int before = asb.count;
                        this.fillAttributedStringBuffer(ctx, nodeElement, false, textPath, subBidiLevel, initialAttributes, asb);
                        if (asb.count != before) {
                            initialAttributes = null;
                        }
                        break;
                    }
                    if (ln.equals("textPath")) {
                        final SVGTextPathElementBridge textPathBridge = (SVGTextPathElementBridge)ctx.getBridge(nodeElement);
                        final TextPath newTextPath = textPathBridge.createTextPath(ctx, nodeElement);
                        if (newTextPath != null) {
                            final int before2 = asb.count;
                            this.fillAttributedStringBuffer(ctx, nodeElement, false, newTextPath, subBidiLevel, initialAttributes, asb);
                            if (asb.count != before2) {
                                initialAttributes = null;
                            }
                        }
                        break;
                    }
                    if (ln.equals("tref")) {
                        final String uriStr = XLinkSupport.getXLinkHref((Element)n);
                        final Element ref = ctx.getReferencedElement((Element)n, uriStr);
                        s = TextUtilities.getElementContent(ref);
                        s = this.normalizeString(s, preserve, prevEndsWithSpace);
                        if (s.length() != 0) {
                            final int trefStart = asb.length();
                            final Map m = (initialAttributes == null) ? new HashMap() : new HashMap(initialAttributes);
                            this.getAttributeMap(ctx, nodeElement, textPath, bidiLevel, m);
                            asb.append(s, m);
                            final int trefEnd = asb.length() - 1;
                            final TextPaintInfo tpi = this.elemTPI.get(nodeElement);
                            tpi.startChar = trefStart;
                            tpi.endChar = trefEnd;
                            initialAttributes = null;
                        }
                        break;
                    }
                    if (ln.equals("a")) {
                        final NodeEventTarget target = (NodeEventTarget)nodeElement;
                        final UserAgent ua = ctx.getUserAgent();
                        final SVGAElementBridge.CursorHolder ch = new SVGAElementBridge.CursorHolder(CursorManager.DEFAULT_CURSOR);
                        final EventListener l = new SVGAElementBridge.AnchorListener(ua, ch);
                        target.addEventListenerNS("http://www.w3.org/2001/xml-events", "click", l, false, null);
                        ctx.storeEventListenerNS(target, "http://www.w3.org/2001/xml-events", "click", l, false);
                        final int before3 = asb.count;
                        this.fillAttributedStringBuffer(ctx, nodeElement, false, textPath, subBidiLevel, initialAttributes, asb);
                        if (asb.count != before3) {
                            initialAttributes = null;
                        }
                        break;
                    }
                    break;
                }
                case 3:
                case 4: {
                    s = n.getNodeValue();
                    s = this.normalizeString(s, preserve, prevEndsWithSpace);
                    if (s.length() != 0) {
                        asb.append(s, map);
                        if (preserve) {
                            this.endLimit = asb.length();
                        }
                        initialAttributes = null;
                        break;
                    }
                    break;
                }
            }
        }
        if (top) {
            boolean strippedSome = false;
            while (this.endLimit < asb.length() && asb.getLastChar() == 32) {
                asb.stripLast();
                strippedSome = true;
            }
            if (strippedSome) {
                for (final Object o2 : this.elemTPI.values()) {
                    final TextPaintInfo tpi2 = (TextPaintInfo)o2;
                    if (tpi2.endChar >= asb.length()) {
                        tpi2.endChar = asb.length() - 1;
                        if (tpi2.startChar <= tpi2.endChar) {
                            continue;
                        }
                        tpi2.startChar = tpi2.endChar;
                    }
                }
            }
        }
        final int elementEndChar = asb.length() - 1;
        final TextPaintInfo tpi3 = this.elemTPI.get(element);
        tpi3.startChar = elementStartChar;
        tpi3.endChar = elementEndChar;
    }
    
    protected String normalizeString(final String s, final boolean preserve, final boolean stripfirst) {
        final StringBuffer sb = new StringBuffer(s.length());
        if (preserve) {
            for (int i = 0; i < s.length(); ++i) {
                final char c = s.charAt(i);
                switch (c) {
                    case '\t':
                    case '\n':
                    case '\r': {
                        sb.append(' ');
                        break;
                    }
                    default: {
                        sb.append(c);
                        break;
                    }
                }
            }
            return sb.toString();
        }
        int idx = 0;
        Label_0177: {
            if (stripfirst) {
                while (idx < s.length()) {
                    switch (s.charAt(idx)) {
                        default: {
                            break Label_0177;
                        }
                        case '\t':
                        case '\n':
                        case '\r':
                        case ' ': {
                            ++idx;
                            continue;
                        }
                    }
                }
            }
        }
        boolean space = false;
        for (int j = idx; j < s.length(); ++j) {
            final char c2 = s.charAt(j);
            switch (c2) {
                case '\n':
                case '\r': {
                    break;
                }
                case '\t':
                case ' ': {
                    if (!space) {
                        sb.append(' ');
                        space = true;
                        break;
                    }
                    break;
                }
                default: {
                    sb.append(c2);
                    space = false;
                    break;
                }
            }
        }
        return sb.toString();
    }
    
    protected boolean nodeAncestorOf(final Node node1, final Node node2) {
        if (node2 == null || node1 == null) {
            return false;
        }
        Node parent;
        for (parent = this.getParentNode(node2); parent != null && parent != node1; parent = this.getParentNode(parent)) {}
        return parent == node1;
    }
    
    protected void addGlyphPositionAttributes(final AttributedString as, final Element element, final BridgeContext ctx) {
        if (!SVGUtilities.matchUserAgent(element, ctx.getUserAgent()) || !CSSUtilities.convertDisplay(element)) {
            return;
        }
        if (element.getLocalName().equals("textPath")) {
            this.addChildGlyphPositionAttributes(as, element, ctx);
            return;
        }
        final int firstChar = this.getElementStartIndex(element);
        if (firstChar == -1) {
            return;
        }
        final int lastChar = this.getElementEndIndex(element);
        if (!(element instanceof SVGTextPositioningElement)) {
            this.addChildGlyphPositionAttributes(as, element, ctx);
            return;
        }
        final SVGTextPositioningElement te = (SVGTextPositioningElement)element;
        try {
            final SVGOMAnimatedLengthList _x = (SVGOMAnimatedLengthList)te.getX();
            _x.check();
            final SVGOMAnimatedLengthList _y = (SVGOMAnimatedLengthList)te.getY();
            _y.check();
            final SVGOMAnimatedLengthList _dx = (SVGOMAnimatedLengthList)te.getDx();
            _dx.check();
            final SVGOMAnimatedLengthList _dy = (SVGOMAnimatedLengthList)te.getDy();
            _dy.check();
            final SVGOMAnimatedNumberList _rotate = (SVGOMAnimatedNumberList)te.getRotate();
            _rotate.check();
            final SVGLengthList xs = _x.getAnimVal();
            final SVGLengthList ys = _y.getAnimVal();
            final SVGLengthList dxs = _dx.getAnimVal();
            final SVGLengthList dys = _dy.getAnimVal();
            final SVGNumberList rs = _rotate.getAnimVal();
            for (int len = xs.getNumberOfItems(), i = 0; i < len && firstChar + i <= lastChar; ++i) {
                as.addAttribute(GVTAttributedCharacterIterator.TextAttribute.X, xs.getItem(i).getValue(), firstChar + i, firstChar + i + 1);
            }
            for (int len = ys.getNumberOfItems(), i = 0; i < len && firstChar + i <= lastChar; ++i) {
                as.addAttribute(GVTAttributedCharacterIterator.TextAttribute.Y, ys.getItem(i).getValue(), firstChar + i, firstChar + i + 1);
            }
            for (int len = dxs.getNumberOfItems(), i = 0; i < len && firstChar + i <= lastChar; ++i) {
                as.addAttribute(GVTAttributedCharacterIterator.TextAttribute.DX, dxs.getItem(i).getValue(), firstChar + i, firstChar + i + 1);
            }
            for (int len = dys.getNumberOfItems(), i = 0; i < len && firstChar + i <= lastChar; ++i) {
                as.addAttribute(GVTAttributedCharacterIterator.TextAttribute.DY, dys.getItem(i).getValue(), firstChar + i, firstChar + i + 1);
            }
            final int len = rs.getNumberOfItems();
            if (len == 1) {
                final Float rad = (float)Math.toRadians(rs.getItem(0).getValue());
                as.addAttribute(GVTAttributedCharacterIterator.TextAttribute.ROTATION, rad, firstChar, lastChar + 1);
            }
            else if (len > 1) {
                for (int i = 0; i < len && firstChar + i <= lastChar; ++i) {
                    final Float rad2 = (float)Math.toRadians(rs.getItem(i).getValue());
                    as.addAttribute(GVTAttributedCharacterIterator.TextAttribute.ROTATION, rad2, firstChar + i, firstChar + i + 1);
                }
            }
            this.addChildGlyphPositionAttributes(as, element, ctx);
        }
        catch (LiveAttributeException ex) {
            throw new BridgeException(ctx, ex);
        }
    }
    
    protected void addChildGlyphPositionAttributes(final AttributedString as, final Element element, final BridgeContext ctx) {
        for (Node child = this.getFirstChild(element); child != null; child = this.getNextSibling(child)) {
            if (child.getNodeType() == 1) {
                final Element childElement = (Element)child;
                if (this.isTextChild(childElement)) {
                    this.addGlyphPositionAttributes(as, childElement, ctx);
                }
            }
        }
    }
    
    protected void addPaintAttributes(final AttributedString as, final Element element, final TextNode node, final TextPaintInfo pi, final BridgeContext ctx) {
        if (!SVGUtilities.matchUserAgent(element, ctx.getUserAgent()) || !CSSUtilities.convertDisplay(element)) {
            return;
        }
        final Object o = this.elemTPI.get(element);
        if (o != null) {
            node.swapTextPaintInfo(pi, (TextPaintInfo)o);
        }
        this.addChildPaintAttributes(as, element, node, pi, ctx);
    }
    
    protected void addChildPaintAttributes(final AttributedString as, final Element element, final TextNode node, final TextPaintInfo parentPI, final BridgeContext ctx) {
        for (Node child = this.getFirstChild(element); child != null; child = this.getNextSibling(child)) {
            if (child.getNodeType() == 1) {
                final Element childElement = (Element)child;
                if (this.isTextChild(childElement)) {
                    final TextPaintInfo pi = this.getTextPaintInfo(childElement, node, parentPI, ctx);
                    this.addPaintAttributes(as, childElement, node, pi, ctx);
                }
            }
        }
    }
    
    protected List getFontList(final BridgeContext ctx, final Element element, final Map result) {
        result.put(SVGTextElementBridge.TEXT_COMPOUND_ID, new SoftReference<Element>(element));
        final Float fsFloat = TextUtilities.convertFontSize(element);
        final float fontSize = fsFloat;
        result.put(TextAttribute.SIZE, fsFloat);
        result.put(TextAttribute.WIDTH, TextUtilities.convertFontStretch(element));
        result.put(TextAttribute.POSTURE, TextUtilities.convertFontStyle(element));
        result.put(TextAttribute.WEIGHT, TextUtilities.convertFontWeight(element));
        final Value v = CSSUtilities.getComputedStyle(element, 27);
        final String fontWeightString = v.getCssText();
        final String fontStyleString = CSSUtilities.getComputedStyle(element, 25).getStringValue();
        result.put(SVGTextElementBridge.TEXT_COMPOUND_DELIMITER, element);
        final Value val = CSSUtilities.getComputedStyle(element, 21);
        final List fontFamilyList = new ArrayList();
        final List fontList = new ArrayList();
        for (int len = val.getLength(), i = 0; i < len; ++i) {
            final Value it = val.item(i);
            final String fontFamilyName = it.getStringValue();
            GVTFontFamily fontFamily = SVGFontUtilities.getFontFamily(element, ctx, fontFamilyName, fontWeightString, fontStyleString);
            if (fontFamily != null && fontFamily instanceof UnresolvedFontFamily) {
                fontFamily = ctx.getFontFamilyResolver().resolve(fontFamily.getFamilyName());
            }
            if (fontFamily != null) {
                fontFamilyList.add(fontFamily);
                if (fontFamily.isComplex()) {
                    this.usingComplexSVGFont = true;
                }
                final GVTFont ft = fontFamily.deriveFont(fontSize, result);
                fontList.add(ft);
            }
        }
        result.put(SVGTextElementBridge.GVT_FONT_FAMILIES, (SoftReference<Element>)fontFamilyList);
        if (!ctx.isDynamic()) {
            result.remove(SVGTextElementBridge.TEXT_COMPOUND_DELIMITER);
        }
        return fontList;
    }
    
    protected Map getAttributeMap(final BridgeContext ctx, final Element element, final TextPath textPath, final Integer bidiLevel, final Map result) {
        SVGTextContentElement tce = null;
        if (element instanceof SVGTextContentElement) {
            tce = (SVGTextContentElement)element;
        }
        Map inheritMap = null;
        if ("http://www.w3.org/2000/svg".equals(element.getNamespaceURI()) && element.getLocalName().equals("altGlyph")) {
            result.put(SVGTextElementBridge.ALT_GLYPH_HANDLER, new SVGAltGlyphHandler(ctx, element));
        }
        final TextPaintInfo pi = new TextPaintInfo();
        pi.visible = true;
        pi.fillPaint = Color.black;
        result.put(SVGTextElementBridge.PAINT_INFO, pi);
        this.elemTPI.put(element, pi);
        if (textPath != null) {
            result.put(SVGTextElementBridge.TEXTPATH, textPath);
        }
        final TextNode.Anchor a = TextUtilities.convertTextAnchor(element);
        result.put(SVGTextElementBridge.ANCHOR_TYPE, a);
        final List fontList = this.getFontList(ctx, element, result);
        result.put(SVGTextElementBridge.GVT_FONTS, fontList);
        final Object bs = TextUtilities.convertBaselineShift(element);
        if (bs != null) {
            result.put(SVGTextElementBridge.BASELINE_SHIFT, bs);
        }
        Value val = CSSUtilities.getComputedStyle(element, 56);
        String s = val.getStringValue();
        if (s.charAt(0) == 'n') {
            if (bidiLevel != null) {
                result.put(TextAttribute.BIDI_EMBEDDING, bidiLevel);
            }
        }
        else {
            val = CSSUtilities.getComputedStyle(element, 11);
            final String rs = val.getStringValue();
            int cbidi = 0;
            if (bidiLevel != null) {
                cbidi = bidiLevel;
            }
            if (cbidi < 0) {
                cbidi = -cbidi;
            }
            switch (rs.charAt(0)) {
                case 'l': {
                    result.put(TextAttribute.RUN_DIRECTION, TextAttribute.RUN_DIRECTION_LTR);
                    if ((cbidi & 0x1) == 0x1) {
                        ++cbidi;
                        break;
                    }
                    cbidi += 2;
                    break;
                }
                case 'r': {
                    result.put(TextAttribute.RUN_DIRECTION, TextAttribute.RUN_DIRECTION_RTL);
                    if ((cbidi & 0x1) == 0x1) {
                        cbidi += 2;
                        break;
                    }
                    ++cbidi;
                    break;
                }
            }
            switch (s.charAt(0)) {
                case 'b': {
                    cbidi = -cbidi;
                    break;
                }
            }
            result.put(TextAttribute.BIDI_EMBEDDING, cbidi);
        }
        val = CSSUtilities.getComputedStyle(element, 59);
        s = val.getStringValue();
        switch (s.charAt(0)) {
            case 'l': {
                result.put(GVTAttributedCharacterIterator.TextAttribute.WRITING_MODE, GVTAttributedCharacterIterator.TextAttribute.WRITING_MODE_LTR);
                break;
            }
            case 'r': {
                result.put(GVTAttributedCharacterIterator.TextAttribute.WRITING_MODE, GVTAttributedCharacterIterator.TextAttribute.WRITING_MODE_RTL);
                break;
            }
            case 't': {
                result.put(GVTAttributedCharacterIterator.TextAttribute.WRITING_MODE, GVTAttributedCharacterIterator.TextAttribute.WRITING_MODE_TTB);
                break;
            }
        }
        val = CSSUtilities.getComputedStyle(element, 29);
        int primitiveType = val.getPrimitiveType();
        switch (primitiveType) {
            case 21: {
                result.put(GVTAttributedCharacterIterator.TextAttribute.VERTICAL_ORIENTATION, GVTAttributedCharacterIterator.TextAttribute.ORIENTATION_AUTO);
                break;
            }
            case 11: {
                result.put(GVTAttributedCharacterIterator.TextAttribute.VERTICAL_ORIENTATION, GVTAttributedCharacterIterator.TextAttribute.ORIENTATION_ANGLE);
                result.put(GVTAttributedCharacterIterator.TextAttribute.VERTICAL_ORIENTATION_ANGLE, val.getFloatValue());
                break;
            }
            case 12: {
                result.put(GVTAttributedCharacterIterator.TextAttribute.VERTICAL_ORIENTATION, GVTAttributedCharacterIterator.TextAttribute.ORIENTATION_ANGLE);
                result.put(GVTAttributedCharacterIterator.TextAttribute.VERTICAL_ORIENTATION_ANGLE, (float)Math.toDegrees(val.getFloatValue()));
                break;
            }
            case 13: {
                result.put(GVTAttributedCharacterIterator.TextAttribute.VERTICAL_ORIENTATION, GVTAttributedCharacterIterator.TextAttribute.ORIENTATION_ANGLE);
                result.put(GVTAttributedCharacterIterator.TextAttribute.VERTICAL_ORIENTATION_ANGLE, val.getFloatValue() * 9.0f / 5.0f);
                break;
            }
            default: {
                throw new IllegalStateException("unexpected primitiveType (V):" + primitiveType);
            }
        }
        val = CSSUtilities.getComputedStyle(element, 28);
        primitiveType = val.getPrimitiveType();
        switch (primitiveType) {
            case 11: {
                result.put(GVTAttributedCharacterIterator.TextAttribute.HORIZONTAL_ORIENTATION_ANGLE, val.getFloatValue());
                break;
            }
            case 12: {
                result.put(GVTAttributedCharacterIterator.TextAttribute.HORIZONTAL_ORIENTATION_ANGLE, (float)Math.toDegrees(val.getFloatValue()));
                break;
            }
            case 13: {
                result.put(GVTAttributedCharacterIterator.TextAttribute.HORIZONTAL_ORIENTATION_ANGLE, val.getFloatValue() * 9.0f / 5.0f);
                break;
            }
            default: {
                throw new IllegalStateException("unexpected primitiveType (H):" + primitiveType);
            }
        }
        Float sp = TextUtilities.convertLetterSpacing(element);
        if (sp != null) {
            result.put(GVTAttributedCharacterIterator.TextAttribute.LETTER_SPACING, sp);
            result.put(GVTAttributedCharacterIterator.TextAttribute.CUSTOM_SPACING, Boolean.TRUE);
        }
        sp = TextUtilities.convertWordSpacing(element);
        if (sp != null) {
            result.put(GVTAttributedCharacterIterator.TextAttribute.WORD_SPACING, sp);
            result.put(GVTAttributedCharacterIterator.TextAttribute.CUSTOM_SPACING, Boolean.TRUE);
        }
        sp = TextUtilities.convertKerning(element);
        if (sp != null) {
            result.put(GVTAttributedCharacterIterator.TextAttribute.KERNING, sp);
            result.put(GVTAttributedCharacterIterator.TextAttribute.CUSTOM_SPACING, Boolean.TRUE);
        }
        if (tce == null) {
            return inheritMap;
        }
        try {
            final AbstractSVGAnimatedLength textLength = (AbstractSVGAnimatedLength)tce.getTextLength();
            if (textLength.isSpecified()) {
                if (inheritMap == null) {
                    inheritMap = new HashMap();
                }
                final Object value = textLength.getCheckedValue();
                result.put(GVTAttributedCharacterIterator.TextAttribute.BBOX_WIDTH, value);
                inheritMap.put(GVTAttributedCharacterIterator.TextAttribute.BBOX_WIDTH, value);
                final SVGOMAnimatedEnumeration _lengthAdjust = (SVGOMAnimatedEnumeration)tce.getLengthAdjust();
                if (_lengthAdjust.getCheckedVal() == 2) {
                    result.put(GVTAttributedCharacterIterator.TextAttribute.LENGTH_ADJUST, GVTAttributedCharacterIterator.TextAttribute.ADJUST_ALL);
                    inheritMap.put(GVTAttributedCharacterIterator.TextAttribute.LENGTH_ADJUST, GVTAttributedCharacterIterator.TextAttribute.ADJUST_ALL);
                }
                else {
                    result.put(GVTAttributedCharacterIterator.TextAttribute.LENGTH_ADJUST, GVTAttributedCharacterIterator.TextAttribute.ADJUST_SPACING);
                    inheritMap.put(GVTAttributedCharacterIterator.TextAttribute.LENGTH_ADJUST, GVTAttributedCharacterIterator.TextAttribute.ADJUST_SPACING);
                    result.put(GVTAttributedCharacterIterator.TextAttribute.CUSTOM_SPACING, Boolean.TRUE);
                    inheritMap.put(GVTAttributedCharacterIterator.TextAttribute.CUSTOM_SPACING, Boolean.TRUE);
                }
            }
        }
        catch (LiveAttributeException ex) {
            throw new BridgeException(ctx, ex);
        }
        return inheritMap;
    }
    
    protected TextPaintInfo getParentTextPaintInfo(final Element child) {
        for (Node parent = this.getParentNode(child); parent != null; parent = this.getParentNode(parent)) {
            final TextPaintInfo tpi = this.elemTPI.get(parent);
            if (tpi != null) {
                return tpi;
            }
        }
        return null;
    }
    
    protected TextPaintInfo getTextPaintInfo(final Element element, final GraphicsNode node, final TextPaintInfo parentTPI, final BridgeContext ctx) {
        CSSUtilities.getComputedStyle(element, 54);
        final TextPaintInfo pi = new TextPaintInfo(parentTPI);
        final StyleMap sm = ((CSSStylableElement)element).getComputedStyleMap(null);
        if (sm.isNullCascaded(54) && sm.isNullCascaded(15) && sm.isNullCascaded(45) && sm.isNullCascaded(52) && sm.isNullCascaded(38)) {
            return pi;
        }
        this.setBaseTextPaintInfo(pi, element, node, ctx);
        if (!sm.isNullCascaded(54)) {
            this.setDecorationTextPaintInfo(pi, element);
        }
        return pi;
    }
    
    public void setBaseTextPaintInfo(final TextPaintInfo pi, final Element element, final GraphicsNode node, final BridgeContext ctx) {
        if (!element.getLocalName().equals("text")) {
            pi.composite = CSSUtilities.convertOpacity(element);
        }
        else {
            pi.composite = AlphaComposite.SrcOver;
        }
        pi.visible = CSSUtilities.convertVisibility(element);
        pi.fillPaint = PaintServer.convertFillPaint(element, node, ctx);
        pi.strokePaint = PaintServer.convertStrokePaint(element, node, ctx);
        pi.strokeStroke = PaintServer.convertStroke(element);
    }
    
    public void setDecorationTextPaintInfo(final TextPaintInfo pi, final Element element) {
        final Value val = CSSUtilities.getComputedStyle(element, 54);
        switch (val.getCssValueType()) {
            case 2: {
                final ListValue lst = (ListValue)val;
                for (int len = lst.getLength(), i = 0; i < len; ++i) {
                    final Value v = lst.item(i);
                    final String s = v.getStringValue();
                    switch (s.charAt(0)) {
                        case 'u': {
                            if (pi.fillPaint != null) {
                                pi.underlinePaint = pi.fillPaint;
                            }
                            if (pi.strokePaint != null) {
                                pi.underlineStrokePaint = pi.strokePaint;
                            }
                            if (pi.strokeStroke != null) {
                                pi.underlineStroke = pi.strokeStroke;
                                break;
                            }
                            break;
                        }
                        case 'o': {
                            if (pi.fillPaint != null) {
                                pi.overlinePaint = pi.fillPaint;
                            }
                            if (pi.strokePaint != null) {
                                pi.overlineStrokePaint = pi.strokePaint;
                            }
                            if (pi.strokeStroke != null) {
                                pi.overlineStroke = pi.strokeStroke;
                                break;
                            }
                            break;
                        }
                        case 'l': {
                            if (pi.fillPaint != null) {
                                pi.strikethroughPaint = pi.fillPaint;
                            }
                            if (pi.strokePaint != null) {
                                pi.strikethroughStrokePaint = pi.strokePaint;
                            }
                            if (pi.strokeStroke != null) {
                                pi.strikethroughStroke = pi.strokeStroke;
                                break;
                            }
                            break;
                        }
                    }
                }
                break;
            }
            default: {
                pi.underlinePaint = null;
                pi.underlineStrokePaint = null;
                pi.underlineStroke = null;
                pi.overlinePaint = null;
                pi.overlineStrokePaint = null;
                pi.overlineStroke = null;
                pi.strikethroughPaint = null;
                pi.strikethroughStrokePaint = null;
                pi.strikethroughStroke = null;
                break;
            }
        }
    }
    
    @Override
    public int getNumberOfChars() {
        return this.getNumberOfChars(this.e);
    }
    
    @Override
    public Rectangle2D getExtentOfChar(final int charnum) {
        return this.getExtentOfChar(this.e, charnum);
    }
    
    @Override
    public Point2D getStartPositionOfChar(final int charnum) {
        return this.getStartPositionOfChar(this.e, charnum);
    }
    
    @Override
    public Point2D getEndPositionOfChar(final int charnum) {
        return this.getEndPositionOfChar(this.e, charnum);
    }
    
    @Override
    public void selectSubString(final int charnum, final int nchars) {
        this.selectSubString(this.e, charnum, nchars);
    }
    
    @Override
    public float getRotationOfChar(final int charnum) {
        return this.getRotationOfChar(this.e, charnum);
    }
    
    @Override
    public float getComputedTextLength() {
        return this.getComputedTextLength(this.e);
    }
    
    @Override
    public float getSubStringLength(final int charnum, final int nchars) {
        return this.getSubStringLength(this.e, charnum, nchars);
    }
    
    @Override
    public int getCharNumAtPosition(final float x, final float y) {
        return this.getCharNumAtPosition(this.e, x, y);
    }
    
    protected int getNumberOfChars(final Element element) {
        final AttributedCharacterIterator aci = this.getTextNode().getAttributedCharacterIterator();
        if (aci == null) {
            return 0;
        }
        final int firstChar = this.getElementStartIndex(element);
        if (firstChar == -1) {
            return 0;
        }
        final int lastChar = this.getElementEndIndex(element);
        return lastChar - firstChar + 1;
    }
    
    protected Rectangle2D getExtentOfChar(final Element element, final int charnum) {
        final TextNode textNode = this.getTextNode();
        final AttributedCharacterIterator aci = textNode.getAttributedCharacterIterator();
        if (aci == null) {
            return null;
        }
        final int firstChar = this.getElementStartIndex(element);
        if (firstChar == -1) {
            return null;
        }
        final List list = this.getTextRuns(textNode);
        final CharacterInformation info = this.getCharacterInformation(list, firstChar, charnum, aci);
        if (info == null) {
            return null;
        }
        final GVTGlyphVector it = info.layout.getGlyphVector();
        Shape b = null;
        if (info.glyphIndexStart == info.glyphIndexEnd) {
            if (it.isGlyphVisible(info.glyphIndexStart)) {
                b = it.getGlyphCellBounds(info.glyphIndexStart);
            }
        }
        else {
            GeneralPath path = null;
            for (int k = info.glyphIndexStart; k <= info.glyphIndexEnd; ++k) {
                if (it.isGlyphVisible(k)) {
                    final Rectangle2D gb = it.getGlyphCellBounds(k);
                    if (path == null) {
                        path = new GeneralPath(gb);
                    }
                    else {
                        path.append(gb, false);
                    }
                }
            }
            b = path;
        }
        if (b == null) {
            return null;
        }
        return b.getBounds2D();
    }
    
    protected Point2D getStartPositionOfChar(final Element element, final int charnum) {
        final TextNode textNode = this.getTextNode();
        final AttributedCharacterIterator aci = textNode.getAttributedCharacterIterator();
        if (aci == null) {
            return null;
        }
        final int firstChar = this.getElementStartIndex(element);
        if (firstChar == -1) {
            return null;
        }
        final List list = this.getTextRuns(textNode);
        final CharacterInformation info = this.getCharacterInformation(list, firstChar, charnum, aci);
        if (info == null) {
            return null;
        }
        return this.getStartPoint(info);
    }
    
    protected Point2D getStartPoint(final CharacterInformation info) {
        final GVTGlyphVector it = info.layout.getGlyphVector();
        if (!it.isGlyphVisible(info.glyphIndexStart)) {
            return null;
        }
        final Point2D b = it.getGlyphPosition(info.glyphIndexStart);
        final AffineTransform glyphTransform = it.getGlyphTransform(info.glyphIndexStart);
        final Point2D.Float result = new Point2D.Float(0.0f, 0.0f);
        if (glyphTransform != null) {
            glyphTransform.transform(result, result);
        }
        final Point2D.Float float1 = result;
        float1.x += (float)b.getX();
        final Point2D.Float float2 = result;
        float2.y += (float)b.getY();
        return result;
    }
    
    protected Point2D getEndPositionOfChar(final Element element, final int charnum) {
        final TextNode textNode = this.getTextNode();
        final AttributedCharacterIterator aci = textNode.getAttributedCharacterIterator();
        if (aci == null) {
            return null;
        }
        final int firstChar = this.getElementStartIndex(element);
        if (firstChar == -1) {
            return null;
        }
        final List list = this.getTextRuns(textNode);
        final CharacterInformation info = this.getCharacterInformation(list, firstChar, charnum, aci);
        if (info == null) {
            return null;
        }
        return this.getEndPoint(info);
    }
    
    protected Point2D getEndPoint(final CharacterInformation info) {
        final GVTGlyphVector it = info.layout.getGlyphVector();
        if (!it.isGlyphVisible(info.glyphIndexEnd)) {
            return null;
        }
        final Point2D b = it.getGlyphPosition(info.glyphIndexEnd);
        final AffineTransform glyphTransform = it.getGlyphTransform(info.glyphIndexEnd);
        final GVTGlyphMetrics metrics = it.getGlyphMetrics(info.glyphIndexEnd);
        final Point2D.Float result = new Point2D.Float(metrics.getHorizontalAdvance(), 0.0f);
        if (glyphTransform != null) {
            glyphTransform.transform(result, result);
        }
        final Point2D.Float float1 = result;
        float1.x += (float)b.getX();
        final Point2D.Float float2 = result;
        float2.y += (float)b.getY();
        return result;
    }
    
    protected float getRotationOfChar(final Element element, final int charnum) {
        final TextNode textNode = this.getTextNode();
        final AttributedCharacterIterator aci = textNode.getAttributedCharacterIterator();
        if (aci == null) {
            return 0.0f;
        }
        final int firstChar = this.getElementStartIndex(element);
        if (firstChar == -1) {
            return 0.0f;
        }
        final List list = this.getTextRuns(textNode);
        final CharacterInformation info = this.getCharacterInformation(list, firstChar, charnum, aci);
        double angle = 0.0;
        int nbGlyphs = 0;
        if (info != null) {
            final GVTGlyphVector it = info.layout.getGlyphVector();
            for (int k = info.glyphIndexStart; k <= info.glyphIndexEnd; ++k) {
                if (it.isGlyphVisible(k)) {
                    ++nbGlyphs;
                    final AffineTransform glyphTransform = it.getGlyphTransform(k);
                    if (glyphTransform != null) {
                        double glyphAngle = 0.0;
                        final double cosTheta = glyphTransform.getScaleX();
                        final double sinTheta = glyphTransform.getShearX();
                        if (cosTheta == 0.0) {
                            if (sinTheta > 0.0) {
                                glyphAngle = 3.141592653589793;
                            }
                            else {
                                glyphAngle = -3.141592653589793;
                            }
                        }
                        else {
                            glyphAngle = Math.atan(sinTheta / cosTheta);
                            if (cosTheta < 0.0) {
                                glyphAngle += 3.141592653589793;
                            }
                        }
                        glyphAngle = Math.toDegrees(-glyphAngle) % 360.0;
                        angle += glyphAngle - info.getComputedOrientationAngle();
                    }
                }
            }
        }
        if (nbGlyphs == 0) {
            return 0.0f;
        }
        return (float)(angle / nbGlyphs);
    }
    
    protected float getComputedTextLength(final Element e) {
        return this.getSubStringLength(e, 0, this.getNumberOfChars(e));
    }
    
    protected float getSubStringLength(final Element element, final int charnum, final int nchars) {
        if (nchars == 0) {
            return 0.0f;
        }
        float length = 0.0f;
        final TextNode textNode = this.getTextNode();
        final AttributedCharacterIterator aci = textNode.getAttributedCharacterIterator();
        if (aci == null) {
            return -1.0f;
        }
        final int firstChar = this.getElementStartIndex(element);
        if (firstChar == -1) {
            return -1.0f;
        }
        final List list = this.getTextRuns(textNode);
        CharacterInformation currentInfo = this.getCharacterInformation(list, firstChar, charnum, aci);
        CharacterInformation lastCharacterInRunInfo = null;
        int chIndex = currentInfo.characterIndex + 1;
        GVTGlyphVector vector = currentInfo.layout.getGlyphVector();
        float[] advs = currentInfo.layout.getGlyphAdvances();
        boolean[] glyphTrack = new boolean[advs.length];
        for (int k = charnum + 1; k < charnum + nchars; ++k) {
            if (currentInfo.layout.isOnATextPath()) {
                for (int gi = currentInfo.glyphIndexStart; gi <= currentInfo.glyphIndexEnd; ++gi) {
                    if (vector.isGlyphVisible(gi) && !glyphTrack[gi]) {
                        length += advs[gi + 1] - advs[gi];
                    }
                    glyphTrack[gi] = true;
                }
                final CharacterInformation newInfo = this.getCharacterInformation(list, firstChar, k, aci);
                if (newInfo.layout != currentInfo.layout) {
                    vector = newInfo.layout.getGlyphVector();
                    advs = newInfo.layout.getGlyphAdvances();
                    glyphTrack = new boolean[advs.length];
                    chIndex = currentInfo.characterIndex + 1;
                }
                currentInfo = newInfo;
            }
            else if (currentInfo.layout.hasCharacterIndex(chIndex)) {
                ++chIndex;
            }
            else {
                lastCharacterInRunInfo = this.getCharacterInformation(list, firstChar, k - 1, aci);
                length += this.distanceFirstLastCharacterInRun(currentInfo, lastCharacterInRunInfo);
                currentInfo = this.getCharacterInformation(list, firstChar, k, aci);
                chIndex = currentInfo.characterIndex + 1;
                vector = currentInfo.layout.getGlyphVector();
                advs = currentInfo.layout.getGlyphAdvances();
                glyphTrack = new boolean[advs.length];
                lastCharacterInRunInfo = null;
            }
        }
        if (currentInfo.layout.isOnATextPath()) {
            for (int gi2 = currentInfo.glyphIndexStart; gi2 <= currentInfo.glyphIndexEnd; ++gi2) {
                if (vector.isGlyphVisible(gi2) && !glyphTrack[gi2]) {
                    length += advs[gi2 + 1] - advs[gi2];
                }
                glyphTrack[gi2] = true;
            }
        }
        else {
            if (lastCharacterInRunInfo == null) {
                lastCharacterInRunInfo = this.getCharacterInformation(list, firstChar, charnum + nchars - 1, aci);
            }
            length += this.distanceFirstLastCharacterInRun(currentInfo, lastCharacterInRunInfo);
        }
        return length;
    }
    
    protected float distanceFirstLastCharacterInRun(final CharacterInformation first, final CharacterInformation last) {
        final float[] advs = first.layout.getGlyphAdvances();
        final int firstStart = first.glyphIndexStart;
        final int firstEnd = first.glyphIndexEnd;
        final int lastStart = last.glyphIndexStart;
        final int lastEnd = last.glyphIndexEnd;
        final int start = (firstStart < lastStart) ? firstStart : lastStart;
        final int end = (firstEnd < lastEnd) ? lastEnd : firstEnd;
        return advs[end + 1] - advs[start];
    }
    
    protected float distanceBetweenRun(final CharacterInformation last, final CharacterInformation first) {
        final CharacterInformation info = new CharacterInformation();
        info.layout = last.layout;
        info.glyphIndexEnd = last.layout.getGlyphCount() - 1;
        final Point2D startPoint = this.getEndPoint(info);
        info.layout = first.layout;
        info.glyphIndexStart = 0;
        final Point2D endPoint = this.getStartPoint(info);
        float distance;
        if (first.isVertical()) {
            distance = (float)(endPoint.getY() - startPoint.getY());
        }
        else {
            distance = (float)(endPoint.getX() - startPoint.getX());
        }
        return distance;
    }
    
    protected void selectSubString(final Element element, final int charnum, final int nchars) {
        final TextNode textNode = this.getTextNode();
        final AttributedCharacterIterator aci = textNode.getAttributedCharacterIterator();
        if (aci == null) {
            return;
        }
        final int firstChar = this.getElementStartIndex(element);
        if (firstChar == -1) {
            return;
        }
        final List list = this.getTextRuns(textNode);
        final int lastChar = this.getElementEndIndex(element);
        final CharacterInformation firstInfo = this.getCharacterInformation(list, firstChar, charnum, aci);
        final CharacterInformation lastInfo = this.getCharacterInformation(list, firstChar, charnum + nchars - 1, aci);
        final Mark firstMark = textNode.getMarkerForChar(firstInfo.characterIndex, true);
        Mark lastMark;
        if (lastInfo != null && lastInfo.characterIndex <= lastChar) {
            lastMark = textNode.getMarkerForChar(lastInfo.characterIndex, false);
        }
        else {
            lastMark = textNode.getMarkerForChar(lastChar, false);
        }
        this.ctx.getUserAgent().setTextSelection(firstMark, lastMark);
    }
    
    protected int getCharNumAtPosition(final Element e, final float x, final float y) {
        final TextNode textNode = this.getTextNode();
        final AttributedCharacterIterator aci = textNode.getAttributedCharacterIterator();
        if (aci == null) {
            return -1;
        }
        final List list = this.getTextRuns(textNode);
        TextHit hit = null;
        StrokingTextPainter.TextRun textRun;
        for (int i = list.size() - 1; i >= 0 && hit == null; hit = textRun.getLayout().hitTestChar(x, y), --i) {
            textRun = list.get(i);
        }
        if (hit == null) {
            return -1;
        }
        final int first = this.getElementStartIndex(e);
        final int last = this.getElementEndIndex(e);
        final int hitIndex = hit.getCharIndex();
        if (hitIndex >= first && hitIndex <= last) {
            return hitIndex - first;
        }
        return -1;
    }
    
    protected List getTextRuns(final TextNode node) {
        if (node.getTextRuns() == null) {
            node.getPrimitiveBounds();
        }
        return node.getTextRuns();
    }
    
    protected CharacterInformation getCharacterInformation(final List list, final int startIndex, final int charnum, final AttributedCharacterIterator aci) {
        final CharacterInformation info = new CharacterInformation();
        info.characterIndex = startIndex + charnum;
        for (final Object aList : list) {
            final StrokingTextPainter.TextRun run = (StrokingTextPainter.TextRun)aList;
            if (!run.getLayout().hasCharacterIndex(info.characterIndex)) {
                continue;
            }
            info.layout = run.getLayout();
            aci.setIndex(info.characterIndex);
            if (aci.getAttribute(SVGTextElementBridge.ALT_GLYPH_HANDLER) != null) {
                info.glyphIndexStart = 0;
                info.glyphIndexEnd = info.layout.getGlyphCount() - 1;
            }
            else {
                info.glyphIndexStart = info.layout.getGlyphIndex(info.characterIndex);
                if (info.glyphIndexStart == -1) {
                    info.glyphIndexStart = 0;
                    info.glyphIndexEnd = info.layout.getGlyphCount() - 1;
                }
                else {
                    info.glyphIndexEnd = info.glyphIndexStart;
                }
            }
            return info;
        }
        return null;
    }
    
    public Set getTextIntersectionSet(final AffineTransform at, final Rectangle2D rect) {
        final Set elems = new HashSet();
        final TextNode tn = this.getTextNode();
        final List list = tn.getTextRuns();
        if (list == null) {
            return elems;
        }
        for (final Object aList : list) {
            final StrokingTextPainter.TextRun run = (StrokingTextPainter.TextRun)aList;
            final TextSpanLayout layout = run.getLayout();
            final AttributedCharacterIterator aci = run.getACI();
            aci.first();
            final SoftReference sr = (SoftReference)aci.getAttribute(SVGTextElementBridge.TEXT_COMPOUND_ID);
            final Element elem = sr.get();
            if (elem == null) {
                continue;
            }
            if (elems.contains(elem)) {
                continue;
            }
            if (!isTextSensitive(elem)) {
                continue;
            }
            Rectangle2D glBounds = layout.getBounds2D();
            if (glBounds != null) {
                glBounds = at.createTransformedShape(glBounds).getBounds2D();
                if (!rect.intersects(glBounds)) {
                    continue;
                }
            }
            final GVTGlyphVector gv = layout.getGlyphVector();
            for (int g = 0; g < gv.getNumGlyphs(); ++g) {
                Shape gBounds = gv.getGlyphLogicalBounds(g);
                if (gBounds != null) {
                    gBounds = at.createTransformedShape(gBounds).getBounds2D();
                    if (gBounds.intersects(rect)) {
                        elems.add(elem);
                        break;
                    }
                }
            }
        }
        return elems;
    }
    
    public Set getTextEnclosureSet(final AffineTransform at, final Rectangle2D rect) {
        final TextNode tn = this.getTextNode();
        final Set elems = new HashSet();
        final List list = tn.getTextRuns();
        if (list == null) {
            return elems;
        }
        final Set reject = new HashSet();
        for (final Object aList : list) {
            final StrokingTextPainter.TextRun run = (StrokingTextPainter.TextRun)aList;
            final TextSpanLayout layout = run.getLayout();
            final AttributedCharacterIterator aci = run.getACI();
            aci.first();
            final SoftReference sr = (SoftReference)aci.getAttribute(SVGTextElementBridge.TEXT_COMPOUND_ID);
            final Element elem = sr.get();
            if (elem == null) {
                continue;
            }
            if (reject.contains(elem)) {
                continue;
            }
            if (!isTextSensitive(elem)) {
                reject.add(elem);
            }
            else {
                Rectangle2D glBounds = layout.getBounds2D();
                if (glBounds == null) {
                    continue;
                }
                glBounds = at.createTransformedShape(glBounds).getBounds2D();
                if (rect.contains(glBounds)) {
                    elems.add(elem);
                }
                else {
                    reject.add(elem);
                    elems.remove(elem);
                }
            }
        }
        return elems;
    }
    
    public static boolean getTextIntersection(final BridgeContext ctx, final Element elem, final AffineTransform ati, final Rectangle2D rect, final boolean checkSensitivity) {
        SVGContext svgCtx = null;
        if (elem instanceof SVGOMElement) {
            svgCtx = ((SVGOMElement)elem).getSVGContext();
        }
        if (svgCtx == null) {
            return false;
        }
        SVGTextElementBridge txtBridge = null;
        if (svgCtx instanceof SVGTextElementBridge) {
            txtBridge = (SVGTextElementBridge)svgCtx;
        }
        else if (svgCtx instanceof AbstractTextChildSVGContext) {
            final AbstractTextChildSVGContext childCtx = (AbstractTextChildSVGContext)svgCtx;
            txtBridge = childCtx.getTextBridge();
        }
        if (txtBridge == null) {
            return false;
        }
        final TextNode tn = txtBridge.getTextNode();
        final List list = tn.getTextRuns();
        if (list == null) {
            return false;
        }
        final Element txtElem = txtBridge.e;
        final AffineTransform at = tn.getGlobalTransform();
        at.preConcatenate(ati);
        Rectangle2D tnRect = tn.getBounds();
        tnRect = at.createTransformedShape(tnRect).getBounds2D();
        if (!rect.intersects(tnRect)) {
            return false;
        }
        for (final Object aList : list) {
            final StrokingTextPainter.TextRun run = (StrokingTextPainter.TextRun)aList;
            final TextSpanLayout layout = run.getLayout();
            final AttributedCharacterIterator aci = run.getACI();
            aci.first();
            final SoftReference sr = (SoftReference)aci.getAttribute(SVGTextElementBridge.TEXT_COMPOUND_ID);
            final Element runElem = sr.get();
            if (runElem == null) {
                continue;
            }
            if (checkSensitivity && !isTextSensitive(runElem)) {
                continue;
            }
            Element p;
            for (p = runElem; p != null && p != txtElem && p != elem; p = (Element)txtBridge.getParentNode(p)) {}
            if (p != elem) {
                continue;
            }
            Rectangle2D glBounds = layout.getBounds2D();
            if (glBounds == null) {
                continue;
            }
            glBounds = at.createTransformedShape(glBounds).getBounds2D();
            if (!rect.intersects(glBounds)) {
                continue;
            }
            final GVTGlyphVector gv = layout.getGlyphVector();
            for (int g = 0; g < gv.getNumGlyphs(); ++g) {
                Shape gBounds = gv.getGlyphLogicalBounds(g);
                if (gBounds != null) {
                    gBounds = at.createTransformedShape(gBounds).getBounds2D();
                    if (gBounds.intersects(rect)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public static Rectangle2D getTextBounds(final BridgeContext ctx, final Element elem, final boolean checkSensitivity) {
        SVGContext svgCtx = null;
        if (elem instanceof SVGOMElement) {
            svgCtx = ((SVGOMElement)elem).getSVGContext();
        }
        if (svgCtx == null) {
            return null;
        }
        SVGTextElementBridge txtBridge = null;
        if (svgCtx instanceof SVGTextElementBridge) {
            txtBridge = (SVGTextElementBridge)svgCtx;
        }
        else if (svgCtx instanceof AbstractTextChildSVGContext) {
            final AbstractTextChildSVGContext childCtx = (AbstractTextChildSVGContext)svgCtx;
            txtBridge = childCtx.getTextBridge();
        }
        if (txtBridge == null) {
            return null;
        }
        final TextNode tn = txtBridge.getTextNode();
        final List list = tn.getTextRuns();
        if (list == null) {
            return null;
        }
        final Element txtElem = txtBridge.e;
        Rectangle2D ret = null;
        for (final Object aList : list) {
            final StrokingTextPainter.TextRun run = (StrokingTextPainter.TextRun)aList;
            final TextSpanLayout layout = run.getLayout();
            final AttributedCharacterIterator aci = run.getACI();
            aci.first();
            final SoftReference sr = (SoftReference)aci.getAttribute(SVGTextElementBridge.TEXT_COMPOUND_ID);
            final Element runElem = sr.get();
            if (runElem == null) {
                continue;
            }
            if (checkSensitivity && !isTextSensitive(runElem)) {
                continue;
            }
            Element p;
            for (p = runElem; p != null && p != txtElem && p != elem; p = (Element)txtBridge.getParentNode(p)) {}
            if (p != elem) {
                continue;
            }
            final Rectangle2D glBounds = layout.getBounds2D();
            if (glBounds == null) {
                continue;
            }
            if (ret == null) {
                ret = (Rectangle2D)glBounds.clone();
            }
            else {
                ret.add(glBounds);
            }
        }
        return ret;
    }
    
    public static boolean isTextSensitive(final Element e) {
        final int ptrEvts = CSSUtilities.convertPointerEvents(e);
        switch (ptrEvts) {
            case 0:
            case 1:
            case 2:
            case 3: {
                return CSSUtilities.convertVisibility(e);
            }
            case 4:
            case 5:
            case 6:
            case 7: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    static {
        ZERO = 0;
        TEXT_COMPOUND_DELIMITER = GVTAttributedCharacterIterator.TextAttribute.TEXT_COMPOUND_DELIMITER;
        TEXT_COMPOUND_ID = GVTAttributedCharacterIterator.TextAttribute.TEXT_COMPOUND_ID;
        PAINT_INFO = GVTAttributedCharacterIterator.TextAttribute.PAINT_INFO;
        ALT_GLYPH_HANDLER = GVTAttributedCharacterIterator.TextAttribute.ALT_GLYPH_HANDLER;
        TEXTPATH = GVTAttributedCharacterIterator.TextAttribute.TEXTPATH;
        ANCHOR_TYPE = GVTAttributedCharacterIterator.TextAttribute.ANCHOR_TYPE;
        GVT_FONT_FAMILIES = GVTAttributedCharacterIterator.TextAttribute.GVT_FONT_FAMILIES;
        GVT_FONTS = GVTAttributedCharacterIterator.TextAttribute.GVT_FONTS;
        BASELINE_SHIFT = GVTAttributedCharacterIterator.TextAttribute.BASELINE_SHIFT;
    }
    
    protected class DOMChildNodeRemovedEventListener implements EventListener
    {
        @Override
        public void handleEvent(final Event evt) {
            SVGTextElementBridge.this.handleDOMChildNodeRemovedEvent((MutationEvent)evt);
        }
    }
    
    protected class DOMSubtreeModifiedEventListener implements EventListener
    {
        @Override
        public void handleEvent(final Event evt) {
            SVGTextElementBridge.this.handleDOMSubtreeModifiedEvent((MutationEvent)evt);
        }
    }
    
    protected static class AttributedStringBuffer
    {
        protected List strings;
        protected List attributes;
        protected int count;
        protected int length;
        
        public AttributedStringBuffer() {
            this.strings = new ArrayList();
            this.attributes = new ArrayList();
            this.count = 0;
            this.length = 0;
        }
        
        public boolean isEmpty() {
            return this.count == 0;
        }
        
        public int length() {
            return this.length;
        }
        
        public void append(final String s, final Map m) {
            if (s.length() == 0) {
                return;
            }
            this.strings.add(s);
            this.attributes.add(m);
            ++this.count;
            this.length += s.length();
        }
        
        public int getLastChar() {
            if (this.count == 0) {
                return -1;
            }
            final String s = this.strings.get(this.count - 1);
            return s.charAt(s.length() - 1);
        }
        
        public void stripFirst() {
            final String s = this.strings.get(0);
            if (s.charAt(s.length() - 1) != ' ') {
                return;
            }
            --this.length;
            if (s.length() == 1) {
                this.attributes.remove(0);
                this.strings.remove(0);
                --this.count;
                return;
            }
            this.strings.set(0, s.substring(1));
        }
        
        public void stripLast() {
            final String s = this.strings.get(this.count - 1);
            if (s.charAt(s.length() - 1) != ' ') {
                return;
            }
            --this.length;
            if (s.length() == 1) {
                this.attributes.remove(--this.count);
                this.strings.remove(this.count);
                return;
            }
            this.strings.set(this.count - 1, s.substring(0, s.length() - 1));
        }
        
        public AttributedString toAttributedString() {
            switch (this.count) {
                case 0: {
                    return null;
                }
                case 1: {
                    return new AttributedString(this.strings.get(0), this.attributes.get(0));
                }
                default: {
                    final StringBuffer sb = new StringBuffer(this.strings.size() * 5);
                    for (final Object string : this.strings) {
                        sb.append((String)string);
                    }
                    final AttributedString result = new AttributedString(sb.toString());
                    final Iterator sit = this.strings.iterator();
                    final Iterator ait = this.attributes.iterator();
                    int idx = 0;
                    while (sit.hasNext()) {
                        final String s = sit.next();
                        final int nidx = idx + s.length();
                        final Map m = ait.next();
                        final Iterator kit = m.keySet().iterator();
                        final Iterator vit = m.values().iterator();
                        while (kit.hasNext()) {
                            final AttributedCharacterIterator.Attribute attr = kit.next();
                            final Object val = vit.next();
                            result.addAttribute(attr, val, idx, nidx);
                        }
                        idx = nidx;
                    }
                    return result;
                }
            }
        }
        
        @Override
        public String toString() {
            switch (this.count) {
                case 0: {
                    return "";
                }
                case 1: {
                    return this.strings.get(0);
                }
                default: {
                    final StringBuffer sb = new StringBuffer(this.strings.size() * 5);
                    for (final Object string : this.strings) {
                        sb.append((String)string);
                    }
                    return sb.toString();
                }
            }
        }
    }
    
    public abstract static class AbstractTextChildSVGContext extends AnimatableSVGBridge
    {
        protected SVGTextElementBridge textBridge;
        
        public AbstractTextChildSVGContext(final BridgeContext ctx, final SVGTextElementBridge parent, final Element e) {
            this.ctx = ctx;
            this.textBridge = parent;
            this.e = e;
        }
        
        @Override
        public String getNamespaceURI() {
            return null;
        }
        
        @Override
        public String getLocalName() {
            return null;
        }
        
        @Override
        public Bridge getInstance() {
            return null;
        }
        
        public SVGTextElementBridge getTextBridge() {
            return this.textBridge;
        }
        
        @Override
        public float getPixelUnitToMillimeter() {
            return this.ctx.getUserAgent().getPixelUnitToMillimeter();
        }
        
        @Override
        public float getPixelToMM() {
            return this.getPixelUnitToMillimeter();
        }
        
        @Override
        public Rectangle2D getBBox() {
            return null;
        }
        
        @Override
        public AffineTransform getCTM() {
            return null;
        }
        
        @Override
        public AffineTransform getGlobalTransform() {
            return null;
        }
        
        @Override
        public AffineTransform getScreenTransform() {
            return null;
        }
        
        @Override
        public void setScreenTransform(final AffineTransform at) {
        }
        
        @Override
        public float getViewportWidth() {
            return this.ctx.getBlockWidth(this.e);
        }
        
        @Override
        public float getViewportHeight() {
            return this.ctx.getBlockHeight(this.e);
        }
        
        @Override
        public float getFontSize() {
            return CSSUtilities.getComputedStyle(this.e, 22).getFloatValue();
        }
    }
    
    protected abstract class AbstractTextChildBridgeUpdateHandler extends AbstractTextChildSVGContext implements BridgeUpdateHandler
    {
        protected AbstractTextChildBridgeUpdateHandler(final BridgeContext ctx, final SVGTextElementBridge parent, final Element e) {
            super(ctx, parent, e);
        }
        
        @Override
        public void handleDOMAttrModifiedEvent(final MutationEvent evt) {
        }
        
        @Override
        public void handleDOMNodeInsertedEvent(final MutationEvent evt) {
            this.textBridge.handleDOMNodeInsertedEvent(evt);
        }
        
        @Override
        public void handleDOMNodeRemovedEvent(final MutationEvent evt) {
        }
        
        @Override
        public void handleDOMCharacterDataModified(final MutationEvent evt) {
            this.textBridge.handleDOMCharacterDataModified(evt);
        }
        
        @Override
        public void handleCSSEngineEvent(final CSSEngineEvent evt) {
            this.textBridge.handleCSSEngineEvent(evt);
        }
        
        @Override
        public void handleAnimatedAttributeChanged(final AnimatedLiveAttributeValue alav) {
        }
        
        @Override
        public void handleOtherAnimationChanged(final String type) {
        }
        
        @Override
        public void dispose() {
            ((SVGOMElement)this.e).setSVGContext(null);
            SVGTextElementBridge.this.elemTPI.remove(this.e);
        }
    }
    
    protected class AbstractTextChildTextContent extends AbstractTextChildBridgeUpdateHandler implements SVGTextContent
    {
        protected AbstractTextChildTextContent(final BridgeContext ctx, final SVGTextElementBridge parent, final Element e) {
            super(ctx, parent, e);
        }
        
        @Override
        public int getNumberOfChars() {
            return this.textBridge.getNumberOfChars(this.e);
        }
        
        @Override
        public Rectangle2D getExtentOfChar(final int charnum) {
            return this.textBridge.getExtentOfChar(this.e, charnum);
        }
        
        @Override
        public Point2D getStartPositionOfChar(final int charnum) {
            return this.textBridge.getStartPositionOfChar(this.e, charnum);
        }
        
        @Override
        public Point2D getEndPositionOfChar(final int charnum) {
            return this.textBridge.getEndPositionOfChar(this.e, charnum);
        }
        
        @Override
        public void selectSubString(final int charnum, final int nchars) {
            this.textBridge.selectSubString(this.e, charnum, nchars);
        }
        
        @Override
        public float getRotationOfChar(final int charnum) {
            return this.textBridge.getRotationOfChar(this.e, charnum);
        }
        
        @Override
        public float getComputedTextLength() {
            return this.textBridge.getComputedTextLength(this.e);
        }
        
        @Override
        public float getSubStringLength(final int charnum, final int nchars) {
            return this.textBridge.getSubStringLength(this.e, charnum, nchars);
        }
        
        @Override
        public int getCharNumAtPosition(final float x, final float y) {
            return this.textBridge.getCharNumAtPosition(this.e, x, y);
        }
    }
    
    protected class TRefBridge extends AbstractTextChildTextContent
    {
        protected TRefBridge(final BridgeContext ctx, final SVGTextElementBridge parent, final Element e) {
            super(ctx, parent, e);
        }
        
        @Override
        public void handleAnimatedAttributeChanged(final AnimatedLiveAttributeValue alav) {
            if (alav.getNamespaceURI() == null) {
                final String ln = alav.getLocalName();
                if (ln.equals("x") || ln.equals("y") || ln.equals("dx") || ln.equals("dy") || ln.equals("rotate") || ln.equals("textLength") || ln.equals("lengthAdjust")) {
                    this.textBridge.computeLaidoutText(this.ctx, this.textBridge.e, this.textBridge.getTextNode());
                    return;
                }
            }
            super.handleAnimatedAttributeChanged(alav);
        }
    }
    
    protected class TextPathBridge extends AbstractTextChildTextContent
    {
        protected TextPathBridge(final BridgeContext ctx, final SVGTextElementBridge parent, final Element e) {
            super(ctx, parent, e);
        }
    }
    
    protected class TspanBridge extends AbstractTextChildTextContent
    {
        protected TspanBridge(final BridgeContext ctx, final SVGTextElementBridge parent, final Element e) {
            super(ctx, parent, e);
        }
        
        @Override
        public void handleAnimatedAttributeChanged(final AnimatedLiveAttributeValue alav) {
            if (alav.getNamespaceURI() == null) {
                final String ln = alav.getLocalName();
                if (ln.equals("x") || ln.equals("y") || ln.equals("dx") || ln.equals("dy") || ln.equals("rotate") || ln.equals("textLength") || ln.equals("lengthAdjust")) {
                    this.textBridge.computeLaidoutText(this.ctx, this.textBridge.e, this.textBridge.getTextNode());
                    return;
                }
            }
            super.handleAnimatedAttributeChanged(alav);
        }
    }
    
    protected static class CharacterInformation
    {
        TextSpanLayout layout;
        int glyphIndexStart;
        int glyphIndexEnd;
        int characterIndex;
        
        public boolean isVertical() {
            return this.layout.isVertical();
        }
        
        public double getComputedOrientationAngle() {
            return this.layout.getComputedOrientationAngle(this.characterIndex);
        }
    }
}
