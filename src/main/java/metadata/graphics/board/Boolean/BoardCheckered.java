// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.board.Boolean;

import annotations.Hide;
import annotations.Opt;
import metadata.graphics.GraphicsItem;

@Hide
public class BoardCheckered implements GraphicsItem
{
    private final boolean checkeredBoard;
    
    public BoardCheckered(@Opt final Boolean checkeredBoard) {
        this.checkeredBoard = (checkeredBoard == null || checkeredBoard);
    }
    
    public boolean checkeredBoard() {
        return this.checkeredBoard;
    }
}
