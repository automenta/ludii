// 
// Decompiled by Procyon v0.5.36
// 

package app.display.dialogs.remote.panels.tournaments;

import app.display.dialogs.remote.RemoteDialog;
import manager.Manager;
import manager.network.DatabaseFunctions;
import manager.network.SettingsNetwork;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class TournamentHostPanel extends BaseFindTournamentPanel
{
    private static final long serialVersionUID = 1L;
    protected String okButtonMessage;
    
    public TournamentHostPanel(final RemoteDialog parentDialog) {
        super(parentDialog);
        this.okButtonMessage = "Start";
    }
    
    @Override
    protected String[] findJoinableGames() {
        return DatabaseFunctions.findHostedTournaments().split("_next_");
    }
    
    @Override
    public void okButtonPressed(final JTable table, final RemoteDialog parentDialog) {
        if (this.tableStoredInformation.get(table.getSelectedRow()).getJoinedPlayers().split(",").length < Integer.parseInt(this.tableStoredInformation.get(table.getSelectedRow()).getMaxPlayerNumber())) {
            Manager.app.addTextToStatusPanel("Tournament must have as many players as the game being played before it can start.\n");
        }
        else if (this.tournamentValidityCheck(table)) {
            final String selectedTournamentID = this.tableStoredInformation.get(table.getSelectedRow()).getTournamentId();
            try {
                final URL phpLudii = new URL(DatabaseFunctions.appFolderLocation + "startTournamentGames.php?tournamentId=" + selectedTournamentID + "&id=" + SettingsNetwork.getActiveGameId() + "&secret=" + SettingsNetwork.getSecretNetworkNumber() + "&networkPlayerId=" + SettingsNetwork.getLoginId());
                final URLConnection yc = phpLudii.openConnection();
                try (final BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()))) {
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        RemoteDialog.bringDialogToFront();
                        Manager.app.addTextToStatusPanel(inputLine + "\n");
                    }
                }
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
            SettingsNetwork.setNetworkTabPosition(parentDialog.getBounds());
            parentDialog.refreshNetworkDialog();
        }
    }
}
