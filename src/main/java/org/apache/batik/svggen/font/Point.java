// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen.font;

public class Point
{
    public int x;
    public int y;
    public boolean onCurve;
    public boolean endOfContour;
    public boolean touched;
    
    public Point(final int x, final int y, final boolean onCurve, final boolean endOfContour) {
        this.x = 0;
        this.y = 0;
        this.onCurve = true;
        this.endOfContour = false;
        this.touched = false;
        this.x = x;
        this.y = y;
        this.onCurve = onCurve;
        this.endOfContour = endOfContour;
    }
}
