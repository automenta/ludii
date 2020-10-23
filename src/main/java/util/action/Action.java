// 
// Decompiled by Procyon v0.5.36
// 

package util.action;

import annotations.Hide;
import game.types.board.SiteType;
import util.Context;

import java.io.Serializable;

@Hide
public interface Action extends Serializable
{
    Action apply(final Context context, final boolean store);
    
    boolean isPass();
    
    boolean isForfeit();
    
    boolean isSwap();
    
    int playerSelected();
    
    boolean containsNextInstance();
    
    boolean matchesUserMove(final int siteA, final int levelA, final SiteType graphElementTypeA, final int siteB, final int levelB, final SiteType graphElementTypeB);
    
    int from();
    
    int levelFrom();
    
    int to();
    
    int levelTo();
    
    int who();
    
    int what();
    
    int state();
    
    int rotation();
    
    int count();
    
    String proposition();
    
    String vote();
    
    String message();
    
    boolean isStacking();
    
    boolean[] hidden();
    
    boolean isDecision();
    
    ActionType actionType();
    
    SiteType fromType();
    
    SiteType toType();
    
    void setDecision(final boolean decision);
    
    Action withDecision(final boolean decision);
    
    String toTrialFormat(final Context context);
    
    String toMoveFormat(final Context context);
    
    String toEnglishString(final Context context);
    
    String getDescription();
    
    String toTurnFormat(final Context context);
    
    void setLevelFrom(final int levelA);
    
    void setLevelTo(final int levelB);
    
    boolean isOtherMove();
    
    static String extractData(final String detailedString, final String data) {
        final int fromIndex = detailedString.indexOf(data + "=");
        if (fromIndex == -1) {
            return "";
        }
        final String beginData = detailedString.substring(fromIndex);
        int toIndex = beginData.indexOf(',');
        if (data.equals("masked") || data.equals("invisible")) {
            final String afterData = beginData.substring(beginData.indexOf('=') + 1);
            int toSpecialIndex = afterData.indexOf('=');
            if (toSpecialIndex == -1) {
                return afterData.substring(0, afterData.length() - 1);
            }
            while (afterData.charAt(toSpecialIndex) != ',') {
                --toSpecialIndex;
            }
            return afterData.substring(0, toSpecialIndex);
        }
        else {
            if (toIndex == -1) {
                toIndex = beginData.indexOf(']');
            }
            if (toIndex == -1) {
                return "";
            }
            return beginData.substring(beginData.indexOf('=') + 1, toIndex);
        }
    }
}
