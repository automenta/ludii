// 
// Decompiled by Procyon v0.5.36
// 

package app.display.dialogs;

import app.DesktopApp;
import app.display.MainWindow;
import app.display.dialogs.util.DialogUtil;
import app.display.util.GUIUtil;
import app.loading.GameLoading;
import distance.DistanceMetric;
import distance.Levenshtein;
import distance.ZhangShasha;
import game.Game;
import main.FileHandling;
import grammar.Description;
import manager.Manager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class DistanceDialog extends JDialog
{
    private static final long serialVersionUID = 1L;
    final JTextField textFieldThinkTime;
    DistanceMetric distanceMetric;
    
    public static void showDialog() {
        try {
            final DistanceDialog dialog = new DistanceDialog();
            DialogUtil.initialiseSingletonDialog(dialog, "Game Distance", null);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public DistanceDialog() {
        this.distanceMetric = new ZhangShasha();
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
        final JButton btnSelectGame = new JButton("Select Game");
        btnSelectGame.setBounds(26, 300, 178, 25);
        contentPanel.add(btnSelectGame);
        final JTextField lblSelectedGame = new JTextField("");
        lblSelectedGame.setBounds(26, 350, 300, 25);
        contentPanel.add(lblSelectedGame);
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
        final JLabel lblDistanceAlgorihtm = new JLabel("Distance Algorihtm");
        lblDistanceAlgorihtm.setBounds(470, 41, 145, 15);
        contentPanel.add(lblDistanceAlgorihtm);
        final JRadioButton ZhangShashaButton = new JRadioButton("Zhang Sasha", true);
        ZhangShashaButton.setBounds(470, 80, 291, 15);
        contentPanel.add(ZhangShashaButton);
        final JRadioButton LevenshteinButton = new JRadioButton("Levenshtein", false);
        LevenshteinButton.setBounds(470, 120, 291, 15);
        contentPanel.add(LevenshteinButton);
        final ButtonGroup group = new ButtonGroup();
        group.add(ZhangShashaButton);
        group.add(LevenshteinButton);
        ZhangShashaButton.addActionListener(e -> {
            if (ZhangShashaButton.isSelected()) {
                DistanceDialog.this.distanceMetric = new ZhangShasha();
            }
        });
        LevenshteinButton.addActionListener(e -> {
            if (LevenshteinButton.isSelected()) {
                DistanceDialog.this.distanceMetric = new Levenshtein();
            }
        });
        comboBoxAIAgents.addActionListener(e -> {
            if (comboBoxAIAgents.getSelectedItem().toString().equals("Custom")) {
                comboBoxAlgorithm.setEnabled(true);
                DistanceDialog.this.textFieldThinkTime.setEnabled(true);
            }
            else {
                comboBoxAlgorithm.setEnabled(false);
                DistanceDialog.this.textFieldThinkTime.setEnabled(false);
            }
        });
        okButton.addActionListener(e -> {
            if (Double.parseDouble(DistanceDialog.this.textFieldThinkTime.getText()) <= 0.0) {
                DesktopApp.playerApp().addTextToAnalysisPanel("Invalid think time, setting to 0.05");
                DistanceDialog.this.textFieldThinkTime.setText("0.05");
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
            final int maxTurns = Integer.parseInt(textFieldMaxTurns.getText());
            final int numberTrials = Integer.parseInt(textFieldNumberTrials.getText());
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
                    thinkTime = Double.valueOf(DistanceDialog.this.textFieldThinkTime.getText());
                    break;
                }
            }
            final List<Game> allGameB = DistanceDialog.getAllGamesFromCategories(lblSelectedGame.getText().split(";"));
            DistanceDialog.this.distanceMetric.distance(Manager.ref().context().game(), allGameB, numberTrials, maxTurns, thinkTime, AIName);
            MainWindow.tabPanel().select(3);
        });
        btnSelectGame.addActionListener(e -> {
            try {
                final String[] choices = FileHandling.listGames();
                String initialChoice = choices[0];
                for (final String choice : choices) {
                    if (Manager.savedLudName() != null && Manager.savedLudName().endsWith(choice.replaceAll(Pattern.quote("\\"), "/"))) {
                        initialChoice = choice;
                        break;
                    }
                }
                final String choice2 = GameLoaderDialog.showDialog(DesktopApp.frame(), choices, initialChoice, false);
                final String str = choice2.replaceAll(Pattern.quote("\\"), "/");
                final String[] parts = str.split("/");
                lblSelectedGame.setText("");
                String gameCategoriesString = "";
                for (final String s : parts) {
                    if (s.length() > 1) {
                        gameCategoriesString = gameCategoriesString + s + "; ";
                    }
                }
                lblSelectedGame.setText(gameCategoriesString);
            }
            catch (Exception ex) {}
        });
    }
    
    static List<Game> getAllGamesFromCategories(final String[] categoriesToMatch) {
        for (int i = 0; i < categoriesToMatch.length; ++i) {
            categoriesToMatch[i] = categoriesToMatch[i].trim();
        }
        final List<Game> validGames = new ArrayList<>();
        final String[] listGames;
        final String[] gamePaths = listGames = FileHandling.listGames();
        for (final String path : listGames) {
            boolean validGame = true;
            final String[] gameCategories = path.split("/");
            for (final String categoryToMatch : categoriesToMatch) {
                if (!Arrays.asList(gameCategories).contains(categoryToMatch.trim())) {
                    validGame = false;
                    break;
                }
            }
            if (validGame) {
                final String gameDescription = GameLoading.getGameDescriptionRawFromName(path);
                validGames.add(new Game(path, new Description(gameDescription)));
            }
        }
        return validGames;
    }
}
