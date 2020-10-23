// 
// Decompiled by Procyon v0.5.36
// 

package game.util.directions;

public class DirectionType
{
    private final int index;
    private final DirectionFacing directionActual;
    
    public DirectionType(final DirectionFacing directionActual) {
        this.index = directionActual.index();
        this.directionActual = directionActual;
    }
    
    public DirectionFacing getDirection() {
        return this.directionActual;
    }
    
    public int index() {
        return this.index;
    }
    
    public DirectionFacing getDirectionActual() {
        return this.directionActual;
    }
    
    @Override
    public String toString() {
        return "[Direction: " + this.directionActual + "]";
    }
}
