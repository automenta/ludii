// 
// Decompiled by Procyon v0.5.36
// 

package manager.referee;

import game.rules.play.moves.Moves;
import game.types.board.SiteType;
import collections.FastArrayList;
import manager.Manager;
import manager.utils.ContextSnapshot;
import manager.utils.PuzzleSelectionType;
import manager.utils.SettingsManager;
import util.Context;
import util.Move;
import util.SettingsVC;
import util.action.Action;
import util.action.die.ActionUpdateDice;
import util.action.die.ActionUseDie;
import util.action.others.ActionPass;
import util.action.puzzle.ActionReset;
import util.action.puzzle.ActionSet;
import util.action.puzzle.ActionToggle;
import util.locations.FullLocation;
import util.locations.Location;
import util.state.containerState.ContainerState;

import java.util.ArrayList;
import java.util.HashMap;

public class UserMoveHandler
{
    public static void tryGameMove(final Location locnFromInfo, final Location locnToInfo, final boolean pieceSelectedThisClick, final boolean pickingDialog) {
        final Context context = ContextSnapshot.getContext();
        final Moves legal = context.game().moves(context);
        boolean pieceSelected = pieceSelectedThisClick;
        if (!userMove(SettingsVC.selectedLocation, locnToInfo, context)) {
            if (locnToInfo.equalsLoc(locnFromInfo)) {
                int numMatchesFrom = 0;
                int numMatchesTo = 0;
                Move onlyMoveFrom = null;
                Move onlyMoveTo = null;
                for (final Move move : legal.moves()) {
                    if (move.to() == locnFromInfo.site() && move.toType() == locnFromInfo.siteType()) {
                        ++numMatchesFrom;
                        onlyMoveFrom = move;
                    }
                    if (move.from() == locnFromInfo.site() && move.fromType() == locnFromInfo.siteType()) {
                        ++numMatchesTo;
                        onlyMoveTo = move;
                    }
                }
                if (SettingsVC.selectedLocation.site() == -1) {
                    if (numMatchesFrom == 1 && SettingsManager.autoMoveFrom) {
                        applyHumanMoveToGame(onlyMoveFrom);
                    }
                }
                else if (locnFromInfo.equalsLoc(SettingsVC.selectedLocation) && !pieceSelected) {
                    SettingsVC.selectedLocation = new FullLocation(-1);
                    pieceSelected = true;
                }
                else if (SettingsVC.pieceBeingDragged) {
                    SettingsVC.selectedLocation = new FullLocation(-1);
                }
                else if (numMatchesTo == 1 && SettingsManager.autoMoveTo) {
                    applyHumanMoveToGame(onlyMoveTo);
                }
                else if (userMove(SettingsVC.selectedLocation, locnFromInfo, context)) {
                    SettingsVC.selectedLocation = new FullLocation(-1);
                }
            }
            else {
                if (pieceSelected && !pickingDialog && !SettingsVC.SelectingConsequenceMove) {
                    Manager.app.setVolatileMessage("That is not a valid move.");
                }
                SettingsVC.selectedLocation = new FullLocation(-1);
            }
        }
        if (!pieceSelected) {
            SettingsVC.selectedLocation = new FullLocation(-1);
        }
    }
    
