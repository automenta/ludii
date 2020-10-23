// 
// Decompiled by Procyon v0.5.36
// 

package app.display.dialogs;

import app.display.dialogs.util.DialogUtil;
import app.display.util.BufferedImageUtil;
import app.display.util.GraphicsCache;
import app.display.util.SVGUtil;
import bridge.Bridge;
import collections.FastArrayList;
import manager.Manager;
import manager.utils.ContextSnapshot;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import util.ContainerUtil;
import util.Context;
import util.Move;
import util.SettingsColour;
import util.action.Action;
import util.action.ActionType;
import util.action.cards.ActionSetTrumpSuit;
import util.action.move.ActionPromote;
import util.action.others.ActionPropose;
import util.action.others.ActionVote;
import util.action.state.ActionBet;
import util.action.state.ActionSetNextPlayer;
import util.action.state.ActionSetRotation;
import util.state.containerState.ContainerState;
import view.component.ComponentStyle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class PossibleMovesDialog extends JDialog
{
    private static final long serialVersionUID = 1L;
    List<JButton> buttonList;
    boolean moveShown;
    
    public static void createAndShowGUI(final Context context, final FastArrayList<Move> validMoves) {
        try {
            final PossibleMovesDialog dialog = new PossibleMovesDialog(context, validMoves);
            final Point drawPosn = new Point(MouseInfo.getPointerInfo().getLocation().x - dialog.getWidth() / 2, MouseInfo.getPointerInfo().getLocation().y - dialog.getHeight() / 2);
            DialogUtil.initialiseDialog(dialog, "Possible Moves", new Rectangle(drawPosn));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public PossibleMovesDialog(final Context context, final FastArrayList<Move> validMoves) {
        this.buttonList = new ArrayList<>();
        this.moveShown = false;
        for (final Move m : context.game().moves(context).moves()) {
            if (m.isPass() || m.isSwap()) {
                validMoves.add(m);
            }
        }
        int columnNumber = 0;
        int rowNumber = 0;
        final int numButtons = validMoves.size();
        columnNumber = (int)Math.ceil(Math.sqrt(numButtons));
        rowNumber = (int)Math.ceil(numButtons / (double)columnNumber);
        final int buttonBorderSize = 20;
        final int imageSize = Math.min(100, Bridge.getContainerStyle(context.board().index()).cellRadiusPixels() * 2);
        final int buttonSize = imageSize + 20;
        this.setSize(buttonSize * columnNumber, buttonSize * rowNumber + 30);
        this.getContentPane().setLayout(new GridLayout(0, columnNumber, 0, 0));
        for (final Move i : validMoves) {
            this.moveShown = false;
            for (final Action a : i.actions()) {
                if (a instanceof ActionSetRotation) {
                    final int componentValue = ContextSnapshot.getContext().containerState(ContainerUtil.getContainerId(context, i.from(), i.fromType())).what(i.from(), i.fromType());
                    if (componentValue != 0) {
                        final game.equipment.component.Component c = context.components()[componentValue];
                        BufferedImage componentImage = GraphicsCache.getComponentImage(ContainerUtil.getContainerId(context, i.from(), i.fromType()), c, 0, i.from(), 0, i.fromType(), imageSize, ContextSnapshot.getContext(), false, true);
                        final int currentRotation = ContextSnapshot.getContext().containerState(ContainerUtil.getContainerId(context, i.from(), i.fromType())).rotation(i.from(), i.fromType());
                        final int maxRotation = 360 / ContextSnapshot.getContext().game().maximalRotationStates();
                        componentImage = BufferedImageUtil.rotateImageByDegrees(componentImage, (a.rotation() - currentRotation) * maxRotation);
                        this.AddButton(i, componentImage, "");
                        break;
                    }
                }
                else {
                    if (a instanceof ActionPromote) {
                        final ContainerState cs = ContextSnapshot.getContext().containerState(ContainerUtil.getContainerId(context, i.from(), i.fromType()));
                        final int maskedValue = cs.isMasked(a.levelFrom(), a.levelFrom(), a.who(), a.fromType()) ? 1 : 0;
                        final int componentValue2 = a.what();
                        final int componentState = a.state();
                        final ComponentStyle componentStyle = Bridge.getComponentStyle(componentValue2);
                        componentStyle.renderImageSVG(context, imageSize, componentState, true, maskedValue);
                        final SVGGraphics2D svg = componentStyle.getImageSVG(componentState);
                        BufferedImage componentImage2 = null;
                        if (svg != null) {
                            componentImage2 = SVGUtil.createSVGImage(svg.getSVGDocument(), imageSize, imageSize);
                        }
                        this.AddButton(i, componentImage2, "");
                        break;
                    }
                    if (a.actionType() == ActionType.Add) {
                        final ContainerState cs = ContextSnapshot.getContext().containerState(ContainerUtil.getContainerId(context, i.from(), i.fromType()));
                        final int maskedValue = cs.isMasked(a.levelFrom(), a.levelFrom(), a.who(), a.fromType()) ? 1 : 0;
                        final int componentValue2 = a.what();
                        final int componentState = a.state();
                        final ComponentStyle componentStyle = Bridge.getComponentStyle(componentValue2);
                        componentStyle.renderImageSVG(context, imageSize, componentState, true, maskedValue);
                        final SVGGraphics2D svg = componentStyle.getImageSVG(componentState);
                        BufferedImage componentImage2 = null;
                        if (svg != null) {
                            componentImage2 = SVGUtil.createSVGImage(svg.getSVGDocument(), imageSize, imageSize);
                        }
                        this.AddButton(i, componentImage2, "");
                        break;
                    }
                    if (a instanceof ActionSetTrumpSuit) {
                        final int trumpValue = a.what();
                        String trumpImage = "";
                        Color imageColor = Color.BLACK;
                        switch (trumpValue) {
                            case 1 -> trumpImage = "card-suit-club";
                            case 2 -> trumpImage = "card-suit-spade";
                            case 3 -> {
                                trumpImage = "card-suit-diamond";
                                imageColor = Color.RED;
                            }
                            case 4 -> {
                                trumpImage = "card-suit-heart";
                                imageColor = Color.RED;
                            }
                        }
                        BufferedImage componentImage3 = BufferedImageUtil.getImageFromSVGName(trumpImage, (int)(imageSize * 0.8));
                        componentImage3 = BufferedImageUtil.setPixelsToColour(componentImage3, imageColor);
                        this.AddButton(i, componentImage3, "");
                        break;
                    }
                    if (a instanceof ActionSetNextPlayer) {
                        final int nextPlayerValue = a.who();
                        this.AddButton(i, null, "Next player: " + nextPlayerValue);
                        break;
                    }
                    if (a instanceof ActionBet) {
                        final int betValue = a.count();
                        final int betWho = a.who();
                        this.AddButton(i, null, "P" + betWho + ", Bet: " + betValue);
                        break;
                    }
                    if (a instanceof ActionPropose) {
                        final String proposition = a.proposition();
                        this.AddButton(i, null, "Propose: " + proposition);
                        break;
                    }
                    if (a instanceof ActionVote) {
                        final String vote = a.vote();
                        this.AddButton(i, null, "Vote: " + vote);
                        break;
                    }
                }
            }
            if (i.isPass()) {
                final BufferedImage passImage = new BufferedImage(imageSize, imageSize, 2);
                final Graphics2D g2d = passImage.createGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(3.0f, 0, 1));
                final int cx = imageSize / 2;
                final int cy = imageSize / 2;
                final double scaleFactor = imageSize / 40.0;
                final GeneralPath path = new GeneralPath();
                path.moveTo(cx - 15.0 * scaleFactor, cy + 10.0 * scaleFactor);
                path.curveTo(cx - 15.0 * scaleFactor, cy + 0.0 * scaleFactor, cx - 8.0 * scaleFactor, cy - 7.0 * scaleFactor, cx + 2.0 * scaleFactor, cy - 7.0 * scaleFactor);
                path.lineTo(cx + 0.0 * scaleFactor, cy - 12.0 * scaleFactor);
                path.lineTo(cx + 15.0 * scaleFactor, cy - 5.0 * scaleFactor);
                path.lineTo(cx + 0.0 * scaleFactor, cy + 2.0 * scaleFactor);
                path.lineTo(cx + 2.0 * scaleFactor, cy - 3.0 * scaleFactor);
                path.curveTo(cx - 7.0 * scaleFactor, cy - 3.0 * scaleFactor, cx - 13.0 * scaleFactor, cy + 6.0 * scaleFactor, cx - 15.0 * scaleFactor, cy + 10.0 * scaleFactor);
                g2d.fill(path);
                this.AddButton(i, passImage, "");
            }
            else if (i.isSwap()) {
                final BufferedImage swapImage = new BufferedImage(imageSize, imageSize, 2);
                final Graphics2D g2d = swapImage.createGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(3.0f, 0, 1));
                final int cx = imageSize / 2;
                final int cy = imageSize / 2;
                final double scaleFactor = imageSize / 40.0;
                final GeneralPath path = new GeneralPath();
                path.moveTo(cx - 6.0 * scaleFactor, cy - 3.0 * scaleFactor);
                path.lineTo(cx - 6.0 * scaleFactor, cy - 7.0 * scaleFactor);
                path.lineTo(cx + 7.0 * scaleFactor, cy - 7.0 * scaleFactor);
                path.lineTo(cx + 5.0 * scaleFactor, cy - 12.0 * scaleFactor);
                path.lineTo(cx + 18.0 * scaleFactor, cy - 5.0 * scaleFactor);
                path.lineTo(cx + 5.0 * scaleFactor, cy + 2.0 * scaleFactor);
                path.lineTo(cx + 7.0 * scaleFactor, cy - 3.0 * scaleFactor);
                path.lineTo(cx - 6.0 * scaleFactor, cy - 3.0 * scaleFactor);
                path.moveTo(cx + 6.0 * scaleFactor, cy + 3.0 * scaleFactor);
                path.lineTo(cx + 6.0 * scaleFactor, cy + 7.0 * scaleFactor);
                path.lineTo(cx - 7.0 * scaleFactor, cy + 7.0 * scaleFactor);
                path.lineTo(cx - 5.0 * scaleFactor, cy + 12.0 * scaleFactor);
                path.lineTo(cx - 18.0 * scaleFactor, cy + 5.0 * scaleFactor);
                path.lineTo(cx - 5.0 * scaleFactor, cy - 2.0 * scaleFactor);
                path.lineTo(cx - 7.0 * scaleFactor, cy + 3.0 * scaleFactor);
                path.lineTo(cx + 6.0 * scaleFactor, cy + 3.0 * scaleFactor);
                g2d.fill(path);
                this.AddButton(i, swapImage, "");
            }
            else {
                if (this.moveShown) {
                    continue;
                }
                this.AddButton(i, null, i.getAllActions(context).toString());
                this.setSize(500 * columnNumber, 200 * rowNumber + 30);
            }
        }
    }
    
    private void AddButton(final Move move, final BufferedImage image, final String text) {
        final JButton button = new JButton();
        if (SettingsColour.getCustomBoardColour()[2] == null) {
            button.setBackground(SettingsColour.getDefaultBoardColour()[2]);
        }
        else if (SettingsColour.getDefaultBoardColour()[2] != null) {
            button.setBackground(SettingsColour.getCustomBoardColour()[2]);
        }
        if (image != null) {
            button.setIcon(new ImageIcon(image));
        }
        if (!text.isEmpty()) {
            final String htmlText = "<html><center>" + text + "</center></html>";
            button.setText(DialogUtil.getWrappedText(button.getGraphics(), button, htmlText));
        }
        button.setFocusPainted(false);
        this.getContentPane().add(button);
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
                    Manager.ref().applyHumanMoveToGame(move);
                    PossibleMovesDialog.this.dispose();
                }
            }
            
            @Override
            public void mouseEntered(final MouseEvent e) {
            }
            
            @Override
            public void mouseExited(final MouseEvent e) {
            }
        });
        this.moveShown = true;
    }
}
