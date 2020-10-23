// 
// Decompiled by Procyon v0.5.36
// 

package view.container.aspects.axes.board;

import topology.AxisLabel;
import view.container.aspects.axes.BoardAxis;
import view.container.aspects.placement.BoardPlacement;
import view.container.styles.BoardStyle;

import java.util.ArrayList;
import java.util.List;

public class SurakartaAxis extends BoardAxis
{
    public SurakartaAxis(final BoardStyle boardStyle, final BoardPlacement boardPlacement) {
        super(boardStyle, boardPlacement);
    }
    
    @Override
    protected List<AxisLabel> getAxisLabels() {
        final List<AxisLabel> axisLabels = new ArrayList<>();
        return axisLabels;
    }
}
