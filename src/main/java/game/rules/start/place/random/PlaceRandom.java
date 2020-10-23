// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.start.place.random;

import annotations.Hide;
import annotations.Name;
import annotations.Opt;
import game.Game;
import game.equipment.component.Component;
import game.functions.ints.IntConstant;
import game.functions.ints.IntFunction;
import game.functions.region.sites.index.SitesEmpty;
import game.rules.start.Start;
import game.rules.start.StartRule;
import game.types.board.SiteType;
import game.types.play.RoleType;
import game.util.math.Count;
import gnu.trove.list.array.TIntArrayList;
import util.Context;

@Hide
public final class PlaceRandom extends StartRule
{
    private static final long serialVersionUID = 1L;
    private final IntFunction container;
    private final String[] item;
    private final Integer count;
    private final boolean stack;
    private final IntFunction where;
    private final String[] pieces;
    private final IntFunction[] counts;
    private final RoleType[] invisibleTo;
    private boolean[] invisible;
    private final RoleType[] maskedTo;
    private boolean[] masked;
    private SiteType type;
    
    public PlaceRandom(@Opt final IntFunction container, final String[] item, @Opt @Name final Integer count, @Opt @Name final RoleType[] invisibleTo, @Opt @Name final RoleType[] maskedTo, @Opt final SiteType type) {
        this.container = ((container == null) ? new IntConstant(0) : container);
        this.count = ((count == null) ? Integer.valueOf(1) : count);
        this.item = item;
        this.where = null;
        this.pieces = null;
        this.counts = null;
        this.stack = false;
        this.invisibleTo = invisibleTo;
        this.maskedTo = maskedTo;
        this.type = type;
    }
    
    public PlaceRandom(final String[] pieces, @Name @Opt final IntFunction[] count, final IntFunction where, @Opt @Name final RoleType[] invisibleTo, @Opt @Name final RoleType[] maskedTo, @Opt final SiteType type) {
        this.container = new IntConstant(0);
        this.item = null;
        this.count = null;
        this.pieces = pieces;
        this.where = where;
        this.counts = count;
        this.stack = true;
        this.invisibleTo = invisibleTo;
        this.maskedTo = maskedTo;
        this.type = type;
    }
    
    public PlaceRandom(final Count[] items, final IntFunction where, @Opt @Name final RoleType[] invisibleTo, @Opt @Name final RoleType[] maskedTo, @Opt final SiteType type) {
        this.container = new IntConstant(0);
        this.item = null;
        this.count = null;
        this.where = where;
        this.stack = true;
        this.invisibleTo = invisibleTo;
        this.maskedTo = maskedTo;
        this.type = type;
        this.pieces = new String[items.length];
        this.counts = new IntFunction[items.length];
        for (int i = 0; i < items.length; ++i) {
            this.pieces[i] = items[i].item();
            this.counts[i] = items[i].count();
        }
    }
    
    @Override
    public void eval(final Context context) {
        this.conversionOfInvisibleTo(context);
        this.conversionOfMaskedTo(context);
        if (this.stack) {
            this.evalStack(context);
        }
        else {
            final SiteType realType = (this.type == null) ? context.board().defaultSite() : this.type;
            for (final String it : this.item) {
                final int containerId = this.container.eval(context);
                final Component component = context.game().getComponent(it);
                if (component == null) {
                    throw new RuntimeException("Component " + this.item + " is not defined.");
                }
                final int what = component.index();
                for (int i = 0; i < this.count; ++i) {
                    if (containerId == 0) {
                        final int[] emptySites = SitesEmpty.construct(realType, null).eval(context).sites();
                        if (emptySites.length == 0) {
                            break;
                        }
                        final int site = emptySites[context.rng().nextInt(emptySites.length)];
                        Start.placePieces(context, site, what, 1, -1, -1, false, this.masked, this.invisible, realType);
                    }
                }
            }
        }
    }
    
    private void evalStack(final Context context) {
        final SiteType realType = (this.type == null) ? context.board().defaultSite() : this.type;
        if (this.invisibleTo != null) {
            this.invisible = new boolean[context.game().players().count()];
            for (int i = 0; i < context.game().players().count(); ++i) {
                this.invisible[i] = true;
            }
        }
        if (this.maskedTo != null) {
            this.masked = new boolean[context.game().players().count()];
            for (int i = 0; i < context.game().players().count(); ++i) {
                this.masked[i] = true;
            }
        }
        final int site = this.where.eval(context);
        final TIntArrayList toPlace = new TIntArrayList();
        for (int j = 0; j < this.pieces.length; ++j) {
            final String piece = this.pieces[j];
            int pieceIndex = 1;
            while (pieceIndex < context.components().length) {
                if (context.components()[pieceIndex].name().equals(piece)) {
                    if (this.counts == null) {
                        toPlace.add(pieceIndex);
                        break;
                    }
                    for (int k = 0; k < this.counts[j].eval(context); ++k) {
                        toPlace.add(pieceIndex);
                    }
                    break;
                }
                else {
                    ++pieceIndex;
                }
            }
        }
        while (!toPlace.isEmpty()) {
            final int index = context.rng().nextInt(toPlace.size());
            final int what = toPlace.getQuick(index);
            Start.placePieces(context, site, what, 1, -1, -1, true, this.masked, this.invisible, realType);
            toPlace.removeAt(index);
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
    
    @Override
    public int count() {
        return 1;
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
        long flags = 0L;
        flags |= SiteType.stateFlags(this.type);
        if (this.container != null) {
            flags |= this.container.gameFlags(game);
        }
        if (this.stack) {
            flags |= 0x10L;
        }
        if (this.where != null) {
            flags |= this.where.gameFlags(game);
        }
        if (this.invisibleTo != null || this.maskedTo != null) {
            flags |= 0x8L;
        }
        if (this.counts != null) {
            for (final IntFunction func : this.counts) {
                flags |= func.gameFlags(game);
            }
        }
        return flags;
    }
    
    @Override
    public void preprocess(final Game game) {
        if (this.container != null) {
            this.container.preprocess(game);
        }
        this.type = SiteType.use(this.type, game);
        if (this.where != null) {
            this.where.preprocess(game);
        }
        if (this.counts != null) {
            for (final IntFunction func : this.counts) {
                func.preprocess(game);
            }
        }
    }
}
