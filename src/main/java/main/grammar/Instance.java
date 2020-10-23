// 
// Decompiled by Procyon v0.5.36
// 

package main.grammar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Instance
{
    private final Symbol symbol;
    private List<Clause> clauses;
    private Object object;
    
    public Instance(final Symbol symbol, final List<Clause> clauses) {
        this.clauses = null;
        this.object = null;
        this.symbol = symbol;
        this.clauses = clauses;
        this.object = null;
    }
    
    public Instance(final Symbol symbol, final Object object) {
        this.clauses = null;
        this.object = null;
        this.symbol = symbol;
        this.clauses = null;
        this.object = object;
    }
    
    public Symbol symbol() {
        return this.symbol;
    }
    
    public List<Clause> clauses() {
        if (this.clauses == null) {
            return null;
        }
        return Collections.unmodifiableList(this.clauses);
    }
    
    public void setClauses(final List<Clause> list) {
        (this.clauses = new ArrayList<>()).addAll(list);
    }
    
    public Object object() {
        return this.object;
    }
    
    public void setObject(final Object object) {
        this.object = object;
    }
    
    public Class<?> cls() {
        if (this.symbol.cls() == null) {
            System.out.println("** Instance: null symbol.cls() for symbol " + this.symbol.name() + ".");
        }
        return this.symbol.cls();
    }
    
    @Override
    public String toString() {
        if (this.symbol == null) {
            return "Unknown";
        }
        return this.symbol.grammarLabel() + ((this.cls() == null) ? " (null)" : (", cls: " + this.cls().getName())) + ((this.object == null) ? " (null)" : (", object: " + this.object));
    }
}
