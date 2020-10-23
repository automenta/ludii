// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt.renderer;

import org.apache.batik.util.Platform;

public class ConcreteImageRendererFactory implements ImageRendererFactory
{
    @Override
    public Renderer createRenderer() {
        return this.createStaticImageRenderer();
    }
    
    @Override
    public ImageRenderer createStaticImageRenderer() {
        if (Platform.isOSX) {
            return new MacRenderer();
        }
        return new StaticRenderer();
    }
    
    @Override
    public ImageRenderer createDynamicImageRenderer() {
        if (Platform.isOSX) {
            return new MacRenderer();
        }
        return new DynamicRenderer();
    }
}