    public static void tryPuzzleMove(final Location locnFromInfo, final Location locnToInfo) {
        final Context context = ContextSnapshot.getContext();
        if (locnFromInfo.site() != -1) {
            boolean validMove = false;
            int site = 0;
            final Moves legal = ContextSnapshot.getContext().game().moves(ContextSnapshot.getContext());
            for (final Move move : legal.moves()) {
                if (move.from() == locnToInfo.site() && move.to() == locnFromInfo.site()) {
                    validMove = true;
                    site = locnFromInfo.site();
                    break;
                }
            }
            if (validMove) {
                final ContainerState cs = ContextSnapshot.getContext().state().containerStates()[0];
                SiteType setType = Manager.ref().context().board().defaultSite();
                int maxValue = 0;
                int puzzleValue = 0;
                boolean valueResolved = false;
                boolean valueFound = false;
                setType = Manager.ref().context().board().defaultSite();
                maxValue = ContextSnapshot.getContext().board().getRange(setType).max();
                puzzleValue = cs.what(site, setType);
                valueResolved = cs.isResolved(site, setType);
                valueFound = false;
                if (!valueResolved) {
                    puzzleValue = -1;
                }
                for (int i = puzzleValue + 1; i < maxValue + 1; ++i) {
                    ActionSet a = null;
                    a = new ActionSet(setType, site, i);
                    a.setDecision(true);
                    final Move m = new Move(a);
                    m.setFromNonDecision(locnFromInfo.site());
                    m.setToNonDecision(locnToInfo.site());
                    m.setEdgeMove(site);
                    m.setDecision(true);
                    m.setOrientedMove(false);
                    if (context.game().moves(context).moves().contains(m) || (SettingsManager.illegalMovesValid && i > 0)) {
                        valueFound = true;
                        puzzleValue = i;
                        break;
                    }
                }
                if (SettingsManager.puzzleDialogOption == PuzzleSelectionType.Dialog || (SettingsManager.puzzleDialogOption == PuzzleSelectionType.Automatic && maxValue > 3)) {
                    Manager.app.showPuzzleDialog(site);
                }
                else {
                    if (!valueFound) {
                        applyHumanMoveToGame(new Move(new ActionReset(setType, site, maxValue + 1)));
                    }
                    else {
                        puzzleMove(site, puzzleValue, true, setType);
                        if (ContextSnapshot.getContext().trial().over()) {
                            setType = SiteType.Edge;
                            for (int i = 0; i < ContextSnapshot.getContext().board().topology().edges().size(); ++i) {
                                if (!cs.isResolvedEdges(i)) {
                                    puzzleMove(i, 0, true, setType);
                                }
                            }
                            setType = SiteType.Cell;
                            for (int i = 0; i < ContextSnapshot.getContext().board().topology().cells().size(); ++i) {
                                if (!cs.isResolvedVerts(i)) {
                                    puzzleMove(i, 0, true, setType);
                                }
                            }
                            setType = SiteType.Vertex;
                            for (int i = 0; i < ContextSnapshot.getContext().board().topology().vertices().size(); ++i) {
                                if (!cs.isResolvedCell(i)) {
                                    puzzleMove(i, 0, true, setType);
                                }
                            }
                        }
                    }
                    SettingsVC.selectedLocation = new FullLocation(-1);
                }
            }
        }
    }
    
    public static boolean userMove(final Location fromInfo, final Location toInfo, final Context context) {
        final Moves legal = context.game().moves(context);
        final FastArrayList<Move> possibleMoves = new FastArrayList<>();
        for (final Move move : legal.moves()) {
            if (SettingsVC.selectedLocation.site() == -1) {
                return false;
            }
            final boolean match = MoveUtil.moveMatchesLocation(move, fromInfo, toInfo, context);
            if (!match) {
                continue;
            }
            boolean moveAlreadyAvailable = false;
            for (final Move m : possibleMoves) {
                if (m.getAllActions(context).equals(move.getAllActions(context))) {
                    moveAlreadyAvailable = true;
                }
            }
            if (moveAlreadyAvailable) {
                continue;
            }
            possibleMoves.add(move);
        }
        if (possibleMoves.size() > 1) {
            return handleMultiplePossibleMoves(possibleMoves, context);
        }
        if (possibleMoves.size() == 1) {
            applyHumanMoveToGame(possibleMoves.get(0));
            return true;
        }
        return false;
    }
    
