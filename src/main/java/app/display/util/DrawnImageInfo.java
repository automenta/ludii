// 
// Decompiled by Procyon v0.5.36
// 

package app.display.util;

import util.ImageInfo;

import java.awt.image.BufferedImage;

public class DrawnImageInfo
{
    private final BufferedImage pieceImage;
    private final ImageInfo imageInfo;
    
    DrawnImageInfo(final BufferedImage pieceImage, final ImageInfo imageInfo) {
        this.pieceImage = pieceImage;
        this.imageInfo = imageInfo;
    }
    
    public BufferedImage pieceImage() {
        return this.pieceImage;
    }
    
    public ImageInfo imageInfo() {
        return this.imageInfo;
    }
}
