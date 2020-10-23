// 
// Decompiled by Procyon v0.5.36
// 

package game.util.directions;

import game.functions.directions.Directions;
import game.functions.directions.DirectionsFunction;
import game.types.board.RelationType;

public enum AbsoluteDirection implements Direction
{
    All {
        @Override
        public boolean matches(final DirectionFacing baseDirn, final DirectionType dirn) {
            return true;
        }
    }, 
    Angled {
        @Override
        public boolean matches(final DirectionFacing baseDirn, final DirectionType dirn) {
            return dirn.getDirectionActual().uniqueName() == DirectionUniqueName.NW || dirn.getDirectionActual().uniqueName() == DirectionUniqueName.NNW || dirn.getDirectionActual().uniqueName() == DirectionUniqueName.WNW || dirn.getDirectionActual().uniqueName() == DirectionUniqueName.WSW || dirn.getDirectionActual().uniqueName() == DirectionUniqueName.SE || dirn.getDirectionActual().uniqueName() == DirectionUniqueName.SW || dirn.getDirectionActual().uniqueName() == DirectionUniqueName.SSE || dirn.getDirectionActual().uniqueName() == DirectionUniqueName.SSW || dirn.getDirectionActual().uniqueName() == DirectionUniqueName.NNE || dirn.getDirectionActual().uniqueName() == DirectionUniqueName.ESE || dirn.getDirectionActual().uniqueName() == DirectionUniqueName.ENE || dirn.getDirectionActual().uniqueName() == DirectionUniqueName.NE;
        }
    }, 
    Adjacent {
        @Override
        public boolean matches(final DirectionFacing baseDirn, final DirectionType dirn) {
            return false;
        }
    }, 
    Axial {
        @Override
        public boolean matches(final DirectionFacing baseDirn, final DirectionType dirn) {
            return dirn.getDirectionActual().uniqueName() == DirectionUniqueName.N || dirn.getDirectionActual().uniqueName() == DirectionUniqueName.S || dirn.getDirectionActual().uniqueName() == DirectionUniqueName.E || dirn.getDirectionActual().uniqueName() == DirectionUniqueName.W;
        }
    }, 
    Orthogonal {
        @Override
        public boolean matches(final DirectionFacing baseDirn, final DirectionType dirn) {
            return false;
        }
    }, 
    Diagonal {
        @Override
        public boolean matches(final DirectionFacing baseDirn, final DirectionType dirn) {
            return false;
        }
    }, 
    OffDiagonal {
        @Override
        public boolean matches(final DirectionFacing baseDirn, final DirectionType dirn) {
            return false;
        }
    }, 
    SameLayer {
        @Override
        public boolean matches(final DirectionFacing baseDirn, final DirectionType dirn) {
            return false;
        }
    }, 
    Upward {
        @Override
        public boolean matches(final DirectionFacing baseDirn, final DirectionType dirn) {
            return dirn.getDirectionActual().uniqueName() == DirectionUniqueName.U || dirn.getDirectionActual().uniqueName() == DirectionUniqueName.UN || dirn.getDirectionActual().uniqueName() == DirectionUniqueName.UE || dirn.getDirectionActual().uniqueName() == DirectionUniqueName.US || dirn.getDirectionActual().uniqueName() == DirectionUniqueName.UW || dirn.getDirectionActual().uniqueName() == DirectionUniqueName.UNW || dirn.getDirectionActual().uniqueName() == DirectionUniqueName.UNE || dirn.getDirectionActual().uniqueName() == DirectionUniqueName.USE || dirn.getDirectionActual().uniqueName() == DirectionUniqueName.USW;
        }
    }, 
    Downward {
        @Override
        public boolean matches(final DirectionFacing baseDirn, final DirectionType dirn) {
            return dirn.getDirectionActual().uniqueName() == DirectionUniqueName.D || dirn.getDirectionActual().uniqueName() == DirectionUniqueName.DN || dirn.getDirectionActual().uniqueName() == DirectionUniqueName.DE || dirn.getDirectionActual().uniqueName() == DirectionUniqueName.DS || dirn.getDirectionActual().uniqueName() == DirectionUniqueName.DW || dirn.getDirectionActual().uniqueName() == DirectionUniqueName.DNW || dirn.getDirectionActual().uniqueName() == DirectionUniqueName.DNE || dirn.getDirectionActual().uniqueName() == DirectionUniqueName.DSE || dirn.getDirectionActual().uniqueName() == DirectionUniqueName.DSW;
        }
    }, 
    Rotational {
        @Override
        public boolean matches(final DirectionFacing baseDirn, final DirectionType dirn) {
            return dirn.getDirectionActual().uniqueName() == DirectionUniqueName.CW || dirn.getDirectionActual().uniqueName() == DirectionUniqueName.CCW || dirn.getDirectionActual().uniqueName() == DirectionUniqueName.In || dirn.getDirectionActual().uniqueName() == DirectionUniqueName.Out;
        }
    }, 
    N {
        @Override
        public boolean matches(final DirectionFacing baseDirn, final DirectionType dirn) {
            return dirn.getDirectionActual().uniqueName() == DirectionUniqueName.N;
        }
    }, 
    E {
        @Override
        public boolean matches(final DirectionFacing baseDirn, final DirectionType dirn) {
            return dirn.getDirectionActual().uniqueName() == DirectionUniqueName.E;
        }
    }, 
    S {
        @Override
        public boolean matches(final DirectionFacing baseDirn, final DirectionType dirn) {
            return dirn.getDirectionActual().uniqueName() == DirectionUniqueName.S;
        }
    }, 
    W {
        @Override
        public boolean matches(final DirectionFacing baseDirn, final DirectionType dirn) {
            return dirn.getDirectionActual().uniqueName() == DirectionUniqueName.W;
        }
    }, 
    NE {
        @Override
        public boolean matches(final DirectionFacing baseDirn, final DirectionType dirn) {
            return dirn.getDirectionActual().uniqueName() == DirectionUniqueName.NE;
        }
    }, 
    SE {
        @Override
        public boolean matches(final DirectionFacing baseDirn, final DirectionType dirn) {
            return dirn.getDirectionActual().uniqueName() == DirectionUniqueName.SE;
        }
    }, 
    NW {
        @Override
        public boolean matches(final DirectionFacing baseDirn, final DirectionType dirn) {
            return dirn.getDirectionActual().uniqueName() == DirectionUniqueName.NW;
        }
    }, 
    SW {
        @Override
        public boolean matches(final DirectionFacing baseDirn, final DirectionType dirn) {
            return dirn.getDirectionActual().uniqueName() == DirectionUniqueName.SW;
        }
    }, 
    NNW {
        @Override
        public boolean matches(final DirectionFacing baseDirn, final DirectionType dirn) {
            return dirn.getDirectionActual().uniqueName() == DirectionUniqueName.NNW;
        }
    }, 
    WNW {
        @Override
        public boolean matches(final DirectionFacing baseDirn, final DirectionType dirn) {
            return dirn.getDirectionActual().uniqueName() == DirectionUniqueName.WNW;
        }
    }, 
    WSW {
        @Override
        public boolean matches(final DirectionFacing baseDirn, final DirectionType dirn) {
            return dirn.getDirectionActual().uniqueName() == DirectionUniqueName.WSW;
        }
    }, 
    SSW {
        @Override
        public boolean matches(final DirectionFacing baseDirn, final DirectionType dirn) {
            return dirn.getDirectionActual().uniqueName() == DirectionUniqueName.SSW;
        }
    }, 
    SSE {
        @Override
        public boolean matches(final DirectionFacing baseDirn, final DirectionType dirn) {
            return dirn.getDirectionActual().uniqueName() == DirectionUniqueName.SSE;
        }
    }, 
    ESE {
        @Override
        public boolean matches(final DirectionFacing baseDirn, final DirectionType dirn) {
            return dirn.getDirectionActual().uniqueName() == DirectionUniqueName.ESE;
        }
    }, 
    ENE {
        @Override
        public boolean matches(final DirectionFacing baseDirn, final DirectionType dirn) {
            return dirn.getDirectionActual().uniqueName() == DirectionUniqueName.ENE;
        }
    }, 
    NNE {
        @Override
        public boolean matches(final DirectionFacing baseDirn, final DirectionType dirn) {
            return dirn.getDirectionActual().uniqueName() == DirectionUniqueName.NNE;
        }
    }, 
    CW {
        @Override
        public boolean matches(final DirectionFacing baseDirn, final DirectionType dirn) {
            return dirn.getDirectionActual().uniqueName() == DirectionUniqueName.CW;
        }
    }, 
    CCW {
        @Override
        public boolean matches(final DirectionFacing baseDirn, final DirectionType dirn) {
            return dirn.getDirectionActual().uniqueName() == DirectionUniqueName.CCW;
        }
    }, 
    In {
        @Override
        public boolean matches(final DirectionFacing baseDirn, final DirectionType dirn) {
            return dirn.getDirectionActual().uniqueName() == DirectionUniqueName.In;
        }
    }, 
    Out {
        @Override
        public boolean matches(final DirectionFacing baseDirn, final DirectionType dirn) {
            return dirn.getDirectionActual().uniqueName() == DirectionUniqueName.Out;
        }
    }, 
    U {
        @Override
        public boolean matches(final DirectionFacing baseDirn, final DirectionType dirn) {
            return dirn.getDirectionActual().uniqueName() == DirectionUniqueName.U;
        }
    }, 
    UN {
        @Override
        public boolean matches(final DirectionFacing baseDirn, final DirectionType dirn) {
            return dirn.getDirectionActual().uniqueName() == DirectionUniqueName.UN;
        }
    }, 
    UNE {
        @Override
        public boolean matches(final DirectionFacing baseDirn, final DirectionType dirn) {
            return dirn.getDirectionActual().uniqueName() == DirectionUniqueName.UNE;
        }
    }, 
    UE {
        @Override
        public boolean matches(final DirectionFacing baseDirn, final DirectionType dirn) {
            return dirn.getDirectionActual().uniqueName() == DirectionUniqueName.UE;
        }
    }, 
    USE {
        @Override
        public boolean matches(final DirectionFacing baseDirn, final DirectionType dirn) {
            return dirn.getDirectionActual().uniqueName() == DirectionUniqueName.USE;
        }
    }, 
    US {
        @Override
        public boolean matches(final DirectionFacing baseDirn, final DirectionType dirn) {
            return dirn.getDirectionActual().uniqueName() == DirectionUniqueName.US;
        }
    }, 
    USW {
        @Override
        public boolean matches(final DirectionFacing baseDirn, final DirectionType dirn) {
            return dirn.getDirectionActual().uniqueName() == DirectionUniqueName.USW;
        }
    }, 
    UW {
        @Override
        public boolean matches(final DirectionFacing baseDirn, final DirectionType dirn) {
            return dirn.getDirectionActual().uniqueName() == DirectionUniqueName.UW;
        }
    }, 
    UNW {
        @Override
        public boolean matches(final DirectionFacing baseDirn, final DirectionType dirn) {
            return dirn.getDirectionActual().uniqueName() == DirectionUniqueName.UNW;
        }
    }, 
    D {
        @Override
        public boolean matches(final DirectionFacing baseDirn, final DirectionType dirn) {
            return dirn.getDirectionActual().uniqueName() == DirectionUniqueName.D;
        }
    }, 
    DN {
        @Override
        public boolean matches(final DirectionFacing baseDirn, final DirectionType dirn) {
            return dirn.getDirectionActual().uniqueName() == DirectionUniqueName.DN;
        }
    }, 
    DNE {
        @Override
        public boolean matches(final DirectionFacing baseDirn, final DirectionType dirn) {
            return dirn.getDirectionActual().uniqueName() == DirectionUniqueName.DNE;
        }
    }, 
    DE {
        @Override
        public boolean matches(final DirectionFacing baseDirn, final DirectionType dirn) {
            return dirn.getDirectionActual().uniqueName() == DirectionUniqueName.DE;
        }
    }, 
    DSE {
        @Override
        public boolean matches(final DirectionFacing baseDirn, final DirectionType dirn) {
            return dirn.getDirectionActual().uniqueName() == DirectionUniqueName.DSE;
        }
    }, 
    DS {
        @Override
        public boolean matches(final DirectionFacing baseDirn, final DirectionType dirn) {
            return dirn.getDirectionActual().uniqueName() == DirectionUniqueName.DS;
        }
    }, 
    DSW {
        @Override
        public boolean matches(final DirectionFacing baseDirn, final DirectionType dirn) {
            return dirn.getDirectionActual().uniqueName() == DirectionUniqueName.DSW;
        }
    }, 
    DW {
        @Override
        public boolean matches(final DirectionFacing baseDirn, final DirectionType dirn) {
            return dirn.getDirectionActual().uniqueName() == DirectionUniqueName.DW;
        }
    }, 
    DNW {
        @Override
        public boolean matches(final DirectionFacing baseDirn, final DirectionType dirn) {
            return dirn.getDirectionActual().uniqueName() == DirectionUniqueName.DNW;
        }
    };
    
