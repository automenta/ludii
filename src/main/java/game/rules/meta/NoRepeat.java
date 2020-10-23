// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.meta;

import annotations.Opt;
import game.Game;
import game.types.play.RepetitionType;
import util.Context;

public class NoRepeat extends MetaRule
{
    private static final long serialVersionUID = 1L;
    private final RepetitionType type;
    
    public NoRepeat(@Opt final RepetitionType type) {
        this.type = ((type == null) ? RepetitionType.InGame : type);
    }
    
    @Override
    public void eval(final Context context) {
        context.game().setRepetitionType(this.type);
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
    public boolean isStatic() {
        return true;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
}
