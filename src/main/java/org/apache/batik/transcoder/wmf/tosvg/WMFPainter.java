// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.transcoder.wmf.tosvg;

import org.apache.batik.util.Platform;
import java.util.Iterator;
import java.awt.geom.AffineTransform;
import java.text.AttributedCharacterIterator;
import java.awt.Dimension;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.awt.font.FontRenderContext;
import java.util.List;
import java.awt.geom.Arc2D;
import java.awt.font.TextLayout;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.Rectangle2D;
import org.apache.batik.ext.awt.geom.Polyline2D;
import java.awt.geom.Line2D;
import java.awt.Shape;
import org.apache.batik.ext.awt.geom.Polygon2D;
import java.util.ArrayList;
import java.awt.Paint;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.Graphics2D;
import java.util.Stack;
import java.awt.Graphics;
import java.awt.Image;
import java.io.BufferedInputStream;
import java.awt.image.ImageObserver;
import java.awt.BasicStroke;
import java.awt.Color;

public class WMFPainter extends AbstractWMFPainter
{
    private static final int INPUT_BUFFER_SIZE = 30720;
    private static final Integer INTEGER_0;
    private float scale;
    private float scaleX;
    private float scaleY;
    private float conv;
    private float xOffset;
    private float yOffset;
    private float vpX;
    private float vpY;
    private float vpW;
    private float vpH;
    private Color frgdColor;
    private Color bkgdColor;
    private boolean opaque;
    private transient boolean firstEffectivePaint;
    private static BasicStroke solid;
    private static BasicStroke textSolid;
    private transient ImageObserver observer;
    private transient BufferedInputStream bufStream;
    
    public WMFPainter(final WMFRecordStore currentStore, final float scale) {
        this(currentStore, 0, 0, scale);
    }
    
    public WMFPainter(final WMFRecordStore currentStore, final int xOffset, final int yOffset, final float scale) {
        this.opaque = false;
        this.firstEffectivePaint = true;
        this.observer = new ImageObserver() {
            @Override
            public boolean imageUpdate(final Image img, final int flags, final int x, final int y, final int width, final int height) {
                return false;
            }
        };
        this.bufStream = null;
        this.setRecordStore(currentStore);
        TextureFactory.getInstance().reset();
        this.conv = scale;
        this.xOffset = (float)(-xOffset);
        this.yOffset = (float)(-yOffset);
        this.scale = currentStore.getWidthPixels() / (float)currentStore.getWidthUnits() * scale;
        this.scale = this.scale * currentStore.getWidthPixels() / currentStore.getVpW();
        final float xfactor = currentStore.getVpW() / (float)currentStore.getWidthPixels() * currentStore.getWidthUnits() / currentStore.getWidthPixels();
        final float yfactor = currentStore.getVpH() / (float)currentStore.getHeightPixels() * currentStore.getHeightUnits() / currentStore.getHeightPixels();
        this.xOffset *= xfactor;
        this.yOffset *= yfactor;
        this.scaleX = this.scale;
        this.scaleY = this.scale;
    }
    
