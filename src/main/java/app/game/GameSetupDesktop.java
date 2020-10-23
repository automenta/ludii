// 
// Decompiled by Procyon v0.5.36
// 

package app.game;

import app.DesktopApp;
import app.display.MainWindow;
import app.display.dialogs.SettingsDialog;
import app.display.dialogs.remote.RemoteDialog;
import app.display.util.GraphicsCache;
import app.display.util.SpinnerFunctions;
import app.loading.GameLoading;
import app.menu.MainMenu;
import app.sandbox.SandboxUtil;
import game.Game;
import grammar.Description;
import grammar.Report;
import manager.Manager;
import manager.ai.AIDetails;
import manager.ai.AIMenuName;
import manager.ai.AIUtil;
import manager.game.GameSetup;
import manager.network.DatabaseFunctions;
import manager.network.SettingsNetwork;
import manager.utils.AnimationUtil;
import manager.utils.ContextSnapshot;
import manager.utils.SettingsManager;
import org.apache.commons.rng.core.RandomProviderDefaultState;
import org.json.JSONObject;
import util.AI;
import util.SettingsVC;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static language.compiler.Compiler.compile;

public class GameSetupDesktop
{
    public static void compileAndShowGame(final String desc, final boolean menuOption, final boolean debug) {
        try {
            EventQueue.invokeLater(() -> {
                if (DesktopApp.view() != null) {
                    SpinnerFunctions.startSpinner();
                }
            });
        }
        catch (Exception ex) {}
        GraphicsCache.clearAllCachedImages();
        DesktopApp.setLoadSuccessful(false);
        DesktopApp.setMatchDescriptionFull("");
        DesktopApp.setMatchDescriptionShort("");
        final Description gameDescription = new Description(desc);
        final Report report = new Report();
        try {
            Game game = null;
            if (menuOption || DesktopApp.preferencesLoaded()) {
                game = compile(gameDescription, SettingsManager.userSelections, report, debug);
                DesktopApp.setPreferencesLoaded(false);
            }
            else {
                game = compile(gameDescription, SettingsManager.userSelections, report, debug);
            }
            if (game.hasSubgames()) {
                DesktopApp.setMatchDescriptionFull(gameDescription.raw());
                DesktopApp.setMatchDescriptionShort(gameDescription.expanded());
            }
            if (Manager.ref().context() != null) {
                Manager.ref().interruptAI();
            }
            Manager.ref().setGame(game);
            GameSetup.setMVC();
            MainMenu.updateRecentGames(Manager.ref().context().game().name());
            Manager.app.updateFrameTitle();
            cleanUpAfterLoading(game.description().expanded(), game, true);
            DesktopApp.setInstanceTrialsSoFar(new ArrayList<>());
            DesktopApp.setCurrentGameIndexForMatch(0);
            DesktopApp.setLoadSuccessful(true);
            SettingsManager.canSendToDatabase = true;
            GameLoading.loadGameSpecificPreference();
            System.out.println("\nCompiled " + game.name() + " successfully.");
            if (!DesktopApp.savedStatusTabString.isEmpty()) {
                DesktopApp.playerApp().addTextToStatusPanel("\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\n");
            }
            DesktopApp.playerApp().addTextToStatusPanel("Compiled " + game.name() + " successfully.\n");
            if (SettingsNetwork.getActiveGameId() != 0) {
                for (int i = 0; i < Manager.aiSelected.length; ++i) {
                    Manager.aiSelected[i] = new AIDetails(null, i, AIMenuName.Human);
                }
                SettingsDialog.setPlayerPanelEnabled(false);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            DesktopApp.playerApp().reportError(e.getMessage());
        }
        if (report.isWarning()) {
            for (final String warning : report.warnings()) {
                DesktopApp.playerApp().reportError("Warning: " + warning);
            }
        }
        for (int j = 0; j < Manager.aiSelected.length; ++j) {
            final AI ai = Manager.aiSelected[j].ai();
            if (ai != null) {
                ai.closeAI();
            }
        }
        AIUtil.checkIfAgentsAllowed(Manager.ref().context());
        try {
            EventQueue.invokeLater(() -> {
                if (DesktopApp.view() != null) {
                    SpinnerFunctions.stopSpinner();
                    GraphicsCache.clearAllCachedImages();
                    Manager.app.repaint();
                    Manager.app.updateTabs(ContextSnapshot.getContext());
                }
            });
        }
        catch (Exception ex2) {}
    }
    
    public static void cleanUpAfterLoading(final String desc, final Game game, final boolean startGame) {
        if (MainWindow.tabPanel() != null) {
            cleanUpPlayerView(desc, game, startGame);
        }
        SettingsVC.noAnimation = game.metadata().graphics().noAnimation();
        SettingsNetwork.resetNetworkPlayers();
        AnimationUtil.resetAnimationValues();
        DesktopApp.setTrialContainsSandbox(SettingsVC.sandboxMode);
        AIUtil.calculateAgentPaused();
        if (game.metadata().graphics().adversarialPuzzle()) {
            final JSONObject json = new JSONObject().put("AI", new JSONObject().put("algorithm", "Alpha-Beta"));
            AIUtil.updateSelectedAI(json, 2, AIMenuName.AlphaBeta);
        }
        GameRestart.resetGameVariables(startGame);
    }
    
    public static void cleanUpPlayerView(final String desc, final Game game, final boolean startGame) {
        DesktopApp.view().createPanels();
        GameRestart.resetUIVariables();
        Arrays.fill(DesktopApp.view().playerSwatchList, null);
        Arrays.fill(DesktopApp.view().playerNameList, null);
        Arrays.fill(DesktopApp.view().playerSwatchHover, false);
        Arrays.fill(DesktopApp.view().playerNameHover, false);
        MainWindow.getPanels().clear();
        MainWindow.tabPanel().resetTabs();
        final String sandboxError = SandboxUtil.isSandboxAllowed(game);
        if (!sandboxError.isEmpty() && SettingsVC.sandboxMode) {
            SettingsVC.sandboxMode = false;
            MainWindow.setTemporaryMessage(sandboxError);
        }
        if (startGame) {
            Manager.updateCurrentGameRngInternalState();
            game.start(Manager.ref().context());
            MainMenu.updateOptionsMenu(Manager.ref().context(), MainMenu.mainOptionsMenu);
        }
        EventQueue.invokeLater(() -> Manager.app.repaint());
        if (startGame) {
            DesktopApp.setSavedTrial(null);
        }
        DesktopApp.frame().setJMenuBar(new MainMenu());
    }
    
    public static void setupNetworkGame(final String gameName, final List<String> gameOptions, final String inputLine) {
        try {
            if (!inputLine.isEmpty() && Integer.parseInt(inputLine) > 0) {
                final List<String> formattedGameOptions = new ArrayList<>();
                for (String formattedString : gameOptions) {
                    formattedString = formattedString.replaceAll("_", " ");
                    formattedString = formattedString.replaceAll("\\|", "/");
                    formattedGameOptions.add(formattedString);
                }
                if (!formattedGameOptions.get(0).equals("-")) {
                    GameLoading.loadGameFromName(gameName, formattedGameOptions, false);
                }
                else {
                    GameLoading.loadGameFromName(gameName, false);
                }
                Manager.app.addTextToStatusPanel("Joined game as player number " + inputLine + "\n");
                SettingsNetwork.setNetworkPlayerNumber(Integer.parseInt(inputLine));
                Manager.ref().context().game().setMaxTurns(1250);
                final String gameRNG = DatabaseFunctions.getRNG();
                final String[] byteStrings = gameRNG.split(Pattern.quote(","));
                final byte[] bytes = new byte[byteStrings.length];
                for (int j = 0; j < byteStrings.length; ++j) {
                    bytes[j] = Byte.parseByte(byteStrings[j]);
                }
                final RandomProviderDefaultState rngState = new RandomProviderDefaultState(bytes);
                Manager.ref().context().rng().restoreState(rngState);
                Manager.ref().context().game().start(Manager.ref().context());
                Manager.setCurrGameStartRngState(rngState);
                RemoteDialog.bringDialogToFront();
            }
            else {
                Manager.app.addTextToStatusPanel(inputLine);
            }
        }
        catch (Exception E) {
            if (!inputLine.isEmpty()) {
                Manager.app.addTextToStatusPanel(inputLine);
            }
        }
    }
}
