// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.apache.batik.parser.ClockParser;
import org.apache.batik.parser.ClockHandler;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.AWTTransformProducer;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import org.apache.batik.gvt.GraphicsNode;
import java.awt.geom.Point2D;
import org.apache.batik.parser.UnitProcessor;
import java.util.Iterator;
import java.util.List;
import java.io.IOException;
import org.w3c.dom.svg.SVGDocument;
import org.apache.batik.util.ParsedURL;
import org.apache.batik.dom.util.XLinkSupport;
import java.util.LinkedList;
import java.util.StringTokenizer;
import org.apache.batik.dom.util.XMLSupport;
import org.w3c.dom.svg.SVGLangSpace;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGNumberList;
import org.w3c.dom.Node;
import org.apache.batik.css.engine.CSSEngine;
import org.w3c.dom.Element;
import org.apache.batik.util.SVGConstants;

public abstract class SVGUtilities implements SVGConstants, ErrorConstants
{
    public static final short USER_SPACE_ON_USE = 1;
    public static final short OBJECT_BOUNDING_BOX = 2;
    public static final short STROKE_WIDTH = 3;
    
    protected SVGUtilities() {
    }
    
    public static Element getParentElement(final Element elt) {
        Node n;
        for (n = CSSEngine.getCSSParentNode(elt); n != null && n.getNodeType() != 1; n = CSSEngine.getCSSParentNode(n)) {}
        return (Element)n;
    }
    
    public static float[] convertSVGNumberList(final SVGNumberList l) {
        final int n = l.getNumberOfItems();
        if (n == 0) {
            return null;
        }
        final float[] fl = new float[n];
        for (int i = 0; i < n; ++i) {
            fl[i] = l.getItem(i).getValue();
        }
        return fl;
    }
    
    public static float convertSVGNumber(final String s) {
        return Float.parseFloat(s);
    }
    
    public static int convertSVGInteger(final String s) {
        return Integer.parseInt(s);
    }
    
    public static float convertRatio(String v) {
        float d = 1.0f;
        if (v.endsWith("%")) {
            v = v.substring(0, v.length() - 1);
            d = 100.0f;
        }
        float r = Float.parseFloat(v) / d;
        if (r < 0.0f) {
            r = 0.0f;
        }
        else if (r > 1.0f) {
            r = 1.0f;
        }
        return r;
    }
    
    public static String getDescription(final SVGElement elt) {
        String result = "";
        boolean preserve = false;
        Node n = elt.getFirstChild();
        if (n != null && n.getNodeType() == 1) {
            final String name = (n.getPrefix() == null) ? n.getNodeName() : n.getLocalName();
            if (name.equals("desc")) {
                preserve = ((SVGLangSpace)n).getXMLspace().equals("preserve");
                for (n = n.getFirstChild(); n != null; n = n.getNextSibling()) {
                    if (n.getNodeType() == 3) {
                        result += n.getNodeValue();
                    }
                }
            }
        }
        return preserve ? XMLSupport.preserveXMLSpace(result) : XMLSupport.defaultXMLSpace(result);
    }
    
