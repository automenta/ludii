// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.util;

import java.io.IOException;
import java.io.PrintStream;
import java.io.OutputStream;

public class Base64EncoderStream extends OutputStream
{
    private static final byte[] pem_array;
    byte[] atom;
    int atomLen;
    byte[] encodeBuf;
    int lineLen;
    PrintStream out;
    boolean closeOutOnClose;
    
    public Base64EncoderStream(final OutputStream out) {
        this.atom = new byte[3];
        this.atomLen = 0;
        this.encodeBuf = new byte[4];
        this.lineLen = 0;
        this.out = new PrintStream(out);
        this.closeOutOnClose = true;
    }
    
    public Base64EncoderStream(final OutputStream out, final boolean closeOutOnClose) {
        this.atom = new byte[3];
        this.atomLen = 0;
        this.encodeBuf = new byte[4];
        this.lineLen = 0;
        this.out = new PrintStream(out);
        this.closeOutOnClose = closeOutOnClose;
    }
    
    @Override
    public void close() throws IOException {
        if (this.out != null) {
            this.encodeAtom();
            this.out.flush();
            if (this.closeOutOnClose) {
                this.out.close();
            }
            this.out = null;
        }
    }
    
    @Override
    public void flush() throws IOException {
        this.out.flush();
    }
    
    @Override
    public void write(final int b) throws IOException {
        this.atom[this.atomLen++] = (byte)b;
        if (this.atomLen == 3) {
            this.encodeAtom();
        }
    }
    
    @Override
    public void write(final byte[] data) throws IOException {
        this.encodeFromArray(data, 0, data.length);
    }
    
    @Override
    public void write(final byte[] data, final int off, final int len) throws IOException {
        this.encodeFromArray(data, off, len);
    }
    
    void encodeAtom() throws IOException {
        switch (this.atomLen) {
            case 0: {
                return;
            }
            case 1: {
                final byte a = this.atom[0];
                this.encodeBuf[0] = Base64EncoderStream.pem_array[a >>> 2 & 0x3F];
                this.encodeBuf[1] = Base64EncoderStream.pem_array[a << 4 & 0x30];
                this.encodeBuf[2] = (this.encodeBuf[3] = 61);
                break;
            }
            case 2: {
                final byte a = this.atom[0];
                final byte b = this.atom[1];
                this.encodeBuf[0] = Base64EncoderStream.pem_array[a >>> 2 & 0x3F];
                this.encodeBuf[1] = Base64EncoderStream.pem_array[(a << 4 & 0x30) | (b >>> 4 & 0xF)];
                this.encodeBuf[2] = Base64EncoderStream.pem_array[b << 2 & 0x3C];
                this.encodeBuf[3] = 61;
                break;
            }
            default: {
                final byte a = this.atom[0];
                final byte b = this.atom[1];
                final byte c = this.atom[2];
                this.encodeBuf[0] = Base64EncoderStream.pem_array[a >>> 2 & 0x3F];
                this.encodeBuf[1] = Base64EncoderStream.pem_array[(a << 4 & 0x30) | (b >>> 4 & 0xF)];
                this.encodeBuf[2] = Base64EncoderStream.pem_array[(b << 2 & 0x3C) | (c >>> 6 & 0x3)];
                this.encodeBuf[3] = Base64EncoderStream.pem_array[c & 0x3F];
                break;
            }
        }
        if (this.lineLen == 64) {
            this.out.println();
            this.lineLen = 0;
        }
        this.out.write(this.encodeBuf);
        this.lineLen += 4;
        this.atomLen = 0;
    }
    
    void encodeFromArray(final byte[] data, int offset, int len) throws IOException {
        if (len == 0) {
            return;
        }
        if (this.atomLen != 0) {
            switch (this.atomLen) {
                case 1: {
                    this.atom[1] = data[offset++];
                    --len;
                    ++this.atomLen;
                    if (len == 0) {
                        return;
                    }
                    this.atom[2] = data[offset++];
                    --len;
                    ++this.atomLen;
                    break;
                }
                case 2: {
                    this.atom[2] = data[offset++];
                    --len;
                    ++this.atomLen;
                    break;
                }
            }
            this.encodeAtom();
        }
        while (len >= 3) {
            final byte a = data[offset++];
            final byte b = data[offset++];
            final byte c = data[offset++];
            this.encodeBuf[0] = Base64EncoderStream.pem_array[a >>> 2 & 0x3F];
            this.encodeBuf[1] = Base64EncoderStream.pem_array[(a << 4 & 0x30) | (b >>> 4 & 0xF)];
            this.encodeBuf[2] = Base64EncoderStream.pem_array[(b << 2 & 0x3C) | (c >>> 6 & 0x3)];
            this.encodeBuf[3] = Base64EncoderStream.pem_array[c & 0x3F];
            this.out.write(this.encodeBuf);
            this.lineLen += 4;
            if (this.lineLen == 64) {
                this.out.println();
                this.lineLen = 0;
            }
            len -= 3;
        }
        switch (len) {
            case 1: {
                this.atom[0] = data[offset];
                break;
            }
            case 2: {
                this.atom[0] = data[offset];
                this.atom[1] = data[offset + 1];
                break;
            }
        }
        this.atomLen = len;
    }
    
    static {
        pem_array = new byte[] { 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47 };
    }
}
