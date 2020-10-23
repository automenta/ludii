// 
// Decompiled by Procyon v0.5.36
// 

package supplementary;

import main.StringRoutines;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class AutoIndenter
{
    private static final String indentString = "    ";
    
    public static void main(final String[] args) {
        indentFilesNicely();
    }
    
    public static void indentFilesNicely() {
        indentFilesNicelyFrom("../Common/res/lud");
        indentFilesNicelyFrom("../Common/res/def");
        indentFilesNicelyFrom("../Common/res/def_ai");
    }
    
    public static void indentFilesNicelyFrom(final String folderPath) {
        final List<File> files = new ArrayList<>();
        final List<File> dirs = new ArrayList<>();
        final File folder = new File(folderPath);
        dirs.add(folder);
        for (int i = 0; i < dirs.size(); ++i) {
            final File dir = dirs.get(i);
            for (final File file : dir.listFiles()) {
                if (file.isDirectory()) {
                    dirs.add(file);
                }
                else {
                    files.add(file);
                }
            }
        }
        for (final File file2 : files) {
            indentFileNicely(file2.getAbsolutePath());
        }
        System.out.println(files.size() + " files found from " + folderPath + ".");
    }
    
    public static void indentFileNicely(final String path) {
        System.out.println("Indenting " + path + " nicely...");
        final File fileToBeModified = new File(path);
        final List<String> lines = new ArrayList<>();
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileToBeModified), StandardCharsets.UTF_8))) {
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                lines.add(line);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        for (int n = 0; n < lines.size(); ++n) {
            String str = lines.get(n);
            final int c = 0;
            while (!str.isEmpty() && (str.charAt(0) == ' ' || str.charAt(0) == '\t')) {
                str = str.substring(1);
            }
            lines.remove(n);
            lines.add(n, str);
        }
        removeDoubleEmptyLines(lines);
        indentLines(lines);
        try (final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileToBeModified), StandardCharsets.UTF_8))) {
            for (final String result : lines) {
                writer.write(result + "\n");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    static void moveDefinesToTop(final List<String> lines) {
        final List<String> defines = new ArrayList<>();
        int brackets = 0;
        boolean isDefine = false;
        int n = 0;
        while (n < lines.size()) {
            final String str = lines.get(n);
            final int numOpen = StringRoutines.numChar(str, '(');
            final int numClose = StringRoutines.numChar(str, ')');
            final int difference = numOpen - numClose;
            final boolean lineHasDefineString = str.contains("(define ");
            if (lineHasDefineString) {
                isDefine = true;
                brackets = 0;
            }
            if (isDefine) {
                if (lineHasDefineString && !defines.isEmpty()) {
                    defines.add("");
                }
                defines.add(str);
                lines.remove(n);
            }
            else {
                ++n;
            }
            if (lineHasDefineString && difference == 0) {
                isDefine = false;
                brackets = 0;
            }
            if (difference < 0) {
                brackets += difference;
                if (brackets < 0) {
                    brackets = 0;
                }
                if (!isDefine || brackets != 0) {
                    continue;
                }
                isDefine = false;
            }
            else {
                if (difference <= 0) {
                    continue;
                }
                brackets += difference;
            }
        }
        for (int d = defines.size() - 1; d >= 0; --d) {
            lines.add(0, defines.get(d));
        }
    }
    
    static void extractAIMetadataToDefine(final List<String> lines) {
        final List<String> define = new ArrayList<>();
        int brackets = 0;
        boolean isAI = false;
        String gameName = "";
        for (final String line : lines) {
            if (line.contains("(game ")) {
                gameName = StringRoutines.gameName(line);
                break;
            }
        }
        int n = 0;
        while (n < lines.size()) {
            final String str = lines.get(n);
            final int numOpen = StringRoutines.numChar(str, '(');
            final int numClose = StringRoutines.numChar(str, ')');
            final int difference = numOpen - numClose;
            final boolean lineHasAIString = str.contains("(ai ");
            if (lineHasAIString) {
                isAI = true;
                brackets = 1;
                define.add("(define \"" + gameName + "_ai\"");
                lines.add(n + 1, "\"" + gameName + "_ai\"");
                n += 2;
            }
            else {
                if (difference < 0) {
                    brackets += difference;
                    if (brackets < 0) {
                        brackets = 0;
                    }
                    if (isAI && brackets == 0) {
                        define.add(")");
                        isAI = false;
                    }
                }
                else if (difference > 0) {
                    brackets += difference;
                }
                if (isAI) {
                    define.add(str);
                    lines.remove(n);
                }
                else {
                    ++n;
                }
            }
        }
        final String outFilePath = "../Common/res/def_ai/" + gameName + "_ai.def";
        try (final FileWriter writer = new FileWriter(outFilePath)) {
            for (final String result : define) {
                writer.write(result + "\n");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    static void removeDoubleEmptyLines(final List<String> lines) {
        int n = 1;
        while (n < lines.size()) {
            if (lines.get(n).isEmpty() && lines.get(n - 1).isEmpty()) {
                lines.remove(n);
            }
            else {
                ++n;
            }
        }
    }
    
    static void insertSeparators(final List<String> lines) {
        boolean optionFound = false;
        boolean rulesetsFound = false;
        for (int n = 2; n < lines.size(); ++n) {
            final String str = lines.get(n);
            if (str.contains("(game ") || str.contains("(metadata ") || (str.contains("(option ") && !optionFound) || (str.contains("(rulesets ") && !rulesetsFound)) {
                lines.add(n, "");
                lines.add(n, "//------------------------------------------------------------------------------");
                lines.add(n, "");
                if (str.contains("(option ")) {
                    optionFound = true;
                }
                if (str.contains("(rulesets ")) {
                    rulesetsFound = true;
                }
                n += 3;
            }
        }
    }
    
    static void indentLines(final List<String> lines) {
        int indent = 0;
        for (int n = 0; n < lines.size(); ++n) {
            String str = lines.get(n);
            final int numOpen = StringRoutines.numChar(str, '(');
            final int numClose = StringRoutines.numChar(str, ')');
            final int difference = numOpen - numClose;
            if (difference < 0) {
                indent += difference;
                if (indent < 0) {
                    indent = 0;
                }
            }
            for (int step = 0; step < indent; ++step) {
                str = "    " + str;
            }
            lines.remove(n);
            lines.add(n, str);
            if (difference > 0) {
                indent += difference;
            }
        }
    }
}
