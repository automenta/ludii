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
import org.apache.batik.anim.dom.SVGOMEllipseElement;
import org.apache.batik.gvt.ShapeNode;
import org.w3c.dom.Element;

public class SVGEllipseElementBridge extends SVGShapeElementBridge
{
    @Override
    public String getLocalName() {
        return "ellipse";
    }
    
    @Override
    public Bridge getInstance() {
        return new SVGEllipseElementBridge();
    }
    
    @Override
    protected void buildShape(final BridgeContext ctx, final Element e, final ShapeNode shapeNode) {
        try {
            final SVGOMEllipseElement ee = (SVGOMEllipseElement)e;
            final AbstractSVGAnimatedLength _cx = (AbstractSVGAnimatedLength)ee.getCx();
            final float cx = _cx.getCheckedValue();
            final AbstractSVGAnimatedLength _cy = (AbstractSVGAnimatedLength)ee.getCy();
            final float cy = _cy.getCheckedValue();
            final AbstractSVGAnimatedLength _rx = (AbstractSVGAnimatedLength)ee.getRx();
            final float rx = _rx.getCheckedValue();
            final AbstractSVGAnimatedLength _ry = (AbstractSVGAnimatedLength)ee.getRy();
            final float ry = _ry.getCheckedValue();
            shapeNode.setShape(new Ellipse2D.Float(cx - rx, cy - ry, rx * 2.0f, ry * 2.0f));
        }
        catch (LiveAttributeException ex) {
            throw new BridgeException(ctx, ex);
        }
    }
    
    @Override
    public void handleAnimatedAttributeChanged(final AnimatedLiveAttributeValue alav) {
        if (alav.getNamespaceURI() == null) {
            final String ln = alav.getLocalName();
            if (ln.equals("cx") || ln.equals("cy") || ln.equals("rx") || ln.equals("ry")) {
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
