// 
// Decompiled by Procyon v0.5.36
// 

package features;

import features.graph_search.Path;
import game.Game;
import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.list.array.TIntArrayList;
import main.StringRoutines;
import topology.TopologyElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class Walk
{
    protected TFloatArrayList steps;
    
    public Walk() {
        this.steps = new TFloatArrayList(1);
    }
    
    public Walk(final float... steps) {
        this.steps = TFloatArrayList.wrap(steps);
    }
    
    public Walk(final TFloatArrayList steps) {
        this.steps = new TFloatArrayList(steps);
    }
    
    public Walk(final Walk other) {
        this.steps = new TFloatArrayList(other.steps());
    }
    
    public Walk(final String string) {
        final String walkString = string.substring("{".length(), string.length() - "}".length());
        if (!walkString.isEmpty()) {
            final String[] stepStrings = walkString.split(",");
            this.steps = new TFloatArrayList(stepStrings.length);
            for (final String stepString : stepStrings) {
                final String s = stepString.trim();
                if (s.contains("/")) {
                    final String[] parts = s.split(Pattern.quote("/"));
                    this.steps.add(Integer.parseInt(parts[0]) / (float)Integer.parseInt(parts[1]));
                }
                else {
                    this.steps.add(Float.parseFloat(stepString.trim()));
                }
            }
        }
        else {
            this.steps = new TFloatArrayList(0);
        }
    }
    
    public void applyReflection(final int reflection) {
        if (reflection == 1) {
            return;
        }
        for (int i = 0; i < this.steps.size(); ++i) {
            this.steps.setQuick(i, this.steps.getQuick(i) * reflection);
        }
    }
    
    public void applyRotation(final float rotation) {
        if (!this.steps.isEmpty()) {
            this.steps.setQuick(0, this.steps.getQuick(0) + rotation);
        }
    }
    
    public void appendWalk(final Walk walk) {
        this.steps.add(walk.steps().toArray());
    }
    
    public void prependStep(final float step) {
        this.steps.insert(0, step);
    }
    
    public void prependWalk(final Walk walk) {
        this.steps.insert(0, walk.steps().toArray());
    }
    
    public void prependWalkWithCorrection(final Walk walk, final Path path, final float rotToRevert, final int refToRevert) {
        if (walk.steps.isEmpty()) {
            return;
        }
        if (!this.steps.isEmpty()) {
            final TopologyElement endSite = path.destination();
            final TopologyElement penultimateSite = path.sites().get(path.sites().size() - 2);
            final TIntArrayList contDirs = new TIntArrayList(2);
            final TopologyElement[] sortedOrthos = endSite.sortedOrthos();
            int fromDir = -1;
            for (int orthIdx = 0; orthIdx < sortedOrthos.length; ++orthIdx) {
                if (sortedOrthos[orthIdx] != null && sortedOrthos[orthIdx].index() == penultimateSite.index()) {
                    fromDir = orthIdx;
                    break;
                }
            }
            if (fromDir == -1) {
                System.err.println("Warning! Walk.prependWalkWithCorrection() could not find fromDir!");
            }
            contDirs.add(fromDir + sortedOrthos.length / 2);
            final float toSubtract = contDirs.getQuick(0) / (float)sortedOrthos.length - rotToRevert * refToRevert;
            this.steps.setQuick(0, this.steps.getQuick(0) - toSubtract);
        }
        this.steps.insert(0, walk.steps().toArray());
    }
    
    public TFloatArrayList steps() {
        return this.steps;
    }
    
    public TIntArrayList resolveWalk(final Game game, final TopologyElement startSite, final float rotModifier, final int reflectionMult) {
        final TIntArrayList results = new TIntArrayList(1);
        if (!this.steps.isEmpty()) {
            final TopologyElement[] sortedOrthos = startSite.sortedOrthos();
            final TIntArrayList connectionIndices = new TIntArrayList(2);
            float connectionIdxFloat = (this.steps.get(0) + rotModifier) * reflectionMult * sortedOrthos.length;
            float connectionIdxFractionalPart = connectionIdxFloat - (int)connectionIdxFloat;
            if (Math.abs(0.5f - connectionIdxFractionalPart) < 0.02f || Math.abs(0.5f + connectionIdxFractionalPart) < 0.02f) {
                connectionIndices.add((int)Math.floor(connectionIdxFloat));
            }
            else {
                connectionIndices.add(Math.round(connectionIdxFloat));
            }
            boolean wentOffBoard = false;
            for (int c = 0; c < connectionIndices.size(); ++c) {
                TopologyElement prevSite = startSite;
                int connectionIdx = connectionIndices.getQuick(c);
                connectionIdx = (connectionIdx % sortedOrthos.length + sortedOrthos.length) % sortedOrthos.length;
                TopologyElement nextSite = sortedOrthos[connectionIdx];
                List<TopologyElement> nextSites = Collections.singletonList(nextSite);
                List<TopologyElement> prevSites = Collections.singletonList(prevSite);
                for (int step = 1; step < this.steps.size(); ++step) {
                    final List<TopologyElement> newNextSites = new ArrayList<>(nextSites.size());
                    final List<TopologyElement> newPrevSites = new ArrayList<>(nextSites.size());
                    for (int i = 0; i < nextSites.size(); ++i) {
                        prevSite = prevSites.get(i);
                        nextSite = nextSites.get(i);
                        if (nextSite == null) {
                            wentOffBoard = true;
                        }
                        else {
                            final TIntArrayList contDirs = new TIntArrayList(2);
                            final TopologyElement[] nextSortedOrthos = nextSite.sortedOrthos();
                            int fromDir = -1;
                            for (int nextOrthIdx = 0; nextOrthIdx < nextSortedOrthos.length; ++nextOrthIdx) {
                                if (nextSortedOrthos[nextOrthIdx] != null && nextSortedOrthos[nextOrthIdx].index() == prevSite.index()) {
                                    fromDir = nextOrthIdx;
                                    break;
                                }
                            }
                            if (fromDir == -1) {
                                System.err.println("Warning! Walk.resolveWalk() could not find fromDir!");
                            }
                            contDirs.add(fromDir + nextSortedOrthos.length / 2);
                            for (int contDirIdx = 0; contDirIdx < contDirs.size(); ++contDirIdx) {
                                final int contDir = contDirs.getQuick(contDirIdx);
                                final TIntArrayList nextConnectionIndices = new TIntArrayList(2);
                                connectionIdxFloat = contDir + this.steps.get(step) * reflectionMult * nextSortedOrthos.length;
                                connectionIdxFractionalPart = connectionIdxFloat - (int)connectionIdxFloat;
                                if (Math.abs(0.5f - connectionIdxFractionalPart) < 0.02f || Math.abs(0.5f + connectionIdxFractionalPart) < 0.02f) {
                                    nextConnectionIndices.add((int)Math.floor(connectionIdxFloat));
                                }
                                else {
                                    nextConnectionIndices.add(Math.round(connectionIdxFloat));
                                }
                                for (int n = 0; n < nextConnectionIndices.size(); ++n) {
                                    connectionIdx = (nextConnectionIndices.getQuick(n) % nextSortedOrthos.length + nextSortedOrthos.length) % nextSortedOrthos.length;
                                    final TopologyElement newNextSite = nextSortedOrthos[connectionIdx];
                                    newPrevSites.add(nextSite);
                                    newNextSites.add(newNextSite);
                                }
                            }
                        }
                    }
                    nextSites = newNextSites;
                    prevSites = newPrevSites;
                }
                for (final TopologyElement destination : nextSites) {
                    if (destination == null) {
                        wentOffBoard = true;
                    }
                    else {
                        results.add(destination.index());
                    }
                }
            }
            if (wentOffBoard) {
                results.add(-1);
            }
        }
        else {
            results.add(startSite.index());
        }
        return results;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.steps == null) ? 0 : this.steps.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(final Object other) {
        return other instanceof Walk && this.steps.equals(((Walk)other).steps());
    }
    
    public static TFloatArrayList allGameRotations(final Game game) {
        final TIntArrayList connectivities = game.board().topology().trueOrthoConnectivities(game);
        final TFloatArrayList rotations = new TFloatArrayList();
        for (int i = connectivities.size() - 1; i >= 0; --i) {
            final int connectivity = connectivities.getQuick(i);
            if (connectivity != 0) {
                boolean alreadyHandled = false;
                for (int j = i + 1; j < connectivities.size(); ++j) {
                    if (connectivities.getQuick(j) % connectivity == 0) {
                        alreadyHandled = true;
                        break;
                    }
                }
                if (!alreadyHandled) {
                    final TFloatArrayList newRots = rotationsForNumOrthos(connectivity);
                    for (int k = 0; k < newRots.size(); ++k) {
                        if (!rotations.contains(newRots.getQuick(k))) {
                            rotations.add(newRots.getQuick(k));
                        }
                    }
                }
            }
        }
        return rotations;
    }
    
    public static TFloatArrayList rotationsForNumOrthos(final int numOrthos) {
        final TFloatArrayList allowedRotations = new TFloatArrayList();
        for (int i = 0; i < numOrthos; ++i) {
            allowedRotations.add(i / (float)numOrthos);
        }
        return allowedRotations;
    }
    
    @Override
    public String toString() {
        String str = "";
        for (int i = 0; i < this.steps.size(); ++i) {
            str += StringRoutines.floatToFraction(this.steps.get(i), 10);
            if (i < this.steps.size() - 1) {
                str += ",";
            }
        }
        return String.format("{%s}", str);
    }
}
