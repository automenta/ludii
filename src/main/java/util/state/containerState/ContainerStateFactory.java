// 
// Decompiled by Procyon v0.5.36
// 

package util.state.containerState;

import game.Game;
import game.equipment.container.Container;
import util.state.containerStackingState.ContainerGraphStateStacks;
import util.state.containerStackingState.ContainerStateCards;
import util.state.containerStackingState.ContainerStateStacks;
import util.state.puzzleState.ContainerDeductionPuzzleState;
import util.state.puzzleState.ContainerDeductionPuzzleStateLarge;
import util.zhash.ZobristHashGenerator;

public class ContainerStateFactory
{
    public static ContainerState createStateForContainer(final ZobristHashGenerator generator, final Game game, final Container container) {
        final int containerSites = container.numSites();
        final int maxWhatValComponents = game.numComponents();
        final int maxWhatValNumPlayers = game.players().count();
        final int maxStateValMaximalLocal = 2 + game.maximalLocalStates();
        final int maxStateValTotalSites = game.equipment().totalDefaultSites();
        final int maxPieces = game.maxCount();
        final int maxCountValMaxPieces = (maxPieces == 0) ? 1 : maxPieces;
        final boolean requiresStack = game.isStacking();
        final boolean requiresCard = game.hasCard();
        final boolean requiresCount = game.requiresCount();
        final boolean requiresState = game.requiresLocalState();
        final boolean requiresRotation = game.requiresRotation();
        final boolean requiresPuzzle = game.isDeductionPuzzle();
        final boolean requiresIndices = game.requiresItemIndices();
        final int numChunks = containerSites;
        int maxWhatVal = -1;
        int maxStateVal = -1;
        int maxRotationVal = -1;
        int maxCountVal = -1;
        if (requiresPuzzle) {
            return constructPuzzle(generator, game, container);
        }
        if (requiresCard) {
            return new ContainerStateCards(generator, game, container, 2);
        }
        if (!container.isHand() && game.isGraphGame() && requiresStack) {
            return new ContainerGraphStateStacks(generator, game, container, 2);
        }
        if (container.isHand()) {
            if (requiresStack) {
                return constructStack(generator, game, container, requiresState, requiresIndices);
            }
            maxWhatVal = maxWhatValComponents;
            if (requiresCount) {
                maxCountVal = maxCountValMaxPieces;
            }
            maxStateVal = maxStateValTotalSites;
            if (game.isGraphGame()) {
                return new ContainerGraphState(generator, game, container, maxWhatVal, maxStateVal, maxCountVal, maxRotationVal);
            }
            return new ContainerFlatState(generator, game, container, numChunks, maxWhatVal, maxStateVal, maxCountVal, maxRotationVal);
        }
        else {
            if (requiresStack) {
                return constructStack(generator, game, container, requiresState, requiresIndices);
            }
            maxCountVal = (requiresCount ? maxCountValMaxPieces : -1);
            maxWhatVal = (requiresIndices ? maxWhatValComponents : maxWhatValNumPlayers);
            if (!requiresCount && !requiresIndices && !requiresState && !game.isGraphGame()) {
                maxWhatVal = -1;
            }
            if (requiresState) {
                maxStateVal = (requiresIndices ? maxStateValTotalSites : maxStateValMaximalLocal);
            }
            if (requiresRotation) {
                maxRotationVal = game.maximalRotationStates();
            }
            if (game.isGraphGame()) {
                return new ContainerGraphState(generator, game, container, maxWhatVal, maxStateVal, maxCountVal, maxRotationVal);
            }
            return new ContainerFlatState(generator, game, container, numChunks, maxWhatVal, maxStateVal, maxCountVal, maxRotationVal);
        }
    }
    
    private static ContainerState constructStack(final ZobristHashGenerator generator, final Game game, final Container container, final boolean requiresState, final boolean requiresIndices) {
        if (!container.isHand() && !requiresIndices && !requiresState) {
            return new ContainerStateStacks(generator, game, container, 1);
        }
        return new ContainerStateStacks(generator, game, container, 2);
    }
    
    private static ContainerState constructPuzzle(final ZobristHashGenerator generator, final Game game, final Container container) {
        final int numComponents = game.numComponents();
        int nbValuesEdge;
        if (game.isDeductionPuzzle()) {
            nbValuesEdge = game.board().edgeRange().max() - game.board().edgeRange().min() + 1;
        }
        else {
            nbValuesEdge = 1;
        }
        int nbValuesVertex;
        if (game.isDeductionPuzzle()) {
            nbValuesVertex = game.board().cellRange().max() - game.board().cellRange().min() + 1;
        }
        else {
            nbValuesVertex = 1;
        }
        if (numComponents + 1 > 31 || nbValuesEdge > 31 || nbValuesVertex > 31) {
            return new ContainerDeductionPuzzleStateLarge(generator, game, container);
        }
        return new ContainerDeductionPuzzleState(generator, game, container);
    }
}
