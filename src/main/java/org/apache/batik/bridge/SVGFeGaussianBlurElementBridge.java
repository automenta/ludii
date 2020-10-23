// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import java.util.StringTokenizer;
import org.apache.batik.ext.awt.image.renderable.PadRable;
import org.apache.batik.ext.awt.image.renderable.GaussianBlurRable8Bit;
import org.apache.batik.ext.awt.image.renderable.PadRable8Bit;
import org.apache.batik.ext.awt.image.PadMode;
import java.util.Map;
import java.awt.geom.Rectangle2D;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;

public class SVGFeGaussianBlurElementBridge extends AbstractSVGFilterPrimitiveElementBridge
{
    @Override
    public String getLocalName() {
        return "feGaussianBlur";
    }
    
    @Override
    public Filter createFilter(final BridgeContext ctx, final Element filterElement, final Element filteredElement, final GraphicsNode filteredNode, final Filter inputFilter, final Rectangle2D filterRegion, final Map filterMap) {
        final float[] stdDeviationXY = convertStdDeviation(filterElement, ctx);
        if (stdDeviationXY[0] < 0.0f || stdDeviationXY[1] < 0.0f) {
            throw new BridgeException(ctx, filterElement, "attribute.malformed", new Object[] { "stdDeviation", String.valueOf(stdDeviationXY[0]) + stdDeviationXY[1] });
        }
        final Filter in = AbstractSVGFilterPrimitiveElementBridge.getIn(filterElement, filteredElement, filteredNode, inputFilter, filterMap, ctx);
        if (in == null) {
            return null;
        }
        final Rectangle2D defaultRegion = in.getBounds2D();
        final Rectangle2D primitiveRegion = SVGUtilities.convertFilterPrimitiveRegion(filterElement, filteredElement, filteredNode, defaultRegion, filterRegion, ctx);
        final PadRable pad = new PadRable8Bit(in, primitiveRegion, PadMode.ZERO_PAD);
        final Filter blur = new GaussianBlurRable8Bit(pad, stdDeviationXY[0], stdDeviationXY[1]);
        AbstractSVGFilterPrimitiveElementBridge.handleColorInterpolationFilters(blur, filterElement);
        final PadRable filter = new PadRable8Bit(blur, primitiveRegion, PadMode.ZERO_PAD);
        AbstractSVGFilterPrimitiveElementBridge.updateFilterMap(filterElement, filter, filterMap);
        return filter;
    }
    
    protected static float[] convertStdDeviation(final Element filterElement, final BridgeContext ctx) {
        final String s = filterElement.getAttributeNS(null, "stdDeviation");
        if (s.length() == 0) {
            return new float[] { 0.0f, 0.0f };
        }
        final float[] stdDevs = new float[2];
        final StringTokenizer tokens = new StringTokenizer(s, " ,");
        try {
            stdDevs[0] = SVGUtilities.convertSVGNumber(tokens.nextToken());
            if (tokens.hasMoreTokens()) {
                stdDevs[1] = SVGUtilities.convertSVGNumber(tokens.nextToken());
            }
            else {
                stdDevs[1] = stdDevs[0];
            }
        }
        catch (NumberFormatException nfEx) {
            throw new BridgeException(ctx, filterElement, nfEx, "attribute.malformed", new Object[] { "stdDeviation", s, nfEx });
        }
        if (tokens.hasMoreTokens()) {
            throw new BridgeException(ctx, filterElement, "attribute.malformed", new Object[] { "stdDeviation", s });
        }
        return stdDevs;
    }
}
