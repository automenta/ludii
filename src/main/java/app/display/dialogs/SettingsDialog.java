// 
// Decompiled by Procyon v0.5.36
// 

package app.display.dialogs;

import app.DesktopApp;
import app.display.MainWindow;
import app.display.dialogs.util.DialogUtil;
import app.display.dialogs.util.MaxLengthTextDocument;
import app.display.util.GUIUtil;
import app.display.util.GraphicsCache;
import app.menu.MainMenu;
import app.utils.SettingsDesktop;
import main.SettingsGeneral;
import manager.Manager;
import manager.ai.AIMenuName;
import manager.ai.AIUtil;
import manager.network.SettingsNetwork;
import manager.utils.ContextSnapshot;
import manager.utils.SettingsManager;
import metadata.graphics.util.BoardGraphicsType;
import org.json.JSONObject;
import util.Context;
import util.SettingsColour;
import util.SettingsVC;
import util.state.State;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class SettingsDialog extends JDialog
{
    private static final long serialVersionUID = 1L;
    private final JPanel colourPanel;
    final JTextField textFieldPlayerName1;
    final JTextField textFieldPlayerName2;
    final JTextField textFieldPlayerName3;
    final JTextField textFieldPlayerName4;
    final JTextField textFieldPlayerName5;
    final JTextField textFieldPlayerName6;
    final JTextField textFieldPlayerName7;
    final JTextField textFieldPlayerName8;
    final JTextField textFieldThinkingTimeAll;
    private static JTabbedPane tabbedPane;
    private static JPanel playerPanel;
    private static JPanel otherPanel;
    final int maxNameLength = 21;
    static SettingsDialog dialog;
    static boolean ignorePlayerComboBoxEvents;
    JTextField textFieldMaximumNumberOfTurns;
    JTextField textFieldTabFontSize;
    JTextField textFieldEditorFontSize;
    JTextField textFieldTickLength;
    
    public static void createAndShowGUI(final int panelNum) {
        try {
            DialogUtil.initialiseSingletonDialog(SettingsDialog.dialog = new SettingsDialog(panelNum), "Preferences", null);
            SettingsDialog.dialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(final WindowEvent e) {
                    for (int i = 0; i < SettingsColour.getSavedPlayerColourPreferences().size(); ++i) {
                        final SettingsColour.PlayerColourPreference g = SettingsColour.getSavedPlayerColourPreferences().get(i);
                        if (g.gameName().equals(Manager.savedLudName())) {
                            SettingsColour.getSavedPlayerColourPreferences().remove(i);
                            break;
                        }
                    }
                    if (!SettingsColour.originalPlayerColours().equals(SettingsColour.getCustomPlayerColours())) {
                        final SettingsColour.PlayerColourPreference g2 = new SettingsColour.PlayerColourPreference(Manager.savedLudName(), SettingsColour.getCustomPlayerColours());
                        SettingsColour.getSavedPlayerColourPreferences().add(g2);
                    }
                    for (int i = 0; i < SettingsColour.getSavedBoardColourPreferences().size(); ++i) {
                        final SettingsColour.BoardColourPreference g3 = SettingsColour.getSavedBoardColourPreferences().get(i);
                        if (g3.gameName().equals(Manager.savedLudName())) {
                            SettingsColour.getSavedBoardColourPreferences().remove(i);
                            break;
                        }
                    }
                    if (!SettingsColour.getDefaultBoardColour().equals(SettingsColour.getCustomBoardColour())) {
                        final SettingsColour.BoardColourPreference g4 = new SettingsColour.BoardColourPreference(Manager.savedLudName(), SettingsColour.getCustomBoardColour());
                        SettingsColour.getSavedBoardColourPreferences().add(g4);
                    }
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public SettingsDialog(final int panelNum) {
        super(null, ModalityType.DOCUMENT_MODAL);
        this.colourPanel = new JPanel();
        this.setTitle("Settings");
        final Context context = ContextSnapshot.getContext();
        this.setBounds(100, 100, 468, 800);
        this.getContentPane().setLayout(new BorderLayout(0, 0));
        SettingsDialog.tabbedPane = new JTabbedPane(1);
        this.getContentPane().add(SettingsDialog.tabbedPane, "Center");
        SettingsDialog.tabbedPane.addTab("Colour", null, this.colourPanel, null);
        this.colourPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        final JButton buttonP1 = new JButton("");
        buttonP1.setBounds(40, 60, 26, 23);
        buttonP1.setBackground(SettingsColour.getCustomPlayerColours()[1]);
        buttonP1.setOpaque(true);
        buttonP1.setBorderPainted(false);
        buttonP1.addActionListener(e -> {
            final Color colour = JColorChooser.showDialog(null, "Choose a color", SettingsColour.getCustomPlayerColours()[1]);
            if (colour != null) {
                SettingsColour.getCustomPlayerColours()[1] = colour;
            }
            Manager.app.repaint();
            GraphicsCache.clearAllCachedImages();
            buttonP1.setBackground(SettingsColour.getCustomPlayerColours()[1]);
        });
        final JButton buttonP2 = new JButton("");
        buttonP2.setBounds(84, 60, 26, 23);
        buttonP2.setOpaque(true);
        buttonP2.setBorderPainted(false);
        if (context.game().players().count() > 1) {
            buttonP2.setBackground(SettingsColour.getCustomPlayerColours()[2]);
            buttonP2.addActionListener(e -> {
                final Color colour = JColorChooser.showDialog(null, "Choose a color", SettingsColour.getCustomPlayerColours()[2]);
                if (colour != null) {
                    SettingsColour.getCustomPlayerColours()[2] = colour;
                }
                Manager.app.repaint();
                GraphicsCache.clearAllCachedImages();
                buttonP2.setBackground(SettingsColour.getCustomPlayerColours()[2]);
            });
        }
        final JButton buttonP3 = new JButton("");
        buttonP3.setBounds(128, 60, 26, 23);
        buttonP3.setOpaque(true);
        buttonP3.setBorderPainted(false);
        if (context.game().players().count() > 2) {
            buttonP3.setBackground(SettingsColour.getCustomPlayerColours()[3]);
            buttonP3.addActionListener(e -> {
                final Color colour = JColorChooser.showDialog(null, "Choose a color", SettingsColour.getCustomPlayerColours()[3]);
                if (colour != null) {
                    SettingsColour.getCustomPlayerColours()[3] = colour;
                }
                Manager.app.repaint();
                GraphicsCache.clearAllCachedImages();
                buttonP3.setBackground(SettingsColour.getCustomPlayerColours()[3]);
            });
        }
        final JButton buttonP4 = new JButton("");
        buttonP4.setBounds(172, 60, 26, 23);
        buttonP4.setOpaque(true);
        buttonP4.setBorderPainted(false);
        if (context.game().players().count() > 3) {
            buttonP4.setBackground(SettingsColour.getCustomPlayerColours()[4]);
            buttonP4.addActionListener(e -> {
                final Color colour = JColorChooser.showDialog(null, "Choose a color", SettingsColour.getCustomPlayerColours()[4]);
                if (colour != null) {
                    SettingsColour.getCustomPlayerColours()[4] = colour;
                }
                Manager.app.repaint();
                GraphicsCache.clearAllCachedImages();
                buttonP4.setBackground(SettingsColour.getCustomPlayerColours()[4]);
            });
        }
        final JButton buttonP5 = new JButton("");
        buttonP5.setBounds(216, 60, 26, 23);
        buttonP5.setOpaque(true);
        buttonP5.setBorderPainted(false);
        if (context.game().players().count() > 4) {
            buttonP5.setBackground(SettingsColour.getCustomPlayerColours()[5]);
            buttonP5.addActionListener(e -> {
                final Color colour = JColorChooser.showDialog(null, "Choose a color", SettingsColour.getCustomPlayerColours()[5]);
                if (colour != null) {
                    SettingsColour.getCustomPlayerColours()[5] = colour;
                }
                Manager.app.repaint();
                GraphicsCache.clearAllCachedImages();
                buttonP5.setBackground(SettingsColour.getCustomPlayerColours()[5]);
            });
        }
        final JButton buttonP6 = new JButton("");
        buttonP6.setBounds(260, 60, 26, 23);
        buttonP6.setOpaque(true);
        buttonP6.setBorderPainted(false);
        if (context.game().players().count() > 5) {
            buttonP6.setBackground(SettingsColour.getCustomPlayerColours()[6]);
            buttonP6.addActionListener(e -> {
                final Color colour = JColorChooser.showDialog(null, "Choose a color", SettingsColour.getCustomPlayerColours()[6]);
                if (colour != null) {
                    SettingsColour.getCustomPlayerColours()[6] = colour;
                }
                Manager.app.repaint();
                GraphicsCache.clearAllCachedImages();
                buttonP6.setBackground(SettingsColour.getCustomPlayerColours()[6]);
            });
        }
        final JButton buttonP7 = new JButton("");
        buttonP7.setBounds(304, 60, 26, 23);
        buttonP7.setOpaque(true);
        buttonP7.setBorderPainted(false);
        if (context.game().players().count() > 6) {
            buttonP7.setBackground(SettingsColour.getCustomPlayerColours()[7]);
            buttonP7.addActionListener(e -> {
                final Color colour = JColorChooser.showDialog(null, "Choose a color", SettingsColour.getCustomPlayerColours()[7]);
                if (colour != null) {
                    SettingsColour.getCustomPlayerColours()[7] = colour;
                }
                Manager.app.repaint();
                GraphicsCache.clearAllCachedImages();
                buttonP7.setBackground(SettingsColour.getCustomPlayerColours()[7]);
            });
        }
        final JButton buttonP8 = new JButton("");
        buttonP8.setBounds(348, 60, 26, 23);
        buttonP8.setOpaque(true);
        buttonP8.setBorderPainted(false);
        if (context.game().players().count() > 7) {
            buttonP8.setBackground(SettingsColour.getCustomPlayerColours()[8]);
            buttonP8.addActionListener(e -> {
                final Color colour = JColorChooser.showDialog(null, "Choose a color", SettingsColour.getCustomPlayerColours()[8]);
                if (colour != null) {
                    SettingsColour.getCustomPlayerColours()[8] = colour;
                }
                Manager.app.repaint();
                GraphicsCache.clearAllCachedImages();
                buttonP8.setBackground(SettingsColour.getCustomPlayerColours()[8]);
            });
        }
        boolean player9Active = false;
        boolean player0Active = false;
        for (final game.equipment.component.Component c : context.components()) {
            if (c != null && c.owner() == context.game().players().count() + 1) {
                player9Active = true;
                break;
            }
        }
        for (final game.equipment.component.Component c : context.components()) {
            if (c != null && c.owner() == 0) {
                player0Active = true;
                break;
            }
        }
        final JButton buttonP9 = new JButton("");
        buttonP9.setBounds(226, 100, 26, 23);
        buttonP9.setOpaque(true);
        buttonP9.setBorderPainted(false);
        if (player9Active) {
            buttonP9.setBackground(SettingsColour.getCustomPlayerColours()[17]);
            buttonP9.addActionListener(e -> {
                final Color colour = JColorChooser.showDialog(null, "Choose a color", SettingsColour.getCustomPlayerColours()[17]);
                if (colour != null) {
                    SettingsColour.getCustomPlayerColours()[17] = colour;
                }
                Manager.app.repaint();
                GraphicsCache.clearAllCachedImages();
                buttonP9.setBackground(SettingsColour.getCustomPlayerColours()[17]);
            });
        }
        final JButton buttonP10 = new JButton("");
        buttonP10.setBounds(182, 100, 26, 23);
        buttonP10.setOpaque(true);
        buttonP10.setBorderPainted(false);
        if (player0Active) {
            buttonP10.setBackground(SettingsColour.getCustomPlayerColours()[0]);
            buttonP10.addActionListener(e -> {
                final Color colour = JColorChooser.showDialog(null, "Choose a color", SettingsColour.getCustomPlayerColours()[0]);
                if (colour != null) {
                    SettingsColour.getCustomPlayerColours()[0] = colour;
                }
                Manager.app.repaint();
                GraphicsCache.clearAllCachedImages();
                buttonP10.setBackground(SettingsColour.getCustomPlayerColours()[0]);
            });
        }
        final JLabel lblPlayerColours = new JLabel("Player Colours");
        lblPlayerColours.setBounds(30, 28, 163, 21);
        lblPlayerColours.setFont(new Font("Dialog", 1, 16));
        final JButton buttonPhase1 = new JButton("");
        buttonPhase1.setBounds(156, 191, 26, 23);
        buttonPhase1.setOpaque(true);
        buttonPhase1.setBorderPainted(false);
        if (SettingsColour.getDefaultBoardColour()[BoardGraphicsType.Phase0.value()] != null) {
            buttonPhase1.addActionListener(e -> {
                Color colour = SettingsColour.getDefaultBoardColour()[BoardGraphicsType.Phase0.value()];
                if (SettingsColour.getCustomBoardColour()[BoardGraphicsType.Phase0.value()] != null) {
                    colour = SettingsColour.getCustomBoardColour()[BoardGraphicsType.Phase0.value()];
                }
                colour = JColorChooser.showDialog(null, "Choose a color", colour);
                if (colour != null) {
                    SettingsColour.getCustomBoardColour()[BoardGraphicsType.Phase0.value()] = colour;
                }
                GraphicsCache.boardImage = null;
                Manager.app.repaint();
                if (SettingsColour.getCustomBoardColour()[BoardGraphicsType.Phase0.value()] == null) {
                    buttonPhase1.setBackground(SettingsColour.getDefaultBoardColour()[BoardGraphicsType.Phase0.value()]);
                }
                else if (SettingsColour.getDefaultBoardColour()[BoardGraphicsType.Phase0.value()] != null) {
                    buttonPhase1.setBackground(SettingsColour.getCustomBoardColour()[BoardGraphicsType.Phase0.value()]);
                }
            });
        }
        final JButton buttonPhase2 = new JButton("");
        buttonPhase2.setBounds(200, 191, 26, 23);
        buttonPhase2.setOpaque(true);
        buttonPhase2.setBorderPainted(false);
        if (SettingsColour.getDefaultBoardColour()[BoardGraphicsType.Phase1.value()] != null) {
            buttonPhase2.addActionListener(e -> {
                Color colour = SettingsColour.getDefaultBoardColour()[BoardGraphicsType.Phase1.value()];
                if (SettingsColour.getCustomBoardColour()[BoardGraphicsType.Phase1.value()] != null) {
                    colour = SettingsColour.getCustomBoardColour()[BoardGraphicsType.Phase1.value()];
                }
                colour = JColorChooser.showDialog(null, "Choose a color", colour);
                if (colour != null) {
                    SettingsColour.getCustomBoardColour()[BoardGraphicsType.Phase1.value()] = colour;
                }
                GraphicsCache.boardImage = null;
                Manager.app.repaint();
                if (SettingsColour.getCustomBoardColour()[BoardGraphicsType.Phase1.value()] == null) {
                    buttonPhase2.setBackground(SettingsColour.getDefaultBoardColour()[BoardGraphicsType.Phase1.value()]);
                }
                else if (SettingsColour.getDefaultBoardColour()[BoardGraphicsType.Phase1.value()] != null) {
                    buttonPhase2.setBackground(SettingsColour.getCustomBoardColour()[BoardGraphicsType.Phase1.value()]);
                }
            });
        }
        final JButton buttonPhase3 = new JButton("");
        buttonPhase3.setBounds(244, 191, 26, 23);
        buttonPhase3.setOpaque(true);
        buttonPhase3.setBorderPainted(false);
        if (SettingsColour.getDefaultBoardColour()[BoardGraphicsType.Phase2.value()] != null) {
            buttonPhase3.addActionListener(e -> {
                Color colour = SettingsColour.getDefaultBoardColour()[BoardGraphicsType.Phase2.value()];
                if (SettingsColour.getCustomBoardColour()[BoardGraphicsType.Phase2.value()] != null) {
                    colour = SettingsColour.getCustomBoardColour()[BoardGraphicsType.Phase2.value()];
                }
                colour = JColorChooser.showDialog(null, "Choose a color", colour);
                if (colour != null) {
                    SettingsColour.getCustomBoardColour()[BoardGraphicsType.Phase2.value()] = colour;
                }
                GraphicsCache.boardImage = null;
                Manager.app.repaint();
                if (SettingsColour.getCustomBoardColour()[BoardGraphicsType.Phase2.value()] == null) {
                    buttonPhase3.setBackground(SettingsColour.getDefaultBoardColour()[BoardGraphicsType.Phase2.value()]);
                }
                else if (SettingsColour.getDefaultBoardColour()[BoardGraphicsType.Phase2.value()] != null) {
                    buttonPhase3.setBackground(SettingsColour.getCustomBoardColour()[BoardGraphicsType.Phase2.value()]);
                }
            });
        }
        final JButton buttonPhase4 = new JButton("");
        buttonPhase4.setBounds(288, 191, 26, 23);
        buttonPhase4.setOpaque(true);
        buttonPhase4.setBorderPainted(false);
        if (SettingsColour.getDefaultBoardColour()[BoardGraphicsType.Phase3.value()] != null) {
            buttonPhase4.addActionListener(e -> {
                Color colour = SettingsColour.getDefaultBoardColour()[BoardGraphicsType.Phase3.value()];
                if (SettingsColour.getCustomBoardColour()[BoardGraphicsType.Phase3.value()] != null) {
                    colour = SettingsColour.getCustomBoardColour()[BoardGraphicsType.Phase3.value()];
                }
                colour = JColorChooser.showDialog(null, "Choose a color", colour);
                if (colour != null) {
                    SettingsColour.getCustomBoardColour()[BoardGraphicsType.Phase3.value()] = colour;
                }
                GraphicsCache.boardImage = null;
                Manager.app.repaint();
                if (SettingsColour.getCustomBoardColour()[BoardGraphicsType.Phase3.value()] == null) {
                    buttonPhase4.setBackground(SettingsColour.getDefaultBoardColour()[BoardGraphicsType.Phase3.value()]);
                }
                else if (SettingsColour.getDefaultBoardColour()[BoardGraphicsType.Phase3.value()] != null) {
                    buttonPhase4.setBackground(SettingsColour.getCustomBoardColour()[BoardGraphicsType.Phase3.value()]);
                }
            });
        }
        final JButton buttonSymbol1 = new JButton("");
        buttonSymbol1.setBounds(156, 273, 26, 23);
        buttonSymbol1.setOpaque(true);
        buttonSymbol1.setBorderPainted(false);
        if (SettingsColour.getDefaultBoardColour()[BoardGraphicsType.Symbols.value()] != null) {
            buttonSymbol1.addActionListener(e -> {
                Color colour = SettingsColour.getDefaultBoardColour()[BoardGraphicsType.Symbols.value()];
                if (SettingsColour.getCustomBoardColour()[BoardGraphicsType.Symbols.value()] != null) {
                    colour = SettingsColour.getCustomBoardColour()[BoardGraphicsType.Symbols.value()];
                }
                colour = JColorChooser.showDialog(null, "Choose a color", colour);
                if (colour != null) {
                    SettingsColour.getCustomBoardColour()[BoardGraphicsType.Symbols.value()] = colour;
                }
                GraphicsCache.boardImage = null;
                Manager.app.repaint();
                if (SettingsColour.getCustomBoardColour()[BoardGraphicsType.Symbols.value()] == null) {
                    buttonSymbol1.setBackground(SettingsColour.getDefaultBoardColour()[BoardGraphicsType.Symbols.value()]);
                }
                else {
                    buttonSymbol1.setBackground(SettingsColour.getCustomBoardColour()[BoardGraphicsType.Symbols.value()]);
                }
            });
        }
        final JButton buttonEdge1 = new JButton("");
        buttonEdge1.setBounds(156, 232, 26, 23);
        buttonEdge1.setOpaque(true);
        buttonEdge1.setBorderPainted(false);
        if (SettingsColour.getDefaultBoardColour()[BoardGraphicsType.InnerEdges.value()] != null) {
            buttonEdge1.addActionListener(e -> {
                Color colour = SettingsColour.getDefaultBoardColour()[BoardGraphicsType.InnerEdges.value()];
                if (SettingsColour.getCustomBoardColour()[BoardGraphicsType.InnerEdges.value()] != null) {
                    colour = SettingsColour.getCustomBoardColour()[BoardGraphicsType.InnerEdges.value()];
                }
                colour = JColorChooser.showDialog(null, "Choose a color", colour);
                if (colour != null) {
                    SettingsColour.getCustomBoardColour()[BoardGraphicsType.InnerEdges.value()] = colour;
                }
                GraphicsCache.boardImage = null;
                Manager.app.repaint();
                if (SettingsColour.getCustomBoardColour()[BoardGraphicsType.InnerEdges.value()] == null) {
                    buttonEdge1.setBackground(SettingsColour.getDefaultBoardColour()[BoardGraphicsType.InnerEdges.value()]);
                }
                else if (SettingsColour.getDefaultBoardColour()[BoardGraphicsType.InnerEdges.value()] != null) {
                    buttonEdge1.setBackground(SettingsColour.getCustomBoardColour()[BoardGraphicsType.InnerEdges.value()]);
                }
            });
        }
        final JButton buttonEdge2 = new JButton("");
        buttonEdge2.setBounds(200, 232, 26, 23);
        buttonEdge2.setOpaque(true);
        buttonEdge2.setBorderPainted(false);
        if (SettingsColour.getDefaultBoardColour()[BoardGraphicsType.OuterEdges.value()] != null) {
            buttonEdge2.addActionListener(e -> {
                Color colour = SettingsColour.getDefaultBoardColour()[BoardGraphicsType.OuterEdges.value()];
                if (SettingsColour.getCustomBoardColour()[BoardGraphicsType.OuterEdges.value()] != null) {
                    colour = SettingsColour.getCustomBoardColour()[BoardGraphicsType.OuterEdges.value()];
                }
                colour = JColorChooser.showDialog(null, "Choose a color", colour);
                if (colour != null) {
                    SettingsColour.getCustomBoardColour()[BoardGraphicsType.OuterEdges.value()] = colour;
                }
                GraphicsCache.boardImage = null;
                Manager.app.repaint();
                if (SettingsColour.getCustomBoardColour()[BoardGraphicsType.OuterEdges.value()] == null) {
                    buttonEdge2.setBackground(SettingsColour.getDefaultBoardColour()[BoardGraphicsType.OuterEdges.value()]);
                }
                else if (SettingsColour.getDefaultBoardColour()[BoardGraphicsType.OuterEdges.value()] != null) {
                    buttonEdge2.setBackground(SettingsColour.getCustomBoardColour()[BoardGraphicsType.OuterEdges.value()]);
                }
            });
        }
        final JComboBox<String> comboBox = new JComboBox<>();
        comboBox.setBounds(30, 472, 144, 22);
        final JLabel lblReset = new JLabel("Reset to Defaults");
        lblReset.setBounds(30, 367, 122, 17);
        lblReset.setFont(new Font("Tahoma", 0, 14));
        final JLabel lblPhases = new JLabel("Phases");
        lblPhases.setBounds(30, 191, 63, 17);
        lblPhases.setFont(new Font("Tahoma", 0, 14));
        final JLabel lblBoardColours = new JLabel("Board Colours");
        lblBoardColours.setBounds(30, 152, 155, 21);
        lblBoardColours.setFont(new Font("Dialog", 1, 16));
        final JButton buttonReset = new JButton("");
        buttonReset.setBounds(162, 367, 26, 23);
        buttonReset.addActionListener(e -> {
            if (context.game().players().count() > 0) {
                SettingsColour.getCustomPlayerColours()[1] = SettingsColour.getDefaultPlayerColours()[1];
                buttonP1.setBackground(SettingsColour.getCustomPlayerColours()[1]);
            }
            if (context.game().players().count() > 1) {
                SettingsColour.getCustomPlayerColours()[2] = SettingsColour.getDefaultPlayerColours()[2];
                buttonP2.setBackground(SettingsColour.getCustomPlayerColours()[2]);
            }
            if (context.game().players().count() > 2) {
                SettingsColour.getCustomPlayerColours()[3] = SettingsColour.getDefaultPlayerColours()[3];
                buttonP3.setBackground(SettingsColour.getCustomPlayerColours()[3]);
            }
            if (context.game().players().count() > 3) {
                SettingsColour.getCustomPlayerColours()[4] = SettingsColour.getDefaultPlayerColours()[4];
                buttonP4.setBackground(SettingsColour.getCustomPlayerColours()[4]);
            }
            if (context.game().players().count() > 4) {
                SettingsColour.getCustomPlayerColours()[5] = SettingsColour.getDefaultPlayerColours()[5];
                buttonP5.setBackground(SettingsColour.getCustomPlayerColours()[5]);
            }
            if (context.game().players().count() > 5) {
                SettingsColour.getCustomPlayerColours()[6] = SettingsColour.getDefaultPlayerColours()[6];
                buttonP6.setBackground(SettingsColour.getCustomPlayerColours()[6]);
            }
            if (context.game().players().count() > 6) {
                SettingsColour.getCustomPlayerColours()[7] = SettingsColour.getDefaultPlayerColours()[7];
                buttonP7.setBackground(SettingsColour.getCustomPlayerColours()[7]);
            }
            if (context.game().players().count() > 7) {
                SettingsColour.getCustomPlayerColours()[8] = SettingsColour.getDefaultPlayerColours()[8];
                buttonP8.setBackground(SettingsColour.getCustomPlayerColours()[8]);
            }
            boolean player9Active1 = false;
            boolean player0Active1 = false;
            for (final game.equipment.component.Component c : context.components()) {
                if (c != null && c.owner() == context.game().players().count() + 1) {
                    player9Active1 = true;
                    break;
                }
            }
            for (final game.equipment.component.Component c : context.components()) {
                if (c != null && c.owner() == 0) {
                    player0Active1 = true;
                    break;
                }
            }
            if (player9Active1) {
                SettingsColour.getCustomPlayerColours()[17] = SettingsColour.getDefaultPlayerColours()[17];
                buttonP9.setBackground(SettingsColour.getCustomPlayerColours()[17]);
            }
            if (player0Active1) {
                SettingsColour.getCustomPlayerColours()[0] = SettingsColour.getDefaultPlayerColours()[0];
                buttonP10.setBackground(SettingsColour.getCustomPlayerColours()[0]);
            }
            SettingsColour.getCustomBoardColour()[BoardGraphicsType.InnerEdges.value()] = SettingsColour.getDefaultBoardColour()[BoardGraphicsType.InnerEdges.value()];
            SettingsColour.getCustomBoardColour()[BoardGraphicsType.Phase0.value()] = SettingsColour.getDefaultBoardColour()[BoardGraphicsType.Phase0.value()];
            SettingsColour.getCustomBoardColour()[BoardGraphicsType.Phase1.value()] = SettingsColour.getDefaultBoardColour()[BoardGraphicsType.Phase1.value()];
            SettingsColour.getCustomBoardColour()[BoardGraphicsType.Phase2.value()] = SettingsColour.getDefaultBoardColour()[BoardGraphicsType.Phase2.value()];
            SettingsColour.getCustomBoardColour()[BoardGraphicsType.Phase3.value()] = SettingsColour.getDefaultBoardColour()[BoardGraphicsType.Phase3.value()];
            SettingsColour.getCustomBoardColour()[BoardGraphicsType.Symbols.value()] = SettingsColour.getDefaultBoardColour()[BoardGraphicsType.Symbols.value()];
            SettingsColour.getCustomBoardColour()[BoardGraphicsType.OuterEdges.value()] = SettingsColour.getDefaultBoardColour()[BoardGraphicsType.OuterEdges.value()];
            buttonEdge1.setBackground(SettingsColour.getDefaultBoardColour()[BoardGraphicsType.InnerEdges.value()]);
            buttonPhase1.setBackground(SettingsColour.getDefaultBoardColour()[BoardGraphicsType.Phase0.value()]);
            buttonPhase2.setBackground(SettingsColour.getDefaultBoardColour()[BoardGraphicsType.Phase1.value()]);
            buttonPhase3.setBackground(SettingsColour.getDefaultBoardColour()[BoardGraphicsType.Phase2.value()]);
            buttonPhase4.setBackground(SettingsColour.getDefaultBoardColour()[BoardGraphicsType.Phase3.value()]);
            buttonSymbol1.setBackground(SettingsColour.getDefaultBoardColour()[BoardGraphicsType.Symbols.value()]);
            buttonEdge2.setBackground(SettingsColour.getDefaultBoardColour()[BoardGraphicsType.OuterEdges.value()]);
            Manager.app.repaint();
            GraphicsCache.clearAllCachedImages();
        });
        final JLabel lblEdges = new JLabel("Edges");
        lblEdges.setBounds(30, 232, 63, 17);
        lblEdges.setFont(new Font("Tahoma", 0, 14));
        final JLabel lblSymbols = new JLabel("Symbols");
        lblSymbols.setBounds(30, 273, 210, 17);
        lblSymbols.setFont(new Font("Tahoma", 0, 14));
        final JLabel lblMiscellaneous = new JLabel("Miscellaneous");
        lblMiscellaneous.setBounds(30, 329, 155, 21);
        lblMiscellaneous.setFont(new Font("Dialog", 1, 16));
        final JLabel lblPieceStyles = new JLabel("Piece Style");
        lblPieceStyles.setBounds(30, 432, 155, 21);
        lblPieceStyles.setFont(new Font("Dialog", 1, 16));
        String[] pieceDesigns = context.game().metadata().graphics().pieceFamilies();
        if (pieceDesigns == null) {
            pieceDesigns = new String[0];
        }
        for (final String s : pieceDesigns) {
            comboBox.addItem(s);
        }
        if (!SettingsVC.getPieceFamily(context.game().name()).equals("")) {
            comboBox.setSelectedItem(SettingsVC.getPieceFamily(context.game().name()));
        }
        comboBox.addActionListener(e -> {
            SettingsVC.setPieceFamily(context.game().name(), comboBox.getSelectedItem().toString());
            GraphicsCache.clearAllCachedImages();
            Manager.app.repaint();
        });
        comboBox.setEnabled(comboBox.getItemCount() != 0);
        final JLabel lblNonplayerColours = new JLabel("Non-Player Colours");
        lblNonplayerColours.setBounds(30, 106, 145, 17);
        lblNonplayerColours.setFont(new Font("Dialog", 0, 14));
        this.colourPanel.setLayout(null);
        this.colourPanel.add(lblPlayerColours);
        this.colourPanel.add(buttonP1);
        this.colourPanel.add(buttonP2);
        this.colourPanel.add(buttonP3);
        this.colourPanel.add(buttonP4);
        this.colourPanel.add(lblNonplayerColours);
        this.colourPanel.add(buttonP5);
        this.colourPanel.add(buttonP6);
        this.colourPanel.add(buttonP7);
        this.colourPanel.add(buttonP8);
        this.colourPanel.add(lblBoardColours);
        this.colourPanel.add(lblEdges);
        this.colourPanel.add(lblPhases);
        this.colourPanel.add(lblReset);
        this.colourPanel.add(buttonEdge1);
        this.colourPanel.add(buttonEdge2);
        this.colourPanel.add(buttonSymbol1);
        this.colourPanel.add(buttonPhase1);
        this.colourPanel.add(buttonPhase2);
        this.colourPanel.add(buttonPhase3);
        this.colourPanel.add(buttonP10);
        this.colourPanel.add(buttonP9);
        this.colourPanel.add(buttonPhase4);
        this.colourPanel.add(buttonReset);
        this.colourPanel.add(lblSymbols);
        this.colourPanel.add(lblMiscellaneous);
        this.colourPanel.add(lblPieceStyles);
        this.colourPanel.add(comboBox);
        final JSeparator separator = new JSeparator();
        separator.setBounds(0, 310, 455, 8);
        this.colourPanel.add(separator);
        final JSeparator separator_1 = new JSeparator();
        separator_1.setBounds(0, 413, 455, 8);
        this.colourPanel.add(separator_1);
        final JSeparator separator_2 = new JSeparator();
        separator_2.setBounds(0, 140, 455, 8);
        this.colourPanel.add(separator_2);
        final JCheckBox chckbxAbstractImagesBy = new JCheckBox("Abstract images by default");
        chckbxAbstractImagesBy.setSelected(true);
        chckbxAbstractImagesBy.setBounds(156, 432, 283, 21);
        this.colourPanel.add(chckbxAbstractImagesBy);
        chckbxAbstractImagesBy.setSelected(SettingsVC.abstractPriority);
        chckbxAbstractImagesBy.addActionListener(e -> SettingsVC.abstractPriority = chckbxAbstractImagesBy.isSelected());
        (SettingsDialog.playerPanel = new JPanel()).setBorder(new EmptyBorder(5, 5, 5, 5));
        SettingsDialog.tabbedPane.addTab("Player", null, SettingsDialog.playerPanel, null);
        final int numPlayers = context.game().players().count();
        final State state = context.state();
        (this.textFieldPlayerName1 = new JTextField()).setBounds(139, 81, 190, 20);
        final MaxLengthTextDocument maxLength1 = new MaxLengthTextDocument();
        maxLength1.setMaxChars(21);
        this.textFieldPlayerName1.setDocument(maxLength1);
        this.textFieldPlayerName1.setEnabled(true);
        this.textFieldPlayerName1.setText(DesktopApp.aiSelected()[state.playerToAgent(1)].name());
        (this.textFieldPlayerName2 = new JTextField()).setBounds(139, 119, 190, 20);
        final MaxLengthTextDocument maxLength2 = new MaxLengthTextDocument();
        maxLength2.setMaxChars(21);
        this.textFieldPlayerName2.setDocument(maxLength2);
        this.textFieldPlayerName2.setEnabled(true);
        this.textFieldPlayerName2.setText(DesktopApp.aiSelected()[state.playerToAgent(2)].name());
        (this.textFieldPlayerName3 = new JTextField()).setBounds(139, 157, 190, 20);
        final MaxLengthTextDocument maxLength3 = new MaxLengthTextDocument();
        maxLength3.setMaxChars(21);
        this.textFieldPlayerName3.setDocument(maxLength3);
        this.textFieldPlayerName3.setEnabled(true);
        this.textFieldPlayerName3.setText(DesktopApp.aiSelected()[state.playerToAgent(3)].name());
        (this.textFieldPlayerName4 = new JTextField()).setBounds(139, 195, 190, 20);
        final MaxLengthTextDocument maxLength4 = new MaxLengthTextDocument();
        maxLength4.setMaxChars(21);
        this.textFieldPlayerName4.setDocument(maxLength4);
        this.textFieldPlayerName4.setEnabled(true);
        this.textFieldPlayerName4.setText(DesktopApp.aiSelected()[state.playerToAgent(4)].name());
        (this.textFieldPlayerName5 = new JTextField()).setBounds(139, 233, 190, 20);
        final MaxLengthTextDocument maxLength5 = new MaxLengthTextDocument();
        maxLength5.setMaxChars(21);
        this.textFieldPlayerName5.setDocument(maxLength5);
        this.textFieldPlayerName5.setEnabled(true);
        this.textFieldPlayerName5.setText(DesktopApp.aiSelected()[state.playerToAgent(5)].name());
        (this.textFieldPlayerName6 = new JTextField()).setBounds(139, 271, 190, 20);
        final MaxLengthTextDocument maxLength6 = new MaxLengthTextDocument();
        maxLength6.setMaxChars(21);
        this.textFieldPlayerName6.setDocument(maxLength6);
        this.textFieldPlayerName6.setEnabled(true);
        this.textFieldPlayerName6.setText(DesktopApp.aiSelected()[state.playerToAgent(6)].name());
        (this.textFieldPlayerName7 = new JTextField()).setBounds(139, 309, 190, 20);
        final MaxLengthTextDocument maxLength7 = new MaxLengthTextDocument();
        maxLength7.setMaxChars(21);
        this.textFieldPlayerName7.setDocument(maxLength7);
        this.textFieldPlayerName7.setEnabled(true);
        this.textFieldPlayerName7.setText(DesktopApp.aiSelected()[state.playerToAgent(7)].name());
        (this.textFieldPlayerName8 = new JTextField()).setBounds(139, 347, 190, 20);
        final MaxLengthTextDocument maxLength8 = new MaxLengthTextDocument();
        maxLength8.setMaxChars(21);
        this.textFieldPlayerName8.setDocument(maxLength8);
        this.textFieldPlayerName8.setEnabled(true);
        this.textFieldPlayerName8.setText(DesktopApp.aiSelected()[state.playerToAgent(8)].name());
        (this.textFieldThinkingTimeAll = new JTextField()).setBounds(275, 439, 103, 20);
        this.textFieldThinkingTimeAll.setEnabled(false);
        if (numPlayers > 1) {
            this.textFieldThinkingTimeAll.setEnabled(true);
        }
        this.textFieldThinkingTimeAll.setColumns(10);
        this.textFieldThinkingTimeAll.setText("-");
        final JLabel lblName = new JLabel("Player Names");
        lblName.setBounds(39, 32, 137, 21);
        lblName.setFont(new Font("Dialog", 1, 16));
        final ArrayList<String> aiStringsBlank = GUIUtil.getAiStrings(true);
        aiStringsBlank.add("-");
        final JComboBox<?> comboBoxAgentAll = new JComboBox<>(aiStringsBlank.toArray());
        comboBoxAgentAll.setBounds(118, 438, 134, 22);
        comboBoxAgentAll.setEnabled(false);
        if (numPlayers > 1) {
            comboBoxAgentAll.setEnabled(true);
            comboBoxAgentAll.setSelectedIndex(aiStringsBlank.size() - 1);
        }
        final DocumentListener documentListenerMaxTurns = new DocumentListener() {
            @Override
            public void changedUpdate(final DocumentEvent documentEvent) {
                this.update(documentEvent);
            }
            
            @Override
            public void insertUpdate(final DocumentEvent documentEvent) {
                this.update(documentEvent);
            }
            
            @Override
            public void removeUpdate(final DocumentEvent documentEvent) {
                this.update(documentEvent);
            }
            
            private void update(final DocumentEvent documentEvent) {
                int numberTurns = 1250;
                try {
                    numberTurns = Integer.parseInt(SettingsDialog.this.textFieldMaximumNumberOfTurns.getText());
                }
                catch (Exception ex) {}
                context.game().setMaxTurns(numberTurns);
                SettingsManager.setTurnLimit(context.game().name(), numberTurns);
                Manager.app.repaint();
            }
        };
        final DocumentListener documentListenerTickLength = new DocumentListener() {
            @Override
            public void changedUpdate(final DocumentEvent documentEvent) {
                this.update(documentEvent);
            }
            
            @Override
            public void insertUpdate(final DocumentEvent documentEvent) {
                this.update(documentEvent);
            }
            
            @Override
            public void removeUpdate(final DocumentEvent documentEvent) {
                this.update(documentEvent);
            }
            
            private void update(final DocumentEvent documentEvent) {
                SettingsManager.agentsPaused = true;
                double tickLength = SettingsManager.tickLength;
                try {
                    tickLength = Double.parseDouble(SettingsDialog.this.textFieldTickLength.getText());
                }
                catch (Exception ex) {}
                SettingsManager.tickLength = tickLength;
                Manager.app.repaint();
            }
        };
        final DocumentListener documentListenerTabFontSize = new DocumentListener() {
            @Override
            public void changedUpdate(final DocumentEvent documentEvent) {
                this.update(documentEvent);
            }
            
            @Override
            public void insertUpdate(final DocumentEvent documentEvent) {
                this.update(documentEvent);
            }
            
            @Override
            public void removeUpdate(final DocumentEvent documentEvent) {
                this.update(documentEvent);
            }
            
            private void update(final DocumentEvent documentEvent) {
                int tabFontSize = SettingsDesktop.tabFontSize;
                try {
                    tabFontSize = Integer.parseInt(SettingsDialog.this.textFieldTabFontSize.getText());
                }
                catch (Exception ex) {}
                SettingsDesktop.tabFontSize = tabFontSize;
                MainWindow.getPanels().clear();
                Manager.app.repaint();
            }
        };
        final DocumentListener documentListenerEditorFontSize = new DocumentListener() {
            @Override
            public void changedUpdate(final DocumentEvent documentEvent) {
                this.update(documentEvent);
            }
            
            @Override
            public void insertUpdate(final DocumentEvent documentEvent) {
                this.update(documentEvent);
            }
            
            @Override
            public void removeUpdate(final DocumentEvent documentEvent) {
                this.update(documentEvent);
            }
            
            private void update(final DocumentEvent documentEvent) {
                int editorFontSize = SettingsDesktop.editorFontSize;
                try {
                    editorFontSize = Integer.parseInt(SettingsDialog.this.textFieldEditorFontSize.getText());
                }
                catch (Exception ex) {}
                SettingsDesktop.editorFontSize = editorFontSize;
            }
        };
        final JLabel lblAgent = new JLabel("Agent");
        lblAgent.setBounds(122, 408, 119, 19);
        lblAgent.setFont(new Font("Dialog", 1, 16));
        final JLabel lblAllPlayers = new JLabel("All Players");
        lblAllPlayers.setBounds(29, 443, 78, 14);
        final JLabel lblThinkingTime = new JLabel("Thinking time");
        lblThinkingTime.setBounds(275, 408, 123, 19);
        lblThinkingTime.setFont(new Font("Dialog", 1, 16));
        final JLabel lblPlayer_1 = new JLabel("Player 1");
        lblPlayer_1.setBounds(47, 84, 74, 14);
        final JLabel lblPlayer_2 = new JLabel("Player 2");
        lblPlayer_2.setBounds(47, 122, 74, 14);
        final JLabel lblPlayer_3 = new JLabel("Player 3");
        lblPlayer_3.setBounds(47, 160, 74, 14);
        final JLabel lblPlayer_4 = new JLabel("Player 4");
        lblPlayer_4.setBounds(47, 198, 74, 14);
        final JLabel lblPlayer_5 = new JLabel("Player 5");
        lblPlayer_5.setBounds(47, 236, 74, 14);
        final JLabel lblPlayer_6 = new JLabel("Player 6");
        lblPlayer_6.setBounds(47, 274, 74, 14);
        final JLabel lblPlayer_7 = new JLabel("Player 7");
        lblPlayer_7.setBounds(47, 312, 74, 14);
        final JLabel lblPlayer_8 = new JLabel("Player 8");
        lblPlayer_8.setBounds(47, 350, 74, 14);
        final JButton btnNewButton = new JButton("Apply");
        btnNewButton.setFont(new Font("Tahoma", 1, 16));
        btnNewButton.setBounds(340, 523, 97, 29);
        btnNewButton.addActionListener(e -> {
            if (comboBoxAgentAll.getSelectedIndex() != aiStringsBlank.size() - 1) {
                final JSONObject json = new JSONObject().put("AI", new JSONObject().put("algorithm", comboBoxAgentAll.getSelectedItem().toString()));
                for (int i = 1; i <= 16; ++i) {
                    AIUtil.updateSelectedAI(json, i, AIMenuName.getAIMenuName(comboBoxAgentAll.getSelectedItem().toString()));
                }
            }
            try {
                double allSearchTimeValue = Double.valueOf(SettingsDialog.this.textFieldThinkingTimeAll.getText());
                if (allSearchTimeValue <= 0.0) {
                    allSearchTimeValue = 1.0;
                }
                for (int j = 1; j <= 16; ++j) {
                    DesktopApp.aiSelected()[j].setThinkTime(allSearchTimeValue);
                }
            }
            catch (Exception ex) {}
            String name = SettingsDialog.this.textFieldPlayerName1.getText();
            if (name != null) {
                DesktopApp.aiSelected()[state.playerToAgent(1)].setName(name);
            }
            else {
                DesktopApp.aiSelected()[state.playerToAgent(1)].setName("");
            }
            name = SettingsDialog.this.textFieldPlayerName2.getText();
            if (name != null) {
                DesktopApp.aiSelected()[state.playerToAgent(2)].setName(name);
            }
            else {
                DesktopApp.aiSelected()[state.playerToAgent(2)].setName("");
            }
            name = SettingsDialog.this.textFieldPlayerName3.getText();
            if (name != null) {
                DesktopApp.aiSelected()[state.playerToAgent(3)].setName(name);
            }
            else {
                DesktopApp.aiSelected()[state.playerToAgent(3)].setName("");
            }
            name = SettingsDialog.this.textFieldPlayerName4.getText();
            if (name != null) {
                DesktopApp.aiSelected()[state.playerToAgent(4)].setName(name);
            }
            else {
                DesktopApp.aiSelected()[state.playerToAgent(4)].setName("");
            }
            name = SettingsDialog.this.textFieldPlayerName5.getText();
            if (name != null) {
                DesktopApp.aiSelected()[state.playerToAgent(5)].setName(name);
            }
            else {
                DesktopApp.aiSelected()[state.playerToAgent(5)].setName("");
            }
            name = SettingsDialog.this.textFieldPlayerName6.getText();
            if (name != null) {
                DesktopApp.aiSelected()[state.playerToAgent(6)].setName(name);
            }
            else {
                DesktopApp.aiSelected()[state.playerToAgent(6)].setName("");
            }
            name = SettingsDialog.this.textFieldPlayerName7.getText();
            if (name != null) {
                DesktopApp.aiSelected()[state.playerToAgent(7)].setName(name);
            }
            else {
                DesktopApp.aiSelected()[state.playerToAgent(7)].setName("");
            }
            name = SettingsDialog.this.textFieldPlayerName8.getText();
            if (name != null) {
                DesktopApp.aiSelected()[state.playerToAgent(8)].setName(name);
            }
            else {
                DesktopApp.aiSelected()[state.playerToAgent(8)].setName("");
            }
            SettingsNetwork.backupAiPlayers();
            EventQueue.invokeLater(() -> DesktopApp.view().createPanels());
            SettingsDialog.this.dispose();
        });
        SettingsDialog.playerPanel.setLayout(null);
        final JSeparator separator_3 = new JSeparator();
        separator_3.setBounds(0, 389, 455, 8);
        SettingsDialog.playerPanel.add(separator_3);
        SettingsDialog.playerPanel.add(lblPlayer_8);
        SettingsDialog.playerPanel.add(lblPlayer_7);
        SettingsDialog.playerPanel.add(lblPlayer_6);
        SettingsDialog.playerPanel.add(lblPlayer_5);
        SettingsDialog.playerPanel.add(lblPlayer_4);
        SettingsDialog.playerPanel.add(lblPlayer_3);
        SettingsDialog.playerPanel.add(lblPlayer_2);
        SettingsDialog.playerPanel.add(lblPlayer_1);
        SettingsDialog.playerPanel.add(this.textFieldPlayerName1);
        SettingsDialog.playerPanel.add(this.textFieldPlayerName2);
        SettingsDialog.playerPanel.add(this.textFieldPlayerName3);
        SettingsDialog.playerPanel.add(this.textFieldPlayerName4);
        SettingsDialog.playerPanel.add(this.textFieldPlayerName5);
        SettingsDialog.playerPanel.add(this.textFieldPlayerName6);
        SettingsDialog.playerPanel.add(this.textFieldPlayerName7);
        SettingsDialog.playerPanel.add(this.textFieldPlayerName8);
        SettingsDialog.playerPanel.add(lblName);
        SettingsDialog.playerPanel.add(btnNewButton);
        SettingsDialog.playerPanel.add(lblAllPlayers);
        SettingsDialog.playerPanel.add(lblAgent);
        SettingsDialog.playerPanel.add(comboBoxAgentAll);
        SettingsDialog.playerPanel.add(this.textFieldThinkingTimeAll);
        SettingsDialog.playerPanel.add(lblThinkingTime);
        final JSeparator separator_4 = new JSeparator();
        separator_4.setBounds(0, 489, 455, 8);
        SettingsDialog.playerPanel.add(separator_4);
        final JButton buttonResetPlayerNames = new JButton("");
        buttonResetPlayerNames.setBounds(340, 30, 26, 23);
        SettingsDialog.playerPanel.add(buttonResetPlayerNames);
        buttonResetPlayerNames.addActionListener(e -> {
            for (int i = 0; i < DesktopApp.aiSelected().length; ++i) {
                DesktopApp.aiSelected()[state.playerToAgent(i)].setName("Player " + i);
            }
            SettingsDialog.createAndShowGUI(1);
        });
        final JLabel label = new JLabel("Reset to Defaults");
        label.setFont(new Font("Dialog", 0, 14));
        label.setBounds(207, 35, 122, 17);
        SettingsDialog.playerPanel.add(label);
        if (SettingsColour.getCustomBoardColour()[BoardGraphicsType.InnerEdges.value()] == null) {
            buttonEdge1.setBackground(SettingsColour.getDefaultBoardColour()[BoardGraphicsType.InnerEdges.value()]);
        }
        else if (SettingsColour.getDefaultBoardColour()[BoardGraphicsType.InnerEdges.value()] != null) {
            buttonEdge1.setBackground(SettingsColour.getCustomBoardColour()[BoardGraphicsType.InnerEdges.value()]);
        }
        if (SettingsColour.getCustomBoardColour()[BoardGraphicsType.Phase0.value()] == null) {
            buttonPhase1.setBackground(SettingsColour.getDefaultBoardColour()[BoardGraphicsType.Phase0.value()]);
        }
        else if (SettingsColour.getDefaultBoardColour()[BoardGraphicsType.Phase0.value()] != null) {
            buttonPhase1.setBackground(SettingsColour.getCustomBoardColour()[BoardGraphicsType.Phase0.value()]);
        }
        if (SettingsColour.getCustomBoardColour()[BoardGraphicsType.Phase1.value()] == null) {
            buttonPhase2.setBackground(SettingsColour.getDefaultBoardColour()[BoardGraphicsType.Phase1.value()]);
        }
        else if (SettingsColour.getDefaultBoardColour()[BoardGraphicsType.Phase1.value()] != null) {
            buttonPhase2.setBackground(SettingsColour.getCustomBoardColour()[BoardGraphicsType.Phase1.value()]);
        }
        if (SettingsColour.getCustomBoardColour()[BoardGraphicsType.Phase2.value()] == null) {
            buttonPhase3.setBackground(SettingsColour.getDefaultBoardColour()[BoardGraphicsType.Phase2.value()]);
        }
        else if (SettingsColour.getDefaultBoardColour()[BoardGraphicsType.Phase2.value()] != null) {
            buttonPhase3.setBackground(SettingsColour.getCustomBoardColour()[BoardGraphicsType.Phase2.value()]);
        }
        if (SettingsColour.getCustomBoardColour()[BoardGraphicsType.Phase3.value()] == null) {
            buttonPhase4.setBackground(SettingsColour.getDefaultBoardColour()[BoardGraphicsType.Phase3.value()]);
        }
        else if (SettingsColour.getDefaultBoardColour()[BoardGraphicsType.Phase3.value()] != null) {
            buttonPhase4.setBackground(SettingsColour.getCustomBoardColour()[BoardGraphicsType.Phase3.value()]);
        }
        if (SettingsColour.getCustomBoardColour()[BoardGraphicsType.Symbols.value()] == null) {
            buttonSymbol1.setBackground(SettingsColour.getDefaultBoardColour()[BoardGraphicsType.Symbols.value()]);
        }
        else if (SettingsColour.getDefaultBoardColour()[BoardGraphicsType.Symbols.value()] != null) {
            buttonSymbol1.setBackground(SettingsColour.getCustomBoardColour()[BoardGraphicsType.Symbols.value()]);
        }
        if (SettingsColour.getCustomBoardColour()[BoardGraphicsType.OuterEdges.value()] == null) {
            buttonEdge2.setBackground(SettingsColour.getDefaultBoardColour()[BoardGraphicsType.OuterEdges.value()]);
        }
        else if (SettingsColour.getDefaultBoardColour()[BoardGraphicsType.OuterEdges.value()] != null) {
            buttonEdge2.setBackground(SettingsColour.getCustomBoardColour()[BoardGraphicsType.OuterEdges.value()]);
        }
        SettingsDialog.tabbedPane.setSelectedIndex(panelNum);
        (SettingsDialog.otherPanel = new JPanel()).setBorder(new EmptyBorder(5, 5, 5, 5));
        SettingsDialog.tabbedPane.addTab("Advanced", null, SettingsDialog.otherPanel, null);
        final JLabel lblMaximumNumberOfTurns = new JLabel("Maximum turn limit (per player)");
        lblMaximumNumberOfTurns.setBounds(30, 40, 281, 19);
        lblMaximumNumberOfTurns.setFont(new Font("Dialog", 1, 14));
        lblMaximumNumberOfTurns.setToolTipText("<html>The maximum number of turns each player is allowed to make.<br>If a player has had more turns than this value, the game is a draw for all remaining active players.</html>");
        (this.textFieldMaximumNumberOfTurns = new JTextField()).setBounds(321, 40, 86, 20);
        this.textFieldMaximumNumberOfTurns.setColumns(10);
        final JLabel lblMovesUseCoordinates = new JLabel("Moves use coordinates");
        lblMovesUseCoordinates.setBounds(30, 160, 227, 17);
        lblMovesUseCoordinates.setFont(new Font("Dialog", 1, 14));
        final JCheckBox rdbtnMoveCoord = new JCheckBox("yes");
        rdbtnMoveCoord.setBounds(321, 159, 86, 23);
        final JLabel lblHideAiPieces = new JLabel("Hide AI moves");
        lblHideAiPieces.setBounds(30, 220, 227, 17);
        lblHideAiPieces.setFont(new Font("Dialog", 1, 14));
        final JCheckBox radioButtonHideAiMoves = new JCheckBox("yes");
        radioButtonHideAiMoves.setBounds(321, 219, 86, 23);
        radioButtonHideAiMoves.setSelected(true);
        final JLabel lblShowMovementAnimation = new JLabel("Movement animation");
        lblShowMovementAnimation.setBounds(30, 280, 227, 17);
        lblShowMovementAnimation.setFont(new Font("Dialog", 1, 14));
        final JCheckBox radioButtonMovementAnimation = new JCheckBox("yes");
        radioButtonMovementAnimation.setBounds(321, 279, 86, 23);
        radioButtonMovementAnimation.setSelected(true);
        final JLabel lblDevMode = new JLabel("Developer options");
        lblDevMode.setBounds(30, 340, 227, 17);
        lblDevMode.setFont(new Font("Dialog", 1, 14));
        final JCheckBox checkBoxDevMode = new JCheckBox("yes");
        checkBoxDevMode.setBounds(321, 339, 86, 23);
        final JLabel lblSoundEffectAfter = new JLabel("<html>Sound effect after AI or<br>network move</html>");
        lblSoundEffectAfter.setBounds(30, 400, 227, 34);
        lblSoundEffectAfter.setFont(new Font("Dialog", 1, 14));
        final JCheckBox checkBoxSoundEffect = new JCheckBox("yes");
        checkBoxSoundEffect.setBounds(321, 399, 86, 23);
        checkBoxSoundEffect.setSelected(false);
        final JLabel lblCoordOutline = new JLabel("Coordinate outline");
        lblCoordOutline.setFont(new Font("Dialog", 1, 14));
        lblCoordOutline.setBounds(30, 460, 227, 17);
        SettingsDialog.otherPanel.add(lblCoordOutline);
        final JLabel lblMoveFormat = new JLabel("Move Format");
        lblMoveFormat.setBounds(30, 520, 227, 17);
        lblMoveFormat.setFont(new Font("Dialog", 1, 14));
        final String[] moveFormat = { "Move", "Short", "Full" };
        final JComboBox<String> comboBoxFormat = new JComboBox<>();
        comboBoxFormat.setBounds(321, 518, 86, 23);
        for (final String s2 : moveFormat) {
            comboBoxFormat.addItem(s2);
        }
        for (int i = 0; i < moveFormat.length; ++i) {
            if (moveFormat[i].equals(SettingsDesktop.moveFormat)) {
                comboBoxFormat.setSelectedIndex(i);
            }
        }
        comboBoxFormat.addActionListener(e -> {
            SettingsDesktop.moveFormat = comboBoxFormat.getSelectedItem().toString();
            MainWindow.tabPanel().page(1).updatePage(context);
        });
        comboBoxFormat.setEnabled(true);
        final JLabel lblTabFontSize = new JLabel("Tab Font Size");
        lblTabFontSize.setBounds(30, 580, 281, 19);
        lblTabFontSize.setFont(new Font("Dialog", 1, 14));
        lblTabFontSize.setToolTipText("<html>The font size for the text displayed in the tabs.<br>");
        (this.textFieldTabFontSize = new JTextField()).setBounds(321, 578, 86, 20);
        this.textFieldTabFontSize.setColumns(10);
        this.textFieldTabFontSize.setText("" + SettingsDesktop.tabFontSize);
        this.textFieldTabFontSize.getDocument().addDocumentListener(documentListenerTabFontSize);
        final JLabel lblEditorFontSize = new JLabel("Editor Font Size");
        lblEditorFontSize.setBounds(30, 620, 281, 19);
        lblEditorFontSize.setFont(new Font("Dialog", 1, 14));
        lblEditorFontSize.setToolTipText("<html>The font size for the text displayed in the editor.<br>");
        (this.textFieldEditorFontSize = new JTextField()).setBounds(321, 618, 86, 20);
        this.textFieldEditorFontSize.setColumns(10);
        this.textFieldEditorFontSize.setText("" + SettingsDesktop.editorFontSize);
        this.textFieldEditorFontSize.getDocument().addDocumentListener(documentListenerEditorFontSize);
        final JCheckBox checkBoxCoordOutline = new JCheckBox("yes");
        checkBoxCoordOutline.setSelected(false);
        checkBoxCoordOutline.setBounds(321, 459, 86, 23);
        SettingsDialog.otherPanel.add(checkBoxCoordOutline);
        final JCheckBox checkBoxEditorAutocomplete = new JCheckBox("yes");
        checkBoxEditorAutocomplete.setSelected(false);
        checkBoxEditorAutocomplete.setBounds(321, 660, 86, 23);
        SettingsDialog.otherPanel.add(checkBoxEditorAutocomplete);
        checkBoxDevMode.setSelected(SettingsManager.devMode);
        checkBoxDevMode.addActionListener(e -> {
            SettingsManager.devMode = checkBoxDevMode.isSelected();
            DesktopApp.frame().setJMenuBar(new MainMenu());
            Manager.app.repaint();
        });
        radioButtonMovementAnimation.setSelected(SettingsManager.showAnimation);
        radioButtonMovementAnimation.addActionListener(e -> SettingsManager.showAnimation = radioButtonMovementAnimation.isSelected());
        checkBoxSoundEffect.setSelected(SettingsManager.moveSoundEffect);
        checkBoxSoundEffect.addActionListener(e -> SettingsManager.moveSoundEffect = checkBoxSoundEffect.isSelected());
        checkBoxCoordOutline.setSelected(SettingsVC.coordWithOutline);
        checkBoxCoordOutline.addActionListener(e -> {
            SettingsVC.coordWithOutline = checkBoxCoordOutline.isSelected();
            Manager.app.repaint();
        });
        radioButtonHideAiMoves.setSelected(SettingsManager.hideAiMoves);
        radioButtonHideAiMoves.addActionListener(e -> SettingsManager.hideAiMoves = radioButtonHideAiMoves.isSelected());
        rdbtnMoveCoord.setSelected(SettingsGeneral.isMoveCoord());
        rdbtnMoveCoord.addActionListener(e -> {
            SettingsGeneral.setMoveCoord(rdbtnMoveCoord.isSelected());
            Manager.app.updateTabs(context);
        });
        checkBoxEditorAutocomplete.setSelected(SettingsManager.editorAutocomplete);
        checkBoxEditorAutocomplete.addActionListener(e -> SettingsManager.editorAutocomplete = checkBoxEditorAutocomplete.isSelected());
        (this.textFieldTickLength = new JTextField()).setText("0");
        this.textFieldTickLength.setColumns(10);
        this.textFieldTickLength.setBounds(321, 76, 86, 20);
        SettingsDialog.otherPanel.add(this.textFieldTickLength);
        this.textFieldMaximumNumberOfTurns.setText("" + context.game().getMaxTurnLimit());
        this.textFieldTickLength.setText("" + SettingsManager.tickLength);
        SettingsDialog.otherPanel.setLayout(null);
        SettingsDialog.otherPanel.add(lblShowMovementAnimation);
        SettingsDialog.otherPanel.add(radioButtonMovementAnimation);
        SettingsDialog.otherPanel.add(lblDevMode);
        SettingsDialog.otherPanel.add(checkBoxDevMode);
        SettingsDialog.otherPanel.add(lblSoundEffectAfter);
        SettingsDialog.otherPanel.add(checkBoxSoundEffect);
        SettingsDialog.otherPanel.add(lblHideAiPieces);
        SettingsDialog.otherPanel.add(radioButtonHideAiMoves);
        SettingsDialog.otherPanel.add(lblMovesUseCoordinates);
        SettingsDialog.otherPanel.add(rdbtnMoveCoord);
        SettingsDialog.otherPanel.add(lblMaximumNumberOfTurns);
        SettingsDialog.otherPanel.add(this.textFieldMaximumNumberOfTurns);
        SettingsDialog.otherPanel.add(lblTabFontSize);
        SettingsDialog.otherPanel.add(this.textFieldTabFontSize);
        SettingsDialog.otherPanel.add(lblEditorFontSize);
        SettingsDialog.otherPanel.add(this.textFieldEditorFontSize);
        SettingsDialog.otherPanel.add(lblMoveFormat);
        SettingsDialog.otherPanel.add(comboBoxFormat);
        final JLabel tickLabel = new JLabel("Tick length (seconds)");
        tickLabel.setToolTipText("<html>The maximum number of turns each player is allowed to make.<br>If a player has had more turns than this value, the game is a draw for all remaining active players.</html>");
        tickLabel.setFont(new Font("Dialog", 1, 14));
        tickLabel.setBounds(30, 76, 281, 19);
        SettingsDialog.otherPanel.add(tickLabel);
        final JLabel lblEditorAutocomplete = new JLabel("Editor Autocomplete");
        lblEditorAutocomplete.setToolTipText("<html>The font size for the text displayed in the editor.<br>");
        lblEditorAutocomplete.setFont(new Font("Dialog", 1, 14));
        lblEditorAutocomplete.setBounds(30, 660, 281, 19);
        SettingsDialog.otherPanel.add(lblEditorAutocomplete);
        this.textFieldMaximumNumberOfTurns.getDocument().addDocumentListener(documentListenerMaxTurns);
        this.textFieldTickLength.getDocument().addDocumentListener(documentListenerTickLength);
        if (SettingsNetwork.getActiveGameId() != 0) {
            setPlayerPanelEnabled(Boolean.FALSE);
        }
        if (SettingsNetwork.getActiveGameId() != 0) {
            this.textFieldMaximumNumberOfTurns.setEnabled(false);
        }
        if (context.game().players().count() < 2) {
            this.textFieldPlayerName2.setVisible(false);
            lblPlayer_2.setVisible(false);
        }
        if (context.game().players().count() < 3) {
            this.textFieldPlayerName3.setVisible(false);
            lblPlayer_3.setVisible(false);
        }
        if (context.game().players().count() < 4) {
            this.textFieldPlayerName4.setVisible(false);
            lblPlayer_4.setVisible(false);
        }
        if (context.game().players().count() < 5) {
            this.textFieldPlayerName5.setVisible(false);
            lblPlayer_5.setVisible(false);
        }
        if (context.game().players().count() < 6) {
            this.textFieldPlayerName6.setVisible(false);
            lblPlayer_6.setVisible(false);
        }
        if (context.game().players().count() < 7) {
            this.textFieldPlayerName7.setVisible(false);
            lblPlayer_7.setVisible(false);
        }
        if (context.game().players().count() < 8) {
            this.textFieldPlayerName8.setVisible(false);
            lblPlayer_8.setVisible(false);
        }
    }
    
    public static void setPlayerPanelEnabled(final Boolean isEnabled) {
        try {
            SettingsDialog.tabbedPane.setEnabledAt(1, isEnabled);
            final JPanel panel = SettingsDialog.playerPanel;
            panel.setEnabled(isEnabled);
            final Component[] components2;
            final Component[] components = components2 = panel.getComponents();
            for (final Component component : components2) {
                component.setEnabled(isEnabled);
            }
        }
        catch (Exception ex) {}
    }
    
    public static void setOtherPanelEnabled(final Boolean isEnabled) {
        try {
            SettingsDialog.tabbedPane.setEnabledAt(2, isEnabled);
            final JPanel panel = SettingsDialog.otherPanel;
            panel.setEnabled(isEnabled);
            final Component[] components2;
            final Component[] components = components2 = panel.getComponents();
            for (final Component component : components2) {
                component.setEnabled(isEnabled);
            }
        }
        catch (Exception ex) {}
    }
    
    static {
        SettingsDialog.tabbedPane = null;
        SettingsDialog.playerPanel = null;
        SettingsDialog.otherPanel = null;
        SettingsDialog.ignorePlayerComboBoxEvents = false;
    }
}
