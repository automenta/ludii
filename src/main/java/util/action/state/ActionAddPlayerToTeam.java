// 
// Decompiled by Procyon v0.5.36
// 

package util.action.state;

import util.Context;
import util.action.Action;
import util.action.ActionType;
import util.action.BaseAction;

public final class ActionAddPlayerToTeam extends BaseAction
{
    private static final long serialVersionUID = 1L;
    private final int team;
    private final int player;
    
    public ActionAddPlayerToTeam(final int team, final int player) {
        this.team = team;
        this.player = player;
    }
    
    public ActionAddPlayerToTeam(final String detailedString) {
        assert detailedString.startsWith("[AddPlayerToTeam:");
        final String strPlayer = Action.extractData(detailedString, "player");
        this.player = Integer.parseInt(strPlayer);
        final String strTeam = Action.extractData(detailedString, "team");
        this.team = Integer.parseInt(strTeam);
        final String strDecision = Action.extractData(detailedString, "decision");
        this.decision = (!strDecision.isEmpty() && Boolean.parseBoolean(strDecision));
    }
    
    @Override
    public Action apply(final Context context, final boolean store) {
        context.state().setPlayerToTeam(this.player, this.team);
        return this;
    }
    
    @Override
    public String toTrialFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        sb.append("[AddPlayerToTeam:");
        sb.append("team=" + this.team);
        sb.append(",player=" + this.player);
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
        result = 31 * result + this.player;
        result = 31 * result + this.team;
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ActionAddPlayerToTeam)) {
            return false;
        }
        final ActionAddPlayerToTeam other = (ActionAddPlayerToTeam)obj;
        return this.team == other.team && this.player == other.player;
    }
    
    @Override
    public String getDescription() {
        return "AddPlayerToTeam";
    }
    
    @Override
    public String toTurnFormat(final Context context) {
        return "Team" + this.team + " + P" + this.player;
    }
    
    @Override
    public String toMoveFormat(final Context context) {
        return "(Add P" + this.player + " to Team" + this.team + ")";
    }
    
    @Override
    public ActionType actionType() {
        return ActionType.AddPlayerToTeam;
    }
}
