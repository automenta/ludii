// 
// Decompiled by Procyon v0.5.36
// 

package view.container.styles;

import game.equipment.container.Container;
import view.container.BaseContainerStyle;
import view.container.aspects.placement.HandPlacement;

public class HandStyle extends BaseContainerStyle
{
    public HandStyle(final Container container) {
        super(container);
        this.containerPlacement = new HandPlacement(this);
    }
}
