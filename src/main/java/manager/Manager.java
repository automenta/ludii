// 
// Decompiled by Procyon v0.5.36
// 

package manager;

import manager.ai.AIDetails;
import manager.referee.Referee;
import org.apache.commons.rng.core.RandomProviderDefaultState;
import tournament.Tournament;
import util.AI;
import util.Trial;

import java.util.ArrayList;
import java.util.List;

public final class Manager
{
    public static PlayerInterface app;
    private static Referee ref;
    public static AIDetails[] aiSelected;
    public static Tournament tournament;
    private static RandomProviderDefaultState currGameStartRngState;
    private static List<AI> liveAIs;
    private static String savedLudName;
    private static Trial savedTrial;
    public static ArrayList<Trial> instanceTrialsSoFar;
    public static int currentGameIndexForMatch;
    
    public Manager(final PlayerInterface playerInterface) {
        Manager.app = playerInterface;
        Manager.ref = new Referee();
    }
    
    public static Referee ref() {
        return Manager.ref;
    }
    
    public static AIDetails[] aiSelected() {
        return Manager.aiSelected;
    }
    
    public static Tournament tournament() {
        return Manager.tournament;
    }
    
    public static void updateCurrentGameRngInternalState() {
        setCurrGameStartRngState((RandomProviderDefaultState)ref().context().rng().saveState());
    }
    
    public static RandomProviderDefaultState currGameStartRngState() {
        return Manager.currGameStartRngState;
    }
    
    public static void setCurrGameStartRngState(final RandomProviderDefaultState newCurrGameStartRngState) {
        Manager.currGameStartRngState = newCurrGameStartRngState;
    }
    
    public static List<AI> liveAIs() {
        return Manager.liveAIs;
    }
    
    public static void setLiveAIs(final List<AI> ais) {
        Manager.liveAIs = ais;
    }
    
    public static String savedLudName() {
        return Manager.savedLudName;
    }
    
    public static void setSavedLudName(final String savedLudName) {
        Manager.savedLudName = savedLudName;
    }
    
    public static int currentGameIndexForMatch() {
        return Manager.currentGameIndexForMatch;
    }
    
    public static void setCurrentGameIndexForMatch(final int currentGameIndexForMatch) {
        Manager.currentGameIndexForMatch = currentGameIndexForMatch;
    }
    
    public static ArrayList<Trial> instanceTrialsSoFar() {
        return Manager.instanceTrialsSoFar;
    }
    
    public static void setInstanceTrialsSoFar(final ArrayList<Trial> instanceTrialsSoFar) {
        Manager.instanceTrialsSoFar = instanceTrialsSoFar;
    }
    
    public static void setSavedTrial(final Trial i) {
        Manager.savedTrial = i;
    }
    
    public static Trial savedTrial() {
        return Manager.savedTrial;
    }
    
    static {
        Manager.aiSelected = new AIDetails[17];
        Manager.currGameStartRngState = null;
        Manager.liveAIs = null;
        Manager.savedTrial = null;
        Manager.instanceTrialsSoFar = new ArrayList<>();
        Manager.currentGameIndexForMatch = 0;
    }
}
