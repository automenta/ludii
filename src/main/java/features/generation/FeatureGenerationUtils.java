// 
// Decompiled by Procyon v0.5.36
// 

package features.generation;

import features.elements.FeatureElement;
import game.Game;
import game.equipment.component.Component;
import game.util.directions.DirectionFacing;
import game.util.directions.RelativeDirection;
import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.list.array.TIntArrayList;
import util.Context;

import java.util.ArrayList;
import java.util.List;

public class FeatureGenerationUtils
{
    private FeatureGenerationUtils() {
    }
    
    public static void generateWalksForDirnChoice(final Game game, final RelativeDirection dirnChoice, final DirectionFacing facing, final List<TFloatArrayList> outAllowedRotations, final List<TFloatArrayList> outWalks) {
    }
    
    public static boolean testElementTypeInState(final Game game, final Context context, final FeatureElement.ElementType elementType, final int site, final int itemIndex) {
        return false;
    }
    
    public static List<FeatureElement.ElementType> usefulElementTypes(final Game game) {
        final ArrayList<FeatureElement.ElementType> elementTypes = new ArrayList<>();
        elementTypes.add(FeatureElement.ElementType.Empty);
        elementTypes.add(FeatureElement.ElementType.Friend);
        elementTypes.add(FeatureElement.ElementType.Off);
        if (game.players().count() > 1) {
            elementTypes.add(FeatureElement.ElementType.Enemy);
        }
        final int[] componentsPerPlayer = new int[game.players().count() + 1];
        for (final Component component : game.equipment().components()) {
            if (component != null && component.owner() <= game.players().count()) {
                final int[] array = componentsPerPlayer;
                final int owner = component.owner();
                ++array[owner];
            }
        }
        if (componentsPerPlayer[0] > 1) {
            elementTypes.add(FeatureElement.ElementType.Item);
        }
        else {
            for (int i = 1; i < componentsPerPlayer.length; ++i) {
                if (componentsPerPlayer[i] > 1) {
                    elementTypes.add(FeatureElement.ElementType.Item);
                }
            }
        }
        final TIntArrayList connectivities = game.board().topology().trueOrthoConnectivities(game);
        if (connectivities.size() > 1) {
            elementTypes.add(FeatureElement.ElementType.Connectivity);
        }
        elementTypes.trimToSize();
        return elementTypes;
    }
}
