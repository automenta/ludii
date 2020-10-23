/*
 * Decompiled with CFR 0.150.
 */
package options;

import root.StringRoutines;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OptionCategory {
    private String tag;
    private String heading;
    private final List<String> argTags;
    private final List<Option> options;

    public OptionCategory(String description) {
        this.argTags = new ArrayList<>();
        this.options = new ArrayList<>();
        try {
            this.extractOptions(description);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public OptionCategory(Option option) {
        this.tag = option.tag();
        this.heading = option.menuHeadings().get(0);
        this.argTags = null;
        this.options = new ArrayList<>();
        this.options.add(option);
    }

    public OptionCategory(List<Option> options) {
        this.tag = options.get(0).tag();
        this.heading = options.get(0).menuHeadings().get(0);
        this.argTags = null;
        this.options = options;
    }

    public String tag() {
        return this.tag;
    }

    public String heading() {
        return this.heading;
    }

    public List<Option> options() {
        return Collections.unmodifiableList(this.options);
    }

    public List<String> argTags() {
        if (this.argTags == null) {
            return this.argTags;
        }
        return Collections.unmodifiableList(this.argTags);
    }

    public void add(Option option) {
        if (!this.tag.equals(option.tag())) {
            System.out.println("** Option label does not match option category label.");
        }
        this.options.add(option);
    }

    void extractOptions(String strIn) throws Exception {
        String str = strIn;
        str = this.extractHeading(str);
        str = this.extractTag(str);
        int c = (str = this.extractArgTags(str)).indexOf(123);
        if (c < 0) {
            throw new Exception("Couldn't find opening bracket '{' for option list " + str.substring(c));
        }
        int cc = StringRoutines.matchingBracketAt(str, c);
        if (cc < 0 || cc >= str.length()) {
            throw new Exception("Couldn't close option bracket '>' in " + str.substring(c));
        }
        String optionList = str.substring(c + 1, cc);
        while ((c = optionList.indexOf("(item ")) >= 0) {
            cc = StringRoutines.matchingBracketAt(optionList, c);
            if (cc < 0 || cc >= optionList.length()) {
                throw new Exception("No closing bracket ')' for option: " + optionList.substring(c));
            }
            ++cc;
            while (cc < optionList.length() && optionList.charAt(cc) == '*') {
                ++cc;
            }
            String optionString = optionList.substring(c, cc);
            Option option = new Option(optionString, this);
            this.options.add(option);
            optionList = optionList.substring(c + 1).trim();
        }
    }

    String extractHeading(String str) throws Exception {
        int cc;
        int c;
        for (c = 0; c < str.length() && str.charAt(c) != '\"'; ++c) {
        }
        if (c >= str.length()) {
            throw new Exception("Failed to find option category heading: " + str);
        }
        for (cc = c + 1; cc < str.length() && str.charAt(cc) != '\"'; ++cc) {
        }
        if (cc < 0 || cc >= str.length()) {
            throw new Exception("Failed to find option category heading: " + str);
        }
        this.heading = str.substring(c + 1, cc);
        return str.substring(cc + 1);
    }

    String extractTag(String str) throws Exception {
        int c = str.indexOf(60);
        if (c < 0) {
            throw new Exception("Failed to find option category tag: " + str);
        }
        int cc = StringRoutines.matchingBracketAt(str, c);
        if (cc < 0 || cc >= str.length()) {
            throw new Exception("Couldn't close option bracket '>' in " + str.substring(c));
        }
        this.tag = str.substring(c + 1, ++cc - 1);
        return str.substring(cc + 1);
    }

    String extractArgTags(String strIn) throws Exception {
        int a;
        if (!strIn.contains("args:")) {
            throw new Exception("Option category must define args:{...}." + strIn);
        }
        int c = strIn.indexOf("args:");
        if (c < 0) {
            throw new Exception("No option argument tags of form args:{...}: " + strIn);
        }
        c = strIn.indexOf("{");
        if (c < 0) {
            throw new Exception("Couldn't find opening bracket '{' in option category " + strIn.substring(c));
        }
        int cc = StringRoutines.matchingBracketAt(strIn, c);
        if (cc < 0 || cc >= strIn.length()) {
            throw new Exception("Couldn't find closing bracket '}' in option category " + strIn.substring(c));
        }
        String str = strIn.substring(c, cc).trim();
        while ((a = str.indexOf("<")) >= 0) {
            int aa = StringRoutines.matchingBracketAt(str, a);
            if (aa < 0 || aa >= str.length()) {
                throw new Exception("No closing bracket '>' for option argument: " + str);
            }
            String arg = str.substring(a + 1, aa);
            this.argTags.add(arg);
            str = str.substring(aa + 1).trim();
        }
        return strIn.substring(cc + 1);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<" + this.tag + "> \"" + this.heading + "\"");
        if (this.argTags != null) {
            sb.append(" [ ");
            for (String arg : this.argTags) {
                sb.append(arg + " ");
            }
            sb.append("]");
        }
        return sb.toString();
    }
}

