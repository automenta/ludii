// 
// Decompiled by Procyon v0.5.36
// 

package game.equipment.component;

import annotations.Name;
import annotations.Opt;
import game.rules.play.moves.Moves;
import game.types.play.RoleType;
import game.util.directions.DirectionFacing;
import metadata.graphics.util.ComponentStyleType;
import util.Context;

import java.io.Serializable;

public class Die extends Component implements Serializable
{
    private static final long serialVersionUID = 1L;
    private final int numFaces;
    private int[] faces;
    
    public Die(final String name, final RoleType role, @Name final Integer numFaces, @Opt final DirectionFacing dirn, @Opt final Integer value, @Opt final Moves generator) {
        super(name, role, null, dirn, value, generator);
        this.numFaces = numFaces;
        this.style = ComponentStyleType.Die;
    }
    
    protected Die(final Die other) {
        super(other);
        this.numFaces = other.numFaces;
        if (other.faces != null) {
            this.faces = new int[other.faces.length];
            System.arraycopy(other.faces, 0, this.faces, 0, other.faces.length);
        }
        else {
            other.faces = null;
        }
    }
    
    @Override
    public Die clone() {
        return new Die(this);
    }
    
    @Override
    public boolean isDie() {
        return true;
    }
    
    @Override
    public int[] getFaces() {
        return this.faces;
    }
    
    @Override
    public int getNumFaces() {
        return this.numFaces;
    }
    
    @Override
    public int roll(final Context context) {
        return context.rng().nextInt(this.faces.length);
    }
    
    @Override
    public void setFaces(final Integer[] faces, final Integer start) {
        if (start != null) {
            this.faces = new int[this.numFaces];
            for (int i = start; i < start + this.numFaces; ++i) {
                this.faces[i - start] = i;
            }
        }
        else if (faces != null) {
            this.faces = new int[faces.length];
            for (int i = 0; i < faces.length; ++i) {
                this.faces[i] = faces[i];
            }
        }
    }
}