    private static boolean handleMultiplePossibleMoves(final FastArrayList<Move> possibleMoves, final Context context) {
        SettingsVC.possibleToLocations.clear();
        SettingsManager.possibleConsequenceMoves.clear();
        SettingsVC.SelectingConsequenceMove = false;
        int minMoveLength = 9999;
        for (final Move m : possibleMoves) {
            if (m.getAllActions(context).size() < minMoveLength) {
                minMoveLength = m.getAllActions(context).size();
            }
        }
        int differentAction = -1;
        for (int i = 0; i < minMoveLength; ++i) {
            Action sameAction = null;
            boolean allSame = true;
            for (final Move j : possibleMoves) {
                if (sameAction == null) {
                    sameAction = j.getAllActions(context).get(i);
                }
                else {
                    if (sameAction.equals(j.getAllActions(context).get(i))) {
                        continue;
                    }
                    allSame = false;
                }
            }
            if (!allSame) {
                differentAction = i;
                break;
            }
        }
        if (differentAction == -1) {
            Manager.app.showPossibleMovesDialog(context, possibleMoves);
            return true;
        }
        final int moveLength = possibleMoves.get(0).getAllActions(context).size();
        boolean onlyDiceDifference = true;
        final ArrayList<Action> allSameActions = new ArrayList<>(context.game().moves(context).moves().get(0).actions());
        for (final Move j : context.game().moves(context).moves()) {
            boolean differentActionAboutDice = false;
            for (int k = 0; k < allSameActions.size(); ++k) {
                if (allSameActions.get(k) != j.actions().get(k)) {
                    differentActionAboutDice = true;
                }
                if (differentActionAboutDice) {
                    allSameActions.remove(k);
                }
            }
            for (int k = 0; k < allSameActions.size(); ++k) {
                if (!(allSameActions.get(k) instanceof ActionUpdateDice)) {
                    allSameActions.remove(k);
                }
            }
        }
        final HashMap<Integer, Integer> newStateValue = new HashMap<>();
        for (final Action action : allSameActions) {
            newStateValue.put(action.from(), action.state());
        }
        for (final Move l : possibleMoves) {
            if (l.getAllActions(context).size() != moveLength) {
                onlyDiceDifference = false;
                break;
            }
            for (int a = 0; a < moveLength; ++a) {
                final Action actionToCheckAgainst = possibleMoves.get(0).getAllActions(context).get(a);
                final Action action2 = l.getAllActions(context).get(a);
                if (action2 instanceof ActionUseDie && actionToCheckAgainst instanceof ActionUseDie) {
                    final ActionUseDie actionToCheckAgainst2 = (ActionUseDie)actionToCheckAgainst;
                    final ActionUseDie action3 = (ActionUseDie)action2;
                    if (context.state().mover() != context.state().prev()) {
                        if (newStateValue.get(action3.to()) != newStateValue.get(actionToCheckAgainst2.to())) {
                            onlyDiceDifference = false;
                            break;
                        }
                    }
                    else if (context.state().currentDice()[action3.indexHandDice()][action3.indexDie()] != context.state().currentDice()[actionToCheckAgainst2.indexHandDice()][actionToCheckAgainst2.indexDie()]) {
                        onlyDiceDifference = false;
                        break;
                    }
                }
                else if (!action2.equals(actionToCheckAgainst)) {
                    onlyDiceDifference = false;
                    break;
                }
            }
        }
        if (onlyDiceDifference) {
            applyHumanMoveToGame(possibleMoves.get(0));
            return true;
        }
        for (final Move l : possibleMoves) {
            SettingsVC.possibleToLocations.add(new FullLocation(l.getAllActions(context).get(differentAction).to(), l.getAllActions(context).get(differentAction).levelTo(), l.getAllActions(context).get(differentAction).toType()));
            SettingsManager.possibleConsequenceMoves.add(l);
            if (l.getAllActions(context).get(differentAction).to() < 0) {
                Manager.app.showPossibleMovesDialog(context, possibleMoves);
                return true;
            }
        }
        final ArrayList<Location> checkForDuplicates = new ArrayList<>();
        boolean duplicateFound = false;
        for (int i2 = 0; i2 < SettingsVC.possibleToLocations.size(); ++i2) {
            for (final Location location : checkForDuplicates) {
                if (location.equalsLoc(SettingsVC.possibleToLocations.get(i2))) {
                    duplicateFound = true;
                    break;
                }
            }
            if (duplicateFound) {
                Manager.app.showPossibleMovesDialog(context, possibleMoves);
                return true;
            }
            checkForDuplicates.add(SettingsVC.possibleToLocations.get(i2));
        }
        Action actionToApply = possibleMoves.get(0).getAllActions(context).get(0);
        final Move firstHalf = new Move(actionToApply);
        for (int i3 = 1; i3 < differentAction; ++i3) {
            actionToApply = possibleMoves.get(0).getAllActions(context).get(i3);
            firstHalf.actions().add(actionToApply);
        }
        final Context tempContext = new Context(context.currentInstanceContext());
        firstHalf.apply(tempContext, true);
        Manager.ref().setIntermediaryContext(tempContext);
        SettingsVC.SelectingConsequenceMove = true;
        Manager.app.setTemporaryMessage("Please select a consequence.");
        return false;
    }
    
