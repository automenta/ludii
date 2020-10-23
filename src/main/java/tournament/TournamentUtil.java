// 
// Decompiled by Procyon v0.5.36
// 

package tournament;

import manager.Manager;
import util.Context;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class TournamentUtil
{
    public static void saveTournamentResults(final Context context) {
        if (Manager.tournament() != null) {
            System.out.println("SAVING RESULTS");
            Manager.tournament().storeResults(context.game(), context.trial().ranking());
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    EventQueue.invokeLater(() -> {
                        System.out.println("LOADING NEXT GAME");
                        Manager.tournament().startNextTournamentGame();
                    });
                }
            }, 5000L);
        }
    }
}
