// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.parser;

public class DefaultPointsHandler implements PointsHandler
{
    public static final DefaultPointsHandler INSTANCE;
    
    protected DefaultPointsHandler() {
    }
    
    @Override
    public void startPoints() throws ParseException {
    }
    
    @Override
    public void point(final float x, final float y) throws ParseException {
    }
    
    @Override
    public void endPoints() throws ParseException {
    }
    
    static {
        INSTANCE = new DefaultPointsHandler();
    }
}
