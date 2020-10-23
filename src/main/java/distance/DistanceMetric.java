// 
// Decompiled by Procyon v0.5.36
// 

package distance;

import game.Game;

import java.util.List;

public interface DistanceMetric
{
    Score distance(final Game p0, final Game p1);
    
    Score distance(final Game p0, final List<Game> p1, final int p2, final int p3, final double p4, final String p5);
}
