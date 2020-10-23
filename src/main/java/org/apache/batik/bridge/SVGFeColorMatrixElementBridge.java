// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import java.util.StringTokenizer;
import org.apache.batik.ext.awt.image.renderable.ColorMatrixRable;
import org.apache.batik.ext.awt.image.renderable.PadRable8Bit;
import org.apache.batik.ext.awt.image.PadMode;
import org.apache.batik.ext.awt.image.renderable.ColorMatrixRable8Bit;
import java.util.Map;
import java.awt.geom.Rectangle2D;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;

public class SVGFeColorMatrixElementBridge extends AbstractSVGFilterPrimitiveElementBridge
{
    @Override
    public String getLocalName() {
        return "feColorMatrix";
    }
    
    @Override
    public Filter createFilter(final BridgeContext ctx, final Element filterElement, final Element filteredElement, final GraphicsNode filteredNode, final Filter inputFilter, final Rectangle2D filterRegion, final Map filterMap) {
        final Filter in = AbstractSVGFilterPrimitiveElementBridge.getIn(filterElement, filteredElement, filteredNode, inputFilter, filterMap, ctx);
        if (in == null) {
            return null;
        }
        final Rectangle2D defaultRegion = in.getBounds2D();
        final Rectangle2D primitiveRegion = SVGUtilities.convertFilterPrimitiveRegion(filterElement, filteredElement, filteredNode, defaultRegion, filterRegion, ctx);
        final int type = convertType(filterElement, ctx);
        ColorMatrixRable colorMatrix = null;
        switch (type) {
            case 2: {
                final float a = convertValuesToHueRotate(filterElement, ctx);
                colorMatrix = ColorMatrixRable8Bit.buildHueRotate(a);
                break;
            }
            case 3: {
                colorMatrix = ColorMatrixRable8Bit.buildLuminanceToAlpha();
                break;
            }
            case 0: {
                final float[][] matrix = convertValuesToMatrix(filterElement, ctx);
                colorMatrix = ColorMatrixRable8Bit.buildMatrix(matrix);
                break;
            }
            case 1: {
                final float s = convertValuesToSaturate(filterElement, ctx);
                colorMatrix = ColorMatrixRable8Bit.buildSaturate(s);
                break;
            }
            default: {
                throw new RuntimeException("invalid convertType:" + type);
            }
        }
        colorMatrix.setSource(in);
        AbstractSVGFilterPrimitiveElementBridge.handleColorInterpolationFilters(colorMatrix, filterElement);
        final Filter filter = new PadRable8Bit(colorMatrix, primitiveRegion, PadMode.ZERO_PAD);
        AbstractSVGFilterPrimitiveElementBridge.updateFilterMap(filterElement, filter, filterMap);
        return filter;
    }
    
    protected static float[][] convertValuesToMatrix(final Element filterElement, final BridgeContext ctx) {
        final String s = filterElement.getAttributeNS(null, "values");
        final float[][] matrix = new float[4][5];
        if (s.length() == 0) {
            matrix[0][0] = 1.0f;
            matrix[1][1] = 1.0f;
            matrix[2][2] = 1.0f;
            matrix[3][3] = 1.0f;
            return matrix;
        }
        final StringTokenizer tokens = new StringTokenizer(s, " ,");
        int n = 0;
        try {
            while (n < 20 && tokens.hasMoreTokens()) {
                matrix[n / 5][n % 5] = SVGUtilities.convertSVGNumber(tokens.nextToken());
                ++n;
            }
        }
        catch (NumberFormatException nfEx) {
            throw new BridgeException(ctx, filterElement, nfEx, "attribute.malformed", new Object[] { "values", s, nfEx });
        }
        if (n != 20 || tokens.hasMoreTokens()) {
            throw new BridgeException(ctx, filterElement, "attribute.malformed", new Object[] { "values", s });
        }
        for (int i = 0; i < 4; ++i) {
            final float[] array = matrix[i];
            final int n2 = 4;
            array[n2] *= 255.0f;
        }
        return matrix;
    }
    
    protected static float convertValuesToSaturate(final Element filterElement, final BridgeContext ctx) {
        final String s = filterElement.getAttributeNS(null, "values");
        if (s.length() == 0) {
            return 1.0f;
        }
        try {
            return SVGUtilities.convertSVGNumber(s);
        }
        catch (NumberFormatException nfEx) {
            throw new BridgeException(ctx, filterElement, nfEx, "attribute.malformed", new Object[] { "values", s });
        }
    }
    
    protected static float convertValuesToHueRotate(final Element filterElement, final BridgeContext ctx) {
        final String s = filterElement.getAttributeNS(null, "values");
        if (s.length() == 0) {
            return 0.0f;
        }
        try {
            return (float)Math.toRadians(SVGUtilities.convertSVGNumber(s));
        }
        catch (NumberFormatException nfEx) {
            throw new BridgeException(ctx, filterElement, nfEx, "attribute.malformed", new Object[] { "values", s });
        }
    }
    
    protected static int convertType(final Element filterElement, final BridgeContext ctx) {
        final String s = filterElement.getAttributeNS(null, "type");
        if (s.length() == 0) {
            return 0;
        }
        if ("hueRotate".equals(s)) {
            return 2;
        }
        if ("luminanceToAlpha".equals(s)) {
            return 3;
        }
        if ("matrix".equals(s)) {
            return 0;
        }
        if ("saturate".equals(s)) {
            return 1;
        }
        throw new BridgeException(ctx, filterElement, "attribute.malformed", new Object[] { "type", s });
    }
}
