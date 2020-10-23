// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.end;

import annotations.Or;
import game.Game;
import game.functions.ints.board.Id;
import game.rules.Rule;
import game.types.play.ResultType;
import game.types.play.RoleType;
import gnu.trove.list.array.TIntArrayList;
import main.Status;
import util.Context;
import util.Trial;
import util.state.State;

import java.util.Arrays;

public class End implements Rule
{
    private static final long serialVersionUID = 1L;
    private final EndRule[] endRules;
    private boolean match;
    
    public End(@Or final EndRule endRule, @Or final EndRule[] endRules) {
        this.match = false;
        int numNonNull = 0;
        if (endRule != null) {
            ++numNonNull;
        }
        if (endRules != null) {
            ++numNonNull;
        }
        if (numNonNull != 1) {
            throw new IllegalArgumentException("Exactly one Or parameter must be non-null.");
        }
        if (endRule != null) {
            this.endRules = new EndRule[] { endRule };
        }
        else {
            this.endRules = endRules;
        }
    }
    
    public EndRule[] endRules() {
        return this.endRules;
    }
    
    @Override
    public void eval(final Context context) {
        if (this.match) {
            this.evalMatch(context);
        }
        else {
            for (final EndRule endingRule : this.endRules) {
                final EndRule endRuleResult = endingRule.eval(context);
                if (endRuleResult != null) {
                    final Result applyResult = endRuleResult.result();
                    if (applyResult != null) {
                        final int whoResult = new Id(null, applyResult.who()).eval(context);
                        if (context.active(whoResult) || whoResult == context.game().players().size()) {
                            applyResult(applyResult, context);
                        }
                    }
                }
            }
            if (!context.trial().over() && context.game().requiresAllPass() && context.allPass()) {
                final Result applyResult = new Result(RoleType.All, ResultType.Draw);
                applyResult(applyResult, context);
            }
        }
        context.setNumEndResultsDecided(0);
    }
    
