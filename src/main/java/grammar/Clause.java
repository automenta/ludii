/*
 * Decompiled with CFR 0.150.
 */
package grammar;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

public class Clause {
    private final Symbol symbol;
    private final List<ClauseArg> args;
    private final boolean isHidden;
    private BitSet mandatory = new BitSet();

    public Clause(Symbol symbol) {
        this.symbol = symbol;
        this.args = null;
        this.isHidden = false;
    }

    public Clause(Symbol symbol, List<ClauseArg> args, boolean isHidden) {
        this.symbol = symbol;
        this.args = new ArrayList<>();
        for (ClauseArg arg : args) {
            this.args.add(new ClauseArg(arg));
        }
        this.isHidden = isHidden;
        this.setMandatory();
    }

    public Clause(Clause other) {
        this.symbol = other.symbol;
        if (other.args == null) {
            this.args = null;
        } else {
            this.args = new ArrayList<>();
            for (ClauseArg arg : other.args) {
                this.args.add(new ClauseArg(arg));
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

    public boolean matches(Clause other) {
        return this.symbol.matches(other.symbol);
    }

    public void setMandatory() {
        this.mandatory.clear();
        for (int a = 0; a < this.args.size(); ++a) {
            ClauseArg arg = this.args.get(a);
            if (arg.optional() || arg.orGroup() != 0) continue;
            this.mandatory.set(a, true);
        }
    }

    public boolean isSubsetOf(Clause other) {
        if (!this.symbol.path().equals(other.symbol().path())) {
            return false;
        }
        for (ClauseArg argA : this.args) {
            int p;
            for (p = 0; p < other.args.size(); ++p) {
                ClauseArg argB = other.args.get(p);
                if ((argA.label() == null || argB.label() == null || argA.label().equals(argB.label())) && argA.symbol().path().equals(argB.symbol().path()) && argA.nesting() == argB.nesting()) break;
            }
            if (p < other.args.size()) continue;
            return false;
        }
        return true;
    }

    public String toString() {
        String str = "";
        String safeKeyword = this.symbol.grammarLabel();
        if (this.args != null) {
            str = str + "(";
            str = str + this.symbol.keyword();
            ClauseArg prevArg = null;
            for (int p = 0; p < this.args.size(); ++p) {
                ClauseArg arg = this.args.get(p);
                str = str + " ";
                int orGroup = arg.orGroup();
                int andGroup = arg.andGroup();
                if (orGroup != 0) {
                    if (prevArg == null && !arg.optional()) {
                        str = str + "(";
                    } else if (prevArg != null && orGroup != prevArg.orGroup()) {
                        if (prevArg.orGroup() != 0 && !prevArg.optional()) {
                            str = str + ") ";
                        }
                        if (!arg.optional()) {
                            str = str + "(";
                        }
                    } else if (prevArg != null && orGroup == prevArg.orGroup() && (andGroup == 0 || andGroup != prevArg.andGroup())) {
                        str = str + "| ";
                    }
                }
                if (orGroup == 0 && prevArg != null && prevArg.orGroup() != 0 && !prevArg.optional()) {
                    str = str + ") ";
                }
                boolean prevAnd = false;
                boolean nextAnd = false;
                if (prevArg != null) {
                    boolean bl = prevAnd = (orGroup == 0 || orGroup != 0 && orGroup == prevArg.orGroup()) && andGroup == prevArg.andGroup();
                }
                if (p < this.args.size() - 1) {
                    ClauseArg nextArg = this.args.get(p + 1);
                    nextAnd = (orGroup == 0 || orGroup != 0 && orGroup == nextArg.orGroup()) && andGroup == nextArg.andGroup();
                }
                String argString = arg.toString();
                if (prevAnd && orGroup != 0 && argString.charAt(0) == '[') {
                    argString = argString.substring(1);
                }
                if (nextAnd && orGroup != 0 && argString.charAt(argString.length() - 1) == ']') {
                    argString = argString.substring(0, argString.length() - 1);
                }
                str = str + argString;
                if (orGroup != 0 && p == this.args.size() - 1 && !arg.optional()) {
                    str = str + ")";
                }
                prevArg = arg;
            }
            str = str + ")";
        } else {
            switch (this.symbol.type()) {
                case Primitive, Predefined, Constant -> str = this.symbol.keyword();
                case Class -> str = "<" + safeKeyword + ">";
                default -> str = str + "[UNKNOWN]";
            }
        }
        for (int n = 0; n < this.symbol.nesting(); ++n) {
            str = "{" + str + "}";
        }
        return str;
    }
}