    public static boolean matchUserAgent(final Element elt, final UserAgent ua) {
        Label_0077: {
            if (elt.hasAttributeNS(null, "systemLanguage")) {
                final String sl = elt.getAttributeNS(null, "systemLanguage");
                if (sl.length() == 0) {
                    return false;
                }
                final StringTokenizer st = new StringTokenizer(sl, ", ");
                while (st.hasMoreTokens()) {
                    final String s = st.nextToken();
                    if (matchUserLanguage(s, ua.getLanguages())) {
                        break Label_0077;
                    }
                }
                return false;
            }
        }
        if (elt.hasAttributeNS(null, "requiredFeatures")) {
            final String rf = elt.getAttributeNS(null, "requiredFeatures");
            if (rf.length() == 0) {
                return false;
            }
            final StringTokenizer st = new StringTokenizer(rf, " ");
            while (st.hasMoreTokens()) {
                final String s = st.nextToken();
                if (!ua.hasFeature(s)) {
                    return false;
                }
            }
        }
        if (elt.hasAttributeNS(null, "requiredExtensions")) {
            final String re = elt.getAttributeNS(null, "requiredExtensions");
            if (re.length() == 0) {
                return false;
            }
            final StringTokenizer st = new StringTokenizer(re, " ");
            while (st.hasMoreTokens()) {
                final String s = st.nextToken();
                if (!ua.supportExtension(s)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    protected static boolean matchUserLanguage(final String s, final String userLanguages) {
        final StringTokenizer st = new StringTokenizer(userLanguages, ", ");
        while (st.hasMoreTokens()) {
            final String t = st.nextToken();
            if (s.startsWith(t)) {
                return s.length() <= t.length() || s.charAt(t.length()) == '-';
            }
        }
        return false;
    }
    
    public static String getChainableAttributeNS(final Element element, final String namespaceURI, final String attrName, final BridgeContext ctx) {
        final DocumentLoader loader = ctx.getDocumentLoader();
        Element e = element;
        final List refs = new LinkedList();
        while (true) {
            final String v = e.getAttributeNS(namespaceURI, attrName);
            if (v.length() > 0) {
                return v;
            }
            final String uriStr = XLinkSupport.getXLinkHref(e);
            if (uriStr.length() == 0) {
                return "";
            }
            final String baseURI = e.getBaseURI();
            final ParsedURL purl = new ParsedURL(baseURI, uriStr);
            for (final Object ref : refs) {
                if (purl.equals(ref)) {
                    throw new BridgeException(ctx, e, "xlink.href.circularDependencies", new Object[] { uriStr });
                }
            }
            try {
                final SVGDocument svgDoc = (SVGDocument)e.getOwnerDocument();
                final URIResolver resolver = ctx.createURIResolver(svgDoc, loader);
                e = resolver.getElement(purl.toString(), e);
                refs.add(purl);
            }
            catch (IOException ioEx) {
                throw new BridgeException(ctx, e, ioEx, "uri.io", new Object[] { uriStr });
            }
            catch (SecurityException secEx) {
                throw new BridgeException(ctx, e, secEx, "uri.unsecure", new Object[] { uriStr });
            }
        }
    }
    
    public static Point2D convertPoint(final String xStr, final String xAttr, final String yStr, final String yAttr, final short unitsType, final UnitProcessor.Context uctx) {
        float x = 0.0f;
        float y = 0.0f;
        switch (unitsType) {
            case 2: {
                x = org.apache.batik.bridge.UnitProcessor.svgHorizontalCoordinateToObjectBoundingBox(xStr, xAttr, uctx);
                y = org.apache.batik.bridge.UnitProcessor.svgVerticalCoordinateToObjectBoundingBox(yStr, yAttr, uctx);
                break;
            }
            case 1: {
                x = org.apache.batik.bridge.UnitProcessor.svgHorizontalCoordinateToUserSpace(xStr, xAttr, uctx);
                y = org.apache.batik.bridge.UnitProcessor.svgVerticalCoordinateToUserSpace(yStr, yAttr, uctx);
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid unit type");
            }
        }
        return new Point2D.Float(x, y);
    }
    
    public static float convertLength(final String length, final String attr, final short unitsType, final UnitProcessor.Context uctx) {
        switch (unitsType) {
            case 2: {
                return org.apache.batik.bridge.UnitProcessor.svgOtherLengthToObjectBoundingBox(length, attr, uctx);
            }
            case 1: {
                return org.apache.batik.bridge.UnitProcessor.svgOtherLengthToUserSpace(length, attr, uctx);
            }
            default: {
                throw new IllegalArgumentException("Invalid unit type");
            }
        }
    }
    
    public static Rectangle2D convertMaskRegion(final Element maskElement, final Element maskedElement, final GraphicsNode maskedNode, final BridgeContext ctx) {
        String xStr = maskElement.getAttributeNS(null, "x");
        if (xStr.length() == 0) {
            xStr = "-10%";
        }
        String yStr = maskElement.getAttributeNS(null, "y");
        if (yStr.length() == 0) {
            yStr = "-10%";
        }
        String wStr = maskElement.getAttributeNS(null, "width");
        if (wStr.length() == 0) {
            wStr = "120%";
        }
        String hStr = maskElement.getAttributeNS(null, "height");
        if (hStr.length() == 0) {
            hStr = "120%";
        }
        final String units = maskElement.getAttributeNS(null, "maskUnits");
        short unitsType;
        if (units.length() == 0) {
            unitsType = 2;
        }
        else {
            unitsType = parseCoordinateSystem(maskElement, "maskUnits", units, ctx);
        }
        final UnitProcessor.Context uctx = org.apache.batik.bridge.UnitProcessor.createContext(ctx, maskedElement);
        return convertRegion(xStr, yStr, wStr, hStr, unitsType, maskedNode, uctx);
    }
    
    public static Rectangle2D convertPatternRegion(final Element patternElement, final Element paintedElement, final GraphicsNode paintedNode, final BridgeContext ctx) {
        String xStr = getChainableAttributeNS(patternElement, null, "x", ctx);
        if (xStr.length() == 0) {
            xStr = "0";
        }
        String yStr = getChainableAttributeNS(patternElement, null, "y", ctx);
        if (yStr.length() == 0) {
            yStr = "0";
        }
        final String wStr = getChainableAttributeNS(patternElement, null, "width", ctx);
        if (wStr.length() == 0) {
            throw new BridgeException(ctx, patternElement, "attribute.missing", new Object[] { "width" });
        }
        final String hStr = getChainableAttributeNS(patternElement, null, "height", ctx);
        if (hStr.length() == 0) {
            throw new BridgeException(ctx, patternElement, "attribute.missing", new Object[] { "height" });
        }
        final String units = getChainableAttributeNS(patternElement, null, "patternUnits", ctx);
        short unitsType;
        if (units.length() == 0) {
            unitsType = 2;
        }
        else {
            unitsType = parseCoordinateSystem(patternElement, "patternUnits", units, ctx);
        }
        final UnitProcessor.Context uctx = org.apache.batik.bridge.UnitProcessor.createContext(ctx, paintedElement);
        return convertRegion(xStr, yStr, wStr, hStr, unitsType, paintedNode, uctx);
    }
    
    public static float[] convertFilterRes(final Element filterElement, final BridgeContext ctx) {
        final float[] filterRes = new float[2];
        final String s = getChainableAttributeNS(filterElement, null, "filterRes", ctx);
        final Float[] vals = convertSVGNumberOptionalNumber(filterElement, "filterRes", s, ctx);
        if (filterRes[0] < 0.0f || filterRes[1] < 0.0f) {
            throw new BridgeException(ctx, filterElement, "attribute.malformed", new Object[] { "filterRes", s });
        }
        if (vals[0] == null) {
            filterRes[0] = -1.0f;
        }
        else {
            filterRes[0] = vals[0];
            if (filterRes[0] < 0.0f) {
                throw new BridgeException(ctx, filterElement, "attribute.malformed", new Object[] { "filterRes", s });
            }
        }
        if (vals[1] == null) {
            filterRes[1] = filterRes[0];
        }
        else {
            filterRes[1] = vals[1];
            if (filterRes[1] < 0.0f) {
                throw new BridgeException(ctx, filterElement, "attribute.malformed", new Object[] { "filterRes", s });
            }
        }
        return filterRes;
    }
    
    public static Float[] convertSVGNumberOptionalNumber(final Element elem, final String attrName, final String attrValue, final BridgeContext ctx) {
        final Float[] ret = new Float[2];
        if (attrValue.length() == 0) {
            return ret;
        }
        try {
            final StringTokenizer tokens = new StringTokenizer(attrValue, " ");
            ret[0] = Float.parseFloat(tokens.nextToken());
            if (tokens.hasMoreTokens()) {
                ret[1] = Float.parseFloat(tokens.nextToken());
            }
            if (tokens.hasMoreTokens()) {
                throw new BridgeException(ctx, elem, "attribute.malformed", new Object[] { attrName, attrValue });
            }
        }
        catch (NumberFormatException nfEx) {
            throw new BridgeException(ctx, elem, nfEx, "attribute.malformed", new Object[] { attrName, attrValue, nfEx });
        }
        return ret;
    }
    
    public static Rectangle2D convertFilterChainRegion(final Element filterElement, final Element filteredElement, final GraphicsNode filteredNode, final BridgeContext ctx) {
        String xStr = getChainableAttributeNS(filterElement, null, "x", ctx);
        if (xStr.length() == 0) {
            xStr = "-10%";
        }
        String yStr = getChainableAttributeNS(filterElement, null, "y", ctx);
        if (yStr.length() == 0) {
            yStr = "-10%";
        }
        String wStr = getChainableAttributeNS(filterElement, null, "width", ctx);
        if (wStr.length() == 0) {
            wStr = "120%";
        }
        String hStr = getChainableAttributeNS(filterElement, null, "height", ctx);
        if (hStr.length() == 0) {
            hStr = "120%";
        }
        String units = getChainableAttributeNS(filterElement, null, "filterUnits", ctx);
        short unitsType;
        if (units.length() == 0) {
            unitsType = 2;
        }
        else {
            unitsType = parseCoordinateSystem(filterElement, "filterUnits", units, ctx);
        }
        final UnitProcessor.Context uctx = org.apache.batik.bridge.UnitProcessor.createContext(ctx, filteredElement);
        final Rectangle2D region = convertRegion(xStr, yStr, wStr, hStr, unitsType, filteredNode, uctx);
        units = getChainableAttributeNS(filterElement, null, "filterMarginsUnits", ctx);
        if (units.length() == 0) {
            unitsType = 1;
        }
        else {
            unitsType = parseCoordinateSystem(filterElement, "filterMarginsUnits", units, ctx);
        }
        String dxStr = filterElement.getAttributeNS(null, "mx");
        if (dxStr.length() == 0) {
            dxStr = "0";
        }
        String dyStr = filterElement.getAttributeNS(null, "my");
        if (dyStr.length() == 0) {
            dyStr = "0";
        }
        String dwStr = filterElement.getAttributeNS(null, "mw");
        if (dwStr.length() == 0) {
            dwStr = "0";
        }
        String dhStr = filterElement.getAttributeNS(null, "mh");
        if (dhStr.length() == 0) {
            dhStr = "0";
        }
        return extendRegion(dxStr, dyStr, dwStr, dhStr, unitsType, filteredNode, region, uctx);
    }
    
    protected static Rectangle2D extendRegion(final String dxStr, final String dyStr, final String dwStr, final String dhStr, final short unitsType, final GraphicsNode filteredNode, final Rectangle2D region, final UnitProcessor.Context uctx) {
        float dx = 0.0f;
        float dy = 0.0f;
        float dw = 0.0f;
        float dh = 0.0f;
        switch (unitsType) {
            case 1: {
                dx = org.apache.batik.bridge.UnitProcessor.svgHorizontalCoordinateToUserSpace(dxStr, "mx", uctx);
                dy = org.apache.batik.bridge.UnitProcessor.svgVerticalCoordinateToUserSpace(dyStr, "my", uctx);
                dw = org.apache.batik.bridge.UnitProcessor.svgHorizontalCoordinateToUserSpace(dwStr, "mw", uctx);
                dh = org.apache.batik.bridge.UnitProcessor.svgVerticalCoordinateToUserSpace(dhStr, "mh", uctx);
                break;
            }
            case 2: {
                final Rectangle2D bounds = filteredNode.getGeometryBounds();
                if (bounds == null) {
                    dy = (dx = (dw = (dh = 0.0f)));
                    break;
                }
                dx = org.apache.batik.bridge.UnitProcessor.svgHorizontalCoordinateToObjectBoundingBox(dxStr, "mx", uctx);
                dx *= (float)bounds.getWidth();
                dy = org.apache.batik.bridge.UnitProcessor.svgVerticalCoordinateToObjectBoundingBox(dyStr, "my", uctx);
                dy *= (float)bounds.getHeight();
                dw = org.apache.batik.bridge.UnitProcessor.svgHorizontalCoordinateToObjectBoundingBox(dwStr, "mw", uctx);
                dw *= (float)bounds.getWidth();
                dh = org.apache.batik.bridge.UnitProcessor.svgVerticalCoordinateToObjectBoundingBox(dhStr, "mh", uctx);
                dh *= (float)bounds.getHeight();
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid unit type");
            }
        }
        region.setRect(region.getX() + dx, region.getY() + dy, region.getWidth() + dw, region.getHeight() + dh);
        return region;
    }
    
    public static Rectangle2D getBaseFilterPrimitiveRegion(final Element filterPrimitiveElement, final Element filteredElement, final GraphicsNode filteredNode, final Rectangle2D defaultRegion, final BridgeContext ctx) {
        final UnitProcessor.Context uctx = org.apache.batik.bridge.UnitProcessor.createContext(ctx, filteredElement);
        double x = defaultRegion.getX();
        String s = filterPrimitiveElement.getAttributeNS(null, "x");
        if (s.length() != 0) {
            x = org.apache.batik.bridge.UnitProcessor.svgHorizontalCoordinateToUserSpace(s, "x", uctx);
        }
        double y = defaultRegion.getY();
        s = filterPrimitiveElement.getAttributeNS(null, "y");
        if (s.length() != 0) {
            y = org.apache.batik.bridge.UnitProcessor.svgVerticalCoordinateToUserSpace(s, "y", uctx);
        }
        double w = defaultRegion.getWidth();
        s = filterPrimitiveElement.getAttributeNS(null, "width");
        if (s.length() != 0) {
            w = org.apache.batik.bridge.UnitProcessor.svgHorizontalLengthToUserSpace(s, "width", uctx);
        }
        double h = defaultRegion.getHeight();
        s = filterPrimitiveElement.getAttributeNS(null, "height");
        if (s.length() != 0) {
            h = org.apache.batik.bridge.UnitProcessor.svgVerticalLengthToUserSpace(s, "height", uctx);
        }
        return new Rectangle2D.Double(x, y, w, h);
    }
    
    public static Rectangle2D convertFilterPrimitiveRegion(final Element filterPrimitiveElement, final Element filterElement, final Element filteredElement, final GraphicsNode filteredNode, final Rectangle2D defaultRegion, final Rectangle2D filterRegion, final BridgeContext ctx) {
        String units = "";
        if (filterElement != null) {
            units = getChainableAttributeNS(filterElement, null, "primitiveUnits", ctx);
        }
        short unitsType;
        if (units.length() == 0) {
            unitsType = 1;
        }
        else {
            unitsType = parseCoordinateSystem(filterElement, "filterUnits", units, ctx);
        }
        String xStr = "";
        String yStr = "";
        String wStr = "";
        String hStr = "";
        if (filterPrimitiveElement != null) {
            xStr = filterPrimitiveElement.getAttributeNS(null, "x");
            yStr = filterPrimitiveElement.getAttributeNS(null, "y");
            wStr = filterPrimitiveElement.getAttributeNS(null, "width");
            hStr = filterPrimitiveElement.getAttributeNS(null, "height");
        }
        double x = defaultRegion.getX();
        double y = defaultRegion.getY();
        double w = defaultRegion.getWidth();
        double h = defaultRegion.getHeight();
        final UnitProcessor.Context uctx = org.apache.batik.bridge.UnitProcessor.createContext(ctx, filteredElement);
        switch (unitsType) {
            case 2: {
                final Rectangle2D bounds = filteredNode.getGeometryBounds();
                if (bounds == null) {
                    break;
                }
                if (xStr.length() != 0) {
                    x = org.apache.batik.bridge.UnitProcessor.svgHorizontalCoordinateToObjectBoundingBox(xStr, "x", uctx);
                    x = bounds.getX() + x * bounds.getWidth();
                }
                if (yStr.length() != 0) {
                    y = org.apache.batik.bridge.UnitProcessor.svgVerticalCoordinateToObjectBoundingBox(yStr, "y", uctx);
                    y = bounds.getY() + y * bounds.getHeight();
                }
                if (wStr.length() != 0) {
                    w = org.apache.batik.bridge.UnitProcessor.svgHorizontalLengthToObjectBoundingBox(wStr, "width", uctx);
                    w *= bounds.getWidth();
                }
                if (hStr.length() != 0) {
                    h = org.apache.batik.bridge.UnitProcessor.svgVerticalLengthToObjectBoundingBox(hStr, "height", uctx);
                    h *= bounds.getHeight();
                    break;
                }
                break;
            }
            case 1: {
                if (xStr.length() != 0) {
                    x = org.apache.batik.bridge.UnitProcessor.svgHorizontalCoordinateToUserSpace(xStr, "x", uctx);
                }
                if (yStr.length() != 0) {
                    y = org.apache.batik.bridge.UnitProcessor.svgVerticalCoordinateToUserSpace(yStr, "y", uctx);
                }
                if (wStr.length() != 0) {
                    w = org.apache.batik.bridge.UnitProcessor.svgHorizontalLengthToUserSpace(wStr, "width", uctx);
                }
                if (hStr.length() != 0) {
                    h = org.apache.batik.bridge.UnitProcessor.svgVerticalLengthToUserSpace(hStr, "height", uctx);
                    break;
                }
                break;
            }
            default: {
                throw new RuntimeException("invalid unitsType:" + unitsType);
            }
        }
        Rectangle2D region = new Rectangle2D.Double(x, y, w, h);
        units = "";
        if (filterElement != null) {
            units = getChainableAttributeNS(filterElement, null, "filterPrimitiveMarginsUnits", ctx);
        }
        if (units.length() == 0) {
            unitsType = 1;
        }
        else {
            unitsType = parseCoordinateSystem(filterElement, "filterPrimitiveMarginsUnits", units, ctx);
        }
        String dxStr = "";
        String dyStr = "";
        String dwStr = "";
        String dhStr = "";
        if (filterPrimitiveElement != null) {
            dxStr = filterPrimitiveElement.getAttributeNS(null, "mx");
            dyStr = filterPrimitiveElement.getAttributeNS(null, "my");
            dwStr = filterPrimitiveElement.getAttributeNS(null, "mw");
            dhStr = filterPrimitiveElement.getAttributeNS(null, "mh");
        }
        if (dxStr.length() == 0) {
            dxStr = "0";
        }
        if (dyStr.length() == 0) {
            dyStr = "0";
        }
        if (dwStr.length() == 0) {
            dwStr = "0";
        }
        if (dhStr.length() == 0) {
            dhStr = "0";
        }
        region = extendRegion(dxStr, dyStr, dwStr, dhStr, unitsType, filteredNode, region, uctx);
        Rectangle2D.intersect(region, filterRegion, region);
        return region;
    }
    
    public static Rectangle2D convertFilterPrimitiveRegion(final Element filterPrimitiveElement, final Element filteredElement, final GraphicsNode filteredNode, final Rectangle2D defaultRegion, final Rectangle2D filterRegion, final BridgeContext ctx) {
        final Node parentNode = filterPrimitiveElement.getParentNode();
        Element filterElement = null;
        if (parentNode != null && parentNode.getNodeType() == 1) {
            filterElement = (Element)parentNode;
        }
        return convertFilterPrimitiveRegion(filterPrimitiveElement, filterElement, filteredElement, filteredNode, defaultRegion, filterRegion, ctx);
    }
    
    public static short parseCoordinateSystem(final Element e, final String attr, final String coordinateSystem, final BridgeContext ctx) {
        if ("userSpaceOnUse".equals(coordinateSystem)) {
            return 1;
        }
        if ("objectBoundingBox".equals(coordinateSystem)) {
            return 2;
        }
        throw new BridgeException(ctx, e, "attribute.malformed", new Object[] { attr, coordinateSystem });
    }
    
    public static short parseMarkerCoordinateSystem(final Element e, final String attr, final String coordinateSystem, final BridgeContext ctx) {
        if ("userSpaceOnUse".equals(coordinateSystem)) {
            return 1;
        }
        if ("strokeWidth".equals(coordinateSystem)) {
            return 3;
        }
        throw new BridgeException(ctx, e, "attribute.malformed", new Object[] { attr, coordinateSystem });
    }
    
    protected static Rectangle2D convertRegion(final String xStr, final String yStr, final String wStr, final String hStr, final short unitsType, final GraphicsNode targetNode, final UnitProcessor.Context uctx) {
        double x = 0.0;
        double y = 0.0;
        double w = 0.0;
        double h = 0.0;
        switch (unitsType) {
            case 2: {
                x = org.apache.batik.bridge.UnitProcessor.svgHorizontalCoordinateToObjectBoundingBox(xStr, "x", uctx);
                y = org.apache.batik.bridge.UnitProcessor.svgVerticalCoordinateToObjectBoundingBox(yStr, "y", uctx);
                w = org.apache.batik.bridge.UnitProcessor.svgHorizontalLengthToObjectBoundingBox(wStr, "width", uctx);
                h = org.apache.batik.bridge.UnitProcessor.svgVerticalLengthToObjectBoundingBox(hStr, "height", uctx);
                final Rectangle2D bounds = targetNode.getGeometryBounds();
                if (bounds != null) {
                    x = bounds.getX() + x * bounds.getWidth();
                    y = bounds.getY() + y * bounds.getHeight();
                    w *= bounds.getWidth();
                    h *= bounds.getHeight();
                    break;
                }
                y = (x = (w = (h = 0.0)));
                break;
            }
            case 1: {
                x = org.apache.batik.bridge.UnitProcessor.svgHorizontalCoordinateToUserSpace(xStr, "x", uctx);
                y = org.apache.batik.bridge.UnitProcessor.svgVerticalCoordinateToUserSpace(yStr, "y", uctx);
                w = org.apache.batik.bridge.UnitProcessor.svgHorizontalLengthToUserSpace(wStr, "width", uctx);
                h = org.apache.batik.bridge.UnitProcessor.svgVerticalLengthToUserSpace(hStr, "height", uctx);
                break;
            }
            default: {
                throw new RuntimeException("invalid unitsType:" + unitsType);
            }
        }
        return new Rectangle2D.Double(x, y, w, h);
    }
    
    public static AffineTransform convertTransform(final Element e, final String attr, final String transform, final BridgeContext ctx) {
        try {
            return AWTTransformProducer.createAffineTransform(transform);
        }
        catch (ParseException pEx) {
            throw new BridgeException(ctx, e, pEx, "attribute.malformed", new Object[] { attr, transform, pEx });
        }
    }
    
    public static AffineTransform toObjectBBox(final AffineTransform Tx, final GraphicsNode node) {
        final AffineTransform Mx = new AffineTransform();
        final Rectangle2D bounds = node.getGeometryBounds();
        if (bounds != null) {
            Mx.translate(bounds.getX(), bounds.getY());
            Mx.scale(bounds.getWidth(), bounds.getHeight());
        }
        Mx.concatenate(Tx);
        return Mx;
    }
    
    public static Rectangle2D toObjectBBox(final Rectangle2D r, final GraphicsNode node) {
        final Rectangle2D bounds = node.getGeometryBounds();
        if (bounds != null) {
            return new Rectangle2D.Double(bounds.getX() + r.getX() * bounds.getWidth(), bounds.getY() + r.getY() * bounds.getHeight(), r.getWidth() * bounds.getWidth(), r.getHeight() * bounds.getHeight());
        }
        return new Rectangle2D.Double();
    }
    
    public static float convertSnapshotTime(final Element e, final BridgeContext ctx) {
        if (!e.hasAttributeNS(null, "snapshotTime")) {
            return 0.0f;
        }
        final String t = e.getAttributeNS(null, "snapshotTime");
        if (t.equals("none")) {
            return 0.0f;
        }
        final ClockParser p = new ClockParser(false);
        class Handler implements ClockHandler
        {
            float time;
            
            @Override
            public void clockValue(final float t) {
                this.time = t;
            }
        }
        final Handler h = new Handler();
        p.setClockHandler(h);
        try {
            p.parse(t);
        }
        catch (ParseException pEx) {
            throw new BridgeException(null, e, pEx, "attribute.malformed", new Object[] { "snapshotTime", t, pEx });
        }
        return h.time;
    }
}