    public void paint(final Graphics g) {
        float fontHeight = 10.0f;
        final float fontAngle = 0.0f;
        float penWidth = 0.0f;
        float startX = 0.0f;
        float startY = 0.0f;
        int brushObject = -1;
        int penObject = -1;
        int fontObject = -1;
        final Font font = null;
        final Stack dcStack = new Stack();
        final int numRecords = this.currentStore.getNumRecords();
        final int numObjects = this.currentStore.getNumObjects();
        this.vpX = this.currentStore.getVpX() * this.scale;
        this.vpY = this.currentStore.getVpY() * this.scale;
        this.vpW = this.currentStore.getVpW() * this.scale;
        this.vpH = this.currentStore.getVpH() * this.scale;
        if (!this.currentStore.isReading()) {
            g.setPaintMode();
            final Graphics2D g2d = (Graphics2D)g;
            g2d.setStroke(WMFPainter.solid);
            brushObject = -1;
            penObject = -1;
            fontObject = -1;
            this.frgdColor = null;
            this.bkgdColor = Color.white;
            for (int i = 0; i < numObjects; ++i) {
                final GdiObject gdiObj = this.currentStore.getObject(i);
                gdiObj.clear();
            }
            final float w = this.vpW;
            final float h = this.vpH;
            g2d.setColor(Color.black);
            for (int iRec = 0; iRec < numRecords; ++iRec) {
                final MetaRecord mr = this.currentStore.getRecord(iRec);
                switch (mr.functionId) {
                    case 523: {
                        this.currentStore.setVpX(this.vpX = (float)(-mr.elementAt(0)));
                        this.currentStore.setVpY(this.vpY = (float)(-mr.elementAt(1)));
                        this.vpX *= this.scale;
                        this.vpY *= this.scale;
                        break;
                    }
                    case 0:
                    case 524: {
                        this.vpW = (float)mr.elementAt(0);
                        this.vpH = (float)mr.elementAt(1);
                        this.scaleX = this.scale;
                        this.scaleY = this.scale;
                        WMFPainter.solid = new BasicStroke(this.scaleX * 2.0f, 0, 1);
                    }
                    case 525:
                    case 526:
                    case 527:
                    case 529:
                    case 1040:
                    case 1042: {}
                    case 762: {
                        int objIndex = 0;
                        final int penStyle = mr.elementAt(0);
                        if (penStyle == 5) {
                            final Color newClr = Color.white;
                            objIndex = this.addObjectAt(this.currentStore, 4, newClr, objIndex);
                        }
                        else {
                            penWidth = (float)mr.elementAt(4);
                            this.setStroke(g2d, penStyle, penWidth, this.scaleX);
                            final Color newClr = new Color(mr.elementAt(1), mr.elementAt(2), mr.elementAt(3));
                            objIndex = this.addObjectAt(this.currentStore, 1, newClr, objIndex);
                        }
                        break;
                    }
                    case 764: {
                        int objIndex = 0;
                        final int brushStyle = mr.elementAt(0);
                        Color clr = new Color(mr.elementAt(1), mr.elementAt(2), mr.elementAt(3));
                        if (brushStyle == 0) {
                            objIndex = this.addObjectAt(this.currentStore, 2, clr, objIndex);
                        }
                        else if (brushStyle == 2) {
                            final int hatch = mr.elementAt(4);
                            Paint paint;
                            if (!this.opaque) {
                                paint = TextureFactory.getInstance().getTexture(hatch, clr);
                            }
                            else {
                                paint = TextureFactory.getInstance().getTexture(hatch, clr, this.bkgdColor);
                            }
                            if (paint != null) {
                                objIndex = this.addObjectAt(this.currentStore, 2, paint, objIndex);
                            }
                            else {
                                clr = Color.black;
                                objIndex = this.addObjectAt(this.currentStore, 5, clr, objIndex);
                            }
                        }
                        else {
                            clr = Color.black;
                            objIndex = this.addObjectAt(this.currentStore, 5, clr, objIndex);
                        }
                        break;
                    }
                    case 763: {
                        float size = (float)(int)(this.scaleY * mr.elementAt(0));
                        final int charset = mr.elementAt(3);
                        final int italic = mr.elementAt(1);
                        final int weight = mr.elementAt(2);
                        int style = (italic > 0) ? 2 : 0;
                        style |= ((weight > 400) ? 1 : 0);
                        String face;
                        int d;
                        for (face = ((MetaRecord.StringRecord)mr).text, d = 0; d < face.length() && (Character.isLetterOrDigit(face.charAt(d)) || Character.isWhitespace(face.charAt(d))); ++d) {}
                        if (d > 0) {
                            face = face.substring(0, d);
                        }
                        else {
                            face = "System";
                        }
                        if (size < 0.0f) {
                            size = -size;
                        }
                        int objIndex2 = 0;
                        fontHeight = size;
                        Font f = new Font(face, style, (int)size);
                        f = f.deriveFont(size);
                        final int underline = mr.elementAt(4);
                        final int strikeOut = mr.elementAt(5);
                        final int orient = mr.elementAt(6);
                        final int escape = mr.elementAt(7);
                        final WMFFont wf = new WMFFont(f, charset, underline, strikeOut, italic, weight, orient, escape);
                        objIndex2 = this.addObjectAt(this.currentStore, 3, wf, objIndex2);
                        break;
                    }
                    case 248:
                    case 505:
                    case 765:
                    case 1790:
                    case 1791: {
                        final int objIndex = this.addObjectAt(this.currentStore, 6, WMFPainter.INTEGER_0, 0);
                        break;
                    }
                    case 247: {
                        final int objIndex = this.addObjectAt(this.currentStore, 8, WMFPainter.INTEGER_0, 0);
                    }
                    case 301: {
                        int gdiIndex = mr.elementAt(0);
                        if ((gdiIndex & Integer.MIN_VALUE) != 0x0) {
                            break;
                        }
                        if (gdiIndex >= numObjects) {
                            gdiIndex -= numObjects;
                            switch (gdiIndex) {
                                case 5: {
                                    brushObject = -1;
                                    break;
                                }
                                case 8: {
                                    penObject = -1;
                                    break;
                                }
                            }
                            break;
                        }
                        final GdiObject gdiObj = this.currentStore.getObject(gdiIndex);
                        if (!gdiObj.used) {
                            break;
                        }
                        switch (gdiObj.type) {
                            case 1: {
                                g2d.setColor((Color)gdiObj.obj);
                                penObject = gdiIndex;
                                break;
                            }
                            case 2: {
                                if (gdiObj.obj instanceof Color) {
                                    g2d.setColor((Color)gdiObj.obj);
                                }
                                else if (gdiObj.obj instanceof Paint) {
                                    g2d.setPaint((Paint)gdiObj.obj);
                                }
                                else {
                                    g2d.setPaint(this.getPaint((byte[])gdiObj.obj));
                                }
                                brushObject = gdiIndex;
                                break;
                            }
                            case 3: {
                                this.wmfFont = (WMFFont)gdiObj.obj;
                                final Font f2 = this.wmfFont.font;
                                g2d.setFont(f2);
                                fontObject = gdiIndex;
                                break;
                            }
                            case 4: {
                                penObject = -1;
                                break;
                            }
                            case 5: {
                                brushObject = -1;
                                break;
                            }
                        }
                        break;
                    }
                    case 496: {
                        final int gdiIndex = mr.elementAt(0);
                        final GdiObject gdiObj = this.currentStore.getObject(gdiIndex);
                        if (gdiIndex == brushObject) {
                            brushObject = -1;
                        }
                        else if (gdiIndex == penObject) {
                            penObject = -1;
                        }
                        else if (gdiIndex == fontObject) {
                            fontObject = -1;
                        }
                        gdiObj.clear();
                        break;
                    }
                    case 1336: {
                        final int numPolygons = mr.elementAt(0);
                        final int[] pts = new int[numPolygons];
                        for (int ip = 0; ip < numPolygons; ++ip) {
                            pts[ip] = mr.elementAt(ip + 1);
                        }
                        int offset = numPolygons + 1;
                        final List v = new ArrayList(numPolygons);
                        for (final int count : pts) {
                            final float[] xpts = new float[count];
                            final float[] ypts = new float[count];
                            for (int k = 0; k < count; ++k) {
                                xpts[k] = this.scaleX * (this.vpX + this.xOffset + mr.elementAt(offset + k * 2));
                                ypts[k] = this.scaleY * (this.vpY + this.yOffset + mr.elementAt(offset + k * 2 + 1));
                            }
                            offset += count * 2;
                            final Polygon2D pol = new Polygon2D(xpts, ypts, count);
                            v.add(pol);
                        }
                        if (brushObject >= 0) {
                            this.setBrushPaint(this.currentStore, g2d, brushObject);
                            this.fillPolyPolygon(g2d, v);
                            this.firstEffectivePaint = false;
                        }
                        if (penObject >= 0) {
                            this.setPenColor(this.currentStore, g2d, penObject);
                            this.drawPolyPolygon(g2d, v);
                            this.firstEffectivePaint = false;
                            break;
                        }
                        break;
                    }
                    case 804: {
                        final int count2 = mr.elementAt(0);
                        final float[] _xpts = new float[count2];
                        final float[] _ypts = new float[count2];
                        for (int l = 0; l < count2; ++l) {
                            _xpts[l] = this.scaleX * (this.vpX + this.xOffset + mr.elementAt(l * 2 + 1));
                            _ypts[l] = this.scaleY * (this.vpY + this.yOffset + mr.elementAt(l * 2 + 2));
                        }
                        final Polygon2D pol2 = new Polygon2D(_xpts, _ypts, count2);
                        this.paint(brushObject, penObject, pol2, g2d);
                        break;
                    }
                    case 532: {
                        startX = this.scaleX * (this.vpX + this.xOffset + mr.elementAt(0));
                        startY = this.scaleY * (this.vpY + this.yOffset + mr.elementAt(1));
                        break;
                    }
                    case 531: {
                        final float endX = this.scaleX * (this.vpX + this.xOffset + mr.elementAt(0));
                        final float endY = this.scaleY * (this.vpY + this.yOffset + mr.elementAt(1));
                        final Line2D.Float line = new Line2D.Float(startX, startY, endX, endY);
                        this.paintWithPen(penObject, line, g2d);
                        startX = endX;
                        startY = endY;
                        break;
                    }
                    case 805: {
                        final int count2 = mr.elementAt(0);
                        final float[] _xpts = new float[count2];
                        final float[] _ypts = new float[count2];
                        for (int l = 0; l < count2; ++l) {
                            _xpts[l] = this.scaleX * (this.vpX + this.xOffset + mr.elementAt(l * 2 + 1));
                            _ypts[l] = this.scaleY * (this.vpY + this.yOffset + mr.elementAt(l * 2 + 2));
                        }
                        final Polyline2D pol3 = new Polyline2D(_xpts, _ypts, count2);
                        this.paintWithPen(penObject, pol3, g2d);
                        break;
                    }
                    case 1051: {
                        final float x1 = this.scaleX * (this.vpX + this.xOffset + mr.elementAt(0));
                        final float x2 = this.scaleX * (this.vpX + this.xOffset + mr.elementAt(2));
                        final float y1 = this.scaleY * (this.vpY + this.yOffset + mr.elementAt(1));
                        final float y2 = this.scaleY * (this.vpY + this.yOffset + mr.elementAt(3));
                        final Rectangle2D.Float rec = new Rectangle2D.Float(x1, y1, x2 - x1, y2 - y1);
                        this.paint(brushObject, penObject, rec, g2d);
                        break;
                    }
                    case 1564: {
                        final float x1 = this.scaleX * (this.vpX + this.xOffset + mr.elementAt(0));
                        final float x2 = this.scaleX * (this.vpX + this.xOffset + mr.elementAt(2));
                        final float x3 = this.scaleX * mr.elementAt(4);
                        final float y1 = this.scaleY * (this.vpY + this.yOffset + mr.elementAt(1));
                        final float y2 = this.scaleY * (this.vpY + this.yOffset + mr.elementAt(3));
                        final float y3 = this.scaleY * mr.elementAt(5);
                        final RoundRectangle2D rec2 = new RoundRectangle2D.Float(x1, y1, x2 - x1, y2 - y1, x3, y3);
                        this.paint(brushObject, penObject, rec2, g2d);
                        break;
                    }
                    case 1048: {
                        final float x1 = this.scaleX * (this.vpX + this.xOffset + mr.elementAt(0));
                        final float x4 = this.scaleX * (this.vpX + this.xOffset + mr.elementAt(2));
                        final float y4 = this.scaleY * (this.vpY + this.yOffset + mr.elementAt(1));
                        final float y2 = this.scaleY * (this.vpY + this.yOffset + mr.elementAt(3));
                        final Ellipse2D.Float el = new Ellipse2D.Float(x1, y4, x4 - x1, y2 - y4);
                        this.paint(brushObject, penObject, el, g2d);
                        break;
                    }
                    case 302: {
                        this.currentHorizAlign = WMFUtilities.getHorizontalAlignment(mr.elementAt(0));
                        this.currentVertAlign = WMFUtilities.getVerticalAlignment(mr.elementAt(0));
                        break;
                    }
                    case 521: {
                        g2d.setColor(this.frgdColor = new Color(mr.elementAt(0), mr.elementAt(1), mr.elementAt(2)));
                        break;
                    }
                    case 513: {
                        g2d.setColor(this.bkgdColor = new Color(mr.elementAt(0), mr.elementAt(1), mr.elementAt(2)));
                        break;
                    }
                    case 2610: {
                        try {
                            final byte[] bstr = ((MetaRecord.ByteRecord)mr).bstr;
                            final String sr = WMFUtilities.decodeString(this.wmfFont, bstr);
                            final float x5 = this.scaleX * (this.vpX + this.xOffset + mr.elementAt(0));
                            float y5 = this.scaleY * (this.vpY + this.yOffset + mr.elementAt(1));
                            if (this.frgdColor != null) {
                                g2d.setColor(this.frgdColor);
                            }
                            else {
                                g2d.setColor(Color.black);
                            }
                            final FontRenderContext frc = g2d.getFontRenderContext();
                            final Point2D.Double pen = new Point2D.Double(0.0, 0.0);
                            final GeneralPath gp = new GeneralPath(1);
                            final TextLayout layout = new TextLayout(sr, g2d.getFont(), frc);
                            final int flag = mr.elementAt(2);
                            int x6 = 0;
                            int y6 = 0;
                            int x7 = 0;
                            int y7 = 0;
                            boolean clipped = false;
                            Shape clip = null;
                            if ((flag & 0x4) != 0x0) {
                                clipped = true;
                                x6 = mr.elementAt(3);
                                y6 = mr.elementAt(4);
                                x7 = mr.elementAt(5);
                                y7 = mr.elementAt(6);
                                clip = g2d.getClip();
                                g2d.setClip(x6, y6, x7, y7);
                            }
                            this.firstEffectivePaint = false;
                            y5 += this.getVerticalAlignmentValue(layout, this.currentVertAlign);
                            this.drawString(flag, g2d, this.getCharacterIterator(g2d, sr, this.wmfFont, this.currentHorizAlign), x5, y5, layout, this.wmfFont, this.currentHorizAlign);
                            if (clipped) {
                                g2d.setClip(clip);
                            }
                        }
                        catch (Exception e) {}
                        break;
                    }
                    case 1313:
                    case 1583: {
                        try {
                            final byte[] bstr = ((MetaRecord.ByteRecord)mr).bstr;
                            final String sr = WMFUtilities.decodeString(this.wmfFont, bstr);
                            final float x5 = this.scaleX * (this.vpX + this.xOffset + mr.elementAt(0));
                            float y5 = this.scaleY * (this.vpY + this.yOffset + mr.elementAt(1));
                            if (this.frgdColor != null) {
                                g2d.setColor(this.frgdColor);
                            }
                            else {
                                g2d.setColor(Color.black);
                            }
                            final FontRenderContext frc = g2d.getFontRenderContext();
                            final Point2D.Double pen = new Point2D.Double(0.0, 0.0);
                            final GeneralPath gp = new GeneralPath(1);
                            final TextLayout layout = new TextLayout(sr, g2d.getFont(), frc);
                            this.firstEffectivePaint = false;
                            y5 += this.getVerticalAlignmentValue(layout, this.currentVertAlign);
                            this.drawString(-1, g2d, this.getCharacterIterator(g2d, sr, this.wmfFont), x5, y5, layout, this.wmfFont, this.currentHorizAlign);
                        }
                        catch (Exception e) {}
                        break;
                    }
                    case 2071:
                    case 2074:
                    case 2096: {
                        final double left = this.scaleX * (this.vpX + this.xOffset + mr.elementAt(0));
                        final double top = this.scaleY * (this.vpY + this.yOffset + mr.elementAt(1));
                        final double right = this.scaleX * (this.vpX + this.xOffset + mr.elementAt(2));
                        final double bottom = this.scaleY * (this.vpY + this.yOffset + mr.elementAt(3));
                        final double xstart = this.scaleX * (this.vpX + this.xOffset + mr.elementAt(4));
                        final double ystart = this.scaleY * (this.vpY + this.yOffset + mr.elementAt(5));
                        final double xend = this.scaleX * (this.vpX + this.xOffset + mr.elementAt(6));
                        final double yend = this.scaleY * (this.vpY + this.yOffset + mr.elementAt(7));
                        this.setBrushPaint(this.currentStore, g2d, brushObject);
                        final double cx = left + (right - left) / 2.0;
                        final double cy = top + (bottom - top) / 2.0;
                        double startAngle = -Math.toDegrees(Math.atan2(ystart - cy, xstart - cx));
                        final double endAngle = -Math.toDegrees(Math.atan2(yend - cy, xend - cx));
                        double extentAngle = endAngle - startAngle;
                        if (extentAngle < 0.0) {
                            extentAngle += 360.0;
                        }
                        if (startAngle < 0.0) {
                            startAngle += 360.0;
                        }
                        switch (mr.functionId) {
                            case 2071: {
                                final Arc2D.Double arc = new Arc2D.Double(left, top, right - left, bottom - top, startAngle, extentAngle, 0);
                                g2d.draw(arc);
                                break;
                            }
                            case 2074: {
                                final Arc2D.Double arc = new Arc2D.Double(left, top, right - left, bottom - top, startAngle, extentAngle, 2);
                                this.paint(brushObject, penObject, arc, g2d);
                                break;
                            }
                            case 2096: {
                                final Arc2D.Double arc = new Arc2D.Double(left, top, right - left, bottom - top, startAngle, extentAngle, 1);
                                this.paint(brushObject, penObject, arc, g2d);
                                break;
                            }
                        }
                        this.firstEffectivePaint = false;
                        break;
                    }
                    case 30: {
                        dcStack.push(penWidth);
                        dcStack.push(startX);
                        dcStack.push(startY);
                        dcStack.push(brushObject);
                        dcStack.push(penObject);
                        dcStack.push(fontObject);
                        dcStack.push(this.frgdColor);
                        dcStack.push(this.bkgdColor);
                        break;
                    }
                    case 295: {
                        this.bkgdColor = dcStack.pop();
                        this.frgdColor = dcStack.pop();
                        fontObject = dcStack.pop();
                        penObject = dcStack.pop();
                        brushObject = dcStack.pop();
                        startY = dcStack.pop();
                        startX = dcStack.pop();
                        penWidth = dcStack.pop();
                        break;
                    }
                    case 4096: {
                        try {
                            this.setPenColor(this.currentStore, g2d, penObject);
                            final int pointCount = mr.elementAt(0);
                            final int bezierCount = (pointCount - 1) / 3;
                            float _startX = this.scaleX * (this.vpX + this.xOffset + mr.elementAt(1));
                            float _startY = this.scaleY * (this.vpY + this.yOffset + mr.elementAt(2));
                            final GeneralPath gp2 = new GeneralPath(1);
                            gp2.moveTo(_startX, _startY);
                            for (int m = 0; m < bezierCount; ++m) {
                                final int j2 = m * 6;
                                final float cp1X = this.scaleX * (this.vpX + this.xOffset + mr.elementAt(j2 + 3));
                                final float cp1Y = this.scaleY * (this.vpY + this.yOffset + mr.elementAt(j2 + 4));
                                final float cp2X = this.scaleX * (this.vpX + this.xOffset + mr.elementAt(j2 + 5));
                                final float cp2Y = this.scaleY * (this.vpY + this.yOffset + mr.elementAt(j2 + 6));
                                final float endX2 = this.scaleX * (this.vpX + this.xOffset + mr.elementAt(j2 + 7));
                                final float endY2 = this.scaleY * (this.vpY + this.yOffset + mr.elementAt(j2 + 8));
                                gp2.curveTo(cp1X, cp1Y, cp2X, cp2Y, endX2, endY2);
                                _startX = endX2;
                                _startY = endY2;
                            }
                            g2d.setStroke(WMFPainter.solid);
                            g2d.draw(gp2);
                            this.firstEffectivePaint = false;
                        }
                        catch (Exception e) {}
                    }
                    case 258: {
                        final int mode = mr.elementAt(0);
                        this.opaque = (mode == 2);
                        break;
                    }
                    case 260: {
                        final float rop = mr.ElementAt(0);
                        Paint paint2 = null;
                        boolean ok = false;
                        if (rop == 66.0f) {
                            paint2 = Color.black;
                            ok = true;
                        }
                        else if (rop == 1.6711778E7f) {
                            paint2 = Color.white;
                            ok = true;
                        }
                        else if (rop == 1.5728673E7f && brushObject >= 0) {
                            paint2 = this.getStoredPaint(this.currentStore, brushObject);
                            ok = true;
                        }
                        if (ok) {
                            if (paint2 != null) {
                                g2d.setPaint(paint2);
                            }
                            else {
                                this.setBrushPaint(this.currentStore, g2d, brushObject);
                            }
                        }
                        break;
                    }
                    case 1565: {
                        final float rop = (float)mr.elementAt(0);
                        final float height = this.scaleY * mr.elementAt(1);
                        final float width = this.scaleX * mr.elementAt(2);
                        final float left2 = this.scaleX * (this.vpX + this.xOffset + mr.elementAt(3));
                        final float top2 = this.scaleY * (this.vpY + this.yOffset + mr.elementAt(4));
                        Paint paint3 = null;
                        boolean ok2 = false;
                        if (rop == 66.0f) {
                            paint3 = Color.black;
                            ok2 = true;
                        }
                        else if (rop == 1.6711778E7f) {
                            paint3 = Color.white;
                            ok2 = true;
                        }
                        else if (rop == 1.5728673E7f && brushObject >= 0) {
                            paint3 = this.getStoredPaint(this.currentStore, brushObject);
                            ok2 = true;
                        }
                        if (ok2) {
                            final Color oldClr = g2d.getColor();
                            if (paint3 != null) {
                                g2d.setPaint(paint3);
                            }
                            else {
                                this.setBrushPaint(this.currentStore, g2d, brushObject);
                            }
                            final Rectangle2D.Float rec3 = new Rectangle2D.Float(left2, top2, width, height);
                            g2d.fill(rec3);
                            g2d.setColor(oldClr);
                        }
                        break;
                    }
                    case 2881: {
                        final int height2 = mr.elementAt(1);
                        final int width2 = mr.elementAt(2);
                        final int sy = mr.elementAt(3);
                        final int sx = mr.elementAt(4);
                        final float dy = this.conv * this.currentStore.getVpWFactor() * (this.vpY + this.yOffset + mr.elementAt(7));
                        final float dx = this.conv * this.currentStore.getVpHFactor() * (this.vpX + this.xOffset + mr.elementAt(8));
                        float heightDst = (float)mr.elementAt(5);
                        float widthDst = (float)mr.elementAt(6);
                        widthDst = widthDst * this.conv * this.currentStore.getVpWFactor();
                        heightDst = heightDst * this.conv * this.currentStore.getVpHFactor();
                        final byte[] bitmap = ((MetaRecord.ByteRecord)mr).bstr;
                        final BufferedImage img = this.getImage(bitmap, width2, height2);
                        if (img != null) {
                            g2d.drawImage(img, (int)dx, (int)dy, (int)(dx + widthDst), (int)(dy + heightDst), sx, sy, sx + width2, sy + height2, this.bkgdColor, this.observer);
                        }
                        break;
                    }
                    case 3907: {
                        final int height2 = mr.elementAt(1);
                        final int width2 = mr.elementAt(2);
                        final int sy = mr.elementAt(3);
                        final int sx = mr.elementAt(4);
                        final float dy = this.conv * this.currentStore.getVpWFactor() * (this.vpY + this.yOffset + mr.elementAt(7));
                        final float dx = this.conv * this.currentStore.getVpHFactor() * (this.vpX + this.xOffset + mr.elementAt(8));
                        float heightDst = (float)mr.elementAt(5);
                        float widthDst = (float)mr.elementAt(6);
                        widthDst = widthDst * this.conv * this.currentStore.getVpWFactor();
                        heightDst = heightDst * this.conv * this.currentStore.getVpHFactor();
                        final byte[] bitmap = ((MetaRecord.ByteRecord)mr).bstr;
                        final BufferedImage img = this.getImage(bitmap, width2, height2);
                        if (img != null) {
                            if (this.opaque) {
                                g2d.drawImage(img, (int)dx, (int)dy, (int)(dx + widthDst), (int)(dy + heightDst), sx, sy, sx + width2, sy + height2, this.bkgdColor, this.observer);
                            }
                            else {
                                g2d.drawImage(img, (int)dx, (int)dy, (int)(dx + widthDst), (int)(dy + heightDst), sx, sy, sx + width2, sy + height2, this.observer);
                            }
                        }
                        break;
                    }
                    case 2368: {
                        final int rop2 = mr.ElementAt(0);
                        final float height = mr.ElementAt(1) * this.conv * this.currentStore.getVpWFactor();
                        final float width = mr.ElementAt(2) * this.conv * this.currentStore.getVpHFactor();
                        final int sy2 = mr.ElementAt(3);
                        final int sx2 = mr.ElementAt(4);
                        final float dy2 = this.conv * this.currentStore.getVpWFactor() * (this.vpY + this.yOffset + mr.ElementAt(5));
                        final float dx2 = this.conv * this.currentStore.getVpHFactor() * (this.vpX + this.xOffset + mr.ElementAt(6));
                        if (mr instanceof MetaRecord.ByteRecord) {
                            final byte[] bitmap2 = ((MetaRecord.ByteRecord)mr).bstr;
                            final BufferedImage img2 = this.getImage(bitmap2);
                            if (img2 != null) {
                                final int withSrc = img2.getWidth();
                                final int heightSrc = img2.getHeight();
                                if (this.opaque) {
                                    g2d.drawImage(img2, (int)dx2, (int)dy2, (int)(dx2 + width), (int)(dy2 + height), sx2, sy2, sx2 + withSrc, sy2 + heightSrc, this.bkgdColor, this.observer);
                                }
                                else {
                                    g2d.drawImage(img2, (int)dx2, (int)dy2, (int)(dx2 + width), (int)(dy2 + height), sx2, sy2, sx2 + withSrc, sy2 + heightSrc, this.observer);
                                }
                            }
                        }
                        else if (this.opaque) {
                            final Color col = g2d.getColor();
                            g2d.setColor(this.bkgdColor);
                            g2d.fill(new Rectangle2D.Float(dx2, dy2, width, height));
                            g2d.setColor(col);
                        }
                        break;
                    }
                    case 322: {
                        int objIndex = 0;
                        final byte[] bitmap3 = ((MetaRecord.ByteRecord)mr).bstr;
                        objIndex = this.addObjectAt(this.currentStore, 2, bitmap3, objIndex);
                        break;
                    }
                }
            }
        }
    }
    
