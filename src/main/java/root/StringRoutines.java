package root;/*
 * Decompiled with CFR 0.150.
 */

import java.util.List;
import java.util.regex.Pattern;

public final class StringRoutines {
    public static final char[][] brackets = new char[][]{{'(', ')'}, {'{', '}'}, {'[', ']'}, {'<', '>'}};
    public static final int Opening = 0;
    public static final int Closing = 1;

    public static boolean isOpenBracket(char ch) {
        for (int n = 0; n < brackets.length; ++n) {
            if (ch != brackets[n][0]) continue;
            return true;
        }
        return false;
    }

    public static boolean isCloseBracket(char ch) {
        for (int n = 0; n < brackets.length; ++n) {
            if (ch != brackets[n][1]) continue;
            return true;
        }
        return false;
    }

    public static boolean isBracket(char ch) {
        return StringRoutines.isOpenBracket(ch) || StringRoutines.isCloseBracket(ch);
    }

    public static int numOpenBrackets(String str) {
        int num = 0;
        for (int c = 0; c < str.length(); ++c) {
            if (!StringRoutines.isOpenBracket(str.charAt(c))) continue;
            ++num;
        }
        return num;
    }

    public static int numCloseBrackets(String str) {
        int num = 0;
        for (int c = 0; c < str.length(); ++c) {
            if (!StringRoutines.isCloseBracket(str.charAt(c))) continue;
            ++num;
        }
        return num;
    }

    public static boolean balancedBrackets(String str) {
        return StringRoutines.numOpenBrackets(str) == StringRoutines.numCloseBrackets(str);
    }

    public static int numChar(String str, char ch) {
        int num = 0;
        for (int c = 0; c < str.length(); ++c) {
            if (str.charAt(c) != ch) continue;
            ++num;
        }
        return num;
    }

    public static int bracketIndex(char ch, int openOrClosed) {
        for (int n = 0; n < brackets.length; ++n) {
            if (brackets[n][openOrClosed] != ch) continue;
            return n;
        }
        return -1;
    }

    public static int matchingBracketAt(String str, int from) {
        int c = from;
        char ch = str.charAt(c);
        int bid = StringRoutines.bracketIndex(ch, 0);
        if (bid == -1) {
            System.out.println("** Specified char '" + ch + "' is not an open bracket.");
            return -1;
        }
        int bracketDepth = 0;
        boolean inString = false;
        while (c < str.length()) {
            char chB = str.charAt(c);
            if (chB == '\"') {
                boolean bl = inString = !inString;
            }
            if (!inString) {
                int chA;
                int n = chA = c == 0 ? 63 : (int)str.charAt(c - 1);
                if (chB == brackets[bid][0]) {
                    if (chA != 40 || chB != '<') {
                        ++bracketDepth;
                    }
                } else if (chB == brackets[bid][1] && (chA != 40 || chB != '>')) {
                    --bracketDepth;
                }
            }
            if (bracketDepth == 0) break;
            ++c;
        }
        if (c >= str.length()) {
            return -1;
        }
        return c;
    }

    public static int matchingQuoteAt(String str, int from) {
        int c = from;
        char ch = str.charAt(c);
        if (ch != '\"') {
            throw new IllegalArgumentException("String expected but no opening \" found.");
        }
        ++c;
        while (c < str.length()) {
            char chC = str.charAt(c);
            switch (chC) {
                case '\\': {
                    if (c >= str.length() - 1 || str.charAt(c + 1) != '\"') break;
                    ++c;
                    break;
                }
                case '\"': {
                    return c;
                }
            }
            ++c;
        }
        return -1;
    }

    public static String toDromedaryCase(String className) {
        return className.substring(0, 1).toLowerCase() + className.substring(1);
    }

    public static final String highlightText(String fullText, String highlight, String tag, String colour) {
        String replacement = "<" + tag + " color=" + colour + ">" + highlight + "</" + tag + ">";
        System.out.println(highlight + " --> " + replacement);
        return fullText.replace(highlight, replacement);
    }

