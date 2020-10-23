// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.parser;

public interface NumberListHandler
{
    void startNumberList() throws ParseException;
    
    void endNumberList() throws ParseException;
    
    void startNumber() throws ParseException;
    
    void endNumber() throws ParseException;
    
    void numberValue(final float p0) throws ParseException;
}
