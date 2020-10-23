/*
 * Decompiled with CFR 0.150.
 */
package grammar;

import main.Constants;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

public class ParseItem {
    public final Token token;
    private final List<ParseItem> arguments = new ArrayList<>();
    public final ParseItem parent;
    private final List<Instance> instances = new ArrayList<>();
    private boolean doesParse = false;
    private boolean visited = false;
    public final int depth;

    public ParseItem(Token token, ParseItem parent) {
        this.token = token;
        this.parent = parent;
        this.depth = parent == null ? 0 : parent.depth() + 1;
    }

    public Token token() {
        return this.token;
    }

    public List<ParseItem> arguments() {
        return Collections.unmodifiableList(this.arguments);
    }

    public ParseItem parent() {
        return this.parent;
    }

    public List<Instance> instances() {
        return Collections.unmodifiableList(this.instances);
    }

    public boolean doesParse() {
        return this.doesParse;
    }

    public boolean visited() {
        return this.visited;
    }

    public int depth() {
        return this.depth;
    }

    public void clearInstances() {
        this.instances.clear();
    }

    public void add(Instance instance) {
        this.instances.add(instance);
    }

    public void add(ParseItem arg) {
        this.arguments.add(arg);
    }

    public boolean parse(Symbol expected, Report report, String tab) {
        if (tab != null) {
            System.out.println("\n" + tab + "Parsing token " + this.token.name() + ", expected type is " + (expected == null ? "null" : expected.name()) + ".");
        }
        this.visited = true;
        switch (this.token.type()) {
            case Terminal -> {
                return this.parseTerminal(expected, tab);
            }
            case Array -> {
                return this.parseArray(expected, report, tab);
            }
            case Class -> {
                return this.parseClass(expected, report, tab);
            }
        }
        return this.doesParse;
    }

    private boolean parseTerminal(Symbol expected, String tab) {
        if (tab != null) {
            System.out.println(tab + "Handling terminal...");
        }
        for (Instance instance : this.instances) {
            if (tab != null) {
                System.out.print(tab + "Instance: " + instance.symbol().name());
                System.out.println(" => " + instance.symbol().returnType().name() + "... ");
            }
            if (instance.clauses() != null) continue;
            if (expected != null && !expected.compatibleWith(instance.symbol())) {
                if (tab == null) continue;
                System.out.println(tab + "No match, move onto next instance...");
                continue;
            }
            if (tab != null) {
                System.out.println(tab + "++++ Terminal '" + instance.symbol().name() + "' parses. ++++");
            }
            this.doesParse = true;
            return true;
        }
        return false;
    }

    private boolean parseArray(Symbol expected, Report report, String tab) {
        if (tab != null) {
            System.out.println(tab + "Handling array...");
        }
        if (tab != null) {
            System.out.println(tab + "> Expected: " + expected.name());
            for (ParseItem element : this.arguments) {
                System.out.println(tab + "> " + element.token().name());
            }
        }
        for (int e = 0; e < this.arguments.size(); ++e) {
            ParseItem element;
            element = this.arguments.get(e);
            if (element.parse(expected, report, tab == null ? null : tab + "   ")) continue;
            if (tab != null) {
                System.out.println(tab + "   X: Couldn't parse array element " + e + ".");
            }
            return false;
        }
        if (tab != null) {
            System.out.println(tab + "++++ Array of '" + expected.name() + "' parses. ++++");
        }
        this.doesParse = true;
        return true;
    }

    private boolean parseClass(Symbol expected, Report report, String tab) {
        if (tab != null) {
            System.out.println(tab + "Handling class...");
        }
        for (Instance instance : this.instances) {
            if (tab != null) {
                System.out.print(tab + "Class instance: " + instance.symbol().name());
                System.out.print(" => " + instance.symbol().returnType().name() + "... ");
            }
            if (expected != null && !expected.compatibleWith(instance.symbol())) {
                if (tab == null) continue;
                System.out.println("no match, move onto next instance...");
                continue;
            }
            if (tab != null) {
                System.out.println("possible match.");
            }
            if (instance.clauses() == null) {
                System.out.println("** No clauses for instance: " + instance);
                continue;
            }
            for (int c = 0; c < instance.clauses().size(); ++c) {
                Clause clause = instance.clauses().get(c);
                if (tab != null) {
                    System.out.println(tab + (c + 1) + ". Trying clause: " + clause);
                }
                if (!this.arguments.isEmpty() && clause.args() == null) {
                    if (tab == null) continue;
                    System.out.println(tab + "   X: Item has arguments but clauses does not.");
                    continue;
                }
                if (this.arguments.size() > clause.args().size()) {
                    if (tab == null) continue;
                    System.out.println(tab + "   X: Too many arguments for this clause.");
                    continue;
                }
                if (clause.args().size() > 20) {
                    if (tab != null) {
                        System.out.println(tab + "   X: " + clause.symbol().name() + " has more than " + 20 + " args.");
                    }
                    report.addWarning(clause.symbol().name() + " has more than " + 20 + " args.");
                    continue;
                }
                int numSlots = clause.args().size();
                for (BitSet combo : Constants.combos[this.arguments.size()][clause.args().size()]) {
                    int a;
                    BitSet subset = (BitSet)combo.clone();
                    subset.and(clause.mandatory());
                    if (!subset.equals(clause.mandatory())) continue;
                    if (tab != null) {
                        System.out.print(tab + "   Trying arg combo: ");
                        int index = 0;
                        for (int n = 0; n < numSlots; ++n) {
                            System.out.print((combo.get(n) ? "-" : Integer.valueOf(++index)) + " ");
                        }
                        System.out.println();
                    }
                    BitSet orGroups = new BitSet();
                    int index = 0;
                    for (a = 0; a < numSlots; ++a) {
                        if (!combo.get(a)) {
                            ClauseArg clauseArg = clause.args().get(a);
                            boolean canSkip = clauseArg.optional() || clauseArg.orGroup() > 0;
                            if (canSkip) continue;
                            break;
                        }
                        ParseItem arg = this.arguments.get(index++);
                        ClauseArg clauseArg = clause.args().get(a);
                        int orGroup = clauseArg.orGroup();
                        if (orGroup > 0) {
                            if (orGroups.get(orGroup)) break;
                            orGroups.set(orGroup, true);
                        }
                        String argName = arg.token().parameterLabel();
                        String clauseArgName = clauseArg.label();
                        if (argName == null && clauseArgName != null || argName != null && clauseArgName == null || argName != null && !argName.equalsIgnoreCase(clauseArgName)) break;
                        if (arg.parse(clauseArg.symbol(), report, tab == null ? null : tab + "   ")) continue;
                        if (tab == null) break;
                        System.out.println(tab + "   X: Couldn't parse arg " + index + ".");
                        break;
                    }
                    if (a >= numSlots) {
                        if (tab != null) {
                            System.out.println(tab + "++++ Class '" + instance.symbol().name() + "' parses. ++++");
                        }
                        this.doesParse = true;
                        return true;
                    }
                    if (tab == null) continue;
                    System.out.println(tab + "Failed to parse this combo, trying next...");
                }
            }
        }
        return false;
    }

