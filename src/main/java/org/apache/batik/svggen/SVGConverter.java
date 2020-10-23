// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import java.util.List;
import org.apache.batik.ext.awt.g2d.GraphicContext;

public interface SVGConverter extends SVGSyntax
{
    SVGDescriptor toSVG(final GraphicContext p0);
    
    List getDefinitionSet();
}
