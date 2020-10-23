// 
// Decompiled by Procyon v0.5.36
// 

package main.options;

import main.StringRoutines;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OptionCategory
{
    private String tag;
    private String heading;
    private final List<String> argTags;
    private final List<Option> options;
    
    public OptionCategory(final String description) {
        this.argTags = new ArrayList<>();
        this.options = new ArrayList<>();
        try {
            this.extractOptions(description);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public OptionCategory(final Option option) {
        this.tag = option.tag();
        this.heading = option.menuHeadings().get(0);
        this.argTags = null;
        (this.options = new ArrayList<>()).add(option);
    }
    
    public OptionCategory(final List<Option> options) {
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
    
    public void add(final Option option) {
        if (!this.tag.equals(option.tag())) {
            System.out.println("** Option label does not match option category label.");
        }
        this.options.add(option);
    }
    
    void extractOptions(final String strIn) throws Exception {
        String str = strIn;
        str = this.extractHeading(str);
        str = this.extractTag(str);
        str = this.extractArgTags(str);
        int c = str.indexOf(123);
        if (c < 0) {
            throw new Exception("Couldn't find opening bracket '{' for option list " + str.substring(c));
        }
        int cc = StringRoutines.matchingBracketAt(str, c);
        if (cc < 0 || cc >= str.length()) {
            throw new Exception("Couldn't close option bracket '>' in " + str.substring(c));
        }
        String optionList = str.substring(c + 1, cc);
        while (true) {
            c = optionList.indexOf("(item ");
            if (c < 0) {
                return;
            }
            cc = StringRoutines.matchingBracketAt(optionList, c);
            if (cc < 0 || cc >= optionList.length()) {
                throw new Exception("No closing bracket ')' for option: " + optionList.substring(c));
            }
            ++cc;
            while (cc < optionList.length() && optionList.charAt(cc) == '*') {
                ++cc;
            }
            final String optionString = optionList.substring(c, cc);
            final Option option = new Option(optionString, this);
            this.options.add(option);
            optionList = optionList.substring(c + 1).trim();
        }
    }
    
    String extractHeading(final String str) throws Exception {
        int c;
        for (c = 0; c < str.length() && str.charAt(c) != '\"'; ++c) {}
        if (c >= str.length()) {
            throw new Exception("Failed to find option category heading: " + str);
        }
        int cc;
        for (cc = c + 1; cc < str.length() && str.charAt(cc) != '\"'; ++cc) {}
        if (cc < 0 || cc >= str.length()) {
            throw new Exception("Failed to find option category heading: " + str);
        }
        this.heading = str.substring(c + 1, cc);
        return str.substring(cc + 1);
    }
    
    String extractTag(final String str) throws Exception {
        final int c = str.indexOf(60);
        if (c < 0) {
            throw new Exception("Failed to find option category tag: " + str);
        }
        int cc = StringRoutines.matchingBracketAt(str, c);
        if (cc < 0 || cc >= str.length()) {
            throw new Exception("Couldn't close option bracket '>' in " + str.substring(c));
        }
        ++cc;
        this.tag = str.substring(c + 1, cc - 1);
        return str.substring(cc + 1);
    }
    
    String extractArgTags(final String strIn) throws Exception {
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
        final int cc = StringRoutines.matchingBracketAt(strIn, c);
        if (cc < 0 || cc >= strIn.length()) {
            throw new Exception("Couldn't find closing bracket '}' in option category " + strIn.substring(c));
        }
        String str = strIn.substring(c, cc).trim();
        while (true) {
            final int a = str.indexOf("<");
            if (a < 0) {
                return strIn.substring(cc + 1);
            }
            final int aa = StringRoutines.matchingBracketAt(str, a);
            if (aa < 0 || aa >= str.length()) {
                throw new Exception("No closing bracket '>' for option argument: " + str);
            }
            final String arg = str.substring(a + 1, aa);
            this.argTags.add(arg);
            str = str.substring(aa + 1).trim();
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<" + this.tag + "> \"" + this.heading + "\"");
        if (this.argTags != null) {
            sb.append(" [ ");
            for (final String arg : this.argTags) {
                sb.append(arg + " ");
            }
            sb.append("]");
        }
        return sb.toString();
    }
}
