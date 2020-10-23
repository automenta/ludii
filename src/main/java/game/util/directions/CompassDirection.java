// 
// Decompiled by Procyon v0.5.36
// 

package game.util.directions;

public enum CompassDirection implements DirectionFacing
{
    N(DirectionUniqueName.N) {
        @Override
        public AbsoluteDirection toAbsolute() {
            return AbsoluteDirection.N;
        }
    }, 
    NNE(DirectionUniqueName.NNE) {
        @Override
        public AbsoluteDirection toAbsolute() {
            return AbsoluteDirection.NNE;
        }
    }, 
    NE(DirectionUniqueName.NE) {
        @Override
        public AbsoluteDirection toAbsolute() {
            return AbsoluteDirection.NE;
        }
    }, 
    ENE(DirectionUniqueName.ENE) {
        @Override
        public AbsoluteDirection toAbsolute() {
            return AbsoluteDirection.ENE;
        }
    }, 
    E(DirectionUniqueName.E) {
        @Override
        public AbsoluteDirection toAbsolute() {
            return AbsoluteDirection.E;
        }
    }, 
    ESE(DirectionUniqueName.ESE) {
        @Override
        public AbsoluteDirection toAbsolute() {
            return AbsoluteDirection.ESE;
        }
    }, 
    SE(DirectionUniqueName.SE) {
        @Override
        public AbsoluteDirection toAbsolute() {
            return AbsoluteDirection.SE;
        }
    }, 
    SSE(DirectionUniqueName.SSE) {
        @Override
        public AbsoluteDirection toAbsolute() {
            return AbsoluteDirection.SSE;
        }
    }, 
    S(DirectionUniqueName.S) {
        @Override
        public AbsoluteDirection toAbsolute() {
            return AbsoluteDirection.S;
        }
    }, 
    SSW(DirectionUniqueName.SSW) {
        @Override
        public AbsoluteDirection toAbsolute() {
            return AbsoluteDirection.SSW;
        }
    }, 
    SW(DirectionUniqueName.SW) {
        @Override
        public AbsoluteDirection toAbsolute() {
            return AbsoluteDirection.SW;
        }
    }, 
    WSW(DirectionUniqueName.WSW) {
        @Override
        public AbsoluteDirection toAbsolute() {
            return AbsoluteDirection.WSW;
        }
    }, 
    W(DirectionUniqueName.W) {
        @Override
        public AbsoluteDirection toAbsolute() {
            return AbsoluteDirection.W;
        }
    }, 
    WNW(DirectionUniqueName.WNW) {
        @Override
        public AbsoluteDirection toAbsolute() {
            return AbsoluteDirection.WNW;
        }
    }, 
    NW(DirectionUniqueName.NW) {
        @Override
        public AbsoluteDirection toAbsolute() {
            return AbsoluteDirection.NW;
        }
    }, 
    NNW(DirectionUniqueName.NNW) {
        @Override
        public AbsoluteDirection toAbsolute() {
            return AbsoluteDirection.NNW;
        }
    };
    
    private static final CompassDirection[] LEFT;
    private static final CompassDirection[] LEFTWARD;
    private static final CompassDirection[] RIGHT;
    private static final CompassDirection[] RIGHTWARD;
    private static final CompassDirection[] OPPOSITE;
    final DirectionUniqueName uniqueName;
    
    CompassDirection(final DirectionUniqueName uniqueName) {
        this.uniqueName = uniqueName;
    }
    
    @Override
    public DirectionFacing left() {
        return CompassDirection.LEFT[this.ordinal()];
    }
    
    @Override
    public DirectionFacing leftward() {
        return CompassDirection.LEFTWARD[this.ordinal()];
    }
    
    @Override
    public DirectionFacing right() {
        return CompassDirection.RIGHT[this.ordinal()];
    }
    
    @Override
    public DirectionFacing rightward() {
        return CompassDirection.RIGHTWARD[this.ordinal()];
    }
    
    @Override
    public DirectionFacing opposite() {
        return CompassDirection.OPPOSITE[this.ordinal()];
    }
    
    @Override
    public int index() {
        return this.ordinal();
    }
    
    @Override
    public DirectionUniqueName uniqueName() {
        return this.uniqueName;
    }
    
    @Override
    public int numDirectionValues() {
        return values().length;
    }
    
    @Override
    public String toEnglish() {
        return "<CompassDirection>";
    }
    
    static {
        LEFT = new CompassDirection[] { CompassDirection.NNW, CompassDirection.N, CompassDirection.NNE, CompassDirection.NE, CompassDirection.ENE, CompassDirection.E, CompassDirection.ESE, CompassDirection.SE, CompassDirection.SSE, CompassDirection.S, CompassDirection.SSW, CompassDirection.SW, CompassDirection.WSW, CompassDirection.W, CompassDirection.WNW, CompassDirection.NW };
        LEFTWARD = new CompassDirection[] { CompassDirection.W, CompassDirection.WNW, CompassDirection.NW, CompassDirection.NNW, CompassDirection.N, CompassDirection.NNE, CompassDirection.NE, CompassDirection.ENE, CompassDirection.E, CompassDirection.ESE, CompassDirection.SE, CompassDirection.SSE, CompassDirection.S, CompassDirection.SSW, CompassDirection.SW, CompassDirection.WSW };
        RIGHT = new CompassDirection[] { CompassDirection.NNE, CompassDirection.NE, CompassDirection.ENE, CompassDirection.E, CompassDirection.ESE, CompassDirection.SE, CompassDirection.SSE, CompassDirection.S, CompassDirection.SSW, CompassDirection.SW, CompassDirection.WSW, CompassDirection.W, CompassDirection.WNW, CompassDirection.NW, CompassDirection.NNW, CompassDirection.N };
        RIGHTWARD = new CompassDirection[] { CompassDirection.E, CompassDirection.ESE, CompassDirection.SE, CompassDirection.SSE, CompassDirection.S, CompassDirection.SSW, CompassDirection.SW, CompassDirection.WSW, CompassDirection.W, CompassDirection.WNW, CompassDirection.NW, CompassDirection.NNW, CompassDirection.N, CompassDirection.NNE, CompassDirection.NE, CompassDirection.ENE };
        OPPOSITE = new CompassDirection[] { CompassDirection.S, CompassDirection.SSW, CompassDirection.SW, CompassDirection.WSW, CompassDirection.W, CompassDirection.WNW, CompassDirection.NW, CompassDirection.NNW, CompassDirection.N, CompassDirection.NNE, CompassDirection.NE, CompassDirection.ENE, CompassDirection.E, CompassDirection.ESE, CompassDirection.SE, CompassDirection.SSE };
    }
}
