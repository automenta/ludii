// 
// Decompiled by Procyon v0.5.36
// 

package app.display.util;

import app.DesktopApp;
import app.display.views.View;
import bridge.Bridge;
import manager.ai.AIMenuName;
import manager.utils.ContextSnapshot;
import metadata.graphics.util.PieceStackType;
import search.flat.FlatMonteCarlo;
import search.mcts.MCTS;
import search.mcts.finalmoveselection.RobustChild;
import search.mcts.playout.RandomPlayout;
import search.mcts.selection.McGRAVE;
import search.minimax.AlphaBetaSearch;
import util.ContainerUtil;
import util.Context;
import util.locations.Location;
import util.state.containerState.ContainerState;
import utils.RandomAI;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GUIUtil
{
    public static boolean pointOverlapsRectangle(final Point p, final Rectangle rectangle) {
        return pointOverlapsRectangles(p, new Rectangle[] { rectangle });
    }
    
    public static boolean pointOverlapsRectangles(final Point p, final Rectangle[] rectangleList) {
        final int bufferDistance = 2;
        for (final Rectangle r : rectangleList) {
            if (r != null && p.x > r.x - 2 && p.x < r.x + r.width + 2 && p.y > r.y - 2 && p.y < r.y + r.height + 2) {
                return true;
            }
        }
        return false;
    }
    
    public static View calculateClickedPanel(final List<View> panels, final Point pt) {
        View clickedPanel = null;
        for (final View p : panels) {
            final Rectangle placement = p.placement();
            if (placement.contains(pt)) {
                clickedPanel = p;
                break;
            }
        }
        return clickedPanel;
    }
    
    public static boolean isMac() {
        final String osName = System.getProperty("os.name");
        final boolean isMac = osName.toLowerCase().startsWith("mac os x");
        return isMac;
    }
    
    public static void gameScreenshot(final String savedName) {


        EventQueue.invokeLater(() -> {

            Robot robot = null;
            try {
                robot = new Robot();
            } catch (AWTException e) {
                e.printStackTrace();
                return;
            }


            Container panel = DesktopApp.frame().getContentPane();
            Point pos = panel.getLocationOnScreen();
            Rectangle bounds = panel.getBounds();
            bounds.x = pos.x-1;
            bounds.y = pos.y-1;
            bounds.width += 2;
            bounds.height += 2;
            try {
                ImageIO.write(robot.createScreenCapture(bounds), "png", new File(savedName + ".png"));
            }
            catch (IOException e2) {
                e2.printStackTrace();
            }
        });
    }
    
    public static ArrayList<String> getAiStrings(final boolean includeHuman) {
        final ArrayList<String> aiStrings = new ArrayList<>();
        if (includeHuman) {
            aiStrings.add(AIMenuName.Human.label);
        }
        aiStrings.add(AIMenuName.LudiiAI.label);
        if (new RandomAI().supportsGame(ContextSnapshot.getContext().game())) {
            aiStrings.add(AIMenuName.Random.label);
        }
        if (new FlatMonteCarlo().supportsGame(ContextSnapshot.getContext().game())) {
            aiStrings.add(AIMenuName.FlatMC.label);
        }
        if (MCTS.createUCT().supportsGame(ContextSnapshot.getContext().game())) {
            aiStrings.add(AIMenuName.UCT.label);
            aiStrings.add(AIMenuName.UCTUncapped.label);
        }
        if (new MCTS(new McGRAVE(), new RandomPlayout(200), new RobustChild()).supportsGame(ContextSnapshot.getContext().game())) {
            aiStrings.add(AIMenuName.MCGRAVE.label);
        }
        if (MCTS.createBiasedMCTS(true).supportsGame(ContextSnapshot.getContext().game())) {
            aiStrings.add(AIMenuName.BiasedMCTS.label);
            aiStrings.add(AIMenuName.BiasedMCTSUniformPlayouts.label);
        }
        if (AlphaBetaSearch.createAlphaBeta().supportsGame(ContextSnapshot.getContext().game())) {
            aiStrings.add(AIMenuName.AlphaBeta.label);
        }
        aiStrings.add(AIMenuName.FromJAR.label);
        return aiStrings;
    }
    
    public static void repaintComponentBetweenPoints(final Context context, final Location componentLocation, final Point oldPoint, final Point newPoint) {
        try {
            if (ContextSnapshot.getContext().game().hasLargePiece()) {
                DesktopApp.view().repaint();
            }
            final int cellSize = Bridge.getContainerStyle(context.board().index()).cellRadiusPixels() * 2;
            final int containerId = ContainerUtil.getContainerId(context, componentLocation.site(), componentLocation.siteType());
            final ContainerState cs = context.state().containerStates()[containerId];
            final int localState = cs.state(componentLocation.site(), componentLocation.level(), componentLocation.siteType());
            final PieceStackType componentStackType = context.metadata().graphics().stackType(context.equipment().containers()[containerId], context, componentLocation.site(), componentLocation.siteType(), localState);
            int maxComponentSize = cellSize;
            int draggedStackSize = 0;
            for (int level = componentLocation.level(); level < 32; ++level) {
                ++draggedStackSize;
                final int what = cs.what(componentLocation.site(), componentLocation.level(), componentLocation.siteType());
                if (what == 0) {
                    break;
                }
                final int isMasked = cs.isMasked(componentLocation.site(), componentLocation.level(), cs.who(componentLocation.site(), componentLocation.level(), componentLocation.siteType()), componentLocation.siteType()) ? 1 : 0;
                final int componentSize = GraphicsCache.getComponentImage(containerId, what, localState, isMasked).getWidth();
                if (componentSize > maxComponentSize) {
                    maxComponentSize = componentSize;
                }
                if (!context.game().isStacking()) {
                    break;
                }
                if (componentStackType.midStackSelectionValid()) {
                    break;
                }
            }
            final int midX = (newPoint.x + oldPoint.x) / 2;
            int midY = (newPoint.y + oldPoint.y) / 2;
            final int width = Math.abs(newPoint.x - oldPoint.x) + maxComponentSize + cellSize;
            int height = Math.abs(newPoint.y - oldPoint.y) + maxComponentSize + cellSize;
            if (draggedStackSize > 1 && !componentStackType.midStackSelectionValid()) {
                height = DesktopApp.frame().getHeight();
                midY = height / 2;
            }
            final Rectangle repaintArea = new Rectangle(midX - width / 2, midY - height / 2, width, height);
            DesktopApp.view().repaint(repaintArea);
        }
        catch (Exception ex) {}
    }
}
