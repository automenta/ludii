// 
// Decompiled by Procyon v0.5.36
// 

package main;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

public class FileHandling {
    private static String[] gamesList;

    static {
        FileHandling.gamesList = null;
    }

    public static String[] listGames() {
        if (FileHandling.gamesList == null) {
            String[] choices = getResourceListing(FileHandling.class, "lud/", ".lud");
            if (choices == null) {
                try {
                    final URL url = FileHandling.class.getResource("/lud/board/space/line/Tic-Tac-Toe.lud");
                    String path = new File(url.toURI()).getPath();
                    path = path.substring(0, path.length() - "board/space/line/Tic-Tac-Toe.lud".length());
                    final List<String> names = new ArrayList<>();
                    visit(path, names);
                    Collections.sort(names);
                    choices = names.toArray(new String[0]);
                } catch (URISyntaxException exception) {
                    exception.printStackTrace();
                }
            }
            FileHandling.gamesList = choices;
        }
        return Arrays.stream(FileHandling.gamesList).filter(s -> !shouldIgnoreLud(s)).toArray(String[]::new);
    }

    private static boolean shouldIgnoreLud(final String lud) {
        return lud.contains("lud/bad/") || lud.contains("lud/bad_playout/") || lud.contains("lud/wishlist/");
    }

    static void visit(final String path, final List<String> names) {
        final File root = new File(path);
        final File[] list = root.listFiles();
        if (list == null) {
            return;
        }
        for (final File file : list) {
            if (file.isDirectory()) {
                if (!file.getName().equals("plex")) {
                    if (!file.getName().equals("bad")) {
                        if (!file.getName().equals("bad_playout")) {
                            if (!file.getName().equals("wishlist")) {
                                visit(path + file.getName() + File.separator, names);
                            }
                        }
                    }
                }
            } else if (file.getName().contains(".lud")) {
                final String name = file.getName();
                if (containsGame(path + File.separator + file.getName())) {
                    names.add(path.substring(path.indexOf(File.separator + "lud" + File.separator)) + name);
                }
            }
        }
    }

    public static boolean containsGame(final String filePath) {
        final File file = new File(filePath);
        if (file != null) {
            InputStream in = null;
            String path = file.getPath().replaceAll(Pattern.quote("\\"), "/");
            path = path.substring(path.indexOf("/lud/"));
            final URL url = FileHandling.class.getResource(path);
            try {
                in = new FileInputStream(new File(url.toURI()));
            } catch (FileNotFoundException | URISyntaxException ex2) {
                ex2.printStackTrace();
            }
            try (final BufferedReader rdr = new BufferedReader(new InputStreamReader(in))) {
                String line;
                while ((line = rdr.readLine()) != null) {
                    if (line.contains("(game")) {
                        return true;
                    }
                }
            } catch (Exception e) {
                System.out.println("FileHandling.containsGame(): Failed to load " + filePath + ".");
            }
        }
        return false;
    }

    public static String loadTextContentsFromFile(final String filePath) throws IOException {
        final StringBuilder sb = new StringBuilder();
        String line = null;
        try (final InputStreamReader isr = new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8);
             final BufferedReader bufferedReader = new BufferedReader(isr)) {
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }

