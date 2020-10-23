// 
// Decompiled by Procyon v0.5.36
// 

package util.state.owned;

import game.Game;

public final class OwnedFactory
{
    private OwnedFactory() {
    }
    
    public static Owned createOwned(final Game game) {
        final long gameFlags = game.gameFlags();
        if ((gameFlags & 0x2000000L) != 0x0L && (gameFlags & 0x4000000L) == 0x0L && (gameFlags & 0x1000000L) == 0x0L) {
            if (game.isStacking()) {
                return new CellOnlyOwned(game);
            }
            return new FlatCellOnlyOwned(game);
        }
        else {
            if ((gameFlags & 0x2000000L) == 0x0L && (gameFlags & 0x4000000L) == 0x0L && (gameFlags & 0x1000000L) != 0x0L && !game.isStacking()) {
                return new FlatVertexOnlyOwned(game);
            }
            return new FullOwned(game);
        }
    }
}
