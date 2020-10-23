// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.script;

public class InterpreterException extends RuntimeException
{
    private int line;
    private int column;
    private Exception embedded;
    
    public InterpreterException(final String message, final int lineno, final int columnno) {
        super(message);
        this.line = -1;
        this.column = -1;
        this.embedded = null;
        this.line = lineno;
        this.column = columnno;
    }
    
    public InterpreterException(final Exception exception, final String message, final int lineno, final int columnno) {
        this(message, lineno, columnno);
        this.embedded = exception;
    }
    
    public int getLineNumber() {
        return this.line;
    }
    
    public int getColumnNumber() {
        return this.column;
    }
    
    public Exception getException() {
        return this.embedded;
    }
    
    @Override
    public String getMessage() {
        final String msg = super.getMessage();
        if (msg != null) {
            return msg;
        }
        if (this.embedded != null) {
            return this.embedded.getMessage();
        }
        return null;
    }
}
