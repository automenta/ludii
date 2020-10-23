// 
// Decompiled by Procyon v0.5.36
// 

package graphics.svg;

import graphics.svg.element.BaseElement;
import graphics.svg.element.Element;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SVG
{
    private final List<Element> elements;
    private Rectangle2D.Double bounds;
    
    public SVG() {
        this.elements = new ArrayList<>();
        this.bounds = new Rectangle2D.Double();
    }
    
    public List<Element> elements() {
        return Collections.unmodifiableList(this.elements);
    }
    
    public Rectangle2D.Double bounds() {
        return this.bounds;
    }
    
    public void clear() {
        this.elements.clear();
    }
    
    public void setBounds() {
        this.bounds = null;
        for (final Element element : this.elements) {
            ((BaseElement)element).setBounds();
            if (this.bounds == null) {
                (this.bounds = new Rectangle2D.Double()).setRect(((BaseElement)element).bounds());
            }
            else {
                this.bounds.add(((BaseElement)element).bounds());
            }
        }
    }
    
    public double maxStrokeWidth() {
        double maxWidth = 0.0;
        for (final Element element : this.elements) {
            final double sw = ((BaseElement)element).strokeWidth();
            if (sw > maxWidth) {
                maxWidth = sw;
            }
        }
        return maxWidth;
    }
    
    public BufferedImage render(final Color fillColour, final Color borderColour, final int desiredSize) {
        final int x0 = (int)this.bounds.getX() - 1;
        final int x2 = (int)(this.bounds.getX() + this.bounds.getWidth()) + 1;
        final int sx = x2 - x0;
        final int y0 = (int)this.bounds.getY() - 1;
        final int y2 = (int)(this.bounds.getY() + this.bounds.getHeight()) + 1;
        final int sy = y2 - y0;
        BufferedImage image = new BufferedImage(sx, sy, 2);
        final Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        for (final Element element : this.elements) {
            element.render(g2d, -this.bounds.getX(), -this.bounds.getY(), fillColour, null, null);
        }
        for (final Element element : this.elements) {
            element.render(g2d, -this.bounds.getX(), -this.bounds.getY(), null, borderColour, null);
        }
        for (final Element element : this.elements) {
            if (element.style().strokeWidth() > 0.0) {
                System.out.println("Stroking element " + element.label());
                element.render(g2d, -this.bounds.getX(), -this.bounds.getY(), null, null, borderColour);
            }
        }
        image = resize(image, desiredSize, desiredSize);
        return image;
    }
    
    public static BufferedImage resize(final BufferedImage img, final int newW, final int newH) {
        final Image tmp = img.getScaledInstance(newW, newH, 4);
        final BufferedImage dimg = new BufferedImage(newW, newH, 2);
        final Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return dimg;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.elements.size()).append(" elements:\n");
        for (final Element element : this.elements) {
            sb.append(element).append("\n");
        }
        return sb.toString();
    }
}
