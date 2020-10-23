// 
// Decompiled by Procyon v0.5.36
// 

package app.display.dialogs.remote;

import app.DesktopApp;
import app.display.dialogs.remote.panels.BaseFindPanel;
import app.display.dialogs.remote.panels.ButtonColumn;
import app.display.dialogs.remote.panels.games.GameJoinablePanel;
import app.display.dialogs.remote.panels.games.GameJoinedPanel;
import app.display.dialogs.remote.panels.games.GameOtherPanel;
import app.display.dialogs.remote.panels.tournaments.TournamentHostPanel;
import app.display.dialogs.remote.panels.tournaments.TournamentJoinablePanel;
import app.display.dialogs.remote.panels.tournaments.TournamentJoinedPanel;
import app.display.dialogs.util.DialogUtil;
import app.display.util.GUIUtil;
import app.menu.MainMenu;
import manager.Manager;
import manager.network.DatabaseFunctions;
import manager.network.SettingsNetwork;
import util.SettingsVC;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class RemoteDialog extends JDialog
{
    private static final long serialVersionUID = 1L;
    static RemoteDialog dialog;
    JPasswordField textFieldPassword;
    JTextField textFieldUsername;
    static Thread dialogRefreshThread;
    GameJoinablePanel gameJoinablePanel;
    GameJoinedPanel gameJoinedPanel;
    GameOtherPanel gameOtherPanel;
    TournamentJoinablePanel tournamentJoinablePanel;
    TournamentJoinedPanel tournamentJoinedPanel;
    TournamentHostPanel tournamentHostPanel;
    final JButton createButton;
    final JButton leaveButton;
    final JButton refreshButton;
    final JButton okButton;
    final JButton startButton;
    BaseFindPanel selectedPanel;
    
    public static void showDialog() {
        try {
            RemoteDialog.dialog = new RemoteDialog();
            if (SettingsNetwork.networkTabPosition() == null) {
                final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                final double displayWidth = screenSize.getWidth();
                final int leftDistance = DesktopApp.frame().getX();
                final int rightDistance = (int)(displayWidth - (DesktopApp.frame().getX() + DesktopApp.frame().getWidth()));
                final int dialogWidth = 485;
                int OSNudgeDistance = 15;
                if (GUIUtil.isMac()) {
                    OSNudgeDistance = 0;
                }
                if (leftDistance > 485) {
                    DialogUtil.initialiseSingletonDialog(RemoteDialog.dialog, "Remote", new Rectangle(DesktopApp.frame().getX() - 485 + OSNudgeDistance, DesktopApp.frame().getY(), 485, DesktopApp.frame().getHeight()));
                }
                else if (rightDistance > 485) {
                    DialogUtil.initialiseSingletonDialog(RemoteDialog.dialog, "Remote", new Rectangle(DesktopApp.frame().getX() + DesktopApp.frame().getWidth() - OSNudgeDistance, DesktopApp.frame().getY(), 485, DesktopApp.frame().getHeight()));
                }
                else if (rightDistance > leftDistance) {
                    DialogUtil.initialiseSingletonDialog(RemoteDialog.dialog, "Remote", new Rectangle((int)(displayWidth - 485.0), DesktopApp.frame().getY(), 485, DesktopApp.frame().getHeight()));
                }
                else {
                    DialogUtil.initialiseSingletonDialog(RemoteDialog.dialog, "Remote", new Rectangle(0, DesktopApp.frame().getY(), 485, DesktopApp.frame().getHeight()));
                }
            }
            else {
                DialogUtil.initialiseSingletonDialog(RemoteDialog.dialog, "Remote", SettingsNetwork.networkTabPosition());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public RemoteDialog() {
        super(null, ModalityType.DOCUMENT_MODAL);
        this.gameJoinablePanel = new GameJoinablePanel(this);
        this.gameJoinedPanel = new GameJoinedPanel(this);
        this.gameOtherPanel = new GameOtherPanel(this);
        this.tournamentJoinablePanel = new TournamentJoinablePanel(this);
        this.tournamentJoinedPanel = new TournamentJoinedPanel(this);
        this.tournamentHostPanel = new TournamentHostPanel(this);
        this.createButton = new JButton("Create");
        this.leaveButton = new JButton("Leave");
        this.refreshButton = new JButton("Refresh");
        this.okButton = new JButton("Join");
        this.startButton = new JButton("Start");
        this.selectedPanel = null;
        this.getContentPane().setLayout(new BorderLayout(0, 0));
        final JTabbedPane tabbedPane = new JTabbedPane(1);
        this.getContentPane().add(tabbedPane, "Center");
        final JPanel remotePanel = new JPanel();
        tabbedPane.addTab("Remote", null, remotePanel, null);
        remotePanel.setLayout(null);
        final JButton btnLogin = new JButton("Login");
        remotePanel.add(btnLogin);
        btnLogin.setEnabled(false);
        btnLogin.setBounds(244, 115, 88, 25);
        remotePanel.add(this.textFieldPassword = new JPasswordField());
        this.textFieldPassword.setColumns(10);
        this.textFieldPassword.setBounds(244, 85, 198, 19);
        remotePanel.add(this.textFieldUsername = new JTextField());
        this.textFieldUsername.setColumns(10);
        this.textFieldUsername.setBounds(244, 54, 198, 19);
        this.textFieldUsername.setText(SettingsNetwork.getLoginUsername());
        final JLabel lblPassword = new JLabel("Ludii Forum Password");
        remotePanel.add(lblPassword);
        lblPassword.setBounds(22, 85, 175, 15);
        final JLabel lblUsername = new JLabel("Ludii Forum Username");
        remotePanel.add(lblUsername);
        lblUsername.setBounds(22, 57, 181, 15);
        final JButton btnLogout = new JButton("Logout");
        remotePanel.add(btnLogout);
        btnLogout.setEnabled(false);
        btnLogout.setBounds(354, 117, 88, 25);
        final JEditorPane editorPane_1 = new JEditorPane();
        remotePanel.add(editorPane_1);
        final int chatMessagHeight = Math.max(70, DesktopApp.frame().getHeight() - 430);
        editorPane_1.setBounds(22, 304, 420, chatMessagHeight);
        final JButton btnSend = new JButton("Send");
        remotePanel.add(btnSend);
        btnSend.setBounds(244, 267, 198, 25);
        final JLabel lblGameChat = new JLabel("Send Game Chat Message");
        remotePanel.add(lblGameChat);
        lblGameChat.setBounds(22, 267, 281, 26);
        final JButton btnMessagePerson = new JButton("Message Person");
        remotePanel.add(btnMessagePerson);
        btnMessagePerson.setBounds(22, 201, 198, 25);
        final JCheckBox chckbxRememberDetails = new JCheckBox("Remember Me");
        remotePanel.add(chckbxRememberDetails);
        chckbxRememberDetails.setBounds(20, 118, 129, 23);
        final JButton btnLeaderboard = new JButton("Leaderboard");
        remotePanel.add(btnLeaderboard);
        btnLeaderboard.setBounds(244, 201, 198, 25);
        final JLabel lblLoginUsingYou = new JLabel("Register at https://ludii.games");
        remotePanel.add(lblLoginUsingYou);
        lblLoginUsingYou.setBounds(22, 22, 385, 20);
        final JSeparator separator_3 = new JSeparator();
        remotePanel.add(separator_3);
        separator_3.setBounds(0, 154, 465, 8);
        final JSeparator separator_4 = new JSeparator();
        remotePanel.add(separator_4);
        separator_4.setBounds(0, 248, 465, 8);
        final JLabel lblNewLabel = new JLabel("Actions");
        remotePanel.add(lblNewLabel);
        lblNewLabel.setBounds(22, 174, 61, 16);
        final JPanel gamePanel = new JPanel();
        tabbedPane.addTab("Games", null, gamePanel, null);
        gamePanel.setLayout(new GridLayout(0, 1, 0, 0));
        gamePanel.add(this.gameJoinedPanel);
        final JLabel lblNewLabel_1 = new JLabel("Games You Are Playing");
        this.gameJoinedPanel.add(lblNewLabel_1, "North");
        gamePanel.add(this.gameJoinablePanel);
        final JLabel label = new JLabel("Games You Can Join");
        this.gameJoinablePanel.add(label, "North");
        gamePanel.add(this.gameOtherPanel);
        final JLabel lblOtherGames = new JLabel("Other Games");
        this.gameOtherPanel.add(lblOtherGames, "North");
        final JPanel tournamentPanel = new JPanel();
        tabbedPane.addTab("Tournaments", null, tournamentPanel, null);
        tournamentPanel.setLayout(new GridLayout(0, 1, 0, 0));
        tournamentPanel.add(this.tournamentJoinedPanel);
        final JLabel lblTournamentCurrentPanel = new JLabel("Tournaments You Are Playing In");
        this.tournamentJoinedPanel.add(lblTournamentCurrentPanel, "North");
        tournamentPanel.add(this.tournamentJoinablePanel);
        final JLabel lblTournamentCanJoin = new JLabel("Tournaments You Can Join");
        this.tournamentJoinablePanel.add(lblTournamentCanJoin, "North");
        tournamentPanel.add(this.tournamentHostPanel);
        final JLabel lblTournamentHosted = new JLabel("Tournaments Hosted by You");
        this.tournamentHostPanel.add(lblTournamentHosted, "North");
        btnLeaderboard.addActionListener(e -> {
            try {
                if (SettingsNetwork.getLoginId() == 0) {
                    Manager.app.addTextToStatusPanel("Please login first.\n");
                }
                else {
                    LeaderboardDialog.showDialog();
                }
            }
            catch (Exception E) {
                E.printStackTrace();
            }
        });
        btnMessagePerson.addActionListener(e -> {
            try {
                if (SettingsNetwork.getLoginId() == 0) {
                    Manager.app.addTextToStatusPanel("Please login first.\n");
                }
                else {
                    MessagePlayersDialog.showDialog();
                }
            }
            catch (Exception E) {
                E.printStackTrace();
            }
        });
        btnSend.addActionListener(e -> {
            try {
                if (SettingsNetwork.getLoginId() == 0) {
                    Manager.app.addTextToStatusPanel("Please login first.\n");
                }
                else if (SettingsNetwork.getActiveGameId() == 0) {
                    Manager.app.addTextToStatusPanel("Please join a game first.\n");
                }
                else {
                    DatabaseFunctions.sendGameChatMessage(editorPane_1.getText());
                    editorPane_1.setText("");
                }
            }
            catch (Exception E) {
                E.printStackTrace();
            }
        });
        btnLogout.addActionListener(e -> {
            try {
                final URL phpLudii = new URL(DatabaseFunctions.appFolderLocation + "appLogout.php?uid=" + SettingsNetwork.getLoginId() + "&secret=" + SettingsNetwork.getSecretNetworkNumber() + "&networkPlayerId=" + SettingsNetwork.getLoginId());
                final URLConnection yc = phpLudii.openConnection();
                try (final BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()))) {
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        try {
                            if (inputLine.charAt(0) != '1') {
                                continue;
                            }
                            SettingsNetwork.restoreAiPlayers();
                            SettingsNetwork.setLoginId(0);
                            SettingsNetwork.setLoginUsername("");
                            SettingsNetwork.setNetworkPlayerNumber(0);
                            SettingsNetwork.setActiveGameId(0);
                            if (SettingsNetwork.getLoginId() == 0) {
                                btnLogout.setEnabled(false);
                                btnLogin.setEnabled(true);
                            }
                            else {
                                btnLogout.setEnabled(true);
                                btnLogin.setEnabled(false);
                            }
                            DesktopApp.frame().setJMenuBar(new MainMenu());
                            tabbedPane.setEnabledAt(1, false);
                            tabbedPane.setEnabledAt(2, false);
                            RemoteDialog.this.createButton.setEnabled(false);
                            RemoteDialog.this.refreshButton.setEnabled(false);
                            RemoteDialog.this.okButton.setEnabled(false);
                            RemoteDialog.this.startButton.setEnabled(false);
                            RemoteDialog.this.leaveButton.setEnabled(false);
                            btnLogin.setEnabled(true);
                            btnLogout.setEnabled(false);
                            RemoteDialog.this.updateSelectedTable(null);
                            RemoteDialog.this.refreshNetworkDialog();
                            RemoteDialog.dialog.repaint();
                            Manager.app.addTextToStatusPanel("You have logged out.\n");
                            RemoteDialog.bringDialogToFront();
                            RemoteDialog.dialogRefreshThread.interrupt();
                            DatabaseFunctions.repeatNetworkActionsThread.interrupt();
                        }
                        catch (Exception ex) {}
                    }
                }
            }
            catch (Exception E) {
                E.printStackTrace();
            }
        });
        btnLogin.addActionListener(e -> {
            try {
                final URL phpLudii = new URL(DatabaseFunctions.appFolderLocation + "appLoginHashed.php?username=" + RemoteDialog.this.textFieldUsername.getText().replace(" ", "%20") + "&password=" + DatabaseFunctions.md5(String.valueOf(RemoteDialog.this.textFieldPassword.getPassword())));
                final URLConnection yc = phpLudii.openConnection();
                try (final BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()))) {
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        try {
                            if (inputLine.startsWith("connected")) {
                                SettingsNetwork.setSecretNetworkNumber(Integer.parseInt(inputLine.substring(10, 16)));
                                SettingsNetwork.setLoginId(Integer.parseInt(inputLine.substring(17)));
                                SettingsNetwork.setLoginUsername(RemoteDialog.this.textFieldUsername.getText());
                                SettingsVC.sandboxMode = false;
                                SettingsNetwork.setRememberDetails(chckbxRememberDetails.isSelected());
                                Manager.app.addTextToStatusPanel("Logged in as: " + SettingsNetwork.getLoginUsername() + ".\n");
                                tabbedPane.setEnabledAt(1, true);
                                tabbedPane.setEnabledAt(2, true);
                                RemoteDialog.this.createButton.setEnabled(false);
                                RemoteDialog.this.refreshButton.setEnabled(false);
                                RemoteDialog.this.okButton.setEnabled(false);
                                RemoteDialog.this.startButton.setEnabled(false);
                                RemoteDialog.this.leaveButton.setEnabled(false);
                                btnLogin.setEnabled(false);
                                btnLogout.setEnabled(true);
                                RemoteDialog.this.updateSelectedTable(null);
                                RemoteDialog.this.refreshNetworkDialog();
                                SettingsNetwork.setNetworkTabPosition(RemoteDialog.dialog.getBounds());
                                if (SettingsNetwork.getLoginId() != 0) {
                                    DatabaseFunctions.repeatNetworkActions();
                                    RemoteDialog.repeatDialogRefresh();
                                }
                                RemoteDialog.dialog.repaint();
                            }
                            else {
                                Manager.app.addTextToStatusPanel(inputLine + ".\n");
                            }
                        }
                        catch (Exception ex) {}
                    }
                }
            }
            catch (Exception E) {
                if (E instanceof FileNotFoundException) {
                    Manager.app.addTextToStatusPanel("Failed to login. Your app version may be out of date. Please make sure you have the latest Ludii version..\n");
                }
                E.printStackTrace();
            }
        });
        final JPanel bottomPane = new JPanel();
        this.getContentPane().add(bottomPane, "South");
        bottomPane.setLayout(new BoxLayout(bottomPane, 2));
        final JPanel buttonPane = new JPanel();
        bottomPane.add(buttonPane);
        buttonPane.setLayout(new GridLayout(0, 5, 5, 0));
        bottomPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.createButton.setActionCommand("Create");
        buttonPane.add(this.createButton);
        this.refreshButton.setActionCommand("Refresh");
        buttonPane.add(this.refreshButton);
        this.okButton.setActionCommand("Join");
        buttonPane.add(this.okButton);
        this.startButton.setActionCommand("Start");
        buttonPane.add(this.startButton);
        this.leaveButton.setActionCommand("Leave");
        buttonPane.add(this.leaveButton);
        this.createButton.addActionListener(e -> {
            try {
                if (tabbedPane.getSelectedIndex() == 2) {
                    CreateTournamentDialog.showDialog(RemoteDialog.dialog);
                }
                else {
                    CreateGameDialog.showDialog(RemoteDialog.dialog);
                }
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
        });
        this.refreshButton.addActionListener(e -> {
            try {
                RemoteDialog.this.refreshNetworkDialog();
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
        });
        this.okButton.addActionListener(e -> {
            try {
                if (RemoteDialog.this.selectedPanel != null) {
                    RemoteDialog.this.selectedPanel.okButtonPressed(RemoteDialog.this.selectedPanel.getTable(), RemoteDialog.dialog);
                }
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
        });
        this.leaveButton.addActionListener(e -> {
            try {
                if (RemoteDialog.this.selectedPanel != null) {
                    RemoteDialog.this.selectedPanel.okButtonPressed(RemoteDialog.this.selectedPanel.getTable(), RemoteDialog.dialog);
                }
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
        });
        this.startButton.addActionListener(e -> {
            try {
                if (RemoteDialog.this.selectedPanel != null) {
                    RemoteDialog.this.selectedPanel.okButtonPressed(RemoteDialog.this.selectedPanel.getTable(), RemoteDialog.dialog);
                }
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
        });
        if (SettingsNetwork.isRememberDetails()) {
            chckbxRememberDetails.setSelected(true);
        }
        if (SettingsNetwork.getLoginId() == 0) {
            btnLogout.setEnabled(false);
            btnLogin.setEnabled(true);
        }
        else {
            btnLogout.setEnabled(true);
            btnLogin.setEnabled(false);
        }
        tabbedPane.setSelectedIndex(SettingsNetwork.tabSelected());
        if (SettingsNetwork.getLoginId() == 0) {
            tabbedPane.setEnabledAt(1, false);
            tabbedPane.setEnabledAt(2, false);
            this.createButton.setEnabled(false);
            this.refreshButton.setEnabled(false);
            this.okButton.setEnabled(false);
            this.startButton.setEnabled(false);
            this.leaveButton.setEnabled(false);
        }
        else {
            this.updateSelectedTable(null);
        }
        tabbedPane.addChangeListener(e -> {
            SettingsNetwork.setTabSelected(tabbedPane.getSelectedIndex());
            SettingsNetwork.setNetworkTabPosition(RemoteDialog.dialog.getBounds());
            RemoteDialog.this.updateSelectedTable(null);
            if (tabbedPane.getSelectedIndex() == 0) {
                RemoteDialog.this.createButton.setEnabled(false);
                RemoteDialog.this.refreshButton.setEnabled(false);
            }
            else {
                RemoteDialog.this.createButton.setEnabled(true);
                RemoteDialog.this.refreshButton.setEnabled(true);
            }
            RemoteDialog.this.refreshNetworkDialog();
        });
        if (tabbedPane.getSelectedIndex() == 0) {
            this.createButton.setEnabled(false);
            this.refreshButton.setEnabled(false);
        }
    }
    
    public static void bringDialogToFront() {
        try {
            EventQueue.invokeLater(() -> RemoteDialog.dialog.toFront());
        }
        catch (Exception E) {
            E.printStackTrace();
        }
    }
    
    public void refreshNetworkDialog() {
        try {
            String priorSelectedRowIdGameJoined = "";
            String priorSelectedRowIdGameJoinable = "";
            String priorSelectedRowIdGameOther = "";
            String priorSelectedRowIdTournamentJoinable = "";
            String priorSelectedRowIdTournamentHost = "";
            String priorSelectedRowIdTournamentJoined = "";
            if (this.gameJoinedPanel.getTable().getSelectedRow() != -1) {
                priorSelectedRowIdGameJoined = this.gameJoinedPanel.getTable().getModel().getValueAt(this.gameJoinedPanel.getTable().getSelectedRow(), 0).toString();
            }
            if (this.gameJoinablePanel.getTable().getSelectedRow() != -1) {
                priorSelectedRowIdGameJoinable = this.gameJoinablePanel.getTable().getModel().getValueAt(this.gameJoinablePanel.getTable().getSelectedRow(), 0).toString();
            }
            if (this.gameOtherPanel.getTable().getSelectedRow() != -1) {
                priorSelectedRowIdGameOther = this.gameOtherPanel.getTable().getModel().getValueAt(this.gameOtherPanel.getTable().getSelectedRow(), 0).toString();
            }
            if (this.tournamentJoinablePanel.getTable().getSelectedRow() != -1) {
                priorSelectedRowIdTournamentJoinable = this.tournamentJoinablePanel.getTable().getModel().getValueAt(this.tournamentJoinablePanel.getTable().getSelectedRow(), 0).toString();
            }
            if (this.tournamentHostPanel.getTable().getSelectedRow() != -1) {
                priorSelectedRowIdTournamentHost = this.tournamentHostPanel.getTable().getModel().getValueAt(this.tournamentHostPanel.getTable().getSelectedRow(), 0).toString();
            }
            if (this.tournamentJoinedPanel.getTable().getSelectedRow() != -1) {
                priorSelectedRowIdTournamentJoined = this.tournamentJoinedPanel.getTable().getModel().getValueAt(this.tournamentJoinedPanel.getTable().getSelectedRow(), 0).toString();
            }
            this.gameJoinedPanel.getTable().setModel(this.gameJoinedPanel.createModel());
            for (int i = 0; i < this.gameJoinedPanel.getTable().getModel().getRowCount(); ++i) {
                if (this.gameJoinedPanel.getTable().getModel().getValueAt(i, 0).toString().equals(priorSelectedRowIdGameJoined)) {
                    this.gameJoinedPanel.getTable().setRowSelectionInterval(i, i);
                }
            }
            new ButtonColumn(this.gameJoinedPanel.getTable(), null, 4);
            this.gameJoinedPanel.getTable().revalidate();
            this.gameJoinedPanel.getTable().repaint();
            this.gameJoinablePanel.getTable().setModel(this.gameJoinablePanel.createModel());
            for (int i = 0; i < this.gameJoinablePanel.getTable().getModel().getRowCount(); ++i) {
                if (this.gameJoinablePanel.getTable().getModel().getValueAt(i, 0).toString().equals(priorSelectedRowIdGameJoinable)) {
                    this.gameJoinablePanel.getTable().setRowSelectionInterval(i, i);
                }
            }
            new ButtonColumn(this.gameJoinablePanel.getTable(), null, 4);
            this.gameJoinablePanel.getTable().revalidate();
            this.gameJoinablePanel.getTable().repaint();
            this.gameOtherPanel.getTable().setModel(this.gameOtherPanel.createModel());
            for (int i = 0; i < this.gameOtherPanel.getTable().getModel().getRowCount(); ++i) {
                if (this.gameOtherPanel.getTable().getModel().getValueAt(i, 0).toString().equals(priorSelectedRowIdGameOther)) {
                    this.gameOtherPanel.getTable().setRowSelectionInterval(i, i);
                }
            }
            new ButtonColumn(this.gameOtherPanel.getTable(), null, 3);
            this.gameOtherPanel.getTable().revalidate();
            this.gameOtherPanel.getTable().repaint();
            this.tournamentJoinablePanel.getTable().setModel(this.tournamentJoinablePanel.createModel());
            for (int i = 0; i < this.tournamentJoinablePanel.getTable().getModel().getRowCount(); ++i) {
                if (this.tournamentJoinablePanel.getTable().getModel().getValueAt(i, 0).toString().equals(priorSelectedRowIdTournamentJoinable)) {
                    this.tournamentJoinablePanel.getTable().setRowSelectionInterval(i, i);
                }
            }
            new ButtonColumn(this.tournamentJoinablePanel.getTable(), null, 5);
            this.tournamentJoinablePanel.getTable().revalidate();
            this.tournamentJoinablePanel.getTable().repaint();
            this.tournamentHostPanel.getTable().setModel(this.tournamentHostPanel.createModel());
            for (int i = 0; i < this.tournamentHostPanel.getTable().getModel().getRowCount(); ++i) {
                if (this.tournamentHostPanel.getTable().getModel().getValueAt(i, 0).toString().equals(priorSelectedRowIdTournamentHost)) {
                    this.tournamentHostPanel.getTable().setRowSelectionInterval(i, i);
                }
            }
            new ButtonColumn(this.tournamentHostPanel.getTable(), null, 5);
            this.tournamentHostPanel.getTable().revalidate();
            this.tournamentHostPanel.getTable().repaint();
            this.tournamentJoinedPanel.getTable().setModel(this.tournamentJoinedPanel.createModel());
            for (int i = 0; i < this.tournamentJoinedPanel.getTable().getModel().getRowCount(); ++i) {
                if (this.tournamentJoinedPanel.getTable().getModel().getValueAt(i, 0).toString().equals(priorSelectedRowIdTournamentJoined)) {
                    this.tournamentJoinedPanel.getTable().setRowSelectionInterval(i, i);
                }
            }
            new ButtonColumn(this.tournamentJoinedPanel.getTable(), null, 5);
            this.tournamentJoinedPanel.getTable().revalidate();
            this.tournamentJoinedPanel.getTable().repaint();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void updateSelectedTable(final BaseFindPanel baseFindPanel) {
        this.selectedPanel = baseFindPanel;
        if (baseFindPanel instanceof GameJoinedPanel) {
            this.gameJoinablePanel.getTable().clearSelection();
            this.gameOtherPanel.getTable().clearSelection();
            this.tournamentJoinablePanel.getTable().clearSelection();
            this.tournamentHostPanel.getTable().clearSelection();
            this.tournamentJoinedPanel.getTable().clearSelection();
            this.leaveButton.setEnabled(false);
            this.okButton.setEnabled(true);
            this.startButton.setEnabled(false);
        }
        else if (baseFindPanel instanceof GameJoinablePanel) {
            this.gameJoinedPanel.getTable().clearSelection();
            this.gameOtherPanel.getTable().clearSelection();
            this.tournamentJoinablePanel.getTable().clearSelection();
            this.tournamentHostPanel.getTable().clearSelection();
            this.tournamentJoinedPanel.getTable().clearSelection();
            this.leaveButton.setEnabled(false);
            this.okButton.setEnabled(true);
            this.startButton.setEnabled(false);
        }
        else if (baseFindPanel instanceof GameOtherPanel) {
            this.gameJoinedPanel.getTable().clearSelection();
            this.gameJoinablePanel.getTable().clearSelection();
            this.tournamentJoinablePanel.getTable().clearSelection();
            this.tournamentHostPanel.getTable().clearSelection();
            this.tournamentJoinedPanel.getTable().clearSelection();
            this.leaveButton.setEnabled(false);
            this.okButton.setEnabled(false);
            this.startButton.setEnabled(false);
        }
        else if (baseFindPanel instanceof TournamentJoinablePanel) {
            this.gameJoinedPanel.getTable().clearSelection();
            this.gameJoinablePanel.getTable().clearSelection();
            this.gameOtherPanel.getTable().clearSelection();
            this.tournamentHostPanel.getTable().clearSelection();
            this.tournamentJoinedPanel.getTable().clearSelection();
            this.leaveButton.setEnabled(false);
            this.okButton.setEnabled(true);
            this.startButton.setEnabled(false);
        }
        else if (baseFindPanel instanceof TournamentHostPanel) {
            this.gameJoinedPanel.getTable().clearSelection();
            this.gameJoinablePanel.getTable().clearSelection();
            this.gameOtherPanel.getTable().clearSelection();
            this.tournamentJoinablePanel.getTable().clearSelection();
            this.tournamentJoinedPanel.getTable().clearSelection();
            this.leaveButton.setEnabled(false);
            this.okButton.setEnabled(false);
            this.startButton.setEnabled(true);
        }
        else if (baseFindPanel instanceof TournamentJoinedPanel) {
            this.gameJoinedPanel.getTable().clearSelection();
            this.gameJoinablePanel.getTable().clearSelection();
            this.gameOtherPanel.getTable().clearSelection();
            this.tournamentJoinablePanel.getTable().clearSelection();
            this.tournamentHostPanel.getTable().clearSelection();
            this.leaveButton.setEnabled(true);
            this.okButton.setEnabled(false);
            this.startButton.setEnabled(false);
        }
        else {
            this.gameJoinedPanel.getTable().clearSelection();
            this.gameJoinablePanel.getTable().clearSelection();
            this.gameOtherPanel.getTable().clearSelection();
            this.tournamentJoinablePanel.getTable().clearSelection();
            this.tournamentHostPanel.getTable().clearSelection();
            this.tournamentJoinedPanel.getTable().clearSelection();
            this.leaveButton.setEnabled(false);
            this.okButton.setEnabled(false);
            this.startButton.setEnabled(false);
        }
    }
    
    public static void repeatDialogRefresh() {
        final long timeInterval = 5000L;
        final Runnable runnable = () -> {
            while (SettingsNetwork.getLoginId() != 0) {
                EventQueue.invokeLater(() -> RemoteDialog.dialog.refreshNetworkDialog());
                try {
                    Thread.sleep(5000L);
                }
                catch (InterruptedException ex) {}
            }
            RemoteDialog.dialogRefreshThread.interrupt();
        };
        (RemoteDialog.dialogRefreshThread = new Thread(runnable)).start();
    }
}
