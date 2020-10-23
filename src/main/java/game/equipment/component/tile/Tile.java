// 
// Decompiled by Procyon v0.5.36
// 

package game.equipment.component.tile;

import annotations.Name;
import annotations.Opt;
import annotations.Or;
import game.equipment.component.Component;
import game.rules.play.moves.Moves;
import game.types.board.StepType;
import game.types.play.RoleType;
import game.util.moves.Flips;
import main.StringRoutines;
import metadata.graphics.util.ComponentStyleType;

import java.io.Serializable;

public class Tile extends Component implements Serializable
{
    private static final long serialVersionUID = 1L;
    private int[] terminus;
    private final Integer numTerminus;
    private final Path[] paths;
    private int numSides;
    private final Flips flips;
    
    public Tile(final String name, @Opt final RoleType role, @Opt @Or final StepType[] walk, @Opt @Or final StepType[][] walks, @Opt @Name final Integer numSides, @Opt @Or @Name final Integer[] slots, @Opt @Or @Name final Integer slotsPerSide, @Opt final Path[] paths, @Opt final Flips flips, @Opt final Moves generator) {
        super(name, (role == null) ? RoleType.Shared : role, (walk != null) ? new StepType[][] { walk } : walks, null, null, generator);
        int numNonNull = 0;
        if (walk != null) {
            ++numNonNull;
        }
        if (walks != null) {
            ++numNonNull;
        }
        if (numNonNull > 1) {
            throw new IllegalArgumentException("Only one of 'walk' and 'wallks' can be specified.");
        }
        numNonNull = 0;
        if (slots != null) {
            ++numNonNull;
        }
        if (slotsPerSide != null) {
            ++numNonNull;
        }
        if (numNonNull > 1) {
            throw new IllegalArgumentException("Zero or one Or parameter can be non-null.");
        }
        if (slots != null) {
            this.terminus = new int[slots.length];
            for (int i = 0; i < this.terminus.length; ++i) {
                this.terminus[i] = slots[i];
            }
        }
        else {
            this.terminus = null;
        }
        this.numTerminus = ((slots == null && slotsPerSide == null) ? Integer.valueOf(1) : slotsPerSide);
        this.paths = paths;
        this.nameWithoutNumber = StringRoutines.removeTrailingNumbers(name);
        if (this.walk() != null) {
            this.style = ComponentStyleType.LargePiece;
        }
        else {
            this.style = ComponentStyleType.Tile;
        }
        this.numSides = ((numSides != null) ? numSides : -1);
        this.flips = flips;
    }
    
    @Override
    public String toEnglish() {
        return "<Tile>";
    }
    
    protected Tile(final Tile other) {
        super(other);
        this.terminus = other.terminus;
        this.numSides = other.numSides;
        if (other.terminus != null) {
            this.terminus = new int[other.terminus.length];
            System.arraycopy(other.terminus, 0, this.terminus, 0, other.terminus.length);
        }
        else {
            this.terminus = null;
        }
        this.numTerminus = other.numTerminus;
        if (other.paths != null) {
            this.paths = new Path[other.paths.length];
            System.arraycopy(other.paths, 0, this.paths, 0, other.paths.length);
        }
        else {
            this.paths = null;
        }
        this.flips = ((other.getFlips() != null) ? new Flips(other.getFlips().flipA(), other.getFlips().flipB()) : null);
    }
    
    @Override
    public Tile clone() {
        return new Tile(this);
    }
    
    @Override
    public boolean isTile() {
        return true;
    }
    
    @Override
    public int[] terminus() {
        return this.terminus;
    }
    
    @Override
    public Integer numTerminus() {
        return this.numTerminus;
    }
    
    @Override
    public Path[] paths() {
        return this.paths;
    }
    
    @Override
    public int numSides() {
        return this.numSides;
    }
    
    @Override
    public void setNumSides(final int numSides) {
        this.numSides = numSides;
    }
    
    @Override
    public Flips getFlips() {
        return this.flips;
    }
}
