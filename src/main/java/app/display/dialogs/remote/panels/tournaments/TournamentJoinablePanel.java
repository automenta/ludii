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

public class TournamentJoinablePanel extends BaseFindTournamentPanel
{
    private static final long serialVersionUID = 1L;
    
    public TournamentJoinablePanel(final RemoteDialog parentDialog) {
        super(parentDialog);
    }
    
    @Override
    protected String[] findJoinableGames() {
        return DatabaseFunctions.findJoinableTournaments().split("_next_");
    }
    
    @Override
    public void okButtonPressed(final JTable table, final RemoteDialog parentDialog) {
        if (this.tournamentValidityCheck(table)) {
            final String selectedTournamentID = this.tableStoredInformation.get(table.getSelectedRow()).getTournamentId();
            final String isPassword = this.tableStoredInformation.get(table.getSelectedRow()).getPassword();
            try {
                if (isPassword.equals("Yes")) {
                    final String password = JOptionPane.showInputDialog(null, "Please enter the password", "Password", 1);
                    try {
                        final URL phpLudii = new URL(DatabaseFunctions.appFolderLocation + "joinTournamentPassword.php?game=" + selectedTournamentID + "&id=" + SettingsNetwork.getLoginId() + "&password=" + password.replace(" ", "%20") + "&secret=" + SettingsNetwork.getSecretNetworkNumber() + "&networkPlayerId=" + SettingsNetwork.getLoginId());
                        final URLConnection yc = phpLudii.openConnection();
                        try (final BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()))) {
                            String inputLine;
                            while ((inputLine = in.readLine()) != null) {
                                RemoteDialog.bringDialogToFront();
                                Manager.app.addTextToStatusPanel(inputLine + "\n");
                            }
                        }
                    }
                    catch (Exception ex) {}
                }
                else {
                    final URL phpLudii2 = new URL(DatabaseFunctions.appFolderLocation + "joinTournament.php?game=" + selectedTournamentID + "&id=" + SettingsNetwork.getLoginId() + "&secret=" + SettingsNetwork.getSecretNetworkNumber() + "&networkPlayerId=" + SettingsNetwork.getLoginId());
                    final URLConnection yc2 = phpLudii2.openConnection();
                    try (final BufferedReader in2 = new BufferedReader(new InputStreamReader(yc2.getInputStream()))) {
                        String inputLine2;
                        while ((inputLine2 = in2.readLine()) != null) {
                            RemoteDialog.bringDialogToFront();
                            Manager.app.addTextToStatusPanel(inputLine2 + "\n");
                        }
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
