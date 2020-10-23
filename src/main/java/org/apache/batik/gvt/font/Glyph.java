// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt.font;

import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.util.Collection;
import java.util.List;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.text.TextPaintInfo;
import java.awt.geom.Rectangle2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Vector;

public class Glyph
{
    private String unicode;
    private Vector names;
    private String orientation;
    private String arabicForm;
    private String lang;
    private Point2D horizOrigin;
    private Point2D vertOrigin;
    private float horizAdvX;
    private float vertAdvY;
    private int glyphCode;
    private AffineTransform transform;
    private Point2D.Float position;
    private GVTGlyphMetrics metrics;
    private Shape outline;
    private Rectangle2D bounds;
    private TextPaintInfo tpi;
    private TextPaintInfo cacheTPI;
    private Shape dShape;
    private GraphicsNode glyphChildrenNode;
    
    public Glyph(final String unicode, final List names, final String orientation, final String arabicForm, final String lang, final Point2D horizOrigin, final Point2D vertOrigin, final float horizAdvX, final float vertAdvY, final int glyphCode, final TextPaintInfo tpi, final Shape dShape, final GraphicsNode glyphChildrenNode) {
        if (unicode == null) {
            throw new IllegalArgumentException();
        }
        if (horizOrigin == null) {
            throw new IllegalArgumentException();
        }
        if (vertOrigin == null) {
            throw new IllegalArgumentException();
        }
        this.unicode = unicode;
        this.names = new Vector(names);
        this.orientation = orientation;
        this.arabicForm = arabicForm;
        this.lang = lang;
        this.horizOrigin = horizOrigin;
        this.vertOrigin = vertOrigin;
        this.horizAdvX = horizAdvX;
        this.vertAdvY = vertAdvY;
        this.glyphCode = glyphCode;
        this.position = new Point2D.Float(0.0f, 0.0f);
        this.outline = null;
        this.bounds = null;
        this.tpi = tpi;
        this.dShape = dShape;
        this.glyphChildrenNode = glyphChildrenNode;
    }
    
    public String getUnicode() {
        return this.unicode;
    }
    
    public Vector getNames() {
        return this.names;
    }
    
    public String getOrientation() {
        return this.orientation;
    }
    
    public String getArabicForm() {
        return this.arabicForm;
    }
    
    public String getLang() {
        return this.lang;
    }
    
    public Point2D getHorizOrigin() {
        return this.horizOrigin;
    }
    
    public Point2D getVertOrigin() {
        return this.vertOrigin;
    }
    
    public float getHorizAdvX() {
        return this.horizAdvX;
    }
    
    public float getVertAdvY() {
        return this.vertAdvY;
    }
    
    public int getGlyphCode() {
        return this.glyphCode;
    }
    
    public AffineTransform getTransform() {
        return this.transform;
    }
    
    public void setTransform(final AffineTransform transform) {
        this.transform = transform;
        this.outline = null;
        this.bounds = null;
    }
    
    public Point2D getPosition() {
        return this.position;
    }
    
    public void setPosition(final Point2D position) {
        this.position.x = (float)position.getX();
        this.position.y = (float)position.getY();
        this.outline = null;
        this.bounds = null;
    }
    
    public GVTGlyphMetrics getGlyphMetrics() {
        if (this.metrics == null) {
            final Rectangle2D gb = this.getGeometryBounds();
            this.metrics = new GVTGlyphMetrics(this.getHorizAdvX(), this.getVertAdvY(), new Rectangle2D.Double(gb.getX() - this.position.getX(), gb.getY() - this.position.getY(), gb.getWidth(), gb.getHeight()), (byte)3);
        }
        return this.metrics;
    }
    
    public GVTGlyphMetrics getGlyphMetrics(final float hkern, final float vkern) {
        return new GVTGlyphMetrics(this.getHorizAdvX() - hkern, this.getVertAdvY() - vkern, this.getGeometryBounds(), (byte)3);
    }
    
    public Rectangle2D getGeometryBounds() {
        return this.getOutline().getBounds2D();
    }
    
    public Rectangle2D getBounds2D() {
        if (this.bounds != null && TextPaintInfo.equivilent(this.tpi, this.cacheTPI)) {
            return this.bounds;
        }
        final AffineTransform tr = AffineTransform.getTranslateInstance(this.position.getX(), this.position.getY());
        if (this.transform != null) {
            tr.concatenate(this.transform);
        }
        Rectangle2D bounds = null;
        if (this.dShape != null && this.tpi != null) {
            if (this.tpi.fillPaint != null) {
                bounds = tr.createTransformedShape(this.dShape).getBounds2D();
            }
            if (this.tpi.strokeStroke != null && this.tpi.strokePaint != null) {
                final Shape s = this.tpi.strokeStroke.createStrokedShape(this.dShape);
                final Rectangle2D r = tr.createTransformedShape(s).getBounds2D();
                if (bounds == null) {
                    bounds = r;
                }
                else {
                    bounds.add(r);
                }
            }
        }
        if (this.glyphChildrenNode != null) {
            final Rectangle2D r2 = this.glyphChildrenNode.getTransformedBounds(tr);
            if (bounds == null) {
                bounds = r2;
            }
            else {
                bounds.add(r2);
            }
        }
        if (bounds == null) {
            bounds = new Rectangle2D.Double(this.position.getX(), this.position.getY(), 0.0, 0.0);
        }
        this.cacheTPI = new TextPaintInfo(this.tpi);
        return bounds;
    }
    
    public Shape getOutline() {
        if (this.outline == null) {
            final AffineTransform tr = AffineTransform.getTranslateInstance(this.position.getX(), this.position.getY());
            if (this.transform != null) {
                tr.concatenate(this.transform);
            }
            Shape glyphChildrenOutline = null;
            if (this.glyphChildrenNode != null) {
                glyphChildrenOutline = this.glyphChildrenNode.getOutline();
            }
            GeneralPath glyphOutline = null;
            if (this.dShape != null && glyphChildrenOutline != null) {
                glyphOutline = new GeneralPath(this.dShape);
                glyphOutline.append(glyphChildrenOutline, false);
            }
            else if (this.dShape != null && glyphChildrenOutline == null) {
                glyphOutline = new GeneralPath(this.dShape);
            }
            else if (this.dShape == null && glyphChildrenOutline != null) {
                glyphOutline = new GeneralPath(glyphChildrenOutline);
            }
            else {
                glyphOutline = new GeneralPath();
            }
            this.outline = tr.createTransformedShape(glyphOutline);
        }
        return this.outline;
    }
    
    public void draw(final Graphics2D graphics2D) {
        final AffineTransform tr = AffineTransform.getTranslateInstance(this.position.getX(), this.position.getY());
        if (this.transform != null) {
            tr.concatenate(this.transform);
        }
        if (this.dShape != null && this.tpi != null) {
            final Shape tShape = tr.createTransformedShape(this.dShape);
            if (this.tpi.fillPaint != null) {
                graphics2D.setPaint(this.tpi.fillPaint);
                graphics2D.fill(tShape);
            }
            if (this.tpi.strokeStroke != null && this.tpi.strokePaint != null) {
                graphics2D.setStroke(this.tpi.strokeStroke);
                graphics2D.setPaint(this.tpi.strokePaint);
                graphics2D.draw(tShape);
            }
        }
        if (this.glyphChildrenNode != null) {
            this.glyphChildrenNode.setTransform(tr);
            this.glyphChildrenNode.paint(graphics2D);
        }
    }
}
