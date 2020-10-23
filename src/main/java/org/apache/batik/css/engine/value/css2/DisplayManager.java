// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value.css2;

import org.apache.batik.css.engine.value.ValueConstants;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.StringMap;
import org.apache.batik.css.engine.value.IdentifierManager;

public class DisplayManager extends IdentifierManager
{
    protected static final StringMap values;
    
    @Override
    public boolean isInheritedProperty() {
        return false;
    }
    
    @Override
    public boolean isAnimatableProperty() {
        return true;
    }
    
    @Override
    public boolean isAdditiveProperty() {
        return false;
    }
    
    @Override
    public int getPropertyType() {
        return 15;
    }
    
    @Override
    public String getPropertyName() {
        return "display";
    }
    
    @Override
    public Value getDefaultValue() {
        return ValueConstants.INLINE_VALUE;
    }
    
    @Override
    public StringMap getIdentifiers() {
        return DisplayManager.values;
    }
    
    static {
        (values = new StringMap()).put("block", ValueConstants.BLOCK_VALUE);
        DisplayManager.values.put("compact", ValueConstants.COMPACT_VALUE);
        DisplayManager.values.put("inline", ValueConstants.INLINE_VALUE);
        DisplayManager.values.put("inline-table", ValueConstants.INLINE_TABLE_VALUE);
        DisplayManager.values.put("list-item", ValueConstants.LIST_ITEM_VALUE);
        DisplayManager.values.put("marker", ValueConstants.MARKER_VALUE);
        DisplayManager.values.put("none", ValueConstants.NONE_VALUE);
        DisplayManager.values.put("run-in", ValueConstants.RUN_IN_VALUE);
        DisplayManager.values.put("table", ValueConstants.TABLE_VALUE);
        DisplayManager.values.put("table-caption", ValueConstants.TABLE_CAPTION_VALUE);
        DisplayManager.values.put("table-cell", ValueConstants.TABLE_CELL_VALUE);
        DisplayManager.values.put("table-column", ValueConstants.TABLE_COLUMN_VALUE);
        DisplayManager.values.put("table-column-group", ValueConstants.TABLE_COLUMN_GROUP_VALUE);
        DisplayManager.values.put("table-footer-group", ValueConstants.TABLE_FOOTER_GROUP_VALUE);
        DisplayManager.values.put("table-header-group", ValueConstants.TABLE_HEADER_GROUP_VALUE);
        DisplayManager.values.put("table-row", ValueConstants.TABLE_ROW_VALUE);
        DisplayManager.values.put("table-row-group", ValueConstants.TABLE_ROW_GROUP_VALUE);
    }
}