    public static void applyResult(final Result applyResult, final Context context) {
        final RoleType whoRole = applyResult.who();
        if (whoRole.toString().contains("Team")) {
            applyTeamResult(applyResult, context);
        }
        else {
            final Trial trial = context.trial();
            final State state = context.state();
            final int who = new Id(null, whoRole).eval(context);
            int rank = 1;
            int onlyOneActive = -1;
            switch (applyResult.result()) {
                case Win: {
                    if (whoRole.equals(RoleType.All)) {
                        if (context.game().players().count() > 1) {
                            final double score = 1.0;
                            for (int player = 1; player < context.trial().ranking().length; ++player) {
                                if (context.trial().ranking()[player] == 0.0) {
                                    context.addWinner(player);
                                    context.trial().ranking()[player] = 1.0;
                                }
                            }
                        }
                        else {
                            context.trial().ranking()[1] = 0.0;
                        }
                        context.setAllInactive();
                        trial.setStatus(new Status(0));
                        break;
                    }
                    context.setActive(who, false);
                    context.addWinner(who);
                    rank = (int)context.computeNextWinRank();
                    context.trial().ranking()[who] = rank;
                    onlyOneActive = context.onlyOneActive();
                    if (onlyOneActive != 0) {
                        context.trial().ranking()[onlyOneActive] = rank + 1;
                        for (int player2 = 1; player2 < context.trial().ranking().length; ++player2) {
                            if (context.trial().ranking()[player2] == 1.0) {
                                trial.setStatus(new Status(player2));
                                break;
                            }
                        }
                        break;
                    }
                    if (!context.active()) {
                        context.setAllInactive();
                        for (int player2 = 1; player2 < context.trial().ranking().length; ++player2) {
                            if (context.trial().ranking()[player2] == 1.0) {
                                trial.setStatus(new Status(player2));
                                break;
                            }
                        }
                        break;
                    }
                    break;
                }
                case Loss: {
                    if (whoRole.equals(RoleType.All)) {
                        if (context.game().players().count() > 1) {
                            final double score = context.game().players().count();
                            for (int player = 1; player < context.trial().ranking().length; ++player) {
                                if (context.trial().ranking()[player] == 0.0) {
                                    context.trial().ranking()[player] = score;
                                }
                            }
                        }
                        else {
                            context.trial().ranking()[1] = 0.0;
                        }
                        context.setAllInactive();
                        trial.setStatus(new Status(0));
                        break;
                    }
                    if (!context.active(who)) {
                        break;
                    }
                    context.setActive(who, false);
                    if (state.next() == who && context.game().players().count() != 1) {
                        int next;
                        for (next = who; !context.active(next) && context.active(); next = 1) {
                            if (++next > context.game().players().count()) {}
                        }
                        state.setNext(next);
                    }
                    if (context.trial().ranking().length > 2) {
                        rank = (int)context.computeNextLossRank();
                        final int numEndResultApplies = context.numEndResultsDecided();
                        context.trial().ranking()[who] = rank + numEndResultApplies / 2.0;
                        if (numEndResultApplies != 0) {
                            for (int id = 0; id < context.trial().ranking().length; ++id) {
                                if (context.trial().ranking()[id] == rank + numEndResultApplies) {
                                    context.trial().ranking()[id] = rank + numEndResultApplies / 2.0;
                                }
                            }
                        }
                        if (!context.active()) {
                            context.setAllInactive();
                            for (int player3 = 1; player3 < context.trial().ranking().length; ++player3) {
                                if (context.trial().ranking()[player3] == 1.0) {
                                    trial.setStatus(new Status(player3));
                                    break;
                                }
                            }
                        }
                        else {
                            onlyOneActive = context.onlyOneActive();
                            if (onlyOneActive != 0) {
                                context.trial().ranking()[onlyOneActive] = rank - 1;
                                double minRank = 10000.0;
                                for (int player4 = 1; player4 < context.trial().ranking().length; ++player4) {
                                    if (context.trial().ranking()[player4] < minRank) {
                                        minRank = context.trial().ranking()[player4];
                                    }
                                }
                                for (int player4 = 1; player4 < context.trial().ranking().length; ++player4) {
                                    if (context.trial().ranking()[player4] == minRank) {
                                        trial.setStatus(new Status(player4));
                                        context.addWinner(player4);
                                        break;
                                    }
                                }
                                context.setActive(onlyOneActive, false);
                            }
                        }
                        break;
                    }
                    context.setAllInactive();
                    trial.setStatus(new Status(0));
                    break;
                }
                case Draw: {
                    if (context.game().players().count() > 1) {
                        final double score = context.computeNextDrawRank();
                        for (int player = 1; player < context.trial().ranking().length; ++player) {
                            if (context.trial().ranking()[player] == 0.0) {
                                context.trial().ranking()[player] = score;
                            }
                        }
                    }
                    else if (context.game().players().count() == 1) {
                        context.trial().ranking()[1] = 0.0;
                    }
                    context.setAllInactive();
                    trial.setStatus(new Status(0));
                    for (int i = 0; i < context.trial().ranking().length; ++i) {
                        if (context.trial().ranking()[i] == 1.0) {
                            trial.setStatus(new Status(i));
                        }
                    }
                    break;
                }
                case Tie: {
                    context.setAllInactive();
                    trial.setStatus(new Status(state.numPlayers() + 1));
                    break;
                }
                case Abandon:
                case Crash: {
                    context.setAllInactive();
                    trial.setStatus(new Status(-1));
                    break;
                }
                default: {
                    System.out.println("** End.apply(): Result type " + applyResult.result() + " not recognised.");
                    break;
                }
            }
        }
        context.setNumEndResultsDecided(context.numEndResultsDecided() + 1);
    }
    
    private void evalMatch(final Context context) {
        for (final EndRule endingRule : this.endRules) {
            final EndRule endRule = endingRule.eval(context);
            if (endRule != null) {
                final Result applyResult = endRule.result();
                if (applyResult != null) {
                    applyResultMatch(applyResult, context);
                    break;
                }
            }
        }
    }
    
    public static void applyResultMatch(final Result applyResult, final Context context) {
        final RoleType whoRole = applyResult.who();
        if (whoRole.toString().contains("Team")) {
            applyTeamResult(applyResult, context);
        }
        else {
            final Trial trial = context.trial();
            final int who = new Id(null, whoRole).eval(context);
            switch (applyResult.result()) {
                case Win: {
                    context.setActive(who, false);
                    context.addWinner(who);
                    int rank;
                    for (rank = 1; rank < context.trial().ranking().length; ++rank) {
                        boolean yourRank = true;
                        for (int player = 1; player < context.trial().ranking().length; ++player) {
                            if (context.trial().ranking()[player] == rank) {
                                yourRank = false;
                                break;
                            }
                        }
                        if (yourRank) {
                            context.trial().ranking()[who] = rank;
                            break;
                        }
                    }
                    final int onlyOneActive = context.onlyOneActive();
                    if (onlyOneActive != 0) {
                        context.trial().ranking()[onlyOneActive] = rank + 1;
                        for (int player = 1; player < context.trial().ranking().length; ++player) {
                            if (context.trial().ranking()[player] == 1.0) {
                                trial.setStatus(new Status(player));
                                break;
                            }
                        }
                        break;
                    }
                    if (!context.active()) {
                        context.setAllInactive();
                        for (int player = 1; player < context.trial().ranking().length; ++player) {
                            if (context.trial().ranking()[player] == 1.0) {
                                trial.setStatus(new Status(player));
                                break;
                            }
                        }
                        break;
                    }
                    break;
                }
                case Loss: {
                    break;
                }
                case Draw: {
                    if (context.game().players().count() > 1) {
                        final double score = (context.numActive() + 1) / 2.0 + context.numWinners();
                        for (int player2 = 1; player2 < context.trial().ranking().length; ++player2) {
                            if (context.trial().ranking()[player2] == 0.0) {
                                context.trial().ranking()[player2] = score;
                            }
                        }
                    }
                    else {
                        context.trial().ranking()[1] = 0.0;
                    }
                    trial.setStatus(new Status(0));
                    for (int i = 0; i < context.trial().ranking().length; ++i) {
                        if (context.trial().ranking()[i] == 1.0) {
                            trial.setStatus(new Status(i));
                        }
                    }
                    break;
                }
                case Tie: {
                    trial.setStatus(new Status(context.game().players().count() + 1));
                    break;
                }
                case Abandon:
                case Crash: {
                    trial.setStatus(new Status(-1));
                    break;
                }
                default: {
                    System.out.println("** End.apply(): Result type " + applyResult.result() + " not recognised.");
                    break;
                }
            }
        }
    }
    
