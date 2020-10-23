// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public class ParsedURLDataProtocolHandler extends AbstractParsedURLProtocolHandler
{
    static final String DATA_PROTOCOL = "data";
    static final String BASE64 = "base64";
    static final String CHARSET = "charset";
    
    public ParsedURLDataProtocolHandler() {
        super("data");
    }
    
    @Override
    public ParsedURLData parseURL(final ParsedURL baseURL, final String urlStr) {
        return this.parseURL(urlStr);
    }
    
    @Override
    public ParsedURLData parseURL(String urlStr) {
        final DataParsedURLData ret = new DataParsedURLData();
        int pidx = 0;
        int len = urlStr.length();
        int idx = urlStr.indexOf(35);
        ret.ref = null;
        if (idx != -1) {
            if (idx + 1 < len) {
                ret.ref = urlStr.substring(idx + 1);
            }
            urlStr = urlStr.substring(0, idx);
            len = urlStr.length();
        }
        idx = urlStr.indexOf(58);
        if (idx != -1) {
            ret.protocol = urlStr.substring(pidx, idx);
            if (ret.protocol.indexOf(47) == -1) {
                pidx = idx + 1;
            }
            else {
                ret.protocol = null;
                pidx = 0;
            }
        }
        idx = urlStr.indexOf(44, pidx);
        if (idx != -1 && idx != pidx) {
            ret.host = urlStr.substring(pidx, idx);
            pidx = idx + 1;
            int aidx = ret.host.lastIndexOf(59);
            if (aidx == -1 || aidx == ret.host.length()) {
                ret.contentType = ret.host;
            }
            else {
                final String enc = ret.host.substring(aidx + 1);
                idx = enc.indexOf(61);
                if (idx == -1) {
                    ret.contentEncoding = enc;
                    ret.contentType = ret.host.substring(0, aidx);
                }
                else {
                    ret.contentType = ret.host;
                }
                aidx = 0;
                idx = ret.contentType.indexOf(59, aidx);
                if (idx != -1) {
                    for (aidx = idx + 1; aidx < ret.contentType.length(); aidx = idx + 1) {
                        idx = ret.contentType.indexOf(59, aidx);
                        if (idx == -1) {
                            idx = ret.contentType.length();
                        }
                        final String param = ret.contentType.substring(aidx, idx);
                        final int eqIdx = param.indexOf(61);
                        if (eqIdx != -1 && "charset".equals(param.substring(0, eqIdx))) {
                            ret.charset = param.substring(eqIdx + 1);
                        }
                    }
                }
            }
        }
        if (pidx < urlStr.length()) {
            ret.path = urlStr.substring(pidx);
        }
        return ret;
    }
    
    static class DataParsedURLData extends ParsedURLData
    {
        String charset;
        
        @Override
        public boolean complete() {
            return this.path != null;
        }
        
        @Override
        public String getPortStr() {
            String portStr = "data:";
            if (this.host != null) {
                portStr += this.host;
            }
            portStr += ",";
            return portStr;
        }
        
        @Override
        public String toString() {
            String ret = this.getPortStr();
            if (this.path != null) {
                ret += this.path;
            }
            if (this.ref != null) {
                ret = ret + '#' + this.ref;
            }
            return ret;
        }
        
        @Override
        public String getContentType(final String userAgent) {
            return this.contentType;
        }
        
        @Override
        public String getContentEncoding(final String userAgent) {
            return this.contentEncoding;
        }
        
        @Override
        protected InputStream openStreamInternal(final String userAgent, final Iterator mimeTypes, final Iterator encodingTypes) throws IOException {
            this.stream = decode(this.path);
            if ("base64".equals(this.contentEncoding)) {
                this.stream = new Base64DecodeStream(this.stream);
            }
            return this.stream;
        }
        
        public static InputStream decode(final String s) {
            final int len = s.length();
            final byte[] data = new byte[len];
            int j = 0;
            for (int i = 0; i < len; ++i) {
                final char c = s.charAt(i);
                switch (c) {
                    default: {
                        data[j++] = (byte)c;
                        break;
                    }
                    case '%': {
                        if (i + 2 < len) {
                            i += 2;
                            final char c2 = s.charAt(i - 1);
                            byte b;
                            if (c2 >= '0' && c2 <= '9') {
                                b = (byte)(c2 - '0');
                            }
                            else if (c2 >= 'a' && c2 <= 'z') {
                                b = (byte)(c2 - 'a' + 10);
                            }
                            else {
                                if (c2 < 'A' || c2 > 'Z') {
                                    break;
                                }
                                b = (byte)(c2 - 'A' + 10);
                            }
                            b *= 16;
                            final char c3 = s.charAt(i);
                            if (c3 >= '0' && c3 <= '9') {
                                b += (byte)(c3 - '0');
                            }
                            else if (c3 >= 'a' && c3 <= 'z') {
                                b += (byte)(c3 - 'a' + 10);
                            }
                            else {
                                if (c3 < 'A' || c3 > 'Z') {
                                    break;
                                }
                                b += (byte)(c3 - 'A' + 10);
                            }
                            data[j++] = b;
                            break;
                        }
                        break;
                    }
                }
            }
            return new ByteArrayInputStream(data, 0, j);
        }
    }
}
