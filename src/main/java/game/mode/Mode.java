// 
// Decompiled by Procyon v0.5.36
// 

package game.mode;

import annotations.Opt;
import game.types.play.ModeType;
import util.BaseLudeme;
import util.model.AlternatingMove;
import util.model.Model;
import util.model.SimulationMove;
import util.model.SimultaneousMove;
import util.playout.Playout;

import java.io.Serializable;

public final class Mode extends BaseLudeme implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected ModeType mode;
    protected Playout playout;
    
    public Mode(@Opt final ModeType mode) {
        this.mode = ((mode == null) ? ModeType.Alternating : mode);
    }
    
    public ModeType mode() {
        return this.mode;
    }
    
    public void setMode(final ModeType modeType) {
        this.mode = modeType;
    }
    
    public Playout playout() {
        return this.playout;
    }
    
    public void setPlayout(final Playout newPlayout) {
        this.playout = newPlayout;
    }
    
    public Model createModel() {
        Model model = null;
        switch (this.mode) {
            case Alternating: {
                model = new AlternatingMove();
                break;
            }
            case Simultaneous: {
                model = new SimultaneousMove();
                break;
            }
            case Simulation: {
                model = new SimulationMove();
                break;
            }
            default: {
                model = null;
                break;
            }
        }
        return model;
    }
}
