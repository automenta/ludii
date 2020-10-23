// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.end;

import annotations.Opt;
import game.Game;
import game.functions.ints.board.Id;
import game.util.end.Score;
import main.Status;
import util.Context;
import util.Trial;

public class ByScore extends Result
{
    private static final long serialVersionUID = 1L;
    private final Score[] finalScore;
    
    public ByScore(@Opt final Score[] finalScore) {
        super(null, null);
        this.finalScore = finalScore;
    }
    
    @Override
    public void eval(final Context context) {
        final Trial trial = context.trial();
        if (this.finalScore != null) {
            for (final Score score : this.finalScore) {
                final int pid = new Id(null, score.role()).eval(context);
                final int scoreToSet = score.score().eval(context);
                context.setScore(pid, scoreToSet);
            }
        }
        final int numPlayers = context.game().players().count();
        context.setAllInactive();
        int pid;
        int[] allScores;
        for (allScores = new int[numPlayers + 1], pid = 1; pid < allScores.length; ++pid) {
            allScores[pid] = context.score(pid);
        }
        while (true) {
            int maxScore = Integer.MIN_VALUE;
            int numMax = 0;
            for (int p = 1; p < allScores.length; ++p) {
                final int score2 = allScores[p];
                if (score2 > maxScore) {
                    maxScore = score2;
                    numMax = 1;
                }
                else if (score2 == maxScore) {
                    ++numMax;
                }
            }
            if (maxScore == Integer.MIN_VALUE) {
                break;
            }
            final double nextWinRank = context.computeNextWinRank();
            final double avgNextBestRank = (nextWinRank * 2.0 + numMax - 1.0) / 2.0;
            for (int p2 = 1; p2 < allScores.length; ++p2) {
                if (maxScore == allScores[p2]) {
                    context.trial().ranking()[p2] = avgNextBestRank;
                    allScores[p2] = Integer.MIN_VALUE;
                }
            }
        }
        int winner = 0;
        for (int p3 = 1; p3 < context.trial().ranking().length; ++p3) {
            if (context.trial().ranking()[p3] == 1.0) {
                winner = p3;
                break;
            }
        }
        trial.setStatus(new Status(winner));
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = 0L;
        gameFlags |= 0x100L;
        if (this.finalScore != null) {
            for (final Score fScore : this.finalScore) {
                gameFlags |= fScore.score().gameFlags(game);
            }
        }
        return gameFlags;
    }
    
    @Override
    public void preprocess(final Game game) {
        if (this.finalScore != null) {
            for (final Score fScore : this.finalScore) {
                fScore.score().preprocess(game);
            }
        }
    }
}
