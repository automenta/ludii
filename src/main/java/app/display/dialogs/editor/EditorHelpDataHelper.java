// 
// Decompiled by Procyon v0.5.36
// 

package app.display.dialogs.editor;

import language.parser.KnownDefines;

import java.util.*;

public final class EditorHelpDataHelper
{
    private static boolean VERBOSE;
    
    public static final String fullDocumentForConstructor(final EditorHelpData data, final String type, final int n) {
        final StringBuilder sb = new StringBuilder();
        sb.append("<table cellspacing=0 cellpadding=10 width='100%' >");
        sb.append("<tr style='border: 1px silver solid;'>");
        sb.append("<td>");
        sb.append(escapeForHTML(data.typeDocString(type)));
        final String remarks = data.typeRemarksString(type);
        if (remarks != null && !remarks.isEmpty()) {
            sb.append(" <br> ");
            sb.append(escapeForHTML(remarks));
        }
        sb.append("</td>");
        sb.append("</tr>");
        if (n < 0) {
            sb.append("</table>");
            return sb.toString();
        }
        sb.append("<tr style='border: 1px silver solid;'>");
        sb.append("<td>").append(highlightKeyword(escapeForHTML(data.nthConstructorLine(type, n)))).append("</td>");
        sb.append("</tr>");
        final List<String> paramLines = data.nthConstructorParamLines(type, n);
        if (paramLines != null && paramLines.size() > 0) {
            sb.append("<tr style='border: 1px silver solid;'>");
            sb.append("<td>");
            sb.append("<b>").append("Parameters").append("</b>");
            sb.append("<table class=\"params\" border=\"0\" cellspacing=0 cellpadding=0>");
            for (final String line : paramLines) {
                final int pos = line.lastIndexOf(":");
                if (pos > 0) {
                    sb.append("<tr>");
                    sb.append("<td>").append(escapeForHTML(line.substring(0, pos).trim())).append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;").append("</td>");
                    sb.append("<td>").append(escapeForHTML(line.substring(pos + 1).trim())).append("</td>");
                    sb.append("</tr>");
                }
                else {
                    sb.append("<tr>").append("<td>").append(escapeForHTML(line)).append("</td>").append("</tr>");
                }
            }
            sb.append("</table>");
            sb.append("</td>");
            sb.append("</tr>");
        }
        final List<String> exampleLines = data.nthConstructorExampleLines(type, n);
        if (exampleLines != null && exampleLines.size() > 0) {
            sb.append("<tr style='border: 1px silver solid;'>");
            sb.append("<td>");
            sb.append("<b>").append("Examples").append("</b>");
            for (final String line2 : exampleLines) {
                sb.append("<br/>");
                sb.append(escapeForHTML(line2));
            }
            sb.append("</td>");
            sb.append("</tr>");
        }
        sb.append("</table>");
        return sb.toString();
    }
    
    public static final String extractKeyword(final String text) {
        if (text == null || text.length() == 0) {
            return "";
        }
        if (text.charAt(0) != '(' && text.charAt(0) != '<' && text.charAt(0) != '[' && text.charAt(0) != '{') {
            return text;
        }
        int pos;
        for (pos = 1; pos < text.length() && Character.isLetterOrDigit(text.charAt(pos)); ++pos) {}
        return text.substring(1, pos);
    }
    
    public static final String highlightKeyword(final String text) {
        if (text == null || text.length() == 0) {
            return "";
        }
        if (text.charAt(0) != '(') {
            return text;
        }
        int pos;
        for (pos = 1; pos < text.length() && Character.isLetterOrDigit(text.charAt(pos)); ++pos) {}
        return text.charAt(0) + "<b>" + text.substring(1, pos) + "</b>" + text.substring(pos);
    }
    
