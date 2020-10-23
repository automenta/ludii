// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import java.awt.geom.GeneralPath;
import org.apache.batik.anim.dom.AnimatedLiveAttributeValue;
import org.w3c.dom.svg.SVGPoint;
import org.w3c.dom.svg.SVGPointList;
import org.apache.batik.anim.dom.SVGOMAnimatedPoints;
import org.apache.batik.dom.svg.LiveAttributeException;
import org.apache.batik.parser.AWTPolylineProducer;
import org.apache.batik.anim.dom.SVGOMPolylineElement;
import org.apache.batik.gvt.ShapeNode;
import org.w3c.dom.Element;
import java.awt.Shape;

public class SVGPolylineElementBridge extends SVGDecoratedShapeElementBridge
{
    protected static final Shape DEFAULT_SHAPE;
    
    @Override
    public String getLocalName() {
        return "polyline";
    }
    
    @Override
    public Bridge getInstance() {
        return new SVGPolylineElementBridge();
    }
    
    @Override
    protected void buildShape(final BridgeContext ctx, final Element e, final ShapeNode shapeNode) {
        final SVGOMPolylineElement pe = (SVGOMPolylineElement)e;
        try {
            final SVGOMAnimatedPoints _points = pe.getSVGOMAnimatedPoints();
            _points.check();
            final SVGPointList pl = _points.getAnimatedPoints();
            final int size = pl.getNumberOfItems();
            if (size == 0) {
                shapeNode.setShape(SVGPolylineElementBridge.DEFAULT_SHAPE);
            }
            else {
                final AWTPolylineProducer app = new AWTPolylineProducer();
                app.setWindingRule(CSSUtilities.convertFillRule(e));
                app.startPoints();
                for (int i = 0; i < size; ++i) {
                    final SVGPoint p = pl.getItem(i);
                    app.point(p.getX(), p.getY());
                }
                app.endPoints();
                shapeNode.setShape(app.getShape());
            }
        }
        catch (LiveAttributeException ex) {
            throw new BridgeException(ctx, ex);
        }
    }
    
    @Override
    public void handleAnimatedAttributeChanged(final AnimatedLiveAttributeValue alav) {
        if (alav.getNamespaceURI() == null) {
            final String ln = alav.getLocalName();
            if (ln.equals("points")) {
                this.buildShape(this.ctx, this.e, (ShapeNode)this.node);
                this.handleGeometryChanged();
                return;
            }
        }
        super.handleAnimatedAttributeChanged(alav);
    }
    
    @Override
    protected void handleCSSPropertyChanged(final int property) {
        switch (property) {
            case 17: {
                this.buildShape(this.ctx, this.e, (ShapeNode)this.node);
                this.handleGeometryChanged();
                break;
            }
            default: {
                super.handleCSSPropertyChanged(property);
                break;
            }
        }
    }
    
    static {
        DEFAULT_SHAPE = new GeneralPath();
    }
}
