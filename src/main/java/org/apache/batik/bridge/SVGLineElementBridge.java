// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.apache.batik.anim.dom.AnimatedLiveAttributeValue;
import org.apache.batik.dom.svg.LiveAttributeException;
import java.awt.Shape;
import java.awt.geom.Line2D;
import org.apache.batik.anim.dom.AbstractSVGAnimatedLength;
import org.apache.batik.anim.dom.SVGOMLineElement;
import org.apache.batik.gvt.ShapePainter;
import org.apache.batik.gvt.ShapeNode;
import org.w3c.dom.Element;

public class SVGLineElementBridge extends SVGDecoratedShapeElementBridge
{
    @Override
    public String getLocalName() {
        return "line";
    }
    
    @Override
    public Bridge getInstance() {
        return new SVGLineElementBridge();
    }
    
    protected ShapePainter createFillStrokePainter(final BridgeContext ctx, final Element e, final ShapeNode shapeNode) {
        return PaintServer.convertStrokePainter(e, shapeNode, ctx);
    }
    
    @Override
    protected void buildShape(final BridgeContext ctx, final Element e, final ShapeNode shapeNode) {
        try {
            final SVGOMLineElement le = (SVGOMLineElement)e;
            final AbstractSVGAnimatedLength _x1 = (AbstractSVGAnimatedLength)le.getX1();
            final float x1 = _x1.getCheckedValue();
            final AbstractSVGAnimatedLength _y1 = (AbstractSVGAnimatedLength)le.getY1();
            final float y1 = _y1.getCheckedValue();
            final AbstractSVGAnimatedLength _x2 = (AbstractSVGAnimatedLength)le.getX2();
            final float x2 = _x2.getCheckedValue();
            final AbstractSVGAnimatedLength _y2 = (AbstractSVGAnimatedLength)le.getY2();
            final float y2 = _y2.getCheckedValue();
            shapeNode.setShape(new Line2D.Float(x1, y1, x2, y2));
        }
        catch (LiveAttributeException ex) {
            throw new BridgeException(ctx, ex);
        }
    }
    
    @Override
    public void handleAnimatedAttributeChanged(final AnimatedLiveAttributeValue alav) {
        if (alav.getNamespaceURI() == null) {
            final String ln = alav.getLocalName();
            if (ln.equals("x1") || ln.equals("y1") || ln.equals("x2") || ln.equals("y2")) {
                this.buildShape(this.ctx, this.e, (ShapeNode)this.node);
                this.handleGeometryChanged();
                return;
            }
        }
        super.handleAnimatedAttributeChanged(alav);
    }
}
