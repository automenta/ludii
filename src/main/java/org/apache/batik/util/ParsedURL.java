// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.util;

import org.apache.batik.Version;
import java.util.Collection;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.net.URL;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

public class ParsedURL
{
    ParsedURLData data;
    String userAgent;
    private static Map handlersMap;
    private static ParsedURLProtocolHandler defaultHandler;
    private static String globalUserAgent;
    
    public static String getGlobalUserAgent() {
        return ParsedURL.globalUserAgent;
    }
    
    public static void setGlobalUserAgent(final String userAgent) {
        ParsedURL.globalUserAgent = userAgent;
    }
    
    private static synchronized Map getHandlersMap() {
        if (ParsedURL.handlersMap != null) {
            return ParsedURL.handlersMap;
        }
        ParsedURL.handlersMap = new HashMap();
        registerHandler(new ParsedURLDataProtocolHandler());
        registerHandler(new ParsedURLJarProtocolHandler());
        final Iterator iter = Service.providers(ParsedURLProtocolHandler.class);
        while (iter.hasNext()) {
            final ParsedURLProtocolHandler handler = iter.next();
            registerHandler(handler);
        }
        return ParsedURL.handlersMap;
    }
    
    public static synchronized ParsedURLProtocolHandler getHandler(final String protocol) {
        if (protocol == null) {
            return ParsedURL.defaultHandler;
        }
        final Map handlers = getHandlersMap();
        ParsedURLProtocolHandler ret = handlers.get(protocol);
        if (ret == null) {
            ret = ParsedURL.defaultHandler;
        }
        return ret;
    }
    
    public static synchronized void registerHandler(final ParsedURLProtocolHandler handler) {
        if (handler.getProtocolHandled() == null) {
            ParsedURL.defaultHandler = handler;
            return;
        }
        final Map handlers = getHandlersMap();
        handlers.put(handler.getProtocolHandled(), handler);
    }
    
    public static InputStream checkGZIP(final InputStream is) throws IOException {
        return ParsedURLData.checkGZIP(is);
    }
    
    public ParsedURL(final String urlStr) {
        this.userAgent = getGlobalUserAgent();
        this.data = parseURL(urlStr);
    }
    
    public ParsedURL(final URL url) {
        this.userAgent = getGlobalUserAgent();
        this.data = new ParsedURLData(url);
    }
    
    public ParsedURL(final String baseStr, final String urlStr) {
        this.userAgent = getGlobalUserAgent();
        if (baseStr != null) {
            this.data = parseURL(baseStr, urlStr);
        }
        else {
            this.data = parseURL(urlStr);
        }
    }
    
    public ParsedURL(final URL baseURL, final String urlStr) {
        this.userAgent = getGlobalUserAgent();
        if (baseURL != null) {
            this.data = parseURL(new ParsedURL(baseURL), urlStr);
        }
        else {
            this.data = parseURL(urlStr);
        }
    }
    
    public ParsedURL(final ParsedURL baseURL, final String urlStr) {
        if (baseURL != null) {
            this.userAgent = baseURL.getUserAgent();
            this.data = parseURL(baseURL, urlStr);
        }
        else {
            this.data = parseURL(urlStr);
        }
    }
    
    @Override
    public String toString() {
        return this.data.toString();
    }
    
