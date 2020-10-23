// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge.svg12;

import org.w3c.dom.events.Event;
import org.apache.batik.css.engine.value.svg12.LineHeightValue;
import org.apache.batik.css.engine.value.ComputedValue;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.svg12.SVG12ValueConstants;
import org.apache.batik.css.engine.value.ValueConstants;
import org.apache.batik.gvt.flow.BlockInfo;
import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.TextUtilities;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.bridge.SVGAElementBridge;
import org.apache.batik.bridge.CursorManager;
import org.apache.batik.dom.events.NodeEventTarget;
import java.awt.font.TextAttribute;
import org.apache.batik.gvt.text.TextPath;
import org.apache.batik.dom.util.XMLSupport;
import java.awt.geom.AffineTransform;
import java.awt.Shape;
import org.apache.batik.gvt.flow.RegionInfo;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ArrayList;
import java.awt.Color;
import org.apache.batik.gvt.text.TextPaintInfo;
import java.util.List;
import org.apache.batik.gvt.flow.TextLineBreaks;
import java.text.AttributedString;
import org.apache.batik.dom.svg.SVGContext;
import org.apache.batik.bridge.SVGTextElementBridge;
import org.apache.batik.anim.dom.SVGOMElement;
import org.apache.batik.bridge.GVTBuilder;
import org.w3c.dom.events.EventListener;
import org.apache.batik.anim.dom.XBLEventSupport;
import org.apache.batik.dom.AbstractNode;
import org.apache.batik.anim.dom.SVGOMFlowRegionElement;
import java.util.HashMap;
import java.awt.geom.Point2D;
import java.awt.RenderingHints;
import org.w3c.dom.Node;
import org.apache.batik.bridge.FlowTextNode;
import org.apache.batik.bridge.CSSUtilities;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.bridge.SVGUtilities;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.TextNode;
import java.util.Map;
import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;
import java.text.AttributedCharacterIterator;

public class SVGFlowRootElementBridge extends SVG12TextElementBridge
{
    public static final AttributedCharacterIterator.Attribute FLOW_PARAGRAPH;
    public static final AttributedCharacterIterator.Attribute FLOW_EMPTY_PARAGRAPH;
    public static final AttributedCharacterIterator.Attribute FLOW_LINE_BREAK;
    public static final AttributedCharacterIterator.Attribute FLOW_REGIONS;
    public static final AttributedCharacterIterator.Attribute LINE_HEIGHT;
    public static final GVTAttributedCharacterIterator.TextAttribute TEXTPATH;
    public static final GVTAttributedCharacterIterator.TextAttribute ANCHOR_TYPE;
    public static final GVTAttributedCharacterIterator.TextAttribute LETTER_SPACING;
    public static final GVTAttributedCharacterIterator.TextAttribute WORD_SPACING;
    public static final GVTAttributedCharacterIterator.TextAttribute KERNING;
    protected Map flowRegionNodes;
    protected TextNode textNode;
    protected RegionChangeListener regionChangeListener;
    protected int startLen;
    int marginTopIndex;
    int marginRightIndex;
    int marginBottomIndex;
    int marginLeftIndex;
    int indentIndex;
    int textAlignIndex;
    int lineHeightIndex;
    
    @Override
    protected TextNode getTextNode() {
        return this.textNode;
    }
    
    public SVGFlowRootElementBridge() {
        this.marginTopIndex = -1;
        this.marginRightIndex = -1;
        this.marginBottomIndex = -1;
        this.marginLeftIndex = -1;
        this.indentIndex = -1;
        this.textAlignIndex = -1;
        this.lineHeightIndex = -1;
    }
    
    @Override
    public String getNamespaceURI() {
        return "http://www.w3.org/2000/svg";
    }
    
    @Override
    public String getLocalName() {
        return "flowRoot";
    }
    
    @Override
    public Bridge getInstance() {
        return new SVGFlowRootElementBridge();
    }
    
