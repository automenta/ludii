// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans.math;

import game.Game;
import game.functions.booleans.BaseBooleanFunction;
import game.functions.booleans.BooleanFunction;
import util.Context;
import util.locations.Location;

import java.util.ArrayList;
import java.util.List;

public final class Or extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    private final BooleanFunction[] list;
    private Boolean precomputedBoolean;
    
    public Or(final BooleanFunction a, final BooleanFunction b) {
        this.list = new BooleanFunction[] { a, b };
    }
    
    public Or(final BooleanFunction[] list) {
        this.list = list;
    }
    
    @Override
    public boolean eval(final Context context) {
        if (this.precomputedBoolean != null) {
            return this.precomputedBoolean;
        }
        for (final BooleanFunction elem : this.list) {
            if (elem.eval(context)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean isStatic() {
        for (final BooleanFunction elem : this.list) {
            if (!elem.isStatic()) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = 0L;
        for (final BooleanFunction elem : this.list) {
            gameFlags |= elem.gameFlags(game);
        }
        return gameFlags;
    }
    
    @Override
    public void preprocess(final Game game) {
        for (final BooleanFunction elem : this.list) {
            elem.preprocess(game);
        }
        if (this.isStatic()) {
            this.precomputedBoolean = this.eval(new Context(game, null));
        }
    }
    
    public BooleanFunction[] list() {
        return this.list;
    }
    
    @Override
    public List<Location> satisfyingSites(final Context context) {
        if (!this.eval(context)) {
            return new ArrayList<>();
        }
        for (final BooleanFunction cond : this.list) {
            if (cond.eval(context)) {
                return cond.satisfyingSites(context);
            }
        }
        return new ArrayList<>();
    }
}
