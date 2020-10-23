// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.transcoder;

public class TranscoderException extends Exception
{
    protected Exception ex;
    
    public TranscoderException(final String s) {
        this(s, null);
    }
    
    public TranscoderException(final Exception ex) {
        this(null, ex);
    }
    
    public TranscoderException(final String s, final Exception ex) {
        super(s, ex);
        this.ex = ex;
    }
    
    @Override
    public String getMessage() {
        String msg = super.getMessage();
        if (this.ex != null) {
            msg += "\nEnclosed Exception:\n";
            msg += this.ex.getMessage();
        }
        return msg;
    }
    
    public Exception getException() {
        return this.ex;
    }
}