    public int deepestFailure() {
        int result = this.doesParse || !this.visited ? -1 : this.depth;
        for (ParseItem arg : this.arguments) {
            int argResult = arg.deepestFailure();
            if (argResult <= result) continue;
            result = argResult;
        }
        return result;
    }

    public void reportFailures(Report report, int failureDepth) {
        if (!this.doesParse && this.visited && this.depth == failureDepth) {
            String clause;
            if (this.parent == null) {
                clause = Report.clippedString(this.tokenClause(), 32);
                report.addError("Unexpected syntax '" + clause + "'.");
            } else {
                clause = Report.clippedString(this.tokenClause(), 24);
                String parentClause = Report.clippedString(this.parent.tokenClause(), 32);
                report.addError("Unexpected syntax '" + clause + "' in '" + parentClause + "'.");
            }
        }
        for (ParseItem arg : this.arguments) {
            arg.reportFailures(report, failureDepth);
        }
    }

    public String dump(String indent) {
        String TAB = "    ";
        StringBuilder sb = new StringBuilder();
        String label = (this.doesParse ? "+" : "-") + " ";
        sb.append(label + indent);
        if (this.token.parameterLabel() != null) {
            sb.append(this.token.parameterLabel() + ":");
        }
        if (this.token.open() != '\u0000') {
            sb.append(this.token.open());
        }
        if (this.token.name() != null) {
            sb.append(this.token.name());
        }
        if (!this.arguments.isEmpty()) {
            sb.append("\n");
            for (ParseItem arg : this.arguments) {
                sb.append(arg.dump(indent + "    "));
            }
            if (this.token.close() != '\u0000') {
                sb.append(label + indent + this.token.close());
            }
        } else if (this.token.close() != '\u0000') {
            sb.append(this.token.close());
        }
        sb.append("\n");
        return sb.toString();
    }

    public String compare() {
        StringBuilder sb = new StringBuilder();
        sb.append("--------------------------\n");
        sb.append(this.tokenClause());
        if (this.token.type() == Token.TokenType.Array) {
            sb.append(" => Array\n");
        } else if (this.token.type() == Token.TokenType.Terminal) {
            if (this.instances == null || this.instances.size() < 1) {
                return "** compare(): No instances for terminal " + this.token.name() + ".\n";
            }
            sb.append(" => " + this.instances.get(0).symbol().cls().getSimpleName() + "\n");
        } else {
            sb.append("\n");
            for (Instance instance : this.instances) {
                if (instance == null || instance.clauses() == null) continue;
                for (int c = 0; c < instance.clauses().size(); ++c) {
                    Clause clause = instance.clauses().get(c);
                    sb.append((c + 1) + ". " + clause.symbol().grammarLabel() + ": " + clause.toString());
                    sb.append(" => " + instance.symbol().cls().getSimpleName());
                    sb.append("\n");
                }
            }
        }
        for (ParseItem arg : this.arguments) {
            sb.append(arg.compare());
        }
        return sb.toString();
    }

    public String tokenClause() {
        StringBuilder sb = new StringBuilder();
        if (this.token.parameterLabel() != null) {
            sb.append(this.token.parameterLabel() + ":");
        }
        if (this.token.open() != '\u0000') {
            sb.append(this.token.open());
        }
        if (this.token.name() != null) {
            sb.append(this.token.name());
        }
        for (int a = 0; a < this.arguments.size(); ++a) {
            ParseItem arg = this.arguments.get(a);
            if (a > 0 || this.token.name() != null) {
                sb.append(" ");
            }
            sb.append(arg.tokenClause());
        }
        if (this.token.close() != '\u0000') {
            sb.append(this.token.close());
        }
        return sb.toString();
    }
}

