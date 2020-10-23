// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.parser;

public class DefaultPreserveAspectRatioHandler implements PreserveAspectRatioHandler
{
    public static final PreserveAspectRatioHandler INSTANCE;
    
    protected DefaultPreserveAspectRatioHandler() {
    }
    
    @Override
    public void startPreserveAspectRatio() throws ParseException {
    }
    
    @Override
    public void none() throws ParseException {
    }
    
    @Override
    public void xMaxYMax() throws ParseException {
    }
    
    @Override
    public void xMaxYMid() throws ParseException {
    }
    
    @Override
    public void xMaxYMin() throws ParseException {
    }
    
    @Override
    public void xMidYMax() throws ParseException {
    }
    
    @Override
    public void xMidYMid() throws ParseException {
    }
    
    @Override
    public void xMidYMin() throws ParseException {
    }
    
    @Override
    public void xMinYMax() throws ParseException {
    }
    
    @Override
    public void xMinYMid() throws ParseException {
    }
    
    @Override
    public void xMinYMin() throws ParseException {
    }
    
    @Override
    public void meet() throws ParseException {
    }
    
    @Override
    public void slice() throws ParseException {
    }
    
    @Override
    public void endPreserveAspectRatio() throws ParseException {
    }
    
    static {
        INSTANCE = new DefaultPreserveAspectRatioHandler();
    }
}