    public static String[] getResourceListing(final Class<?> cls, final String path, final String filter) {
        URL dirURL = cls.getClassLoader().getResource(path);
        if (dirURL == null) {
            final String me = cls.getName().replace(".", "/") + ".class";
            dirURL = cls.getClassLoader().getResource(me);
        }
        if (dirURL != null && dirURL.getProtocol().equals("file")) {
            try {
                final String dirPath = dirURL.toURI().toString().substring("file:/".length()).replaceAll(Pattern.quote("%20"), " ");
                final String[] toReturn = listFilesOfType(dirPath, filter).toArray(new String[0]);
                for (int i = 0; i < toReturn.length; ++i) {
                    toReturn[i] = toReturn[i].replaceAll(Pattern.quote("\\"), "/");
                    toReturn[i] = "/" + toReturn[i].substring(toReturn[i].indexOf(path));
                }
                return toReturn;
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        if (dirURL.getProtocol().equals("jar")) {
            final String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf('!'));
            JarFile jar = null;
            try {
                jar = new JarFile(URLDecoder.decode(jarPath, StandardCharsets.UTF_8));
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            final Enumeration<JarEntry> entries = jar.entries();
            final List<String> result = new ArrayList<>();
            while (entries.hasMoreElements()) {
                final String filePath = entries.nextElement().getName();
                if (filePath.endsWith(filter) && filePath.startsWith(path)) {
                    result.add(File.separator + filePath);
                }
            }
            try {
                jar.close();
            } catch (IOException e4) {
                e4.printStackTrace();
            }
            Collections.sort(result);
            return result.toArray(new String[0]);
        }
        return null;
    }

    public static String[] getResourceListingSingle(final Class<?> cls, final String path, final String filter) {
        URL dirURL = cls.getClassLoader().getResource(path);
        if (dirURL == null) {
            final String me = cls.getName().replace(".", "/") + ".class";
            dirURL = cls.getClassLoader().getResource(me);
        }
        if (dirURL != null && dirURL.getProtocol().equals("file")) {
            try {
                final String dirPath = dirURL.toURI().toString().substring("file:/".length()).replaceAll(Pattern.quote("%20"), " ");
                if (new File(dirPath + filter).exists()) {
                    return new String[]{"/" + path + filter};
                }
                final String[] toReturn = listFilesOfType(dirPath, filter).toArray(new String[0]);
                for (int i = 0; i < toReturn.length; ++i) {
                    toReturn[i] = toReturn[i].replaceAll(Pattern.quote("\\"), "/");
                    toReturn[i] = "/" + toReturn[i].substring(toReturn[i].indexOf(path));
                }
                return toReturn;
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        if (!dirURL.getProtocol().equals("jar")) {
            return null;
        }
        final String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf('!'));
        JarFile jar = null;
        try {
            jar = new JarFile(URLDecoder.decode(jarPath, StandardCharsets.UTF_8));
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        final ZipEntry entry = jar.getEntry(path + filter);
        if (entry != null) {
            return new String[]{File.separator + path + filter};
        }
        final Enumeration<JarEntry> entries = jar.entries();
        final List<String> result = new ArrayList<>();
        while (entries.hasMoreElements()) {
            final String filePath = entries.nextElement().getName();
            if (filePath.endsWith(filter) && filePath.startsWith(path)) {
                System.out.println("filePath = " + filePath);
                result.add(File.separator + filePath);
            }
        }
        try {
            jar.close();
        } catch (IOException e4) {
            e4.printStackTrace();
        }
        Collections.sort(result);
        return result.toArray(new String[0]);
    }

    public static List<String> listFilesOfType(final String path, final String extension) {
        final List<String> files = new ArrayList<>();
        walk(path, files, extension);
        return files;
    }

    static void walk(final String path, final List<String> files, final String extension) {
        final File root = new File("/" + path);
        final File[] list = root.listFiles();
        if (list == null) {
            return;
        }
        for (final File file : list) {
            if (file.isDirectory()) {
                walk(file.getAbsolutePath(), files, extension);
            } else if (file.getName().contains(extension)) {
                files.add(file.getAbsolutePath());
            }
        }
    }

//    public static void findMissingConstructors() {
//        final List<String> files = listFilesOfType("/Users/cambolbro/Ludii/dev/Core/src/game", ".java");
//        System.out.println(files.size() + " .java files found.");
//        Label_0227_Outer:
//        for (final String path : files) {
//            int c;
//            for (c = path.length() - 1; c >= 0 && path.charAt(c) != '/'; --c) {
//            }
//            if (c < 0) {
//                continue;
//            }
//            final String className = path.substring(c + 1, path.length() - 5);
//            final String constructorName = "public " + className;
//            boolean abstractClass = false;
//
//            boolean constructorFound = false;
//            try {
//                final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8));
//                Throwable t = null;
//                Label_0314:
//                {
//                    try {
//                        while (true) {
//                            String line;
//                            do {
//                                line = reader.readLine();
//                                if (line != null) {
//                                    if (!line.contains("abstract class")) {
//                                        continue Label_0227_Outer;
//                                    }
//                                    abstractClass = true;
//                                    break Label_0314;
//                                }
//                            } while (!line.contains(constructorName) && !line.contains(" construct()"));
//                            constructorFound = true;
//                            break;
//                        }
//                    } finally {
//                        if (t != null) {
//                            try {
//                                reader.close();
//                            } catch (Throwable exception) {
//                                t.addSuppressed(exception);
//                            }
//                        } else {
//                            reader.close();
//                        }
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            if (abstractClass || constructorFound) {
//                continue;
//            }
//            System.out.println("Missing " + path);
//        }
//    }

    public static void findEmptyRulesets() {
        final List<String> files = listFilesOfType("/Users/cambolbro/Ludii/dev/Common/res/lud", ".lud");
        System.out.println(files.size() + " .lud files found.");
        for (final String path : files) {
            int c;
            for (c = path.length() - 1; c >= 0 && path.charAt(c) != '/'; --c) {
            }
            if (c < 0) {
                continue;
            }
            final StringBuilder sb = new StringBuilder();
            try (final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))) {
                String line;
                do {
                    line = reader.readLine();
                    sb.append(line);
                } while (line != null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            final String str = sb.toString();
            int r = str.indexOf("(ruleset");
            if (r < 0) {
                continue;
            }
            while (r < str.length() && str.charAt(r) != '{') {
                ++r;
            }
            if (r >= str.length()) {
                continue;
            }
            final int rr = StringRoutines.matchingBracketAt(str, r);
            if (rr < 0) {
                throw new RuntimeException("No closing '}' in ruleset: " + str);
            }
            final String sub = str.substring(r + 1, rr);
            boolean isChar = false;
            for (int s = 0; s < sub.length(); ++s) {
                final char ch = sub.charAt(s);
                if (StringRoutines.isTokenChar(ch) || StringRoutines.isNameChar(ch) || StringRoutines.isNumeric(ch) || StringRoutines.isBracket(ch)) {
                    isChar = true;
                }
            }
            if (isChar) {
                continue;
            }
            System.out.println(path + " has an empty ruleset.");
        }
    }

    public static void printOptionsToFile(final String fileName) throws IOException {
        final String[] list = listGames();
        final File file = new File(fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        try (final FileWriter fw = new FileWriter(file.getName(), false);
             final BufferedWriter writer = new BufferedWriter(fw)) {
            for (final String name : list) {
                final String str = gameAsString(name);
                System.out.println(name + "(" + str.length() + " chars).");
                final String[] subs = str.split("\n");
                writer.write("\n" + name + "(" + str.length() + " chars):\n");
                for (String sub : subs) {
                    if (sub.contains("(option ")) {
                        writer.write(sub + "\n");
                    }
                }
            }
        }
    }

    public static String gameAsString(final String name) {
        InputStream in = FileHandling.class.getResourceAsStream(name.startsWith("/lud/") ? name : ("/lud/" + name));
        if (in == null) {
            final String[] allGameNames = listGames();
            int shortestNonMatchLength = Integer.MAX_VALUE;
            String bestMatchFilepath = null;
            final String givenName = name.toLowerCase().replaceAll(Pattern.quote("\\"), "/");
            for (final String gameName : allGameNames) {
                final String str = gameName.toLowerCase().replaceAll(Pattern.quote("\\"), "/");
                if (str.endsWith(givenName)) {
                    final int nonMatchLength = str.length() - givenName.length();
                    if (nonMatchLength < shortestNonMatchLength) {
                        shortestNonMatchLength = nonMatchLength;
                        bestMatchFilepath = "..\\Common\\res\\" + gameName;
                    }
                }
            }
            String resourceStr = bestMatchFilepath.replaceAll(Pattern.quote("\\"), "/");
            resourceStr = resourceStr.substring(resourceStr.indexOf("/lud/"));
            in = FileHandling.class.getResourceAsStream(resourceStr);
        }
        final StringBuilder sb = new StringBuilder();
        try (final BufferedReader rdr = new BufferedReader(new InputStreamReader(in))) {
            String line;
            while ((line = rdr.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static boolean isCambolbro() {
        return isUser("/Users/cambolbro/eclipse/Ludii/dev/Player");
    }

    public static boolean isUser(final String userName) {
        final Path path = Paths.get(System.getProperty("user.dir"));
        return path.toString().contains(userName);
    }
}
