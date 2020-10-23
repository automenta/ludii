// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image;

import java.io.ObjectStreamException;
import java.io.Serializable;

public final class CompositeRule implements Serializable
{
    public static final int RULE_OVER = 1;
    public static final int RULE_IN = 2;
    public static final int RULE_OUT = 3;
    public static final int RULE_ATOP = 4;
    public static final int RULE_XOR = 5;
    public static final int RULE_ARITHMETIC = 6;
    public static final int RULE_MULTIPLY = 7;
    public static final int RULE_SCREEN = 8;
    public static final int RULE_DARKEN = 9;
    public static final int RULE_LIGHTEN = 10;
    public static final CompositeRule OVER;
    public static final CompositeRule IN;
    public static final CompositeRule OUT;
    public static final CompositeRule ATOP;
    public static final CompositeRule XOR;
    public static final CompositeRule MULTIPLY;
    public static final CompositeRule SCREEN;
    public static final CompositeRule DARKEN;
    public static final CompositeRule LIGHTEN;
    private int rule;
    private float k1;
    private float k2;
    private float k3;
    private float k4;
    
    public static CompositeRule ARITHMETIC(final float k1, final float k2, final float k3, final float k4) {
        return new CompositeRule(k1, k2, k3, k4);
    }
    
    public int getRule() {
        return this.rule;
    }
    
    private CompositeRule(final int rule) {
        this.rule = rule;
    }
    
    private CompositeRule(final float k1, final float k2, final float k3, final float k4) {
        this.rule = 6;
        this.k1 = k1;
        this.k2 = k2;
        this.k3 = k3;
        this.k4 = k4;
    }
    
    public float[] getCoefficients() {
        if (this.rule != 6) {
            return null;
        }
        return new float[] { this.k1, this.k2, this.k3, this.k4 };
    }
    
    private Object readResolve() throws ObjectStreamException {
        switch (this.rule) {
            case 1: {
                return CompositeRule.OVER;
            }
            case 2: {
                return CompositeRule.IN;
            }
            case 3: {
                return CompositeRule.OUT;
            }
            case 4: {
                return CompositeRule.ATOP;
            }
            case 5: {
                return CompositeRule.XOR;
            }
            case 6: {
                return this;
            }
            case 7: {
                return CompositeRule.MULTIPLY;
            }
            case 8: {
                return CompositeRule.SCREEN;
            }
            case 9: {
                return CompositeRule.DARKEN;
            }
            case 10: {
                return CompositeRule.LIGHTEN;
            }
            default: {
                throw new RuntimeException("Unknown Composite Rule type");
            }
        }
    }
    
    @Override
    public String toString() {
        switch (this.rule) {
            case 1: {
                return "[CompositeRule: OVER]";
            }
            case 2: {
                return "[CompositeRule: IN]";
            }
            case 3: {
                return "[CompositeRule: OUT]";
            }
            case 4: {
                return "[CompositeRule: ATOP]";
            }
            case 5: {
                return "[CompositeRule: XOR]";
            }
            case 6: {
                return "[CompositeRule: ARITHMATIC k1:" + this.k1 + " k2: " + this.k2 + " k3: " + this.k3 + " k4: " + this.k4 + ']';
            }
            case 7: {
                return "[CompositeRule: MULTIPLY]";
            }
            case 8: {
                return "[CompositeRule: SCREEN]";
            }
            case 9: {
                return "[CompositeRule: DARKEN]";
            }
            case 10: {
                return "[CompositeRule: LIGHTEN]";
            }
            default: {
                throw new RuntimeException("Unknown Composite Rule type");
            }
        }
    }
    
    static {
        OVER = new CompositeRule(1);
        IN = new CompositeRule(2);
        OUT = new CompositeRule(3);
        ATOP = new CompositeRule(4);
        XOR = new CompositeRule(5);
        MULTIPLY = new CompositeRule(7);
        SCREEN = new CompositeRule(8);
        DARKEN = new CompositeRule(9);
        LIGHTEN = new CompositeRule(10);
    }
}
