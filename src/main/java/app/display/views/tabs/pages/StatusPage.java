// 
// Decompiled by Procyon v0.5.36
// 

package app.display.views.tabs.pages;

import app.DesktopApp;
import app.display.MainWindow;
import app.display.views.tabs.TabPage;
import app.display.views.tabs.TabView;
import bridge.Bridge;
import game.Game;
import game.equipment.component.Component;
import game.functions.booleans.is.component.IsThreatened;
import game.functions.ints.IntConstant;
import manager.Manager;
import manager.network.DatabaseFunctions;
import manager.network.SettingsNetwork;
import topology.TopologyElement;
import util.Context;
import util.Move;
import util.Trial;
import util.action.Action;

import java.awt.*;

public class StatusPage extends TabPage
{
    public StatusPage(final Rectangle rect, final String title, final String text, final int pageIndex, final TabView parent) {
        super(rect, title, text, pageIndex, parent);
    }
    
    @Override
    public void updatePage(final Context context) {
        this.addText(getStatusStringToDisplay(context, context.trial().numMoves() - 1));
        DesktopApp.savedStatusTabString = this.text();
    }
    
    private static String getStatusStringToDisplay(final Context context, final int moveNumber) {
        Trial longestTrial = context.trial();
        if (Manager.savedTrial() != null) {
            longestTrial = Manager.savedTrial();
        }
        final Move lastMove = (moveNumber >= 0) ? longestTrial.moves().get(moveNumber) : null;
        int nextMover = context.state().mover();
        if (longestTrial.moves().size() > moveNumber + 1) {
            nextMover = longestTrial.moves().get(moveNumber + 1).mover();
        }
        String statusString = "";
        final int nbPlayers = context.game().players().count();
        final Game game = context.game();
        if (longestTrial.over() && moveNumber == longestTrial.numMoves() - 1) {
            final int winner = longestTrial.status().winner();
            String str = "";
            if (winner == 0) {
                final double[] ranks = longestTrial.ranking();
                boolean allLose = true;
                boolean allWin = true;
                for (int i = 1; i < ranks.length; ++i) {
                    if (ranks[i] != 1.0) {
                        allWin = false;
                    }
                    if (ranks[i] != game.players().count()) {
                        allLose = false;
                    }
                }
                if (allWin) {
                    str += "Congratulations, puzzle solved!\n";
                }
                else if (allLose) {
                    str += "Game Over, you lose!\n";
                }
                else if (nbPlayers == 1) {
                    str += "Game Over, you lose!\n";
                }
                else {
                    str += "Game won by no one.\n";
                }
                if (game.checkMaxTurns(context)) {
                    str += "Maximum number of moves reached.\n";
                }
            }
            else if (winner == -1) {
                str += "Game aborted.\n";
            }
            else if (winner > game.players().count()) {
                str += "Game won by everyone.\n";
            }
            else if (nbPlayers == 1) {
                str += "Congratulations, puzzle solved!\n";
            }
            else if (game.requiresTeams()) {
                str = str + "Game won by team " + context.state().getTeam(winner) + ".\n";
            }
            else {
                str = str + "Game won by " + context.getPlayerName(winner) + ".\n";
            }
            if (game.players().count() >= 3) {
                for (int j = 1; j <= game.players().count(); ++j) {
                    boolean anyPlayers = false;
                    str = str + "Rank " + j + ": ";
                    for (int k = 1; k <= game.players().count(); ++k) {
                        final double rank = longestTrial.ranking()[k];
                        if (Math.floor(rank) == j) {
                            if (!anyPlayers) {
                                str += context.getPlayerName(k);
                                anyPlayers = true;
                            }
                            else {
                                str = str + ", " + context.getPlayerName(k);
                            }
                        }
                    }
                    if (!anyPlayers) {
                        str += "No one\n";
                    }
                    else {
                        str += "\n";
                    }
                }
            }
            statusString += str;
        }
        else if (game.players().count() > 1) {
            final int indexMover = context.state().mover();
            for (final TopologyElement element : context.board().topology().getAllGraphElements()) {
                final int indexPiece = context.containerState(0).what(element.index(), element.elementType());
                if (indexPiece != 0) {
                    final Component component = context.components()[indexPiece];
                    if (!game.metadata().graphics().checkUsed(indexMover, component.name(), context)) {
                        continue;
                    }
                    boolean check = false;
                    final IsThreatened threat = new IsThreatened(new IntConstant(indexPiece), element.elementType(), new IntConstant(element.index()), null, null);
                    threat.preprocess(context.game());
                    check = threat.eval(context);
                    if (!check) {
                        continue;
                    }
                    EventQueue.invokeLater(() -> MainWindow.setTemporaryMessage("Check."));
                }
            }
            if (lastMove != null) {
                for (final Action action : lastMove.actions()) {
                    if (action.message() != null && lastMove.who() == Bridge.graphicsRenderer().getSingleHumanMover(lastMove.who(), context)) {
                        statusString = statusString + action.message() + ".\n";
                    }
                }
            }
            for (int l = 1; l <= game.players().count(); ++l) {
                if (!context.active(l) && SettingsNetwork.activePlayers[l]) {
                    SettingsNetwork.activePlayers[l] = false;
                    if (SettingsNetwork.getActiveGameId() != 0) {
                        final double[] tempRanking = new double[longestTrial.ranking().length];
                        for (int m = 0; m < longestTrial.ranking().length; ++m) {
                            tempRanking[m] = longestTrial.ranking()[m];
                        }
                        for (int player = 1; player < longestTrial.ranking().length; ++player) {
                            if (longestTrial.ranking()[player] == 0.0) {
                                tempRanking[player] = 1000.0;
                            }
                        }
                        DatabaseFunctions.sendGameRankings(tempRanking);
                    }
                }
            }
            if (nextMover < game.players().size()) {
                final String str = DesktopApp.aiSelected()[context.state().playerToAgent(nextMover)].name() + " to move.\n";
                statusString += str;
            }
        }
        return statusString;
    }
    
    @Override
    public void reset() {
        this.clear();
        this.addText(DesktopApp.aiSelected()[1].name() + " to move.\n");
    }
}