    @Override
    public boolean isComposite() {
        return false;
    }
    
    @Override
    public GraphicsNode createGraphicsNode(final BridgeContext ctx, final Element e) {
        if (!SVGUtilities.matchUserAgent(e, ctx.getUserAgent())) {
            return null;
        }
        final CompositeGraphicsNode cgn = new CompositeGraphicsNode();
        final String s = e.getAttributeNS(null, "transform");
        if (s.length() != 0) {
            cgn.setTransform(SVGUtilities.convertTransform(e, "transform", s, ctx));
        }
        cgn.setVisible(CSSUtilities.convertVisibility(e));
        RenderingHints hints = null;
        hints = CSSUtilities.convertColorRendering(e, hints);
        hints = CSSUtilities.convertTextRendering(e, hints);
        if (hints != null) {
            cgn.setRenderingHints(hints);
        }
        final CompositeGraphicsNode cgn2 = new CompositeGraphicsNode();
        cgn.add(cgn2);
        final FlowTextNode tn = (FlowTextNode)this.instantiateGraphicsNode();
        tn.setLocation(this.getLocation(ctx, e));
        if (ctx.getTextPainter() != null) {
            tn.setTextPainter(ctx.getTextPainter());
        }
        cgn.add(this.textNode = tn);
        this.associateSVGContext(ctx, e, cgn);
        for (Node child = this.getFirstChild(e); child != null; child = this.getNextSibling(child)) {
            if (child.getNodeType() == 1) {
                this.addContextToChild(ctx, (Element)child);
            }
        }
        return cgn;
    }
    
    @Override
    protected GraphicsNode instantiateGraphicsNode() {
        return new FlowTextNode();
    }
    
    @Override
    protected Point2D getLocation(final BridgeContext ctx, final Element e) {
        return new Point2D.Float(0.0f, 0.0f);
    }
    
    @Override
    protected boolean isTextElement(final Element e) {
        if (!"http://www.w3.org/2000/svg".equals(e.getNamespaceURI())) {
            return false;
        }
        final String nodeName = e.getLocalName();
        return nodeName.equals("flowDiv") || nodeName.equals("flowLine") || nodeName.equals("flowPara") || nodeName.equals("flowRegionBreak") || nodeName.equals("flowSpan");
    }
    
    @Override
    protected boolean isTextChild(final Element e) {
        if (!"http://www.w3.org/2000/svg".equals(e.getNamespaceURI())) {
            return false;
        }
        final String nodeName = e.getLocalName();
        return nodeName.equals("a") || nodeName.equals("flowLine") || nodeName.equals("flowPara") || nodeName.equals("flowRegionBreak") || nodeName.equals("flowSpan");
    }
    
    @Override
    public void buildGraphicsNode(final BridgeContext ctx, final Element e, final GraphicsNode node) {
        final CompositeGraphicsNode cgn = (CompositeGraphicsNode)node;
        final boolean isStatic = !ctx.isDynamic();
        if (isStatic) {
            this.flowRegionNodes = new HashMap();
        }
        else {
            this.regionChangeListener = new RegionChangeListener();
        }
        final CompositeGraphicsNode cgn2 = (CompositeGraphicsNode)cgn.get(0);
        final GVTBuilder builder = ctx.getGVTBuilder();
        for (Node n = this.getFirstChild(e); n != null; n = this.getNextSibling(n)) {
            if (n instanceof SVGOMFlowRegionElement) {
                for (Node m = this.getFirstChild(n); m != null; m = this.getNextSibling(m)) {
                    if (m.getNodeType() == 1) {
                        final GraphicsNode gn = builder.build(ctx, (Element)m);
                        if (gn != null) {
                            cgn2.add(gn);
                            if (isStatic) {
                                this.flowRegionNodes.put(m, gn);
                            }
                        }
                    }
                }
                if (!isStatic) {
                    final AbstractNode an = (AbstractNode)n;
                    final XBLEventSupport es = (XBLEventSupport)an.initializeEventSupport();
                    es.addImplementationEventListenerNS("http://www.w3.org/2000/svg", "shapechange", this.regionChangeListener, false);
                }
            }
        }
        final GraphicsNode tn = (GraphicsNode)cgn.get(1);
        super.buildGraphicsNode(ctx, e, tn);
        this.flowRegionNodes = null;
    }
    
