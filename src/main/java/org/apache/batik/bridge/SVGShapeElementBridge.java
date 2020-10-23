// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.apache.batik.css.engine.CSSEngineEvent;
import org.apache.batik.gvt.ShapePainter;
import java.awt.RenderingHints;
import org.apache.batik.gvt.ShapeNode;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;

public abstract class SVGShapeElementBridge extends AbstractGraphicsNodeBridge
{
    protected boolean hasNewShapePainter;
    
    protected SVGShapeElementBridge() {
    }
    
    @Override
    public GraphicsNode createGraphicsNode(final BridgeContext ctx, final Element e) {
        final ShapeNode shapeNode = (ShapeNode)super.createGraphicsNode(ctx, e);
        if (shapeNode == null) {
            return null;
        }
        this.associateSVGContext(ctx, e, shapeNode);
        this.buildShape(ctx, e, shapeNode);
        RenderingHints hints = null;
        hints = CSSUtilities.convertColorRendering(e, hints);
        hints = CSSUtilities.convertShapeRendering(e, hints);
        if (hints != null) {
            shapeNode.setRenderingHints(hints);
        }
        return shapeNode;
    }
    
    @Override
    protected GraphicsNode instantiateGraphicsNode() {
        return new ShapeNode();
    }
    
    @Override
    public void buildGraphicsNode(final BridgeContext ctx, final Element e, final GraphicsNode node) {
        final ShapeNode shapeNode = (ShapeNode)node;
        shapeNode.setShapePainter(this.createShapePainter(ctx, e, shapeNode));
        super.buildGraphicsNode(ctx, e, node);
    }
    
    protected ShapePainter createShapePainter(final BridgeContext ctx, final Element e, final ShapeNode shapeNode) {
        return PaintServer.convertFillAndStroke(e, shapeNode, ctx);
    }
    
    protected abstract void buildShape(final BridgeContext p0, final Element p1, final ShapeNode p2);
    
    @Override
    public boolean isComposite() {
        return false;
    }
    
    @Override
    protected void handleGeometryChanged() {
        super.handleGeometryChanged();
        final ShapeNode shapeNode = (ShapeNode)this.node;
        shapeNode.setShapePainter(this.createShapePainter(this.ctx, this.e, shapeNode));
    }
    
    @Override
    public void handleCSSEngineEvent(final CSSEngineEvent evt) {
        this.hasNewShapePainter = false;
        super.handleCSSEngineEvent(evt);
    }
    
    @Override
    protected void handleCSSPropertyChanged(final int property) {
        switch (property) {
            case 15:
            case 16:
            case 45:
            case 46:
            case 47:
            case 48:
            case 49:
            case 50:
            case 51:
            case 52: {
                if (!this.hasNewShapePainter) {
                    this.hasNewShapePainter = true;
                    final ShapeNode shapeNode = (ShapeNode)this.node;
                    shapeNode.setShapePainter(this.createShapePainter(this.ctx, this.e, shapeNode));
                    break;
                }
                break;
            }
            case 42: {
                RenderingHints hints = this.node.getRenderingHints();
                hints = CSSUtilities.convertShapeRendering(this.e, hints);
                if (hints != null) {
                    this.node.setRenderingHints(hints);
                    break;
                }
                break;
            }
            case 9: {
                RenderingHints hints = this.node.getRenderingHints();
                hints = CSSUtilities.convertColorRendering(this.e, hints);
                if (hints != null) {
                    this.node.setRenderingHints(hints);
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
