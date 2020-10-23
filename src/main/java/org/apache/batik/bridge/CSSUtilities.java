// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSValue;
import java.awt.Color;
import org.apache.batik.gvt.filter.Mask;
import org.apache.batik.ext.awt.image.renderable.ClipRable;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.gvt.GraphicsNode;
import java.awt.AlphaComposite;
import java.util.Map;
import java.awt.RenderingHints;
import java.awt.Cursor;
import org.apache.batik.ext.awt.MultipleGradientPaint;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.css.engine.value.ListValue;
import java.awt.geom.Rectangle2D;
import org.apache.batik.css.engine.CSSStylableElement;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.anim.dom.SVGOMDocument;
import org.apache.batik.css.engine.CSSEngine;
import org.w3c.dom.Element;
import java.awt.Composite;
import org.apache.batik.constants.XMLConstants;
import org.apache.batik.util.CSSConstants;

public abstract class CSSUtilities implements CSSConstants, ErrorConstants, XMLConstants
{
    public static final Composite TRANSPARENT;
    
    protected CSSUtilities() {
    }
    
    public static CSSEngine getCSSEngine(final Element e) {
        return ((SVGOMDocument)e.getOwnerDocument()).getCSSEngine();
    }
    
    public static Value getComputedStyle(final Element e, final int property) {
        final CSSEngine engine = getCSSEngine(e);
        if (engine == null) {
            return null;
        }
        return engine.getComputedStyle((CSSStylableElement)e, null, property);
    }
    
    public static int convertPointerEvents(final Element e) {
        final Value v = getComputedStyle(e, 40);
        final String s = v.getStringValue();
        switch (s.charAt(0)) {
            case 'v': {
                if (s.length() == 7) {
                    return 3;
                }
                switch (s.charAt(7)) {
                    case 'p': {
                        return 0;
                    }
                    case 'f': {
                        return 1;
                    }
                    case 's': {
                        return 2;
                    }
                    default: {
                        throw new IllegalStateException("unexpected event, must be one of (p,f,s) is:" + s.charAt(7));
                    }
                }
                break;
            }
            case 'p': {
                return 4;
            }
            case 'f': {
                return 5;
            }
            case 's': {
                return 6;
            }
            case 'a': {
                return 7;
            }
            case 'n': {
                return 8;
            }
            default: {
                throw new IllegalStateException("unexpected event, must be one of (v,p,f,s,a,n) is:" + s.charAt(0));
            }
        }
    }
    
    public static Rectangle2D convertEnableBackground(final Element e) {
        final Value v = getComputedStyle(e, 14);
        if (v.getCssValueType() != 2) {
            return null;
        }
        final ListValue lv = (ListValue)v;
        final int length = lv.getLength();
        switch (length) {
            case 1: {
                return CompositeGraphicsNode.VIEWPORT;
            }
            case 5: {
                final float x = lv.item(1).getFloatValue();
                final float y = lv.item(2).getFloatValue();
                final float w = lv.item(3).getFloatValue();
                final float h = lv.item(4).getFloatValue();
                return new Rectangle2D.Float(x, y, w, h);
            }
            default: {
                throw new IllegalStateException("Unexpected length:" + length);
            }
        }
    }
    
    public static boolean convertColorInterpolationFilters(final Element e) {
        final Value v = getComputedStyle(e, 7);
        return "linearrgb" == v.getStringValue();
    }
    
    public static MultipleGradientPaint.ColorSpaceEnum convertColorInterpolation(final Element e) {
        final Value v = getComputedStyle(e, 6);
        return ("linearrgb" == v.getStringValue()) ? MultipleGradientPaint.LINEAR_RGB : MultipleGradientPaint.SRGB;
    }
    
    public static boolean isAutoCursor(final Element e) {
        final Value cursorValue = getComputedStyle(e, 10);
        boolean isAuto = false;
        if (cursorValue != null) {
            if (cursorValue.getCssValueType() == 1 && cursorValue.getPrimitiveType() == 21 && cursorValue.getStringValue().charAt(0) == 'a') {
                isAuto = true;
            }
            else if (cursorValue.getCssValueType() == 2 && cursorValue.getLength() == 1) {
                final Value lValue = cursorValue.item(0);
                if (lValue != null && lValue.getCssValueType() == 1 && lValue.getPrimitiveType() == 21 && lValue.getStringValue().charAt(0) == 'a') {
                    isAuto = true;
                }
            }
        }
        return isAuto;
    }
    
    public static Cursor convertCursor(final Element e, final BridgeContext ctx) {
        return ctx.getCursorManager().convertCursor(e);
    }
    
