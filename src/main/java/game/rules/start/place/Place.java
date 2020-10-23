// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.start.place;

import annotations.Name;
import annotations.Opt;
import annotations.Or;
import annotations.Or2;
import game.Game;
import game.functions.ints.IntFunction;
import game.functions.region.RegionFunction;
import game.rules.Rule;
import game.rules.start.StartRule;
import game.rules.start.place.item.PlaceItem;
import game.rules.start.place.random.PlaceRandom;
import game.rules.start.place.stack.PlaceCustomStack;
import game.rules.start.place.stack.PlaceMonotonousStack;
import game.types.board.SiteType;
import game.types.play.RoleType;
import game.util.math.Count;
import util.Context;

public final class Place extends StartRule
{
    private static final long serialVersionUID = 1L;
    
    public static Rule construct(final String item, @Opt final String container, @Opt final SiteType type, @Opt final IntFunction loc, @Opt @Name final String coord, @Opt @Name final Integer count, @Opt @Name final Integer state, @Opt @Name final Integer rotation, @Opt @Name final RoleType[] invisibleTo, @Opt @Name final RoleType[] maskedTo) {
        return new PlaceItem(item, container, type, loc, coord, count, state, rotation, invisibleTo, maskedTo);
    }
    
    public static Rule construct(final String item, @Opt final SiteType type, @Opt final IntFunction[] locs, @Opt final RegionFunction region, @Opt final String[] coords, @Opt @Name final Integer[] counts, @Opt @Name final Integer state, @Opt @Name final Integer rotation, @Opt @Name final RoleType[] invisibleTo) {
        return new PlaceItem(item, type, locs, region, coords, counts, state, rotation, invisibleTo);
    }
    
    public static Rule construct(final PlaceStackType placeType, @Or final String item, @Or @Name final String[] items, @Opt final String container, @Opt final SiteType type, @Or @Opt final IntFunction loc, @Or @Opt final IntFunction[] locs, @Or @Opt final RegionFunction region, @Or @Opt @Name final String coord, @Or @Opt final String[] coords, @Or2 @Opt @Name final Integer count, @Or2 @Opt @Name final Integer[] counts, @Opt @Name final Integer state, @Opt @Name final Integer rotation, @Opt @Name final RoleType[] invisibleTo, @Opt @Name final RoleType[] maskedTo) {
        int numNonNull = 0;
        if (item != null) {
            ++numNonNull;
        }
        if (items != null) {
            ++numNonNull;
        }
        if (numNonNull != 1) {
            throw new IllegalArgumentException("Place(): With PlaceStackType exactly one item or items parameter must be non-null.");
        }
        numNonNull = 0;
        if (count != null) {
            ++numNonNull;
        }
        if (counts != null) {
            ++numNonNull;
        }
        if (numNonNull > 1) {
            throw new IllegalArgumentException("Place(): With PlaceStackType zero or one count or counts parameter must be non-null.");
        }
        numNonNull = 0;
        if (coord != null) {
            ++numNonNull;
        }
        if (coords != null) {
            ++numNonNull;
        }
        if (loc != null) {
            ++numNonNull;
        }
        if (locs != null) {
            ++numNonNull;
        }
        if (region != null) {
            ++numNonNull;
        }
        if (numNonNull > 1) {
            throw new IllegalArgumentException("Place(): With PlaceStackType zero or one coord or coords or loc or locs or region parameter must be non-null.");
        }
        if (items == null && (locs != null || region != null || coord != null || counts != null)) {
            return new PlaceMonotonousStack(item, type, locs, region, coords, count, counts, state, rotation, invisibleTo);
        }
        return new PlaceCustomStack(item, items, container, type, loc, coord, count, state, rotation, invisibleTo, maskedTo);
    }
    
    public static Rule construct(final PlaceRandomType placeType, @Opt final IntFunction container, final String[] item, @Opt @Name final Integer count, @Opt @Name final RoleType[] invisibleTo, @Opt @Name final RoleType[] maskedTo, @Opt final SiteType type) {
        return new PlaceRandom(container, item, count, maskedTo, invisibleTo, type);
    }
    
    public static Rule construct(final PlaceRandomType placeType, final String[] pieces, @Name @Opt final IntFunction[] count, final IntFunction where, @Opt @Name final RoleType[] invisibleTo, @Opt @Name final RoleType[] maskedTo, @Opt final SiteType type) {
        return new PlaceRandom(pieces, count, where, maskedTo, invisibleTo, type);
    }
    
    public static Rule construct(final PlaceRandomType placeType, final Count[] items, final IntFunction where, @Opt @Name final RoleType[] invisibleTo, @Opt @Name final RoleType[] maskedTo, @Opt final SiteType type) {
        return new PlaceRandom(items, where, maskedTo, invisibleTo, type);
    }
    
    private Place() {
    }
    
    @Override
    public void eval(final Context context) {
        throw new UnsupportedOperationException("Place.eval(): Should never be called directly.");
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
}
