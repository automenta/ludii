// 
// Decompiled by Procyon v0.5.36
// 

package graphics.svg;

import java.awt.*;

public class BallSVG
{
    private static final String[] template;
    
    public static String generate(final Color shade) {
        final StringBuilder svg = new StringBuilder();
        final int r1 = shade.getRed();
        final int g1 = shade.getGreen();
        final int b1 = shade.getBlue();
        final int darken = 4;
        final int r2 = r1 / 4;
        final int g2 = g1 / 4;
        final int b2 = b1 / 4;
        for (int l = 0; l < BallSVG.template.length; ++l) {
            String line = BallSVG.template[l];
            if (line.contains("<RGB_")) {
                for (int perc = 0; perc <= 100; ++perc) {
                    final String pattern = String.format("<RGB_%d>", perc);
                    final int c = line.indexOf(pattern);
                    if (c != -1) {
                        final double t = Math.pow(perc / 100.0, 4.0);
                        final int r3 = r1 - (int)(t * (r1 - r2));
                        final int g3 = g1 - (int)(t * (g1 - g2));
                        final int b3 = b1 - (int)(t * (b1 - b2));
                        line = line.substring(0, c) + r3 + "," + g3 + "," + b3 + line.substring(c + pattern.length());
                        break;
                    }
                }
            }
            svg.append(line + "\n");
        }
        return svg.toString();
    }
    
    static {
        template = new String[] { "<?xml version=\"1.0\" standalone=\"no\"?>", "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\"", "\"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">", "<svg", "  width=\"10cm\" height=\"10cm\" viewBox=\"0 0 1000 1000\" version=\"1.1\"", "  xmlns=\"http://www.w3.org/2000/svg\">", "  <desc>Rendered 3Dish reflective ball.</desc>", "  <g>", "    <defs>", "      <radialGradient id=\"Shading\" gradientUnits=\"userSpaceOnUse\"", "                      cx=\"500\" cy=\"500\" r=\"490\" fx=\"500\" fy=\"500\">", "        <stop offset=   \"0%\" stop-color=\"rgb(<RGB_0>)\" />", "        <stop offset=  \"10%\" stop-color=\"rgb(<RGB_10>)\" />", "        <stop offset=  \"20%\" stop-color=\"rgb(<RGB_20>)\" />", "        <stop offset=  \"30%\" stop-color=\"rgb(<RGB_30>)\" />", "        <stop offset=  \"40%\" stop-color=\"rgb(<RGB_40>)\" />", "        <stop offset=  \"50%\" stop-color=\"rgb(<RGB_50>)\" />", "        <stop offset=  \"60%\" stop-color=\"rgb(<RGB_60>)\" />", "        <stop offset=  \"70%\" stop-color=\"rgb(<RGB_70>)\" />", "        <stop offset=  \"80%\" stop-color=\"rgb(<RGB_80>)\" />", "        <stop offset=  \"90%\" stop-color=\"rgb(<RGB_90>)\" />", "        <stop offset= \"100%\" stop-color=\"rgb(<RGB_100>)\" />", "      </radialGradient>", "      <radialGradient id=\"Highlight\" gradientUnits=\"userSpaceOnUse\"", "                      cx=\"500\" cy=\"500\" r=\"490\" fx=\"500\" fy=\"500\">", "        <stop offset=   \"0%\" stop-color=\"rgb(255,255,255,0.0)\" />", "        <stop offset=  \"25%\" stop-color=\"rgb(255,255,255,0.05)\" />", "        <stop offset=  \"50%\" stop-color=\"rgb(255,255,255,0.15)\" />", "        <stop offset=  \"75%\" stop-color=\"rgb(255,255,255,0.5)\" />", "        <stop offset= \"100%\" stop-color=\"rgb(255,255,255,1.0)\" />", "      </radialGradient>", "    </defs>", "    <circle cx=\"500\" cy=\"500\" r=\"490\" fill=\"url(#Shading)\" />", "    <path", "      d=\"M500,500", "      C250,500,100,475,100,340", "      C100,130,360,25,500,25", "      C640,25,900,130,900,340", "      C900,475,750,500,500,500", "      z\"", "      fill=\"url(#Highlight)\"", "    />", "  </g>", "</svg>" };
    }
}
