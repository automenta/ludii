// 
// Decompiled by Procyon v0.5.36
// 

package language.compiler;

import language.compiler.exceptions.CompilerException;
import language.compiler.exceptions.UnknownArrayErrorException;
import language.grammar.Grammar;
import grammar.Report;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArgArray extends Arg
{
    private final List<Arg> elements;
    
    public ArgArray(final String name, final String label) {
        super(name, label);
        this.elements = new ArrayList<>();
    }
    
    public List<Arg> elements() {
        return Collections.unmodifiableList(this.elements);
    }
    
    public void add(final Arg arg) {
        this.elements.add(arg);
    }
    
    @Override
    public boolean matchSymbols(final Grammar grammar, final Report report) {
        for (final Arg arg : this.elements) {
            if (!arg.matchSymbols(grammar, report)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public Object compile(final Class<?> expected, final int depth) {
        String pre = "";
        for (int n = 0; n < depth; ++n) {
            pre += ". ";
        }
        pre += "[]: ";
        if (depth != -1) {
            System.out.println("\n" + pre + "[][][][][][][][][][][][][][][][][][][][][][][][][][]");
            System.out.println(pre + "Compiling ArgArray (expected=" + expected.getName() + "):");
        }
        final Class<?> elementType = expected.getComponentType();
        if (depth != -1) {
            System.out.println(pre + "Element type is: " + elementType);
        }
        if (elementType == null) {
            return null;
        }
        Object objs = null;
        try {
            objs = Array.newInstance(elementType, this.elements.size());
            for (int i = 0; i < this.elements.size(); ++i) {
                final Arg elem = this.elements.get(i);
                final Object match = elem.compile(elementType, (depth == -1) ? -1 : (depth + 1));
                if (match == null) {
                    return null;
                }
                Array.set(objs, i, match);
            }
        }
        catch (CompilerException e) {
            throw e;
        }
        catch (Exception e2) {
            e2.printStackTrace();
            throw new UnknownArrayErrorException(expected.getName(), e2.getMessage());
        }
        final Object[] array = (Object[])objs;
        if (depth != -1) {
            System.out.println(pre + "+ Array okay, " + array.length + " elements matched.");
        }
        return array;
    }
    
    @Override
    public String toString() {
        String str = "";
        str += "{ ";
        for (final Arg arg : this.elements) {
            str = str + arg.toString() + " ";
        }
        str += "}";
        return str;
    }
}
