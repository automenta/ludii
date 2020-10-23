// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.parser;

import java.io.IOException;
import java.awt.Shape;
import java.io.Reader;
import java.awt.geom.GeneralPath;

public class AWTPolylineProducer implements PointsHandler, ShapeProducer
{
    protected GeneralPath path;
    protected boolean newPath;
    protected int windingRule;
    
    public static Shape createShape(final Reader r, final int wr) throws IOException, ParseException {
        final PointsParser p = new PointsParser();
        final AWTPolylineProducer ph = new AWTPolylineProducer();
        ph.setWindingRule(wr);
        p.setPointsHandler(ph);
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
    public void startPoints() throws ParseException {
        this.path = new GeneralPath(this.windingRule);
        this.newPath = true;
    }
    
    @Override
    public void point(final float x, final float y) throws ParseException {
        if (this.newPath) {
            this.newPath = false;
            this.path.moveTo(x, y);
        }
        else {
            this.path.lineTo(x, y);
        }
    }
    
    @Override
    public void endPoints() throws ParseException {
    }
}
