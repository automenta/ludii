// 
// Decompiled by Procyon v0.5.36
// 

package app.menu;

import app.DesktopApp;
import app.display.MainWindow;
import app.display.dialogs.*;
import app.display.dialogs.editor.EditorDialog;
import app.display.dialogs.remote.RemoteDialog;
import app.display.util.GUIUtil;
import app.display.util.Thumbnails;
import app.display.views.tools.ToolView;
import app.game.GameRestart;
import app.game.GameSetupDesktop;
import app.loading.GameLoading;
import app.loading.MiscLoading;
import app.loading.TrialLoading;
import app.utils.SettingsDesktop;
import game.Game;
import game.functions.booleans.BooleanConstant;
import game.rules.end.End;
import game.rules.end.If;
import game.rules.end.Result;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.types.play.RepetitionType;
import game.types.play.ResultType;
import game.types.play.RoleType;
import game.types.state.GameType;
import graphics.svg.SVGLoader;
import language.grammar.Grammar;
import language.parser.Parser;
import main.Constants;
import main.FileHandling;
import main.StringRoutines;
import grammar.Description;
import grammar.Report;
import options.GameOptions;
import options.Option;
import options.Ruleset;
import manager.Manager;
import manager.ai.AIDetails;
import manager.ai.AIUtil;
import manager.network.DatabaseFunctions;
import manager.network.LocalFunctions;
import manager.network.SettingsNetwork;
import manager.utils.ContextSnapshot;
import manager.utils.PuzzleSelectionType;
import manager.utils.SettingsManager;
import metadata.ai.heuristics.Heuristics;
import search.pns.ProofNumberSearch;
import supplementary.EvalUtil;
import supplementary.experiments.EvalAIsThread;
import supplementary.experiments.ludemes.CountLudemes;
import util.Context;
import util.GameLoader;
import util.Move;
import util.SettingsVC;
import util.action.Action;
import util.action.move.ActionRemove;
import util.action.state.ActionSetNextPlayer;
import util.locations.FullLocation;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.List;
import java.util.Timer;
import java.util.*;
import java.util.regex.Pattern;

import static language.compiler.Compiler.compileObject;

public class MainMenuFunctions extends JMenuBar
{
    private static Thread timeRandomPlayoutsThread;
    
