// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.parser;

public interface LengthHandler
{
    void startLength() throws ParseException;
    
    void lengthValue(final float p0) throws ParseException;
    
    void em() throws ParseException;
    
    void ex() throws ParseException;
    
    void in() throws ParseException;
    
    void cm() throws ParseException;
    
    void mm() throws ParseException;
    
    void pc() throws ParseException;
    
    void pt() throws ParseException;
    
    void px() throws ParseException;
    
    void percentage() throws ParseException;
    
    void endLength() throws ParseException;
}
