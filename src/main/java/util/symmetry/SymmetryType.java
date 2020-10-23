// 
// Decompiled by Procyon v0.5.36
// 

package util.symmetry;

import java.util.EnumSet;

public enum SymmetryType
{
    ROTATIONS, 
    REFLECTIONS, 
    SUBSTITUTIONS;
    
    public static final EnumSet<SymmetryType> ALL;
    public static final EnumSet<SymmetryType> NONE;
    
    static {
        ALL = EnumSet.allOf(SymmetryType.class);
        NONE = EnumSet.noneOf(SymmetryType.class);
    }
}
