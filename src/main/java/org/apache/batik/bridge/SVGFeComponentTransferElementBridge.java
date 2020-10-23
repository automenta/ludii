// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import java.util.StringTokenizer;
import org.apache.batik.ext.awt.image.ConcreteComponentTransferFunction;
import org.w3c.dom.Node;
import org.apache.batik.ext.awt.image.ComponentTransferFunction;
import org.apache.batik.ext.awt.image.renderable.PadRable8Bit;
import org.apache.batik.ext.awt.image.PadMode;
import org.apache.batik.ext.awt.image.renderable.ComponentTransferRable8Bit;
import java.util.Map;
import java.awt.geom.Rectangle2D;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;

public class SVGFeComponentTransferElementBridge extends AbstractSVGFilterPrimitiveElementBridge
{
    @Override
    public String getLocalName() {
        return "feComponentTransfer";
    }
    
    @Override
    public Filter createFilter(final BridgeContext ctx, final Element filterElement, final Element filteredElement, final GraphicsNode filteredNode, final Filter inputFilter, final Rectangle2D filterRegion, final Map filterMap) {
        final Filter in = AbstractSVGFilterPrimitiveElementBridge.getIn(filterElement, filteredElement, filteredNode, inputFilter, filterMap, ctx);
        if (in == null) {
            return null;
        }
        final Rectangle2D defaultRegion = in.getBounds2D();
        final Rectangle2D primitiveRegion = SVGUtilities.convertFilterPrimitiveRegion(filterElement, filteredElement, filteredNode, defaultRegion, filterRegion, ctx);
        ComponentTransferFunction funcR = null;
        ComponentTransferFunction funcG = null;
        ComponentTransferFunction funcB = null;
        ComponentTransferFunction funcA = null;
        for (Node n = filterElement.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() == 1) {
                final Element e = (Element)n;
                final Bridge bridge = ctx.getBridge(e);
                if (bridge != null) {
                    if (bridge instanceof SVGFeFuncElementBridge) {
                        final SVGFeFuncElementBridge funcBridge = (SVGFeFuncElementBridge)bridge;
                        final ComponentTransferFunction func = funcBridge.createComponentTransferFunction(filterElement, e);
                        if (funcBridge instanceof SVGFeFuncRElementBridge) {
                            funcR = func;
                        }
                        else if (funcBridge instanceof SVGFeFuncGElementBridge) {
                            funcG = func;
                        }
                        else if (funcBridge instanceof SVGFeFuncBElementBridge) {
                            funcB = func;
                        }
                        else if (funcBridge instanceof SVGFeFuncAElementBridge) {
                            funcA = func;
                        }
                    }
                }
            }
        }
        Filter filter = new ComponentTransferRable8Bit(in, funcA, funcR, funcG, funcB);
        AbstractSVGFilterPrimitiveElementBridge.handleColorInterpolationFilters(filter, filterElement);
        filter = new PadRable8Bit(filter, primitiveRegion, PadMode.ZERO_PAD);
        AbstractSVGFilterPrimitiveElementBridge.updateFilterMap(filterElement, filter, filterMap);
        return filter;
    }
    
    public static class SVGFeFuncAElementBridge extends SVGFeFuncElementBridge
    {
        @Override
        public String getLocalName() {
            return "feFuncA";
        }
    }
    
    public static class SVGFeFuncRElementBridge extends SVGFeFuncElementBridge
    {
        @Override
        public String getLocalName() {
            return "feFuncR";
        }
    }
    
    public static class SVGFeFuncGElementBridge extends SVGFeFuncElementBridge
    {
        @Override
        public String getLocalName() {
            return "feFuncG";
        }
    }
    
    public static class SVGFeFuncBElementBridge extends SVGFeFuncElementBridge
    {
        @Override
        public String getLocalName() {
            return "feFuncB";
        }
    }
    
    protected abstract static class SVGFeFuncElementBridge extends AnimatableGenericSVGBridge
    {
        public ComponentTransferFunction createComponentTransferFunction(final Element filterElement, final Element funcElement) {
            final int type = convertType(funcElement, this.ctx);
            switch (type) {
                case 2: {
                    final float[] v = convertTableValues(funcElement, this.ctx);
                    if (v == null) {
                        return ConcreteComponentTransferFunction.getIdentityTransfer();
                    }
                    return ConcreteComponentTransferFunction.getDiscreteTransfer(v);
                }
                case 0: {
                    return ConcreteComponentTransferFunction.getIdentityTransfer();
                }
                case 4: {
                    final float amplitude = AbstractSVGFilterPrimitiveElementBridge.convertNumber(funcElement, "amplitude", 1.0f, this.ctx);
                    final float exponent = AbstractSVGFilterPrimitiveElementBridge.convertNumber(funcElement, "exponent", 1.0f, this.ctx);
                    final float offset = AbstractSVGFilterPrimitiveElementBridge.convertNumber(funcElement, "offset", 0.0f, this.ctx);
                    return ConcreteComponentTransferFunction.getGammaTransfer(amplitude, exponent, offset);
                }
                case 3: {
                    final float slope = AbstractSVGFilterPrimitiveElementBridge.convertNumber(funcElement, "slope", 1.0f, this.ctx);
                    final float intercept = AbstractSVGFilterPrimitiveElementBridge.convertNumber(funcElement, "intercept", 0.0f, this.ctx);
                    return ConcreteComponentTransferFunction.getLinearTransfer(slope, intercept);
                }
                case 1: {
                    final float[] v = convertTableValues(funcElement, this.ctx);
                    if (v == null) {
                        return ConcreteComponentTransferFunction.getIdentityTransfer();
                    }
                    return ConcreteComponentTransferFunction.getTableTransfer(v);
                }
                default: {
                    throw new RuntimeException("invalid convertType:" + type);
                }
            }
        }
        
        protected static float[] convertTableValues(final Element e, final BridgeContext ctx) {
            final String s = e.getAttributeNS(null, "tableValues");
            if (s.length() == 0) {
                return null;
            }
            final StringTokenizer tokens = new StringTokenizer(s, " ,");
            final float[] v = new float[tokens.countTokens()];
            try {
                int i = 0;
                while (tokens.hasMoreTokens()) {
                    v[i] = SVGUtilities.convertSVGNumber(tokens.nextToken());
                    ++i;
                }
            }
            catch (NumberFormatException nfEx) {
                throw new BridgeException(ctx, e, nfEx, "attribute.malformed", new Object[] { "tableValues", s });
            }
            return v;
        }
        
        protected static int convertType(final Element e, final BridgeContext ctx) {
            final String s = e.getAttributeNS(null, "type");
            if (s.length() == 0) {
                throw new BridgeException(ctx, e, "attribute.missing", new Object[] { "type" });
            }
            if ("discrete".equals(s)) {
                return 2;
            }
            if ("identity".equals(s)) {
                return 0;
            }
            if ("gamma".equals(s)) {
                return 4;
            }
            if ("linear".equals(s)) {
                return 3;
            }
            if ("table".equals(s)) {
                return 1;
            }
            throw new BridgeException(ctx, e, "attribute.malformed", new Object[] { "type", s });
        }
    }
}
