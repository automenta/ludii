// 
// Decompiled by Procyon v0.5.36
// 

package manager.network;

import manager.Manager;
import manager.ai.AIDetails;

import java.awt.*;
import java.util.Arrays;

public class SettingsNetwork
{
    private static int networkPlayerNumber;
    private static int numberConnectedPlayers;
    private static int activeGameId;
    private static int tournamentId;
    private static int secretNetworkNumber;
    private static int loginId;
    private static String loginUsername;
    private static boolean rememberDetails;
    private static int tabSelected;
    private static Rectangle networkTabPosition;
    public static AIDetails[] onlineBackupAiPlayers;
    public static int[] playerTimeRemaining;
    public static boolean[] activePlayers;
    public static boolean[] onlinePlayers;
    public static boolean[] drawProposedPlayers;
    
    public static int getNetworkPlayerNumber() {
        return SettingsNetwork.networkPlayerNumber;
    }
    
    public static void setNetworkPlayerNumber(final int networkPlayerNumber) {
        SettingsNetwork.networkPlayerNumber = networkPlayerNumber;
    }
    
    public static int getNumberConnectedPlayers() {
        return SettingsNetwork.numberConnectedPlayers;
    }
    
    public static void setNumberConnectedPlayers(final int numberConnectedPlayers) {
        SettingsNetwork.numberConnectedPlayers = numberConnectedPlayers;
    }
    
    public static int getLoginId() {
        return SettingsNetwork.loginId;
    }
    
    public static void setLoginId(final int loginId) {
        SettingsNetwork.loginId = loginId;
    }
    
    public static String getLoginUsername() {
        return SettingsNetwork.loginUsername;
    }
    
    public static void setLoginUsername(final String loginUsername) {
        SettingsNetwork.loginUsername = loginUsername;
    }
    
    public static boolean isRememberDetails() {
        return SettingsNetwork.rememberDetails;
    }
    
    public static void setRememberDetails(final boolean rememberDetails) {
        SettingsNetwork.rememberDetails = rememberDetails;
    }
    
    public static int getActiveGameId() {
        return SettingsNetwork.activeGameId;
    }
    
    public static void setActiveGameId(final int activeGameId) {
        SettingsNetwork.activeGameId = activeGameId;
    }
    
    public static void resetNetworkPlayers() {
        Arrays.fill(SettingsNetwork.activePlayers, true);
        Arrays.fill(SettingsNetwork.onlinePlayers, false);
        Arrays.fill(SettingsNetwork.drawProposedPlayers, false);
    }
    
    public static void backupAiPlayers() {
        boolean allSame = true;
        if (SettingsNetwork.onlineBackupAiPlayers != null) {
            for (int i = 0; i < SettingsNetwork.onlineBackupAiPlayers.length; ++i) {
                if (!SettingsNetwork.onlineBackupAiPlayers[i].equals(Manager.aiSelected()[i])) {
                    allSame = false;
                    break;
                }
            }
        }
        if ((!allSame || SettingsNetwork.onlineBackupAiPlayers == null) && SettingsNetwork.activeGameId == 0) {
            SettingsNetwork.onlineBackupAiPlayers = new AIDetails[17];
            for (int i = 0; i < Manager.aiSelected().length; ++i) {
                SettingsNetwork.onlineBackupAiPlayers[i] = AIDetails.getCopyOf(Manager.aiSelected()[i], i);
            }
        }
    }
    
    public static void restoreAiPlayers() {
        for (int i = 0; i < SettingsNetwork.onlineBackupAiPlayers.length; ++i) {
            Manager.aiSelected()[i] = AIDetails.getCopyOf(SettingsNetwork.onlineBackupAiPlayers[i], i);
        }
    }
    
    public static int tabSelected() {
        return SettingsNetwork.tabSelected;
    }
    
    public static void setTabSelected(final int tabSelected) {
        SettingsNetwork.tabSelected = tabSelected;
    }
    
    public static Rectangle networkTabPosition() {
        return SettingsNetwork.networkTabPosition;
    }
    
    public static void setNetworkTabPosition(final Rectangle networkTabPosition) {
        SettingsNetwork.networkTabPosition = networkTabPosition;
    }
    
    public static int getTournamentId() {
        return SettingsNetwork.tournamentId;
    }
    
    public static void setTournamentId(final int tournamentId) {
        SettingsNetwork.tournamentId = tournamentId;
    }
    
    public static int getSecretNetworkNumber() {
        return SettingsNetwork.secretNetworkNumber;
    }
    
    public static void setSecretNetworkNumber(final int secretNetworkNumber) {
        SettingsNetwork.secretNetworkNumber = secretNetworkNumber;
    }
    
    static {
        SettingsNetwork.networkPlayerNumber = 0;
        SettingsNetwork.numberConnectedPlayers = 0;
        SettingsNetwork.activeGameId = 0;
        SettingsNetwork.tournamentId = 0;
        SettingsNetwork.secretNetworkNumber = 0;
        SettingsNetwork.loginId = 0;
        SettingsNetwork.loginUsername = "";
        SettingsNetwork.rememberDetails = false;
        SettingsNetwork.tabSelected = 0;
        SettingsNetwork.playerTimeRemaining = new int[16];
        Arrays.fill(SettingsNetwork.activePlayers = new boolean[17], true);
        Arrays.fill(SettingsNetwork.onlinePlayers = new boolean[17], false);
        Arrays.fill(SettingsNetwork.drawProposedPlayers = new boolean[17], false);
    }
}
