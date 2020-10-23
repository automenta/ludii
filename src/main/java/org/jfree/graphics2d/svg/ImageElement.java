// 
// Decompiled by Procyon v0.5.36
// 

package org.jfree.graphics2d.svg;

import org.jfree.graphics2d.Args;

import java.awt.*;

public final class ImageElement
{
    private final String href;
    private final Image image;
    
    public ImageElement(final String href, final Image image) {
        Args.nullNotPermitted(href, "href");
        Args.nullNotPermitted(image, "image");
        this.href = href;
        this.image = image;
    }
    
    public String getHref() {
        return this.href;
    }
    
    public Image getImage() {
        return this.image;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ImageElement[");
        sb.append(this.href).append(", ").append(this.image);
        sb.append("]");
        return sb.toString();
    }
}
