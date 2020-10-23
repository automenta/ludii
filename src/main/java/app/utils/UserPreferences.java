// 
// Decompiled by Procyon v0.5.36
// 

package app.utils;

import app.DesktopApp;
import app.menu.MainMenu;
import main.SettingsGeneral;
import manager.Manager;
import manager.ai.AIDetails;
import manager.ai.AIMenuName;
import manager.network.DatabaseFunctions;
import manager.network.SettingsNetwork;
import manager.utils.PuzzleSelectionType;
import manager.utils.SettingsManager;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import util.SettingsColour;
import util.SettingsVC;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UserPreferences
{
    public static void savePreferences() {
        BufferedWriter bw = null;
        try {
            SettingsNetwork.restoreAiPlayers();
            final File file = new File("." + File.separator + "ludii_preferences.json");
            if (!file.exists()) {
                file.createNewFile();
            }
            final JSONObject json = new JSONObject();
            json.put("SavedLudName", Manager.savedLudName());
            json.put("VersionNumber", "1.0.8.1");
            final List<String> gameOptionStrings = SettingsManager.userSelections.selectedOptionStrings();
            final JSONArray jsonArray = new JSONArray(gameOptionStrings);
            json.put("OptionStrings", jsonArray);
            json.put("SelectedRuleset", SettingsManager.userSelections.ruleset());
            json.put("LoadedFromMemory", DesktopApp.loadedFromMemory());
            if (!DesktopApp.loadedFromMemory()) {
                final Path path = Paths.get(Manager.savedLudName());
                FileTime fileTime = null;
                try {
                    fileTime = Files.getLastModifiedTime(path);
                    json.put("SavedLudLastModifiedTime", fileTime.toString());
                }
                catch (IOException e) {
                    System.err.println("Cannot get the last modified time - " + e);
                }
            }
            json.put("ShowBoard", SettingsManager.showBoard);
            json.put("ShowPieces", SettingsManager.showPieces);
            json.put("ShowGraph", SettingsManager.showGraph);
            json.put("ShowConnections", SettingsManager.showConnections);
            json.put("ShowAxes", SettingsManager.showAxes);
            json.put("ShowCellIndices", SettingsVC.showCellIndices);
            json.put("ShowEdgeIndices", SettingsVC.showEdgeIndices);
            json.put("ShowFaceIndices", SettingsVC.showVertexIndices);
            json.put("ShowVertexCoordinates", SettingsVC.showCellCoordinates);
            json.put("ShowEdgeCoordinates", SettingsVC.showEdgeCoordinates);
            json.put("ShowFaceCoordinates", SettingsVC.showVertexCoordinates);
            json.put("ShowIndices", SettingsVC.showIndices);
            json.put("ShowCoordinates", SettingsVC.showCoordinates);
            json.put("MoveCoord", SettingsGeneral.isMoveCoord());
            json.put("HideAiMoves", SettingsManager.hideAiMoves);
            json.put("ShowAnimation", SettingsManager.showAnimation);
            json.put("devMode", SettingsManager.devMode);
            json.put("CursorTooltip", SettingsDesktop.cursorTooltipDev);
            json.put("tabFontSize", SettingsDesktop.tabFontSize);
            json.put("editorFontSize", SettingsDesktop.editorFontSize);
            json.put("drawBottomCells", SettingsVC.drawBottomCells);
            json.put("drawCornersCells", SettingsVC.drawCornerCells);
            json.put("drawCornerConcaveCells", SettingsVC.drawCornerConcaveCells);
            json.put("drawCornerConvexCells", SettingsVC.drawCornerConvexCells);
            json.put("drawMajorCells", SettingsVC.drawMajorCells);
            json.put("drawMinorCells", SettingsVC.drawMinorCells);
            json.put("drawInnerCells", SettingsVC.drawInnerCells);
            json.put("drawLeftCells", SettingsVC.drawLeftCells);
            json.put("drawOuterCells", SettingsVC.drawOuterCells);
            json.put("drawPerimeterCells", SettingsVC.drawPerimeterCells);
            json.put("drawRightCells", SettingsVC.drawRightCells);
            json.put("drawCenterCells", SettingsVC.drawCenterCells);
            json.put("drawTopCells", SettingsVC.drawTopCells);
            json.put("drawPhasesCells", SettingsVC.drawPhasesCells);
            json.put("drawNeighboursCells", SettingsVC.drawNeighboursCells);
            json.put("drawRadialsCells", SettingsVC.drawRadialsCells);
            json.put("drawDistanceCells", SettingsVC.drawDistanceCells);
            json.put("canSelectLocalState", SettingsManager.canSelectLocalState);
            json.put("canSelectCount", SettingsManager.canSelectCount);
            json.put("canSelectRotation", SettingsManager.canSelectRotation);
            json.put("drawBottomVertices", SettingsVC.drawBottomVertices);
            json.put("drawCornersVertices", SettingsVC.drawCornerVertices);
            json.put("drawCornerConcaveVertices", SettingsVC.drawCornerConcaveVertices);
            json.put("drawCornerConvexVertices", SettingsVC.drawCornerConvexVertices);
            json.put("drawMajorVertices", SettingsVC.drawMajorVertices);
            json.put("drawMinorVertices", SettingsVC.drawMinorVertices);
            json.put("drawInnerVertices", SettingsVC.drawInnerVertices);
            json.put("drawLeftVertices", SettingsVC.drawLeftVertices);
            json.put("drawOuterVertices", SettingsVC.drawOuterVertices);
            json.put("drawPerimeterVertices", SettingsVC.drawPerimeterVertices);
            json.put("drawRightVertices", SettingsVC.drawRightVertices);
            json.put("drawCenterVertices", SettingsVC.drawCenterVertices);
            json.put("drawTopVertices", SettingsVC.drawTopVertices);
            json.put("drawPhasesVertices", SettingsVC.drawPhasesVertices);
            json.put("drawSideNumberVertices", SettingsVC.drawSideVertices.size());
            json.put("drawNeighboursVertices", SettingsVC.drawNeighboursVertices);
            json.put("drawRadialsVertices", SettingsVC.drawRadialsVertices);
            json.put("drawDistanceVertices", SettingsVC.drawDistanceVertices);
            json.put("drawCornerEdges", SettingsVC.drawCornerEdges);
            json.put("drawCornerConcaveEdges", SettingsVC.drawCornerConcaveEdges);
            json.put("drawCornerConvexEdges", SettingsVC.drawCornerConvexEdges);
            json.put("drawMajorEdges", SettingsVC.drawMajorEdges);
            json.put("drawMinorEdges", SettingsVC.drawMinorEdges);
            json.put("drawBottomEdges", SettingsVC.drawBottomEdges);
            json.put("drawInnerEdges", SettingsVC.drawInnerEdges);
            json.put("drawLeftEdges", SettingsVC.drawLeftEdges);
            json.put("drawOuterEdges", SettingsVC.drawOuterEdges);
            json.put("drawPerimeterEdges", SettingsVC.drawPerimeterEdges);
            json.put("drawRightEdges", SettingsVC.drawRightEdges);
            json.put("drawTopEdges", SettingsVC.drawTopEdges);
            json.put("drawCentreEdges", SettingsVC.drawCentreEdges);
            json.put("drawPhasesEdges", SettingsVC.drawPhasesEdges);
            json.put("drawDistanceEdges", SettingsVC.drawDistanceEdges);
            json.put("drawAxialEdges", SettingsVC.drawAxialEdges);
            json.put("drawHorizontalEdges", SettingsVC.drawHorizontalEdges);
            json.put("drawVerticalEdges", SettingsVC.drawVerticalEdges);
            json.put("drawAngledEdges", SettingsVC.drawAngledEdges);
            json.put("drawSlashEdges", SettingsVC.drawSlashEdges);
            json.put("drawSloshEdges", SettingsVC.drawSloshEdges);
            json.put("editorAutocomplete", SettingsManager.editorAutocomplete);
            json.put("moveFormat", SettingsDesktop.moveFormat);
            json.put("TickLength", SettingsManager.tickLength);
            json.put("SwapRule", SettingsManager.swapRule);
            json.put("NoRepetition", SettingsManager.noRepetition);
            json.put("NoRepetitionWithinTurn", SettingsManager.noRepetitionWithinTurn);
            json.put("SaveHeuristics", SettingsManager.saveHeuristics);
            for (int i = 0; i < SettingsVC.trackNames.size(); ++i) {
                json.put(SettingsVC.trackNames.get(i), SettingsVC.trackShown.get(i));
            }
            json.put("ShowPossibleMoves", SettingsVC.showPossibleMoves);
            json.put("ShowLastMove", SettingsManager.showLastMove);
            json.put("ShowEndingMove", SettingsManager.showEndingMove);
            json.put("ShowAIDistribution", SettingsManager.showAIDistribution);
            for (int p = 0; p < MainMenu.recentGames.length; ++p) {
                json.put("RecentGames_" + p, MainMenu.recentGames[p]);
            }
            json.put("FrameWidth", DesktopApp.frame().getWidth());
            json.put("FrameHeight", DesktopApp.frame().getHeight());
            json.put("FrameLocX", DesktopApp.frame().getLocation().x);
            json.put("FrameLocY", DesktopApp.frame().getLocation().y);
            json.put("FrameMaximizedBoth", DesktopApp.frame().getExtendedState() == 6);
            if (SettingsNetwork.isRememberDetails()) {
                json.put("LoginUsername", SettingsNetwork.getLoginUsername());
                json.put("RememberDetails", SettingsNetwork.isRememberDetails());
            }
            for (int p = 0; p < SettingsColour.getCustomPlayerColours().length; ++p) {
                json.put("PlayerColour_" + p, SettingsColour.getCustomPlayerColours()[p].getRGB());
            }
            for (int p = 0; p < DesktopApp.aiSelected().length; ++p) {
                json.put("Names_" + p, DesktopApp.aiSelected()[p].name());
                json.put("MenuNames_" + p, DesktopApp.aiSelected()[p].menuItemName().label);
                if (DesktopApp.aiSelected()[p].ai() != null) {
                    json.put("AI_" + p, DesktopApp.aiSelected()[p].object());
                    json.put("SearchTime_" + p, DesktopApp.aiSelected()[p].thinkTime());
                }
            }
            json.put("PuzzleValueSelection", SettingsManager.puzzleDialogOption.name());
            json.put("IllegalMoves", SettingsManager.illegalMovesValid);
            json.put("CandidateMoves", SettingsVC.showCandidateValues);
            json.put("moveSoundEffect", SettingsManager.moveSoundEffect);
            json.put("abstractPriority", SettingsVC.abstractPriority);
            json.put("coordWithOutline", SettingsVC.coordWithOutline);
            final File selectedJsonFile = DesktopApp.jsonFileChooser().getSelectedFile();
            if (selectedJsonFile != null && selectedJsonFile.exists()) {
                json.put("LastSelectedJsonFile", selectedJsonFile.getCanonicalPath());
            }
            final File selectedJarFile = DesktopApp.jarFileChooser().getSelectedFile();
            if (selectedJarFile != null && selectedJarFile.exists()) {
                json.put("LastSelectedJarFile", selectedJarFile.getCanonicalPath());
            }
            final File selectedGameFile = DesktopApp.gameFileChooser().getSelectedFile();
            if (selectedGameFile != null && selectedGameFile.exists()) {
                json.put("LastSelectedGameFile", selectedGameFile.getCanonicalPath());
            }
            final File selectedSaveGameFile = DesktopApp.saveGameFileChooser().getSelectedFile();
            if (selectedSaveGameFile != null && selectedSaveGameFile.exists()) {
                json.put("LastSelectedSaveGameFile", selectedSaveGameFile.getCanonicalPath());
            }
            final File selectedLoadTrialFile = DesktopApp.loadTrialFileChooser().getSelectedFile();
            if (selectedLoadTrialFile != null && selectedLoadTrialFile.exists()) {
                json.put("LastSelectedLoadTrialFile", selectedLoadTrialFile.getCanonicalPath());
            }
            final File selectedLoadTournamentFile = DesktopApp.loadTournamentFileChooser().getSelectedFile();
            if (selectedLoadTournamentFile != null && selectedLoadTournamentFile.exists()) {
                json.put("LastSelectedLoadTournamentFile", selectedLoadTournamentFile.getCanonicalPath());
            }
            for (final SettingsColour.PlayerColourPreference g : SettingsColour.getSavedPlayerColourPreferences()) {
                final Color[] playerColours = SettingsColour.getGamePreferencePlayerColors(g.gameName());
                final ArrayList<Integer> listPlayer = new ArrayList<>();
                for (final Color c : playerColours) {
                    listPlayer.add(c.getRGB());
                }
                json.put("SPC" + g.gameName(), listPlayer);
            }
            for (final SettingsColour.BoardColourPreference g2 : SettingsColour.getSavedBoardColourPreferences()) {
                final Color[] BoardColour = SettingsColour.getGamePreferenceBoardColors(g2.gameName());
                final ArrayList<Integer> listPlayer = new ArrayList<>();
                for (final Color c : BoardColour) {
                    if (c == null) {
                        listPlayer.add(-1);
                    }
                    else {
                        listPlayer.add(c.getRGB());
                    }
                }
                json.put("SBC" + g2.gameName(), listPlayer);
            }
            for (final String gameName : SettingsManager.turnLimits.keySet()) {
                json.put("MAXTURN" + gameName, SettingsManager.turnLimits.get(gameName));
            }
            for (final String gameName : SettingsVC.pieceFamilies.keySet()) {
                json.put("PIECEFAMILY" + gameName, SettingsVC.pieceFamilies.get(gameName));
            }
            final FileWriter fw = new FileWriter(file);
            bw = new BufferedWriter(fw);
            bw.write(json.toString(4));
        }
        catch (Exception e2) {
            e2.printStackTrace();
            System.out.println("Problem while saving preferences.");
            DatabaseFunctions.logout();
            try {
                if (bw != null) {
                    bw.close();
                }
            }
            catch (Exception ex) {
                System.out.println("Error in closing the BufferedWriter" + ex);
            }
        }
        finally {
            DatabaseFunctions.logout();
            try {
                if (bw != null) {
                    bw.close();
                }
            }
            catch (Exception ex2) {
                System.out.println("Error in closing the BufferedWriter" + ex2);
            }
        }
    }
    
    public static void loadPreferences() {
        DesktopApp.setPreferencesLoaded(false);
        try (final InputStream inputStream = new FileInputStream(new File("." + File.separator + "ludii_preferences.json"))) {
            final JSONObject json = new JSONObject(new JSONTokener(inputStream));
            if (!json.getString("VersionNumber").equals("1.0.8.1")) {
                throw new Exception("Incorrect version number");
            }
            final List<String> listdata = new ArrayList<>();
            final JSONArray jArray = json.getJSONArray("OptionStrings");
            if (jArray != null) {
                for (int i = 0; i < jArray.length(); ++i) {
                    listdata.add(jArray.getString(i));
                }
            }
            SettingsManager.userSelections.setSelectOptionStrings(listdata);
            if (json.has("SelectedRuleset")) {
                SettingsManager.userSelections.setRuleset(json.getInt("SelectedRuleset"));
            }
            DesktopApp.setLoadedFromMemory(json.getBoolean("LoadedFromMemory"));
            Manager.setSavedLudName(json.getString("SavedLudName"));
            if (!DesktopApp.loadedFromMemory()) {
                final String fileModifiedTime = json.getString("SavedLudLastModifiedTime");
                final Path path = Paths.get(Manager.savedLudName());
                FileTime fileTime = null;
                fileTime = Files.getLastModifiedTime(path);
                if (fileModifiedTime.matches(fileTime.toString())) {
                    DesktopApp.setLoadTrial(true);
                }
            }
            else {
                DesktopApp.setLoadTrial(true);
            }
            SettingsVC.showPossibleMoves = json.optBoolean("ShowPossibleMoves");
            SettingsManager.showLastMove = json.optBoolean("ShowLastMove");
            SettingsManager.showEndingMove = json.optBoolean("ShowEndingMove");
            SettingsManager.showAIDistribution = json.optBoolean("ShowAIDistribution");
            SettingsManager.showBoard = json.optBoolean("ShowBoard");
            SettingsManager.showPieces = json.optBoolean("ShowPieces");
            SettingsManager.showGraph = json.optBoolean("ShowGraph");
            SettingsManager.showConnections = json.optBoolean("ShowConnections");
            SettingsManager.showAxes = json.optBoolean("ShowAxes");
            SettingsVC.showCellIndices = json.optBoolean("ShowCellIndices");
            SettingsVC.showEdgeIndices = json.optBoolean("ShowEdgeIndices");
            SettingsVC.showVertexIndices = json.optBoolean("ShowFaceIndices");
            SettingsVC.showCellCoordinates = json.optBoolean("ShowVertexCoordinates");
            SettingsVC.showEdgeCoordinates = json.optBoolean("ShowEdgeCoordinates");
            SettingsVC.showVertexCoordinates = json.optBoolean("ShowFaceCoordinates");
            SettingsVC.showIndices = json.optBoolean("ShowIndices");
            SettingsVC.showCoordinates = json.optBoolean("ShowCoordinates");
            SettingsGeneral.setMoveCoord(json.optBoolean("MoveCoord"));
            SettingsManager.hideAiMoves = json.optBoolean("HideAiMoves");
            SettingsManager.showAnimation = json.optBoolean("ShowAnimation");
            SettingsManager.devMode = json.optBoolean("devMode");
            SettingsDesktop.cursorTooltipDev = json.optBoolean("CursorTooltip");
            SettingsDesktop.tabFontSize = json.optInt("tabFontSize");
            SettingsDesktop.editorFontSize = json.optInt("editorFontSize");
            SettingsVC.drawBottomCells = json.optBoolean("drawBottomCells");
            SettingsVC.drawCornerCells = json.optBoolean("drawCornerCells");
            SettingsVC.drawCornerConcaveCells = json.optBoolean("drawCornerConcaveCells");
            SettingsVC.drawCornerConvexCells = json.optBoolean("drawCornerConvexCells");
            SettingsVC.drawMajorCells = json.optBoolean("drawMajorCells");
            SettingsVC.drawMinorCells = json.optBoolean("drawMinorCells");
            SettingsVC.drawInnerCells = json.optBoolean("drawInnerCells");
            SettingsVC.drawLeftCells = json.optBoolean("drawLeftCells");
            SettingsVC.drawOuterCells = json.optBoolean("drawOuterCells");
            SettingsVC.drawPerimeterCells = json.optBoolean("drawPerimeterCells");
            SettingsVC.drawRightCells = json.optBoolean("drawRightCells");
            SettingsVC.drawTopCells = json.optBoolean("drawTopCells");
            SettingsVC.drawCenterCells = json.optBoolean("drawCenterCells");
            SettingsVC.drawPhasesCells = json.optBoolean("drawPhasesCells");
            SettingsVC.drawNeighboursCells = json.optBoolean("drawNeighboursCells");
            SettingsVC.drawRadialsCells = json.optBoolean("drawRadialsCells");
            SettingsVC.drawDistanceCells = json.optBoolean("drawDistanceCells");
            SettingsManager.canSelectLocalState = json.optBoolean("canSelectLocalState");
            SettingsManager.canSelectCount = json.optBoolean("canSelectCount");
            SettingsManager.canSelectRotation = json.optBoolean("canSelectRotation");
            SettingsVC.drawBottomVertices = json.optBoolean("drawBottomVertices");
            SettingsVC.drawCornerVertices = json.optBoolean("drawCornerVertices");
            SettingsVC.drawCornerConcaveVertices = json.optBoolean("drawCornerConcaveVertices");
            SettingsVC.drawCornerConvexVertices = json.optBoolean("drawCornerConvexVertices");
            SettingsVC.drawMajorVertices = json.optBoolean("drawMajorVertices");
            SettingsVC.drawMinorVertices = json.optBoolean("drawMinorVertices");
            SettingsVC.drawInnerVertices = json.optBoolean("drawInnerVertices");
            SettingsVC.drawLeftVertices = json.optBoolean("drawLeftVertices");
            SettingsVC.drawOuterVertices = json.optBoolean("drawOuterVertices");
            SettingsVC.drawPerimeterVertices = json.optBoolean("drawPerimeterVertices");
            SettingsVC.drawRightVertices = json.optBoolean("drawRightVertices");
            SettingsVC.drawTopVertices = json.optBoolean("drawTopVertices");
            SettingsVC.drawCenterVertices = json.optBoolean("drawCenterVertices");
            SettingsVC.drawPhasesVertices = json.optBoolean("drawPhasesVertices");
            SettingsVC.drawCornerEdges = json.optBoolean("drawCornerEdges");
            SettingsVC.drawCornerConcaveEdges = json.optBoolean("drawCornerConcaveEdges");
            SettingsVC.drawCornerConvexEdges = json.optBoolean("drawCornerConvexEdges");
            SettingsVC.drawMajorEdges = json.optBoolean("drawMajorEdges");
            SettingsVC.drawMinorEdges = json.optBoolean("drawMinorEdges");
            SettingsVC.drawBottomEdges = json.optBoolean("drawBottomEdges");
            SettingsVC.drawInnerEdges = json.optBoolean("drawInnerEdges");
            SettingsVC.drawLeftEdges = json.optBoolean("drawLeftEdges");
            SettingsVC.drawOuterEdges = json.optBoolean("drawOuterEdges");
            SettingsVC.drawPerimeterEdges = json.optBoolean("drawPerimeterEdges");
            SettingsVC.drawRightEdges = json.optBoolean("drawRightEdges");
            SettingsVC.drawTopEdges = json.optBoolean("drawTopEdges");
            SettingsVC.drawCentreEdges = json.optBoolean("drawCentreEdges");
            SettingsVC.drawPhasesEdges = json.optBoolean("drawPhasesEdges");
            SettingsVC.drawDistanceEdges = json.optBoolean("drawDistanceEdges");
            SettingsVC.drawAxialEdges = json.optBoolean("drawAxialEdges");
            SettingsVC.drawHorizontalEdges = json.optBoolean("drawHorizontalEdges");
            SettingsVC.drawVerticalEdges = json.optBoolean("drawVerticalEdges");
            SettingsVC.drawAngledEdges = json.optBoolean("drawAngledEdges");
            SettingsVC.drawSlashEdges = json.optBoolean("drawSlashEdges");
            SettingsVC.drawSloshEdges = json.optBoolean("drawSloshEdges");
            SettingsManager.editorAutocomplete = json.optBoolean("editorAutocomplete");
            SettingsVC.drawNeighboursVertices = json.optBoolean("drawNeighboursVertices");
            SettingsVC.drawRadialsVertices = json.optBoolean("drawRadialsVertices");
            SettingsVC.drawDistanceVertices = json.optBoolean("drawDistanceVertices");
            SettingsDesktop.moveFormat = json.optString("moveFormat");
            SettingsManager.tickLength = json.optDouble("TickLength");
            SettingsManager.swapRule = json.optBoolean("SwapRule");
            SettingsManager.noRepetition = json.optBoolean("NoRepetition");
            SettingsManager.noRepetitionWithinTurn = json.optBoolean("NoRepetitionWithinTurn");
            SettingsManager.saveHeuristics = json.optBoolean("SaveHeuristics");
            for (int p = 0; p < MainMenu.recentGames.length; ++p) {
                if (json.has("RecentGames_" + p)) {
                    MainMenu.recentGames[p] = json.getString("RecentGames_" + p);
                }
            }
            SettingsNetwork.setLoginUsername(json.optString("LoginUsername"));
            SettingsNetwork.setRememberDetails(json.optBoolean("RememberDetails"));
            final int frameWidth = json.getInt("FrameWidth");
            final int frameHeight = json.getInt("FrameHeight");
            final int frameX = json.getInt("FrameLocX");
            final int frameY = json.getInt("FrameLocY");
            final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            final double displayWidth = screenSize.getWidth();
            final double displayHeight = screenSize.getHeight();
            if (frameX + frameWidth <= displayWidth && frameY + frameHeight <= displayHeight) {
                SettingsDesktop.defaultWidth = frameWidth;
                SettingsDesktop.defaultHeight = frameHeight;
                SettingsDesktop.defaultX = frameX;
                SettingsDesktop.defaultY = frameY;
            }
            SettingsDesktop.frameMaximised = json.getBoolean("FrameMaximizedBoth");
            for (int p2 = 0; p2 < DesktopApp.aiSelected().length; ++p2) {
                DesktopApp.aiSelected()[p2].setName(json.getString("Names_" + p2));
                try {
                    final JSONObject jsonAI = json.getJSONObject("AI_" + p2);
                    (DesktopApp.aiSelected()[p2] = new AIDetails(jsonAI, p2, AIMenuName.getAIMenuName(json.getString("MenuNames_" + p2)))).setThinkTime(json.getDouble("SearchTime_" + p2));
                }
                catch (Exception ex) {}
            }
            SettingsManager.puzzleDialogOption = PuzzleSelectionType.getPuzzleSelectionType(json.getString("PuzzleValueSelection"));
            SettingsManager.illegalMovesValid = json.getBoolean("IllegalMoves");
            SettingsVC.showCandidateValues = json.getBoolean("CandidateMoves");
            SettingsManager.moveSoundEffect = json.getBoolean("moveSoundEffect");
            SettingsVC.abstractPriority = json.getBoolean("abstractPriority");
            SettingsVC.coordWithOutline = json.getBoolean("coordWithOutline");
            DesktopApp.setLastSelectedJsonPath(json.optString("LastSelectedJsonFile"));
            DesktopApp.setLastSelectedJarPath(json.optString("LastSelectedJarFile"));
            DesktopApp.setLastSelectedGamePath(json.optString("LastSelectedGameFile"));
            DesktopApp.setLastSelectedSaveGamePath(json.optString("LastSelectedSaveGameFile"));
            DesktopApp.setLastSelectedLoadTrialPath(json.optString("LastSelectedLoadTrialFile"));
            DesktopApp.setLastSelectedLoadTournamentPath(json.optString("LastSelectedLoadTournamentFile"));
            final Iterator<?> keysToCopyIterator = json.keys();
            final List<String> keysList = new ArrayList<>();
            while (keysToCopyIterator.hasNext()) {
                final String key = (String)keysToCopyIterator.next();
                keysList.add(key);
            }
            final String[] keysArray = keysList.toArray(new String[keysList.size()]);
            for (int j = 0; j < keysArray.length; ++j) {
                if (keysArray[j].substring(0, 3).contentEquals("SPC")) {
                    final Color[] playerColours = SettingsColour.originalPlayerColours();
                    final JSONArray listPlayer = json.getJSONArray(keysArray[j]);
                    for (int k = 0; k < listPlayer.length(); ++k) {
                        playerColours[k] = new Color(listPlayer.getInt(k));
                    }
                    SettingsColour.getSavedPlayerColourPreferences().add(new SettingsColour.PlayerColourPreference(keysArray[j].substring(3), playerColours));
                }
                if (keysArray[j].substring(0, 3).contentEquals("SBC")) {
                    final Color[] boardColour = { null, null, null, null, null, null, null };
                    final JSONArray listBoard = json.getJSONArray(keysArray[j]);
                    for (int k = 0; k < listBoard.length(); ++k) {
                        if (listBoard.getInt(k) == -1) {
                            boardColour[k] = null;
                        }
                        else {
                            boardColour[k] = new Color(listBoard.getInt(k));
                        }
                    }
                    SettingsColour.getSavedBoardColourPreferences().add(new SettingsColour.BoardColourPreference(keysArray[j].substring(3), boardColour));
                }
                if (keysArray[j].length() > 7 && keysArray[j].substring(0, 7).contentEquals("MAXTURN")) {
                    SettingsManager.setTurnLimit(keysArray[j].substring(7), json.optInt(keysArray[j]));
                }
                if (keysArray[j].length() > 11 && keysArray[j].substring(0, 11).contentEquals("PIECEFAMILY")) {
                    SettingsVC.setPieceFamily(keysArray[j].substring(11), json.optString(keysArray[j]));
                }
            }
            SettingsNetwork.backupAiPlayers();
            DesktopApp.setPreferencesLoaded(true);
        }
        catch (Exception e) {
            System.out.println("Loading default preferences.");
            final File brokenPreferences = new File("." + File.separator + "ludii_preferences.json");
            brokenPreferences.delete();
        }
    }
}
