// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import java.awt.BasicStroke;
import org.apache.xmlgraphics.java2d.color.DeviceCMYKColorSpace;
import org.apache.xmlgraphics.java2d.color.ColorSpaces;
import org.apache.xmlgraphics.java2d.color.CIELabColorSpace;
import org.apache.xmlgraphics.java2d.color.NamedColorSpace;
import org.apache.xmlgraphics.java2d.color.profile.NamedColorProfile;
import java.awt.color.ICC_Profile;
import java.awt.color.ColorSpace;
import org.apache.xmlgraphics.java2d.color.ColorWithAlternatives;
import java.io.IOException;
import org.apache.xmlgraphics.java2d.color.profile.NamedColorProfileParser;
import org.apache.xmlgraphics.java2d.color.ICCColorSpaceWithIntent;
import org.apache.batik.css.engine.value.svg12.DeviceColor;
import org.apache.batik.css.engine.value.svg12.CIELabColor;
import org.apache.batik.css.engine.value.svg12.ICCNamedColor;
import org.apache.batik.css.engine.value.svg.ICCColor;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.Paint;
import java.awt.Shape;
import org.apache.batik.gvt.CompositeShapePainter;
import org.apache.batik.gvt.StrokeShapePainter;
import org.apache.batik.gvt.FillShapePainter;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.Marker;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.gvt.MarkerShapePainter;
import org.apache.batik.gvt.ShapePainter;
import org.apache.batik.gvt.ShapeNode;
import org.w3c.dom.Element;
import org.apache.batik.util.CSSConstants;
import org.apache.batik.util.SVGConstants;

public abstract class PaintServer implements SVGConstants, CSSConstants, ErrorConstants
{
    protected PaintServer() {
    }
    
    public static ShapePainter convertMarkers(final Element e, final ShapeNode node, final BridgeContext ctx) {
        Value v = CSSUtilities.getComputedStyle(e, 36);
        final Marker startMarker = convertMarker(e, v, ctx);
        v = CSSUtilities.getComputedStyle(e, 35);
        final Marker midMarker = convertMarker(e, v, ctx);
        v = CSSUtilities.getComputedStyle(e, 34);
        final Marker endMarker = convertMarker(e, v, ctx);
        if (startMarker != null || midMarker != null || endMarker != null) {
            final MarkerShapePainter p = new MarkerShapePainter(node.getShape());
            p.setStartMarker(startMarker);
            p.setMiddleMarker(midMarker);
            p.setEndMarker(endMarker);
            return p;
        }
        return null;
    }
    
    public static Marker convertMarker(final Element e, final Value v, final BridgeContext ctx) {
        if (v.getPrimitiveType() == 21) {
            return null;
        }
        final String uri = v.getStringValue();
        final Element markerElement = ctx.getReferencedElement(e, uri);
        final Bridge bridge = ctx.getBridge(markerElement);
        if (bridge == null || !(bridge instanceof MarkerBridge)) {
            throw new BridgeException(ctx, e, "css.uri.badTarget", new Object[] { uri });
        }
        return ((MarkerBridge)bridge).createMarker(ctx, markerElement, e);
    }
    
    public static ShapePainter convertFillAndStroke(final Element e, final ShapeNode node, final BridgeContext ctx) {
        final Shape shape = node.getShape();
        if (shape == null) {
            return null;
        }
        final Paint fillPaint = convertFillPaint(e, node, ctx);
        final FillShapePainter fp = new FillShapePainter(shape);
        fp.setPaint(fillPaint);
        final Stroke stroke = convertStroke(e);
        if (stroke == null) {
            return fp;
        }
        final Paint strokePaint = convertStrokePaint(e, node, ctx);
        final StrokeShapePainter sp = new StrokeShapePainter(shape);
        sp.setStroke(stroke);
        sp.setPaint(strokePaint);
        final CompositeShapePainter cp = new CompositeShapePainter(shape);
        cp.addShapePainter(fp);
        cp.addShapePainter(sp);
        return cp;
    }
    
