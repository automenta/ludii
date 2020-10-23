// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.RectangularShape;
import org.w3c.dom.Element;
import java.awt.geom.Rectangle2D;

public class SVGRectangle extends SVGGraphicObjectConverter
{
    private SVGLine svgLine;
    
    public SVGRectangle(final SVGGeneratorContext generatorContext) {
        super(generatorContext);
        this.svgLine = new SVGLine(generatorContext);
    }
    
    public Element toSVG(final Rectangle2D rect) {
        return this.toSVG((RectangularShape)rect);
    }
    
    public Element toSVG(final RoundRectangle2D rect) {
        final Element svgRect = this.toSVG((RectangularShape)rect);
        if (svgRect != null && svgRect.getTagName() == "rect") {
            svgRect.setAttributeNS(null, "rx", this.doubleString(Math.abs(rect.getArcWidth() / 2.0)));
            svgRect.setAttributeNS(null, "ry", this.doubleString(Math.abs(rect.getArcHeight() / 2.0)));
        }
        return svgRect;
    }
    
    private Element toSVG(final RectangularShape rect) {
        if (rect.getWidth() > 0.0 && rect.getHeight() > 0.0) {
            final Element svgRect = this.generatorContext.domFactory.createElementNS("http://www.w3.org/2000/svg", "rect");
            svgRect.setAttributeNS(null, "x", this.doubleString(rect.getX()));
            svgRect.setAttributeNS(null, "y", this.doubleString(rect.getY()));
            svgRect.setAttributeNS(null, "width", this.doubleString(rect.getWidth()));
            svgRect.setAttributeNS(null, "height", this.doubleString(rect.getHeight()));
            return svgRect;
        }
        if (rect.getWidth() == 0.0 && rect.getHeight() > 0.0) {
            final Line2D line = new Line2D.Double(rect.getX(), rect.getY(), rect.getX(), rect.getY() + rect.getHeight());
            return this.svgLine.toSVG(line);
        }
        if (rect.getWidth() > 0.0 && rect.getHeight() == 0.0) {
            final Line2D line = new Line2D.Double(rect.getX(), rect.getY(), rect.getX() + rect.getWidth(), rect.getY());
            return this.svgLine.toSVG(line);
        }
        return null;
    }
}
