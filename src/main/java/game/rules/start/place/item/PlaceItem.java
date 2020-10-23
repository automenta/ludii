// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.start.place.item;

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
import topology.SiteFinder;
import topology.TopologyElement;
import util.Context;
import util.Move;
import util.action.BaseAction;
import util.action.puzzle.ActionSet;
import util.state.containerState.ContainerState;

@Hide
public final class PlaceItem extends StartRule
{
    private static final long serialVersionUID = 1L;
    protected final String item;
    protected final String container;
    protected final IntFunction siteId;
    protected final String coord;
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
    
    public PlaceItem(final String item, @Opt final String container, @Opt final SiteType type, @Opt final IntFunction loc, @Opt @Name final String coord, @Opt @Name final Integer count, @Opt @Name final Integer state, @Opt @Name final Integer rotation, @Opt @Name final RoleType[] invisibleTo, @Opt @Name final RoleType[] maskedTo) {
        this.item = (item);
        this.container = (container);
        this.siteId = (loc);
        this.coord = (coord);
        this.count = ((count == null) ? 1 : count);
        this.state = ((state == null) ? -1 : state);
        this.rotation = ((rotation == null) ? -1 : rotation);
        this.invisibleTo = invisibleTo;
        this.maskedTo = maskedTo;
        this.locationIds = null;
        this.region = null;
        this.coords = null;
        this.counts = null;
        this.type = type;
    }
    
    public PlaceItem(final String item, @Opt final SiteType type, @Opt final IntFunction[] locs, @Opt final RegionFunction region, @Opt final String[] coords, @Opt @Name final Integer[] counts, @Opt @Name final Integer state, @Opt @Name final Integer rotation, @Opt @Name final RoleType[] invisibleTo) {
        this.item = (item);
        this.container = null;
        this.locationIds = locs;
        this.region = (region);
        this.coords = coords;
        this.count = ((counts == null) ? 1 : counts[0]);
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
        this.coord = null;
        this.siteId = null;
        this.maskedTo = null;
        this.type = type;
    }
    
    @Override
    public void eval(final Context context) {
        this.conversionOfInvisibleTo(context);
        this.conversionOfMaskedTo(context);
        if (this.locationIds != null || this.region != null || this.coords != null || this.counts != null) {
            this.evalFill(context);
        }
        else if (context.game().isDeductionPuzzle()) {
            this.evalPuzzle(context);
        }
        else {
            final Component testComponent = context.game().getComponent(this.item);
            if (stringWitoutNumber(this.item) && this.container != null && this.container.equals("Hand") && (testComponent == null || testComponent.role() != RoleType.Shared)) {
                for (int pid = 1; pid <= context.game().players().count(); ++pid) {
                    final String itemPlayer = this.item + pid;
                    final String handPlayer = this.container + pid;
                    final Component component = context.game().getComponent(itemPlayer);
                    if (component == null) {
                        throw new RuntimeException("In the starting rules (place) the component " + itemPlayer + " is not defined (A).");
                    }
                    final Container c = context.game().mapContainer().get(handPlayer);
                    ContainerState cs;
                    int site;
                    for (cs = context.containerState(c.index()), site = context.game().equipment().sitesFrom()[c.index()]; !cs.isEmpty(site, this.type); ++site) {}
                    Start.placePieces(context, site, component.index(), this.count, this.state, this.rotation, false, this.invisible, null, this.type);
                }
                return;
            }
            final Component component2 = context.game().getComponent(this.item);
            if (component2 == null) {
                throw new RuntimeException("In the starting rules (place) the component " + this.item + " is not defined (B).");
            }
            final int what = component2.index();
            if (this.container != null) {
                final Container c2 = context.game().mapContainer().get(this.container);
                final int siteFrom = context.game().equipment().sitesFrom()[c2.index()];
                final Component comp = context.game().equipment().components()[what];
                if (comp.isDie()) {
                    for (int pos = siteFrom; pos < siteFrom + c2.numSites(); ++pos) {
                        if (context.state().containerStates()[c2.index()].what(pos, this.type) == 0) {
                            Start.placePieces(context, pos, what, this.count, this.state, this.rotation, false, this.invisible, this.masked, this.type);
                            break;
                        }
                    }
                }
                else {
                    if (this.container.contains("Hand")) {
                        Start.placePieces(context, siteFrom, c2.index(), this.count, this.state, this.rotation, false, this.invisible, null, this.type);
                        return;
                    }
                    Start.placePieces(context, this.siteId.eval(context) + siteFrom, what, this.count, this.state, this.rotation, false, this.invisible, this.masked, this.type);
                }
            }
            else {
                if (this.siteId == null && this.coord == null) {
                    return;
                }
                int site2 = -1;
                if (this.coord != null) {
                    final TopologyElement element = SiteFinder.find(context.board(), this.coord, this.type);
                    if (element == null) {
                        throw new RuntimeException("In the starting rules (place) the coordinate " + this.coord + " not found.");
                    }
                    site2 = element.index();
                }
                else if (this.siteId != null) {
                    site2 = this.siteId.eval(context);
                }
                Start.placePieces(context, site2, what, this.count, this.state, this.rotation, false, this.invisible, this.masked, this.type);
            }
        }
    }
    
