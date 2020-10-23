// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.util;

import java.io.IOException;
import java.io.InputStream;

public class Base64DecodeStream extends InputStream
{
    InputStream src;
    private static final byte[] pem_array;
    byte[] decode_buffer;
    byte[] out_buffer;
    int out_offset;
    boolean EOF;
    
    public Base64DecodeStream(final InputStream src) {
        this.decode_buffer = new byte[4];
        this.out_buffer = new byte[3];
        this.out_offset = 3;
        this.EOF = false;
        this.src = src;
    }
    
    @Override
    public boolean markSupported() {
        return false;
    }
    
    @Override
    public void close() throws IOException {
        this.EOF = true;
    }
    
    @Override
    public int available() throws IOException {
        return 3 - this.out_offset;
    }
    
    @Override
    public int read() throws IOException {
        if (this.out_offset == 3 && (this.EOF || this.getNextAtom())) {
            this.EOF = true;
            return -1;
        }
        return this.out_buffer[this.out_offset++] & 0xFF;
    }
    
    @Override
    public int read(final byte[] out, final int offset, final int len) throws IOException {
        int idx = 0;
        while (idx < len) {
            if (this.out_offset == 3 && (this.EOF || this.getNextAtom())) {
                this.EOF = true;
                if (idx == 0) {
                    return -1;
                }
                return idx;
            }
            else {
                out[offset + idx] = this.out_buffer[this.out_offset++];
                ++idx;
            }
        }
        return idx;
    }
    
    final boolean getNextAtom() throws IOException {
        int out;
        for (int off = 0; off != 4; off = out) {
            final int count = this.src.read(this.decode_buffer, off, 4 - off);
            if (count == -1) {
                return true;
            }
            int in = off;
            out = off;
            while (in < off + count) {
                if (this.decode_buffer[in] != 10 && this.decode_buffer[in] != 13 && this.decode_buffer[in] != 32) {
                    this.decode_buffer[out++] = this.decode_buffer[in];
                }
                ++in;
            }
        }
        final int a = Base64DecodeStream.pem_array[this.decode_buffer[0] & 0xFF];
        final int b = Base64DecodeStream.pem_array[this.decode_buffer[1] & 0xFF];
        final int c = Base64DecodeStream.pem_array[this.decode_buffer[2] & 0xFF];
        final int d = Base64DecodeStream.pem_array[this.decode_buffer[3] & 0xFF];
        this.out_buffer[0] = (byte)(a << 2 | b >>> 4);
        this.out_buffer[1] = (byte)(b << 4 | c >>> 2);
        this.out_buffer[2] = (byte)(c << 6 | d);
        if (this.decode_buffer[3] != 61) {
            this.out_offset = 0;
        }
        else if (this.decode_buffer[2] == 61) {
            this.out_buffer[2] = this.out_buffer[0];
            this.out_offset = 2;
            this.EOF = true;
        }
        else {
            this.out_buffer[2] = this.out_buffer[1];
            this.out_buffer[1] = this.out_buffer[0];
            this.out_offset = 1;
            this.EOF = true;
        }
        return false;
    }
    
    static {
        pem_array = new byte[256];
        for (int i = 0; i < Base64DecodeStream.pem_array.length; ++i) {
            Base64DecodeStream.pem_array[i] = -1;
        }
        int idx = 0;
        for (char c = 'A'; c <= 'Z'; ++c) {
            Base64DecodeStream.pem_array[c] = (byte)(idx++);
        }
        for (char c = 'a'; c <= 'z'; ++c) {
            Base64DecodeStream.pem_array[c] = (byte)(idx++);
        }
        for (char c = '0'; c <= '9'; ++c) {
            Base64DecodeStream.pem_array[c] = (byte)(idx++);
        }
        Base64DecodeStream.pem_array[43] = (byte)(idx++);
        Base64DecodeStream.pem_array[47] = (byte)(idx++);
    }
}
