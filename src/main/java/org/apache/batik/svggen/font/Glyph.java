// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen.font;

import org.apache.batik.svggen.font.table.GlyphDescription;

public class Glyph
{
    protected short leftSideBearing;
    protected int advanceWidth;
    private Point[] points;
    
    public Glyph(final GlyphDescription gd, final short lsb, final int advance) {
        this.leftSideBearing = lsb;
        this.advanceWidth = advance;
        this.describe(gd);
    }
    
    public int getAdvanceWidth() {
        return this.advanceWidth;
    }
    
    public short getLeftSideBearing() {
        return this.leftSideBearing;
    }
    
    public Point getPoint(final int i) {
        return this.points[i];
    }
    
    public int getPointCount() {
        return this.points.length;
    }
    
    public void reset() {
    }
    
    public void scale(final int factor) {
        for (final Point point : this.points) {
            point.x = (point.x << 10) * factor >> 26;
            point.y = (point.y << 10) * factor >> 26;
        }
        this.leftSideBearing = (short)(this.leftSideBearing * factor >> 6);
        this.advanceWidth = this.advanceWidth * factor >> 6;
    }
    
    private void describe(final GlyphDescription gd) {
        int endPtIndex = 0;
        this.points = new Point[gd.getPointCount() + 2];
        for (int i = 0; i < gd.getPointCount(); ++i) {
            final boolean endPt = gd.getEndPtOfContours(endPtIndex) == i;
            if (endPt) {
                ++endPtIndex;
            }
            this.points[i] = new Point(gd.getXCoordinate(i), gd.getYCoordinate(i), (gd.getFlags(i) & 0x1) != 0x0, endPt);
        }
        this.points[gd.getPointCount()] = new Point(0, 0, true, true);
        this.points[gd.getPointCount() + 1] = new Point(this.advanceWidth, 0, true, true);
    }
}
