// 
// Decompiled by Procyon v0.5.36
// 

package app.display.dialogs.remote;

import app.display.dialogs.util.DialogUtil;
import manager.Manager;
import manager.network.DatabaseFunctions;
import manager.network.SettingsNetwork;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;

public class MessagePlayersDialog extends JDialog
{
    private static final long serialVersionUID = 1L;
    private final JPanel contentPanel;
    
    public static void showDialog() {
        try {
            final MessagePlayersDialog dialog = new MessagePlayersDialog();
            DialogUtil.initialiseDialog(dialog, "Select Player", null);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public MessagePlayersDialog() {
        this.contentPanel = new JPanel();
        this.setBounds(100, 100, 632, 461);
        this.getContentPane().setLayout(new BorderLayout());
        this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        final String[] allPlayersPre = DatabaseFunctions.GetAllPlayers().split("_next_");
        final String[] allPlayers = new String[allPlayersPre.length - 1];
        int allPlayersIndex = 0;
        for (int i = 0; i < allPlayersPre.length; ++i) {
            if (Integer.parseInt(allPlayersPre[i].split("NEXT_COL")[0]) != SettingsNetwork.getLoginId()) {
                allPlayers[allPlayersIndex] = allPlayersPre[i];
                ++allPlayersIndex;
            }
        }
        final DefaultTableModel model = new DefaultTableModel();
        final JTable table = new JTable(model) {
            private static final long serialVersionUID = 1L;
            
            @Override
            public boolean isCellEditable(final int row, final int column) {
                return false;
            }
        };
        model.addColumn("ID");
        model.addColumn("Username");
        model.addColumn("Online");
        for (int j = 0; j < allPlayers.length; ++j) {
            try {
                final String gameDetails = allPlayers[j];
                final String[] splitDetails = gameDetails.split("NEXT_COL");
                model.addRow(new Object[] { splitDetails[0], splitDetails[1], splitDetails[2] });
            }
            catch (Exception ex) {}
        }
        this.getContentPane().add(this.contentPanel, "Center");
        this.contentPanel.setLayout(null);
        table.setBounds(0, 12, 440, 223);
        this.contentPanel.add(table);
        final JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(10, 15, 600, 373);
        this.contentPanel.add(scrollPane);
        scrollPane.setHorizontalScrollBarPolicy(31);
        final JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(2));
        this.getContentPane().add(buttonPane, "South");
        final JButton okButton = new JButton("OK");
        okButton.setActionCommand("OK");
        buttonPane.add(okButton);
        this.getRootPane().setDefaultButton(okButton);
        okButton.addActionListener(e -> {
            final String selectedPlayerID = table.getModel().getValueAt(table.getSelectedRow(), 0).toString();
            String messageToSend = JOptionPane.showInputDialog(null, "Please enter your message to this player", "Message", 1);
            try {
                if (messageToSend.length() > 500) {
                    Manager.app.addTextToStatusPanel("Message must be less than 500 characters.\n");
                }
                else {
                    final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    messageToSend = messageToSend + " [" + timestamp + "]";
                    final URL phpLudii = new URL(DatabaseFunctions.appFolderLocation + "sendMessageDirect.php?id=" + selectedPlayerID + "&message=" + messageToSend.replace(" ", "%20") + "&name=" + SettingsNetwork.getLoginUsername().replace(" ", "%20") + "&secret=" + SettingsNetwork.getSecretNetworkNumber() + "&networkPlayerId=" + SettingsNetwork.getLoginId());
                    final URLConnection yc = phpLudii.openConnection();
                    try (final BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()))) {
                        String inputLine;
                        while ((inputLine = in.readLine()) != null) {
                            System.out.println(inputLine);
                        }
                        Manager.app.addTextToStatusPanel("Message Sent.\n");
                        in.close();
                        MessagePlayersDialog.this.dispose();
                    }
                }
            }
            catch (Exception ex) {}
        });
        final JButton cancelButton = new JButton("Cancel");
        cancelButton.setActionCommand("Cancel");
        buttonPane.add(cancelButton);
        cancelButton.addActionListener(e -> {
            try {
                MessagePlayersDialog.this.dispose();
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
        });
    }
}