    public abstract boolean matches(final DirectionFacing baseDirn, final DirectionType dirn);
    
    public static boolean specific(final AbsoluteDirection dirn) {
        return convert(dirn) != null;
    }
    
    public boolean specific() {
        return specific(this);
    }
    
    public static DirectionFacing convert(final AbsoluteDirection absoluteDirection) {
        switch (absoluteDirection) {
            case Adjacent: {
                return null;
            }
            case All: {
                return null;
            }
            case Axial: {
                return null;
            }
            case Angled: {
                return null;
            }
            case Diagonal: {
                return null;
            }
            case OffDiagonal: {
                return null;
            }
            case Orthogonal: {
                return null;
            }
            case Downward: {
                return null;
            }
            case SameLayer: {
                return null;
            }
            case Upward: {
                return null;
            }
            case Rotational: {
                return null;
            }
            case E: {
                return CompassDirection.E;
            }
            case ENE: {
                return CompassDirection.ENE;
            }
            case ESE: {
                return CompassDirection.ESE;
            }
            case N: {
                return CompassDirection.N;
            }
            case NE: {
                return CompassDirection.NE;
            }
            case NNE: {
                return CompassDirection.NNE;
            }
            case NNW: {
                return CompassDirection.NNW;
            }
            case NW: {
                return CompassDirection.NW;
            }
            case S: {
                return CompassDirection.S;
            }
            case SE: {
                return CompassDirection.SE;
            }
            case SSE: {
                return CompassDirection.SSE;
            }
            case SSW: {
                return CompassDirection.SSW;
            }
            case SW: {
                return CompassDirection.SW;
            }
            case W: {
                return CompassDirection.W;
            }
            case WNW: {
                return CompassDirection.WNW;
            }
            case WSW: {
                return CompassDirection.WSW;
            }
            case CCW: {
                return RotationalDirection.CCW;
            }
            case CW: {
                return RotationalDirection.CW;
            }
            case In: {
                return RotationalDirection.In;
            }
            case Out: {
                return RotationalDirection.Out;
            }
            case U: {
                return SpatialDirection.U;
            }
            case UN: {
                return SpatialDirection.UN;
            }
            case UNE: {
                return SpatialDirection.UNE;
            }
            case UE: {
                return SpatialDirection.UE;
            }
            case USE: {
                return SpatialDirection.USE;
            }
            case US: {
                return SpatialDirection.US;
            }
            case USW: {
                return SpatialDirection.USW;
            }
            case UW: {
                return SpatialDirection.UW;
            }
            case UNW: {
                return SpatialDirection.UNW;
            }
            case D: {
                return SpatialDirection.D;
            }
            case DN: {
                return SpatialDirection.DN;
            }
            case DNE: {
                return SpatialDirection.DNE;
            }
            case DE: {
                return SpatialDirection.DE;
            }
            case DSE: {
                return SpatialDirection.DSE;
            }
            case DS: {
                return SpatialDirection.DS;
            }
            case DSW: {
                return SpatialDirection.DSW;
            }
            case DW: {
                return SpatialDirection.DW;
            }
            case DNW: {
                return SpatialDirection.DNW;
            }
            default: {
                return null;
            }
        }
    }
    
    @Override
    public DirectionsFunction directionsFunctions() {
        return new Directions(this, null);
    }
    
    public static RelationType converToRelationType(final AbsoluteDirection absoluteDirection) {
        switch (absoluteDirection) {
            case Adjacent: {
                return RelationType.Adjacent;
            }
            case Diagonal: {
                return RelationType.Diagonal;
            }
            case All: {
                return RelationType.All;
            }
            case OffDiagonal: {
                return RelationType.OffDiagonal;
            }
            case Orthogonal: {
                return RelationType.Orthogonal;
            }
            default: {
                return null;
            }
        }
    }
}
