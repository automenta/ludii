// 
// Decompiled by Procyon v0.5.36
// 

package util;

import java.awt.*;
import java.util.ArrayList;

public class SettingsColour
{
    private static Color[] customBoardColour;
    private static boolean customBoardColoursFound;
    private static boolean customPlayerColoursFound;
    private static Color[] defaultPlayerColours;
    private static Color[] defaultBoardColour;
    private static Color[] customPlayerColours;
    private static ArrayList<PlayerColourPreference> savedPlayerColourPreferences;
    private static ArrayList<BoardColourPreference> savedBoardColourPreferences;
    
    public static Color[] getGamePreferencePlayerColors(final String gameName) {
        for (final PlayerColourPreference g : getSavedPlayerColourPreferences()) {
            if (g.gameName().equals(gameName)) {
                setCustomPlayerColoursFound(true);
                return g.playerColours();
            }
        }
        return originalPlayerColours();
    }
    
    public static Color[] getGamePreferenceBoardColors(final String gameName) {
        for (final BoardColourPreference g : getSavedBoardColourPreferences()) {
            if (g.gameName().equals(gameName)) {
                return g.boardColour();
            }
        }
        return null;
    }
    
    public static Color[] originalPlayerColours() {
        final Color[] originalPlayerColours = { new Color(250, 250, 250), new Color(250, 250, 250), new Color(50, 50, 50), new Color(190, 0, 0), new Color(0, 190, 0), new Color(0, 0, 190), new Color(190, 0, 190), new Color(0, 190, 190), new Color(255, 153, 0), new Color(255, 255, 153), new Color(153, 204, 255), new Color(255, 153, 204), new Color(204, 153, 255), new Color(255, 204, 153), new Color(153, 204, 0), new Color(255, 204, 0), new Color(255, 102, 0), new Color(250, 250, 250) };
        return originalPlayerColours;
    }
    
    public static Color playerColour(final int playerId, final Context context) {
        if (playerId > context.game().players().count()) {
            return SettingsColour.customPlayerColours[17];
        }
        return SettingsColour.customPlayerColours[playerId];
    }
    
    public static Color[] getCustomBoardColour() {
        return SettingsColour.customBoardColour;
    }
    
    public static void setCustomBoardColour(final Color[] customBoardColour) {
        SettingsColour.customBoardColour = customBoardColour;
    }
    
    public static boolean isCustomBoardColoursFound() {
        return SettingsColour.customBoardColoursFound;
    }
    
    public static void setCustomBoardColoursFound(final boolean customBoardColoursFound) {
        SettingsColour.customBoardColoursFound = customBoardColoursFound;
    }
    
    public static boolean isCustomPlayerColoursFound() {
        return SettingsColour.customPlayerColoursFound;
    }
    
    public static void setCustomPlayerColoursFound(final boolean customPlayerColoursFound) {
        SettingsColour.customPlayerColoursFound = customPlayerColoursFound;
    }
    
    public static Color[] getDefaultPlayerColours() {
        return SettingsColour.defaultPlayerColours;
    }
    
    public static void setDefaultPlayerColours(final Color[] defaultPlayerColours) {
        SettingsColour.defaultPlayerColours = defaultPlayerColours;
    }
    
    public static Color[] getDefaultBoardColour() {
        return SettingsColour.defaultBoardColour;
    }
    
    public static void setDefaultBoardColour(final Color[] defaultBoardColour) {
        SettingsColour.defaultBoardColour = defaultBoardColour;
    }
    
    public static Color[] getCustomPlayerColours() {
        return SettingsColour.customPlayerColours;
    }
    
    public static void setCustomPlayerColours(final Color[] customPlayerColours) {
        SettingsColour.customPlayerColours = customPlayerColours;
    }
    
    public static ArrayList<PlayerColourPreference> getSavedPlayerColourPreferences() {
        return SettingsColour.savedPlayerColourPreferences;
    }
    
    public static void setSavedPlayerColourPreferences(final ArrayList<PlayerColourPreference> savedPlayerColourPreferences) {
        SettingsColour.savedPlayerColourPreferences = savedPlayerColourPreferences;
    }
    
    public static ArrayList<BoardColourPreference> getSavedBoardColourPreferences() {
        return SettingsColour.savedBoardColourPreferences;
    }
    
    public static void setSavedBoardColourPreferences(final ArrayList<BoardColourPreference> savedBoardColourPreferences) {
        SettingsColour.savedBoardColourPreferences = savedBoardColourPreferences;
    }
    
    static {
        SettingsColour.customBoardColour = new Color[] { null, null, null, null, null, null, null, null, null };
        SettingsColour.customBoardColoursFound = false;
        SettingsColour.customPlayerColoursFound = false;
        SettingsColour.defaultPlayerColours = originalPlayerColours();
        SettingsColour.defaultBoardColour = new Color[] { null, null, null, null, null, null, null, null, null };
        SettingsColour.customPlayerColours = originalPlayerColours();
        SettingsColour.savedPlayerColourPreferences = new ArrayList<>();
        SettingsColour.savedBoardColourPreferences = new ArrayList<>();
    }
    
    public static class BoardColourPreference
    {
        private final String gameName;
        private Color[] boardColour;
        
        public BoardColourPreference(final String gameName, final Color[] boardColour) {
            this.boardColour = new Color[] { null, null, null, null, null, null, null, null, null };
            this.gameName = gameName;
            this.setBoardColour(boardColour);
        }
        
        public String gameName() {
            return this.gameName;
        }
        
        public Color[] boardColour() {
            return this.boardColour;
        }
        
        public void setBoardColour(final Color[] boardColour) {
            this.boardColour = boardColour;
        }
    }
    
    public static class PlayerColourPreference
    {
        private final String gameName;
        private Color[] playerColours;
        
        public PlayerColourPreference(final String gameName, final Color[] playerColours) {
            this.playerColours = SettingsColour.originalPlayerColours();
            this.gameName = gameName;
            this.playerColours = playerColours;
        }
        
        public String gameName() {
            return this.gameName;
        }
        
        public Color[] playerColours() {
            return this.playerColours;
        }
        
        public void setPlayerColours(final Color[] playerColours) {
            this.playerColours = playerColours;
        }
    }
}
