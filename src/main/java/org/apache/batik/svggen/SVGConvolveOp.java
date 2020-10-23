// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import org.w3c.dom.Element;
import java.awt.image.Kernel;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import java.awt.image.ConvolveOp;
import java.awt.Rectangle;
import java.awt.image.BufferedImageOp;

public class SVGConvolveOp extends AbstractSVGFilterConverter
{
    public SVGConvolveOp(final SVGGeneratorContext generatorContext) {
        super(generatorContext);
    }
    
    @Override
    public SVGFilterDescriptor toSVG(final BufferedImageOp filter, final Rectangle filterRect) {
        if (filter instanceof ConvolveOp) {
            return this.toSVG((ConvolveOp)filter);
        }
        return null;
    }
    
    public SVGFilterDescriptor toSVG(final ConvolveOp convolveOp) {
        SVGFilterDescriptor filterDesc = this.descMap.get(convolveOp);
        final Document domFactory = this.generatorContext.domFactory;
        if (filterDesc == null) {
            final Kernel kernel = convolveOp.getKernel();
            final Element filterDef = domFactory.createElementNS("http://www.w3.org/2000/svg", "filter");
            final Element feConvolveMatrixDef = domFactory.createElementNS("http://www.w3.org/2000/svg", "feConvolveMatrix");
            feConvolveMatrixDef.setAttributeNS(null, "order", kernel.getWidth() + " " + kernel.getHeight());
            final float[] data = kernel.getKernelData(null);
            final StringBuffer kernelMatrixBuf = new StringBuffer(data.length * 8);
            for (final float aData : data) {
                kernelMatrixBuf.append(this.doubleString(aData));
                kernelMatrixBuf.append(" ");
            }
            feConvolveMatrixDef.setAttributeNS(null, "kernelMatrix", kernelMatrixBuf.toString().trim());
            filterDef.appendChild(feConvolveMatrixDef);
            filterDef.setAttributeNS(null, "id", this.generatorContext.idGenerator.generateID("convolve"));
            if (convolveOp.getEdgeCondition() == 1) {
                feConvolveMatrixDef.setAttributeNS(null, "edgeMode", "duplicate");
            }
            else {
                feConvolveMatrixDef.setAttributeNS(null, "edgeMode", "none");
            }
            final StringBuffer filterAttrBuf = new StringBuffer("url(");
            filterAttrBuf.append("#");
            filterAttrBuf.append(filterDef.getAttributeNS(null, "id"));
            filterAttrBuf.append(")");
            filterDesc = new SVGFilterDescriptor(filterAttrBuf.toString(), filterDef);
            this.defSet.add(filterDef);
            this.descMap.put(convolveOp, filterDesc);
        }
        return filterDesc;
    }
}