    public static void applyTeamResult(final Result applyResult, final Context context) {
        final Trial trial = context.trial();
        final State state = context.state();
        final RoleType whoRole = applyResult.who();
        final int team = new Id(null, whoRole).eval(context);
        switch (applyResult.result()) {
            case Win: {
                final TIntArrayList teamMembers = new TIntArrayList();
                for (int pid = 1; pid <= context.game().players().count(); ++pid) {
                    if (context.state().playerInTeam(pid, team)) {
                        teamMembers.add(pid);
                    }
                }
                for (int i = 0; i < teamMembers.size(); ++i) {
                    final int pid2 = teamMembers.getQuick(i);
                    context.setActive(pid2, false);
                    context.addWinner(pid2);
                }
                int rank;
                for (rank = 1; rank < context.trial().ranking().length; ++rank) {
                    boolean yourRank = true;
                    for (int player = 1; player < context.trial().ranking().length; ++player) {
                        if (context.trial().ranking()[player] == rank) {
                            yourRank = false;
                            break;
                        }
                    }
                    if (yourRank) {
                        for (int j = 0; j < teamMembers.size(); ++j) {
                            final int pid3 = teamMembers.getQuick(j);
                            context.trial().ranking()[pid3] = rank;
                        }
                        break;
                    }
                }
                final int onlyOneActive = context.onlyOneTeamActive();
                if (onlyOneActive != 0) {
                    final TIntArrayList teamLossMembers = new TIntArrayList();
                    for (int pid3 = 1; pid3 <= context.game().players().count(); ++pid3) {
                        if (context.state().playerInTeam(pid3, onlyOneActive)) {
                            teamLossMembers.add(pid3);
                        }
                    }
                    for (int k = 0; k < teamLossMembers.size(); ++k) {
                        final int pid4 = teamLossMembers.getQuick(k);
                        context.trial().ranking()[pid4] = rank + teamMembers.size();
                        for (int player2 = 1; player2 < context.trial().ranking().length; ++player2) {
                            if (context.trial().ranking()[player2] == 1.0) {
                                trial.setStatus(new Status(player2));
                                break;
                            }
                        }
                    }
                    break;
                }
                if (!context.active()) {
                    context.setAllInactive();
                    for (int player = 1; player < context.trial().ranking().length; ++player) {
                        if (context.trial().ranking()[player] == 1.0) {
                            trial.setStatus(new Status(player));
                            break;
                        }
                    }
                    break;
                }
                break;
            }
            case Loss: {
                break;
            }
            case Draw: {
                break;
            }
            case Tie: {
                context.setAllInactive();
                trial.setStatus(new Status(state.numPlayers() + 1));
                break;
            }
            case Abandon:
            case Crash: {
                context.setAllInactive();
                trial.setStatus(new Status(-1));
                break;
            }
            default: {
                System.out.println("** End.apply(): Result type " + applyResult.result() + " not recognised.");
                break;
            }
        }
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = 0L;
        for (final EndRule endRule : this.endRules) {
            gameFlags |= endRule.gameFlags(game);
        }
        return gameFlags;
    }
    
    @Override
    public void preprocess(final Game game) {
        for (final EndRule endRule : this.endRules) {
            endRule.preprocess(game);
        }
    }
    
    public void setMatch(final boolean value) {
        this.match = value;
    }
    
    @Override
    public String toEnglish() {
        return "<End>";
    }
    
    @Override
    public String toString() {
        return "[End: " + Arrays.toString(this.endRules) + "]";
    }
}
