// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.swing;

import java.awt.Insets;
import java.awt.GridBagConstraints;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.GridBagLayout;
import javax.swing.JPanel;

public class JGridBagPanel extends JPanel implements GridBagConstants
{
    public static final InsetsManager ZERO_INSETS;
    public static final InsetsManager DEFAULT_INSETS;
    public InsetsManager insetsManager;
    
    public JGridBagPanel() {
        this(new DefaultInsetsManager());
    }
    
    public JGridBagPanel(final InsetsManager insetsManager) {
        super(new GridBagLayout());
        if (insetsManager != null) {
            this.insetsManager = insetsManager;
        }
        else {
            this.insetsManager = new DefaultInsetsManager();
        }
    }
    
    @Override
    public void setLayout(final LayoutManager layout) {
        if (layout instanceof GridBagLayout) {
            super.setLayout(layout);
        }
    }
    
    public void add(final Component cmp, final int gridx, final int gridy, final int gridwidth, final int gridheight, final int anchor, final int fill, final double weightx, final double weighty) {
        final Insets insets = this.insetsManager.getInsets(gridx, gridy);
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = gridx;
        constraints.gridy = gridy;
        constraints.gridwidth = gridwidth;
        constraints.gridheight = gridheight;
        constraints.anchor = anchor;
        constraints.fill = fill;
        constraints.weightx = weightx;
        constraints.weighty = weighty;
        constraints.insets = insets;
        this.add(cmp, constraints);
    }
    
    static {
        ZERO_INSETS = new ZeroInsetsManager();
        DEFAULT_INSETS = new DefaultInsetsManager();
    }
    
    private static class ZeroInsetsManager implements InsetsManager
    {
        private Insets insets;
        
        private ZeroInsetsManager() {
            this.insets = new Insets(0, 0, 0, 0);
        }
        
        @Override
        public Insets getInsets(final int gridx, final int gridy) {
            return this.insets;
        }
    }
    
    private static class DefaultInsetsManager implements InsetsManager
    {
        int leftInset;
        int topInset;
        public Insets positiveInsets;
        public Insets leftInsets;
        public Insets topInsets;
        public Insets topLeftInsets;
        
        private DefaultInsetsManager() {
            this.leftInset = 5;
            this.topInset = 5;
            this.positiveInsets = new Insets(this.topInset, this.leftInset, 0, 0);
            this.leftInsets = new Insets(this.topInset, 0, 0, 0);
            this.topInsets = new Insets(0, this.leftInset, 0, 0);
            this.topLeftInsets = new Insets(0, 0, 0, 0);
        }
        
        @Override
        public Insets getInsets(final int gridx, final int gridy) {
            if (gridx > 0) {
                if (gridy > 0) {
                    return this.positiveInsets;
                }
                return this.topInsets;
            }
            else {
                if (gridy > 0) {
                    return this.leftInsets;
                }
                return this.topLeftInsets;
            }
        }
    }
    
    public interface InsetsManager
    {
        Insets getInsets(final int p0, final int p1);
    }
}
