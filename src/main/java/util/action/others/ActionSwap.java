// 
// Decompiled by Procyon v0.5.36
// 

package util.action.others;

import util.Context;
import util.action.Action;
import util.action.BaseAction;

public final class ActionSwap extends BaseAction
{
    private static final long serialVersionUID = 1L;
    private final int player1;
    private final int player2;
    
    public ActionSwap(final int player1, final int player2) {
        this.player1 = player1;
        this.player2 = player2;
    }
    
    public ActionSwap(final String detailedString) {
        assert detailedString.startsWith("[Swap:");
        final String strPlayer1 = Action.extractData(detailedString, "player1");
        this.player1 = Integer.parseInt(strPlayer1);
        final String strPlayer2 = Action.extractData(detailedString, "player2");
        this.player2 = Integer.parseInt(strPlayer2);
        final String strDecision = Action.extractData(detailedString, "decision");
        this.decision = (!strDecision.isEmpty() && Boolean.parseBoolean(strDecision));
    }
    
    @Override
    public Action apply(final Context context, final boolean store) {
        context.state().swapPlayerOrder(this.player1, this.player2);
        return this;
    }
    
    @Override
    public String toTrialFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        sb.append("[Swap:");
        sb.append("player1=" + this.player1);
        sb.append(",player2=" + this.player2);
        if (this.decision) {
            sb.append(",decision=" + this.decision);
        }
        sb.append(']');
        return sb.toString();
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + (this.decision ? 1231 : 1237);
        result = 31 * result + this.player1;
        result = 31 * result + this.player2;
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ActionSwap)) {
            return false;
        }
        final ActionSwap other = (ActionSwap)obj;
        return this.decision == other.decision && this.player1 == other.player1 && this.player2 == other.player2;
    }
    
    @Override
    public String getDescription() {
        return "Swap";
    }
    
    @Override
    public String toTurnFormat(final Context context) {
        return "Swap P" + this.player1 + " P" + this.player2;
    }
    
    @Override
    public String toMoveFormat(final Context context) {
        return "(Swap P" + this.player1 + " P" + this.player2 + ")";
    }
    
    @Override
    public boolean isSwap() {
        return true;
    }
    
    public int player1() {
        return this.player1;
    }
    
    public int player2() {
        return this.player2;
    }
}
