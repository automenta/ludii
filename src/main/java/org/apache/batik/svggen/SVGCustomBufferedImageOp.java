// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import org.w3c.dom.Element;
import java.awt.Rectangle;
import java.awt.image.BufferedImageOp;

public class SVGCustomBufferedImageOp extends AbstractSVGFilterConverter
{
    private static final String ERROR_EXTENSION = "SVGCustomBufferedImageOp:: ExtensionHandler could not convert filter";
    
    public SVGCustomBufferedImageOp(final SVGGeneratorContext generatorContext) {
        super(generatorContext);
    }
    
    @Override
    public SVGFilterDescriptor toSVG(final BufferedImageOp filter, final Rectangle filterRect) {
        SVGFilterDescriptor filterDesc = this.descMap.get(filter);
        if (filterDesc == null) {
            filterDesc = this.generatorContext.extensionHandler.handleFilter(filter, filterRect, this.generatorContext);
            if (filterDesc != null) {
                final Element def = filterDesc.getDef();
                if (def != null) {
                    this.defSet.add(def);
                }
                this.descMap.put(filter, filterDesc);
            }
            else {
                System.err.println("SVGCustomBufferedImageOp:: ExtensionHandler could not convert filter");
            }
        }
        return filterDesc;
    }
}
