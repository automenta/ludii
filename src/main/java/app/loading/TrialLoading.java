// 
// Decompiled by Procyon v0.5.36
// 

package app.loading;

import app.DesktopApp;
import app.display.MainWindow;
import app.game.GameRestart;
import game.Game;
import manager.Manager;
import manager.ai.AIUtil;
import manager.referee.Referee;
import manager.utils.ContextSnapshot;
import manager.utils.SettingsManager;
import supplementary.game_logs.MatchRecord;
import util.Context;
import util.Move;
import utils.AIUtils;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class TrialLoading
{
    public static void saveTrial() {
        final int fcReturnVal = DesktopApp.saveGameFileChooser().showSaveDialog(DesktopApp.frame());
        if (fcReturnVal == 0) {
            File file = DesktopApp.saveGameFileChooser().getSelectedFile();
            String filePath = file.getAbsolutePath();
            if (!filePath.endsWith(".trl")) {
                filePath += ".trl";
                file = new File(filePath);
            }
            saveTrial(file);
            if (SettingsManager.saveHeuristics) {
                AIUtils.saveHeuristicScores(Manager.ref().context().trial(), Manager.ref().context(), Manager.currGameStartRngState(), new File(filePath.replaceAll(Pattern.quote(".trl"), "_heuristics.csv")));
            }
        }
    }
    
    public static void saveTrial(final File file) {
        try {
            final Referee ref = Manager.ref();
            List<String> gameOptionStrings = new ArrayList<>();
            if (ref.context().game().description().gameOptions() != null) {
                gameOptionStrings = ref.context().game().description().gameOptions().allOptionStrings(SettingsManager.userSelections.selectedOptionStrings());
            }
            ref.context().trial().saveTrialToTextFile(file, Manager.savedLudName(), gameOptionStrings, Manager.currGameStartRngState(), DesktopApp.trialContainsSandbox());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void loadTrial(final boolean debug) {
        final int fcReturnVal = DesktopApp.loadTrialFileChooser().showOpenDialog(DesktopApp.frame());
        if (fcReturnVal == 0) {
            Manager.ref().interruptAI();
            final File file = DesktopApp.loadTrialFileChooser().getSelectedFile();
            loadTrial(file, debug);
        }
    }
    
    public static void loadTrial(final File file, final boolean debug) {
        try {
            try (final BufferedReader reader = new BufferedReader(new FileReader(file))) {
                final String gamePathLine = reader.readLine();
                final String loadedGamePath = gamePathLine.substring("game=".length());
                final List<String> gameOptions = new ArrayList<>();
                String nextLine = reader.readLine();
                boolean endOptionsFound = false;
                while (nextLine != null) {
                    if (nextLine.startsWith("END GAME OPTIONS")) {
                        endOptionsFound = true;
                    }
                    if (!nextLine.startsWith("START GAME OPTIONS") && !endOptionsFound) {
                        gameOptions.add(nextLine);
                    }
                    if (nextLine.startsWith("SANDBOX=true")) {
                        EventQueue.invokeLater(() -> DesktopApp.setTrialContainsSandbox(true));
                    }
                    nextLine = reader.readLine();
                }
                boolean alreadyLoadedGame = true;
                if (!gamePathLine.substring("/lud/".length()).equals(Manager.savedLudName())) {
                    alreadyLoadedGame = false;
                }
                else {
                    final Game loadedGame = Manager.ref().context().game();
                    final List<String> currentSelections = loadedGame.description().gameOptions().allOptionStrings(SettingsManager.userSelections.selectedOptionStrings());
                    for (final String trialOption : gameOptions) {
                        if (!currentSelections.contains(trialOption)) {
                            alreadyLoadedGame = false;
                            break;
                        }
                    }
                    for (final String currentSelection : currentSelections) {
                        if (!gameOptions.contains(currentSelection)) {
                            alreadyLoadedGame = false;
                            break;
                        }
                    }
                }
                SettingsManager.userSelections.setRuleset(-1);
                SettingsManager.userSelections.setSelectOptionStrings(gameOptions);
                if (!alreadyLoadedGame) {
                    GameLoading.loadGameFromName(loadedGamePath, gameOptions, debug);
                }
            }
            final MatchRecord loadedRecord = MatchRecord.loadMatchRecordFromTextFile(file, Manager.ref().context().game());
            DesktopApp.setSavedTrial(loadedRecord.trial());
            SettingsManager.canSendToDatabase = false;
            final List<Move> tempActions = new ArrayList<>(Manager.savedTrial().moves());
            Manager.setCurrGameStartRngState(loadedRecord.rngState());
            GameRestart.clearBoard();


            EventQueue.invokeLater(() -> {
                MainWindow.tabPanel().page(1).clear();
                MainWindow.tabPanel().page(2).clear();
                MainWindow.tabPanel().page(0).disableCaretUpdates();
                MainWindow.tabPanel().page(1).disableCaretUpdates();
                MainWindow.tabPanel().page(2).disableCaretUpdates();
                List<Move> list = tempActions;
                Context context = Manager.ref().context();
                boolean moveMade = false;
                for (int i = context.trial().moves().size(); i < list.size(); ++i) {
                    Manager.ref().makeSavedMove(list.get(i));
                    moveMade = true;
                    int moveNumber = context.currentInstanceContext().trial().numMoves() - 1;
                    if (context.trial().over() || (context.isAMatch() && moveNumber < context.currentInstanceContext().trial().numInitialPlacementMoves())) {
                        DesktopApp.playerApp().gameOverTasks();
                    }
                }
                MainWindow.tabPanel().page(0).enableCaretUpdates();
                MainWindow.tabPanel().page(1).enableCaretUpdates();
                MainWindow.tabPanel().page(2).enableCaretUpdates();
                if (DesktopApp.trialContainsSandbox()) {
                    System.out.println("Warning! Trial was from Sandbox mode.");
                    MainWindow.setVolatileMessage("Warning! Trial was from Sandbox mode.");
                }
                if (moveMade) {
                    EventQueue.invokeLater(() -> {
                        Manager.app.updateTabs(ContextSnapshot.getContext());
                        Manager.app.repaint();
                    });
                }
            });
            DesktopApp.setSavedTrial(null);
            SettingsManager.canSendToDatabase = false;
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
        AIUtil.calculateAgentPaused();
    }
    
    public static void loadStartTrial() {
        try {
            final File file = new File("." + File.separator + "ludii.trl");
            if (!file.exists()) {
                try {
                    file.createNewFile();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (DesktopApp.shouldLoadTrial()) {
                EventQueue.invokeLater(() -> TrialLoading.loadTrial(file, false));
            }
        }
        catch (Exception e2) {
            final File brokenPreferences = new File("." + File.separator + "ludii.trl");
            brokenPreferences.delete();
        }
    }
}
