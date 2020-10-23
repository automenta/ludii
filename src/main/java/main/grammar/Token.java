// 
// Decompiled by Procyon v0.5.36
// 

package main.grammar;

import main.StringRoutines;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Token
{
    private String name;
    private String parameterLabel;
    private char open;
    private char close;
    private final List<Token> arguments;
    public static final int MAX_CHARS = 78;
    public static final String TAB = "    ";
    
    public Token(final String str, final Report report) {
        this.name = null;
        this.parameterLabel = null;
        this.open = '\0';
        this.close = '\0';
        this.arguments = new ArrayList<>();
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
        if (this.open == '\0' && this.close == '\0' && this.name != null) {
            return TokenType.Terminal;
        }
        return null;
    }
    
    public boolean isTerminal() {
        return this.open == '\0' && this.close == '\0';
    }
    
    public boolean isClass() {
        return this.open == '(' && this.close == ')' && this.name != null;
    }
    
    public boolean isArray() {
        return this.open == '{' && this.close == '}';
    }
    
    public int count() {
        int count = 1;
        for (final Token sub : this.arguments) {
            count += sub.count();
        }
        return count;
    }
    
    int length() {
        int length = 0;
        if (this.name != null) {
            length += this.name.length();
        }
        if (this.open != '\0' && this.close != '\0') {
            length += 2;
        }
        if (this.parameterLabel != null) {
            length += this.parameterLabel.length();
        }
        if (!this.arguments.isEmpty()) {
            for (final Token sub : this.arguments) {
                length += sub.length();
            }
            length += this.arguments.size() - 1;
        }
        return length;
    }
    
    public void decompose(final String strIn, final Report report) {
        String str = strIn.trim();
        if (str.isEmpty()) {
            report.addError("Can't decompose token from empty string.");
            return;
        }
        if (StringRoutines.isName(str)) {
            str = this.consumeParameterName(str, 0, true);
        }
        String argsString = null;
        final char ch = str.charAt(0);
        if (ch == '\"') {
            this.consumeString(str);
            return;
        }
        if (ch == '(') {
            this.open = '(';
            final int cb = StringRoutines.matchingBracketAt(str, 0);
            if (cb == -1) {
                report.addError("No closing bracket ')' for clause '" + Report.clippedString(str, 20) + "'.");
                return;
            }
            this.close = ')';
            str = str.substring(1, cb);
            argsString = this.consumeToken(str);
        }
        else if (ch == '{') {
            this.open = '{';
            final int cb = StringRoutines.matchingBracketAt(str, 0);
            if (cb == -1) {
                report.addError("No closing bracket '}' for clause '" + Report.clippedString(str, 20) + "'.");
                return;
            }
            this.close = '}';
            str = str.substring(1, cb);
            argsString = str;
        }
        else if (ch != ' ') {
            this.consumeToken(str);
            return;
        }
        if (argsString != null) {
            this.handleArgs(argsString, report);
        }
    }
    
    void handleArgs(final String strIn, final Report report) {
        String str = strIn;
        while (!str.isEmpty()) {
            str = str.trim();
            if (str.isEmpty()) {
                break;
            }
            int c = 0;
            if (StringRoutines.isName(str)) {
                while (c < str.length()) {
                    final char ch = str.charAt(c++);
                    if (ch == ':') {
                        break;
                    }
                }
            }
            if (c >= str.length()) {
                System.out.println("Unexpected EOL in: " + str);
                break;
            }
            final char ch = str.charAt(c);
            if (ch == '\"') {
                int cc;
                for (cc = c + 1; cc < str.length() && (str.charAt(cc) != '\"' || str.charAt(cc - 1) == '\\'); ++cc) {}
                if (cc >= str.length()) {
                    report.addError("No closing quote '\"' for token arg '" + Report.clippedString(str.substring(c), 20) + "'.");
                    return;
                }
                if (str.substring(0, cc + 1).trim().isEmpty()) {
                    System.out.println("A - Empty substring.");
                }
                final Token sub = new Token(str.substring(0, cc + 1), report);
                this.arguments.add(sub);
                str = str.substring(cc + 1);
            }
            else if (ch == '{') {
                final int cb = StringRoutines.matchingBracketAt(str, c);
                if (cb == -1) {
                    report.addError("No closing bracket '}' for token arg '" + Report.clippedString(str.substring(c), 20) + "'.");
                    return;
                }
                if (str.substring(0, cb + 1).trim().isEmpty()) {
                    System.out.println("B - Empty substring.");
                }
                final Token sub = new Token(str.substring(0, cb + 1), report);
                this.arguments.add(sub);
                str = str.substring(cb + 1);
            }
            else if (ch == '(') {
                final int cb = StringRoutines.matchingBracketAt(str, c);
                if (cb == -1) {
                    report.addError("No closing bracket ')' for token arg '" + Report.clippedString(str.substring(c), 20) + "'.");
                    return;
                }
                if (str.substring(0, cb + 1).trim().isEmpty()) {
                    System.out.println("C - Empty substring.");
                }
                final Token sub = new Token(str.substring(0, cb + 1), report);
                this.arguments.add(sub);
                str = str.substring(cb + 1);
            }
            else if (ch != ' ') {
                int cc;
                for (cc = c; cc < str.length() && StringRoutines.isTokenChar(str.charAt(cc)); ++cc) {}
                if (cc == 0) {
                    str = str.substring(1);
                    report.addError("Empty substring from '" + Report.clippedString(strIn, 20) + "'. Maybe a wrong bracket type '}'?");
                    return;
                }
                final Token sub = new Token(str.substring(0, cc), report);
                this.arguments.add(sub);
                str = str.substring(cc);
            }
            else {
                System.out.println("** Token.handleArgs(): Not handling arg: " + str);
                str = str.substring(1);
            }
        }
        for (int a = this.arguments.size() - 1; a >= 0; --a) {
            if (this.arguments.get(a) == null || this.arguments.get(a).type() == null) {
                this.arguments.remove(a);
            }
        }
    }
    
    void consumeString(final String strIn) {
        final String str = strIn;
        if (str.isEmpty() || str.charAt(0) != '\"') {
            System.out.println("Not a string: " + str);
            return;
        }
        this.name = "\"";
        int c = 1;
        while (c < str.length()) {
            final char ch = str.charAt(c);
            final boolean isQuote = ch == '\"';
            final boolean isEmbedded = ch == '\"' && str.charAt(c - 1) == '\\';
            if (isQuote && !isEmbedded) {
                break;
            }
            ++c;
            if (isEmbedded) {
                this.name = this.name.substring(0, this.name.length() - 1) + "'";
            }
            else {
                this.name += ch;
            }
        }
        this.name += "\"";
    }
    
    String consumeToken(final String strIn) {
        final String str = strIn;
        if (str.isEmpty()) {
            System.out.println("Not a token: " + str);
            return null;
        }
        this.name = "";
        int c = 0;
        while (c < str.length()) {
            final char ch = str.charAt(c++);
            if (!StringRoutines.isTokenChar(ch)) {
                break;
            }
            this.name += ch;
        }
        return str.substring(c).trim();
    }
    
    String consumeParameterName(final String strIn, final int cIn, final boolean store) {
        final String str = strIn;
        if (str.isEmpty()) {
            System.out.println("Not a parameter name: " + str);
            return null;
        }
        if (store) {
            this.parameterLabel = "";
        }
        int c = cIn;
        while (c < str.length()) {
            final char ch = str.charAt(c++);
            if (ch == ':') {
                break;
            }
            if (!store) {
                continue;
            }
            this.parameterLabel += ch;
        }
        final String str2 = str.substring(0, cIn) + str.substring(c);
        return str2.trim();
    }
    
    @Override
    public String toString() {
        return this.format();
    }
    
    public String format() {
        final List<String> lines = new ArrayList<>();
        this.format(lines, 0, false);
        for (int n = 0; n < lines.size() - 1; ++n) {
            if (lines.get(n).contains("(game") || lines.get(n).contains("(match") || lines.get(n).contains("(piece")) {
                mergeNameLinesAt(lines, n);
            }
        }
        compressNumberPairArrayElements(lines);
        mergeArrayLines(lines);
        final StringBuilder sb = new StringBuilder();
        for (final String line : lines) {
            sb.append(line + "\n");
        }
        return sb.toString();
    }
    
    public void format(final List<String> lines, final int depth, final boolean doSplit) {
        String line = indent(depth);
        final String tokenLine = this.formatSingleLine();
        final boolean isEquipmentToken = tokenLine.indexOf("(equipment") == 0;
        final boolean isRulesToken = tokenLine.indexOf("(rules") == 0;
        if (line.length() + tokenLine.length() <= 78 && !doSplit && !isRulesToken && !isEquipmentToken) {
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
        line += this.open;
        if (this.name != null) {
            line += this.name;
        }
        lines.add(line);
        for (final Token arg : this.arguments) {
            final String argStr = arg.formatSingleLine();
            final boolean isEquipmentArg = argStr.indexOf("(equipment") == 0;
            final boolean isRulesArg = argStr.indexOf("(rules") == 0;
            if (indent(depth + 1).length() + argStr.length() > 78 || isEquipmentToken || isEquipmentArg || isRulesToken || isRulesArg) {
                final List<String> subLines = new ArrayList<>();
                arg.format(subLines, depth + 1, isEquipmentToken);
                lines.addAll(subLines);
            }
            else {
                lines.add(indent(depth + 1) + argStr);
            }
        }
        lines.add(indent(depth) + this.close);
    }
    
    static String indent(final int depth) {
        final StringBuilder sb = new StringBuilder();
        for (int d = 0; d < depth; ++d) {
            sb.append("    ");
        }
        return sb.toString();
    }
    
    static void mergeNameLinesAt(final List<String> lines, final int n) {
        if (n >= lines.size()) {
            return;
        }
        final boolean isName = lines.get(n + 1).trim().charAt(0) == '\"';
        if (isName) {
            mergeLinesAt(lines, n);
        }
    }
    
    static void mergeLinesAt(final List<String> lines, final int n) {
        final String line = lines.get(n) + " " + lines.get(n + 1).trim();
        lines.remove(n);
        lines.remove(n);
        lines.add(n, line);
    }
    
    static void mergeArrayLines(final List<String> lines) {
        for (int n = 0; n < lines.size(); ++n) {
            if (isArrayOpen(lines.get(n))) {
                boolean containsClass = false;
                int nn;
                for (nn = n + 1; nn < lines.size(); ++nn) {
                    if (isClass(lines.get(nn))) {
                        containsClass = true;
                    }
                    if (isArrayClose(lines.get(nn))) {
                        break;
                    }
                }
                final boolean isEquipment = n > 0 && lines.get(n - 1).contains("(equipment");
                if (nn < lines.size() && !containsClass && !isEquipment) {
                    ++n;
                    while (n < lines.size() - 1) {
                        final String nextLine = lines.get(n + 1);
                        if (isArrayClose(nextLine)) {
                            break;
                        }
                        if (lines.get(n).length() + nextLine.trim().length() < 78) {
                            mergeLinesAt(lines, n);
                        }
                        else {
                            ++n;
                        }
                    }
                }
            }
        }
    }
    
    static boolean isArrayOpen(final String line) {
        final int numOpen = StringRoutines.numOpenBrackets(line);
        final int numClose = StringRoutines.numCloseBrackets(line);
        return line.contains("{") && numOpen == 1 && numClose == 0;
    }
    
    static boolean isArrayClose(final String line) {
        final int numOpen = StringRoutines.numOpenBrackets(line);
        final int numClose = StringRoutines.numCloseBrackets(line);
        return line.contains("}") && numOpen == 0 && numClose == 1;
    }
    
    static boolean isClass(final String line) {
        return line.contains("(");
    }
    
    static void compressNumberPairArrayElements(final List<String> lines) {
        for (int n = 0; n < lines.size(); ++n) {
            String line = lines.get(n);
            if (line.contains("{ ")) {
                if (line.contains(" }")) {
                    int c = line.indexOf("{ ");
                    if (c >= 0) {
                        final char ch = line.charAt(c + 2);
                        if (ch == '\"' || StringRoutines.isNumeric(ch)) {
                            line = line.substring(0, c + 1) + line.substring(c + 2);
                        }
                    }
                    c = line.indexOf(" }");
                    if (c >= 0) {
                        final char ch = line.charAt(c - 1);
                        if (ch == '\"' || StringRoutines.isNumeric(ch)) {
                            line = line.substring(0, c) + line.substring(c + 1);
                        }
                    }
                    lines.remove(n);
                    lines.add(n, line);
                }
            }
        }
    }
    
    public String formatSingleLine() {
        final StringBuilder sb = new StringBuilder();
        if (this.parameterLabel != null) {
            sb.append(this.parameterLabel + ":");
        }
        if (this.isTerminal()) {
            sb.append(this.name);
            return sb.toString();
        }
        sb.append(this.open);
        if (this.isClass()) {
            sb.append(this.name);
        }
        for (final Token sub : this.arguments) {
            sb.append(" " + sub.formatSingleLine());
        }
        if (this.isArray()) {
            sb.append(" ");
        }
        sb.append(this.close);
        return sb.toString();
    }
    
    public String formatZhangShasha(final String indent, final int depth, final boolean inline, final boolean zhangShasha) {
        String str = "";
        if (this.open == '\0') {
            if (zhangShasha && StringRoutines.isInteger(this.name)) {
                if (this.parameterLabel != null) {
                    str = str + indent + "\"" + this.parameterLabel + ":" + this.name + "\"";
                }
                else {
                    str = str + "\"" + this.name + "\"";
                }
            }
            else {
                if (this.parameterLabel != null) {
                    str = str + indent + this.parameterLabel + ":";
                }
                str += this.name;
            }
            return str;
        }
        if (this.parameterLabel != null) {
            str = str + indent + this.parameterLabel + ":";
        }
        if (this.name != null) {
            final int len = this.length();
            if (this.name.equals("game")) {
                if (zhangShasha) {
                    str = str + this.name + this.open;
                }
                else {
                    str = str + this.open + this.name;
                }
                str = str + " " + this.arguments.get(0).formatZhangShasha("", depth + 1, true, zhangShasha);
                for (final Token arg : this.arguments) {
                    if (arg != null && arg.type() == TokenType.Class) {
                        str = str + indent + "    " + arg.formatZhangShasha("    ", depth + 1, false, zhangShasha) + "\n";
                    }
                }
                str += this.close;
            }
            else if (len < 78 && (depth > 1 || inline)) {
                if (zhangShasha) {
                    str = str + this.name + this.open;
                }
                else {
                    str = str + this.open + this.name;
                }
                for (final Token sub : this.arguments) {
                    str = str + " " + sub.formatZhangShasha("", depth + 1, true, zhangShasha);
                }
                str += this.close;
            }
            else {
                if (zhangShasha) {
                    str = str + this.name + this.open;
                }
                else {
                    str = str + this.open + this.name;
                }
                str += "\n";
                for (final Token sub : this.arguments) {
                    str = str + indent + "    " + sub.formatZhangShasha(indent + "    ", depth + 1, false, zhangShasha) + "\n";
                }
                str = str + indent + this.close;
            }
        }
        else {
            final int len = this.length();
            if (len < 78 || this.shortArguments()) {
                if (zhangShasha) {
                    str += "array(";
                }
                else {
                    str += this.open;
                }
                if (this.name != null) {
                    str += this.name;
                }
                for (final Token sub : this.arguments) {
                    str = str + " " + sub.formatZhangShasha("", depth + 1, true, zhangShasha);
                }
                if (zhangShasha) {
                    str += " )";
                }
                else {
                    str = str + " " + this.close;
                }
            }
            else {
                if (zhangShasha) {
                    str += "array(";
                }
                else {
                    str += this.open;
                }
                if (this.name != null) {
                    str = str + this.name + "\n";
                }
                for (final Token sub : this.arguments) {
                    str = str + indent + "    " + sub.formatZhangShasha(indent + "    ", depth + 1, false, zhangShasha) + "\n";
                }
                if (zhangShasha) {
                    str = str + indent + ")";
                }
                else {
                    str = str + indent + this.close;
                }
            }
        }
        return str;
    }
    
    public boolean shortArguments() {
        int maxLen = 0;
        for (final Token sub : this.arguments) {
            final int len = sub.length();
            if (len > maxLen) {
                maxLen = len;
            }
        }
        return maxLen < 6;
    }
    
    public String dump(final String indent) {
        final String label = "" + this.type().name().charAt(0) + this.type().name().charAt(1) + ": ";
        final StringBuilder sb = new StringBuilder();
        sb.append(label + indent);
        if (this.parameterLabel != null) {
            sb.append(this.parameterLabel + ":");
        }
        if (this.open != '\0') {
            sb.append(this.open);
        }
        if (this.name != null) {
            sb.append(this.name);
        }
        if (this.arguments.size() > 0) {
            sb.append("\n");
            for (final Token arg : this.arguments) {
                sb.append(arg.dump(indent + "    "));
            }
            if (this.close != '\0') {
                sb.append(label + indent + this.close);
            }
        }
        else if (this.close != '\0') {
            sb.append(this.close);
        }
        sb.append("\n");
        return sb.toString();
    }
    
    public enum TokenType
    {
        Class, 
        Array, 
        Terminal
    }
}
