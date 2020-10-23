// 
// Decompiled by Procyon v0.5.36
// 

package util;

import annotations.Hide;
import game.types.board.SiteType;
import topology.Cell;
import topology.Edge;
import topology.TopologyElement;
import topology.Vertex;

@Hide
public final class GraphUtilities
{
    private GraphUtilities() {
    }
    
    public static void addNeighbour(final SiteType type, final TopologyElement element, final TopologyElement neighbour) {
        switch (type) {
            case Cell -> ((Cell) element).neighbours().add((Cell) neighbour);
            case Vertex -> ((Vertex) element).neighbours().add((Vertex) neighbour);
            case Edge -> ((Edge) element).neighbours().add((Edge) neighbour);
        }
    }
    
    public static void addAdjacent(final SiteType type, final TopologyElement element, final TopologyElement adjacent) {
        switch (type) {
            case Cell -> ((Cell) element).adjacent().add((Cell) adjacent);
            case Vertex -> ((Vertex) element).adjacent().add((Vertex) adjacent);
        }
    }
    
    public static void addOrthogonal(final SiteType type, final TopologyElement element, final TopologyElement orthogonal) {
        switch (type) {
            case Cell -> ((Cell) element).orthogonal().add((Cell) orthogonal);
            case Vertex -> ((Vertex) element).orthogonal().add((Vertex) orthogonal);
        }
    }
    
    public static void addDiagonal(final SiteType type, final TopologyElement element, final TopologyElement diagonal) {
        switch (type) {
            case Cell -> ((Cell) element).diagonal().add((Cell) diagonal);
            case Vertex -> ((Vertex) element).diagonal().add((Vertex) diagonal);
        }
    }
    
    public static void addOff(final SiteType type, final TopologyElement element, final TopologyElement off) {
        switch (type) {
            case Cell -> ((Cell) element).off().add((Cell) off);
            case Vertex -> ((Vertex) element).off().add((Vertex) off);
        }
    }
}
