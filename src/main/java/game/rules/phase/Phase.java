// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.phase;

import annotations.Opt;
import annotations.Or;
import game.Game;
import game.mode.Mode;
import game.rules.end.End;
import game.rules.play.Play;
import game.types.play.RoleType;
import util.BaseLudeme;
import util.playout.Playout;

import java.io.Serializable;

public class Phase extends BaseLudeme implements Serializable
{
    private static final long serialVersionUID = 1L;
    private Play play;
    private End end;
    private final RoleType role;
    private final Mode mode;
    private final String name;
    private final NextPhase[] nextPhase;
    private Playout playout;
    
    public Phase(final String name, @Opt final RoleType role, @Opt final Mode mode, @Opt final Play play, @Opt final End end, @Opt @Or final NextPhase nextPhase, @Opt @Or final NextPhase[] nextPhases) {
        this.name = name;
        this.role = ((role == null) ? RoleType.Shared : role);
        this.mode = mode;
        this.play = play;
        this.end = end;
        int numNonNull = 0;
        if (nextPhase != null) {
            ++numNonNull;
        }
        if (nextPhases != null) {
            ++numNonNull;
        }
        if (numNonNull > 1) {
            throw new IllegalArgumentException("Zero or one Or parameter must be non-null.");
        }
        if (nextPhase != null) {
            (this.nextPhase = new NextPhase[1])[0] = nextPhase;
        }
        else if (nextPhases != null) {
            this.nextPhase = nextPhases;
        }
        else {
            this.nextPhase = new NextPhase[0];
        }
    }
    
    public Mode mode() {
        return this.mode;
    }
    
    public Play play() {
        return this.play;
    }
    
    public End end() {
        return this.end;
    }
    
    public String name() {
        return this.name;
    }
    
    public RoleType owner() {
        return this.role;
    }
    
    public NextPhase[] nextPhase() {
        return this.nextPhase;
    }
    
    public void setPlay(final Play play) {
        this.play = play;
    }
    
    public void setEnd(final End end) {
        this.end = end;
    }
    
    public Playout playout() {
        return this.playout;
    }
    
    public void setPlayout(final Playout playout) {
        this.playout = playout;
    }
    
    public void preprocess(final Game game) {
        this.play.moves().preprocess(game);
        for (final NextPhase next : this.nextPhase()) {
            next.preprocess(game);
        }
        if (this.end() != null) {
            this.end().preprocess(game);
        }
    }
    
    public long gameFlags(final Game game) {
        long gameFlags = this.play.moves().gameFlags(game);
        for (final NextPhase next : this.nextPhase()) {
            gameFlags |= next.gameFlags(game);
        }
        if (this.end() != null) {
            gameFlags |= this.end().gameFlags(game);
        }
        return gameFlags;
    }
}
