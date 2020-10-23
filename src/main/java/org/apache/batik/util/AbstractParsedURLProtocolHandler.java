// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.util;

public abstract class AbstractParsedURLProtocolHandler implements ParsedURLProtocolHandler
{
    protected String protocol;
    
    public AbstractParsedURLProtocolHandler(final String protocol) {
        this.protocol = protocol;
    }
    
    @Override
    public String getProtocolHandled() {
        return this.protocol;
    }
}
