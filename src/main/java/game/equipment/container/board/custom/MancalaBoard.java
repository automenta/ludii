// 
// Decompiled by Procyon v0.5.36
// 

package game.equipment.container.board.custom;

import annotations.Name;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.equipment.container.board.Board;
import game.equipment.container.board.Track;
import game.functions.dim.DimConstant;
import game.functions.floats.FloatConstant;
import game.functions.graph.BaseGraphFunction;
import game.functions.graph.GraphFunction;
import game.functions.graph.generators.basis.square.Square;
import game.functions.graph.generators.shape.Rectangle;
import game.functions.graph.operators.Shift;
import game.functions.graph.operators.Union;
import game.types.board.SiteType;
import game.types.board.StoreType;
import game.util.graph.Graph;
import util.Context;

public class MancalaBoard extends Board
{
    private static final long serialVersionUID = 1L;
    private final int numRows;
    private final int numColumns;
    private final StoreType storeType;
    private final int numStore;
    
    public MancalaBoard(final Integer rows, final Integer columns, @Opt @Name final StoreType store, @Opt @Name final Integer numStores, @Opt @Or final Track track, @Opt @Or final Track[] tracks) {
        super(new BaseGraphFunction() {
            private static final long serialVersionUID = 1L;
            
            @Override
            public Graph eval(final Context context, final SiteType siteType) {
                final int numRows = rows;
                final int numColumns = columns;
                final StoreType storeType = (store == null) ? StoreType.Outer : store;
                final int numberStore = (numStores == null) ? 2 : numStores;
                if (storeType == StoreType.Inner || numberStore != 2 || numRows < 2 || numRows > 4) {
                    return Square.construct(null, new DimConstant(rows), null, null).eval(context, siteType);
                }
                if (numRows == 2) {
                    return this.makeMancalaTwoRows(storeType, numColumns).eval(context, siteType);
                }
                if (numRows == 3) {
                    return this.makeMancalaThreeRows(storeType, numColumns).eval(context, siteType);
                }
                if (numRows == 4) {
                    return this.makeMancalaFourRows(storeType, numColumns).eval(context, siteType);
                }
                return new Graph(new Float[0][0], new Integer[0][0]);
            }
            
            @Override
            public long gameFlags(final Game game) {
                return 0L;
            }
            
            @Override
            public void preprocess(final Game game) {
            }
            
            public GraphFunction makeMancalaTwoRows(final StoreType storeType, final int numColumns) {
                final GraphFunction bottomRow = Rectangle.construct(new DimConstant(1), new DimConstant(numColumns), null);
                final GraphFunction topRow = new Shift(new FloatConstant(0.0f), new FloatConstant(1.0f), null, Rectangle.construct(new DimConstant(1), new DimConstant(numColumns), null));
                if (storeType != StoreType.None) {
                    final GraphFunction leftStore = new Graph(new Float[][] { { -0.85f, 0.5f } }, null);
                    final GraphFunction rightStore = new Shift(new FloatConstant(-0.15f), new FloatConstant(0.0f), null, new Graph(new Float[][] { {(float) numColumns, 0.5f } }, null));
                    return new Union(new GraphFunction[] { leftStore, bottomRow, topRow, rightStore }, Boolean.TRUE);
                }
                return new Union(new GraphFunction[] { bottomRow, topRow }, Boolean.TRUE);
            }
            
            public GraphFunction makeMancalaThreeRows(final StoreType storeType, final int numColumns) {
                final GraphFunction bottomRow = Rectangle.construct(new DimConstant(1), new DimConstant(numColumns), null);
                final GraphFunction middleRow = new Shift(new FloatConstant(0.0f), new FloatConstant(1.0f), null, Rectangle.construct(new DimConstant(1), new DimConstant(numColumns), null));
                final GraphFunction topRow = new Shift(new FloatConstant(0.0f), new FloatConstant(2.0f), null, Rectangle.construct(new DimConstant(1), new DimConstant(numColumns), null));
                if (storeType != StoreType.None) {
                    final GraphFunction leftStore = new Graph(new Float[][] { { -1.0f, 1.0f } }, null);
                    final GraphFunction rightStore = new Shift(new FloatConstant(0.0f), new FloatConstant(0.0f), null, new Graph(new Float[][] { {(float) numColumns, 1.0f } }, null));
                    return new Union(new GraphFunction[] { leftStore, bottomRow, middleRow, topRow, rightStore }, Boolean.TRUE);
                }
                return new Union(new GraphFunction[] { bottomRow, middleRow, topRow }, Boolean.TRUE);
            }
            
            public GraphFunction makeMancalaFourRows(final StoreType storeType, final int numColumns) {
                final GraphFunction bottomOuterRow = Rectangle.construct(new DimConstant(1), new DimConstant(numColumns), null);
                final GraphFunction bottomInnerRow = new Shift(new FloatConstant(0.0f), new FloatConstant(1.0f), null, Rectangle.construct(new DimConstant(1), new DimConstant(numColumns), null));
                final GraphFunction topInnerRow = new Shift(new FloatConstant(0.0f), new FloatConstant(2.0f), null, Rectangle.construct(new DimConstant(1), new DimConstant(numColumns), null));
                final GraphFunction topOuterRow = new Shift(new FloatConstant(0.0f), new FloatConstant(3.0f), null, Rectangle.construct(new DimConstant(1), new DimConstant(numColumns), null));
                if (storeType != StoreType.None) {
                    final GraphFunction leftStore = new Graph(new Float[][] { { -0.9f, 1.5f } }, null);
                    final GraphFunction rightStore = new Shift(new FloatConstant(-0.1f), new FloatConstant(0.0f), null, new Graph(new Float[][] { {(float) numColumns, 1.5f } }, null));
                    return new Union(new GraphFunction[] { leftStore, bottomOuterRow, bottomInnerRow, topInnerRow, topOuterRow, rightStore }, Boolean.TRUE);
                }
                return new Union(new GraphFunction[] { bottomOuterRow, bottomInnerRow, topInnerRow, topOuterRow }, Boolean.TRUE);
            }
        }, track, tracks, null, null, SiteType.Vertex);
        this.numRows = rows;
        this.numColumns = columns;
        this.storeType = ((store == null) ? StoreType.Outer : store);
        this.numStore = ((numStores == null) ? 2 : numStores);
        if (this.numRows > 4 || this.numRows < 2) {
            throw new IllegalArgumentException("Board: Only 2 to 4 rows are supported for the Mancala board.");
        }
        int numNonNull = 0;
        if (track != null) {
            ++numNonNull;
        }
        if (tracks != null) {
            ++numNonNull;
        }
        if (numNonNull > 1) {
            throw new IllegalArgumentException("Board: Only one of `track' or `tracks' can be non-null.");
        }
    }
    
    public int numRows() {
        return this.numRows;
    }
    
    public int numColumns() {
        return this.numColumns;
    }
    
    public StoreType storeType() {
        return this.storeType;
    }
    
    public int numStore() {
        return this.numStore;
    }
    
    public GraphFunction createTwoRowMancala() {
        final GraphFunction leftStore = new Graph(new Float[][] { { 0.85f, 0.5f } }, null);
        final GraphFunction rightStore = new Shift(new FloatConstant(-0.15f), new FloatConstant(0.0f), null, new Graph(new Float[][] { {(float) this.numColumns, 0.5f } }, null));
        final GraphFunction bottomRow = Rectangle.construct(new DimConstant(1), new DimConstant(this.numColumns), null);
        final GraphFunction topRow = new Shift(new FloatConstant(0.0f), new FloatConstant(1.0f), null, Rectangle.construct(new DimConstant(1), new DimConstant(this.numColumns), null));
        return new Union(new GraphFunction[] { leftStore, bottomRow, topRow, rightStore }, Boolean.TRUE);
    }
}
