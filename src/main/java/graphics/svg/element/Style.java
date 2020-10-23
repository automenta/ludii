// 
// Decompiled by Procyon v0.5.36
// 

package graphics.svg.element;

import graphics.svg.SVGParser;

import java.awt.*;

public class Style
{
    private Color stroke;
    private Color fill;
    private double strokeWidth;
    
    public Style() {
        this.stroke = null;
        this.fill = null;
        this.strokeWidth = 0.0;
    }
    
    public Color stroke() {
        return this.stroke;
    }
    
    public void setStroke(final Color clr) {
        this.stroke = clr;
    }
    
    public Color fill() {
        return this.fill;
    }
    
    public void setFill(final Color clr) {
        this.fill = clr;
    }
    
    public double strokeWidth() {
        return this.strokeWidth;
    }
    
    public void setStrokeWidth(final double val) {
        this.strokeWidth = val;
    }
    
    public boolean load(final String expr) {
        final boolean okay = true;
        String str = expr.replaceAll(":", "=");
        str = str.replaceAll("\"", " ");
        str = str.replaceAll(",", " ");
        str = str.replaceAll(";", " ");
        int pos = str.indexOf("stroke=");
        if (pos != -1) {
            final String result = SVGParser.extractStringAt(str, pos + 7);
            if (result != null) {
                if (result.equals("red")) {
                    this.stroke = new Color(255, 0, 0);
                }
                else if (result.equals("green")) {
                    this.stroke = new Color(0, 175, 0);
                }
                else if (result.equals("blue")) {
                    this.stroke = new Color(0, 0, 255);
                }
                else if (result.equals("white")) {
                    this.stroke = new Color(255, 255, 255);
                }
                else if (result.equals("black")) {
                    this.stroke = new Color(0, 0, 0);
                }
                else if (result.equals("orange")) {
                    this.stroke = new Color(255, 175, 0);
                }
                else if (result.equals("yellow")) {
                    this.stroke = new Color(255, 240, 0);
                }
                else if (result.contains("#")) {
                    this.stroke = colourFromCode(result);
                }
            }
        }
        pos = str.indexOf("fill=");
        if (pos != -1) {
            final String result = SVGParser.extractStringAt(str, pos + 5);
            if (result != null) {
                if (result.equals("transparent")) {
                    this.fill = null;
                }
                else if (result.equals("red")) {
                    this.fill = new Color(255, 0, 0);
                }
                else if (result.equals("green")) {
                    this.fill = new Color(0, 175, 0);
                }
                else if (result.equals("blue")) {
                    this.fill = new Color(0, 0, 255);
                }
                else if (result.equals("white")) {
                    this.fill = new Color(255, 255, 255);
                }
                else if (result.equals("black")) {
                    this.fill = new Color(0, 0, 0);
                }
                else if (result.equals("orange")) {
                    this.fill = new Color(255, 175, 0);
                }
                else if (result.equals("yellow")) {
                    this.fill = new Color(255, 240, 0);
                }
                else if (result.contains("#")) {
                    this.fill = colourFromCode(result);
                }
            }
        }
        if (str.contains("stroke-width=")) {
            final Double result2 = SVGParser.extractDouble(str, "stroke-width=");
            if (result2 != null) {
                this.strokeWidth = result2;
            }
        }
        return okay;
    }
    
    public static Color colourFromCode(final String strIn) {
        final String str = strIn.replaceAll("\"", "").trim();
        if (str.charAt(0) != '#' || str.length() != 7) {
            return null;
        }
        final int[] values = new int[7];
        for (int c = 1; c < str.length(); ++c) {
            final char ch = Character.toLowerCase(str.charAt(c));
            if (ch >= '0' && ch <= '9') {
                values[c] = ch - '0';
            }
            else {
                if (ch < 'a' || ch > 'f') {
                    return null;
                }
                values[c] = ch - 'a' + 10;
            }
        }
        final int r = values[1] << 4 | values[2];
        final int g = values[3] << 4 | values[4];
        final int b = values[5] << 4 | values[6];
        return new Color(r, g, b);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<");
        sb.append("fill=(").append(this.fill).append(")");
        sb.append(" stroke=(").append(this.stroke).append(")");
        sb.append(" strokeWidth=(").append(this.strokeWidth).append(")");
        sb.append(">");
        return sb.toString();
    }
}
