// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

public class Location implements org.apache.batik.w3c.dom.Location
{
    private BridgeContext bridgeContext;
    
    public Location(final BridgeContext ctx) {
        this.bridgeContext = ctx;
    }
    
    @Override
    public void assign(final String url) {
        this.bridgeContext.getUserAgent().loadDocument(url);
    }
    
    @Override
    public void reload() {
        final String url = this.bridgeContext.getDocument().getDocumentURI();
        this.bridgeContext.getUserAgent().loadDocument(url);
    }
    
    @Override
    public String toString() {
        return this.bridgeContext.getDocument().getDocumentURI();
    }
}
