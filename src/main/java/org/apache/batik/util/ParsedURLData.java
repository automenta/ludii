// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.util;

import java.util.LinkedList;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.util.Iterator;
import java.net.MalformedURLException;
import java.io.IOException;
import java.util.zip.ZipException;
import java.util.zip.InflaterInputStream;
import java.util.zip.GZIPInputStream;
import java.io.BufferedInputStream;
import java.net.URL;
import java.io.InputStream;
import java.util.List;

public class ParsedURLData
{
    protected static final String HTTP_USER_AGENT_HEADER = "User-Agent";
    protected static final String HTTP_ACCEPT_HEADER = "Accept";
    protected static final String HTTP_ACCEPT_LANGUAGE_HEADER = "Accept-Language";
    protected static final String HTTP_ACCEPT_ENCODING_HEADER = "Accept-Encoding";
    protected static List acceptedEncodings;
    public static final byte[] GZIP_MAGIC;
    public String protocol;
    public String host;
    public int port;
    public String path;
    public String ref;
    public String contentType;
    public String contentEncoding;
    public InputStream stream;
    public boolean hasBeenOpened;
    protected String contentTypeMediaType;
    protected String contentTypeCharset;
    protected URL postConnectionURL;
    
    public static InputStream checkGZIP(InputStream is) throws IOException {
        if (!is.markSupported()) {
            is = new BufferedInputStream(is);
        }
        final byte[] data = new byte[2];
        try {
            is.mark(2);
            is.read(data);
            is.reset();
        }
        catch (Exception ex) {
            is.reset();
            return is;
        }
        if (data[0] == ParsedURLData.GZIP_MAGIC[0] && data[1] == ParsedURLData.GZIP_MAGIC[1]) {
            return new GZIPInputStream(is);
        }
        if ((data[0] & 0xF) == 0x8 && data[0] >>> 4 <= 7) {
            final int chk = (data[0] & 0xFF) * 256 + (data[1] & 0xFF);
            if (chk % 31 == 0) {
                try {
                    is.mark(100);
                    InputStream ret = new InflaterInputStream(is);
                    if (!ret.markSupported()) {
                        ret = new BufferedInputStream(ret);
                    }
                    ret.mark(2);
                    ret.read(data);
                    is.reset();
                    ret = new InflaterInputStream(is);
                    return ret;
                }
                catch (ZipException ze) {
                    is.reset();
                    return is;
                }
            }
        }
        return is;
    }
    
    public ParsedURLData() {
        this.protocol = null;
        this.host = null;
        this.port = -1;
        this.path = null;
        this.ref = null;
        this.contentType = null;
        this.contentEncoding = null;
        this.stream = null;
        this.hasBeenOpened = false;
    }
    
    public ParsedURLData(final URL url) {
        this.protocol = null;
        this.host = null;
        this.port = -1;
        this.path = null;
        this.ref = null;
        this.contentType = null;
        this.contentEncoding = null;
        this.stream = null;
        this.hasBeenOpened = false;
        this.protocol = url.getProtocol();
        if (this.protocol != null && this.protocol.length() == 0) {
            this.protocol = null;
        }
        this.host = url.getHost();
        if (this.host != null && this.host.length() == 0) {
            this.host = null;
        }
        this.port = url.getPort();
        this.path = url.getFile();
        if (this.path != null && this.path.length() == 0) {
            this.path = null;
        }
        this.ref = url.getRef();
        if (this.ref != null && this.ref.length() == 0) {
            this.ref = null;
        }
    }
    
    protected URL buildURL() throws MalformedURLException {
        if (this.protocol == null || this.host == null) {
            return new URL(this.toString());
        }
        String file = "";
        if (this.path != null) {
            file = this.path;
        }
        if (this.port == -1) {
            return new URL(this.protocol, this.host, file);
        }
        return new URL(this.protocol, this.host, this.port, file);
    }
    
