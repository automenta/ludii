// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.apache.batik.ext.awt.image.Light;
import org.apache.batik.ext.awt.image.renderable.SpecularLightingRable8Bit;
import java.util.Map;
import java.awt.geom.Rectangle2D;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;

public class SVGFeSpecularLightingElementBridge extends AbstractSVGLightingElementBridge
{
    @Override
    public String getLocalName() {
        return "feSpecularLighting";
    }
    
    @Override
    public Filter createFilter(final BridgeContext ctx, final Element filterElement, final Element filteredElement, final GraphicsNode filteredNode, final Filter inputFilter, final Rectangle2D filterRegion, final Map filterMap) {
        final float surfaceScale = AbstractSVGFilterPrimitiveElementBridge.convertNumber(filterElement, "surfaceScale", 1.0f, ctx);
        final float specularConstant = AbstractSVGFilterPrimitiveElementBridge.convertNumber(filterElement, "specularConstant", 1.0f, ctx);
        final float specularExponent = convertSpecularExponent(filterElement, ctx);
        final Light light = AbstractSVGLightingElementBridge.extractLight(filterElement, ctx);
        final double[] kernelUnitLength = AbstractSVGLightingElementBridge.convertKernelUnitLength(filterElement, ctx);
        final Filter in = AbstractSVGFilterPrimitiveElementBridge.getIn(filterElement, filteredElement, filteredNode, inputFilter, filterMap, ctx);
        if (in == null) {
            return null;
        }
        final Rectangle2D defaultRegion = in.getBounds2D();
        final Rectangle2D primitiveRegion = SVGUtilities.convertFilterPrimitiveRegion(filterElement, filteredElement, filteredNode, defaultRegion, filterRegion, ctx);
        final Filter filter = new SpecularLightingRable8Bit(in, primitiveRegion, light, specularConstant, specularExponent, surfaceScale, kernelUnitLength);
        AbstractSVGFilterPrimitiveElementBridge.handleColorInterpolationFilters(filter, filterElement);
        AbstractSVGFilterPrimitiveElementBridge.updateFilterMap(filterElement, filter, filterMap);
        return filter;
    }
    
    protected static float convertSpecularExponent(final Element filterElement, final BridgeContext ctx) {
        final String s = filterElement.getAttributeNS(null, "specularExponent");
        if (s.length() == 0) {
            return 1.0f;
        }
        try {
            final float v = SVGUtilities.convertSVGNumber(s);
            if (v < 1.0f || v > 128.0f) {
                throw new BridgeException(ctx, filterElement, "attribute.malformed", new Object[] { "specularConstant", s });
            }
            return v;
        }
        catch (NumberFormatException nfEx) {
            throw new BridgeException(ctx, filterElement, nfEx, "attribute.malformed", new Object[] { "specularConstant", s, nfEx });
        }
    }
}
