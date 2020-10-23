// 
// Decompiled by Procyon v0.5.36
// 

package app.utils;

import manager.Manager;
import manager.utils.SettingsManager;
import metrics.Metric;
import org.json.JSONObject;
import supplementary.experiments.EvalGamesThread;
import util.AI;
import utils.AIFactory;

import java.util.ArrayList;
import java.util.List;

public class AIDesktop
{
    public static void AIEvalution(final int numberTrials, final int maxTurns, final double thinkTime, final String AIName, final List<Metric> metricsToEvaluate, final ArrayList<Double> weights) {
        final List<AI> AIList = new ArrayList<>();
        for (int i = 0; i < Manager.aiSelected().length; ++i) {
            final JSONObject json = new JSONObject().put("AI", new JSONObject().put("algorithm", AIName));
            AIList.add(AIFactory.fromJson(json));
        }
        final List<String> options = SettingsManager.userSelections.selectedOptionStrings();
        final EvalGamesThread evalThread = EvalGamesThread.construct(Manager.ref().context().game().name(), options, AIList, numberTrials, thinkTime, maxTurns, metricsToEvaluate, weights);
        evalThread.setDaemon(true);
        evalThread.start();
    }
}
