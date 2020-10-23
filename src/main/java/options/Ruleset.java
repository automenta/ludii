/*
 * Decompiled with CFR 0.150.
 */
package options;

import main.StringRoutines;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

public class Ruleset {
    private String heading = null;
    private final List<String> optionSettings = new ArrayList<>();
    private int priority = 0;

    public Ruleset(String str) {
        try {
            this.interpret(str);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String heading() {
        return this.heading;
    }

    public List<String> optionSettings() {
        return Collections.unmodifiableList(this.optionSettings);
    }

    public int priority() {
        return this.priority;
    }

    void interpret(String strIn) {
        String str = strIn.trim();
        this.priority = 0;
        while (str.charAt(str.length() - 1) == '*') {
            ++this.priority;
            str = str.substring(0, str.length() - 1);
        }
        int c = str.indexOf("(ruleset ");
        if (c < 0) {
            throw new RuntimeException("Ruleset not found: " + str);
        }
        int cc = StringRoutines.matchingBracketAt(str, c);
        if (cc < 0) {
            throw new RuntimeException("No closing bracket ')' in ruleset: " + str);
        }
        c = str.indexOf(34);
        if (c < 0) {
            throw new RuntimeException("Ruleset heading not found: " + str);
        }
        for (cc = c + 1; cc < str.length() && (str.charAt(cc) != '\"' || str.charAt(cc - 1) == '\\'); ++cc) {
        }
        if (cc < 0) {
            throw new RuntimeException("No closing quote for ruleset heading: " + str);
        }
        this.heading = str.substring(c + 1, cc);
        str = str.substring(cc + 1).trim();
        while ((c = str.indexOf(34)) >= 0) {
            for (cc = c + 1; cc < str.length() && str.charAt(cc) != '\"'; ++cc) {
            }
            if (cc < 0) {
                throw new RuntimeException("No closing quote for option setting: " + str);
            }
            String option = str.substring(c + 1, cc);
            this.optionSettings.add(option);
            str = str.substring(cc + 1).trim();
        }
    }

    @Deprecated
    public void setOptionSelections(GameOptions gameOptions, int[] selections) {
        BitSet used = new BitSet();
        int numCategories = gameOptions.categories().size();
        for (String optionSetting : this.optionSettings) {
            String[] subs = optionSetting.split("/");
            if (subs.length < 2) {
                throw new RuntimeException("Badly formed option heading: " + optionSetting);
            }
            for (int cat = 0; cat < numCategories; ++cat) {
                OptionCategory category = gameOptions.categories().get(cat);
                if (!category.heading().equals(subs[0])) continue;
                for (int o = 0; o < category.options().size(); ++o) {
                    Option option = category.options().get(o);
                    if (!option.menuHeadings().get(1).equals(subs[1])) continue;
                    if (used.get(cat)) {
                        throw new RuntimeException("Option category already set in ruleset: " + optionSetting);
                    }
                    selections[cat] = o;
                    used.set(cat, true);
                }
            }
        }
        if (used.cardinality() != numCategories) {
            throw new RuntimeException("Not all options are specified in ruleset: " + this.toString());
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[\"").append(this.heading).append("\" {");
        for (String option : this.optionSettings) {
            sb.append(" \"").append(option).append("\"");
        }
        sb.append(" }]");
        return sb.toString();
    }
}

