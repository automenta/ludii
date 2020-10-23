// 
// Decompiled by Procyon v0.5.36
// 

package app.display.dialogs;

import app.display.dialogs.util.DialogUtil;
import bridge.Bridge;
import game.types.board.SiteType;
import manager.Manager;
import manager.referee.UserMoveHandler;
import manager.utils.SettingsManager;
import util.Context;
import util.Move;
import util.SettingsColour;
import util.action.puzzle.ActionReset;
import util.action.puzzle.ActionSet;
import util.action.puzzle.ActionToggle;
import util.state.containerState.ContainerState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class PuzzleDialog extends JDialog
{
    private static final long serialVersionUID = 1L;
    private ContainerState cs;
    private final List<JButton> buttonList;
    
    public static void createAndShowGUI(final Context context, final int site) {
        try {
            final PuzzleDialog dialog = new PuzzleDialog(context, site);
            final Point drawPosn = new Point(MouseInfo.getPointerInfo().getLocation().x - dialog.getWidth() / 2, MouseInfo.getPointerInfo().getLocation().y - dialog.getHeight() / 2);
            DialogUtil.initialiseDialog(dialog, "Puzzle Values", new Rectangle(drawPosn));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public PuzzleDialog(final Context context, final int site) {
        this.cs = null;
        this.buttonList = new ArrayList<>();
        final int maxValue = context.board().getRange(context.board().defaultSite()).max();
        final int minValue = context.board().getRange(context.board().defaultSite()).min();
        final int numButtonsNeeded = maxValue - minValue + 2;
        int columnNumber = 0;
        int rowNumber = 0;
        columnNumber = (int)Math.ceil(Math.sqrt(numButtonsNeeded));
        rowNumber = (int)Math.ceil(numButtonsNeeded / (double)columnNumber);
        final int buttonSize = 80;
        this.setSize(80 * columnNumber, 80 * rowNumber + 30);
        this.getContentPane().setLayout(new GridLayout(0, columnNumber, 0, 0));
        this.cs = context.state().containerStates()[0];
        final JButton buttonReset = new JButton();
        buttonReset.setFocusPainted(false);
        this.getContentPane().add(buttonReset);
        if (SettingsColour.getCustomBoardColour()[2] == null) {
            buttonReset.setBackground(SettingsColour.getDefaultBoardColour()[2]);
        }
        else if (SettingsColour.getDefaultBoardColour()[2] != null) {
            buttonReset.setBackground(SettingsColour.getCustomBoardColour()[2]);
        }
        buttonReset.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(final MouseEvent e) {
            }
            
            @Override
            public void mousePressed(final MouseEvent e) {
            }
            
            @Override
            public void mouseReleased(final MouseEvent e) {
                Manager.ref().applyHumanMoveToGame(new Move(new ActionReset(context.board().defaultSite(), site, maxValue + 1)));
                PuzzleDialog.this.dispose();
            }
            
            @Override
            public void mouseEntered(final MouseEvent e) {
            }
            
            @Override
            public void mouseExited(final MouseEvent e) {
            }
        });
        for (int i = minValue; i <= maxValue; ++i) {
            final int puzzleValue = i;
            ActionSet a = null;
            a = new ActionSet(context.board().defaultSite(), site, puzzleValue);
            a.setDecision(true);
            final Move m = new Move(a);
            m.setFromNonDecision(site);
            m.setToNonDecision(site);
            m.setEdgeMove(site);
            m.setDecision(true);
            final JButton button = new JButton();
            try {
                final BufferedImage puzzleImage = new BufferedImage(80, 80, 2);
                final Graphics2D g2d = puzzleImage.createGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2d.setFont(new Font("Arial", 0, 30));
                g2d.setColor(Color.BLACK);
                Bridge.getContainerStyle(0).drawPuzzleValue(puzzleValue, site, context, g2d, new Point(40, 40), 40);
                button.setIcon(new ImageIcon(puzzleImage));
            }
            catch (Exception ex) {
                System.out.println(ex);
            }
            button.setFocusPainted(false);
            this.getContentPane().add(button);
            if (!context.game().moves(context).moves().contains(m) && !SettingsManager.illegalMovesValid) {
                button.setEnabled(false);
            }
            else {
                this.paintButton(button, site, puzzleValue, context.board().defaultSite());
                ActionToggle a2 = null;
                a2 = new ActionToggle(context.board().defaultSite(), site, puzzleValue);
                a2.setDecision(true);
                final Move m2 = new Move(a2);
                m2.setFromNonDecision(site);
                m2.setToNonDecision(site);
                m2.setEdgeMove(site);
                m2.setDecision(true);
                button.addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(final MouseEvent e) {
                    }
                    
                    @Override
                    public void mousePressed(final MouseEvent e) {
                    }
                    
                    @Override
                    public void mouseReleased(final MouseEvent e) {
                        if (e.getButton() == 1) {
                            UserMoveHandler.puzzleMove(site, puzzleValue, true, context.board().defaultSite());
                            PuzzleDialog.this.dispose();
                        }
                        else if (e.getButton() == 3 && (context.game().moves(context).moves().contains(m2) || SettingsManager.illegalMovesValid)) {
                            UserMoveHandler.puzzleMove(site, puzzleValue, false, context.board().defaultSite());

                            EventQueue.invokeLater(() -> {
                                final int val$minValue;
                                final int val$maxValue;
                                final int val$site;
                                final Context val$context;
                                final JButton val$button;
                                final int val$puzzleValue;
                                final ArrayList<Integer> optionsLeft;
                                int i;
                                val$minValue = minValue;
                                val$maxValue = maxValue;
                                val$site = site;
                                val$context = context;
                                val$button = button;
                                val$puzzleValue = puzzleValue;
                                optionsLeft = new ArrayList<>();
                                for (i = val$minValue; i <= val$maxValue; ++i) {
                                    if (PuzzleDialog.this.cs.bit(val$site, i, val$context.board().defaultSite()) && PuzzleDialog.this.buttonList.get(i - val$minValue).isEnabled()) {
                                        optionsLeft.add(i);
                                    }
                                }
                                if (optionsLeft.size() == 1) {
                                    UserMoveHandler.puzzleMove(val$site, optionsLeft.get(0), true, val$context.board().defaultSite());
                                }
                                PuzzleDialog.this.paintButton(val$button, val$site, val$puzzleValue, val$context.board().defaultSite());
                            });
                        }
                    }
                    
                    @Override
                    public void mouseEntered(final MouseEvent e) {
                    }
                    
                    @Override
                    public void mouseExited(final MouseEvent e) {
                    }
                });
            }
            this.buttonList.add(button);
        }
    }
    
    void paintButton(final JButton button, final int site, final int puzzleValue, final SiteType siteType) {
        if (!this.cs.bit(site, puzzleValue, siteType)) {
            button.setBackground(Color.GRAY);
        }
        else if (SettingsColour.getCustomBoardColour()[2] == null) {
            button.setBackground(SettingsColour.getDefaultBoardColour()[2]);
        }
        else if (SettingsColour.getDefaultBoardColour()[2] != null) {
            button.setBackground(SettingsColour.getCustomBoardColour()[2]);
        }
        else {
            button.setBackground(Color.white);
        }
    }
}
