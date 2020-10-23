// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt;

import java.awt.geom.Rectangle2D;
import java.awt.image.renderable.RenderableImage;

public interface Mask extends RenderableImage
{
    Rectangle2D getBounds2D();
}
