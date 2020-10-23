// 
// Decompiled by Procyon v0.5.36
// 

package view.component.custom.pieceTypes;

public enum XiangqiType
{
    KING("\u5468", "Zhou", "King"), 
    WHITEGENERAL("\u79e6", "Qin Jiang", "General White"), 
    REDGENERAL("\u695a", "Chu Jiang", "General Red"), 
    ORANGEGENERAL("\u97d3", "Han Jiang", "General Orange"), 
    BLUEGENERAL("\u9f4a", "Qi Jiang", "General Blue"), 
    GREENGENERAL("\u9b4f", "Wei Jiang", "General Green"), 
    BLACKGENERAL("\u8d99", "Yan Jiang", "General Grey"), 
    PURPLEGENERAL("\u71d5", "Zhao Jiang", "General Magenta"), 
    DEPUTYGENERAL("\u504f", "Pian", "Deputy General"), 
    OFFICER("\u88e8", "Bai", "Officer"), 
    DIPLOMAT("\u884c\u4eba", "Xing ren", "Diplomat"), 
    CATAPULT("\u7832", "Pao", "Catapult"), 
    ARCHER("\u5f13", "Gong", "Archer"), 
    CROSSBOW("\u5f29", "Nu", "Crossbow"), 
    KNIFE("\u5200", "Dao", "Knife"), 
    BROADSWORD("\u528d", "Jian", "Broadsword"), 
    KNIGHT("\u9a0e", "Qi", "Knight"), 
    FIRE("\u706b", "Huo", "Fire"), 
    FLAG("\u65d7", "Qi", "Flag"), 
    OCEAN("\u6d77", "Hai", "Ocean"), 
    MOUNTAIN("\u5c71", "Shan", "Mountain"), 
    CITY("\u57ce", "Cheng", "City");
    
    private final String kanji;
    private final String romaji;
    private final String englishName;
    
    XiangqiType(final String kanji, final String romaji, final String englishName) {
        this.kanji = kanji;
        this.romaji = romaji;
        this.englishName = englishName;
    }
    
    public String kanji() {
        return this.kanji;
    }
    
    public String romaji() {
        return this.romaji;
    }
    
    public String englishName() {
        return this.englishName;
    }
}
