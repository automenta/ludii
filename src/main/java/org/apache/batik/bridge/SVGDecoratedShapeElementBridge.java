// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import java.awt.Shape;
import org.apache.batik.gvt.CompositeShapePainter;
import org.apache.batik.gvt.ShapePainter;
import org.apache.batik.gvt.ShapeNode;
import org.w3c.dom.Element;

public abstract class SVGDecoratedShapeElementBridge extends SVGShapeElementBridge
{
    protected SVGDecoratedShapeElementBridge() {
    }
    
    ShapePainter createFillStrokePainter(final BridgeContext ctx, final Element e, final ShapeNode shapeNode) {
        return super.createShapePainter(ctx, e, shapeNode);
    }
    
    ShapePainter createMarkerPainter(final BridgeContext ctx, final Element e, final ShapeNode shapeNode) {
        return PaintServer.convertMarkers(e, shapeNode, ctx);
    }
    
    @Override
    protected ShapePainter createShapePainter(final BridgeContext ctx, final Element e, final ShapeNode shapeNode) {
        final ShapePainter fillAndStroke = this.createFillStrokePainter(ctx, e, shapeNode);
        final ShapePainter markerPainter = this.createMarkerPainter(ctx, e, shapeNode);
        final Shape shape = shapeNode.getShape();
        ShapePainter painter;
        if (markerPainter != null) {
            if (fillAndStroke != null) {
                final CompositeShapePainter cp = new CompositeShapePainter(shape);
                cp.addShapePainter(fillAndStroke);
                cp.addShapePainter(markerPainter);
                painter = cp;
            }
            else {
                painter = markerPainter;
            }
        }
        else {
            painter = fillAndStroke;
        }
        return painter;
    }
    
    @Override
    protected void handleCSSPropertyChanged(final int property) {
        switch (property) {
            case 34:
            case 35:
            case 36: {
                if (!this.hasNewShapePainter) {
                    this.hasNewShapePainter = true;
                    final ShapeNode shapeNode = (ShapeNode)this.node;
                    shapeNode.setShapePainter(this.createShapePainter(this.ctx, this.e, shapeNode));
                    break;
                }
                break;
            }
            default: {
                super.handleCSSPropertyChanged(property);
                break;
            }
        }
    }
}
