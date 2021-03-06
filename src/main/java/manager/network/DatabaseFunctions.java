// 
// Decompiled by Procyon v0.5.36
// 

package manager.network;

import manager.Manager;
import manager.referee.Referee;
import manager.utils.ContextSnapshot;
import manager.utils.SettingsManager;
import org.apache.commons.rng.core.RandomProviderDefaultState;
import util.Context;
import util.Move;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseFunctions
{
    public static Thread repeatNetworkActionsThread;
    public static String appFolderLocation;
    
    public static void repeatNetworkActions() {
        final long timeInterval = 500L;
        final Runnable runnable = () -> {
            while (SettingsNetwork.getLoginId() != 0) {
                try {
                    final URL phpLudii = new URL(DatabaseFunctions.appFolderLocation + "refreshLogin.php?uid=" + SettingsNetwork.getLoginId() + "&secret=" + SettingsNetwork.getSecretNetworkNumber() + "&networkPlayerId=" + SettingsNetwork.getLoginId());
                    final URLConnection yc = phpLudii.openConnection();
                    try (final BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()))) {
                        while (in.readLine() != null) {}
                    }
                }
                catch (Exception E) {
                    System.out.println("Something went wrong with the login refresh");
                }
                if (SettingsNetwork.getActiveGameId() != 0) {
                    try {
                        final String[] activeGamePlayerNames = DatabaseFunctions.getActiveGamePlayerNames();
                        for (int i = 0; i < activeGamePlayerNames.length; ++i) {
                            Manager.aiSelected()[i + 1].setName(activeGamePlayerNames[i]);
                        }
                        DatabaseFunctions.checkOnlinePlayers();
                        DatabaseFunctions.getMoveFromDatabase();
                        DatabaseFunctions.checkRemainingTime();
                        DatabaseFunctions.checkDrawProposed();
                        DatabaseFunctions.checkForfeitAndTimeoutAndDraw();
                    }
                    catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
                DatabaseFunctions.updateIncomingMessages();
                try {
                    Thread.sleep(500L);
                }
                catch (InterruptedException ex) {}
            }
        };
        (DatabaseFunctions.repeatNetworkActionsThread = new Thread(runnable)).start();
    }
    
    public static String md5(final String passwordToHash) {
        String generatedPassword = null;
        try {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(passwordToHash.getBytes());
            final byte[] bytes = md.digest();
            final StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xFF) + 256, 16).substring(1));
            }
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }
    
    public static void checkRemainingTime() {
        try {
            final String[] allPlayersRemainingTime = getRemainingTime().split("_");
            for (int playerId = 0; playerId < SettingsNetwork.playerTimeRemaining.length; ++playerId) {
                SettingsNetwork.playerTimeRemaining[playerId] = Integer.parseInt(allPlayersRemainingTime[playerId]);
                if (SettingsNetwork.playerTimeRemaining[playerId] > 0) {
                    Manager.app.repaintTimerForPlayer(playerId);
                }
            }
        }
        catch (Exception ex) {}
    }
    
    public static String getRemainingTime() {
        String returnString = "";
        try {
            final URL phpLudii = new URL(DatabaseFunctions.appFolderLocation + "getRemainingTime.php?id=" + SettingsNetwork.getActiveGameId());
            final URLConnection yc = phpLudii.openConnection();
            try (final BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    returnString += inputLine;
                }
            }
            return returnString;
        }
        catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    
    public static String GetAllPlayers() {
        String returnString = "";
        try {
            final URL phpLudii = new URL(DatabaseFunctions.appFolderLocation + "getAllPlayers.php");
            final URLConnection yc = phpLudii.openConnection();
            try (final BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    returnString += inputLine;
                }
            }
            return returnString;
        }
        catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    
    public static String findJoinedTournaments() {
        String returnString = "";
        try {
            final URL phpLudii = new URL(DatabaseFunctions.appFolderLocation + "findJoinedTournaments.php?id=" + SettingsNetwork.getLoginId());
            final URLConnection yc = phpLudii.openConnection();
            try (final BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    returnString += inputLine;
                }
            }
            return returnString;
        }
        catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    
    public static void updateIncomingMessages() {
        try {
            final URL phpLudii = new URL(DatabaseFunctions.appFolderLocation + "getDirectMessages.php?id=" + SettingsNetwork.getLoginId() + "&secret=" + SettingsNetwork.getSecretNetworkNumber() + "&networkPlayerId=" + SettingsNetwork.getLoginId());
            final URLConnection yc = phpLudii.openConnection();
            try (final BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    try {
                        if (inputLine.length() <= 0) {
                            continue;
                        }
                        Manager.app.addTextToStatusPanel(inputLine.trim() + "\n");
                    }
                    catch (Exception E) {
                        E.printStackTrace();
                    }
                }
            }
        }
        catch (Exception E2) {
            E2.printStackTrace();
        }
    }
    
    public static String findJoinableGames() {
        String returnString = "";
        try {
            final URL phpLudii = new URL(DatabaseFunctions.appFolderLocation + "findPrivateGames.php?id=" + SettingsNetwork.getLoginId());
            final URLConnection yc = phpLudii.openConnection();
            try (final BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    returnString += inputLine;
                }
            }
            return returnString;
        }
        catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    
    public static String findActiveGames() {
        String returnString = "";
        try {
            final URL phpLudii = new URL(DatabaseFunctions.appFolderLocation + "findActiveGames.php?id=" + SettingsNetwork.getLoginId());
            final URLConnection yc = phpLudii.openConnection();
            try (final BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    returnString += inputLine;
                }
            }
            return returnString;
        }
        catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    
    public static String findOtherGames() {
        String returnString = "";
        try {
            final URL phpLudii = new URL(DatabaseFunctions.appFolderLocation + "findOtherGames.php?id=" + SettingsNetwork.getLoginId());
            final URLConnection yc = phpLudii.openConnection();
            try (final BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    returnString += inputLine;
                }
            }
            return returnString;
        }
        catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    
    public static String findJoinableTournaments() {
        String returnString = "";
        try {
            final URL phpLudii = new URL(DatabaseFunctions.appFolderLocation + "findPrivateTournaments.php?id=" + SettingsNetwork.getLoginId());
            final URLConnection yc = phpLudii.openConnection();
            try (final BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    returnString += inputLine;
                }
            }
            return returnString;
        }
        catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    
    public static String findHostedTournaments() {
        String returnString = "";
        try {
            final URL phpLudii = new URL(DatabaseFunctions.appFolderLocation + "findHostedTournaments.php?id=" + SettingsNetwork.getLoginId());
            final URLConnection yc = phpLudii.openConnection();
            try (final BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    returnString += inputLine;
                }
            }
            return returnString;
        }
        catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    
    public static void sendGameChatMessage(final String s) {
        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        final String stringToSend = s + " [" + timestamp + "]";
        try {
            final URL phpLudii = new URL(DatabaseFunctions.appFolderLocation + "sendGroupChat.php?message=" + stringToSend.replace(" ", "%20") + "&id=" + SettingsNetwork.getActiveGameId() + "&name=" + SettingsNetwork.getLoginUsername().replace(" ", "%20") + "&secret=" + SettingsNetwork.getSecretNetworkNumber() + "&networkPlayerId=" + SettingsNetwork.getLoginId());
            final URLConnection yc = phpLudii.openConnection();
            try (final BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    try {
                        System.out.println(inputLine);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
    }
    
    public static void sendMoveToDatabase(final Move m, final int nextMover, final String score) {
        if (m.mover() != SettingsNetwork.getNetworkPlayerNumber()) {
            System.out.println("you weren't supposed to be making this move!");
            return;
        }
        final String moveToStore = m.toTrialFormat(null);
        try {
            final Referee ref = Manager.ref();
            final Context context = ref.context();
            final int lastMoverNumberInDB = context.state().playerToAgent(m.mover());
            final int nextMoverNumberInDB = context.state().playerToAgent(nextMover);
            List<String> gameOptionStrings = new ArrayList<>();
            if (ref.context().game().description().gameOptions() != null) {
                gameOptionStrings = ref.context().game().description().gameOptions().allOptionStrings(SettingsManager.userSelections.selectedOptionStrings());
            }
            String trialString = context.trial().convertTrialToString(Manager.savedLudName(), gameOptionStrings, Manager.currGameStartRngState());
            trialString = URLEncoder.encode(trialString, StandardCharsets.UTF_8);
            final URL phpLudii = new URL(DatabaseFunctions.appFolderLocation + "sendMove.php?move=" + moveToStore + "&id=" + SettingsNetwork.getActiveGameId() + "&lastMover=" + lastMoverNumberInDB + "&nextMover=" + nextMoverNumberInDB + "&score=" + score + "&trial=" + trialString + "&secret=" + SettingsNetwork.getSecretNetworkNumber() + "&networkPlayerId=" + SettingsNetwork.getLoginId());
            final URLConnection yc = phpLudii.openConnection();
            try (final BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()))) {
                while (in.readLine() != null) {
                    System.out.println(in.readLine());
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void sendGameOverDatabase() {
        try {
            final URL phpLudii = new URL(DatabaseFunctions.appFolderLocation + "sendGameOver.php?id=" + SettingsNetwork.getActiveGameId() + "&secret=" + SettingsNetwork.getSecretNetworkNumber() + "&networkPlayerId=" + SettingsNetwork.getLoginId());
            final URLConnection yc = phpLudii.openConnection();
            try (final BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()))) {
                while (in.readLine() != null) {}
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void sendForfeitToDatabase() {
        try {
            final URL phpLudii = new URL(DatabaseFunctions.appFolderLocation + "forfeitGame.php?id=" + SettingsNetwork.getActiveGameId() + "&mover=" + SettingsNetwork.getNetworkPlayerNumber() + "&secret=" + SettingsNetwork.getSecretNetworkNumber() + "&networkPlayerId=" + SettingsNetwork.getLoginId());
            final URLConnection yc = phpLudii.openConnection();
            try (final BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()))) {
                while (in.readLine() != null) {
                    Manager.app.addTextToStatusPanel(in.readLine() + "\n");
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void sendProposeDraw() {
        try {
            final URL phpLudii = new URL(DatabaseFunctions.appFolderLocation + "proposeDraw.php?id=" + SettingsNetwork.getActiveGameId() + "&mover=" + SettingsNetwork.getNetworkPlayerNumber() + "&secret=" + SettingsNetwork.getSecretNetworkNumber() + "&networkPlayerId=" + SettingsNetwork.getLoginId());
            final URLConnection yc = phpLudii.openConnection();
            try (final BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()))) {
                while (in.readLine() != null) {}
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void checkOnlinePlayers() {
        final String[] allPlayers = GetAllPlayers().split("_next_");
        for (int playerId = 0; playerId < SettingsNetwork.onlinePlayers.length; ++playerId) {
            SettingsNetwork.onlinePlayers[playerId] = false;
            for (String allPlayer : allPlayers) {
                if (Manager.aiSelected()[ContextSnapshot.getContext().state().playerToAgent(playerId)].name().equals(allPlayer.split("NEXT_COL")[1]) && allPlayer.split("NEXT_COL")[2].equals("yes")) {
                    SettingsNetwork.onlinePlayers[playerId] = true;
                }
            }
        }
    }
    
    public static void getMoveFromDatabase() {
        try {
            final URL phpLudii = new URL(DatabaseFunctions.appFolderLocation + "getMove.php?id=" + SettingsNetwork.getActiveGameId());
            final URLConnection yc = phpLudii.openConnection();
            try (final BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    try {
                        final String[] allMoves = inputLine.split("END_MOVE");
                        int numberMovesMade = Manager.ref().context().trial().numSubmovesPlayed();
                        if (Manager.ref().context().model().movesPerPlayer() != null) {
                            for (final Move m : Manager.ref().context().model().movesPerPlayer()) {
                                if (m != null) {
                                    ++numberMovesMade;
                                }
                            }
                        }
                        for (int i = numberMovesMade; i < allMoves.length; ++i) {
                            if (!allMoves[i].isEmpty()) {
                                final Move j = new Move(allMoves[i]);
                                Manager.ref().applyNetworkMoveToGame(j);
                            }
                        }
                    }
                    catch (Exception e) {
                        System.out.println("could not load move properly");
                        e.printStackTrace();
                    }
                }
            }
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
    }
    
    public static void checkForfeitAndTimeoutAndDraw() {
        try {
            final URL phpLudii = new URL(DatabaseFunctions.appFolderLocation + "checkForfeitAndTimeout.php?id=" + SettingsNetwork.getActiveGameId());
            final URLConnection yc = phpLudii.openConnection();
            try (final BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    try {
                        if (inputLine.contains("DRAWAGREED")) {
                            Manager.app.reportDrawAgreed();
                        }
                        if (inputLine.contains("FORFEIT")) {
                            final int playerForfeitNumber = Integer.parseInt(inputLine.substring(8));
                            Manager.app.reportForfeit(playerForfeitNumber);
                        }
                        if (!inputLine.contains("TIMEOUT")) {
                            continue;
                        }
                        final int playerForfeitNumber = Integer.parseInt(inputLine.substring(8));
                        Manager.app.reportTimeout(playerForfeitNumber);
                    }
                    catch (Exception ex) {}
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void checkDrawProposed() {
        try {
            final URL phpLudii = new URL(DatabaseFunctions.appFolderLocation + "checkDrawProposed.php?id=" + SettingsNetwork.getActiveGameId());
            final URLConnection yc = phpLudii.openConnection();
            try (final BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()))) {
                boolean foundDrawList = false;
                final String inputLine;
                if ((inputLine = in.readLine()) != null) {
                    try {
                        final String[] drawProposedArray = inputLine.split("_");
                        for (int i = 0; i < drawProposedArray.length; ++i) {
                            if (drawProposedArray[i].equals("1")) {
                                if (!SettingsNetwork.drawProposedPlayers[i]) {
                                    Manager.app.addTextToStatusPanel("Player " + (i + 1) + " has proposed a draw.\n");
                                }
                                SettingsNetwork.drawProposedPlayers[i] = true;
                            }
                            else {
                                SettingsNetwork.drawProposedPlayers[i] = false;
                            }
                            foundDrawList = true;
                        }
                    }
                    catch (Exception ex) {}
                }
                if (!foundDrawList) {
                    Arrays.fill(SettingsNetwork.drawProposedPlayers, false);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static String[] getActiveGamePlayerNames() {
        try {
            final URL phpLudii = new URL(DatabaseFunctions.appFolderLocation + "getActiveGamePlayerNames.php?id=" + SettingsNetwork.getActiveGameId());
            final URLConnection yc = phpLudii.openConnection();
            final String[] playerNamesFinal = new String[16];
            Arrays.fill(playerNamesFinal, "");
            String[] playerNames = new String[0];
            try (final BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()))) {
                final String inputLine;
                if ((inputLine = in.readLine()) != null) {
                    try {
                        playerNames = inputLine.split("_NEXT_");
                    }
                    catch (Exception E) {
                        E.printStackTrace();
                    }
                }
            }
            System.arraycopy(playerNames, 0, playerNamesFinal, 0, playerNames.length);
            return playerNamesFinal;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static void setMaxPlayerNumber() {
        try {
            final URL phpLudii = new URL(DatabaseFunctions.appFolderLocation + "setMaxPlayerNumber.php?max=" + ContextSnapshot.getContext().game().players().count() + "&id=" + SettingsNetwork.getActiveGameId() + "&secret=" + SettingsNetwork.getSecretNetworkNumber() + "&networkPlayerId=" + SettingsNetwork.getLoginId());
            final URLConnection yc = phpLudii.openConnection();
            try (final BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println(inputLine);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void sendGameRankings(final double[] rankingOriginal) {
        final double[] ranking = Arrays.copyOf(rankingOriginal, rankingOriginal.length);
        for (int i = 1; i < rankingOriginal.length; ++i) {
            ranking[Manager.ref().context().state().playerToAgent(i)] = rankingOriginal[i];
        }
        final double[] normalisedRanking = ranking;
        String normalisedRankingString = "";
        final int numRankings = normalisedRanking.length - 1;
        for (int j = 1; j < numRankings + 1; ++j) {
            normalisedRanking[j] = 1.0 - (normalisedRanking[j] - 1.0) / (numRankings - 1);
            normalisedRankingString = normalisedRankingString + normalisedRanking[j] + "_";
        }
        double highest = -999.0;
        double lowest = 999.0;
        for (int k = 1; k < numRankings + 1; ++k) {
            final double score = normalisedRanking[k];
            if (score < lowest) {
                lowest = score;
            }
            if (score > highest) {
                highest = score;
            }
        }
        if (lowest < 0.0) {
            lowest = 0.0;
        }
        try {
            final URL phpLudii = new URL(DatabaseFunctions.appFolderLocation + "recordRanking.php?ranking=" + normalisedRankingString + "&id=" + SettingsNetwork.getActiveGameId() + "&highest=" + highest + "&lowest=" + lowest + "&secret=" + SettingsNetwork.getSecretNetworkNumber() + "&networkPlayerId=" + SettingsNetwork.getLoginId());
            final URLConnection yc = phpLudii.openConnection();
            try (final BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println(inputLine);
                }
            }
        }
        catch (Exception E) {
            E.printStackTrace();
        }
    }
    
    public static void setRNG(final RandomProviderDefaultState rngState) {
        final String textRNG = convertRNGToText(rngState);
        try {
            final URL phpLudii = new URL(DatabaseFunctions.appFolderLocation + "setRNG.php?rng=" + textRNG + "&id=" + SettingsNetwork.getActiveGameId() + "&secret=" + SettingsNetwork.getSecretNetworkNumber() + "&networkPlayerId=" + SettingsNetwork.getLoginId());
            final URLConnection yc = phpLudii.openConnection();
            try (final BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println(inputLine);
                }
            }
        }
        catch (Exception E) {
            E.printStackTrace();
        }
    }
    
    public static String convertRNGToText(final RandomProviderDefaultState rngState) {
        String textRNG = "";
        final byte[] bytes = rngState.getState();
        for (int i = 0; i < bytes.length; ++i) {
            textRNG += bytes[i];
            if (i < bytes.length - 1) {
                textRNG += ",";
            }
        }
        return textRNG;
    }
    
    public static String getRNG() {
        String rng = "";
        try {
            final URL phpLudii = new URL(DatabaseFunctions.appFolderLocation + "getRNG.php?id=" + SettingsNetwork.getActiveGameId());
            final URLConnection yc = phpLudii.openConnection();
            try (final BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    rng += inputLine;
                }
            }
        }
        catch (Exception E) {
            E.printStackTrace();
        }
        return rng;
    }
    
    public static String getLeaderboard() {
        String returnString = "";
        try {
            final URL phpLudii = new URL(DatabaseFunctions.appFolderLocation + "getLeaderboard.php");
            final URLConnection yc = phpLudii.openConnection();
            try (final BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    returnString += inputLine;
                }
            }
            return returnString;
        }
        catch (Exception E) {
            E.printStackTrace();
            return "";
        }
    }
    
    public static boolean pingServer(final String URLName) {
        try {
            HttpURLConnection.setFollowRedirects(false);
            final HttpURLConnection con = (HttpURLConnection)new URL(URLName).openConnection();
            con.setConnectTimeout(100);
            con.setReadTimeout(100);
            con.setRequestMethod("HEAD");
            return con.getResponseCode() == 200;
        }
        catch (Exception e) {
            return false;
        }
    }
    
    public static void sendResultToDatabase(final Context context) {
        if (SettingsNetwork.getActiveGameId() != 0) {
            sendGameRankings(context.trial().ranking());
            sendGameOverDatabase();
        }
    }
    
    public static void logout() {
        try {
            final String logoutURL = DatabaseFunctions.appFolderLocation + "appLogout.php?uid=" + SettingsNetwork.getLoginId() + "&secret=" + SettingsNetwork.getSecretNetworkNumber() + "&networkPlayerId=" + SettingsNetwork.getLoginId();
            if (pingServer(logoutURL)) {
                final URL phpLudii = new URL(logoutURL);
                final URLConnection yc = phpLudii.openConnection();
                try (final BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()))) {
                    while (in.readLine() != null) {}
                }
            }
        }
        catch (Exception e) {
            System.out.println("Could not connect to server");
        }
    }
    
    static {
        DatabaseFunctions.appFolderLocation = "https://www.ludii.games/appVersions/1.0.8/";
    }
}
