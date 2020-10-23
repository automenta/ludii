// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.end;

import annotations.Hide;
import annotations.Opt;
import game.Game;
import util.Context;

@Hide
public class BaseEndRule extends EndRule
{
    private static final long serialVersionUID = 1L;
    
    public BaseEndRule(@Opt final Result result) {
        super(result);
    }
    
    @Override
    public EndRule eval(final Context context) {
        return null;
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
}
