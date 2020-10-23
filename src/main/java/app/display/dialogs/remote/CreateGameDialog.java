// 
// Decompiled by Procyon v0.5.36
// 

package app.display.dialogs.remote;

import app.DesktopApp;
import app.display.dialogs.GameLoaderDialog;
import app.display.dialogs.util.DialogUtil;
import app.display.dialogs.util.JComboCheckBox;
import app.game.GameRestart;
import app.loading.GameLoading;
import app.menu.MainMenu;
import game.Game;
import main.FileHandling;
import manager.Manager;
import manager.ai.AIDetails;
import manager.ai.AIMenuName;
import manager.network.DatabaseFunctions;
import manager.network.SettingsNetwork;
import manager.utils.ContextSnapshot;
import manager.utils.SettingsManager;
import options.Option;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

public class CreateGameDialog extends JDialog
{
    private static final long serialVersionUID = 1L;
    static CreateGameDialog dialog;
    static boolean okButtonPressed;
    
    public static void showDialog(final RemoteDialog parentDialog) {
        try {
            DialogUtil.initialiseDialog(CreateGameDialog.dialog = new CreateGameDialog(parentDialog), "Create Game", null);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public CreateGameDialog(final RemoteDialog parentDialog) {
        this.setBounds(100, 100, 450, 440);
        final JPanel contentPanel = new JPanel();
        this.getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.getContentPane().add(contentPanel, "Center");
        contentPanel.setLayout(null);
        final JButton btnSelectGame = new JButton("Select Game");
        btnSelectGame.setBounds(233, 28, 178, 25);
        contentPanel.add(btnSelectGame);
        final JLabel lblSelectedGame = new JLabel("No game selected.");
        lblSelectedGame.setBounds(23, 33, 200, 15);
        contentPanel.add(lblSelectedGame);
        final JLabel lblPassword = new JLabel("Password to Join Game");
        lblPassword.setBounds(23, 71, 180, 15);
        contentPanel.add(lblPassword);
        final JTextField txtPassword = new JTextField();
        txtPassword.setBounds(233, 69, 178, 19);
        contentPanel.add(txtPassword);
        txtPassword.setColumns(10);
        final JLabel lblRestrictToOne = new JLabel("Restrict to Specific Players");
        lblRestrictToOne.setBounds(23, 110, 270, 15);
        contentPanel.add(lblRestrictToOne);
        final String[] allPlayersPre = DatabaseFunctions.GetAllPlayers().split("_next_");
        final String[] allPlayers = new String[allPlayersPre.length - 1];
        int allPlayersIndex = 0;
        for (String s : allPlayersPre) {
            if (Integer.parseInt(s.split("NEXT_COL")[0]) != SettingsNetwork.getLoginId()) {
                allPlayers[allPlayersIndex] = s;
                ++allPlayersIndex;
            }
        }
        final String[] comboOptions = new String[allPlayers.length];
        for (int j = 0; j < allPlayers.length; ++j) {
            comboOptions[j] = allPlayers[j].split("NEXT_COL")[0] + ": " + allPlayers[j].split("NEXT_COL")[1];
        }
        final Vector<JCheckBox> v = new Vector<>();
        for (String comboOption : comboOptions) {
            final JCheckBox tempCheckBox = new JCheckBox(comboOption, false);
            v.add(tempCheckBox);
        }
        final JComboCheckBox comboBox = new JComboCheckBox(v);
        comboBox.setBounds(233, 105, 178, 24);
        contentPanel.add(comboBox);
        final JLabel lblTimeLimitPerPlayer = new JLabel("Time Per Player (Minutes)");
        lblTimeLimitPerPlayer.setBounds(23, 189, 200, 15);
        contentPanel.add(lblTimeLimitPerPlayer);
        final JTextField txtTimeLimitPlayer = new JTextField();
        txtTimeLimitPlayer.setColumns(10);
        txtTimeLimitPlayer.setBounds(233, 187, 178, 19);
        contentPanel.add(txtTimeLimitPlayer);
        final JLabel lblLeaveBlankForPlayer = new JLabel("Leave blank for no limit.");
        lblLeaveBlankForPlayer.setFont(new Font("Dialog", 2, 12));
        lblLeaveBlankForPlayer.setBounds(23, 205, 200, 15);
        contentPanel.add(lblLeaveBlankForPlayer);
        final JLabel lblPlayerNumber = new JLabel("Player Number");
        lblPlayerNumber.setBounds(23, 230, 270, 15);
        contentPanel.add(lblPlayerNumber);
        final JComboBox<Object> comboBox_1 = new JComboBox<>(new Object[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16});
        comboBox_1.setEnabled(false);
        comboBox_1.setBounds(233, 225, 178, 24);
        contentPanel.add(comboBox_1);
        final JLabel lblNotes = new JLabel("Notes");
        lblNotes.setBounds(23, 250, 388, 30);
        contentPanel.add(lblNotes);
        final JEditorPane editorPane_1 = new JEditorPane();
        editorPane_1.setBounds(23, 275, 385, 56);
        contentPanel.add(editorPane_1);
        final JLabel lblAsd = new JLabel("Options can be selected from the menu bar above.");
        lblAsd.setBounds(23, 0, 388, 30);
        contentPanel.add(lblAsd);
        lblAsd.setVisible(false);
        final JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(2));
        this.getContentPane().add(buttonPane, "South");
        final JButton okButton = new JButton("OK");
        okButton.setActionCommand("OK");
        buttonPane.add(okButton);
        this.getRootPane().setDefaultButton(okButton);
        okButton.addActionListener(e -> {
            if (CreateGameDialog.okButtonPressed) {
                return;
            }
            CreateGameDialog.okButtonPressed = true;
            try {
                final Game game = ContextSnapshot.getContext().game();
                final Pattern p = Pattern.compile("[^a-z0-9 ]", 2);
                int timeLimitPlayer = 0;
                if (!txtTimeLimitPlayer.getText().isEmpty()) {
                    timeLimitPlayer = Integer.parseInt(txtTimeLimitPlayer.getText());
                }
                if (lblSelectedGame.getText() == "No game selected.") {
                    Manager.app.addTextToStatusPanel("please select a game.\n");
                }
                else if (editorPane_1.getText().length() > 500) {
                    Manager.app.addTextToStatusPanel("Notes must be less than 500 characters.\n");
                }
                else if (p.matcher(txtPassword.getText()).find()) {
                    Manager.app.addTextToStatusPanel("Password cannot contain any special characters.\n");
                }
                else if (p.matcher(editorPane_1.getText()).find()) {
                    Manager.app.addTextToStatusPanel("Notes cannot contain any special characters.\n");
                }
                else {
                    try {
                        String allowedPlayerId = "";
                        for (int i = 0; i < comboBox.getItemCount(); ++i) {
                            if (comboBox.getItemAt(i).isSelected()) {
                                allowedPlayerId = allowedPlayerId + comboBox.getItemAt(i).getText().split(":")[0] + ",";
                            }
                        }
                        int playerNumber = comboBox_1.getSelectedIndex() + 1;
                        if (playerNumber > game.players().count()) {
                            playerNumber = game.players().count();
                        }
                        String optionString = "";
                        final int[] activeOptions = game.description().gameOptions().computeOptionSelections(SettingsManager.userSelections.selectedOptionStrings());
                        for (int j = 0; j < game.description().gameOptions().numCategories(); ++j) {
                            final List<Option> options = game.description().gameOptions().categories().get(j).options();
                            if (!options.isEmpty()) {
                                optionString = optionString + options.get(activeOptions[j]).menuHeadings().get(0).replaceAll("\\s+", "_") + "|" + options.get(activeOptions[j]).menuHeadings().get(1).replaceAll("\\s+", "_") + "_NEXT_";
                            }
                        }
                        final URL phpLudii = new URL(DatabaseFunctions.appFolderLocation + "startGame.php?game=" + URLEncoder.encode(lblSelectedGame.getText(), StandardCharsets.UTF_8) + "&id=" + SettingsNetwork.getLoginId() + "&password=" + txtPassword.getText().replace(" ", "%20") + "&allowedPlayerId=" + allowedPlayerId + "&playerTimeLimit=" + timeLimitPlayer * 60 + "&playerNumber=" + playerNumber + "&options=" + optionString + "&appVersion=" + "1.0.8" + "&gameHash=" + game.description().raw().hashCode() + "&notes=" + editorPane_1.getText().replace(" ", "%20") + "&secret=" + SettingsNetwork.getSecretNetworkNumber() + "&networkPlayerId=" + SettingsNetwork.getLoginId());
                        final URLConnection yc = phpLudii.openConnection();
                        try (final BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()))) {
                            String inputLine;
                            while ((inputLine = in.readLine()) != null) {
                                try {
                                    if (inputLine.startsWith("started")) {
                                        SettingsNetwork.setActiveGameId(Integer.parseInt(inputLine.substring(8)));
                                        SettingsNetwork.setNetworkPlayerNumber(playerNumber);
                                        DesktopApp.frame().setJMenuBar(new MainMenu());
                                        for (int k = 0; k < DesktopApp.aiSelected().length; ++k) {
                                            DesktopApp.aiSelected()[k] = new AIDetails(null, k, AIMenuName.Human);
                                        }
                                        GameRestart.restartGame(true);
                                        game.setMaxTurns(1250);
                                        final String id = inputLine.substring(8);
                                        EventQueue.invokeLater(() -> {
                                            DatabaseFunctions.setMaxPlayerNumber();
                                            DatabaseFunctions.setRNG(Manager.currGameStartRngState());
                                            EventQueue.invokeLater(() -> Manager.app.addTextToStatusPanel("Game Started: " + lblSelectedGame.getText() + " | ID: " + id + ".\n"));
                                            CreateGameDialog.this.dispose();
                                            parentDialog.refreshNetworkDialog();
                                        });
                                    }
                                    else {
                                        Manager.app.addTextToStatusPanel(inputLine + "\n");
                                    }
                                }
                                catch (Exception ex) {}
                            }
                        }
                    }
                    catch (Exception E) {
                        E.printStackTrace();
                    }
                }
            }
            catch (Exception ee) {
                Manager.app.addTextToStatusPanel("Please input a valid time limit.\n");
            }
            EventQueue.invokeLater(() -> CreateGameDialog.okButtonPressed = false);
        });
        final JButton cancelButton = new JButton("Cancel");
        cancelButton.setActionCommand("Cancel");
        buttonPane.add(cancelButton);
        cancelButton.addActionListener(e -> {
            try {
                CreateGameDialog.this.dispose();
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
        });
        btnSelectGame.addActionListener(e -> {
            try {
                final String[] choices = FileHandling.listGames();
                String initialChoice = choices[0];
                for (final String choice : choices) {
                    if (Manager.savedLudName() != null && Manager.savedLudName().endsWith(choice.replaceAll(Pattern.quote("\\"), "/"))) {
                        initialChoice = choice;
                        break;
                    }
                }
                final String choice2 = GameLoaderDialog.showDialog(DesktopApp.frame(), choices, initialChoice, false);
                final String str = choice2.replaceAll(Pattern.quote("\\"), "/");
                final String[] parts = str.split("/");
                lblSelectedGame.setText(parts[parts.length - 1]);
                if (lblSelectedGame.getText() == "No game selected.") {
                    contentPanel.setEnabled(true);
                }
                else {
                    SettingsNetwork.setActiveGameId(0);
                    GameLoading.loadGameFromName(lblSelectedGame.getText(), false);
                    CreateGameDialog.bringDialogToFront();
                    final DefaultComboBoxModel model = (DefaultComboBoxModel)comboBox_1.getModel();
                    model.removeAllElements();
                    final ArrayList<String> items = new ArrayList<>();
                    for (int i = 1; i <= ContextSnapshot.getContext().game().players().count(); ++i) {
                        items.add(Integer.toString(i));
                    }
                    for (final String item : items) {
                        model.addElement(item);
                    }
                    comboBox_1.setModel(model);
                    comboBox_1.setEnabled(true);
                    if (!ContextSnapshot.getContext().game().isAlternatingMoveGame()) {
                        txtTimeLimitPlayer.setText("");
                        txtTimeLimitPlayer.setEnabled(false);
                    }
                    else {
                        txtTimeLimitPlayer.setEnabled(true);
                    }
                    final JMenuBar menuBar = new JMenuBar();
                    CreateGameDialog.this.setJMenuBar(menuBar);
                    lblAsd.setVisible(false);
                    if (!ContextSnapshot.getContext().isAMatch()) {
                        boolean optionsFound = false;
                        for (int o = 0; o < ContextSnapshot.getContext().game().description().gameOptions().numCategories(); ++o) {
                            final List<Option> options = ContextSnapshot.getContext().game().description().gameOptions().categories().get(o).options();
                            if (!options.isEmpty()) {
                                optionsFound = true;
                            }
                        }
                        if (optionsFound && SettingsNetwork.getActiveGameId() == 0) {
                            lblAsd.setVisible(true);
                            final JMenu menu = new JMenu("Options");
                            MainMenu.updateOptionsMenu(ContextSnapshot.getContext(), menu);
                            menuBar.add(menu);
                            CreateGameDialog.this.setJMenuBar(menuBar);
                        }
                    }
                }
            }
            catch (Exception ex) {}
        });
        comboBox_1.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(final FocusEvent e) {
                if (lblSelectedGame.getText() != "No game selected.") {
                    final DefaultComboBoxModel model = (DefaultComboBoxModel)comboBox_1.getModel();
                    model.removeAllElements();
                    final ArrayList<String> items = new ArrayList<>();
                    for (int i = 1; i <= ContextSnapshot.getContext().game().players().count(); ++i) {
                        items.add(Integer.toString(i));
                    }
                    for (final String item : items) {
                        model.addElement(item);
                    }
                    comboBox_1.setModel(model);
                    comboBox_1.setEnabled(true);
                }
            }
        });
    }
    
    public static void bringDialogToFront() {
        try {
            EventQueue.invokeLater(() -> CreateGameDialog.dialog.toFront());
        }
        catch (Exception ex) {}
    }
    
    static {
        CreateGameDialog.okButtonPressed = false;
    }
}
