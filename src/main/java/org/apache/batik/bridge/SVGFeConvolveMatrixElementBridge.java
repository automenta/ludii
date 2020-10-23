// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import java.util.StringTokenizer;
import org.apache.batik.ext.awt.image.renderable.ConvolveMatrixRable;
import org.apache.batik.ext.awt.image.renderable.PadRable;
import java.awt.Point;
import java.awt.image.Kernel;
import org.apache.batik.ext.awt.image.renderable.ConvolveMatrixRable8Bit;
import org.apache.batik.ext.awt.image.renderable.PadRable8Bit;
import org.apache.batik.ext.awt.image.PadMode;
import java.util.Map;
import java.awt.geom.Rectangle2D;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;

public class SVGFeConvolveMatrixElementBridge extends AbstractSVGFilterPrimitiveElementBridge
{
    @Override
    public String getLocalName() {
        return "feConvolveMatrix";
    }
    
    @Override
    public Filter createFilter(final BridgeContext ctx, final Element filterElement, final Element filteredElement, final GraphicsNode filteredNode, final Filter inputFilter, final Rectangle2D filterRegion, final Map filterMap) {
        final int[] orderXY = convertOrder(filterElement, ctx);
        final float[] kernelMatrix = convertKernelMatrix(filterElement, orderXY, ctx);
        final float divisor = convertDivisor(filterElement, kernelMatrix, ctx);
        final float bias = AbstractSVGFilterPrimitiveElementBridge.convertNumber(filterElement, "bias", 0.0f, ctx);
        final int[] targetXY = convertTarget(filterElement, orderXY, ctx);
        final PadMode padMode = convertEdgeMode(filterElement, ctx);
        final double[] kernelUnitLength = convertKernelUnitLength(filterElement, ctx);
        final boolean preserveAlpha = convertPreserveAlpha(filterElement, ctx);
        final Filter in = AbstractSVGFilterPrimitiveElementBridge.getIn(filterElement, filteredElement, filteredNode, inputFilter, filterMap, ctx);
        if (in == null) {
            return null;
        }
        final Rectangle2D defaultRegion = in.getBounds2D();
        final Rectangle2D primitiveRegion = SVGUtilities.convertFilterPrimitiveRegion(filterElement, filteredElement, filteredNode, defaultRegion, filterRegion, ctx);
        final PadRable pad = new PadRable8Bit(in, primitiveRegion, PadMode.ZERO_PAD);
        final ConvolveMatrixRable convolve = new ConvolveMatrixRable8Bit(pad);
        for (int i = 0; i < kernelMatrix.length; ++i) {
            final float[] array = kernelMatrix;
            final int n = i;
            array[n] /= divisor;
        }
        convolve.setKernel(new Kernel(orderXY[0], orderXY[1], kernelMatrix));
        convolve.setTarget(new Point(targetXY[0], targetXY[1]));
        convolve.setBias(bias);
        convolve.setEdgeMode(padMode);
        convolve.setKernelUnitLength(kernelUnitLength);
        convolve.setPreserveAlpha(preserveAlpha);
        AbstractSVGFilterPrimitiveElementBridge.handleColorInterpolationFilters(convolve, filterElement);
        final PadRable filter = new PadRable8Bit(convolve, primitiveRegion, PadMode.ZERO_PAD);
        AbstractSVGFilterPrimitiveElementBridge.updateFilterMap(filterElement, filter, filterMap);
        return filter;
    }
    
    protected static int[] convertOrder(final Element filterElement, final BridgeContext ctx) {
        final String s = filterElement.getAttributeNS(null, "order");
        if (s.length() == 0) {
            return new int[] { 3, 3 };
        }
        final int[] orderXY = new int[2];
        final StringTokenizer tokens = new StringTokenizer(s, " ,");
        try {
            orderXY[0] = SVGUtilities.convertSVGInteger(tokens.nextToken());
            if (tokens.hasMoreTokens()) {
                orderXY[1] = SVGUtilities.convertSVGInteger(tokens.nextToken());
            }
            else {
                orderXY[1] = orderXY[0];
            }
        }
        catch (NumberFormatException nfEx) {
            throw new BridgeException(ctx, filterElement, nfEx, "attribute.malformed", new Object[] { "order", s, nfEx });
        }
        if (tokens.hasMoreTokens() || orderXY[0] <= 0 || orderXY[1] <= 0) {
            throw new BridgeException(ctx, filterElement, "attribute.malformed", new Object[] { "order", s });
        }
        return orderXY;
    }
    
