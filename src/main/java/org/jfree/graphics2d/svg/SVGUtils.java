// 
// Decompiled by Procyon v0.5.36
// 

package org.jfree.graphics2d.svg;

import org.jfree.graphics2d.Args;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;

public class SVGUtils
{
    private SVGUtils() {
    }
    
    public static String escapeForXML(final String source) {
        Args.nullNotPermitted(source, "source");
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < source.length(); ++i) {
            final char c = source.charAt(i);
            switch (c) {
                case '<' -> {
                    sb.append("&lt;");
                    break;
                }
                case '>' -> {
                    sb.append("&gt;");
                    break;
                }
                case '&' -> {
                    final String next = source.substring(i, Math.min(i + 6, source.length()));
                    if (next.startsWith("&lt;") || next.startsWith("&gt;") || next.startsWith("&amp;") || next.startsWith("&apos;") || next.startsWith("&quot;")) {
                        sb.append(c);
                        break;
                    }
                    sb.append("&amp;");
                    break;
                }
                case '\'' -> {
                    sb.append("&apos;");
                    break;
                }
                case '\"' -> {
                    sb.append("&quot;");
                    break;
                }
                default -> {
                    sb.append(c);
                    break;
                }
            }
        }
        return sb.toString();
    }
    
    public static void writeToSVG(final File file, final String svgElement) throws IOException {
        writeToSVG(file, svgElement, false);
    }
    
    public static void writeToSVG(final File file, final String svgElement, final boolean zip) throws IOException {
        BufferedWriter writer = null;
        try {
            OutputStream os = new FileOutputStream(file);
            if (zip) {
                os = new GZIPOutputStream(os);
            }
            final OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);
            writer = new BufferedWriter(osw);
            writer.write("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n");
            writer.write(svgElement + "\n");
            writer.flush();
        }
        finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            }
            catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    
    public static void writeToHTML(final File file, final String title, final String svgElement) throws IOException {
        BufferedWriter writer = null;
        try {
            final FileOutputStream fos = new FileOutputStream(file);
            final OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            writer = new BufferedWriter(osw);
            writer.write("<!DOCTYPE html>\n");
            writer.write("<html>\n");
            writer.write("<head>\n");
            writer.write("<title>" + title + "</title>\n");
            writer.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n");
            writer.write("</head>\n");
            writer.write("<body>\n");
            writer.write(svgElement + "\n");
            writer.write("</body>\n");
            writer.write("</html>\n");
            writer.flush();
        }
        finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            }
            catch (IOException ex) {
                Logger.getLogger(SVGUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
