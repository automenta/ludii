// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

public class DefaultErrorHandler implements ErrorHandler
{
    @Override
    public void handleError(final SVGGraphics2DIOException ex) throws SVGGraphics2DIOException {
        throw ex;
    }
    
    @Override
    public void handleError(final SVGGraphics2DRuntimeException ex) throws SVGGraphics2DRuntimeException {
        System.err.println(ex.getMessage());
    }
}
