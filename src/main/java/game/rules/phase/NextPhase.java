// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.phase;

import annotations.Opt;
import annotations.Or;
import game.Game;
import game.functions.booleans.BooleanConstant;
import game.functions.booleans.BooleanFunction;
import game.functions.ints.BaseIntFunction;
import game.functions.ints.IntFunction;
import game.functions.ints.board.Id;
import game.types.play.RoleType;
import game.util.moves.Player;
import util.Context;

public final class NextPhase extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction who;
    private final BooleanFunction cond;
    private final String phaseName;
    
    public NextPhase(@Opt @Or final RoleType role, @Opt @Or final Player indexPlayer, @Opt final BooleanFunction cond, @Opt final String phaseName) {
        int numNonNull = 0;
        if (role != null) {
            ++numNonNull;
        }
        if (indexPlayer != null) {
            ++numNonNull;
        }
        if (numNonNull > 1) {
            throw new IllegalArgumentException("Zero or one Or parameter must be non-null.");
        }
        this.cond = ((cond == null) ? BooleanConstant.construct(true) : cond);
        this.phaseName = phaseName;
        if (indexPlayer != null) {
            this.who = indexPlayer.index();
        }
        else if (role != null) {
            this.who = new Id(null, role);
        }
        else {
            this.who = new Id(null, RoleType.Shared);
        }
    }
    
    @Override
    public final int eval(final Context context) {
        if (context.game().rules().phases() == null) {
            return -1;
        }
        final Phase[] phases = context.game().rules().phases();
        if (!this.cond.eval(context)) {
            return -1;
        }
        if (this.phaseName == null) {
            final int pid = this.who.eval(context);
            final int currentPhase = (pid == context.game().players().size()) ? context.state().currentPhase(context.state().mover()) : context.state().currentPhase(pid);
            return (currentPhase + 1) % phases.length;
        }
        for (int phaseId = 0; phaseId < phases.length; ++phaseId) {
            final Phase phase = phases[phaseId];
            if (phase.name().equals(this.phaseName)) {
                return phaseId;
            }
        }
        throw new IllegalArgumentException("BUG: Phase " + this.phaseName + " unfounded.");
    }
    
    public IntFunction who() {
        return this.who;
    }
    
    @Override
    public boolean isStatic() {
        return this.cond.isStatic() && this.who.isStatic();
    }
    
    @Override
    public long gameFlags(final Game game) {
        return this.cond.gameFlags(game) | this.who.gameFlags(game);
    }
    
    @Override
    public void preprocess(final Game game) {
        this.cond.preprocess(game);
        this.who.preprocess(game);
    }
}
