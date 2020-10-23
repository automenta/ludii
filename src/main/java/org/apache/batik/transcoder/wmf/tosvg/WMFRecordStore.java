// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.transcoder.wmf.tosvg;

import java.io.IOException;
import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.List;
import java.net.URL;

public class WMFRecordStore extends AbstractWMFReader
{
    private URL url;
    protected int numRecords;
    protected float vpX;
    protected float vpY;
    protected List records;
    private boolean _bext;
    
    public WMFRecordStore() {
        this._bext = true;
        this.reset();
    }
    
    @Override
    public void reset() {
        this.numRecords = 0;
        this.vpX = 0.0f;
        this.vpY = 0.0f;
        this.vpW = 1000;
        this.vpH = 1000;
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.scaleXY = 1.0f;
        this.inch = 84;
        this.records = new ArrayList(20);
    }
    
    @Override
    protected boolean readRecords(final DataInputStream is) throws IOException {
        short functionId = 1;
        int recSize = 0;
        this.numRecords = 0;
        while (functionId > 0) {
            recSize = this.readInt(is);
            recSize -= 3;
            functionId = this.readShort(is);
            if (functionId <= 0) {
                break;
            }
            MetaRecord mr = new MetaRecord();
            switch (functionId) {
                case 259: {
                    mr.numPoints = recSize;
                    mr.functionId = functionId;
                    final int mapmode = this.readShort(is);
                    if (mapmode == 8) {
                        this.isotropic = false;
                    }
                    mr.addElement(mapmode);
                    this.records.add(mr);
                    break;
                }
                case 1583: {
                    for (int i = 0; i < recSize; ++i) {
                        final short recData = this.readShort(is);
                    }
                    --this.numRecords;
                    break;
                }
                case 2610: {
                    final int yVal = this.readShort(is) * this.ySign;
                    final int xVal = (int)(this.readShort(is) * this.xSign * this.scaleXY);
                    final int lenText = this.readShort(is);
                    final int flag = this.readShort(is);
                    int read = 4;
                    boolean clipped = false;
                    int x1 = 0;
                    int y1 = 0;
                    int x2 = 0;
                    int y2 = 0;
                    if ((flag & 0x4) != 0x0) {
                        x1 = (int)(this.readShort(is) * this.xSign * this.scaleXY);
                        y1 = this.readShort(is) * this.ySign;
                        x2 = (int)(this.readShort(is) * this.xSign * this.scaleXY);
                        y2 = this.readShort(is) * this.ySign;
                        read += 4;
                        clipped = true;
                    }
                    final byte[] bstr = new byte[lenText];
                    for (int j = 0; j < lenText; ++j) {
                        bstr[j] = is.readByte();
                    }
                    read += (lenText + 1) / 2;
                    if (lenText % 2 != 0) {
                        is.readByte();
                    }
                    if (read < recSize) {
                        for (int k = read; k < recSize; ++k) {
                            this.readShort(is);
                        }
                    }
                    mr = new MetaRecord.ByteRecord(bstr);
                    mr.numPoints = recSize;
                    mr.functionId = functionId;
                    mr.addElement(xVal);
                    mr.addElement(yVal);
                    mr.addElement(flag);
                    if (clipped) {
                        mr.addElement(x1);
                        mr.addElement(y1);
                        mr.addElement(x2);
                        mr.addElement(y2);
                    }
                    this.records.add(mr);
                    break;
                }
                case 1313: {
                    final int len = this.readShort(is);
                    int read2 = 1;
                    final byte[] bstr2 = new byte[len];
                    for (int l = 0; l < len; ++l) {
                        bstr2[l] = is.readByte();
                    }
                    if (len % 2 != 0) {
                        is.readByte();
                    }
                    read2 += (len + 1) / 2;
                    final int yVal2 = this.readShort(is) * this.ySign;
                    final int xVal2 = (int)(this.readShort(is) * this.xSign * this.scaleXY);
                    read2 += 2;
                    if (read2 < recSize) {
                        for (int m = read2; m < recSize; ++m) {
                            this.readShort(is);
                        }
                    }
                    mr = new MetaRecord.ByteRecord(bstr2);
                    mr.numPoints = recSize;
                    mr.functionId = functionId;
                    mr.addElement(xVal2);
                    mr.addElement(yVal2);
                    this.records.add(mr);
                    break;
                }
                case 763: {
                    final int lfHeight = this.readShort(is);
                    final int lfWidth = this.readShort(is);
                    final int lfEscapement = this.readShort(is);
                    final int lfOrientation = this.readShort(is);
                    final int lfWeight = this.readShort(is);
                    final int lfItalic = is.readByte();
                    final int lfUnderline = is.readByte();
                    final int lfStrikeOut = is.readByte();
                    final int lfCharSet = is.readByte() & 0xFF;
                    final int lfOutPrecision = is.readByte();
                    final int lfClipPrecision = is.readByte();
                    final int lfQuality = is.readByte();
                    final int lfPitchAndFamily = is.readByte();
                    final int len2 = 2 * (recSize - 9);
                    final byte[] lfFaceName = new byte[len2];
                    for (int i2 = 0; i2 < len2; ++i2) {
                        lfFaceName[i2] = is.readByte();
                    }
                    final String str = new String(lfFaceName);
                    mr = new MetaRecord.StringRecord(str);
                    mr.numPoints = recSize;
                    mr.functionId = functionId;
                    mr.addElement(lfHeight);
                    mr.addElement(lfItalic);
                    mr.addElement(lfWeight);
                    mr.addElement(lfCharSet);
                    mr.addElement(lfUnderline);
                    mr.addElement(lfStrikeOut);
                    mr.addElement(lfOrientation);
                    mr.addElement(lfEscapement);
                    this.records.add(mr);
                    break;
                }
                case 523:
                case 524:
                case 525:
                case 526: {
                    mr.numPoints = recSize;
                    mr.functionId = functionId;
                    int height = this.readShort(is);
                    int width = this.readShort(is);
                    if (width < 0) {
                        width = -width;
                        this.xSign = -1;
                    }
                    if (height < 0) {
                        height = -height;
                        this.ySign = -1;
                    }
                    if (this._bext && functionId == 524) {
                        this.vpW = width;
                        this.vpH = height;
                        this._bext = false;
                    }
                    if (!this.isAldus) {
                        this.width = this.vpW;
                        this.height = this.vpH;
                    }
                    mr.addElement((int)(width * this.scaleXY));
                    mr.addElement(height);
                    this.records.add(mr);
                    break;
                }
                case 527:
                case 529: {
                    mr.numPoints = recSize;
                    mr.functionId = functionId;
                    final int y3 = this.readShort(is) * this.ySign;
                    final int x3 = (int)(this.readShort(is) * this.xSign * this.scaleXY);
                    mr.addElement(x3);
                    mr.addElement(y3);
                    this.records.add(mr);
                    break;
                }
                case 1040:
                case 1042: {
                    mr.numPoints = recSize;
                    mr.functionId = functionId;
                    final int ydenom = this.readShort(is);
                    final int ynum = this.readShort(is);
                    final int xdenom = this.readShort(is);
                    final int xnum = this.readShort(is);
                    mr.addElement(xdenom);
                    mr.addElement(ydenom);
                    mr.addElement(xnum);
                    mr.addElement(ynum);
                    this.records.add(mr);
                    this.scaleX = this.scaleX * xdenom / xnum;
                    this.scaleY = this.scaleY * ydenom / ynum;
                    break;
                }
                case 764: {
                    mr.numPoints = recSize;
                    mr.functionId = functionId;
                    mr.addElement(this.readShort(is));
                    final int colorref = this.readInt(is);
                    final int red = colorref & 0xFF;
                    final int green = (colorref & 0xFF00) >> 8;
                    final int blue = (colorref & 0xFF0000) >> 16;
                    final int flags = (colorref & 0x3000000) >> 24;
                    mr.addElement(red);
                    mr.addElement(green);
                    mr.addElement(blue);
                    mr.addElement(this.readShort(is));
                    this.records.add(mr);
                    break;
                }
                case 762: {
                    mr.numPoints = recSize;
                    mr.functionId = functionId;
                    mr.addElement(this.readShort(is));
                    final int width2 = this.readInt(is);
                    final int colorref2 = this.readInt(is);
                    if (recSize == 6) {
                        this.readShort(is);
                    }
                    final int red2 = colorref2 & 0xFF;
                    final int green2 = (colorref2 & 0xFF00) >> 8;
                    final int blue2 = (colorref2 & 0xFF0000) >> 16;
                    final int flags2 = (colorref2 & 0x3000000) >> 24;
                    mr.addElement(red2);
                    mr.addElement(green2);
                    mr.addElement(blue2);
                    mr.addElement(width2);
                    this.records.add(mr);
                    break;
                }
                case 302: {
                    mr.numPoints = recSize;
                    mr.functionId = functionId;
                    final int align = this.readShort(is);
                    if (recSize > 1) {
                        for (int i3 = 1; i3 < recSize; ++i3) {
                            this.readShort(is);
                        }
                    }
                    mr.addElement(align);
                    this.records.add(mr);
                    break;
                }
                case 513:
                case 521: {
                    mr.numPoints = recSize;
                    mr.functionId = functionId;
                    final int colorref = this.readInt(is);
                    final int red = colorref & 0xFF;
                    final int green = (colorref & 0xFF00) >> 8;
                    final int blue = (colorref & 0xFF0000) >> 16;
                    final int flags = (colorref & 0x3000000) >> 24;
                    mr.addElement(red);
                    mr.addElement(green);
                    mr.addElement(blue);
                    this.records.add(mr);
                    break;
                }
                case 531:
                case 532: {
                    mr.numPoints = recSize;
                    mr.functionId = functionId;
                    final int y3 = this.readShort(is) * this.ySign;
                    final int x3 = (int)(this.readShort(is) * this.xSign * this.scaleXY);
                    mr.addElement(x3);
                    mr.addElement(y3);
                    this.records.add(mr);
                    break;
                }
                case 262: {
                    mr.numPoints = recSize;
                    mr.functionId = functionId;
                    final int mode = this.readShort(is);
                    if (recSize > 1) {
                        for (int i3 = 1; i3 < recSize; ++i3) {
                            this.readShort(is);
                        }
                    }
                    mr.addElement(mode);
                    this.records.add(mr);
                    break;
                }
                case 1336: {
                    mr.numPoints = recSize;
                    mr.functionId = functionId;
                    final int count = this.readShort(is);
                    final int[] pts = new int[count];
                    int ptCount = 0;
                    for (int l = 0; l < count; ++l) {
                        pts[l] = this.readShort(is);
                        ptCount += pts[l];
                    }
                    mr.addElement(count);
                    for (int l = 0; l < count; ++l) {
                        mr.addElement(pts[l]);
                    }
                    final int offset = count + 1;
                    for (final int nPoints : pts) {
                        for (int j2 = 0; j2 < nPoints; ++j2) {
                            mr.addElement((int)(this.readShort(is) * this.xSign * this.scaleXY));
                            mr.addElement(this.readShort(is) * this.ySign);
                        }
                    }
                    this.records.add(mr);
                    break;
                }
                case 804:
                case 805: {
                    mr.numPoints = recSize;
                    mr.functionId = functionId;
                    final int count = this.readShort(is);
                    mr.addElement(count);
                    for (int i3 = 0; i3 < count; ++i3) {
                        mr.addElement((int)(this.readShort(is) * this.xSign * this.scaleXY));
                        mr.addElement(this.readShort(is) * this.ySign);
                    }
                    this.records.add(mr);
                    break;
                }
                case 1046:
                case 1048:
                case 1051: {
                    mr.numPoints = recSize;
                    mr.functionId = functionId;
                    final int bottom = this.readShort(is) * this.ySign;
                    final int right = (int)(this.readShort(is) * this.xSign * this.scaleXY);
                    final int top = this.readShort(is) * this.ySign;
                    final int left = (int)(this.readShort(is) * this.xSign * this.scaleXY);
                    mr.addElement(left);
                    mr.addElement(top);
                    mr.addElement(right);
                    mr.addElement(bottom);
                    this.records.add(mr);
                    break;
                }
                case 1791: {
                    mr.numPoints = recSize;
                    mr.functionId = functionId;
                    final int left2 = (int)(this.readShort(is) * this.xSign * this.scaleXY);
                    final int top2 = this.readShort(is) * this.ySign;
                    final int right2 = (int)(this.readShort(is) * this.xSign * this.scaleXY);
                    final int bottom2 = this.readShort(is) * this.ySign;
                    mr.addElement(left2);
                    mr.addElement(top2);
                    mr.addElement(right2);
                    mr.addElement(bottom2);
                    this.records.add(mr);
                    break;
                }
                case 1564: {
                    mr.numPoints = recSize;
                    mr.functionId = functionId;
                    final int el_height = this.readShort(is) * this.ySign;
                    final int el_width = (int)(this.readShort(is) * this.xSign * this.scaleXY);
                    final int bottom3 = this.readShort(is) * this.ySign;
                    final int right3 = (int)(this.readShort(is) * this.xSign * this.scaleXY);
                    final int top3 = this.readShort(is) * this.ySign;
                    final int left3 = (int)(this.readShort(is) * this.xSign * this.scaleXY);
                    mr.addElement(left3);
                    mr.addElement(top3);
                    mr.addElement(right3);
                    mr.addElement(bottom3);
                    mr.addElement(el_width);
                    mr.addElement(el_height);
                    this.records.add(mr);
                    break;
                }
                case 2071:
                case 2074: {
                    mr.numPoints = recSize;
                    mr.functionId = functionId;
                    final int yend = this.readShort(is) * this.ySign;
                    final int xend = (int)(this.readShort(is) * this.xSign * this.scaleXY);
                    final int ystart = this.readShort(is) * this.ySign;
                    final int xstart = (int)(this.readShort(is) * this.xSign * this.scaleXY);
                    final int bottom4 = this.readShort(is) * this.ySign;
                    final int right4 = (int)(this.readShort(is) * this.xSign * this.scaleXY);
                    final int top4 = this.readShort(is) * this.ySign;
                    final int left4 = (int)(this.readShort(is) * this.xSign * this.scaleXY);
                    mr.addElement(left4);
                    mr.addElement(top4);
                    mr.addElement(right4);
                    mr.addElement(bottom4);
                    mr.addElement(xstart);
                    mr.addElement(ystart);
                    mr.addElement(xend);
                    mr.addElement(yend);
                    this.records.add(mr);
                    break;
                }
                case 1565: {
                    mr.numPoints = recSize;
                    mr.functionId = functionId;
                    final int rop = this.readInt(is);
                    final int height2 = this.readShort(is) * this.ySign;
                    final int width3 = (int)(this.readShort(is) * this.xSign * this.scaleXY);
                    final int left = (int)(this.readShort(is) * this.xSign * this.scaleXY);
                    final int top3 = this.readShort(is) * this.ySign;
                    mr.addElement(rop);
                    mr.addElement(height2);
                    mr.addElement(width3);
                    mr.addElement(top3);
                    mr.addElement(left);
                    this.records.add(mr);
                    break;
                }
                case 258: {
                    mr.numPoints = recSize;
                    mr.functionId = functionId;
                    final int mode = this.readShort(is);
                    mr.addElement(mode);
                    if (recSize > 1) {
                        for (int i3 = 1; i3 < recSize; ++i3) {
                            this.readShort(is);
                        }
                    }
                    this.records.add(mr);
                    break;
                }
                case 260: {
                    mr.numPoints = recSize;
                    mr.functionId = functionId;
                    int rop;
                    if (recSize == 1) {
                        rop = this.readShort(is);
                    }
                    else {
                        rop = this.readInt(is);
                    }
                    mr.addElement(rop);
                    this.records.add(mr);
                    break;
                }
                case 2881: {
                    final int mode = is.readInt() & 0xFF;
                    final int heightSrc = this.readShort(is) * this.ySign;
                    final int widthSrc = this.readShort(is) * this.xSign;
                    final int sy = this.readShort(is) * this.ySign;
                    final int sx = this.readShort(is) * this.xSign;
                    final int heightDst = this.readShort(is) * this.ySign;
                    final int widthDst = (int)(this.readShort(is) * this.xSign * this.scaleXY);
                    final int dy = this.readShort(is) * this.ySign;
                    final int dx = (int)(this.readShort(is) * this.xSign * this.scaleXY);
                    final int len3 = 2 * recSize - 20;
                    final byte[] bitmap = new byte[len3];
                    for (int i5 = 0; i5 < len3; ++i5) {
                        bitmap[i5] = is.readByte();
                    }
                    mr = new MetaRecord.ByteRecord(bitmap);
                    mr.numPoints = recSize;
                    mr.functionId = functionId;
                    mr.addElement(mode);
                    mr.addElement(heightSrc);
                    mr.addElement(widthSrc);
                    mr.addElement(sy);
                    mr.addElement(sx);
                    mr.addElement(heightDst);
                    mr.addElement(widthDst);
                    mr.addElement(dy);
                    mr.addElement(dx);
                    this.records.add(mr);
                    break;
                }
                case 3907: {
                    final int mode = is.readInt() & 0xFF;
                    final int usage = this.readShort(is);
                    final int heightSrc2 = this.readShort(is) * this.ySign;
                    final int widthSrc2 = this.readShort(is) * this.xSign;
                    final int sy2 = this.readShort(is) * this.ySign;
                    final int sx2 = this.readShort(is) * this.xSign;
                    final int heightDst2 = this.readShort(is) * this.ySign;
                    final int widthDst2 = (int)(this.readShort(is) * this.xSign * this.scaleXY);
                    final int dy2 = this.readShort(is) * this.ySign;
                    final int dx2 = (int)(this.readShort(is) * this.xSign * this.scaleXY);
                    final int len4 = 2 * recSize - 22;
                    final byte[] bitmap2 = new byte[len4];
                    for (int j = 0; j < len4; ++j) {
                        bitmap2[j] = is.readByte();
                    }
                    mr = new MetaRecord.ByteRecord(bitmap2);
                    mr.numPoints = recSize;
                    mr.functionId = functionId;
                    mr.addElement(mode);
                    mr.addElement(heightSrc2);
                    mr.addElement(widthSrc2);
                    mr.addElement(sy2);
                    mr.addElement(sx2);
                    mr.addElement(heightDst2);
                    mr.addElement(widthDst2);
                    mr.addElement(dy2);
                    mr.addElement(dx2);
                    this.records.add(mr);
                    break;
                }
                case 2368: {
                    final int mode = is.readInt() & 0xFF;
                    final int sy3 = this.readShort(is);
                    final int sx3 = this.readShort(is);
                    final int hdc = this.readShort(is);
                    final int height3 = this.readShort(is);
                    final int width4 = (int)(this.readShort(is) * this.xSign * this.scaleXY);
                    final int dy3 = this.readShort(is);
                    final int dx3 = (int)(this.readShort(is) * this.xSign * this.scaleXY);
                    final int len5 = 2 * recSize - 18;
                    if (len5 > 0) {
                        final byte[] bitmap3 = new byte[len5];
                        for (int i6 = 0; i6 < len5; ++i6) {
                            bitmap3[i6] = is.readByte();
                        }
                        mr = new MetaRecord.ByteRecord(bitmap3);
                        mr.numPoints = recSize;
                        mr.functionId = functionId;
                    }
                    else {
                        mr.numPoints = recSize;
                        mr.functionId = functionId;
                        for (int i7 = 0; i7 < len5; ++i7) {
                            is.readByte();
                        }
                    }
                    mr.addElement(mode);
                    mr.addElement(height3);
                    mr.addElement(width4);
                    mr.addElement(sy3);
                    mr.addElement(sx3);
                    mr.addElement(dy3);
                    mr.addElement(dx3);
                    this.records.add(mr);
                    break;
                }
                case 322: {
                    final int type = is.readInt() & 0xFF;
                    final int len6 = 2 * recSize - 4;
                    final byte[] bitmap4 = new byte[len6];
                    for (int l = 0; l < len6; ++l) {
                        bitmap4[l] = is.readByte();
                    }
                    mr = new MetaRecord.ByteRecord(bitmap4);
                    mr.numPoints = recSize;
                    mr.functionId = functionId;
                    mr.addElement(type);
                    this.records.add(mr);
                    break;
                }
                default: {
                    mr.numPoints = recSize;
                    mr.functionId = functionId;
                    for (int j3 = 0; j3 < recSize; ++j3) {
                        mr.addElement(this.readShort(is));
                    }
                    this.records.add(mr);
                    break;
                }
            }
            ++this.numRecords;
        }
        if (!this.isAldus) {
            this.right = (int)this.vpX;
            this.left = (int)(this.vpX + this.vpW);
            this.top = (int)this.vpY;
            this.bottom = (int)(this.vpY + this.vpH);
        }
        this.setReading(false);
        return true;
    }
    
    public URL getUrl() {
        return this.url;
    }
    
    public void setUrl(final URL newUrl) {
        this.url = newUrl;
    }
    
    public MetaRecord getRecord(final int idx) {
        return this.records.get(idx);
    }
    
    public int getNumRecords() {
        return this.numRecords;
    }
    
    public float getVpX() {
        return this.vpX;
    }
    
    public float getVpY() {
        return this.vpY;
    }
    
    public void setVpX(final float newValue) {
        this.vpX = newValue;
    }
    
    public void setVpY(final float newValue) {
        this.vpY = newValue;
    }
}
