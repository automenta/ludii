// 
// Decompiled by Procyon v0.5.36
// 

package app.display.dialogs.remote.panels;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ButtonColumn extends AbstractCellEditor implements TableCellRenderer, TableCellEditor, ActionListener, MouseListener
{
    private static final long serialVersionUID = 1L;
    private final JTable table;
    private final Action action;
    private int mnemonic;
    private final Border originalBorder;
    private Border focusBorder;
    private final JButton renderButton;
    private final JButton editButton;
    private Object editorValue;
    private boolean isButtonColumnEditor;
    
    public ButtonColumn(final JTable table, final Action action, final int column) {
        this.table = table;
        this.action = action;
        this.renderButton = new JButton();
        this.editButton = new JButton();
        this.renderButton.setBorder(null);
        this.editButton.setBorder(null);
        this.editButton.setFocusPainted(false);
        this.editButton.addActionListener(this);
        this.originalBorder = this.editButton.getBorder();
        this.setFocusBorder(new LineBorder(Color.BLUE));
        final TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(column).setCellRenderer(this);
        columnModel.getColumn(column).setCellEditor(this);
        table.addMouseListener(this);
    }
    
    public Border getFocusBorder() {
        return this.focusBorder;
    }
    
    public void setFocusBorder(final Border focusBorder) {
        this.focusBorder = focusBorder;
        this.editButton.setBorder(focusBorder);
    }
    
    public int getMnemonic() {
        return this.mnemonic;
    }
    
    public void setMnemonic(final int mnemonic) {
        this.mnemonic = mnemonic;
        this.renderButton.setMnemonic(mnemonic);
        this.editButton.setMnemonic(mnemonic);
    }
    
    @Override
    public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected, final int row, final int column) {
        if (value == null) {
            this.editButton.setText("");
            this.editButton.setIcon(null);
        }
        else if (value instanceof Icon) {
            this.editButton.setText("");
            this.editButton.setIcon((Icon)value);
        }
        else {
            this.editButton.setText(value.toString());
            this.editButton.setIcon(null);
        }
        this.editorValue = value;
        return this.editButton;
    }
    
    @Override
    public Object getCellEditorValue() {
        return this.editorValue;
    }
    
    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
        if (isSelected) {
            this.renderButton.setForeground(table.getSelectionForeground());
            this.renderButton.setBackground(table.getSelectionBackground());
        }
        else {
            this.renderButton.setForeground(table.getForeground());
            this.renderButton.setBackground(UIManager.getColor("Button.background"));
        }
        if (hasFocus) {
            this.renderButton.setBorder(this.focusBorder);
        }
        else {
            this.renderButton.setBorder(this.originalBorder);
        }
        if (value == null) {
            this.renderButton.setText("");
            this.renderButton.setIcon(null);
        }
        else if (value instanceof Icon) {
            this.renderButton.setText("");
            this.renderButton.setIcon((Icon)value);
        }
        else {
            this.renderButton.setText(value.toString());
            this.renderButton.setIcon(null);
        }
        return this.renderButton;
    }
    
    @Override
    public void actionPerformed(final ActionEvent e) {
        final int row = this.table.convertRowIndexToModel(this.table.getEditingRow());
        this.fireEditingStopped();
        final ActionEvent event = new ActionEvent(this.table, 1001, "" + row);
        this.action.actionPerformed(event);
    }
    
    @Override
    public void mousePressed(final MouseEvent e) {
        if (this.table.isEditing() && this.table.getCellEditor() == this) {
            this.isButtonColumnEditor = true;
        }
    }
    
    @Override
    public void mouseReleased(final MouseEvent e) {
        if (this.isButtonColumnEditor && this.table.isEditing()) {
            this.table.getCellEditor().stopCellEditing();
        }
        this.isButtonColumnEditor = false;
    }
    
    @Override
    public void mouseClicked(final MouseEvent e) {
    }
    
    @Override
    public void mouseEntered(final MouseEvent e) {
    }
    
    @Override
    public void mouseExited(final MouseEvent e) {
    }
}
