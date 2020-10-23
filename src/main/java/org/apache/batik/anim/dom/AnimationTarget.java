// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.apache.batik.anim.values.AnimatableValue;
import org.w3c.dom.Element;

public interface AnimationTarget
{
    public static final short PERCENTAGE_FONT_SIZE = 0;
    public static final short PERCENTAGE_VIEWPORT_WIDTH = 1;
    public static final short PERCENTAGE_VIEWPORT_HEIGHT = 2;
    public static final short PERCENTAGE_VIEWPORT_SIZE = 3;
    
    Element getElement();
    
    void updatePropertyValue(final String p0, final AnimatableValue p1);
    
    void updateAttributeValue(final String p0, final String p1, final AnimatableValue p2);
    
    void updateOtherValue(final String p0, final AnimatableValue p1);
    
    AnimatableValue getUnderlyingValue(final String p0, final String p1);
    
    short getPercentageInterpretation(final String p0, final String p1, final boolean p2);
    
    boolean useLinearRGBColorInterpolation();
    
    float svgToUserSpace(final float p0, final short p1, final short p2);
    
    void addTargetListener(final String p0, final String p1, final boolean p2, final AnimationTargetListener p3);
    
    void removeTargetListener(final String p0, final String p1, final boolean p2, final AnimationTargetListener p3);
}
