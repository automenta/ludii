// 
// Decompiled by Procyon v0.5.36
// 

package app.display.dialogs;

import analysis.Complexity;
import app.DesktopApp;
import app.display.MainWindow;
import app.display.dialogs.util.DialogUtil;
import app.display.util.GUIUtil;
import app.utils.AIDesktop;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import manager.Manager;
import manager.utils.ContextSnapshot;
import manager.utils.SettingsManager;
import metrics.Metric;
import metrics.evaluation.Evaluation;
import metrics.evaluation.playability.Duration;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class EvaluationDialog extends JDialog
{
    private static final long serialVersionUID = 1L;
    final JTextField textFieldThinkTime;
    final JTextField textFieldMinIdealTurns;
    final JTextField textFieldMaxIdealTurns;
    
    public static void showDialog() {
        try {
            final EvaluationDialog dialog = new EvaluationDialog();
            DialogUtil.initialiseSingletonDialog(dialog, "Game Evaluation", null);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public EvaluationDialog() {
        final Evaluation E = new Evaluation(ContextSnapshot.getContext().game());
        final List<Metric> metrics = E.metrics();
        final ArrayList<Double> weights = new ArrayList<>();
        final JPanel contentPanel = new JPanel();
        this.setBounds(100, 100, 900, 500);
        this.getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.getContentPane().add(contentPanel, "Center");
        final JPanel buttonPane = new JPanel();
        buttonPane.setBorder(new LineBorder(new Color(0, 0, 0)));
        buttonPane.setLayout(new FlowLayout(2));
        this.getContentPane().add(buttonPane, "South");
        final JButton okButton = new JButton("Evaluate");
        okButton.setActionCommand("OK");
        buttonPane.add(okButton);
        this.getRootPane().setDefaultButton(okButton);
        contentPanel.setLayout(null);
        final JLabel lblNewLabel = new JLabel("Number of Trials");
        lblNewLabel.setBounds(26, 41, 145, 15);
        contentPanel.add(lblNewLabel);
        final JTextField textFieldNumberTrials = new JTextField();
        textFieldNumberTrials.setBounds(220, 39, 162, 19);
        textFieldNumberTrials.setText("10");
        contentPanel.add(textFieldNumberTrials);
        textFieldNumberTrials.setColumns(10);
        final JLabel lblAiModes = new JLabel("AI Agents");
        lblAiModes.setBounds(26, 169, 91, 15);
        contentPanel.add(lblAiModes);
        final JComboBox<String> comboBoxAIAgents = new JComboBox<>();
        comboBoxAIAgents.addItem("Random");
        comboBoxAIAgents.addItem("Very weak AI");
        comboBoxAIAgents.addItem("Weak AI");
        comboBoxAIAgents.addItem("Strong AI");
        comboBoxAIAgents.addItem("Very strong AI");
        comboBoxAIAgents.addItem("Custom");
        comboBoxAIAgents.setBounds(220, 164, 162, 24);
        comboBoxAIAgents.setEnabled(true);
        contentPanel.add(comboBoxAIAgents);
        final JLabel labelMaxTurns = new JLabel("Maximum # Turns");
        labelMaxTurns.setBounds(26, 83, 175, 15);
        contentPanel.add(labelMaxTurns);
        final JTextField textFieldMaxTurns = new JTextField();
        textFieldMaxTurns.setBounds(220, 81, 162, 19);
        textFieldMaxTurns.setText("50");
        textFieldMaxTurns.setColumns(10);
        contentPanel.add(textFieldMaxTurns);
        final JSeparator separator = new JSeparator();
        separator.setOrientation(1);
        separator.setBounds(430, 0, 8, 450);
        contentPanel.add(separator);
        final JLabel labelThinkTime = new JLabel("Agent Think Time");
        labelThinkTime.setBounds(26, 252, 175, 15);
        contentPanel.add(labelThinkTime);
        (this.textFieldThinkTime = new JTextField()).setEnabled(false);
        this.textFieldThinkTime.setText("0.5");
        this.textFieldThinkTime.setColumns(10);
        this.textFieldThinkTime.setBounds(220, 250, 162, 19);
        contentPanel.add(this.textFieldThinkTime);
        final JLabel lblAiAlgorithm = new JLabel("AI Algorithm");
        lblAiAlgorithm.setBounds(26, 212, 91, 15);
        contentPanel.add(lblAiAlgorithm);
        final String[] comboBoxContents = GUIUtil.getAiStrings(false).toArray(new String[0]);
        final JComboBox<String> comboBoxAlgorithm = new JComboBox<>(comboBoxContents);
        comboBoxAlgorithm.setEnabled(false);
        comboBoxAlgorithm.setBounds(220, 207, 162, 24);
        contentPanel.add(comboBoxAlgorithm);
        final JLabel lblIdealTurnNumber = new JLabel("Ideal Turn Number Range");
        lblIdealTurnNumber.setBounds(26, 331, 175, 15);
        contentPanel.add(lblIdealTurnNumber);
        final JLabel lblMinimum = new JLabel("Minimum");
        lblMinimum.setBounds(26, 357, 175, 15);
        contentPanel.add(lblMinimum);
        final JLabel lblMaximum = new JLabel("Maximum");
        lblMaximum.setBounds(26, 383, 175, 15);
        contentPanel.add(lblMaximum);
        (this.textFieldMinIdealTurns = new JTextField()).setText("0");
        this.textFieldMinIdealTurns.setColumns(10);
        this.textFieldMinIdealTurns.setBounds(220, 354, 162, 19);
        contentPanel.add(this.textFieldMinIdealTurns);
        (this.textFieldMaxIdealTurns = new JTextField()).setText("1000");
        this.textFieldMaxIdealTurns.setColumns(10);
        this.textFieldMaxIdealTurns.setBounds(220, 380, 162, 19);
        contentPanel.add(this.textFieldMaxIdealTurns);
        final JButton btnCalculateTurnRange = new JButton("Calculate Automatically");
        btnCalculateTurnRange.setBounds(220, 323, 162, 23);
        contentPanel.add(btnCalculateTurnRange);
        JTextField textField_1 = new JTextField();
        final List<JTextField> allMetricTextFields = new ArrayList<>();
        final List<JSlider> allMetricSliders = new ArrayList<>();
        int currentYDistance = 50;
        for (int i = 0; i < metrics.size(); ++i) {
            final int metricIndex = i;
            final Metric m = metrics.get(metricIndex);
            final JLabel metricNameLabel = new JLabel(m.name());
            metricNameLabel.setBounds(476, currentYDistance, 150, 19);
            contentPanel.add(metricNameLabel);
            final String metricInfo = m.notes();
            metricNameLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(final MouseEvent evt) {
                    metricNameLabel.setToolTipText(metricInfo);
                }
            });
            final JSlider slider = new JSlider();
            slider.setMinorTickSpacing(1);
            slider.setMajorTickSpacing(10);
            slider.setValue(100);
            slider.setMinimum(-100);
            slider.setBounds(680, currentYDistance + 30, 162, 16);
            contentPanel.add(slider);
            textField_1 = new JTextField();
            textField_1.setEditable(false);
            textField_1.setBounds(680, currentYDistance, 162, 19);
            contentPanel.add(textField_1);
            textField_1.setColumns(10);
            final JButton zeroButton = new JButton();
            zeroButton.setBounds(580, currentYDistance, 70, 19);
            zeroButton.setText("Zero");
            contentPanel.add(zeroButton);
            zeroButton.addActionListener(e -> slider.setValue(0));
            currentYDistance += 70;
            final double initialWeightValue = slider.getValue() / 100.0;
            allMetricTextFields.add(textField_1);
            allMetricSliders.add(slider);
            weights.add(initialWeightValue);
            allMetricTextFields.get(metricIndex).setText(Double.toString(initialWeightValue));
            allMetricSliders.get(metricIndex).addChangeListener(arg0 -> {
                allMetricTextFields.get(metricIndex).setText(Double.toString(allMetricSliders.get(metricIndex).getValue() / 100.0));
                weights.set(metricIndex, Double.valueOf(allMetricTextFields.get(metricIndex).getText()));
            });
        }
        comboBoxAIAgents.addActionListener(e -> {
            if (comboBoxAIAgents.getSelectedItem().toString().equals("Custom")) {
                comboBoxAlgorithm.setEnabled(true);
                EvaluationDialog.this.textFieldThinkTime.setEnabled(true);
            }
            else {
                comboBoxAlgorithm.setEnabled(false);
                EvaluationDialog.this.textFieldThinkTime.setEnabled(false);
            }
        });
        okButton.addActionListener(e -> {
            if (Double.parseDouble(EvaluationDialog.this.textFieldThinkTime.getText()) <= 0.0) {
                DesktopApp.playerApp().addTextToAnalysisPanel("Invalid think time, setting to 0.05");
                EvaluationDialog.this.textFieldThinkTime.setText("0.05");
            }
            try {
                if (Integer.parseInt(textFieldMaxTurns.getText()) <= 0) {
                    DesktopApp.playerApp().addTextToAnalysisPanel("Invalid maximum number of turns, setting to 50");
                    textFieldMaxTurns.setText("50");
                }
            }
            catch (NumberFormatException exception) {
                DesktopApp.playerApp().addTextToAnalysisPanel("Invalid maximum number of turns, setting to 50");
                textFieldMaxTurns.setText("50");
            }
            try {
                if (Integer.parseInt(textFieldNumberTrials.getText()) <= 0) {
                    DesktopApp.playerApp().addTextToAnalysisPanel("Invalid number of trials, setting to 10");
                    textFieldNumberTrials.setText("10");
                }
            }
            catch (NumberFormatException exception) {
                DesktopApp.playerApp().addTextToAnalysisPanel("Invalid number of trials, setting to 10");
                textFieldNumberTrials.setText("10");
            }
            try {
                if (Double.parseDouble(EvaluationDialog.this.textFieldMinIdealTurns.getText()) < 0.0) {
                    DesktopApp.playerApp().addTextToAnalysisPanel("Invalid minimum number of ideal turns, setting to 0");
                    EvaluationDialog.this.textFieldMinIdealTurns.setText("0");
                }
            }
            catch (NumberFormatException exception) {
                DesktopApp.playerApp().addTextToAnalysisPanel("Invalid minimum number of ideal turns, setting to 0");
                EvaluationDialog.this.textFieldMinIdealTurns.setText("0");
            }
            try {
                if (Double.parseDouble(EvaluationDialog.this.textFieldMaxIdealTurns.getText()) <= 0.0) {
                    DesktopApp.playerApp().addTextToAnalysisPanel("Invalid maximum number of ideal turns, setting to 1000");
                    EvaluationDialog.this.textFieldMaxIdealTurns.setText("1000");
                }
            }
            catch (NumberFormatException exception) {
                DesktopApp.playerApp().addTextToAnalysisPanel("Invalid maximum number of ideal turns, setting to 1000");
                EvaluationDialog.this.textFieldMaxIdealTurns.setText("1000");
            }
            final int maxTurns = Integer.parseInt(textFieldMaxTurns.getText());
            final int numberIterations = Integer.parseInt(textFieldNumberTrials.getText());
            double thinkTime = 0.5;
            String AIName = null;
            final String string = comboBoxAIAgents.getSelectedItem().toString();
            switch (string) {
                case "Random" -> {
                    AIName = "Random";
                    break;
                }
                case "Very weak AI" -> {
                    AIName = "Ludii AI";
                    thinkTime = 0.1;
                    break;
                }
                case "Weak AI" -> {
                    AIName = "Ludii AI";
                    thinkTime = 0.5;
                    break;
                }
                case "Strong AI" -> {
                    AIName = "Ludii AI";
                    thinkTime = 2.0;
                    break;
                }
                case "very strong AI" -> {
                    AIName = "Ludii AI";
                    thinkTime = 5.0;
                    break;
                }
                case "Custom" -> {
                    AIName = comboBoxAlgorithm.getSelectedItem().toString();
                    thinkTime = Double.valueOf(EvaluationDialog.this.textFieldThinkTime.getText());
                    break;
                }
            }
            for (final Metric m : metrics) {
                if (m instanceof Duration) {
                    ((Duration)m).setMinTurn(Double.parseDouble(EvaluationDialog.this.textFieldMinIdealTurns.getText()));
                    ((Duration)m).setMaxTurn(Double.parseDouble(EvaluationDialog.this.textFieldMaxIdealTurns.getText()));
                }
            }
            AIDesktop.AIEvalution(numberIterations, maxTurns, thinkTime, AIName, metrics, weights);
            MainWindow.tabPanel().select(3);
        });
        btnCalculateTurnRange.addActionListener(e -> {
            Manager.app.setVolatileMessage("Please Wait a few seconds.");
            Manager.app.repaint();
            EventQueue.invokeLater(() -> EventQueue.invokeLater(() -> {
                double brachingFactor = EvaluationDialog.estimateBranchingFactor();
                if (brachingFactor != 0.0) {
                    EvaluationDialog.this.textFieldMinIdealTurns.setText(String.valueOf(brachingFactor));
                    EvaluationDialog.this.textFieldMaxIdealTurns.setText(String.valueOf(brachingFactor * 2.0));
                }
                else {
                    DesktopApp.playerApp().addTextToAnalysisPanel("Failed to calculate branching factor");
                }
            }));
        });
    }
    
    public static double estimateBranchingFactor() {
        if (!Manager.ref().context().game().isDeductionPuzzle()) {
            final double numSecs = 5.0;
            final TObjectDoubleHashMap<String> results = Complexity.estimateBranchingFactor(Manager.savedLudName(), SettingsManager.userSelections, 5.0);
            return results.get("Avg Trial Branching Factor");
        }
        return 0.0;
    }
}
