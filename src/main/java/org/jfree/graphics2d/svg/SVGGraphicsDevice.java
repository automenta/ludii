// 
// Decompiled by Procyon v0.5.36
// 

package org.jfree.graphics2d.svg;

import java.awt.*;

public class SVGGraphicsDevice extends GraphicsDevice
{
    private final String id;
    GraphicsConfiguration defaultConfig;
    
    public SVGGraphicsDevice(final String id, final GraphicsConfiguration defaultConfig) {
        this.id = id;
        this.defaultConfig = defaultConfig;
    }
    
    @Override
    public int getType() {
        return 1;
    }
    
    @Override
    public String getIDstring() {
        return this.id;
    }
    
    @Override
    public GraphicsConfiguration[] getConfigurations() {
        return new GraphicsConfiguration[] { this.getDefaultConfiguration() };
    }
    
    @Override
    public GraphicsConfiguration getDefaultConfiguration() {
        return this.defaultConfig;
    }
}
