// 
// Decompiled by Procyon v0.5.36
// 

package features.graph_search;

import features.Walk;
import game.Game;
import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import topology.TopologyElement;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class GraphSearch
{
    private GraphSearch() {
    }
    
    public static Path shortestPathTo(final Game game, final TopologyElement startSite, final TopologyElement destination) {
        final TIntSet alreadyVisited = new TIntHashSet();
        final Queue<Path> fringe = new ArrayDeque<>();
        List<TopologyElement> pathSites = new ArrayList<>();
        pathSites.add(startSite);
        fringe.add(new Path(pathSites, new Walk()));
        alreadyVisited.add(startSite.index());
        final List<? extends TopologyElement> sites = game.graphPlayElements();
        while (!fringe.isEmpty()) {
            final Path path = fringe.remove();
            final TopologyElement pathEnd = path.destination();
            final int numOrthos = pathEnd.sortedOrthos().length;
            final TFloatArrayList rotations = Walk.rotationsForNumOrthos(numOrthos);
            for (int i = 0; i < rotations.size(); ++i) {
                final float nextStep = rotations.getQuick(i);
                final Walk newWalk = new Walk(path.walk());
                newWalk.steps().add(nextStep);
                final TIntArrayList destinations = newWalk.resolveWalk(game, startSite, 0.0f, 1);
                if (destinations.size() != 1) {
                    System.err.println("WARNING: GraphSearch.shortestPathTo() resolved a walk with " + destinations.size() + " destinations!");
                }
                final int endWalkIdx = destinations.getQuick(0);
                if (destination.index() == endWalkIdx) {
                    pathSites = new ArrayList<>(path.sites);
                    pathSites.add(destination);
                    return new Path(pathSites, newWalk);
                }
                if (endWalkIdx >= 0 && !alreadyVisited.contains(endWalkIdx)) {
                    alreadyVisited.add(endWalkIdx);
                    pathSites = new ArrayList<>(path.sites);
                    pathSites.add(sites.get(endWalkIdx));
                    fringe.add(new Path(pathSites, newWalk));
                }
            }
        }
        return null;
    }
}
