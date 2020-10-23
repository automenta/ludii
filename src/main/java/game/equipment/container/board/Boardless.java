// 
// Decompiled by Procyon v0.5.36
// 

package game.equipment.container.board;

import game.Game;
import game.functions.dim.DimConstant;
import game.functions.graph.generators.basis.hex.HexagonOnHex;
import game.functions.graph.generators.basis.square.RectangleOnSquare;
import game.functions.graph.generators.basis.tri.TriangleOnTri;
import game.types.board.SiteType;
import game.types.board.TilingBoardlessType;
import metadata.graphics.util.ContainerStyleType;

public class Boardless extends Board
{
    private static final long serialVersionUID = 1L;
    
    public Boardless(final TilingBoardlessType tiling) {
        super((tiling == TilingBoardlessType.Square) ? new RectangleOnSquare(new DimConstant(41), null, null, null) : ((tiling == TilingBoardlessType.Hexagonal) ? new HexagonOnHex(new DimConstant(21)) : new TriangleOnTri(new DimConstant(41))), null, null, null, null, SiteType.Cell);
        this.style = ContainerStyleType.Boardless;
    }
    
    @Override
    public boolean isBoardless() {
        return true;
    }
    
    @Override
    public long gameFlags(final Game game) {
        return super.gameFlags(game) | 0x20L;
    }
}
