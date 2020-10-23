// 
// Decompiled by Procyon v0.5.36
// 

package util.action.others;

import util.Context;
import util.action.Action;
import util.action.ActionType;
import util.action.BaseAction;

import java.util.ArrayList;
import java.util.List;

public final class ActionVote extends BaseAction
{
    private static final long serialVersionUID = 1L;
    private final String vote;
    
    public ActionVote(final String vote, final boolean useless) {
        this.vote = vote;
    }
    
    public ActionVote(final String detailedString) {
        assert detailedString.startsWith("[Vote:");
        final String strVote = Action.extractData(detailedString, "vote");
        this.vote = strVote;
        final String strDecision = Action.extractData(detailedString, "decision");
        this.decision = (!strDecision.isEmpty() && Boolean.parseBoolean(strDecision));
    }
    
    @Override
    public Action apply(final Context context, final boolean store) {
        context.state().votes().add(this.vote);
        final List<String> votes = context.state().votes();
        final int nbPlayers = context.game().players().count();
        if (votes.size() == nbPlayers) {
            final List<String> votesChecked = new ArrayList<>();
            int countForDecision = 0;
            int decisionindex = 0;
            for (int i = 0; i < votes.size(); ++i) {
                final String v = votes.get(i);
                int currentCount = 0;
                if (!votesChecked.contains(v)) {
                    votesChecked.add(v);
                    for (int j = i; j < votes.size(); ++j) {
                        if (votes.get(j).equals(v)) {
                            ++currentCount;
                        }
                    }
                    if (currentCount > countForDecision) {
                        countForDecision = currentCount;
                        decisionindex = i;
                    }
                }
            }
            if (countForDecision > nbPlayers / 2) {
                context.state().setIsDecided(votes.get(decisionindex));
            }
            context.state().clearPropositions();
            context.state().clearVotes();
        }
        return this;
    }
    
    @Override
    public String toTrialFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        sb.append("[Vote:");
        sb.append("vote=").append(this.vote);
        if (this.decision) {
            sb.append(",decision=").append(this.decision);
        }
        sb.append(']');
        return sb.toString();
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + (this.decision ? 1231 : 1237);
        result = 31 * result + this.vote.hashCode();
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ActionVote)) {
            return false;
        }
        final ActionVote other = (ActionVote)obj;
        return this.decision == other.decision && this.vote.equals(other.vote);
    }
    
    @Override
    public String toTurnFormat(final Context context) {
        return "Vote \"" + this.vote + "\"";
    }
    
    @Override
    public String toMoveFormat(final Context context) {
        return "(Vote \"" + this.vote + "\")";
    }
    
    @Override
    public String getDescription() {
        return "Vote";
    }
    
    @Override
    public String vote() {
        return this.vote;
    }
    
    @Override
    public boolean isOtherMove() {
        return true;
    }
    
    @Override
    public ActionType actionType() {
        return ActionType.Vote;
    }
}
