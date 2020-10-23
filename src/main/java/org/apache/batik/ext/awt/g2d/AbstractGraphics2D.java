// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.g2d;

import java.awt.font.FontRenderContext;
import java.util.Map;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImageOp;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.Image;
import java.text.AttributedCharacterIterator;
import java.awt.Polygon;
import java.awt.geom.GeneralPath;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.Paint;
import java.awt.geom.Line2D;
import java.awt.Shape;
import java.awt.Rectangle;
import java.awt.Font;
import java.awt.Composite;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;

public abstract class AbstractGraphics2D extends Graphics2D implements Cloneable
{
    protected GraphicContext gc;
    protected boolean textAsShapes;
    
    public AbstractGraphics2D(final boolean textAsShapes) {
        this.textAsShapes = false;
        this.textAsShapes = textAsShapes;
    }
    
    public AbstractGraphics2D(final AbstractGraphics2D g) {
        this.textAsShapes = false;
        (this.gc = (GraphicContext)g.gc.clone()).validateTransformStack();
        this.textAsShapes = g.textAsShapes;
    }
    
    @Override
    public void translate(final int x, final int y) {
        this.gc.translate(x, y);
    }
    
    @Override
    public Color getColor() {
        return this.gc.getColor();
    }
    
    @Override
    public void setColor(final Color c) {
        this.gc.setColor(c);
    }
    
    @Override
    public void setPaintMode() {
        this.gc.setComposite(AlphaComposite.SrcOver);
    }
    
    @Override
    public Font getFont() {
        return this.gc.getFont();
    }
    
    @Override
    public void setFont(final Font font) {
        this.gc.setFont(font);
    }
    
    @Override
    public Rectangle getClipBounds() {
        return this.gc.getClipBounds();
    }
    
    @Override
    public void clipRect(final int x, final int y, final int width, final int height) {
        this.gc.clipRect(x, y, width, height);
    }
    
    @Override
    public void setClip(final int x, final int y, final int width, final int height) {
        this.gc.setClip(x, y, width, height);
    }
    
    @Override
    public Shape getClip() {
        return this.gc.getClip();
    }
    
    @Override
    public void setClip(final Shape clip) {
        this.gc.setClip(clip);
    }
    
    @Override
    public void drawLine(final int x1, final int y1, final int x2, final int y2) {
        final Line2D line = new Line2D.Float((float)x1, (float)y1, (float)x2, (float)y2);
        this.draw(line);
    }
    
    @Override
    public void fillRect(final int x, final int y, final int width, final int height) {
        final Rectangle rect = new Rectangle(x, y, width, height);
        this.fill(rect);
    }
    
    @Override
    public void drawRect(final int x, final int y, final int width, final int height) {
        final Rectangle rect = new Rectangle(x, y, width, height);
        this.draw(rect);
    }
    
    @Override
    public void clearRect(final int x, final int y, final int width, final int height) {
        final Paint paint = this.gc.getPaint();
        this.gc.setColor(this.gc.getBackground());
        this.fillRect(x, y, width, height);
        this.gc.setPaint(paint);
    }
    
    @Override
    public void drawRoundRect(final int x, final int y, final int width, final int height, final int arcWidth, final int arcHeight) {
        final RoundRectangle2D rect = new RoundRectangle2D.Float((float)x, (float)y, (float)width, (float)height, (float)arcWidth, (float)arcHeight);
        this.draw(rect);
    }
    
    @Override
    public void fillRoundRect(final int x, final int y, final int width, final int height, final int arcWidth, final int arcHeight) {
        final RoundRectangle2D rect = new RoundRectangle2D.Float((float)x, (float)y, (float)width, (float)height, (float)arcWidth, (float)arcHeight);
        this.fill(rect);
    }
    
    @Override
    public void drawOval(final int x, final int y, final int width, final int height) {
        final Ellipse2D oval = new Ellipse2D.Float((float)x, (float)y, (float)width, (float)height);
        this.draw(oval);
    }
    
