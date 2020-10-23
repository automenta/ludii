// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.parser;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.awt.Shape;
import java.io.Reader;
import org.apache.batik.ext.awt.geom.ExtendedGeneralPath;

public class AWTPathProducer implements PathHandler, ShapeProducer
{
    protected ExtendedGeneralPath path;
    protected float currentX;
    protected float currentY;
    protected float xCenter;
    protected float yCenter;
    protected int windingRule;
    
    public static Shape createShape(final Reader r, final int wr) throws IOException, ParseException {
        final PathParser p = new PathParser();
        final AWTPathProducer ph = new AWTPathProducer();
        ph.setWindingRule(wr);
        p.setPathHandler(ph);
        p.parse(r);
        return ph.getShape();
    }
    
    @Override
    public void setWindingRule(final int i) {
        this.windingRule = i;
    }
    
    @Override
    public int getWindingRule() {
        return this.windingRule;
    }
    
    @Override
    public Shape getShape() {
        return this.path;
    }
    
    @Override
    public void startPath() throws ParseException {
        this.currentX = 0.0f;
        this.currentY = 0.0f;
        this.xCenter = 0.0f;
        this.yCenter = 0.0f;
        this.path = new ExtendedGeneralPath(this.windingRule);
    }
    
    @Override
    public void endPath() throws ParseException {
    }
    
    @Override
    public void movetoRel(final float x, final float y) throws ParseException {
        final ExtendedGeneralPath path = this.path;
        final float x2 = this.currentX + x;
        this.currentX = x2;
        this.xCenter = x2;
        final float n = this.currentY + y;
        this.currentY = n;
        path.moveTo(x2, this.yCenter = n);
    }
    
    @Override
    public void movetoAbs(final float x, final float y) throws ParseException {
        final ExtendedGeneralPath path = this.path;
        this.currentX = x;
        this.xCenter = x;
        this.currentY = y;
        path.moveTo(x, this.yCenter = y);
    }
    
    @Override
    public void closePath() throws ParseException {
        this.path.closePath();
        final Point2D pt = this.path.getCurrentPoint();
        this.currentX = (float)pt.getX();
        this.currentY = (float)pt.getY();
    }
    
    @Override
    public void linetoRel(final float x, final float y) throws ParseException {
        final ExtendedGeneralPath path = this.path;
        final float x2 = this.currentX + x;
        this.currentX = x2;
        this.xCenter = x2;
        final float n = this.currentY + y;
        this.currentY = n;
        path.lineTo(x2, this.yCenter = n);
    }
    
    @Override
    public void linetoAbs(final float x, final float y) throws ParseException {
        final ExtendedGeneralPath path = this.path;
        this.currentX = x;
        this.xCenter = x;
        this.currentY = y;
        path.lineTo(x, this.yCenter = y);
    }
    
    @Override
    public void linetoHorizontalRel(final float x) throws ParseException {
        final ExtendedGeneralPath path = this.path;
        final float n = this.currentX + x;
        this.currentX = n;
        path.lineTo(this.xCenter = n, this.yCenter = this.currentY);
    }
    
    @Override
    public void linetoHorizontalAbs(final float x) throws ParseException {
        final ExtendedGeneralPath path = this.path;
        this.currentX = x;
        path.lineTo(this.xCenter = x, this.yCenter = this.currentY);
    }
    
    @Override
    public void linetoVerticalRel(final float y) throws ParseException {
        final ExtendedGeneralPath path = this.path;
        final float currentX = this.currentX;
        this.xCenter = currentX;
        final float n = this.currentY + y;
        this.currentY = n;
        path.lineTo(currentX, this.yCenter = n);
    }
    
    @Override
    public void linetoVerticalAbs(final float y) throws ParseException {
        final ExtendedGeneralPath path = this.path;
        final float currentX = this.currentX;
        this.xCenter = currentX;
        this.currentY = y;
        path.lineTo(currentX, this.yCenter = y);
    }
    
    @Override
    public void curvetoCubicRel(final float x1, final float y1, final float x2, final float y2, final float x, final float y) throws ParseException {
        this.path.curveTo(this.currentX + x1, this.currentY + y1, this.xCenter = this.currentX + x2, this.yCenter = this.currentY + y2, this.currentX += x, this.currentY += y);
    }
    
    @Override
    public void curvetoCubicAbs(final float x1, final float y1, final float x2, final float y2, final float x, final float y) throws ParseException {
        this.path.curveTo(x1, y1, this.xCenter = x2, this.yCenter = y2, this.currentX = x, this.currentY = y);
    }
    
    @Override
    public void curvetoCubicSmoothRel(final float x2, final float y2, final float x, final float y) throws ParseException {
        this.path.curveTo(this.currentX * 2.0f - this.xCenter, this.currentY * 2.0f - this.yCenter, this.xCenter = this.currentX + x2, this.yCenter = this.currentY + y2, this.currentX += x, this.currentY += y);
    }
    
    @Override
    public void curvetoCubicSmoothAbs(final float x2, final float y2, final float x, final float y) throws ParseException {
        this.path.curveTo(this.currentX * 2.0f - this.xCenter, this.currentY * 2.0f - this.yCenter, this.xCenter = x2, this.yCenter = y2, this.currentX = x, this.currentY = y);
    }
    
    @Override
    public void curvetoQuadraticRel(final float x1, final float y1, final float x, final float y) throws ParseException {
        this.path.quadTo(this.xCenter = this.currentX + x1, this.yCenter = this.currentY + y1, this.currentX += x, this.currentY += y);
    }
    
    @Override
    public void curvetoQuadraticAbs(final float x1, final float y1, final float x, final float y) throws ParseException {
        this.path.quadTo(this.xCenter = x1, this.yCenter = y1, this.currentX = x, this.currentY = y);
    }
    
    @Override
    public void curvetoQuadraticSmoothRel(final float x, final float y) throws ParseException {
        this.path.quadTo(this.xCenter = this.currentX * 2.0f - this.xCenter, this.yCenter = this.currentY * 2.0f - this.yCenter, this.currentX += x, this.currentY += y);
    }
    
    @Override
    public void curvetoQuadraticSmoothAbs(final float x, final float y) throws ParseException {
        this.path.quadTo(this.xCenter = this.currentX * 2.0f - this.xCenter, this.yCenter = this.currentY * 2.0f - this.yCenter, this.currentX = x, this.currentY = y);
    }
    
    @Override
    public void arcRel(final float rx, final float ry, final float xAxisRotation, final boolean largeArcFlag, final boolean sweepFlag, final float x, final float y) throws ParseException {
        final ExtendedGeneralPath path = this.path;
        final float x2 = this.currentX + x;
        this.currentX = x2;
        this.xCenter = x2;
        final float n = this.currentY + y;
        this.currentY = n;
        path.arcTo(rx, ry, xAxisRotation, largeArcFlag, sweepFlag, x2, this.yCenter = n);
    }
    
    @Override
    public void arcAbs(final float rx, final float ry, final float xAxisRotation, final boolean largeArcFlag, final boolean sweepFlag, final float x, final float y) throws ParseException {
        final ExtendedGeneralPath path = this.path;
        this.currentX = x;
        this.xCenter = x;
        this.currentY = y;
        path.arcTo(rx, ry, xAxisRotation, largeArcFlag, sweepFlag, x, this.yCenter = y);
    }
}
