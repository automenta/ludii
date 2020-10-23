// 
// Decompiled by Procyon v0.5.36
// 

package manager.ai;

import manager.Manager;
import manager.network.SettingsNetwork;
import manager.utils.ContextSnapshot;
import manager.utils.SettingsManager;
import org.json.JSONObject;
import util.AI;
import util.Context;
import util.model.SimultaneousMove;
import utils.AIUtils;

import java.awt.*;

public class AIUtil
{
    public static void cycleAgents() {
        SettingsManager.agentsPaused = true;
        final AIDetails player1Details = AIDetails.getCopyOf(Manager.aiSelected()[1], 1);
        for (int i = 2; i <= ContextSnapshot.getContext().game().players().count(); ++i) {
            Manager.aiSelected()[i - 1] = AIDetails.getCopyOf(Manager.aiSelected()[i], i);
        }
        Manager.aiSelected()[ContextSnapshot.getContext().game().players().count()] = player1Details;
        SettingsNetwork.backupAiPlayers();
        Manager.app.updateTabs(ContextSnapshot.getContext());
    }
    
    public static void updateSelectedAI(final JSONObject inJSON, final int playerNum, final AIMenuName aImenuName) {
        AIMenuName menuName = aImenuName;
        JSONObject json = inJSON;
        final JSONObject aiObj = json.getJSONObject("AI");
        final String algName = aiObj.getString("algorithm");
        SettingsManager.canSendToDatabase = false;
        if (algName.equals("Human")) {
            Manager.aiSelected[playerNum] = new AIDetails(null, playerNum, AIMenuName.Human);
            return;
        }
        if (algName.equals("From JAR") && (!aiObj.has("JAR File") || !aiObj.has("Class Name"))) {
            json = Manager.app.getNameFromJar();
            if (json == null) {
                return;
            }
            menuName = AIMenuName.FromJAR;
        }
        if (Manager.aiSelected[playerNum].ai() != null) {
            Manager.aiSelected[playerNum].ai().closeAI();
        }
        Manager.aiSelected[playerNum] = new AIDetails(json, playerNum, menuName);
        SettingsNetwork.backupAiPlayers();
        calculateAgentPaused();
    }
    
    public static void calculateAgentPaused() {
        if (SettingsNetwork.getActiveGameId() != 0) {
            SettingsManager.agentsPaused = true;
        }
        else if (Manager.aiSelected[Manager.ref().context().state().mover()].ai() != null) {
            SettingsManager.agentsPaused = true;
        }
        else if (Manager.ref().context().model() instanceof SimultaneousMove) {
            SettingsManager.agentsPaused = true;
        }
        else SettingsManager.agentsPaused = Manager.ref().context().game().players().count() == 0;
    }
    
    public static void checkIfAgentsAllowed(final Context context) {
        for (int p = 1; p < Manager.aiSelected.length; ++p) {
            if (Manager.aiSelected[p].ai() != null) {
                if (!Manager.aiSelected[p].ai().supportsGame(context.game())) {
                    final AI oldAI = Manager.aiSelected[p].ai();
                    final AI newAI = AIUtils.defaultAiForGame(context.game());
                    final JSONObject json = new JSONObject().put("AI", new JSONObject().put("algorithm", newAI.friendlyName));
                    Manager.aiSelected[p] = new AIDetails(json, p, AIMenuName.LudiiAI);
                    EventQueue.invokeLater(() -> Manager.app.addTextToStatusPanel(oldAI.friendlyName + " does not support this game. Switching to default AI for this game: " + newAI.friendlyName + ".\n"));
                }
                if (p <= context.game().players().count()) {
                    Manager.aiSelected[p].ai().initIfNeeded(context.game(), p);
                }
            }
        }
        SettingsNetwork.backupAiPlayers();
    }
}
