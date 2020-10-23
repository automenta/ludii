// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.util;

import game.equipment.other.Regions;
import game.functions.ints.board.Id;
import game.types.play.RoleType;
import util.Context;

import java.util.ArrayList;

public class MetadataFunctions
{
    public static ArrayList<ArrayList<Integer>> convertRegionToSiteArray(final Context context, final String regionName, final RoleType roleType) {
        final ArrayList<ArrayList<Integer>> allRegionSites = new ArrayList<>();
        for (final Regions region : context.equipment().regions()) {
            if (region.name().equals(regionName) && (roleType == null || roleType.owner() == region.owner())) {
                allRegionSites.add(new ArrayList<>());
                for (final int site : region.eval(context)) {
                    allRegionSites.get(allRegionSites.size() - 1).add(site);
                }
            }
        }
        return allRegionSites;
    }
    
    public static int getRealOwner(final Context context, final RoleType roletype) {
        return new Id(null, roletype).eval(context.currentInstanceContext());
    }
}
