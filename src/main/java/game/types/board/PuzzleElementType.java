// 
// Decompiled by Procyon v0.5.36
// 

package game.types.board;

public enum PuzzleElementType
{
    Cell, 
    Edge, 
    Vertex, 
    Hint;
    
    public static SiteType convert(final PuzzleElementType puzzleElement) {
        switch (puzzleElement) {
            case Cell: {
                return SiteType.Cell;
            }
            case Edge: {
                return SiteType.Edge;
            }
            case Vertex: {
                return SiteType.Vertex;
            }
            default: {
                return null;
            }
        }
    }
}
