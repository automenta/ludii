// 
// Decompiled by Procyon v0.5.36
// 

package view.component.custom.pieceTypes;

public enum NativeAmericanDiceType
{
    Patol1("PatolDice", "blank on one side, two lines on other"), 
    Patol2("Dice2", "blank on one side, three lines on other"), 
    Notched("NotchedDice", "blank on one side, dots on the other"), 
    SetDilth("SetDilthDice", "blank on one side, two lines on other near middle"), 
    Nebakuthana1("NebakuthanaDice1", "blank on one side, many lines on other"), 
    Nebakuthana2("NebakuthanaDice2", "blank on one side, cross on other"), 
    Nebakuthana3("NebakuthanaDice3", "blank on one side, diamond on other"), 
    Nebakuthana4("NebakuthanaDice4", "line and dots on one side, star pattern of lines on other"), 
    Kints1("KintsDice1", "blank on one side, zigzag on other"), 
    Kints2("KintsDice2", "blank on one side, four lines on other"), 
    Kints3("KintsDice3", "blank on one side, two triangles on other"), 
    Kints4("KintsDice4", "blank on one side, cross on other");
    
    private final String englishName;
    private final String description;
    
    NativeAmericanDiceType(final String englishName, final String description) {
        this.englishName = englishName;
        this.description = description;
    }
    
    public String englishName() {
        return this.englishName;
    }
    
    public String description() {
        return this.description;
    }
}
