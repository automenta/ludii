// 
// Decompiled by Procyon v0.5.36
// 

package game.equipment.component.tile;

import annotations.Name;
import annotations.Opt;
import game.equipment.component.Component;
import game.rules.play.moves.Moves;
import game.types.board.StepType;
import game.types.play.RoleType;
import main.StringRoutines;
import metadata.graphics.util.ComponentStyleType;

import java.io.Serializable;

public class Domino extends Component implements Serializable
{
    private static final long serialVersionUID = 1L;
    private final int value2;
    
    public Domino(final String name, final RoleType role, @Name final Integer value, @Name final Integer value2, @Opt final Moves generator) {
        super(name, role, new StepType[][] { { StepType.F, StepType.R, StepType.F, StepType.R, StepType.F, StepType.L, StepType.F, StepType.L, StepType.F, StepType.R, StepType.F, StepType.R, StepType.F } }, null, value, generator);
        this.value2 = value2;
        this.nameWithoutNumber = StringRoutines.removeTrailingNumbers(name);
        this.style = ComponentStyleType.Domino;
    }
    
    @Override
    public String toEnglish() {
        return "<Domino>";
    }
    
    protected Domino(final Domino other) {
        super(other);
        this.value2 = other.value2;
    }
    
    @Override
    public Domino clone() {
        return new Domino(this);
    }
    
    @Override
    public int getValue2() {
        return this.value2;
    }
    
    @Override
    public boolean isDoubleDomino() {
        return this.getValue() == this.getValue2();
    }
    
    @Override
    public boolean isDomino() {
        return true;
    }
    
    @Override
    public int numSides() {
        return 4;
    }
    
    @Override
    public boolean isTile() {
        return true;
    }
}
