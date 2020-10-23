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

public class TournamentJoinedPanel extends BaseFindTournamentPanel
{
    private static final long serialVersionUID = 1L;
    
    public TournamentJoinedPanel(final RemoteDialog parentDialog) {
        super(parentDialog);
    }
    
    @Override
    protected String[] findJoinableGames() {
        return DatabaseFunctions.findJoinedTournaments().split("_next_");
    }
    
    @Override
    public void okButtonPressed(final JTable table, final RemoteDialog parentDialog) {
        if (this.tournamentValidityCheck(table)) {
            final String selectedTournamentID = this.tableStoredInformation.get(table.getSelectedRow()).getTournamentId();
            try {
                final URL phpLudii = new URL(DatabaseFunctions.appFolderLocation + "leaveTournament.php?game=" + selectedTournamentID + "&id=" + SettingsNetwork.getLoginId() + "&secret=" + SettingsNetwork.getSecretNetworkNumber() + "&networkPlayerId=" + SettingsNetwork.getLoginId());
                final URLConnection yc = phpLudii.openConnection();
                try (final BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()))) {
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        RemoteDialog.bringDialogToFront();
                        Manager.app.addTextToStatusPanel(inputLine + "\n");
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
