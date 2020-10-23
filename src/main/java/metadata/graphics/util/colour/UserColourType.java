// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.util.colour;

import metadata.graphics.GraphicsItem;

import java.awt.*;

public enum UserColourType implements GraphicsItem
{
    White("White", 255, 255, 255), 
    Black("Black", 0, 0, 0), 
    Grey("Grey", 150, 150, 150), 
    LightGrey("Light Grey", 200, 200, 200), 
    VeryLightGrey("Very Light Grey", 230, 230, 230), 
    DarkGrey("Dark Grey", 100, 100, 100), 
    VeryDarkGrey("Very Dark Grey", 50, 50, 50), 
    Dark("Dark", 30, 30, 30), 
    Red("Red", 255, 0, 0), 
    Green("Green", 0, 200, 0), 
    Blue("Blue", 0, 127, 255), 
    Yellow("Yellow", 255, 245, 0), 
    Pink("Pink", 255, 0, 255), 
    Cyan("Cyan", 0, 255, 255), 
    Brown("Brown", 139, 69, 19), 
    DarkBrown("Dark Brown", 101, 67, 33), 
    Purple("Purple", 127, 0, 127), 
    Magenta("Magenta", 255, 0, 255), 
    Turquoise("Turquoise", 0, 127, 127), 
    Orange("Orange", 255, 165, 0), 
    DarkOrange("Dark Orange", 255, 140, 0), 
    LightRed("Light Red", 255, 127, 127), 
    DarkRed("Dark Red", 127, 0, 0), 
    LightGreen("Light Green", 127, 255, 127), 
    DarkGreen("Dark Green", 0, 127, 0), 
    LightBlue("Light Blue", 127, 191, 255), 
    VeryLightBlue("Very Light Blue", 205, 234, 237), 
    DarkBlue("Dark Blue", 0, 0, 127), 
    IceBlue("Ice Blue", 183, 226, 228), 
    Gold("Gold", 212, 175, 55), 
    Silver("Silver", 192, 192, 192), 
    Bronze("Bronze", 205, 127, 50), 
    GunMetal("GunMetal", 44, 53, 57), 
    HumanLight("Human Light", 204, 182, 140), 
    HumanDark("Human Dark", 108, 86, 60), 
    Cream("Cream", 255, 255, 230), 
    DeepPurple("Deep Purple", 127, 0, 127), 
    PinkFloyd("Pink Floyd", 255, 75, 150), 
    BlackSabbath("Black Sabbath", 0, 0, 32), 
    KingCrimson("King Crimson", 220, 20, 60), 
    MoodyBlues("Moody Blues", 0, 127, 255), 
    TangerineDream("Tangerine Dream", 242, 133, 0);
    
    private final String label;
    private final int r;
    private final int g;
    private final int b;
    
    UserColourType(final String label, final int r, final int g, final int b) {
        this.label = label;
        this.r = r;
        this.g = g;
        this.b = b;
    }
    
    public String label() {
        return this.label;
    }
    
    public int r() {
        return this.r;
    }
    
    public int g() {
        return this.g;
    }
    
    public int b() {
        return this.b;
    }
    
    public Color colour() {
        return new Color(this.r, this.g, this.b);
    }
    
    public static UserColourType find(final String key) {
        for (final UserColourType uc : values()) {
            if (uc.label.equals(key)) {
                return uc;
            }
        }
        return null;
    }
}
