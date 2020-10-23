// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen.font.table;

public class Panose
{
    byte bFamilyType;
    byte bSerifStyle;
    byte bWeight;
    byte bProportion;
    byte bContrast;
    byte bStrokeVariation;
    byte bArmStyle;
    byte bLetterform;
    byte bMidline;
    byte bXHeight;
    
    public Panose(final byte[] panose) {
        this.bFamilyType = 0;
        this.bSerifStyle = 0;
        this.bWeight = 0;
        this.bProportion = 0;
        this.bContrast = 0;
        this.bStrokeVariation = 0;
        this.bArmStyle = 0;
        this.bLetterform = 0;
        this.bMidline = 0;
        this.bXHeight = 0;
        this.bFamilyType = panose[0];
        this.bSerifStyle = panose[1];
        this.bWeight = panose[2];
        this.bProportion = panose[3];
        this.bContrast = panose[4];
        this.bStrokeVariation = panose[5];
        this.bArmStyle = panose[6];
        this.bLetterform = panose[7];
        this.bMidline = panose[8];
        this.bXHeight = panose[9];
    }
    
    public byte getFamilyType() {
        return this.bFamilyType;
    }
    
    public byte getSerifStyle() {
        return this.bSerifStyle;
    }
    
    public byte getWeight() {
        return this.bWeight;
    }
    
    public byte getProportion() {
        return this.bProportion;
    }
    
    public byte getContrast() {
        return this.bContrast;
    }
    
    public byte getStrokeVariation() {
        return this.bStrokeVariation;
    }
    
    public byte getArmStyle() {
        return this.bArmStyle;
    }
    
    public byte getLetterForm() {
        return this.bLetterform;
    }
    
    public byte getMidline() {
        return this.bMidline;
    }
    
    public byte getXHeight() {
        return this.bXHeight;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append(String.valueOf(this.bFamilyType)).append(" ").append(String.valueOf(this.bSerifStyle)).append(" ").append(String.valueOf(this.bWeight)).append(" ").append(String.valueOf(this.bProportion)).append(" ").append(String.valueOf(this.bContrast)).append(" ").append(String.valueOf(this.bStrokeVariation)).append(" ").append(String.valueOf(this.bArmStyle)).append(" ").append(String.valueOf(this.bLetterform)).append(" ").append(String.valueOf(this.bMidline)).append(" ").append(String.valueOf(this.bXHeight));
        return sb.toString();
    }
}
