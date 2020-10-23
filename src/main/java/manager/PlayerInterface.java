// 
// Decompiled by Procyon v0.5.36
// 

package manager;

import bridge.PlatformGraphics;
import game.Game;
import main.collections.FastArrayList;
import org.json.JSONObject;
import util.Context;
import util.Move;

import java.util.List;

public interface PlayerInterface
{
    void loadGameFromName(final String p0, final List<String> p1, final boolean p2);
    
    void loadGameFromName(final String p0, final boolean p1);
    
    JSONObject getNameFromJar();
    
    void addTextToStatusPanel(final String p0);
    
    void addTextToAnalysisPanel(final String p0);
    
    void showPuzzleDialog(final int p0);
    
    void showPossibleMovesDialog(final Context p0, final FastArrayList<Move> p1);
    
    void selectAnalysisTab();
    
    void repaint();
    
    void reportForfeit(final int p0);
    
    void reportTimeout(final int p0);
    
    void reportDrawAgreed();
    
    void updateFrameTitle();
    
    void updateTabs(final Context p0);
    
    void playSound(final String p0);
    
    void gameOverTasks();
    
    void restartGame(final boolean p0);
    
    void cleanUpAfterLoading(final String p0, final Game p1, final boolean p2);
    
    PlatformGraphics platformGraphics();
    
    void repaintTimerForPlayer(final int p0);
    
    void setTemporaryMessage(final String p0);
    
    void setVolatileMessage(final String p0);
}
