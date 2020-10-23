// 
// Decompiled by Procyon v0.5.36
// 

package game.match;

import annotations.Or;
import util.BaseLudeme;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Games extends BaseLudeme implements Serializable
{
    private static final long serialVersionUID = 1L;
    final List<Subgame> games;
    
    public Games(@Or final Subgame game, @Or final Subgame[] games) {
        int numNonNull = 0;
        if (game != null) {
            ++numNonNull;
        }
        if (games != null) {
            ++numNonNull;
        }
        if (numNonNull != 1) {
            throw new IllegalArgumentException("Exactly one Or parameter must be non-null.");
        }
        if (game != null) {
            this.games = new ArrayList<>();
        }
        else {
            if (games.length < 1) {
                throw new IllegalArgumentException("A match needs at least one game.");
            }
            this.games = new ArrayList<>();
            for (final Subgame subGame : games) {
                this.games.add(subGame);
            }
        }
    }
    
    public List<Subgame> games() {
        return this.games;
    }
}
