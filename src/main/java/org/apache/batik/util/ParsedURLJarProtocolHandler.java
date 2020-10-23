// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.util;

import java.net.MalformedURLException;
import java.net.URL;

public class ParsedURLJarProtocolHandler extends ParsedURLDefaultProtocolHandler
{
    public static final String JAR = "jar";
    
    public ParsedURLJarProtocolHandler() {
        super("jar");
    }
    
    @Override
    public ParsedURLData parseURL(final ParsedURL baseURL, final String urlStr) {
        final String start = urlStr.substring(0, "jar".length() + 1).toLowerCase();
        if (start.equals("jar:")) {
            return this.parseURL(urlStr);
        }
        try {
            final URL context = new URL(baseURL.toString());
            final URL url = new URL(context, urlStr);
            return this.constructParsedURLData(url);
        }
        catch (MalformedURLException mue) {
            return super.parseURL(baseURL, urlStr);
        }
    }
}
