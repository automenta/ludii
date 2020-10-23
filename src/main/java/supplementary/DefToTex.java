// 
// Decompiled by Procyon v0.5.36
// 

package supplementary;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class DefToTex
{
    public static void main(final String[] args) {
        String tex = "";
        try {
            tex = convertAllDefToTex("../Common/res/def");
            System.out.println(tex);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        final String outFilePath = "../LudiiDocGen/out/tex/KnownDefines.tex";
        try (final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("../LudiiDocGen/out/tex/KnownDefines.tex"), StandardCharsets.UTF_8))) {
            writer.write(tex + "\n");
        }
        catch (IOException e2) {
            e2.printStackTrace();
        }
    }
    
    public static String convertAllDefToTex(final String folderPath) throws IOException {
        final StringBuilder sb = new StringBuilder();
        final List<File> dirs = new ArrayList<>();
        final File folder = new File(folderPath);
        dirs.add(folder);
        for (int i = 0; i < dirs.size(); ++i) {
            final File dir = dirs.get(i);
            for (final File file : dir.listFiles()) {
                if (file.isDirectory()) {
                    dirs.add(file);
                }
            }
        }
        Collections.sort(dirs);
        for (File dir : dirs) {
            final String path = dir.getCanonicalPath();
            if (path.contains("/def/")) {
                final String name = path.substring(path.indexOf("def/"));
                sb.append(texSection(name));
            }
            for (final File file2 : dir.listFiles()) {
                if (!file2.isDirectory()) {
                    convertDefToTex(file2, sb);
                }
            }
        }
        return sb.toString();
    }
    
    public static void convertDefToTex(final File file, final StringBuilder sb) {
        if (!file.getPath().contains(".def")) {
            System.out.println("Bad file: " + file.getPath());
            return;
        }
        final List<String> lines = new ArrayList<>();
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                line = line.replace("&", "\\&");
                line = line.replace("_", "\\_");
                lines.add(line);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        final String name = file.getName().substring(0, file.getName().length() - 4);
        sb.append(texSubsection(name));
        final String comments = commentsFromLines(lines);
        if (comments == "") {
            sb.append("\\phantom{}\n");
        }
        else {
            sb.append("\n" + comments + "\n");
        }
        final List<String> examples = examplesFromLines(lines);
        if (!examples.isEmpty()) {
            sb.append(texExample(examples));
        }
        final String define = defineFromLines(lines);
        sb.append(texDefine(define));
    }
    
    static String commentsFromLines(final List<String> lines) {
        final StringBuilder sb = new StringBuilder();
        int commentLinesAdded = 0;
        for (final String line : lines) {
            final int c = line.indexOf("//");
            if (c < 0) {
                break;
            }
            if (line.contains("@example")) {
                break;
            }
            if (commentLinesAdded > 0) {
                sb.append(" \\\\ ");
            }
            sb.append(line.substring(c + 2).trim() + " ");
            ++commentLinesAdded;
        }
        final String comments = sb.toString().replace("#", "\\#");
        return comments;
    }
    
    static List<String> examplesFromLines(final List<String> lines) {
        final List<String> examples = new ArrayList<>();
        for (final String line : lines) {
            final int c = line.indexOf("@example");
            if (c < 0) {
                continue;
            }
            examples.add(line.substring(c + 8).trim());
        }
        return examples;
    }
    
    static String defineFromLines(final List<String> lines) {
        final StringBuilder sb = new StringBuilder();
        boolean defineFound = false;
        for (final String line : lines) {
            final int c = line.indexOf("(define ");
            if (c >= 0) {
                defineFound = true;
            }
            if (defineFound) {
                sb.append(line + "\n");
            }
        }
        return sb.toString();
    }
    
    static String texThinLine() {
        final StringBuilder sb = new StringBuilder();
        sb.append("\n\\vspace{-1mm}\n");
        sb.append("\\noindent\\rule{\\textwidth}{0.5pt}\n");
        sb.append("\\vspace{-6mm}\n");
        return sb.toString();
    }
    
    static String texThickLine() {
        final StringBuilder sb = new StringBuilder();
        sb.append("\n\\vspace{3mm}\n");
        sb.append("\\noindent\\rule{\\textwidth}{2pt}\n");
        sb.append("\\vspace{-7mm}\n");
        return sb.toString();
    }
    
    static String texSection(final String title) {
        final StringBuilder sb = new StringBuilder();
        sb.append("\n%==========================================================\n");
        sb.append(texThickLine());
        sb.append("\n\\section{" + title + "}\n");
        return sb.toString();
    }
    
    static String texSubsection(final String name) {
        final StringBuilder sb = new StringBuilder();
        sb.append("\n%-----------------------------------------\n");
        sb.append(texThinLine());
        sb.append("\n\\subsection{``" + name + "''}");
        sb.append("  \\label{known:" + name + "}\n");
        return sb.toString();
    }
    
    static String texExample(final List<String> examples) {
        final StringBuilder sb = new StringBuilder();
        if (examples.size() < 2) {
            sb.append("\n% Example\n");
        }
        else {
            sb.append("\n% Examples\n");
        }
        sb.append("\\vspace{-1mm}\n");
        sb.append("\\subsubsection*{Example}\n");
        sb.append("\\vspace{-3mm}\n");
        sb.append("\n\\begin{formatbox}\n");
        sb.append("\\begin{verbatim}\n");
        for (final String example : examples) {
            sb.append(example + "\n");
        }
        sb.append("\\end{verbatim}\n");
        sb.append("\\vspace{-1mm}\n");
        sb.append("\\end{formatbox}\n");
        sb.append("\\vspace{-2mm}\n");
        return sb.toString();
    }
    
    static String texDefine(final String define) {
        final StringBuilder sb = new StringBuilder();
        sb.append("\n% Define\n");
        sb.append("{\\tt\n");
        sb.append("\\begin{verbatim}\n");
        sb.append(define + "\n");
        sb.append("\\end{verbatim}\n");
        sb.append("}\n");
        sb.append("\\vspace{-4mm}\n");
        return sb.toString();
    }
}
