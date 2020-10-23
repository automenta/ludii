// 
// Decompiled by Procyon v0.5.36
// 

package supplementary.experiments.ludemes;

import game.Game;
import language.grammar.ClassEnumerator;
import main.CommandLineArgParse;
import main.FileHandling;
import main.ReflectionUtils;
import util.GameLoader;
import util.Ludeme;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Pattern;

public class ListAllLudemesUsage
{
    public static void listAllLudemesUsage() {
        Class<?> clsRoot = null;
        try {
            clsRoot = Class.forName("game.Game");
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }
        final ArrayList<Class<?>> classes = (ArrayList<Class<?>>) ClassEnumerator.getClassesForPackage(clsRoot.getPackage());
        final Map<String, Set<String>> ludemesToUsingGames = new HashMap<>();
        for (final Class<?> clazz : classes) {
            if (clazz.getName().contains("$")) {
                continue;
            }
            if (!Ludeme.class.isAssignableFrom(clazz)) {
                continue;
            }
            ludemesToUsingGames.put(clazz.getName(), new HashSet<>());
        }
        final String[] listGames;
        final String[] allGames = listGames = FileHandling.listGames();
        for (String gameName : listGames) {
            gameName = gameName.replaceAll(Pattern.quote("\\"), "/");
            final String[] gameNameParts = gameName.split(Pattern.quote("/"));
            boolean skipGame = false;
            for (final String part : gameNameParts) {
                if (part.equals("bad") || part.equals("bad_playout") || part.equals("wip") || part.equals("test") || part.equals("wishlist")) {
                    skipGame = true;
                    break;
                }
            }
            if (!skipGame) {
                System.out.println("Checking game: " + gameName + "...");
                final Game game = GameLoader.loadGameFromName(gameName);
                ludemesToUsingGames.get(Game.class.getName()).add(gameName);
                updateMap(ludemesToUsingGames, game, gameName, new HashMap<>());
            }
        }
        System.out.println();
        final String[] ludemeNames = ludemesToUsingGames.keySet().toArray(new String[0]);
        Arrays.sort(ludemeNames);
        System.out.println("Usage of all ludemes:");
        for (final String ludemeName : ludemeNames) {
            final Set<String> games = ludemesToUsingGames.get(ludemeName);
            final StringBuilder sb = new StringBuilder();
            sb.append(ludemeName).append(": ");
            while (sb.length() < 62) {
                sb.append(" ");
            }
            sb.append(games.size()).append(" games");
            if (!games.isEmpty()) {
                final String[] sortedGames = games.toArray(new String[0]);
                Arrays.sort(sortedGames);
                sb.append("(");
                for (int i = 0; i < sortedGames.length; ++i) {
                    final String[] nameSplit = sortedGames[i].split(Pattern.quote("/"));
                    sb.append(nameSplit[nameSplit.length - 1]);
                    if (i + 1 < sortedGames.length) {
                        sb.append(", ");
                    }
                }
                sb.append(")");
            }
            System.out.println(sb.toString());
        }
        System.out.println();
        int numUnusedLudemes = 0;
        System.out.println("Unused Ludemes:");
        for (final String ludemeName2 : ludemeNames) {
            final Set<String> games2 = ludemesToUsingGames.get(ludemeName2);
            if (games2.isEmpty()) {
                System.out.println(ludemeName2);
                ++numUnusedLudemes;
            }
        }
        System.out.println();
        System.out.println("Number of ludemes used in at least 1 game: " + (ludemeNames.length - numUnusedLudemes));
    }
    
