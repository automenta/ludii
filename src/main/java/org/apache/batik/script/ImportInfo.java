// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.script;

import java.io.Reader;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Enumeration;
import java.net.URL;
import java.io.IOException;
import java.util.Set;

public class ImportInfo
{
    static final String defaultFile = "META-INF/imports/script.txt";
    static String importFile;
    static ImportInfo defaultImports;
    protected Set classes;
    protected Set packages;
    static final String classStr = "class";
    static final String packageStr = "package";
    
    public static ImportInfo getImports() {
        if (ImportInfo.defaultImports == null) {
            ImportInfo.defaultImports = readImports();
        }
        return ImportInfo.defaultImports;
    }
    
    static ImportInfo readImports() {
        final ImportInfo ret = new ImportInfo();
        final ClassLoader cl = ImportInfo.class.getClassLoader();
        if (cl == null) {
            return ret;
        }
        Enumeration e;
        try {
            e = cl.getResources(ImportInfo.importFile);
        }
        catch (IOException ioe) {
            return ret;
        }
        while (e.hasMoreElements()) {
            try {
                final URL url = e.nextElement();
                ret.addImports(url);
            }
            catch (Exception ex) {}
        }
        return ret;
    }
    
    public ImportInfo() {
        this.classes = new HashSet();
        this.packages = new HashSet();
    }
    
    public Iterator getClasses() {
        return Collections.unmodifiableSet((Set<?>)this.classes).iterator();
    }
    
    public Iterator getPackages() {
        return Collections.unmodifiableSet((Set<?>)this.packages).iterator();
    }
    
    public void addClass(final String cls) {
        this.classes.add(cls);
    }
    
    public void addPackage(final String pkg) {
        this.packages.add(pkg);
    }
    
    public boolean removeClass(final String cls) {
        return this.classes.remove(cls);
    }
    
    public boolean removePackage(final String pkg) {
        return this.packages.remove(pkg);
    }
    
    public void addImports(final URL src) throws IOException {
        InputStream is = null;
        Reader r = null;
        BufferedReader br = null;
        try {
            is = src.openStream();
            r = new InputStreamReader(is, "UTF-8");
            br = new BufferedReader(r);
            String line;
            while ((line = br.readLine()) != null) {
                int idx = line.indexOf(35);
                if (idx != -1) {
                    line = line.substring(0, idx);
                }
                line = line.trim();
                if (line.length() == 0) {
                    continue;
                }
                idx = line.indexOf(32);
                if (idx == -1) {
                    continue;
                }
                final String prefix = line.substring(0, idx);
                line = line.substring(idx + 1);
                final boolean isPackage = "package".equals(prefix);
                final boolean isClass = "class".equals(prefix);
                if (!isPackage && !isClass) {
                    continue;
                }
                while (line.length() != 0) {
                    idx = line.indexOf(32);
                    String id;
                    if (idx == -1) {
                        id = line;
                        line = "";
                    }
                    else {
                        id = line.substring(0, idx);
                        line = line.substring(idx + 1);
                    }
                    if (id.length() == 0) {
                        continue;
                    }
                    if (isClass) {
                        this.addClass(id);
                    }
                    else {
                        this.addPackage(id);
                    }
                }
            }
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (IOException ex) {}
                is = null;
            }
            if (r != null) {
                try {
                    r.close();
                }
                catch (IOException ex2) {}
                r = null;
            }
            if (br != null) {
                try {
                    br.close();
                }
                catch (IOException ex3) {}
                br = null;
            }
        }
    }
    
    static {
        ImportInfo.importFile = "META-INF/imports/script.txt";
        try {
            ImportInfo.importFile = System.getProperty("org.apache.batik.script.imports", "META-INF/imports/script.txt");
        }
        catch (SecurityException se) {}
        catch (NumberFormatException ex) {}
        ImportInfo.defaultImports = null;
    }
}
