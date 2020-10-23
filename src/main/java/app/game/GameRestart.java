// 
// Decompiled by Procyon v0.5.36
// 

package app.game;

import app.DesktopApp;
import app.display.MainWindow;
import app.display.util.GraphicsCache;
import manager.Manager;
import manager.ai.AIUtil;
import manager.game.GameSetup;
import manager.network.SettingsNetwork;
import manager.referee.Referee;
import manager.utils.AnimationUtil;
import manager.utils.ContextSnapshot;
import manager.utils.SettingsManager;
import util.Context;
import util.SettingsVC;
import util.Trial;
import util.locations.FullLocation;

import java.awt.*;
import java.util.ArrayList;

public class GameRestart
{
    public static void restartGame(final boolean fromServer) {
        final Referee ref = Manager.ref();
        resetGameVariables(true);
        if (fromServer) {
            Manager.updateCurrentGameRngInternalState();
        }
        final Context context = new Context(ref.context().game(), new Trial(ref.context().game()));
        ref.setContext(context);
        if (!fromServer) {
            Manager.updateCurrentGameRngInternalState();
        }
        else {
            context.rng().restoreState(Manager.currGameStartRngState());
        }
        ref.context().game().start(context);
        for (int p = 1; p < Manager.aiSelected.length; ++p) {
            if (Manager.aiSelected[p].ai() != null) {
                Manager.aiSelected[p].ai().closeAI();
            }
            if (Manager.aiSelected[p].ai() != null) {
                Manager.aiSelected[p].ai().initIfNeeded(ref.context().game(), p);
            }
        }
        if (ref.context().isAMatch()) {
            GraphicsCache.clearAllCachedImages();
            DesktopApp.setCurrentGameIndexForMatch(0);
            DesktopApp.setInstanceTrialsSoFar(new ArrayList<>());
            Manager.app.updateFrameTitle();
            GameSetup.setMVC();
        }
        resetUIVariables();
        DesktopApp.playerApp().addTextToStatusPanel("\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\n");
        DesktopApp.playerApp().addTextToStatusPanel("Game Restarted.\n");
        EventQueue.invokeLater(() -> Manager.app.updateTabs(ContextSnapshot.getContext()));
    }
    
    public static void clearBoard() {
        final Context context = Manager.ref().context();
        final Trial trial = context.trial();
        context.rng().restoreState(Manager.currGameStartRngState());
        context.reset();
        context.state().initialise(context.currentInstanceContext().game());
        context.game().start(context);
        trial.setStatus(null);
        if (context.isAMatch()) {
            GraphicsCache.clearAllCachedImages();
            DesktopApp.setCurrentGameIndexForMatch(0);
            DesktopApp.setInstanceTrialsSoFar(new ArrayList<>());
            Manager.app.updateFrameTitle();
            GameSetup.setMVC();
        }
        resetUIVariables();
    }
    
    public static void resetUIVariables() {
        MainWindow.setTemporaryMessage("");
        MainWindow.tabPanel().resetTabs();
        SettingsVC.selectedLocation = new FullLocation(-1);
        MainWindow.currentWalkExtra = 0;
        SettingsNetwork.resetNetworkPlayers();
        DesktopApp.frame().setContentPane(DesktopApp.view());
        AnimationUtil.resetAnimationValues();
        EventQueue.invokeLater(() -> Manager.app.repaint());
    }
    
    public static void resetGameVariables(final boolean resetSavedTrial) {
        DesktopApp.view().createPanels();
        MainWindow.tabPanel().resetTabs();
        SettingsManager.canSendToDatabase = true;
        Manager.ref().interruptAI();
        if (resetSavedTrial) {
            DesktopApp.setSavedTrial(null);
        }
        AIUtil.calculateAgentPaused();
        DesktopApp.setTrialContainsSandbox(SettingsVC.sandboxMode);
    }
}