    private Paint getPaint(final byte[] bit) {
        final Dimension d = this.getImageDimension(bit);
        final BufferedImage img = this.getImage(bit);
        final Rectangle2D rec = new Rectangle2D.Float(0.0f, 0.0f, (float)d.width, (float)d.height);
        final TexturePaint paint = new TexturePaint(img, rec);
        return paint;
    }
    
    private void drawString(final int flag, final Graphics2D g2d, final AttributedCharacterIterator ati, final float x, final float y, final TextLayout layout, final WMFFont wmfFont, final int align) {
        if (wmfFont.escape == 0) {
            if (flag != -1) {
                this.fillTextBackground(-1, flag, g2d, x, y, 0.0f, layout);
            }
            final float width = (float)layout.getBounds().getWidth();
            if (align == 6) {
                g2d.drawString(ati, x - width / 2.0f, y);
            }
            else if (align == 2) {
                g2d.drawString(ati, x - width, y);
            }
            else {
                g2d.drawString(ati, x, y);
            }
        }
        else {
            final AffineTransform tr = g2d.getTransform();
            final float angle = -(float)(wmfFont.escape * 3.141592653589793 / 1800.0);
            final float width2 = (float)layout.getBounds().getWidth();
            final float height = (float)layout.getBounds().getHeight();
            if (align == 6) {
                g2d.translate(-width2 / 2.0f, height / 2.0f);
                g2d.rotate(angle, x - width2 / 2.0f, y);
            }
            else if (align == 2) {
                g2d.translate(-width2 / 2.0f, height / 2.0f);
                g2d.rotate(angle, x - width2, y);
            }
            else {
                g2d.translate(0.0, height / 2.0f);
                g2d.rotate(angle, x, y);
            }
            if (flag != -1) {
                this.fillTextBackground(align, flag, g2d, x, y, width2, layout);
            }
            final Stroke _st = g2d.getStroke();
            g2d.setStroke(WMFPainter.textSolid);
            g2d.drawString(ati, x, y);
            g2d.setStroke(_st);
            g2d.setTransform(tr);
        }
    }
    
