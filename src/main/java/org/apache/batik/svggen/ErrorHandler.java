// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

public interface ErrorHandler
{
    void handleError(final SVGGraphics2DIOException p0) throws SVGGraphics2DIOException;
    
    void handleError(final SVGGraphics2DRuntimeException p0) throws SVGGraphics2DRuntimeException;
}
