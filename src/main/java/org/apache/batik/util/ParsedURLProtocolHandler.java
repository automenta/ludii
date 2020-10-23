// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.util;

public interface ParsedURLProtocolHandler
{
    String getProtocolHandled();
    
    ParsedURLData parseURL(final String p0);
    
    ParsedURLData parseURL(final ParsedURL p0, final String p1);
}
