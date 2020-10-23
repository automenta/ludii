// 
// Decompiled by Procyon v0.5.36
// 

package util.action.state;

import util.Context;
import util.action.Action;
import util.action.ActionType;
import util.action.BaseAction;

public final class ActionBet extends BaseAction
{
    private static final long serialVersionUID = 1L;
    private final int player;
    private final int bet;
    
    public ActionBet(final int player, final int bet) {
        this.player = player;
        this.bet = bet;
    }
    
    public ActionBet(final String detailedString) {
        assert detailedString.startsWith("[Bet:");
        final String strPlayer = Action.extractData(detailedString, "player");
        this.player = Integer.parseInt(strPlayer);
        final String strBet = Action.extractData(detailedString, "bet");
        this.bet = Integer.parseInt(strBet);
        final String strDecision = Action.extractData(detailedString, "decision");
        this.decision = (!strDecision.isEmpty() && Boolean.parseBoolean(strDecision));
    }
    
    @Override
    public Action apply(final Context context, final boolean store) {
        context.state().setAmount(this.player, this.bet);
        return this;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + (this.decision ? 1231 : 1237);
        result = 31 * result + this.player;
        result = 31 * result + this.bet;
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ActionBet)) {
            return false;
        }
        final ActionBet other = (ActionBet)obj;
        return this.decision == other.decision && this.bet == other.bet && this.player == other.player;
    }
    
    @Override
    public String toTrialFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        sb.append("[Bet:");
        sb.append("player=" + this.player);
        sb.append(",bet=" + this.bet);
        if (this.decision) {
            sb.append(",decision=" + this.decision);
        }
        sb.append(']');
        return sb.toString();
    }
    
    @Override
    public String getDescription() {
        return "Bet";
    }
    
    @Override
    public String toTurnFormat(final Context context) {
        return "Bet P" + this.player + " $" + this.bet;
    }
    
    @Override
    public String toMoveFormat(final Context context) {
        return "(P" + this.player + " Bet = " + this.bet + ")";
    }
    
    @Override
    public boolean isOtherMove() {
        return true;
    }
    
    @Override
    public int who() {
        return this.player;
    }
    
    @Override
    public int count() {
        return this.bet;
    }
    
    @Override
    public ActionType actionType() {
        return ActionType.Bet;
    }
}
