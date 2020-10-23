// 
// Decompiled by Procyon v0.5.36
// 

package app.display.dialogs.remote.panels.tournaments;

import app.display.dialogs.remote.RemoteDialog;
import app.display.dialogs.remote.panels.BaseFindPanel;
import app.display.dialogs.remote.panels.OnlineGameInfo;
import manager.Manager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public abstract class BaseFindTournamentPanel extends BaseFindPanel
{
    private static final long serialVersionUID = 1L;
    
    public BaseFindTournamentPanel(final RemoteDialog parentDialog) {
        super(parentDialog);
    }
    
    @Override
    public DefaultTableModel createModel() {
        this.tableStoredInformation.clear();
        final String[] joinableGames = this.findJoinableGames();
        final DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Id");
        model.addColumn("Game");
        model.addColumn("Name");
        model.addColumn("Host");
        model.addColumn("#");
        model.addColumn("Details");
        for (int i = 0; i < joinableGames.length; ++i) {
            try {
                final String gameDetails = joinableGames[i];
                final String[] splitDetails = gameDetails.split("NEXT_COL");
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
                String roundTimeString = "";
                if (Integer.parseInt(splitDetails[7]) % 60 == 0) {
                    roundTimeString = Integer.parseInt(splitDetails[7]) / 60 + ":00";
                }
                else {
                    roundTimeString = Integer.parseInt(splitDetails[7]) / 60 + ":" + Integer.parseInt(splitDetails[7]) % 60;
                }
                if (roundTimeString.equals("0:00")) {
                    roundTimeString = "";
                }
                final OnlineGameInfo info = new OnlineGameInfo();
                info.setTournamentId(splitDetails[0]);
                info.setTournamentName(splitDetails[1]);
                info.setGameName(splitDetails[2]);
                info.setTournamentFormat(splitDetails[3]);
                info.setHostUsername(splitDetails[4]);
                info.setMaxPlayerNumber(splitDetails[5]);
                info.setTimeRemainingForPlayer(playerTimeString);
                info.setTimeRemainingForRound(roundTimeString);
                info.setPassword(splitDetails[8]);
                info.setJoinedPlayers(splitDetails[9]);
                info.setPrivateGame(splitDetails[10]);
                info.setOptions(splitDetails[11].replaceAll("_NEXT_", " "));
                info.setAppVersion(splitDetails[12]);
                info.setNotes(splitDetails[13]);
                this.tableStoredInformation.add(info);
                final String numberJoinedPlayers = splitDetails[9].split(":")[0];
                model.addRow(new Object[] { splitDetails[0], splitDetails[2].substring(0, splitDetails[2].length() - 4), splitDetails[1], splitDetails[4], numberJoinedPlayers, "Details" });
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
                return false;
            }
        };
        table.getColumnModel().getColumn(0).setPreferredWidth(10);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(35);
        table.getColumnModel().getColumn(5).setPreferredWidth(35);
        table.getTableHeader().setReorderingAllowed(false);
        this.add(table);
        final JScrollPane scrollPane = new JScrollPane(table);
        this.add(scrollPane);
        scrollPane.setHorizontalScrollBarPolicy(31);
        return table;
    }
    
    public boolean tournamentValidityCheck(final JTable table) {
        if (this.tableStoredInformation.get(table.getSelectedRow()).getTournamentId() == "") {
            Manager.app.addTextToStatusPanel("Please select a tournament.\n");
            return false;
        }
        if (!this.tableStoredInformation.get(table.getSelectedRow()).getAppVersion().equals("1.0.8")) {
            Manager.app.addTextToStatusPanel("You have Ludii version 1.0.8, please select a tournament with the same version number.\n");
            return false;
        }
        return true;
    }
}