    public static ShapePainter convertStrokePainter(final Element e, final ShapeNode node, final BridgeContext ctx) {
        final Shape shape = node.getShape();
        if (shape == null) {
            return null;
        }
        final Stroke stroke = convertStroke(e);
        if (stroke == null) {
            return null;
        }
        final Paint strokePaint = convertStrokePaint(e, node, ctx);
        final StrokeShapePainter sp = new StrokeShapePainter(shape);
        sp.setStroke(stroke);
        sp.setPaint(strokePaint);
        return sp;
    }
    
    public static Paint convertStrokePaint(final Element strokedElement, final GraphicsNode strokedNode, final BridgeContext ctx) {
        Value v = CSSUtilities.getComputedStyle(strokedElement, 51);
        final float opacity = convertOpacity(v);
        v = CSSUtilities.getComputedStyle(strokedElement, 45);
        return convertPaint(strokedElement, strokedNode, v, opacity, ctx);
    }
    
    public static Paint convertFillPaint(final Element filledElement, final GraphicsNode filledNode, final BridgeContext ctx) {
        Value v = CSSUtilities.getComputedStyle(filledElement, 16);
        final float opacity = convertOpacity(v);
        v = CSSUtilities.getComputedStyle(filledElement, 15);
        return convertPaint(filledElement, filledNode, v, opacity, ctx);
    }
    
    public static Paint convertPaint(final Element paintedElement, final GraphicsNode paintedNode, final Value paintDef, final float opacity, final BridgeContext ctx) {
        if (paintDef.getCssValueType() == 1) {
            switch (paintDef.getPrimitiveType()) {
                case 21: {
                    return null;
                }
                case 25: {
                    return convertColor(paintDef, opacity);
                }
                case 20: {
                    return convertURIPaint(paintedElement, paintedNode, paintDef, opacity, ctx);
                }
                default: {
                    throw new IllegalArgumentException("Paint argument is not an appropriate CSS value");
                }
            }
        }
        else {
            Value v = paintDef.item(0);
            switch (v.getPrimitiveType()) {
                case 25: {
                    return convertRGBICCColor(paintedElement, v, paintDef.item(1), opacity, ctx);
                }
                case 20: {
                    final Paint result = silentConvertURIPaint(paintedElement, paintedNode, v, opacity, ctx);
                    if (result != null) {
                        return result;
                    }
                    v = paintDef.item(1);
                    switch (v.getPrimitiveType()) {
                        case 21: {
                            return null;
                        }
                        case 25: {
                            if (paintDef.getLength() == 2) {
                                return convertColor(v, opacity);
                            }
                            return convertRGBICCColor(paintedElement, v, paintDef.item(2), opacity, ctx);
                        }
                        default: {
                            throw new IllegalArgumentException("Paint argument is not an appropriate CSS value");
                        }
                    }
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Paint argument is not an appropriate CSS value");
                }
            }
        }
    }
    
    public static Paint silentConvertURIPaint(final Element paintedElement, final GraphicsNode paintedNode, final Value paintDef, final float opacity, final BridgeContext ctx) {
        Paint paint = null;
        try {
            paint = convertURIPaint(paintedElement, paintedNode, paintDef, opacity, ctx);
        }
        catch (BridgeException ex) {}
        return paint;
    }
    
    public static Paint convertURIPaint(final Element paintedElement, final GraphicsNode paintedNode, final Value paintDef, final float opacity, final BridgeContext ctx) {
        final String uri = paintDef.getStringValue();
        final Element paintElement = ctx.getReferencedElement(paintedElement, uri);
        final Bridge bridge = ctx.getBridge(paintElement);
        if (bridge == null || !(bridge instanceof PaintBridge)) {
            throw new BridgeException(ctx, paintedElement, "css.uri.badTarget", new Object[] { uri });
        }
        return ((PaintBridge)bridge).createPaint(ctx, paintElement, paintedElement, paintedNode, opacity);
    }
    
