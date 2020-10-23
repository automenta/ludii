// 
// Decompiled by Procyon v0.5.36
// 

package game.rules;

import annotations.Name;
import annotations.Opt;
import game.rules.end.End;
import game.rules.meta.Meta;
import game.rules.phase.Phase;
import game.rules.play.Play;
import game.rules.play.moves.nonDecision.operators.logical.Or;
import game.rules.start.Start;
import game.types.play.RoleType;
import util.BaseLudeme;

import java.io.Serializable;

public final class Rules extends BaseLudeme implements Serializable
{
    private static final long serialVersionUID = 1L;
    private final Meta metarules;
    private final Start start;
    private final Phase[] phases;
    private End end;
    
    public Rules(@Opt final Meta meta, @Opt final Start start, final Play play, final End end) {
        this.metarules = meta;
        this.start = start;
        this.phases = new Phase[] { new Phase("Default Phase", RoleType.Shared, null, play, null, null, null) };
        this.end = end;
    }
    
    public Rules(@Opt final Meta meta, @Opt final Start start, @Opt final Play play, @Name final Phase[] phases, @Opt final End end) {
        this.metarules = meta;
        this.start = start;
        this.phases = phases;
        for (final Phase phase : phases) {
            if (phase.play() == null) {
                phase.setPlay(play);
            }
            else if (play != null) {
                phase.setPlay(new Play(new Or(phase.play().moves(), play.moves(), null)));
            }
        }
        this.end = end;
    }
    
    public Meta meta() {
        return this.metarules;
    }
    
    public Start start() {
        return this.start;
    }
    
    public Phase[] phases() {
        return this.phases;
    }
    
    public End end() {
        return this.end;
    }
    
    @Override
    public String toEnglish() {
        return "Rules()";
    }
    
    public void setEnd(final End e) {
        this.end = e;
    }
}
