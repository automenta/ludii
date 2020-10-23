// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.util;

import java.util.Collection;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.jar.JarFile;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import java.io.IOException;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.io.File;

public class ClassFileUtilities
{
    public static final byte CONSTANT_UTF8_INFO = 1;
    public static final byte CONSTANT_INTEGER_INFO = 3;
    public static final byte CONSTANT_FLOAT_INFO = 4;
    public static final byte CONSTANT_LONG_INFO = 5;
    public static final byte CONSTANT_DOUBLE_INFO = 6;
    public static final byte CONSTANT_CLASS_INFO = 7;
    public static final byte CONSTANT_STRING_INFO = 8;
    public static final byte CONSTANT_FIELDREF_INFO = 9;
    public static final byte CONSTANT_METHODREF_INFO = 10;
    public static final byte CONSTANT_INTERFACEMETHODREF_INFO = 11;
    public static final byte CONSTANT_NAMEANDTYPE_INFO = 12;
    
    protected ClassFileUtilities() {
    }
    
    public static void main(final String[] args) {
        boolean showFiles = false;
        if (args.length == 1 && args[0].equals("-f")) {
            showFiles = true;
        }
        else if (args.length != 0) {
            System.err.println("usage: org.apache.batik.util.ClassFileUtilities [-f]");
            System.err.println();
            System.err.println("  -f    list files that cause each jar file dependency");
            System.exit(1);
        }
        final File cwd = new File(".");
        File buildDir = null;
        final String[] arr$;
        final String[] cwdFiles = arr$ = cwd.list();
        for (final String cwdFile : arr$) {
            if (cwdFile.startsWith("batik-")) {
                buildDir = new File(cwdFile);
                if (buildDir.isDirectory()) {
                    break;
                }
                buildDir = null;
            }
        }
        if (buildDir == null || !buildDir.isDirectory()) {
            System.out.println("Directory 'batik-xxx' not found in current directory!");
            return;
        }
        try {
            final Map cs = new HashMap();
            final Map js = new HashMap();
            collectJars(buildDir, js, cs);
            final Set classpath = new HashSet();
            Iterator i = js.values().iterator();
            while (i.hasNext()) {
                classpath.add(i.next().jarFile);
            }
            i = cs.values().iterator();
            while (i.hasNext()) {
                final ClassFile fromFile = i.next();
                final Set result = getClassDependencies(fromFile.getInputStream(), classpath, false);
                for (final Object aResult : result) {
                    final ClassFile toFile = cs.get(aResult);
                    if (fromFile != toFile && toFile != null) {
                        fromFile.deps.add(toFile);
                    }
                }
            }
            i = cs.values().iterator();
            while (i.hasNext()) {
                final ClassFile fromFile = i.next();
                for (final Object dep : fromFile.deps) {
                    final ClassFile toFile2 = (ClassFile)dep;
                    final Jar fromJar = fromFile.jar;
                    final Jar toJar = toFile2.jar;
                    if (!fromFile.name.equals(toFile2.name) && toJar != fromJar) {
                        if (fromJar.files.contains(toFile2.name)) {
                            continue;
                        }
                        final Integer n = fromJar.deps.get(toJar);
                        if (n == null) {
                            fromJar.deps.put(toJar, 1);
                        }
                        else {
                            fromJar.deps.put(toJar, n + 1);
                        }
                    }
                }
            }
            final List triples = new ArrayList(10);
            i = js.values().iterator();
            while (i.hasNext()) {
                final Jar fromJar2 = i.next();
                for (final Object o : fromJar2.deps.keySet()) {
                    final Jar toJar2 = (Jar)o;
                    final Triple t = new Triple();
                    t.from = fromJar2;
                    t.to = toJar2;
                    t.count = fromJar2.deps.get(toJar2);
                    triples.add(t);
                }
            }
            Collections.sort((List<Comparable>)triples);
            i = triples.iterator();
            while (i.hasNext()) {
                final Triple t2 = i.next();
                System.out.println(t2.count + "," + t2.from.name + "," + t2.to.name);
                if (showFiles) {
                    for (final Object file : t2.from.files) {
                        final ClassFile fromFile2 = (ClassFile)file;
                        for (final Object dep2 : fromFile2.deps) {
                            final ClassFile toFile3 = (ClassFile)dep2;
                            if (toFile3.jar == t2.to && !t2.from.files.contains(toFile3.name)) {
                                System.out.println("\t" + fromFile2.name + " --> " + toFile3.name);
                            }
                        }
                    }
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static void collectJars(final File dir, final Map jars, final Map classFiles) throws IOException {
        final File[] arr$;
        final File[] files = arr$ = dir.listFiles();
        for (final File file : arr$) {
            final String n = file.getName();
            if (n.endsWith(".jar") && file.isFile()) {
                final Jar j = new Jar();
                j.name = file.getPath();
                j.file = file;
                j.jarFile = new JarFile(file);
                jars.put(j.name, j);
                final Enumeration entries = j.jarFile.entries();
                while (entries.hasMoreElements()) {
                    final ZipEntry ze = entries.nextElement();
                    final String name = ze.getName();
                    if (name.endsWith(".class")) {
                        final ClassFile cf = new ClassFile();
                        cf.name = name;
                        cf.jar = j;
                        classFiles.put(j.name + '!' + cf.name, cf);
                        j.files.add(cf);
                    }
                }
            }
            else if (file.isDirectory()) {
                collectJars(file, jars, classFiles);
            }
        }
    }
    
    public static Set getClassDependencies(final String path, final Set classpath, final boolean rec) throws IOException {
        return getClassDependencies(new FileInputStream(path), classpath, rec);
    }
    
    public static Set getClassDependencies(final InputStream is, final Set classpath, final boolean rec) throws IOException {
        final Set result = new HashSet();
        final Set done = new HashSet();
        computeClassDependencies(is, classpath, done, result, rec);
        return result;
    }
    
    private static void computeClassDependencies(final InputStream is, final Set classpath, final Set done, final Set result, final boolean rec) throws IOException {
        for (final Object o : getClassDependencies(is)) {
            final String s = (String)o;
            if (!done.contains(s)) {
                done.add(s);
                for (final Object aClasspath : classpath) {
                    InputStream depis = null;
                    String path = null;
                    final Object cpEntry = aClasspath;
                    if (cpEntry instanceof JarFile) {
                        final JarFile jarFile = (JarFile)cpEntry;
                        final String classFileName = s + ".class";
                        final ZipEntry ze = jarFile.getEntry(classFileName);
                        if (ze != null) {
                            path = jarFile.getName() + '!' + classFileName;
                            depis = jarFile.getInputStream(ze);
                        }
                    }
                    else {
                        path = (String)cpEntry + '/' + s + ".class";
                        final File f = new File(path);
                        if (f.isFile()) {
                            depis = new FileInputStream(f);
                        }
                    }
                    if (depis != null) {
                        result.add(path);
                        if (!rec) {
                            continue;
                        }
                        computeClassDependencies(depis, classpath, done, result, rec);
                    }
                }
            }
        }
    }
    
    public static Set getClassDependencies(final InputStream is) throws IOException {
        final DataInputStream dis = new DataInputStream(is);
        if (dis.readInt() != -889275714) {
            throw new IOException("Invalid classfile");
        }
        dis.readInt();
        final int len = dis.readShort();
        final String[] strs = new String[len];
        final Set classes = new HashSet();
        final Set desc = new HashSet();
        for (int i = 1; i < len; ++i) {
            final int constCode = dis.readByte() & 0xFF;
            switch (constCode) {
                case 5:
                case 6: {
                    dis.readLong();
                    ++i;
                    break;
                }
                case 3:
                case 4:
                case 9:
                case 10:
                case 11: {
                    dis.readInt();
                    break;
                }
                case 7: {
                    classes.add(dis.readShort() & 0xFFFF);
                    break;
                }
                case 8: {
                    dis.readShort();
                    break;
                }
                case 12: {
                    dis.readShort();
                    desc.add(dis.readShort() & 0xFFFF);
                    break;
                }
                case 1: {
                    strs[i] = dis.readUTF();
                    break;
                }
                default: {
                    throw new RuntimeException("unexpected data in constant-pool:" + constCode);
                }
            }
        }
        final Set result = new HashSet();
        Iterator it = classes.iterator();
        while (it.hasNext()) {
            result.add(strs[it.next()]);
        }
        it = desc.iterator();
        while (it.hasNext()) {
            result.addAll(getDescriptorClasses(strs[it.next()]));
        }
        return result;
    }
    
    protected static Set getDescriptorClasses(final String desc) {
        final Set result = new HashSet();
        int i = 0;
        char c = desc.charAt(i);
        switch (c) {
            case '(': {
            Label_0181:
                while (true) {
                    c = desc.charAt(++i);
                    switch (c) {
                        case '[': {
                            do {
                                c = desc.charAt(++i);
                            } while (c == '[');
                            if (c != 'L') {
                                continue;
                            }
                        }
                        case 'L': {
                            c = desc.charAt(++i);
                            final StringBuffer sb = new StringBuffer();
                            while (c != ';') {
                                sb.append(c);
                                c = desc.charAt(++i);
                            }
                            result.add(sb.toString());
                            continue;
                        }
                        default: {
                            continue;
                        }
                        case ')': {
                            break Label_0181;
                        }
                    }
                }
                c = desc.charAt(++i);
                switch (c) {
                    case '[': {
                        do {
                            c = desc.charAt(++i);
                        } while (c == '[');
                        if (c != 'L') {
                            break;
                        }
                    }
                    case 'L': {
                        c = desc.charAt(++i);
                        final StringBuffer sb = new StringBuffer();
                        while (c != ';') {
                            sb.append(c);
                            c = desc.charAt(++i);
                        }
                        result.add(sb.toString());
                        break;
                    }
                }
                break;
            }
            case '[': {
                do {
                    c = desc.charAt(++i);
                } while (c == '[');
                if (c != 'L') {
                    break;
                }
            }
            case 'L': {
                c = desc.charAt(++i);
                final StringBuffer sb = new StringBuffer();
                while (c != ';') {
                    sb.append(c);
                    c = desc.charAt(++i);
                }
                result.add(sb.toString());
                break;
            }
        }
        return result;
    }
    
    protected static class ClassFile
    {
        public String name;
        public List deps;
        public Jar jar;
        
        protected ClassFile() {
            this.deps = new ArrayList(10);
        }
        
        public InputStream getInputStream() throws IOException {
            return this.jar.jarFile.getInputStream(this.jar.jarFile.getEntry(this.name));
        }
    }
    
    protected static class Jar
    {
        public String name;
        public File file;
        public JarFile jarFile;
        public Map deps;
        public Set files;
        
        protected Jar() {
            this.deps = new HashMap();
            this.files = new HashSet();
        }
    }
    
    protected static class Triple implements Comparable
    {
        public Jar from;
        public Jar to;
        public int count;
        
        @Override
        public int compareTo(final Object o) {
            return ((Triple)o).count - this.count;
        }
    }
}
