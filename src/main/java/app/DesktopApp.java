// 
// Decompiled by Procyon v0.5.36
// 

package app;

import app.display.MainWindow;
import app.loading.FileLoading;
import app.loading.GameLoading;
import app.loading.TrialLoading;
import app.menu.MainMenu;
import app.menu.MainMenuFunctions;
import app.utils.SettingsDesktop;
import app.utils.UserPreferences;
import game.Game;
import main.StringRoutines;
import manager.Manager;
import manager.ai.AIDetails;
import manager.ai.AIMenuName;
import manager.ai.AIUtil;
import manager.network.SettingsNetwork;
import manager.utils.SettingsManager;
import options.GameOptions;
import org.json.JSONObject;
import tournament.Tournament;
import util.Context;
import util.Trial;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public final class DesktopApp extends BaseApp implements ActionListener, ItemListener
{
    public static final String GAME_DEFAULT =
            //WORKING:
            //"/lud/board/space/line/Tic-Tac-Mo.lud"
            //"/lud/board/space/line/Tic-Tac-Toe.lud"
            "/lud/experimental/Flowers.lud"
            ;

    public static boolean devJar;
    private static JFrameList frame;
    private static MainWindow view;
    private static GraphicsDevice currentGraphicsDevice;
    private static final int minimumViewWidth = 400;
    private static final int minimumViewHeight = 400;
    public static String savedStatusTabString;
    private static JFileChooser jsonFileChooser;
    private static JFileChooser jarFileChooser;
    private static JFileChooser gameFileChooser;
    private static boolean shouldLoadTrial;
    private static boolean loadSuccessful;
    private static boolean loadedFromMemory;
    private static boolean preferencesLoaded;
    private static JFileChooser saveGameFileChooser;
    protected static JFileChooser loadGameFileChooser;
    private static JFileChooser loadTrialFileChooser;
    private static JFileChooser loadTournamentFileChooser;
    private static String lastSelectedJsonPath;
    private static String lastSelectedJarPath;
    private static String lastSelectedGamePath;
    private static String lastSelectedSaveGamePath;
    private static String lastSelectedLoadTrialPath;
    private static String lastSelectedLoadTournamentPath;
    private static String matchDescriptionFull;
    private static String matchDescriptionShort;
    private static boolean trialContainsSandbox;
    
    public void createPlayerApp() {
        new Manager(this);
        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < 17; ++i) {
                final JSONObject json = new JSONObject().put("AI", new JSONObject().put("algorithm", "Human"));
                Manager.aiSelected[i] = new AIDetails(json, i, AIMenuName.Human);
            }
            DesktopApp.createFrame();
        });
    }
    
    public static String getFrameTitle(final Context context) {
        final Game game = context.game();
        String frameTitle = "Ludii Player - " + game.name();
        GameOptions gameOptions = game.description().gameOptions();
        if (gameOptions.numCategories() > 0) {
            final List<String> optionHeadings = gameOptions.allOptionStrings(SettingsManager.userSelections.selectedOptionStrings());
            if (!optionHeadings.isEmpty()) {
                final String appendOptions = " (" + StringRoutines.join(", ", optionHeadings) + ")";
                frameTitle += appendOptions;
            }
        }
        if (context.isAMatch()) {
            final Context instanceContext = context.currentInstanceContext();
            frameTitle = frameTitle + " - " + instanceContext.game().name();
            gameOptions = game.description().gameOptions();
            if (gameOptions != null && gameOptions.numCategories() > 0) {
                String appendOptions = " (";
                int found = 0;
                for (int cat = 0; cat < gameOptions.numCategories(); ++cat) {
                    try {
                        if (!gameOptions.categories().get(cat).options().isEmpty()) {
                            final List<String> optionHeadings2 = gameOptions.categories().get(cat).options().get(0).menuHeadings();
                            String optionSelected = optionHeadings2.get(0);
                            optionSelected = optionSelected.substring(optionSelected.indexOf(47) + 1);
                            if (found > 0) {
                                appendOptions += ", ";
                            }
                            appendOptions += optionSelected;
                            ++found;
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                appendOptions += ")";
                if (found > 0) {
                    frameTitle += appendOptions;
                }
            }
            frameTitle = frameTitle + " - game #" + (currentGameIndexForMatch() + 1);
        }
        if (SettingsNetwork.getActiveGameId() > 0 && SettingsNetwork.getTournamentId() > 0) {
            frameTitle = frameTitle + " (game " + SettingsNetwork.getActiveGameId() + " in tournament " + SettingsNetwork.getTournamentId() + ")";
        }
        else if (SettingsNetwork.getActiveGameId() > 0) {
            frameTitle = frameTitle + " (game " + SettingsNetwork.getActiveGameId() + ")";
        }
        return frameTitle;
    }
    
    public void reportError(final String text) {
        if (DesktopApp.view != null && DesktopApp.frame != null) {
            DesktopApp.frame.setContentPane(DesktopApp.view);
            DesktopApp.frame.repaint();
            DesktopApp.frame.revalidate();
        }
        EventQueue.invokeLater(() -> this.addTextToStatusPanel(text + "\n"));
    }
    
    @Override
    public void actionPerformed(final ActionEvent e) {
        MainMenuFunctions.checkActionsPerformed(e);
    }
    
    @Override
    public void itemStateChanged(final ItemEvent e) {
        MainMenuFunctions.checkItemStateChanges(e);
    }
    
    public static void appClosedTasks() {
        final File file = new File("." + File.separator + "ludii.trl");
        TrialLoading.saveTrial(file);
        UserPreferences.savePreferences();
    }
    
    static void createFrame() {
        try {
            UserPreferences.loadPreferences();
            (DesktopApp.frame = new JFrameList("Ludii Player")).setDefaultCloseOperation(3);
            try {
                final URL resource = playerApp().getClass().getResource("/ludii-logo-100x100.png");
                final BufferedImage image = ImageIO.read(resource);
                DesktopApp.frame.setIconImage(image);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            DesktopApp.view = new MainWindow();
            DesktopApp.frame.setContentPane(DesktopApp.view);
            DesktopApp.frame.setSize(SettingsDesktop.defaultWidth, SettingsDesktop.defaultHeight);
            try {
                if (SettingsDesktop.defaultX == -1 || SettingsDesktop.defaultY == -1) {
                    DesktopApp.frame.setLocationRelativeTo(null);
                }
                else {
                    DesktopApp.frame.setLocation(SettingsDesktop.defaultX, SettingsDesktop.defaultY);
                }
                if (SettingsDesktop.frameMaximised) {
                    DesktopApp.frame.setExtendedState(DesktopApp.frame.getExtendedState() | 0x6);
                }
            }
            catch (Exception e2) {
                DesktopApp.frame.setLocationRelativeTo(null);
            }
            DesktopApp.frame.setVisible(true);
            DesktopApp.frame.setMinimumSize(new Dimension(400, 400));
            FileLoading.createFileChoosers();
            setCurrentGraphicsDevice(DesktopApp.frame.getGraphicsConfiguration().getDevice());
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (DesktopApp.loadSuccessful()) {
                    DesktopApp.appClosedTasks();
                }
            }));
            loadInitialGame(true);
        }
        catch (Exception e2) {
            System.out.println("Failed to create application frame.");
            System.out.println(e2.getMessage());
        }
    }
    
    private static void loadInitialGame(final boolean firstTry) {
        try {
            if (Manager.savedLudName() != null) {
                if (loadedFromMemory()) {
                    GameLoading.loadGameFromMemory(Manager.savedLudName(), false);
                }
                else {
                    GameLoading.loadGameFromFilePath(Manager.savedLudName(), false);
                }
            }
            else if (firstTry) {
                GameLoading.loadGameFromMemory(
                        GAME_DEFAULT
                , false);
            }
            else {
                GameLoading.loadFailSafeGame();
                EventQueue.invokeLater(() -> playerApp().setTemporaryMessage("Failed to start game. Loading default game (Tic-Tac-Toe)."));
            }
            DesktopApp.frame.setJMenuBar(new MainMenu());
            for (int i = 1; i < Manager.ref().context().players().size(); ++i) {
                if (aiSelected()[i] != null) {
                    AIUtil.updateSelectedAI(Manager.aiSelected[i].object(), i, Manager.aiSelected[i].menuItemName());
                }
            }
            Manager.updateCurrentGameRngInternalState();
            Manager.ref().context().game().start(Manager.ref().context());
            TrialLoading.loadStartTrial();
            EventQueue.invokeLater(() -> MainWindow.tabPanel().resetTabs());
        }
        catch (Exception e) {
            if (firstTry) {
                Manager.setSavedLudName(null);
                setLoadedFromMemory(true);
                setLoadTrial(false);
                loadInitialGame(false);
            }
            if (Manager.savedLudName() != null) {
                playerApp().addTextToStatusPanel("Failed to start game: " + Manager.savedLudName() + "\n");
            }
            else if (Manager.ref().context().game().name() != null) {
                playerApp().addTextToStatusPanel("Failed to start external game description.\n");
            }
        }
    }
    
    public static void setSavedTrial(final Trial i) {
        Manager.setSavedTrial(i);
    }
    
    public static Tournament tournament() {
        return Manager.tournament;
    }
    
    public static void setTournament(final Tournament tournament) {
        Manager.tournament = tournament;
    }
    
    public static GraphicsDevice currentGraphicsDevice() {
        return DesktopApp.currentGraphicsDevice;
    }
    
    public static void setCurrentGraphicsDevice(final GraphicsDevice currentGraphicsDevice) {
        DesktopApp.currentGraphicsDevice = currentGraphicsDevice;
    }
    
    public static int currentGameIndexForMatch() {
        return Manager.currentGameIndexForMatch;
    }
    
    public static void setCurrentGameIndexForMatch(final int currentGameIndexForMatch) {
        Manager.currentGameIndexForMatch = currentGameIndexForMatch;
    }
    
    public static ArrayList<Trial> instanceTrialsSoFar() {
        return Manager.instanceTrialsSoFar;
    }
    
    public static void setInstanceTrialsSoFar(final ArrayList<Trial> instanceTrialsSoFar) {
        Manager.instanceTrialsSoFar = instanceTrialsSoFar;
    }
    
    public static boolean trialContainsSandbox() {
        return DesktopApp.trialContainsSandbox;
    }
    
    public static void setTrialContainsSandbox(final boolean trialContainsSandbox) {
        DesktopApp.trialContainsSandbox = trialContainsSandbox;
    }
    
    public static boolean loadedFromMemory() {
        return DesktopApp.loadedFromMemory;
    }
    
    public static void setLoadedFromMemory(final boolean loadedFromMemory) {
        DesktopApp.loadedFromMemory = loadedFromMemory;
    }
    
    public static AIDetails[] aiSelected() {
        return Manager.aiSelected;
    }
    
    public static JFileChooser jsonFileChooser() {
        return DesktopApp.jsonFileChooser;
    }
    
    public static void setJsonFileChooser(final JFileChooser jsonFileChooser) {
        DesktopApp.jsonFileChooser = jsonFileChooser;
    }
    
    public static JFileChooser jarFileChooser() {
        return DesktopApp.jarFileChooser;
    }
    
    public static JFileChooser gameFileChooser() {
        return DesktopApp.gameFileChooser;
    }
    
    public static void setJarFileChooser(final JFileChooser jarFileChooser) {
        DesktopApp.jarFileChooser = jarFileChooser;
    }
    
    public static void setGameFileChooser(final JFileChooser gameFileChooser) {
        DesktopApp.gameFileChooser = gameFileChooser;
    }
    
    public static JFileChooser saveGameFileChooser() {
        return DesktopApp.saveGameFileChooser;
    }
    
    public static void setSaveGameFileChooser(final JFileChooser saveGameFileChooser) {
        DesktopApp.saveGameFileChooser = saveGameFileChooser;
    }
    
    public static JFileChooser loadTrialFileChooser() {
        return DesktopApp.loadTrialFileChooser;
    }
    
    public static void setLoadTrialFileChooser(final JFileChooser loadTrialFileChooser) {
        DesktopApp.loadTrialFileChooser = loadTrialFileChooser;
    }
    
    public static JFileChooser loadTournamentFileChooser() {
        return DesktopApp.loadTournamentFileChooser;
    }
    
    public static void setLoadTournamentFileChooser(final JFileChooser loadTournamentFileChooser) {
        DesktopApp.loadTournamentFileChooser = loadTournamentFileChooser;
    }
    
    public static boolean preferencesLoaded() {
        return DesktopApp.preferencesLoaded;
    }
    
    public static void setPreferencesLoaded(final boolean preferencesLoaded) {
        DesktopApp.preferencesLoaded = preferencesLoaded;
    }
    
    public static void setLoadTrial(final boolean shouldLoadTrial) {
        DesktopApp.shouldLoadTrial = shouldLoadTrial;
    }
    
    public static boolean shouldLoadTrial() {
        return DesktopApp.shouldLoadTrial;
    }
    
    public static String lastSelectedSaveGamePath() {
        return DesktopApp.lastSelectedSaveGamePath;
    }
    
    public static void setLastSelectedSaveGamePath(final String lastSelectedSaveGamePath) {
        DesktopApp.lastSelectedSaveGamePath = lastSelectedSaveGamePath;
    }
    
    public static String lastSelectedJsonPath() {
        return DesktopApp.lastSelectedJsonPath;
    }
    
    public static void setLastSelectedJsonPath(final String lastSelectedJsonPath) {
        DesktopApp.lastSelectedJsonPath = lastSelectedJsonPath;
    }
    
    public static String lastSelectedJarPath() {
        return DesktopApp.lastSelectedJarPath;
    }
    
    public static String lastSelectedGamePath() {
        return DesktopApp.lastSelectedGamePath;
    }
    
    public static void setLastSelectedJarPath(final String lastSelectedJarPath) {
        DesktopApp.lastSelectedJarPath = lastSelectedJarPath;
    }
    
    public static void setLastSelectedGamePath(final String lastSelectedGamePath) {
        DesktopApp.lastSelectedGamePath = lastSelectedGamePath;
    }
    
    public static String lastSelectedLoadTrialPath() {
        return DesktopApp.lastSelectedLoadTrialPath;
    }
    
    public static void setLastSelectedLoadTrialPath(final String lastSelectedLoadTrialPath) {
        DesktopApp.lastSelectedLoadTrialPath = lastSelectedLoadTrialPath;
    }
    
    public static String lastSelectedLoadTournamentPath() {
        return DesktopApp.lastSelectedLoadTournamentPath;
    }
    
    public static void setLastSelectedLoadTournamentPath(final String lastSelectedLoadTournamentPath) {
        DesktopApp.lastSelectedLoadTournamentPath = lastSelectedLoadTournamentPath;
    }
    
    public static String matchDescriptionFull() {
        return DesktopApp.matchDescriptionFull;
    }
    
    public static void setMatchDescriptionFull(final String matchDescriptionFull) {
        DesktopApp.matchDescriptionFull = matchDescriptionFull;
    }
    
    public static String matchDescriptionShort() {
        return DesktopApp.matchDescriptionShort;
    }
    
    public static void setMatchDescriptionShort(final String matchDescriptionShort) {
        DesktopApp.matchDescriptionShort = matchDescriptionShort;
    }
    
    public static DesktopApp playerApp() {
        return PlayerAppProvider.PLAYERAPP;
    }
    
    public static MainWindow view() {
        return DesktopApp.view;
    }
    
    public static JFrameList frame() {
        return DesktopApp.frame;
    }
    
    public static boolean loadSuccessful() {
        return DesktopApp.loadSuccessful;
    }
    
    public static void setLoadSuccessful(final boolean loadSuccessful) {
        DesktopApp.loadSuccessful = loadSuccessful;
    }
    
    @Override
    public void updateFrameTitle() {
        if (SettingsDesktop.jumpingMoveSavedImage == null) {
            frame().setTitle(getFrameTitle(Manager.ref().context()));
        }
    }
    
    static {
        DesktopApp.devJar = false;
        DesktopApp.currentGraphicsDevice = null;
        DesktopApp.savedStatusTabString = "";
        DesktopApp.shouldLoadTrial = false;
        DesktopApp.loadSuccessful = false;
        DesktopApp.loadedFromMemory = false;
        DesktopApp.preferencesLoaded = false;
        DesktopApp.matchDescriptionFull = "";
        DesktopApp.matchDescriptionShort = "";
        DesktopApp.trialContainsSandbox = false;
    }
    
    private static class PlayerAppProvider
    {
        public static final DesktopApp PLAYERAPP;
        
        static {
            PLAYERAPP = new DesktopApp();
        }
    }
}
