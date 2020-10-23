// 
// Decompiled by Procyon v0.5.36
// 

package main;

import java.util.List;
import java.util.regex.Pattern;

public final class StringRoutines
{
    public static final char[][] brackets;
    public static final int Opening = 0;
    public static final int Closing = 1;
    
    public static boolean isOpenBracket(final char ch) {
        for (int n = 0; n < StringRoutines.brackets.length; ++n) {
            if (ch == StringRoutines.brackets[n][0]) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isCloseBracket(final char ch) {
        for (int n = 0; n < StringRoutines.brackets.length; ++n) {
            if (ch == StringRoutines.brackets[n][1]) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isBracket(final char ch) {
        return isOpenBracket(ch) || isCloseBracket(ch);
    }
    
    public static int numOpenBrackets(final String str) {
        int num = 0;
        for (int c = 0; c < str.length(); ++c) {
            if (isOpenBracket(str.charAt(c))) {
                ++num;
            }
        }
        return num;
    }
    
    public static int numCloseBrackets(final String str) {
        int num = 0;
        for (int c = 0; c < str.length(); ++c) {
            if (isCloseBracket(str.charAt(c))) {
                ++num;
            }
        }
        return num;
    }
    
    public static boolean balancedBrackets(final String str) {
        return numOpenBrackets(str) == numCloseBrackets(str);
    }
    
    public static int numChar(final String str, final char ch) {
        int num = 0;
        for (int c = 0; c < str.length(); ++c) {
            if (str.charAt(c) == ch) {
                ++num;
            }
        }
        return num;
    }
    
    public static int bracketIndex(final char ch, final int openOrClosed) {
        for (int n = 0; n < StringRoutines.brackets.length; ++n) {
            if (StringRoutines.brackets[n][openOrClosed] == ch) {
                return n;
            }
        }
        return -1;
    }
    
    public static int matchingBracketAt(final String str, final int from) {
        int c = from;
        final char ch = str.charAt(c);
        final int bid = bracketIndex(ch, 0);
        if (bid == -1) {
            System.out.println("** Specified char '" + ch + "' is not an open bracket.");
            return -1;
        }
        int bracketDepth = 0;
        boolean inString = false;
        while (c < str.length()) {
            final char chB = str.charAt(c);
            if (chB == '\"') {
                inString = !inString;
            }
            if (!inString) {
                final char chA = (c == 0) ? '?' : str.charAt(c - 1);
                if (chB == StringRoutines.brackets[bid][0]) {
                    if (chA != '(' || chB != '<') {
                        ++bracketDepth;
                    }
                }
                else if (chB == StringRoutines.brackets[bid][1] && (chA != '(' || chB != '>')) {
                    --bracketDepth;
                }
            }
            if (bracketDepth == 0) {
                break;
            }
            ++c;
        }
        if (c >= str.length()) {
            return -1;
        }
        return c;
    }
    
    public static int matchingQuoteAt(final String str, final int from) {
        int c = from;
        final char ch = str.charAt(c);
        if (ch != '\"') {
            throw new IllegalArgumentException("String expected but no opening \" found.");
        }
        ++c;
        while (c < str.length()) {
            final char chC = str.charAt(c);
            switch (chC) {
                case '\\': {
                    if (c < str.length() - 1 && str.charAt(c + 1) == '\"') {
                        ++c;
                        break;
                    }
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
    
    public static String toDromedaryCase(final String className) {
        return className.substring(0, 1).toLowerCase() + className.substring(1);
    }
    
    public static final String highlightText(final String fullText, final String highlight, final String tag, final String colour) {
        final String replacement = "<" + tag + " color=" + colour + ">" + highlight + "</" + tag + ">";
        System.out.println(highlight + " --> " + replacement);
        return fullText.replace(highlight, replacement);
    }
    
    public static final String escapeText(final String text) {
        return text.replace("&", "&amp;").replace("'", "&apos;").replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;").replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;").replace(" ", "&nbsp;").replace("\n", "<br/>");
    }
    
    public static final String cleanGameName(final String gameName) {
        return gameName.trim().replaceAll(Pattern.quote(" "), "_").replaceAll(Pattern.quote(".lud"), "").replaceAll(Pattern.quote("'"), "").replaceAll(Pattern.quote("("), "").replaceAll(Pattern.quote(")"), "");
    }
    
    public static final String cleanWhitespace(final String str) {
        return str.trim().replaceAll("\\s+", " ");
    }
    
    public static boolean isInteger(final String str) {
        try {
            Integer.parseInt(str);
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }
    
    public static boolean isFloat(final String str) {
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
    
    public static boolean isDouble(final String str) {
        try {
            Double.parseDouble(str);
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }
    
    public static boolean isDigit(final char ch) {
        return ch >= '0' && ch <= '9';
    }
    
    public static boolean isLetter(final char ch) {
        return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z');
    }
    
    public static boolean isNumeric(final char ch) {
        return (ch >= '0' && ch <= '9') || ch == '.' || ch == 'e' || ch == '-';
    }
    
    public static String lowerCaseInitial(final String str) {
        if (str.length() < 1) {
            return "";
        }
        return Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }
    
    public static String upperCaseInitial(final String str) {
        if (str.length() < 1) {
            return "";
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
    
    public static String[] upperCaseInitialEach(final String... strings) {
        final String[] ret = new String[strings.length];
        for (int i = 0; i < ret.length; ++i) {
            ret[i] = upperCaseInitial(strings[i]);
        }
        return ret;
    }
    
    public static boolean isToken(final String str) {
        final int lpos = str.indexOf(58);
        if (lpos == -1) {
            return false;
        }
        for (int c = 0; c < lpos; ++c) {
            if (!isTokenChar(str.charAt(c))) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isName(final String str) {
        final int lpos = str.indexOf(58);
        if (lpos == -1) {
            return false;
        }
        for (int c = 0; c < lpos; ++c) {
            if (!isNameChar(str.charAt(c))) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isCoordinate(final String str) {
        if (str == null) {
            return false;
        }
        int c = str.length() - 1;
        if (!isDigit(str.charAt(c))) {
            return false;
        }
        while (c >= 0 && isDigit(str.charAt(c))) {
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
        while (c >= 0 && isLetter(str.charAt(c))) {
            --c;
        }
        return c < 0;
    }
    
    public static boolean isVisibleChar(final char ch) {
        return ch >= ' ' && ch < '\u007f';
    }
    
    public static boolean isNameChar(final char ch) {
        return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9') || ch == '_' || ch == '-';
    }
    
    public static boolean isTokenChar(final char ch) {
        return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9') || ch == '_' || ch == '\"' || ch == '-' || ch == '.' || ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '%' || ch == '=' || ch == '!' || ch == 'v' || ch == '^' || ch == '~' || ch == '<' || ch == '>' || ch == '&' || ch == '|' || ch == '#';
    }
    
    public static int numberAtEnd(final String strIn) {
        final String str = strIn.trim();
        boolean found = false;
        int index = 0;
        int tens = 1;
        for (int n = str.length() - 1; n >= 0; --n) {
            final char ch = str.charAt(n);
            if (!isDigit(ch)) {
                break;
            }
            found = true;
            index += (ch - '0') * tens;
            tens *= 10;
        }
        return found ? index : -1;
    }
    
    public static String floatToFraction(final float fIn, final int factor) {
        final StringBuilder sb = new StringBuilder();
        float f = fIn;
        if (f < 0.0f) {
            sb.append('-');
            f = -f;
        }
        final long l = (long)f;
        if (l != 0L) {
            sb.append(l);
        }
        f -= l;
        float error = Math.abs(f);
        int bestDenominator = 1;
        for (int i = 2; i <= factor; ++i) {
            final float error2 = Math.abs(f - Math.round(f * i) / (float)i);
            if (error2 < error) {
                error = error2;
                bestDenominator = i;
            }
        }
        if (bestDenominator > 1) {
            sb.append(Math.round(f * bestDenominator)).append('/').append(bestDenominator);
        }
        else {
            sb.append(Math.round(f));
        }
        return sb.toString();
    }
    
    public static String join(final String joinStr, final List<String> strings) {
        return join(joinStr, strings.toArray(new String[strings.size()]));
    }
    
    public static String join(final String joinStr, final String... strings) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strings.length; ++i) {
            if (i > 0) {
                sb.append(joinStr);
            }
            sb.append(strings[i]);
        }
        return sb.toString();
    }
    
    public static String quote(final String str) {
        return "\"" + str + "\"";
    }
    
    public static String removeTrailingNumbers(final String string) {
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
    
    public static final String gameName(final String str) {
        int c = str.indexOf("(game ");
        if (c < 0) {
            return null;
        }
        for (c += 5; c < str.length() && str.charAt(c) != '\"'; ++c) {}
        if (c >= str.length()) {
            throw new RuntimeException("gameName(): Game name not found.");
        }
        final int cc = matchingQuoteAt(str, c);
        if (cc < 0 || cc >= str.length()) {
            throw new RuntimeException("gameName(): Game name not found.");
        }
        return str.substring(c + 1, cc);
    }
    
    public static int levenshteinDistance(final String strA, final String strB) {
        final String a = strA.toLowerCase();
        final String b = strB.toLowerCase();
        final int[] costs = new int[b.length() + 1];
        for (int j = 0; j < costs.length; ++j) {
            costs[j] = j;
        }
        for (int i = 1; i <= a.length(); ++i) {
            costs[0] = i;
            int nw = i - 1;
            for (int k = 1; k <= b.length(); ++k) {
                final int cj = Math.min(1 + Math.min(costs[k], costs[k - 1]), (a.charAt(i - 1) == b.charAt(k - 1)) ? nw : (nw + 1));
                nw = costs[k];
                costs[k] = cj;
            }
        }
        return costs[b.length()];
    }
    
    static {
        brackets = new char[][] { { '(', ')' }, { '{', '}' }, { '[', ']' }, { '<', '>' } };
    }
}
