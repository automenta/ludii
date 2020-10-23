// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.dice;

import game.Game;
import game.equipment.component.Component;
import game.functions.ints.BaseIntFunction;
import game.functions.ints.IntFunction;
import util.Context;
import util.state.containerState.ContainerState;

public final class Face extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction locn;
    
    public Face(final IntFunction locn) {
        this.locn = locn;
    }
    
    @Override
    public int eval(final Context context) {
        final int loc = this.locn.eval(context);
        if (loc == -1) {
            return -1;
        }
        final int containerId = context.containerId()[loc];
        final ContainerState cs = context.state().containerStates()[containerId];
        final int what = cs.whatCell(loc);
        if (what < 1) {
            return -1;
        }
        final Component component = context.components()[what];
        if (!component.isDie()) {
            return -1;
        }
        final int state = cs.stateCell(loc);
        if (state < 0) {
            return -1;
        }
        return component.getFaces()[state];
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        final long stateFlag = this.locn.gameFlags(game);
        return stateFlag;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.locn.preprocess(game);
    }
}
