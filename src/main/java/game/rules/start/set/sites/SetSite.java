// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.start.set.sites;

import annotations.Hide;
import annotations.Name;
import annotations.Opt;
import game.Game;
import game.equipment.component.Component;
import game.functions.ints.IntFunction;
import game.functions.ints.board.Id;
import game.functions.region.RegionFunction;
import game.rules.start.Start;
import game.rules.start.StartRule;
import game.types.board.SiteType;
import game.types.play.RoleType;
import topology.SiteFinder;
import topology.TopologyElement;
import util.Context;

@Hide
public final class SetSite extends StartRule
{
    private static final long serialVersionUID = 1L;
    private final RoleType role;
    private final IntFunction siteId;
    private final String coord;
    private final RoleType[] invisibleTo;
    private boolean[] invisible;
    private final RoleType[] maskedTo;
    private boolean[] masked;
    private SiteType type;
    private final IntFunction[] locationIds;
    private final RegionFunction region;
    private final String[] coords;
    
    public SetSite(final RoleType role, @Opt final SiteType type, @Opt final IntFunction loc, @Opt @Name final String coord, @Opt @Name final RoleType[] invisibleTo, @Opt @Name final RoleType[] maskedTo) {
        this.siteId = (loc);
        this.coord = (coord);
        this.invisibleTo = invisibleTo;
        this.maskedTo = maskedTo;
        this.locationIds = null;
        this.region = null;
        this.coords = null;
        this.type = type;
        this.role = role;
    }
    
    public SetSite(final RoleType role, @Opt final SiteType type, @Opt final IntFunction[] locs, @Opt final RegionFunction region, @Opt final String[] coords, @Opt @Name final RoleType[] invisibleTo) {
        this.locationIds = locs;
        this.region = (region);
        this.coords = coords;
        this.invisibleTo = invisibleTo;
        this.coord = null;
        this.siteId = null;
        this.maskedTo = null;
        this.type = type;
        this.role = role;
    }
    
    @Override
    public void eval(final Context context) {
        int what = new Id(null, this.role).eval(context);
        if (this.role == RoleType.Neutral) {
            for (int i = 1; i < context.components().length; ++i) {
                final Component component = context.components()[i];
                if (component.owner() == 0) {
                    what = component.index();
                    break;
                }
            }
        }
        else if (this.role == RoleType.Shared || this.role == RoleType.All) {
            for (int i = 1; i < context.components().length; ++i) {
                final Component component = context.components()[i];
                if (component.owner() == context.game().players().size()) {
                    what = component.index();
                    break;
                }
            }
        }
        else {
            boolean find = false;
            for (int j = 1; j < context.components().length; ++j) {
                final Component component2 = context.components()[j];
                if (component2.index() == what) {
                    find = true;
                    break;
                }
            }
            if (!find) {
                what = -1;
            }
        }
        if (what < 1 || what >= context.components().length) {
            System.err.println("Warning: A piece which not exist is trying to be set in the starting rule.");
            return;
        }
        this.conversionOfInvisibleTo(context);
        this.conversionOfMaskedTo(context);
        if (this.locationIds != null || this.region != null || this.coords != null) {
            this.evalFill(context, what);
        }
        else {
            if (this.siteId == null && this.coord == null) {
                return;
            }
            int site = -1;
            if (this.coord != null) {
                final TopologyElement element = SiteFinder.find(context.board(), this.coord, this.type);
                if (element == null) {
                    throw new RuntimeException("In the starting rules (place) the coordinate " + this.coord + " not found.");
                }
                site = element.index();
            }
            else if (this.siteId != null) {
                site = this.siteId.eval(context);
            }
            Start.placePieces(context, site, what, 1, -1, -1, false, this.invisible, this.masked, this.type);
        }
    }
    
    private void evalFill(final Context context, final int what) {
        if (this.coords != null) {
            for (final String coordinate : this.coords) {
                final TopologyElement element = SiteFinder.find(context.board(), coordinate, this.type);
                if (element == null) {
                    System.out.println("** SetSite.evalFill(): Coord " + coordinate + " not found.");
                }
                else {
                    Start.placePieces(context, element.index(), what, 1, -1, -1, false, this.invisible, null, this.type);
                }
            }
        }
        else if (this.region != null) {
            final int[] sites;
            final int[] locs = sites = this.region.eval(context).sites();
            for (final int loc : sites) {
                Start.placePieces(context, loc, what, 1, -1, -1, false, this.invisible, null, this.type);
            }
        }
        else if (this.locationIds != null) {
            for (final IntFunction loc2 : this.locationIds) {
                Start.placePieces(context, loc2.eval(context), what, 1, -1, -1, false, this.invisible, null, this.type);
            }
        }
    }
    
    public boolean[] conversionOfInvisibleTo(final Context context) {
        if (this.invisibleTo != null) {
            final int numPlayer = context.game().players().count();
            this.invisible = new boolean[numPlayer];
            for (int pid = 1; pid <= numPlayer; ++pid) {
                boolean isInvisible = false;
                for (final RoleType roleInvisible : this.invisibleTo) {
                    if (roleInvisible.owner() == pid) {
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
                for (final RoleType roleMasked : this.maskedTo) {
                    if (roleMasked.owner() == pid) {
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
    
    @Override
    public int count() {
        return 1;
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
        long flags = 0L;
        flags |= SiteType.stateFlags(this.type);
        if (this.siteId != null) {
            flags = this.siteId.gameFlags(game);
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
        final String str = "(set)";
        return "(set)";
    }
}
