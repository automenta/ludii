// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import org.w3c.dom.Element;
import java.awt.Paint;
import org.apache.batik.ext.awt.g2d.GraphicContext;

public class SVGCustomPaint extends AbstractSVGConverter
{
    public SVGCustomPaint(final SVGGeneratorContext generatorContext) {
        super(generatorContext);
    }
    
    @Override
    public SVGDescriptor toSVG(final GraphicContext gc) {
        return this.toSVG(gc.getPaint());
    }
    
    public SVGPaintDescriptor toSVG(final Paint paint) {
        SVGPaintDescriptor paintDesc = this.descMap.get(paint);
        if (paintDesc == null) {
            paintDesc = this.generatorContext.extensionHandler.handlePaint(paint, this.generatorContext);
            if (paintDesc != null) {
                final Element def = paintDesc.getDef();
                if (def != null) {
                    this.defSet.add(def);
                }
                this.descMap.put(paint, paintDesc);
            }
        }
        return paintDesc;
    }
}
