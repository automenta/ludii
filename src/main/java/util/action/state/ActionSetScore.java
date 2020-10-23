// 
// Decompiled by Procyon v0.5.36
// 

package util.action.state;

import util.Context;
import util.action.Action;
import util.action.BaseAction;

public final class ActionSetScore extends BaseAction
{
    private static final long serialVersionUID = 1L;
    private final int player;
    private final int score;
    private final boolean add;
    
    public ActionSetScore(final int player, final int score, final Boolean add) {
        this.player = player;
        this.score = score;
        this.add = (add != null && add);
    }
    
    public ActionSetScore(final String detailedString) {
        assert detailedString.startsWith("[SetScore:");
        final String strPlayer = Action.extractData(detailedString, "player");
        this.player = Integer.parseInt(strPlayer);
        final String strScore = Action.extractData(detailedString, "score");
        this.score = Integer.parseInt(strScore);
        final String strAddScore = Action.extractData(detailedString, "add");
        this.add = (!strAddScore.isEmpty() && Boolean.parseBoolean(strAddScore));
        final String strDecision = Action.extractData(detailedString, "decision");
        this.decision = (!strDecision.isEmpty() && Boolean.parseBoolean(strDecision));
    }
    
    @Override
    public Action apply(final Context context, final boolean store) {
        if (this.add) {
            context.setScore(this.player, context.score(this.player) + this.score);
        }
        else {
            context.setScore(this.player, this.score);
        }
        return this;
    }
    
    @Override
    public String toTrialFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        sb.append("[SetScore:");
        sb.append("player=").append(this.player);
        sb.append(",score=").append(this.score);
        if (this.add) {
            sb.append(",add=").append(this.add);
        }
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
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ActionSetScore)) {
            return false;
        }
        final ActionSetScore other = (ActionSetScore)obj;
        return this.decision == other.decision && this.score == other.score && this.player == other.player;
    }
    
    @Override
    public String getDescription() {
        return "SetScore";
    }
    
    @Override
    public String toTurnFormat(final Context context) {
        if (!this.add) {
            return "P" + this.player + "=" + this.score;
        }
        return "P" + this.player + "+=" + this.score;
    }
    
    @Override
    public String toMoveFormat(final Context context) {
        if (!this.add) {
            return "(Score P" + this.player + " = " + this.score + ")";
        }
        return "(Score P" + this.player + " += " + this.score + ")";
    }
    
    @Override
    public int who() {
        return this.player;
    }
}
