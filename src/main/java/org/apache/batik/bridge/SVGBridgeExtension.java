// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.w3c.dom.Element;
import java.util.Collections;
import java.util.Iterator;

public class SVGBridgeExtension implements BridgeExtension
{
    @Override
    public float getPriority() {
        return 0.0f;
    }
    
    @Override
    public Iterator getImplementedExtensions() {
        return Collections.EMPTY_LIST.iterator();
    }
    
    @Override
    public String getAuthor() {
        return "The Apache Batik Team.";
    }
    
    @Override
    public String getContactAddress() {
        return "batik-dev@xmlgraphics.apache.org";
    }
    
    @Override
    public String getURL() {
        return "http://xml.apache.org/batik";
    }
    
    @Override
    public String getDescription() {
        return "The required SVG 1.0 tags";
    }
    
    @Override
    public void registerTags(final BridgeContext ctx) {
        ctx.putBridge(new SVGAElementBridge());
        ctx.putBridge(new SVGAltGlyphElementBridge());
        ctx.putBridge(new SVGCircleElementBridge());
        ctx.putBridge(new SVGClipPathElementBridge());
        ctx.putBridge(new SVGColorProfileElementBridge());
        ctx.putBridge(new SVGDescElementBridge());
        ctx.putBridge(new SVGEllipseElementBridge());
        ctx.putBridge(new SVGFeBlendElementBridge());
        ctx.putBridge(new SVGFeColorMatrixElementBridge());
        ctx.putBridge(new SVGFeComponentTransferElementBridge());
        ctx.putBridge(new SVGFeCompositeElementBridge());
        ctx.putBridge(new SVGFeComponentTransferElementBridge.SVGFeFuncAElementBridge());
        ctx.putBridge(new SVGFeComponentTransferElementBridge.SVGFeFuncRElementBridge());
        ctx.putBridge(new SVGFeComponentTransferElementBridge.SVGFeFuncGElementBridge());
        ctx.putBridge(new SVGFeComponentTransferElementBridge.SVGFeFuncBElementBridge());
        ctx.putBridge(new SVGFeConvolveMatrixElementBridge());
        ctx.putBridge(new SVGFeDiffuseLightingElementBridge());
        ctx.putBridge(new SVGFeDisplacementMapElementBridge());
        ctx.putBridge(new AbstractSVGLightingElementBridge.SVGFeDistantLightElementBridge());
        ctx.putBridge(new SVGFeFloodElementBridge());
        ctx.putBridge(new SVGFeGaussianBlurElementBridge());
        ctx.putBridge(new SVGFeImageElementBridge());
        ctx.putBridge(new SVGFeMergeElementBridge());
        ctx.putBridge(new SVGFeMergeElementBridge.SVGFeMergeNodeElementBridge());
        ctx.putBridge(new SVGFeMorphologyElementBridge());
        ctx.putBridge(new SVGFeOffsetElementBridge());
        ctx.putBridge(new AbstractSVGLightingElementBridge.SVGFePointLightElementBridge());
        ctx.putBridge(new SVGFeSpecularLightingElementBridge());
        ctx.putBridge(new AbstractSVGLightingElementBridge.SVGFeSpotLightElementBridge());
        ctx.putBridge(new SVGFeTileElementBridge());
        ctx.putBridge(new SVGFeTurbulenceElementBridge());
        ctx.putBridge(new SVGFontElementBridge());
        ctx.putBridge(new SVGFontFaceElementBridge());
        ctx.putBridge(new SVGFilterElementBridge());
        ctx.putBridge(new SVGGElementBridge());
        ctx.putBridge(new SVGGlyphElementBridge());
        ctx.putBridge(new SVGHKernElementBridge());
        ctx.putBridge(new SVGImageElementBridge());
        ctx.putBridge(new SVGLineElementBridge());
        ctx.putBridge(new SVGLinearGradientElementBridge());
        ctx.putBridge(new SVGMarkerElementBridge());
        ctx.putBridge(new SVGMaskElementBridge());
        ctx.putBridge(new SVGMissingGlyphElementBridge());
        ctx.putBridge(new SVGPathElementBridge());
        ctx.putBridge(new SVGPatternElementBridge());
        ctx.putBridge(new SVGPolylineElementBridge());
        ctx.putBridge(new SVGPolygonElementBridge());
        ctx.putBridge(new SVGRadialGradientElementBridge());
        ctx.putBridge(new SVGRectElementBridge());
        ctx.putBridge(new AbstractSVGGradientElementBridge.SVGStopElementBridge());
        ctx.putBridge(new SVGSVGElementBridge());
        ctx.putBridge(new SVGSwitchElementBridge());
        ctx.putBridge(new SVGTextElementBridge());
        ctx.putBridge(new SVGTextPathElementBridge());
        ctx.putBridge(new SVGTitleElementBridge());
        ctx.putBridge(new SVGUseElementBridge());
        ctx.putBridge(new SVGVKernElementBridge());
        ctx.putBridge(new SVGSetElementBridge());
        ctx.putBridge(new SVGAnimateElementBridge());
        ctx.putBridge(new SVGAnimateColorElementBridge());
        ctx.putBridge(new SVGAnimateTransformElementBridge());
        ctx.putBridge(new SVGAnimateMotionElementBridge());
    }
    
    @Override
    public boolean isDynamicElement(final Element e) {
        final String ns = e.getNamespaceURI();
        if (!"http://www.w3.org/2000/svg".equals(ns)) {
            return false;
        }
        final String ln = e.getLocalName();
        return ln.equals("script") || ln.startsWith("animate") || ln.equals("set");
    }
}
