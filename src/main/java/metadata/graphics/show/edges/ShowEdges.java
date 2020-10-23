// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.show.edges;

import annotations.Hide;
import annotations.Name;
import annotations.Opt;
import game.types.board.RelationType;
import metadata.graphics.GraphicsItem;
import metadata.graphics.util.EdgeType;
import metadata.graphics.util.LineStyle;
import metadata.graphics.util.colour.Colour;
import metadata.graphics.util.colour.UserColourType;

@Hide
public class ShowEdges implements GraphicsItem
{
    private final EdgeType type;
    private final RelationType relationType;
    private final Boolean connection;
    private final LineStyle style;
    private final Colour colour;
    
    public ShowEdges(@Opt final EdgeType type, @Opt final RelationType relationType, @Opt @Name final Boolean connection, @Opt final LineStyle style, @Opt final Colour colour) {
        this.type = ((type != null) ? type : EdgeType.All);
        this.relationType = ((relationType != null) ? relationType : RelationType.All);
        this.connection = ((connection == null) ? Boolean.valueOf(false) : connection);
        this.style = ((style != null) ? style : LineStyle.ThinDotted);
        this.colour = ((colour != null) ? colour : new Colour(UserColourType.LightGrey));
    }
    
    public EdgeType type() {
        return this.type;
    }
    
    public RelationType relationType() {
        return this.relationType;
    }
    
    public Boolean connection() {
        return this.connection;
    }
    
    public LineStyle style() {
        return this.style;
    }
    
    public Colour colour() {
        return this.colour;
    }
}
