// 
// Decompiled by Procyon v0.5.36
// 

package game.equipment.component.tile;

import annotations.Name;
import annotations.Opt;
import util.BaseLudeme;

import java.io.Serializable;

public final class Path extends BaseLudeme implements Serializable
{
    private static final long serialVersionUID = 1L;
    private final Integer from;
    private final Integer slotsFrom;
    private final Integer to;
    private final Integer slotsTo;
    private final Integer colour;
    
    public Path(@Name final Integer from, @Name @Opt final Integer slotsFrom, @Name final Integer to, @Name @Opt final Integer slotsTo, @Name final Integer colour) {
        this.from = from;
        this.slotsFrom = ((slotsFrom == null) ? Integer.valueOf(0) : slotsFrom);
        this.to = to;
        this.slotsTo = ((slotsTo == null) ? Integer.valueOf(0) : slotsTo);
        this.colour = colour;
    }
    
    @Override
    public String toEnglish() {
        return "(Path)";
    }
    
    public Integer side1() {
        return this.from;
    }
    
    public Integer side2() {
        return this.to;
    }
    
    public Integer terminus1() {
        return this.slotsFrom;
    }
    
    public Integer terminus2() {
        return this.slotsTo;
    }
    
    public Integer colour() {
        return this.colour;
    }
    
    public int side1(final int rotation, final int maxOrthoRotation) {
        return (this.from + rotation) % maxOrthoRotation;
    }
    
    public int side2(final int rotation, final int maxOrthoRotation) {
        return (this.to + rotation) % maxOrthoRotation;
    }
}
