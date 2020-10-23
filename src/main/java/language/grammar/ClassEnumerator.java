// 
// Decompiled by Procyon v0.5.36
// 

package language.grammar;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassEnumerator
{
    private static Class<?> loadClass(final String className) {
        try {
            return Class.forName(className);
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException("Unexpected ClassNotFoundException loading class '" + className + "'");
        }
    }
    
    public static List<Class<?>> processDirectory(final File directory, final String pkgname) {
        final ArrayList<Class<?>> classes = new ArrayList<>();
        final String[] files = directory.list();
        for (final String fileName : files) {
            String className = null;
            if (fileName.endsWith(".class")) {
                className = pkgname + '.' + fileName.substring(0, fileName.length() - 6);
            }
            if (className != null) {
                classes.add(loadClass(className));
            }
            final File subdir = new File(directory, fileName);
            if (subdir.isDirectory()) {
                classes.addAll(processDirectory(subdir, pkgname + '.' + fileName));
            }
        }
        return classes;
    }
    
    public static List<Class<?>> processJarfile(final JarFile jarFile, final String pkgname) {
        final List<Class<?>> classes = new ArrayList<>();
        final String relPath = pkgname.replace('.', '/');
        try {
            final Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                final JarEntry entry = entries.nextElement();
                final String entryName = entry.getName();
                String className = null;
                if (entryName.endsWith(".class") && entryName.startsWith(relPath) && entryName.length() > relPath.length() + "/".length()) {
                    className = entryName.replace('/', '.').replace('\\', '.').replace(".class", "");
                }
                if (className != null) {
                    classes.add(loadClass(className));
                }
            }
            jarFile.close();
        }
        catch (IOException e) {
            throw new RuntimeException("Unexpected IOException reading JAR File '" + jarFile.getName() + "'", e);
        }
        return classes;
    }
    
    public static List<Class<?>> getClassesForPackage(final Package pkg) {
        final ArrayList<Class<?>> classes = new ArrayList<>();
        final String pkgname = pkg.getName();
        final String relPath = pkgname.replace('.', '/');
        final URL url = ClassLoader.getSystemClassLoader().getResource(relPath);
        if (url.getPath().contains(".jar")) {
            try {
                try (final JarFile jarFile = new JarFile(new File(ClassEnumerator.class.getProtectionDomain().getCodeSource().getLocation().toURI()))) {
                    classes.addAll(processJarfile(jarFile, pkgname));
                }
                return classes;
            }
            catch (IOException | URISyntaxException ex2) {
                throw new RuntimeException("Unexpected problem with JAR file for: " + relPath, ex2);
            }
        }
        classes.addAll(processDirectory(new File(URLDecoder.decode(url.getPath(), StandardCharsets.UTF_8)), pkgname));
        return classes;
    }
}
