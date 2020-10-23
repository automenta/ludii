// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.parser;

public class DefaultLengthHandler implements LengthHandler
{
    public static final LengthHandler INSTANCE;
    
    protected DefaultLengthHandler() {
    }
    
    @Override
    public void startLength() throws ParseException {
    }
    
    @Override
    public void lengthValue(final float v) throws ParseException {
    }
    
    @Override
    public void em() throws ParseException {
    }
    
    @Override
    public void ex() throws ParseException {
    }
    
    @Override
    public void in() throws ParseException {
    }
    
    @Override
    public void cm() throws ParseException {
    }
    
    @Override
    public void mm() throws ParseException {
    }
    
    @Override
    public void pc() throws ParseException {
    }
    
    @Override
    public void pt() throws ParseException {
    }
    
    @Override
    public void px() throws ParseException {
    }
    
    @Override
    public void percentage() throws ParseException {
    }
    
    @Override
    public void endLength() throws ParseException {
    }
    
    static {
        INSTANCE = new DefaultLengthHandler();
    }
}
