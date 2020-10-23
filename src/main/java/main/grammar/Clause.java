// 
// Decompiled by Procyon v0.5.36
// 

package main.grammar;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

public class Clause
{
    private final Symbol symbol;
    private final List<ClauseArg> args;
    private final boolean isHidden;
    private BitSet mandatory;
    
    public Clause(final Symbol symbol) {
        this.mandatory = new BitSet();
        this.symbol = symbol;
        this.args = null;
        this.isHidden = false;
    }
    
    public Clause(final Symbol symbol, final List<ClauseArg> args, final boolean isHidden) {
        this.mandatory = new BitSet();
        this.symbol = symbol;
        this.args = new ArrayList<>();
        for (final ClauseArg arg : args) {
            this.args.add(new ClauseArg(arg));
        }
        this.isHidden = isHidden;
        this.setMandatory();
    }
    
    public Clause(final Clause other) {
        this.mandatory = new BitSet();
        this.symbol = other.symbol;
        if (other.args == null) {
            this.args = null;
        }
        else {
            this.args = ((other.args == null) ? null : new ArrayList<>());
            if (this.args != null) {
                for (final ClauseArg arg : other.args) {
                    this.args.add(new ClauseArg(arg));
                }
            }
        }
        this.isHidden = other.isHidden;
        this.mandatory = (BitSet)other.mandatory.clone();
    }
    
    public Symbol symbol() {
        return this.symbol;
    }
    
    public List<ClauseArg> args() {
        if (this.args == null) {
            return null;
        }
        return Collections.unmodifiableList(this.args);
    }
    
    public boolean isConstructor() {
        return this.args != null;
    }
    
    public boolean isHidden() {
        return this.isHidden;
    }
    
    public BitSet mandatory() {
        return this.mandatory;
    }
    
    public boolean matches(final Clause other) {
        return this.symbol.matches(other.symbol);
    }
    
    public void setMandatory() {
        this.mandatory.clear();
        for (int a = 0; a < this.args.size(); ++a) {
            final ClauseArg arg = this.args.get(a);
            if (!arg.optional() && arg.orGroup() == 0) {
                this.mandatory.set(a, true);
            }
        }
    }
    
    public boolean isSubsetOf(final Clause other) {
        if (!this.symbol.path().equals(other.symbol().path())) {
            return false;
        }
        for (final ClauseArg argA : this.args) {
            int p;
            for (p = 0; p < other.args.size(); ++p) {
                final ClauseArg argB = other.args.get(p);
                if ((argA.label() == null || argB.label() == null || argA.label().equals(argB.label())) && argA.symbol().path().equals(argB.symbol().path()) && argA.nesting() == argB.nesting()) {
                    break;
                }
            }
            if (p >= other.args.size()) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public String toString() {
        String str = "";
        final String safeKeyword = this.symbol.grammarLabel();
        if (this.args != null) {
            str += "(";
            str += this.symbol.keyword();
            ClauseArg prevArg = null;
            for (int p = 0; p < this.args.size(); ++p) {
                final ClauseArg arg = this.args.get(p);
                str += " ";
                final int orGroup = arg.orGroup();
                final int andGroup = arg.andGroup();
                if (orGroup != 0) {
                    if (prevArg == null && !arg.optional()) {
                        str += "(";
                    }
                    else if (prevArg != null && orGroup != prevArg.orGroup()) {
                        if (prevArg.orGroup() != 0 && !prevArg.optional()) {
                            str += ") ";
                        }
                        if (!arg.optional()) {
                            str += "(";
                        }
                    }
                    else if (prevArg != null && orGroup == prevArg.orGroup() && (andGroup == 0 || andGroup != prevArg.andGroup())) {
                        str += "| ";
                    }
                }
                if (orGroup == 0 && prevArg != null && prevArg.orGroup() != 0 && !prevArg.optional()) {
                    str += ") ";
                }
                boolean prevAnd = false;
                boolean nextAnd = false;
                if (prevArg != null) {
                    prevAnd = ((orGroup == 0 || (orGroup != 0 && orGroup == prevArg.orGroup())) && andGroup == prevArg.andGroup());
                }
                if (p < this.args.size() - 1) {
                    final ClauseArg nextArg = this.args.get(p + 1);
                    nextAnd = ((orGroup == 0 || (orGroup != 0 && orGroup == nextArg.orGroup())) && andGroup == nextArg.andGroup());
                }
                String argString = arg.toString();
                if (prevAnd && orGroup != 0 && argString.charAt(0) == '[') {
                    argString = argString.substring(1);
                }
                if (nextAnd && orGroup != 0 && argString.charAt(argString.length() - 1) == ']') {
                    argString = argString.substring(0, argString.length() - 1);
                }
                str += argString;
                if (orGroup != 0 && p == this.args.size() - 1 && !arg.optional()) {
                    str += ")";
                }
                prevArg = arg;
            }
            str += ")";
        }
        else {
            switch (this.symbol.type()) {
                case Primitive:
                case Predefined:
                case Constant: {
                    str = this.symbol.keyword();
                    break;
                }
                case Class: {
                    str = "<" + safeKeyword + ">";
                    break;
                }
                default: {
                    str += "[UNKNOWN]";
                    break;
                }
            }
        }
        for (int n = 0; n < this.symbol.nesting(); ++n) {
            str = "{" + str + "}";
        }
        return str;
    }
}
