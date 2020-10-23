// 
// Decompiled by Procyon v0.5.36
// 

package app.display.dialogs.remote;

import app.DesktopApp;
import app.display.dialogs.GameLoaderDialog;
import app.display.dialogs.util.DialogUtil;
import app.display.dialogs.util.JComboCheckBox;
import app.loading.GameLoading;
import app.menu.MainMenu;
import game.Game;
import main.FileHandling;
import main.options.Option;
import manager.Manager;
import manager.network.DatabaseFunctions;
import manager.network.SettingsNetwork;
import manager.utils.ContextSnapshot;
import manager.utils.SettingsManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

public class CreateTournamentDialog extends JDialog
{
    private static final long serialVersionUID = 1L;
    static CreateTournamentDialog dialog;
    static boolean okButtonPressed;
    
    public static void showDialog(final RemoteDialog parentDialog) {
        try {
            DialogUtil.initialiseDialog(CreateTournamentDialog.dialog = new CreateTournamentDialog(parentDialog), "Create Tournament", null);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public CreateTournamentDialog(final RemoteDialog parentDialog) {
        this.setBounds(100, 100, 450, 600);
        final JPanel contentPanel = new JPanel();
        this.getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.getContentPane().add(contentPanel, "Center");
        contentPanel.setLayout(null);
        final JButton btnSelectGame = new JButton("Select Game");
        btnSelectGame.setBounds(233, 28, 178, 25);
        contentPanel.add(btnSelectGame);
        final JLabel lblSelectedGame = new JLabel("No game selected");
        lblSelectedGame.setBounds(23, 33, 200, 15);
        contentPanel.add(lblSelectedGame);
        final JLabel lblPassword = new JLabel("Password to join tournament");
        lblPassword.setBounds(23, 71, 180, 15);
        contentPanel.add(lblPassword);
        final JTextField txtPassword = new JTextField();
        txtPassword.setBounds(233, 69, 178, 19);
        contentPanel.add(txtPassword);
        txtPassword.setColumns(10);
        final JLabel lblRestrictToOne = new JLabel("Restrict to specific players");
        lblRestrictToOne.setBounds(23, 110, 270, 15);
        contentPanel.add(lblRestrictToOne);
        final String[] allPlayersPre = DatabaseFunctions.GetAllPlayers().split("_next_");
        final String[] allPlayers = new String[allPlayersPre.length];
        int allPlayersIndex = 0;
        for (int i = 0; i < allPlayersPre.length; ++i) {
            allPlayers[allPlayersIndex] = allPlayersPre[i];
            ++allPlayersIndex;
        }
        final String[] comboOptions = new String[allPlayers.length];
        for (int j = 0; j < allPlayers.length; ++j) {
            comboOptions[j] = allPlayers[j].split("NEXT_COL")[0] + ": " + allPlayers[j].split("NEXT_COL")[1];
        }
        final Vector<JCheckBox> v = new Vector<>();
        for (int k = 0; k < comboOptions.length; ++k) {
            final JCheckBox tempCheckBox = new JCheckBox(comboOptions[k], false);
            v.add(tempCheckBox);
        }
        final JComboBox<JCheckBox> comboBox = new JComboCheckBox(v);
        comboBox.setBounds(233, 105, 178, 24);
        contentPanel.add(comboBox);
        final JLabel lblTimeLimitPerPlayer = new JLabel("Time per player (minutes)");
        lblTimeLimitPerPlayer.setBounds(23, 189, 200, 15);
        contentPanel.add(lblTimeLimitPerPlayer);
        final JTextField txtTimeLimitPlayer = new JTextField();
        txtTimeLimitPlayer.setColumns(10);
        txtTimeLimitPlayer.setBounds(233, 187, 178, 19);
        contentPanel.add(txtTimeLimitPlayer);
        final JLabel lblLeaveBlankForPlayer = new JLabel("Leave blank for no limit");
        lblLeaveBlankForPlayer.setFont(new Font("Dialog", 2, 12));
        lblLeaveBlankForPlayer.setBounds(23, 205, 200, 15);
        contentPanel.add(lblLeaveBlankForPlayer);
        final JLabel lblAsd = new JLabel("Options can be selected from the menu bar above.");
        lblAsd.setBounds(23, 0, 388, 30);
        contentPanel.add(lblAsd);
        lblAsd.setVisible(false);
        final JSeparator separator_3 = new JSeparator();
        separator_3.setBounds(0, 245, 450, 8);
        contentPanel.add(separator_3);
        final JLabel lblTimeLimitPerRound = new JLabel("Time per round (minutes)");
        lblTimeLimitPerRound.setBounds(23, 269, 200, 15);
        contentPanel.add(lblTimeLimitPerRound);
        final JTextField txtTimeLimitRound = new JTextField();
        txtTimeLimitRound.setColumns(10);
        txtTimeLimitRound.setBounds(233, 267, 178, 19);
        contentPanel.add(txtTimeLimitRound);
        final JLabel lblLeaveBlankForRound = new JLabel("Leave blank for no limit");
        lblLeaveBlankForRound.setFont(new Font("Dialog", 2, 12));
        lblLeaveBlankForRound.setBounds(23, 285, 200, 15);
        contentPanel.add(lblLeaveBlankForRound);
        final JLabel lblPlayerNumber = new JLabel("Tournament format");
        lblPlayerNumber.setBounds(23, 315, 270, 15);
        contentPanel.add(lblPlayerNumber);
        final JComboBox<Object> comboBoxFormat = new JComboBox<>(new Object[]{"Round Robin", "Swiss System", "Elimination"});
        comboBoxFormat.setBounds(233, 310, 178, 24);
        contentPanel.add(comboBoxFormat);
        final JLabel lblNumberRounds = new JLabel("Number of Rounds");
        lblNumberRounds.setBounds(23, 347, 200, 15);
        contentPanel.add(lblNumberRounds);
        lblNumberRounds.setVisible(false);
        final JTextField txtNumberRounds = new JTextField();
        txtNumberRounds.setColumns(10);
        txtNumberRounds.setText("5");
        txtNumberRounds.setBounds(233, 345, 178, 19);
        contentPanel.add(txtNumberRounds);
        txtNumberRounds.setVisible(false);
        final JLabel lblName = new JLabel("Tournament Name");
        lblName.setBounds(23, 365, 388, 30);
        contentPanel.add(lblName);
        final JEditorPane editorPaneName = new JEditorPane();
        editorPaneName.setBounds(23, 390, 385, 20);
        contentPanel.add(editorPaneName);
        final JLabel lblNotes = new JLabel("Notes");
        lblNotes.setBounds(23, 410, 388, 30);
        contentPanel.add(lblNotes);
        final JEditorPane editorPaneNotes = new JEditorPane();
        editorPaneNotes.setBounds(23, 435, 385, 56);
        contentPanel.add(editorPaneNotes);
        final JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(2));
        this.getContentPane().add(buttonPane, "South");
        final JButton okButton = new JButton("OK");
        okButton.setActionCommand("OK");
        buttonPane.add(okButton);
        this.getRootPane().setDefaultButton(okButton);
        okButton.addActionListener(e -> {
            if (CreateTournamentDialog.okButtonPressed) {
                return;
            }
            CreateTournamentDialog.okButtonPressed = true;
            try {
                int timeLimitPlayer = 0;
                if (txtTimeLimitPlayer.getText().length() > 0) {
                    timeLimitPlayer = Integer.parseInt(txtTimeLimitPlayer.getText());
                }
                int timeLimitRound = 0;
                if (txtTimeLimitRound.getText().length() > 0) {
                    timeLimitRound = Integer.parseInt(txtTimeLimitRound.getText());
                }
                long numberRounds = 1L;
                if (txtNumberRounds.getText().length() > 0 && txtNumberRounds.isVisible()) {
                    numberRounds = Integer.parseInt(txtNumberRounds.getText());
                }
                if (lblSelectedGame.getText() == "No game selected") {
                    Manager.app.addTextToStatusPanel("Please select a game.\n");
                }
                else {
                    try {
                        final String formatString = comboBoxFormat.getSelectedItem().toString().replaceAll("\\s+", "");
                        String allowedPlayerIds = "";
                        for (int i = 0; i < comboBox.getItemCount(); ++i) {
                            if (comboBox.getItemAt(i).isSelected()) {
                                allowedPlayerIds = allowedPlayerIds + comboBox.getItemAt(i).getText().toString().split(":")[0] + ",";
                            }
                        }
                        final Game game = ContextSnapshot.getContext().game();
                        final Pattern p = Pattern.compile("[^a-z0-9 ]", 2);
                        if (numberRounds > 20L && formatString.equals("SwissSystem")) {
                            Manager.app.addTextToStatusPanel("Swiss System tournaments cannot have more than 20 rounds.\n");
                        }
                        else if (timeLimitRound != 0 && (timeLimitPlayer == 0 || timeLimitRound <= timeLimitPlayer * ContextSnapshot.getContext().game().players().count())) {
                            Manager.app.addTextToStatusPanel("The combined time limits for each player, must be less than the round time limit.\n");
                        }
                        else if (editorPaneName.getText().length() > 100) {
                            Manager.app.addTextToStatusPanel("Names must be less than 100 characters.\n");
                        }
                        else if (editorPaneNotes.getText().length() > 500) {
                            Manager.app.addTextToStatusPanel("Notes must be less than 500 characters.\n");
                        }
                        else if (p.matcher(txtPassword.getText()).find()) {
                            Manager.app.addTextToStatusPanel("Password cannot contain any special characters.\n");
                        }
                        else if (p.matcher(editorPaneName.getText()).find()) {
                            Manager.app.addTextToStatusPanel("Names cannot contain any special characters.\n");
                        }
                        else if (p.matcher(editorPaneNotes.getText()).find()) {
                            Manager.app.addTextToStatusPanel("Notes cannot contain any special characters.\n");
                        }
                        else {
                            String optionString = "";
                            final int[] activeOptions = game.description().gameOptions().computeOptionSelections(SettingsManager.userSelections.selectedOptionStrings());
                            for (int j = 0; j < game.description().gameOptions().numCategories(); ++j) {
                                final List<Option> options = game.description().gameOptions().categories().get(j).options();
                                if (options.size() > 0) {
                                    optionString = optionString + options.get(activeOptions[j]).menuHeadings().get(0).replaceAll("\\s+", "_") + "|" + options.get(activeOptions[j]).menuHeadings().get(1).replaceAll("\\s+", "_") + "_NEXT_";
                                }
                            }
                            int tournamentId = -1;
                            final URL phpLudii = new URL(DatabaseFunctions.appFolderLocation + "startTournament.php?game=" + URLEncoder.encode(lblSelectedGame.getText(), StandardCharsets.UTF_8) + "&id=" + SettingsNetwork.getLoginId() + "&password=" + txtPassword.getText().replace(" ", "%20") + "&allowedPlayerId=" + allowedPlayerIds + "&playerTimeLimit=" + timeLimitPlayer * 60 + "&options=" + optionString + "&roundLength=" + timeLimitRound * 60 + "&RNG=" + DatabaseFunctions.convertRNGToText(Manager.currGameStartRngState()) + "&maxPlayers=" + ContextSnapshot.getContext().game().players().count() + "&format=" + formatString + "&rounds=" + numberRounds + "&appVersion=" + "1.0.8" + "&gameHash=" + game.description().raw().hashCode() + "&notes=" + editorPaneNotes.getText().replace(" ", "%20") + "&name=" + editorPaneName.getText().replace(" ", "%20") + "&secret=" + SettingsNetwork.getSecretNetworkNumber() + "&networkPlayerId=" + SettingsNetwork.getLoginId());
                            final URLConnection yc = phpLudii.openConnection();
                            try (final BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()))) {
                                String inputLine;
                                while ((inputLine = in.readLine()) != null) {
                                    try {
                                        if (inputLine.startsWith("started")) {
                                            tournamentId = Integer.parseInt(inputLine.substring(8).trim());
                                        }
                                        else {
                                            Manager.app.addTextToStatusPanel(inputLine + "\n");
                                        }
                                    }
                                    catch (Exception ex) {}
                                }
                                in.close();
                            }
                            if (tournamentId == -1) {
                                Manager.app.addTextToStatusPanel("Could not start the tournament.\n");
                            }
                            else {
                                Manager.app.addTextToStatusPanel("Started tournament " + tournamentId + ".\n");
                                CreateTournamentDialog.this.dispose();
                                parentDialog.refreshNetworkDialog();
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
            EventQueue.invokeLater(() -> CreateTournamentDialog.okButtonPressed = false);
        });
        final JButton cancelButton = new JButton("Cancel");
        cancelButton.setActionCommand("Cancel");
        buttonPane.add(cancelButton);
        cancelButton.addActionListener(e -> {
            try {
                CreateTournamentDialog.this.dispose();
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
        });
        comboBoxFormat.addActionListener(e -> {
            if (comboBoxFormat.getSelectedItem().equals("Swiss System")) {
                lblNumberRounds.setVisible(true);
                txtNumberRounds.setVisible(true);
            }
            else {
                lblNumberRounds.setVisible(false);
                txtNumberRounds.setVisible(false);
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
                if (lblSelectedGame.getText() == "No game selected") {
                    contentPanel.setEnabled(true);
                }
                else {
                    SettingsNetwork.setActiveGameId(0);
                    GameLoading.loadGameFromName(lblSelectedGame.getText(), false);
                    CreateTournamentDialog.bringDialogToFront();
                    final ArrayList<String> items = new ArrayList<>();
                    for (int i = 1; i <= ContextSnapshot.getContext().game().players().count(); ++i) {
                        items.add(Integer.toString(i));
                    }
                    if (!ContextSnapshot.getContext().game().isAlternatingMoveGame()) {
                        txtTimeLimitPlayer.setText("");
                        txtTimeLimitPlayer.setEnabled(false);
                    }
                    else {
                        txtTimeLimitPlayer.setEnabled(true);
                    }
                    final JMenuBar menuBar = new JMenuBar();
                    CreateTournamentDialog.this.setJMenuBar(menuBar);
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
                            CreateTournamentDialog.this.setJMenuBar(menuBar);
                        }
                    }
                }
            }
            catch (Exception ex) {}
        });
    }
    
    public static void bringDialogToFront() {
        try {
            EventQueue.invokeLater(() -> CreateTournamentDialog.dialog.toFront());
        }
        catch (Exception ex) {}
    }
    
    static {
        CreateTournamentDialog.okButtonPressed = false;
    }
}
