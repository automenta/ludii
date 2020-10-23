// 
// Decompiled by Procyon v0.5.36
// 

package supplementary.bgg;

import app.loading.aliases.AliasesData;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Database
{
    private static final List<Integer> validGameIds;
    
    public static void saveValidGameIds() {
        try {
            final BufferedReader reader = new BufferedReader(new FileReader("../Common/res/recommender/validBGGId.txt"));
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                try {
                    final int gameId = Integer.parseInt(line.trim().toLowerCase());
                    Database.validGameIds.add(gameId);
                }
                catch (Exception ex) {}
            }
            reader.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void findGameMatchesFromTextFile(final BggData data, final boolean getRecommendations) {
        saveValidGameIds();
        int numMatches = 0;
        try {
            final BufferedReader reader = new BufferedReader(new FileReader("../Common/res/recommender/gameNames.txt"));
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                final String gameName = line.trim().toLowerCase();
                List<String> aliases = new ArrayList<>();
                aliases.add(gameName);
                final AliasesData aliasesData = AliasesData.loadData();
                final List<String> loadedAliases = aliasesData.aliasesForGameName(gameName);
                if (loadedAliases != null) {
                    aliases.addAll(loadedAliases);
                }
                else {
                    aliases = new ArrayList<>();
                    aliases.add(gameName);
                }
                boolean matchFound = false;
                for (final String name : aliases) {
                    if (!matchFound) {
                        final BggGame game = Recommender.findGame(data, name.trim().toLowerCase(), "", true, true);
                        if (game == null) {
                            continue;
                        }
                        if (getRecommendations) {
                            System.out.print(line.trim() + ": ");
                            Recommender.ratingSimilarityRecommendFor(data, String.valueOf(game.bggId()), "");
                        }
                        else {
                            System.out.println(line.trim() + ": " + game.bggId());
                        }
                        ++numMatches;
                        matchFound = true;
                    }
                }
            }
            reader.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(numMatches);
    }
    
    public static List<Integer> validGameIds() {
        return Database.validGameIds;
    }
    
    static {
        validGameIds = new ArrayList<>();
    }
}