    public static void puzzleMove(final int site, final int puzzleValue, final boolean leftClick, final SiteType type) {
        Action a = null;
        if (leftClick) {
            a = new ActionSet(type, site, puzzleValue);
        }
        else {
            a = new ActionToggle(type, site, puzzleValue);
        }
        final Move m = new Move(a);
        m.setDecision(true);
        applyHumanMoveToGame(m);
    }
    
    public static void applyConsequenceChosen(final Location location) {
        boolean moveMade = false;
        for (int i = 0; i < SettingsVC.possibleToLocations.size(); ++i) {
            if (SettingsVC.possibleToLocations.get(i).site() == location.site()) {
                applyHumanMoveToGame(SettingsManager.possibleConsequenceMoves.get(i));
                moveMade = true;
                break;
            }
        }
        if (!moveMade) {
            Manager.app.setVolatileMessage("That is not a valid move.");
        }
        SettingsVC.SelectingConsequenceMove = false;
        SettingsVC.possibleToLocations.clear();
        SettingsManager.possibleConsequenceMoves.clear();
        SettingsVC.selectedLocation = new FullLocation(-1);
    }
    
    public static void playerSelectMove(final int player) {
        final Context context = ContextSnapshot.getContext();
        final Moves legal = context.game().moves(context);
        Move playerSelectMove = null;
        for (final Move m : legal.moves()) {
            if (m.playerSelected() == player) {
                playerSelectMove = m;
                break;
            }
        }
        if (playerSelectMove != null) {
            applyHumanMoveToGame(playerSelectMove);
            Manager.app.addTextToStatusPanel("Player " + player + " has been selected.\n");
        }
    }
    
    public static void passMove(final int player) {
        final Context context = ContextSnapshot.getContext();
        final Moves legal = context.game().moves(context);
        Move passMove = null;
        for (final Move m : legal.moves()) {
            if (m.isPass()) {
                passMove = m;
                break;
            }
            if (m.containsNextInstance()) {
                passMove = m;
                break;
            }
        }
        if (passMove == null) {
            System.out.println("I am trying to pass when I have no moves");
            final ActionPass actionPass = new ActionPass();
            actionPass.setDecision(true);
            passMove = new Move(actionPass);
            passMove.setMover(player);
        }
        applyHumanMoveToGame(passMove);
    }
    
    public static void swapMove(final int player) {
        final Context context = ContextSnapshot.getContext();
        final Moves legal = context.game().moves(context);
        Move swapMove = null;
        for (final Move m : legal.moves()) {
            if (m.isSwap()) {
                swapMove = m;
                break;
            }
        }
        applyHumanMoveToGame(swapMove);
        Manager.app.addTextToStatusPanel("Player " + player + " has swapped.\n");
    }
    
    public static void applyHumanMoveToGame(final Move m) {
        Manager.ref().applyHumanMoveToGame(m);
    }
}