    protected static float[] convertKernelMatrix(final Element filterElement, final int[] orderXY, final BridgeContext ctx) {
        final String s = filterElement.getAttributeNS(null, "kernelMatrix");
        if (s.length() == 0) {
            throw new BridgeException(ctx, filterElement, "attribute.missing", new Object[] { "kernelMatrix" });
        }
        final int size = orderXY[0] * orderXY[1];
        final float[] kernelMatrix = new float[size];
        final StringTokenizer tokens = new StringTokenizer(s, " ,");
        int i = 0;
        try {
            while (tokens.hasMoreTokens() && i < size) {
                kernelMatrix[i++] = SVGUtilities.convertSVGNumber(tokens.nextToken());
            }
        }
        catch (NumberFormatException nfEx) {
            throw new BridgeException(ctx, filterElement, nfEx, "attribute.malformed", new Object[] { "kernelMatrix", s, nfEx });
        }
        if (i != size) {
            throw new BridgeException(ctx, filterElement, "attribute.malformed", new Object[] { "kernelMatrix", s });
        }
        return kernelMatrix;
    }
    
    protected static float convertDivisor(final Element filterElement, final float[] kernelMatrix, final BridgeContext ctx) {
        final String s = filterElement.getAttributeNS(null, "divisor");
        if (s.length() == 0) {
            float sum = 0.0f;
            for (final float aKernelMatrix : kernelMatrix) {
                sum += aKernelMatrix;
            }
            return (sum == 0.0f) ? 1.0f : sum;
        }
        try {
            return SVGUtilities.convertSVGNumber(s);
        }
        catch (NumberFormatException nfEx) {
            throw new BridgeException(ctx, filterElement, nfEx, "attribute.malformed", new Object[] { "divisor", s, nfEx });
        }
    }
    
    protected static int[] convertTarget(final Element filterElement, final int[] orderXY, final BridgeContext ctx) {
        final int[] targetXY = new int[2];
        String s = filterElement.getAttributeNS(null, "targetX");
        if (s.length() == 0) {
            targetXY[0] = orderXY[0] / 2;
        }
        else {
            try {
                final int v = SVGUtilities.convertSVGInteger(s);
                if (v < 0 || v >= orderXY[0]) {
                    throw new BridgeException(ctx, filterElement, "attribute.malformed", new Object[] { "targetX", s });
                }
                targetXY[0] = v;
            }
            catch (NumberFormatException nfEx) {
                throw new BridgeException(ctx, filterElement, nfEx, "attribute.malformed", new Object[] { "targetX", s, nfEx });
            }
        }
        s = filterElement.getAttributeNS(null, "targetY");
        if (s.length() == 0) {
            targetXY[1] = orderXY[1] / 2;
        }
        else {
            try {
                final int v = SVGUtilities.convertSVGInteger(s);
                if (v < 0 || v >= orderXY[1]) {
                    throw new BridgeException(ctx, filterElement, "attribute.malformed", new Object[] { "targetY", s });
                }
                targetXY[1] = v;
            }
            catch (NumberFormatException nfEx) {
                throw new BridgeException(ctx, filterElement, nfEx, "attribute.malformed", new Object[] { "targetY", s, nfEx });
            }
        }
        return targetXY;
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
    
    protected static PadMode convertEdgeMode(final Element filterElement, final BridgeContext ctx) {
        final String s = filterElement.getAttributeNS(null, "edgeMode");
        if (s.length() == 0) {
            return PadMode.REPLICATE;
        }
        if ("duplicate".equals(s)) {
            return PadMode.REPLICATE;
        }
        if ("wrap".equals(s)) {
            return PadMode.WRAP;
        }
        if ("none".equals(s)) {
            return PadMode.ZERO_PAD;
        }
        throw new BridgeException(ctx, filterElement, "attribute.malformed", new Object[] { "edgeMode", s });
    }
    
    protected static boolean convertPreserveAlpha(final Element filterElement, final BridgeContext ctx) {
        final String s = filterElement.getAttributeNS(null, "preserveAlpha");
        if (s.length() == 0) {
            return false;
        }
        if ("true".equals(s)) {
            return true;
        }
        if ("false".equals(s)) {
            return false;
        }
        throw new BridgeException(ctx, filterElement, "attribute.malformed", new Object[] { "preserveAlpha", s });
    }
}
