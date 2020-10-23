// 
// Decompiled by Procyon v0.5.36
// 

package game.equipment.other;

import annotations.Opt;
import game.equipment.Item;
import game.types.board.SiteType;
import game.types.play.RoleType;
import game.util.equipment.Hint;
import util.ItemType;

public class Hints extends Item
{
    private final Integer[][] where;
    private final Integer[] values;
    private final SiteType type;
    
    public Hints(@Opt final String label, final Hint[] records, @Opt final SiteType type) {
        super(label, -1, RoleType.Neutral);
        if (records == null) {
            this.values = null;
            this.where = null;
        }
        else {
            this.values = new Integer[records.length];
            this.where = new Integer[records.length][];
            for (int n = 0; n < records.length; ++n) {
                this.where[n] = records[n].region();
                this.values[n] = records[n].hint();
            }
        }
        this.type = ((type == null) ? SiteType.Cell : type);
        this.setType(ItemType.Hints);
    }
    
    public Integer[][] where() {
        return this.where;
    }
    
    public Integer[] values() {
        return this.values;
    }
    
    public SiteType getType() {
        return this.type;
    }
}
