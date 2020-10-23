// 
// Decompiled by Procyon v0.5.36
// 

package graphics.svg;

import main.StringRoutines;
import math.MathRoutines;
import org.jfree.graphics2d.svg.SVGGraphics2D;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SVGtoImage
{
    public static final char[] SVG_Symbols;
    final boolean verbose = false;
    
    public static boolean isSVGSymbol(final char ch) {
        final char chLower = Character.toLowerCase(ch);
        for (int s = 0; s < SVGtoImage.SVG_Symbols.length; ++s) {
            if (chLower == SVGtoImage.SVG_Symbols[s]) {
                return true;
            }
        }
        return false;
    }
    
    public static String getSVGString(final String filePath) {
        final InputStream in = SVGtoImage.class.getResourceAsStream(filePath);
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String str = "";
            String line = null;
            while ((line = reader.readLine()) != null) {
                str = str + line + "\n";
            }
            reader.close();
            return str;
        }
        catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
    
    public static void loadFromString(final Graphics2D g2d, final String filePath, final int imgSz, final Color borderColour, final Color fillColour, final boolean centre) {
        loadFromString(g2d, filePath, imgSz, 0, 0, borderColour, fillColour, centre, 0);
    }
    
    public static void loadFromString(final Graphics2D g2d, final String filePath, final double imgSz, final int x, final int y, final Color borderColour, final Color fillColour, final boolean centre) {
        loadFromString(g2d, filePath, imgSz, x, y, borderColour, fillColour, centre, 0);
    }
    
    public static void loadFromString(final Graphics2D g2d, final String filePath, final double imgSz, final int x, final int y, final Color borderColour, final Color fillColour, final boolean centre, final int rotation) {
        final InputStream in = SVGtoImage.class.getResourceAsStream(filePath);
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            loadFromReader(g2d, reader, (int)imgSz, x, y, borderColour, fillColour, centre, rotation);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void loadFromReader(final Graphics2D g2d, final BufferedReader bufferedReader, final int imgSz, final Color borderColour, final Color fillColour, final boolean centre) {
        loadFromReader(g2d, bufferedReader, imgSz, 0.0, 0.0, borderColour, fillColour, centre, 0);
    }
    
    public static void loadFromReader(final Graphics2D g2d, final BufferedReader bufferedReader, final int imgSz, final double x, final double y, final Color borderColour, final Color fillColour, final boolean centre, final int rotation) {
        String svg = "";
        String line = null;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                svg = svg + line + "\n";
            }
            bufferedReader.close();
        }
        catch (Exception ex) {}
        loadFromSource(g2d, svg, imgSz, x, y, borderColour, fillColour, centre, rotation);
    }
    
    public static void loadFromSource(final Graphics2D g2d, final String svg, final int imgSz, final double x, final double y, final Color borderColour, final Color fillColour, final boolean centre, final int rotation) {
        final SVGtoImage temp = new SVGtoImage();
        final List<List<SVGPathOp>> paths = new ArrayList<>();
        final Rectangle2D.Double bounds = new Rectangle2D.Double();
        if (temp.parse(svg, paths, bounds)) {
            temp.findBounds(paths, bounds);
            temp.render(g2d, imgSz, borderColour, fillColour, (int)x, (int)y, centre, paths, bounds, rotation);
        }
    }
    
    public static Rectangle2D getBounds(final String filePath, final int imgSz) {
        final InputStream in = SVGtoImage.class.getResourceAsStream(filePath);
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            return getBounds(reader, imgSz);
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static Rectangle2D getBounds(final BufferedReader reader, final int imgSz) {
        final SVGtoImage temp = new SVGtoImage();
        String str = "";
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                str = str + line + "\n";
            }
            reader.close();
        }
        catch (Exception ex) {}
        final List<List<SVGPathOp>> paths = new ArrayList<>();
        final Rectangle2D.Double bounds = new Rectangle2D.Double();
        if (temp.parse(str, paths, bounds)) {
            temp.findBounds(paths, bounds);
            final int x0 = (int)bounds.getX() - 1;
            final int x2 = (int)(bounds.getX() + bounds.getWidth()) + 1;
            final int sx = x2 - x0;
            final int y0 = (int)bounds.getY() - 1;
            final int y2 = (int)(bounds.getY() + bounds.getHeight()) + 1;
            final int sy = y2 - y0;
            final double scale = imgSz / (double)Math.max(sx, sy);
            bounds.height = (int)(scale * sx + 0.5);
            bounds.width = (int)(scale * sy + 0.5);
            return bounds;
        }
        return null;
    }
    
    public static double getDesiredScale(final String filePath, final int imgSz) {
        final InputStream in = SVGtoImage.class.getResourceAsStream(filePath);
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            final SVGtoImage temp = new SVGtoImage();
            String str = "";
            String line = null;
            while ((line = reader.readLine()) != null) {
                str = str + line + "\n";
            }
            reader.close();
            final List<List<SVGPathOp>> paths = new ArrayList<>();
            final Rectangle2D.Double bounds = new Rectangle2D.Double();
            if (temp.parse(str, paths, bounds)) {
                temp.findBounds(paths, bounds);
                final int x0 = (int)bounds.getX() - 1;
                final int x2 = (int)(bounds.getX() + bounds.getWidth()) + 1;
                final int sx = x2 - x0;
                final int y0 = (int)bounds.getY() - 1;
                final int y2 = (int)(bounds.getY() + bounds.getHeight()) + 1;
                final int sy = y2 - y0;
                return imgSz / (double)Math.max(sx, sy);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
    
    boolean parse(final String in, final List<List<SVGPathOp>> paths, final Rectangle2D.Double bounds) {
        paths.clear();
        int cc;
        for (String str = in; str.contains("<path"); str = str.substring(cc + 1)) {
            final int c = str.indexOf("<path");
            cc = StringRoutines.matchingBracketAt(str, c);
            String pathStr;
            int d;
            for (pathStr = str.substring(c, cc + 1), d = pathStr.indexOf("d="); d < pathStr.length() && pathStr.charAt(d) != '\"'; ++d) {}
            final int dd = StringRoutines.matchingQuoteAt(pathStr, d);
            final String data = pathStr.substring(d + 1, dd);
            final List<String> tokens = this.tokenise(data);
            this.processPathData(tokens, paths);
        }
        return true;
    }
    
    List<String> tokenise(final String data) {
        final List<String> tokens = new ArrayList<>();
        for (int c = 0; c < data.length(); ++c) {
            final char ch = data.charAt(c);
            if (isSVGSymbol(ch)) {
                tokens.add(String.valueOf(ch));
            }
            else if (StringRoutines.isNumeric(ch)) {
                int cc = c;
                int numDots = 0;
                while (cc < data.length() - 1 && StringRoutines.isNumeric(data.charAt(cc + 1))) {
                    if (data.charAt(cc) == '.') {
                        if (numDots > 0) {
                            --cc;
                            break;
                        }
                        ++numDots;
                    }
                    if (++cc < data.length() - 1 && cc > c + 1 && data.charAt(cc) != 'e' && data.charAt(cc + 1) == '-') {
                        break;
                    }
                    if (cc > c && data.charAt(cc - 1) != 'e' && data.charAt(cc) == '-') {
                        --cc;
                        break;
                    }
                }
                final String token = data.substring(c, cc + 1);
                if (token.contains("e")) {
                    tokens.add("0");
                }
                else {
                    tokens.add(token);
                }
                c = cc;
            }
            else if (ch == '<') {
                final int cc = c = StringRoutines.matchingBracketAt(data, c);
            }
        }
        return tokens;
    }
    
    boolean processPathData(final List<String> tokens, final List<List<SVGPathOp>> paths) {
        final List<SVGPathOp> path = new ArrayList<>();
        paths.add(path);
        char lastOperator = '?';
        int s = 0;
        while (s < tokens.size()) {
            String token = tokens.get(s);
            if (token.isEmpty()) {
                ++s;
            }
            else {
                char ch = token.charAt(0);
                final boolean hasSymbol = token.length() == 1 && isSVGSymbol(ch);
                if (hasSymbol) {
                    if (++s >= tokens.size()) {
                        return true;
                    }
                    token = tokens.get(s);
                }
                else {
                    ch = lastOperator;
                }
                lastOperator = ch;
                switch (ch) {
                    case 'A', 'a' -> {
                        if (s >= tokens.size() - 7) {
                            return false;
                        }
                        final SVGPathOp op = new SVGPathOp(SVGPathOp.PathOpType.ArcTo, ch == 'A', new String[]{tokens.get(s), tokens.get(s + 1), tokens.get(s + 5), tokens.get(s + 6), tokens.get(s + 2), tokens.get(s + 3), tokens.get(s + 4)});
                        path.add(op);
                        s += 7;
                        continue;
                    }
                    case 'M', 'm' -> {
                        if (s >= tokens.size() - 2) {
                            return false;
                        }
                        final SVGPathOp op = new SVGPathOp(SVGPathOp.PathOpType.MoveTo, ch == 'M', new String[]{tokens.get(s), tokens.get(s + 1)});
                        path.add(op);
                        s += 2;
                        continue;
                    }
                    case 'L', 'l' -> {
                        if (s >= tokens.size() - 2) {
                            return false;
                        }
                        final SVGPathOp op = new SVGPathOp(SVGPathOp.PathOpType.LineTo, ch == 'L', new String[]{tokens.get(s), tokens.get(s + 1)});
                        path.add(op);
                        s += 2;
                        continue;
                    }
                    case 'H', 'h' -> {
                        if (s >= tokens.size() - 1) {
                            return false;
                        }
                        final SVGPathOp op = new SVGPathOp(SVGPathOp.PathOpType.HLineTo, ch == 'H', new String[]{tokens.get(s), "0"});
                        path.add(op);
                        ++s;
                        continue;
                    }
                    case 'V', 'v' -> {
                        if (s >= tokens.size() - 1) {
                            return false;
                        }
                        final SVGPathOp op = new SVGPathOp(SVGPathOp.PathOpType.VLineTo, ch == 'V', new String[]{"0", tokens.get(s)});
                        path.add(op);
                        ++s;
                        continue;
                    }
                    case 'Q', 'q' -> {
                        if (s >= tokens.size() - 4) {
                            return false;
                        }
                        final SVGPathOp op = new SVGPathOp(SVGPathOp.PathOpType.QuadraticTo, ch == 'Q', new String[]{tokens.get(s), tokens.get(s + 1), tokens.get(s + 2), tokens.get(s + 3)});
                        path.add(op);
                        s += 4;
                        continue;
                    }
                    case 'C', 'c' -> {
                        if (s >= tokens.size() - 6) {
                            return false;
                        }
                        final SVGPathOp op = new SVGPathOp(SVGPathOp.PathOpType.CurveTo, ch == 'C', new String[]{tokens.get(s), tokens.get(s + 1), tokens.get(s + 2), tokens.get(s + 3), tokens.get(s + 4), tokens.get(s + 5)});
                        path.add(op);
                        s += 6;
                        continue;
                    }
                    case 'S', 's' -> {
                        if (s >= tokens.size() - 4) {
                            return false;
                        }
                        final SVGPathOp op = new SVGPathOp(SVGPathOp.PathOpType.ShortCurveTo, ch == 'S', new String[]{tokens.get(s), tokens.get(s + 1), tokens.get(s + 2), tokens.get(s + 3)});
                        path.add(op);
                        s += 4;
                        continue;
                    }
                    case 'T', 't' -> {
                        if (s >= tokens.size() - 2) {
                            return false;
                        }
                        final SVGPathOp op = new SVGPathOp(SVGPathOp.PathOpType.ShortQuadraticTo, ch == 'T', new String[]{tokens.get(s), tokens.get(s + 1)});
                        path.add(op);
                        s += 2;
                        continue;
                    }
                    case 'Z', 'z' -> {
                        final SVGPathOp op = new SVGPathOp(SVGPathOp.PathOpType.ClosePath, ch == 'Z', null);
                        path.add(op);
                        continue;
                    }
                    default -> {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    int findPositiveInteger(final String in, final String label) {
        int from = in.indexOf(label);
        if (from == -1) {
            System.out.println("** Failed to find '" + label + "' in '" + in + "'.");
            return 0;
        }
        ++from;
        String str = "";
        for (int cc = from + label.length(); cc < in.length() && in.charAt(cc) >= '0' && in.charAt(cc) <= '9'; str += in.charAt(cc++)) {}
        int value = 0;
        try {
            value = Integer.parseInt(str);
        }
        catch (Exception ex) {}
        return value;
    }
    
    public void findBounds(final List<List<SVGPathOp>> paths, final Rectangle2D.Double bounds) {
        double minX = 1000000.0;
        double minY = 1000000.0;
        double maxX = -1000000.0;
        double maxY = -1000000.0;
        double lastX = 0.0;
        double lastY = 0.0;
        double x = 0.0;
        double y = 0.0;
        double x2 = 0.0;
        double y2 = 0.0;
        double x3 = 0.0;
        double y3 = 0.0;
        for (final List<SVGPathOp> path : paths) {
            lastX = 0.0;
            lastY = 0.0;
            for (final SVGPathOp op : path) {
                switch (op.type()) {
                    case ArcTo -> {
                        final double rx = op.pts().get(0).x;
                        final double ry = op.pts().get(0).y;
                        x = op.pts().get(1).x + (op.absolute() ? 0.0 : lastX);
                        y = op.pts().get(1).y + (op.absolute() ? 0.0 : lastY);
                        lastX = x + rx;
                        lastY = y;
                        x2 = x - rx;
                        y2 = y - ry;
                        x3 = x + rx;
                        y3 = y + ry;
                        if (x2 < minX) {
                            minX = x2;
                        }
                        if (y2 < minY) {
                            minY = y2;
                        }
                        if (x2 > maxX) {
                            maxX = x2;
                        }
                        if (y2 > maxY) {
                            maxY = y2;
                        }
                        if (x3 < minX) {
                            minX = x3;
                        }
                        if (y3 < minY) {
                            minY = y3;
                        }
                        if (x3 > maxX) {
                            maxX = x3;
                        }
                        if (y3 > maxY) {
                            maxY = y3;
                            break;
                        }
                        break;
                    }
                    case MoveTo -> {
                        x = op.pts().get(0).x + (op.absolute() ? 0.0 : lastX);
                        y = op.pts().get(0).y + (op.absolute() ? 0.0 : lastY);
                        lastX = x;
                        lastY = y;
                        break;
                    }
                    case LineTo -> {
                        x = op.pts().get(0).x + (op.absolute() ? 0.0 : lastX);
                        y = op.pts().get(0).y + (op.absolute() ? 0.0 : lastY);
                        lastX = x;
                        lastY = y;
                        break;
                    }
                    case HLineTo -> {
                        x = (lastX = op.pts().get(0).x + (op.absolute() ? 0.0 : lastX));
                        break;
                    }
                    case VLineTo -> {
                        y = (lastY = op.pts().get(0).y + (op.absolute() ? 0.0 : lastY));
                        break;
                    }
                    case QuadraticTo -> {
                        x = op.pts().get(1).x + (op.absolute() ? 0.0 : lastX);
                        y = op.pts().get(1).y + (op.absolute() ? 0.0 : lastY);
                        lastX = x;
                        lastY = y;
                        x2 = op.pts().get(0).x + (op.absolute() ? 0.0 : lastX);
                        y2 = op.pts().get(0).y + (op.absolute() ? 0.0 : lastY);
                        break;
                    }
                    case CurveTo -> {
                        x = op.pts().get(2).x + (op.absolute() ? 0.0 : lastX);
                        y = op.pts().get(2).y + (op.absolute() ? 0.0 : lastY);
                        lastX = x;
                        lastY = y;
                        x2 = op.pts().get(0).x + (op.absolute() ? 0.0 : lastX);
                        y2 = op.pts().get(0).y + (op.absolute() ? 0.0 : lastY);
                        x3 = op.pts().get(1).x + (op.absolute() ? 0.0 : lastX);
                        y3 = op.pts().get(1).y + (op.absolute() ? 0.0 : lastY);
                        break;
                    }
                    case ShortQuadraticTo -> {
                        x = op.pts().get(0).x + (op.absolute() ? 0.0 : lastX);
                        y = op.pts().get(0).y + (op.absolute() ? 0.0 : lastY);
                        lastX = x;
                        lastY = y;
                        break;
                    }
                    case ShortCurveTo -> {
                        x = op.pts().get(1).x + (op.absolute() ? 0.0 : lastX);
                        y = op.pts().get(1).y + (op.absolute() ? 0.0 : lastY);
                        lastX = x;
                        lastY = y;
                        x2 = op.pts().get(0).x + (op.absolute() ? 0.0 : lastX);
                        y2 = op.pts().get(0).y + (op.absolute() ? 0.0 : lastY);
                        break;
                    }
                    case ClosePath -> {
                        x = lastX;
                        y = lastY;
                        break;
                    }
                }
                if (x < minX) {
                    minX = x;
                }
                if (y < minY) {
                    minY = y;
                }
                if (x > maxX) {
                    maxX = x;
                }
                if (y > maxY) {
                    maxY = y;
                }
            }
        }
        bounds.setRect(minX, minY, maxX - minX, maxY - minY);
    }
    
    public void render(final Graphics2D g2d, final int maxDim, final Color borderColour, final Color fillColour, final int x, final int y, final boolean centre, final List<List<SVGPathOp>> paths, final Rectangle2D.Double bounds, final int rotation) {
        int x2 = (int)bounds.getX() - 1;
        final int x3 = (int)(bounds.getX() + bounds.getWidth()) + 1;
        final int sx = x3 - x2;
        int y2 = (int)bounds.getY() - 1;
        final int y3 = (int)(bounds.getY() + bounds.getHeight()) + 1;
        final int sy = y3 - y2;
        final double scale = maxDim / (double)Math.max(sx, sy);
        final int imgSx = (int)(scale * sx + 0.5);
        final int imgSy = (int)(scale * sy + 0.5);
        if (centre && g2d instanceof SVGGraphics2D) {
            final SVGGraphics2D svg2d = (SVGGraphics2D)g2d;
            x2 -= (int)((svg2d.getWidth() - imgSx) / 2 / scale);
            y2 -= (int)((svg2d.getHeight() - imgSy) / 2 / scale);
        }
        else {
            x2 -= (int)(x / scale);
            y2 -= (int)(y / scale);
        }
        if (rotation != 0) {
            g2d.rotate(Math.toRadians(rotation), x + maxDim / 2, y + maxDim / 2);
        }
        if (fillColour != null) {
            this.renderPaths(g2d, x2, y2, scale, fillColour, null, paths, bounds);
        }
        if (borderColour != null) {
            this.renderPaths(g2d, x2, y2, scale, null, borderColour, paths, bounds);
        }
        if (rotation != 0) {
            g2d.rotate(-Math.toRadians(rotation), x + maxDim / 2, y + maxDim / 2);
        }
    }
    
    void renderPaths(final Graphics2D g2d, final double x0, final double y0, final double scale, final Color fillColour, final Color borderColour, final List<List<SVGPathOp>> paths, final Rectangle2D.Double bounds) {
        double x = 0.0;
        double y = 0.0;
        for (final List<SVGPathOp> opList : paths) {
            final GeneralPath path = new GeneralPath();
            final List<Point2D> pts = new ArrayList<>();
            Point2D prev = null;
            double startX = 0.0;
            double startY = 0.0;
            double lastX = 0.0;
            double lastY = 0.0;
            for (final SVGPathOp op : opList) {
                Point2D current = path.getCurrentPoint();
                if (current == null) {
                    current = new Point2D.Double(0.0, 0.0);
                }
                switch (op.type()) {
                    case ArcTo -> {
                        System.out.println("** Warning: Path ArcTo not fully supported yet.");
                        final double x2 = (lastX - x0) * scale;
                        final double y2 = (lastY - y0) * scale;
                        final double x3 = (op.pts().get(1).x + (op.absolute() ? 0.0 : lastX) - x0) * scale;
                        final double y3 = (op.pts().get(1).y + (op.absolute() ? 0.0 : lastY) - y0) * scale;
                        final double rx = op.pts().get(0).x * scale;
                        final double ry = op.pts().get(0).y * scale;
                        final double theta = op.xAxisRotation();
                        final double fa = op.largeArcSweep();
                        final double fs = op.sweepFlag();
                        final double xx1 = Math.cos(theta) * (x2 - x3) / 2.0 + Math.sin(theta) * (y2 - y3) / 2.0;
                        final double yy1 = -Math.sin(theta) * (x2 - x3) / 2.0 + Math.cos(theta) * (y2 - y3) / 2.0;
                        final int signF = (fa == fs) ? 1 : -1;
                        final double term = Math.sqrt((rx * rx * ry * ry - rx * rx * yy1 * yy1 - ry * ry * xx1 * xx1) / (rx * rx * yy1 * yy1 + ry * ry * xx1 * xx1));
                        final double ccx = signF * term * (rx * yy1 / ry);
                        final double ccy = signF * term * (-ry * xx1 / rx);
                        final double cx = (Math.cos(theta) * ccx - Math.sin(theta) * ccy + (x2 + x3) / 2.0 - x0) * scale;
                        final double cy = (Math.sin(theta) * ccx + Math.cos(theta) * ccy + (y2 + y3) / 2.0 - y0) * scale;
                        path.append(new Ellipse2D.Double(cx - rx, cy - ry, 2.0 * rx, 2.0 * ry), true);
                        path.lineTo(x3, y3);
                        lastX = x3 / scale + x0;
                        lastY = y3 / scale + y0;
                        prev = new Point2D.Double(x3, y3);
                        pts.add(op.pts().get(1));
                        continue;
                    }
                    case MoveTo -> {
                        if (fillColour != null && !pts.isEmpty()) {
                            if (MathRoutines.isClockwise(pts)) {
                                path.closePath();
                                g2d.setPaint(fillColour);
                                g2d.fill(path);
                            }
                            pts.clear();
                            path.reset();
                        }
                        x = (op.pts().get(0).x + (op.absolute() ? 0.0 : lastX) - x0) * scale;
                        y = (op.pts().get(0).y + (op.absolute() ? 0.0 : lastY) - y0) * scale;
                        lastX = op.pts().get(0).x + (op.absolute() ? 0.0 : lastX);
                        lastY = op.pts().get(0).y + (op.absolute() ? 0.0 : lastY);
                        startX = lastX;
                        startY = lastY;
                        path.moveTo(x, y);
                        prev = new Point2D.Double(x, y);
                        pts.add(op.pts().get(0));
                        continue;
                    }
                    case LineTo -> {
                        x = (op.pts().get(0).x + (op.absolute() ? 0.0 : lastX) - x0) * scale;
                        y = (op.pts().get(0).y + (op.absolute() ? 0.0 : lastY) - y0) * scale;
                        lastX = op.pts().get(0).x + (op.absolute() ? 0.0 : lastX);
                        lastY = op.pts().get(0).y + (op.absolute() ? 0.0 : lastY);
                        path.lineTo(x, y);
                        prev = new Point2D.Double(current.getX(), current.getY());
                        pts.add(op.pts().get(0));
                        continue;
                    }
                    case HLineTo -> {
                        x = (op.pts().get(0).x + (op.absolute() ? 0.0 : lastX) - x0) * scale;
                        y = current.getY();
                        lastX = op.pts().get(0).x + (op.absolute() ? 0.0 : lastX);
                        path.lineTo(x, y);
                        prev = new Point2D.Double(current.getX(), current.getY());
                        pts.add(op.pts().get(0));
                        continue;
                    }
                    case VLineTo -> {
                        x = current.getX();
                        y = (op.pts().get(0).y + (op.absolute() ? 0.0 : lastY) - y0) * scale;
                        lastY = op.pts().get(0).y + (op.absolute() ? 0.0 : lastY);
                        path.lineTo(x, y);
                        prev = new Point2D.Double(current.getX(), current.getY());
                        pts.add(op.pts().get(0));
                        continue;
                    }
                    case QuadraticTo -> {
                        final double x2 = (op.pts().get(0).x + (op.absolute() ? 0.0 : lastX) - x0) * scale;
                        final double y2 = (op.pts().get(0).y + (op.absolute() ? 0.0 : lastY) - y0) * scale;
                        x = (op.pts().get(1).x + (op.absolute() ? 0.0 : lastX) - x0) * scale;
                        y = (op.pts().get(1).y + (op.absolute() ? 0.0 : lastY) - y0) * scale;
                        lastX = op.pts().get(1).x + (op.absolute() ? 0.0 : lastX);
                        lastY = op.pts().get(1).y + (op.absolute() ? 0.0 : lastY);
                        path.quadTo(x2, y2, x, y);
                        prev = new Point2D.Double(x2, y2);
                        pts.add(op.pts().get(1));
                        continue;
                    }
                    case CurveTo -> {
                        final double x2 = (op.pts().get(0).x + (op.absolute() ? 0.0 : lastX) - x0) * scale;
                        final double y2 = (op.pts().get(0).y + (op.absolute() ? 0.0 : lastY) - y0) * scale;
                        final double x3 = (op.pts().get(1).x + (op.absolute() ? 0.0 : lastX) - x0) * scale;
                        final double y3 = (op.pts().get(1).y + (op.absolute() ? 0.0 : lastY) - y0) * scale;
                        x = (op.pts().get(2).x + (op.absolute() ? 0.0 : lastX) - x0) * scale;
                        y = (op.pts().get(2).y + (op.absolute() ? 0.0 : lastY) - y0) * scale;
                        lastX = op.pts().get(2).x + (op.absolute() ? 0.0 : lastX);
                        lastY = op.pts().get(2).y + (op.absolute() ? 0.0 : lastY);
                        path.curveTo(x2, y2, x3, y3, x, y);
                        prev = new Point2D.Double(x3, y3);
                        pts.add(op.pts().get(2));
                        continue;
                    }
                    case ShortQuadraticTo -> {
                        x = (op.pts().get(0).x + (op.absolute() ? 0.0 : lastX) - x0) * scale;
                        y = (op.pts().get(0).y + (op.absolute() ? 0.0 : lastY) - y0) * scale;
                        lastX = op.pts().get(0).x + (op.absolute() ? 0.0 : lastX);
                        lastY = op.pts().get(0).y + (op.absolute() ? 0.0 : lastY);
                        final double curX = current.getX();
                        final double curY = current.getY();
                        final double oldX = prev.getX();
                        final double oldY = prev.getY();
                        final double x2 = 2.0 * curX - oldX;
                        final double y2 = 2.0 * curY - oldY;
                        path.quadTo(x2, y2, x, y);
                        prev = new Point2D.Double(x2, y2);
                        pts.add(op.pts().get(1));
                        continue;
                    }
                    case ShortCurveTo -> {
                        final double x3 = (op.pts().get(0).x + (op.absolute() ? 0.0 : lastX) - x0) * scale;
                        final double y3 = (op.pts().get(0).y + (op.absolute() ? 0.0 : lastY) - y0) * scale;
                        x = (op.pts().get(1).x + (op.absolute() ? 0.0 : lastX) - x0) * scale;
                        y = (op.pts().get(1).y + (op.absolute() ? 0.0 : lastY) - y0) * scale;
                        lastX = op.pts().get(1).x + (op.absolute() ? 0.0 : lastX);
                        lastY = op.pts().get(1).y + (op.absolute() ? 0.0 : lastY);
                        final double curX = current.getX();
                        final double curY = current.getY();
                        final double oldX = prev.getX();
                        final double oldY = prev.getY();
                        final double x2 = 2.0 * curX - oldX;
                        final double y2 = 2.0 * curY - oldY;
                        path.quadTo(x2, y2, x, y);
                        prev = new Point2D.Double(x2, y2);
                        pts.add(op.pts().get(1));
                        continue;
                    }
                    case ClosePath -> {
                        path.closePath();
                        if (fillColour != null) {
                            g2d.setPaint(fillColour);
                            g2d.fill(path);
                            path.reset();
                            pts.clear();
                            prev = null;
                        }
                        lastX = startX;
                        lastY = startY;
                        continue;
                    }
                }
            }
            if (fillColour == null) {
                g2d.setPaint(borderColour);
                g2d.fill(path);
            }
        }
    }
    
    static {
        SVG_Symbols = new char[] { 'a', 'c', 'h', 'l', 'm', 'q', 's', 't', 'v', 'z' };
    }
}
