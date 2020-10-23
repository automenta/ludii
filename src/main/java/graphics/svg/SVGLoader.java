// 
// Decompiled by Procyon v0.5.36
// 

package graphics.svg;

import main.FileHandling;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public final class SVGLoader
{
    private static String[] choices;
    
    private SVGLoader() {
    }
    
    public static boolean containsSVG(final String filePath) {
        final File file = new File(filePath);
        if (file != null) {
            InputStream in = null;
            String path = file.getPath().replaceAll(Pattern.quote("\\"), "/");
            path = path.substring(path.indexOf("/svg/"));
            final URL url = SVGLoader.class.getResource(path);
            try {
                in = new FileInputStream(new File(url.toURI()));
            }
            catch (FileNotFoundException | URISyntaxException ex2) {
                ex2.printStackTrace();
            }
            try (final BufferedReader rdr = new BufferedReader(new InputStreamReader(in))) {
                String line;
                while ((line = rdr.readLine()) != null) {
                    if (line.contains("svg")) {
                        return true;
                    }
                }
            }
            catch (Exception e) {
                System.out.println("GameLoader.containsGame(): Failed to load " + filePath + ".");
            }
        }
        return false;
    }
    
    public static String[] listSVGs() {
        if (SVGLoader.choices == null) {
            SVGLoader.choices = FileHandling.getResourceListing(SVGLoader.class, "svg/", ".svg");
            if (SVGLoader.choices == null) {
                try {
                    final URL url = SVGLoader.class.getResource("/svg/misc/dot.svg");
                    String path = new File(url.toURI()).getPath();
                    path = path.substring(0, path.length() - "misc/dot.svg".length());
                    final List<String> names = new ArrayList<>();
                    visit(path, names);
                    Collections.sort(names);
                    SVGLoader.choices = names.toArray(new String[0]);
                }
                catch (URISyntaxException exception) {
                    exception.printStackTrace();
                }
            }
        }
        return SVGLoader.choices;
    }
    
    static void visit(final String path, final List<String> names) {
        final File root = new File(path);
        final File[] list = root.listFiles();
        if (list == null) {
            return;
        }
        for (final File file : list) {
            if (file.isDirectory()) {
                visit(path + file.getName() + File.separator, names);
            }
            else if (file.getName().contains(".svg")) {
                final String name = file.getName();
                if (containsSVG(path + File.separator + file.getName())) {
                    names.add(path.substring(path.indexOf(File.separator + "svg" + File.separator)) + name);
                }
            }
        }
    }
    
    static {
        SVGLoader.choices = null;
    }
}
