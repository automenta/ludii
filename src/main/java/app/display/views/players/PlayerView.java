// 
// Decompiled by Procyon v0.5.36
// 

package app.display.views.players;

import app.DesktopApp;
import app.display.MainWindow;
import app.display.views.View;
import bridge.Bridge;
import game.Game;
import manager.utils.ContextSnapshot;
import manager.utils.SettingsManager;
import util.Context;
import util.PlaneType;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class PlayerView extends View
{
    public final List<PlayerViewUser> playerSections = new ArrayList<>();
    public static final Font playerNameFont;
    
    public PlayerView() {
        final MainWindow mainWindow = DesktopApp.view();
        final Game game = ContextSnapshot.getContext().game();
        final int boardSize = DesktopApp.view().boardSize();
        final int numPlayers = game.players().count();
        final int maxHandHeight = 100;
        final double maxPanelPercentageHeight = 0.7;
        int width = 0;
        final int height = Math.min(100, (int)(mainWindow.getHeight() * 0.7 / numPlayers));
        int startX;
        int startY;
        if (DesktopApp.view().getWidth() < DesktopApp.view().getHeight()) {
            startX = 8;
            startY = boardSize;
            width = mainWindow.getWidth() - startX;
        }
        else {
            startX = boardSize;
            startY = 8;
            width = mainWindow.getWidth() - boardSize;
        }
        for (int pid = 1; pid <= numPlayers; ++pid) {
            final int x0 = startX;
            final int y0 = startY + (pid - 1) * height;
            final Rectangle placement = new Rectangle(x0, y0, width, height);
            final PlayerViewUser playerPage = new PlayerViewUser(placement, pid, this);
            MainWindow.getPanels().add(playerPage);
            this.playerSections.add(playerPage);
        }
        if (ContextSnapshot.getContext().hasSharedPlayer()) {
            final Rectangle placement2 = new Rectangle(0, 0, boardSize, mainWindow.getHeight() / 10);
            final PlayerViewShared naturePlayerPage = new PlayerViewShared(placement2, numPlayers + 1, this);
            MainWindow.getPanels().add(naturePlayerPage);
            this.playerSections.add(naturePlayerPage);
        }
        final int playerPanelWidth = mainWindow.getWidth() - boardSize;
        final int playerPanelHeight = startY + numPlayers * height + 16;
        this.placement.setBounds(boardSize, 0, playerPanelWidth, playerPanelHeight);
    }
    
    @Override
    public void paint(final Graphics2D g2d) {
        for (final PlayerViewUser p : this.playerSections) {
            p.paint(g2d);
        }
        this.paintDebug(g2d, Color.PINK);
    }
    
    public int maximalPlayerNameWidth(final Context context, final Graphics2D g2d) {
        int numUsers = this.playerSections.size();
        if (ContextSnapshot.getContext().hasSharedPlayer()) {
            --numUsers;
        }
        int maxNameWidth = 0;
        for (int panelIndex = 0; panelIndex < numUsers; ++panelIndex) {
            final String stringNameAndExtras = this.playerSections.get(panelIndex).getNameAndExtrasString(context, g2d);
            final Rectangle2D bounds = PlayerView.playerNameFont.getStringBounds(stringNameAndExtras, g2d.getFontRenderContext());
            maxNameWidth = Math.max((int)bounds.getWidth(), maxNameWidth);
        }
        return maxNameWidth;
    }
    
    public boolean anyPlayersAreAgents(final Context context) {
        boolean anyPlayersAreAgents = false;
        for (int i = 1; i <= context.game().players().count(); ++i) {
            if (DesktopApp.aiSelected()[i].ai() != null) {
                anyPlayersAreAgents = true;
            }
        }
        return anyPlayersAreAgents;
    }
    
    public void paintHand(final Graphics2D g2d, final Context context, final Rectangle placement, final int handIndex) {
        Bridge.getContainerStyle(handIndex).setPlacement(context, placement);
        if (SettingsManager.showPieces) {
            Bridge.getContainerStyle(handIndex).draw(g2d, PlaneType.COMPONENTS, context);
        }
        Bridge.getContainerStyle(handIndex).draw(g2d, PlaneType.INDICES, context);
        Bridge.getContainerStyle(handIndex).draw(g2d, PlaneType.POSSIBLEMOVES, context);
    }
    
    static {
        playerNameFont = new Font("Arial", 0, 16);
    }
}
