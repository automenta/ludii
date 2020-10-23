// 
// Decompiled by Procyon v0.5.36
// 

package app.display.dialogs.remote.panels;

import app.display.dialogs.remote.RemoteDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseFindPanel extends JPanel
{
    private static final long serialVersionUID = 1L;
    public List<TableCellEditor> comboBox;
    final JTable table;
    public final ArrayList<OnlineGameInfo> tableStoredInformation;
    public RemoteDialog parentDialog;
    
    public BaseFindPanel(final RemoteDialog parentDialog) {
        this.comboBox = new ArrayList<>();
        this.tableStoredInformation = new ArrayList<>();
        this.parentDialog = parentDialog;
        this.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.setLayout(new BorderLayout(0, 0));
        final DefaultTableModel model = this.createModel();
        this.table = this.createTable(model);
        final BaseFindPanel baseFindPanel = this;
        this.table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(final MouseEvent mouseEvent) {
                final JTable table = (JTable)mouseEvent.getSource();
                if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    BaseFindPanel.this.okButtonPressed(table, parentDialog);
                }
                if (table.getSelectedRow() != -1) {
                    parentDialog.updateSelectedTable(baseFindPanel);
                }
                if (table.getSelectedRow() != -1 && table.getSelectedColumn() == table.getColumnCount() - 1) {
                    BaseFindPanel.this.showExtraInformation(table.getSelectedRow());
                }
            }
        });
    }
    
    public abstract void okButtonPressed(final JTable p0, final RemoteDialog p1);
    
    public abstract JTable createTable(final DefaultTableModel p0);
    
    public abstract DefaultTableModel createModel();
    
    protected abstract String[] findJoinableGames();
    
    protected void showExtraInformation(final int selectedRow) {
        ExtraInformationDialog.showDialog(this, selectedRow);
    }
    
    public JTable getTable() {
        return this.table;
    }
}
