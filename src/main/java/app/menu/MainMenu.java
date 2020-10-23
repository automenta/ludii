// 
// Decompiled by Procyon v0.5.36
// 

package app.menu;

import app.DesktopApp;
import app.loading.MiscLoading;
import app.sandbox.SandboxUtil;
import app.utils.SettingsDesktop;
import game.equipment.container.board.Track;
import game.types.play.RepetitionType;
import main.FileHandling;
import main.StringRoutines;
import manager.Manager;
import manager.network.SettingsNetwork;
import manager.utils.ContextSnapshot;
import manager.utils.PuzzleSelectionType;
import manager.utils.SettingsManager;
import options.GameOptions;
import options.Option;
import options.Ruleset;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import util.Context;
import util.SettingsVC;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class MainMenu extends JMenuBar
{
    public static JMenu mainOptionsMenu;
    protected JMenu submenu;
    public JMenuItem showIndexOption;
    public JMenuItem showCoordinateOption;
    public static String[] recentGames;
    
    public MainMenu() {
        final ActionListener al = DesktopApp.playerApp();
        final ItemListener il = DesktopApp.playerApp();
        UIManager.put("Menu.font", new Font("Arial", 0, 16));
        UIManager.put("MenuItem.font", new Font("Arial", 0, 16));
        UIManager.put("CheckBoxMenuItem.font", new Font("Arial", 0, 16));
        UIManager.put("RadioButtonMenuItem.font", new Font("Arial", 0, 16));
        JMenu menu = new JMenu("Ludii");
        this.add(menu);
        JMenuItem menuItem = new JMenuItem("Preferences");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(80, 512));
        menuItem.addActionListener(al);
        menu.add(menuItem);
        menuItem = new JMenuItem("Quit");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(81, 128));
        menuItem.addActionListener(al);
        menu.add(menuItem);
        if (SettingsNetwork.getActiveGameId() == 0) {
            menu = new JMenu("File");
            this.add(menu);
            menuItem = new JMenuItem("Load Game");
            menuItem.setAccelerator(KeyStroke.getKeyStroke(76, 128));
            menuItem.addActionListener(al);
            menu.add(menuItem);
            this.submenu = new JMenu("Load Recent");
            for (int i = 0; i < MainMenu.recentGames.length && MainMenu.recentGames[i] != null; ++i) {
                menuItem = new JMenuItem(MainMenu.recentGames[i]);
                switch (i) {
                    case 0 -> menuItem.setAccelerator(KeyStroke.getKeyStroke(49, 512));
                    case 1 -> menuItem.setAccelerator(KeyStroke.getKeyStroke(50, 512));
                    case 2 -> menuItem.setAccelerator(KeyStroke.getKeyStroke(51, 512));
                    case 3 -> menuItem.setAccelerator(KeyStroke.getKeyStroke(52, 512));
                    case 4 -> menuItem.setAccelerator(KeyStroke.getKeyStroke(53, 512));
                    case 5 -> menuItem.setAccelerator(KeyStroke.getKeyStroke(54, 512));
                    case 6 -> menuItem.setAccelerator(KeyStroke.getKeyStroke(55, 512));
                    case 7 -> menuItem.setAccelerator(KeyStroke.getKeyStroke(56, 512));
                    case 8 -> menuItem.setAccelerator(KeyStroke.getKeyStroke(57, 512));
                    case 9 -> menuItem.setAccelerator(KeyStroke.getKeyStroke(48, 512));
                }
                menuItem.addActionListener(al);
                this.submenu.add(menuItem);
            }
            menu.add(this.submenu);
            menuItem = new JMenuItem("Load Game from File");
            menuItem.setAccelerator(KeyStroke.getKeyStroke(70, 128));
            menuItem.addActionListener(al);
            menuItem.setToolTipText("Load a game description from and external .lud file");
            menu.add(menuItem);
            menuItem = new JMenuItem("Load Random Game");
            menuItem.addActionListener(al);
            menu.add(menuItem);
            if (!SettingsVC.sandboxMode) {
                menu.addSeparator();
                menuItem = new JMenuItem("Load Trial");
                menuItem.addActionListener(al);
                menu.add(menuItem);
                menuItem.setAccelerator(KeyStroke.getKeyStroke(84, 128));
                menuItem = new JMenuItem("Save Trial");
                menuItem.addActionListener(al);
                menu.add(menuItem);
                menuItem.setAccelerator(KeyStroke.getKeyStroke(83, 128));
                menu.addSeparator();
                menuItem = new JMenuItem("Create Game");
                menuItem.addActionListener(al);
                menu.add(menuItem);
                menuItem = new JMenuItem("Editor (Packed)");
                menuItem.addActionListener(al);
                menu.add(menuItem);
                menuItem.setAccelerator(KeyStroke.getKeyStroke(80, 64));
                menuItem = new JMenuItem("Editor (Expanded)");
                menuItem.addActionListener(al);
                menu.add(menuItem);
                menuItem.setAccelerator(KeyStroke.getKeyStroke(69, 64));
            }
        }
        menu = new JMenu("Game");
        this.add(menu);
        if (SettingsNetwork.getActiveGameId() == 0) {
            menuItem = new JMenuItem("Restart");
            menuItem.setAccelerator(KeyStroke.getKeyStroke(82, 128));
            menuItem.addActionListener(al);
            menu.add(menuItem);
            if (!SettingsVC.sandboxMode) {
                menuItem = new JMenuItem("Random Move");
                menuItem.setAccelerator(KeyStroke.getKeyStroke(77, 128));
                menuItem.addActionListener(al);
                menu.add(menuItem);
                menuItem = new JMenuItem("Random Playout");
                menuItem.setAccelerator(KeyStroke.getKeyStroke(80, 128));
                menuItem.addActionListener(al);
                menu.add(menuItem);
            }
        }
        else if (SettingsNetwork.getActiveGameId() != 0) {
            menuItem = new JMenuItem("Propose/Accept a Draw");
            menuItem.addActionListener(al);
            menu.add(menuItem);
            menuItem = new JMenuItem("Leave/Resign Game");
            menuItem.addActionListener(al);
            menu.add(menuItem);
        }
        menu.addSeparator();
        menuItem = new JMenuItem("List Legal Moves");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(76, 64));
        menuItem.addActionListener(al);
        menu.add(menuItem);
        menuItem = new JMenuItem("Game Screenshot");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(67, 64));
        menuItem.addActionListener(al);
        menu.add(menuItem);
        menu.addSeparator();
        JCheckBoxMenuItem cbMenuItem = new JCheckBoxMenuItem("Auto From Moves");
        cbMenuItem.setAccelerator(KeyStroke.getKeyStroke(70, 64));
        cbMenuItem.setSelected(SettingsManager.autoMoveFrom);
        cbMenuItem.addItemListener(il);
        menu.add(cbMenuItem);
        cbMenuItem = new JCheckBoxMenuItem("Auto To Moves");
        cbMenuItem.setAccelerator(KeyStroke.getKeyStroke(84, 64));
        cbMenuItem.setSelected(SettingsManager.autoMoveTo);
        cbMenuItem.addItemListener(il);
        menu.add(cbMenuItem);
        if (SettingsNetwork.getActiveGameId() == 0) {
            menu.addSeparator();
            menuItem = new JMenuItem("Cycle Players");
            menuItem.setAccelerator(KeyStroke.getKeyStroke(82, 512));
            menuItem.addActionListener(al);
            menu.add(menuItem);
            menu.addSeparator();
            menuItem = new JMenuItem("Generate Grammar");
            menuItem.setAccelerator(KeyStroke.getKeyStroke(71, 128));
            menuItem.addActionListener(al);
            menu.add(menuItem);
            menuItem = new JMenuItem("Count Ludemes");
            menuItem.addActionListener(al);
            menu.add(menuItem);
        }
        if (SettingsNetwork.getActiveGameId() == 0) {
            menu = new JMenu("Navigation");
            this.add(menu);
            menuItem = new JMenuItem("Play/Pause");
            menuItem.addActionListener(al);
            menu.add(menuItem);
            menu.addSeparator();
            menuItem = new JMenuItem("Previous Move");
            menuItem.addActionListener(al);
            menu.add(menuItem);
            menuItem = new JMenuItem("Next Move");
            menuItem.addActionListener(al);
            menu.add(menuItem);
            menu.addSeparator();
            menuItem = new JMenuItem("Go To Start");
            menuItem.addActionListener(al);
            menu.add(menuItem);
            menuItem = new JMenuItem("Go To End");
            menuItem.addActionListener(al);
            menu.add(menuItem);
            if (ContextSnapshot.getContext().game().hasSubgames()) {
                menu.addSeparator();
                menuItem = new JMenuItem("Random Playout Instance");
                menuItem.addActionListener(al);
                menu.add(menuItem);
            }
        }
        if (ContextSnapshot.getContext().game().isDeductionPuzzle()) {
            menu = new JMenu("Puzzle");
            this.add(menu);
            this.submenu = new JMenu("Value Selection");
            JRadioButtonMenuItem rbMenuItem = new JRadioButtonMenuItem("Automatic");
            rbMenuItem.setSelected(SettingsManager.puzzleDialogOption == PuzzleSelectionType.Automatic);
            rbMenuItem.addItemListener(il);
            this.submenu.add(rbMenuItem);
            rbMenuItem = new JRadioButtonMenuItem("Dialog");
            rbMenuItem.setSelected(SettingsManager.puzzleDialogOption == PuzzleSelectionType.Dialog);
            rbMenuItem.addItemListener(il);
            this.submenu.add(rbMenuItem);
            rbMenuItem = new JRadioButtonMenuItem("Cycle");
            rbMenuItem.setSelected(SettingsManager.puzzleDialogOption == PuzzleSelectionType.Cycle);
            rbMenuItem.addItemListener(il);
            this.submenu.add(rbMenuItem);
            menu.add(this.submenu);
            menu.addSeparator();
            cbMenuItem = new JCheckBoxMenuItem("Illegal Moves Allowed");
            cbMenuItem.setSelected(SettingsManager.illegalMovesValid);
            cbMenuItem.setAccelerator(KeyStroke.getKeyStroke(73, 64));
            cbMenuItem.addItemListener(il);
            menu.add(cbMenuItem);
            menu.addSeparator();
            cbMenuItem = new JCheckBoxMenuItem("Show Possible Values");
            cbMenuItem.setSelected(SettingsVC.showCandidateValues);
            cbMenuItem.setAccelerator(KeyStroke.getKeyStroke(86, 64));
            cbMenuItem.addItemListener(il);
            menu.add(cbMenuItem);
        }
        if (SettingsVC.sandboxMode) {
            menu = new JMenu("Sandbox");
            this.add(menu);
            menuItem = new JMenuItem("Clear Board");
            menuItem.setAccelerator(KeyStroke.getKeyStroke(75, 64));
            menuItem.addActionListener(al);
            menu.add(menuItem);
            menuItem = new JMenuItem("Next Player");
            menuItem.setAccelerator(KeyStroke.getKeyStroke(89, 64));
            menuItem.addActionListener(al);
            menu.add(menuItem);
            menuItem = new JMenuItem("Exit Sandbox Mode");
            menuItem.setAccelerator(KeyStroke.getKeyStroke(81, 64));
            menuItem.addActionListener(al);
            menu.add(menuItem);
            menu.addSeparator();
            cbMenuItem = new JCheckBoxMenuItem("Show local state options");
            cbMenuItem.setSelected(SettingsManager.canSelectLocalState);
            cbMenuItem.addItemListener(il);
            menu.add(cbMenuItem);
            cbMenuItem = new JCheckBoxMenuItem("Show count options");
            cbMenuItem.setSelected(SettingsManager.canSelectCount);
            cbMenuItem.addItemListener(il);
            menu.add(cbMenuItem);
            cbMenuItem = new JCheckBoxMenuItem("Show rotation options");
            cbMenuItem.setSelected(SettingsManager.canSelectRotation);
            cbMenuItem.addItemListener(il);
            menu.add(cbMenuItem);
        }
        if (SettingsNetwork.getActiveGameId() == 0 && !SettingsVC.sandboxMode) {
            menu = new JMenu("Analysis");
            this.add(menu);
            menuItem = new JMenuItem("Estimate Branching Factor");
            menuItem.addActionListener(al);
            menu.add(menuItem);
            menuItem = new JMenuItem("Estimate Game Length");
            menuItem.addActionListener(al);
            menu.add(menuItem);
            menuItem = new JMenuItem("Estimate Game Tree Complexity");
            menuItem.addActionListener(al);
            menu.add(menuItem);
            menuItem = new JMenuItem("Estimate Game Tree Complexity (No State Repetition)");
            menuItem.addActionListener(al);
            menu.add(menuItem);
            menu.addSeparator();
            menuItem = new JMenuItem("Evaluation Dialog");
            menuItem.setAccelerator(KeyStroke.getKeyStroke(69, 128));
            menuItem.addActionListener(al);
            menu.add(menuItem);
            menu.addSeparator();
            menuItem = new JMenuItem("Distance Dialog");
            menuItem.addActionListener(al);
            menu.add(menuItem);
            menu.addSeparator();
            menuItem = new JMenuItem("Show Properties");
            menuItem.addActionListener(al);
            menu.add(menuItem);
            menu.addSeparator();
            if (SettingsNetwork.getActiveGameId() == 0 && !SettingsVC.sandboxMode) {
                menuItem = new JMenuItem("Time Random Playouts");
                menuItem.setAccelerator(KeyStroke.getKeyStroke(79, 128));
                menuItem.addActionListener(al);
                menu.add(menuItem);
                menuItem = new JMenuItem("Time Random Playouts in Background");
                menuItem.setAccelerator(KeyStroke.getKeyStroke(82, 64));
                menuItem.addActionListener(al);
                menu.add(menuItem);
            }
        }
        if (!ContextSnapshot.getContext().isAMatch()) {
            boolean optionsFound = false;
            for (int o = 0; o < ContextSnapshot.getContext().game().description().gameOptions().numCategories(); ++o) {
                final List<Option> options = ContextSnapshot.getContext().game().description().gameOptions().categories().get(o).options();
                if (!options.isEmpty()) {
                    optionsFound = true;
                }
            }
            if (optionsFound && SettingsNetwork.getActiveGameId() == 0) {
                this.add(MainMenu.mainOptionsMenu = new JMenu("Options"));
                updateOptionsMenu(ContextSnapshot.getContext(), MainMenu.mainOptionsMenu);
            }
        }
        menu = new JMenu("Remote");
        this.add(menu);
        menuItem = new JMenuItem("Remote Play");
        menuItem.addActionListener(al);
        menu.add(menuItem);
        menu.addSeparator();
        menuItem = new JMenuItem("Initialise Server Socket");
        menuItem.addActionListener(al);
        menu.add(menuItem);
        menuItem = new JMenuItem("Test Message Socket");
        menuItem.addActionListener(al);
        menu.add(menuItem);
        if (SettingsNetwork.getActiveGameId() == 0 && !SettingsVC.sandboxMode) {
            menu = new JMenu("Demos");
            final String[] demos = findDemos();
            if (demos.length > 0) {
                this.add(menu);
                for (String demo : demos) {
                    if (demo.endsWith(".json")) {
                        demo = demo.replaceAll(Pattern.quote("\\"), "/");
                        if (demo.contains("/demos/")) {
                            demo = demo.substring(demo.indexOf("/demos/"));
                        }
                        if (!(!demo.isEmpty() && demo.charAt(0) == '/')) {
                            demo = "/" + demo;
                        }
                        if (!demo.startsWith("/demos")) {
                            demo = "/demos" + demo;
                        }
                        try (final InputStream inputStream = MainMenu.class.getResourceAsStream(demo)) {
                            final JSONObject json = new JSONObject(new JSONTokener(inputStream));
                            final JSONObject jsonDemo = json.getJSONObject("Demo");
                            final String demoName = jsonDemo.getString("Name");
                            menuItem = new JMenuItem(demoName);
                            menuItem.addActionListener(e -> MiscLoading.loadDemo(jsonDemo));
                            menu.add(menuItem);
                        }
                        catch (JSONException e2) {
                            System.err.println("Warning: JSON parsing error for demo file: " + demo);
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        if (SettingsManager.devMode && SettingsNetwork.getActiveGameId() == 0) {
            menu = new JMenu("Developer");
            this.add(menu);
            menuItem = new JMenuItem("Compile Game (Debug)");
            menuItem.addActionListener(al);
            menu.add(menuItem);
            menuItem = new JMenuItem("Expanded Description");
            menuItem.addActionListener(al);
            menu.add(menuItem);
            menuItem = new JMenuItem("Metadata Description");
            menuItem.addActionListener(al);
            menu.add(menuItem);
            menuItem = new JMenuItem("Generate Symbols");
            menuItem.setAccelerator(KeyStroke.getKeyStroke(71, 512));
            menuItem.addActionListener(al);
            menu.add(menuItem);
            menu.addSeparator();
            menuItem = new JMenuItem("Print Board Graph");
            menuItem.addActionListener(al);
            menu.add(menuItem);
            menuItem = new JMenuItem("Print Trajectories");
            menuItem.addActionListener(al);
            menu.add(menuItem);
            menu.addSeparator();
            menuItem = new JMenuItem("Jump to Move");
            menuItem.addActionListener(al);
            menu.add(menuItem);
            cbMenuItem = new JCheckBoxMenuItem("Show Board Shape");
            cbMenuItem.setSelected(SettingsVC.showBoardShape);
            cbMenuItem.addItemListener(il);
            menu.add(cbMenuItem);
            menu.addSeparator();
            cbMenuItem = new JCheckBoxMenuItem("Show dev tooltip");
            cbMenuItem.setSelected(SettingsDesktop.cursorTooltipDev);
            cbMenuItem.addItemListener(il);
            menu.add(cbMenuItem);
            menu.addSeparator();
            if (DesktopApp.devJar) {
                menuItem = new JMenuItem("Export Thumbnails");
                menuItem.addActionListener(al);
                menu.add(menuItem);
                menuItem = new JMenuItem("Export All Thumbnails");
                menuItem.addActionListener(al);
                menu.add(menuItem);
                menuItem = new JMenuItem("Export Board Thumbnail");
                menuItem.addActionListener(al);
                menu.add(menuItem);
                menuItem = new JMenuItem("Export All Board Thumbnails");
                menuItem.addActionListener(al);
                menu.add(menuItem);
                menu.addSeparator();
            }
            SettingsManager.swapRule = ContextSnapshot.getContext().game().usesSwapRule();
            SettingsManager.noRepetition = ContextSnapshot.getContext().game().repetitionType() == RepetitionType.InGame;
            SettingsManager.noRepetitionWithinTurn = ContextSnapshot.getContext().game().repetitionType() == RepetitionType.InTurn;
            if (SettingsNetwork.getActiveGameId() == 0) {
                cbMenuItem = new JCheckBoxMenuItem("Swap Rule");
                cbMenuItem.setAccelerator(KeyStroke.getKeyStroke(74, 64));
                cbMenuItem.setSelected(SettingsManager.swapRule);
                cbMenuItem.addItemListener(il);
                menu.add(cbMenuItem);
            }
            if (SettingsNetwork.getActiveGameId() == 0) {
                cbMenuItem = new JCheckBoxMenuItem("No Repetition Of Game State");
                cbMenuItem.setAccelerator(KeyStroke.getKeyStroke(78, 64));
                cbMenuItem.setSelected(SettingsManager.noRepetition);
                cbMenuItem.addItemListener(il);
                menu.add(cbMenuItem);
            }
            if (SettingsNetwork.getActiveGameId() == 0) {
                cbMenuItem = new JCheckBoxMenuItem("No Repetition Within A Turn");
                cbMenuItem.setAccelerator(KeyStroke.getKeyStroke(87, 64));
                cbMenuItem.setSelected(SettingsManager.noRepetitionWithinTurn);
                cbMenuItem.addItemListener(il);
                menu.add(cbMenuItem);
            }
            if (SettingsNetwork.getActiveGameId() == 0 && SandboxUtil.isSandboxAllowed(Manager.ref().context().game()).isEmpty()) {
                menu.addSeparator();
                cbMenuItem = new JCheckBoxMenuItem("Sandbox Mode (Beta)");
                cbMenuItem.setSelected(SettingsVC.sandboxMode);
                cbMenuItem.addItemListener(il);
                menu.add(cbMenuItem);
            }
            menu.addSeparator();
            cbMenuItem = new JCheckBoxMenuItem("Show Cell Indices");
            cbMenuItem.setAccelerator(KeyStroke.getKeyStroke(73, 512));
            cbMenuItem.setSelected(SettingsVC.showCellIndices);
            cbMenuItem.addItemListener(il);
            menu.add(cbMenuItem);
            cbMenuItem = new JCheckBoxMenuItem("Show Edge Indices");
            cbMenuItem.setAccelerator(KeyStroke.getKeyStroke(69, 512));
            cbMenuItem.setSelected(SettingsVC.showEdgeIndices);
            cbMenuItem.addItemListener(il);
            menu.add(cbMenuItem);
            cbMenuItem = new JCheckBoxMenuItem("Show Vertex Indices");
            cbMenuItem.setAccelerator(KeyStroke.getKeyStroke(70, 512));
            cbMenuItem.setSelected(SettingsVC.showVertexIndices);
            cbMenuItem.addItemListener(il);
            menu.add(cbMenuItem);
            menu.addSeparator();
            cbMenuItem = new JCheckBoxMenuItem("Show Cell Coordinates");
            cbMenuItem.setAccelerator(KeyStroke.getKeyStroke(67, 512));
            cbMenuItem.setSelected(SettingsVC.showCellCoordinates);
            cbMenuItem.addItemListener(il);
            menu.add(cbMenuItem);
            cbMenuItem = new JCheckBoxMenuItem("Show Edge Coordinates");
            cbMenuItem.setSelected(SettingsVC.showEdgeCoordinates);
            cbMenuItem.setAccelerator(KeyStroke.getKeyStroke(66, 512));
            cbMenuItem.addItemListener(il);
            menu.add(cbMenuItem);
            cbMenuItem = new JCheckBoxMenuItem("Show Vertex Coordinates");
            cbMenuItem.setAccelerator(KeyStroke.getKeyStroke(87, 512));
            cbMenuItem.setSelected(SettingsVC.showVertexCoordinates);
            cbMenuItem.addItemListener(il);
            menu.add(cbMenuItem);
            menu.addSeparator();
            menuItem = new JMenuItem("Evaluate Heuristic");
            menuItem.addActionListener(al);
            menu.add(menuItem);
            menu.addSeparator();
            menuItem = new JMenuItem("More Developer Options");
            menuItem.addActionListener(al);
            menu.add(menuItem);
        }
        menu = new JMenu("View");
        this.add(menu);
        cbMenuItem = new JCheckBoxMenuItem("Show Board");
        cbMenuItem.setAccelerator(KeyStroke.getKeyStroke(66, 64));
        cbMenuItem.setSelected(SettingsManager.showBoard);
        cbMenuItem.addItemListener(il);
        menu.add(cbMenuItem);
        cbMenuItem = new JCheckBoxMenuItem("Show Pieces");
        cbMenuItem.setAccelerator(KeyStroke.getKeyStroke(80, 64));
        cbMenuItem.setSelected(SettingsManager.showPieces);
        cbMenuItem.addItemListener(il);
        menu.add(cbMenuItem);
        cbMenuItem = new JCheckBoxMenuItem("Show Graph");
        cbMenuItem.setAccelerator(KeyStroke.getKeyStroke(71, 64));
        cbMenuItem.setSelected(SettingsManager.showGraph);
        cbMenuItem.addItemListener(il);
        menu.add(cbMenuItem);
        cbMenuItem = new JCheckBoxMenuItem("Show Cell Connections");
        cbMenuItem.setAccelerator(KeyStroke.getKeyStroke(68, 64));
        cbMenuItem.setSelected(SettingsManager.showConnections);
        cbMenuItem.addItemListener(il);
        menu.add(cbMenuItem);
        menu.addSeparator();
        cbMenuItem = new JCheckBoxMenuItem("Show Legal Moves");
        cbMenuItem.setAccelerator(KeyStroke.getKeyStroke(77, 512));
        cbMenuItem.setSelected(SettingsVC.showPossibleMoves);
        cbMenuItem.addItemListener(il);
        menu.add(cbMenuItem);
        cbMenuItem = new JCheckBoxMenuItem("Show Last Move");
        cbMenuItem.setAccelerator(KeyStroke.getKeyStroke(76, 512));
        cbMenuItem.setSelected(SettingsManager.showLastMove);
        cbMenuItem.addItemListener(il);
        menu.add(cbMenuItem);
        cbMenuItem = new JCheckBoxMenuItem("Show Ending Moves");
        cbMenuItem.setSelected(SettingsManager.showEndingMove);
        cbMenuItem.addItemListener(il);
        menu.add(cbMenuItem);
        menu.addSeparator();
        cbMenuItem = new JCheckBoxMenuItem("Show Indices");
        cbMenuItem.setAccelerator(KeyStroke.getKeyStroke(73, 128));
        cbMenuItem.setSelected(SettingsVC.showIndices);
        cbMenuItem.addItemListener(il);
        menu.add(cbMenuItem);
        cbMenuItem = new JCheckBoxMenuItem("Show Coordinates");
        cbMenuItem.setAccelerator(KeyStroke.getKeyStroke(67, 128));
        cbMenuItem.setSelected(SettingsVC.showCoordinates);
        cbMenuItem.addItemListener(il);
        menu.add(cbMenuItem);
        if (SettingsNetwork.getActiveGameId() == 0) {
            menu.addSeparator();
            cbMenuItem = new JCheckBoxMenuItem("Show AI Distribution");
            cbMenuItem.setAccelerator(KeyStroke.getKeyStroke(65, 512));
            cbMenuItem.setSelected(SettingsManager.showAIDistribution);
            cbMenuItem.addItemListener(il);
            menu.add(cbMenuItem);
        }
        this.submenu = new JMenu("Pick Tracks to show");
        if (!ContextSnapshot.getContext().board().tracks().isEmpty()) {
            menu.addSeparator();
            this.submenu = new JMenu("Show Tracks");
            for (int trackNumber = 0; trackNumber < ContextSnapshot.getContext().board().tracks().size(); ++trackNumber) {
                final Track track = ContextSnapshot.getContext().board().tracks().get(trackNumber);
                cbMenuItem = new JCheckBoxMenuItem("Show Track " + track.name());
                boolean trackFound = false;
                for (int j = 0; j < SettingsVC.trackNames.size(); ++j) {
                    if (cbMenuItem.getText().equals(SettingsVC.trackNames.get(j))) {
                        cbMenuItem.setSelected(SettingsVC.trackShown.get(j));
                        trackFound = true;
                        break;
                    }
                }
                if (!trackFound) {
                    SettingsVC.trackNames.add(cbMenuItem.getText());
                    SettingsVC.trackShown.add(false);
                }
                if (trackNumber < 10) {
                    cbMenuItem.setAccelerator(KeyStroke.getKeyStroke((char)(trackNumber + 48), 64));
                }
                cbMenuItem.addItemListener(il);
                this.submenu.add(cbMenuItem);
            }
            menu.add(this.submenu);
        }
        if (SettingsNetwork.getActiveGameId() == 0) {
            menu.addSeparator();
            menuItem = new JMenuItem("View SVG");
            menuItem.setAccelerator(KeyStroke.getKeyStroke(86, 512));
            menuItem.addActionListener(al);
            menu.add(menuItem);
            menuItem = new JMenuItem("Load SVG");
            menuItem.setAccelerator(KeyStroke.getKeyStroke(85, 512));
            menuItem.addActionListener(al);
            menu.add(menuItem);
        }
        menu = new JMenu("Help");
        this.add(menu);
        menuItem = new JMenuItem("About");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(72, 128));
        menuItem.addActionListener(al);
        menu.add(menuItem);
    }
    
    public static void updateOptionsMenu(final Context context, final JMenu optionsMenu) {
        if (context.isAMatch()) {
            return;
        }
        if (optionsMenu != null) {
            optionsMenu.removeAll();
            final GameOptions gameOptions = context.game().description().gameOptions();
            final List<String> currentOptions = gameOptions.allOptionStrings(SettingsManager.userSelections.selectedOptionStrings());
            for (int cat = 0; cat < gameOptions.numCategories(); ++cat) {
                final List<Option> options = gameOptions.categories().get(cat).options();
                if (!options.isEmpty()) {
                    final List<String> headings = options.get(0).menuHeadings();
                    if (headings.size() < 2) {
                        System.out.println("** Not enough headings for menu option group: " + headings);
                        return;
                    }
                    final JMenu submenu = new JMenu(headings.get(0));
                    optionsMenu.add(submenu);
                    final ButtonGroup group = new ButtonGroup();
                    for (final Option option : options) {
                        if (option.menuHeadings().size() < 2) {
                            System.out.println("** Not enough headings for menu option: " + option.menuHeadings());
                            return;
                        }
                        final JRadioButtonMenuItem rbMenuItem = new JRadioButtonMenuItem(option.menuHeadings().get(1));
                        rbMenuItem.setSelected(currentOptions.contains(StringRoutines.join("/", option.menuHeadings())));
                        rbMenuItem.addItemListener(DesktopApp.playerApp());
                        group.add(rbMenuItem);
                        submenu.add(rbMenuItem);
                    }
                }
            }
            if (SettingsManager.userSelections.ruleset() == -1) {
                SettingsManager.userSelections.setRuleset(context.game().description().autoSelectRuleset(currentOptions));
            }
            final List<Ruleset> rulesets = context.game().description().rulesets();
            if (rulesets != null && !rulesets.isEmpty()) {
                optionsMenu.addSeparator();
                final ButtonGroup rulesetGroup = new ButtonGroup();
                for (int rs = 0; rs < rulesets.size(); ++rs) {
                    final Ruleset ruleset = rulesets.get(rs);
                    if (!ruleset.optionSettings().isEmpty()) {
                        final JRadioButtonMenuItem rbMenuItem2 = new JRadioButtonMenuItem(ruleset.heading());
                        rbMenuItem2.setSelected(SettingsManager.userSelections.ruleset() == rs);
                        rbMenuItem2.addItemListener(DesktopApp.playerApp());
                        rulesetGroup.add(rbMenuItem2);
                        optionsMenu.add(rbMenuItem2);
                    }
                }
            }
        }
    }
    
    private static String[] findDemos() {
        String[] choices = FileHandling.getResourceListing(MainMenu.class, "demos/", ".json");
        if (choices == null) {
            try {
                final URL url = MainMenu.class.getResource("/demos/Hnefatafl - Common.json");
                String path = new File(url.toURI()).getPath();
                path = path.substring(0, path.length() - "Hnefatafl - Common.json".length());
                final List<String> names = new ArrayList<>();
                visitFindDemos(path, names);
                Collections.sort(names);
                choices = names.toArray(new String[0]);
            }
            catch (URISyntaxException exception) {
                exception.printStackTrace();
            }
        }
        return choices;
    }
    
    private static void visitFindDemos(final String path, final List<String> names) {
        final File root = new File(path);
        final File[] list = root.listFiles();
        if (list == null) {
            return;
        }
        for (final File file : list) {
            if (file.isDirectory()) {
                visitFindDemos(path + file.getName() + File.separator, names);
            }
            else if (file.getName().contains(".json")) {
                final String name = file.getName();
                names.add(path.substring(path.indexOf(File.separator + "demos" + File.separator)) + name);
            }
        }
    }
    
    public static void updateRecentGames(final String gameName) {
        String GameMenuName = gameName;
        if (!DesktopApp.loadedFromMemory()) {
            GameMenuName = Manager.savedLudName();
        }
        int gameAlreadyIncluded = -1;
        for (int i = 0; i < MainMenu.recentGames.length; ++i) {
            if (MainMenu.recentGames[i] != null && MainMenu.recentGames[i].equals(GameMenuName)) {
                gameAlreadyIncluded = i;
            }
        }
        if (gameAlreadyIncluded == -1) {
            gameAlreadyIncluded = MainMenu.recentGames.length - 1;
        }
        if (gameAlreadyIncluded >= 0)
            System.arraycopy(MainMenu.recentGames, 0, MainMenu.recentGames, 1, gameAlreadyIncluded);
        MainMenu.recentGames[0] = GameMenuName;
    }
    
    static {
        MainMenu.recentGames = new String[] { null, null, null, null, null, null, null, null, null, null };
    }
}
