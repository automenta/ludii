// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.transcoder.wmf.tosvg;

import java.awt.geom.AffineTransform;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import org.apache.batik.ext.awt.geom.Polyline2D;
import java.awt.Shape;
import org.apache.batik.ext.awt.geom.Polygon2D;
import java.awt.Font;
import java.awt.font.TextLayout;
import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.File;
import java.awt.font.FontRenderContext;
import java.io.DataInputStream;

public class WMFHeaderProperties extends AbstractWMFReader
{
    private static final Integer INTEGER_0;
    protected DataInputStream stream;
    private int _bleft;
    private int _bright;
    private int _btop;
    private int _bbottom;
    private int _bwidth;
    private int _bheight;
    private int _ileft;
    private int _iright;
    private int _itop;
    private int _ibottom;
    private float scale;
    private int startX;
    private int startY;
    private int currentHorizAlign;
    private int currentVertAlign;
    private WMFFont wf;
    private static final FontRenderContext fontCtx;
    private transient boolean firstEffectivePaint;
    public static final int PEN = 1;
    public static final int BRUSH = 2;
    public static final int FONT = 3;
    public static final int NULL_PEN = 4;
    public static final int NULL_BRUSH = 5;
    public static final int PALETTE = 6;
    public static final int OBJ_BITMAP = 7;
    public static final int OBJ_REGION = 8;
    
    public WMFHeaderProperties(final File wmffile) throws IOException {
        this.scale = 1.0f;
        this.startX = 0;
        this.startY = 0;
        this.currentHorizAlign = 0;
        this.currentVertAlign = 0;
        this.wf = null;
        this.firstEffectivePaint = true;
        this.reset();
        this.read(this.stream = new DataInputStream(new BufferedInputStream(new FileInputStream(wmffile))));
        this.stream.close();
    }
    
    public WMFHeaderProperties() {
        this.scale = 1.0f;
        this.startX = 0;
        this.startY = 0;
        this.currentHorizAlign = 0;
        this.currentVertAlign = 0;
        this.wf = null;
        this.firstEffectivePaint = true;
    }
    
    public void closeResource() {
        try {
            if (this.stream != null) {
                this.stream.close();
            }
        }
        catch (IOException ex) {}
    }
    
    public void setFile(final File wmffile) throws IOException {
        this.read(this.stream = new DataInputStream(new BufferedInputStream(new FileInputStream(wmffile))));
        this.stream.close();
    }
    
    @Override
    public void reset() {
        this.left = 0;
        this.right = 0;
        this.top = 1000;
        this.bottom = 1000;
        this.inch = 84;
        this._bleft = -1;
        this._bright = -1;
        this._btop = -1;
        this._bbottom = -1;
        this._ileft = -1;
        this._iright = -1;
        this._itop = -1;
        this._ibottom = -1;
        this._bwidth = -1;
        this._bheight = -1;
        this.vpW = -1;
        this.vpH = -1;
        this.vpX = 0;
        this.vpY = 0;
        this.startX = 0;
        this.startY = 0;
        this.scaleXY = 1.0f;
        this.firstEffectivePaint = true;
    }
    
    public DataInputStream getStream() {
        return this.stream;
    }
    
