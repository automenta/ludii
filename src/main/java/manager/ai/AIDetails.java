// 
// Decompiled by Procyon v0.5.36
// 

package manager.ai;

import manager.Manager;
import org.json.JSONObject;
import util.AI;
import utils.AIFactory;

import java.util.ArrayList;
import java.util.List;

public class AIDetails
{
    private JSONObject object;
    private AI aI;
    private String name;
    private double thinkTime;
    private AIMenuName menuItemName;
    
    public AIDetails(final JSONObject object, final int playerId, final AIMenuName menuItemName) {
        this.object = object;
        if (object != null) {
            final JSONObject aiObj = object.getJSONObject("AI");
            final String algName = aiObj.getString("algorithm");
            if (!algName.equalsIgnoreCase("Human")) {
                this.aI = AIFactory.fromJson(object);
            }
        }
        else {
            this.object = new JSONObject().put("AI", new JSONObject().put("algorithm", "Human"));
        }
        try {
            this.name = Manager.aiSelected()[playerId].name();
        }
        catch (Exception e) {
            this.name = "Player " + playerId;
        }
        try {
            this.thinkTime = Manager.aiSelected()[playerId].thinkTime();
        }
        catch (Exception e) {
            this.thinkTime = 1.0;
        }
        this.menuItemName = menuItemName;
    }
    
    public String name() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public AIMenuName menuItemName() {
        return this.menuItemName;
    }
    
    public void setMenuItemName(final AIMenuName menuItemName) {
        this.menuItemName = menuItemName;
    }
    
    public JSONObject object() {
        return this.object;
    }
    
    public AI ai() {
        return this.aI;
    }
    
    public double thinkTime() {
        return this.thinkTime;
    }
    
    public void setThinkTime(final double thinkTime) {
        this.thinkTime = thinkTime;
    }
    
    public static AIDetails getCopyOf(final AIDetails oldAIDetails, final int playerId) {
        final AIDetails newAIDetails = new AIDetails(oldAIDetails.object(), playerId, oldAIDetails.menuItemName);
        newAIDetails.setName(oldAIDetails.name());
        newAIDetails.setThinkTime(oldAIDetails.thinkTime());
        newAIDetails.setName(oldAIDetails.name());
        return newAIDetails;
    }
    
    public static List<AI> convertToAIList(final AIDetails[] details) {
        final List<AI> aiList = new ArrayList<>();
        for (final AIDetails detail : details) {
            aiList.add(detail.ai());
        }
        return aiList;
    }
    
    public static String[] convertToNameArray(final AIDetails[] details) {
        final String[] nameArray = new String[details.length];
        for (int i = 0; i < details.length; ++i) {
            nameArray[i] = details[i].name();
        }
        return nameArray;
    }
    
    public static double[] convertToThinkTimeArray(final AIDetails[] details) {
        final double[] timeArray = new double[details.length];
        for (int i = 0; i < details.length; ++i) {
            timeArray[i] = details[i].thinkTime();
        }
        return timeArray;
    }
    
    public boolean equals(final AIDetails aiDetails) {
        return aiDetails.object.equals(this.object) && aiDetails.name.equals(this.name) && aiDetails.menuItemName.equals(this.menuItemName);
    }
}
