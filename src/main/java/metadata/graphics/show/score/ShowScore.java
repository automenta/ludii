// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.show.score;

import annotations.Hide;
import annotations.Opt;
import metadata.graphics.GraphicsItem;
import metadata.graphics.util.WhenScoreType;

@Hide
public class ShowScore implements GraphicsItem
{
    private final WhenScoreType showScore;
    
    public ShowScore(@Opt final WhenScoreType showScore) {
        this.showScore = ((showScore == null) ? WhenScoreType.Always : showScore);
    }
    
    public WhenScoreType showScore() {
        return this.showScore;
    }
}
