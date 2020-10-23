// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.g2d;

import java.awt.font.FontRenderContext;
import java.awt.geom.Area;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import java.util.Map;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.Font;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Composite;
import java.awt.Stroke;
import java.awt.Paint;
import java.util.List;
import java.awt.geom.AffineTransform;

public class GraphicContext implements Cloneable
{
    protected AffineTransform defaultTransform;
    protected AffineTransform transform;
    protected List transformStack;
    protected boolean transformStackValid;
    protected Paint paint;
    protected Stroke stroke;
    protected Composite composite;
    protected Shape clip;
    protected RenderingHints hints;
    protected Font font;
    protected Color background;
    protected Color foreground;
    
    public GraphicContext() {
        this.defaultTransform = new AffineTransform();
        this.transform = new AffineTransform();
        this.transformStack = new ArrayList();
        this.transformStackValid = true;
        this.paint = Color.black;
        this.stroke = new BasicStroke();
        this.composite = AlphaComposite.SrcOver;
        this.clip = null;
        this.hints = new RenderingHints(null);
        this.font = new Font("sanserif", 0, 12);
        this.background = new Color(0, 0, 0, 0);
        this.foreground = Color.black;
        this.hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_DEFAULT);
    }
    
    public GraphicContext(final AffineTransform defaultDeviceTransform) {
        this();
        this.defaultTransform = new AffineTransform(defaultDeviceTransform);
        this.transform = new AffineTransform(this.defaultTransform);
        if (!this.defaultTransform.isIdentity()) {
            this.transformStack.add(TransformStackElement.createGeneralTransformElement(this.defaultTransform));
        }
    }
    
    public Object clone() {
        final GraphicContext copyGc = new GraphicContext(this.defaultTransform);
        copyGc.transform = new AffineTransform(this.transform);
        copyGc.transformStack = new ArrayList(this.transformStack.size());
        for (int i = 0; i < this.transformStack.size(); ++i) {
            final TransformStackElement stackElement = this.transformStack.get(i);
            copyGc.transformStack.add(stackElement.clone());
        }
        copyGc.transformStackValid = this.transformStackValid;
        copyGc.paint = this.paint;
        copyGc.stroke = this.stroke;
        copyGc.composite = this.composite;
        if (this.clip != null) {
            copyGc.clip = new GeneralPath(this.clip);
        }
        else {
            copyGc.clip = null;
        }
        copyGc.hints = (RenderingHints)this.hints.clone();
        copyGc.font = this.font;
        copyGc.background = this.background;
        copyGc.foreground = this.foreground;
        return copyGc;
    }
    
    public Color getColor() {
        return this.foreground;
    }
    
    public void setColor(final Color c) {
        if (c == null) {
            return;
        }
        if (this.paint != c) {
            this.setPaint(c);
        }
    }
    
    public Font getFont() {
        return this.font;
    }
    
    public void setFont(final Font font) {
        if (font != null) {
            this.font = font;
        }
    }
    
    public Rectangle getClipBounds() {
        final Shape c = this.getClip();
        if (c == null) {
            return null;
        }
        return c.getBounds();
    }
    
    public void clipRect(final int x, final int y, final int width, final int height) {
        this.clip(new Rectangle(x, y, width, height));
    }
    
    public void setClip(final int x, final int y, final int width, final int height) {
        this.setClip(new Rectangle(x, y, width, height));
    }
    
    public Shape getClip() {
        try {
            return this.transform.createInverse().createTransformedShape(this.clip);
        }
        catch (NoninvertibleTransformException e) {
            return null;
        }
    }
    
    public void setClip(final Shape clip) {
        if (clip != null) {
            this.clip = this.transform.createTransformedShape(clip);
        }
        else {
            this.clip = null;
        }
    }
    
    public void setComposite(final Composite comp) {
        this.composite = comp;
    }
    
    public void setPaint(final Paint paint) {
        if (paint == null) {
            return;
        }
        this.paint = paint;
        if (paint instanceof Color) {
            this.foreground = (Color)paint;
        }
    }
    
    public void setStroke(final Stroke s) {
        this.stroke = s;
    }
    
    public void setRenderingHint(final RenderingHints.Key hintKey, final Object hintValue) {
        this.hints.put(hintKey, hintValue);
    }
    
    public Object getRenderingHint(final RenderingHints.Key hintKey) {
        return this.hints.get(hintKey);
    }
    
    public void setRenderingHints(final Map hints) {
        this.hints = new RenderingHints(hints);
    }
    
    public void addRenderingHints(final Map hints) {
        this.hints.putAll(hints);
    }
    
    public RenderingHints getRenderingHints() {
        return this.hints;
    }
    
    public void translate(final int x, final int y) {
        if (x != 0 || y != 0) {
            this.transform.translate(x, y);
            this.transformStack.add(TransformStackElement.createTranslateElement(x, y));
        }
    }
    
    public void translate(final double tx, final double ty) {
        this.transform.translate(tx, ty);
        this.transformStack.add(TransformStackElement.createTranslateElement(tx, ty));
    }
    
    public void rotate(final double theta) {
        this.transform.rotate(theta);
        this.transformStack.add(TransformStackElement.createRotateElement(theta));
    }
    
    public void rotate(final double theta, final double x, final double y) {
        this.transform.rotate(theta, x, y);
        this.transformStack.add(TransformStackElement.createTranslateElement(x, y));
        this.transformStack.add(TransformStackElement.createRotateElement(theta));
        this.transformStack.add(TransformStackElement.createTranslateElement(-x, -y));
    }
    
    public void scale(final double sx, final double sy) {
        this.transform.scale(sx, sy);
        this.transformStack.add(TransformStackElement.createScaleElement(sx, sy));
    }
    
    public void shear(final double shx, final double shy) {
        this.transform.shear(shx, shy);
        this.transformStack.add(TransformStackElement.createShearElement(shx, shy));
    }
    
    public void transform(final AffineTransform Tx) {
        this.transform.concatenate(Tx);
        this.transformStack.add(TransformStackElement.createGeneralTransformElement(Tx));
    }
    
    public void setTransform(final AffineTransform Tx) {
        this.transform = new AffineTransform(Tx);
        this.invalidateTransformStack();
        if (!Tx.isIdentity()) {
            this.transformStack.add(TransformStackElement.createGeneralTransformElement(Tx));
        }
    }
    
    public void validateTransformStack() {
        this.transformStackValid = true;
    }
    
    public boolean isTransformStackValid() {
        return this.transformStackValid;
    }
    
    public TransformStackElement[] getTransformStack() {
        final TransformStackElement[] stack = new TransformStackElement[this.transformStack.size()];
        this.transformStack.toArray(stack);
        return stack;
    }
    
    protected void invalidateTransformStack() {
        this.transformStack.clear();
        this.transformStackValid = false;
    }
    
    public AffineTransform getTransform() {
        return new AffineTransform(this.transform);
    }
    
    public Paint getPaint() {
        return this.paint;
    }
    
    public Composite getComposite() {
        return this.composite;
    }
    
    public void setBackground(final Color color) {
        if (color == null) {
            return;
        }
        this.background = color;
    }
    
    public Color getBackground() {
        return this.background;
    }
    
    public Stroke getStroke() {
        return this.stroke;
    }
    
    public void clip(Shape s) {
        if (s != null) {
            s = this.transform.createTransformedShape(s);
        }
        if (this.clip != null) {
            final Area newClip = new Area(this.clip);
            newClip.intersect(new Area(s));
            this.clip = new GeneralPath(newClip);
        }
        else {
            this.clip = s;
        }
    }
    
    public FontRenderContext getFontRenderContext() {
        Object antialiasingHint = this.hints.get(RenderingHints.KEY_TEXT_ANTIALIASING);
        boolean isAntialiased = true;
        if (antialiasingHint != RenderingHints.VALUE_TEXT_ANTIALIAS_ON && antialiasingHint != RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT) {
            if (antialiasingHint != RenderingHints.VALUE_TEXT_ANTIALIAS_OFF) {
                antialiasingHint = this.hints.get(RenderingHints.KEY_ANTIALIASING);
                if (antialiasingHint != RenderingHints.VALUE_ANTIALIAS_ON && antialiasingHint != RenderingHints.VALUE_ANTIALIAS_DEFAULT && antialiasingHint == RenderingHints.VALUE_ANTIALIAS_OFF) {
                    isAntialiased = false;
                }
            }
            else {
                isAntialiased = false;
            }
        }
        boolean useFractionalMetrics = true;
        if (this.hints.get(RenderingHints.KEY_FRACTIONALMETRICS) == RenderingHints.VALUE_FRACTIONALMETRICS_OFF) {
            useFractionalMetrics = false;
        }
        final FontRenderContext frc = new FontRenderContext(this.defaultTransform, isAntialiased, useFractionalMetrics);
        return frc;
    }
}
