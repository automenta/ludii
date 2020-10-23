// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.util.io;

import java.io.IOException;
import java.io.InputStream;

public class ASCIIDecoder extends AbstractCharDecoder
{
    public ASCIIDecoder(final InputStream is) {
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
        final int result = this.buffer[this.position++];
        if (result < 0) {
            this.charError("ASCII");
        }
        return result;
    }
}
