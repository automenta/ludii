// 
// Decompiled by Procyon v0.5.36
// 

package game.util.directions;

public enum RotationalDirection implements DirectionFacing
{
    Out(DirectionUniqueName.Out) {
        @Override
        public AbsoluteDirection toAbsolute() {
            return AbsoluteDirection.Out;
        }
    }, 
    CW(DirectionUniqueName.CW) {
        @Override
        public AbsoluteDirection toAbsolute() {
            return AbsoluteDirection.CW;
        }
    }, 
    In(DirectionUniqueName.In) {
        @Override
        public AbsoluteDirection toAbsolute() {
            return AbsoluteDirection.In;
        }
    }, 
    CCW(DirectionUniqueName.CCW) {
        @Override
        public AbsoluteDirection toAbsolute() {
            return AbsoluteDirection.CCW;
        }
    };
    
    private static final RotationalDirection[] LEFT;
    private static final RotationalDirection[] RIGHT;
    private static final RotationalDirection[] OPPOSITE;
    final DirectionUniqueName uniqueName;
    
    RotationalDirection(final DirectionUniqueName uniqueName) {
        this.uniqueName = uniqueName;
    }
    
    @Override
    public DirectionFacing left() {
        return RotationalDirection.LEFT[this.ordinal()];
    }
    
    @Override
    public DirectionFacing leftward() {
        return RotationalDirection.LEFT[this.ordinal()];
    }
    
    @Override
    public DirectionFacing right() {
        return RotationalDirection.RIGHT[this.ordinal()];
    }
    
    @Override
    public DirectionFacing rightward() {
        return RotationalDirection.RIGHT[this.ordinal()];
    }
    
    @Override
    public DirectionFacing opposite() {
        return RotationalDirection.OPPOSITE[this.ordinal()];
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
        return "<WheelDirection>";
    }
    
    static {
        LEFT = new RotationalDirection[] { RotationalDirection.CCW, RotationalDirection.In, RotationalDirection.CW, RotationalDirection.Out };
        RIGHT = new RotationalDirection[] { RotationalDirection.CW, RotationalDirection.In, RotationalDirection.CCW, RotationalDirection.Out };
        OPPOSITE = new RotationalDirection[] { RotationalDirection.In, RotationalDirection.CCW, RotationalDirection.Out, RotationalDirection.CW };
    }
}
