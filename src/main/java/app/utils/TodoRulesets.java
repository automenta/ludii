// 
// Decompiled by Procyon v0.5.36
// 

package app.utils;

import main.StringRoutines;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public final class TodoRulesets
{
    public static void main(final String[] args) {
        missingRulesets();
    }
    
    private static void missingRulesets() {
        final File startFolder = new File("../Common/res/lud");
        final List<File> gameDirs = new ArrayList<>();
        gameDirs.add(startFolder);
        final List<File> entries = new ArrayList<>();
        for (int i = 0; i < gameDirs.size(); ++i) {
            final File gameDir = gameDirs.get(i);
            for (final File fileEntry : gameDir.listFiles()) {
                if (fileEntry.isDirectory()) {
                    final String fileEntryPath = fileEntry.getPath().replaceAll(Pattern.quote("\\"), "/");
                    if (!fileEntryPath.equals("../Common/res/lud/plex")) {
                        if (!fileEntryPath.equals("../Common/res/lud/wip")) {
                            if (!fileEntryPath.equals("../Common/res/lud/wishlist")) {
                                if (!fileEntryPath.equals("../Common/res/lud/WishlistDLP")) {
                                    if (!fileEntryPath.equals("../Common/res/lud/test")) {
                                        if (!fileEntryPath.equals("../Common/res/lud/puzzle/deduction")) {
                                            if (!fileEntryPath.equals("../Common/res/lud/bad")) {
                                                if (!fileEntryPath.equals("../Common/res/lud/bad_playout")) {
                                                    gameDirs.add(fileEntry);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                else {
                    entries.add(fileEntry);
                }
            }
        }
        for (final File file : entries) {
            final String path = file.getAbsolutePath();
            final StringBuilder sb = new StringBuilder();
            try (final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))) {
                String line;
                do {
                    line = reader.readLine();
                    sb.append(line);
                } while (line != null);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            final String str = sb.toString();
            for (int indexRuleset = str.indexOf("(ruleset "); indexRuleset >= 0; indexRuleset = str.indexOf("(ruleset ", indexRuleset)) {
                while (indexRuleset < str.length() && str.charAt(indexRuleset) != '{') {
                    ++indexRuleset;
                }
                if (indexRuleset < str.length()) {
                    final int indexCurlyBrace = StringRoutines.matchingBracketAt(str, indexRuleset);
                    if (indexCurlyBrace < 0) {
                        throw new RuntimeException("No closing '}' in ruleset: " + str);
                    }
                    final String sub = str.substring(indexRuleset + 1, indexCurlyBrace);
                    boolean isChar = false;
                    for (int indexChar = 0; indexChar < sub.length(); ++indexChar) {
                        final char ch = sub.charAt(indexChar);
                        if (StringRoutines.isTokenChar(ch) || StringRoutines.isNameChar(ch) || StringRoutines.isNumeric(ch) || StringRoutines.isBracket(ch)) {
                            isChar = true;
                            break;
                        }
                    }
                    if (!isChar) {
                        System.err.println(path + " has an empty ruleset.");
                    }
                }
            }
        }
    }
}
