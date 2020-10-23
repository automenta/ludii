// 
// Decompiled by Procyon v0.5.36
// 

package game.util.moves;

import util.BaseLudeme;

public class Flips extends BaseLudeme
{
    private final int flipA;
    private final int flipB;
    
    public Flips(final Integer flipA, final Integer flipB) {
        this.flipA = flipA;
        this.flipB = flipB;
    }
    
    public int flipValue(final int currentState) {
        if (currentState == this.flipA) {
            return this.flipB;
        }
        if (currentState == this.flipB) {
            return this.flipA;
        }
        return currentState;
    }
    
    public int flipA() {
        return this.flipA;
    }
    
    public int flipB() {
        return this.flipB;
    }
}
