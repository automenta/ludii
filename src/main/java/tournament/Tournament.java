// 
// Decompiled by Procyon v0.5.36
// 

package tournament;

import game.Game;
import manager.Manager;
import manager.ai.AIMenuName;
import manager.ai.AIUtil;
import manager.utils.SettingsManager;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tournament
{
    protected final List<String> gamesToPlay;
    protected final List<Object> agentsToPlay;
    protected final List<String[]> results;
    private List<int[]> matchUps;
    private int matchUpIndex;
    private int[] matchUp;
    
    public Tournament(final JSONObject json) {
        final JSONArray listGames = json.getJSONArray("GAMES");
        this.gamesToPlay = new ArrayList<>(listGames.length());
        System.out.println("Tournament games:");
        for (final Object obj : listGames) {
            final String game = (String)obj;
            this.gamesToPlay.add(game);
            System.out.println(game);
        }
        final JSONArray listAgents = json.getJSONArray("AGENTS");
        this.agentsToPlay = new ArrayList<>(listAgents.length());
        System.out.println("Tournament agents:");
        for (final Object obj2 : listAgents) {
            this.agentsToPlay.add(obj2);
            System.out.println(obj2);
        }
        this.results = new ArrayList<>();
    }
    
    public void setupTournament() {
        this.results.clear();
        final int totalNumberPlayers = this.agentsToPlay.size();
        final List<int[]> matchUpsFlipped = generate(totalNumberPlayers, 2);
        this.matchUps = generate(totalNumberPlayers, 2);
        for (int[] up : this.matchUps) {
            for (int i = 0; i < up.length / 2; ++i) {
                final int temp = up[i];
                up[i] = up[up.length - i - 1];
                up[up.length - i - 1] = temp;
            }
        }
        this.matchUps.addAll(matchUpsFlipped);
        this.matchUpIndex = 0;
    }
    
    public void startNextTournamentGame() {
        if (!this.gamesToPlay.isEmpty() && !this.matchUps.isEmpty()) {
            this.matchUp = this.matchUps.get(this.matchUpIndex);
            for (int i = 0; i < this.matchUp.length; ++i) {
                final Object agent = this.agentsToPlay.get(this.matchUp[i]);
                JSONObject json;
                if (agent instanceof JSONObject) {
                    json = (JSONObject)agent;
                }
                else {
                    json = new JSONObject().put("AI", new JSONObject().put("algorithm", agent));
                }
                AIUtil.updateSelectedAI(json, i + 1, AIMenuName.getAIMenuName(json.getJSONObject("AI").getString("algorithm")));
            }
            final List<String> gameAndOptions = Arrays.asList(this.gamesToPlay.get(0).split("-"));
            if (gameAndOptions.size() > 1) {
                System.out.println(gameAndOptions.get(1));
                Manager.app.loadGameFromName(gameAndOptions.get(0).trim(), gameAndOptions.subList(1, gameAndOptions.size()), false);
            }
            else {
                Manager.app.loadGameFromName(gameAndOptions.get(0).trim(), false);
            }
            ++this.matchUpIndex;
            if (this.matchUpIndex >= this.matchUps.size()) {
                this.matchUpIndex = 0;
                this.gamesToPlay.remove(0);
            }
            SettingsManager.agentsPaused = false;
            Manager.ref().nextMove(false);
        }
        else {
            System.out.println("FINAL RESULTS SHORT");
            String finalResultsToSend = "FINAL RESULTS SHORT";
            for (String[] strings : this.results) {
                final String result = Arrays.toString(strings);
                System.out.println(result);
                finalResultsToSend += result;
            }
            System.out.println("\nFINAL RESULTS LONG");
            finalResultsToSend += "FINAL RESULTS LONG";
            for (int j = 0; j < this.results.size(); ++j) {
                final String gameData = "GAME(" + (j + 1) + ") " + this.results.get(j)[0];
                System.out.println(gameData);
                finalResultsToSend += gameData;
                try {
                    for (int k = 0; k < this.results.get(j)[1].length(); ++k) {
                        final String result2 = "Player " + (Integer.parseInt(this.results.get(j)[1].split(",")[k].replace("[", "").replace("]", "").trim()) + 1) + " : " + this.results.get(j)[k + 2];
                        System.out.println(result2);
                        finalResultsToSend += result2;
                    }
                }
                catch (Exception ex) {}
            }
        }
    }
    
    public void storeResults(final Game game, final double[] ranking) {
        final String[] result = new String[10];
        try {
            result[0] = game.name();
            result[1] = Arrays.toString(this.matchUp);
            result[2] = Double.toString(ranking[1]);
            result[3] = Double.toString(ranking[2]);
            result[4] = Double.toString(ranking[3]);
            result[5] = Double.toString(ranking[4]);
            result[6] = Double.toString(ranking[5]);
            result[7] = Double.toString(ranking[6]);
            result[8] = Double.toString(ranking[7]);
            result[9] = Double.toString(ranking[8]);
        }
        catch (Exception ex) {}
        this.results.add(result);
    }
    
    public void endTournament() {
    }
    
    private static List<int[]> generate(final int n, final int r) {
        final List<int[]> combinations = new ArrayList<>();
        final int[] combination = new int[r];
        for (int i = 0; i < r; ++i) {
            combination[i] = i;
        }
        while (combination[r - 1] < n) {
            combinations.add(combination.clone());
            int t;
            for (t = r - 1; t != 0 && combination[t] == n - r + t; --t) {}
            final int[] array = combination;
            final int n2 = t;
            ++array[n2];
            for (int j = t + 1; j < r; ++j) {
                combination[j] = combination[j - 1] + 1;
            }
        }
        return combinations;
    }
}
