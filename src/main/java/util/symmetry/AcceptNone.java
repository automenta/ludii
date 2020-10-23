// 
// Decompiled by Procyon v0.5.36
// 

package util.symmetry;

public class AcceptNone implements SymmetryValidator
{
    @Override
    public boolean isValid(final SymmetryType type, final int symmetryIndex, final int symmetryCount) {
        switch (type) {
            case ROTATIONS, SUBSTITUTIONS -> {
                return symmetryIndex == 0;
            }
            default -> {
                return false;
            }
        }
    }
}
