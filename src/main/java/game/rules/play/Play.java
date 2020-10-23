// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play;

import game.rules.play.moves.Moves;
import util.BaseLudeme;

import java.io.Serializable;

public final class Play extends BaseLudeme implements Serializable
{
    private static final long serialVersionUID = 1L;
    private final Moves moves;
    
    public Play(final Moves moves) {
        this.moves = moves;
    }
    
    public Moves moves() {
        return this.moves;
    }
    
    @Override
    public String toEnglish() {
        return "<Play>";
    }
}
