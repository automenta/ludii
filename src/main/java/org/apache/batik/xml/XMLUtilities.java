// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.xml;

import org.apache.batik.util.EncodingUtilities;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.io.Reader;
import java.io.InputStream;

public class XMLUtilities extends XMLCharacters
{
    public static final int IS_XML_10_NAME = 1;
    public static final int IS_XML_10_QNAME = 2;
    
    protected XMLUtilities() {
    }
    
    public static boolean isXMLSpace(final char c) {
        return c <= ' ' && (4294977024L >> c & 0x1L) != 0x0L;
    }
    
    public static boolean isXMLNameFirstCharacter(final char c) {
        return (XMLUtilities.NAME_FIRST_CHARACTER[c / ' '] & 1 << c % ' ') != 0x0;
    }
    
    public static boolean isXML11NameFirstCharacter(final char c) {
        return (XMLUtilities.NAME11_FIRST_CHARACTER[c / ' '] & 1 << c % ' ') != 0x0;
    }
    
    public static boolean isXMLNameCharacter(final char c) {
        return (XMLUtilities.NAME_CHARACTER[c / ' '] & 1 << c % ' ') != 0x0;
    }
    
    public static boolean isXML11NameCharacter(final char c) {
        return (XMLUtilities.NAME11_CHARACTER[c / ' '] & 1 << c % ' ') != 0x0;
    }
    
    public static boolean isXMLCharacter(final int c) {
        return (XMLUtilities.XML_CHARACTER[c >>> 5] & 1 << (c & 0x1F)) != 0x0 || (c >= 65536 && c <= 1114111);
    }
    
    public static boolean isXML11Character(final int c) {
        return (c >= 1 && c <= 55295) || (c >= 57344 && c <= 65533) || (c >= 65536 && c <= 1114111);
    }
    
    public static boolean isXMLPublicIdCharacter(final char c) {
        return c < '\u0080' && (XMLUtilities.PUBLIC_ID_CHARACTER[c / ' '] & 1 << c % ' ') != 0x0;
    }
    
    public static boolean isXMLVersionCharacter(final char c) {
        return c < '\u0080' && (XMLUtilities.VERSION_CHARACTER[c / ' '] & 1 << c % ' ') != 0x0;
    }
    
    public static boolean isXMLAlphabeticCharacter(final char c) {
        return c < '\u0080' && (XMLUtilities.ALPHABETIC_CHARACTER[c / ' '] & 1 << c % ' ') != 0x0;
    }
    
    public static int testXMLQName(final String s) {
        int isQName = 2;
        boolean foundColon = false;
        final int len = s.length();
        if (len == 0) {
            return 0;
        }
        char c = s.charAt(0);
        if (!isXMLNameFirstCharacter(c)) {
            return 0;
        }
        if (c == ':') {
            isQName = 0;
        }
        for (int i = 1; i < len; ++i) {
            c = s.charAt(i);
            if (!isXMLNameCharacter(c)) {
                return 0;
            }
            if (isQName != 0 && c == ':') {
                if (foundColon || i == len - 1) {
                    isQName = 0;
                }
                else {
                    foundColon = true;
                }
            }
        }
        return 0x1 | isQName;
    }
    
    public static Reader createXMLDocumentReader(final InputStream is) throws IOException {
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
                        return new InputStreamReader(pbis, "UnicodeBig");
                    }
                    break;
                }
                case 60: {
                    switch (buf[1] & 0xFF) {
                        case 0: {
                            if (buf[2] == 63 && buf[3] == 0) {
                                return new InputStreamReader(pbis, "UnicodeLittle");
                            }
                            break;
                        }
                        case 63: {
                            if (buf[2] == 120 && buf[3] == 109) {
                                final Reader r = createXMLDeclarationReader(pbis, "UTF8");
                                final String enc = getXMLDeclarationEncoding(r, "UTF8");
                                return new InputStreamReader(pbis, enc);
                            }
                            break;
                        }
                    }
                    break;
                }
                case 76: {
                    if (buf[1] == 111 && (buf[2] & 0xFF) == 0xA7 && (buf[3] & 0xFF) == 0x94) {
                        final Reader r = createXMLDeclarationReader(pbis, "CP037");
                        final String enc = getXMLDeclarationEncoding(r, "CP037");
                        return new InputStreamReader(pbis, enc);
                    }
                    break;
                }
                case 254: {
                    if ((buf[1] & 0xFF) == 0xFF) {
                        return new InputStreamReader(pbis, "Unicode");
                    }
                    break;
                }
                case 255: {
                    if ((buf[1] & 0xFF) == 0xFE) {
                        return new InputStreamReader(pbis, "Unicode");
                    }
                    break;
                }
            }
        }
        return new InputStreamReader(pbis, "UTF8");
    }
    
    protected static Reader createXMLDeclarationReader(final PushbackInputStream pbis, final String enc) throws IOException {
        final byte[] buf = new byte[128];
        final int len = pbis.read(buf);
        if (len > 0) {
            pbis.unread(buf, 0, len);
        }
        return new InputStreamReader(new ByteArrayInputStream(buf, 4, len), enc);
    }
    
    protected static String getXMLDeclarationEncoding(final Reader r, final String e) throws IOException {
        int c;
        if ((c = r.read()) != 108) {
            return e;
        }
        if (!isXMLSpace((char)(c = r.read()))) {
            return e;
        }
        while (isXMLSpace((char)(c = r.read()))) {}
        if (c != 118) {
            return e;
        }
        if ((c = r.read()) != 101) {
            return e;
        }
        if ((c = r.read()) != 114) {
            return e;
        }
        if ((c = r.read()) != 115) {
            return e;
        }
        if ((c = r.read()) != 105) {
            return e;
        }
        if ((c = r.read()) != 111) {
            return e;
        }
        if ((c = r.read()) != 110) {
            return e;
        }
        for (c = r.read(); isXMLSpace((char)c); c = r.read()) {}
        if (c != 61) {
            return e;
        }
        while (isXMLSpace((char)(c = r.read()))) {}
        if (c != 34 && c != 39) {
            return e;
        }
        char sc = (char)c;
        do {
            c = r.read();
            if (c == sc) {
                if (!isXMLSpace((char)(c = r.read()))) {
                    return e;
                }
                while (isXMLSpace((char)(c = r.read()))) {}
                if (c != 101) {
                    return e;
                }
                if ((c = r.read()) != 110) {
                    return e;
                }
                if ((c = r.read()) != 99) {
                    return e;
                }
                if ((c = r.read()) != 111) {
                    return e;
                }
                if ((c = r.read()) != 100) {
                    return e;
                }
                if ((c = r.read()) != 105) {
                    return e;
                }
                if ((c = r.read()) != 110) {
                    return e;
                }
                if ((c = r.read()) != 103) {
                    return e;
                }
                for (c = r.read(); isXMLSpace((char)c); c = r.read()) {}
                if (c != 61) {
                    return e;
                }
                while (isXMLSpace((char)(c = r.read()))) {}
                if (c != 34 && c != 39) {
                    return e;
                }
                sc = (char)c;
                final StringBuffer enc = new StringBuffer();
                while (true) {
                    c = r.read();
                    if (c == -1) {
                        return e;
                    }
                    if (c == sc) {
                        return encodingToJavaEncoding(enc.toString(), e);
                    }
                    enc.append((char)c);
                }
            }
        } while (isXMLVersionCharacter((char)c));
        return e;
    }
    
    public static String encodingToJavaEncoding(final String e, final String de) {
        final String result = EncodingUtilities.javaEncoding(e);
        return (result == null) ? de : result;
    }
}
