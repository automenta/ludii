// 
// Decompiled by Procyon v0.5.36
// 

package app.display.dialogs;

import app.DesktopApp;
import app.display.MainWindow;
import app.display.dialogs.util.DialogUtil;
import app.display.util.GraphicsCache;
import app.sandbox.SandboxValueType;
import bridge.Bridge;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.types.board.SiteType;
import manager.Manager;
import manager.utils.ContextSnapshot;
import manager.utils.SettingsManager;
import util.Context;
import util.Move;
import util.SettingsColour;
import util.SettingsVC;
import util.action.Action;
import util.action.move.ActionAdd;
import util.action.move.ActionRemove;
import util.action.state.ActionSetCount;
import util.action.state.ActionSetNextPlayer;
import util.action.state.ActionSetRotation;
import util.action.state.ActionSetState;
import util.locations.FullLocation;
import util.locations.Location;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class SandboxDialog extends JDialog
{
    private static final long serialVersionUID = 1L;
    List<JButton> buttonList;
    
    public static void createAndShowGUI(final Context context, final Location location, final SandboxValueType sandboxValueType) {
        try {
            if (context.components().length == 1) {
                MainWindow.setTemporaryMessage("No valid components.");
                return;
            }
            final SandboxDialog dialog = new SandboxDialog(context, location, sandboxValueType);
            final Point drawPosn = new Point(MouseInfo.getPointerInfo().getLocation().x - dialog.getWidth() / 2, MouseInfo.getPointerInfo().getLocation().y - dialog.getHeight() / 2);
            DialogUtil.initialiseDialog(dialog, "Sandbox Options", new Rectangle(drawPosn));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public SandboxDialog(final Context context, final Location location, final SandboxValueType sandboxValueType) {
        this.buttonList = new ArrayList<>();
        final int locnUpSite = location.site();
        final SiteType locnType = location.siteType();
        int containerId = 0;
        if (locnType == SiteType.Cell) {
            containerId = context.containerId()[locnUpSite];
        }
        final int currentMover = context.state().mover();
        final int nextMover = context.state().next();
        final int previousMover = context.state().prev();
        int numButtonsNeeded = context.components().length;
        final game.equipment.component.Component componentAtSite = context.components()[context.containerState(containerId).what(locnUpSite, locnType)];
        if (componentAtSite != null && componentAtSite.isDie()) {
            MainWindow.setVolatileMessage("Setting dice not supported yet.");
            EventQueue.invokeLater(() -> this.dispose());
        }
        if (sandboxValueType == SandboxValueType.LocalState) {
            numButtonsNeeded = context.game().maximalLocalStates();
        }
        else if (sandboxValueType == SandboxValueType.Count) {
            numButtonsNeeded = context.game().maxCount();
        }
        else if (sandboxValueType == SandboxValueType.Rotation) {
            numButtonsNeeded = context.game().maximalRotationStates();
        }
        int columnNumber = 0;
        int rowNumber = 0;
        columnNumber = (int)Math.ceil(Math.sqrt(numButtonsNeeded));
        rowNumber = (int)Math.ceil(numButtonsNeeded / (double)columnNumber);
        final int buttonBorderSize = 20;
        final int imageSize = (int)(Bridge.getContainerStyle(context.board().index()).cellRadius() * 2.0 * DesktopApp.view().boardSize());
        final int buttonSize = imageSize + 20;
        this.setSize(buttonSize * columnNumber, buttonSize * rowNumber + 30);
        this.getContentPane().setLayout(new GridLayout(0, columnNumber, 0, 0));
        if (sandboxValueType != SandboxValueType.Component) {
            for (int i = 0; i < numButtonsNeeded; ++i) {
                try {
                    final String text = Integer.toString(i);
                    BufferedImage img = new BufferedImage(1, 1, 2);
                    Graphics2D g2d = img.createGraphics();
                    final Font font = new Font("Arial", 0, 40);
                    g2d.setFont(font);
                    FontMetrics fm = g2d.getFontMetrics();
                    final int width = fm.stringWidth(text);
                    final int height = fm.getHeight();
                    g2d.dispose();
                    img = new BufferedImage(width, height, 2);
                    g2d = img.createGraphics();
                    g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
                    g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
                    g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                    g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                    g2d.setFont(font);
                    fm = g2d.getFontMetrics();
                    g2d.setColor(Color.BLACK);
                    g2d.drawString(text, 0, fm.getAscent());
                    g2d.dispose();
                    final JButton button = new JButton();
                    if (SettingsColour.getCustomBoardColour()[2] == null) {
                        button.setBackground(SettingsColour.getDefaultBoardColour()[2]);
                    }
                    else if (SettingsColour.getDefaultBoardColour()[2] != null) {
                        button.setBackground(SettingsColour.getCustomBoardColour()[2]);
                    }
                    try {
                        button.setIcon(new ImageIcon(img));
                    }
                    catch (Exception ex) {
                        System.out.println(ex);
                    }
                    button.setFocusPainted(false);
                    this.getContentPane().add(button);
                    final int value = i;
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
                                Action action = null;
                                if (sandboxValueType == SandboxValueType.LocalState) {
                                    action = new ActionSetState(locnType, locnUpSite, value);
                                }
                                if (sandboxValueType == SandboxValueType.Count) {
                                    final int what = context.game().equipment().components()[context.game().equipment().components().length - 1].index();
                                    action = new ActionSetCount(locnType, locnUpSite, what, value);
                                }
                                if (sandboxValueType == SandboxValueType.Rotation) {
                                    action = new ActionSetRotation(locnType, locnUpSite, value);
                                }
                                final Move moveToApply = new Move(action);
                                final Moves csq = new BaseMoves(null);
                                final Move nextMove = new Move(new ActionSetNextPlayer(context.state().mover()));
                                csq.moves().add(nextMove);
                                moveToApply.then().add(csq);
                                moveToApply.apply(context, true);
                                SandboxDialog.this.dispose();
                                context.state().setMover(currentMover);
                                context.state().setNext(nextMover);
                                context.state().setPrev(previousMover);
                                Manager.app.updateTabs(context);
                                SettingsVC.selectedLocation = new FullLocation(-1);
                                Manager.app.repaint();
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
                catch (Exception ex3) {}
            }
        }
        else {
            final JButton emptyButton = new JButton();
            if (SettingsColour.getCustomBoardColour()[2] == null) {
                emptyButton.setBackground(SettingsColour.getDefaultBoardColour()[2]);
            }
            else if (SettingsColour.getDefaultBoardColour()[2] != null) {
                emptyButton.setBackground(SettingsColour.getCustomBoardColour()[2]);
            }
            emptyButton.setFocusPainted(false);
            this.getContentPane().add(emptyButton);
            emptyButton.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(final MouseEvent e) {
                }
                
                @Override
                public void mousePressed(final MouseEvent e) {
                }
                
                @Override
                public void mouseReleased(final MouseEvent e) {
                    if (e.getButton() == 1) {
                        final Action actionRemove = new ActionRemove(context.board().defaultSite(), locnUpSite, true);
                        final Move moveToApply = new Move(actionRemove);
                        final Moves csq = new BaseMoves(null);
                        final Move nextMove = new Move(new ActionSetNextPlayer(context.state().mover()));
                        csq.moves().add(nextMove);
                        moveToApply.then().add(csq);
                        moveToApply.apply(context, true);
                        SandboxDialog.this.dispose();
                        context.state().setMover(currentMover);
                        context.state().setNext(nextMover);
                        context.state().setPrev(previousMover);
                        Manager.app.updateTabs(context);
                        SettingsVC.selectedLocation = new FullLocation(-1);
                        Manager.app.repaint();
                    }
                }
                
                @Override
                public void mouseEntered(final MouseEvent e) {
                }
                
                @Override
                public void mouseExited(final MouseEvent e) {
                }
            });
            for (final game.equipment.component.Component c : context.components()) {
                try {
                    if (!c.isDie()) {
                        final BufferedImage im = GraphicsCache.getComponentImage(containerId, c, 0, 0, 0, locnType, imageSize, ContextSnapshot.getContext(), false, true);
                        final JButton button2 = new JButton();
                        if (SettingsColour.getCustomBoardColour()[2] == null) {
                            button2.setBackground(SettingsColour.getDefaultBoardColour()[2]);
                        }
                        else if (SettingsColour.getDefaultBoardColour()[2] != null) {
                            button2.setBackground(SettingsColour.getCustomBoardColour()[2]);
                        }
                        try {
                            button2.setIcon(new ImageIcon(im));
                        }
                        catch (Exception ex2) {
                            System.out.println(ex2);
                        }
                        button2.setFocusPainted(false);
                        this.getContentPane().add(button2);
                        button2.addMouseListener(new MouseListener() {
                            @Override
                            public void mouseClicked(final MouseEvent e) {
                            }
                            
                            @Override
                            public void mousePressed(final MouseEvent e) {
                            }
                            
                            @Override
                            public void mouseReleased(final MouseEvent e) {
                                if (e.getButton() == 1) {
                                    final Action actionAdd = new ActionAdd(locnType, locnUpSite, c.index(), 1, -1, -1, null, null, null);
                                    final Move moveToApply = new Move(actionAdd);
                                    final Moves csq = new BaseMoves(null);
                                    final Move nextMove = new Move(new ActionSetNextPlayer(context.state().mover()));
                                    csq.moves().add(nextMove);
                                    moveToApply.then().add(csq);
                                    moveToApply.apply(context, true);
                                    SandboxDialog.this.dispose();
                                    context.state().setMover(currentMover);
                                    context.state().setNext(nextMover);
                                    context.state().setPrev(previousMover);
                                    Manager.app.updateTabs(context);
                                    SettingsVC.selectedLocation = new FullLocation(-1);
                                    Manager.app.repaint();
                                    if (context.game().requiresLocalState() && context.game().maximalLocalStates() > 1 && SettingsManager.canSelectLocalState) {
                                        SandboxDialog.createAndShowGUI(context, location, SandboxValueType.LocalState);
                                    }
                                    if (context.game().requiresRotation() && context.game().maximalRotationStates() > 1 && SettingsManager.canSelectRotation) {
                                        SandboxDialog.createAndShowGUI(context, location, SandboxValueType.Rotation);
                                    }
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
                }
                catch (Exception ex4) {}
            }
        }
    }
}