    private void fillTextBackground(final int align, final int flag, final Graphics2D g2d, final float x, final float y, final float width, final TextLayout layout) {
        float _x = x;
        if (align == 6) {
            _x = x - width / 2.0f;
        }
        else if (align == 2) {
            _x = x - width;
        }
        if ((flag & 0x2) != 0x0) {
            final Color c = g2d.getColor();
            final AffineTransform tr = g2d.getTransform();
            g2d.setColor(this.bkgdColor);
            g2d.translate(_x, y);
            g2d.fill(layout.getBounds());
            g2d.setColor(c);
            g2d.setTransform(tr);
        }
        else if (this.opaque) {
            final Color c = g2d.getColor();
            final AffineTransform tr = g2d.getTransform();
            g2d.setColor(this.bkgdColor);
            g2d.translate(_x, y);
            g2d.fill(layout.getBounds());
            g2d.setColor(c);
            g2d.setTransform(tr);
        }
    }
    
    private void drawPolyPolygon(final Graphics2D g2d, final List pols) {
        for (final Object pol1 : pols) {
            final Polygon2D pol2 = (Polygon2D)pol1;
            g2d.draw(pol2);
        }
    }
    
    private void fillPolyPolygon(final Graphics2D g2d, final List pols) {
        if (pols.size() == 1) {
            g2d.fill(pols.get(0));
        }
        else {
            final GeneralPath path = new GeneralPath(0);
            for (final Object pol1 : pols) {
                final Polygon2D pol2 = (Polygon2D)pol1;
                path.append(pol2, false);
            }
            g2d.fill(path);
        }
    }
    
