// 
// Decompiled by Procyon v0.5.36
// 

package app.utils;

import game.Game;
import game.types.state.GameType;
import main.FileHandling;
import main.StringRoutines;
import main.UnixPrintWriter;
import util.GameLoader;

import java.io.*;
import java.lang.reflect.Field;
import java.util.regex.Pattern;

public class ListGameFlags
{
    public static void main(final String[] args) throws IllegalArgumentException, IllegalAccessException {
        try (final PrintWriter writer = new UnixPrintWriter(new File("./LudiiGameFlags.csv"), "UTF-8")) {
            final Field[] fields = GameType.class.getFields();
            final String[] flags = new String[fields.length];
            final long[] flagsValues = new long[fields.length];
            for (int i = 0; i < fields.length; ++i) {
                flags[i] = fields[i].toString();
                flags[i] = flags[i].substring(flags[i].lastIndexOf(46) + 1);
                flagsValues[i] = fields[i].getLong(GameType.class);
            }
            final String[] headers = new String[flags.length + 1];
            headers[0] = "Game Name";
            System.arraycopy(flags, 0, headers, 1, flags.length);
            writer.println(StringRoutines.join(";", headers));
            final String[] listGames;
            final String[] gameNames = listGames = FileHandling.listGames();
            for (final String gameName : listGames) {
                if (!gameName.replaceAll(Pattern.quote("\\"), "/").contains("/lud/bad/")) {
                    if (!gameName.replaceAll(Pattern.quote("\\"), "/").contains("/lud/wip/")) {
                        if (!gameName.replaceAll(Pattern.quote("\\"), "/").contains("/lud/WishlistDLP/")) {
                            if (!gameName.replaceAll(Pattern.quote("\\"), "/").contains("/lud/test/")) {
                                System.out.println("Loading game: " + gameName);
                                final Game game = GameLoader.loadGameFromName(gameName);
                                final String[] flagsOn = new String[flags.length + 1];
                                flagsOn[0] = game.name();
                                for (int k = 0; k < flagsValues.length; ++k) {
                                    if ((game.gameFlags() & flagsValues[k]) != 0x0L) {
                                        flagsOn[k + 1] = "Yes";
                                    }
                                    else {
                                        flagsOn[k + 1] = "";
                                    }
                                }
                                writer.println(StringRoutines.join(";", flagsOn));
                            }
                        }
                    }
                }
            }
        }
        catch (FileNotFoundException | UnsupportedEncodingException ex2) {
            ex2.printStackTrace();
        }
    }
}
