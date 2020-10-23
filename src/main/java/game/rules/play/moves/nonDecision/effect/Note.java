// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect;

import annotations.Name;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.functions.ints.IntFunction;
import game.functions.ints.board.Id;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.types.play.RoleType;
import game.util.moves.Player;
import util.Context;
import util.Move;
import util.action.others.ActionNote;

public final class Note extends Effect
{
    private static final long serialVersionUID = 1L;
    final IntFunction playerMessage;
    final IntFunction playerFn;
    final RoleType role;
    final String message;
    
    public Note(@Opt @Or final IntFunction playerMessage, @Opt @Or final RoleType roleMessage, final String message, @Opt @Or @Name final Player to, @Opt @Or @Name final RoleType To) {
        super(null);
        int numNonNull = 0;
        if (roleMessage != null) {
            ++numNonNull;
        }
        if (playerMessage != null) {
            ++numNonNull;
        }
        if (numNonNull > 1) {
            throw new IllegalArgumentException("Note(): Only one 'playerMessage' or 'roleMessage' parameters can be non-null.");
        }
        numNonNull = 0;
        if (to != null) {
            ++numNonNull;
        }
        if (To != null) {
            ++numNonNull;
        }
        if (numNonNull > 1) {
            throw new IllegalArgumentException("Note(): Only one 'to' or 'role' parameters can be non-null.");
        }
        this.playerFn = ((to != null) ? to.index() : ((To != null) ? new Id(null, To) : new Id(null, RoleType.All)));
        this.role = ((to == null && To == null) ? RoleType.All : To);
        this.message = message;
        this.playerMessage = ((roleMessage != null) ? new Id(null, roleMessage) : playerMessage);
    }
    
    @Override
    public Moves eval(final Context context) {
        final BaseMoves moves = new BaseMoves(super.then());
        final String messageToSend = (this.playerMessage == null) ? this.message : ("P" + this.playerMessage.eval(context) + " " + this.message);
        if (this.role == RoleType.All) {
            for (int i = 1; i < context.game().players().size(); ++i) {
                final Move move = new Move(new ActionNote(messageToSend, i));
                moves.moves().add(move);
            }
        }
        else {
            final int pid = this.playerFn.eval(context);
            if (pid > 0 && pid < context.game().players().size()) {
                final Move move = new Move(new ActionNote(messageToSend, pid));
                moves.moves().add(move);
            }
        }
        return moves;
    }
    
    @Override
    public boolean canMove(final Context context) {
        return false;
    }
    
    @Override
    public boolean canMoveTo(final Context context, final int target) {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long flags = super.gameFlags(game) | 0x100000000L | this.playerFn.gameFlags(game);
        if (this.playerMessage != null) {
            flags |= this.playerMessage.gameFlags(game);
        }
        return flags;
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public void preprocess(final Game game) {
        super.preprocess(game);
        this.playerFn.preprocess(game);
        if (this.playerMessage != null) {
            this.playerMessage.preprocess(game);
        }
    }
    
    @Override
    public String toEnglish() {
        return "Note";
    }
}
