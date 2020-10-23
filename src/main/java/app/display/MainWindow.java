// 
// Decompiled by Procyon v0.5.36
// 

package app.display;

import app.DesktopApp;
import app.StartApp;
import app.display.dialogs.SandboxDialog;
import app.display.dialogs.SettingsDialog;
import app.display.util.GUIUtil;
import app.display.util.GraphicsCache;
import app.display.util.SVGUtil;
import app.display.util.SpinnerFunctions;
import app.display.views.BoardView;
import app.display.views.OverlayView;
import app.display.views.View;
import app.display.views.players.PlayerView;
import app.display.views.tabs.TabView;
import app.display.views.tools.ToolButton;
import app.display.views.tools.ToolView;
import app.loading.GameLoading;
import app.sandbox.SandboxValueType;
import app.utils.SettingsDesktop;
import bridge.Bridge;
import game.Game;
import game.rules.play.moves.Moves;
import game.types.board.SiteType;
import game.types.component.CardType;
import main.Constants;
import manager.Manager;
import manager.network.SettingsNetwork;
import manager.referee.UserMoveHandler;
import manager.utils.ContextSnapshot;
import manager.utils.SettingsManager;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import topology.Cell;
import topology.Edge;
import topology.Vertex;
import util.*;
import util.action.Action;
import util.action.move.ActionMove;
import util.locations.FullLocation;
import util.locations.Location;
import util.state.State;
import util.state.containerState.ContainerState;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

public final class MainWindow extends JPanel implements MouseListener, MouseMotionListener
{
    protected static List<View> panels;
    protected static OverlayView overlayPanel;
    protected static BoardView boardPanel;
    protected static PlayerView playerPanel;
    protected static ToolView toolPanel;
    protected static TabView tabPanel;
    protected int width;
    protected int height;
    protected Location locnFromInfo;
    protected Location locnToInfo;
    private static boolean pieceSelectedThisClick;
    private static boolean mouseDown;
    boolean pickingDialog;
    private int boardSize;
    public static int currentWalkExtra;
    public Rectangle[] playerSwatchList;
    public Rectangle[] playerNameList;
    public boolean[] playerSwatchHover;
    public boolean[] playerNameHover;
    public static final int MIN_UI_FONT_SIZE = 12;
    public static final int MAX_UI_FONT_SIZE = 24;
    private static String temporaryMessage;
    static String volatileMessage;
    
    public MainWindow() {
        this.pickingDialog = false;
        this.playerSwatchList = new Rectangle[] { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null };
        this.playerNameList = new Rectangle[] { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null };
        this.playerSwatchHover = new boolean[] { false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false };
        this.playerNameHover = new boolean[] { false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false };
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }
    
    public void createPanels() {
        MainWindow.panels.clear();
        this.removeAll();
        GraphicsCache.clearAllCachedImages();
        MainWindow.boardPanel = new BoardView();
        MainWindow.panels.add(MainWindow.boardPanel);
        MainWindow.playerPanel = new PlayerView();
        MainWindow.panels.add(MainWindow.playerPanel);
        MainWindow.toolPanel = new ToolView();
        MainWindow.panels.add(MainWindow.toolPanel);
        MainWindow.tabPanel = new TabView();
        MainWindow.panels.add(MainWindow.tabPanel);
        MainWindow.overlayPanel = new OverlayView();
        MainWindow.panels.add(MainWindow.overlayPanel);
        SpinnerFunctions.initialiseSpinnerGraphics(this);
    }
    
