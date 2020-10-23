// 
// Decompiled by Procyon v0.5.36
// 

package main.collections;

import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.list.array.TIntArrayList;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ListUtils
{
    private ListUtils() {
    }
    
    public static List<TIntArrayList> generatePermutations(final TIntArrayList list) {
        if (list.size() == 0) {
            final List<TIntArrayList> perms = new ArrayList<>(1);
            perms.add(new TIntArrayList(0, list.getNoEntryValue()));
            return perms;
        }
        final int lastElement = list.removeAt(list.size() - 1);
        final List<TIntArrayList> perms2 = new ArrayList<>();
        final List<TIntArrayList> smallPerms = generatePermutations(list);
        for (final TIntArrayList smallPerm : smallPerms) {
            for (int i = smallPerm.size(); i >= 0; --i) {
                final TIntArrayList newPerm = new TIntArrayList(smallPerm);
                newPerm.insert(i, lastElement);
                perms2.add(newPerm);
            }
        }
        return perms2;
    }
    
    public static <E> List<List<E>> generateTuples(final List<List<E>> optionsLists) {
        final List<List<E>> allTuples = new ArrayList<>();
        if (optionsLists.size() > 0) {
            final List<E> firstEntryOptions = optionsLists.get(0);
            final List<List<E>> remainingOptionsLists = new ArrayList<>();
            for (int i = 1; i < optionsLists.size(); ++i) {
                remainingOptionsLists.add(optionsLists.get(i));
            }
            final List<List<E>> nMinOneTuples = generateTuples(remainingOptionsLists);
            for (int j = 0; j < firstEntryOptions.size(); ++j) {
                for (final List<E> nMinOneTuple : nMinOneTuples) {
                    final List<E> newTuple = new ArrayList<>(nMinOneTuple);
                    newTuple.add(0, firstEntryOptions.get(j));
                    allTuples.add(newTuple);
                }
            }
        }
        else {
            allTuples.add(new ArrayList<>(0));
        }
        return allTuples;
    }
    
    public static int argMax(final TFloatArrayList list) {
        int argMax = 0;
        float maxVal = list.getQuick(0);
        for (int i = 1; i < list.size(); ++i) {
            final float val = list.getQuick(i);
            if (val > maxVal) {
                maxVal = val;
                argMax = i;
            }
        }
        return argMax;
    }
    
    public static <E> void removeSwap(final List<E> list, final int idx) {
        final int lastIdx = list.size() - 1;
        list.set(idx, list.get(lastIdx));
        list.remove(lastIdx);
    }
    
    public static int getCapacity(final ArrayList<?> l) {
        try {
            final Field dataField = ArrayList.class.getDeclaredField("elementData");
            dataField.setAccessible(true);
            return ((Object[])dataField.get(l)).length;
        }
        catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex2) {
            ex2.printStackTrace();
            return -1;
        }
    }
}
