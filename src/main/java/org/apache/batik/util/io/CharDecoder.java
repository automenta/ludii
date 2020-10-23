// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.util.io;

import java.io.IOException;

public interface CharDecoder
{
    public static final int END_OF_STREAM = -1;
    
    int readChar() throws IOException;
    
    void dispose() throws IOException;
}
