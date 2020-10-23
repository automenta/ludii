// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.parser;

public class DefaultAngleHandler implements AngleHandler
{
    public static final AngleHandler INSTANCE;
    
    protected DefaultAngleHandler() {
    }
    
    @Override
    public void startAngle() throws ParseException {
    }
    
    @Override
    public void angleValue(final float v) throws ParseException {
    }
    
    @Override
    public void deg() throws ParseException {
    }
    
    @Override
    public void grad() throws ParseException {
    }
    
    @Override
    public void rad() throws ParseException {
    }
    
    @Override
    public void endAngle() throws ParseException {
    }
    
    static {
        INSTANCE = new DefaultAngleHandler();
    }
}
