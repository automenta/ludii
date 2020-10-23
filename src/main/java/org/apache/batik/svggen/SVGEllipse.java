// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import java.awt.geom.Line2D;
import org.w3c.dom.Element;
import java.awt.geom.Ellipse2D;

public class SVGEllipse extends SVGGraphicObjectConverter
{
    private SVGLine svgLine;
    
    public SVGEllipse(final SVGGeneratorContext generatorContext) {
        super(generatorContext);
    }
    
    public Element toSVG(final Ellipse2D ellipse) {
        if (ellipse.getWidth() < 0.0 || ellipse.getHeight() < 0.0) {
            return null;
        }
        if (ellipse.getWidth() == ellipse.getHeight()) {
            return this.toSVGCircle(ellipse);
        }
        return this.toSVGEllipse(ellipse);
    }
    
    private Element toSVGCircle(final Ellipse2D ellipse) {
        final Element svgCircle = this.generatorContext.domFactory.createElementNS("http://www.w3.org/2000/svg", "circle");
        svgCircle.setAttributeNS(null, "cx", this.doubleString(ellipse.getX() + ellipse.getWidth() / 2.0));
        svgCircle.setAttributeNS(null, "cy", this.doubleString(ellipse.getY() + ellipse.getHeight() / 2.0));
        svgCircle.setAttributeNS(null, "r", this.doubleString(ellipse.getWidth() / 2.0));
        return svgCircle;
    }
    
    private Element toSVGEllipse(final Ellipse2D ellipse) {
        if (ellipse.getWidth() > 0.0 && ellipse.getHeight() > 0.0) {
            final Element svgCircle = this.generatorContext.domFactory.createElementNS("http://www.w3.org/2000/svg", "ellipse");
            svgCircle.setAttributeNS(null, "cx", this.doubleString(ellipse.getX() + ellipse.getWidth() / 2.0));
            svgCircle.setAttributeNS(null, "cy", this.doubleString(ellipse.getY() + ellipse.getHeight() / 2.0));
            svgCircle.setAttributeNS(null, "rx", this.doubleString(ellipse.getWidth() / 2.0));
            svgCircle.setAttributeNS(null, "ry", this.doubleString(ellipse.getHeight() / 2.0));
            return svgCircle;
        }
        if (ellipse.getWidth() == 0.0 && ellipse.getHeight() > 0.0) {
            final Line2D line = new Line2D.Double(ellipse.getX(), ellipse.getY(), ellipse.getX(), ellipse.getY() + ellipse.getHeight());
            if (this.svgLine == null) {
                this.svgLine = new SVGLine(this.generatorContext);
            }
            return this.svgLine.toSVG(line);
        }
        if (ellipse.getWidth() > 0.0 && ellipse.getHeight() == 0.0) {
            final Line2D line = new Line2D.Double(ellipse.getX(), ellipse.getY(), ellipse.getX() + ellipse.getWidth(), ellipse.getY());
            if (this.svgLine == null) {
                this.svgLine = new SVGLine(this.generatorContext);
            }
            return this.svgLine.toSVG(line);
        }
        return null;
    }
}
