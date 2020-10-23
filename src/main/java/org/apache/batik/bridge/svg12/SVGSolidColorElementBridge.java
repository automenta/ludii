// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge.svg12;

import java.awt.Color;
import org.apache.batik.css.engine.StyleMap;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.CSSEngine;
import java.util.Map;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.util.ParsedURL;
import org.apache.batik.anim.dom.SVGOMDocument;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.bridge.PaintServer;
import org.apache.batik.css.engine.CSSStylableElement;
import org.apache.batik.bridge.CSSUtilities;
import java.util.HashMap;
import java.awt.Paint;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.PaintBridge;
import org.apache.batik.bridge.AnimatableGenericSVGBridge;

public class SVGSolidColorElementBridge extends AnimatableGenericSVGBridge implements PaintBridge
{
    @Override
    public String getNamespaceURI() {
        return "http://www.w3.org/2000/svg";
    }
    
    @Override
    public String getLocalName() {
        return "solidColor";
    }
    
    @Override
    public Paint createPaint(final BridgeContext ctx, final Element paintElement, final Element paintedElement, final GraphicsNode paintedNode, float opacity) {
        opacity = extractOpacity(paintElement, opacity, ctx);
        return extractColor(paintElement, opacity, ctx);
    }
    
    protected static float extractOpacity(Element paintElement, final float opacity, final BridgeContext ctx) {
        final Map refs = new HashMap();
        final CSSEngine eng = CSSUtilities.getCSSEngine(paintElement);
        final int pidx = eng.getPropertyIndex("solid-opacity");
        while (true) {
            final Value opacityVal = CSSUtilities.getComputedStyle(paintElement, pidx);
            final StyleMap sm = ((CSSStylableElement)paintElement).getComputedStyleMap(null);
            if (!sm.isNullCascaded(pidx)) {
                final float attr = PaintServer.convertOpacity(opacityVal);
                return opacity * attr;
            }
            final String uri = XLinkSupport.getXLinkHref(paintElement);
            if (uri.length() == 0) {
                return opacity;
            }
            final SVGOMDocument doc = (SVGOMDocument)paintElement.getOwnerDocument();
            final ParsedURL purl = new ParsedURL(doc.getURL(), uri);
            if (refs.containsKey(purl)) {
                throw new BridgeException(ctx, paintElement, "xlink.href.circularDependencies", new Object[] { uri });
            }
            refs.put(purl, purl);
            paintElement = ctx.getReferencedElement(paintElement, uri);
        }
    }
    
    protected static Color extractColor(Element paintElement, final float opacity, final BridgeContext ctx) {
        final Map refs = new HashMap();
        final CSSEngine eng = CSSUtilities.getCSSEngine(paintElement);
        final int pidx = eng.getPropertyIndex("solid-color");
        while (true) {
            final Value colorDef = CSSUtilities.getComputedStyle(paintElement, pidx);
            final StyleMap sm = ((CSSStylableElement)paintElement).getComputedStyleMap(null);
            if (!sm.isNullCascaded(pidx)) {
                if (colorDef.getCssValueType() == 1) {
                    return PaintServer.convertColor(colorDef, opacity);
                }
                return PaintServer.convertRGBICCColor(paintElement, colorDef.item(0), colorDef.item(1), opacity, ctx);
            }
            else {
                final String uri = XLinkSupport.getXLinkHref(paintElement);
                if (uri.length() == 0) {
                    return new Color(0.0f, 0.0f, 0.0f, opacity);
                }
                final SVGOMDocument doc = (SVGOMDocument)paintElement.getOwnerDocument();
                final ParsedURL purl = new ParsedURL(doc.getURL(), uri);
                if (refs.containsKey(purl)) {
                    throw new BridgeException(ctx, paintElement, "xlink.href.circularDependencies", new Object[] { uri });
                }
                refs.put(purl, purl);
                paintElement = ctx.getReferencedElement(paintElement, uri);
            }
        }
    }
}