    @Override
    protected boolean readRecords(final DataInputStream is) throws IOException {
        short functionId = 1;
        int recSize = 0;
        int brushObject = -1;
        int penObject = -1;
        int fontObject = -1;
        while (functionId > 0) {
            recSize = this.readInt(is);
            recSize -= 3;
            functionId = this.readShort(is);
            if (functionId <= 0) {
                break;
            }
            switch (functionId) {
                case 259: {
                    final int mapmode = this.readShort(is);
                    if (mapmode != 8) {
                        continue;
                    }
                    this.isotropic = false;
                    continue;
                }
                case 523: {
                    this.vpY = this.readShort(is);
                    this.vpX = this.readShort(is);
                    continue;
                }
                case 524: {
                    this.vpH = this.readShort(is);
                    this.vpW = this.readShort(is);
                    if (!this.isotropic) {
                        this.scaleXY = this.vpW / (float)this.vpH;
                    }
                    this.vpW *= (int)this.scaleXY;
                    continue;
                }
                case 762: {
                    int objIndex = 0;
                    final int penStyle = this.readShort(is);
                    this.readInt(is);
                    final int colorref = this.readInt(is);
                    final int red = colorref & 0xFF;
                    final int green = (colorref & 0xFF00) >> 8;
                    final int blue = (colorref & 0xFF0000) >> 16;
                    final Color color = new Color(red, green, blue);
                    if (recSize == 6) {
                        this.readShort(is);
                    }
                    if (penStyle == 5) {
                        objIndex = this.addObjectAt(4, color, objIndex);
                    }
                    else {
                        objIndex = this.addObjectAt(1, color, objIndex);
                    }
                    continue;
                }
                case 764: {
                    int objIndex = 0;
                    final int brushStyle = this.readShort(is);
                    final int colorref = this.readInt(is);
                    final int red = colorref & 0xFF;
                    final int green = (colorref & 0xFF00) >> 8;
                    final int blue = (colorref & 0xFF0000) >> 16;
                    final Color color = new Color(red, green, blue);
                    this.readShort(is);
                    if (brushStyle == 5) {
                        objIndex = this.addObjectAt(5, color, objIndex);
                    }
                    else {
                        objIndex = this.addObjectAt(2, color, objIndex);
                    }
                    continue;
                }
                case 302: {
                    final int align = this.readShort(is);
                    if (recSize > 1) {
                        for (int i = 1; i < recSize; ++i) {
                            this.readShort(is);
                        }
                    }
                    this.currentHorizAlign = WMFUtilities.getHorizontalAlignment(align);
                    this.currentVertAlign = WMFUtilities.getVerticalAlignment(align);
                    continue;
                }
                case 2610: {
                    final int y = this.readShort(is);
                    int x = (int)(this.readShort(is) * this.scaleXY);
                    final int lenText = this.readShort(is);
                    final int flag = this.readShort(is);
                    int read = 4;
                    boolean clipped = false;
                    int x2 = 0;
                    int y2 = 0;
                    int x3 = 0;
                    int y3 = 0;
                    if ((flag & 0x4) != 0x0) {
                        x2 = (int)(this.readShort(is) * this.scaleXY);
                        y2 = this.readShort(is);
                        x3 = (int)(this.readShort(is) * this.scaleXY);
                        y3 = this.readShort(is);
                        read += 4;
                        clipped = true;
                    }
                    final byte[] bstr = new byte[lenText];
                    for (int j = 0; j < lenText; ++j) {
                        bstr[j] = is.readByte();
                    }
                    final String sr = WMFUtilities.decodeString(this.wf, bstr);
                    read += (lenText + 1) / 2;
                    if (lenText % 2 != 0) {
                        is.readByte();
                    }
                    if (read < recSize) {
                        for (int k = read; k < recSize; ++k) {
                            this.readShort(is);
                        }
                    }
                    final TextLayout layout = new TextLayout(sr, this.wf.font, WMFHeaderProperties.fontCtx);
                    final int lfWidth = (int)layout.getBounds().getWidth();
                    x = (int)layout.getBounds().getX();
                    final int lfHeight = (int)this.getVerticalAlignmentValue(layout, this.currentVertAlign);
                    this.resizeBounds(x, y);
                    this.resizeBounds(x + lfWidth, y + lfHeight);
                    this.firstEffectivePaint = false;
                    continue;
                }
                case 1313:
                case 1583: {
                    final int len = this.readShort(is);
                    int read2 = 1;
                    final byte[] bstr2 = new byte[len];
                    for (int l = 0; l < len; ++l) {
                        bstr2[l] = is.readByte();
                    }
                    final String sr2 = WMFUtilities.decodeString(this.wf, bstr2);
                    if (len % 2 != 0) {
                        is.readByte();
                    }
                    read2 += (len + 1) / 2;
                    final int y4 = this.readShort(is);
                    int x4 = (int)(this.readShort(is) * this.scaleXY);
                    read2 += 2;
                    if (read2 < recSize) {
                        for (int m = read2; m < recSize; ++m) {
                            this.readShort(is);
                        }
                    }
                    final TextLayout layout2 = new TextLayout(sr2, this.wf.font, WMFHeaderProperties.fontCtx);
                    final int lfWidth2 = (int)layout2.getBounds().getWidth();
                    x4 = (int)layout2.getBounds().getX();
                    final int lfHeight2 = (int)this.getVerticalAlignmentValue(layout2, this.currentVertAlign);
                    this.resizeBounds(x4, y4);
                    this.resizeBounds(x4 + lfWidth2, y4 + lfHeight2);
                    continue;
                }
                case 763: {
                    final int lfHeight3 = this.readShort(is);
                    float size = (float)(int)(this.scaleY * lfHeight3);
                    final int lfWidth3 = this.readShort(is);
                    final int escape = this.readShort(is);
                    final int orient = this.readShort(is);
                    final int weight = this.readShort(is);
                    final int italic = is.readByte();
                    final int underline = is.readByte();
                    final int strikeOut = is.readByte();
                    final int charset = is.readByte() & 0xFF;
                    final int lfOutPrecision = is.readByte();
                    final int lfClipPrecision = is.readByte();
                    final int lfQuality = is.readByte();
                    final int lfPitchAndFamily = is.readByte();
                    int style = (italic > 0) ? 2 : 0;
                    style |= ((weight > 400) ? 1 : 0);
                    final int len2 = 2 * (recSize - 9);
                    final byte[] lfFaceName = new byte[len2];
                    for (int i2 = 0; i2 < len2; ++i2) {
                        lfFaceName[i2] = is.readByte();
                    }
                    String face;
                    int d;
                    for (face = new String(lfFaceName), d = 0; d < face.length() && (Character.isLetterOrDigit(face.charAt(d)) || Character.isWhitespace(face.charAt(d))); ++d) {}
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
                    Font f = new Font(face, style, (int)size);
                    f = f.deriveFont(size);
                    final WMFFont wf = new WMFFont(f, charset, underline, strikeOut, italic, weight, orient, escape);
                    objIndex2 = this.addObjectAt(3, wf, objIndex2);
                    continue;
                }
                case 1791: {
                    int objIndex3 = 0;
                    for (int j2 = 0; j2 < recSize; ++j2) {
                        this.readShort(is);
                    }
                    objIndex3 = this.addObjectAt(6, WMFHeaderProperties.INTEGER_0, 0);
                    continue;
                }
                case 247: {
                    int objIndex3 = 0;
                    for (int j2 = 0; j2 < recSize; ++j2) {
                        this.readShort(is);
                    }
                    objIndex3 = this.addObjectAt(8, WMFHeaderProperties.INTEGER_0, 0);
                    continue;
                }
                case 301: {
                    final int gdiIndex = this.readShort(is);
                    if ((gdiIndex & Integer.MIN_VALUE) != 0x0) {
                        continue;
                    }
                    final GdiObject gdiObj = this.getObject(gdiIndex);
                    if (!gdiObj.used) {
                        continue;
                    }
                    switch (gdiObj.type) {
                        case 1: {
                            penObject = gdiIndex;
                            continue;
                        }
                        case 2: {
                            brushObject = gdiIndex;
                            continue;
                        }
                        case 3: {
                            this.wf = (WMFFont)gdiObj.obj;
                            fontObject = gdiIndex;
                            continue;
                        }
                        case 4: {
                            penObject = -1;
                            continue;
                        }
                        case 5: {
                            brushObject = -1;
                            continue;
                        }
                    }
                    continue;
                }
                case 496: {
                    final int gdiIndex = this.readShort(is);
                    final GdiObject gdiObj = this.getObject(gdiIndex);
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
                    continue;
                }
                case 531: {
                    final int y = this.readShort(is);
                    final int x = (int)(this.readShort(is) * this.scaleXY);
                    if (penObject >= 0) {
                        this.resizeBounds(this.startX, this.startY);
                        this.resizeBounds(x, y);
                        this.firstEffectivePaint = false;
                    }
                    this.startX = x;
                    this.startY = y;
                    continue;
                }
                case 532: {
                    this.startY = this.readShort(is);
                    this.startX = (int)(this.readShort(is) * this.scaleXY);
                    continue;
                }
                case 1336: {
                    final int count = this.readShort(is);
                    final int[] pts = new int[count];
                    int ptCount = 0;
                    for (int l = 0; l < count; ++l) {
                        pts[l] = this.readShort(is);
                        ptCount += pts[l];
                    }
                    final int offset = count + 1;
                    for (int i3 = 0; i3 < count; ++i3) {
                        for (int j3 = 0; j3 < pts[i3]; ++j3) {
                            final int x5 = (int)(this.readShort(is) * this.scaleXY);
                            final int y5 = this.readShort(is);
                            if (brushObject >= 0 || penObject >= 0) {
                                this.resizeBounds(x5, y5);
                            }
                        }
                    }
                    this.firstEffectivePaint = false;
                    continue;
                }
                case 804: {
                    final int count = this.readShort(is);
                    final float[] _xpts = new float[count + 1];
                    final float[] _ypts = new float[count + 1];
                    for (int l = 0; l < count; ++l) {
                        _xpts[l] = this.readShort(is) * this.scaleXY;
                        _ypts[l] = this.readShort(is);
                    }
                    _xpts[count] = _xpts[0];
                    _ypts[count] = _ypts[0];
                    final Polygon2D pol = new Polygon2D(_xpts, _ypts, count);
                    this.paint(brushObject, penObject, pol);
                    continue;
                }
                case 805: {
                    final int count = this.readShort(is);
                    final float[] _xpts = new float[count];
                    final float[] _ypts = new float[count];
                    for (int l = 0; l < count; ++l) {
                        _xpts[l] = this.readShort(is) * this.scaleXY;
                        _ypts[l] = this.readShort(is);
                    }
                    final Polyline2D pol2 = new Polyline2D(_xpts, _ypts, count);
                    this.paintWithPen(penObject, pol2);
                    continue;
                }
                case 1046:
                case 1048:
                case 1051: {
                    final int bot = this.readShort(is);
                    final int right = (int)(this.readShort(is) * this.scaleXY);
                    final int top = this.readShort(is);
                    final int left = (int)(this.readShort(is) * this.scaleXY);
                    final Rectangle2D.Float rec = new Rectangle2D.Float((float)left, (float)top, (float)(right - left), (float)(bot - top));
                    this.paint(brushObject, penObject, rec);
                    continue;
                }
                case 1564: {
                    this.readShort(is);
                    this.readShort(is);
                    final int bot = this.readShort(is);
                    final int right = (int)(this.readShort(is) * this.scaleXY);
                    final int top = this.readShort(is);
                    final int left = (int)(this.readShort(is) * this.scaleXY);
                    final Rectangle2D.Float rec = new Rectangle2D.Float((float)left, (float)top, (float)(right - left), (float)(bot - top));
                    this.paint(brushObject, penObject, rec);
                    continue;
                }
                case 2071:
                case 2074:
                case 2096: {
                    this.readShort(is);
                    this.readShort(is);
                    this.readShort(is);
                    this.readShort(is);
                    final int bot = this.readShort(is);
                    final int right = (int)(this.readShort(is) * this.scaleXY);
                    final int top = this.readShort(is);
                    final int left = (int)(this.readShort(is) * this.scaleXY);
                    final Rectangle2D.Float rec = new Rectangle2D.Float((float)left, (float)top, (float)(right - left), (float)(bot - top));
                    this.paint(brushObject, penObject, rec);
                    continue;
                }
                case 1565: {
                    this.readInt(is);
                    final int height = this.readShort(is);
                    final int width = (int)(this.readShort(is) * this.scaleXY);
                    final int left2 = (int)(this.readShort(is) * this.scaleXY);
                    final int top2 = this.readShort(is);
                    if (penObject >= 0) {
                        this.resizeBounds(left2, top2);
                    }
                    if (penObject < 0) {
                        continue;
                    }
                    this.resizeBounds(left2 + width, top2 + height);
                    continue;
                }
                case 2881: {
                    is.readInt();
                    this.readShort(is);
                    this.readShort(is);
                    this.readShort(is);
                    this.readShort(is);
                    float heightDst = this.readShort(is);
                    float widthDst = this.readShort(is) * this.scaleXY;
                    final float dy = this.readShort(is) * this.getVpWFactor() * this.inch / WMFHeaderProperties.PIXEL_PER_INCH;
                    final float dx = this.readShort(is) * this.getVpWFactor() * this.inch / WMFHeaderProperties.PIXEL_PER_INCH * this.scaleXY;
                    widthDst = widthDst * this.getVpWFactor() * this.inch / WMFHeaderProperties.PIXEL_PER_INCH;
                    heightDst = heightDst * this.getVpHFactor() * this.inch / WMFHeaderProperties.PIXEL_PER_INCH;
                    this.resizeImageBounds((int)dx, (int)dy);
                    this.resizeImageBounds((int)(dx + widthDst), (int)(dy + heightDst));
                    for (int len3 = 2 * recSize - 20, i4 = 0; i4 < len3; ++i4) {
                        is.readByte();
                    }
                    continue;
                }
                case 3907: {
                    is.readInt();
                    this.readShort(is);
                    this.readShort(is);
                    this.readShort(is);
                    this.readShort(is);
                    this.readShort(is);
                    float heightDst = this.readShort(is);
                    float widthDst = this.readShort(is) * this.scaleXY;
                    final float dy = this.readShort(is) * this.getVpHFactor() * this.inch / WMFHeaderProperties.PIXEL_PER_INCH;
                    final float dx = this.readShort(is) * this.getVpHFactor() * this.inch / WMFHeaderProperties.PIXEL_PER_INCH * this.scaleXY;
                    widthDst = widthDst * this.getVpWFactor() * this.inch / WMFHeaderProperties.PIXEL_PER_INCH;
                    heightDst = heightDst * this.getVpHFactor() * this.inch / WMFHeaderProperties.PIXEL_PER_INCH;
                    this.resizeImageBounds((int)dx, (int)dy);
                    this.resizeImageBounds((int)(dx + widthDst), (int)(dy + heightDst));
                    final int len3 = 2 * recSize - 22;
                    final byte[] bitmap = new byte[len3];
                    for (int i5 = 0; i5 < len3; ++i5) {
                        bitmap[i5] = is.readByte();
                    }
                    continue;
                }
                case 2368: {
                    is.readInt();
                    this.readShort(is);
                    this.readShort(is);
                    this.readShort(is);
                    final float height2 = this.readShort(is) * (float)this.inch / WMFHeaderProperties.PIXEL_PER_INCH * this.getVpHFactor();
                    final float width2 = this.readShort(is) * (float)this.inch / WMFHeaderProperties.PIXEL_PER_INCH * this.getVpWFactor() * this.scaleXY;
                    final float dy = this.inch / WMFHeaderProperties.PIXEL_PER_INCH * this.getVpHFactor() * this.readShort(is);
                    final float dx = this.inch / WMFHeaderProperties.PIXEL_PER_INCH * this.getVpWFactor() * this.readShort(is) * this.scaleXY;
                    this.resizeImageBounds((int)dx, (int)dy);
                    this.resizeImageBounds((int)(dx + width2), (int)(dy + height2));
                    continue;
                }
                default: {
                    for (int j4 = 0; j4 < recSize; ++j4) {
                        this.readShort(is);
                    }
                    continue;
                }
            }
        }
        if (!this.isAldus) {
            this.width = this.vpW;
            this.height = this.vpH;
            this.right = this.vpX;
            this.left = this.vpX + this.vpW;
            this.top = this.vpY;
            this.bottom = this.vpY + this.vpH;
        }
        this.resetBounds();
        return true;
    }
    
