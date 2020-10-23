// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.svg;

import org.w3c.dom.svg.SVGPathSegClosePath;
import org.w3c.dom.svg.SVGPathSeg;

public class SVGPathSegItem extends AbstractSVGItem implements SVGPathSeg, SVGPathSegClosePath
{
    protected short type;
    protected String letter;
    private float x;
    private float y;
    private float x1;
    private float y1;
    private float x2;
    private float y2;
    private float r1;
    private float r2;
    private float angle;
    private boolean largeArcFlag;
    private boolean sweepFlag;
    
    protected SVGPathSegItem() {
    }
    
    public SVGPathSegItem(final short type, final String letter) {
        this.type = type;
        this.letter = letter;
    }
    
    public SVGPathSegItem(final SVGPathSeg pathSeg) {
        switch (this.type = pathSeg.getPathSegType()) {
            case 1: {
                this.letter = "z";
                break;
            }
        }
    }
    
    @Override
    protected String getStringValue() {
        return this.letter;
    }
    
    @Override
    public short getPathSegType() {
        return this.type;
    }
    
    @Override
    public String getPathSegTypeAsLetter() {
        return this.letter;
    }
    
    public float getR1() {
        return this.r1;
    }
    
    public void setR1(final float r1) {
        this.r1 = r1;
    }
    
    public float getR2() {
        return this.r2;
    }
    
    public void setR2(final float r2) {
        this.r2 = r2;
    }
    
    public float getAngle() {
        return this.angle;
    }
    
    public void setAngle(final float angle) {
        this.angle = angle;
    }
    
    public boolean isLargeArcFlag() {
        return this.largeArcFlag;
    }
    
    public void setLargeArcFlag(final boolean largeArcFlag) {
        this.largeArcFlag = largeArcFlag;
    }
    
    public boolean isSweepFlag() {
        return this.sweepFlag;
    }
    
    public void setSweepFlag(final boolean sweepFlag) {
        this.sweepFlag = sweepFlag;
    }
    
    public float getX() {
        return this.x;
    }
    
    public void setX(final float x) {
        this.x = x;
    }
    
    public float getY() {
        return this.y;
    }
    
    public void setY(final float y) {
        this.y = y;
    }
    
    public float getX1() {
        return this.x1;
    }
    
    public void setX1(final float x1) {
        this.x1 = x1;
    }
    
    public float getY1() {
        return this.y1;
    }
    
    public void setY1(final float y1) {
        this.y1 = y1;
    }
    
    public float getX2() {
        return this.x2;
    }
    
    public void setX2(final float x2) {
        this.x2 = x2;
    }
    
    public float getY2() {
        return this.y2;
    }
    
    public void setY2(final float y2) {
        this.y2 = y2;
    }
}
