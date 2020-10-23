// 
// Decompiled by Procyon v0.5.36
// 

package app.display.dialogs.remote.panels.games;

import app.display.dialogs.remote.RemoteDialog;
import app.display.dialogs.remote.panels.OnlineGameInfo;
import manager.network.DatabaseFunctions;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class GameOtherPanel extends BaseGamePanel
{
    private static final long serialVersionUID = 1L;
    
    public GameOtherPanel(final RemoteDialog parentDialog) {
        super(parentDialog);
    }
    
    @Override
    protected String[] findJoinableGames() {
        return DatabaseFunctions.findOtherGames().split("_next_");
    }
    
    @Override
    public DefaultTableModel createModel() {
        this.tableStoredInformation.clear();
        final String[] joinableGames = this.findJoinableGames();
        final DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Id");
        model.addColumn("Game");
        model.addColumn("Players");
        model.addColumn("Details");
        for (String joinableGame : joinableGames) {
            try {
                final String gameDetails = joinableGame;
                final String[] splitDetails = gameDetails.split("NEXT_COL");
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
                info.setPassword(splitDetails[7]);
                info.setJoinedPlayers(splitDetails[8]);
                info.setPrivateGame(splitDetails[9]);
                info.setOptions(splitDetails[10].replaceAll("_NEXT_", " "));
                info.setAppVersion(splitDetails[11]);
                info.setNotes(splitDetails[12]);
                info.setGameHash(splitDetails[13]);
                this.tableStoredInformation.add(info);
                model.addRow(new Object[]{splitDetails[0], splitDetails[1].substring(0, splitDetails[1].length() - 4), splitDetails[8], "Details"});
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
        };
        table.getColumnModel().getColumn(0).setPreferredWidth(10);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(200);
        table.getColumnModel().getColumn(3).setPreferredWidth(35);
        table.getTableHeader().setReorderingAllowed(false);
        this.add(table);
        final JScrollPane scrollPane = new JScrollPane(table);
        this.add(scrollPane, "Center");
        scrollPane.setHorizontalScrollBarPolicy(31);
        return table;
    }
    
    @Override
    public void okButtonPressed(final JTable table, final RemoteDialog parentDialog) {
    }
    
    void displayExtraInformation(final int modelRow) {
        System.out.println(modelRow);
    }
}