    public int getWidthBoundsPixels() {
        return this._bwidth;
    }
    
    public int getHeightBoundsPixels() {
        return this._bheight;
    }
    
    public int getWidthBoundsUnits() {
        return (int)(this.inch * (float)this._bwidth / WMFHeaderProperties.PIXEL_PER_INCH);
    }
    
    public int getHeightBoundsUnits() {
        return (int)(this.inch * (float)this._bheight / WMFHeaderProperties.PIXEL_PER_INCH);
    }
    
    public int getXOffset() {
        return this._bleft;
    }
    
    public int getYOffset() {
        return this._btop;
    }
    
    private void resetBounds() {
        this.scale = this.getWidthPixels() / (float)this.vpW;
        if (this._bright != -1) {
            this._bright = (int)(this.scale * (this.vpX + this._bright));
            this._bleft = (int)(this.scale * (this.vpX + this._bleft));
            this._bbottom = (int)(this.scale * (this.vpY + this._bbottom));
            this._btop = (int)(this.scale * (this.vpY + this._btop));
        }
        if (this._iright != -1) {
            this._iright = (int)(this._iright * (float)this.getWidthPixels() / this.width);
            this._ileft = (int)(this._ileft * (float)this.getWidthPixels() / this.width);
            this._ibottom = (int)(this._ibottom * (float)this.getWidthPixels() / this.width);
            this._itop = (int)(this._itop * (float)this.getWidthPixels() / this.width);
            if (this._bright == -1 || this._iright > this._bright) {
                this._bright = this._iright;
            }
            if (this._bleft == -1 || this._ileft < this._bleft) {
                this._bleft = this._ileft;
            }
            if (this._btop == -1 || this._itop < this._btop) {
                this._btop = this._itop;
            }
            if (this._bbottom == -1 || this._ibottom > this._bbottom) {
                this._bbottom = this._ibottom;
            }
        }
        if (this._bleft != -1 && this._bright != -1) {
            this._bwidth = this._bright - this._bleft;
        }
        if (this._btop != -1 && this._bbottom != -1) {
            this._bheight = this._bbottom - this._btop;
        }
    }
    
