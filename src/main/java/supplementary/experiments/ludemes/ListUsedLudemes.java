// 
// Decompiled by Procyon v0.5.36
// 

package supplementary.experiments.ludemes;

import game.Game;
import gnu.trove.map.hash.TObjectIntHashMap;
import main.CommandLineArgParse;
import main.ReflectionUtils;
import util.GameLoader;
import util.Ludeme;

import java.lang.reflect.Field;
import java.util.*;

public final class ListUsedLudemes
{
    private String gameName;
    private List<String> gameOptions;
    
    public void listUsedLudemes() {
        final Game game = GameLoader.loadGameFromName(this.gameName, this.gameOptions);
        final TObjectIntHashMap<String> ludemeCounts = new TObjectIntHashMap<>();
        ludemeCounts.put(Game.class.getName(), 1);
        updateLudemeCounts(ludemeCounts, game, new HashMap<>());
        final String[] ludemeNames = ludemeCounts.keySet().toArray(new String[0]);
        Arrays.sort(ludemeNames);
        System.out.println("Ludemes used by game: " + this.gameName);
        for (final String ludemeName : ludemeNames) {
            System.out.println(ludemeName + ": " + ludemeCounts.get(ludemeName));
        }
    }
    
    private static void updateLudemeCounts(final TObjectIntHashMap<String> ludemeCounts, final Ludeme ludeme, final Map<Object, Set<String>> visited) {
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
                    ludemeCounts.adjustOrPutValue(valueClass.getName(), 1, 1);
                    updateLudemeCounts(ludemeCounts, (Ludeme)value, visited);
                }
                else if (valueClass.isArray()) {
                    final Object[] castArray;
                    final Object[] array = castArray = ReflectionUtils.castArray(value);
                    for (final Object element : castArray) {
                        if (element != null) {
                            final Class<?> elementClass = element.getClass();
                            if (Ludeme.class.isAssignableFrom(elementClass)) {
                                ludemeCounts.adjustOrPutValue(element.getClass().getName(), 1, 1);
                                updateLudemeCounts(ludemeCounts, (Ludeme)element, visited);
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
                            ludemeCounts.adjustOrPutValue(element2.getClass().getName(), 1, 1);
                            updateLudemeCounts(ludemeCounts, (Ludeme)element2, visited);
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
        final CommandLineArgParse argParse = new CommandLineArgParse(true, "List all the ludemes used by a game (after compilation).");
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--game").help("Name of the game to inspect. Should end with \".lud\".").withNumVals("1").withType(CommandLineArgParse.OptionTypes.String).setRequired());
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--game-options").help("Game Options to load.").withDefault(new ArrayList(0)).withNumVals("*").withType(CommandLineArgParse.OptionTypes.String));
        if (!argParse.parseArguments(args)) {
            return;
        }
        final ListUsedLudemes func = new ListUsedLudemes();
        func.gameName = (String)argParse.getValue("--game");
        func.gameOptions = (List<String>)argParse.getValue("--game-options");
        func.listUsedLudemes();
    }
}
