// 
// Decompiled by Procyon v0.5.36
// 

package org.w3c.css.sac;

public class CSSParseException extends CSSException
{
    private final String uri;
    private final int lineNumber;
    private final int columnNumber;
    
    public CSSParseException(final String s, final Locator locator) {
        super(s);
        super.code = 2;
        this.uri = locator.getURI();
        this.lineNumber = locator.getLineNumber();
        this.columnNumber = locator.getColumnNumber();
    }
    
    public CSSParseException(final String s, final Locator locator, final Exception ex) {
        super((short)2, s, ex);
        this.uri = locator.getURI();
        this.lineNumber = locator.getLineNumber();
        this.columnNumber = locator.getColumnNumber();
    }
    
    public CSSParseException(final String s, final String uri, final int lineNumber, final int columnNumber) {
        super(s);
        super.code = 2;
        this.uri = uri;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }
    
    public CSSParseException(final String s, final String uri, final int lineNumber, final int columnNumber, final Exception ex) {
        super((short)2, s, ex);
        this.uri = uri;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }
    
    public String getURI() {
        return this.uri;
    }
    
    public int getLineNumber() {
        return this.lineNumber;
    }
    
    public int getColumnNumber() {
        return this.columnNumber;
    }
}
