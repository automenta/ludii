// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.parser;

public class DefaultLengthListHandler extends DefaultLengthHandler implements LengthListHandler
{
    public static final LengthListHandler INSTANCE;
    
    protected DefaultLengthListHandler() {
    }
    
    @Override
    public void startLengthList() throws ParseException {
    }
    
    @Override
    public void endLengthList() throws ParseException {
    }
    
    static {
        INSTANCE = new DefaultLengthListHandler();
    }
}
