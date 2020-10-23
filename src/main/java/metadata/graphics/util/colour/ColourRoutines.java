// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.util.colour;

import java.awt.*;

public class ColourRoutines
{
    public static Color getSpecifiedColour(final String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        Color colour;
        if (value.charAt(0) == '#') {
            try {
                colour = Color.decode(value);
            }
            catch (Exception e) {
                colour = new Color(255, 255, 255);
            }
        }
        else if (value.length() > 4 && value.startsWith("RGBA")) {
            try {
                colour = new Color(Integer.parseInt(value.split(",")[0].replaceAll("RGBA", "").replaceAll("\\(", "").replaceAll("\\)", "")), Integer.parseInt(value.split(",")[1].replaceAll("RGBA", "").replaceAll("\\(", "").replaceAll("\\)", "")), Integer.parseInt(value.split(",")[2].replaceAll("RGBA", "").replaceAll("\\(", "").replaceAll("\\)", "")), Integer.parseInt(value.split(",")[3].replaceAll("RGBA", "").replaceAll("\\(", "").replaceAll("\\)", "")));
            }
            catch (Exception e) {
                colour = new Color(255, 255, 255);
            }
        }
        else if (value.length() > 3 && value.startsWith("RGB")) {
            try {
                colour = new Color(Integer.parseInt(value.split(",")[0].replaceAll("RGB", "").replaceAll("\\(", "").replaceAll("\\)", "")), Integer.parseInt(value.split(",")[1].replaceAll("RGB", "").replaceAll("\\(", "").replaceAll("\\)", "")), Integer.parseInt(value.split(",")[2].replaceAll("RGB", "").replaceAll("\\(", "").replaceAll("\\)", "")));
            }
            catch (Exception e) {
                colour = new Color(255, 255, 255);
            }
        }
        else {
            final UserColourType userColour = UserColourType.find(value);
            colour = ((userColour == null) ? new Color(255, 255, 255) : new Color(userColour.r(), userColour.g(), userColour.b()));
        }
        return colour;
    }
}
