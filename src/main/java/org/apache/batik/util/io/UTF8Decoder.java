// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.util.io;

import java.io.IOException;
import java.io.InputStream;

public class UTF8Decoder extends AbstractCharDecoder
{
    protected static final byte[] UTF8_BYTES;
    protected int nextChar;
    
    public UTF8Decoder(final InputStream is) {
        super(is);
        this.nextChar = -1;
    }
    
    @Override
    public int readChar() throws IOException {
        if (this.nextChar != -1) {
            final int result = this.nextChar;
            this.nextChar = -1;
            return result;
        }
        if (this.position == this.count) {
            this.fillBuffer();
        }
        if (this.count == -1) {
            return -1;
        }
        final int b1 = this.buffer[this.position++] & 0xFF;
        switch (UTF8Decoder.UTF8_BYTES[b1]) {
            default: {
                this.charError("UTF-8");
                return b1;
            }
            case 1: {
                return b1;
            }
            case 2: {
                if (this.position == this.count) {
                    this.fillBuffer();
                }
                if (this.count == -1) {
                    this.endOfStreamError("UTF-8");
                }
                return (b1 & 0x1F) << 6 | (this.buffer[this.position++] & 0x3F);
            }
            case 3: {
                if (this.position == this.count) {
                    this.fillBuffer();
                }
                if (this.count == -1) {
                    this.endOfStreamError("UTF-8");
                }
                final int b2 = this.buffer[this.position++];
                if (this.position == this.count) {
                    this.fillBuffer();
                }
                if (this.count == -1) {
                    this.endOfStreamError("UTF-8");
                }
                final int b3 = this.buffer[this.position++];
                if ((b2 & 0xC0) != 0x80 || (b3 & 0xC0) != 0x80) {
                    this.charError("UTF-8");
                }
                return (b1 & 0x1F) << 12 | (b2 & 0x3F) << 6 | (b3 & 0x1F);
            }
            case 4: {
                if (this.position == this.count) {
                    this.fillBuffer();
                }
                if (this.count == -1) {
                    this.endOfStreamError("UTF-8");
                }
                final int b2 = this.buffer[this.position++];
                if (this.position == this.count) {
                    this.fillBuffer();
                }
                if (this.count == -1) {
                    this.endOfStreamError("UTF-8");
                }
                final int b3 = this.buffer[this.position++];
                if (this.position == this.count) {
                    this.fillBuffer();
                }
                if (this.count == -1) {
                    this.endOfStreamError("UTF-8");
                }
                final int b4 = this.buffer[this.position++];
                if ((b2 & 0xC0) != 0x80 || (b3 & 0xC0) != 0x80 || (b4 & 0xC0) != 0x80) {
                    this.charError("UTF-8");
                }
                final int c = (b1 & 0x1F) << 18 | (b2 & 0x3F) << 12 | (b3 & 0x1F) << 6 | (b4 & 0x1F);
                this.nextChar = (c - 65536) % 1024 + 56320;
                return (c - 65536) / 1024 + 55296;
            }
        }
    }
    
    static {
        UTF8_BYTES = new byte[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 0, 0, 0, 0, 0, 0, 0, 0 };
    }
}