    @Override
    protected void computeLaidoutText(final BridgeContext ctx, final Element e, final GraphicsNode node) {
        super.computeLaidoutText(ctx, this.getFlowDivElement(e), node);
    }
    
    @Override
    protected void addContextToChild(final BridgeContext ctx, final Element e) {
        if ("http://www.w3.org/2000/svg".equals(e.getNamespaceURI())) {
            final String ln = e.getLocalName();
            if (ln.equals("flowDiv") || ln.equals("flowLine") || ln.equals("flowPara") || ln.equals("flowSpan")) {
                ((SVGOMElement)e).setSVGContext(new FlowContentBridge(ctx, this, e));
            }
        }
        for (Node child = this.getFirstChild(e); child != null; child = this.getNextSibling(child)) {
            if (child.getNodeType() == 1) {
                this.addContextToChild(ctx, (Element)child);
            }
        }
    }
    
    @Override
    protected void removeContextFromChild(final BridgeContext ctx, final Element e) {
        if ("http://www.w3.org/2000/svg".equals(e.getNamespaceURI())) {
            final String ln = e.getLocalName();
            if (ln.equals("flowDiv") || ln.equals("flowLine") || ln.equals("flowPara") || ln.equals("flowSpan")) {
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
    protected AttributedString buildAttributedString(final BridgeContext ctx, final Element element) {
        if (element == null) {
            return null;
        }
        final List rgns = this.getRegions(ctx, element);
        final AttributedString ret = this.getFlowDiv(ctx, element);
        if (ret == null) {
            return ret;
        }
        ret.addAttribute(SVGFlowRootElementBridge.FLOW_REGIONS, rgns, 0, 1);
        TextLineBreaks.findLineBrk(ret);
        return ret;
    }
    
    protected void dumpACIWord(final AttributedString as) {
        if (as == null) {
            return;
        }
        final StringBuffer chars = new StringBuffer();
        final StringBuffer brkStr = new StringBuffer();
        final AttributedCharacterIterator aci = as.getIterator();
        final AttributedCharacterIterator.Attribute WORD_LIMIT = TextLineBreaks.WORD_LIMIT;
        for (char ch = aci.current(); ch != '\uffff'; ch = aci.next()) {
            chars.append(ch).append(' ').append(' ');
            final int w = (int)aci.getAttribute(WORD_LIMIT);
            brkStr.append(w).append(' ');
            if (w < 10) {
                brkStr.append(' ');
            }
        }
        System.out.println(chars.toString());
        System.out.println(brkStr.toString());
    }
    
    protected Element getFlowDivElement(final Element elem) {
        final String eNS = elem.getNamespaceURI();
        if (!eNS.equals("http://www.w3.org/2000/svg")) {
            return null;
        }
        final String nodeName = elem.getLocalName();
        if (nodeName.equals("flowDiv")) {
            return elem;
        }
        if (!nodeName.equals("flowRoot")) {
            return null;
        }
        for (Node n = this.getFirstChild(elem); n != null; n = this.getNextSibling(n)) {
            if (n.getNodeType() == 1) {
                final String nNS = n.getNamespaceURI();
                if ("http://www.w3.org/2000/svg".equals(nNS)) {
                    final Element e = (Element)n;
                    final String ln = e.getLocalName();
                    if (ln.equals("flowDiv")) {
                        return e;
                    }
                }
            }
        }
        return null;
    }
    
    protected AttributedString getFlowDiv(final BridgeContext ctx, final Element element) {
        final Element flowDiv = this.getFlowDivElement(element);
        if (flowDiv == null) {
            return null;
        }
        return this.gatherFlowPara(ctx, flowDiv);
    }
    
    protected AttributedString gatherFlowPara(final BridgeContext ctx, final Element div) {
        final TextPaintInfo divTPI = new TextPaintInfo();
        divTPI.visible = true;
        divTPI.fillPaint = Color.black;
        this.elemTPI.put(div, divTPI);
        final AttributedStringBuffer asb = new AttributedStringBuffer();
        final List paraEnds = new ArrayList();
        final List paraElems = new ArrayList();
        final List lnLocs = new ArrayList();
        for (Node n = this.getFirstChild(div); n != null; n = this.getNextSibling(n)) {
            if (n.getNodeType() == 1) {
                if (this.getNamespaceURI().equals(n.getNamespaceURI())) {
                    final Element e = (Element)n;
                    final String ln = e.getLocalName();
                    if (ln.equals("flowPara")) {
                        this.fillAttributedStringBuffer(ctx, e, true, null, null, asb, lnLocs);
                        paraElems.add(e);
                        paraEnds.add(asb.length());
                    }
                    else if (ln.equals("flowRegionBreak")) {
                        this.fillAttributedStringBuffer(ctx, e, true, null, null, asb, lnLocs);
                        paraElems.add(e);
                        paraEnds.add(asb.length());
                    }
                }
            }
        }
        divTPI.startChar = 0;
        divTPI.endChar = asb.length() - 1;
        final AttributedString ret = asb.toAttributedString();
        if (ret == null) {
            return null;
        }
        int prevLN = 0;
        for (final Object lnLoc : lnLocs) {
            final int nextLN = (int)lnLoc;
            if (nextLN == prevLN) {
                continue;
            }
            ret.addAttribute(SVGFlowRootElementBridge.FLOW_LINE_BREAK, new Object(), prevLN, nextLN);
            prevLN = nextLN;
        }
        int start = 0;
        List emptyPara = null;
        int end;
        for (int i = 0; i < paraElems.size(); ++i, start = end) {
            final Element elem = paraElems.get(i);
            end = paraEnds.get(i);
            if (start == end) {
                if (emptyPara == null) {
                    emptyPara = new LinkedList();
                }
                emptyPara.add(this.makeBlockInfo(ctx, elem));
            }
            else {
                ret.addAttribute(SVGFlowRootElementBridge.FLOW_PARAGRAPH, this.makeBlockInfo(ctx, elem), start, end);
                if (emptyPara != null) {
                    ret.addAttribute(SVGFlowRootElementBridge.FLOW_EMPTY_PARAGRAPH, emptyPara, start, end);
                    emptyPara = null;
                }
            }
        }
        return ret;
    }
    
    protected List getRegions(final BridgeContext ctx, Element element) {
        element = (Element)element.getParentNode();
        final List ret = new LinkedList();
        for (Node n = this.getFirstChild(element); n != null; n = this.getNextSibling(n)) {
            if (n.getNodeType() == 1) {
                if ("http://www.w3.org/2000/svg".equals(n.getNamespaceURI())) {
                    final Element e = (Element)n;
                    final String ln = e.getLocalName();
                    if ("flowRegion".equals(ln)) {
                        final float verticalAlignment = 0.0f;
                        this.gatherRegionInfo(ctx, e, verticalAlignment, ret);
                    }
                }
            }
        }
        return ret;
    }
    
    protected void gatherRegionInfo(final BridgeContext ctx, final Element rgn, final float verticalAlign, final List regions) {
        final boolean isStatic = !ctx.isDynamic();
        for (Node n = this.getFirstChild(rgn); n != null; n = this.getNextSibling(n)) {
            if (n.getNodeType() == 1) {
                final GraphicsNode gn = isStatic ? this.flowRegionNodes.get(n) : ctx.getGraphicsNode(n);
                Shape s = gn.getOutline();
                if (s != null) {
                    final AffineTransform at = gn.getTransform();
                    if (at != null) {
                        s = at.createTransformedShape(s);
                    }
                    regions.add(new RegionInfo(s, verticalAlign));
                }
            }
        }
    }
    
    protected void fillAttributedStringBuffer(final BridgeContext ctx, final Element element, final boolean top, final Integer bidiLevel, Map initialAttributes, final AttributedStringBuffer asb, final List lnLocs) {
        if (!SVGUtilities.matchUserAgent(element, ctx.getUserAgent()) || !CSSUtilities.convertDisplay(element)) {
            return;
        }
        String s = XMLSupport.getXMLSpace(element);
        final boolean preserve = s.equals("preserve");
        Element nodeElement = element;
        final int elementStartChar = asb.length();
        if (top) {
            final int length = asb.length();
            this.startLen = length;
            this.endLimit = length;
        }
        if (preserve) {
            this.endLimit = this.startLen;
        }
        final Map map = (initialAttributes == null) ? new HashMap() : new HashMap(initialAttributes);
        initialAttributes = (Map<?, ?>)this.getAttributeMap(ctx, element, null, bidiLevel, map);
        final Object o = map.get(TextAttribute.BIDI_EMBEDDING);
        Integer subBidiLevel = bidiLevel;
        if (o != null) {
            subBidiLevel = (Integer)o;
        }
        int lineBreak = -1;
        if (lnLocs.size() != 0) {
            lineBreak = lnLocs.get(lnLocs.size() - 1);
        }
        for (Node n = this.getFirstChild(element); n != null; n = this.getNextSibling(n)) {
            boolean prevEndsWithSpace;
            if (preserve) {
                prevEndsWithSpace = false;
            }
            else {
                final int len = asb.length();
                if (len == this.startLen) {
                    prevEndsWithSpace = true;
                }
                else {
                    prevEndsWithSpace = (asb.getLastChar() == 32);
                    final int idx = lnLocs.size() - 1;
                    if (!prevEndsWithSpace && idx >= 0) {
                        final Integer i = lnLocs.get(idx);
                        if (i == len) {
                            prevEndsWithSpace = true;
                        }
                    }
                }
            }
            switch (n.getNodeType()) {
                case 1: {
                    if (!"http://www.w3.org/2000/svg".equals(n.getNamespaceURI())) {
                        break;
                    }
                    nodeElement = (Element)n;
                    final String ln = n.getLocalName();
                    if (ln.equals("flowLine")) {
                        final int before = asb.length();
                        this.fillAttributedStringBuffer(ctx, nodeElement, false, subBidiLevel, initialAttributes, asb, lnLocs);
                        lineBreak = asb.length();
                        lnLocs.add(lineBreak);
                        if (before != lineBreak) {
                            initialAttributes = null;
                        }
                        break;
                    }
                    if (ln.equals("flowSpan") || ln.equals("altGlyph")) {
                        final int before = asb.length();
                        this.fillAttributedStringBuffer(ctx, nodeElement, false, subBidiLevel, initialAttributes, asb, lnLocs);
                        if (asb.length() != before) {
                            initialAttributes = null;
                        }
                        break;
                    }
                    if (ln.equals("a")) {
                        if (ctx.isInteractive()) {
                            final NodeEventTarget target = (NodeEventTarget)nodeElement;
                            final UserAgent ua = ctx.getUserAgent();
                            final SVGAElementBridge.CursorHolder ch = new SVGAElementBridge.CursorHolder(CursorManager.DEFAULT_CURSOR);
                            target.addEventListenerNS("http://www.w3.org/2001/xml-events", "click", new SVGAElementBridge.AnchorListener(ua, ch), false, null);
                            target.addEventListenerNS("http://www.w3.org/2001/xml-events", "mouseover", new SVGAElementBridge.CursorMouseOverListener(ua, ch), false, null);
                            target.addEventListenerNS("http://www.w3.org/2001/xml-events", "mouseout", new SVGAElementBridge.CursorMouseOutListener(ua, ch), false, null);
                        }
                        final int before = asb.length();
                        this.fillAttributedStringBuffer(ctx, nodeElement, false, subBidiLevel, initialAttributes, asb, lnLocs);
                        if (asb.length() != before) {
                            initialAttributes = null;
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
                            final Map m = new HashMap();
                            this.getAttributeMap(ctx, nodeElement, null, bidiLevel, m);
                            asb.append(s, m);
                            final int trefEnd = asb.length() - 1;
                            final TextPaintInfo tpi = this.elemTPI.get(nodeElement);
                            tpi.startChar = trefStart;
                            tpi.endChar = trefEnd;
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
                int idx2 = lnLocs.size() - 1;
                final int len2 = asb.length();
                if (idx2 >= 0) {
                    Integer i = lnLocs.get(idx2);
                    if (i >= len2) {
                        i = len2 - 1;
                        lnLocs.set(idx2, i);
                        --idx2;
                        while (idx2 >= 0) {
                            i = lnLocs.get(idx2);
                            if (i < len2 - 1) {
                                break;
                            }
                            lnLocs.remove(idx2);
                            --idx2;
                        }
                    }
                }
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
    
    @Override
    protected Map getAttributeMap(final BridgeContext ctx, final Element element, final TextPath textPath, final Integer bidiLevel, final Map result) {
        final Map inheritingMap = super.getAttributeMap(ctx, element, textPath, bidiLevel, result);
        final float fontSize = TextUtilities.convertFontSize(element);
        final float lineHeight = this.getLineHeight(ctx, element, fontSize);
        result.put(SVGFlowRootElementBridge.LINE_HEIGHT, lineHeight);
        return inheritingMap;
    }
    
    protected void checkMap(final Map attrs) {
        if (attrs.containsKey(SVGFlowRootElementBridge.TEXTPATH)) {
            return;
        }
        if (attrs.containsKey(SVGFlowRootElementBridge.ANCHOR_TYPE)) {
            return;
        }
        if (attrs.containsKey(SVGFlowRootElementBridge.LETTER_SPACING)) {
            return;
        }
        if (attrs.containsKey(SVGFlowRootElementBridge.WORD_SPACING)) {
            return;
        }
        if (attrs.containsKey(SVGFlowRootElementBridge.KERNING)) {
            return;
        }
    }
    
    protected void initCSSPropertyIndexes(final Element e) {
        final CSSEngine eng = CSSUtilities.getCSSEngine(e);
        this.marginTopIndex = eng.getPropertyIndex("margin-top");
        this.marginRightIndex = eng.getPropertyIndex("margin-right");
        this.marginBottomIndex = eng.getPropertyIndex("margin-bottom");
        this.marginLeftIndex = eng.getPropertyIndex("margin-left");
        this.indentIndex = eng.getPropertyIndex("indent");
        this.textAlignIndex = eng.getPropertyIndex("text-align");
        this.lineHeightIndex = eng.getPropertyIndex("line-height");
    }
    
    public BlockInfo makeBlockInfo(final BridgeContext ctx, final Element element) {
        if (this.marginTopIndex == -1) {
            this.initCSSPropertyIndexes(element);
        }
        Value v = CSSUtilities.getComputedStyle(element, this.marginTopIndex);
        final float top = v.getFloatValue();
        v = CSSUtilities.getComputedStyle(element, this.marginRightIndex);
        final float right = v.getFloatValue();
        v = CSSUtilities.getComputedStyle(element, this.marginBottomIndex);
        final float bottom = v.getFloatValue();
        v = CSSUtilities.getComputedStyle(element, this.marginLeftIndex);
        final float left = v.getFloatValue();
        v = CSSUtilities.getComputedStyle(element, this.indentIndex);
        final float indent = v.getFloatValue();
        v = CSSUtilities.getComputedStyle(element, this.textAlignIndex);
        if (v == ValueConstants.INHERIT_VALUE) {
            v = CSSUtilities.getComputedStyle(element, 11);
            if (v == ValueConstants.LTR_VALUE) {
                v = SVG12ValueConstants.START_VALUE;
            }
            else {
                v = SVG12ValueConstants.END_VALUE;
            }
        }
        int textAlign;
        if (v == SVG12ValueConstants.START_VALUE) {
            textAlign = 0;
        }
        else if (v == SVG12ValueConstants.MIDDLE_VALUE) {
            textAlign = 1;
        }
        else if (v == SVG12ValueConstants.END_VALUE) {
            textAlign = 2;
        }
        else {
            textAlign = 3;
        }
        final Map fontAttrs = new HashMap(20);
        final List fontList = this.getFontList(ctx, element, fontAttrs);
        final Float fs = fontAttrs.get(TextAttribute.SIZE);
        final float fontSize = fs;
        final float lineHeight = this.getLineHeight(ctx, element, fontSize);
        final String ln = element.getLocalName();
        final boolean rgnBr = ln.equals("flowRegionBreak");
        return new BlockInfo(top, right, bottom, left, indent, textAlign, lineHeight, fontList, fontAttrs, rgnBr);
    }
    
    protected float getLineHeight(final BridgeContext ctx, final Element element, final float fontSize) {
        if (this.lineHeightIndex == -1) {
            this.initCSSPropertyIndexes(element);
        }
        Value v = CSSUtilities.getComputedStyle(element, this.lineHeightIndex);
        if (v == ValueConstants.INHERIT_VALUE || v == SVG12ValueConstants.NORMAL_VALUE) {
            return fontSize * 1.1f;
        }
        float lineHeight = v.getFloatValue();
        if (v instanceof ComputedValue) {
            v = ((ComputedValue)v).getComputedValue();
        }
        if (v instanceof LineHeightValue && ((LineHeightValue)v).getFontSizeRelative()) {
            lineHeight *= fontSize;
        }
        return lineHeight;
    }
    
    static {
        FLOW_PARAGRAPH = GVTAttributedCharacterIterator.TextAttribute.FLOW_PARAGRAPH;
        FLOW_EMPTY_PARAGRAPH = GVTAttributedCharacterIterator.TextAttribute.FLOW_EMPTY_PARAGRAPH;
        FLOW_LINE_BREAK = GVTAttributedCharacterIterator.TextAttribute.FLOW_LINE_BREAK;
        FLOW_REGIONS = GVTAttributedCharacterIterator.TextAttribute.FLOW_REGIONS;
        LINE_HEIGHT = GVTAttributedCharacterIterator.TextAttribute.LINE_HEIGHT;
        TEXTPATH = GVTAttributedCharacterIterator.TextAttribute.TEXTPATH;
        ANCHOR_TYPE = GVTAttributedCharacterIterator.TextAttribute.ANCHOR_TYPE;
        LETTER_SPACING = GVTAttributedCharacterIterator.TextAttribute.LETTER_SPACING;
        WORD_SPACING = GVTAttributedCharacterIterator.TextAttribute.WORD_SPACING;
        KERNING = GVTAttributedCharacterIterator.TextAttribute.KERNING;
    }
    
    protected class FlowContentBridge extends AbstractTextChildTextContent
    {
        public FlowContentBridge(final BridgeContext ctx, final SVGTextElementBridge parent, final Element e) {
            super(ctx, parent, e);
        }
    }
    
    protected class RegionChangeListener implements EventListener
    {
        @Override
        public void handleEvent(final Event evt) {
            SVGFlowRootElementBridge.this.laidoutText = null;
            SVGFlowRootElementBridge.this.computeLaidoutText(SVGFlowRootElementBridge.this.ctx, SVGFlowRootElementBridge.this.e, SVGFlowRootElementBridge.this.getTextNode());
        }
    }
}
