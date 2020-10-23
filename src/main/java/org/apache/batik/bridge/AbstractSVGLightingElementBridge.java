// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.apache.batik.ext.awt.image.PointLight;
import org.apache.batik.ext.awt.image.DistantLight;
import org.apache.batik.ext.awt.image.SpotLight;
import java.util.StringTokenizer;
import org.w3c.dom.Node;
import java.awt.Color;
import org.apache.batik.ext.awt.image.Light;
import org.w3c.dom.Element;

public abstract class AbstractSVGLightingElementBridge extends AbstractSVGFilterPrimitiveElementBridge
{
    protected AbstractSVGLightingElementBridge() {
    }
    
    protected static Light extractLight(final Element filterElement, final BridgeContext ctx) {
        final Color color = CSSUtilities.convertLightingColor(filterElement, ctx);
        for (Node n = filterElement.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() == 1) {
                final Element e = (Element)n;
                final Bridge bridge = ctx.getBridge(e);
                if (bridge != null) {
                    if (bridge instanceof AbstractSVGLightElementBridge) {
                        return ((AbstractSVGLightElementBridge)bridge).createLight(ctx, filterElement, e, color);
                    }
                }
            }
        }
        return null;
    }
    
    protected static double[] convertKernelUnitLength(final Element filterElement, final BridgeContext ctx) {
        final String s = filterElement.getAttributeNS(null, "kernelUnitLength");
        if (s.length() == 0) {
            return null;
        }
        final double[] units = new double[2];
        final StringTokenizer tokens = new StringTokenizer(s, " ,");
        try {
            units[0] = SVGUtilities.convertSVGNumber(tokens.nextToken());
            if (tokens.hasMoreTokens()) {
                units[1] = SVGUtilities.convertSVGNumber(tokens.nextToken());
            }
            else {
                units[1] = units[0];
            }
        }
        catch (NumberFormatException nfEx) {
            throw new BridgeException(ctx, filterElement, nfEx, "attribute.malformed", new Object[] { "kernelUnitLength", s });
        }
        if (tokens.hasMoreTokens() || units[0] <= 0.0 || units[1] <= 0.0) {
            throw new BridgeException(ctx, filterElement, "attribute.malformed", new Object[] { "kernelUnitLength", s });
        }
        return units;
    }
    
    protected abstract static class AbstractSVGLightElementBridge extends AnimatableGenericSVGBridge
    {
        public abstract Light createLight(final BridgeContext p0, final Element p1, final Element p2, final Color p3);
    }
    
    public static class SVGFeSpotLightElementBridge extends AbstractSVGLightElementBridge
    {
        @Override
        public String getLocalName() {
            return "feSpotLight";
        }
        
        @Override
        public Light createLight(final BridgeContext ctx, final Element filterElement, final Element lightElement, final Color color) {
            final double x = AbstractSVGFilterPrimitiveElementBridge.convertNumber(lightElement, "x", 0.0f, ctx);
            final double y = AbstractSVGFilterPrimitiveElementBridge.convertNumber(lightElement, "y", 0.0f, ctx);
            final double z = AbstractSVGFilterPrimitiveElementBridge.convertNumber(lightElement, "z", 0.0f, ctx);
            final double px = AbstractSVGFilterPrimitiveElementBridge.convertNumber(lightElement, "pointsAtX", 0.0f, ctx);
            final double py = AbstractSVGFilterPrimitiveElementBridge.convertNumber(lightElement, "pointsAtY", 0.0f, ctx);
            final double pz = AbstractSVGFilterPrimitiveElementBridge.convertNumber(lightElement, "pointsAtZ", 0.0f, ctx);
            final double specularExponent = AbstractSVGFilterPrimitiveElementBridge.convertNumber(lightElement, "specularExponent", 1.0f, ctx);
            final double limitingConeAngle = AbstractSVGFilterPrimitiveElementBridge.convertNumber(lightElement, "limitingConeAngle", 90.0f, ctx);
            return new SpotLight(x, y, z, px, py, pz, specularExponent, limitingConeAngle, color);
        }
    }
    
    public static class SVGFeDistantLightElementBridge extends AbstractSVGLightElementBridge
    {
        @Override
        public String getLocalName() {
            return "feDistantLight";
        }
        
        @Override
        public Light createLight(final BridgeContext ctx, final Element filterElement, final Element lightElement, final Color color) {
            final double azimuth = AbstractSVGFilterPrimitiveElementBridge.convertNumber(lightElement, "azimuth", 0.0f, ctx);
            final double elevation = AbstractSVGFilterPrimitiveElementBridge.convertNumber(lightElement, "elevation", 0.0f, ctx);
            return new DistantLight(azimuth, elevation, color);
        }
    }
    
    public static class SVGFePointLightElementBridge extends AbstractSVGLightElementBridge
    {
        @Override
        public String getLocalName() {
            return "fePointLight";
        }
        
        @Override
        public Light createLight(final BridgeContext ctx, final Element filterElement, final Element lightElement, final Color color) {
            final double x = AbstractSVGFilterPrimitiveElementBridge.convertNumber(lightElement, "x", 0.0f, ctx);
            final double y = AbstractSVGFilterPrimitiveElementBridge.convertNumber(lightElement, "y", 0.0f, ctx);
            final double z = AbstractSVGFilterPrimitiveElementBridge.convertNumber(lightElement, "z", 0.0f, ctx);
            return new PointLight(x, y, z, color);
        }
    }
}
