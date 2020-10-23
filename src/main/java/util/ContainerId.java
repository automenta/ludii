// 
// Decompiled by Procyon v0.5.36
// 

package util;

import game.equipment.container.Container;
import game.functions.ints.IntConstant;
import game.functions.ints.IntFunction;
import game.functions.ints.board.Id;
import game.types.play.RoleType;

import java.io.Serializable;

public final class ContainerId implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected final IntFunction index;
    protected final String name;
    protected final RoleType role;
    protected final IntFunction playerId;
    protected final IntFunction site;
    
    public ContainerId(final IntFunction index, final String name, final RoleType role, final IntFunction playerId, final IntFunction site) {
        if (index == null && name == null && role == null && playerId == null && site == null) {
            this.index = new IntConstant(0);
            this.name = null;
            this.role = null;
            this.playerId = null;
            this.site = null;
        }
        else if (index != null && name == null && role == null && playerId == null && site == null) {
            this.index = index;
            this.name = null;
            this.role = null;
            this.playerId = null;
            this.site = null;
        }
        else if (index == null && name != null && role == null && playerId == null && site == null) {
            this.index = null;
            this.name = name;
            this.role = null;
            this.playerId = null;
            this.site = null;
        }
        else if (index == null && name != null && role != null && playerId == null && site == null) {
            this.index = null;
            this.name = name;
            this.role = role;
            this.playerId = null;
            this.site = null;
        }
        else if (index == null && name != null && role == null && playerId != null && site == null) {
            this.index = null;
            this.name = name;
            this.role = null;
            this.playerId = playerId;
            this.site = null;
        }
        else {
            if (index != null || name != null || role != null || playerId != null || site == null) {
                throw new IllegalArgumentException("Unexpected parameter combination.");
            }
            this.index = null;
            this.name = null;
            this.role = null;
            this.playerId = null;
            this.site = site;
        }
    }
    
    public int eval(final Context context) {
        if (this.index != null) {
            return this.index.eval(context);
        }
        if (this.site != null) {
            final int indexSite = this.site.eval(context);
            if (indexSite == -1) {
                return 0;
            }
            return context.containerId()[indexSite];
        }
        else {
            if (this.role == null && this.playerId == null) {
                return context.game().mapContainer().get(this.name).index();
            }
            final int pid = (this.role != null) ? new Id(null, this.role).eval(context) : this.playerId.eval(context);
            for (int cid = 0; cid < context.containers().length; ++cid) {
                final Container container = context.containers()[cid];
                if (container.isHand() && container.name().contains(this.name) && container.owner() == pid) {
                    return cid;
                }
            }
            throw new RuntimeException("Could not find specified container.");
        }
    }
}
