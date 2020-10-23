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
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;

public class GameJoinedPanel extends BaseGamePanel
{
    private static final long serialVersionUID = 1L;
    
    public GameJoinedPanel(final RemoteDialog parentDialog) {
        super(parentDialog);
    }
    
    @Override
    protected String[] findJoinableGames() {
        return DatabaseFunctions.findActiveGames().split("_next_");
    }
    
    @Override
    public DefaultTableModel createModel() {
        this.tableStoredInformation.clear();
        final String[] joinableGames = this.findJoinableGames();
        final DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Id");
        model.addColumn("Game");
        model.addColumn("Time");
        model.addColumn("Players");
        model.addColumn("Details");
        for (String joinableGame : joinableGames) {
            try {
                final String gameDetails = joinableGame;
                final String[] splitDetails = gameDetails.split("NEXT_COL");
                String tournamentId = splitDetails[10];
                if (tournamentId.equals("0")) {
                    tournamentId = "";
                }
                String playerTimeString = "";
                if (Integer.parseInt(splitDetails[6]) % 60 == 0) {
                    playerTimeString = Integer.parseInt(splitDetails[6]) / 60 + ":00";
                } else {
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
                info.setCurrentMover(splitDetails[7]);
                info.setJoinedPlayers(splitDetails[8]);
                info.setOptions(splitDetails[9].replaceAll("_NEXT_", " "));
                info.setTournamentId(tournamentId);
                info.setAppVersion(splitDetails[11]);
                info.setNotes(splitDetails[12]);
                info.setGameHash(splitDetails[13]);
                this.tableStoredInformation.add(info);
                model.addRow(new Object[]{info.getGameId(), info.getGameName().substring(0, info.getGameName().length() - 4), info.getTimeRemainingForPlayer(), info.getJoinedPlayers(), "Details"});
            } catch (Exception ex) {
            }
        }
        return model;
    }
    
    @Override
    public JTable createTable(final DefaultTableModel model) {
        final JTable table = new JTable(model) {
            private static final long serialVersionUID = 1L;
            
            @Override
            public boolean isCellEditable(final int row, final int column) {
                return false;
            }
            
            @Override
            public Component prepareRenderer(final TableCellRenderer renderer, final int row, final int column) {
                final Object value = this.getValueAt(row, column);
                final Component c = super.prepareRenderer(renderer, row, column);
                if (Integer.parseInt(GameJoinedPanel.this.tableStoredInformation.get(row).getMaxPlayerNumber()) == GameJoinedPanel.this.tableStoredInformation.get(row).getJoinedPlayers().split(",").length && GameJoinedPanel.this.tableStoredInformation.get(row).getCurrentMover().equals(SettingsNetwork.getLoginUsername())) {
                    c.setBackground(new Color(255, 235, 235));
                }
                else {
                    c.setBackground(Color.WHITE);
                }
                boolean isSelected = false;
                boolean hasFocus = false;
                if (!this.isPaintingForPrint()) {
                    isSelected = this.isCellSelected(row, column);
                    final boolean rowIsLead = this.selectionModel.getLeadSelectionIndex() == row;
                    final boolean colIsLead = this.columnModel.getSelectionModel().getLeadSelectionIndex() == column;
                    hasFocus = (rowIsLead && colIsLead && this.isFocusOwner());
                }
                return renderer.getTableCellRendererComponent(this, value, isSelected, hasFocus, row, column);
            }
        };
        table.getColumnModel().getColumn(0).setPreferredWidth(10);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(35);
        table.getColumnModel().getColumn(3).setPreferredWidth(200);
        table.getColumnModel().getColumn(4).setPreferredWidth(35);
        table.getTableHeader().setReorderingAllowed(false);
        this.add(table);
        final JScrollPane scrollPaneContinue = new JScrollPane(table);
        this.add(scrollPaneContinue, "Center");
        scrollPaneContinue.setHorizontalScrollBarPolicy(31);
        return table;
    }
    
    @Override
    public void okButtonPressed(final JTable table, final RemoteDialog parentDialog) {
        final String gameName = this.tableStoredInformation.get(table.getSelectedRow()).getGameName();
        final List<String> gameOptions = Arrays.asList(this.tableStoredInformation.get(table.getSelectedRow()).getOptions().split("\\s+"));
        if (this.gameValidityCheck(table)) {
            final String selectedGameID = this.tableStoredInformation.get(table.getSelectedRow()).getGameId();
            try {
                final URL phpLudii = new URL(DatabaseFunctions.appFolderLocation + "continueGame.php?game=" + selectedGameID + "&id=" + SettingsNetwork.getLoginId() + "&number=" + 0 + "&secret=" + SettingsNetwork.getSecretNetworkNumber() + "&networkPlayerId=" + SettingsNetwork.getLoginId());
                final URLConnection yc = phpLudii.openConnection();
                try (final BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()))) {
                    SettingsNetwork.setActiveGameId(Integer.parseInt(selectedGameID));
                    if (!this.tableStoredInformation.get(table.getSelectedRow()).getTournamentId().isEmpty()) {
                        SettingsNetwork.setTournamentId(Integer.parseInt(this.tableStoredInformation.get(table.getSelectedRow()).getTournamentId()));
                    }
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        GameSetupDesktop.setupNetworkGame(gameName, gameOptions, inputLine);
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
}
