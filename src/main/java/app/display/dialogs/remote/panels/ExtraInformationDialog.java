// 
// Decompiled by Procyon v0.5.36
// 

package app.display.dialogs.remote.panels;

import app.display.dialogs.util.DialogUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Style;
import javax.swing.text.StyledDocument;
import java.awt.*;

public class ExtraInformationDialog extends JDialog
{
    private static final long serialVersionUID = 1L;
    private final JPanel contentPanel;
    static ExtraInformationDialog dialog;
    
    public static void showDialog(final BaseFindPanel selectedPanel, final int selectedRow) {
        try {
            DialogUtil.initialiseDialog(ExtraInformationDialog.dialog = new ExtraInformationDialog(selectedPanel, selectedRow), "Extra Information", new Rectangle(selectedPanel.parentDialog.getBounds().x, selectedPanel.parentDialog.getBounds().y, 300, 300));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public ExtraInformationDialog(final BaseFindPanel selectedPanel, final int selectedRow) {
        this.contentPanel = new JPanel();
        this.setBounds(100, 100, 400, 300);
        this.getContentPane().setLayout(new BorderLayout());
        final JTextPane textArea = new JTextPane();
        this.getContentPane().add(textArea, "Center");
        this.contentPanel.setLayout(new FlowLayout());
        this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        textArea.setContentType("text/html");
        final StyledDocument doc = textArea.getStyledDocument();
        final Style textstyle = textArea.addStyle("text style", null);
        textArea.setEditable(false);
        textArea.setBackground(new Color(240, 240, 240));
        final DefaultCaret caret = (DefaultCaret)textArea.getCaret();
        caret.setUpdatePolicy(2);
        textArea.setFont(new Font("Arial", 0, 14));
        textArea.setContentType("text/html");
        textArea.putClientProperty("JEditorPane.honorDisplayProperties", true);
        final OnlineGameInfo onlineGameInfo = selectedPanel.tableStoredInformation.get(selectedRow);
        try {
            if (!onlineGameInfo.getGameId().equals("")) {
                doc.insertString(doc.getLength(), "Game Id: " + onlineGameInfo.getGameId() + "\n", textstyle);
            }
            if (!onlineGameInfo.getGameName().equals("")) {
                doc.insertString(doc.getLength(), "Game Name: " + onlineGameInfo.getGameName() + "\n", textstyle);
            }
            if (!onlineGameInfo.getHostUsername().equals("")) {
                doc.insertString(doc.getLength(), "Host Username: " + onlineGameInfo.getHostUsername() + "\n", textstyle);
            }
            if (!onlineGameInfo.getPlayerNumber().equals("")) {
                doc.insertString(doc.getLength(), "Player Number: " + onlineGameInfo.getPlayerNumber() + "\n", textstyle);
            }
            if (!onlineGameInfo.getMaxPlayerNumber().equals("")) {
                doc.insertString(doc.getLength(), "Max Number of Players: " + onlineGameInfo.getMaxPlayerNumber() + "\n", textstyle);
            }
            if (!onlineGameInfo.getTimeRemainingForPlayer().equals("")) {
                doc.insertString(doc.getLength(), "Player Time Remaining: " + onlineGameInfo.getTimeRemainingForPlayer() + "\n", textstyle);
            }
            if (!onlineGameInfo.getTimeRemainingForRound().equals("")) {
                doc.insertString(doc.getLength(), "Round Time Remaining: " + onlineGameInfo.getTimeRemainingForRound() + "\n", textstyle);
            }
            if (!onlineGameInfo.getCurrentMover().equals("")) {
                doc.insertString(doc.getLength(), "Current Mover: " + onlineGameInfo.getCurrentMover() + "\n", textstyle);
            }
            if (!onlineGameInfo.getJoinedPlayers().equals("")) {
                doc.insertString(doc.getLength(), "Joined Players: " + onlineGameInfo.getJoinedPlayers() + "\n", textstyle);
            }
            if (!onlineGameInfo.getPrivateGame().equals("")) {
                doc.insertString(doc.getLength(), "Private Game: " + onlineGameInfo.getPrivateGame() + "\n", textstyle);
            }
            if (!onlineGameInfo.getOptions().equals("") && !onlineGameInfo.getOptions().equals("-")) {
                doc.insertString(doc.getLength(), "Game Options: " + onlineGameInfo.getOptions() + "\n", textstyle);
            }
            if (!onlineGameInfo.getTournamentId().equals("")) {
                doc.insertString(doc.getLength(), "Tournament Id: " + onlineGameInfo.getTournamentId() + "\n", textstyle);
            }
            if (!onlineGameInfo.getTournamentName().equals("")) {
                doc.insertString(doc.getLength(), "Tournament Name: " + onlineGameInfo.getTournamentName() + "\n", textstyle);
            }
            if (!onlineGameInfo.getTournamentFormat().equals("")) {
                doc.insertString(doc.getLength(), "Tournament Format: " + onlineGameInfo.getTournamentFormat() + "\n", textstyle);
            }
            if (!onlineGameInfo.getRoundNumber().equals("")) {
                doc.insertString(doc.getLength(), "Round Number: " + onlineGameInfo.getRoundNumber() + "\n", textstyle);
            }
            if (!onlineGameInfo.getAppVersion().equals("")) {
                doc.insertString(doc.getLength(), "App Version: " + onlineGameInfo.getAppVersion() + "\n", textstyle);
            }
            if (!onlineGameInfo.getNotes().equals("")) {
                doc.insertString(doc.getLength(), "Notes: " + onlineGameInfo.getNotes() + "\n", textstyle);
            }
            if (!onlineGameInfo.getGameHash().equals("")) {
                doc.insertString(doc.getLength(), "Game Hash: " + onlineGameInfo.getGameHash() + "\n", textstyle);
            }
            if (!onlineGameInfo.getPassword().equals("")) {
                doc.insertString(doc.getLength(), "Password: " + onlineGameInfo.getPassword() + "\n", textstyle);
            }
        }
        catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
}