    @Override
    public void fillOval(final int x, final int y, final int width, final int height) {
        final Ellipse2D oval = new Ellipse2D.Float((float)x, (float)y, (float)width, (float)height);
        this.fill(oval);
    }
    
    @Override
    public void drawArc(final int x, final int y, final int width, final int height, final int startAngle, final int arcAngle) {
        final Arc2D arc = new Arc2D.Float((float)x, (float)y, (float)width, (float)height, (float)startAngle, (float)arcAngle, 0);
        this.draw(arc);
    }
    
    @Override
    public void fillArc(final int x, final int y, final int width, final int height, final int startAngle, final int arcAngle) {
        final Arc2D arc = new Arc2D.Float((float)x, (float)y, (float)width, (float)height, (float)startAngle, (float)arcAngle, 2);
        this.fill(arc);
    }
    
    @Override
    public void drawPolyline(final int[] xPoints, final int[] yPoints, final int nPoints) {
        if (nPoints > 0) {
            final GeneralPath path = new GeneralPath();
            path.moveTo((float)xPoints[0], (float)yPoints[0]);
            for (int i = 1; i < nPoints; ++i) {
                path.lineTo((float)xPoints[i], (float)yPoints[i]);
            }
            this.draw(path);
        }
    }
    
    @Override
    public void drawPolygon(final int[] xPoints, final int[] yPoints, final int nPoints) {
        final Polygon polygon = new Polygon(xPoints, yPoints, nPoints);
        this.draw(polygon);
    }
    
    @Override
    public void fillPolygon(final int[] xPoints, final int[] yPoints, final int nPoints) {
        final Polygon polygon = new Polygon(xPoints, yPoints, nPoints);
        this.fill(polygon);
    }
    
    @Override
    public void drawString(final String str, final int x, final int y) {
        this.drawString(str, (float)x, (float)y);
    }
    
    @Override
    public void drawString(final AttributedCharacterIterator iterator, final int x, final int y) {
        this.drawString(iterator, (float)x, (float)y);
    }
    
    @Override
    public boolean drawImage(final Image img, final int x, final int y, final Color bgcolor, final ImageObserver observer) {
        return this.drawImage(img, x, y, img.getWidth(null), img.getHeight(null), bgcolor, observer);
    }
    
    @Override
    public boolean drawImage(final Image img, final int x, final int y, final int width, final int height, final Color bgcolor, final ImageObserver observer) {
        final Paint paint = this.gc.getPaint();
        this.gc.setPaint(bgcolor);
        this.fillRect(x, y, width, height);
        this.gc.setPaint(paint);
        this.drawImage(img, x, y, width, height, observer);
        return true;
    }
    
    @Override
    public boolean drawImage(final Image img, final int dx1, final int dy1, final int dx2, final int dy2, final int sx1, final int sy1, final int sx2, final int sy2, final ImageObserver observer) {
        BufferedImage src = new BufferedImage(img.getWidth(null), img.getHeight(null), 2);
        final Graphics2D g = src.createGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();
        src = src.getSubimage(sx1, sy1, sx2 - sx1, sy2 - sy1);
        return this.drawImage(src, dx1, dy1, dx2 - dx1, dy2 - dy1, observer);
    }
    