    private void resizeBounds(final int x, final int y) {
        if (this._bleft == -1) {
            this._bleft = x;
        }
        else if (x < this._bleft) {
            this._bleft = x;
        }
        if (this._bright == -1) {
            this._bright = x;
        }
        else if (x > this._bright) {
            this._bright = x;
        }
        if (this._btop == -1) {
            this._btop = y;
        }
        else if (y < this._btop) {
            this._btop = y;
        }
        if (this._bbottom == -1) {
            this._bbottom = y;
        }
        else if (y > this._bbottom) {
            this._bbottom = y;
        }
    }
    
    private void resizeImageBounds(final int x, final int y) {
        if (this._ileft == -1) {
            this._ileft = x;
        }
        else if (x < this._ileft) {
            this._ileft = x;
        }
        if (this._iright == -1) {
            this._iright = x;
        }
        else if (x > this._iright) {
            this._iright = x;
        }
        if (this._itop == -1) {
            this._itop = y;
        }
        else if (y < this._itop) {
            this._itop = y;
        }
        if (this._ibottom == -1) {
            this._ibottom = y;
        }
        else if (y > this._ibottom) {
            this._ibottom = y;
        }
    }
    
    private Color getColorFromObject(final int brushObject) {
        final Color color = null;
        if (brushObject >= 0) {
            final GdiObject gdiObj = this.getObject(brushObject);
            return (Color)gdiObj.obj;
        }
        return null;
    }
    
