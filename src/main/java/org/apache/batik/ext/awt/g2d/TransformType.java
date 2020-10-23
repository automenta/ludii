// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.g2d;

public final class TransformType
{
    public static final int TRANSFORM_TRANSLATE = 0;
    public static final int TRANSFORM_ROTATE = 1;
    public static final int TRANSFORM_SCALE = 2;
    public static final int TRANSFORM_SHEAR = 3;
    public static final int TRANSFORM_GENERAL = 4;
    public static final String TRANSLATE_STRING = "translate";
    public static final String ROTATE_STRING = "rotate";
    public static final String SCALE_STRING = "scale";
    public static final String SHEAR_STRING = "shear";
    public static final String GENERAL_STRING = "general";
    public static final TransformType TRANSLATE;
    public static final TransformType ROTATE;
    public static final TransformType SCALE;
    public static final TransformType SHEAR;
    public static final TransformType GENERAL;
    private String desc;
    private int val;
    
    private TransformType(final int val, final String desc) {
        this.desc = desc;
        this.val = val;
    }
    
    @Override
    public String toString() {
        return this.desc;
    }
    
    public int toInt() {
        return this.val;
    }
    
    public Object readResolve() {
        switch (this.val) {
            case 0: {
                return TransformType.TRANSLATE;
            }
            case 1: {
                return TransformType.ROTATE;
            }
            case 2: {
                return TransformType.SCALE;
            }
            case 3: {
                return TransformType.SHEAR;
            }
            case 4: {
                return TransformType.GENERAL;
            }
            default: {
                throw new RuntimeException("Unknown TransformType value:" + this.val);
            }
        }
    }
    
    static {
        TRANSLATE = new TransformType(0, "translate");
        ROTATE = new TransformType(1, "rotate");
        SCALE = new TransformType(2, "scale");
        SHEAR = new TransformType(3, "shear");
        GENERAL = new TransformType(4, "general");
    }
}
