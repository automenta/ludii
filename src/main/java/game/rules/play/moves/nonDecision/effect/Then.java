// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect;

import annotations.Name;
import annotations.Opt;
import game.Game;
import game.rules.play.moves.Moves;
import game.rules.play.moves.nonDecision.NonDecision;
import util.Ludeme;

import java.io.Serializable;

public class Then implements Serializable, Ludeme
{
    private static final long serialVersionUID = 1L;
    private final Moves moves;
    
    public Then(final NonDecision moves, @Opt @Name final Boolean applyAfterAllMoves) {
        (this.moves = moves).setApplyAfterAllMoves(applyAfterAllMoves != null && applyAfterAllMoves);
    }
    
    @Override
    public String toString() {
        return "[Then: " + this.moves + "]";
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.moves == null) ? 0 : this.moves.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Then)) {
            return false;
        }
        final Then other = (Then)obj;
        if (this.moves == null) {
            if (other.moves != null) {
                return false;
            }
            return this.moves.equals(other.moves);
        }
        return true;
    }
    
    @Override
    public String toEnglish() {
        return "<Then>";
    }
    
    public Moves moves() {
        return this.moves;
    }
    
    public int gameFlags(final Game game) {
        int gameFlags = 0;
        if (this.moves != null) {
            gameFlags = (int)((long)gameFlags | this.moves.gameFlags(game));
        }
        return gameFlags;
    }
}