    @Override
    public int hashCode() {
        int hc = this.port;
        if (this.protocol != null) {
            hc ^= this.protocol.hashCode();
        }
        if (this.host != null) {
            hc ^= this.host.hashCode();
        }
        if (this.path != null) {
            final int len = this.path.length();
            if (len > 20) {
                hc ^= this.path.substring(len - 20).hashCode();
            }
            else {
                hc ^= this.path.hashCode();
            }
        }
        if (this.ref != null) {
            final int len = this.ref.length();
            if (len > 20) {
                hc ^= this.ref.substring(len - 20).hashCode();
            }
            else {
                hc ^= this.ref.hashCode();
            }
        }
        return hc;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ParsedURLData)) {
            return false;
        }
        final ParsedURLData ud = (ParsedURLData)obj;
        if (ud.port != this.port) {
            return false;
        }
        if (ud.protocol == null) {
            if (this.protocol != null) {
                return false;
            }
        }
        else {
            if (this.protocol == null) {
                return false;
            }
            if (!ud.protocol.equals(this.protocol)) {
                return false;
            }
        }
        if (ud.host == null) {
            if (this.host != null) {
                return false;
            }
        }
        else {
            if (this.host == null) {
                return false;
            }
            if (!ud.host.equals(this.host)) {
                return false;
            }
        }
        if (ud.ref == null) {
            if (this.ref != null) {
                return false;
            }
        }
        else {
            if (this.ref == null) {
                return false;
            }
            if (!ud.ref.equals(this.ref)) {
                return false;
            }
        }
        if (ud.path == null) {
            if (this.path != null) {
                return false;
            }
        }
        else {
            if (this.path == null) {
                return false;
            }
            if (!ud.path.equals(this.path)) {
                return false;
            }
        }
        return true;
    }
    
    public String getContentType(final String userAgent) {
        if (this.contentType != null) {
            return this.contentType;
        }
        if (!this.hasBeenOpened) {
            try {
                this.openStreamInternal(userAgent, null, null);
            }
            catch (IOException ex) {}
        }
        return this.contentType;
    }
    
    public String getContentTypeMediaType(final String userAgent) {
        if (this.contentTypeMediaType != null) {
            return this.contentTypeMediaType;
        }
        this.extractContentTypeParts(userAgent);
        return this.contentTypeMediaType;
    }
    
    public String getContentTypeCharset(final String userAgent) {
        if (this.contentTypeMediaType != null) {
            return this.contentTypeCharset;
        }
        this.extractContentTypeParts(userAgent);
        return this.contentTypeCharset;
    }
    
    public boolean hasContentTypeParameter(final String userAgent, final String param) {
        this.getContentType(userAgent);
        if (this.contentType == null) {
            return false;
        }
        int i = 0;
        final int len = this.contentType.length();
        final int plen = param.length();
    Label_0081:
        while (i < len) {
            switch (this.contentType.charAt(i)) {
                case ' ':
                case ';': {
                    break Label_0081;
                }
                default: {
                    ++i;
                    continue;
                }
            }
        }
        if (i == len) {
            this.contentTypeMediaType = this.contentType;
        }
        else {
            this.contentTypeMediaType = this.contentType.substring(0, i);
        }
    Label_0111:
        while (true) {
            if (i < len && this.contentType.charAt(i) != ';') {
                ++i;
            }
            else {
                if (i == len) {
                    return false;
                }
                ++i;
                while (i < len && this.contentType.charAt(i) == ' ') {
                    ++i;
                }
                if (i >= len - plen - 1) {
                    return false;
                }
                for (int j = 0; j < plen; ++j) {
                    if (this.contentType.charAt(i++) != param.charAt(j)) {
                        continue Label_0111;
                    }
                }
                if (this.contentType.charAt(i) == '=') {
                    return true;
                }
                continue;
            }
        }
    }
    
    protected void extractContentTypeParts(final String userAgent) {
        this.getContentType(userAgent);
        if (this.contentType == null) {
            return;
        }
        int i = 0;
        final int len = this.contentType.length();
    Label_0073:
        while (i < len) {
            switch (this.contentType.charAt(i)) {
                case ' ':
                case ';': {
                    break Label_0073;
                }
                default: {
                    ++i;
                    continue;
                }
            }
        }
        if (i == len) {
            this.contentTypeMediaType = this.contentType;
        }
        else {
            this.contentTypeMediaType = this.contentType.substring(0, i);
        }
        while (true) {
            if (i < len && this.contentType.charAt(i) != ';') {
                ++i;
            }
            else {
                if (i == len) {
                    return;
                }
                ++i;
                while (i < len && this.contentType.charAt(i) == ' ') {
                    ++i;
                }
                if (i >= len - 8) {
                    return;
                }
                if (this.contentType.charAt(i++) != 'c') {
                    continue;
                }
                if (this.contentType.charAt(i++) != 'h') {
                    continue;
                }
                if (this.contentType.charAt(i++) != 'a') {
                    continue;
                }
                if (this.contentType.charAt(i++) != 'r') {
                    continue;
                }
                if (this.contentType.charAt(i++) != 's') {
                    continue;
                }
                if (this.contentType.charAt(i++) != 'e') {
                    continue;
                }
                if (this.contentType.charAt(i++) != 't') {
                    continue;
                }
                if (this.contentType.charAt(i++) != '=') {
                    continue;
                }
                final int j = i;
            Label_0369:
                while (i < len) {
                    switch (this.contentType.charAt(i)) {
                        case ' ':
                        case ';': {
                            break Label_0369;
                        }
                        default: {
                            ++i;
                            continue;
                        }
                    }
                }
                this.contentTypeCharset = this.contentType.substring(j, i);
            }
        }
    }
    
    public String getContentEncoding(final String userAgent) {
        if (this.contentEncoding != null) {
            return this.contentEncoding;
        }
        if (!this.hasBeenOpened) {
            try {
                this.openStreamInternal(userAgent, null, null);
            }
            catch (IOException ex) {}
        }
        return this.contentEncoding;
    }
    
    public boolean complete() {
        try {
            this.buildURL();
        }
        catch (MalformedURLException mue) {
            return false;
        }
        return true;
    }
    
    public InputStream openStream(final String userAgent, final Iterator mimeTypes) throws IOException {
        final InputStream raw = this.openStreamInternal(userAgent, mimeTypes, ParsedURLData.acceptedEncodings.iterator());
        if (raw == null) {
            return null;
        }
        this.stream = null;
        return checkGZIP(raw);
    }
    
    public InputStream openStreamRaw(final String userAgent, final Iterator mimeTypes) throws IOException {
        final InputStream ret = this.openStreamInternal(userAgent, mimeTypes, null);
        this.stream = null;
        return ret;
    }
    
    protected InputStream openStreamInternal(final String userAgent, final Iterator mimeTypes, final Iterator encodingTypes) throws IOException {
        if (this.stream != null) {
            return this.stream;
        }
        this.hasBeenOpened = true;
        URL url = null;
        try {
            url = this.buildURL();
        }
        catch (MalformedURLException mue) {
            throw new IOException("Unable to make sense of URL for connection");
        }
        if (url == null) {
            return null;
        }
        final URLConnection urlC = url.openConnection();
        if (urlC instanceof HttpURLConnection) {
            if (userAgent != null) {
                urlC.setRequestProperty("User-Agent", userAgent);
            }
            if (mimeTypes != null) {
                String acceptHeader = "";
                while (mimeTypes.hasNext()) {
                    acceptHeader += mimeTypes.next();
                    if (mimeTypes.hasNext()) {
                        acceptHeader += ",";
                    }
                }
                urlC.setRequestProperty("Accept", acceptHeader);
            }
            if (encodingTypes != null) {
                String encodingHeader = "";
                while (encodingTypes.hasNext()) {
                    encodingHeader += encodingTypes.next();
                    if (encodingTypes.hasNext()) {
                        encodingHeader += ",";
                    }
                }
                urlC.setRequestProperty("Accept-Encoding", encodingHeader);
            }
            this.contentType = urlC.getContentType();
            this.contentEncoding = urlC.getContentEncoding();
            this.postConnectionURL = urlC.getURL();
        }
        try {
            return this.stream = urlC.getInputStream();
        }
        catch (IOException e) {
            if (!(urlC instanceof HttpURLConnection)) {
                throw e;
            }
            this.stream = ((HttpURLConnection)urlC).getErrorStream();
            if (this.stream == null) {
                throw e;
            }
            return this.stream;
        }
    }
    
    public String getPortStr() {
        String portStr = "";
        if (this.protocol != null) {
            portStr = portStr + this.protocol + ":";
        }
        if (this.host != null || this.port != -1) {
            portStr += "//";
            if (this.host != null) {
                portStr += this.host;
            }
            if (this.port != -1) {
                portStr = portStr + ":" + this.port;
            }
        }
        return portStr;
    }
    
    protected boolean sameFile(final ParsedURLData other) {
        return this == other || (this.port == other.port && (this.path == other.path || (this.path != null && this.path.equals(other.path))) && (this.host == other.host || (this.host != null && this.host.equals(other.host))) && (this.protocol == other.protocol || (this.protocol != null && this.protocol.equals(other.protocol))));
    }
    
    @Override
    public String toString() {
        String ret = this.getPortStr();
        if (this.path != null) {
            ret += this.path;
        }
        if (this.ref != null) {
            ret = ret + "#" + this.ref;
        }
        return ret;
    }
    
    public String getPostConnectionURL() {
        if (this.postConnectionURL == null) {
            return this.toString();
        }
        if (this.ref != null) {
            return this.postConnectionURL.toString() + '#' + this.ref;
        }
        return this.postConnectionURL.toString();
    }
    
    static {
        (ParsedURLData.acceptedEncodings = new LinkedList()).add("gzip");
        GZIP_MAGIC = new byte[] { 31, -117 };
    }
}
