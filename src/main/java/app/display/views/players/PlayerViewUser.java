// 
// Decompiled by Procyon v0.5.36
// 

package app.display.views.players;

import app.DesktopApp;
import app.display.util.GUIUtil;
import app.display.util.SVGUtil;
import app.display.views.View;
import game.Game;
import game.equipment.Equipment;
import game.equipment.container.Container;
import graphics.svg.SVGtoImage;
import manager.ai.AIDetails;
import manager.ai.AIMenuName;
import manager.ai.AIUtil;
import manager.network.SettingsNetwork;
import manager.utils.ContextSnapshot;
import metadata.graphics.util.WhenScoreType;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.json.JSONObject;
import util.AI;
import util.Context;
import util.SettingsColour;
import util.model.SimultaneousMove;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class PlayerViewUser extends View
{
    protected int playerId;
    public PlayerView playerView;
    Container hand;
    protected final int numberPlayersForReducedHeightFormat = 4;
    JComboBox<String> myComboBox;
    JComboBox<String> myComboBoxThinkTime;
    protected boolean ignoreAIComboBoxEvents;
    
    public PlayerViewUser(final Rectangle rect, final int pid, final PlayerView playerView) {
        this.playerId = 0;
        this.hand = null;
        this.myComboBox = null;
        this.myComboBoxThinkTime = null;
        this.ignoreAIComboBoxEvents = false;
        this.playerView = playerView;
        this.playerId = pid;
        this.determineHand(ContextSnapshot.getContext().equipment());
        this.placement = rect;
    }
    
    @Override
    public void paint(final Graphics2D g2d) {
        final Context context = ContextSnapshot.getContext();
        final int mover = context.state().mover();
        final ArrayList<Integer> winnerNumbers = getWinnerNumbers(context);
        final AIDetails associatedAI = DesktopApp.aiSelected()[context.state().playerToAgent(this.playerId)];
        this.drawColourSwatch(g2d, mover, winnerNumbers, context);
        this.drawPlayerName(g2d, mover, winnerNumbers, context);
        this.drawPlayerComboBox(g2d, context, associatedAI);
        this.drawThinkTimeComboBox(g2d, context, associatedAI);
        this.drawAIFace(g2d);
        int componentPushBufferX = 0;
        final int aiInterfaceWidth = this.aiInterfaceWidth(context);
        final int swatchWidth = DesktopApp.view().playerSwatchList[this.playerId].width;
        final int maxNameWidth = this.playerView.maximalPlayerNameWidth(context, g2d);
        if (context.game().players().count() > 4) {
            componentPushBufferX = swatchWidth + maxNameWidth + aiInterfaceWidth;
        }
        else {
            componentPushBufferX = swatchWidth + Math.max(maxNameWidth + 36, aiInterfaceWidth);
        }
        if (this.hand != null) {
            final int containerMarginWidth = (int)(0.05 * this.placement.height);
            final Rectangle containerPlacement = new Rectangle(this.placement.x + componentPushBufferX + containerMarginWidth, this.placement.y - this.placement.height / 2, this.placement.width - componentPushBufferX - containerMarginWidth * 2, this.placement.height);
            this.playerView.paintHand(g2d, context, containerPlacement, this.hand.index());
        }
        this.paintDebug(g2d, Color.RED);
    }
    
    private void drawColourSwatch(final Graphics2D g2d, final int mover, final ArrayList<Integer> winnerNumbers, final Context context) {
        g2d.setStroke(new BasicStroke(1.0f, 0, 1));
        final int discR = (int)(0.275 * this.placement.height);
        final int cx = this.placement.x + discR;
        final int cy = this.placement.y + this.placement.height / 2;
        final Color fillColour = SettingsColour.playerColour(this.playerId, context);
        final boolean fullColour = (ContextSnapshot.getContext().trial().over() && winnerNumbers.contains(this.playerId)) || (!ContextSnapshot.getContext().trial().over() && this.playerId == mover);
        final int fcr = fillColour.getRed();
        final int fcg = fillColour.getGreen();
        final int fcb = fillColour.getBlue();
        final boolean isLight = fcr + fcg + fcb >= 666;
        if (SettingsNetwork.getActiveGameId() != 0) {
            Color markerColour = Color.RED;
            if (SettingsNetwork.onlinePlayers[this.playerId]) {
                markerColour = Color.GREEN;
            }
            g2d.setColor(markerColour);
            g2d.fillArc(cx - discR - 4, cy - discR - 4, discR * 2 + 8, discR * 2 + 8, 0, 360);
        }
        if (fullColour) {
            g2d.setColor(new Color(63, 63, 63));
        }
        else {
            g2d.setColor(new Color(215, 215, 215));
        }
        g2d.fillArc(cx - discR - 2, cy - discR - 2, discR * 2 + 4, discR * 2 + 4, 0, 360);
        if (DesktopApp.view().playerSwatchHover[this.playerId]) {
            final int rr = fcr + (int)((255 - fcr) * 0.5);
            final int gg = fcg + (int)((255 - fcg) * 0.5);
            final int bb = fcb + (int)((255 - fcb) * 0.5);
            g2d.setColor(new Color(rr, gg, bb));
        }
        else if (fullColour) {
            g2d.setColor(fillColour);
        }
        else {
            final int rr = fcr + (int)((255 - fcr) * 0.75);
            final int gg = fcg + (int)((255 - fcg) * 0.75);
            final int bb = fcb + (int)((255 - fcb) * 0.75);
            g2d.setColor(new Color(rr, gg, bb));
        }
        g2d.fillArc(cx - discR, cy - discR, discR * 2, discR * 2, 0, 360);
        if (DesktopApp.view().playerSwatchHover[this.playerId]) {
            g2d.setColor(new Color(150, 150, 150));
        }
        else if (this.playerId == mover || ContextSnapshot.getContext().model() instanceof SimultaneousMove) {
            g2d.setColor(new Color(50, 50, 50));
        }
        else {
            g2d.setColor(new Color(215, 215, 215));
        }
        final Font oldFont = g2d.getFont();
        final Font indexFont = new Font("Arial", 1, (int)(1.0 * discR));
        g2d.setFont(indexFont);
        final String str = "" + this.playerId;
        final Rectangle2D bounds = indexFont.getStringBounds(str, g2d.getFontRenderContext());
        final int tx = cx - (int)(0.5 * bounds.getWidth());
        final int ty = cy + (int)(0.3 * bounds.getHeight()) + 1;
        if (isLight) {
            if (fullColour) {
                g2d.setColor(new Color(63, 63, 63));
            }
            else {
                g2d.setColor(new Color(215, 215, 215));
            }
        }
        else {
            g2d.setColor(Color.white);
        }
        g2d.drawString(str, tx, ty);
        g2d.setFont(oldFont);
        final boolean gameOver = context.trial().over();
        if (!context.active(this.playerId) && !gameOver) {
            g2d.setColor(new Color(255, 255, 255));
            g2d.setStroke(new BasicStroke(7.0f, 0, 1));
            g2d.drawLine(cx - 20, cy - 20, cx + 20, cy + 20);
            g2d.drawLine(cx - 20, cy + 20, cx + 20, cy - 20);
        }
        DesktopApp.view().playerSwatchList[this.playerId] = new Rectangle(cx - discR, cy - discR, discR * 2, discR * 2);
    }
    
    private void drawPlayerName(final Graphics2D g2d, final int mover, final ArrayList<Integer> winnerNumbers, final Context context) {
        g2d.setFont(PlayerView.playerNameFont);
        final String stringNameAndExtras = this.getNameAndExtrasString(context, g2d);
        final Rectangle2D bounds = PlayerView.playerNameFont.getStringBounds(stringNameAndExtras, g2d.getFontRenderContext());
        final Rectangle2D square = DesktopApp.view().playerSwatchList[this.playerId];
        final Point2D drawPosn = new Point2D.Double(square.getCenterX() + square.getWidth(), square.getCenterY());
        int strNameX = 0;
        int strNameY;
        if (context.players().size() > 5 || SettingsNetwork.getActiveGameId() != 0) {
            strNameY = (int)(drawPosn.getY() + bounds.getHeight() / 3.0);
            strNameX = (int)drawPosn.getX();
        }
        else {
            strNameY = (int)(drawPosn.getY() - bounds.getHeight() / 3.0) - 3;
            strNameX = (int)drawPosn.getX();
        }
        if (!context.trial().over() || !winnerNumbers.contains(this.playerId)) {
            if (DesktopApp.view().playerNameHover[this.playerId]) {
                g2d.setColor(new Color(150, 150, 150));
            }
            else if (this.playerId == mover || ContextSnapshot.getContext().model() instanceof SimultaneousMove) {
                g2d.setColor(new Color(50, 50, 50));
            }
            else {
                g2d.setColor(new Color(215, 215, 215));
            }
        }
        else {
            g2d.setColor(Color.red);
        }
        final Rectangle NameAndExtrasBounds = bounds.getBounds();
        NameAndExtrasBounds.x = strNameX;
        NameAndExtrasBounds.y = (int)(strNameY - bounds.getHeight());
        DesktopApp.view().playerNameList[this.playerId] = NameAndExtrasBounds;
        g2d.drawString(stringNameAndExtras, strNameX, strNameY);
    }
    
    private void drawPlayerComboBox(final Graphics2D g2d, final Context context, final AIDetails associatedAI) {
        if (this.myComboBox == null) {
            final String[] comboBoxContents = GUIUtil.getAiStrings(true).toArray(new String[GUIUtil.getAiStrings(true).size()]);
            this.myComboBox = new JComboBox<>(comboBoxContents);
            DesktopApp.frame().setLayout(null);
            DesktopApp.frame().add(this.myComboBox);
            this.myComboBox.addActionListener(e -> {
                final int newPlayerIndex = ContextSnapshot.getContext().state().playerToAgent(PlayerViewUser.this.playerId);
                if (!PlayerViewUser.this.ignoreAIComboBoxEvents) {
                    final JSONObject json = new JSONObject().put("AI", new JSONObject().put("algorithm", PlayerViewUser.this.myComboBox.getSelectedItem().toString()));
                    AIUtil.updateSelectedAI(json, newPlayerIndex, AIMenuName.getAIMenuName(PlayerViewUser.this.myComboBox.getSelectedItem().toString()));
                }
                if (PlayerViewUser.this.myComboBox.getSelectedItem().toString().equals("Ludii AI")) {
                    DesktopApp.aiSelected()[newPlayerIndex].ai().initIfNeeded(ContextSnapshot.getContext().game(), newPlayerIndex);
                }
                SettingsNetwork.backupAiPlayers();
            });
        }
        if (SettingsNetwork.getActiveGameId() != 0) {
            if (this.myComboBox.isVisible()) {
                this.myComboBox.setVisible(false);
            }
        }
        else if (!this.myComboBox.isVisible()) {
            this.myComboBox.setVisible(true);
        }
        if (!this.myComboBox.getSelectedItem().equals(associatedAI.menuItemName().label)) {
            this.ignoreAIComboBoxEvents = true;
            this.myComboBox.setSelectedItem(associatedAI.menuItemName().label);
            this.ignoreAIComboBoxEvents = false;
        }
        if (this.myComboBox.isVisible()) {
            final int width = 120;
            final int height = 20;
            final Rectangle2D bounds = PlayerView.playerNameFont.getStringBounds("A DUMMY STRING FOR HEIGHT PURPOSES", g2d.getFontRenderContext());
            final Rectangle2D square = DesktopApp.view().playerSwatchList[this.playerId];
            final Point2D drawPosn = new Point2D.Double(square.getCenterX() + square.getWidth(), square.getCenterY());
            int strAINameX = 0;
            int strAINameY;
            if (context.game().players().count() > 4 || SettingsNetwork.getActiveGameId() != 0) {
                strAINameY = (int)drawPosn.getY();
                strAINameX = (int)drawPosn.getX();
            }
            else {
                strAINameY = (int)(drawPosn.getY() + bounds.getHeight() / 3.0) - 1;
                strAINameX = (int)drawPosn.getX();
            }
            if (context.game().players().count() > 4) {
                this.myComboBox.setBounds(strAINameX + this.playerView.maximalPlayerNameWidth(context, g2d) + 10, strAINameY - 10, 120, 20);
            }
            else {
                final int macAIOffX = GUIUtil.isMac() ? -5 : 0;
                final int macAIOffY = GUIUtil.isMac() ? 6 : 0;
                this.myComboBox.setBounds(strAINameX + macAIOffX, strAINameY - 10 + macAIOffY, 120, 20);
            }
        }
    }
    
    private void drawThinkTimeComboBox(final Graphics2D g2d, final Context context, final AIDetails associatedAI) {
        if (this.myComboBoxThinkTime == null) {
            final String[] comboBoxContentsThinkTime = { "1", "2", "3", "5", "10", "30", "60", "120", "180", "240", "300" };
            (this.myComboBoxThinkTime = new JComboBox<>(comboBoxContentsThinkTime)).setEditable(true);
            DesktopApp.frame().setLayout(null);
            DesktopApp.frame().add(this.myComboBoxThinkTime);
            this.myComboBoxThinkTime.addActionListener(e -> {
                final int newPlayerIndex = ContextSnapshot.getContext().state().playerToAgent(PlayerViewUser.this.playerId);
                double thinkTime = Double.valueOf(PlayerViewUser.this.myComboBoxThinkTime.getSelectedItem().toString());
                if (thinkTime <= 0.0) {
                    thinkTime = 1.0;
                    PlayerViewUser.this.myComboBoxThinkTime.setSelectedIndex(0);
                }
                DesktopApp.aiSelected()[newPlayerIndex].setThinkTime(thinkTime);
                SettingsNetwork.backupAiPlayers();
            });
        }
        if (SettingsNetwork.getActiveGameId() != 0 || associatedAI.ai() == null) {
            if (this.myComboBoxThinkTime.isVisible()) {
                this.myComboBoxThinkTime.setVisible(false);
            }
        }
        else if (!this.myComboBoxThinkTime.isVisible()) {
            this.myComboBoxThinkTime.setVisible(true);
        }
        if (!this.myComboBoxThinkTime.getSelectedItem().equals(associatedAI.thinkTime())) {
            this.myComboBoxThinkTime.setSelectedItem(associatedAI.thinkTime());
        }
        if (this.myComboBoxThinkTime.isVisible()) {
            final int width = 50;
            final int height = 20;
            this.myComboBoxThinkTime.setBounds(this.myComboBox.getX() + this.myComboBox.getWidth() + 10, this.myComboBox.getY(), 50, 20);
        }
    }
    
    void drawAIFace(final Graphics2D g2d) {
        final Point2D drawPosn = new Point2D.Double(this.myComboBox.getX() + this.myComboBox.getWidth() + this.myComboBoxThinkTime.getWidth() + 16, this.myComboBox.getY() + 2);
        final double r = PlayerView.playerNameFont.getSize();
        final AI ai = DesktopApp.aiSelected()[ContextSnapshot.getContext().state().playerToAgent(this.playerId)].ai();
        InputStream in = null;
        final Color faceColor = Color.WHITE;
        if (ai != null && SettingsNetwork.getActiveGameId() == 0) {
            final double happinessValue = ai.estimateValue();
            if (happinessValue < -0.8) {
                in = this.getClass().getResourceAsStream("/svg/faces/symbola_sad.svg");
            }
            else if (happinessValue < -0.5) {
                in = this.getClass().getResourceAsStream("/svg/faces/symbola_scared.svg");
            }
            else if (happinessValue < -0.2) {
                in = this.getClass().getResourceAsStream("/svg/faces/symbola_worried.svg");
            }
            else if (happinessValue < 0.2) {
                in = this.getClass().getResourceAsStream("/svg/faces/symbola_neutral.svg");
            }
            else if (happinessValue < 0.5) {
                in = this.getClass().getResourceAsStream("/svg/faces/symbola_pleased.svg");
            }
            else if (happinessValue < 0.8) {
                in = this.getClass().getResourceAsStream("/svg/faces/symbola_happy.svg");
            }
            else {
                in = this.getClass().getResourceAsStream("/svg/faces/symbola_cool.svg");
            }
            try (final BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                final SVGGraphics2D svg = new SVGGraphics2D((int)r, (int)r);
                SVGtoImage.loadFromReader(svg, reader, (int)r, Color.BLACK, faceColor, true);
                g2d.drawImage(SVGUtil.createSVGImage(svg.getSVGDocument(), (int)r, (int)r), (int)drawPosn.getX(), (int)drawPosn.getY(), null);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public void mouseOverAt(final Point pixel) {
        for (int i = 0; i < DesktopApp.view().playerSwatchList.length; ++i) {
            final Rectangle rectangle = DesktopApp.view().playerSwatchList[i];
            final boolean overlap = GUIUtil.pointOverlapsRectangle(pixel, rectangle);
            if (DesktopApp.view().playerSwatchHover[i] != overlap) {
                DesktopApp.view().playerSwatchHover[i] = overlap;
                DesktopApp.view().repaint(rectangle.getBounds());
            }
        }
        for (int i = 0; i < DesktopApp.view().playerNameList.length; ++i) {
            final Rectangle rectangle = DesktopApp.view().playerNameList[i];
            final boolean overlap = GUIUtil.pointOverlapsRectangle(pixel, rectangle);
            if (DesktopApp.view().playerNameHover[i] != overlap) {
                DesktopApp.view().playerNameHover[i] = overlap;
                DesktopApp.view().repaint(rectangle.getBounds());
            }
        }
    }
    
    public String getNameAndExtrasString(final Context context, final Graphics2D g2d) {
        final Context instanceContext = context.currentInstanceContext();
        final Game instance = instanceContext.game();
        final int playerIndex = instanceContext.state().playerToAgent(this.playerId);
        final Font playerNameFont = g2d.getFont();
        String strName = DesktopApp.aiSelected()[playerIndex].name();
        String strExtras = "";
        String strAIName = "";
        if (DesktopApp.aiSelected()[playerIndex].menuItemName().label.equals("Ludii AI")) {
            strAIName = strAIName + " " + DesktopApp.aiSelected()[playerIndex].ai().friendlyName + " ";
        }
        if (DesktopApp.aiSelected()[playerIndex].menuItemName().label.equals("From JAR")) {
            strAIName = strAIName + " (" + DesktopApp.aiSelected()[playerIndex].ai().friendlyName + ")";
        }
        if ((instance.gameFlags() & 0x100L) != 0x0L && (instance.metadata().graphics().showScore() == WhenScoreType.Always || (instance.metadata().graphics().showScore() == WhenScoreType.AtEnd && instanceContext.trial().over()))) {
            strExtras = strExtras + " (" + instanceContext.score(this.playerId);
        }
        if (context.isAMatch()) {
            if (strExtras == "") {
                strExtras += " (";
            }
            else {
                strExtras += " : ";
            }
            strExtras += context.score(this.playerId);
        }
        if (strExtras != "") {
            strExtras += ")";
        }
        if (ContextSnapshot.getContext().game().requiresBet()) {
            strExtras = strExtras + " $" + context.state().amount(this.playerId);
        }
        if (ContextSnapshot.getContext().game().requiresTeams()) {
            strExtras = strExtras + " Team " + ContextSnapshot.getContext().state().getTeam(this.playerId);
        }
        if (SettingsNetwork.getActiveGameId() != 0 && SettingsNetwork.playerTimeRemaining[ContextSnapshot.getContext().state().playerToAgent(this.playerId) - 1] > 0) {
            strExtras = strExtras + " Time: " + SettingsNetwork.playerTimeRemaining[ContextSnapshot.getContext().state().playerToAgent(this.playerId) - 1] + "s";
        }
        strExtras = strAIName + strExtras;
        final int maxLengthPixels = 150;
        String shortendedString = "";
        for (int i = 0; i < strName.length(); ++i) {
            shortendedString += strName.charAt(i);
            final int stringWidth = (int)playerNameFont.getStringBounds(shortendedString, g2d.getFontRenderContext()).getWidth();
            if (stringWidth > 150) {
                shortendedString = shortendedString.substring(0, i - 2);
                shortendedString = (strName = shortendedString + "...");
                break;
            }
        }
        return strName + " " + strExtras;
    }
    
    private static ArrayList<Integer> getWinnerNumbers(final Context context) {
        final Game game = context.game();
        final ArrayList<Integer> winnerNumbers = new ArrayList<>();
        final int firstWinner = (context.trial().status() == null) ? 0 : context.trial().status().winner();
        if (game.requiresTeams()) {
            final int winningTeam = context.state().getTeam(firstWinner);
            for (int i = 1; i < game.players().size(); ++i) {
                if (context.state().getTeam(i) == winningTeam) {
                    winnerNumbers.add(i);
                }
            }
        }
        else {
            winnerNumbers.add(firstWinner);
        }
        return winnerNumbers;
    }
    
    private void determineHand(final Equipment equipment) {
        for (int i = 0; i < equipment.containers().length; ++i) {
            if (equipment.containers()[i].isHand() && equipment.containers()[i].owner() == this.playerId) {
                this.hand = equipment.containers()[i];
            }
        }
    }
    
    private int aiInterfaceWidth(final Context context) {
        final int agentComboBoxWidth = 150;
        final int thinkTimeComboboxWidth = 55;
        final int aiExpressionWidth = 30;
        if (this.playerView.anyPlayersAreAgents(context)) {
            return 235;
        }
        return 150;
    }
    
    @Override
    public int containerIndex() {
        if (this.hand == null) {
            return -1;
        }
        return this.hand.index();
    }
    
    public int playerId() {
        return this.playerId;
    }
}
