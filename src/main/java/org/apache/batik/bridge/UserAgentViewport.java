// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

public class UserAgentViewport implements Viewport
{
    private UserAgent userAgent;
    
    public UserAgentViewport(final UserAgent userAgent) {
        this.userAgent = userAgent;
    }
    
    @Override
    public float getWidth() {
        return (float)this.userAgent.getViewportSize().getWidth();
    }
    
    @Override
    public float getHeight() {
        return (float)this.userAgent.getViewportSize().getHeight();
    }
}
