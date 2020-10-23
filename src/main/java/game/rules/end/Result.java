// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.end;

import game.Game;
import game.types.play.ResultType;
import game.types.play.RoleType;
import util.BaseLudeme;
import util.Context;

import java.io.Serializable;

public class Result extends BaseLudeme implements Serializable
{
    private static final long serialVersionUID = 1L;
    private final RoleType who;
    private final ResultType result;
    
    public Result(final RoleType who, final ResultType result) {
        this.who = who;
        this.result = result;
    }
    
    public RoleType who() {
        return this.who;
    }
    
    public ResultType result() {
        return this.result;
    }
    
    public void eval(final Context context) {
    }
    
    public long gameFlags(final Game game) {
        return 0L;
    }
    
    public void preprocess(final Game game) {
    }
    
    @Override
    public String toString() {
        return "[Result: " + this.who + " " + this.result + "]";
    }
}
