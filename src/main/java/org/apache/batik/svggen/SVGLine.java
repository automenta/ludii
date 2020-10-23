// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import org.w3c.dom.Element;
import java.awt.geom.Line2D;

public class SVGLine extends SVGGraphicObjectConverter
{
    public SVGLine(final SVGGeneratorContext generatorContext) {
        super(generatorContext);
    }
    
    public Element toSVG(final Line2D line) {
        final Element svgLine = this.generatorContext.domFactory.createElementNS("http://www.w3.org/2000/svg", "line");
        svgLine.setAttributeNS(null, "x1", this.doubleString(line.getX1()));
        svgLine.setAttributeNS(null, "y1", this.doubleString(line.getY1()));
        svgLine.setAttributeNS(null, "x2", this.doubleString(line.getX2()));
        svgLine.setAttributeNS(null, "y2", this.doubleString(line.getY2()));
        return svgLine;
    }
}
