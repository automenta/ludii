// 
// Decompiled by Procyon v0.5.36
// 

package app.display.dialogs.remote.panels.games;

import app.display.dialogs.remote.RemoteDialog;
import app.display.dialogs.remote.panels.OnlineGameInfo;
import app.game.GameSetupDesktop;
import manager.network.DatabaseFunctions;
import manager.network.SettingsNetwork;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;

public class GameJoinablePanel extends BaseGamePanel
{
    private static final long serialVersionUID = 1L;
    
    public GameJoinablePanel(final RemoteDialog parentDialog) {
        super(parentDialog);
    }
    
    @Override
    protected String[] findJoinableGames() {
        return DatabaseFunctions.findJoinableGames().split("_next_");
    }
    
    @Override
    public DefaultTableModel createModel() {
        this.tableStoredInformation.clear();
        final String[] joinableGames = this.findJoinableGames();
        final DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Id");
        model.addColumn("Game");
        model.addColumn("#");
        model.addColumn("Players");
        model.addColumn("Details");
        this.comboBox.clear();
        for (int i = 0; i < joinableGames.length; ++i) {
            try {
                final String gameDetails = joinableGames[i];
                final String[] splitDetails = gameDetails.split("NEXT_COL");
                final JComboBox<String> comboBoxActiveTemp = new JComboBox<>();
                comboBoxActiveTemp.addItem("Any");
                for (int j = 0; j < splitDetails[3].split(",").length - 1; ++j) {
                    comboBoxActiveTemp.addItem(splitDetails[3].split(",")[j].trim());
                }
                this.comboBox.add(new DefaultCellEditor(comboBoxActiveTemp));
                String playerTimeString = "";
                if (Integer.parseInt(splitDetails[6]) % 60 == 0) {
                    playerTimeString = Integer.parseInt(splitDetails[6]) / 60 + ":00";
                }
                else {
                    playerTimeString = Integer.parseInt(splitDetails[6]) / 60 + ":" + Integer.parseInt(splitDetails[6]) % 60;
                }
                if (playerTimeString.equals("0:00")) {
                    playerTimeString = "";
                }
                final OnlineGameInfo info = new OnlineGameInfo();
                info.setGameId(splitDetails[0]);
                info.setGameName(splitDetails[1]);
                info.setHostUsername(splitDetails[2]);
                info.setMaxPlayerNumber(splitDetails[4]);
                info.setTimeRemainingForPlayer(playerTimeString);
                info.setPassword(splitDetails[7]);
                info.setJoinedPlayers(splitDetails[8]);
                info.setPrivateGame(splitDetails[9]);
                info.setOptions(splitDetails[10].replaceAll("_NEXT_", " "));
                info.setAppVersion(splitDetails[11]);
                info.setNotes(splitDetails[12]);
                info.setGameHash(splitDetails[13]);
                this.tableStoredInformation.add(info);
                model.addRow(new Object[] { splitDetails[0], splitDetails[1].substring(0, splitDetails[1].length() - 4), "Any", splitDetails[8], "Details" });
            }
            catch (Exception ex) {}
        }
        return model;
    }
    
    @Override
    public JTable createTable(final DefaultTableModel model) {
        final JTable table = new JTable(model) {
            private static final long serialVersionUID = 1L;
            
            @Override
            public boolean isCellEditable(final int row, final int column) {
                return column == 2;
            }
            
            @Override
            public TableCellEditor getCellEditor(final int row, final int column) {
                final int modelColumn = this.convertColumnIndexToModel(column);
                if (modelColumn == 2) {
                    return GameJoinablePanel.this.comboBox.get(row);
                }
                return super.getCellEditor(row, column);
            }
        };
        table.getColumnModel().getColumn(0).setPreferredWidth(10);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(35);
        table.getColumnModel().getColumn(3).setPreferredWidth(200);
        table.getColumnModel().getColumn(4).setPreferredWidth(35);
        table.getTableHeader().setReorderingAllowed(false);
        this.add(table);
        final JScrollPane scrollPane = new JScrollPane(table);
        this.add(scrollPane, "Center");
        scrollPane.setHorizontalScrollBarPolicy(31);
        return table;
    }
    
    @Override
    public void okButtonPressed(final JTable table, final RemoteDialog parentDialog) {
        if (this.gameValidityCheck(table)) {
            final String selectedGameID = this.tableStoredInformation.get(table.getSelectedRow()).getGameId();
            String selectedPlayerNumber = table.getModel().getValueAt(table.getSelectedRow(), 2).toString();
            if (selectedPlayerNumber.equals("Any")) {
                selectedPlayerNumber = "0";
            }
            final String isPassword = this.tableStoredInformation.get(table.getSelectedRow()).getPassword();
            try {
                final String gameName = this.tableStoredInformation.get(table.getSelectedRow()).getGameName();
                final List<String> gameOptions = Arrays.asList(this.tableStoredInformation.get(table.getSelectedRow()).getOptions().split("\\s+"));
                if (isPassword.equals("Yes")) {
                    final String password = JOptionPane.showInputDialog(null, "Please enter the password", "Password", 1);
                    try {
                        final URL phpLudii = new URL(DatabaseFunctions.appFolderLocation + "joinGamePassword.php?game=" + selectedGameID + "&id=" + SettingsNetwork.getLoginId() + "&password=" + password.replace(" ", "%20") + "&number=" + selectedPlayerNumber + "&secret=" + SettingsNetwork.getSecretNetworkNumber() + "&networkPlayerId=" + SettingsNetwork.getLoginId());
                        final URLConnection yc = phpLudii.openConnection();
                        try (final BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()))) {
                            SettingsNetwork.setActiveGameId(Integer.parseInt(selectedGameID));
                            String inputLine;
                            while ((inputLine = in.readLine()) != null) {
                                GameSetupDesktop.setupNetworkGame(gameName, gameOptions, inputLine);
                            }
                            in.close();
                        }
                    }
                    catch (Exception ex) {}
                }
                else {
                    final URL phpLudii2 = new URL(DatabaseFunctions.appFolderLocation + "joinGame.php?game=" + selectedGameID + "&id=" + SettingsNetwork.getLoginId() + "&number=" + selectedPlayerNumber + "&secret=" + SettingsNetwork.getSecretNetworkNumber() + "&networkPlayerId=" + SettingsNetwork.getLoginId());
                    final URLConnection yc2 = phpLudii2.openConnection();
                    try (final BufferedReader in2 = new BufferedReader(new InputStreamReader(yc2.getInputStream()))) {
                        SettingsNetwork.setActiveGameId(Integer.parseInt(selectedGameID));
                        String inputLine2;
                        while ((inputLine2 = in2.readLine()) != null) {
                            GameSetupDesktop.setupNetworkGame(gameName, gameOptions, inputLine2);
                        }
                        in2.close();
                    }
                }
            }
            catch (Exception E) {
                E.printStackTrace();
            }
            SettingsNetwork.setNetworkTabPosition(parentDialog.getBounds());
            parentDialog.refreshNetworkDialog();
        }
    }
    
    void displayExtraInformation(final int modelRow) {
        System.out.println(modelRow);
    }
}
