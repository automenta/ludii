// 
// Decompiled by Procyon v0.5.36
// 

package game.equipment.component;

import annotations.Name;
import annotations.Opt;
import game.rules.play.moves.Moves;
import game.types.play.RoleType;
import game.util.directions.DirectionFacing;
import game.util.moves.Flips;
import main.StringRoutines;

import java.io.Serializable;

public class Piece extends Component implements Serializable
{
    private static final long serialVersionUID = 1L;
    private final Flips flips;
    
    public Piece(final String name, @Opt final RoleType role, @Opt final DirectionFacing dirn, @Opt @Name final Integer value, @Opt final Flips flips, @Opt final Moves generator) {
        super(name, (role == null) ? RoleType.Each : role, null, dirn, value, generator);
        this.nameWithoutNumber = StringRoutines.removeTrailingNumbers(name);
        this.flips = flips;
    }
    
    protected Piece(final Piece other) {
        super(other);
        this.flips = ((other.getFlips() != null) ? new Flips(other.getFlips().flipA(), other.getFlips().flipB()) : null);
    }
    
    @Override
    public Piece clone() {
        return new Piece(this);
    }
    
    @Override
    public String toEnglish() {
        return "<Piece>";
    }
    
    @Override
    public Flips getFlips() {
        return this.flips;
    }
}