    private void setStroke(final Graphics2D g2d, final int penStyle, final float penWidth, final float scale) {
        float _width;
        if (penWidth == 0.0f) {
            _width = 1.0f;
        }
        else {
            _width = penWidth;
        }
        float _scale = Platform.getScreenResolution() / (float)this.currentStore.getMetaFileUnitsPerInch();
        final float factor = scale / _scale;
        _width = _width * _scale * factor;
        _scale = this.currentStore.getWidthPixels() * 1.0f / 350.0f;
        if (penStyle == 0) {
            final BasicStroke stroke = new BasicStroke(_width, 0, 1);
            g2d.setStroke(stroke);
        }
        else if (penStyle == 2) {
            final float[] dash = { 1.0f * _scale, 5.0f * _scale };
            final BasicStroke stroke = new BasicStroke(_width, 0, 1, 10.0f * _scale, dash, 0.0f);
            g2d.setStroke(stroke);
        }
        else if (penStyle == 1) {
            final float[] dash = { 5.0f * _scale, 2.0f * _scale };
            final BasicStroke stroke = new BasicStroke(_width, 0, 1, 10.0f * _scale, dash, 0.0f);
            g2d.setStroke(stroke);
        }
        else if (penStyle == 3) {
            final float[] dash = { 5.0f * _scale, 2.0f * _scale, 1.0f * _scale, 2.0f * _scale };
            final BasicStroke stroke = new BasicStroke(_width, 0, 1, 10.0f * _scale, dash, 0.0f);
            g2d.setStroke(stroke);
        }
        else if (penStyle == 4) {
            final float[] dash = { 5.0f * _scale, 2.0f * _scale, 1.0f * _scale, 2.0f * _scale, 1.0f * _scale, 2.0f * _scale };
            final BasicStroke stroke = new BasicStroke(_width, 0, 1, 15.0f * _scale, dash, 0.0f);
            g2d.setStroke(stroke);
        }
        else {
            final BasicStroke stroke = new BasicStroke(_width, 0, 1);
            g2d.setStroke(stroke);
        }
    }
    
