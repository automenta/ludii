// 
// Decompiled by Procyon v0.5.36
// 

package language.parser;

import exception.UnusedOptionException;
import main.FileHandling;
import main.StringRoutines;
import main.grammar.Description;
import main.grammar.Report;
import main.options.*;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Expander
{
    private static final int MAX_EXPANSIONS = 1000;
    private static final int MAX_CHARACTERS = 1000000;
    private static final int MAX_RANGE = 1000;
    private static final int MAX_DEFINE_ARGS = 20;
    public static final String DEFINE_PARAMETER_PLACEHOLDER = "~";
    
    private Expander() {
    }
    
    public static void expand(final Description description, final UserSelections userSelections, final Report report, final boolean isVerbose) {
        if (isVerbose) {
            System.out.println("+++++++++++++++++++++\nExpanding:\n" + description.raw());
        }
        String str = description.raw();
        str = removeComments(str);
        str = realiseOptions(str, description, userSelections, report);
        if (report.isError()) {
            return;
        }
        str = realiseRulesets(str, description, report);
        if (report.isError()) {
            return;
        }
        str = expandDefines(str, report);
        if (report.isError()) {
            return;
        }
        str = removeComments(str);
        str = expandRanges(str, report);
        if (report.isError()) {
            return;
        }
        str = extractMetadata(str, description, userSelections, report);
        if (report.isError()) {
            return;
        }
        str = cleanUp(str, report);
        if (report.isError()) {
            return;
        }
        if (isVerbose) {
            System.out.println("Cleaned up:\n" + str);
        }
        if (str == null || str.trim().isEmpty()) {
            str = description.metadata();
        }
        tokenise(str, description, report, isVerbose);
        description.setExpanded(description.tokenForest().toString());
    }
    
    static String cleanUp(final String strIn, final Report report) {
        String str;
        for (str = strIn, str = str.replaceAll("\n", " "), str = str.replaceAll("\r", " "), str = str.replaceAll("\t", " "); str.contains("    "); str = str.replaceAll("    ", " ")) {}
        while (str.contains("  ")) {
            str = str.replaceAll("  ", " ");
        }
        for (str = str.replaceAll(" \\)", "\\)"), str = str.replaceAll("\\( ", "\\("), str = str.replaceAll(" \\}", "\\}"), str = str.replaceAll("\\{ ", "\\{"); str.contains(": "); str = str.replaceAll(": ", ":")) {}
        str = handleDoubleBrackets(str, report);
        if (report.isError()) {
            return null;
        }
        return str;
    }
    
    public static void tokenise(final String str, final Description description, final Report report, final boolean isVerbose) {
        description.tokenForest().populate(str, report);
        if (isVerbose) {
            System.out.println("\nToken tree:\n" + description.tokenForest().tokenTree());
        }
        if (description.tokenForest().tokenTree() == null || description.tokenForest().tokenTree().type() == null) {
            report.addError("Expander can't tokenise the game description.");
        }
    }
    
    private static String handleDoubleBrackets(final String strIn, final Report report) {
        if (!strIn.contains("((")) {
            return strIn;
        }
        String str = strIn;
        while (true) {
            final int c = str.indexOf("((");
            if (c < 0) {
                return str;
            }
            final int cc = StringRoutines.matchingBracketAt(str, c);
            if (cc < 0 || cc >= str.length()) {
                report.addError("Couldn't close clause '" + Report.clippedString(str.substring(c), 20) + "'.");
                return null;
            }
            if (str.charAt(cc) != ')' || str.charAt(cc - 1) != ')') {
                report.addError("Opening bracket pair '((' in '" + Report.clippedString(str.substring(c), 20) + "' does not have closing pair.");
                return null;
            }
            str = str.substring(0, cc) + str.substring(cc + 1);
            str = str.substring(0, c) + str.substring(c + 1);
        }
    }
    
    private static String realiseOptions(final String strIn, final Description description, final UserSelections userSelections, final Report report) {
        description.gameOptions().clear();
        if (!strIn.contains("(option \"")) {
            return strIn;
        }
        String str = strIn;
        str = extractOptions(str, description, report);
        if (report.isError() || str == null) {
            return null;
        }
        int[] optionSelections;
        try {
            optionSelections = description.gameOptions().computeOptionSelections(userSelections.selectedOptionStrings());
        }
        catch (UnusedOptionException e) {
            System.err.println("Reverting to default options for game due to unrecognised option being specified!");
            userSelections.setSelectOptionStrings(new ArrayList<>());
            optionSelections = description.gameOptions().computeOptionSelections(userSelections.selectedOptionStrings());
        }
        for (int cat = 0; cat < description.gameOptions().numCategories(); ++cat) {
            final OptionCategory category = description.gameOptions().categories().get(cat);
            if (category.options().size() > 0) {
                final Option option = category.options().get(optionSelections[cat]);
                if (option.arguments().isEmpty()) {
                    return str;
                }
                str = expandOption(str, option, report);
                if (report.isError() || str == null) {
                    return null;
                }
            }
        }
        return str;
    }
    
    private static final String extractOptions(final String strIn, final Description description, final Report report) {
        String str;
        int c;
        int cc;
        for (str = strIn; str.contains("(option \""); str = str.substring(0, c) + str.substring(cc)) {
            c = str.indexOf("(option \"");
            cc = StringRoutines.matchingBracketAt(str, c);
            if (cc < 0 || cc >= str.length()) {
                report.addError("Couldn't close clause '" + Report.clippedString(str.substring(c), 20) + "'.");
                return null;
            }
            ++cc;
            final OptionCategory category = new OptionCategory(str.substring(c, cc));
            for (final Option option : category.options()) {
                for (final String header : option.menuHeadings()) {
                    if (header.contains("/")) {
                        report.addError("Bad '/' in option header \"" + header + "\".");
                        return null;
                    }
                }
            }
            description.gameOptions().add(category);
        }
        return str;
    }
    
    private static String expandOption(final String strIn, final Option option, final Report report) {
        String str = strIn;
        for (final OptionArgument arg : option.arguments()) {
            final String name = arg.name();
            if (name == null) {
                report.addError("Some option arguments are named but this one is not.");
                return null;
            }
            final String marker = "<" + option.tag() + ":" + name + ">";
            int iterations = 0;
            while (str.contains(marker)) {
                if (++iterations > 1000) {
                    report.addError("An option has more than 1000 expansions.");
                    return null;
                }
                if (str.length() > 1000000) {
                    report.addError("The option " + option.toString() + " has more than " + 1000000 + " characters.");
                    return null;
                }
                final int c = str.indexOf(marker);
                str = str.substring(0, c) + arg.expression() + str.substring(c + marker.length());
            }
        }
        final String marker2 = "<" + option.tag() + ">";
        int iterations2 = 0;
        while (str.contains(marker2)) {
            if (++iterations2 > 1000) {
                report.addError("An option has more than 1000 expansions.");
                return null;
            }
            if (str.length() > 1000000) {
                report.addError("The option " + option.toString() + " has more than " + 1000000 + " characters.");
                return null;
            }
            final int c2 = str.indexOf(marker2);
            final int index = 0;
            str = str.substring(0, c2) + option.arguments().get(0).expression() + str.substring(c2 + marker2.length());
        }
        return str;
    }
    
    private static String realiseRulesets(final String strIn, final Description description, final Report report) {
        if (!strIn.contains("(rulesets")) {
            return strIn;
        }
        final String str = extractRulesets(strIn, description, report);
        return str;
    }
    
    private static final String extractRulesets(final String strIn, final Description description, final Report report) {
        description.clearRulesets();
        String str = strIn;
        int c = str.indexOf("(rulesets");
        if (c < 0) {
            report.addError("Rulesets not found.");
            return null;
        }
        int cc = StringRoutines.matchingBracketAt(str, c);
        if (cc < 0) {
            report.addError("No closing bracket ')' in rulesets '" + Report.clippedString(str.substring(c), 20) + "'.");
            return null;
        }
        String rulesetsStr = str.substring(c + 8, cc - 1).trim();
        str = str.substring(0, c) + str.substring(cc);
        while (true) {
            c = rulesetsStr.indexOf("(ruleset ");
            if (c < 0) {
                while (true) {
                    c = str.indexOf("(rulesets");
                    if (c < 0) {
                        return str;
                    }
                    cc = StringRoutines.matchingBracketAt(str, c);
                    if (cc < 0) {
                        report.addError("No closing bracket ')' in extra rulesets '" + Report.clippedString(str.substring(c), 20) + "'.");
                        return null;
                    }
                    str = str.substring(0, c) + str.substring(cc);
                }
            }
            else {
                cc = StringRoutines.matchingBracketAt(rulesetsStr, c);
                if (cc < 0) {
                    report.addError("No closing bracket ')' in ruleset '" + Report.clippedString(rulesetsStr.substring(c), 20) + "'.");
                    return null;
                }
                String rulesetStr = rulesetsStr.substring(c, cc + 1);
                for (int priorityIndex = cc + 1; priorityIndex < rulesetsStr.length() && rulesetsStr.charAt(priorityIndex) == '*'; ++priorityIndex) {
                    rulesetStr += '*';
                }
                final Ruleset ruleset = new Ruleset(rulesetStr);
                description.add(ruleset);
                rulesetsStr = rulesetsStr.substring(cc + 1);
            }
        }
    }
    
    private static String expandDefines(final String strIn, final Report report) {
        final Define knownAIDefine = loadKnownAIDefine(strIn, report);
        if (report.isError()) {
            return null;
        }
        int defineIterations = 0;
        String str = strIn;
        while (true) {
            final String strDef = expandDefinesPass(str, knownAIDefine, report);
            if (report.isError()) {
                return null;
            }
            if (str.equals(strDef)) {
                return str;
            }
            str = strDef;
            if (++defineIterations > 1000 || str.length() > 1000000) {
                report.addError("Suspected infinitely recursive define.");
                return null;
            }
        }
    }
    
    private static String expandDefinesPass(final String strIn, final Define knownAIDefine, final Report report) {
        final List<Define> defines = new ArrayList<>();
        final Map<String, Define> knownDefines = KnownDefines.getKnownDefines().knownDefines();
        String str = extractDefines(strIn, defines, report);
        if (report.isError()) {
            return null;
        }
        boolean didExpandAny;
        do {
            didExpandAny = false;
            final boolean[] didExpand = { false };
            do {
                didExpand[0] = false;
                for (final Define def : defines) {
                    if (str.contains(def.tag())) {
                        str = expandDefine(str, def, didExpand, report);
                        if (report.isError()) {
                            return null;
                        }
                        continue;
                    }
                }
                if (didExpand[0]) {
                    didExpandAny = true;
                }
            } while (didExpand[0]);
            do {
                didExpand[0] = false;
                for (final Map.Entry<String, Define> entry : knownDefines.entrySet()) {
                    final Define def2 = entry.getValue();
                    if (str.contains(def2.tag())) {
                        str = expandDefine(str, def2, didExpand, report);
                        if (report.isError()) {
                            return null;
                        }
                        continue;
                    }
                }
                if (didExpand[0]) {
                    didExpandAny = true;
                }
            } while (didExpand[0]);
            if (knownAIDefine != null && str.contains(knownAIDefine.tag())) {
                didExpand[0] = false;
                str = expandDefine(str, knownAIDefine, didExpand, report);
                if (report.isError()) {
                    return null;
                }
                if (!didExpand[0]) {
                    continue;
                }
                didExpandAny = true;
            }
        } while (didExpandAny);
        return str;
    }
    
    private static String extractDefines(final String strIn, final List<Define> defines, final Report report) {
        final int[] extent = new int[2];
        String str = strIn;
        while (str.contains("(define ")) {
            final Define define = interpretDefine(str, extent, report);
            if (report.isError()) {
                return null;
            }
            if (define == null) {
                System.out.println("** Failed to load define:\n" + str);
            }
            else {
                defines.add(define);
                str = str.substring(0, extent[0]) + str.substring(extent[1] + 1);
            }
        }
        return str;
    }
    
    public static Define interpretDefine(final String str, final int[] extent, final Report report) {
        int c = str.indexOf("(define ");
        if (c < 0) {
            return null;
        }
        final int cc = StringRoutines.matchingBracketAt(str, c);
        if (cc < 0) {
            report.addError("Could not close '(define ...' in '" + Report.clippedString(str.substring(c), 20) + "'.");
            return null;
        }
        String desc = str.substring(c + 1, cc).trim();
        if (extent != null) {
            extent[0] = c;
            extent[1] = cc;
        }
        c = 0;
        int numQuotes = 0;
        while (c < desc.length()) {
            if (desc.charAt(c) == '\"') {
                ++numQuotes;
            }
            if (numQuotes >= 2) {
                break;
            }
            ++c;
        }
        if (numQuotes < 2) {
            report.addError("Badly fomred '(define \"name\"...' in '" + Report.clippedString(desc, 20) + "'.");
            return null;
        }
        final int openingQuoteIdx = desc.indexOf("\"");
        final int closingQuoteIdx = desc.indexOf("\"", openingQuoteIdx + 1);
        final String key = desc.substring(openingQuoteIdx, closingQuoteIdx + 1);
        desc = desc.substring(c + 1).trim();
        final Define define = new Define(key, desc);
        return define;
    }
    
    private static String expandDefine(final String strIn, final Define define, final boolean[] didExpand, final Report report) {
        String str = strIn;
        final int len = define.tag().length();
        int iterations = 0;
        int c = 0;
        while (++iterations <= 1000) {
            if (str.length() > 1000000) {
                report.addError("Define has more than 1000000 characters '" + Report.clippedString(str, 20) + "'.");
                return null;
            }
            c = str.indexOf(define.tag(), c + 1);
            if (c == -1) {
                str = str.replace("<DELETE_ME>", "");
                return str;
            }
            if (protectedSubstring(str, c)) {
                continue;
            }
            int cc = c + len - 1;
            if (str.charAt(c - 1) == '(') {
                --c;
                cc = StringRoutines.matchingBracketAt(str, c);
                if (cc < 0 || cc >= str.length()) {
                    report.addError("Failed to handle parameter in define '" + Report.clippedString(str.substring(c), 20) + "'.");
                    return null;
                }
            }
            final String argString = str.substring(c, cc + 1).trim();
            final List<String> args = extractDefineArgs(argString, report);
            if (report.isError()) {
                return null;
            }
            final String exprn = expandDefineArgs(define, args, report);
            if (report.isError()) {
                return null;
            }
            str = str.substring(0, c) + exprn + str.substring(cc + 1);
            didExpand[0] = true;
        }
        report.addError("Define has more than 1000 expansions '" + Report.clippedString(str, 20) + "'.");
        return null;
    }
    
    private static final List<String> extractDefineArgs(final String argString, final Report report) {
        final List<String> args = new ArrayList<>();
        String str = argString.trim();
        if (str.charAt(0) == '(') {
            final int cc = StringRoutines.matchingBracketAt(str, 0);
            if (cc == -1) {
                report.addError("Failed to read bracketed clause '(...)' from '" + Report.clippedString(str, 20) + "'.");
                return null;
            }
            str = str.substring(1, cc);
        }
        int a;
        for (a = 0; a < str.length() && !Character.isWhitespace(str.charAt(a)); ++a) {}
        if (a >= str.length()) {
            return args;
        }
        int aTo;
        for (str = str.substring(a).trim(); !str.isEmpty(); str = str.substring(aTo).trim()) {
            aTo = 0;
            if (StringRoutines.isOpenBracket(str.charAt(0))) {
                aTo = StringRoutines.matchingBracketAt(str, 0);
                if (aTo == -1) {
                    report.addError("Failed to read bracketed clause from '" + Report.clippedString(str, 20) + "'.");
                    return null;
                }
                ++aTo;
            }
            else if (str.charAt(0) == '\"') {
                aTo = StringRoutines.matchingQuoteAt(str, 0);
                if (aTo == -1) {
                    report.addError("Failed to read quoted clause '\"...\"' from '" + Report.clippedString(str, 20) + "'.");
                    return null;
                }
                ++aTo;
            }
            else {
                aTo = 0;
                while (aTo < str.length() && !Character.isWhitespace(str.charAt(aTo))) {
                    if (str.charAt(aTo) == ':' && StringRoutines.isOpenBracket(str.charAt(aTo + 1))) {
                        aTo = StringRoutines.matchingBracketAt(str, aTo + 1);
                        if (aTo == -1) {
                            report.addError("Failed to read bracketed clause '{...}' from '" + Report.clippedString(str, 20) + "'.");
                            return null;
                        }
                        ++aTo;
                        break;
                    }
                    else {
                        ++aTo;
                    }
                }
            }
            if (aTo >= str.length()) {
                aTo = str.length();
            }
            final String arg = str.substring(0, aTo);
            args.add(arg);
        }
        return args;
    }
    
    private static String expandDefineArgs(final Define define, final List<String> args, final Report report) {
        String exprn = define.expression();
        for (int n = 0; n < 20; ++n) {
            final String marker = "#" + (n + 1);
            if (!exprn.contains(marker)) {
                break;
            }
            int innerIterations = 0;
            while (exprn.contains(marker)) {
                if (++innerIterations > 1000) {
                    report.addError("Define has more than 1000 expansions '" + Report.clippedString(exprn, 20) + "'.");
                    return null;
                }
                if (exprn.length() > 1000000) {
                    report.addError("Define has more than 1000000 characters '" + Report.clippedString(exprn, 20) + "'.");
                    return null;
                }
                final int m = exprn.indexOf(marker);
                String arg = "<DELETE_ME>";
                if (n < args.size() && !args.get(n).equals("~")) {
                    arg = args.get(n);
                }
                exprn = exprn.substring(0, m) + arg + exprn.substring(m + 2);
                if (arg.charAt(0) == '#') {
                    break;
                }
            }
        }
        return exprn;
    }
    
    public static boolean protectedSubstring(final String str, final int fromIndex) {
        final String[] safeTokens = { "game", "match", "instance" };
        int c;
        for (c = fromIndex - 1; c >= 0 && !StringRoutines.isTokenChar(str.charAt(c)); --c) {}
        if (c < 0) {
            System.out.println("** Warning: Failed to find previous token (probably from define).");
            System.out.println("** fromIndex=" + fromIndex + ", str:=\n" + str);
            return false;
        }
        String token = "";
        while (c >= 0) {
            final char ch = str.charAt(c);
            if (!StringRoutines.isTokenChar(ch)) {
                break;
            }
            token = ch + token;
            --c;
        }
        if (c < 0) {
            System.out.println("** Warning: Failed to read previous token (probably from define).");
            System.out.println("** fromIndex=" + fromIndex + ", str:=\n" + str);
            return false;
        }
        for (final String safeToken : safeTokens) {
            if (token.equals(safeToken)) {
                return true;
            }
        }
        return false;
    }
    
    private static Define loadKnownAIDefine(final String strIn, final Report report) {
        if (!strIn.contains("(ai") || !strIn.contains("_ai")) {
            return null;
        }
        Define knownAIDefine = null;
        final String gameName = StringRoutines.gameName(strIn);
        final int c = strIn.indexOf("_ai\"");
        if (c >= 0) {
            int cc;
            for (cc = c; cc >= 0 && strIn.charAt(cc) != '\"'; --cc) {}
            final String aiName = strIn.substring(cc + 1, c);
            if (!aiName.equals(gameName)) {
                report.addError("Define '" + aiName + "_ai' found in AI metadata; use '" + gameName + "_ai' or remove it.");
                return null;
            }
        }
        final String[] defs = FileHandling.getResourceListingSingle(Expander.class, "def_ai/", gameName + "_ai.def");
        if (defs == null) {
            try {
                final URL url = Expander.class.getResource("/def_ai/Chess_ai.def");
                String path = new File(url.toURI()).getPath();
                path = path.substring(0, path.length() - "Chess_ai.def".length());
                final File root = new File(path);
                final File[] list = root.listFiles();
                if (list == null) {
                    return null;
                }
                for (final File file : list) {
                    if (file != null && !file.isDirectory() && file.getName() != null && file.getName().equals(gameName + "_ai.def")) {
                        final String filePath = path + file.getName();
                        knownAIDefine = KnownDefines.processDefFile(filePath.replaceAll(Pattern.quote("\\"), "/"), "/def_ai/", report);
                        if (report.isError()) {
                            return null;
                        }
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            for (String def : defs) {
                def = def.replaceAll(Pattern.quote("\\"), "/");
                final String[] defSplit = def.split(Pattern.quote("/"));
                final String filename = defSplit[defSplit.length - 1];
                if (filename.equals(gameName + "_ai.def")) {
                    knownAIDefine = KnownDefines.processDefFile(def, "/def_ai/", report);
                    if (report.isError()) {
                        return null;
                    }
                }
            }
        }
        return knownAIDefine;
    }
    
    private static String expandRanges(final String strIn, final Report report) {
        if (!strIn.contains("..")) {
            return strIn;
        }
        String str = strIn;
        for (int ref = 1; ref < str.length() - 2; ++ref) {
            if (str.charAt(ref) == '.' && str.charAt(ref + 1) == '.' && Character.isDigit(str.charAt(ref - 1)) && Character.isDigit(str.charAt(ref + 2))) {
                int c;
                for (c = ref - 1; c >= 0 && Character.isDigit(str.charAt(c)); --c) {}
                ++c;
                final String strM = str.substring(c, ref);
                final int m = Integer.parseInt(strM);
                for (c = ref + 2; c < str.length() && Character.isDigit(str.charAt(c)); ++c) {}
                final String strN = str.substring(ref + 2, c);
                final int n = Integer.parseInt(strN);
                if (Math.abs(n - m) > 1000) {
                    report.addError("Range exceeded maximum of 1000.");
                    return null;
                }
                String sub = " ";
                for (int inc = (m <= n) ? 1 : -1, step = m; step != n; step += inc) {
                    if (step != m) {
                        if (step != n) {
                            sub = sub + step + " ";
                        }
                    }
                }
                str = str.substring(0, ref) + sub + str.substring(ref + 2);
                ref += sub.length();
            }
        }
        return str;
    }
    
    private static String extractMetadata(final String strIn, final Description description, final UserSelections userSelections, final Report report) {
        final int c = strIn.indexOf("(metadata");
        if (c < 0) {
            return strIn;
        }
        final int cc = StringRoutines.matchingBracketAt(strIn, c);
        if (cc < 0) {
            report.addError("Failed to close '(metadata' in '" + Report.clippedString(strIn.substring(c), 20) + "'.");
            return null;
        }
        String str = strIn.substring(c, cc + 1);
        str = removeUnselectedOptionParts(str, description, userSelections, report);
        if (report.isError()) {
            return null;
        }
        description.setMetadata(str);
        final String removed = strIn.substring(0, c) + strIn.substring(cc + 1);
        return removed;
    }
    
    private static String removeUnselectedOptionParts(final String strIn, final Description description, final UserSelections userSelections, final Report report) {
        String str = strIn;
        int activeRuleset = userSelections.ruleset();
        if (activeRuleset < 0) {
            activeRuleset = description.autoSelectRuleset(userSelections.selectedOptionStrings());
            if (activeRuleset >= 0) {
                userSelections.setRuleset(activeRuleset);
            }
        }
        final List<String> active = description.gameOptions().allOptionStrings(userSelections.selectedOptionStrings());
        while (true) {
            final int optsCondStartIdx = str.indexOf("(useFor ");
            if (optsCondStartIdx < 0) {
                return str;
            }
            final int optsCondClosingBracketIdx = StringRoutines.matchingBracketAt(str, optsCondStartIdx);
            if (optsCondClosingBracketIdx < 0) {
                report.addError("Failed to close '(useFor' in '" + Report.clippedString(str.substring(optsCondStartIdx), 20) + "'.");
                return null;
            }
            final int nextOpeningCurlyIdx = str.indexOf(123, optsCondStartIdx);
            final int nextQuoteIdx = str.indexOf(34, optsCondStartIdx);
            if (nextQuoteIdx < 0) {
                report.addError("No quote after '(useFor' in '" + Report.clippedString(str.substring(optsCondStartIdx), 20) + "'.");
                return null;
            }
            int requirementsSubstrIdxStart;
            int requirementsSubstrIdxEnd;
            if (nextOpeningCurlyIdx >= 0 && nextOpeningCurlyIdx < nextQuoteIdx) {
                final int openCurlyBracketIdx = nextOpeningCurlyIdx;
                final int closCurlyBracketIdx = StringRoutines.matchingBracketAt(str, openCurlyBracketIdx);
                if (closCurlyBracketIdx < 0) {
                    report.addError("Failed to close curly bracket '{' in '" + Report.clippedString(str.substring(optsCondStartIdx), 20) + "'.");
                    return null;
                }
                requirementsSubstrIdxStart = openCurlyBracketIdx + 1;
                requirementsSubstrIdxEnd = closCurlyBracketIdx;
            }
            else {
                final int openingQuoteIdx = nextQuoteIdx;
                int closingQuoteIdx;
                for (closingQuoteIdx = str.indexOf(34, openingQuoteIdx + 1); closingQuoteIdx >= 0 && str.charAt(closingQuoteIdx - 1) == '\\'; closingQuoteIdx = str.indexOf(34, closingQuoteIdx + 1)) {}
                if (closingQuoteIdx < 0) {
                    report.addError("Failed to close quote after '(useFor' in '" + Report.clippedString(str.substring(openingQuoteIdx), 20) + "'.");
                    return null;
                }
                requirementsSubstrIdxStart = openingQuoteIdx;
                requirementsSubstrIdxEnd = closingQuoteIdx + 1;
            }
            final List<String> requiredOptions = new ArrayList<>();
            final String requirementsSubstr = str.substring(requirementsSubstrIdxStart, requirementsSubstrIdxEnd);
            int requiredOptClosingQuoteIdx;
            for (int requiredOptOpenQuoteIdx = requirementsSubstr.indexOf(34); requiredOptOpenQuoteIdx >= 0; requiredOptOpenQuoteIdx = requirementsSubstr.indexOf(34, requiredOptClosingQuoteIdx + 1)) {
                for (requiredOptClosingQuoteIdx = requirementsSubstr.indexOf(34, requiredOptOpenQuoteIdx + 1); requirementsSubstr.charAt(requiredOptClosingQuoteIdx - 1) == '\\'; requiredOptClosingQuoteIdx = requirementsSubstr.indexOf(34, requiredOptClosingQuoteIdx + 1)) {}
                if (requiredOptClosingQuoteIdx < 0) {
                    report.addError("Failed to close String quote in '" + Report.clippedString(requirementsSubstr.substring(requiredOptClosingQuoteIdx), 20) + "'.");
                    return null;
                }
                requiredOptions.add(requirementsSubstr.substring(requiredOptOpenQuoteIdx + 1, requiredOptClosingQuoteIdx));
            }
            for (final String requiredOption : requiredOptions) {
                if (!description.gameOptions().optionExists(requiredOption)) {
                    boolean foundMatch = false;
                    for (final Ruleset ruleset : description.rulesets()) {
                        if (requiredOption.equals(ruleset.heading())) {
                            foundMatch = true;
                            break;
                        }
                    }
                    if (!foundMatch) {
                        report.addError("Metadata has option requirement for option or ruleset that does not exist: " + requiredOption);
                        return null;
                    }
                    continue;
                }
            }
            boolean failedRequirement = false;
            for (final String requiredOpt : requiredOptions) {
                if (!active.contains(requiredOpt.replaceAll(Pattern.quote("\""), "")) && (activeRuleset < 0 || !description.rulesets().get(activeRuleset).heading().equals(requiredOpt.replaceAll(Pattern.quote("\""), "")))) {
                    failedRequirement = true;
                    break;
                }
            }
            final StringBuffer stringBuffer = new StringBuffer(str);
            if (failedRequirement) {
                stringBuffer.replace(optsCondStartIdx, optsCondClosingBracketIdx + 1, "");
            }
            else {
                stringBuffer.replace(optsCondClosingBracketIdx, optsCondClosingBracketIdx + 1, "");
                stringBuffer.replace(optsCondStartIdx, requirementsSubstrIdxEnd + 1, "");
            }
            str = stringBuffer.toString();
        }
    }
    
    public static String removeComments(final String strIn) {
        String str = strIn;
        int c = 0;
        while (c < str.length() - 1) {
            if (str.charAt(c) == '\"') {
                c = StringRoutines.matchingQuoteAt(str, c) + 1;
                if (c <= 0) {
                    return str;
                }
                continue;
            }
            else if (str.charAt(c) == '/' && str.charAt(c + 1) == '/') {
                int cc;
                for (cc = c + 2; cc < str.length() && str.charAt(cc) != '\n'; ++cc) {}
                str = str.substring(0, c) + str.substring(cc);
            }
            else {
                ++c;
            }
        }
        return str;
    }
}