    private void paint(final int brushObject, final int penObject, final Shape shape) {
        if (brushObject >= 0 || penObject >= 0) {
            Color col;
            if (brushObject >= 0) {
                col = this.getColorFromObject(brushObject);
            }
            else {
                col = this.getColorFromObject(penObject);
            }
            if (!this.firstEffectivePaint || !col.equals(Color.white)) {
                final Rectangle rec = shape.getBounds();
                this.resizeBounds((int)rec.getMinX(), (int)rec.getMinY());
                this.resizeBounds((int)rec.getMaxX(), (int)rec.getMaxY());
                this.firstEffectivePaint = false;
            }
        }
    }
    
    private void paintWithPen(final int penObject, final Shape shape) {
        if (penObject >= 0) {
            final Color col = this.getColorFromObject(penObject);
            if (!this.firstEffectivePaint || !col.equals(Color.white)) {
                final Rectangle rec = shape.getBounds();
                this.resizeBounds((int)rec.getMinX(), (int)rec.getMinY());
                this.resizeBounds((int)rec.getMaxX(), (int)rec.getMaxY());
                this.firstEffectivePaint = false;
            }
        }
    }
    
    private float getVerticalAlignmentValue(final TextLayout layout, final int vertAlign) {
        if (vertAlign == 24) {
            return -layout.getAscent();
        }
        if (vertAlign == 0) {
            return layout.getAscent() + layout.getDescent();
        }
        return 0.0f;
    }
    
    static {
        INTEGER_0 = 0;
        fontCtx = new FontRenderContext(new AffineTransform(), false, true);
    }
}
