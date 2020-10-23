// 
// Decompiled by Procyon v0.5.36
// 

package manager.utils;

public enum PuzzleSelectionType
{
    Automatic, 
    Dialog, 
    Cycle;
    
    public static PuzzleSelectionType getPuzzleSelectionType(final String name) {
        for (final PuzzleSelectionType e : values()) {
            if (e.name().equals(name)) {
                return e;
            }
        }
        return PuzzleSelectionType.Automatic;
    }
}
