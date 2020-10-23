// 
// Decompiled by Procyon v0.5.36
// 

package language.grammar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassEnumeratorFindAll
{
    private static Class<?> loadClass(final String className) {
        try {
            return Class.forName(className);
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException("Unexpected ClassNotFoundException loading class '" + className + "'");
        }
    }
    
    public static List<Class<?>> processDirectory(final File directory) {
        final ArrayList<Class<?>> classes = new ArrayList<>();
        try {
            final String[] files = directory.list();
            for (int i = 0; i < files.length; ++i) {
                final String fileName = files[i];
                String className = null;
                if (fileName.endsWith(".class")) {
                    className = fileName.substring(0, fileName.length() - 6);
                }
                if (className != null) {
                    classes.add(loadClass(className));
                }
                final File subdir = new File(directory, fileName);
                if (subdir.isDirectory()) {
                    classes.addAll(processDirectory(subdir));
                }
                if (fileName.endsWith(".jar")) {
                    classes.addAll(processJarfile(directory.getAbsolutePath() + "/" + fileName));
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return classes;
    }
    
    public static List<Class<?>> processJarfile(final String jarPath) {
        final List<Class<?>> classes = new ArrayList<>();
        try (final JarFile jarFile = new JarFile(jarPath)) {
            final Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                final JarEntry entry = entries.nextElement();
                final String entryName = entry.getName();
                String className = null;
                if (entryName.endsWith(".class")) {
                    className = entryName.replace('/', '.').replace('\\', '.').replace(".class", "");
                }
                if (className != null) {
                    classes.add(loadClass(className));
                }
                else if (entryName.endsWith(".jar")) {
                    classes.addAll(processJarfile(entryName));
                }
                else {
                    final File subdir = new File(entryName);
                    if (!subdir.isDirectory()) {
                        continue;
                    }
                    classes.addAll(processDirectory(subdir));
                }
            }
            jarFile.close();
        }
        catch (IOException e) {
            throw new RuntimeException("Unexpected IOException reading JAR File '" + jarPath + "'", e);
        }
        return classes;
    }
}
