// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import org.apache.batik.anim.dom.AnimatedLiveAttributeValue;
import org.w3c.dom.svg.SVGPathSegList;
import org.apache.batik.anim.dom.SVGOMAnimatedPathData;
import org.apache.batik.dom.svg.LiveAttributeException;
import org.apache.batik.parser.PathHandler;
import org.apache.batik.dom.svg.SVGAnimatedPathDataSupport;
import org.apache.batik.parser.AWTPathProducer;
import org.apache.batik.anim.dom.SVGOMPathElement;
import org.apache.batik.gvt.ShapeNode;
import org.w3c.dom.Element;
import org.apache.batik.ext.awt.geom.PathLength;
import java.awt.Shape;
import org.apache.batik.dom.svg.SVGPathContext;

public class SVGPathElementBridge extends SVGDecoratedShapeElementBridge implements SVGPathContext
{
    protected static final Shape DEFAULT_SHAPE;
    protected Shape pathLengthShape;
    protected PathLength pathLength;
    
    @Override
    public String getLocalName() {
        return "path";
    }
    
    @Override
    public Bridge getInstance() {
        return new SVGPathElementBridge();
    }
    
    @Override
    protected void buildShape(final BridgeContext ctx, final Element e, final ShapeNode shapeNode) {
        final SVGOMPathElement pe = (SVGOMPathElement)e;
        final AWTPathProducer app = new AWTPathProducer();
        try {
            final SVGOMAnimatedPathData _d = pe.getAnimatedPathData();
            _d.check();
            final SVGPathSegList p = _d.getAnimatedPathSegList();
            app.setWindingRule(CSSUtilities.convertFillRule(e));
            SVGAnimatedPathDataSupport.handlePathSegList(p, app);
        }
        catch (LiveAttributeException ex) {
            throw new BridgeException(ctx, ex);
        }
        finally {
            shapeNode.setShape(app.getShape());
        }
    }
    
    @Override
    public void handleAnimatedAttributeChanged(final AnimatedLiveAttributeValue alav) {
        if (alav.getNamespaceURI() == null && alav.getLocalName().equals("d")) {
            this.buildShape(this.ctx, this.e, (ShapeNode)this.node);
            this.handleGeometryChanged();
        }
        else {
            super.handleAnimatedAttributeChanged(alav);
        }
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
    
    protected PathLength getPathLengthObj() {
        final Shape s = ((ShapeNode)this.node).getShape();
        if (this.pathLengthShape != s) {
            this.pathLength = new PathLength(s);
            this.pathLengthShape = s;
        }
        return this.pathLength;
    }
    
    @Override
    public float getTotalLength() {
        final PathLength pl = this.getPathLengthObj();
        return pl.lengthOfPath();
    }
    
    @Override
    public Point2D getPointAtLength(final float distance) {
        final PathLength pl = this.getPathLengthObj();
        return pl.pointAtLength(distance);
    }
    
    @Override
    public int getPathSegAtLength(final float distance) {
        final PathLength pl = this.getPathLengthObj();
        return pl.segmentAtLength(distance);
    }
    
    static {
        DEFAULT_SHAPE = new GeneralPath();
    }
}
