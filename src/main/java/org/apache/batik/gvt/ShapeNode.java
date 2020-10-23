// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt;

import org.apache.batik.util.HaltingThread;
import java.awt.geom.Point2D;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.Shape;

public class ShapeNode extends AbstractGraphicsNode
{
    protected Shape shape;
    protected ShapePainter shapePainter;
    private Rectangle2D primitiveBounds;
    private Rectangle2D geometryBounds;
    private Rectangle2D sensitiveBounds;
    private Shape paintedArea;
    private Shape sensitiveArea;
    
    public void setShape(final Shape newShape) {
        this.fireGraphicsNodeChangeStarted();
        this.invalidateGeometryCache();
        this.shape = newShape;
        if (this.shapePainter != null) {
            if (newShape != null) {
                this.shapePainter.setShape(newShape);
            }
            else {
                this.shapePainter = null;
            }
        }
        this.fireGraphicsNodeChangeCompleted();
    }
    
    public Shape getShape() {
        return this.shape;
    }
    
    public void setShapePainter(final ShapePainter newShapePainter) {
        if (this.shape == null) {
            return;
        }
        this.fireGraphicsNodeChangeStarted();
        this.invalidateGeometryCache();
        this.shapePainter = newShapePainter;
        if (this.shapePainter != null && this.shape != this.shapePainter.getShape()) {
            this.shapePainter.setShape(this.shape);
        }
        this.fireGraphicsNodeChangeCompleted();
    }
    
    public ShapePainter getShapePainter() {
        return this.shapePainter;
    }
    
    @Override
    public void paint(final Graphics2D g2d) {
        if (this.isVisible) {
            super.paint(g2d);
        }
    }
    
    @Override
    public void primitivePaint(final Graphics2D g2d) {
        if (this.shapePainter != null) {
            this.shapePainter.paint(g2d);
        }
    }
    
    @Override
    protected void invalidateGeometryCache() {
        super.invalidateGeometryCache();
        this.primitiveBounds = null;
        this.geometryBounds = null;
        this.sensitiveBounds = null;
        this.paintedArea = null;
        this.sensitiveArea = null;
    }
    
    @Override
    public void setPointerEventType(final int pointerEventType) {
        super.setPointerEventType(pointerEventType);
        this.sensitiveBounds = null;
        this.sensitiveArea = null;
    }
    
    @Override
    public boolean contains(final Point2D p) {
        switch (this.pointerEventType) {
            case 0:
            case 1:
            case 2:
            case 3: {
                if (!this.isVisible) {
                    return false;
                }
            }
            case 4:
            case 5:
            case 6:
            case 7: {
                final Rectangle2D b = this.getSensitiveBounds();
                return b != null && b.contains(p) && this.inSensitiveArea(p);
            }
            default: {
                return false;
            }
        }
    }
    
    @Override
    public boolean intersects(final Rectangle2D r) {
        final Rectangle2D b = this.getBounds();
        return b != null && b.intersects(r) && this.paintedArea != null && this.paintedArea.intersects(r);
    }
    
    @Override
    public Rectangle2D getPrimitiveBounds() {
        if (!this.isVisible) {
            return null;
        }
        if (this.shape == null) {
            return null;
        }
        if (this.primitiveBounds != null) {
            return this.primitiveBounds;
        }
        if (this.shapePainter == null) {
            this.primitiveBounds = this.shape.getBounds2D();
        }
        else {
            this.primitiveBounds = this.shapePainter.getPaintedBounds2D();
        }
        if (HaltingThread.hasBeenHalted()) {
            this.invalidateGeometryCache();
        }
        return this.primitiveBounds;
    }
    
    public boolean inSensitiveArea(final Point2D pt) {
        if (this.shapePainter == null) {
            return false;
        }
        ShapePainter strokeShapePainter = null;
        ShapePainter fillShapePainter = null;
        if (this.shapePainter instanceof StrokeShapePainter) {
            strokeShapePainter = this.shapePainter;
        }
        else if (this.shapePainter instanceof FillShapePainter) {
            fillShapePainter = this.shapePainter;
        }
        else {
            if (!(this.shapePainter instanceof CompositeShapePainter)) {
                return false;
            }
            final CompositeShapePainter cp = (CompositeShapePainter)this.shapePainter;
            for (int i = 0; i < cp.getShapePainterCount(); ++i) {
                final ShapePainter sp = cp.getShapePainter(i);
                if (sp instanceof StrokeShapePainter) {
                    strokeShapePainter = sp;
                }
                else if (sp instanceof FillShapePainter) {
                    fillShapePainter = sp;
                }
            }
        }
        switch (this.pointerEventType) {
            case 0:
            case 4: {
                return this.shapePainter.inPaintedArea(pt);
            }
            case 3:
            case 7: {
                return this.shapePainter.inSensitiveArea(pt);
            }
            case 1:
            case 5: {
                if (fillShapePainter != null) {
                    return fillShapePainter.inSensitiveArea(pt);
                }
                break;
            }
            case 2:
            case 6: {
                if (strokeShapePainter != null) {
                    return strokeShapePainter.inSensitiveArea(pt);
                }
                break;
            }
        }
        return false;
    }
    
