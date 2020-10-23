// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import java.util.LinkedList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractSVGFilterConverter implements SVGFilterConverter, ErrorConstants
{
    protected SVGGeneratorContext generatorContext;
    protected Map descMap;
    protected List defSet;
    
    public AbstractSVGFilterConverter(final SVGGeneratorContext generatorContext) {
        this.descMap = new HashMap();
        this.defSet = new LinkedList();
        if (generatorContext == null) {
            throw new SVGGraphics2DRuntimeException("generatorContext should not be null");
        }
        this.generatorContext = generatorContext;
    }
    
    @Override
    public List getDefinitionSet() {
        return this.defSet;
    }
    
    public final String doubleString(final double value) {
        return this.generatorContext.doubleString(value);
    }
}
