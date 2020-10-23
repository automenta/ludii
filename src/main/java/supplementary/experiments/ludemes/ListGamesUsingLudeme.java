// 
// Decompiled by Procyon v0.5.36
// 

package supplementary.experiments.ludemes;

import game.Game;
import gnu.trove.map.hash.TObjectIntHashMap;
import main.CommandLineArgParse;
import main.FileHandling;
import main.ReflectionUtils;
import util.GameLoader;
import util.Ludeme;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Pattern;

public class ListGamesUsingLudeme
{
    private String ludemeName;
    
    public void listGames() {
        final String[] allGames = FileHandling.listGames();
        final TObjectIntHashMap<String> gameCounts = new TObjectIntHashMap<>();
        final Set<String> matchingLudemes = new HashSet<>();
        for (String gameName : allGames) {
            gameName = gameName.replaceAll(Pattern.quote("\\"), "/");
            final String[] gameNameParts = gameName.split(Pattern.quote("/"));
            boolean skipGame = false;
            for (final String part : gameNameParts) {
                if (part.equals("bad") || part.equals("bad_playout") || part.equals("wip") || part.equals("test")) {
                    skipGame = true;
                    break;
                }
            }
            if (!skipGame) {
                final Game game = GameLoader.loadGameFromName(gameName);
                updateGameCounts(gameCounts, matchingLudemes, game, gameName, this.ludemeName, new HashMap<>());
            }
        }
        if (matchingLudemes.size() > 1) {
            System.err.println("Warning! Target ludeme name is ambiguous. Included ludemes:");
            for (final String name : matchingLudemes) {
                System.err.println(name);
            }
            System.err.println();
        }
        final String[] gameNames = gameCounts.keySet().toArray(new String[gameCounts.keySet().size()]);
        Arrays.sort(gameNames);
        System.out.println("Games using ludeme: " + this.ludemeName);
        for (final String gameName2 : gameNames) {
            System.out.println(gameName2 + ": " + gameCounts.get(gameName2));
        }
    }
    
    private static void updateGameCounts(final TObjectIntHashMap<String> gameCounts, final Set<String> matchingLudemes, final Ludeme ludeme, final String gameName, final String targetLudemeName, final Map<Object, Set<String>> visited) {
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
                    boolean matchesTargetLudeme = false;
                    if (valueClass.getName().endsWith(targetLudemeName)) {
                        matchesTargetLudeme = true;
                    }
                    else if (valueClass.getDeclaringClass() != null && valueClass.getDeclaringClass().getName().endsWith(targetLudemeName)) {
                        matchesTargetLudeme = true;
                    }
                    if (matchesTargetLudeme) {
                        matchingLudemes.add(valueClass.getName());
                        gameCounts.adjustOrPutValue(gameName, 1, 1);
                    }
                    updateGameCounts(gameCounts, matchingLudemes, (Ludeme)value, gameName, targetLudemeName, visited);
                }
                else if (valueClass.isArray()) {
                    final Object[] castArray;
                    final Object[] array = castArray = ReflectionUtils.castArray(value);
                    for (final Object element : castArray) {
                        if (element != null) {
                            final Class<?> elementClass = element.getClass();
                            if (Ludeme.class.isAssignableFrom(elementClass)) {
                                boolean matchesTargetLudeme2 = false;
                                if (elementClass.getName().endsWith(targetLudemeName)) {
                                    matchesTargetLudeme2 = true;
                                }
                                else if (elementClass.getDeclaringClass() != null && elementClass.getDeclaringClass().getName().endsWith(targetLudemeName)) {
                                    matchesTargetLudeme2 = true;
                                }
                                if (matchesTargetLudeme2) {
                                    matchingLudemes.add(elementClass.getName());
                                    gameCounts.adjustOrPutValue(gameName, 1, 1);
                                }
                                updateGameCounts(gameCounts, matchingLudemes, (Ludeme)element, gameName, targetLudemeName, visited);
                            }
                        }
                    }
                }
                else {
                    if (!List.class.isAssignableFrom(valueClass)) {
                        continue;
                    }
                    final List<?> list = (List<?>)value;
                    for (final Object element2 : list) {
                        if (element2 != null) {
                            final Class<?> elementClass2 = element2.getClass();
                            if (!Ludeme.class.isAssignableFrom(elementClass2)) {
                                continue;
                            }
                            boolean matchesTargetLudeme3 = false;
                            if (elementClass2.getName().endsWith(targetLudemeName)) {
                                matchesTargetLudeme3 = true;
                            }
                            else if (elementClass2.getDeclaringClass() != null && elementClass2.getDeclaringClass().getName().endsWith(targetLudemeName)) {
                                matchesTargetLudeme3 = true;
                            }
                            if (matchesTargetLudeme3) {
                                matchingLudemes.add(elementClass2.getName());
                                gameCounts.adjustOrPutValue(gameName, 1, 1);
                            }
                            updateGameCounts(gameCounts, matchingLudemes, (Ludeme)element2, gameName, targetLudemeName, visited);
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
        final CommandLineArgParse argParse = new CommandLineArgParse(true, "List all the games that use a particular ludeme (after compilation).");
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--ludeme").help("Name of the ludeme for which to find users.").withNumVals("1").withType(CommandLineArgParse.OptionTypes.String).setRequired());
        if (!argParse.parseArguments(args)) {
            return;
        }
        final ListGamesUsingLudeme func = new ListGamesUsingLudeme();
        func.ludemeName = (String)argParse.getValue("--ludeme");
        func.listGames();
    }
}