    public String getPostConnectionURL() {
        return this.data.getPostConnectionURL();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ParsedURL)) {
            return false;
        }
        final ParsedURL purl = (ParsedURL)obj;
        return this.data.equals(purl.data);
    }
    
    @Override
    public int hashCode() {
        return this.data.hashCode();
    }
    
    public boolean complete() {
        return this.data.complete();
    }
    
    public String getUserAgent() {
        return this.userAgent;
    }
    
    public void setUserAgent(final String userAgent) {
        this.userAgent = userAgent;
    }
    
    public String getProtocol() {
        if (this.data.protocol == null) {
            return null;
        }
        return this.data.protocol;
    }
    
    public String getHost() {
        if (this.data.host == null) {
            return null;
        }
        return this.data.host;
    }
    
    public int getPort() {
        return this.data.port;
    }
    
    public String getPath() {
        if (this.data.path == null) {
            return null;
        }
        return this.data.path;
    }
    
    public String getRef() {
        if (this.data.ref == null) {
            return null;
        }
        return this.data.ref;
    }
    
    public String getPortStr() {
        return this.data.getPortStr();
    }
    
    public String getContentType() {
        return this.data.getContentType(this.userAgent);
    }
    
    public String getContentTypeMediaType() {
        return this.data.getContentTypeMediaType(this.userAgent);
    }
    
    public String getContentTypeCharset() {
        return this.data.getContentTypeCharset(this.userAgent);
    }
    
    public boolean hasContentTypeParameter(final String param) {
        return this.data.hasContentTypeParameter(this.userAgent, param);
    }
    
    public String getContentEncoding() {
        return this.data.getContentEncoding(this.userAgent);
    }
    
    public InputStream openStream() throws IOException {
        return this.data.openStream(this.userAgent, null);
    }
    
    public InputStream openStream(final String mimeType) throws IOException {
        final List mt = new ArrayList(1);
        mt.add(mimeType);
        return this.data.openStream(this.userAgent, mt.iterator());
    }
    
    public InputStream openStream(final String[] mimeTypes) throws IOException {
        final List mt = new ArrayList(mimeTypes.length);
        for (final String mimeType : mimeTypes) {
            mt.add(mimeType);
        }
        return this.data.openStream(this.userAgent, mt.iterator());
    }
    
    public InputStream openStream(final Iterator mimeTypes) throws IOException {
        return this.data.openStream(this.userAgent, mimeTypes);
    }
    
    public InputStream openStreamRaw() throws IOException {
        return this.data.openStreamRaw(this.userAgent, null);
    }
    
    public InputStream openStreamRaw(final String mimeType) throws IOException {
        final List mt = new ArrayList(1);
        mt.add(mimeType);
        return this.data.openStreamRaw(this.userAgent, mt.iterator());
    }
    
    public InputStream openStreamRaw(final String[] mimeTypes) throws IOException {
        final List mt = new ArrayList(mimeTypes.length);
        mt.addAll(Arrays.asList(mimeTypes));
        return this.data.openStreamRaw(this.userAgent, mt.iterator());
    }
    
    public InputStream openStreamRaw(final Iterator mimeTypes) throws IOException {
        return this.data.openStreamRaw(this.userAgent, mimeTypes);
    }
    
    public boolean sameFile(final ParsedURL other) {
        return this.data.sameFile(other.data);
    }
    
    protected static String getProtocol(final String urlStr) {
        if (urlStr == null) {
            return null;
        }
        int idx = 0;
        final int len = urlStr.length();
        if (len == 0) {
            return null;
        }
        char ch;
        for (ch = urlStr.charAt(idx); ch == '-' || ch == '+' || ch == '.' || (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z'); ch = urlStr.charAt(idx)) {
            if (++idx == len) {
                ch = '\0';
                break;
            }
        }
        if (ch == ':') {
            return urlStr.substring(0, idx).toLowerCase();
        }
        return null;
    }
    
    public static ParsedURLData parseURL(String urlStr) {
        if (urlStr != null && !urlStr.contains(":") && !urlStr.startsWith("#")) {
            urlStr = "file:" + urlStr;
        }
        final ParsedURLProtocolHandler handler = getHandler(getProtocol(urlStr));
        return handler.parseURL(urlStr);
    }
    
    public static ParsedURLData parseURL(final String baseStr, final String urlStr) {
        if (baseStr == null) {
            return parseURL(urlStr);
        }
        final ParsedURL purl = new ParsedURL(baseStr);
        return parseURL(purl, urlStr);
    }
    
    public static ParsedURLData parseURL(final ParsedURL baseURL, final String urlStr) {
        if (baseURL == null) {
            return parseURL(urlStr);
        }
        String protocol = getProtocol(urlStr);
        if (protocol == null) {
            protocol = baseURL.getProtocol();
        }
        final ParsedURLProtocolHandler handler = getHandler(protocol);
        return handler.parseURL(baseURL, urlStr);
    }
    
    static {
        ParsedURL.handlersMap = null;
        ParsedURL.defaultHandler = new ParsedURLDefaultProtocolHandler();
        ParsedURL.globalUserAgent = "Batik/" + Version.getVersion();
    }
}
