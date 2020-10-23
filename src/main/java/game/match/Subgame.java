// 
// Decompiled by Procyon v0.5.36
// 

package game.match;

import annotations.Name;
import annotations.Opt;
import game.Game;
import game.functions.ints.IntFunction;
import util.BaseLudeme;

import java.io.Serializable;

public final class Subgame extends BaseLudeme implements Serializable
{
    private static final long serialVersionUID = 1L;
    private final String gameName;
    private final String optionName;
    private Game game;
    private final IntFunction nextInstance;
    private final IntFunction result;
    private boolean disableMemorylessPlayouts;
    
    public Subgame(final String name, @Opt final String option, @Opt @Name final IntFunction next, @Opt @Name final IntFunction result) {
        this.game = null;
        this.disableMemorylessPlayouts = false;
        this.gameName = name;
        this.optionName = option;
        this.nextInstance = next;
        this.result = result;
    }
    
    public void setGame(final Game game) {
        this.game = game;
        if (this.disableMemorylessPlayouts) {
            game.disableMemorylessPlayouts();
        }
    }
    
    public String gameName() {
        return this.gameName;
    }
    
    public String optionName() {
        return this.optionName;
    }
    
    public Game getGame() {
        return this.game;
    }
    
    public void disableMemorylessPlayouts() {
        this.disableMemorylessPlayouts = true;
        if (this.game != null) {
            this.game.disableMemorylessPlayouts();
        }
    }
    
    @Override
    public String toEnglish() {
        return "(Instance)";
    }
    
    public IntFunction next() {
        return this.nextInstance;
    }
    
    public IntFunction result() {
        return this.result;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("[Subgame: ").append(this.gameName);
        if (this.optionName != null) {
            sb.append(" (").append(this.optionName).append(")");
        }
        sb.append("]");
        return sb.toString();
    }
}
