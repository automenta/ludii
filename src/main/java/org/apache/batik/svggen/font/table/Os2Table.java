// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;

public class Os2Table implements Table
{
    private int version;
    private short xAvgCharWidth;
    private int usWeightClass;
    private int usWidthClass;
    private short fsType;
    private short ySubscriptXSize;
    private short ySubscriptYSize;
    private short ySubscriptXOffset;
    private short ySubscriptYOffset;
    private short ySuperscriptXSize;
    private short ySuperscriptYSize;
    private short ySuperscriptXOffset;
    private short ySuperscriptYOffset;
    private short yStrikeoutSize;
    private short yStrikeoutPosition;
    private short sFamilyClass;
    private Panose panose;
    private int ulUnicodeRange1;
    private int ulUnicodeRange2;
    private int ulUnicodeRange3;
    private int ulUnicodeRange4;
    private int achVendorID;
    private short fsSelection;
    private int usFirstCharIndex;
    private int usLastCharIndex;
    private short sTypoAscender;
    private short sTypoDescender;
    private short sTypoLineGap;
    private int usWinAscent;
    private int usWinDescent;
    private int ulCodePageRange1;
    private int ulCodePageRange2;
    
    protected Os2Table(final DirectoryEntry de, final RandomAccessFile raf) throws IOException {
        raf.seek(de.getOffset());
        this.version = raf.readUnsignedShort();
        this.xAvgCharWidth = raf.readShort();
        this.usWeightClass = raf.readUnsignedShort();
        this.usWidthClass = raf.readUnsignedShort();
        this.fsType = raf.readShort();
        this.ySubscriptXSize = raf.readShort();
        this.ySubscriptYSize = raf.readShort();
        this.ySubscriptXOffset = raf.readShort();
        this.ySubscriptYOffset = raf.readShort();
        this.ySuperscriptXSize = raf.readShort();
        this.ySuperscriptYSize = raf.readShort();
        this.ySuperscriptXOffset = raf.readShort();
        this.ySuperscriptYOffset = raf.readShort();
        this.yStrikeoutSize = raf.readShort();
        this.yStrikeoutPosition = raf.readShort();
        this.sFamilyClass = raf.readShort();
        final byte[] buf = new byte[10];
        raf.read(buf);
        this.panose = new Panose(buf);
        this.ulUnicodeRange1 = raf.readInt();
        this.ulUnicodeRange2 = raf.readInt();
        this.ulUnicodeRange3 = raf.readInt();
        this.ulUnicodeRange4 = raf.readInt();
        this.achVendorID = raf.readInt();
        this.fsSelection = raf.readShort();
        this.usFirstCharIndex = raf.readUnsignedShort();
        this.usLastCharIndex = raf.readUnsignedShort();
        this.sTypoAscender = raf.readShort();
        this.sTypoDescender = raf.readShort();
        this.sTypoLineGap = raf.readShort();
        this.usWinAscent = raf.readUnsignedShort();
        this.usWinDescent = raf.readUnsignedShort();
        this.ulCodePageRange1 = raf.readInt();
        this.ulCodePageRange2 = raf.readInt();
    }
    
    public int getVersion() {
        return this.version;
    }
    
    public short getAvgCharWidth() {
        return this.xAvgCharWidth;
    }
    
    public int getWeightClass() {
        return this.usWeightClass;
    }
    
    public int getWidthClass() {
        return this.usWidthClass;
    }
    
    public short getLicenseType() {
        return this.fsType;
    }
    
    public short getSubscriptXSize() {
        return this.ySubscriptXSize;
    }
    
    public short getSubscriptYSize() {
        return this.ySubscriptYSize;
    }
    
    public short getSubscriptXOffset() {
        return this.ySubscriptXOffset;
    }
    
    public short getSubscriptYOffset() {
        return this.ySubscriptYOffset;
    }
    
    public short getSuperscriptXSize() {
        return this.ySuperscriptXSize;
    }
    
    public short getSuperscriptYSize() {
        return this.ySuperscriptYSize;
    }
    
    public short getSuperscriptXOffset() {
        return this.ySuperscriptXOffset;
    }
    
    public short getSuperscriptYOffset() {
        return this.ySuperscriptYOffset;
    }
    
    public short getStrikeoutSize() {
        return this.yStrikeoutSize;
    }
    
    public short getStrikeoutPosition() {
        return this.yStrikeoutPosition;
    }
    
    public short getFamilyClass() {
        return this.sFamilyClass;
    }
    
    public Panose getPanose() {
        return this.panose;
    }
    
    public int getUnicodeRange1() {
        return this.ulUnicodeRange1;
    }
    
    public int getUnicodeRange2() {
        return this.ulUnicodeRange2;
    }
    
    public int getUnicodeRange3() {
        return this.ulUnicodeRange3;
    }
    
    public int getUnicodeRange4() {
        return this.ulUnicodeRange4;
    }
    
    public int getVendorID() {
        return this.achVendorID;
    }
    
    public short getSelection() {
        return this.fsSelection;
    }
    
    public int getFirstCharIndex() {
        return this.usFirstCharIndex;
    }
    
    public int getLastCharIndex() {
        return this.usLastCharIndex;
    }
    
    public short getTypoAscender() {
        return this.sTypoAscender;
    }
    
    public short getTypoDescender() {
        return this.sTypoDescender;
    }
    
    public short getTypoLineGap() {
        return this.sTypoLineGap;
    }
    
    public int getWinAscent() {
        return this.usWinAscent;
    }
    
    public int getWinDescent() {
        return this.usWinDescent;
    }
    
    public int getCodePageRange1() {
        return this.ulCodePageRange1;
    }
    
    public int getCodePageRange2() {
        return this.ulCodePageRange2;
    }
    
    @Override
    public int getType() {
        return 1330851634;
    }
}
