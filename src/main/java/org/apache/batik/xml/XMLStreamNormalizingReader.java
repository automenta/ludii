// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.xml;

import java.io.IOException;
import java.io.Reader;
import org.apache.batik.util.io.UTF16Decoder;
import java.io.PushbackInputStream;
import java.io.InputStream;
import org.apache.batik.util.io.StreamNormalizingReader;

public class XMLStreamNormalizingReader extends StreamNormalizingReader
{
    public XMLStreamNormalizingReader(final InputStream is, String encod) throws IOException {
        final PushbackInputStream pbis = new PushbackInputStream(is, 128);
        final byte[] buf = new byte[4];
        final int len = pbis.read(buf);
        if (len > 0) {
            pbis.unread(buf, 0, len);
        }
        if (len == 4) {
            switch (buf[0] & 0xFF) {
                case 0: {
                    if (buf[1] == 60 && buf[2] == 0 && buf[3] == 63) {
                        this.charDecoder = new UTF16Decoder(pbis, true);
                        return;
                    }
                    break;
                }
                case 60: {
                    switch (buf[1] & 0xFF) {
                        case 0: {
                            if (buf[2] == 63 && buf[3] == 0) {
                                this.charDecoder = new UTF16Decoder(pbis, false);
                                return;
                            }
                            break;
                        }
                        case 63: {
                            if (buf[2] == 120 && buf[3] == 109) {
                                final Reader r = XMLUtilities.createXMLDeclarationReader(pbis, "UTF8");
                                final String enc = XMLUtilities.getXMLDeclarationEncoding(r, "UTF-8");
                                this.charDecoder = this.createCharDecoder(pbis, enc);
                                return;
                            }
                            break;
                        }
                    }
                    break;
                }
                case 76: {
                    if (buf[1] == 111 && (buf[2] & 0xFF) == 0xA7 && (buf[3] & 0xFF) == 0x94) {
                        final Reader r = XMLUtilities.createXMLDeclarationReader(pbis, "CP037");
                        final String enc = XMLUtilities.getXMLDeclarationEncoding(r, "EBCDIC-CP-US");
                        this.charDecoder = this.createCharDecoder(pbis, enc);
                        return;
                    }
                    break;
                }
                case 254: {
                    if ((buf[1] & 0xFF) == 0xFF) {
                        this.charDecoder = this.createCharDecoder(pbis, "UTF-16");
                        return;
                    }
                    break;
                }
                case 255: {
                    if ((buf[1] & 0xFF) == 0xFE) {
                        this.charDecoder = this.createCharDecoder(pbis, "UTF-16");
                        return;
                    }
                    break;
                }
            }
        }
        encod = ((encod == null) ? "UTF-8" : encod);
        this.charDecoder = this.createCharDecoder(pbis, encod);
    }
}
