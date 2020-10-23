// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import java.io.IOException;

public class SVGGraphics2DIOException extends IOException
{
    private IOException embedded;
    
    public SVGGraphics2DIOException(final String s) {
        this(s, null);
    }
    
    public SVGGraphics2DIOException(final IOException ex) {
        this(null, ex);
    }
    
    public SVGGraphics2DIOException(final String s, final IOException ex) {
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
    
    public IOException getException() {
        return this.embedded;
    }
}
