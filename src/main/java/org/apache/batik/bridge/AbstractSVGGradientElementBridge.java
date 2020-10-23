// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.w3c.dom.Node;
import org.apache.batik.util.ParsedURL;
import org.apache.batik.dom.util.XLinkSupport;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.List;
import java.awt.geom.AffineTransform;
import org.apache.batik.ext.awt.MultipleGradientPaint;
import java.awt.Color;
import java.awt.Paint;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;

public abstract class AbstractSVGGradientElementBridge extends AnimatableGenericSVGBridge implements PaintBridge, ErrorConstants
{
    protected AbstractSVGGradientElementBridge() {
    }
    
    @Override
    public Paint createPaint(final BridgeContext ctx, final Element paintElement, final Element paintedElement, final GraphicsNode paintedNode, final float opacity) {
        final List stops = extractStop(paintElement, opacity, ctx);
        if (stops == null) {
            return null;
        }
        final int stopLength = stops.size();
        if (stopLength == 1) {
            return stops.get(0).color;
        }
        final float[] offsets = new float[stopLength];
        final Color[] colors = new Color[stopLength];
        final Iterator iter = stops.iterator();
        int i = 0;
        while (iter.hasNext()) {
            final Stop stop = iter.next();
            offsets[i] = stop.offset;
            colors[i] = stop.color;
            ++i;
        }
        MultipleGradientPaint.CycleMethodEnum spreadMethod = MultipleGradientPaint.NO_CYCLE;
        String s = SVGUtilities.getChainableAttributeNS(paintElement, null, "spreadMethod", ctx);
        if (s.length() != 0) {
            spreadMethod = convertSpreadMethod(paintElement, s, ctx);
        }
        final MultipleGradientPaint.ColorSpaceEnum colorSpace = CSSUtilities.convertColorInterpolation(paintElement);
        s = SVGUtilities.getChainableAttributeNS(paintElement, null, "gradientTransform", ctx);
        AffineTransform transform;
        if (s.length() != 0) {
            transform = SVGUtilities.convertTransform(paintElement, "gradientTransform", s, ctx);
        }
        else {
            transform = new AffineTransform();
        }
        final Paint paint = this.buildGradient(paintElement, paintedElement, paintedNode, spreadMethod, colorSpace, transform, colors, offsets, ctx);
        return paint;
    }
    
    protected abstract Paint buildGradient(final Element p0, final Element p1, final GraphicsNode p2, final MultipleGradientPaint.CycleMethodEnum p3, final MultipleGradientPaint.ColorSpaceEnum p4, final AffineTransform p5, final Color[] p6, final float[] p7, final BridgeContext p8);
    
    protected static MultipleGradientPaint.CycleMethodEnum convertSpreadMethod(final Element paintElement, final String s, final BridgeContext ctx) {
        if ("repeat".equals(s)) {
            return MultipleGradientPaint.REPEAT;
        }
        if ("reflect".equals(s)) {
            return MultipleGradientPaint.REFLECT;
        }
        if ("pad".equals(s)) {
            return MultipleGradientPaint.NO_CYCLE;
        }
        throw new BridgeException(ctx, paintElement, "attribute.malformed", new Object[] { "spreadMethod", s });
    }
    
    protected static List extractStop(Element paintElement, final float opacity, final BridgeContext ctx) {
        final List refs = new LinkedList();
        while (true) {
            final List stops = extractLocalStop(paintElement, opacity, ctx);
            if (stops != null) {
                return stops;
            }
            final String uri = XLinkSupport.getXLinkHref(paintElement);
            if (uri.length() == 0) {
                return null;
            }
            final String baseURI = paintElement.getBaseURI();
            final ParsedURL purl = new ParsedURL(baseURI, uri);
            if (contains(refs, purl)) {
                throw new BridgeException(ctx, paintElement, "xlink.href.circularDependencies", new Object[] { uri });
            }
            refs.add(purl);
            paintElement = ctx.getReferencedElement(paintElement, uri);
        }
    }
    
    protected static List extractLocalStop(final Element gradientElement, final float opacity, final BridgeContext ctx) {
        LinkedList stops = null;
        Stop previous = null;
        for (Node n = gradientElement.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() == 1) {
                final Element e = (Element)n;
                final Bridge bridge = ctx.getBridge(e);
                if (bridge != null) {
                    if (bridge instanceof SVGStopElementBridge) {
                        final Stop stop = ((SVGStopElementBridge)bridge).createStop(ctx, gradientElement, e, opacity);
                        if (stops == null) {
                            stops = new LinkedList();
                        }
                        if (previous != null && stop.offset < previous.offset) {
                            stop.offset = previous.offset;
                        }
                        stops.add(stop);
                        previous = stop;
                    }
                }
            }
        }
        return stops;
    }
    
    private static boolean contains(final List urls, final ParsedURL key) {
        for (final Object url : urls) {
            if (key.equals(url)) {
                return true;
            }
        }
        return false;
    }
    
    public static class Stop
    {
        public Color color;
        public float offset;
        
        public Stop(final Color color, final float offset) {
            this.color = color;
            this.offset = offset;
        }
    }
    
    public static class SVGStopElementBridge extends AnimatableGenericSVGBridge implements Bridge
    {
        @Override
        public String getLocalName() {
            return "stop";
        }
        
        public Stop createStop(final BridgeContext ctx, final Element gradientElement, final Element stopElement, final float opacity) {
            final String s = stopElement.getAttributeNS(null, "offset");
            if (s.length() == 0) {
                throw new BridgeException(ctx, stopElement, "attribute.missing", new Object[] { "offset" });
            }
            float offset;
            try {
                offset = SVGUtilities.convertRatio(s);
            }
            catch (NumberFormatException nfEx) {
                throw new BridgeException(ctx, stopElement, nfEx, "attribute.malformed", new Object[] { "offset", s, nfEx });
            }
            final Color color = CSSUtilities.convertStopColor(stopElement, opacity, ctx);
            return new Stop(color, offset);
        }
    }
}
