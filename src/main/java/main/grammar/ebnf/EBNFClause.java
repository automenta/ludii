// 
// Decompiled by Procyon v0.5.36
// 

package main.grammar.ebnf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EBNFClause
{
    protected String token;
    private boolean isConstructor;
    private boolean isRule;
    private boolean isTerminal;
    private List<EBNFClauseArg> args;
    
    public EBNFClause() {
        this.token = "?";
        this.isConstructor = false;
        this.isRule = false;
        this.isTerminal = false;
        this.args = null;
    }
    
    public EBNFClause(final String input) {
        this.token = "?";
        this.isConstructor = false;
        this.isRule = false;
        this.isTerminal = false;
        this.args = null;
        this.decompose(input);
    }
    
    public String token() {
        return this.token;
    }
    
    public boolean isConstructor() {
        return this.isConstructor;
    }
    
    public boolean isRule() {
        return this.isRule;
    }
    
    public boolean isTerminal() {
        return this.isTerminal;
    }
    
    public List<EBNFClauseArg> args() {
        if (this.args == null) {
            return null;
        }
        return Collections.unmodifiableList(this.args);
    }
    
    void decompose(final String input) {
        String str = input.trim();
        switch (str.charAt(0)) {
            case '(': {
                this.isConstructor = true;
                if (str.charAt(0) != '(' || str.charAt(str.length() - 1) != ')') {
                    System.out.println("** Bad bracketing of constructor: " + str);
                    return;
                }
                int c;
                for (str = str.substring(1, str.length() - 1), c = 0; c < str.length() && str.charAt(c) != ' '; ++c) {}
                this.token = str.substring(0, c).trim();
                str = ((c >= str.length()) ? "" : str.substring(c + 1).trim());
                this.args = new ArrayList<>();
                if (str == "") {
                    return;
                }
                final String[] subs = str.split(" ");
                final int[] orGroups = new int[subs.length];
                final boolean[] optional = new boolean[subs.length];
                int orGroup = 0;
                for (int n = 1; n < subs.length - 1; ++n) {
                    if (subs[n].equals("|")) {
                        if (n < 2 || !subs[n - 2].equals("|")) {
                            ++orGroup;
                        }
                        orGroups[n + 1] = (orGroups[n - 1] = orGroup);
                    }
                }
                boolean on = false;
                for (int n2 = 0; n2 < subs.length; ++n2) {
                    final boolean isOpen = subs[n2].contains("[");
                    final boolean isClose = subs[n2].contains("]");
                    if (isOpen || isClose || on) {
                        optional[n2] = true;
                    }
                    if (isOpen) {
                        on = true;
                    }
                    if (isClose) {
                        on = false;
                    }
                }
                for (int n2 = 0; n2 < subs.length; ++n2) {
                    if (!subs[n2].equals("|")) {
                        final String strArg = subs[n2].replace("[", "").replace("]", "").replace("(", "").replace(")", "");
                        final EBNFClauseArg arg = new EBNFClauseArg(strArg, optional[n2], orGroups[n2]);
                        this.args.add(arg);
                    }
                }
            }
            case '<': {
                this.isRule = true;
                this.token = str;
            }
            default: {
                this.isTerminal = true;
                this.token = str;
            }
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        if (this.isConstructor) {
            sb.append("(");
        }
        sb.append(this.token);
        if (this.args != null) {
            for (int a = 0; a < this.args.size(); ++a) {
                final EBNFClauseArg arg = this.args.get(a);
                sb.append(" ");
                if (arg.orGroup() != 0) {
                    if (a == 0 || this.args.get(a - 1).orGroup() != arg.orGroup()) {
                        if (arg.isOptional()) {
                            sb.append("[");
                        }
                        else {
                            sb.append("(");
                        }
                    }
                    if (a > 0 && this.args.get(a - 1).orGroup() == arg.orGroup()) {
                        sb.append("| ");
                    }
                }
                else if (arg.isOptional()) {
                    sb.append("[");
                }
                sb.append(arg);
                if (arg.orGroup() != 0) {
                    if (a == this.args.size() - 1 || this.args.get(a + 1).orGroup() != arg.orGroup()) {
                        if (arg.isOptional()) {
                            sb.append("]");
                        }
                        else {
                            sb.append(")");
                        }
                    }
                }
                else if (arg.isOptional()) {
                    sb.append("]");
                }
            }
        }
        if (this.isConstructor) {
            sb.append(")");
        }
        return sb.toString();
    }
}
