// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.parser;

public class ParseException extends RuntimeException
{
    protected Exception exception;
    protected int lineNumber;
    protected int columnNumber;
    
    public ParseException(final String message, final int line, final int column) {
        super(message);
        this.exception = null;
        this.lineNumber = line;
        this.columnNumber = column;
    }
    
    public ParseException(final Exception e) {
        this.exception = e;
        this.lineNumber = -1;
        this.columnNumber = -1;
    }
    
    public ParseException(final String message, final Exception e) {
        super(message);
        this.exception = e;
    }
    
    @Override
    public String getMessage() {
        final String message = super.getMessage();
        if (message == null && this.exception != null) {
            return this.exception.getMessage();
        }
        return message;
    }
    
    public Exception getException() {
        return this.exception;
    }
    
    public int getLineNumber() {
        return this.lineNumber;
    }
    
    public int getColumnNumber() {
        return this.columnNumber;
    }
}
