// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.util.io;

import java.io.IOException;
import java.io.InputStream;

public class ISO_8859_1Decoder extends AbstractCharDecoder
{
    public ISO_8859_1Decoder(final InputStream is) {
        super(is);
    }
    
    @Override
    public int readChar() throws IOException {
        if (this.position == this.count) {
            this.fillBuffer();
        }
        if (this.count == -1) {
            return -1;
        }
        return this.buffer[this.position++] & 0xFF;
    }
}
