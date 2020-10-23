// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import java.awt.image.ConvolveOp;
import java.awt.image.RescaleOp;
import java.awt.image.LookupOp;
import java.awt.Rectangle;
import java.awt.image.BufferedImageOp;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class SVGBufferedImageOp extends AbstractSVGFilterConverter
{
    private SVGLookupOp svgLookupOp;
    private SVGRescaleOp svgRescaleOp;
    private SVGConvolveOp svgConvolveOp;
    private SVGCustomBufferedImageOp svgCustomBufferedImageOp;
    
    public SVGBufferedImageOp(final SVGGeneratorContext generatorContext) {
        super(generatorContext);
        this.svgLookupOp = new SVGLookupOp(generatorContext);
        this.svgRescaleOp = new SVGRescaleOp(generatorContext);
        this.svgConvolveOp = new SVGConvolveOp(generatorContext);
        this.svgCustomBufferedImageOp = new SVGCustomBufferedImageOp(generatorContext);
    }
    
    @Override
    public List getDefinitionSet() {
        final List filterSet = new LinkedList(this.svgLookupOp.getDefinitionSet());
        filterSet.addAll(this.svgRescaleOp.getDefinitionSet());
        filterSet.addAll(this.svgConvolveOp.getDefinitionSet());
        filterSet.addAll(this.svgCustomBufferedImageOp.getDefinitionSet());
        return filterSet;
    }
    
    public SVGLookupOp getLookupOpConverter() {
        return this.svgLookupOp;
    }
    
    public SVGRescaleOp getRescaleOpConverter() {
        return this.svgRescaleOp;
    }
    
    public SVGConvolveOp getConvolveOpConverter() {
        return this.svgConvolveOp;
    }
    
    public SVGCustomBufferedImageOp getCustomBufferedImageOpConverter() {
        return this.svgCustomBufferedImageOp;
    }
    
    @Override
    public SVGFilterDescriptor toSVG(final BufferedImageOp op, final Rectangle filterRect) {
        SVGFilterDescriptor filterDesc = this.svgCustomBufferedImageOp.toSVG(op, filterRect);
        if (filterDesc == null) {
            if (op instanceof LookupOp) {
                filterDesc = this.svgLookupOp.toSVG(op, filterRect);
            }
            else if (op instanceof RescaleOp) {
                filterDesc = this.svgRescaleOp.toSVG(op, filterRect);
            }
            else if (op instanceof ConvolveOp) {
                filterDesc = this.svgConvolveOp.toSVG(op, filterRect);
            }
        }
        return filterDesc;
    }
}
