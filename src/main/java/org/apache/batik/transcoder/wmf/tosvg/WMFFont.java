// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.transcoder.wmf.tosvg;

import java.awt.Font;

public class WMFFont
{
    public Font font;
    public int charset;
    public int underline;
    public int strikeOut;
    public int italic;
    public int weight;
    public int orientation;
    public int escape;
    
    public WMFFont(final Font font, final int charset) {
        this.underline = 0;
        this.strikeOut = 0;
        this.italic = 0;
        this.weight = 0;
        this.orientation = 0;
        this.escape = 0;
        this.font = font;
        this.charset = charset;
    }
    
    public WMFFont(final Font font, final int charset, final int underline, final int strikeOut, final int italic, final int weight, final int orient, final int escape) {
        this.underline = 0;
        this.strikeOut = 0;
        this.italic = 0;
        this.weight = 0;
        this.orientation = 0;
        this.escape = 0;
        this.font = font;
        this.charset = charset;
        this.underline = underline;
        this.strikeOut = strikeOut;
        this.italic = italic;
        this.weight = weight;
        this.orientation = orient;
        this.escape = escape;
    }
}
