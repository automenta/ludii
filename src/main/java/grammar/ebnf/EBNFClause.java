/*
 * Decompiled with CFR 0.150.
 */
package grammar.ebnf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EBNFClause {
    protected String token = "?";
    private boolean isConstructor = false;
    private boolean isRule = false;
    private boolean isTerminal = false;
    private List<EBNFClauseArg> args = null;

    public EBNFClause() {
    }

    public EBNFClause(String input) {
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

    void decompose(String input) {
        int n;
        int c;
        String str = input.trim();
        switch (str.charAt(0)) {
            case '(' -> {
                this.isConstructor = true;
                break;
            }
            case '<' -> {
                this.isRule = true;
                this.token = str;
                return;
            }
            default -> {
                this.isTerminal = true;
                this.token = str;
                return;
            }
        }
        if (str.charAt(0) != '(' || str.charAt(str.length() - 1) != ')') {
            System.out.println("** Bad bracketing of constructor: " + str);
            return;
        }
        str = str.substring(1, str.length() - 1);
        for (c = 0; c < str.length() && str.charAt(c) != ' '; ++c) {
        }
        this.token = str.substring(0, c).trim();
        str = c >= str.length() ? "" : str.substring(c + 1).trim();
        this.args = new ArrayList<>();
        if (str == "") {
            return;
        }
        String[] subs = str.split(" ");
        int[] orGroups = new int[subs.length];
        boolean[] optional = new boolean[subs.length];
        int orGroup = 0;
        for (int n2 = 1; n2 < subs.length - 1; ++n2) {
            if (!subs[n2].equals("|")) continue;
            if (n2 < 2 || !subs[n2 - 2].equals("|")) {
                // empty if block
            }
            orGroups[n2 - 1] = ++orGroup;
            orGroups[n2 + 1] = orGroup;
        }
        boolean on = false;
        for (n = 0; n < subs.length; ++n) {
            boolean isOpen = subs[n].contains("[");
            boolean isClose = subs[n].contains("]");
            if (isOpen || isClose || on) {
                optional[n] = true;
            }
            if (isOpen) {
                on = true;
            }
            if (!isClose) continue;
            on = false;
        }
        for (n = 0; n < subs.length; ++n) {
            if (subs[n].equals("|")) continue;
            String strArg = subs[n].replace("[", "").replace("]", "").replace("(", "").replace(")", "");
            EBNFClauseArg arg = new EBNFClauseArg(strArg, optional[n], orGroups[n]);
            this.args.add(arg);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.isConstructor) {
            sb.append("(");
        }
        sb.append(this.token);
        if (this.args != null) {
            for (int a = 0; a < this.args.size(); ++a) {
                EBNFClauseArg arg = this.args.get(a);
                sb.append(" ");
                if (arg.orGroup() != 0) {
                    if (a == 0 || this.args.get(a - 1).orGroup() != arg.orGroup()) {
                        if (arg.isOptional()) {
                            sb.append("[");
                        } else {
                            sb.append("(");
                        }
                    }
                    if (a > 0 && this.args.get(a - 1).orGroup() == arg.orGroup()) {
                        sb.append("| ");
                    }
                } else if (arg.isOptional()) {
                    sb.append("[");
                }
                sb.append(arg);
                if (arg.orGroup() != 0) {
                    if (a != this.args.size() - 1 && this.args.get(a + 1).orGroup() == arg.orGroup()) continue;
                    if (arg.isOptional()) {
                        sb.append("]");
                        continue;
                    }
                    sb.append(")");
                    continue;
                }
                if (!arg.isOptional()) continue;
                sb.append("]");
            }
        }
        if (this.isConstructor) {
            sb.append(")");
        }
        return sb.toString();
    }
}

