// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import java.util.HashMap;
import java.awt.Paint;
import org.apache.batik.ext.awt.g2d.GraphicContext;
import java.util.Map;
import java.awt.Color;

public class SVGColor extends AbstractSVGConverter
{
    public static final Color aqua;
    public static final Color black;
    public static final Color blue;
    public static final Color fuchsia;
    public static final Color gray;
    public static final Color green;
    public static final Color lime;
    public static final Color maroon;
    public static final Color navy;
    public static final Color olive;
    public static final Color purple;
    public static final Color red;
    public static final Color silver;
    public static final Color teal;
    public static final Color white;
    public static final Color yellow;
    private static Map colorMap;
    
    public SVGColor(final SVGGeneratorContext generatorContext) {
        super(generatorContext);
    }
    
    @Override
    public SVGDescriptor toSVG(final GraphicContext gc) {
        final Paint paint = gc.getPaint();
        return toSVG((Color)paint, this.generatorContext);
    }
    
    public static SVGPaintDescriptor toSVG(final Color color, final SVGGeneratorContext gc) {
        String cssColor = SVGColor.colorMap.get(color);
        if (cssColor == null) {
            final StringBuffer cssColorBuffer = new StringBuffer("rgb(");
            cssColorBuffer.append(color.getRed());
            cssColorBuffer.append(",");
            cssColorBuffer.append(color.getGreen());
            cssColorBuffer.append(",");
            cssColorBuffer.append(color.getBlue());
            cssColorBuffer.append(")");
            cssColor = cssColorBuffer.toString();
        }
        final float alpha = color.getAlpha() / 255.0f;
        final String alphaString = gc.doubleString(alpha);
        return new SVGPaintDescriptor(cssColor, alphaString);
    }
    
    static {
        aqua = Color.cyan;
        black = Color.black;
        blue = Color.blue;
        fuchsia = Color.magenta;
        gray = Color.gray;
        green = new Color(0, 128, 0);
        lime = Color.green;
        maroon = new Color(128, 0, 0);
        navy = new Color(0, 0, 128);
        olive = new Color(128, 128, 0);
        purple = new Color(128, 0, 128);
        red = Color.red;
        silver = new Color(192, 192, 192);
        teal = new Color(0, 128, 128);
        white = Color.white;
        yellow = Color.yellow;
        (SVGColor.colorMap = new HashMap()).put(SVGColor.black, "black");
        SVGColor.colorMap.put(SVGColor.silver, "silver");
        SVGColor.colorMap.put(SVGColor.gray, "gray");
        SVGColor.colorMap.put(SVGColor.white, "white");
        SVGColor.colorMap.put(SVGColor.maroon, "maroon");
        SVGColor.colorMap.put(SVGColor.red, "red");
        SVGColor.colorMap.put(SVGColor.purple, "purple");
        SVGColor.colorMap.put(SVGColor.fuchsia, "fuchsia");
        SVGColor.colorMap.put(SVGColor.green, "green");
        SVGColor.colorMap.put(SVGColor.lime, "lime");
        SVGColor.colorMap.put(SVGColor.olive, "olive");
        SVGColor.colorMap.put(SVGColor.yellow, "yellow");
        SVGColor.colorMap.put(SVGColor.navy, "navy");
        SVGColor.colorMap.put(SVGColor.blue, "blue");
        SVGColor.colorMap.put(SVGColor.teal, "teal");
        SVGColor.colorMap.put(SVGColor.aqua, "aqua");
    }
}
