// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import java.awt.geom.Rectangle2D;
import org.apache.batik.gvt.ShapePainter;
import org.apache.batik.anim.dom.AnimatedLiveAttributeValue;
import org.apache.batik.dom.svg.LiveAttributeException;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import org.apache.batik.anim.dom.AbstractSVGAnimatedLength;
import org.apache.batik.anim.dom.SVGOMCircleElement;
import org.apache.batik.gvt.ShapeNode;
import org.w3c.dom.Element;

public class SVGCircleElementBridge extends SVGShapeElementBridge
{
    @Override
    public String getLocalName() {
        return "circle";
    }
    
    @Override
    public Bridge getInstance() {
        return new SVGCircleElementBridge();
    }
    
    @Override
    protected void buildShape(final BridgeContext ctx, final Element e, final ShapeNode shapeNode) {
        try {
            final SVGOMCircleElement ce = (SVGOMCircleElement)e;
            final AbstractSVGAnimatedLength _cx = (AbstractSVGAnimatedLength)ce.getCx();
            final float cx = _cx.getCheckedValue();
            final AbstractSVGAnimatedLength _cy = (AbstractSVGAnimatedLength)ce.getCy();
            final float cy = _cy.getCheckedValue();
            final AbstractSVGAnimatedLength _r = (AbstractSVGAnimatedLength)ce.getR();
            final float r = _r.getCheckedValue();
            final float x = cx - r;
            final float y = cy - r;
            final float w = r * 2.0f;
            shapeNode.setShape(new Ellipse2D.Float(x, y, w, w));
        }
        catch (LiveAttributeException ex) {
            throw new BridgeException(ctx, ex);
        }
    }
    
    @Override
    public void handleAnimatedAttributeChanged(final AnimatedLiveAttributeValue alav) {
        if (alav.getNamespaceURI() == null) {
            final String ln = alav.getLocalName();
            if (ln.equals("cx") || ln.equals("cy") || ln.equals("r")) {
                this.buildShape(this.ctx, this.e, (ShapeNode)this.node);
                this.handleGeometryChanged();
                return;
            }
        }
        super.handleAnimatedAttributeChanged(alav);
    }
    
    @Override
    protected ShapePainter createShapePainter(final BridgeContext ctx, final Element e, final ShapeNode shapeNode) {
        final Rectangle2D r2d = shapeNode.getShape().getBounds2D();
        if (r2d.getWidth() == 0.0 || r2d.getHeight() == 0.0) {
            return null;
        }
        return super.createShapePainter(ctx, e, shapeNode);
    }
}
