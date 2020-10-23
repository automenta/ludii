// 
// Decompiled by Procyon v0.5.36
// 

package game.util.directions;

import util.Ludeme;

public interface DirectionFacing extends Ludeme
{
    DirectionFacing left();
    
    DirectionFacing right();
    
    DirectionFacing rightward();
    
    DirectionFacing leftward();
    
    DirectionFacing opposite();
    
    int index();
    
    DirectionUniqueName uniqueName();
    
    int numDirectionValues();
    
    AbsoluteDirection toAbsolute();
    
    class DirectionMap<V>
    {
        protected final Object[] values;
        protected final boolean[] occupied;
        protected int size;
        
        public DirectionMap(final DirectionFacing exampleKey) {
            this.size = 0;
            this.values = new Object[exampleKey.numDirectionValues()];
            this.occupied = new boolean[exampleKey.numDirectionValues()];
        }
        
        public V get(final DirectionFacing key) {
            return (V)this.values[key.index()];
        }
        
        public V put(final DirectionFacing key, final V value) {
            final int idx = key.index();
            final V old = (V)this.values[idx];
            this.values[idx] = value;
            if (!this.occupied[idx]) {
                ++this.size;
                this.occupied[idx] = true;
            }
            return old;
        }
    }
}
