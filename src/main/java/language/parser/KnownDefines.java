// 
// Decompiled by Procyon v0.5.36
// 

package language.parser;

import grammar.Report;
import main.FileHandling;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class KnownDefines
{
    private final Map<String, Define> knownDefines;
    
    KnownDefines() {
        this.knownDefines = new HashMap<>();
        final Report report = new Report();
        this.loadKnownDefines(report);
        if (report.isError()) {
            System.out.println(report);
        }
    }
    
    public static KnownDefines getKnownDefines() {
        return KnownDefinesProvider.KNOWN_DEFINES;
    }
    
    public Map<String, Define> knownDefines() {
        return this.knownDefines;
    }
    
    void loadKnownDefines(final Report report) {
        this.knownDefines.clear();
        final String[] defs = FileHandling.getResourceListing(KnownDefines.class, "def/", ".def");
        if (defs == null) {
            final URL url = KnownDefines.class.getResource("def/rules/play/moves/StepToEmpty.def");
            try {
                String path = new File(url.toURI()).getPath();
                path = path.substring(0, path.length() - "rules/play/moves/StepToEmpty.def".length());
                this.recurseKnownDefines(path, report);
                if (report.isError()) {
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            for (final String def : defs) {
                final Define define = processDefFile(def.replaceAll(Pattern.quote("\\"), "/"), "/def/", report);
                if (report.isError()) {
                    return;
                }
                this.knownDefines.put(define.tag(), define);
            }
        }
    }
    
    void recurseKnownDefines(final String path, final Report report) {
        final File root = new File(path);
        final File[] list = root.listFiles();
        if (list == null) {
            return;
        }
        for (final File file : list) {
            if (file.isDirectory()) {
                this.recurseKnownDefines(path + file.getName() + File.separator, report);
                if (report.isError()) {
                    return;
                }
            }
            else if (file.getName().contains(".def")) {
                final String filePath = path + file.getName();
                final Define define = processDefFile(filePath.replaceAll(Pattern.quote("\\"), "/"), "/def/", report);
                if (report.isError()) {
                    return;
                }
                this.knownDefines.put(define.tag(), define);
            }
        }
    }
    
    public static Define processDefFile(final String defFilePath, final String defRoot, final Report report) {
        final InputStream in = KnownDefines.class.getResourceAsStream(defFilePath.substring(defFilePath.indexOf(defRoot)));
        final StringBuilder sb = new StringBuilder();
        try (final BufferedReader rdr = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = rdr.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        final Define define = Expander.interpretDefine(sb.toString(), null, report);
        return define;
    }
    
    private static class KnownDefinesProvider
    {
        public static final KnownDefines KNOWN_DEFINES;
        
        static {
            KNOWN_DEFINES = new KnownDefines();
        }
    }
}