    private void setPenColor(final WMFRecordStore currentStore, final Graphics2D g2d, int penObject) {
        if (penObject >= 0) {
            final GdiObject gdiObj = currentStore.getObject(penObject);
            g2d.setColor((Color)gdiObj.obj);
            penObject = -1;
        }
    }
    
    private int getHorizontalAlignement(final int align) {
        int v = align;
        v %= 24;
        v %= 8;
        if (v >= 6) {
            return 6;
        }
        if (v >= 2) {
            return 2;
        }
        return 0;
    }
    
    private void setBrushPaint(final WMFRecordStore currentStore, final Graphics2D g2d, int brushObject) {
        if (brushObject >= 0) {
            final GdiObject gdiObj = currentStore.getObject(brushObject);
            if (gdiObj.obj instanceof Color) {
                g2d.setColor((Color)gdiObj.obj);
            }
            else if (gdiObj.obj instanceof Paint) {
                g2d.setPaint((Paint)gdiObj.obj);
            }
            else {
                g2d.setPaint(this.getPaint((byte[])gdiObj.obj));
            }
            brushObject = -1;
        }
    }
    
    private Paint getStoredPaint(final WMFRecordStore currentStore, final int object) {
        if (object < 0) {
            return null;
        }
        final GdiObject gdiObj = currentStore.getObject(object);
        if (gdiObj.obj instanceof Paint) {
            return (Paint)gdiObj.obj;
        }
        return this.getPaint((byte[])gdiObj.obj);
    }
    
