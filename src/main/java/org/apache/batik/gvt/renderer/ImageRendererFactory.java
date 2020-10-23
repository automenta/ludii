// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt.renderer;

public interface ImageRendererFactory extends RendererFactory
{
    ImageRenderer createStaticImageRenderer();
    
    ImageRenderer createDynamicImageRenderer();
}
