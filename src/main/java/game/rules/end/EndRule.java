// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.end;

import annotations.Opt;
import game.Game;
import util.BaseLudeme;
import util.Context;

import java.io.Serializable;

public abstract class EndRule extends BaseLudeme implements Serializable
{
    private static final long serialVersionUID = 1L;
    private Result result;
    
    public EndRule(@Opt final Result result) {
        this.result = null;
        this.result = result;
    }
    
    public Result result() {
        return this.result;
    }
    
    public void setResult(final Result rslt) {
        this.result = rslt;
    }
    
    public abstract EndRule eval(final Context context);
    
    public abstract long gameFlags(final Game game);
    
    public abstract void preprocess(final Game game);
    
    @Override
    public String toEnglish() {
        return "<EndRule>";
    }
}
