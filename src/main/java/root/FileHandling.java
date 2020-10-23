package root;/*
 * Decompiled with CFR 0.150.
 */

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
    private static String[] gamesList = null;

    public static String[] listGames() {
        if (gamesList == null) {
            String[] choices = FileHandling.getResourceListing(FileHandling.class, "lud/", ".lud");
            if (choices == null) {
                try {
                    URL url = FileHandling.class.getResource("/lud/board/space/line/Tic-Tac-Toe.lud");
                    String path = new File(url.toURI()).getPath();
                    path = path.substring(0, path.length() - "board/space/line/Tic-Tac-Toe.lud".length());
                    ArrayList<String> names = new ArrayList<>();
                    FileHandling.visit(path, names);
                    Collections.sort(names);
                    choices = names.toArray(new String[names.size()]);
                }
                catch (URISyntaxException exception) {
                    exception.printStackTrace();
                }
            }
            gamesList = choices;
        }
        return Arrays.stream(gamesList).filter(s -> !FileHandling.shouldIgnoreLud(s)).toArray(String[]::new);
    }

    private static boolean shouldIgnoreLud(String lud) {
        return lud.contains("lud/bad/") || lud.contains("lud/bad_playout/") || lud.contains("lud/wishlist/");
    }

    static void visit(String path, List<String> names) {
        File root = new File(path);
        File[] list = root.listFiles();
        if (list == null) {
            return;
        }
        for (File file : list) {
            if (file.isDirectory()) {
                if (file.getName().equals("plex") || file.getName().equals("bad") || file.getName().equals("bad_playout") || file.getName().equals("wishlist")) continue;
                FileHandling.visit(path + file.getName() + File.separator, names);
                continue;
            }
            if (!file.getName().contains(".lud")) continue;
            String name = file.getName();
            if (!FileHandling.containsGame(path + File.separator + file.getName())) continue;
            names.add(path.substring(path.indexOf(File.separator + "lud" + File.separator)) + name);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static boolean containsGame(String filePath) {
        File file = new File(filePath);
        if (file == null) return false;
        FileInputStream in = null;
        String path = file.getPath().replaceAll(Pattern.quote("\\"), "/");
        path = path.substring(path.indexOf("/lud/"));
        URL url = FileHandling.class.getResource(path);
        try {
            in = new FileInputStream(new File(url.toURI()));
        }
        catch (FileNotFoundException | URISyntaxException e) {
            e.printStackTrace();
        }
        try (BufferedReader rdr = new BufferedReader(new InputStreamReader(in))){
            String line;
            do {
                if ((line = rdr.readLine()) == null) return false;
            } while (!line.contains("(game"));
            boolean bl = true;
            return bl;
        }
        catch (Exception e) {
            System.out.println("root.FileHandling.containsGame(): Failed to load " + filePath + ".");
        }
        return false;
    }

    public static String loadTextContentsFromFile(String filePath) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line = null;
        try (InputStreamReader isr = new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(isr)){
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line + "\n");
            }
        }
        return sb.toString();
    }

    public static String[] getResourceListing(Class<?> cls, String path, String filter) {
        URL dirURL = cls.getClassLoader().getResource(path);
        if (dirURL == null) {
            String me = cls.getName().replace(".", "/") + ".class";
            dirURL = cls.getClassLoader().getResource(me);
        }
        if (dirURL != null && dirURL.getProtocol().equals("file")) {
            try {
                String dirPath = dirURL.toURI().toString().substring("file:/".length()).replaceAll(Pattern.quote("%20"), " ");
                String[] toReturn = FileHandling.listFilesOfType(dirPath, filter).toArray(new String[0]);
                for (int i = 0; i < toReturn.length; ++i) {
                    toReturn[i] = toReturn[i].replaceAll(Pattern.quote("\\"), "/");
                    toReturn[i] = "/" + toReturn[i].substring(toReturn[i].indexOf(path));
                }
                return toReturn;
            }
            catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        if (dirURL.getProtocol().equals("jar")) {
            String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!"));
            JarFile jar = null;
            try {
                jar = new JarFile(URLDecoder.decode(jarPath, StandardCharsets.UTF_8));
            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            Enumeration<JarEntry> entries = jar.entries();
            ArrayList<String> result = new ArrayList<>();
            while (entries.hasMoreElements()) {
                String filePath = entries.nextElement().getName();
                if (!filePath.endsWith(filter) || !filePath.startsWith(path)) continue;
                result.add(File.separator + filePath);
            }
            try {
                jar.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            Collections.sort(result);
            return result.toArray(new String[result.size()]);
        }
        return null;
    }

    public static String[] getResourceListingSingle(Class<?> cls, String path, String filter) {
        URL dirURL = cls.getClassLoader().getResource(path);
        if (dirURL == null) {
            String me = cls.getName().replace(".", "/") + ".class";
            dirURL = cls.getClassLoader().getResource(me);
        }
        if (dirURL != null && dirURL.getProtocol().equals("file")) {
            try {
                String dirPath = dirURL.toURI().toString().substring("file:/".length()).replaceAll(Pattern.quote("%20"), " ");
                if (new File(dirPath + filter).exists()) {
                    return new String[]{"/" + path + filter};
                }
                String[] toReturn = FileHandling.listFilesOfType(dirPath, filter).toArray(new String[0]);
                for (int i = 0; i < toReturn.length; ++i) {
                    toReturn[i] = toReturn[i].replaceAll(Pattern.quote("\\"), "/");
                    toReturn[i] = "/" + toReturn[i].substring(toReturn[i].indexOf(path));
                }
                return toReturn;
            }
            catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        if (dirURL.getProtocol().equals("jar")) {
            String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!"));
            JarFile jar = null;
            try {
                jar = new JarFile(URLDecoder.decode(jarPath, StandardCharsets.UTF_8));
            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            ZipEntry entry = jar.getEntry(path + filter);
            if (entry != null) {
                return new String[]{File.separator + path + filter};
            }
            Enumeration<JarEntry> entries = jar.entries();
            ArrayList<String> result = new ArrayList<>();
            while (entries.hasMoreElements()) {
                String filePath = entries.nextElement().getName();
                if (!filePath.endsWith(filter) || !filePath.startsWith(path)) continue;
                System.out.println("filePath = " + filePath);
                result.add(File.separator + filePath);
            }
            try {
                jar.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            Collections.sort(result);
            return result.toArray(new String[result.size()]);
        }
        return null;
    }

    public static List<String> listFilesOfType(String path, String extension) {
        ArrayList<String> files = new ArrayList<>();
        FileHandling.walk(path, files, extension);
        return files;
    }

    static void walk(String path, List<String> files, String extension) {
        File root = new File("/" + path);
        File[] list = root.listFiles();
        if (list == null) {
            return;
        }
        for (File file : list) {
            if (file.isDirectory()) {
                FileHandling.walk(file.getAbsolutePath(), files, extension);
                continue;
            }
            if (!file.getName().contains(extension)) continue;
            files.add(file.getAbsolutePath());
        }
    }

    public static void findMissingConstructors() {
        List<String> files = FileHandling.listFilesOfType("/Users/cambolbro/Ludii/dev/Core/src/game", ".java");
        System.out.println(files.size() + " .java files found.");
        for (String path : files) {
            boolean constructorFound;
            boolean abstractClass;
            block19: {
                int c;
                for (c = path.length() - 1; c >= 0 && path.charAt(c) != '/'; --c) {
                }
                if (c < 0) continue;
                String className = path.substring(c + 1, path.length() - 5);
                String constructorName = "public " + className;
                abstractClass = false;
                constructorFound = false;
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))){
                    String line;
                    do {
                        if ((line = reader.readLine()) == null) {
                        } else {
                            if (!line.contains("abstract class")) continue;
                            abstractClass = true;
                        }
                        break block19;
                    } while (!line.contains(constructorName) && !line.contains(" construct()"));
                    constructorFound = true;
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (abstractClass || constructorFound) continue;
            System.out.println("Missing " + path);
        }
    }

    public static void findEmptyRulesets() {
        List<String> files = FileHandling.listFilesOfType("/Users/cambolbro/Ludii/dev/Common/res/lud", ".lud");
        System.out.println(files.size() + " .lud files found.");
        for (String path : files) {
            String str;
            int r;
            int c;
            for (c = path.length() - 1; c >= 0 && path.charAt(c) != '/'; --c) {
            }
            if (c < 0) continue;
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))){
                String line;
                do {
                    line = reader.readLine();
                    sb.append(line);
                } while (line != null);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            if ((r = (str = sb.toString()).indexOf("(ruleset")) < 0) continue;
            while (r < str.length() && str.charAt(r) != '{') {
                ++r;
            }
            if (r >= str.length()) continue;
            int rr = StringRoutines.matchingBracketAt(str, r);
            if (rr < 0) {
                throw new RuntimeException("No closing '}' in ruleset: " + str);
            }
            String sub = str.substring(r + 1, rr);
            boolean isChar = false;
            for (int s = 0; s < sub.length(); ++s) {
                char ch = sub.charAt(s);
                if (!StringRoutines.isTokenChar(ch) && !StringRoutines.isNameChar(ch) && !StringRoutines.isNumeric(ch) && !StringRoutines.isBracket(ch)) continue;
                isChar = true;
            }
            if (isChar) continue;
            System.out.println(path + " has an empty ruleset.");
        }
    }

    public static void printOptionsToFile(String fileName) throws IOException {
        String[] list = FileHandling.listGames();
        File file = new File(fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        try (FileWriter fw = new FileWriter(file.getName(), false);
             BufferedWriter writer = new BufferedWriter(fw)){
            for (String name : list) {
                String str = FileHandling.gameAsString(name);
                System.out.println(name + "(" + str.length() + " chars).");
                String[] subs = str.split("\n");
                writer.write("\n" + name + "(" + str.length() + " chars):\n");
                for (int n = 0; n < subs.length; ++n) {
                    if (!subs[n].contains("(option ")) continue;
                    writer.write(subs[n] + "\n");
                }
            }
        }
    }

    public static String gameAsString(String name) {
        InputStream in = FileHandling.class.getResourceAsStream(name.startsWith("/lud/") ? name : "/lud/" + name);
        if (in == null) {
            String[] allGameNames = FileHandling.listGames();
            int shortestNonMatchLength = Integer.MAX_VALUE;
            String bestMatchFilepath = null;
            String givenName = name.toLowerCase().replaceAll(Pattern.quote("\\"), "/");
            for (String gameName : allGameNames) {
                int nonMatchLength;
                String str = gameName.toLowerCase().replaceAll(Pattern.quote("\\"), "/");
                if (!str.endsWith(givenName) || (nonMatchLength = str.length() - givenName.length()) >= shortestNonMatchLength) continue;
                shortestNonMatchLength = nonMatchLength;
                bestMatchFilepath = "..\\Common\\res\\" + gameName;
            }
            String resourceStr = bestMatchFilepath.replaceAll(Pattern.quote("\\"), "/");
            resourceStr = resourceStr.substring(resourceStr.indexOf("/lud/"));
            in = FileHandling.class.getResourceAsStream(resourceStr);
        }
        StringBuilder sb = new StringBuilder();
        try (BufferedReader rdr = new BufferedReader(new InputStreamReader(in))){
            String line;
            while ((line = rdr.readLine()) != null) {
                sb.append(line + "\n");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static boolean isCambolbro() {
        return FileHandling.isUser("/Users/cambolbro/eclipse/Ludii/dev/Player");
    }

    public static boolean isUser(String userName) {
        Path path = Paths.get(System.getProperty("user.dir"));
        return path.toString().contains(userName);
    }
}

