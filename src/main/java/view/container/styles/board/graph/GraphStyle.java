// 
// Decompiled by Procyon v0.5.36
// 

package view.container.styles.board.graph;

import game.equipment.container.Container;
import util.Context;
import view.container.aspects.designs.board.graph.GraphDesign;
import view.container.styles.board.puzzle.PuzzleStyle;

import java.awt.*;

public class GraphStyle extends PuzzleStyle
{
    protected final Color baseGraphColour;
    protected double baseVertexRadius;
    protected double baseLineWidth;
    
    public GraphStyle(final Container container, final Context context) {
        super(container, context);
        this.baseGraphColour = new Color(90, 90, 90);
        this.baseVertexRadius = 4.0;
        this.baseLineWidth = 0.5 * this.baseVertexRadius;
        this.containerDesign = new GraphDesign(this, this.boardPlacement, true, true);
    }
    
    public Color baseGraphColour() {
        return this.baseGraphColour;
    }
    
    public double baseVertexRadius() {
        return this.baseVertexRadius;
    }
    
    public void setBaseVertexRadius(final double vr) {
        this.baseVertexRadius = vr;
    }
    
    public double baseLineWidth() {
        return this.baseLineWidth;
    }
    
    public void setBaseLineWidth(final double lw) {
        this.baseLineWidth = lw;
    }
}
