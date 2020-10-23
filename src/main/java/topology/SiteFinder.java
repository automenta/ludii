// 
// Decompiled by Procyon v0.5.36
// 

package topology;

import game.equipment.container.board.Board;
import game.types.board.SiteType;

public final class SiteFinder
{
    public static TopologyElement find(final Board board, final String coord, final SiteType type) {
        if ((type == null && board.defaultSite() == SiteType.Cell) || (type == SiteType.Cell)) {
            for (final Cell cell : board.topology().cells()) {
                if (cell.label().equals(coord)) {
                    return cell;
                }
            }
        }
        else if ((type == null && board.defaultSite() == SiteType.Vertex) || (type == SiteType.Vertex)) {
            for (final Vertex vertex : board.topology().vertices()) {
                if (vertex.label().equals(coord)) {
                    return vertex;
                }
            }
        }
        return null;
    }
}
