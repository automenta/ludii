// 
// Decompiled by Procyon v0.5.36
// 

package app.display.util;

import graphics.svg.SVGtoImage;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import util.ImageUtil;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

public class BufferedImageUtil
{
    public static BufferedImage makeImageTranslucent(final BufferedImage source, final double alpha) {
        if (source == null) {
            return null;
        }
        final BufferedImage target = new BufferedImage(source.getWidth(), source.getHeight(), 3);
        final Graphics2D g = target.createGraphics();
        g.setComposite(AlphaComposite.getInstance(3, (float)alpha));
        g.drawImage(source, null, 0, 0);
        g.dispose();
        return target;
    }
    
    public static BufferedImage createFlippedVertically(final BufferedImage image) {
        if (image == null) {
            return null;
        }
        final AffineTransform at = new AffineTransform();
        at.concatenate(AffineTransform.getScaleInstance(1.0, -1.0));
        at.concatenate(AffineTransform.getTranslateInstance(0.0, -image.getHeight()));
        return createTransformed(image, at);
    }
    
    public static BufferedImage createFlippedHorizontally(final BufferedImage image) {
        if (image == null) {
            return null;
        }
        final AffineTransform at = new AffineTransform();
        at.concatenate(AffineTransform.getScaleInstance(-1.0, 1.0));
        at.concatenate(AffineTransform.getTranslateInstance(-image.getWidth(), 0.0));
        return createTransformed(image, at);
    }
    
    public static BufferedImage createTransformed(final BufferedImage image, final AffineTransform at) {
        if (image == null) {
            return null;
        }
        final BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), 2);
        final Graphics2D g = newImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.transform(at);
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return newImage;
    }
    
    public static BufferedImage rotateImageByDegrees(final BufferedImage img, final double angle) {
        if (img == null) {
            return null;
        }
        final double rads = Math.toRadians(angle);
        final int w = img.getWidth();
        final int h = img.getHeight();
        final BufferedImage rotated = new BufferedImage(w, h, 2);
        final Graphics2D g2d = rotated.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        final AffineTransform at = new AffineTransform();
        at.translate(0.0, 0.0);
        final int x = w / 2;
        final int y = h / 2;
        at.rotate(rads, x, y);
        g2d.setTransform(at);
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();
        return rotated;
    }
    
    public static BufferedImage resize(final BufferedImage img, final int newW, final int newH) {
        if (img == null) {
            return null;
        }
        int width = newW;
        int height = newH;
        if (newW < 1) {
            width = 1;
        }
        if (newH < 1) {
            height = 1;
        }
        final Image tmp = img.getScaledInstance(width, height, 4);
        final BufferedImage dimg = new BufferedImage(width, height, 2);
        final Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return dimg;
    }
    
    public static BufferedImage setPixelsToColour(final BufferedImage image, final Color colour) {
        if (image == null) {
            return null;
        }
        for (int y = 0; y < image.getHeight(); ++y) {
            for (int x = 0; x < image.getWidth(); ++x) {
                if (image.getRGB(x, y) != 0) {
                    image.setRGB(x, y, colour.getRGB());
                }
            }
        }
        return image;
    }
    
    public static BufferedImage deepCopy(final BufferedImage image) {
        final ColorModel cm = image.getColorModel();
        final boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        final WritableRaster raster = image.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }
    
    public static BufferedImage joinBufferedImages(final BufferedImage img1, final BufferedImage img2) {
        return joinBufferedImages(img1, img2, 0, 0);
    }
    
    public static BufferedImage joinBufferedImages(final BufferedImage img1, final BufferedImage img2, final int offsetX, final int offsetY) {
        final int w = Math.max(img1.getWidth(), img2.getWidth());
        final int h = Math.max(img1.getHeight(), img2.getHeight());
        final BufferedImage combined = new BufferedImage(w, h, 2);
        final Graphics2D g = combined.createGraphics();
        g.drawImage(img1, 0, 0, null);
        g.drawImage(img2, offsetX, offsetY, null);
        return combined;
    }
    
    public static BufferedImage getImageFromSVGName(final String svgName, final int imageSize) {
        final String SVGPath = ImageUtil.getImageFullPath(svgName);
        SVGGraphics2D g2d = new SVGGraphics2D(imageSize, imageSize);
        g2d = new SVGGraphics2D(imageSize, imageSize);
        SVGtoImage.loadFromString(g2d, SVGPath, imageSize, 0, 0, Color.BLACK, Color.BLACK, true);
        return SVGUtil.createSVGImage(g2d.getSVGDocument(), imageSize, imageSize);
    }
    
    public static boolean pointOverlapsImage(final Point p, final BufferedImage image, final Point imageDrawPosn) {
        try {
            final int imageWidth = image.getWidth();
            final int imageHeight = image.getHeight();
            final int pixelOnImageX = p.x - imageDrawPosn.x;
            final int pixelOnImageY = p.y - imageDrawPosn.y;
            if (pixelOnImageX < 0 || pixelOnImageY < 0 || pixelOnImageY > imageHeight || pixelOnImageX > imageWidth) {
                return false;
            }
            final int pixelClicked = image.getRGB(pixelOnImageX, pixelOnImageY);
            if (pixelClicked >> 24 != 0) {
                return true;
            }
        }
        catch (Exception E) {
            return false;
        }
        return false;
    }
}
