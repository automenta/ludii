// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.util.io;

import java.util.HashMap;
import org.apache.batik.util.EncodingUtilities;
import java.io.Reader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class StreamNormalizingReader extends NormalizingReader
{
    protected CharDecoder charDecoder;
    protected int nextChar;
    protected int line;
    protected int column;
    protected static final Map charDecoderFactories;
    
    public StreamNormalizingReader(final InputStream is) throws IOException {
        this(is, null);
    }
    
    public StreamNormalizingReader(final InputStream is, String enc) throws IOException {
        this.nextChar = -1;
        this.line = 1;
        if (enc == null) {
            enc = "ISO-8859-1";
        }
        this.charDecoder = this.createCharDecoder(is, enc);
    }
    
    public StreamNormalizingReader(final Reader r) throws IOException {
        this.nextChar = -1;
        this.line = 1;
        this.charDecoder = new GenericDecoder(r);
    }
    
    protected StreamNormalizingReader() {
        this.nextChar = -1;
        this.line = 1;
    }
    
    @Override
    public int read() throws IOException {
        int result = this.nextChar;
        if (result != -1) {
            this.nextChar = -1;
            if (result == 13) {
                this.column = 0;
                ++this.line;
            }
            else {
                ++this.column;
            }
            return result;
        }
        result = this.charDecoder.readChar();
        switch (result) {
            case 13: {
                this.column = 0;
                ++this.line;
                final int c = this.charDecoder.readChar();
                if (c == 10) {
                    return 10;
                }
                this.nextChar = c;
                return 10;
            }
            case 10: {
                this.column = 0;
                ++this.line;
                break;
            }
        }
        return result;
    }
    
    @Override
    public int getLine() {
        return this.line;
    }
    
    @Override
    public int getColumn() {
        return this.column;
    }
    
    @Override
    public void close() throws IOException {
        this.charDecoder.dispose();
        this.charDecoder = null;
    }
    
    protected CharDecoder createCharDecoder(final InputStream is, final String enc) throws IOException {
        final CharDecoderFactory cdf = StreamNormalizingReader.charDecoderFactories.get(enc.toUpperCase());
        if (cdf != null) {
            return cdf.createCharDecoder(is);
        }
        String e = EncodingUtilities.javaEncoding(enc);
        if (e == null) {
            e = enc;
        }
        return new GenericDecoder(is, e);
    }
    
    static {
        charDecoderFactories = new HashMap(11);
        final CharDecoderFactory cdf = new ASCIIDecoderFactory();
        StreamNormalizingReader.charDecoderFactories.put("ASCII", cdf);
        StreamNormalizingReader.charDecoderFactories.put("US-ASCII", cdf);
        StreamNormalizingReader.charDecoderFactories.put("ISO-8859-1", new ISO_8859_1DecoderFactory());
        StreamNormalizingReader.charDecoderFactories.put("UTF-8", new UTF8DecoderFactory());
        StreamNormalizingReader.charDecoderFactories.put("UTF-16", new UTF16DecoderFactory());
    }
    
    protected static class ASCIIDecoderFactory implements CharDecoderFactory
    {
        @Override
        public CharDecoder createCharDecoder(final InputStream is) throws IOException {
            return new ASCIIDecoder(is);
        }
    }
    
    protected static class ISO_8859_1DecoderFactory implements CharDecoderFactory
    {
        @Override
        public CharDecoder createCharDecoder(final InputStream is) throws IOException {
            return new ISO_8859_1Decoder(is);
        }
    }
    
    protected static class UTF8DecoderFactory implements CharDecoderFactory
    {
        @Override
        public CharDecoder createCharDecoder(final InputStream is) throws IOException {
            return new UTF8Decoder(is);
        }
    }
    
    protected static class UTF16DecoderFactory implements CharDecoderFactory
    {
        @Override
        public CharDecoder createCharDecoder(final InputStream is) throws IOException {
            return new UTF16Decoder(is);
        }
    }
    
    protected interface CharDecoderFactory
    {
        CharDecoder createCharDecoder(final InputStream p0) throws IOException;
    }
}
