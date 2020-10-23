// 
// Decompiled by Procyon v0.5.36
// 

package app.display;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class SVGWindow extends JPanel
{
    private static final long serialVersionUID = 1L;
    private final BufferedImage[] images;
    
    public SVGWindow() {
        this.images = new BufferedImage[17];
    }
    
    public void setImages(final BufferedImage img1, final BufferedImage img2) {
        this.images[1] = img1;
        this.images[2] = img2;
    }
    
    @Override
    public void paint(final Graphics g) {
        g.setColor(Color.white);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        if (this.images[1] != null && this.images[2] != null) {
            g.drawImage(this.images[1], 10, 10, null);
            g.drawImage(this.images[2], 10 + this.images[1].getWidth() + 10, 10, null);
        }
    }
}
