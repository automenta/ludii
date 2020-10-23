// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.parser;

public interface AngleHandler
{
    void startAngle() throws ParseException;
    
    void angleValue(final float p0) throws ParseException;
    
    void deg() throws ParseException;
    
    void grad() throws ParseException;
    
    void rad() throws ParseException;
    
    void endAngle() throws ParseException;
}
