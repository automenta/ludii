// 
// Decompiled by Procyon v0.5.36
// 

package util;

import graphics.svg.SVGLoader;
import graphics.svg.SVGtoImage;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.regex.Pattern;

public class ImageUtil
{
    public static String getImageFullPath(final String imageName) {
        final String imageNameLower = imageName.toLowerCase();
        final String[] listSVGs;
        final String[] svgNames = listSVGs = SVGLoader.listSVGs();
        for (final String svgName : listSVGs) {
            final String sReplaced = svgName.replaceAll(Pattern.quote("\\"), "/");
            final String[] subs = sReplaced.split("/");
            if (subs[subs.length - 1].toLowerCase().equals(imageNameLower + ".svg")) {
                String fullPath = svgName.replaceAll(Pattern.quote("\\"), "/");
                fullPath = fullPath.substring(fullPath.indexOf("/svg/"));
                return fullPath;
            }
        }
        if (Arrays.asList(ImageConstants.customImageKeywords).contains(imageNameLower)) {
            return imageNameLower;
        }
        String longestName = null;
        String longestNamePath = null;
        for (final String svgName2 : svgNames) {
            final String sReplaced2 = svgName2.replaceAll(Pattern.quote("\\"), "/");
            final String[] subs2 = sReplaced2.split("/");
            final String shortName = subs2[subs2.length - 1].split("\\.")[0].toLowerCase();
            if (imageNameLower.contains(shortName)) {
                String fullPath2 = svgName2.replaceAll(Pattern.quote("\\"), "/");
                fullPath2 = fullPath2.substring(fullPath2.indexOf("/svg/"));
                if (longestName == null || shortName.length() > longestName.length()) {
                    longestName = shortName;
                    longestNamePath = fullPath2;
                }
            }
        }
        return longestNamePath;
    }
    
    public static void drawImageAtPosn(final Graphics2D g2d, final String img, final int xPosn, final int yPosn, final Rectangle2D rect, final Color edgeColour, final Color fillColour) {
        drawImageAtPosn(g2d, img, xPosn, yPosn, rect, edgeColour, fillColour, 0);
    }
    
    public static void drawImageAtPosn(final Graphics2D g2d, final String img, final int xPosn, final int yPosn, final Rectangle2D rect, final Color edgeColour, final Color fillColour, final int rotation) {
        final int x = (int)(xPosn - rect.getHeight() / 2.0);
        final int y = (int)(yPosn - rect.getWidth() / 2.0);
        final double longestDim = Math.max(rect.getWidth(), rect.getHeight());
        SVGtoImage.loadFromString(g2d, img, longestDim, x, y, edgeColour, fillColour, false, rotation);
    }
}
