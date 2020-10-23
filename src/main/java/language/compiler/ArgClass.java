// 
// Decompiled by Procyon v0.5.36
// 

package language.compiler;

import annotations.Hide;
import language.compiler.exceptions.BadKeywordException;
import language.compiler.exceptions.BadSymbolException;
import language.compiler.exceptions.BadSyntaxException;
import language.compiler.exceptions.ListNotSupportedException;
import language.grammar.Grammar;
import main.StringRoutines;
import main.grammar.Instance;
import main.grammar.Report;
import main.grammar.Symbol;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

public class ArgClass extends Arg
{
    private final List<Arg> argsIn;
    
    public ArgClass(final String name, final String label) {
        super(name, label);
        this.argsIn = new ArrayList<>();
    }
    
    public List<Arg> argsIn() {
        return Collections.unmodifiableList(this.argsIn);
    }
    
    public void add(final Arg arg) {
        this.argsIn.add(arg);
    }
    
    @Override
    public boolean matchSymbols(final Grammar grammar, final Report report) {
        final char initial = this.symbolName.charAt(0);
        if (Character.isAlphabetic(initial) && !Character.isLowerCase(initial)) {
            throw new BadKeywordException(this.symbolName, "Class names should be lowercase.");
        }
        for (final Arg arg : this.argsIn) {
            arg.matchSymbols(grammar, report);
        }
        final String name = StringRoutines.upperCaseInitial(this.symbolName);
        final List<Symbol> existing = grammar.symbolListFromClassName(name);
        if (existing == null) {
            throw new BadKeywordException(name, null);
        }
        final List<Symbol> symbols = new ArrayList<>(existing);
        this.instances.clear();
        for (final Symbol symbol : symbols) {
            if (symbol == null) {
                throw new BadSymbolException(this.symbolName);
            }
            final Class<?> cls = loadClass(symbol);
            if (cls == null) {
                continue;
            }
            this.instances.add(new Instance(symbol, null));
        }
        return true;
    }
    
    private static Class<?> loadClass(final Symbol symbol) {
        Class<?> cls = null;
        try {
            cls = Class.forName(symbol.path());
        }
        catch (ClassNotFoundException ex) {}
        if (cls == null && symbol.type() != Symbol.SymbolType.Constant) {
            final Exception e = new Exception("Couldn't load ArgClass " + symbol.path() + ".");
            e.printStackTrace();
        }
        return cls;
    }
    
