// 
// Decompiled by Procyon v0.5.36
// 

package game.util.moves;

import game.functions.ints.IntFunction;
import game.functions.ints.state.Mover;
import util.BaseLudeme;

public class Player extends BaseLudeme
{
    private final IntFunction index;
    private final IntFunction indexReturned;
    
    public Player(final IntFunction index) {
        this.index = index;
        this.indexReturned = ((index == null) ? new Mover() : index);
    }
    
    public IntFunction originalIndex() {
        return this.index;
    }
    
    public IntFunction index() {
        return this.indexReturned;
    }
}
