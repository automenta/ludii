// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

public class TraitInformation
{
    public static final short PERCENTAGE_FONT_SIZE = 0;
    public static final short PERCENTAGE_VIEWPORT_WIDTH = 1;
    public static final short PERCENTAGE_VIEWPORT_HEIGHT = 2;
    public static final short PERCENTAGE_VIEWPORT_SIZE = 3;
    protected boolean isAnimatable;
    protected int type;
    protected short percentageInterpretation;
    
    public TraitInformation(final boolean isAnimatable, final int type, final short percentageInterpretation) {
        this.isAnimatable = isAnimatable;
        this.type = type;
        this.percentageInterpretation = percentageInterpretation;
    }
    
    public TraitInformation(final boolean isAnimatable, final int type) {
        this.isAnimatable = isAnimatable;
        this.type = type;
        this.percentageInterpretation = -1;
    }
    
    public boolean isAnimatable() {
        return this.isAnimatable;
    }
    
    public int getType() {
        return this.type;
    }
    
    public short getPercentageInterpretation() {
        return this.percentageInterpretation;
    }
}
