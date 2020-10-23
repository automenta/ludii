// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.parser;

import java.io.IOException;

public class NumberListParser extends NumberParser
{
    protected NumberListHandler numberListHandler;
    
    public NumberListParser() {
        this.numberListHandler = DefaultNumberListHandler.INSTANCE;
    }
    
    public void setNumberListHandler(final NumberListHandler handler) {
        this.numberListHandler = handler;
    }
    
    public NumberListHandler getNumberListHandler() {
        return this.numberListHandler;
    }
    
    @Override
    protected void doParse() throws ParseException, IOException {
        this.numberListHandler.startNumberList();
        this.current = this.reader.read();
        this.skipSpaces();
        try {
            do {
                this.numberListHandler.startNumber();
                final float f = this.parseFloat();
                this.numberListHandler.numberValue(f);
                this.numberListHandler.endNumber();
                this.skipCommaSpaces();
            } while (this.current != -1);
        }
        catch (NumberFormatException e) {
            this.reportUnexpectedCharacterError(this.current);
        }
        this.numberListHandler.endNumberList();
    }
}
