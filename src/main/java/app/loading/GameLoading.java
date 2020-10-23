// 
// Decompiled by Procyon v0.5.36
// 

package app.loading;

import app.DesktopApp;
import app.display.dialogs.GameLoaderDialog;
import app.game.GameSetupDesktop;
import main.Constants;
import main.FileHandling;
import main.GameNames;
import manager.Manager;
import manager.utils.SettingsManager;
import util.GameLoader;
import util.SettingsColour;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

public class GameLoading
{
    public static void loadGameFromFile(final boolean debug) {
        final int fcReturnVal = DesktopApp.gameFileChooser().showOpenDialog(DesktopApp.frame());
        if (fcReturnVal == 0) {
            File file = DesktopApp.gameFileChooser().getSelectedFile();
            String filePath = file.getAbsolutePath();
            if (!filePath.endsWith(".lud")) {
                filePath += ".lud";
                file = new File(filePath);
            }
            if (file != null && file.exists()) {
                final String fileName = file.getAbsolutePath();
                SettingsManager.userSelections.setRuleset(-1);
                SettingsManager.userSelections.setSelectOptionStrings(new ArrayList<>());
                loadGameFromFilePath(fileName, debug);
            }
        }
    }
    
    public static void loadGameFromFilePath(final String filePath, final boolean debug) {
        if (filePath != null) {
            Manager.setSavedLudName(filePath);
            String desc = "";
            try {
                DesktopApp.setLoadedFromMemory(false);
                desc = FileHandling.loadTextContentsFromFile(filePath);
                GameSetupDesktop.compileAndShowGame(desc, false, debug);
            }
            catch (FileNotFoundException ex) {
                System.out.println("Unable to open file '" + filePath + "'");
            }
            catch (IOException ex2) {
                System.out.println("Error reading file '" + filePath + "'");
            }
        }
    }
    
    public static void loadGameFromMemory(final boolean debug) {
        final String[] choices = FileHandling.listGames();
        String initialChoice = choices[0];
        for (final String choice : choices) {
            if (Manager.savedLudName() != null && Manager.savedLudName().endsWith(choice.replaceAll(Pattern.quote("\\"), "/"))) {
                initialChoice = choice;
                break;
            }
        }
        final String choice2 = GameLoaderDialog.showDialog(DesktopApp.frame(), choices, initialChoice, true);
        if (choice2 != null) {
            SettingsManager.userSelections.setRuleset(-1);
            SettingsManager.userSelections.setSelectOptionStrings(new ArrayList<>());
            loadGameFromMemory(choice2, debug);
        }
    }
    