    public static Color convertRGBICCColor(final Element paintedElement, final Value colorDef, final Value iccColor, final float opacity, final BridgeContext ctx) {
        Color color = null;
        if (iccColor != null) {
            if (iccColor instanceof ICCColor) {
                color = convertICCColor(paintedElement, (ICCColor)iccColor, opacity, ctx);
            }
            else if (iccColor instanceof ICCNamedColor) {
                color = convertICCNamedColor(paintedElement, (ICCNamedColor)iccColor, opacity, ctx);
            }
            else if (iccColor instanceof CIELabColor) {
                color = convertCIELabColor(paintedElement, (CIELabColor)iccColor, opacity, ctx);
            }
            else if (iccColor instanceof DeviceColor) {
                color = convertDeviceColor(paintedElement, colorDef, (DeviceColor)iccColor, opacity, ctx);
            }
        }
        if (color == null) {
            color = convertColor(colorDef, opacity);
        }
        return color;
    }
    
    public static Color convertICCColor(final Element e, final ICCColor c, final float opacity, final BridgeContext ctx) {
        final String iccProfileName = c.getColorProfile();
        if (iccProfileName == null) {
            return null;
        }
        final SVGColorProfileElementBridge profileBridge = (SVGColorProfileElementBridge)ctx.getBridge("http://www.w3.org/2000/svg", "color-profile");
        if (profileBridge == null) {
            return null;
        }
        final ICCColorSpaceWithIntent profileCS = profileBridge.createICCColorSpaceWithIntent(ctx, e, iccProfileName);
        if (profileCS == null) {
            return null;
        }
        final int n = c.getNumberOfColors();
        final float[] colorValue = new float[n];
        if (n == 0) {
            return null;
        }
        for (int i = 0; i < n; ++i) {
            colorValue[i] = c.getColor(i);
        }
        final float[] rgb = profileCS.intendedToRGB(colorValue);
        return new Color(rgb[0], rgb[1], rgb[2], opacity);
    }
    
    public static Color convertICCNamedColor(final Element e, final ICCNamedColor c, final float opacity, final BridgeContext ctx) {
        final String iccProfileName = c.getColorProfile();
        if (iccProfileName == null) {
            return null;
        }
        final SVGColorProfileElementBridge profileBridge = (SVGColorProfileElementBridge)ctx.getBridge("http://www.w3.org/2000/svg", "color-profile");
        if (profileBridge == null) {
            return null;
        }
        final ICCColorSpaceWithIntent profileCS = profileBridge.createICCColorSpaceWithIntent(ctx, e, iccProfileName);
        if (profileCS == null) {
            return null;
        }
        final ICC_Profile iccProfile = profileCS.getProfile();
        final String iccProfileSrc = null;
        if (NamedColorProfileParser.isNamedColorProfile(iccProfile)) {
            final NamedColorProfileParser parser = new NamedColorProfileParser();
            NamedColorProfile ncp;
            try {
                ncp = parser.parseProfile(iccProfile, iccProfileName, iccProfileSrc);
            }
            catch (IOException ioe) {
                return null;
            }
            final NamedColorSpace ncs = ncp.getNamedColor(c.getColorName());
            if (ncs != null) {
                final Color specColor = new ColorWithAlternatives(ncs, new float[] { 1.0f }, opacity, null);
                return specColor;
            }
        }
        return null;
    }
    
    public static Color convertCIELabColor(final Element e, final CIELabColor c, final float opacity, final BridgeContext ctx) {
        final CIELabColorSpace cs = new CIELabColorSpace(c.getWhitePoint());
        final float[] lab = c.getColorValues();
        final Color specColor = cs.toColor(lab[0], lab[1], lab[2], opacity);
        return specColor;
    }
    
    public static Color convertDeviceColor(final Element e, final Value srgb, final DeviceColor c, final float opacity, final BridgeContext ctx) {
        final int r = resolveColorComponent(srgb.getRed());
        final int g = resolveColorComponent(srgb.getGreen());
        final int b = resolveColorComponent(srgb.getBlue());
        if (c.isNChannel()) {
            return convertColor(srgb, opacity);
        }
        if (c.getNumberOfColors() == 4) {
            final DeviceCMYKColorSpace cmykCs = ColorSpaces.getDeviceCMYKColorSpace();
            final float[] comps = new float[4];
            for (int i = 0; i < 4; ++i) {
                comps[i] = c.getColor(i);
            }
            final Color cmyk = new ColorWithAlternatives(cmykCs, comps, opacity, null);
            final Color specColor = new ColorWithAlternatives(r, g, b, Math.round(opacity * 255.0f), new Color[] { cmyk });
            return specColor;
        }
        return convertColor(srgb, opacity);
    }
    