    public static final String escapeForHTML(final String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
    
    public static final List<SuggestionInstance> suggestionsForClasspaths(final EditorHelpData data, final List<String> rawCandidates, final boolean isPartial) {
        final List<SuggestionInstance> suggestions = new ArrayList<>();
        final Set<String> allCandidates = expandHierarchy(data, rawCandidates);
        final Set<String> uniqueEnums = new HashSet<>();
        final Set<String> uniqueConstructors = new HashSet<>();
        for (final String classPath : allCandidates) {
            if (isDefine(classPath)) {
                final StringBuilder sb = new StringBuilder();
                sb.append(escapeForHTML(data.defineDocString(classPath)));
                final List<String> exampleLines = data.defineExampleLines(classPath);
                if (exampleLines != null && exampleLines.size() > 0) {
                    sb.append("<br/>");
                    sb.append("<br/>");
                    sb.append("<b>").append("Examples").append("</b>");
                    for (final String line : exampleLines) {
                        sb.append("<br/>");
                        sb.append(escapeForHTML(line));
                    }
                }
                if (isPartial) {
                    final String token = extractKeyword(classPath);
                    if (uniqueConstructors.add(token)) {
                        suggestions.add(new SuggestionInstance(classPath, token, token, sb.toString()));
                    }
                }
                else {
                    suggestions.add(new SuggestionInstance(classPath, classPath, classPath, sb.toString()));
                }
            }
            if (isEnum(classPath)) {
                String key = classPath.replace('$', '.');
                Collection<String> enums = data.enumConstantLines(key);
                if (enums == null || enums.size() == 0) {
                    final String[] parts = classPath.split("\\$");
                    key = parts[0];
                    enums = data.enumConstantLines(key);
                }
                if (enums == null || enums.size() == 0) {
                    if (!EditorHelpDataHelper.VERBOSE) {
                        continue;
                    }
                    System.out.println("Can't find enums for " + classPath);
                }
                else {
                    if (EditorHelpDataHelper.VERBOSE) {
                        System.out.println("Processing " + enums.size() + "enums for " + classPath + ": " + enums);
                    }
                    final String javadoc = data.typeDocString(key);
                    for (final String label : enums) {
                        final int pos = label.indexOf(":");
                        if (pos > 0) {
                            final String substitution = label.substring(0, pos).trim();
                            final String embeddedDoc = label.substring(pos + 1).trim();
                            if (!uniqueEnums.add(substitution)) {
                                continue;
                            }
                            suggestions.add(new SuggestionInstance(classPath, label, substitution, javadoc + "<br/>" + "<br/>" + embeddedDoc));
                        }
                        else {
                            if (!uniqueEnums.add(label)) {
                                continue;
                            }
                            suggestions.add(new SuggestionInstance(classPath, label, label, javadoc));
                        }
                    }
                }
            }
            else if (classPath.equalsIgnoreCase("true")) {
                suggestions.add(new SuggestionInstance("false", "false", "false", "Make condition false."));
            }
            else if (classPath.equalsIgnoreCase("false")) {
                suggestions.add(new SuggestionInstance("true", "true", "true", "Make condition true."));
            }
            else {
                final int count = data.numConstructors(classPath);
                if (count > 0) {
                    if (EditorHelpDataHelper.VERBOSE) {
                        System.out.println("Found " + count + " constructors for " + classPath);
                    }
                    if (isPartial) {
                        final String label2 = data.nthConstructorLine(classPath, 0);
                        final String token = extractKeyword(label2);
                        if (!uniqueConstructors.add(token)) {
                            continue;
                        }
                        final String javadoc2 = fullDocumentForConstructor(data, classPath, -1);
                        suggestions.add(new SuggestionInstance(classPath, token, token, javadoc2));
                    }
                    else {
                        for (int n = 0; n < count; ++n) {
                            final String label3 = data.nthConstructorLine(classPath, n);
                            if (EditorHelpDataHelper.VERBOSE) {
                                System.out.println("#" + n + ": " + label3);
                            }
                            final String javadoc2 = fullDocumentForConstructor(data, classPath, n);
                            suggestions.add(new SuggestionInstance(classPath, label3, label3, javadoc2));
                        }
                    }
                }
                else {
                    final String key2 = classPath;
                    final String javadoc = data.typeDocString(key2);
                    final List<String> enums2 = data.enumConstantLines(key2);
                    if (enums2 != null && enums2.size() > 0) {
                        if (EditorHelpDataHelper.VERBOSE) {
                            System.out.println("Processing " + enums2.size() + "enum constant lines for " + key2 + ": " + enums2);
                        }
                        for (final String label4 : enums2) {
                            final int pos2 = label4.indexOf(":");
                            if (pos2 > 0) {
                                final String substitution2 = label4.substring(0, pos2).trim();
                                final String embeddedDoc2 = label4.substring(pos2 + 1).trim();
                                suggestions.add(new SuggestionInstance(classPath, label4, substitution2, javadoc + "<br/>" + "<br/>" + embeddedDoc2));
                            }
                            else {
                                suggestions.add(new SuggestionInstance(classPath, label4, label4, javadoc));
                            }
                        }
                    }
                    final List<String> subclasses = data.subclassDocLines(classPath);
                    if (subclasses == null || subclasses.size() <= 0) {
                        continue;
                    }
                    for (final String label5 : subclasses) {
                        final int pos3 = label5.indexOf(":");
                        if (pos3 > 0) {
                            final String substitution3 = label5.substring(0, pos3).trim();
                            final String embeddedDoc3 = label5.substring(pos3 + 1).trim();
                            suggestions.add(new SuggestionInstance(classPath, label5, substitution3, javadoc + "<br/>" + "<br/>" + embeddedDoc3));
                        }
                        else {
                            suggestions.add(new SuggestionInstance(classPath, label5, label5, javadoc));
                        }
                    }
                }
            }
        }
        return suggestions;
    }
    
    private static Set<String> expandHierarchy(final EditorHelpData data, final List<String> rawCandidates) {
        final Set<String> results = new HashSet<>();
        if (EditorHelpDataHelper.VERBOSE) {
            System.out.println("Expanding: " + rawCandidates);
        }
        for (int pos = 0; pos < rawCandidates.size(); ++pos) {
            final String candidate = rawCandidates.get(pos);
            final String key = removeAngleBrackets(candidate);
            final List<String> subclasses = data.subclassDocLines(key);
            if (subclasses != null && subclasses.size() > 0) {
                results.addAll(expandHierarchy(data, subclasses));
            }
            else if (data.numConstructors(key) > 0 || "true".equals(candidate) || "false".equals(candidate) || isEnum(key)) {
                results.add(key);
            }
        }
        return results;
    }
    
    private static String removeAngleBrackets(final String candidate) {
        if (candidate.startsWith("<")) {
            return candidate.substring(1, candidate.indexOf(">"));
        }
        return candidate;
    }
    
    public static final String formatLabel(final String label) {
        final String[] tokens = escapeForHTML(label).split(" ");
        if (tokens[0].startsWith("(")) {
            tokens[0] = "(<b>" + tokens[0].substring(1) + "</b>";
        }
        else {
            tokens[0] = "<b>" + tokens[0] + "</b>";
        }
        final String result = "<html>" + String.join("&nbsp;", tokens) + "</html>";
        return result;
    }
    
    private static boolean isEnum(final String classPath) {
        return classPath.contains("$");
    }
    
    private static final boolean isDefine(final String classPath) {
        final String key = extractKeyword(classPath);
        return KnownDefines.getKnownDefines().knownDefines().get(key) != null;
    }
    
    static {
        EditorHelpDataHelper.VERBOSE = true;
    }
}