    @Override
    public Object compile(final Class<?> expected, final int depth) {
        String pre = "";
        for (int n = 0; n < depth; ++n) {
            pre += ". ";
        }
        pre += "C: ";
        if (depth != -1) {
            System.out.println("\n" + pre + "==========================================");
            System.out.println(pre + "Compiling ArgClass: " + this.symbolName);
            System.out.println(pre + "\n" + pre + "Expected: name=" + expected.getName() + ", type=" + expected.getTypeName() + ".");
        }
        if (expected.getName().contains("[L")) {
            return null;
        }
        if (depth != -1) {
            System.out.println(pre + this.instances.size() + " instances:");
        }
        for (int inst = 0; inst < this.instances.size(); ++inst) {
            final Instance instance = this.instances.get(inst);
            if (depth != -1) {
                System.out.println(pre + "-- instance " + inst + ": " + instance);
            }
            final Class<?> cls = instance.cls();
            if (cls != null) {
                if (expected.isArray()) {
                    final Class<?> elementType = expected.getComponentType();
                    if (!elementType.isAssignableFrom(cls)) {
                        if (depth != -1) {
                            System.out.println(pre + "Skipping non-assignable class " + cls.getName() + " (in array).");
                        }
                        continue;
                    }
                }
                else if (!expected.isAssignableFrom(cls)) {
                    if (depth != -1) {
                        System.out.println(pre + "Skipping non-assignable class " + cls.getName() + ".");
                    }
                    continue;
                }
                if (cls.getAnnotation(Hide.class) == null) {
                    Object object = null;
                    if (depth != -1) {
                        System.out.println(pre + "\n" + pre + "Constructing: " + cls + "...");
                    }
                    for (final boolean tryConstructors : new boolean[] { false, true }) {
                        final List<Executable> executables = new ArrayList<>();
                        if (tryConstructors) {
                            executables.addAll(Arrays.asList(cls.getDeclaredConstructors()));
                        }
                        else {
                            final Method[] declaredMethods;
                            final Method[] methods = declaredMethods = cls.getDeclaredMethods();
                            for (final Method method : declaredMethods) {
                                if (method.getName().equals("construct") && Modifier.isStatic(method.getModifiers())) {
                                    executables.add(method);
                                }
                            }
                        }
                        if (depth != -1) {
                            System.out.println(pre + executables.size() + " constructors found.");
                        }
                        for (int c = 0; c < executables.size(); ++c) {
                            final Executable exec = executables.get(c);
                            if (depth != -1) {
                                System.out.println(pre + "\n" + pre + "Constructor " + c + ": " + exec.toString());
                            }
                            if (exec.getAnnotation(Hide.class) == null) {
                                Parameter[] params = null;
                                Class<?>[] types = null;
                                Annotation[][] annos = null;
                                try {
                                    params = exec.getParameters();
                                    types = exec.getParameterTypes();
                                    annos = exec.getParameterAnnotations();
                                }
                                catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                final int numSlots = params.length;
                                if (numSlots < this.argsIn.size()) {
                                    if (depth != -1) {
                                        System.out.println(pre + "Not enough args in constructor for " + this.argsIn.size() + " input args.");
                                    }
                                }
                                else {
                                    if (numSlots == 0) {
                                        try {
                                            if (tryConstructors) {
                                                object = ((Constructor)exec).newInstance();
                                            }
                                            else {
                                                object = ((Method)exec).invoke(null);
                                            }
                                        }
                                        catch (Exception e2) {
                                            if (depth != -1) {
                                                System.out.println(pre + "*********************");
                                                System.out.println(pre + "Failed to create new instance (no args).");
                                                System.out.println(pre + "*********************\n");
                                            }
                                            e2.printStackTrace();
                                        }
                                        if (object != null) {
                                            break;
                                        }
                                    }
                                    final String[] name = new String[numSlots];
                                    int numOptional = 0;
                                    final BitSet isOptional = new BitSet();
                                    for (int a = 0; a < numSlots; ++a) {
                                        name[a] = null;
                                        if (depth != -1) {
                                            System.out.print(pre + "- con arg " + a + ": " + types[a].getName());
                                        }
                                        if (types[a].getName().equals("java.util.List")) {
                                            throw new ListNotSupportedException();
                                        }
                                        for (int b = 0; b < annos[a].length; ++b) {
                                            if (annos[a][b].toString().equals("@annotations.Opt()") || annos[a][b].toString().equals("@annotations.Or()") || annos[a][b].toString().equals("@annotations.Or2()")) {
                                                isOptional.set(a, true);
                                                ++numOptional;
                                                if (depth != -1) {
                                                    System.out.print(" [Opt] (or an Or)");
                                                }
                                            }
                                            else if (annos[a][b].toString().equals("@annotations.Name()")) {
                                                name[a] = params[a].getName();
                                                if (Character.isUpperCase(name[a].charAt(0))) {
                                                    name[a] = Character.toLowerCase(name[a].charAt(0)) + name[a].substring(1);
                                                }
                                                if (depth != -1) {
                                                    System.out.print(" [name=" + name[a] + "]");
                                                }
                                            }
                                        }
                                        if (depth != -1) {
                                            System.out.println();
                                        }
                                    }
                                    if (this.argsIn.size() < numSlots - numOptional) {
                                        if (depth != -1) {
                                            System.out.println(pre + "Not enough input args (" + this.argsIn.size() + ") for non-optional constructor args (" + (numSlots - numOptional) + ").");
                                        }
                                    }
                                    else {
                                        final Object[] argObjects = new Object[numSlots];
                                        final List<List<Arg>> combos = argCombos(this.argsIn, numSlots);
                                        for (int cmb = 0; cmb < combos.size(); ++cmb) {
                                            final List<Arg> combo = combos.get(cmb);
                                            if (depth != -1) {
                                                System.out.print(pre);
                                                int count = 0;
                                                for (int n2 = 0; n2 < combo.size(); ++n2) {
                                                    final Arg arg = combo.get(n2);
                                                    System.out.print(((arg == null) ? "-" : Character.valueOf((char)(65 + count))) + " ");
                                                    if (arg != null) {
                                                        ++count;
                                                    }
                                                }
                                                System.out.println();
                                            }
                                            int slot;
                                            for (slot = 0; slot < numSlots && (combo.get(slot) != null || isOptional.get(slot)); ++slot) {}
                                            if (slot >= numSlots) {
                                                for (slot = 0; slot < numSlots; ++slot) {
                                                    argObjects[slot] = null;
                                                    final Arg argIn = combo.get(slot);
                                                    if (depth != -1) {
                                                        System.out.print(pre + "argIn " + slot + ": ");
                                                        System.out.println((argIn == null) ? "null" : (argIn.symbolName() + " (" + argIn.getClass().getName() + ")" + "."));
                                                    }
                                                    if (depth != -1 && argIn != null && argIn.parameterName() != null) {
                                                        System.out.println(pre + "argIn has parameterName: " + argIn.parameterName());
                                                    }
                                                    if (argIn == null) {
                                                        if (!isOptional.get(slot)) {
                                                            break;
                                                        }
                                                    }
                                                    else if (name[slot] != null && (argIn.parameterName() == null || !argIn.parameterName().equals(name[slot]))) {
                                                        if (depth != -1) {
                                                            System.out.println(pre + "- Named arg '" + name[slot] + "' in constructor does not match argIn parameterName '" + argIn.parameterName() + "'.");
                                                            break;
                                                        }
                                                        break;
                                                    }
                                                    else if (argIn.parameterName() != null && (name[slot] == null || !argIn.parameterName().equals(name[slot]))) {
                                                        if (depth != -1) {
                                                            System.out.println(pre + "- Named argIn '" + argIn.parameterName() + "' does not match parameter constructor arg label '" + name[slot] + "'.");
                                                            break;
                                                        }
                                                        break;
                                                    }
                                                    else {
                                                        final Object match = argIn.compile(types[slot], (depth == -1) ? -1 : (depth + 1));
                                                        if (match == null) {
                                                            if (depth != -1) {
                                                                System.out.println(pre + "- Arg '" + argIn.toString() + "' doesn't match '" + types[slot] + ".");
                                                                break;
                                                            }
                                                            break;
                                                        }
                                                        else {
                                                            argObjects[slot] = match;
                                                            if (depth != -1) {
                                                                System.out.println(pre + "arg " + slot + " corresponds to " + argIn + ",");
                                                                System.out.println(pre + "  returned match " + match + " for expected " + types[slot]);
                                                            }
                                                        }
                                                    }
                                                }
                                                if (slot >= numSlots) {
                                                    if (depth != -1) {
                                                        System.out.println(pre + "++ Matched all input args.");
                                                    }
                                                    if (depth != -1) {
                                                        System.out.println(pre + "   Trying to create instance of " + exec.getName() + " with " + argObjects.length + " args:");
                                                        for (int o = 0; o < argObjects.length; ++o) {
                                                            System.out.println(pre + "   - argObject " + o + ": " + ((argObjects[o] == null) ? "null" : argObjects[o].toString()));
                                                        }
                                                    }
                                                    try {
                                                        if (tryConstructors) {
                                                            object = ((Constructor)exec).newInstance(argObjects);
                                                        }
                                                        else {
                                                            object = ((Method)exec).invoke(null, argObjects);
                                                        }
                                                    }
                                                    catch (Exception e3) {
                                                        System.out.println("***************************");
                                                        e3.printStackTrace();
                                                        if (depth != -1) {
                                                            System.out.println(pre + "\n" + pre + "*********************");
                                                            System.out.println(pre + "Failed to create new instance (with args).");
                                                            System.out.println(pre + "Expected types:");
                                                            for (final Type type : types) {
                                                                System.out.println(pre + "= " + type);
                                                            }
                                                            System.out.println(pre + "Actual argObjects:");
                                                            for (final Object obj : argObjects) {
                                                                System.out.println(pre + "= " + obj);
                                                            }
                                                            System.out.println(pre + "*********************\n");
                                                        }
                                                    }
                                                    if (object != null) {
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (object != null) {
                            break;
                        }
                    }
                    if (object != null) {
                        if (depth != -1) {
                            System.out.println(pre + "------------------------------");
                            System.out.println(pre + "Compiled object " + object + " successfully.");
                            System.out.println(pre + "------------------------------");
                        }
                        instance.setObject(object);
                        return object;
                    }
                }
            }
        }
        if (this.symbolName.equals("game")) {
            throw new BadSyntaxException("game", "Could not create \"game\" ludeme from description.");
        }
        if (this.symbolName.equals("match")) {
            throw new BadSyntaxException("match", "Could not create a \"match\" ludeme from description.");
        }
        return null;
    }
    
    private static List<List<Arg>> argCombos(final List<Arg> args, final int numSlots) {
        final List<List<Arg>> combos = new ArrayList<>();
        final Arg[] current = new Arg[numSlots];
        for (int i = 0; i < current.length; ++i) {
            current[i] = null;
        }
        argCombos(args, numSlots, 0, 0, current, combos);
        return combos;
    }
    
    private static void argCombos(final List<Arg> args, final int numSlots, final int numUsed, final int slot, final Arg[] current, final List<List<Arg>> combos) {
        if (numUsed > args.size()) {
            return;
        }
        if (slot != numSlots) {
            if (numUsed < args.size()) {
                current[slot] = args.get(numUsed);
                argCombos(args, numSlots, numUsed + 1, slot + 1, current, combos);
                current[slot] = null;
            }
            argCombos(args, numSlots, numUsed, slot + 1, current, combos);
            return;
        }
        if (numUsed < args.size()) {
            return;
        }
        final List<Arg> combo = new ArrayList<>();
        for (int n = 0; n < numSlots; ++n) {
            combo.add(current[n]);
        }
        combos.add(combo);
    }
    
    @Override
    public String toString() {
        String strT = "";
        if (this.parameterName != null) {
            strT = strT + this.parameterName + ":";
        }
        strT = strT + "(" + this.symbolName;
        if (this.argsIn.size() > 0) {
            for (final Arg arg : this.argsIn) {
                strT = strT + " " + arg.toString();
            }
        }
        strT += ")";
        return strT;
    }
}
