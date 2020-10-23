// 
// Decompiled by Procyon v0.5.36
// 

package view.component.custom;

import bridge.Bridge;
import bridge.ViewControllerFactory;
import game.equipment.component.Component;
import game.equipment.container.board.Board;
import game.functions.dim.DimConstant;
import game.functions.graph.generators.basis.hex.HexagonOnHex;
import game.functions.graph.generators.basis.square.RectangleOnSquare;
import game.functions.graph.generators.basis.tri.TriangleOnTri;
import game.types.board.SiteType;
import gnu.trove.list.array.TIntArrayList;
import graphics.svg.SVGtoImage;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import topology.Cell;
import util.Context;
import util.SettingsColour;
import view.container.ContainerStyle;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class LargePieceStyle extends TileStyle
{
    protected Point size;
    private final ArrayList<Point> origin;
    protected ArrayList<Point2D> largeOffsets;
    protected Board boardForLargePiece;
    protected ArrayList<Point2D> originalCellLocations;
    
    public LargePieceStyle(final Component component) {
        super(component);
        this.origin = new ArrayList<>();
        this.largeOffsets = new ArrayList<>();
        this.boardForLargePiece = null;
        this.originalCellLocations = new ArrayList<>();
    }
    
    @Override
    public void renderImageSVG(final Context context, final int imageSize, final int localState, final boolean secondary, final int maskedValue) {
        this.colour = null;
        while (this.imageSVG.size() <= localState) {
            this.imageSVG.add(null);
        }
        this.imageSVG.set(localState, this.getLargeSVGImage(context, imageSize, localState, secondary, maskedValue));
    }
    
    public SVGGraphics2D getLargeSVGImage(final Context context, final int imageSize, final int state, final boolean secondary, final int maskedValue) {
        final int maxStepsForward = this.component.maxStepsForward() + 1;
        final int pieceScale = maxStepsForward * 2 + 1;
        if (this.boardForLargePiece == null) {
            final int numEdges = this.component.numSides();
            if (numEdges == 3) {
                this.boardForLargePiece = new Board(new TriangleOnTri(new DimConstant(pieceScale)), null, null, null, null, null);
            }
            if (numEdges == 6) {
                this.boardForLargePiece = new Board(new HexagonOnHex(new DimConstant(pieceScale)), null, null, null, null, null);
            }
            else {
                this.boardForLargePiece = new Board(new RectangleOnSquare(new DimConstant(pieceScale), null, null, null), null, null, null, null, null);
            }
            this.boardForLargePiece.createTopology(0, context.board().topology().edges().size());
            this.boardForLargePiece.setTopology(this.boardForLargePiece.topology());
            this.boardForLargePiece.topology().computeSupportedDirection(SiteType.Cell);
            this.boardForLargePiece.setStyle(context.board().style());
            for (final Cell c : this.boardForLargePiece.topology().cells()) {
                this.originalCellLocations.add(c.centroid());
            }
        }
        final ContainerStyle boardForLargePieceStyle = ViewControllerFactory.createStyle(this.boardForLargePiece, this.boardForLargePiece.style(), context);
        final TIntArrayList cellLocations = this.component.locs(context, this.boardForLargePiece.numSites() / 2 + 1, state, this.boardForLargePiece.topology());
        final double boardSizeDif = Bridge.getContainerStyle(context.board().index()).cellRadius() / boardForLargePieceStyle.cellRadius() * Bridge.getContainerStyle(context.board().index()).containerScale() * Bridge.getContainerStyle(context.board().index()).containerZoom();
        final int pixels = (int)(Bridge.getContainerStyle(0).placement().getWidth() * 1.25);
        int imageX = 0;
        int imageY = 0;
        int imageWidth = 0;
        int imageHeight = 0;
        double minCellX = 9999.0;
        double maxCellX = -9999.0;
        double minCellY = 9999.0;
        double maxCellY = -9999.0;
        final Point2D startPoint = this.boardForLargePiece.topology().cells().get(cellLocations.getQuick(0)).centroid();
        Point2D currentPoint = null;
        for (int i = 0; i < cellLocations.size(); ++i) {
            if (i > 0) {
                currentPoint = this.boardForLargePiece.topology().cells().get(cellLocations.get(i)).centroid();
                currentPoint.setLocation((currentPoint.getX() - startPoint.getX()) * pixels * boardSizeDif, (currentPoint.getY() - startPoint.getY()) * pixels * boardSizeDif);
                this.boardForLargePiece.topology().cells().get(cellLocations.get(i)).setCentroid(currentPoint.getX(), currentPoint.getY(), 0.0);
            }
            else {
                currentPoint = startPoint;
            }
            if (minCellX > currentPoint.getX()) {
                minCellX = currentPoint.getX();
            }
            if (maxCellX < currentPoint.getX()) {
                maxCellX = currentPoint.getX();
            }
            if (minCellY > currentPoint.getY()) {
                minCellY = currentPoint.getY();
            }
            if (maxCellY < currentPoint.getY()) {
                maxCellY = currentPoint.getY();
            }
            imageX = (int)Math.min(imageX, currentPoint.getX());
            imageY = (int)Math.min(imageY, currentPoint.getY());
            imageWidth = (int)Math.max(imageWidth, currentPoint.getX() + imageSize);
            imageHeight = (int)Math.max(imageHeight, currentPoint.getY() + imageSize);
        }
        final Point2D offsetPoint = new Point();
        offsetPoint.setLocation(minCellX + (maxCellX - minCellX) / 2.0, minCellY + (maxCellY - minCellY) / 2.0);
        while (this.largeOffsets.size() <= state) {
            this.largeOffsets.add(null);
        }
        this.largeOffsets.set(state, offsetPoint);
        this.size = new Point(imageWidth + Math.abs(imageX), imageHeight + Math.abs(imageY));
        while (this.origin.size() <= state) {
            this.origin.add(null);
        }
        final int x = -imageX;
        final int y = this.size.y + imageY - imageSize;
        this.origin.set(state, new Point(x, y));
        final SVGGraphics2D g2d = new SVGGraphics2D(this.size.x, this.size.y);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        final SVGGraphics2D imageToReturn = this.drawLargePieceVisuals(g2d, cellLocations, imageSize, imageX, imageY, state, context, secondary, maskedValue);
        for (int j = 0; j < this.boardForLargePiece.topology().cells().size(); ++j) {
            this.boardForLargePiece.topology().cells().get(j).setCentroid(this.originalCellLocations.get(j).getX(), this.originalCellLocations.get(j).getY(), 0.0);
        }
        return imageToReturn;
    }
    
    protected SVGGraphics2D drawLargePieceVisuals(final SVGGraphics2D g2d, final TIntArrayList cellLocations, final int imageSize, final int imageX, final int imageY, final int state, final Context context, final boolean secondary, final int maskedValue) {
        final String defaultFilePath = "/svg/shapes/square.svg";
        final InputStream in = this.getClass().getResourceAsStream("/svg/shapes/square.svg");
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            for (int i = 0; i < cellLocations.size(); ++i) {
                final Point2D currentPoint = this.boardForLargePiece.topology().cells().get(cellLocations.get(i)).centroid();
                final int y = this.size.y - ((int)currentPoint.getY() - imageY) - imageSize;
                final int x = (int)currentPoint.getX() - imageX;
                final SVGGraphics2D g2dIndividual = this.getTileSVGImage(context, imageSize, state, secondary, maskedValue);
                final Color pieceColour = SettingsColour.playerColour(this.component.owner(), context);
                SVGtoImage.loadFromSource(g2d, g2dIndividual.getSVGDocument(), imageSize + 3, x, y, pieceColour, pieceColour, false, 0);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return g2d;
    }
    
    @Override
    public ArrayList<Point2D> getLargeOffsets() {
        return this.largeOffsets;
    }
    
    @Override
    public ArrayList<Point> origin() {
        return this.origin;
    }
    
    @Override
    public Point largePieceSize() {
        return this.size;
    }
}
