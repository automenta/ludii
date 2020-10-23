// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.util;

import org.xml.sax.SAXException;
import java.io.IOException;

public class SAXIOException extends IOException
{
    protected SAXException saxe;
    
    public SAXIOException(final SAXException saxe) {
        super(saxe.getMessage());
        this.saxe = saxe;
    }
    
    public SAXException getSAXException() {
        return this.saxe;
    }
    
    @Override
    public Throwable getCause() {
        return this.saxe;
    }
}