    @Override
    public boolean drawImage(final Image img, final int dx1, final int dy1, final int dx2, final int dy2, final int sx1, final int sy1, final int sx2, final int sy2, final Color bgcolor, final ImageObserver observer) {
        final Paint paint = this.gc.getPaint();
        this.gc.setPaint(bgcolor);
        this.fillRect(dx1, dy1, dx2 - dx1, dy2 - dy1);
        this.gc.setPaint(paint);
        return this.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer);
    }
    
    @Override
    public boolean drawImage(final Image img, final AffineTransform xform, final ImageObserver obs) {
        boolean retVal = true;
        if (xform.getDeterminant() != 0.0) {
            AffineTransform inverseTransform = null;
            try {
                inverseTransform = xform.createInverse();
            }
            catch (NoninvertibleTransformException e) {
                throw new RuntimeException(e.getMessage());
            }
            this.gc.transform(xform);
            retVal = this.drawImage(img, 0, 0, null);
            this.gc.transform(inverseTransform);
        }
        else {
            final AffineTransform savTransform = new AffineTransform(this.gc.getTransform());
            this.gc.transform(xform);
            retVal = this.drawImage(img, 0, 0, null);
            this.gc.setTransform(savTransform);
        }
        return retVal;
    }
    
    @Override
    public void drawImage(BufferedImage img, final BufferedImageOp op, final int x, final int y) {
        img = op.filter(img, null);
        this.drawImage(img, x, y, null);
    }
    
    @Override
    public void drawGlyphVector(final GlyphVector g, final float x, final float y) {
        final Shape glyphOutline = g.getOutline(x, y);
        this.fill(glyphOutline);
    }
    
    @Override
    public boolean hit(final Rectangle rect, Shape s, final boolean onStroke) {
        if (onStroke) {
            s = this.gc.getStroke().createStrokedShape(s);
        }
        s = this.gc.getTransform().createTransformedShape(s);
        return s.intersects(rect);
    }
    
    @Override
    public void setComposite(final Composite comp) {
        this.gc.setComposite(comp);
    }
    
    @Override
    public void setPaint(final Paint paint) {
        this.gc.setPaint(paint);
    }
    
    @Override
    public void setStroke(final Stroke s) {
        this.gc.setStroke(s);
    }
    
    @Override
    public void setRenderingHint(final RenderingHints.Key hintKey, final Object hintValue) {
        this.gc.setRenderingHint(hintKey, hintValue);
    }
    
    @Override
    public Object getRenderingHint(final RenderingHints.Key hintKey) {
        return this.gc.getRenderingHint(hintKey);
    }
    
    @Override
    public void setRenderingHints(final Map hints) {
        this.gc.setRenderingHints(hints);
    }
    
    @Override
    public void addRenderingHints(final Map hints) {
        this.gc.addRenderingHints(hints);
    }
    
    @Override
    public RenderingHints getRenderingHints() {
        return this.gc.getRenderingHints();
    }
    
    @Override
    public void translate(final double tx, final double ty) {
        this.gc.translate(tx, ty);
    }
    
    @Override
    public void rotate(final double theta) {
        this.gc.rotate(theta);
    }
    
    @Override
    public void rotate(final double theta, final double x, final double y) {
        this.gc.rotate(theta, x, y);
    }
    
    @Override
    public void scale(final double sx, final double sy) {
        this.gc.scale(sx, sy);
    }
    
    @Override
    public void shear(final double shx, final double shy) {
        this.gc.shear(shx, shy);
    }
    
    @Override
    public void transform(final AffineTransform Tx) {
        this.gc.transform(Tx);
    }
    
    @Override
    public void setTransform(final AffineTransform Tx) {
        this.gc.setTransform(Tx);
    }
    
    @Override
    public AffineTransform getTransform() {
        return this.gc.getTransform();
    }
    
    @Override
    public Paint getPaint() {
        return this.gc.getPaint();
    }
    
    @Override
    public Composite getComposite() {
        return this.gc.getComposite();
    }
    
    @Override
    public void setBackground(final Color color) {
        this.gc.setBackground(color);
    }
    
    @Override
    public Color getBackground() {
        return this.gc.getBackground();
    }
    
    @Override
    public Stroke getStroke() {
        return this.gc.getStroke();
    }
    
    @Override
    public void clip(final Shape s) {
        this.gc.clip(s);
    }
    
    @Override
    public FontRenderContext getFontRenderContext() {
        return this.gc.getFontRenderContext();
    }
    
    public GraphicContext getGraphicContext() {
        return this.gc;
    }
}
