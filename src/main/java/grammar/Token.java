/*
 * Decompiled with CFR 0.150.
 */
package grammar;

import main.StringRoutines;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Token {
    private String name = null;
    private String parameterLabel = null;
    private char open = '\u0000';
    private char close = '\u0000';
    private final List<Token> arguments = new ArrayList<>();
    public static final int MAX_CHARS = 78;
    public static final String TAB = "    ";

    public Token(String str, Report report) {
        this.decompose(str, report);
    }

    public String name() {
        return this.name;
    }

    public String parameterLabel() {
        return this.parameterLabel;
    }

    public char open() {
        return this.open;
    }

    public char close() {
        return this.close;
    }

    public List<Token> arguments() {
        return Collections.unmodifiableList(this.arguments);
    }

    public TokenType type() {
        if (this.open == '(' && this.close == ')' && this.name != null) {
            return TokenType.Class;
        }
        if (this.open == '{' && this.close == '}') {
            return TokenType.Array;
        }
        if (this.open == '\u0000' && this.close == '\u0000' && this.name != null) {
            return TokenType.Terminal;
        }
        return null;
    }

    public boolean isTerminal() {
        return this.open == '\u0000' && this.close == '\u0000';
    }

    public boolean isClass() {
        return this.open == '(' && this.close == ')' && this.name != null;
    }

    public boolean isArray() {
        return this.open == '{' && this.close == '}';
    }

    public int count() {
        int count = 1;
        for (Token sub : this.arguments) {
            count += sub.count();
        }
        return count;
    }

    int length() {
        int length = 0;
        if (this.name != null) {
            length += this.name.length();
        }
        if (this.open != '\u0000' && this.close != '\u0000') {
            length += 2;
        }
        if (this.parameterLabel != null) {
            length += this.parameterLabel.length();
        }
        if (!this.arguments.isEmpty()) {
            for (Token sub : this.arguments) {
                length += sub.length();
            }
            length += this.arguments.size() - 1;
        }
        return length;
    }

    public void decompose(String strIn, Report report) {
        String str = strIn.trim();
        if (str.isEmpty()) {
            report.addError("Can't decompose token from empty string.");
            return;
        }
        if (StringRoutines.isName(str)) {
            str = this.consumeParameterName(str, 0, true);
        }
        String argsString = null;
        char ch = str.charAt(0);
        if (ch == '\"') {
            this.consumeString(str);
            return;
        }
        if (ch == '(') {
            this.open = 40;
            int cb = StringRoutines.matchingBracketAt(str, 0);
            if (cb == -1) {
                report.addError("No closing bracket ')' for clause '" + Report.clippedString(str, 20) + "'.");
                return;
            }
            this.close = 41;
            str = str.substring(1, cb);
            argsString = this.consumeToken(str);
        } else if (ch == '{') {
            this.open = 123;
            int cb = StringRoutines.matchingBracketAt(str, 0);
            if (cb == -1) {
                report.addError("No closing bracket '}' for clause '" + Report.clippedString(str, 20) + "'.");
                return;
            }
            this.close = 125;
            str = str.substring(1, cb);
            argsString = str;
        } else if (ch != ' ') {
            this.consumeToken(str);
            return;
        }
        if (argsString != null) {
            this.handleArgs(argsString, report);
        }
    }

    void handleArgs(String strIn, Report report) {
        String str = strIn;
        while (!str.isEmpty() && !(str = str.trim()).isEmpty()) {
            int cb;
            Token sub;
            int cc;
            char ch;
            int c = 0;
            if (StringRoutines.isName(str)) {
                while (c < str.length() && (ch = str.charAt(c++)) != ':') {
                }
            }
            if (c >= str.length()) {
                System.out.println("Unexpected EOL in: " + str);
                break;
            }
            ch = str.charAt(c);
            if (ch == '\"') {
                for (cc = c + 1; cc < str.length() && (str.charAt(cc) != '\"' || str.charAt(cc - 1) == '\\'); ++cc) {
                }
                if (cc >= str.length()) {
                    report.addError("No closing quote '\"' for token arg '" + Report.clippedString(str.substring(c), 20) + "'.");
                    return;
                }
                if (str.substring(0, cc + 1).trim().isEmpty()) {
                    System.out.println("A - Empty substring.");
                }
                sub = new Token(str.substring(0, cc + 1), report);
                this.arguments.add(sub);
                str = str.substring(cc + 1);
                continue;
            }
            if (ch == '{') {
                cb = StringRoutines.matchingBracketAt(str, c);
                if (cb == -1) {
                    report.addError("No closing bracket '}' for token arg '" + Report.clippedString(str.substring(c), 20) + "'.");
                    return;
                }
                if (str.substring(0, cb + 1).trim().isEmpty()) {
                    System.out.println("B - Empty substring.");
                }
                sub = new Token(str.substring(0, cb + 1), report);
                this.arguments.add(sub);
                str = str.substring(cb + 1);
                continue;
            }
            if (ch == '(') {
                cb = StringRoutines.matchingBracketAt(str, c);
                if (cb == -1) {
                    report.addError("No closing bracket ')' for token arg '" + Report.clippedString(str.substring(c), 20) + "'.");
                    return;
                }
                if (str.substring(0, cb + 1).trim().isEmpty()) {
                    System.out.println("C - Empty substring.");
                }
                sub = new Token(str.substring(0, cb + 1), report);
                this.arguments.add(sub);
                str = str.substring(cb + 1);
                continue;
            }
            if (ch != ' ') {
                for (cc = c; cc < str.length() && StringRoutines.isTokenChar(str.charAt(cc)); ++cc) {
                }
                if (cc == 0) {
                    str = str.substring(1);
                    report.addError("Empty substring from '" + Report.clippedString(strIn, 20) + "'. Maybe a wrong bracket type '}'?");
                    return;
                }
                sub = new Token(str.substring(0, cc), report);
                this.arguments.add(sub);
                str = str.substring(cc);
                continue;
            }
            System.out.println("** Token.handleArgs(): Not handling arg: " + str);
            str = str.substring(1);
        }
        for (int a = this.arguments.size() - 1; a >= 0; --a) {
            if (this.arguments.get(a) != null && this.arguments.get(a).type() != null) continue;
            this.arguments.remove(a);
        }
    }

    void consumeString(String strIn) {
        String str = strIn;
        if (str.isEmpty() || str.charAt(0) != '\"') {
            System.out.println("Not a string: " + str);
            return;
        }
        this.name = "\"";
        for (int c = 1; c < str.length(); ++c) {
            boolean isEmbedded;
            char ch = str.charAt(c);
            boolean isQuote = ch == '\"';
            boolean bl = isEmbedded = ch == '\"' && str.charAt(c - 1) == '\\';
            if (isQuote && !isEmbedded) break;
            if (isEmbedded) {
                this.name = this.name.substring(0, this.name.length() - 1) + "'";
                continue;
            }
            this.name = this.name + ch;
        }
        this.name = this.name + "\"";
    }

    String consumeToken(String strIn) {
        char ch;
        String str = strIn;
        if (str.isEmpty()) {
            System.out.println("Not a token: " + str);
            return null;
        }
        this.name = "";
        int c = 0;
        while (c < str.length() && StringRoutines.isTokenChar(ch = str.charAt(c++))) {
            this.name = this.name + ch;
        }
        return str.substring(c).trim();
    }

    String consumeParameterName(String strIn, int cIn, boolean store) {
        char ch;
        String str = strIn;
        if (str.isEmpty()) {
            System.out.println("Not a parameter name: " + str);
            return null;
        }
        if (store) {
            this.parameterLabel = "";
        }
        int c = cIn;
        while (c < str.length() && (ch = str.charAt(c++)) != ':') {
            if (!store) continue;
            this.parameterLabel = this.parameterLabel + ch;
        }
        String str2 = str.substring(0, cIn) + str.substring(c);
        return str2.trim();
    }

    public String toString() {
        return this.format();
    }

    public String format() {
        ArrayList<String> lines = new ArrayList<>();
        this.format(lines, 0, false);
        for (int n = 0; n < lines.size() - 1; ++n) {
            if (!lines.get(n).contains("(game") && !lines.get(n).contains("(match") && !lines.get(n).contains("(piece")) continue;
            Token.mergeNameLinesAt(lines, n);
        }
        Token.compressNumberPairArrayElements(lines);
        Token.mergeArrayLines(lines);
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

    public void format(List<String> lines, int depth, boolean doSplit) {
        boolean isRulesToken;
        String line = Token.indent(depth);
        String tokenLine = this.formatSingleLine();
        boolean isEquipmentToken = tokenLine.indexOf("(equipment") == 0;
        boolean bl = isRulesToken = tokenLine.indexOf("(rules") == 0;
        if (!(line.length() + tokenLine.length() > 78 || doSplit || isRulesToken || isEquipmentToken)) {
            lines.add(line + tokenLine);
            return;
        }
        if (this.parameterLabel != null) {
            line = line + this.parameterLabel + ":";
        }
        if (this.isTerminal()) {
            lines.add(line + this.name);
            return;
        }
        line = line + this.open;
        if (this.name != null) {
            line = line + this.name;
        }
        lines.add(line);
        for (Token arg : this.arguments) {
            boolean isRulesArg;
            String argStr = arg.formatSingleLine();
            boolean isEquipmentArg = argStr.indexOf("(equipment") == 0;
            boolean bl2 = isRulesArg = argStr.indexOf("(rules") == 0;
            if (Token.indent(depth + 1).length() + argStr.length() > 78 || isEquipmentToken || isEquipmentArg || isRulesToken || isRulesArg) {
                ArrayList<String> subLines = new ArrayList<>();
                arg.format(subLines, depth + 1, isEquipmentToken);
                lines.addAll(subLines);
                continue;
            }
            lines.add(Token.indent(depth + 1) + argStr);
        }
        lines.add(Token.indent(depth) + this.close);
    }

    static String indent(int depth) {
        StringBuilder sb = new StringBuilder();
        sb.append(TAB.repeat(Math.max(0, depth)));
        return sb.toString();
    }

    static void mergeNameLinesAt(List<String> lines, int n) {
        boolean isName;
        if (n >= lines.size()) {
            return;
        }
        boolean bl = isName = lines.get(n + 1).trim().charAt(0) == '\"';
        if (isName) {
            Token.mergeLinesAt(lines, n);
        }
    }

    static void mergeLinesAt(List<String> lines, int n) {
        String line = lines.get(n) + " " + lines.get(n + 1).trim();
        lines.remove(n);
        lines.remove(n);
        lines.add(n, line);
    }

    static void mergeArrayLines(List<String> lines) {
        for (int n = 0; n < lines.size(); ++n) {
            String nextLine;
            boolean isEquipment;
            int nn;
            if (!Token.isArrayOpen(lines.get(n))) continue;
            boolean containsClass = false;
            for (nn = n + 1; nn < lines.size(); ++nn) {
                if (Token.isClass(lines.get(nn))) {
                    containsClass = true;
                }
                if (Token.isArrayClose(lines.get(nn))) break;
            }
            boolean bl = isEquipment = n > 0 && lines.get(n - 1).contains("(equipment");
            if (nn >= lines.size() || containsClass || isEquipment) continue;
            ++n;
            while (n < lines.size() - 1 && !Token.isArrayClose(nextLine = lines.get(n + 1))) {
                if (lines.get(n).length() + nextLine.trim().length() < 78) {
                    Token.mergeLinesAt(lines, n);
                    continue;
                }
                ++n;
            }
        }
    }

    static boolean isArrayOpen(String line) {
        int numOpen = StringRoutines.numOpenBrackets(line);
        int numClose = StringRoutines.numCloseBrackets(line);
        return line.contains("{") && numOpen == 1 && numClose == 0;
    }

    static boolean isArrayClose(String line) {
        int numOpen = StringRoutines.numOpenBrackets(line);
        int numClose = StringRoutines.numCloseBrackets(line);
        return line.contains("}") && numOpen == 0 && numClose == 1;
    }

    static boolean isClass(String line) {
        return line.contains("(");
    }

    static void compressNumberPairArrayElements(List<String> lines) {
        for (int n = 0; n < lines.size(); ++n) {
            char ch;
            String line = lines.get(n);
            if (!line.contains("{ ") || !line.contains(" }")) continue;
            int c = line.indexOf("{ ");
            if (c >= 0 && ((ch = line.charAt(c + 2)) == '\"' || StringRoutines.isNumeric(ch))) {
                line = line.substring(0, c + 1) + line.substring(c + 2);
            }
            if ((c = line.indexOf(" }")) >= 0 && ((ch = line.charAt(c - 1)) == '\"' || StringRoutines.isNumeric(ch))) {
                line = line.substring(0, c) + line.substring(c + 1);
            }
            lines.remove(n);
            lines.add(n, line);
        }
    }

    public String formatSingleLine() {
        StringBuilder sb = new StringBuilder();
        if (this.parameterLabel != null) {
            sb.append(this.parameterLabel).append(":");
        }
        if (this.isTerminal()) {
            sb.append(this.name);
            return sb.toString();
        }
        sb.append(this.open);
        if (this.isClass()) {
            sb.append(this.name);
        }
        for (Token sub : this.arguments) {
            sb.append(" ").append(sub.formatSingleLine());
        }
        if (this.isArray()) {
            sb.append(" ");
        }
        sb.append(this.close);
        return sb.toString();
    }

    public String formatZhangShasha(String indent, int depth, boolean inline, boolean zhangShasha) {
        String str = "";
        if (this.open == '\u0000') {
            if (zhangShasha && StringRoutines.isInteger(this.name)) {
                str = this.parameterLabel != null ? str + indent + "\"" + this.parameterLabel + ":" + this.name + "\"" : str + "\"" + this.name + "\"";
            } else {
                if (this.parameterLabel != null) {
                    str = str + indent + this.parameterLabel + ":";
                }
                str = str + this.name;
            }
            return str;
        }
        if (this.parameterLabel != null) {
            str = str + indent + this.parameterLabel + ":";
        }
        if (this.name != null) {
            int len = this.length();
            if (this.name.equals("game")) {
                str = zhangShasha ? str + this.name + this.open : str + this.open + this.name;
                str = str + " " + this.arguments.get(0).formatZhangShasha("", depth + 1, true, zhangShasha);
                for (Token arg : this.arguments) {
                    if (arg == null || arg.type() != TokenType.Class) continue;
                    str = str + indent + TAB + arg.formatZhangShasha(TAB, depth + 1, false, zhangShasha) + "\n";
                }
                str = str + this.close;
            } else if (len < 78 && (depth > 1 || inline)) {
                str = zhangShasha ? str + this.name + this.open : str + this.open + this.name;
                for (Token sub : this.arguments) {
                    str = str + " " + sub.formatZhangShasha("", depth + 1, true, zhangShasha);
                }
                str = str + this.close;
            } else {
                str = zhangShasha ? str + this.name + this.open : str + this.open + this.name;
                str = str + "\n";
                for (Token sub : this.arguments) {
                    str = str + indent + TAB + sub.formatZhangShasha(indent + TAB, depth + 1, false, zhangShasha) + "\n";
                }
                str = str + indent + this.close;
            }
        } else {
            int len = this.length();
            if (len < 78 || this.shortArguments()) {
                str = zhangShasha ? str + "array(" : str + this.open;
                if (this.name != null) {
                    str = str + this.name;
                }
                for (Token sub : this.arguments) {
                    str = str + " " + sub.formatZhangShasha("", depth + 1, true, zhangShasha);
                }
                str = zhangShasha ? str + " )" : str + " " + this.close;
            } else {
                str = zhangShasha ? str + "array(" : str + this.open;
                if (this.name != null) {
                    str = str + this.name + "\n";
                }
                for (Token sub : this.arguments) {
                    str = str + indent + TAB + sub.formatZhangShasha(indent + TAB, depth + 1, false, zhangShasha) + "\n";
                }
                str = zhangShasha ? str + indent + ")" : str + indent + this.close;
            }
        }
        return str;
    }

    public boolean shortArguments() {
        int maxLen = 0;
        for (Token sub : this.arguments) {
            int len = sub.length();
            if (len <= maxLen) continue;
            maxLen = len;
        }
        return maxLen < 6;
    }

    public String dump(String indent) {
        String label = String.valueOf(this.type().name().charAt(0)) + this.type().name().charAt(1) + ": ";
        StringBuilder sb = new StringBuilder();
        sb.append(label).append(indent);
        if (this.parameterLabel != null) {
            sb.append(this.parameterLabel).append(":");
        }
        if (this.open != '\u0000') {
            sb.append(this.open);
        }
        if (this.name != null) {
            sb.append(this.name);
        }
        if (!this.arguments.isEmpty()) {
            sb.append("\n");
            for (Token arg : this.arguments) {
                sb.append(arg.dump(indent + TAB));
            }
            if (this.close != '\u0000') {
                sb.append(label).append(indent).append(this.close);
            }
        } else if (this.close != '\u0000') {
            sb.append(this.close);
        }
        sb.append("\n");
        return sb.toString();
    }

    public enum TokenType {
        Class,
        Array,
        Terminal

    }
}

