// 
// Decompiled by Procyon v0.5.36
// 

package language.parser;

import game.functions.booleans.math.And;
import language.compiler.Arg;
import language.compiler.ArgClass;
import language.compiler.ArgTerminal;
import language.compiler.exceptions.CompilerException;
import language.grammar.Grammar;
import main.Constants;
import main.StringRoutines;
import grammar.*;
import options.UserSelections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser
{
    private Parser() {
    }
    
    public static boolean parseTest(final Description description, final UserSelections userSelections, final Report report, final boolean isVerbose) {
        return expandAndParse(description, userSelections, report, isVerbose);
    }
    
    public static boolean expandAndParse(final Description description, final UserSelections userSelections, final Report report, final boolean isVerbose) {
        try {
            try {
                report.clear();
                Expander.expand(description, userSelections, report, isVerbose);
                if (report.isError()) {
                    return false;
                }
            }
            catch (Exception e2) {
                if (report.isError()) {
                    return false;
                }
            }
            return parseExpanded(description, report, isVerbose);
        }
        catch (CompilerException e) {
            if (isVerbose) {
                e.printStackTrace();
            }
            throw new CompilerException(e.getMessageBody(description.raw()), e);
        }
        catch (Exception e2) {
            e2.printStackTrace();
            throw new IllegalArgumentException(e2);
        }
    }
    
    private static boolean parseExpanded(final Description description, final Report report, final boolean isVerbose) {
        report.clear();
        if (Constants.combos == null) {
            Constants.createCombos();
        }
        if (description.raw() == null || description.raw().isEmpty()) {
            report.addError("Could not expand empty game description. This message was brought to you by Dennis.");
            return false;
        }
        checkVersion(description.raw(), report);
        String rawGame = description.raw();
        rawGame = Expander.removeComments(rawGame);
        final int mdc = rawGame.indexOf("(metadata");
        if (mdc != -1) {
            rawGame = rawGame.substring(0, mdc).trim();
        }
        checkQuotes(rawGame, report);
        if (report.isError()) {
            return false;
        }
        checkBrackets(rawGame, report);
        if (report.isError()) {
            return false;
        }
        if (description.expanded() == null) {
            report.addError("Could not expand. Check that bracket pairs '(..)' and '{..}' match.");
            return false;
        }
        checkQuotes(description.expanded(), report);
        if (report.isError()) {
            return false;
        }
        checkBrackets(description.expanded(), report);
        if (report.isError()) {
            return false;
        }
        checkOptionsExpanded(description.expanded(), report);
        if (report.isError()) {
            return false;
        }
        if (description.tokenForest().tokenTree() == null || description.tokenForest().tokenTree().type() == null) {
            System.out.println("** Parser.parse(): No token tree.");
            report.addError("Couldn't generate token tree from expanded game description.");
            return false;
        }
        if (isVerbose) {
            System.out.println("+++++++++++++++++++++\nParsing:\n" + description.expanded());
        }
        description.createParseTree();
        if (description.parseTree() == null) {
            report.addError("Couldn't generate parse tree from token tree.");
            return false;
        }
        matchTokensWithSymbols(description.parseTree(), Grammar.grammar(), report);
        if (report.isError()) {
            return false;
        }
        checkStrings(description.expanded(), report);
        if (report.isError()) {
            return false;
        }
        if (!description.parseTree().parse(null, report, null)) {
            final int failureDepth = description.parseTree().deepestFailure();
            if (failureDepth >= 0) {
                description.parseTree().reportFailures(report, failureDepth);
            }
            return !report.isError();
        }
        return true;
    }
    
    private static void checkQuotes(final String str, final Report report) {
        final int numQuotes = StringRoutines.numChar(str, '\"');
        if (numQuotes % 2 != 0) {
            report.addError("Mismatched quotation marks '\"'.");
        }
    }
    
    private static void checkBrackets(final String str, final Report report) {
        int numOpen = StringRoutines.numChar(str, '(');
        int numClose = StringRoutines.numChar(str, ')');
        if (numOpen < numClose) {
            if (numClose - numOpen == 1) {
                report.addError("Missing an open bracket '('.");
            }
            else {
                report.addError("Missing " + (numClose - numOpen) + " open brackets '('.");
            }
            return;
        }
        if (numOpen > numClose) {
            if (numOpen - numClose == 1) {
                report.addError("Missing a close bracket ')'.");
            }
            else {
                report.addError("Missing " + (numOpen - numClose) + " close brackets ')'.");
            }
            return;
        }
        numOpen = StringRoutines.numChar(str, '{');
        numClose = StringRoutines.numChar(str, '}');
        if (numOpen < numClose) {
            if (numClose - numOpen == 1) {
                report.addError("Missing an open brace '{'.");
            }
            else {
                report.addError("Missing " + (numClose - numOpen) + " open braces '{'.");
            }
            return;
        }
        if (numOpen > numClose) {
            if (numOpen - numClose == 1) {
                report.addError("Missing a close brace '}'.");
            }
            else {
                report.addError("Missing " + (numOpen - numClose) + " close braces '}'.");
            }
        }
    }
    
    private static void checkOptionsExpanded(final String expanded, final Report report) {
        int c = -1;
        while (true) {
            c = expanded.indexOf('<', c + 1);
            if (c == -1) {
                return;
            }
            int cc = StringRoutines.matchingBracketAt(expanded, c);
            if (cc == -1) {
                cc = c + 5;
            }
            final char ch = expanded.charAt(c + 1);
            if (StringRoutines.isNameChar(ch)) {
                report.addError("Option tag " + expanded.substring(c, cc + 1) + " not expanded.");
            }
        }
    }
    
    private static void matchTokensWithSymbols(final ParseItem item, final Grammar grammar, final Report report) {
        if (item.token == null) {
            report.addError("Null token for item: " + item.dump(" "));
            return;
        }
        try {
            matchSymbols(item, grammar, report);
        }
        catch (Exception ex) {}
        if (item.instances().isEmpty()) {
            switch (item.token.type()) {
                case Terminal -> {
                    String error = "Couldn't find token '" + item.token.name() + "'.";
                    if (Character.isLowerCase(item.token.name().charAt(0))) {
                        error = error + " Maybe missing bracket '(" + item.token.name() + " ...)'?";
                    }
                    report.addError(error);
                }
                case Class -> {
                    report.addError("Couldn't find ludeme class for token '" + item.token.name() + "'.");
                }
            }
        }
        for (final ParseItem arg : item.arguments()) {
            matchTokensWithSymbols(arg, grammar, report);
        }
    }
    
    public static void matchSymbols(final ParseItem item, final Grammar grammar, final Report report) {
        item.clearInstances();
        switch (item.token.type()) {
            case Terminal -> {
                final Arg arg = new ArgTerminal(item.token.name(), item.token.parameterLabel());
                arg.matchSymbols(grammar, report);
                for (final Instance instance : arg.instances()) {
                    item.add(instance);
                }
            }
            case Class -> {
                final Arg arg = new ArgClass(item.token.name(), item.token.parameterLabel());
                arg.matchSymbols(grammar, report);
                for (final Instance instance : arg.instances()) {
                    item.add(instance);
                }
                for (final Instance instance : item.instances()) {
                    final GrammarRule rule = instance.symbol.rule();
                    if (rule != null) {
                        instance.setClauses(rule.rhs());
                    }
                }
            }
        }
    }
    
    private static void checkStrings(final String expanded, final Report report) {
        int numPlayers = 0;
        final int playersFrom = expanded.indexOf("(players");
        if (playersFrom >= 0) {
            final int playersTo = StringRoutines.matchingBracketAt(expanded, playersFrom);
            if (playersTo >= 0) {
                int p = 0;
                while (true) {
                    p = expanded.indexOf("(player", p + 1);
                    if (p == -1) {
                        break;
                    }
                    if (p <= playersFrom || p >= playersTo || expanded.charAt(p + 7) == 's') {
                        continue;
                    }
                    ++numPlayers;
                }
                if (numPlayers == 0) {
                    p = expanded.indexOf("(players");
                    if (p != -1) {
                        final int pp = StringRoutines.matchingBracketAt(expanded, p);
                        if (pp == -1) {
                            report.addError("No closing bracket for '(players ...'.");
                            return;
                        }
                        final String playerString = expanded.substring(p, pp + 1).trim();
                        final String countString = expanded.substring(p + 8, pp).trim();
                        final String[] subs = countString.split(" ");
                        try {
                            numPlayers = Integer.parseInt(subs[0]);
                        }
                        catch (Exception e) {
                            report.addError("Couldn't extract player count from '" + playerString + "'.");
                            return;
                        }
                    }
                }
            }
        }
        final Map<Integer, String> knownStrings = new HashMap<>();
        final String[] array;
        final String[] defaults = array = new String[] { "Player", "Board", "Hand", "Ball", "Bag", "Domino" };
        for (final String def : array) {
            knownStrings.put(def.hashCode(), def);
        }
        extractKnownStrings(expanded, "(game", true, knownStrings, report);
        extractKnownStrings(expanded, "(match", true, knownStrings, report);
        extractKnownStrings(expanded, "(subgame", true, knownStrings, report);
        extractKnownStrings(expanded, "(players", false, knownStrings, report);
        extractKnownStrings(expanded, "(equipment", false, knownStrings, report);
        extractKnownStrings(expanded, "(phase", true, knownStrings, report);
        extractKnownStrings(expanded, "(vote", true, knownStrings, report);
        extractKnownStrings(expanded, "(is Proposed", true, knownStrings, report);
        extractKnownStrings(expanded, "(is Decided", true, knownStrings, report);
        extractKnownStrings(expanded, "(note", true, knownStrings, report);
        extractKnownStrings(expanded, "(trigger", true, knownStrings, report);
        extractKnownStrings(expanded, "(is Trigger", true, knownStrings, report);
        extractKnownStrings(expanded, "(trackSite", false, knownStrings, report);
        int c = -1;
        while (true) {
            c = expanded.indexOf(34, c + 1);
            if (c == -1) {
                return;
            }
            final int cc = StringRoutines.matchingQuoteAt(expanded, c);
            if (cc == -1) {
                report.addError("Couldn't close string: " + expanded.substring(c));
                return;
            }
            final String str = expanded.substring(c + 1, cc);
            if (!StringRoutines.isCoordinate(str)) {
                boolean match = false;
                final int key = str.hashCode();
                if (knownStrings.containsKey(key)) {
                    match = true;
                }
                if (!match) {
                    for (final String known : knownStrings.values()) {
                        if (known.equals(str)) {
                            match = true;
                            break;
                        }
                        if (!known.contains(str) && !str.contains(known)) {
                            continue;
                        }
                        if (Math.abs(str.length() - known.length()) > 2) {
                            continue;
                        }
                        final int pid = StringRoutines.numberAtEnd(str);
                        if (pid > numPlayers && !str.contains("Hand")) {
                            report.addWarning("Item '" + str + "' is numbered " + pid + " but only " + numPlayers + " players.");
                        }
                        match = true;
                        break;
                    }
                }
                if (!match && str.length() <= 5 && StringRoutines.isCoordinate(str)) {
                    continue;
                }
                if (!match) {
                    report.addError("Could not match string '" + str + "'. Misspelt define or item?");
                    return;
                }
            }
            c = cc + 1;
        }
    }
    
    private static void extractKnownStrings(final String expanded, final String targetClause, final boolean firstStringPerClause, final Map<Integer, String> knownStrings, final Report report) {
        int e = -1;
        while (true) {
            e = expanded.indexOf(targetClause, e + 1);
            if (e == -1) {
                return;
            }
            final int ee = StringRoutines.matchingBracketAt(expanded, e);
            if (ee == -1) {
                report.addError("Couldn't close string: " + expanded.substring(e));
                return;
            }
            final String clause = expanded.substring(e, ee + 1);
            int i = -1;
            while (true) {
                i = clause.indexOf(34, i + 1);
                if (i == -1) {
                    break;
                }
                final int ii = StringRoutines.matchingQuoteAt(clause, i);
                if (ii == -1) {
                    report.addError("Couldn't close item string: " + clause.substring(i));
                    return;
                }
                final String known = clause.substring(i + 1, ii);
                if (!StringRoutines.isCoordinate(known)) {
                    knownStrings.put(known.hashCode(), known);
                    if (firstStringPerClause) {
                        break;
                    }
                }
                i = ii;
            }
        }
    }
    
    private static void checkVersion(final String raw, final Report report) {
        final int v = raw.indexOf("(version");
        if (v == -1) {
            report.addWarning("No version info.");
            return;
        }
        int s;
        for (s = v; s < raw.length() && raw.charAt(s) != '\"'; ++s) {}
        if (s >= raw.length()) {
            report.addError("Couldn't find version string in (version ...) entry.");
            return;
        }
        final int ss = StringRoutines.matchingQuoteAt(raw, s);
        if (ss == -1) {
            report.addError("Couldn't close version string in (version ...) entry.");
            return;
        }
        final String version = raw.substring(s + 1, ss);
        final int result = version.compareTo("1.0.8");
        if (result < 0) {
            report.addWarning("Game version (" + version + ") older than app version (" + "1.0.8" + ").");
        }
        else if (result > 0) {
            report.addWarning("Game version (" + version + ") newer than app version (" + "1.0.8" + ").");
        }
    }
    
    public static String tooltipHelp(final Class<?> cls) {
        if (cls.getSimpleName().equalsIgnoreCase("add")) {
            return "add\nAdd a piece...\n\nFormat\n(add ...)\nwhere:\n\u2022 <int>: Minimum length of lines.\n\u2022 [<absoluteDirection>]: Direction category that potential lines must belong to.\n\nExample\n(add ...)\n";
        }
        return "No tooltip found for class " + cls.getName();
    }
    
    public static List<Class<?>> alternativeClasses(final Class<?> cls) {
        final List<Class<?>> alternatives = new ArrayList<>();
        if (cls.getSimpleName().equalsIgnoreCase("and")) {
            Class<?> alternative = null;
            try {
                alternative = Class.forName("game.functions.booleans.math.And");
            }
            catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            alternatives.add(alternative);
        }
        return alternatives;
    }
    
    public static List<Object> alternativeInstances(final Class<?> cls) {
        final List<Object> alternatives = new ArrayList<>();
        if (cls.getSimpleName().equalsIgnoreCase("and")) {
            alternatives.add(new And(null, null));
        }
        return alternatives;
    }
    
    public static TokenRange tokenScope(final String description, final int cursorAt, final boolean isSelect, final SelectionType type) {
        System.out.println("Selection type: " + type);
        if (cursorAt <= 0 || cursorAt >= description.length()) {
            System.out.println("** Grammar.classPaths(): Invalid cursor position " + cursorAt + " specified.");
            return null;
        }
        int c = cursorAt - 1;
        char ch = description.charAt(c);
        if (!StringRoutines.isTokenChar(ch)) {
            return null;
        }
        while (c > 0 && StringRoutines.isTokenChar(ch)) {
            --c;
            ch = description.charAt(c);
            if (ch == '<' && StringRoutines.isLetter(description.charAt(c + 1))) {
                break;
            }
        }
        int cc = cursorAt;
        for (char ch2 = description.charAt(cc); cc < description.length() && StringRoutines.isTokenChar(ch2); ch2 = description.charAt(cc)) {
            if (++cc < description.length()) {}
        }
        if (cc >= description.length()) {
            System.out.println("** Grammar.classPaths(): Couldn't find end of token scope from position " + cursorAt + ".");
            return null;
        }
        final String token = description.substring(c + 1, cc);
        System.out.println("token: " + token);
        if (isSelect) {
            if (description.charAt(c) == ':' || description.charAt(c - 1) == ':') {
                for (c -= 2; c > 0 && StringRoutines.isTokenChar(description.charAt(c)); --c) {}
                ++c;
            }
            if (description.charAt(cc) == ':' || description.charAt(cc + 1) == ':') {
                ++cc;
                while (cc < description.length() && StringRoutines.isTokenChar(description.charAt(cc))) {
                    ++cc;
                }
            }
            if (c > 0 && description.charAt(c - 1) == '[') {
                --c;
                cc = StringRoutines.matchingBracketAt(description, c) + 1;
            }
            else if (c > 0 && description.charAt(c) == '[') {
                cc = StringRoutines.matchingBracketAt(description, c) + 1;
            }
            else if (description.charAt(cc + 1) == ']') {
                for (c = ++cc - 1; c > 0 && description.charAt(c) != '['; --c) {}
            }
        }
        if (token.charAt(0) == '\"') {
            System.out.println("String scope includes: " + description.substring(c, cc));
            return new TokenRange(c, cc);
        }
        if (token.equalsIgnoreCase("true")) {
            if (description.charAt(c) == ':') {
                ++c;
            }
            System.out.println("True scope includes: " + description.substring(c, cc));
            return new TokenRange(c, cc);
        }
        if (token.equalsIgnoreCase("false")) {
            if (description.charAt(c) == ':') {
                ++c;
            }
            System.out.println("False scope includes: " + description.substring(c, cc));
            return new TokenRange(c, cc);
        }
        if (StringRoutines.isInteger(token)) {
            System.out.println("Int scope includes: " + description.substring(c, cc));
            return new TokenRange(c, cc);
        }
        if (StringRoutines.isFloat(token) || StringRoutines.isDouble(token)) {
            System.out.println("Float scope includes: " + description.substring(c, cc));
            return new TokenRange(c, cc);
        }
        if (type == SelectionType.SELECTION || type == SelectionType.TYPING) {
            if (ch == '(') {
                ++c;
            }
            System.out.println("Selected token scope includes: '" + description.substring(c, cc) + "'");
            return new TokenRange(c, cc);
        }
        if (ch == '(') {
            final int closing = StringRoutines.matchingBracketAt(description, c);
            if (closing < 0) {
                System.out.println("** Couldn't close token: " + token);
                return null;
            }
            System.out.println("Class scope includes: " + description.substring(c, closing + 1));
            return new TokenRange(c, closing + 1);
        }
        else {
            if (ch == '<') {
                System.out.println("Rule scope includes: " + description.substring(c, cc));
                return new TokenRange(c, cc);
            }
            if (ch == ' ' || ch == '\n' || ch == '\r' || ch == '\t' || ch == ':' || ch == '{') {
                System.out.println("Enum constant scope includes: '" + description.substring(c + 1, cc) + "'");
                return new TokenRange(c + 1, cc);
            }
            return new TokenRange(c, cc);
        }
    }
}
