// 
// Decompiled by Procyon v0.5.36
// 

package util.symmetry;

import java.awt.geom.Point2D;
import java.util.BitSet;

public class SymmetryUtils
{
    public static final int[][] playerPermutations(final int numPlayers) {
        if (numPlayers > 4) {
            final int[][] permutations = new int[1][numPlayers + 1];
            for (int who = 1; who <= numPlayers; ++who) {
                permutations[0][who] = who;
            }
            return permutations;
        }
        if (numPlayers == 1) {
            return new int[][] { { 0, 1 } };
        }
        final int[][] permutations = new int[factorial(numPlayers)][numPlayers + 1];
        for (int who = 1; who <= numPlayers; ++who) {
            permutations[0][who] = who;
        }
        for (int p = 1; p < permutations.length; ++p) {
            permutations[p] = nextPermutation(permutations[p - 1]);
        }
        return permutations;
    }
    
    private static int factorial(final int numPlayers) {
        int product = 1;
        for (int n = 1; n <= numPlayers; ++n) {
            product *= n;
        }
        return product;
    }
    
    private static int[] nextPermutation(final int[] previous) {
        int[] next;
        int j;
        for (next = previous.clone(), j = previous.length - 2; j >= 1 && next[j] > next[j + 1]; --j) {}
        int l;
        for (l = previous.length - 1; next[j] > next[l]; --l) {}
        swap(next, j, l);
        int lo = j + 1;
        int hi = previous.length - 1;
        while (lo < hi) {
            swap(next, lo++, hi--);
        }
        return next;
    }
    
    private static final void swap(final int[] array, final int idx1, final int idx2) {
        final int temp = array[idx1];
        array[idx1] = array[idx2];
        array[idx2] = temp;
    }
    
    public static int[] combine(final int[] op1, final int[] op2) {
        final int[] result = new int[op1.length];
        for (int idx = 0; idx < op1.length; ++idx) {
            result[idx] = op2[op1[idx]];
        }
        return result;
    }
    
    public static Point2D rotateAroundPoint(final Point2D origin, final Point2D source, final int steps, final int numSymmetries) {
        final double angle = 6.283185307179586 * steps / numSymmetries;
        final double normalisedX = source.getX() - origin.getX();
        final double normalisedY = source.getY() - origin.getY();
        final double rotatedX = normalisedX * Math.cos(angle) - normalisedY * Math.sin(angle);
        final double rotatedY = normalisedY * Math.cos(angle) + normalisedX * Math.sin(angle);
        return new Point2D.Double(origin.getX() + rotatedX, origin.getY() + rotatedY);
    }
    
    public static Point2D reflectAroundLine(final Point2D origin, final Point2D source, final int steps, final int numSymmetries) {
        if (2 * steps == numSymmetries) {
            final double reflectedY = source.getY();
            final double reflectedX = origin.getX() * 2.0 - source.getX();
            return new Point2D.Double(reflectedX, reflectedY);
        }
        final double angle = 3.141592653589793 * steps / numSymmetries;
        final double m = Math.tan(angle);
        final double c = origin.getY() - m * origin.getX();
        final double d = (source.getX() + (source.getY() - c) * m) / (1.0 + m * m);
        final double reflectedX2 = 2.0 * d - source.getX();
        final double reflectedY2 = 2.0 * d * m - source.getY() + 2.0 * c;
        return new Point2D.Double(reflectedX2, reflectedY2);
    }
    
    public static boolean closeEnough(final Point2D p1, final Point2D p2, final double allowedError) {
        return p1.distance(p2) <= allowedError;
    }
    
    public static boolean isBijective(final int[] mapping) {
        final BitSet set = new BitSet(mapping.length);
        for (final int cell : mapping) {
            if (cell < 0 || cell >= mapping.length) {
                return false;
            }
            set.set(cell);
        }
        return set.cardinality() == mapping.length;
    }
}
