// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.apache.batik.gvt.ShapePainter;
import org.apache.batik.anim.dom.AnimatedLiveAttributeValue;
import java.awt.Shape;
import org.apache.batik.dom.svg.LiveAttributeException;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.Rectangle2D;
import org.apache.batik.anim.dom.AbstractSVGAnimatedLength;
import org.apache.batik.anim.dom.SVGOMRectElement;
import org.apache.batik.gvt.ShapeNode;
import org.w3c.dom.Element;

public class SVGRectElementBridge extends SVGShapeElementBridge
{
    @Override
    public String getLocalName() {
        return "rect";
    }
    
    @Override
    public Bridge getInstance() {
        return new SVGRectElementBridge();
    }
    
    @Override
    protected void buildShape(final BridgeContext ctx, final Element e, final ShapeNode shapeNode) {
        try {
            final SVGOMRectElement re = (SVGOMRectElement)e;
            final AbstractSVGAnimatedLength _x = (AbstractSVGAnimatedLength)re.getX();
            final float x = _x.getCheckedValue();
            final AbstractSVGAnimatedLength _y = (AbstractSVGAnimatedLength)re.getY();
            final float y = _y.getCheckedValue();
            final AbstractSVGAnimatedLength _width = (AbstractSVGAnimatedLength)re.getWidth();
            final float w = _width.getCheckedValue();
            final AbstractSVGAnimatedLength _height = (AbstractSVGAnimatedLength)re.getHeight();
            final float h = _height.getCheckedValue();
            final AbstractSVGAnimatedLength _rx = (AbstractSVGAnimatedLength)re.getRx();
            float rx = _rx.getCheckedValue();
            if (rx > w / 2.0f) {
                rx = w / 2.0f;
            }
            final AbstractSVGAnimatedLength _ry = (AbstractSVGAnimatedLength)re.getRy();
            float ry = _ry.getCheckedValue();
            if (ry > h / 2.0f) {
                ry = h / 2.0f;
            }
            Shape shape;
            if (rx == 0.0f || ry == 0.0f) {
                shape = new Rectangle2D.Float(x, y, w, h);
            }
            else {
                shape = new RoundRectangle2D.Float(x, y, w, h, rx * 2.0f, ry * 2.0f);
            }
            shapeNode.setShape(shape);
        }
        catch (LiveAttributeException ex) {
            throw new BridgeException(ctx, ex);
        }
    }
    
    @Override
    public void handleAnimatedAttributeChanged(final AnimatedLiveAttributeValue alav) {
        if (alav.getNamespaceURI() == null) {
            final String ln = alav.getLocalName();
            if (ln.equals("x") || ln.equals("y") || ln.equals("width") || ln.equals("height") || ln.equals("rx") || ln.equals("ry")) {
                this.buildShape(this.ctx, this.e, (ShapeNode)this.node);
                this.handleGeometryChanged();
                return;
            }
        }
        super.handleAnimatedAttributeChanged(alav);
    }
    
    @Override
    protected ShapePainter createShapePainter(final BridgeContext ctx, final Element e, final ShapeNode shapeNode) {
        final Shape shape = shapeNode.getShape();
        final Rectangle2D r2d = shape.getBounds2D();
        if (r2d.getWidth() == 0.0 || r2d.getHeight() == 0.0) {
            return null;
        }
        return super.createShapePainter(ctx, e, shapeNode);
    }
}