    @Override
    public Rectangle2D getSensitiveBounds() {
        if (this.sensitiveBounds != null) {
            return this.sensitiveBounds;
        }
        if (this.shapePainter == null) {
            return null;
        }
        ShapePainter strokeShapePainter = null;
        ShapePainter fillShapePainter = null;
        if (this.shapePainter instanceof StrokeShapePainter) {
            strokeShapePainter = this.shapePainter;
        }
        else if (this.shapePainter instanceof FillShapePainter) {
            fillShapePainter = this.shapePainter;
        }
        else {
            if (!(this.shapePainter instanceof CompositeShapePainter)) {
                return null;
            }
            final CompositeShapePainter cp = (CompositeShapePainter)this.shapePainter;
            for (int i = 0; i < cp.getShapePainterCount(); ++i) {
                final ShapePainter sp = cp.getShapePainter(i);
                if (sp instanceof StrokeShapePainter) {
                    strokeShapePainter = sp;
                }
                else if (sp instanceof FillShapePainter) {
                    fillShapePainter = sp;
                }
            }
        }
        switch (this.pointerEventType) {
            case 0:
            case 4: {
                this.sensitiveBounds = this.shapePainter.getPaintedBounds2D();
                break;
            }
            case 1:
            case 5: {
                if (fillShapePainter != null) {
                    this.sensitiveBounds = fillShapePainter.getSensitiveBounds2D();
                    break;
                }
                break;
            }
            case 2:
            case 6: {
                if (strokeShapePainter != null) {
                    this.sensitiveBounds = strokeShapePainter.getSensitiveBounds2D();
                    break;
                }
                break;
            }
            case 3:
            case 7: {
                this.sensitiveBounds = this.shapePainter.getSensitiveBounds2D();
                break;
            }
        }
        return this.sensitiveBounds;
    }
    
    public Shape getSensitiveArea() {
        if (this.sensitiveArea != null) {
            return this.sensitiveArea;
        }
        if (this.shapePainter == null) {
            return null;
        }
        ShapePainter strokeShapePainter = null;
        ShapePainter fillShapePainter = null;
        if (this.shapePainter instanceof StrokeShapePainter) {
            strokeShapePainter = this.shapePainter;
        }
        else if (this.shapePainter instanceof FillShapePainter) {
            fillShapePainter = this.shapePainter;
        }
        else {
            if (!(this.shapePainter instanceof CompositeShapePainter)) {
                return null;
            }
            final CompositeShapePainter cp = (CompositeShapePainter)this.shapePainter;
            for (int i = 0; i < cp.getShapePainterCount(); ++i) {
                final ShapePainter sp = cp.getShapePainter(i);
                if (sp instanceof StrokeShapePainter) {
                    strokeShapePainter = sp;
                }
                else if (sp instanceof FillShapePainter) {
                    fillShapePainter = sp;
                }
            }
        }
        switch (this.pointerEventType) {
            case 0:
            case 4: {
                this.sensitiveArea = this.shapePainter.getPaintedArea();
                break;
            }
            case 1:
            case 5: {
                if (fillShapePainter != null) {
                    this.sensitiveArea = fillShapePainter.getSensitiveArea();
                    break;
                }
                break;
            }
            case 2:
            case 6: {
                if (strokeShapePainter != null) {
                    this.sensitiveArea = strokeShapePainter.getSensitiveArea();
                    break;
                }
                break;
            }
            case 3:
            case 7: {
                this.sensitiveArea = this.shapePainter.getSensitiveArea();
                break;
            }
        }
        return this.sensitiveArea;
    }
    
    @Override
    public Rectangle2D getGeometryBounds() {
        if (this.geometryBounds == null) {
            if (this.shape == null) {
                return null;
            }
            this.geometryBounds = this.normalizeRectangle(this.shape.getBounds2D());
        }
        return this.geometryBounds;
    }
    
    @Override
    public Shape getOutline() {
        return this.shape;
    }
}
