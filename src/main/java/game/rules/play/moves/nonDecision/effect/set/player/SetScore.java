// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect.set.player;

import annotations.Hide;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.functions.ints.IntFunction;
import game.functions.ints.board.Id;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.rules.play.moves.nonDecision.effect.Effect;
import game.rules.play.moves.nonDecision.effect.Then;
import game.types.play.RoleType;
import game.util.moves.Player;
import util.Context;
import util.Move;
import util.action.state.ActionSetScore;

@Hide
public final class SetScore extends Effect
{
    private static final long serialVersionUID = 1L;
    private final IntFunction playerFn;
    private final IntFunction scoreFn;
    
    public SetScore(@Or final Player player, @Or final RoleType role, final IntFunction score, @Opt final Then then) {
        super(then);
        if (player != null) {
            this.playerFn = player.index();
        }
        else {
            this.playerFn = new Id(null, role);
        }
        this.scoreFn = score;
    }
    
    @Override
    public Moves eval(final Context context) {
        final Moves result = new BaseMoves(super.then());
        final int playerId = this.playerFn.eval(context);
        final int score = this.scoreFn.eval(context);
        final ActionSetScore actionScore = new ActionSetScore(playerId, score, Boolean.FALSE);
        final Move move = new Move(actionScore);
        result.moves().add(move);
        return result;
    }
    
    @Override
    public boolean canMoveTo(final Context context, final int target) {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long stateFlag = 0x100100L | super.gameFlags(game);
        stateFlag |= this.playerFn.gameFlags(game);
        stateFlag |= this.scoreFn.gameFlags(game);
        return stateFlag;
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.playerFn.preprocess(game);
        this.scoreFn.preprocess(game);
        super.preprocess(game);
    }
    
    @Override
    public String toEnglish() {
        return "SetScore";
    }
    
    public IntFunction player() {
        return this.playerFn;
    }
    
    public IntFunction score() {
        return this.scoreFn;
    }
}
