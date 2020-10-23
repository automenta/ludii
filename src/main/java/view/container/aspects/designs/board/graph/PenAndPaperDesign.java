// 
// Decompiled by Procyon v0.5.36
// 

package view.container.aspects.designs.board.graph;

import util.SettingsVC;
import view.container.aspects.placement.BoardPlacement;
import view.container.styles.BoardStyle;

public class PenAndPaperDesign extends GraphDesign
{
    public PenAndPaperDesign(final BoardStyle boardStyle, final BoardPlacement boardPlacement) {
        super(boardStyle, boardPlacement, false, false);
        SettingsVC.noAnimation = true;
    }
}
