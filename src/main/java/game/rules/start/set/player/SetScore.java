// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.start.set.player;

import annotations.Hide;
import annotations.Opt;
import game.Game;
import game.functions.ints.IntFunction;
import game.functions.ints.board.Id;
import game.rules.start.StartRule;
import game.types.play.RoleType;
import util.Context;
import util.Move;
import util.action.state.ActionSetScore;

@Hide
public final class SetScore extends StartRule
{
    private static final long serialVersionUID = 1L;
    protected final IntFunction[] players;
    protected final IntFunction[] scores;
    protected final boolean InitSameScoreToEachPlayer;
    
    public SetScore(final RoleType role, @Opt final IntFunction score) {
        if (role == RoleType.Each) {
            this.InitSameScoreToEachPlayer = true;
            this.players = new IntFunction[0];
        }
        else {
            this.players = new IntFunction[] { new Id(null, role) };
            this.InitSameScoreToEachPlayer = false;
        }
        this.scores = new IntFunction[] { score };
    }
    
    @Override
    public void eval(final Context context) {
        if (this.InitSameScoreToEachPlayer) {
            final int score = this.scores[0].eval(context);
            for (int pid = 1; pid < context.game().players().size(); ++pid) {
                final ActionSetScore actionScore = new ActionSetScore(pid, score, Boolean.FALSE);
                actionScore.apply(context, true);
                final Move move = new Move(actionScore);
                context.trial().moves().add(move);
                context.trial().addInitPlacement();
            }
        }
        else {
            for (int length = Math.min(this.players.length, this.scores.length), i = 0; i < length; ++i) {
                final int playerId = this.players[i].eval(context);
                final int score2 = this.scores[i].eval(context);
                final ActionSetScore actionScore2 = new ActionSetScore(playerId, score2, Boolean.FALSE);
                actionScore2.apply(context, true);
                final Move move2 = new Move(actionScore2);
                context.trial().moves().add(move2);
                context.trial().addInitPlacement();
            }
        }
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 256L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
    
    @Override
    public String toString() {
        String str = "(initScore ";
        for (int length = Math.min(this.players.length, this.scores.length), i = 0; i < length; ++i) {
            str = str + this.players[i] + " = " + this.scores[i];
            if (i != length - 1) {
                str += ",";
            }
        }
        str += ")";
        return str;
    }
}
