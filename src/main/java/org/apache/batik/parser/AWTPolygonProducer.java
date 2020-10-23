// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.parser;

import java.io.IOException;
import java.awt.Shape;
import java.io.Reader;

public class AWTPolygonProducer extends AWTPolylineProducer
{
    public static Shape createShape(final Reader r, final int wr) throws IOException, ParseException {
        final PointsParser p = new PointsParser();
        final AWTPolygonProducer ph = new AWTPolygonProducer();
        ph.setWindingRule(wr);
        p.setPointsHandler(ph);
        p.parse(r);
        return ph.getShape();
    }
    
    @Override
    public void endPoints() throws ParseException {
        this.path.closePath();
    }
}
