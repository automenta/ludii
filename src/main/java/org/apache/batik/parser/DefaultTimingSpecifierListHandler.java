// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.parser;

public class DefaultTimingSpecifierListHandler extends DefaultTimingSpecifierHandler implements TimingSpecifierListHandler
{
    public static final TimingSpecifierListHandler INSTANCE;
    
    protected DefaultTimingSpecifierListHandler() {
    }
    
    @Override
    public void startTimingSpecifierList() {
    }
    
    @Override
    public void endTimingSpecifierList() {
    }
    
    static {
        INSTANCE = new DefaultTimingSpecifierListHandler();
    }
}