    public static Color convertColor(final Value c, final float opacity) {
        final int r = resolveColorComponent(c.getRed());
        final int g = resolveColorComponent(c.getGreen());
        final int b = resolveColorComponent(c.getBlue());
        return new Color(r, g, b, Math.round(opacity * 255.0f));
    }
    
    public static Stroke convertStroke(final Element e) {
        Value v = CSSUtilities.getComputedStyle(e, 52);
        final float width = v.getFloatValue();
        if (width == 0.0f) {
            return null;
        }
        v = CSSUtilities.getComputedStyle(e, 48);
        final int linecap = convertStrokeLinecap(v);
        v = CSSUtilities.getComputedStyle(e, 49);
        final int linejoin = convertStrokeLinejoin(v);
        v = CSSUtilities.getComputedStyle(e, 50);
        final float miterlimit = convertStrokeMiterlimit(v);
        v = CSSUtilities.getComputedStyle(e, 46);
        final float[] dasharray = convertStrokeDasharray(v);
        float dashoffset = 0.0f;
        if (dasharray != null) {
            v = CSSUtilities.getComputedStyle(e, 47);
            dashoffset = v.getFloatValue();
            if (dashoffset < 0.0f) {
                float dashpatternlength = 0.0f;
                for (final float aDasharray : dasharray) {
                    dashpatternlength += aDasharray;
                }
                if (dasharray.length % 2 != 0) {
                    dashpatternlength *= 2.0f;
                }
                if (dashpatternlength == 0.0f) {
                    dashoffset = 0.0f;
                }
                else {
                    while (dashoffset < 0.0f) {
                        dashoffset += dashpatternlength;
                    }
                }
            }
        }
        return new BasicStroke(width, linecap, linejoin, miterlimit, dasharray, dashoffset);
    }
    
    public static float[] convertStrokeDasharray(final Value v) {
        float[] dasharray = null;
        if (v.getCssValueType() == 2) {
            final int length = v.getLength();
            dasharray = new float[length];
            float sum = 0.0f;
            for (int i = 0; i < dasharray.length; ++i) {
                dasharray[i] = v.item(i).getFloatValue();
                sum += dasharray[i];
            }
            if (sum == 0.0f) {
                dasharray = null;
            }
        }
        return dasharray;
    }
    
    public static float convertStrokeMiterlimit(final Value v) {
        final float miterlimit = v.getFloatValue();
        return (miterlimit < 1.0f) ? 1.0f : miterlimit;
    }
    
    public static int convertStrokeLinecap(final Value v) {
        final String s = v.getStringValue();
        switch (s.charAt(0)) {
            case 'b': {
                return 0;
            }
            case 'r': {
                return 1;
            }
            case 's': {
                return 2;
            }
            default: {
                throw new IllegalArgumentException("Linecap argument is not an appropriate CSS value");
            }
        }
    }
    
    public static int convertStrokeLinejoin(final Value v) {
        final String s = v.getStringValue();
        switch (s.charAt(0)) {
            case 'm': {
                return 0;
            }
            case 'r': {
                return 1;
            }
            case 'b': {
                return 2;
            }
            default: {
                throw new IllegalArgumentException("Linejoin argument is not an appropriate CSS value");
            }
        }
    }
    
    public static int resolveColorComponent(final Value v) {
        switch (v.getPrimitiveType()) {
            case 2: {
                float f = v.getFloatValue();
                f = ((f > 100.0f) ? 100.0f : ((f < 0.0f) ? 0.0f : f));
                return Math.round(255.0f * f / 100.0f);
            }
            case 1: {
                float f = v.getFloatValue();
                f = ((f > 255.0f) ? 255.0f : ((f < 0.0f) ? 0.0f : f));
                return Math.round(f);
            }
            default: {
                throw new IllegalArgumentException("Color component argument is not an appropriate CSS value");
            }
        }
    }
    
    public static float convertOpacity(final Value v) {
        final float r = v.getFloatValue();
        return (r < 0.0f) ? 0.0f : ((r > 1.0f) ? 1.0f : r);
    }
}
