// 
// Decompiled by Procyon v0.5.36
// 

package game.types.board;

import game.util.directions.AbsoluteDirection;

public enum RelationType
{
    Orthogonal, 
    Diagonal, 
    OffDiagonal, 
    Adjacent, 
    All;
    
    public static AbsoluteDirection convert(final RelationType relation) {
        switch (relation) {
            case Adjacent: {
                return AbsoluteDirection.Adjacent;
            }
            case Diagonal: {
                return AbsoluteDirection.Diagonal;
            }
            case All: {
                return AbsoluteDirection.All;
            }
            case OffDiagonal: {
                return AbsoluteDirection.OffDiagonal;
            }
            case Orthogonal: {
                return AbsoluteDirection.Orthogonal;
            }
            default: {
                throw new IllegalArgumentException("RelationType.convert(): a RelationType is not implemented.");
            }
        }
    }
    
    public boolean supersetOf(final RelationType rA) {
        return this.equals(rA) || this.equals(RelationType.All) || (this.equals(RelationType.Adjacent) && rA.equals(RelationType.Orthogonal));
    }
}
