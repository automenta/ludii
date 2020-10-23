// 
// Decompiled by Procyon v0.5.36
// 

package app.display.dialogs.remote;

import app.display.dialogs.util.DialogUtil;
import manager.network.DatabaseFunctions;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;

public class LeaderboardDialog extends JDialog
{
    private static final long serialVersionUID = 1L;
    private final JPanel contentPanel;
    
    public static void showDialog() {
        try {
            final LeaderboardDialog dialog = new LeaderboardDialog();
            DialogUtil.initialiseDialog(dialog, "Leaderboard", null);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public LeaderboardDialog() {
        this.contentPanel = new JPanel();
        this.setBounds(100, 100, 632, 461);
        this.getContentPane().setLayout(new BorderLayout());
        this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        final String[] allPlayers = DatabaseFunctions.getLeaderboard().split("_next_");
        final DefaultTableModel model = new DefaultTableModel() {
            private static final long serialVersionUID = 1L;
            
            @Override
            public Class<?> getColumnClass(final int colNum) {
                switch (colNum) {
                    case 0, 2 -> {
                        return Integer.class;
                    }
                    case 3 -> {
                        return Float.class;
                    }
                    default -> {
                        return String.class;
                    }
                }
            }
        };
        final JTable table = new JTable(model) {
            private static final long serialVersionUID = 1L;
            
            @Override
            public boolean isCellEditable(final int row, final int column) {
                return false;
            }
        };
        model.addColumn("ID");
        model.addColumn("Username");
        model.addColumn("Games Played");
        model.addColumn("Elo Rating");
        for (String allPlayer : allPlayers) {
            try {
                final String gameDetails = allPlayer;
                final String[] splitDetails = gameDetails.split("NEXT_COL");
                model.addRow(new Object[]{Integer.valueOf(splitDetails[0]), splitDetails[1], Integer.valueOf(splitDetails[3]), Float.parseFloat(splitDetails[2])});
            } catch (Exception ex) {
            }
        }
        final TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
        table.setRowSorter(sorter);
        final ArrayList<RowSorter.SortKey> sortKeys = new ArrayList<>();
        final int columnIndexToSort = 3;
        sortKeys.add(new RowSorter.SortKey(3, SortOrder.DESCENDING));
        sorter.setSortKeys(sortKeys);
        sorter.sort();
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
        okButton.addActionListener(e -> LeaderboardDialog.this.dispose());
    }
}
