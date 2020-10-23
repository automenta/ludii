// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.parser;

public class DefaultNumberListHandler implements NumberListHandler
{
    public static final NumberListHandler INSTANCE;
    
    protected DefaultNumberListHandler() {
    }
    
    @Override
    public void startNumberList() throws ParseException {
    }
    
    @Override
    public void endNumberList() throws ParseException {
    }
    
    @Override
    public void startNumber() throws ParseException {
    }
    
    @Override
    public void numberValue(final float v) throws ParseException {
    }
    
    @Override
    public void endNumber() throws ParseException {
    }
    
    static {
        INSTANCE = new DefaultNumberListHandler();
    }
}
