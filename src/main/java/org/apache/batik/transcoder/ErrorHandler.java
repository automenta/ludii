// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.transcoder;

public interface ErrorHandler
{
    void error(final TranscoderException p0) throws TranscoderException;
    
    void fatalError(final TranscoderException p0) throws TranscoderException;
    
    void warning(final TranscoderException p0) throws TranscoderException;
}
