// 
// Decompiled by Procyon v0.5.36
// 

package util.symmetry;

public class ReflectionsOnly implements SymmetryValidator
{
    @Override
    public boolean isValid(final SymmetryType type, final int symmetryIndex, final int symmetryCount) {
        switch (type) {
            case REFLECTIONS -> {
                return true;
            }
            case ROTATIONS -> {
                return symmetryIndex == 0;
            }
            case SUBSTITUTIONS -> {
                return symmetryIndex == 0;
            }
            default -> {
                return true;
            }
        }
    }
}
