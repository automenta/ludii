// 
// Decompiled by Procyon v0.5.36
// 

package language.compiler;

import language.grammar.Grammar;
import main.grammar.Instance;
import main.grammar.Report;
import main.grammar.Token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Arg
{
    protected String symbolName;
    protected String parameterName;
    protected final List<Instance> instances;
    
    public Arg(final String symbolName, final String parameterName) {
        this.symbolName = null;
        this.parameterName = null;
        this.instances = new ArrayList<>();
        this.symbolName = ((symbolName == null) ? null : symbolName);
        this.parameterName = ((parameterName == null) ? null : parameterName);
    }
    
    public String symbolName() {
        return this.symbolName;
    }
    
    public String parameterName() {
        return this.parameterName;
    }
    
    public List<Instance> instances() {
        return Collections.unmodifiableList(this.instances);
    }
    
    public static Arg createFromToken(final Grammar grammar, final Token token) {
        Arg arg = null;
        switch (token.type()) {
            case Terminal: {
                return new ArgTerminal(token.name(), token.parameterLabel());
            }
            case Class: {
                arg = new ArgClass(token.name(), token.parameterLabel());
                for (final Token sub : token.arguments()) {
                    ((ArgClass)arg).add(createFromToken(grammar, sub));
                }
                break;
            }
            case Array: {
                arg = new ArgArray(token.name(), token.parameterLabel());
                for (final Token sub : token.arguments()) {
                    ((ArgArray)arg).add(createFromToken(grammar, sub));
                }
                break;
            }
            default: {
                return null;
            }
        }
        return arg;
    }
    
    public abstract boolean matchSymbols(final Grammar grammar, final Report report);
    
    public abstract Object compile(final Class<?> expected, final int depth);
}
