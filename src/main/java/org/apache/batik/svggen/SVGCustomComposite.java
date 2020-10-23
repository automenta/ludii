// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import org.w3c.dom.Element;
import java.awt.Composite;
import org.apache.batik.ext.awt.g2d.GraphicContext;

public class SVGCustomComposite extends AbstractSVGConverter
{
    public SVGCustomComposite(final SVGGeneratorContext generatorContext) {
        super(generatorContext);
    }
    
    @Override
    public SVGDescriptor toSVG(final GraphicContext gc) {
        return this.toSVG(gc.getComposite());
    }
    
    public SVGCompositeDescriptor toSVG(final Composite composite) {
        if (composite == null) {
            throw new NullPointerException();
        }
        final SVGCompositeDescriptor compositeDesc = this.descMap.get(composite);
        if (compositeDesc == null) {
            final SVGCompositeDescriptor desc = this.generatorContext.extensionHandler.handleComposite(composite, this.generatorContext);
            if (desc != null) {
                final Element def = desc.getDef();
                if (def != null) {
                    this.defSet.add(def);
                }
                this.descMap.put(composite, desc);
            }
        }
        return compositeDesc;
    }
}
