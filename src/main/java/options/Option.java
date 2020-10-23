/*
 * Decompiled with CFR 0.150.
 */
package options;

import main.StringRoutines;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Option {
    private String tag = "";
    private String description = "";
    private final List<OptionArgument> arguments = new ArrayList<>();
    private List<String> headings = new ArrayList<>();
    private int priority = 0;

    public Option(String str, OptionCategory category) {
        try {
            this.interpret(str, category);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Option() {
    }

    public String tag() {
        return this.tag;
    }

    public String description() {
        return this.description;
    }

    public List<String> menuHeadings() {
        return Collections.unmodifiableList(this.headings);
    }

    public void setHeadings(List<String> headings) {
        this.headings = headings;
        System.out.println("Headings are now:");
        for (String heading : this.headings) {
            System.out.println("-- " + heading);
        }
    }

    public List<OptionArgument> arguments() {
        return Collections.unmodifiableList(this.arguments);
    }

    public int priority() {
        return this.priority;
    }

    void interpret(String strIn, OptionCategory category) throws Exception {
        int cc;
        String str = strIn.trim();
        if (!str.contains("(item ") || !str.contains(")")) {
            throw new Exception("Option not bracketed properly: " + str);
        }
        this.tag = category.tag();
        this.priority = 0;
        while (str.charAt(str.length() - 1) == '*') {
            ++this.priority;
            str = str.substring(0, str.length() - 1);
        }
        int c = (str = str.substring(1, str.length() - 1).trim()).indexOf(34);
        if (c < 0) {
            throw new Exception("Failed to find option heading: " + str);
        }
        for (cc = c + 1; cc < str.length() && str.charAt(cc) != '\"'; ++cc) {
        }
        if (cc < 0 || cc >= str.length()) {
            throw new Exception("Failed to find option heading: " + str);
        }
        String heading = str.substring(c + 1, cc);
        this.headings.add(category.heading());
        this.headings.add(heading);
        str = str.substring(cc + 1).trim();
        for (cc = str.length() - 1; cc >= 0 && str.charAt(cc) != '\"'; --cc) {
        }
        if (cc < 0) {
            throw new Exception("Failed to find option description: " + str);
        }
        for (c = cc - 1; c >= 0 && str.charAt(c) != '\"'; --c) {
        }
        if (c < 0) {
            throw new Exception("Failed to find option description: " + str);
        }
        this.description = str.substring(c + 1, cc);
        str = str.substring(0, c).trim();
        List<String> argTags = category.argTags();
        while ((c = str.indexOf('<')) >= 0) {
            String arg;
            if (c > 0 && str.charAt(c - 1) == '(') {
                str = str.substring(c + 1).trim();
                continue;
            }
            cc = StringRoutines.matchingBracketAt(str, c);
            if (cc < 0 || cc >= str.length()) {
                throw new Exception("No closing bracket '>' for option argument: " + str);
            }
            String string = arg = c + 1 >= ++cc - 1 ? "" : str.substring(c + 1, cc - 1);
            if (this.arguments.size() >= argTags.size()) {
                throw new Exception("Not enough tags for option arguments: " + strIn);
            }
            String name = argTags.get(this.arguments.size());
            OptionArgument optArg = new OptionArgument(name, arg);
            this.arguments.add(optArg);
            str = str.substring(c + 1).trim();
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[" + this.tag + ", \"");
        for (int n = 0; n < this.headings.size(); ++n) {
            if (n > 0) {
                sb.append("/");
            }
            sb.append(this.headings.get(n));
        }
        sb.append("\",");
        for (OptionArgument arg : this.arguments) {
            sb.append(" ");
            if (arg.name() != null) {
                sb.append(arg.name() + ":");
            }
            sb.append("<" + arg.expression() + ">");
        }
        sb.append(", priority " + this.priority + "]");
        return sb.toString();
    }
}

