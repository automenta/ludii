// 
// Decompiled by Procyon v0.5.36
// 

package supplementary.bgg;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Recommender
{
    public static BggGame findGame(final BggData data, final String gameName, final String date, final boolean pickMostRated, final boolean skipNullGames) {
        BggGame game = null;
        List<BggGame> candidates = data.gamesByName().get(gameName);
        if (candidates == null) {
            try {
                final BggGame gameById = data.gamesByBggId().get(Integer.parseInt(gameName));
                if (gameById != null) {
                    candidates = new ArrayList<>();
                    candidates.add(gameById);
                }
            }
            catch (NumberFormatException ex) {}
        }
        if (candidates == null) {
            if (!skipNullGames) {
                JOptionPane.showMessageDialog(null, "Couldn't find game with name '" + gameName + "'.", "Failed to Find Game", -1, null);
            }
            return null;
        }
        if (candidates.size() == 1) {
            game = candidates.get(0);
        }
        else {
            for (final BggGame gm : candidates) {
                if (gm.date().equalsIgnoreCase(date)) {
                    game = gm;
                    break;
                }
            }
        }
        if (game == null) {
            if (!pickMostRated) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Couldn't choose game among candidates:\n");
                for (final BggGame gm2 : candidates) {
                    sb.append(gm2.name()).append(" (").append(gm2.date()).append(")\n");
                }
                System.out.println(sb.toString());
                JOptionPane.showMessageDialog(null, sb.toString(), "Failed to Find Game", -1, null);
                return null;
            }
            BggGame mostRatedCandidate = null;
            int mostRatings = -1;
            for (final BggGame gm3 : candidates) {
                if (gm3.ratings().size() > mostRatings) {
                    mostRatings = gm3.ratings().size();
                    mostRatedCandidate = gm3;
                }
            }
            game = mostRatedCandidate;
        }
        return game;
    }
    
    public static String recommendCBB(final BggData data, final String gameName, final String date) {
        String message = "";
        final BggGame game = findGame(data, gameName, date, false, false);
        if (game == null) {
            return "";
        }
        message = "\n" + game.name() + " (" + date + ") has " + game.ratings().size() + " ratings.\n";
        final int ratingsThreshold = 30;
        final int matchesThreshold = 5;
        final Map<Integer, Matches> ratingMap = new HashMap<>();
        for (final Rating gameRating : game.ratings()) {
            final User user = gameRating.user();
            final double baseScore = gameRating.score() / 10.0;
            final double userPenalty = 1.0;
            for (final Rating userRating : user.ratings()) {
                final BggGame otherGame = userRating.game();
                final double otherScore = userRating.score() / 10.0;
                Matches matches = ratingMap.get(otherGame.index());
                if (matches == null) {
                    matches = new Matches(otherGame);
                    ratingMap.put(otherGame.index(), matches);
                }
                matches.add(baseScore * otherScore * 1.0);
            }
        }
        final List<Matches> result = new ArrayList<>();
        for (final Matches matches2 : ratingMap.values()) {
            if (matches2.game().ratings().size() < 30 || matches2.scores().size() < 5) {
                matches2.setScore(0.0);
            }
            else {
                matches2.setScore(matches2.score() / Math.sqrt(matches2.game().ratings().size()));
            }
            if (Database.validGameIds().isEmpty() || Database.validGameIds().contains(matches2.game().bggId())) {
                result.add(matches2);
            }
        }
        result.sort((a, b) -> {
            if (a.score() == b.score()) {
                return 0;
            }
            return (a.score() > b.score()) ? -1 : 1;
        });
        for (int n = 0; n < Math.min(50, result.size()); ++n) {
            final Matches matches2 = result.get(n);
            message += String.format("%2d. %s (%s) %.3f / %d\n", n + 1, matches2.game().name(), matches2.game().date(), matches2.score(), matches2.scores().size());
        }
        return message;
    }
    
    public static String recommendGameByUser(final BggData data, final String gameName, final String date) {
        return "Not implement yet.";
    }
    
    public static String recommendFor(final BggData data, final String userName, final boolean includeOwn) {
        String messageString = "";
        final User userA = data.usersByName().get(userName);
        if (userA == null) {
            return "Couldn't find user '" + userName + "'.";
        }
        messageString = messageString + userA.name() + " has " + userA.ratings().size() + " ratings.\n";
        final Map<Integer, Matches> ratingMap = new HashMap<>();
        for (final Rating ratingA : userA.ratings()) {
            final BggGame gameA = ratingA.game();
            for (final Rating ratingB : gameA.ratings()) {
                final User userB = ratingB.user();
                final double scoreB = ratingB.score() / 10.0;
                for (final Rating ratingC : userB.ratings()) {
                    final BggGame gameC = ratingC.game();
                    final double scoreC = ratingC.score() / 10.0;
                    Matches matches = ratingMap.get(gameC.index());
                    if (matches == null) {
                        matches = new Matches(gameC);
                        ratingMap.put(gameC.index(), matches);
                    }
                    matches.add(scoreB * scoreC);
                }
            }
        }
        final List<Matches> result = new ArrayList<>();
        for (final Matches matches2 : ratingMap.values()) {
            if (Database.validGameIds().isEmpty() || Database.validGameIds().contains(matches2.game().bggId())) {
                result.add(matches2);
            }
        }
        result.sort((a, b) -> {
            if (a.score() == b.score()) {
                return 0;
            }
            return (a.score() > b.score()) ? -1 : 1;
        });
        for (int n = 0; n < Math.min(20, result.size()); ++n) {
            final Matches matches2 = result.get(n);
            messageString = messageString + "Match: " + matches2.score() + " (" + matches2.scores().size() + ") " + matches2.game().name() + "\n";
        }
        return messageString;
    }
    
    public static double userMatch(final BggData data, final User userA, final User userB) {
        double tally = 0.0;
        int count = 0;
        final User minUser = (userA.ratings().size() < userB.ratings().size()) ? userA : userB;
        final User maxUser = (userA.ratings().size() < userB.ratings().size()) ? userB : userA;
        for (final Rating ratingMin : minUser.ratings()) {
            final int gameIndexMin = ratingMin.game().index();
            double score = 0.0;
            for (final Rating ratingMax : maxUser.ratings()) {
                if (ratingMax.game().index() == gameIndexMin) {
                    score = 1.0 - Math.abs(ratingMin.score() - ratingMax.score()) / 10.0;
                    ++count;
                    break;
                }
            }
            tally += score;
        }
        if (count == 0) {
            System.out.println("** No shared rating between users.");
            return 0.0;
        }
        return tally / minUser.ratings().size();
    }
    
    public static String findMatchingUsers(final BggData data, final String userName) {
        String messageString = "";
        final User user = data.usersByName().get(userName);
        if (user == null) {
            return "Couldn't find user '" + userName + "'.";
        }
        messageString = messageString + user.name() + " has " + user.ratings().size() + " ratings.\n";
        final Map<String, User> othersMap = new HashMap<>();
        for (final Rating rating : user.ratings()) {
            final BggGame game = rating.game();
            for (final Rating otherRating : game.ratings()) {
                othersMap.put(otherRating.user().name(), otherRating.user());
            }
        }
        final List<User> others = new ArrayList<>(othersMap.values());
        messageString = messageString + others.size() + " users have scored at least one game that " + userName + " has scored.\n";
        for (final User other : others) {
            double tally = 0.0;
            for (final Rating userRating : user.ratings()) {
                double score = 0.0;
                for (final Rating otherRating2 : other.ratings()) {
                    if (userRating.game().index() == otherRating2.game().index()) {
                        score = 1.0 - Math.abs(userRating.score() - otherRating2.score()) / 10.0;
                        break;
                    }
                }
                tally += score;
            }
            tally /= user.ratings().size();
            other.setMatch(tally);
        }
        others.sort((a, b) -> {
            if (a.match() == b.match()) {
                return 0;
            }
            return (a.match() > b.match()) ? -1 : 1;
        });
        for (int n = 0; n < Math.min(100, others.size()); ++n) {
            final User other = others.get(n);
            messageString = messageString + (n + 1) + ". " + other.name() + ", " + other.ratings().size() + " ratings, match=" + other.match() + ".\n";
        }
        return messageString;
    }
    
    public static String binaryRecommendFor(final BggData data, final String gameName, final String date) {
        String messageString = "";
        final BggGame game = findGame(data, gameName, date, false, false);
        if (game == null) {
            return "";
        }
        messageString = "\n" + game.name() + " (" + date + ") has " + game.ratings().size() + " ratings.\n";
        int threshold = 10;
        if (game.ratings().size() > 100) {
            threshold = 20;
        }
        if (game.ratings().size() > 1000) {
            threshold = 30;
        }
        final Map<Integer, Integer> numberOfRecommendsMap = new HashMap<>();
        final Map<Integer, Integer> numberOfMatchesMap = new HashMap<>();
        for (final Rating gameRating : game.ratings()) {
            final User user = gameRating.user();
            final boolean wouldrecommend = gameRating.score() >= 7.0;
            if (wouldrecommend) {
                for (final Rating userRating : user.ratings()) {
                    final BggGame otherGame = userRating.game();
                    final boolean wouldrecommendOther = userRating.score() >= 7.0;
                    final int gameIndex = otherGame.index();
                    int newScore = 1;
                    if (numberOfMatchesMap.containsKey(gameIndex)) {
                        newScore = numberOfMatchesMap.get(gameIndex) + 1;
                    }
                    numberOfMatchesMap.put(gameIndex, newScore);
                    if (wouldrecommendOther) {
                        newScore = 1;
                        if (numberOfRecommendsMap.containsKey(gameIndex)) {
                            newScore = numberOfRecommendsMap.get(gameIndex) + 1;
                        }
                        numberOfRecommendsMap.put(gameIndex, newScore);
                    }
                }
            }
        }
        final List<Matches> result = new ArrayList<>();
        for (final Map.Entry<Integer, Integer> entry : numberOfRecommendsMap.entrySet()) {
            final Integer gameId = entry.getKey();
            if (entry.getValue() > threshold) {
                final Matches match = new Matches(data.games().get(gameId));
                match.setNumberMatches(entry.getValue());
                match.setScore(entry.getValue() / (double)numberOfMatchesMap.get(gameId));
                if (!Database.validGameIds().isEmpty() && !Database.validGameIds().contains(match.game().bggId())) {
                    continue;
                }
                result.add(match);
            }
        }
        result.sort((a, b) -> {
            if (a.score() == b.score()) {
                return 0;
            }
            return (a.score() > b.score()) ? -1 : 1;
        });
        for (int n = 0; n < Math.min(50, result.size()); ++n) {
            final Matches matches = result.get(n);
            messageString = messageString + (n + 1) + ". Match: " + matches.score() + " (" + matches.getNumberMatches() + ") " + matches.game().name() + "\n";
        }
        return messageString;
    }
    
    public static String ratingSimilarityRecommendFor(final BggData data, final String gameName, final String date) {
        String messageString = "";
        final BggGame game = findGame(data, gameName, date, false, false);
        if (game == null) {
            return "";
        }
        messageString = "\n" + game.name() + " (" + date + ") has " + game.ratings().size() + " ratings.\n";
        int threshold = 0;
        if (game.ratings().size() > 50) {
            threshold = 10;
        }
        if (game.ratings().size() > 100) {
            threshold = 20;
        }
        if (game.ratings().size() > 1000) {
            threshold = 30;
        }
        final Map<Integer, Integer> scoreSimilarityMap = new HashMap<>();
        final Map<Integer, Integer> numberOfMatchesMap = new HashMap<>();
        for (final Rating gameRating : game.ratings()) {
            final User user = gameRating.user();
            final int gameScore = gameRating.score();
            for (final Rating userRating : user.ratings()) {
                final BggGame otherGame = userRating.game();
                final int otherGameScore = userRating.score();
                final int gameIndex = otherGame.index();
                final int scoreSimilarity = 10 - Math.abs(gameScore - otherGameScore);
                int newTotal = 1;
                if (numberOfMatchesMap.containsKey(gameIndex)) {
                    newTotal = numberOfMatchesMap.get(gameIndex) + 1;
                }
                numberOfMatchesMap.put(gameIndex, newTotal);
                int newScore = scoreSimilarity;
                if (scoreSimilarityMap.containsKey(gameIndex)) {
                    newScore += scoreSimilarityMap.get(gameIndex);
                }
                scoreSimilarityMap.put(gameIndex, newScore);
            }
        }
        final List<Matches> result = new ArrayList<>();
        for (final Map.Entry<Integer, Integer> entry : numberOfMatchesMap.entrySet()) {
            final Integer gameId = entry.getKey();
            if (entry.getValue() > threshold) {
                final Matches match = new Matches(data.games().get(gameId));
                match.setNumberMatches(entry.getValue());
                match.setScore(scoreSimilarityMap.get(gameId) / (double) entry.getValue());
                if (!Database.validGameIds().isEmpty() && !Database.validGameIds().contains(match.game().bggId())) {
                    continue;
                }
                result.add(match);
            }
        }
        result.sort((a, b) -> {
            if (a.score() == b.score()) {
                return 0;
            }
            return (a.score() > b.score()) ? -1 : 1;
        });
        for (int n = 0; n < Math.min(50, result.size()); ++n) {
            final Matches matches = result.get(n);
            messageString = messageString + (n + 1) + ". Match: " + matches.score() + " (" + matches.getNumberMatches() + ") " + matches.game().name() + "\n";
            System.out.print(matches.game().bggId() + ", ");
        }
        System.out.println();
        return messageString;
    }
}
