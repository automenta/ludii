// 
// Decompiled by Procyon v0.5.36
// 

package util.symmetry;

public class AcceptAll implements SymmetryValidator
{
    @Override
    public boolean isValid(final SymmetryType type, final int angleIndex, final int maxAngles) {
        return true;
    }
}
