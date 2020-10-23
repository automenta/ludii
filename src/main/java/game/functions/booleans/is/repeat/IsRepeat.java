// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans.is.repeat;

import annotations.Hide;
import annotations.Opt;
import game.Game;
import game.functions.booleans.BaseBooleanFunction;
import game.types.play.RepetitionType;
import util.Context;

@Hide
public final class IsRepeat extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    private final RepetitionType type;
    
    public IsRepeat(@Opt final RepetitionType type) {
        this.type = ((type == null) ? RepetitionType.InGame : type);
    }
    
    @Override
    public boolean eval(final Context context) {
        final long hashState = context.state().stateHash();
        switch (this.type) {
            case InTurn: {
                return context.trial().previousStateWithinATurn().contains(hashState);
            }
            case InGame: {
                return context.trial().previousState().contains(hashState);
            }
            case Situational: {
                return context.trial().previousState().contains(hashState);
            }
            default: {
                return false;
            }
        }
    }
    
    @Override
    public String toString() {
        return "IsRepeat(" + this.type + ")";
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = 0L;
        if (this.type == RepetitionType.Situational || this.type == RepetitionType.InGame) {
            gameFlags |= 0x20000000000L;
        }
        if (this.type == RepetitionType.InTurn) {
            gameFlags |= 0x40000000000L;
        }
        return gameFlags;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
}
