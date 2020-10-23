// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.util.io;

import java.io.IOException;
import java.io.InputStream;

public class UTF16Decoder extends AbstractCharDecoder
{
    protected boolean bigEndian;
    
    public UTF16Decoder(final InputStream is) throws IOException {
        super(is);
        final int b1 = is.read();
        if (b1 == -1) {
            this.endOfStreamError("UTF-16");
        }
        final int b2 = is.read();
        if (b2 == -1) {
            this.endOfStreamError("UTF-16");
        }
        final int m = (b1 & 0xFF) << 8 | (b2 & 0xFF);
        switch (m) {
            case 65279: {
                this.bigEndian = true;
                break;
            }
            case 65534: {
                break;
            }
            default: {
                this.charError("UTF-16");
                break;
            }
        }
    }
    
    public UTF16Decoder(final InputStream is, final boolean be) {
        super(is);
        this.bigEndian = be;
    }
    
    @Override
    public int readChar() throws IOException {
        if (this.position == this.count) {
            this.fillBuffer();
        }
        if (this.count == -1) {
            return -1;
        }
        final byte b1 = this.buffer[this.position++];
        if (this.position == this.count) {
            this.fillBuffer();
        }
        if (this.count == -1) {
            this.endOfStreamError("UTF-16");
        }
        final byte b2 = this.buffer[this.position++];
        final int c = this.bigEndian ? ((b1 & 0xFF) << 8 | (b2 & 0xFF)) : ((b2 & 0xFF) << 8 | (b1 & 0xFF));
        if (c == 65534) {
            this.charError("UTF-16");
        }
        return c;
    }
}
