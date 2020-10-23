// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.swing;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentAdapter;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.io.Serializable;
import java.awt.event.WindowAdapter;
import java.awt.Container;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import java.awt.FlowLayout;
import javax.swing.JPanel;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import javax.swing.JOptionPane;
import javax.swing.JDialog;
import java.awt.event.ComponentListener;
import java.awt.event.WindowListener;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import java.awt.Component;
import javax.swing.text.Document;
import java.awt.geom.AffineTransform;

public class JAffineTransformChooser extends JGridBagPanel
{
    public static final String LABEL_ANGLE = "JAffineTransformChooser.label.angle";
    public static final String LABEL_DEGREE = "JAffineTransformChooser.label.degree";
    public static final String LABEL_PERCENT = "JAffineTransformChooser.label.percent";
    public static final String LABEL_ROTATE = "JAffineTransformChooser.label.rotate";
    public static final String LABEL_SCALE = "JAffineTransformChooser.label.scale";
    public static final String LABEL_RX = "JAffineTransformChooser.label.rx";
    public static final String LABEL_RY = "JAffineTransformChooser.label.ry";
    public static final String LABEL_SX = "JAffineTransformChooser.label.sx";
    public static final String LABEL_SY = "JAffineTransformChooser.label.sy";
    public static final String LABEL_TRANSLATE = "JAffineTransformChooser.label.translate";
    public static final String LABEL_TX = "JAffineTransformChooser.label.tx";
    public static final String LABEL_TY = "JAffineTransformChooser.label.ty";
    public static final String CONFIG_TEXT_FIELD_WIDTH = "JAffineTransformChooser.config.text.field.width";
    public static final String CONFIG_TOP_PAD = "JAffineTransformChooser.config.top.pad";
    public static final String CONFIG_LEFT_PAD = "JAffineTransformChooser.config.left.pad";
    public static final String CONFIG_BOTTOM_PAD = "JAffineTransformChooser.config.bottom.pad";
    public static final String CONFIG_RIGHT_PAD = "JAffineTransformChooser.config.right.pad";
    protected AffineTransform txf;
    protected DoubleDocument txModel;
    protected DoubleDocument tyModel;
    protected DoubleDocument sxModel;
    protected DoubleDocument syModel;
    protected DoubleDocument rxModel;
    protected DoubleDocument ryModel;
    protected DoubleDocument rotateModel;
    protected static final double RAD_TO_DEG = 57.29577951308232;
    protected static final double DEG_TO_RAD = 0.017453292519943295;
    
    public JAffineTransformChooser() {
        this.txModel = new DoubleDocument();
        this.tyModel = new DoubleDocument();
        this.sxModel = new DoubleDocument();
        this.syModel = new DoubleDocument();
        this.rxModel = new DoubleDocument();
        this.ryModel = new DoubleDocument();
        this.rotateModel = new DoubleDocument();
        this.build();
        this.setAffineTransform(new AffineTransform());
    }
    
    protected void build() {
        final Component txyCmp = this.buildPanel(Resources.getString("JAffineTransformChooser.label.translate"), Resources.getString("JAffineTransformChooser.label.tx"), this.txModel, Resources.getString("JAffineTransformChooser.label.ty"), this.tyModel, "", "", true);
        final Component sxyCmp = this.buildPanel(Resources.getString("JAffineTransformChooser.label.scale"), Resources.getString("JAffineTransformChooser.label.sx"), this.sxModel, Resources.getString("JAffineTransformChooser.label.sy"), this.syModel, Resources.getString("JAffineTransformChooser.label.percent"), Resources.getString("JAffineTransformChooser.label.percent"), true);
        final Component rCmp = this.buildRotatePanel();
        this.add(txyCmp, 0, 0, 1, 1, 10, 1, 1.0, 1.0);
        this.add(sxyCmp, 1, 0, 1, 1, 10, 1, 1.0, 1.0);
        this.add(rCmp, 0, 1, 2, 1, 10, 1, 1.0, 1.0);
    }
    
