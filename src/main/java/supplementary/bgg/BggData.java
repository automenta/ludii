// 
// Decompiled by Procyon v0.5.36
// 

package supplementary.bgg;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BggData
{
    private final List<BggGame> games;
    private final Map<String, List<BggGame>> gamesByName;
    private final Map<Integer, BggGame> gamesByBggId;
    private final Map<String, User> usersByName;
    
    public BggData() {
        this.games = new ArrayList<>();
        this.gamesByName = new HashMap<>();
        this.gamesByBggId = new HashMap<>();
        this.usersByName = new HashMap<>();
    }
    
    public List<BggGame> games() {
        return this.games;
    }
    
    public Map<String, List<BggGame>> gamesByName() {
        return this.gamesByName;
    }
    
    public Map<Integer, BggGame> gamesByBggId() {
        return this.gamesByBggId;
    }
    
    public Map<String, User> usersByName() {
        return this.usersByName;
    }
    
    void loadGames(final String filePath) {
        final long startAt = System.currentTimeMillis();
        this.games.clear();
        this.gamesByName.clear();
        this.gamesByBggId.clear();
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {
            while (true) {
                final String line = reader.readLine();
                if (line == null) {
                    break;
                }
                final String[] subs = line.split("\t");
                final int bggId = Integer.parseInt(subs[0].trim());
                final String name = subs[1].trim();
                final String date = subs[2].trim();
                final BggGame game = new BggGame(this.games.size(), bggId, name, date, subs);
                this.games.add(game);
                List<BggGame> nameList = this.gamesByName.computeIfAbsent(name.toLowerCase(), k -> new ArrayList<>());
                nameList.add(game);
                this.gamesByBggId.put(bggId, game);
                if (!name.equals("scrabble")) {
                    continue;
                }
                System.out.println(game.name() + " has BGG id " + game.bggId() + ".");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        final long stopAt = System.currentTimeMillis();
        final double secs = (stopAt - startAt) / 1000.0;
        System.out.println(this.games.size() + " games loaded in " + secs + "s.");
        System.out.println(this.gamesByName.size() + " entries by name and " + this.gamesByBggId.size() + " by BGG id.");
    }
    
    void loadUserData(final String filePath) {
        final long startAt = System.currentTimeMillis();
        this.usersByName.clear();
        int items = 0;
        int kept = 0;
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {
            int lineIndex = 0;
            while (true) {
                final String line = reader.readLine();
                if (line == null) {
                    break;
                }
                final BggGame game = this.games.get(lineIndex);
                final String[] subs = line.split("\t");
                items += subs.length;
                for (final String sub : subs) {
                    kept += (this.processUserData(sub, game) ? 1 : 0);
                }
                ++lineIndex;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        final long stopAt = System.currentTimeMillis();
        final double secs = (stopAt - startAt) / 1000.0;
        System.out.println(kept + "/" + items + " items processed for " + this.usersByName.size() + " users in " + secs + "s.");
    }
    
    boolean processUserData(final String entry, final BggGame game) {
        boolean kept = false;
        int c;
        for (c = 0; c < entry.length() && entry.charAt(c) != '\''; ++c) {}
        int cc;
        for (cc = c + 1; cc < entry.length() && entry.charAt(cc) != '\''; ++cc) {}
        if (c >= entry.length() || cc >= entry.length()) {
            return false;
        }
        final String name = entry.substring(c + 1, cc);
        User user = null;
        if (this.usersByName.containsKey(name)) {
            user = this.usersByName.get(name);
        }
        else {
            user = new User(name);
            this.usersByName.put(name, user);
        }
        final Rating rating = new Rating(game, user, entry);
        if (rating.score() != 0) {
            kept = true;
            game.add(rating);
            user.add(rating);
        }
        return kept;
    }
    
    void testCamb() {
        final User camb = this.usersByName.get("camb");
        if (camb == null) {
            System.out.println("camb not found.");
        }
        else {
            System.out.println("camb ratings:");
            for (final Rating rating : this.usersByName.get("camb").ratings()) {
                System.out.println(" " + rating.game().name() + "=" + rating.score());
            }
        }
    }
    
    public void findUniqueRatings(final String userName) {
        final User user = this.usersByName.get(userName);
        if (user == null) {
            System.out.println("Couldn't find user '" + userName + "'.");
            return;
        }
        System.out.println(user.name() + " has " + user.ratings().size() + " ratings, and is the only person to have rated:");
        for (final Rating rating : user.ratings()) {
            final BggGame game = rating.game();
            if (game.ratings().size() == 1) {
                System.out.println(game.name() + " (" + game.date() + ")");
            }
        }
    }
    
    public void runDialog() {
        while (true) {
            final Object[] options = { "Similar Games", "Similar Games (rating)", "Similar Games (binary)", "Similar Users", "Suggestions for User", "Similar Games (by user)" };
            final int searchType = JOptionPane.showOptionDialog(null, "Query BGG Data", "Query Type", 1, 3, null, options, -1);
            String message = "";
            if (searchType == 0) {
                final String selection = JOptionPane.showInputDialog("Game Name or Id (Optionally Comma, Year)");
                final String[] subs = selection.split(",");
                final String name = (subs.length < 1) ? "" : subs[0].trim().toLowerCase();
                final String year = (subs.length < 2) ? "" : subs[1].trim().toLowerCase();
                message = Recommender.recommendCBB(this, name, year);
            }
            else if (searchType == 1) {
                final String selection = JOptionPane.showInputDialog("Game Name or Id (Optionally Comma, Year)");
                final String[] subs = selection.split(",");
                final String name = (subs.length < 1) ? "" : subs[0].trim().toLowerCase();
                final String year = (subs.length < 2) ? "" : subs[1].trim().toLowerCase();
                message = Recommender.ratingSimilarityRecommendFor(this, name, year);
            }
            else if (searchType == 2) {
                final String selection = JOptionPane.showInputDialog("Game Name or Id (Optionally Comma, Year)");
                final String[] subs = selection.split(",");
                final String name = (subs.length < 1) ? "" : subs[0].trim().toLowerCase();
                final String year = (subs.length < 2) ? "" : subs[1].trim().toLowerCase();
                message = Recommender.binaryRecommendFor(this, name, year);
            }
            else if (searchType == 3) {
                final String selection = JOptionPane.showInputDialog("BGG User Name");
                message = Recommender.findMatchingUsers(this, selection);
            }
            else if (searchType == 4) {
                final String selection = JOptionPane.showInputDialog("BGG User Name");
                message = Recommender.recommendFor(this, selection, false);
            }
            else if (searchType == 5) {
                final String selection = JOptionPane.showInputDialog("Game Name or Id (Optionally Comma, Year)");
                final String[] subs = selection.split(",");
                final String name = (subs.length < 1) ? "" : subs[0].trim().toLowerCase();
                final String year = (subs.length < 2) ? "" : subs[1].trim().toLowerCase();
                message = Recommender.recommendGameByUser(this, name, year);
            }
            System.out.println(message);
            JOptionPane.showMessageDialog(null, message);
        }
    }
    
    public void run() {
        this.loadGames("../Common/res/recommender/BGG_dataset.csv");
        this.loadUserData("../Common/res/recommender/user_rating.csv");
        this.runDialog();
    }
    
    public static void main(final String[] args) {
        final BggData bgg = new BggData();
        bgg.run();
    }
}
