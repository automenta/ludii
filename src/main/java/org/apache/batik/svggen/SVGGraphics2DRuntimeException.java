// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

public class SVGGraphics2DRuntimeException extends RuntimeException
{
    private Exception embedded;
    
    public SVGGraphics2DRuntimeException(final String s) {
        this(s, null);
    }
    
    public SVGGraphics2DRuntimeException(final Exception ex) {
        this(null, ex);
    }
    
    public SVGGraphics2DRuntimeException(final String s, final Exception ex) {
        super(s);
        this.embedded = ex;
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
    
    public Exception getException() {
        return this.embedded;
    }
}