    private void paint(final int brushObject, final int penObject, final Shape shape, final Graphics2D g2d) {
        if (brushObject >= 0) {
            final Paint paint = this.getStoredPaint(this.currentStore, brushObject);
            if (!this.firstEffectivePaint || !paint.equals(Color.white)) {
                this.setBrushPaint(this.currentStore, g2d, brushObject);
                g2d.fill(shape);
                this.firstEffectivePaint = false;
            }
        }
        if (penObject >= 0) {
            final Paint paint = this.getStoredPaint(this.currentStore, penObject);
            if (!this.firstEffectivePaint || !paint.equals(Color.white)) {
                this.setPenColor(this.currentStore, g2d, penObject);
                g2d.draw(shape);
                this.firstEffectivePaint = false;
            }
        }
    }
    
    private void paintWithPen(final int penObject, final Shape shape, final Graphics2D g2d) {
        if (penObject >= 0) {
            final Paint paint = this.getStoredPaint(this.currentStore, penObject);
            if (!this.firstEffectivePaint || !paint.equals(Color.white)) {
                this.setPenColor(this.currentStore, g2d, penObject);
                g2d.draw(shape);
                this.firstEffectivePaint = false;
            }
        }
    }
    
    private float getVerticalAlignmentValue(final TextLayout layout, final int vertAlign) {
        if (vertAlign == 8) {
            return -layout.getDescent();
        }
        if (vertAlign == 0) {
            return layout.getAscent();
        }
        return 0.0f;
    }
    
    @Override
    public WMFRecordStore getRecordStore() {
        return this.currentStore;
    }
    
    static {
        INTEGER_0 = 0;
        WMFPainter.solid = new BasicStroke(1.0f, 0, 1);
        WMFPainter.textSolid = new BasicStroke(1.0f, 0, 1);
    }
}
