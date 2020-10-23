// 
// Decompiled by Procyon v0.5.36
// 

package features.visualisation;

import features.Walk;
import features.features.Feature;
import game.Game;
import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.list.array.TIntArrayList;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FeatureToEPS
{
    private FeatureToEPS() {
    }
    
    public static void createEPS(final Feature feature, final int player, final Game game, final File outputFile) {
    }
    
    public static List<int[]> computeOffsets(final Walk walk, final int numOrths) {
        if (walk == null) {
            return Arrays.asList(new int[][] { new int[0] });
        }
        int[][] dirOffsets;
        if (numOrths == 4) {
            dirOffsets = new int[][] { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };
        }
        else {
            if (numOrths != 6) {
                return null;
            }
            dirOffsets = new int[][] { { 1, 1 }, { 1, 0 }, { 1, -1 }, { -1, -1 }, { -1, 0 }, { -1, 1 } };
        }
        final List<int[]> offsets = new ArrayList<>();
        final TFloatArrayList steps = walk.steps();
        if (steps.size() > 0) {
            final TIntArrayList connectionIndices = new TIntArrayList(2);
            float connectionIdxFloat = steps.get(0) * dirOffsets.length;
            float connectionIdxFractionalPart = connectionIdxFloat - (int)connectionIdxFloat;
            if (Math.abs(0.5f - connectionIdxFractionalPart) < 0.02f || Math.abs(0.5f + connectionIdxFractionalPart) < 0.02f) {
                connectionIndices.add((int)Math.floor(connectionIdxFloat));
                connectionIndices.add((int)Math.ceil(connectionIdxFloat));
            }
            else {
                connectionIndices.add(Math.round(connectionIdxFloat));
            }
            for (int c = 0; c < connectionIndices.size(); ++c) {
                int connectionIdx = connectionIndices.getQuick(c);
                connectionIdx = (connectionIdx % dirOffsets.length + dirOffsets.length) % dirOffsets.length;
                List<List<int[]>> offsetPaths = new ArrayList<>();
                List<int[]> currPath = new ArrayList<>();
                currPath.add(dirOffsets[connectionIdx]);
                offsetPaths.add(currPath);
                for (int step = 1; step < steps.size(); ++step) {
                    final List<List<int[]>> newOffsetPaths = new ArrayList<>();
                    for (int i = 0; i < offsetPaths.size(); ++i) {
                        currPath = offsetPaths.get(i);
                        final TIntArrayList contDirs = new TIntArrayList(2);
                        int fromDir = -1;
                        final int[] lastOffset = currPath.get(currPath.size() - 1);
                        for (int dirIdx = 0; dirIdx < dirOffsets.length; ++dirIdx) {
                            if (dirOffsets[dirIdx][0] + lastOffset[0] == 0 && dirOffsets[dirIdx][1] + lastOffset[1] == 0) {
                                fromDir = dirIdx;
                                break;
                            }
                        }
                        if (fromDir == -1) {
                            System.err.println("Warning! FeatureToEPS.computeOffsets() could not find fromDir!");
                        }
                        if (dirOffsets.length % 2 == 0) {
                            contDirs.add(fromDir + dirOffsets.length / 2);
                        }
                        else {
                            contDirs.add(fromDir + dirOffsets.length / 2);
                            contDirs.add(fromDir + 1 + dirOffsets.length / 2);
                        }
                        for (int contDirIdx = 0; contDirIdx < contDirs.size(); ++contDirIdx) {
                            final int contDir = contDirs.getQuick(contDirIdx);
                            final TIntArrayList nextConnectionIndices = new TIntArrayList(2);
                            connectionIdxFloat = contDir + steps.get(step) * dirOffsets.length;
                            connectionIdxFractionalPart = connectionIdxFloat - (int)connectionIdxFloat;
                            if (Math.abs(0.5f - connectionIdxFractionalPart) < 0.02f || Math.abs(0.5f + connectionIdxFractionalPart) < 0.02f) {
                                nextConnectionIndices.add((int)Math.floor(connectionIdxFloat));
                                nextConnectionIndices.add((int)Math.ceil(connectionIdxFloat));
                            }
                            else {
                                nextConnectionIndices.add(Math.round(connectionIdxFloat));
                            }
                            for (int n = 0; n < nextConnectionIndices.size(); ++n) {
                                connectionIdx = (nextConnectionIndices.getQuick(n) % dirOffsets.length + dirOffsets.length) % dirOffsets.length;
                                final List<int[]> newCurrPath = new ArrayList<>();
                                newCurrPath.addAll(currPath);
                                newCurrPath.add(dirOffsets[connectionIdx]);
                                newOffsetPaths.add(newCurrPath);
                            }
                        }
                    }
                    offsetPaths = newOffsetPaths;
                }
                for (final List<int[]> offsetPath : offsetPaths) {
                    final int[] sumOffsets = { 0, 0 };
                    for (final int[] offset : offsetPath) {
                        final int[] array = sumOffsets;
                        final int n2 = 0;
                        array[n2] += offset[0];
                        final int[] array2 = sumOffsets;
                        final int n3 = 1;
                        array2[n3] += offset[1];
                    }
                    offsets.add(sumOffsets);
                }
            }
        }
        else {
            offsets.add(new int[] { 0, 0 });
        }
        return offsets;
    }
}