    private static void updateMap(final Map<String, Set<String>> ludemesToUsingGames, final Ludeme ludeme, final String gameName, final Map<Object, Set<String>> visited) {
        final Class<? extends Ludeme> clazz = ludeme.getClass();
        final List<Field> fields = ReflectionUtils.getAllFields(clazz);
        try {
            for (final Field field : fields) {
                field.setAccessible(true);
                if ((field.getModifiers() & 0x8) != 0x0) {
                    continue;
                }
                if (visited.containsKey(ludeme) && visited.get(ludeme).contains(field.getName())) {
                    continue;
                }
                final Object value = field.get(ludeme);
                if (!visited.containsKey(ludeme)) {
                    visited.put(ludeme, new HashSet<>());
                }
                visited.get(ludeme).add(field.getName());
                if (value == null) {
                    continue;
                }
                final Class<?> valueClass = value.getClass();
                if (Ludeme.class.isAssignableFrom(valueClass)) {
                    if (ludemesToUsingGames.containsKey(valueClass.getName())) {
                        ludemesToUsingGames.get(valueClass.getName()).add(gameName);
                    }
                    else if (valueClass.getDeclaringClass() != null && ludemesToUsingGames.containsKey(valueClass.getDeclaringClass().getName())) {
                        ludemesToUsingGames.get(valueClass.getDeclaringClass().getName()).add(gameName);
                    }
                    else if (!valueClass.getName().contains("$")) {
                        System.err.println("WARNING: ludeme class " + valueClass.getName() + " not in map!");
                    }
                    updateMap(ludemesToUsingGames, (Ludeme)value, gameName, visited);
                }
                else if (valueClass.isArray()) {
                    final Object[] castArray;
                    final Object[] array = castArray = ReflectionUtils.castArray(value);
                    for (final Object element : castArray) {
                        if (element != null) {
                            final Class<?> elementClass = element.getClass();
                            if (Ludeme.class.isAssignableFrom(elementClass)) {
                                if (ludemesToUsingGames.containsKey(elementClass.getName())) {
                                    ludemesToUsingGames.get(elementClass.getName()).add(gameName);
                                }
                                else if (elementClass.getDeclaringClass() != null && ludemesToUsingGames.containsKey(elementClass.getDeclaringClass().getName())) {
                                    ludemesToUsingGames.get(elementClass.getDeclaringClass().getName()).add(gameName);
                                }
                                else if (!elementClass.getName().contains("$")) {
                                    System.err.println("WARNING: ludeme class " + elementClass.getName() + " not in map!");
                                }
                                updateMap(ludemesToUsingGames, (Ludeme)element, gameName, visited);
                            }
                        }
                    }
                }
                else {
                    if (!Iterable.class.isAssignableFrom(valueClass)) {
                        continue;
                    }
                    final Iterable<?> iterable = (Iterable<?>)value;
                    for (final Object element2 : iterable) {
                        if (element2 != null) {
                            final Class<?> elementClass2 = element2.getClass();
                            if (!Ludeme.class.isAssignableFrom(elementClass2)) {
                                continue;
                            }
                            if (ludemesToUsingGames.containsKey(elementClass2.getName())) {
                                ludemesToUsingGames.get(elementClass2.getName()).add(gameName);
                            }
                            else if (elementClass2.getDeclaringClass() != null && ludemesToUsingGames.containsKey(elementClass2.getDeclaringClass().getName())) {
                                ludemesToUsingGames.get(elementClass2.getDeclaringClass().getName()).add(gameName);
                            }
                            else if (!elementClass2.getName().contains("$")) {
                                System.err.println("WARNING: ludeme class " + elementClass2.getName() + " not in map!");
                            }
                            updateMap(ludemesToUsingGames, (Ludeme)element2, gameName, visited);
                        }
                    }
                }
            }
        }
        catch (IllegalArgumentException | IllegalAccessException ex2) {
            ex2.printStackTrace();
        }
    }
    
    public static void main(final String[] args) {
        final CommandLineArgParse argParse = new CommandLineArgParse(true, "List information on usage for ALL ludemes in Ludii.");
        if (!argParse.parseArguments(args)) {
            return;
        }
        listAllLudemesUsage();
    }
}
