// 
// Decompiled by Procyon v0.5.36
// 

package app.loading.aliases;

import game.Game;
import util.GameLoader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class GenerateAliasesFile
{
    private static final String ALIASES_FILEPATH = "../PlayerDesktop/res/help/Aliases.txt";
    
    private GenerateAliasesFile() {
    }
    
    public static void main(final String[] args) {
        final File startFolder = new File("../Common/res/lud/");
        final List<File> gameDirs = new ArrayList<>();
        gameDirs.add(startFolder);
        final List<File> entries = new ArrayList<>();
        for (int i = 0; i < gameDirs.size(); ++i) {
            final File gameDir = gameDirs.get(i);
            for (final File fileEntry : gameDir.listFiles()) {
                if (fileEntry.isDirectory()) {
                    final String path = fileEntry.getPath().replaceAll(Pattern.quote("\\"), "/");
                    if (!path.equals("../Common/res/lud/plex")) {
                        if (!path.equals("../Common/res/lud/wishlist")) {
                            if (!path.equals("../Common/res/lud/WishlistDLP")) {
                                if (!path.equals("../Common/res/lud/wip")) {
                                    if (!path.equals("../Common/res/lud/test")) {
                                        if (!path.equals("../Common/res/lud/bad")) {
                                            if (!path.equals("../Common/res/lud/bad_playout")) {
                                                gameDirs.add(fileEntry);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                else if (fileEntry.getName().contains(".lud")) {
                    entries.add(fileEntry);
                }
            }
        }
        try (final PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream("../PlayerDesktop/res/help/Aliases.txt"), StandardCharsets.UTF_8))) {
            for (final File fileEntry2 : entries) {
                System.out.println("Processing: " + fileEntry2.getAbsolutePath() + "...");
                final Game game = GameLoader.loadGameFromFile(fileEntry2);
                final String[] aliases = game.metadata().info().getAliases();
                if (aliases != null && aliases.length > 0) {
                    final String fileEntryPath = fileEntry2.getAbsolutePath().replaceAll(Pattern.quote("\\"), "/");
                    final int ludPathStartIdx = fileEntryPath.indexOf("/lud/");
                    writer.println(fileEntryPath.substring(ludPathStartIdx));
                    for (final String alias : aliases) {
                        writer.println(alias);
                    }
                }
            }
            System.out.println("Finished processing aliases.");
            System.out.println("Wrote to file: " + new File("../PlayerDesktop/res/help/Aliases.txt").getAbsolutePath());
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
