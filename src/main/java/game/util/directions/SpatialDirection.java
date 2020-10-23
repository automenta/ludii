// 
// Decompiled by Procyon v0.5.36
// 

package game.util.directions;

public enum SpatialDirection implements DirectionFacing
{
    D(DirectionUniqueName.D) {
        @Override
        public AbsoluteDirection toAbsolute() {
            return AbsoluteDirection.D;
        }
    }, 
    DN(DirectionUniqueName.DN) {
        @Override
        public AbsoluteDirection toAbsolute() {
            return AbsoluteDirection.DN;
        }
    }, 
    DNE(DirectionUniqueName.DNE) {
        @Override
        public AbsoluteDirection toAbsolute() {
            return AbsoluteDirection.DNE;
        }
    }, 
    DE(DirectionUniqueName.DE) {
        @Override
        public AbsoluteDirection toAbsolute() {
            return AbsoluteDirection.DE;
        }
    }, 
    DSE(DirectionUniqueName.DSE) {
        @Override
        public AbsoluteDirection toAbsolute() {
            return AbsoluteDirection.DSE;
        }
    }, 
    DS(DirectionUniqueName.DS) {
        @Override
        public AbsoluteDirection toAbsolute() {
            return AbsoluteDirection.DS;
        }
    }, 
    DSW(DirectionUniqueName.DSW) {
        @Override
        public AbsoluteDirection toAbsolute() {
            return AbsoluteDirection.DSW;
        }
    }, 
    DW(DirectionUniqueName.DW) {
        @Override
        public AbsoluteDirection toAbsolute() {
            return AbsoluteDirection.DW;
        }
    }, 
    DNW(DirectionUniqueName.DSW) {
        @Override
        public AbsoluteDirection toAbsolute() {
            return AbsoluteDirection.DSW;
        }
    }, 
    U(DirectionUniqueName.U) {
        @Override
        public AbsoluteDirection toAbsolute() {
            return AbsoluteDirection.U;
        }
    }, 
    UN(DirectionUniqueName.UN) {
        @Override
        public AbsoluteDirection toAbsolute() {
            return AbsoluteDirection.UN;
        }
    }, 
    UNE(DirectionUniqueName.UNE) {
        @Override
        public AbsoluteDirection toAbsolute() {
            return AbsoluteDirection.UNE;
        }
    }, 
    UE(DirectionUniqueName.UE) {
        @Override
        public AbsoluteDirection toAbsolute() {
            return AbsoluteDirection.UE;
        }
    }, 
    USE(DirectionUniqueName.USE) {
        @Override
        public AbsoluteDirection toAbsolute() {
            return AbsoluteDirection.USE;
        }
    }, 
    US(DirectionUniqueName.US) {
        @Override
        public AbsoluteDirection toAbsolute() {
            return AbsoluteDirection.US;
        }
    }, 
    USW(DirectionUniqueName.USW) {
        @Override
        public AbsoluteDirection toAbsolute() {
            return AbsoluteDirection.USW;
        }
    }, 
    UW(DirectionUniqueName.UW) {
        @Override
        public AbsoluteDirection toAbsolute() {
            return AbsoluteDirection.UW;
        }
    }, 
    UNW(DirectionUniqueName.UNW) {
        @Override
        public AbsoluteDirection toAbsolute() {
            return AbsoluteDirection.UNW;
        }
    };
    
    private static final SpatialDirection[] LEFT;
    private static final SpatialDirection[] RIGHT;
    private static final SpatialDirection[] OPPOSITE;
    final DirectionUniqueName uniqueName;
    
    SpatialDirection(final DirectionUniqueName uniqueName) {
        this.uniqueName = uniqueName;
    }
    
    @Override
    public DirectionFacing left() {
        return SpatialDirection.LEFT[this.ordinal()];
    }
    
    @Override
    public DirectionFacing leftward() {
        return SpatialDirection.LEFT[this.ordinal()];
    }
    
    @Override
    public DirectionFacing right() {
        return SpatialDirection.RIGHT[this.ordinal()];
    }
    
    @Override
    public DirectionFacing rightward() {
        return SpatialDirection.RIGHT[this.ordinal()];
    }
    
    @Override
    public DirectionFacing opposite() {
        return SpatialDirection.OPPOSITE[this.ordinal()];
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
        return "<SpatialDirection>";
    }
    
    static {
        LEFT = new SpatialDirection[] { SpatialDirection.D, SpatialDirection.DNW, SpatialDirection.DN, SpatialDirection.DNE, SpatialDirection.DE, SpatialDirection.DSE, SpatialDirection.DS, SpatialDirection.DSW, SpatialDirection.DW, SpatialDirection.U, SpatialDirection.UNW, SpatialDirection.UN, SpatialDirection.UNE, SpatialDirection.UE, SpatialDirection.USE, SpatialDirection.US, SpatialDirection.USW, SpatialDirection.UW };
        RIGHT = new SpatialDirection[] { SpatialDirection.D, SpatialDirection.DNE, SpatialDirection.DE, SpatialDirection.DSE, SpatialDirection.DS, SpatialDirection.DSW, SpatialDirection.DW, SpatialDirection.DNW, SpatialDirection.DN, SpatialDirection.U, SpatialDirection.UNE, SpatialDirection.UE, SpatialDirection.USE, SpatialDirection.US, SpatialDirection.USW, SpatialDirection.UW, SpatialDirection.UNW, SpatialDirection.UN };
        OPPOSITE = new SpatialDirection[] { SpatialDirection.U, SpatialDirection.US, SpatialDirection.USW, SpatialDirection.UW, SpatialDirection.UNW, SpatialDirection.UN, SpatialDirection.UNE, SpatialDirection.UE, SpatialDirection.USE, SpatialDirection.D, SpatialDirection.DS, SpatialDirection.DSW, SpatialDirection.DW, SpatialDirection.DNW, SpatialDirection.DN, SpatialDirection.DNE, SpatialDirection.DE, SpatialDirection.DSE };
    }
}
