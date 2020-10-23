// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt.text;

import java.awt.geom.Point2D;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import org.apache.batik.ext.awt.geom.PathLength;

public class TextPath
{
    private PathLength pathLength;
    private float startOffset;
    
    public TextPath(final GeneralPath path) {
        this.pathLength = new PathLength(path);
        this.startOffset = 0.0f;
    }
    
    public void setStartOffset(final float startOffset) {
        this.startOffset = startOffset;
    }
    
    public float getStartOffset() {
        return this.startOffset;
    }
    
    public float lengthOfPath() {
        return this.pathLength.lengthOfPath();
    }
    
    public float angleAtLength(final float length) {
        return this.pathLength.angleAtLength(length);
    }
    
    public Point2D pointAtLength(final float length) {
        return this.pathLength.pointAtLength(length);
    }
}
