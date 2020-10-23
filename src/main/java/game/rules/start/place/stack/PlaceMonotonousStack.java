// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.start.place.stack;

import annotations.Hide;
import annotations.Name;
import annotations.Opt;
import game.Game;
import game.equipment.component.Component;
import game.equipment.container.Container;
import game.functions.ints.IntFunction;
import game.functions.region.RegionFunction;
import game.rules.start.Start;
import game.rules.start.StartRule;
import game.types.board.SiteType;
import game.types.play.RoleType;
import util.Context;

@Hide
public final class PlaceMonotonousStack extends StartRule
{
    private static final long serialVersionUID = 1L;
    protected final String[] items;
    protected final String container;
    protected final int count;
    protected final int state;
    protected final int rotation;
    protected final RoleType[] invisibleTo;
    protected boolean[] invisible;
    protected final RoleType[] maskedTo;
    protected boolean[] masked;
    private SiteType type;
    protected final IntFunction[] locationIds;
    protected final RegionFunction region;
    protected final String[] coords;
    protected final int[] counts;
    
    public PlaceMonotonousStack(final String item, @Opt final SiteType type, @Opt final IntFunction[] locs, @Opt final RegionFunction region, @Opt final String[] coords, @Opt @Name final Integer count, @Opt @Name final Integer[] counts, @Opt @Name final Integer state, @Opt @Name final Integer rotation, @Opt @Name final RoleType[] invisibleTo) {
        this.items = new String[] { item };
        this.container = null;
        this.locationIds = locs;
        this.region = (region);
        this.coords = coords;
        this.count = ((counts == null) ? ((count != null) ? count : 1) : counts[0]);
        if (counts == null) {
            this.counts = new int[0];
        }
        else {
            this.counts = new int[counts.length];
            for (int i = 0; i < counts.length; ++i) {
                this.counts[i] = counts[i];
            }
        }
        this.state = ((state == null) ? -1 : state);
        this.rotation = ((rotation == null) ? -1 : rotation);
        this.invisibleTo = invisibleTo;
        this.maskedTo = null;
        this.type = type;
    }
    
    @Override
    public void eval(final Context context) {
        this.conversionOfInvisibleTo(context);
        this.conversionOfMaskedTo(context);
        final String item = this.items[0];
        final Component component = context.game().getComponent(item);
        if (component == null) {
            throw new RuntimeException("In the starting rules (place) the component " + item + " is not defined.");
        }
        final int what = component.index();
        if (this.container != null) {
            final Container c = context.game().mapContainer().get(this.container);
            int pos;
            for (int siteFrom = pos = context.game().equipment().sitesFrom()[c.index()]; pos < siteFrom + c.numSites(); ++pos) {
                Start.placePieces(context, pos, what, this.count, this.state, this.rotation, true, this.invisible, null, this.type);
            }
        }
        else {
            final int[] locs = this.region.eval(context).sites();
            if (this.counts.length != 0 && locs.length != this.counts.length) {
                throw new RuntimeException("In the starting rules (place) the region size is greater than the size of the array counts.");
            }
            for (int k = 0; k < locs.length; ++k) {
                final int loc = locs[k];
                for (int i = 0; i < ((this.counts.length == 0) ? this.count : this.counts[k]); ++i) {
                    Start.placePieces(context, loc, what, this.count, this.state, this.rotation, true, this.invisible, null, this.type);
                }
            }
        }
    }
    
    public boolean[] conversionOfInvisibleTo(final Context context) {
        if (this.invisibleTo != null) {
            final int numPlayer = context.game().players().count();
            this.invisible = new boolean[numPlayer];
            for (int pid = 1; pid <= numPlayer; ++pid) {
                boolean isInvisible = false;
                for (int i = 0; i < this.invisibleTo.length; ++i) {
                    final RoleType role = this.invisibleTo[i];
                    if (role.owner() == pid) {
                        isInvisible = true;
                        this.invisible[pid - 1] = isInvisible;
                        break;
                    }
                }
                if (!isInvisible) {
                    this.invisible[pid - 1] = isInvisible;
                }
            }
        }
        return null;
    }
    
    public boolean[] conversionOfMaskedTo(final Context context) {
        if (this.maskedTo != null) {
            final int numPlayer = context.game().players().count();
            this.masked = new boolean[numPlayer];
            for (int pid = 1; pid <= numPlayer; ++pid) {
                boolean isMasked = false;
                for (int i = 0; i < this.maskedTo.length; ++i) {
                    final RoleType role = this.maskedTo[i];
                    if (role.owner() == pid) {
                        isMasked = true;
                        this.masked[pid - 1] = isMasked;
                        break;
                    }
                }
                if (!isMasked) {
                    this.masked[pid - 1] = isMasked;
                }
            }
        }
        return null;
    }
    
    public String container() {
        return this.container;
    }
    
    @Override
    public int count() {
        return this.count;
    }
    
    @Override
    public int howManyPlace(final Game game) {
        if (this.region != null) {
            return this.region.eval(new Context(game, null)).sites().length;
        }
        if (this.locationIds != null) {
            return this.locationIds.length;
        }
        return 1;
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long flags = 16L;
        flags |= SiteType.stateFlags(this.type);
        if (this.state != -1) {
            flags |= 0x2L;
        }
        if (this.rotation != -1) {
            flags |= 0x20000L;
        }
        if (this.count > 1) {
            flags |= 0x4L;
        }
        if (this.invisible != null || this.masked != null) {
            flags |= 0x8L;
        }
        if (this.region != null) {
            flags |= this.region.gameFlags(game);
        }
        return flags;
    }
    
    @Override
    public void preprocess(final Game game) {
        for (final String it : this.items) {
            boolean found = false;
            for (int i = 1; i < game.equipment().components().length; ++i) {
                final String nameComponent = game.equipment().components()[i].name();
                if (nameComponent.contains(it)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new RuntimeException("Place: The component " + it + " is expected but the corresponding component is not defined in the equipment.");
            }
        }
        if (this.container != null) {
            boolean found2 = false;
            for (int j = 1; j < game.equipment().containers().length; ++j) {
                final String nameContainer = game.equipment().containers()[j].name();
                if (nameContainer.contains(this.container)) {
                    found2 = true;
                    break;
                }
            }
            if (!found2) {
                throw new RuntimeException("Place: The container " + this.container + " is expected but the corresponding container is not defined in the equipment.");
            }
        }
        this.type = SiteType.use(this.type, game);
        if (this.locationIds != null) {
            for (final IntFunction locationId : this.locationIds) {
                locationId.preprocess(game);
            }
        }
        if (this.region != null) {
            this.region.preprocess(game);
        }
    }
    
    @Override
    public String toString() {
        return "";
    }
}
