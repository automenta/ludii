// 
// Decompiled by Procyon v0.5.36
// 

package util.action.others;

import util.Context;
import util.action.Action;
import util.action.ActionType;
import util.action.BaseAction;

public final class ActionNote extends BaseAction
{
    private static final long serialVersionUID = 1L;
    private final String message;
    private final int player;
    
    public ActionNote(final String message, final int player) {
        this.message = message;
        this.player = player;
    }
    
    public ActionNote(final String detailedString) {
        assert detailedString.startsWith("[Note:");
        final String strMessage = Action.extractData(detailedString, "message");
        this.message = strMessage;
        final String strPlayer = Action.extractData(detailedString, "to");
        this.player = Integer.parseInt(strPlayer);
        final String strDecision = Action.extractData(detailedString, "decision");
        this.decision = (!strDecision.isEmpty() && Boolean.parseBoolean(strDecision));
    }
    
    @Override
    public Action apply(final Context context, final boolean store) {
        context.state().addNote(context.trial().moveNumber(), this.player, this.message);
        return this;
    }
    
    @Override
    public String toTrialFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        sb.append("[Note:");
        sb.append("message=" + this.message);
        sb.append(",to=" + this.player);
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
        result = 31 * result + this.message.hashCode();
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ActionNote)) {
            return false;
        }
        final ActionNote other = (ActionNote)obj;
        return this.decision == other.decision && this.message.equals(other.message) && this.player == other.player;
    }
    
    @Override
    public String getDescription() {
        return "Note";
    }
    
    @Override
    public String toTurnFormat(final Context context) {
        return "Note P" + this.player + " \"" + this.message + "\"";
    }
    
    @Override
    public String toMoveFormat(final Context context) {
        return "(Note \"" + this.message + "\" to " + this.player + ")";
    }
    
    @Override
    public int who() {
        return this.player;
    }
    
    @Override
    public String message() {
        return this.message;
    }
    
    @Override
    public ActionType actionType() {
        return ActionType.Note;
    }
}
