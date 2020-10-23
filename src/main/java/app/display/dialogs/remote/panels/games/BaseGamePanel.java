// 
// Decompiled by Procyon v0.5.36
// 

package app.display.dialogs.remote.panels.games;

import app.display.dialogs.remote.RemoteDialog;
import app.display.dialogs.remote.panels.BaseFindPanel;
import app.loading.GameLoading;
import manager.Manager;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

public abstract class BaseGamePanel extends BaseFindPanel
{
    private static final long serialVersionUID = 1L;
    
    public BaseGamePanel(final RemoteDialog parentDialog) {
        super(parentDialog);
    }
    
    public boolean gameValidityCheck(final JTable table) {
        final String gameName = this.tableStoredInformation.get(table.getSelectedRow()).getGameName();
        final List<String> gameOptions = Arrays.asList(this.tableStoredInformation.get(table.getSelectedRow()).getOptions().split("\\s+"));
        if (!gameOptions.get(0).equals("-")) {
            for (int i = 0; i < gameOptions.size(); ++i) {
                gameOptions.set(i, gameOptions.get(i).replaceAll("_", " ").replaceAll("\\|", "/"));
            }
        }
        final int expectedGameHashcode = GameLoading.getGameDescriptionRawFromName(gameName).hashCode();
        if (this.tableStoredInformation.get(table.getSelectedRow()).getGameId() == "") {
            Manager.app.addTextToStatusPanel("Please select a game.\n");
            return false;
        }
        if (!this.tableStoredInformation.get(table.getSelectedRow()).getAppVersion().equals("1.0.8")) {
            Manager.app.addTextToStatusPanel("You have Ludii version 1.0.8, please select a game with the same version number.\n");
            return false;
        }
        if (Integer.parseInt(this.tableStoredInformation.get(table.getSelectedRow()).getGameHash()) != expectedGameHashcode) {
            Manager.app.addTextToStatusPanel("The hashcodes of the games do not match, suspected game description modification.\n");
            return false;
        }
        return true;
    }
}
