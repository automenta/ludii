// 
// Decompiled by Procyon v0.5.36
// 

package game.equipment.other;

import annotations.Opt;
import game.Game;
import game.equipment.Item;
import game.equipment.component.Component;
import game.equipment.container.board.Board;
import game.functions.ints.IntFunction;
import game.types.board.LandmarkType;
import game.types.board.SiteType;
import game.types.play.RoleType;
import game.util.math.Pair;
import gnu.trove.map.hash.TIntIntHashMap;
import main.StringRoutines;
import topology.SiteFinder;
import topology.TopologyElement;
import util.Context;
import util.ItemType;
import util.Trial;

public class Map extends Item
{
    private final TIntIntHashMap map;
    private final Pair[] mapPairs;
    
    public Map(@Opt final String name, final Pair[] pairs) {
        super((name == null) ? "Map" : name, -1, RoleType.Neutral);
        this.map = new TIntIntHashMap();
        this.mapPairs = pairs;
        this.setType(ItemType.Map);
    }
    
    public Map(@Opt final String name, final IntFunction[] keys, final IntFunction[] values) {
        super((name == null) ? "Map" : name, -1, RoleType.Neutral);
        this.map = new TIntIntHashMap();
        final int minLength = Math.min(keys.length, values.length);
        this.mapPairs = new Pair[minLength];
        for (int i = 0; i < minLength; ++i) {
            this.mapPairs[i] = new Pair(keys[i], values[i]);
        }
        this.setType(ItemType.Map);
    }
    
    public TIntIntHashMap map() {
        return this.map;
    }
    
    public int to(final int key) {
        return this.map.get(key);
    }
    
    public int noEntryValue() {
        return this.map.getNoEntryValue();
    }
    
    public void computeMap(final Game game) {
        for (final Pair pair : this.mapPairs) {
            int intKey = pair.intKey().eval(new Context(game, new Trial(game)));
            if (intKey == -1) {
                final TopologyElement element = SiteFinder.find(game.board(), pair.stringKey(), game.board().defaultSite());
                if (element != null) {
                    intKey = element.index();
                }
            }
            int intValue = pair.intValue().eval(new Context(game, new Trial(game)));
            if (intValue == -1) {
                if (pair.stringValue() != null) {
                    if (StringRoutines.isCoordinate(pair.stringValue())) {
                        final TopologyElement element2 = SiteFinder.find(game.board(), pair.stringValue(), game.board().defaultSite());
                        if (element2 != null) {
                            intValue = element2.index();
                        }
                    }
                    else {
                        for (int i = 1; i < game.equipment().components().length; ++i) {
                            final Component component = game.equipment().components()[i];
                            if (component.name().equals(pair.stringValue())) {
                                intValue = i;
                                break;
                            }
                        }
                    }
                }
                else {
                    final LandmarkType landmarkType = pair.landmarkType();
                    intValue = getSite(game.board(), landmarkType);
                }
            }
            if (intValue != -1 && intKey != -1) {
                this.map.put(intKey, intValue);
            }
        }
    }
    
    private static int getSite(final Board board, final LandmarkType landmarkType) {
        switch (landmarkType) {
            case BottomSite -> {
                return ((board.defaultSite() == SiteType.Vertex) ? board.topology().bottom(SiteType.Vertex) : board.topology().bottom(SiteType.Cell)).get(0).index();
            }
            case CentreSite -> {
                return ((board.defaultSite() == SiteType.Vertex) ? board.topology().centre(SiteType.Vertex) : board.topology().centre(SiteType.Cell)).get(0).index();
            }
            case LeftSite -> {
                return ((board.defaultSite() == SiteType.Vertex) ? board.topology().left(SiteType.Vertex) : board.topology().left(SiteType.Cell)).get(0).index();
            }
            case RightSite -> {
                return ((board.defaultSite() == SiteType.Vertex) ? board.topology().right(SiteType.Vertex) : board.topology().right(SiteType.Cell)).get(0).index();
            }
            case Topsite -> {
                return ((board.defaultSite() == SiteType.Vertex) ? board.topology().top(SiteType.Vertex) : board.topology().top(SiteType.Cell)).get(0).index();
            }
            case FirstSite -> {
                return 0;
            }
            case LastSite -> {
                return (board.defaultSite() == SiteType.Vertex) ? board.topology().vertices().get(board.topology().vertices().size() - 1).index() : board.topology().cells().get(board.topology().cells().size() - 1).index();
            }
            default -> {
                return -1;
            }
        }
    }
}
