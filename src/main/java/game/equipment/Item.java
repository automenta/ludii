// 
// Decompiled by Procyon v0.5.36
// 

package game.equipment;

import game.Game;
import game.types.play.RoleType;
import util.BaseLudeme;
import util.ItemType;

public abstract class Item extends BaseLudeme
{
    private RoleType owner;
    private ItemType type;
    private int index;
    private String name;
    private int ownerID;
    
    public Item(final String name, final int index, final RoleType owner) {
        this.index = -1;
        this.ownerID = -1;
        this.name = name;
        this.index = index;
        this.owner = owner;
    }
    
    protected Item(final Item other) {
        this.index = -1;
        this.ownerID = -1;
        this.owner = other.owner;
        this.index = other.index;
        this.name = other.name;
        this.type = other.type;
    }
    
    public int index() {
        return this.index;
    }
    
    public void setIndex(final int id) {
        this.index = id;
    }
    
    public RoleType role() {
        return this.owner;
    }
    
    public void setRole(final RoleType role) {
        this.owner = role;
    }
    
    public void setRoleFromPlayerId(final int pid) {
        this.owner = RoleType.roleForPlayerId(pid);
    }
    
    public int owner() {
        return this.ownerID;
    }
    
    public void create(final Game game) {
        if (this.owner == RoleType.Shared || this.owner == RoleType.All) {
            this.ownerID = game.players().count() + 1;
        }
        else {
            this.ownerID = this.owner.owner();
        }
    }
    
    public String name() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public ItemType type() {
        return this.type;
    }
    
    public void setType(final ItemType type) {
        this.type = type;
    }
    
    public long gameFlags(final Game game) {
        return 0L;
    }
    
    public String credit() {
        return null;
    }
}
