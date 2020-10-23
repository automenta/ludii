// 
// Decompiled by Procyon v0.5.36
// 

package language.compiler;

import game.functions.booleans.BooleanConstant;
import game.functions.dim.DimConstant;
import game.functions.floats.FloatConstant;
import game.functions.ints.IntConstant;
import language.compiler.exceptions.TerminalNotFoundException;
import language.grammar.Grammar;
import main.StringRoutines;
import main.grammar.Instance;
import main.grammar.Report;
import main.grammar.Symbol;

import java.util.ArrayList;
import java.util.List;

public class ArgTerminal extends Arg
{
    public ArgTerminal(final String name, final String label) {
        super(name, label);
    }
    
    @Override
    public boolean matchSymbols(final Grammar grammar, final Report report) {
        final String className = StringRoutines.upperCaseInitial(this.symbolName);
        final List<Symbol> match = grammar.symbolMap().get(className);
        final List<Symbol> symbols = (match == null) ? null : new ArrayList<>(match);
        this.instances.clear();
        if (symbols == null || Grammar.applicationConstantIndex(this.symbolName) != -1) {
            if (this.symbolName.length() >= 2 && this.symbolName.charAt(0) == '\"' && this.symbolName.charAt(this.symbolName.length() - 1) == '\"') {
                final Symbol symbol = grammar.symbolMap().get("String").get(0);
                String str;
                for (str = this.symbolName; str.contains("\""); str = str.replace("\"", "")) {}
                final Object object = str;
                this.instances.add(new Instance(symbol, object));
            }
            else if (StringRoutines.isInteger(this.symbolName) || Grammar.applicationConstantIndex(this.symbolName) != -1) {
                final int acIndex = Grammar.applicationConstantIndex(this.symbolName);
                final String valueName = (acIndex == -1) ? this.symbolName : Grammar.ApplicationConstants[acIndex][3];
                int value;
                try {
                    value = Integer.parseInt(valueName);
                }
                catch (NumberFormatException e) {
                    e.printStackTrace();
                    return false;
                }
                catch (NullPointerException e2) {
                    e2.printStackTrace();
                    return false;
                }
                Symbol symbol2 = grammar.symbolMap().get("IntConstant").get(0);
                Object object2 = new IntConstant(value);
                this.instances.add(new Instance(symbol2, object2));
                symbol2 = grammar.symbolMap().get("DimConstant").get(0);
                object2 = new DimConstant(value);
                this.instances.add(new Instance(symbol2, object2));
                symbol2 = grammar.symbolMap().get("Integer").get(0);
                object2 = value;
                this.instances.add(new Instance(symbol2, object2));
                symbol2 = grammar.symbolMap().get("int").get(0);
                object2 = value;
                this.instances.add(new Instance(symbol2, object2));
            }
            else if (this.symbolName.equalsIgnoreCase("true") || this.symbolName.equalsIgnoreCase("false")) {
                final boolean value2 = this.symbolName.equalsIgnoreCase("true");
                Symbol symbol3 = grammar.symbolMap().get("BooleanConstant").get(0);
                Object object = BooleanConstant.construct(value2);
                this.instances.add(new Instance(symbol3, object));
                symbol3 = grammar.symbolMap().get("Boolean").get(0);
                object = value2;
                this.instances.add(new Instance(symbol3, object));
                symbol3 = grammar.symbolMap().get("boolean").get(0);
                object = value2;
                this.instances.add(new Instance(symbol3, object));
            }
            if (StringRoutines.isFloat(this.symbolName)) {
                final String valueName2 = this.symbolName;
                float value3;
                try {
                    value3 = Float.parseFloat(valueName2);
                }
                catch (NumberFormatException e3) {
                    e3.printStackTrace();
                    return false;
                }
                catch (NullPointerException e4) {
                    e4.printStackTrace();
                    return false;
                }
                Symbol symbol4 = grammar.symbolMap().get("FloatConstant").get(0);
                Object object3 = new FloatConstant(value3);
                this.instances.add(new Instance(symbol4, object3));
                symbol4 = grammar.symbolMap().get("Float").get(0);
                object3 = value3;
                this.instances.add(new Instance(symbol4, object3));
                symbol4 = grammar.symbolMap().get("float").get(0);
                object3 = value3;
                this.instances.add(new Instance(symbol4, object3));
            }
        }
        else {
            for (final Symbol symbol3 : symbols) {
                if (symbol3.type() == Symbol.SymbolType.Constant) {
                    final Class<?> cls = symbol3.cls();
                    if (cls == null) {
                        System.out.println("** ArgTerminal: null cls, symbolName=" + this.symbolName + ", parameterName=" + this.parameterName);
                    }
                    final Object[] enums = cls.getEnumConstants();
                    if (enums == null || enums.length <= 0) {
                        continue;
                    }
                    for (final Object obj : enums) {
                        if (obj.toString().equals(symbol3.keyword())) {
                            final Instance instance = new Instance(symbol3, obj);
                            this.instances.add(instance);
                        }
                    }
                }
            }
        }
        return this.instances.size() != 0;
    }
    
    @Override
    public Object compile(final Class<?> expected, final int depth) {
        String pre = "";
        for (int n = 0; n < depth; ++n) {
            pre += ". ";
        }
        pre += "T: ";
        if (depth != -1) {
            System.out.println("\n" + pre + "Compiling ArgTerminal: " + this.toString());
            System.out.println(pre + "Trying expected type: " + expected);
        }
        if (depth != -1) {
            for (final Instance instance : this.instances) {
                final Symbol symbol = instance.symbol();
                System.out.println(pre + "T: > " + symbol + " (" + symbol.path() + ") " + symbol.keyword() + ".");
            }
        }
        if (depth != -1) {
            System.out.println(pre + "Instances:");
        }
        for (int n = 0; n < this.instances.size(); ++n) {
            final Instance instance = this.instances.get(n);
            if (depth != -1) {
                System.out.println(pre + "\n" + pre + "Instance " + n + " is " + instance.symbol().grammarLabel() + ": symbol=" + instance.symbol() + " (path=" + instance.symbol().path() + ").");
            }
            final Class<?> cls = instance.cls();
            if (depth != -1) {
                System.out.println(pre + "- cls is: " + ((cls == null) ? "null" : cls.getName()));
            }
            if (cls == null) {
                System.out.println(pre + "- unexpected null cls.");
                throw new TerminalNotFoundException(expected.getName());
            }
            if (expected.isAssignableFrom(cls)) {
                if (depth != -1) {
                    System.out.println(pre + "+ MATCH! Returning object " + instance.object());
                }
                return instance.object();
            }
        }
        if (depth != -1) {
            System.out.println(pre + "\n" + pre + "* Failed to compile ArgTerminal: " + this.toString());
        }
        return null;
    }
    
    @Override
    public String toString() {
        return ((this.parameterName == null) ? "" : (this.parameterName + ":")) + this.symbolName;
    }
}
