// 
// Decompiled by Procyon v0.5.36
// 

package game.util.moves;

import annotations.Name;
import annotations.Opt;
import annotations.Or;
import game.functions.ints.IntFunction;
import game.functions.ints.board.Id;
import util.BaseLudeme;

public class Piece extends BaseLudeme
{
    private final IntFunction component;
    private final IntFunction[] components;
    private final IntFunction state;
    private final String name;
    private final String[] names;
    
    public Piece(@Or final String nameComponent, @Or final IntFunction component, @Or final String[] nameComponents, @Or final IntFunction[] components, @Opt @Name final IntFunction state) {
        int numNonNull = 0;
        if (component != null) {
            ++numNonNull;
        }
        if (components != null) {
            ++numNonNull;
        }
        if (nameComponent != null) {
            ++numNonNull;
        }
        if (nameComponents != null) {
            ++numNonNull;
        }
        if (numNonNull != 1) {
            throw new IllegalArgumentException("Piece(): One nameComponent, component, nameComponents or components parameter must be non-null.");
        }
        this.name = nameComponent;
        this.names = nameComponents;
        this.component = ((nameComponent != null) ? new Id(nameComponent, null) : component);
        if (nameComponents != null) {
            this.components = new IntFunction[nameComponents.length];
            for (int i = 0; i < nameComponents.length; ++i) {
                this.components[i] = new Id(nameComponents[i], null);
            }
        }
        else {
            this.components = components;
        }
        this.state = state;
    }
    
    public IntFunction state() {
        return this.state;
    }
    
    public IntFunction component() {
        return this.component;
    }
    
    public IntFunction[] components() {
        return this.components;
    }
    
    public String nameComponent() {
        return this.name;
    }
    
    public String[] nameComponents() {
        return this.names;
    }
}