    public static void checkActionsPerformed(final ActionEvent e) {
        SettingsVC.selectedLocation = new FullLocation(-1);
        final JMenuItem source = (JMenuItem)e.getSource();
        final Context context = Manager.ref().context();
        final Game game = context.game();
        if (source.getText().equals("About")) {
            AboutDialog.showAboutDialog();
        }
        else if (source.getText().equals("Count Ludemes")) {
            final CountLudemes ludemeCounter = new CountLudemes();
            DesktopApp.playerApp().addTextToStatusPanel(ludemeCounter.result());
            System.out.println(ludemeCounter.result());
        }
        else if (source.getText().equals("Load Game")) {
            if (!SettingsManager.agentsPaused) {
                SettingsManager.agentsPaused = true;
                Manager.ref().interruptAI();
            }
            GameLoading.loadGameFromMemory(false);
        }
        else if (source.getText().equals("Load Game from File")) {
            if (!SettingsManager.agentsPaused) {
                SettingsManager.agentsPaused = true;
                Manager.ref().interruptAI();
            }
            GameLoading.loadGameFromFile(false);
        }
        else if (source.getText().equals("Load Random Game")) {
            if (!SettingsManager.agentsPaused) {
                SettingsManager.agentsPaused = true;
                Manager.ref().interruptAI();
            }
            GameLoading.loadRandomGame();
        }
        else if (source.getText().equals("Save Trial")) {
            TrialLoading.saveTrial();
        }
        else if (source.getText().equals("Create Game")) {
            final String savedPath = EditorDialog.saveGameDescription(DesktopApp.playerApp(), Constants.BASIC_GAME_DESCRIPTION);
            GameLoading.loadGameFromFilePath(savedPath + ".lud", false);
            EditorDialog.createAndShowGUI(true, true, true);
        }
        else if (source.getText().equals("Load Trial")) {
            if (!SettingsManager.agentsPaused) {
                SettingsManager.agentsPaused = true;
                Manager.ref().interruptAI();
            }
            TrialLoading.loadTrial(false);
        }
        else if (source.getText().equals("Load Tournament File")) {
            if (!SettingsManager.agentsPaused) {
                SettingsManager.agentsPaused = true;
                Manager.ref().interruptAI();
            }
            MiscLoading.loadTournamentFile();
        }
        else if (source.getText().equals("Editor (Packed)")) {
            EditorDialog.createAndShowGUI(false, true, true);
        }
        else if (source.getText().equals("Editor (Expanded)")) {
            EditorDialog.createAndShowGUI(true, true, true);
        }
        else if (source.getText().equals("Export Thumbnails")) {
            DesktopApp.frame().setSize(464, 464);
            EventQueue.invokeLater(() -> EventQueue.invokeLater(Thumbnails::generateThumbnails));
        }
        else if (source.getText().equals("Export All Thumbnails")) {
            DesktopApp.frame().setSize(464, 464);
            final String[] choices = FileHandling.listGames();
            final ArrayList<String> validChoices = new ArrayList<>();
            for (final String s : choices) {
                if (!s.contains("/bad/") && !s.contains("/bad_playout/") && !s.contains("/test/") && !s.contains("/wip/") && !s.contains("/wishlist/")) {
                    validChoices.add(s);
                }
            }
            final Timer t = new Timer();
            t.scheduleAtFixedRate(new TimerTask() {
                int gameChoice = 0;
                
                @Override
                public void run() {
                    EventQueue.invokeLater(() -> {
                        GameLoading.loadGameFromName(validChoices.get(this.gameChoice), false);
                        ++this.gameChoice;
                    });
                }
            }, 1000L, 50000L);
            final Timer t2 = new Timer();
            t2.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    Thumbnails.generateThumbnails();
                }
            }, 14000L, 50000L);
        }
        else if (source.getText().equals("Export Board Thumbnail")) {
            Thumbnails.generateBoardThumbnail();
        }
        else if (source.getText().equals("Export All Board Thumbnails")) {
            final String[] choices = FileHandling.listGames();
            final ArrayList<String> validChoices = new ArrayList<>();
            for (final String s : choices) {
                if (!s.contains("/bad/") && !s.contains("/bad_playout/") && !s.contains("/test/") && !s.contains("/wip/") && !s.contains("/wishlist/")) {
                    validChoices.add(s);
                }
            }
            final Timer t = new Timer();
            t.scheduleAtFixedRate(new TimerTask() {
                int gameChoice = 0;
                
                @Override
                public void run() {
                    GameLoading.loadGameFromName(validChoices.get(this.gameChoice), false);
                    ++this.gameChoice;
                }
            }, 1000L, 20000L);
            final Timer t2 = new Timer();
            t2.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    Thumbnails.generateBoardThumbnail();
                }
            }, 11000L, 20000L);
        }
        else if (source.getText().equals("Restart")) {
            GameRestart.restartGame(false);
        }
        else if (source.getText().equals("Random Move")) {
            Manager.ref().randomMove();
        }
        else if (source.getText().equals("Random Playout")) {
            if (!game.isDeductionPuzzle()) {
                Manager.ref().randomPlayout();
            }
        }
        else if (source.getText().equals("Time Random Playouts")) {
            if (!game.isDeductionPuzzle()) {
                DesktopApp.playerApp().setTemporaryMessage("This will take about 40 seconds, during which time the UI will not respond.\n");
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Manager.ref().interruptAI();
                        DesktopApp.frame().setContentPane(DesktopApp.view());
                        final int rate = Manager.ref().timeRandomPlayouts();
                        DesktopApp.playerApp().addTextToStatusPanel(rate + " random playouts/s.\n");
                        DesktopApp.playerApp().setTemporaryMessage("");
                        DesktopApp.playerApp().setTemporaryMessage("Analysis Complete.\n");
                        Manager.app.repaint();
                    }
                }, 1000L);
            }
            else {
                DesktopApp.playerApp().setVolatileMessage("Time Random Playouts is disabled for deduction puzzles.\n");
            }
        }
        else if (source.getText().equals("Time Random Playouts in Background")) {
            if (MainMenuFunctions.timeRandomPlayoutsThread != null) {
                DesktopApp.playerApp().addTextToStatusPanel("Time Random Playouts is already in progress!\n");
                return;
            }
            if (!game.isDeductionPuzzle()) {
                MainMenuFunctions.timeRandomPlayoutsThread = new Thread(() -> {
                    int rate = Manager.ref().timeRandomPlayouts();
                    EventQueue.invokeLater(() -> {
                        DesktopApp.playerApp().addTextToStatusPanel(rate + " random playouts/s.\n");
                        DesktopApp.playerApp().setTemporaryMessage("");
                        DesktopApp.playerApp().setTemporaryMessage("Analysis Complete.\n");
                        Manager.app.repaint();
                        MainMenuFunctions.timeRandomPlayoutsThread = null;
                    });
                });
                DesktopApp.playerApp().setTemporaryMessage("Time Random Playouts is starting. This will take about 40 seconds.\n");
                MainMenuFunctions.timeRandomPlayoutsThread.setDaemon(true);
                MainMenuFunctions.timeRandomPlayoutsThread.start();
            }
            else {
                DesktopApp.playerApp().setVolatileMessage("Time Random Playouts is disabled for deduction puzzles.\n");
            }
        }
        else if (source.getText().equals("Show Properties")) {
            final Field[] fields = GameType.class.getFields();
            final String[] flags = new String[fields.length];
            final long[] flagsValues = new long[fields.length];
            final StringBuilder properties = new StringBuilder("The properties of the game are: \n\n");
            for (int i = 0; i < fields.length; ++i) {
                flags[i] = fields[i].toString();
                flags[i] = flags[i].substring(flags[i].lastIndexOf(46) + 1);
                try {
                    flagsValues[i] = fields[i].getLong(GameType.class);
                }
                catch (IllegalArgumentException | IllegalAccessException e2) {
                    e2.printStackTrace();
                }
            }
            for (int i = 0; i < flagsValues.length; ++i) {
                if ((game.gameFlags() & flagsValues[i]) != 0x0L) {
                    properties.append(flags[i] + "\n");
                }
            }
            Manager.app.addTextToAnalysisPanel(properties.toString());
        }
        else if (source.getText().equals("Leave/Resign Game")) {
            if (!SettingsNetwork.activePlayers[SettingsNetwork.getNetworkPlayerNumber()]) {
                System.out.println("the game is already over for you");
            }
            else if (SettingsNetwork.getActiveGameId() != 0) {
                final URL resource = DesktopApp.playerApp().getClass().getResource("/ludii-logo-64x64.png");
                BufferedImage image = null;
                try {
                    image = ImageIO.read(resource);
                }
                catch (IOException ex) {}
                final int dialogResult = JOptionPane.showConfirmDialog(DesktopApp.frame(), "Do you really want to leave this game?\nIf the game has already started then this will be considered a loss.", "Last Chance!", 0, 3, new ImageIcon(image));
                if (dialogResult == 0) {
                    DatabaseFunctions.sendForfeitToDatabase();
                }
            }
        }
        else if (source.getText().equals("Propose/Accept a Draw")) {
            if (!SettingsNetwork.activePlayers[SettingsNetwork.getNetworkPlayerNumber()]) {
                System.out.println("the game is already over for you");
            }
            else if (SettingsNetwork.getActiveGameId() != 0) {
                DatabaseFunctions.sendProposeDraw();
            }
        }
        else if (source.getText().equals("List Legal Moves")) {
            final Moves legal = game.moves(context);
            DesktopApp.playerApp().addTextToStatusPanel("Legal Moves: \n");
            for (int j = 0; j < legal.moves().size(); ++j) {
                DesktopApp.playerApp().addTextToStatusPanel(j + " - " + legal.moves().get(j).getAllActions(context) + "\n");
            }
        }
        else if (source.getText().equals("Game Screenshot")) {
            GUIUtil.gameScreenshot("Image " + new Date().getTime());
        }
        else if (source.getText().equals("Play/Pause")) {
            DesktopApp.view();
            MainWindow.toolPanel();
            DesktopApp.frame().buttons.get(7).press();
        }
        else if (source.getText().equals("Previous Move")) {
            DesktopApp.view();
            MainWindow.toolPanel();
            DesktopApp.frame().buttons.get(6).press();
        }
        else if (source.getText().equals("Next Move")) {
            DesktopApp.view();
            MainWindow.toolPanel();
            DesktopApp.frame().buttons.get(8).press();
        }
        else if (source.getText().equals("Go To Start")) {
            DesktopApp.view();
            MainWindow.toolPanel();
            DesktopApp.frame().buttons.get(5).press();
        }
        else if (source.getText().equals("Go To End")) {
            DesktopApp.view();
            MainWindow.toolPanel();
            DesktopApp.frame().buttons.get(9).press();
        }
        else if (source.getText().equals("Random Playout Instance")) {
            Manager.ref().randomPlayoutSingleInstance();
        }
        else if (source.getText().equals("Clear Board")) {
            final Moves csq = new BaseMoves(null);
            final Move nextMove = new Move(new ActionSetNextPlayer(context.state().mover()));
            csq.moves().add(nextMove);
            for (int k = 0; k < context.board().numSites(); ++k) {
                final ActionRemove actionRemove = new ActionRemove(context.board().defaultSite(), k, true);
                final Move moveToApply = new Move(actionRemove);
                moveToApply.then().add(csq);
                for (final Action a : moveToApply.actions()) {
                    a.apply(context, false);
                }
                final int currentMover = context.state().mover();
                final int nextMover = context.state().next();
                final int previousMover = context.state().prev();
                context.state().setMover(currentMover);
                context.state().setNext(nextMover);
                context.state().setPrev(previousMover);
            }
            Manager.app.updateTabs(context);
            Manager.app.repaint();
        }
        else if (source.getText().equals("Next Player")) {
            final ActionSetNextPlayer actionSetNextPlayer = new ActionSetNextPlayer(context.state().next());
            final Move moveToApply2 = new Move(actionSetNextPlayer);
            for (final Action a2 : moveToApply2.actions()) {
                a2.apply(context, false);
            }
            final int currentMover2 = context.state().mover();
            final int nextMover2 = context.state().next();
            final int previousMover2 = context.state().prev();
            context.state().setMover(currentMover2);
            context.state().setNext(nextMover2);
            context.state().setPrev(previousMover2);
            Manager.app.updateTabs(context);
            Manager.app.repaint();
        }
        else if (source.getText().equals("Exit Sandbox Mode")) {
            SettingsVC.sandboxMode = false;
            context.game().rules().setEnd(SettingsManager.priorSandboxEndRules);
            context.trial().clearLegalMoves();
            context.game().moves(context);
            Manager.app.repaint();
        }
        else if (source.getText().equals("Cycle Players")) {
            AIUtil.cycleAgents();
        }
        else if (source.getText().equals("Generate Grammar")) {
            DesktopApp.playerApp().addTextToStatusPanel(Grammar.grammar().toString());
            System.out.print(Grammar.grammar());
            try {
                Grammar.grammar().export("ludii-grammar-1.0.8.txt");
            }
            catch (IOException e4) {
                e4.printStackTrace();
            }
        }
        else if (source.getText().equals("Generate Symbols")) {
            DesktopApp.playerApp().addTextToStatusPanel(Grammar.grammar().getLudemes());
            System.out.print(Grammar.grammar().getLudemes());
        }
        else if (source.getText().equals("Rules in English")) {
            final String rules = game.toEnglish();
            DesktopApp.playerApp().addTextToStatusPanel(rules);
            System.out.print(rules);
        }
        else if (source.getText().equals("Estimate Branching Factor")) {
            EvalUtil.estimateBranchingFactor();
        }
        else if (source.getText().equals("Estimate Game Length")) {
            EvalUtil.estimateGameLength();
        }
        else if (source.getText().equals("Estimate Game Tree Complexity")) {
            EvalUtil.estimateGameTreeComplexity(false);
        }
        else if (source.getText().equals("Estimate Game Tree Complexity (No State Repetition)")) {
            EvalUtil.estimateGameTreeComplexity(true);
        }
        else if (source.getText().equals("Prove Win")) {
            EvalUtil.proveState(ProofNumberSearch.ProofGoals.PROVE_WIN);
        }
        else if (source.getText().equals("Prove Loss")) {
            EvalUtil.proveState(ProofNumberSearch.ProofGoals.PROVE_LOSS);
        }
        else if (source.getText().equals("Evaluate AI vs. AI")) {
            if (!SettingsManager.agentsPaused) {
                DesktopApp.view();
                MainWindow.tabPanel().page(3).addText("Cannot start evaluation of AIs when agents are not paused!");
                return;
            }
            final EvalAIsThread evalThread = EvalAIsThread.construct(Manager.ref(), AIDetails.convertToAIList(Manager.aiSelected));
            SettingsManager.canSendToDatabase = false;
            SettingsManager.agentsPaused = false;
            evalThread.start();
            Manager.app.repaint();
        }
        else if (source.getText().startsWith("Evaluation Dialog")) {
            EvaluationDialog.showDialog();
        }
        else if (source.getText().startsWith("Distance Dialog")) {
            DistanceDialog.showDialog();
        }
        else if (source.getText().startsWith("Compile Game (Debug)")) {
            if (!SettingsManager.agentsPaused) {
                SettingsManager.agentsPaused = true;
                Manager.ref().interruptAI();
            }
            GameLoading.loadGameFromMemory(true);
        }
        else if (source.getText().startsWith("Expanded Description")) {
            final String[] choices = FileHandling.listGames();
            String initialChoice = choices[0];
            for (final String choice : choices) {
                if (Manager.savedLudName() != null && Manager.savedLudName().endsWith(choice.replaceAll(Pattern.quote("\\"), "/"))) {
                    initialChoice = choice;
                    break;
                }
            }
            final String choice2 = GameLoaderDialog.showDialog(DesktopApp.frame(), choices, initialChoice, true);
            if (choice2 == null) {
                return;
            }
            String path = choice2.replaceAll(Pattern.quote("\\"), "/");
            path = path.substring(path.indexOf("/lud/"));
            final InputStream in = GameLoader.class.getResourceAsStream(path);
            String desc = "";
            try (final BufferedReader rdr = new BufferedReader(new InputStreamReader(in))) {
                String line;
                while ((line = rdr.readLine()) != null) {
                    desc = desc + line + "\n";
                }
            }
            catch (IOException e5) {
                e5.printStackTrace();
            }
            final Description gameDescription = new Description(desc);
            final boolean didParse = Parser.expandAndParse(gameDescription, SettingsManager.userSelections, new Report(), false);
            final String report = "Expanded game description:\n" + gameDescription.expanded() + "\nGame description " + (didParse ? "parsed." : "did not parse. ");
            System.out.println(report);
            DesktopApp.playerApp().addTextToStatusPanel(report);
        }
        else if (source.getText().startsWith("Metadata Description")) {
            final String[] choices = FileHandling.listGames();
            String initialChoice = choices[0];
            for (final String choice : choices) {
                if (Manager.savedLudName() != null && Manager.savedLudName().endsWith(choice.replaceAll(Pattern.quote("\\"), "/"))) {
                    initialChoice = choice;
                    break;
                }
            }
            final String choice2 = GameLoaderDialog.showDialog(DesktopApp.frame(), choices, initialChoice, true);
            if (choice2 == null) {
                return;
            }
            String path = choice2.replaceAll(Pattern.quote("\\"), "/");
            path = path.substring(path.indexOf("/lud/"));
            final InputStream in = GameLoader.class.getResourceAsStream(path);
            String desc = "";
            try (final BufferedReader rdr = new BufferedReader(new InputStreamReader(in))) {
                String line;
                while ((line = rdr.readLine()) != null) {
                    desc = desc + line + "\n";
                }
            }
            catch (IOException e5) {
                e5.printStackTrace();
            }
            final Description gameDescription = new Description(desc);
            Parser.expandAndParse(gameDescription, SettingsManager.userSelections, new Report(), false);
            final String report2 = "Metadata:\n" + gameDescription.metadata();
            System.out.println(report2);
            DesktopApp.playerApp().addTextToStatusPanel(report2);
        }
        else if (source.getText().startsWith("Jump to Move")) {
            final int moveToJumpTo = Integer.parseInt(JOptionPane.showInputDialog(DesktopApp.frame(), "Which move to jump to?"));
            if (SettingsNetwork.getActiveGameId() == 0) {
                ToolView.jumpToMove(moveToJumpTo + ContextSnapshot.getContext().trial().numInitialPlacementMoves());
            }
        }
        else if (source.getText().startsWith("Evaluate Heuristic")) {
            final String heuristicStr = JOptionPane.showInputDialog(DesktopApp.frame(), "Enter heuristic.");
            if (SettingsNetwork.getActiveGameId() == 0) {
                final Heuristics heuristics = (Heuristics)compileObject("(heuristics " + heuristicStr + ")", "metadata.ai.heuristics.Heuristics", new Report());
                heuristics.init(game);
                for (int p = 1; p <= game.players().count(); ++p) {
                    System.out.println("Heuristic score for Player " + p + " = " + heuristics.computeValue(context, p, 0.0f));
                }
            }
        }
        else if (source.getText().startsWith("Print Board Graph")) {
            System.out.println(context.board().graph());
        }
        else if (source.getText().startsWith("Print Trajectories")) {
            context.board().graph().trajectories().report(context.board().graph());
        }
        else if (source.getText().startsWith("Preferences")) {
            SettingsDialog.createAndShowGUI(0);
        }
        else if (source.getText().equals("Load SVG")) {
            MiscLoading.loadSVG(DesktopApp.view());
        }
        else if (source.getText().equals("View SVG")) {
            SVGViewerDialog.showDialog(DesktopApp.frame(), SVGLoader.listSVGs());
        }
        else if (source.getText().equals("Remote Play")) {
            SettingsNetwork.setNetworkTabPosition(null);
            RemoteDialog.showDialog();
        }
        else if (source.getText().equals("Initialise Server Socket")) {
            final String port = JOptionPane.showInputDialog("Port Number (4 digits)");
            int portNumber = 0;
            try {
                if (port.length() != 4) {
                    throw new Exception("Port number must be four digits long.");
                }
                portNumber = Integer.parseInt(port);
            }
            catch (Exception E) {
                DesktopApp.playerApp().addTextToStatusPanel("Please enter a valid four digit port number.\n");
                return;
            }
            LocalFunctions.initialiseServerSocket(portNumber);
        }
        else if (source.getText().equals("Test Message Socket")) {
            final String port = JOptionPane.showInputDialog("Port Number (4 digits)");
            int portNumber = 0;
            try {
                if (port.length() != 4) {
                    throw new Exception("Port number must be four digits long.");
                }
                portNumber = Integer.parseInt(port);
            }
            catch (Exception E) {
                DesktopApp.playerApp().addTextToStatusPanel("Please enter a valid four digit port number.\n");
                return;
            }
            final String message = JOptionPane.showInputDialog("Message");
            LocalFunctions.initialiseClientSocket(portNumber, message);
        }
        else if (source.getText().equals("Quit")) {
            System.exit(0);
        }
        else if (source.getText().equals("More Developer Options")) {
            DeveloperDialog.showDialog();
        }
        else {
            try {
                GameLoading.loadGameFromName(source.getText() + ".lud", false);
            }
            catch (Exception E2) {
                System.out.println("This game no longer exists");
            }
        }
    }
    
    public static void checkItemStateChanges(final ItemEvent e) {
        final JMenuItem source = (JMenuItem)e.getSource();
        final Context context = ContextSnapshot.getContext();
        if (source.getText().equals("Sandbox Mode (Beta)")) {
            SettingsVC.sandboxMode = !SettingsVC.sandboxMode;
            if (SettingsVC.sandboxMode) {
                DesktopApp.setTrialContainsSandbox(true);
                SettingsManager.agentsPaused = true;
                SettingsManager.priorSandboxEndRules = context.game().rules().end();
                context.game().rules().setEnd(new End(new If(BooleanConstant.construct(false), null, null, new Result(RoleType.P1, ResultType.Win)), null));
                try {
                    if (ContextSnapshot.getContext().trial().moves().size() != Manager.savedTrial().moves().size()) {
                        MainWindow.tabPanel().page(1).clear();
                        MainWindow.tabPanel().page(2).clear();
                        final List<Move> tempActions = new ArrayList<>(ContextSnapshot.getContext().trial().moves());
                        GameRestart.clearBoard();
                        for (int i = ContextSnapshot.getContext().trial().moves().size(); i < tempActions.size(); ++i) {
                            Manager.ref().makeSavedMove(tempActions.get(i));
                        }
                    }
                }
                catch (Exception ex) {}
            }
            else {
                SettingsVC.sandboxMode = false;
                context.game().rules().setEnd(SettingsManager.priorSandboxEndRules);
                context.trial().clearLegalMoves();
                context.game().moves(context);
                Manager.app.repaint();
            }
        }
        if (source.getText().equals("Auto From Moves")) {
            SettingsManager.autoMoveFrom = !SettingsManager.autoMoveFrom;
        }
        if (source.getText().equals("Auto To Moves")) {
            SettingsManager.autoMoveTo = !SettingsManager.autoMoveTo;
        }
        if (source.getText().equals("Show Legal Moves")) {
            SettingsVC.showPossibleMoves = !SettingsVC.showPossibleMoves;
        }
        if (source.getText().equals("Show Board")) {
            SettingsManager.showBoard = !SettingsManager.showBoard;
        }
        if (source.getText().equals("Show dev tooltip")) {
            SettingsDesktop.cursorTooltipDev = !SettingsDesktop.cursorTooltipDev;
        }
        if (source.getText().equals("Show Board Shape")) {
            SettingsVC.showBoardShape = !SettingsVC.showBoardShape;
        }
        if (source.getText().equals("Show Pieces")) {
            SettingsManager.showPieces = !SettingsManager.showPieces;
        }
        else if (source.getText().equals("Show Graph")) {
            SettingsManager.showGraph = !SettingsManager.showGraph;
        }
        else if (source.getText().equals("Show Cell Connections")) {
            SettingsManager.showConnections = !SettingsManager.showConnections;
        }
        else if (source.getText().equals("Show local state options")) {
            SettingsManager.canSelectLocalState = !SettingsManager.canSelectLocalState;
        }
        else if (source.getText().equals("Show count options")) {
            SettingsManager.canSelectCount = !SettingsManager.canSelectCount;
        }
        else if (source.getText().equals("Show rotation options")) {
            SettingsManager.canSelectRotation = !SettingsManager.canSelectRotation;
        }
        else if (source.getText().equals("Show Indices")) {
            SettingsVC.showIndices = !SettingsVC.showIndices;
            if (SettingsVC.showCoordinates) {
                SettingsVC.showCoordinates = false;
            }
            if (SettingsVC.showIndices) {
                SettingsVC.showVertexIndices = false;
                SettingsVC.showEdgeIndices = false;
                SettingsVC.showCellIndices = false;
            }
        }
        else if (source.getText().equals("Show Coordinates")) {
            SettingsVC.showCoordinates = !SettingsVC.showCoordinates;
            if (SettingsVC.showIndices) {
                SettingsVC.showIndices = false;
            }
            if (SettingsVC.showCoordinates) {
                SettingsVC.showVertexCoordinates = false;
                SettingsVC.showEdgeCoordinates = false;
                SettingsVC.showCellCoordinates = false;
            }
        }
        else if (source.getText().equals("Show Cell Indices")) {
            SettingsVC.showCellIndices = !SettingsVC.showCellIndices;
            if (SettingsVC.showCellCoordinates) {
                SettingsVC.showCellCoordinates = false;
            }
            if (SettingsVC.showCellIndices) {
                SettingsVC.showIndices = false;
            }
        }
        else if (source.getText().equals("Show Edge Indices")) {
            SettingsVC.showEdgeIndices = !SettingsVC.showEdgeIndices;
            if (SettingsVC.showEdgeCoordinates) {
                SettingsVC.showEdgeCoordinates = false;
            }
            if (SettingsVC.showEdgeIndices) {
                SettingsVC.showIndices = false;
            }
        }
        else if (source.getText().equals("Show Vertex Indices")) {
            SettingsVC.showVertexIndices = !SettingsVC.showVertexIndices;
            if (SettingsVC.showVertexCoordinates) {
                SettingsVC.showVertexCoordinates = false;
            }
            if (SettingsVC.showVertexIndices) {
                SettingsVC.showIndices = false;
            }
        }
        else if (source.getText().equals("Show Cell Coordinates")) {
            SettingsVC.showCellCoordinates = !SettingsVC.showCellCoordinates;
            if (SettingsVC.showCellIndices) {
                SettingsVC.showCellIndices = false;
            }
            if (SettingsVC.showCellCoordinates) {
                SettingsVC.showCoordinates = false;
            }
        }
        else if (source.getText().equals("Show Edge Coordinates")) {
            SettingsVC.showEdgeCoordinates = !SettingsVC.showEdgeCoordinates;
            if (SettingsVC.showEdgeIndices) {
                SettingsVC.showEdgeIndices = false;
            }
            if (SettingsVC.showEdgeCoordinates) {
                SettingsVC.showCoordinates = false;
            }
        }
        else if (source.getText().equals("Show Vertex Coordinates")) {
            SettingsVC.showVertexCoordinates = !SettingsVC.showVertexCoordinates;
            if (SettingsVC.showVertexIndices) {
                SettingsVC.showVertexIndices = false;
            }
            if (SettingsVC.showVertexCoordinates) {
                SettingsVC.showCoordinates = false;
            }
        }
        else if (source.getText().equals("Show AI Distribution")) {
            SettingsManager.showAIDistribution = !SettingsManager.showAIDistribution;
        }
        else if (source.getText().equals("Show Last Move")) {
            SettingsManager.showLastMove = !SettingsManager.showLastMove;
        }
        else if (source.getText().equals("Show Ending Moves")) {
            SettingsManager.showEndingMove = !SettingsManager.showEndingMove;
        }
        else if (source.getText().contains("Show Track")) {
            for (int j = 0; j < SettingsVC.trackNames.size(); ++j) {
                if (source.getText().equals(SettingsVC.trackNames.get(j))) {
                    SettingsVC.trackShown.set(j, !SettingsVC.trackShown.get(j));
                }
            }
        }
        else if (source.getText().equals("Swap Rule")) {
            SettingsManager.swapRule = !SettingsManager.swapRule;
            context.game().setUsesSwapRule(SettingsManager.swapRule);
            GameRestart.restartGame(false);
        }
        else if (source.getText().equals("No Repetition Of Game State")) {
            SettingsManager.noRepetition = !SettingsManager.noRepetition;
            if (SettingsManager.noRepetition) {
                context.game().setRepetitionType(RepetitionType.InGame);
            }
            GameRestart.restartGame(false);
        }
        else if (source.getText().equals("No Repetition Within A Turn")) {
            SettingsManager.noRepetitionWithinTurn = !SettingsManager.noRepetitionWithinTurn;
            if (SettingsManager.noRepetition) {
                context.game().setRepetitionType(RepetitionType.InTurn);
            }
            GameRestart.restartGame(false);
        }
        else if (source.getText().equals("Save Heuristics")) {
            SettingsManager.saveHeuristics = !SettingsManager.saveHeuristics;
        }
        else if (source.getText().equals("Automatic")) {
            SettingsManager.puzzleDialogOption = PuzzleSelectionType.Automatic;
        }
        else if (source.getText().equals("Dialog")) {
            SettingsManager.puzzleDialogOption = PuzzleSelectionType.Dialog;
        }
        else if (source.getText().equals("Cycle")) {
            SettingsManager.puzzleDialogOption = PuzzleSelectionType.Cycle;
        }
        else if (source.getText().equals("Illegal Moves Allowed")) {
            SettingsManager.illegalMovesValid = !SettingsManager.illegalMovesValid;
        }
        else if (source.getText().equals("Show Possible Values")) {
            SettingsVC.showCandidateValues = !SettingsVC.showCandidateValues;
        }
        else if (!context.isAMatch() && e.getStateChange() == 1) {
            final Game game = context.game();
            final GameOptions gameOptions = game.description().gameOptions();
            final List<Ruleset> rulesets = game.description().rulesets();
            boolean rulesetSelected = false;
            if (rulesets != null && !rulesets.isEmpty()) {
                for (int rs = 0; rs < rulesets.size(); ++rs) {
                    final Ruleset ruleset = rulesets.get(rs);
                    if (ruleset.heading().equals(source.getText())) {
                        SettingsManager.userSelections.setRuleset(rs);
                        SettingsManager.userSelections.setSelectOptionStrings(new ArrayList<>(ruleset.optionSettings()));
                        rulesetSelected = true;
                        try {
                            GameSetupDesktop.compileAndShowGame(game.description().raw(), true, false);
                        }
                        catch (Exception exception) {
                            GameRestart.restartGame(false);
                        }
                        break;
                    }
                }
            }
            if (!rulesetSelected && gameOptions.numCategories() > 0 && source.getParent() != null) {
                final JPopupMenu fromParent = (JPopupMenu)source.getParent();
                final JMenu parent = (JMenu)fromParent.getInvoker();
                final List<String> currentOptions = SettingsManager.userSelections.selectedOptionStrings();
                for (int cat = 0; cat < gameOptions.numCategories(); ++cat) {
                    final List<Option> options = gameOptions.categories().get(cat).options();
                    if (!options.isEmpty()) {
                        if (options.get(0).menuHeadings().get(0).equals(parent.getText())) {
                            for (final Option option : options) {
                                if (option.menuHeadings().get(1).equals(source.getText())) {
                                    final String selectedOptString = StringRoutines.join("/", option.menuHeadings());
                                    for (int k = 0; k < currentOptions.size(); ++k) {
                                        final String currOption = currentOptions.get(k);
                                        if (currOption.substring(0, currOption.lastIndexOf('/')).equals(selectedOptString.substring(0, selectedOptString.lastIndexOf('/')))) {
                                            currentOptions.remove(k);
                                            break;
                                        }
                                    }
                                    currentOptions.add(selectedOptString);
                                    SettingsManager.userSelections.setSelectOptionStrings(currentOptions);
                                    gameOptions.setOptionsLoaded(true);
                                    SettingsManager.userSelections.setRuleset(game.description().autoSelectRuleset(SettingsManager.userSelections.selectedOptionStrings()));
                                    try {
                                        GameSetupDesktop.compileAndShowGame(game.description().raw(), true, false);
                                    }
                                    catch (Exception exception2) {
                                        GameRestart.restartGame(false);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        EventQueue.invokeLater(() -> {
            DesktopApp.frame().setJMenuBar(new MainMenu());
            Manager.app.repaint();
        });
    }
    
    static {
        MainMenuFunctions.timeRandomPlayoutsThread = null;
    }
}
