// 
// Decompiled by Procyon v0.5.36
// 

package app;

import app.display.MainWindow;
import app.display.dialogs.PossibleMovesDialog;
import app.display.dialogs.PuzzleDialog;
import app.display.util.DesktopGraphics;
import app.game.GameRestart;
import app.game.GameSetupDesktop;
import app.loading.GameLoading;
import app.utils.Sound;
import bridge.PlatformGraphics;
import collections.FastArrayList;
import game.Game;
import manager.Manager;
import manager.PlayerInterface;
import manager.game.GameSetup;
import manager.network.DatabaseFunctions;
import manager.network.SettingsNetwork;
import org.json.JSONObject;
import tournament.TournamentUtil;
import util.Context;
import util.Move;
import util.Trial;
import utils.AIFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.List;

public abstract class BaseApp implements PlayerInterface
{
    @Override
    public void loadGameFromName(final String name, final List<String> options, final boolean debug) {
        GameLoading.loadGameFromName(name, options, debug);
    }
    
    @Override
    public void loadGameFromName(final String name, final boolean debug) {
        GameLoading.loadGameFromName(name, debug);
    }
    
    @Override
    public JSONObject getNameFromJar() {
        final JFileChooser fileChooser = DesktopApp.jarFileChooser();
        fileChooser.setDialogTitle("Select JAR file containing AI.");
        final int jarReturnVal = fileChooser.showOpenDialog(DesktopApp.frame());
        File jarFile;
        if (jarReturnVal == 0) {
            jarFile = fileChooser.getSelectedFile();
        }
        else {
            jarFile = null;
        }
        if (jarFile == null || !jarFile.exists()) {
            System.err.println("Could not find JAR file.");
            return null;
        }
        final List<Class<?>> classes = AIFactory.loadThirdPartyAIClasses(jarFile);
        if (classes.size() <= 0) {
            System.err.println("Could not find any AI classes.");
            return null;
        }
        final URL logoURL = DesktopApp.playerApp().getClass().getResource("/ludii-logo-64x64.png");
        final ImageIcon icon = new ImageIcon(logoURL);
        final String[] choices = new String[classes.size()];
        for (int i = 0; i < choices.length; ++i) {
            choices[i] = classes.get(i).getName();
        }
        final String choice = (String)JOptionPane.showInputDialog(DesktopApp.frame(), "AI Classes", "Choose an AI class to load", 3, icon, choices, choices[0]);
        if (choice == null) {
            System.err.println("No AI class selected.");
            return null;
        }
        return new JSONObject().put("AI", new JSONObject().put("algorithm", "From JAR").put("JAR File", jarFile.getAbsolutePath()).put("Class Name", choice));
    }
    
    @Override
    public void addTextToStatusPanel(final String text) {
        MainWindow.tabPanel().page(0).addText(text);
        DesktopApp.savedStatusTabString = MainWindow.tabPanel().page(0).text();
    }
    
    @Override
    public void addTextToAnalysisPanel(final String text) {
        MainWindow.tabPanel().page(3).addText(text);
    }
    
    @Override
    public void setTemporaryMessage(final String text) {
        MainWindow.setTemporaryMessage(text);
    }
    
    @Override
    public void setVolatileMessage(final String text) {
        MainWindow.setVolatileMessage(text);
    }
    
    @Override
    public void showPuzzleDialog(final int site) {
        PuzzleDialog.createAndShowGUI(Manager.ref().context(), site);
    }
    
    @Override
    public void showPossibleMovesDialog(final Context context, final FastArrayList<Move> possibleMoves) {
        PossibleMovesDialog.createAndShowGUI(context, possibleMoves);
    }
    
    @Override
    public void selectAnalysisTab() {
        MainWindow.tabPanel().select(3);
    }
    
    @Override
    public void repaint() {
        EventQueue.invokeLater(() -> {
            DesktopApp.view().repaint();
            DesktopApp.view().revalidate();
        });
    }
    
    @Override
    public void reportDrawAgreed() {
        final String message = "All players have agreed to a draw, for Game " + SettingsNetwork.getActiveGameId() + ".\nThe Game is Over.\n";
        if (!MainWindow.tabPanel().page(0).text().contains(message)) {
            this.addTextToStatusPanel(message);
        }
    }
    
    @Override
    public void reportForfeit(final int playerForfeitNumber) {
        final String message = "Player " + playerForfeitNumber + " has resigned Game " + SettingsNetwork.getActiveGameId() + ".\nThe Game is Over.\n";
        if (!MainWindow.tabPanel().page(0).text().contains(message)) {
            this.addTextToStatusPanel(message);
        }
    }
    
    @Override
    public void reportTimeout(final int playerForfeitNumber) {
        final String message = "Player " + playerForfeitNumber + " has timed out for Game " + SettingsNetwork.getActiveGameId() + ".\nThe Game is Over.\n";
        if (!MainWindow.tabPanel().page(0).text().contains(message)) {
            this.addTextToStatusPanel(message);
        }
    }
    
    @Override
    public void updateTabs(final Context context) {
        EventQueue.invokeLater(() -> MainWindow.tabPanel().updateTabs(context));
    }
    
    @Override
    public void playSound(final String soundName) {
        Sound.playSound(soundName);
    }
    
    @Override
    public void gameOverTasks() {
        if (Manager.ref().context().isAMatch()) {
            final List<Trial> completedTrials = Manager.ref().context().completedTrials();
            DesktopApp.instanceTrialsSoFar().add(completedTrials.get(completedTrials.size() - 1));
            if (!Manager.ref().context().trial().over()) {
//                final List list;
                EventQueue.invokeLater(() -> {
                    GameSetup.setMVC();
                    DesktopApp.setCurrentGameIndexForMatch(0);//list.size());
                    GameSetupDesktop.cleanUpAfterLoading("", Manager.ref().context().currentInstanceContext().game(), false);
                    Manager.app.updateFrameTitle();
                });
                return;
            }
        }
        DatabaseFunctions.sendResultToDatabase(Manager.ref().context());
        TournamentUtil.saveTournamentResults(Manager.ref().context());
        this.setTemporaryMessage("Choose Game>Restart to play again.");
    }
    
    @Override
    public void restartGame(final boolean b) {
        GameRestart.restartGame(false);
    }
    
    @Override
    public void cleanUpAfterLoading(final String desc, final Game game, final boolean startGame) {
        GameSetupDesktop.cleanUpAfterLoading(desc, game, startGame);
    }
    
    @Override
    public PlatformGraphics platformGraphics() {
        return new DesktopGraphics();
    }
    
    @Override
    public void repaintTimerForPlayer(final int playerId) {
        if (DesktopApp.view().playerNameList[playerId] != null) {
            DesktopApp.view().repaint(DesktopApp.view().playerNameList[playerId]);
        }
    }
}
