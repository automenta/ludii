// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import java.awt.image.RescaleOp;
import java.awt.Rectangle;
import java.awt.image.BufferedImageOp;

public class SVGRescaleOp extends AbstractSVGFilterConverter
{
    public SVGRescaleOp(final SVGGeneratorContext generatorContext) {
        super(generatorContext);
    }
    
    @Override
    public SVGFilterDescriptor toSVG(final BufferedImageOp filter, final Rectangle filterRect) {
        if (filter instanceof RescaleOp) {
            return this.toSVG((RescaleOp)filter);
        }
        return null;
    }
    
    public SVGFilterDescriptor toSVG(final RescaleOp rescaleOp) {
        SVGFilterDescriptor filterDesc = this.descMap.get(rescaleOp);
        final Document domFactory = this.generatorContext.domFactory;
        if (filterDesc == null) {
            final Element filterDef = domFactory.createElementNS("http://www.w3.org/2000/svg", "filter");
            final Element feComponentTransferDef = domFactory.createElementNS("http://www.w3.org/2000/svg", "feComponentTransfer");
            final float[] offsets = rescaleOp.getOffsets(null);
            final float[] scaleFactors = rescaleOp.getScaleFactors(null);
            if (offsets.length != scaleFactors.length) {
                throw new SVGGraphics2DRuntimeException("RescapeOp offsets and scaleFactor array length do not match");
            }
            if (offsets.length != 1 && offsets.length != 3 && offsets.length != 4) {
                throw new SVGGraphics2DRuntimeException("BufferedImage RescaleOp should have 1, 3 or 4 scale factors");
            }
            final Element feFuncR = domFactory.createElementNS("http://www.w3.org/2000/svg", "feFuncR");
            final Element feFuncG = domFactory.createElementNS("http://www.w3.org/2000/svg", "feFuncG");
            final Element feFuncB = domFactory.createElementNS("http://www.w3.org/2000/svg", "feFuncB");
            Element feFuncA = null;
            final String type = "linear";
            if (offsets.length == 1) {
                final String slope = this.doubleString(scaleFactors[0]);
                final String intercept = this.doubleString(offsets[0]);
                feFuncR.setAttributeNS(null, "type", type);
                feFuncG.setAttributeNS(null, "type", type);
                feFuncB.setAttributeNS(null, "type", type);
                feFuncR.setAttributeNS(null, "slope", slope);
                feFuncG.setAttributeNS(null, "slope", slope);
                feFuncB.setAttributeNS(null, "slope", slope);
                feFuncR.setAttributeNS(null, "intercept", intercept);
                feFuncG.setAttributeNS(null, "intercept", intercept);
                feFuncB.setAttributeNS(null, "intercept", intercept);
            }
            else if (offsets.length >= 3) {
                feFuncR.setAttributeNS(null, "type", type);
                feFuncG.setAttributeNS(null, "type", type);
                feFuncB.setAttributeNS(null, "type", type);
                feFuncR.setAttributeNS(null, "slope", this.doubleString(scaleFactors[0]));
                feFuncG.setAttributeNS(null, "slope", this.doubleString(scaleFactors[1]));
                feFuncB.setAttributeNS(null, "slope", this.doubleString(scaleFactors[2]));
                feFuncR.setAttributeNS(null, "intercept", this.doubleString(offsets[0]));
                feFuncG.setAttributeNS(null, "intercept", this.doubleString(offsets[1]));
                feFuncB.setAttributeNS(null, "intercept", this.doubleString(offsets[2]));
                if (offsets.length == 4) {
                    feFuncA = domFactory.createElementNS("http://www.w3.org/2000/svg", "feFuncA");
                    feFuncA.setAttributeNS(null, "type", type);
                    feFuncA.setAttributeNS(null, "slope", this.doubleString(scaleFactors[3]));
                    feFuncA.setAttributeNS(null, "intercept", this.doubleString(offsets[3]));
                }
            }
            feComponentTransferDef.appendChild(feFuncR);
            feComponentTransferDef.appendChild(feFuncG);
            feComponentTransferDef.appendChild(feFuncB);
            if (feFuncA != null) {
                feComponentTransferDef.appendChild(feFuncA);
            }
            filterDef.appendChild(feComponentTransferDef);
            filterDef.setAttributeNS(null, "id", this.generatorContext.idGenerator.generateID("componentTransfer"));
            final String filterAttrBuf = "url(#" + filterDef.getAttributeNS(null, "id") + ")";
            filterDesc = new SVGFilterDescriptor(filterAttrBuf, filterDef);
            this.defSet.add(filterDef);
            this.descMap.put(rescaleOp, filterDesc);
        }
        return filterDesc;
    }
}
