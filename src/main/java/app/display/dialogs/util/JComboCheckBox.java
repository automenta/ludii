// 
// Decompiled by Procyon v0.5.36
// 

package app.display.dialogs.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

public class JComboCheckBox extends JComboBox<JCheckBox>
{
    public JComboCheckBox() {
        this.init();
    }
    
    public JComboCheckBox(final JCheckBox[] items) {
        super(items);
        this.init();
    }
    
    public JComboCheckBox(final Vector items) {
        super(items);
        this.init();
    }
    
    public JComboCheckBox(final ComboBoxModel aModel) {
        super(aModel);
        this.init();
    }
    
    private void init() {
        this.setRenderer(new ComboBoxRenderer());
        this.addActionListener(ae -> JComboCheckBox.this.itemSelected());
    }
    
    void itemSelected() {
        if (this.getSelectedItem() instanceof JCheckBox) {
            final JCheckBox jcb = (JCheckBox)this.getSelectedItem();
            jcb.setSelected(!jcb.isSelected());
        }
    }
    
    class ComboBoxRenderer implements ListCellRenderer
    {
        private JLabel label;
        
        public ComboBoxRenderer() {
            JComboCheckBox.this.setOpaque(true);
        }
        
        @Override
        public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
            if (value instanceof Component) {
                final Component c = (Component)value;
                if (isSelected) {
                    c.setBackground(list.getSelectionBackground());
                    c.setForeground(list.getSelectionForeground());
                }
                else {
                    c.setBackground(list.getBackground());
                    c.setForeground(list.getForeground());
                }
                return c;
            }
            if (this.label == null) {
                this.label = new JLabel(value.toString());
            }
            else {
                this.label.setText(value.toString());
            }
            return this.label;
        }
    }
}