    protected Component buildRotatePanel() {
        final JGridBagPanel panel = new JGridBagPanel();
        final Component anglePanel = this.buildPanel(Resources.getString("JAffineTransformChooser.label.rotate"), Resources.getString("JAffineTransformChooser.label.angle"), this.rotateModel, null, null, Resources.getString("JAffineTransformChooser.label.degree"), null, false);
        final Component centerPanel = this.buildPanel("", Resources.getString("JAffineTransformChooser.label.rx"), this.rxModel, Resources.getString("JAffineTransformChooser.label.ry"), this.ryModel, null, null, false);
        panel.add(anglePanel, 0, 0, 1, 1, 10, 1, 1.0, 1.0);
        panel.add(centerPanel, 1, 0, 1, 1, 10, 1, 1.0, 1.0);
        this.setPanelBorder(panel, Resources.getString("JAffineTransformChooser.label.rotate"));
        return panel;
    }
    
    protected Component buildPanel(final String panelName, final String tfALabel, final Document tfAModel, final String tfBLabel, final Document tfBModel, final String tfASuffix, final String tfBSuffix, final boolean setBorder) {
        final JGridBagPanel panel = new JGridBagPanel();
        this.addToPanelAtRow(tfALabel, tfAModel, tfASuffix, panel, 0);
        if (tfBLabel != null) {
            this.addToPanelAtRow(tfBLabel, tfBModel, tfBSuffix, panel, 1);
        }
        if (setBorder) {
            this.setPanelBorder(panel, panelName);
        }
        return panel;
    }
    