    public static final String escapeText(String text) {
        return text.replace("&", "&amp;").replace("'", "&apos;").replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;").replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;").replace(" ", "&nbsp;").replace("\n", "<br/>");
    }

    public static final String cleanGameName(String gameName) {
        return gameName.trim().replaceAll(Pattern.quote(" "), "_").replaceAll(Pattern.quote(".lud"), "").replaceAll(Pattern.quote("'"), "").replaceAll(Pattern.quote("("), "").replaceAll(Pattern.quote(")"), "");
    }

    public static final String cleanWhitespace(String str) {
        return str.trim().replaceAll("\\s+", " ");
    }

    public static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean isFloat(String str) {
        try {
            Float.parseFloat(str);
        }
        catch (Exception e) {
            try {
                Integer.parseInt(str);
            }
            catch (Exception e2) {
                return false;
            }
        }
        return true;
    }

    public static boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }

    public static boolean isLetter(char ch) {
        return ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z';
    }

    public static boolean isNumeric(char ch) {
        return ch >= '0' && ch <= '9' || ch == '.' || ch == 'e' || ch == '-';
    }

    public static String lowerCaseInitial(String str) {
        if (str.length() < 1) {
            return "";
        }
        return Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }

    public static String upperCaseInitial(String str) {
        if (str.length() < 1) {
            return "";
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    public static String[] upperCaseInitialEach(String ... strings) {
        String[] ret = new String[strings.length];
        for (int i = 0; i < ret.length; ++i) {
            ret[i] = StringRoutines.upperCaseInitial(strings[i]);
        }
        return ret;
    }

    public static boolean isToken(String str) {
        int lpos = str.indexOf(58);
        if (lpos == -1) {
            return false;
        }
        for (int c = 0; c < lpos; ++c) {
            if (StringRoutines.isTokenChar(str.charAt(c))) continue;
            return false;
        }
        return true;
    }

    public static boolean isName(String str) {
        int lpos = str.indexOf(58);
        if (lpos == -1) {
            return false;
        }
        for (int c = 0; c < lpos; ++c) {
            if (StringRoutines.isNameChar(str.charAt(c))) continue;
            return false;
        }
        return true;
    }

    public static boolean isCoordinate(String str) {
        if (str == null) {
            return false;
        }
        int c = str.length() - 1;
        if (!StringRoutines.isDigit(str.charAt(c))) {
            return false;
        }
        while (c >= 0 && StringRoutines.isDigit(str.charAt(c))) {
            --c;
        }
        if (c < 0) {
            return true;
        }
        if (c > 2) {
            return false;
        }
        if (c > 1 && str.length() > 1 && str.charAt(0) != str.charAt(1)) {
            return false;
        }
        while (c >= 0 && StringRoutines.isLetter(str.charAt(c))) {
            --c;
        }
        return c < 0;
    }

    public static boolean isVisibleChar(char ch) {
        return ch >= ' ' && ch < '\u007f';
    }

    public static boolean isNameChar(char ch) {
        return ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z' || ch >= '0' && ch <= '9' || ch == '_' || ch == '-';
    }

    public static boolean isTokenChar(char ch) {
        return ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z' || ch >= '0' && ch <= '9' || ch == '_' || ch == '\"' || ch == '-' || ch == '.' || ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '%' || ch == '=' || ch == '!' || ch == 'v' || ch == '^' || ch == '~' || ch == '<' || ch == '>' || ch == '&' || ch == '|' || ch == '#';
    }

    public static int numberAtEnd(String strIn) {
        char ch;
        String str = strIn.trim();
        boolean found = false;
        int index = 0;
        int tens = 1;
        for (int n = str.length() - 1; n >= 0 && StringRoutines.isDigit(ch = str.charAt(n)); --n) {
            found = true;
            index += (ch - 48) * tens;
            tens *= 10;
        }
        return found ? index : -1;
    }

    public static String floatToFraction(float fIn, int factor) {
        long l;
        StringBuilder sb = new StringBuilder();
        float f = fIn;
        if (f < 0.0f) {
            sb.append('-');
            f = -f;
        }
        if ((l = (long)f) != 0L) {
            sb.append(l);
        }
        float error = Math.abs(f -= (float)l);
        int bestDenominator = 1;
        for (int i = 2; i <= factor; ++i) {
            float error2 = Math.abs(f - (float)Math.round(f * (float)i) / (float)i);
            if (!(error2 < error)) continue;
            error = error2;
            bestDenominator = i;
        }
        if (bestDenominator > 1) {
            sb.append(Math.round(f * (float)bestDenominator)).append('/').append(bestDenominator);
        } else {
            sb.append(Math.round(f));
        }
        return sb.toString();
    }

    public static String join(String joinStr, List<String> strings) {
        return StringRoutines.join(joinStr, strings.toArray(new String[strings.size()]));
    }

    public static String join(String joinStr, String ... strings) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strings.length; ++i) {
            if (i > 0) {
                sb.append(joinStr);
            }
            sb.append(strings[i]);
        }
        return sb.toString();
    }

    public static String quote(String str) {
        return "\"" + str + "\"";
    }

    public static String removeTrailingNumbers(String string) {
        String newString = string;
        if (!newString.chars().allMatch(Character::isDigit)) {
            int valueToRemove = 0;
            for (int i = newString.length() - 1; i >= 0 && newString.charAt(i) >= '0' && newString.charAt(i) <= '9'; --i) {
                ++valueToRemove;
            }
            newString = newString.substring(0, newString.length() - valueToRemove);
        }
        return newString;
    }

    public static final String gameName(String str) {
        int c = str.indexOf("(game ");
        if (c < 0) {
            return null;
        }
        c += 5;
        while (c < str.length() && str.charAt(c) != '\"') {
            ++c;
        }
        if (c >= str.length()) {
            throw new RuntimeException("gameName(): Game name not found.");
        }
        int cc = StringRoutines.matchingQuoteAt(str, c);
        if (cc < 0 || cc >= str.length()) {
            throw new RuntimeException("gameName(): Game name not found.");
        }
        return str.substring(c + 1, cc);
    }

    public static int levenshteinDistance(String strA, String strB) {
        String a = strA.toLowerCase();
        String b = strB.toLowerCase();
        int[] costs = new int[b.length() + 1];
        for (int j = 0; j < costs.length; ++j) {
            costs[j] = j;
        }
        for (int i = 1; i <= a.length(); ++i) {
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); ++j) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }
}