    private void evalFill(final Context context) {
        final Component component = context.game().getComponent(this.item);
        if (component == null) {
            throw new RuntimeException("In the starting rules (place) the component " + this.item + " is not defined (C).");
        }
        final int what = component.index();
        if (this.container != null) {
            final Container c = context.game().mapContainer().get(this.container);
            final int siteFrom = context.game().equipment().sitesFrom()[c.index()];
            if (this.region != null) {
                final int[] sites;
                final int[] locs = sites = this.region.eval(context).sites();
                for (final int loc : sites) {
                    Start.placePieces(context, loc + siteFrom, what, this.count, this.state, this.rotation, false, this.invisible, null, this.type);
                }
            }
            else if (this.locationIds != null) {
                for (final IntFunction loc2 : this.locationIds) {
                    Start.placePieces(context, loc2.eval(context) + siteFrom, what, this.count, this.state, this.rotation, false, this.invisible, null, this.type);
                }
            }
            else {
                for (int pos = siteFrom; pos < siteFrom + c.numSites(); ++pos) {
                    if (context.state().containerStates()[c.index()].what(pos, this.type) == 0) {
                        Start.placePieces(context, pos, what, this.count, this.state, this.rotation, false, this.invisible, null, this.type);
                        break;
                    }
                }
            }
        }
        else if (this.coords != null) {
            for (final String coordinate : this.coords) {
                final TopologyElement element = SiteFinder.find(context.board(), coordinate, this.type);
                if (element == null) {
                    System.out.println("** Coord " + coordinate + " not found.");
                }
                else {
                    Start.placePieces(context, element.index(), what, this.count, this.state, this.rotation, false, this.invisible, null, this.type);
                }
            }
        }
        else if (this.region != null) {
            final int[] sites2;
            final int[] locs2 = sites2 = this.region.eval(context).sites();
            for (final int loc3 : sites2) {
                Start.placePieces(context, loc3, what, this.count, this.state, this.rotation, false, this.invisible, null, this.type);
            }
        }
        else if (this.locationIds != null) {
            for (final IntFunction loc4 : this.locationIds) {
                Start.placePieces(context, loc4.eval(context), what, this.count, this.state, this.rotation, false, this.invisible, null, this.type);
            }
        }
    }
    
    private static boolean stringWitoutNumber(final String str) {
        for (int i = 0; i < str.length(); ++i) {
            if (str.charAt(i) >= '0' && str.charAt(i) <= '9') {
                return false;
            }
        }
        return true;
    }
    
    private void evalPuzzle(final Context context) {
        final Component component = context.game().getComponent(this.item);
        if (component == null) {
            throw new RuntimeException("In the starting rules (place) the component " + this.item + " is not defined.");
        }
        final int what = component.index();
        final BaseAction actionAtomic = new ActionSet(SiteType.Cell, this.siteId.eval(context), what);
        actionAtomic.apply(context, true);
        context.trial().moves().add(new Move(actionAtomic));
        context.trial().addInitPlacement();
    }
    
    public boolean[] conversionOfInvisibleTo(final Context context) {
        if (this.invisibleTo != null) {
            final int numPlayer = context.game().players().count();
            this.invisible = new boolean[numPlayer];
            for (int pid = 1; pid <= numPlayer; ++pid) {
                boolean isInvisible = false;
                for (final RoleType role : this.invisibleTo) {
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
                for (final RoleType role : this.maskedTo) {
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
    
    public String item() {
        return this.item;
    }
    
    public IntFunction posn() {
        return this.siteId;
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
            this.region.preprocess(game);
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
        long flags = 0L;
        flags |= SiteType.stateFlags(this.type);
        if (this.siteId != null) {
            flags = this.siteId.gameFlags(game);
        }
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
        if (this.item != null) {
            boolean found = false;
            for (int i = 1; i < game.equipment().components().length; ++i) {
                final String nameComponent = game.equipment().components()[i].name();
                if (nameComponent.contains(this.item)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new RuntimeException("Place: The component " + this.item + " is expected but the corresponding component is not defined in the equipment.");
            }
        }
        if (this.container != null) {
            boolean found = false;
            for (int i = 1; i < game.equipment().containers().length; ++i) {
                final String nameContainer = game.equipment().containers()[i].name();
                if (nameContainer.contains(this.container)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new RuntimeException("Place: The container " + this.container + " is expected but the corresponding container is not defined in the equipment.");
            }
        }
        this.type = SiteType.use(this.type, game);
        if (this.siteId != null) {
            this.siteId.preprocess(game);
        }
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
        String str = "(place " + this.item;
        if (this.container != null) {
            str = str + " on cont: " + this.container;
        }
        if (this.siteId != null) {
            str = str + " at: " + this.siteId;
        }
        str = str + " count: " + this.count;
        str = str + " state: " + this.state;
        if (this.invisibleTo != null) {
            for (RoleType roleType : this.invisibleTo) {
                str = str + " invisible for " + roleType;
            }
        }
        str += ")";
        return str;
    }
}
