// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.transcoder;

import java.util.Map;

public interface Transcoder
{
    void transcode(final TranscoderInput p0, final TranscoderOutput p1) throws TranscoderException;
    
    TranscodingHints getTranscodingHints();
    
    void addTranscodingHint(final TranscodingHints.Key p0, final Object p1);
    
    void removeTranscodingHint(final TranscodingHints.Key p0);
    
    void setTranscodingHints(final Map p0);
    
    void setTranscodingHints(final TranscodingHints p0);
    
    void setErrorHandler(final ErrorHandler p0);
    
    ErrorHandler getErrorHandler();
}
