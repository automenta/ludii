// 
// Decompiled by Procyon v0.5.36
// 

package util.action.others;

import game.functions.booleans.BooleanConstant;
import game.rules.end.End;
import game.rules.end.If;
import game.rules.end.Result;
import game.types.play.ResultType;
import game.types.play.RoleType;
import util.Context;
import util.action.Action;
import util.action.ActionType;
import util.action.BaseAction;

public final class ActionForfeit extends BaseAction
{
    private static final long serialVersionUID = 1L;
    private final RoleType player;
    
    public ActionForfeit(final RoleType player) {
        this.player = player;
    }
    
    public ActionForfeit(final String detailedString) {
        assert detailedString.startsWith("[Forfeit:");
        final String strPlayer = Action.extractData(detailedString, "player");
        this.player = RoleType.valueOf(strPlayer);
        this.decision = true;
    }
    
    @Override
    public Action apply(final Context context, final boolean store) {
        new End(new If(BooleanConstant.construct(true), null, null, new Result(this.player, ResultType.Loss)), null).eval(context);
        return this;
    }
    
    @Override
    public String toTrialFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        sb.append("[Forfeit:");
        sb.append("player=" + this.player);
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
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ActionForfeit)) {
            return false;
        }
        final ActionForfeit other = (ActionForfeit)obj;
        return this.player.equals(other.player);
    }
    
    @Override
    public String toTurnFormat(final Context context) {
        return "Forfeit " + this.player;
    }
    
    @Override
    public String toMoveFormat(final Context context) {
        return "(Forfeit " + this.player + ")";
    }
    
    @Override
    public String getDescription() {
        return "Forfeit";
    }
    
    @Override
    public ActionType actionType() {
        return ActionType.Forfeit;
    }
    
    @Override
    public boolean isForfeit() {
        return true;
    }
}
