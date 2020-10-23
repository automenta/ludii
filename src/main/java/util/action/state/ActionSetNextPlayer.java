// 
// Decompiled by Procyon v0.5.36
// 

package util.action.state;

import util.Context;
import util.action.Action;
import util.action.BaseAction;

public final class ActionSetNextPlayer extends BaseAction
{
    private static final long serialVersionUID = 1L;
    private final int player;
    
    public ActionSetNextPlayer(final int player) {
        this.player = player;
    }
    
    public ActionSetNextPlayer(final String detailedString) {
        assert detailedString.startsWith("[SetNextPlayer:");
        final String strPlayer = Action.extractData(detailedString, "player");
        this.player = Integer.parseInt(strPlayer);
        final String strDecision = Action.extractData(detailedString, "decision");
        this.decision = (!strDecision.isEmpty() && Boolean.parseBoolean(strDecision));
    }
    
    @Override
    public Action apply(final Context context, final boolean store) {
        context.state().setNext(this.player);
        return this;
    }
    
    @Override
    public String toTrialFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        sb.append("[SetNextPlayer:");
        sb.append("player=").append(this.player);
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
        result = 31 * result + this.player;
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ActionSetNextPlayer)) {
            return false;
        }
        final ActionSetNextPlayer other = (ActionSetNextPlayer)obj;
        return this.decision == other.decision && this.player == other.player;
    }
    
    @Override
    public String getDescription() {
        return "SetNextPlayer";
    }
    
    @Override
    public String toTurnFormat(final Context context) {
        return "Next P" + this.player;
    }
    
    @Override
    public String toMoveFormat(final Context context) {
        return "(Next Player = P" + this.player + ")";
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
    public int what() {
        return this.player;
    }
    
    @Override
    public int playerSelected() {
        return this.player;
    }
}
