// 
// Decompiled by Procyon v0.5.36
// 

package org.w3c.css.sac;

public class CSSException extends RuntimeException
{
    protected String s;
    public static final short SAC_UNSPECIFIED_ERR = 0;
    public static final short SAC_NOT_SUPPORTED_ERR = 1;
    public static final short SAC_SYNTAX_ERR = 2;
    protected static final String S_SAC_UNSPECIFIED_ERR = "unknown error";
    protected static final String S_SAC_NOT_SUPPORTED_ERR = "not supported";
    protected static final String S_SAC_SYNTAX_ERR = "syntax error";
    protected Exception e;
    protected short code;
    
    public CSSException() {
    }
    
    public CSSException(final String s) {
        this.code = 0;
        this.s = s;
    }
    
    public CSSException(final Exception e) {
        this.code = 0;
        this.e = e;
    }
    
    public CSSException(final short code) {
        this.code = code;
    }
    
    public CSSException(final short code, final String s, final Exception e) {
        this.code = code;
        this.s = s;
        this.e = e;
    }
    
    public String getMessage() {
        if (this.s != null) {
            return this.s;
        }
        if (this.e != null) {
            return this.e.getMessage();
        }
        switch (this.code) {
            case 0: {
                return "unknown error";
            }
            case 1: {
                return "not supported";
            }
            case 2: {
                return "syntax error";
            }
            default: {
                return null;
            }
        }
    }
    
    public short getCode() {
        return this.code;
    }
    
    public Exception getException() {
        return this.e;
    }
}
