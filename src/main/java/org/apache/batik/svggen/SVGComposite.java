// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import java.awt.AlphaComposite;
import java.awt.Composite;
import org.apache.batik.ext.awt.g2d.GraphicContext;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class SVGComposite implements SVGConverter
{
    private SVGAlphaComposite svgAlphaComposite;
    private SVGCustomComposite svgCustomComposite;
    
    public SVGComposite(final SVGGeneratorContext generatorContext) {
        this.svgAlphaComposite = new SVGAlphaComposite(generatorContext);
        this.svgCustomComposite = new SVGCustomComposite(generatorContext);
    }
    
    @Override
    public List getDefinitionSet() {
        final List compositeDefs = new LinkedList(this.svgAlphaComposite.getDefinitionSet());
        compositeDefs.addAll(this.svgCustomComposite.getDefinitionSet());
        return compositeDefs;
    }
    
    public SVGAlphaComposite getAlphaCompositeConverter() {
        return this.svgAlphaComposite;
    }
    
    public SVGCustomComposite getCustomCompositeConverter() {
        return this.svgCustomComposite;
    }
    
    @Override
    public SVGDescriptor toSVG(final GraphicContext gc) {
        return this.toSVG(gc.getComposite());
    }
    
    public SVGCompositeDescriptor toSVG(final Composite composite) {
        if (composite instanceof AlphaComposite) {
            return this.svgAlphaComposite.toSVG((AlphaComposite)composite);
        }
        return this.svgCustomComposite.toSVG(composite);
    }
}
