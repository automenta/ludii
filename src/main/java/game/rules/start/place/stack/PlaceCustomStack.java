// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.start.place.stack;

import annotations.Hide;
import annotations.Name;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.equipment.component.Component;
import game.equipment.container.Container;
import game.functions.ints.IntFunction;
import game.rules.start.Start;
import game.rules.start.StartRule;
import game.types.board.SiteType;
import game.types.play.RoleType;
import topology.SiteFinder;
import topology.TopologyElement;
import util.Context;
import util.Move;
import util.action.die.ActionUpdateDice;

@Hide
public final class PlaceCustomStack extends StartRule
{
    private static final long serialVersionUID = 1L;
    protected final String[] items;
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
    
    public PlaceCustomStack(@Or final String item, @Or @Name final String[] items, @Opt final String container, @Opt final SiteType type, @Opt final IntFunction loc, @Opt @Name final String coord, @Opt @Name final Integer count, @Opt @Name final Integer state, @Opt @Name final Integer rotation, @Opt @Name final RoleType[] invisibleTo, @Opt @Name final RoleType[] maskedTo) {
        this.items = ((items == null) ? new String[] { item } : items);
        this.container = (container);
        this.siteId = (loc);
        this.coord = (coord);
        this.count = ((count == null) ? 1 : count);
        this.state = ((state == null) ? -1 : state);
        this.rotation = ((rotation == null) ? -1 : rotation);
        this.invisibleTo = invisibleTo;
        this.maskedTo = maskedTo;
        this.type = type;
    }
    
    @Override
    public void eval(final Context context) {
        this.conversionOfInvisibleTo(context);
        this.conversionOfMaskedTo(context);
        if (this.items.length > 1) {
            for (final String it : this.items) {
                final Component component = context.game().getComponent(it);
                if (component == null) {
                    throw new RuntimeException("In the starting rules (place) the component " + it + " is not defined.");
                }
                final int what = component.index();
                if (this.container != null) {
                    final Container c = context.game().mapContainer().get(this.container);
                    final int siteFrom = context.game().equipment().sitesFrom()[c.index()];
                    if (this.siteId != null) {
                        Start.placePieces(context, this.siteId.eval(context) + siteFrom, what, this.count, this.state, this.rotation, true, this.invisible, this.masked, this.type);
                    }
                    else {
                        for (int pos = siteFrom; pos < siteFrom + c.numSites(); ++pos) {
                            Start.placePieces(context, pos, what, this.count, this.state, this.rotation, true, this.invisible, this.masked, this.type);
                        }
                    }
                }
                else {
                    int site = -1;
                    if (this.coord != null) {
                        final TopologyElement element = SiteFinder.find(context.board(), this.coord, this.type);
                        if (element == null) {
                            throw new RuntimeException("In the starting rules (place) the Coordinates " + this.coord + " not found.");
                        }
                        site = element.index();
                    }
                    else {
                        site = this.siteId.eval(context);
                    }
                    for (int i = 0; i < this.count; ++i) {
                        Start.placePieces(context, site, what, this.count, this.state, this.rotation, true, this.invisible, this.masked, this.type);
                    }
                }
            }
        }
        else {
            final String item = this.items[0];
            final Component component2 = context.game().getComponent(item);
            if (component2 == null) {
                throw new RuntimeException("In the starting rules (place) the component " + item + " is not defined.");
            }
            final int what2 = component2.index();
            if (this.container != null) {
                final Container c2 = context.game().mapContainer().get(this.container);
                final int siteFrom2 = context.game().equipment().sitesFrom()[c2.index()];
                if (component2.isDie()) {
                    for (int pos2 = siteFrom2; pos2 < siteFrom2 + c2.numSites(); ++pos2) {
                        if (context.state().containerStates()[c2.index()].what(pos2, this.type) == 0) {
                            Start.placePieces(context, pos2, what2, this.count, this.state, this.rotation, true, this.invisible, this.masked, this.type);
                            final int newState = component2.roll(context);
                            final ActionUpdateDice actionChangeState = new ActionUpdateDice(pos2, newState);
                            actionChangeState.apply(context, true);
                            context.trial().moves().add(new Move(actionChangeState));
                            context.trial().addInitPlacement();
                            break;
                        }
                    }
                }
                else if (this.siteId != null) {
                    Start.placePieces(context, this.siteId.eval(context) + siteFrom2, what2, this.count, this.state, this.rotation, true, this.invisible, this.masked, this.type);
                }
                else {
                    for (int pos2 = siteFrom2; pos2 < siteFrom2 + c2.numSites(); ++pos2) {
                        Start.placePieces(context, pos2, what2, this.count, this.state, this.rotation, true, this.invisible, this.masked, this.type);
                    }
                }
            }
            else {
                int site2 = -1;
                if (this.coord != null) {
                    final TopologyElement element2 = SiteFinder.find(context.board(), this.coord, this.type);
                    if (element2 == null) {
                        throw new RuntimeException("In the starting rules (place) the Coordinates " + this.coord + " not found.");
                    }
                    site2 = element2.index();
                }
                else {
                    site2 = this.siteId.eval(context);
                }
                for (int j = 0; j < this.count; ++j) {
                    Start.placePieces(context, site2, what2, this.count, this.state, this.rotation, true, this.invisible, this.masked, this.type);
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
        if (this.siteId != null) {
            flags |= this.siteId.gameFlags(game);
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
        if (this.siteId != null) {
            this.siteId.preprocess(game);
        }
    }
    
    @Override
    public String toString() {
        return "";
    }
}
