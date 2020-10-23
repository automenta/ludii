// 
// Decompiled by Procyon v0.5.36
// 

package view.container;

import game.types.board.SiteType;
import topology.*;
import util.Context;
import util.PlaneType;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;

public interface ContainerStyle
{
    void render(final PlaneType p0, final Context p1);
    
    void draw(final Graphics2D p0, final PlaneType p1, final Context p2);
    
    void setPlacement(final Context p0, final Rectangle p1);
    
    String containerSVGImage();
    
    String graphSVGImage();
    
    String dualSVGImage();
    
    List<Cell> drawnCells();
    
    List<Edge> drawnEdges();
    
    List<Vertex> drawnVertices();
    
    List<TopologyElement> drawnGraphElements();
    
    TopologyElement drawnGraphElement(final int p0, final SiteType p1);
    
    Rectangle placement();
    
    double cellRadius();
    
    int cellRadiusPixels();
    
    Point screenPosn(final Point2D p0);
    
    double containerScale();
    
    double containerZoom();
    
    double pieceScale();
    
    Topology topology();
    
    void drawPuzzleValue(final int p0, final int p1, final Context p2, final Graphics2D p3, final Point p4, final int p5);
    
    boolean ignorePieceSelectionLimit();
}