    public static void loadGameFromMemory(final String choice, final boolean debug) {
        final StringBuilder sb = new StringBuilder();
        if (choice != null) {
            InputStream in = null;
            String path = choice.replaceAll(Pattern.quote("\\"), "/");
            path = path.substring(path.indexOf("/lud/"));
            Manager.setSavedLudName(path);
            in = GameLoader.class.getResourceAsStream(path);
            try (final BufferedReader rdr = new BufferedReader(new InputStreamReader(in))) {
                String line;
                while ((line = rdr.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            DesktopApp.setLoadedFromMemory(true);
            GameSetupDesktop.compileAndShowGame(sb.toString(), false, debug);
        }
    }
    
    public static void loadGameFromName(final String name, final boolean debug) {
        loadGameFromName(name, new ArrayList<>(), debug);
    }
    
    public static void loadGameFromName(final String name, final List<String> options, final boolean debug) {
        try {
            String inName = name.replaceAll(Pattern.quote("\\"), "/");
            if (inName.startsWith("../Common/res")) {
                inName = inName.substring("../Common/res".length());
            }
            if (!inName.startsWith("/lud/")) {
                inName = "/lud/" + inName;
            }
            InputStream in = GameLoader.class.getResourceAsStream(inName);
            Manager.setSavedLudName(name);
            if (in == null) {
                final String[] allGameNames = FileHandling.listGames();
                int shortestNonMatchLength = Integer.MAX_VALUE;
                String bestMatchFilepath = null;
                String givenName = name.toLowerCase().replaceAll(Pattern.quote("\\"), "/");
                if (givenName.startsWith("/lud/")) {
                    givenName = givenName.substring("/lud/".length());
                }
                else if (givenName.startsWith("lud/")) {
                    givenName = givenName.substring("lud/".length());
                }
                for (final String gameName : allGameNames) {
                    final String str = gameName.toLowerCase().replaceAll(Pattern.quote("\\"), "/");
                    if (str.endsWith("/" + givenName)) {
                        final int nonMatchLength = str.length() - givenName.length();
                        if (nonMatchLength < shortestNonMatchLength) {
                            shortestNonMatchLength = nonMatchLength;
                            bestMatchFilepath = "..\\Common\\res\\" + gameName;
                        }
                    }
                }
                if (bestMatchFilepath == null) {
                    for (final String gameName : allGameNames) {
                        final String str = gameName.toLowerCase().replaceAll(Pattern.quote("\\"), "/");
                        if (str.endsWith(givenName)) {
                            final int nonMatchLength = str.length() - givenName.length();
                            if (nonMatchLength < shortestNonMatchLength) {
                                shortestNonMatchLength = nonMatchLength;
                                bestMatchFilepath = "..\\Common\\res\\" + gameName;
                            }
                        }
                    }
                }
                if (bestMatchFilepath == null) {
                    final String[] givenSplit = givenName.split(Pattern.quote("/"));
                    if (givenSplit.length > 1) {
                        final String givenEnd = givenSplit[givenSplit.length - 1];
                        for (final String gameName2 : allGameNames) {
                            final String str2 = gameName2.toLowerCase().replaceAll(Pattern.quote("\\"), "/");
                            if (str2.endsWith(givenEnd)) {
                                final int nonMatchLength2 = str2.length() - givenName.length();
                                if (nonMatchLength2 < shortestNonMatchLength) {
                                    shortestNonMatchLength = nonMatchLength2;
                                    bestMatchFilepath = "..\\Common\\res\\" + gameName2;
                                }
                            }
                        }
                    }
                }
                String resourceStr = bestMatchFilepath.replaceAll(Pattern.quote("\\"), "/");
                resourceStr = resourceStr.substring(resourceStr.indexOf("/lud/"));
                in = GameLoader.class.getResourceAsStream(resourceStr);
                Manager.setSavedLudName(resourceStr);
            }
            final StringBuilder sb = new StringBuilder();
            try (final BufferedReader rdr = new BufferedReader(new InputStreamReader(in))) {
                String line;
                while ((line = rdr.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            DesktopApp.setLoadedFromMemory(true);
            SettingsManager.userSelections.setRuleset(-1);
            SettingsManager.userSelections.setSelectOptionStrings(options);
            GameSetupDesktop.compileAndShowGame(sb.toString(), true, debug);
        }
        catch (Exception e2) {
            loadGameFromFilePath(name.substring(0, name.length() - 4), debug);
        }
    }
    
    public static String getGameDescriptionRawFromName(final String name) {
        try {
            String inName = name.replaceAll(Pattern.quote("\\"), "/");
            if (inName.startsWith("../Common/res")) {
                inName = inName.substring("../Common/res".length());
            }
            if (!inName.startsWith("/lud/")) {
                inName = "/lud/" + inName;
            }
            InputStream in = GameLoader.class.getResourceAsStream(inName);
            if (in == null) {
                final String[] allGameNames = FileHandling.listGames();
                int shortestNonMatchLength = Integer.MAX_VALUE;
                String bestMatchFilepath = null;
                String givenName = name.toLowerCase().replaceAll(Pattern.quote("\\"), "/");
                if (givenName.startsWith("/lud/")) {
                    givenName = givenName.substring("/lud/".length());
                }
                else if (givenName.startsWith("lud/")) {
                    givenName = givenName.substring("lud/".length());
                }
                for (final String gameName : allGameNames) {
                    final String str = gameName.toLowerCase().replaceAll(Pattern.quote("\\"), "/");
                    if (str.endsWith("/" + givenName)) {
                        final int nonMatchLength = str.length() - givenName.length();
                        if (nonMatchLength < shortestNonMatchLength) {
                            shortestNonMatchLength = nonMatchLength;
                            bestMatchFilepath = "..\\Common\\res\\" + gameName;
                        }
                    }
                }
                if (bestMatchFilepath == null) {
                    for (final String gameName : allGameNames) {
                        final String str = gameName.toLowerCase().replaceAll(Pattern.quote("\\"), "/");
                        if (str.endsWith(givenName)) {
                            final int nonMatchLength = str.length() - givenName.length();
                            if (nonMatchLength < shortestNonMatchLength) {
                                shortestNonMatchLength = nonMatchLength;
                                bestMatchFilepath = "..\\Common\\res\\" + gameName;
                            }
                        }
                    }
                }
                if (bestMatchFilepath == null) {
                    final String[] givenSplit = givenName.split(Pattern.quote("/"));
                    if (givenSplit.length > 1) {
                        final String givenEnd = givenSplit[givenSplit.length - 1];
                        for (final String gameName2 : allGameNames) {
                            final String str2 = gameName2.toLowerCase().replaceAll(Pattern.quote("\\"), "/");
                            if (str2.endsWith(givenEnd)) {
                                final int nonMatchLength2 = str2.length() - givenName.length();
                                if (nonMatchLength2 < shortestNonMatchLength) {
                                    shortestNonMatchLength = nonMatchLength2;
                                    bestMatchFilepath = "..\\Common\\res\\" + gameName2;
                                }
                            }
                        }
                    }
                }
                String resourceStr = bestMatchFilepath.replaceAll(Pattern.quote("\\"), "/");
                resourceStr = resourceStr.substring(resourceStr.indexOf("/lud/"));
                in = GameLoader.class.getResourceAsStream(resourceStr);
            }
            final StringBuilder sb = new StringBuilder();
            try (final BufferedReader rdr = new BufferedReader(new InputStreamReader(in))) {
                String line;
                while ((line = rdr.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return sb.toString();
        }
        catch (Exception e2) {
            System.out.println("Did you change the name??");
            return null;
        }
    }
    
    public static void loadGameSpecificPreference() {
        SettingsColour.setDefaultPlayerColours(SettingsColour.originalPlayerColours());
        SettingsColour.setCustomBoardColour(new Color[] { null, null, null, null, null, null, null, null, null });
        SettingsColour.setCustomBoardColoursFound(false);
        SettingsColour.setCustomPlayerColoursFound(false);
        try {
            SettingsColour.setCustomPlayerColours(SettingsColour.getGamePreferencePlayerColors(Manager.savedLudName()));
        }
        catch (Exception e) {
            SettingsColour.setCustomPlayerColours(SettingsColour.originalPlayerColours());
        }
        try {
            final Color[] c = SettingsColour.getGamePreferenceBoardColors(Manager.savedLudName());
            if (c != null) {
                SettingsColour.setCustomBoardColour(c);
                SettingsColour.setCustomBoardColoursFound(true);
            }
        }
        catch (Exception ex) {}
        Manager.ref().context().game().setMaxTurns(SettingsManager.getTurnLimit(Manager.ref().context().game().name()));
    }
    
    public static void loadFailSafeGame() {
        DesktopApp.setLoadedFromMemory(true);
        GameSetupDesktop.compileAndShowGame(Constants.FAIL_SAFE_GAME_DESCRIPTION, false, false);
    }
    
    public static void loadRandomGame() {
        final List<String> allGameNames = new ArrayList<>();
        EnumSet.allOf(GameNames.class).forEach(game -> allGameNames.add(game.ludName()));
        final Random random = new Random();
        final String chosenGameName = allGameNames.get(random.nextInt(allGameNames.size()));
        SettingsManager.userSelections.setRuleset(-1);
        SettingsManager.userSelections.setSelectOptionStrings(new ArrayList<>());
        loadGameFromName(chosenGameName + ".lud", new ArrayList<>(), false);
    }
}
