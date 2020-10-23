// 
// Decompiled by Procyon v0.5.36
// 

package game.types.board;

import game.Game;

public enum SiteType
{
    Vertex, 
    Edge, 
    Cell;
    
    public static SiteType use(final SiteType preferred, final Game game) {
        if (preferred != null) {
            return preferred;
        }
        return game.board().defaultSite();
    }
    
    public static long stateFlags(final SiteType type) {
        long stateFlag = 0L;
        if (type != null) {
            switch (type) {
                case Vertex: {
                    stateFlag |= 0x1800000L;
                    break;
                }
                case Edge: {
                    stateFlag |= 0x4800000L;
                    break;
                }
                case Cell: {
                    stateFlag |= 0x2000000L;
                    break;
                }
            }
        }
        return stateFlag;
    }
}