    public static RenderingHints convertShapeRendering(final Element e, RenderingHints hints) {
        final Value v = getComputedStyle(e, 42);
        final String s = v.getStringValue();
        final int len = s.length();
        if (len == 4 && s.charAt(0) == 'a') {
            return hints;
        }
        if (len < 10) {
            return hints;
        }
        if (hints == null) {
            hints = new RenderingHints(null);
        }
        switch (s.charAt(0)) {
            case 'o': {
                hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
                hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                break;
            }
            case 'c': {
                hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_DEFAULT);
                hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                break;
            }
            case 'g': {
                hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                hints.put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                break;
            }
        }
        return hints;
    }
    
    public static RenderingHints convertTextRendering(final Element e, RenderingHints hints) {
        final Value v = getComputedStyle(e, 55);
        final String s = v.getStringValue();
        final int len = s.length();
        if (len == 4 && s.charAt(0) == 'a') {
            return hints;
        }
        if (len < 13) {
            return hints;
        }
        if (hints == null) {
            hints = new RenderingHints(null);
        }
        switch (s.charAt(8)) {
            case 's': {
                hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
                hints.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
                hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                break;
            }
            case 'l': {
                hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                hints.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
                hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                break;
            }
            case 'c': {
                hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                hints.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                hints.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
                hints.put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                break;
            }
        }
        return hints;
    }
    
    public static RenderingHints convertImageRendering(final Element e, RenderingHints hints) {
        final Value v = getComputedStyle(e, 30);
        final String s = v.getStringValue();
        final int len = s.length();
        if (len == 4 && s.charAt(0) == 'a') {
            return hints;
        }
        if (len < 13) {
            return hints;
        }
        if (hints == null) {
            hints = new RenderingHints(null);
        }
        switch (s.charAt(8)) {
            case 's': {
                hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
                hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                break;
            }
            case 'q': {
                hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                break;
            }
        }
        return hints;
    }
    
    public static RenderingHints convertColorRendering(final Element e, RenderingHints hints) {
        final Value v = getComputedStyle(e, 9);
        final String s = v.getStringValue();
        final int len = s.length();
        if (len == 4 && s.charAt(0) == 'a') {
            return hints;
        }
        if (len < 13) {
            return hints;
        }
        if (hints == null) {
            hints = new RenderingHints(null);
        }
        switch (s.charAt(8)) {
            case 's': {
                hints.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
                hints.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
                break;
            }
            case 'q': {
                hints.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
                hints.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
                break;
            }
        }
        return hints;
    }
    
    public static boolean convertDisplay(final Element e) {
        if (!(e instanceof CSSStylableElement)) {
            return true;
        }
        final Value v = getComputedStyle(e, 12);
        return v.getStringValue().charAt(0) != 'n';
    }
    
    public static boolean convertVisibility(final Element e) {
        final Value v = getComputedStyle(e, 57);
        return v.getStringValue().charAt(0) == 'v';
    }
    
    public static Composite convertOpacity(final Element e) {
        final Value v = getComputedStyle(e, 38);
        final float f = v.getFloatValue();
        if (f <= 0.0f) {
            return CSSUtilities.TRANSPARENT;
        }
        if (f >= 1.0f) {
            return AlphaComposite.SrcOver;
        }
        return AlphaComposite.getInstance(3, f);
    }
    
    public static boolean convertOverflow(final Element e) {
        final Value v = getComputedStyle(e, 39);
        final String s = v.getStringValue();
        return s.charAt(0) == 'h' || s.charAt(0) == 's';
    }
    
    public static float[] convertClip(final Element e) {
        final Value v = getComputedStyle(e, 2);
        final int primitiveType = v.getPrimitiveType();
        switch (primitiveType) {
            case 24: {
                final float[] off = { v.getTop().getFloatValue(), v.getRight().getFloatValue(), v.getBottom().getFloatValue(), v.getLeft().getFloatValue() };
                return off;
            }
            case 21: {
                return null;
            }
            default: {
                throw new IllegalStateException("Unexpected primitiveType:" + primitiveType);
            }
        }
    }
    
    public static Filter convertFilter(final Element filteredElement, final GraphicsNode filteredNode, final BridgeContext ctx) {
        final Value v = getComputedStyle(filteredElement, 18);
        final int primitiveType = v.getPrimitiveType();
        switch (primitiveType) {
            case 21: {
                return null;
            }
            case 20: {
                final String uri = v.getStringValue();
                final Element filter = ctx.getReferencedElement(filteredElement, uri);
                final Bridge bridge = ctx.getBridge(filter);
                if (bridge == null || !(bridge instanceof FilterBridge)) {
                    throw new BridgeException(ctx, filteredElement, "css.uri.badTarget", new Object[] { uri });
                }
                return ((FilterBridge)bridge).createFilter(ctx, filter, filteredElement, filteredNode);
            }
            default: {
                throw new IllegalStateException("Unexpected primitive type:" + primitiveType);
            }
        }
    }
    
    public static ClipRable convertClipPath(final Element clippedElement, final GraphicsNode clippedNode, final BridgeContext ctx) {
        final Value v = getComputedStyle(clippedElement, 3);
        final int primitiveType = v.getPrimitiveType();
        switch (primitiveType) {
            case 21: {
                return null;
            }
            case 20: {
                final String uri = v.getStringValue();
                final Element cp = ctx.getReferencedElement(clippedElement, uri);
                final Bridge bridge = ctx.getBridge(cp);
                if (bridge == null || !(bridge instanceof ClipBridge)) {
                    throw new BridgeException(ctx, clippedElement, "css.uri.badTarget", new Object[] { uri });
                }
                return ((ClipBridge)bridge).createClip(ctx, cp, clippedElement, clippedNode);
            }
            default: {
                throw new IllegalStateException("Unexpected primitive type:" + primitiveType);
            }
        }
    }
    
    public static int convertClipRule(final Element e) {
        final Value v = getComputedStyle(e, 4);
        return (v.getStringValue().charAt(0) == 'n') ? 1 : 0;
    }
    
    public static Mask convertMask(final Element maskedElement, final GraphicsNode maskedNode, final BridgeContext ctx) {
        final Value v = getComputedStyle(maskedElement, 37);
        final int primitiveType = v.getPrimitiveType();
        switch (primitiveType) {
            case 21: {
                return null;
            }
            case 20: {
                final String uri = v.getStringValue();
                final Element m = ctx.getReferencedElement(maskedElement, uri);
                final Bridge bridge = ctx.getBridge(m);
                if (bridge == null || !(bridge instanceof MaskBridge)) {
                    throw new BridgeException(ctx, maskedElement, "css.uri.badTarget", new Object[] { uri });
                }
                return ((MaskBridge)bridge).createMask(ctx, m, maskedElement, maskedNode);
            }
            default: {
                throw new IllegalStateException("Unexpected primitive type:" + primitiveType);
            }
        }
    }
    
    public static int convertFillRule(final Element e) {
        final Value v = getComputedStyle(e, 17);
        return (v.getStringValue().charAt(0) == 'n') ? 1 : 0;
    }
    
    public static Color convertLightingColor(final Element e, final BridgeContext ctx) {
        final Value v = getComputedStyle(e, 33);
        if (v.getCssValueType() == 1) {
            return PaintServer.convertColor(v, 1.0f);
        }
        return PaintServer.convertRGBICCColor(e, v.item(0), v.item(1), 1.0f, ctx);
    }
    
    public static Color convertFloodColor(final Element e, final BridgeContext ctx) {
        final Value v = getComputedStyle(e, 19);
        final Value o = getComputedStyle(e, 20);
        final float f = PaintServer.convertOpacity(o);
        if (v.getCssValueType() == 1) {
            return PaintServer.convertColor(v, f);
        }
        return PaintServer.convertRGBICCColor(e, v.item(0), v.item(1), f, ctx);
    }
    
    public static Color convertStopColor(final Element e, float opacity, final BridgeContext ctx) {
        final Value v = getComputedStyle(e, 43);
        final Value o = getComputedStyle(e, 44);
        opacity *= PaintServer.convertOpacity(o);
        if (v.getCssValueType() == 1) {
            return PaintServer.convertColor(v, opacity);
        }
        return PaintServer.convertRGBICCColor(e, v.item(0), v.item(1), opacity, ctx);
    }
    
    public static void computeStyleAndURIs(final Element refElement, final Element localRefElement, String uri) {
        final int idx = uri.indexOf(35);
        if (idx != -1) {
            uri = uri.substring(0, idx);
        }
        if (uri.length() != 0) {
            localRefElement.setAttributeNS("http://www.w3.org/XML/1998/namespace", "base", uri);
        }
        final CSSEngine engine = getCSSEngine(localRefElement);
        final CSSEngine refEngine = getCSSEngine(refElement);
        engine.importCascadedStyleMaps(refElement, refEngine, localRefElement);
    }
    
    protected static int rule(final CSSValue v) {
        return (((CSSPrimitiveValue)v).getStringValue().charAt(0) == 'n') ? 1 : 0;
    }
    
    static {
        TRANSPARENT = AlphaComposite.getInstance(3, 0.0f);
    }
}