    public void paintComponent(final Graphics g) {
        if (SettingsDesktop.jumpingMoveSavedImage != null) {
            g.drawImage(SettingsDesktop.jumpingMoveSavedImage, 0, 0, null);
            return;
        }
        try {
            final Graphics2D g2d = (Graphics2D)g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            ContextSnapshot.setContext(Manager.ref().context());
            SettingsVC.thisFrameIsAnimated = SettingsManager.nextFrameIsAnimated;
            setDisplayFont();
            GraphicsCache.drawnImageInfo.clear();
            setPlayerColours(ContextSnapshot.getContext());
            if (MainWindow.panels.isEmpty() || this.width != this.getWidth() || this.height != this.getHeight()) {
                this.width = this.getWidth();
                this.height = this.getHeight();
                this.createPanels();
            }
            if (SettingsDesktop.darkMode) {
                g2d.setColor(Color.black);
            }
            else {
                g2d.setColor(Color.white);
            }
            g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
            for (final View panel : MainWindow.panels) {
                if (g.getClipBounds().intersects(panel.placement())) {
                    panel.paint(g2d);
                }
            }
            SpinnerFunctions.drawSpinner(g2d);
            if (SettingsVC.errorReport != "") {
                DesktopApp.playerApp().addTextToStatusPanel(SettingsVC.errorReport);
                SettingsVC.errorReport = "";
            }
            final metadata.graphics.Graphics graphics = ContextSnapshot.getContext().game().metadata().graphics();
            if (graphics.getErrorReport() != "") {
                DesktopApp.playerApp().addTextToStatusPanel(graphics.getErrorReport());
                graphics.setErrorReport("");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            if (ContextSnapshot.getContext().game().description().raw().equals(Constants.FAIL_SAFE_GAME_DESCRIPTION)) {
                final File brokenPreferences = new File("." + File.separator + "ludii_preferences.json");
                brokenPreferences.delete();
                final File brokenTrial = new File("." + File.separator + "ludii.trl");
                brokenTrial.delete();
                DesktopApp.frame().dispose();
                StartApp.main(new String[0]);
            }
            GameLoading.loadFailSafeGame();
            EventQueue.invokeLater(() -> {
                setTemporaryMessage("Error painting components. Loading default game (Tic-Tac-Toe).");
                DesktopApp.playerApp().addTextToStatusPanel(e.getMessage() + "\n");
            });
        }
    }
    
    private boolean checkPointOverlapsButton(final MouseEvent e) {
        if (GUIUtil.pointOverlapsRectangles(e.getPoint(), this.playerSwatchList)) {
            for (int i = 0; i < this.playerSwatchList.length; ++i) {
                final Rectangle r = this.playerSwatchList[i];
                if (GUIUtil.pointOverlapsRectangle(e.getPoint(), r)) {
                    UserMoveHandler.playerSelectMove(i);
                }
            }
            return true;
        }
        if (GUIUtil.pointOverlapsRectangles(e.getPoint(), this.playerNameList)) {
            SettingsDialog.createAndShowGUI(1);
            return true;
        }
        if (MainWindow.tabPanel.placement().contains(e.getPoint())) {
            MainWindow.tabPanel.clickAt(e.getPoint());
            return true;
        }
        if (MainWindow.toolPanel.placement().contains(e.getPoint())) {
            MainWindow.toolPanel.clickAt(e.getPoint());
            return true;
        }
        return false;
    }
    
    @Override
    public void mousePressed(final MouseEvent e) {
        DesktopApp.frame().requestFocus();
        final Context context = ContextSnapshot.getContext();
        final State state = context.state();
        if (this.checkPointOverlapsButton(e)) {
            SettingsVC.selectedLocation = new FullLocation(-1);
            return;
        }
        MainWindow.mouseDown = true;
        MainWindow.pieceSelectedThisClick = false;
        MainWindow.currentWalkExtra = 0;
        this.pickingDialog = false;
        this.locnFromInfo = calculateNearestLocation(ContextSnapshot.getContext(), e.getPoint(), true, true);
        if (this.locnFromInfo == null) {
            return;
        }
        final Moves legal = context.game().moves(context);
        boolean matchFrom = false;
        boolean matchTo = false;
        for (final Move move : legal.moves()) {
            if (this.movePossible(move, state)) {
                if (move.getFromLocation().equalsLoc(SettingsVC.selectedLocation) && move.getToLocation().equalsLoc(this.locnFromInfo)) {
                    matchTo = true;
                }
                if (move.isOrientedMove() || matchTo || !move.getToLocation().equalsLoc(SettingsVC.selectedLocation) || !move.getFromLocation().equalsLoc(this.locnFromInfo)) {
                    continue;
                }
                matchTo = true;
            }
        }
        if (!matchTo) {
            for (final Move move : legal.moves()) {
                if (this.movePossible(move, state)) {
                    if (move.from() == this.locnFromInfo.site() && move.levelFrom() == this.locnFromInfo.level()) {
                        matchFrom = true;
                    }
                    if (move.isOrientedMove() || matchFrom || move.to() != this.locnFromInfo.site()) {
                        continue;
                    }
                    matchFrom = true;
                }
            }
        }
        if (matchFrom) {
            MainWindow.pieceSelectedThisClick = !this.locnFromInfo.equalsLoc(SettingsVC.selectedLocation) || MainWindow.pieceSelectedThisClick;
            SettingsVC.selectedLocation = this.locnFromInfo;
        }
        else if (SettingsVC.sandboxMode) {
            SettingsVC.selectedLocation = this.locnFromInfo;
            SettingsVC.pieceBeingDragged = false;
        }
    }
    
    private boolean movePossible(final Move move, final State state) {
        return (DesktopApp.aiSelected()[state.playerToAgent(move.mover())].ai() == null || SettingsManager.agentsPaused) && (SettingsNetwork.getActiveGameId() == 0 || SettingsNetwork.getNetworkPlayerNumber() == move.mover()) && ((move.fromType() == SiteType.Edge && (this.locnFromInfo.siteType() == SiteType.Edge || this.locnFromInfo.siteType() == SiteType.Vertex)) || (move.fromType() == SiteType.Vertex && this.locnFromInfo.siteType() == SiteType.Vertex) || (move.fromType() == SiteType.Cell && this.locnFromInfo.siteType() == SiteType.Cell));
    }
    
    @Override
    public void mouseReleased(final MouseEvent e) {
        final Context context = ContextSnapshot.getContext();
        this.locnToInfo = calculateNearestLocation(context, e.getPoint(), true, false);
        if (SettingsManager.dragComponent != null && Bridge.getComponentStyle(SettingsManager.dragComponent.index()).getLargeOffsets().size() > SettingsManager.dragComponentState) {
            final Point newPoint = e.getPoint();
            newPoint.x -= (int)Bridge.getComponentStyle(SettingsManager.dragComponent.index()).getLargeOffsets().get(SettingsManager.dragComponentState).getX();
            newPoint.y += (int)Bridge.getComponentStyle(SettingsManager.dragComponent.index()).getLargeOffsets().get(SettingsManager.dragComponentState).getY();
            this.locnToInfo = calculateNearestLocation(context, newPoint, true, false);
        }
        if (SettingsVC.SelectingConsequenceMove) {
            if (this.locnToInfo != null) {
                UserMoveHandler.applyConsequenceChosen(this.locnToInfo);
            }
        }
        else if (SettingsVC.selectedLocation.site() != -1 && !MainWindow.tabPanel.placement().contains(e.getPoint()) && !MainWindow.toolPanel.placement().contains(e.getPoint())) {
            if (this.locnFromInfo == null || this.locnToInfo == null) {
                return;
            }
            if (this.locnFromInfo.site() != -1 || this.locnToInfo.site() != -1) {
                if (SettingsVC.sandboxMode) {
                    if (this.locnToInfo.equalsLoc(this.locnFromInfo)) {
                        SandboxDialog.createAndShowGUI(Manager.ref().context(), this.locnToInfo, SandboxValueType.Component);
                    }
                    else {
                        final Context realContext = Manager.ref().context();
                        final int currentMover = realContext.state().mover();
                        final int nextMover = realContext.state().next();
                        final int previousMover = realContext.state().prev();
                        final Action actionMove = new ActionMove(SiteType.Cell, this.locnFromInfo.site(), -1, SiteType.Cell, this.locnToInfo.site(), -1, -1, -1, true);
                        actionMove.apply(realContext, false);
                        SettingsVC.selectedLocation = new FullLocation(-1);
                        realContext.state().setMover(currentMover);
                        realContext.state().setNext(nextMover);
                        realContext.state().setPrev(previousMover);
                        Manager.app.updateTabs(realContext);
                    }
                }
                else {
                    if (SettingsVC.selectedLocation.site() != -1) {
                        this.locnFromInfo = SettingsVC.selectedLocation;
                    }
                    if (context.game().isDeductionPuzzle()) {
                        UserMoveHandler.tryPuzzleMove(this.locnFromInfo, this.locnToInfo);
                    }
                    else {
                        UserMoveHandler.tryGameMove(this.locnFromInfo, this.locnToInfo, MainWindow.pieceSelectedThisClick, this.pickingDialog);
                    }
                }
            }
            this.locnFromInfo = new FullLocation(-1);
            this.locnToInfo = new FullLocation(-1);
        }
        SettingsVC.pieceBeingDragged = false;
        MainWindow.mouseDown = false;
        Manager.app.repaint();
    }
    
    @Override
    public void mouseDragged(final MouseEvent me) {
        DesktopApp.frame().requestFocus();
        final Context context = ContextSnapshot.getContext();
        boolean dragMoveLegal = false;
        final Moves legal = context.game().moves(context);
        for (final Move m : legal.moves()) {
            if (SettingsNetwork.getActiveGameId() != 0 && SettingsNetwork.getNetworkPlayerNumber() != m.mover()) {
                continue;
            }
            if (m.from() != m.to() && m.getFromLocation().equalsLoc(SettingsVC.selectedLocation)) {
                dragMoveLegal = true;
                break;
            }
            if (SettingsVC.sandboxMode) {
                dragMoveLegal = true;
                break;
            }
        }
        final Game game = context.game();
        if (!game.isDeductionPuzzle() && SettingsVC.selectedLocation.site() != -1 && dragMoveLegal) {
            final Point newMousePoint = this.getMousePosition();
            try {
                if (!SettingsVC.pieceBeingDragged) {
                    Manager.app.repaint();
                }
                SettingsVC.pieceBeingDragged = true;
                MainWindow.pieceSelectedThisClick = true;
            }
            catch (NullPointerException e) {
                e.printStackTrace();
            }
            GUIUtil.repaintComponentBetweenPoints(context, SettingsVC.selectedLocation, SettingsManager.oldMousePoint, newMousePoint);
            SettingsManager.oldMousePoint = newMousePoint;
        }
    }
    
    @Override
    public void mouseMoved(final MouseEvent e) {
        try {
            for (final View view : MainWindow.panels) {
                view.mouseOverAt(e.getPoint());
            }
            this.displayToolTipMessage(e.getPoint());
        }
        catch (Exception ex) {}
    }
    
    @Override
    public void mouseEntered(final MouseEvent e) {
        Manager.app.repaint();
    }
    
    @Override
    public void mouseExited(final MouseEvent e) {
        Manager.app.repaint();
    }
    
    public void displayToolTipMessage(final Point pt) {
        ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
        ToolTipManager.sharedInstance().setReshowDelay(500);
        boolean toolTipShown = false;
        for (final ToolButton toolButton : MainWindow.toolPanel.buttons) {
            if (toolButton != null && toolButton.mouseOver()) {
                String toolTipMessage = "<html>";
                toolTipMessage += toolButton.tooltipMessage();
                toolTipMessage += "</html>";
                this.setToolTipText(toolTipMessage);
                toolTipShown = true;
                break;
            }
        }
        if (!toolTipShown && SettingsDesktop.cursorTooltipDev && SettingsNetwork.getActiveGameId() == 0) {
            try {
                final Context context = ContextSnapshot.getContext();
                final Game game = context.game();
                ToolTipManager.sharedInstance().setEnabled(true);
                final Point ptOff = new Point(pt.x, pt.y);
                final Location location = calculateNearestLocation(context, ptOff, false, false);
                final int index = location.site();
                final int level = location.level();
                final SiteType type = location.siteType();
                final int containerId = ContainerUtil.getContainerId(context, index, type);
                final ContainerState cs = context.state().containerStates()[containerId];
                final int componentIndex = cs.what(index, level, type);
                String componentName = "";
                game.equipment.component.Component component = null;
                if (componentIndex != 0) {
                    component = context.equipment().components()[componentIndex];
                    componentName = component.name();
                }
                final int owner = cs.who(index, type);
                final int localState = cs.state(index, type);
                final int rotationState = cs.rotation(index, level, type);
                final int countState = cs.count(index, type);
                final int cardSuit = component.isCard() ? component.suit() : -1;
                final int cardRank = component.rank();
                final int trumpRank = component.trumpRank();
                final int trumpValue = component.trumpValue();
                final CardType cardType = component.cardType();
                final int value1 = component.getValue();
                final int value2 = component.getValue2();
                final int stackSize = cs.sizeStack(index, type);
                final String[] hidden = new String[game.players().count()];
                if (game.hiddenInformation() || game.hasCard()) {
                    for (int i = 1; i <= game.players().count(); ++i) {
                        if (cs.isInvisible(index, i, type)) {
                            hidden[i - 1] = "invisible";
                        }
                        else if (cs.isMasked(index, i, type)) {
                            hidden[i - 1] = "masked";
                        }
                        else {
                            hidden[i - 1] = "visible";
                        }
                    }
                }
                String toolTipMessage2 = "<html>";
                if (component != null) {
                    try {
                        final int imageSize = 100;
                        final File outputfile = File.createTempFile("tooltipImage", ".png");
                        outputfile.deleteOnExit();
                        String fullPath = outputfile.getAbsolutePath();
                        fullPath = "file:" + fullPath.replaceAll(Pattern.quote("\\"), "/");
                        Bridge.getComponentStyle(component.index()).renderImageSVG(context, 100, localState, true, 0);
                        final SVGGraphics2D svg = Bridge.getComponentStyle(component.index()).getImageSVG(localState);
                        final BufferedImage toolTipImage = SVGUtil.createSVGImage(svg.getSVGDocument(), 100.0, 100.0);
                        ImageIO.write(toolTipImage, "png", outputfile);
                        toolTipMessage2 = toolTipMessage2 + "<img src=\"" + fullPath + "\"><br>";
                    }
                    catch (Exception ex) {}
                }
                toolTipMessage2 = toolTipMessage2 + "Index: " + index + "<br>";
                if (type != null) {
                    toolTipMessage2 = toolTipMessage2 + "Type: " + type + "<br>";
                }
                if (componentIndex != 0) {
                    toolTipMessage2 = toolTipMessage2 + "componentName: " + componentName + "<br>";
                }
                if (componentIndex != 0) {
                    toolTipMessage2 = toolTipMessage2 + "componentIndex: " + componentIndex + "<br>";
                }
                if (owner != 0) {
                    toolTipMessage2 = toolTipMessage2 + "Owner: " + owner + "<br>";
                }
                if (localState != 0) {
                    toolTipMessage2 = toolTipMessage2 + "localState: " + localState + "<br>";
                }
                toolTipMessage2 = toolTipMessage2 + "rotationState: " + rotationState + "<br>";
                if (countState != 0) {
                    toolTipMessage2 = toolTipMessage2 + "countState: " + countState + "<br>";
                }
                if (value1 != -1) {
                    toolTipMessage2 = toolTipMessage2 + "value1: " + value1 + "<br>";
                }
                if (value2 != -1) {
                    toolTipMessage2 = toolTipMessage2 + "value2: " + value2 + "<br>";
                }
                if (game.isStacking()) {
                    toolTipMessage2 = toolTipMessage2 + "stackSize: " + stackSize + "<br>";
                }
                if (game.hasCard()) {
                    toolTipMessage2 = toolTipMessage2 + "cardSuit: " + cardSuit + "<br>";
                    toolTipMessage2 = toolTipMessage2 + "cardRank: " + cardRank + "<br>";
                    toolTipMessage2 = toolTipMessage2 + "trumpRank: " + trumpRank + "<br>";
                    toolTipMessage2 = toolTipMessage2 + "trumpValue: " + trumpValue + "<br>";
                    toolTipMessage2 = toolTipMessage2 + "cardType: " + cardType + "<br>";
                }
                for (int j = 0; j < hidden.length; ++j) {
                    if (hidden[j] != null) {
                        toolTipMessage2 = toolTipMessage2 + "Player " + (j + 1) + " hidden: " + hidden[j] + "<br>";
                    }
                }
                toolTipMessage2 += "</html>";
                this.setToolTipText(toolTipMessage2);
                toolTipShown = true;
            }
            catch (Exception e) {
                this.setToolTipText(null);
                return;
            }
        }
        if (!toolTipShown) {
            this.setToolTipText(null);
        }
    }
    
    public static Location calculateNearestLocation(final Context context, final Point pt, final boolean legal, final boolean moveFrom) {
        Location location = null;
        for (final View view : MainWindow.panels) {
            if (view.placement().contains(pt) && view.containerIndex() != -1) {
                if (legal) {
                    location = Bridge.getContainerController(view.containerIndex()).calculateNearestLocation(context, pt, moveFrom);
                }
                else {
                    location = Bridge.getContainerController(view.containerIndex()).calculateNearestLocationAll(context, pt);
                }
            }
        }
        return location;
    }
    
    protected static void setDisplayFont() {
        int maxDisplayNumber = 0;
        int minCellSize = 9999999;
        int maxCoordDigitLength = 0;
        for (final game.equipment.container.Container container : ContextSnapshot.getContext().equipment().containers()) {
            final int maxVertices = container.topology().cells().size();
            final int maxEdges = container.topology().edges().size();
            final int maxFaces = container.topology().vertices().size();
            maxDisplayNumber = Math.max(maxDisplayNumber, Math.max(maxVertices, Math.max(maxEdges, maxFaces)));
            minCellSize = Math.min(minCellSize, Bridge.getContainerStyle(container.index()).cellRadiusPixels());
            for (final Vertex vertex : container.topology().vertices()) {
                if (vertex.label().length() > maxCoordDigitLength) {
                    maxCoordDigitLength = vertex.label().length();
                }
            }
            for (final Edge edge : container.topology().edges()) {
                if (edge.label().length() > maxCoordDigitLength) {
                    maxCoordDigitLength = edge.label().length();
                }
            }
            for (final Cell cell : container.topology().cells()) {
                if (cell.label().length() > maxCoordDigitLength) {
                    maxCoordDigitLength = cell.label().length();
                }
            }
        }
        final int maxStringLength = Math.max(maxCoordDigitLength, Integer.toString(maxDisplayNumber).length());
        int fontSize = (int)(minCellSize * (1.0 - maxStringLength * 0.1));
        if (fontSize < 12) {
            fontSize = 12;
        }
        else if (fontSize > 24) {
            fontSize = 24;
        }
        SettingsVC.displayFont = new Font("Arial", 1, fontSize);
    }
    
    private static void setPlayerColours(final Context context) {
        for (int pid = 0; pid <= context.game().players().count() + 1; ++pid) {
            final Color colour = context.game().metadata().graphics().playerColour(pid, context);
            if (pid > context.game().players().count()) {
                pid = 17;
            }
            if (colour != null) {
                if (!SettingsColour.isCustomPlayerColoursFound()) {
                    SettingsColour.getCustomPlayerColours()[pid] = colour;
                }
                SettingsColour.getDefaultPlayerColours()[pid] = colour;
            }
        }
        SettingsColour.setCustomPlayerColoursFound(true);
    }
    
    public static BoardView getStatePanel() {
        return MainWindow.boardPanel;
    }
    
    public static PlayerView getPlayerPanel() {
        return MainWindow.playerPanel;
    }
    
    public int boardSize() {
        return this.boardSize;
    }
    
    public static List<View> getPanels() {
        return MainWindow.panels;
    }
    
    public static TabView tabPanel() {
        return MainWindow.tabPanel;
    }
    
    public static ToolView toolPanel() {
        return MainWindow.toolPanel;
    }
    
    public static boolean pieceSelectedThisClick() {
        return MainWindow.pieceSelectedThisClick;
    }
    
    public static boolean mouseDown() {
        return MainWindow.mouseDown;
    }
    
    public static Rectangle getPlacementforContainer(final int containerIndex) {
        for (final View p : MainWindow.panels) {
            if (p.containerIndex() == containerIndex) {
                return p.placement();
            }
        }
        return null;
    }
    
    public static int getContainerId(final int site) {
        if (site == -1) {
            return -1;
        }
        final int containerId = ContextSnapshot.getContext().containerId()[site];
        return containerId;
    }
    
    public void setResolution(final int resolution) {
        this.boardSize = resolution;
    }
    
    public static String temporaryMessage() {
        return MainWindow.temporaryMessage;
    }
    
    public static String volatileMessage() {
        return MainWindow.volatileMessage;
    }
    
    public static void setTemporaryMessage(final String s) {
        if (s.isEmpty()) {
            MainWindow.temporaryMessage = "";
            MainWindow.volatileMessage = "";
        }
        else if (!MainWindow.temporaryMessage.contains(s)) {
            MainWindow.temporaryMessage = MainWindow.temporaryMessage + " " + s;
        }
    }
    
    public static void setVolatileMessage(final String s) {
        MainWindow.volatileMessage = s;
        final Timer timer = new Timer(3000, arg0 -> {
            MainWindow.volatileMessage = "";
            Manager.app.repaint();
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    @Override
    public void mouseClicked(final MouseEvent e) {
    }
    
    static {
        MainWindow.panels = new CopyOnWriteArrayList<>();
        MainWindow.pieceSelectedThisClick = false;
        MainWindow.mouseDown = false;
        MainWindow.currentWalkExtra = 0;
        MainWindow.temporaryMessage = "";
        MainWindow.volatileMessage = "";
    }
}
