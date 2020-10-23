// 
// Decompiled by Procyon v0.5.36
// 

package util.symmetry;

public class AcceptNone implements SymmetryValidator
{
    @Override
    public boolean isValid(final SymmetryType type, final int symmetryIndex, final int symmetryCount) {
        switch (type) {
            case REFLECTIONS: {
                return false;
            }
            case ROTATIONS: {
                return symmetryIndex == 0;
            }
            case SUBSTITUTIONS: {
                return symmetryIndex == 0;
            }
            default: {
                return false;
            }
        }
    }
}
