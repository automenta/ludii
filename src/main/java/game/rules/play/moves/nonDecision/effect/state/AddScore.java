// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect.state;

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

public final class AddScore extends Effect
{
    private static final long serialVersionUID = 1L;
    private final IntFunction[] players;
    private final IntFunction[] scores;
    
    public AddScore(@Or final Player player, @Or final RoleType role, final IntFunction score, @Opt final Then then) {
        super(then);
        int numNonNull = 0;
        if (player != null) {
            ++numNonNull;
        }
        if (role != null) {
            ++numNonNull;
        }
        if (numNonNull != 1) {
            throw new IllegalArgumentException("Exactly one Or parameter must be non-null.");
        }
        this.players = new IntFunction[1];
        if (player != null) {
            this.players[0] = player.index();
        }
        else {
            this.players[0] = new Id(null, role);
        }
        if (score != null) {
            (this.scores = new IntFunction[1])[0] = score;
        }
        else {
            this.scores = null;
        }
    }
    
    public AddScore(@Or final IntFunction[] players, @Or final RoleType[] roles, final IntFunction[] scores, @Opt final Then then) {
        super(then);
        int numNonNull = 0;
        if (players != null) {
            ++numNonNull;
        }
        if (roles != null) {
            ++numNonNull;
        }
        if (numNonNull != 1) {
            throw new IllegalArgumentException("Exactly one Or parameter must be non-null.");
        }
        if (players != null) {
            this.players = players;
        }
        else {
            this.players = new IntFunction[roles.length];
            for (int i = 0; i < roles.length; ++i) {
                final RoleType role = roles[i];
                this.players[i] = new Id(null, role);
            }
        }
        this.scores = scores;
    }
    
    @Override
    public Moves eval(final Context context) {
        final Moves result = new BaseMoves(super.then());
        for (int length = Math.min(this.players.length, this.scores.length), i = 0; i < length; ++i) {
            final int playerId = this.players[i].eval(context);
            final int score = this.scores[i].eval(context);
            final ActionSetScore actionScore = new ActionSetScore(playerId, score, Boolean.TRUE);
            final Move move = new Move(actionScore);
            result.moves().add(move);
        }
        return result;
    }
    
    @Override
    public boolean canMoveTo(final Context context, final int target) {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long stateFlag = 0x100100L | super.gameFlags(game);
        for (final IntFunction player : this.players) {
            stateFlag |= player.gameFlags(game);
        }
        for (final IntFunction score : this.scores) {
            stateFlag |= score.gameFlags(game);
        }
        return stateFlag;
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public void preprocess(final Game game) {
        super.preprocess(game);
        for (final IntFunction player : this.players) {
            player.preprocess(game);
        }
        if (this.scores != null) {
            for (final IntFunction score : this.scores) {
                score.preprocess(game);
            }
        }
    }
    
    @Override
    public String toEnglish() {
        return "AddScore";
    }
}
