// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.util.colour;

import metadata.graphics.GraphicsItem;

import java.awt.*;

public class Colour implements GraphicsItem
{
    private final Color colour;
    
    public Colour(final Integer r, final Integer g, final Integer b) {
        this.colour = new Color(r, g, b);
    }
    
    public Colour(final Integer r, final Integer g, final Integer b, final Integer a) {
        this.colour = new Color(r, g, b, a);
    }
    
    public Colour(final String hexCode) {
        this.colour = interpretHexCode(hexCode);
    }
    
    public Colour(final UserColourType type) {
        this.colour = type.colour();
    }
    
    public Color colour() {
        return this.colour;
    }
    
    public static Color HSVtoColor(final double hue, final double saturation, final double value) {
        double h = hue;
        double r = 0.0;
        double g = 0.0;
        double b = 0.0;
        if (saturation == 0.0) {
            if (h >= 0.0) {
                System.out.println("** Colour.HSVtoColor(): Bad HSV colour combination.");
                return Color.black;
            }
            r = value;
            g = value;
            b = value;
        }
        else {
            while (h > 360.0) {
                h -= 360.0;
            }
            while (h < 0.0) {
                h += 360.0;
            }
            h /= 60.0;
            final int i = (int)h;
            final double f = h - i;
            final double p = value * (1.0 - saturation);
            final double q = value * (1.0 - saturation * f);
            final double t = value * (1.0 - saturation * (1.0 - f));
            switch (i) {
                case 0 -> {
                    r = value;
                    g = t;
                    b = p;
                }
                case 1 -> {
                    r = q;
                    g = value;
                    b = p;
                }
                case 2 -> {
                    r = p;
                    g = value;
                    b = t;
                }
                case 3 -> {
                    r = p;
                    g = q;
                    b = value;
                }
                case 4 -> {
                    r = t;
                    g = p;
                    b = value;
                }
                case 5 -> {
                    r = value;
                    g = p;
                    b = q;
                }
                default -> {
                    System.out.println("** Colour.HSVtoColor(): Invalid HSV case, i=" + i + ".");
                    return Color.black;
                }
            }
        }
        return new Color(Math.max(0, Math.min(255, (int)(r * 255.0 + 0.5))), Math.max(0, Math.min(255, (int)(g * 255.0 + 0.5))), Math.max(0, Math.min(255, (int)(b * 255.0 + 0.5))));
    }
    
    public static Color interpretHexCode(final String code) {
        return Color.decode(code);
    }
    
    public static Color interpretHexCode(final int value) {
        return new Color(value >> 16 & 0xFF, value >> 8 & 0xFF, value & 0xFF);
    }
}
