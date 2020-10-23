// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.parser;

public interface PointsHandler
{
    void startPoints() throws ParseException;
    
    void point(final float p0, final float p1) throws ParseException;
    
    void endPoints() throws ParseException;
}