    public void setPanelBorder(final JComponent panel, final String panelName) {
        Border border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), panelName);
        final int topPad = Resources.getInteger("JAffineTransformChooser.config.top.pad");
        final int leftPad = Resources.getInteger("JAffineTransformChooser.config.left.pad");
        final int bottomPad = Resources.getInteger("JAffineTransformChooser.config.bottom.pad");
        final int rightPad = Resources.getInteger("JAffineTransformChooser.config.right.pad");
        border = BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(topPad, leftPad, bottomPad, rightPad));
        panel.setBorder(border);
    }
    
    protected void addToPanelAtRow(final String label, final Document model, final String suffix, final JGridBagPanel p, final int row) {
        final JTextField tf = new JTextField(Resources.getInteger("JAffineTransformChooser.config.text.field.width"));
        tf.setDocument(model);
        p.add(new JLabel(label), 0, row, 1, 1, 17, 2, 0.0, 0.0);
        p.add(tf, 1, row, 1, 1, 10, 2, 1.0, 0.0);
        p.add(new JLabel(suffix), 2, row, 1, 1, 17, 2, 0.0, 0.0);
    }
    
    public AffineTransform getAffineTransform() {
        final double sx = this.sxModel.getValue() / 100.0;
        final double sy = this.syModel.getValue() / 100.0;
        final double theta = this.rotateModel.getValue() * 0.017453292519943295;
        final double rx = this.rxModel.getValue();
        final double ry = this.ryModel.getValue();
        final double tx = this.txModel.getValue();
        final double ty = this.tyModel.getValue();
        final double[] m = new double[6];
        final double SIN_THETA = Math.sin(theta);
        final double COS_THETA = Math.cos(theta);
        m[0] = sx * COS_THETA;
        m[1] = sx * SIN_THETA;
        m[2] = -sy * SIN_THETA;
        m[3] = sy * COS_THETA;
        m[4] = tx + rx - rx * COS_THETA + ry * SIN_THETA;
        m[5] = ty + ry - rx * SIN_THETA - ry * COS_THETA;
        return this.txf = new AffineTransform(m);
    }
    
    public void setAffineTransform(AffineTransform txf) {
        if (txf == null) {
            txf = new AffineTransform();
        }
        this.txf = txf;
        final double[] m = new double[6];
        txf.getMatrix(m);
        this.txModel.setValue(m[4]);
        this.tyModel.setValue(m[5]);
        final double sx = Math.sqrt(m[0] * m[0] + m[1] * m[1]);
        final double sy = Math.sqrt(m[2] * m[2] + m[3] * m[3]);
        this.sxModel.setValue(100.0 * sx);
        this.syModel.setValue(100.0 * sy);
        double theta = 0.0;
        if (m[0] > 0.0) {
            theta = Math.atan2(m[1], m[0]);
        }
        this.rotateModel.setValue(57.29577951308232 * theta);
        this.rxModel.setValue(0.0);
        this.ryModel.setValue(0.0);
    }
    
    public static AffineTransform showDialog(final Component cmp, final String title) {
        final JAffineTransformChooser pane = new JAffineTransformChooser();
        final AffineTransformTracker tracker = new AffineTransformTracker(pane);
        final JDialog dialog = new Dialog(cmp, title, true, pane, tracker, null);
        dialog.addWindowListener(new Closer());
        dialog.addComponentListener(new DisposeOnClose());
        dialog.setVisible(true);
        return tracker.getAffineTransform();
    }
    
    public static Dialog createDialog(final Component cmp, final String title) {
        final JAffineTransformChooser pane = new JAffineTransformChooser();
        final AffineTransformTracker tracker = new AffineTransformTracker(pane);
        final Dialog dialog = new Dialog(cmp, title, true, pane, tracker, null);
        dialog.addWindowListener(new Closer());
        dialog.addComponentListener(new DisposeOnClose());
        return dialog;
    }
    
    public static void main(final String[] args) {
        final AffineTransform t = showDialog(null, "Hello");
        if (t == null) {
            System.out.println("Cancelled");
        }
        else {
            System.out.println("t = " + t);
        }
    }
    
    public static class Dialog extends JDialog
    {
        private JAffineTransformChooser chooserPane;
        private AffineTransformTracker tracker;
        public static final String LABEL_OK = "JAffineTransformChooser.label.ok";
        public static final String LABEL_CANCEL = "JAffineTransformChooser.label.cancel";
        public static final String LABEL_RESET = "JAffineTransformChooser.label.reset";
        public static final String ACTION_COMMAND_OK = "OK";
        public static final String ACTION_COMMAND_CANCEL = "cancel";
        
        public Dialog(final Component c, final String title, final boolean modal, final JAffineTransformChooser chooserPane, final AffineTransformTracker okListener, final ActionListener cancelListener) {
            super(JOptionPane.getFrameForComponent(c), title, modal);
            this.chooserPane = chooserPane;
            this.tracker = okListener;
            final String okString = Resources.getString("JAffineTransformChooser.label.ok");
            final String cancelString = Resources.getString("JAffineTransformChooser.label.cancel");
            final String resetString = Resources.getString("JAffineTransformChooser.label.reset");
            final Container contentPane = this.getContentPane();
            contentPane.setLayout(new BorderLayout());
            contentPane.add(chooserPane, "Center");
            final JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(1));
            final JButton okButton = new JButton(okString);
            this.getRootPane().setDefaultButton(okButton);
            okButton.setActionCommand("OK");
            if (okListener != null) {
                okButton.addActionListener(okListener);
            }
            okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    Dialog.this.setVisible(false);
                }
            });
            buttonPane.add(okButton);
            final JButton cancelButton = new JButton(cancelString);
            this.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(final KeyEvent evt) {
                    if (evt.getKeyCode() == 27) {
                        Dialog.this.setVisible(false);
                    }
                }
            });
            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    Dialog.this.setVisible(false);
                }
            });
            buttonPane.add(cancelButton);
            final JButton resetButton = new JButton(resetString);
            resetButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    Dialog.this.reset();
                }
            });
            buttonPane.add(resetButton);
            contentPane.add(buttonPane, "South");
            this.pack();
            this.setLocationRelativeTo(c);
        }
        
        @Override
        public void setVisible(final boolean b) {
            if (b) {
                this.tracker.reset();
            }
            super.setVisible(b);
        }
        
        public AffineTransform showDialog() {
            this.setVisible(true);
            return this.tracker.getAffineTransform();
        }
        
        public void reset() {
            this.chooserPane.setAffineTransform(new AffineTransform());
        }
        
        public void setTransform(AffineTransform at) {
            if (at == null) {
                at = new AffineTransform();
            }
            this.chooserPane.setAffineTransform(at);
        }
    }
    
    static class Closer extends WindowAdapter implements Serializable
    {
        @Override
        public void windowClosing(final WindowEvent e) {
            final Window w = e.getWindow();
            w.setVisible(false);
        }
    }
    
    static class DisposeOnClose extends ComponentAdapter implements Serializable
    {
        @Override
        public void componentHidden(final ComponentEvent e) {
            final Window w = (Window)e.getComponent();
            w.dispose();
        }
    }
}
