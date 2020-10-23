// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt.text;

import java.awt.Composite;
import java.awt.Stroke;
import java.awt.Paint;

public class TextPaintInfo
{
    public boolean visible;
    public Paint fillPaint;
    public Paint strokePaint;
    public Stroke strokeStroke;
    public Composite composite;
    public Paint underlinePaint;
    public Paint underlineStrokePaint;
    public Stroke underlineStroke;
    public Paint overlinePaint;
    public Paint overlineStrokePaint;
    public Stroke overlineStroke;
    public Paint strikethroughPaint;
    public Paint strikethroughStrokePaint;
    public Stroke strikethroughStroke;
    public int startChar;
    public int endChar;
    
    public TextPaintInfo() {
    }
    
    public TextPaintInfo(final TextPaintInfo pi) {
        this.set(pi);
    }
    
    public void set(final TextPaintInfo pi) {
        if (pi == null) {
            this.fillPaint = null;
            this.strokePaint = null;
            this.strokeStroke = null;
            this.composite = null;
            this.underlinePaint = null;
            this.underlineStrokePaint = null;
            this.underlineStroke = null;
            this.overlinePaint = null;
            this.overlineStrokePaint = null;
            this.overlineStroke = null;
            this.strikethroughPaint = null;
            this.strikethroughStrokePaint = null;
            this.strikethroughStroke = null;
            this.visible = false;
        }
        else {
            this.fillPaint = pi.fillPaint;
            this.strokePaint = pi.strokePaint;
            this.strokeStroke = pi.strokeStroke;
            this.composite = pi.composite;
            this.underlinePaint = pi.underlinePaint;
            this.underlineStrokePaint = pi.underlineStrokePaint;
            this.underlineStroke = pi.underlineStroke;
            this.overlinePaint = pi.overlinePaint;
            this.overlineStrokePaint = pi.overlineStrokePaint;
            this.overlineStroke = pi.overlineStroke;
            this.strikethroughPaint = pi.strikethroughPaint;
            this.strikethroughStrokePaint = pi.strikethroughStrokePaint;
            this.strikethroughStroke = pi.strikethroughStroke;
            this.visible = pi.visible;
        }
    }
    
    public static boolean equivilent(final TextPaintInfo tpi1, final TextPaintInfo tpi2) {
        if (tpi1 == null) {
            return tpi2 == null;
        }
        if (tpi2 == null) {
            return false;
        }
        if (tpi1.fillPaint == null != (tpi2.fillPaint == null)) {
            return false;
        }
        if (tpi1.visible != tpi2.visible) {
            return false;
        }
        final boolean tpi1Stroke = tpi1.strokePaint != null && tpi1.strokeStroke != null;
        final boolean tpi2Stroke = tpi2.strokePaint != null && tpi2.strokeStroke != null;
        return tpi1Stroke == tpi2Stroke;
    }
}
