/*
 * Decompiled with CFR 0.150.
 */
package collections;

import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.list.array.TIntArrayList;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ListUtils {
    private ListUtils() {
    }

    public static List<TIntArrayList> generatePermutations(TIntArrayList list) {
        if (list.isEmpty()) {
            ArrayList<TIntArrayList> perms = new ArrayList<>(1);
            perms.add(new TIntArrayList(0, list.getNoEntryValue()));
            return perms;
        }
        int lastElement = list.removeAt(list.size() - 1);
        ArrayList<TIntArrayList> perms = new ArrayList<>();
        List<TIntArrayList> smallPerms = ListUtils.generatePermutations(list);
        for (TIntArrayList smallPerm : smallPerms) {
            for (int i = smallPerm.size(); i >= 0; --i) {
                TIntArrayList newPerm = new TIntArrayList(smallPerm);
                newPerm.insert(i, lastElement);
                perms.add(newPerm);
            }
        }
        return perms;
    }

    public static <E> List<List<E>> generateTuples(List<List<E>> optionsLists) {
        ArrayList allTuples = new ArrayList();
        if (!optionsLists.isEmpty()) {
            List<E> firstEntryOptions = optionsLists.get(0);
            ArrayList<List<E>> remainingOptionsLists = new ArrayList<>();
            for (int i = 1; i < optionsLists.size(); ++i) {
                remainingOptionsLists.add(optionsLists.get(i));
            }
            List<List<E>> nMinOneTuples = ListUtils.generateTuples(remainingOptionsLists);
            for (E firstEntryOption : firstEntryOptions) {
                for (List<E> nMinOneTuple : nMinOneTuples) {
                    ArrayList<E> newTuple = new ArrayList<>(nMinOneTuple);
                    newTuple.add(0, firstEntryOption);
                    allTuples.add(newTuple);
                }
            }
        } else {
            allTuples.add(new ArrayList(0));
        }
        return allTuples;
    }

    public static int argMax(TFloatArrayList list) {
        int argMax = 0;
        float maxVal = list.getQuick(0);
        for (int i = 1; i < list.size(); ++i) {
            float val = list.getQuick(i);
            if (!(val > maxVal)) continue;
            maxVal = val;
            argMax = i;
        }
        return argMax;
    }

    public static <E> void removeSwap(List<E> list, int idx) {
        int lastIdx = list.size() - 1;
        list.set(idx, list.get(lastIdx));
        list.remove(lastIdx);
    }

    public static int getCapacity(ArrayList<?> l) {
        try {
            Field dataField = ArrayList.class.getDeclaredField("elementData");
            dataField.setAccessible(true);
            return ((Object[])dataField.get(l)).length;
        }
        catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException exception) {
            exception.printStackTrace();
            return -1;
        }
    }
}

