// 
// Decompiled by Procyon v0.5.36
// 

package view.container.aspects.designs.board;

import util.Context;
import view.container.aspects.designs.BoardDesign;
import view.container.aspects.placement.Board.BoardlessPlacement;
import view.container.styles.board.BoardlessStyle;

public class BoardlessDesign extends BoardDesign
{
    BoardlessStyle boardlessStyle;
    BoardlessPlacement boardlessPlacement;
    
    public BoardlessDesign(final BoardlessStyle boardlessStyle, final BoardlessPlacement boardlessPlacement) {
        super(boardlessStyle, boardlessPlacement);
        this.boardlessStyle = boardlessStyle;
        this.boardlessPlacement = boardlessPlacement;
    }
    
    @Override
    public String createSVGImage(final Context context) {
        this.boardlessPlacement.updateZoomImage(context);
        this.setStrokesAndColours(context, null, null, null, null, null, null, null, 1.0f, 1.0f);
        return "";
    }
}
