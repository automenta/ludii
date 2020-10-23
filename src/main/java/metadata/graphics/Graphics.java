// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics;

import annotations.Or;
import game.equipment.container.Container;
import game.types.board.RelationType;
import game.types.board.ShapeType;
import game.types.board.SiteType;
import game.types.component.SuitType;
import main.StringRoutines;
import metadata.graphics.board.Boolean.BoardCheckered;
import metadata.graphics.board.colour.BoardColour;
import metadata.graphics.board.ground.BoardBackground;
import metadata.graphics.board.ground.BoardForeground;
import metadata.graphics.board.shape.BoardShape;
import metadata.graphics.board.style.BoardStyle;
import metadata.graphics.board.styleThickness.BoardStyleThickness;
import metadata.graphics.no.Boolean.*;
import metadata.graphics.others.AdversarialPuzzle;
import metadata.graphics.others.HintType;
import metadata.graphics.others.StackType;
import metadata.graphics.others.SuitRanking;
import metadata.graphics.piece.colour.PieceColour;
import metadata.graphics.piece.families.PieceFamilies;
import metadata.graphics.piece.ground.PieceBackground;
import metadata.graphics.piece.ground.PieceForeground;
import metadata.graphics.piece.name.PieceAddStateToName;
import metadata.graphics.piece.name.PieceExtendName;
import metadata.graphics.piece.name.PieceRename;
import metadata.graphics.piece.reflect.PieceReflect;
import metadata.graphics.piece.rotate.PieceRotate;
import metadata.graphics.piece.scale.PieceScale;
import metadata.graphics.piece.scale.PieceScaleByValue;
import metadata.graphics.piece.style.PieceStyle;
import metadata.graphics.player.colour.PlayerColour;
import metadata.graphics.region.colour.RegionColour;
import metadata.graphics.show.Boolean.*;
import metadata.graphics.show.check.ShowCheck;
import metadata.graphics.show.component.ShowPieceState;
import metadata.graphics.show.component.ShowPieceValue;
import metadata.graphics.show.edges.ShowEdges;
import metadata.graphics.show.line.ShowLine;
import metadata.graphics.show.score.ShowScore;
import metadata.graphics.show.sites.ShowSitesAsHoles;
import metadata.graphics.show.sites.ShowSitesShape;
import metadata.graphics.show.symbol.ShowSymbol;
import metadata.graphics.util.*;
import metadata.graphics.util.colour.Colour;
import util.Context;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Graphics implements Serializable
{
    private static final long serialVersionUID = 1L;
    final List<GraphicsItem> items;
    String errorReport;
    
    public Graphics(@Or final GraphicsItem item, @Or final GraphicsItem[] items) {
        this.items = new ArrayList<>();
        this.errorReport = "";
        int numNonNull = 0;
        if (item != null) {
            ++numNonNull;
        }
        if (items != null) {
            ++numNonNull;
        }
        if (numNonNull > 1) {
            throw new IllegalArgumentException("Only one of @Or should be different to null");
        }
        if (items != null) {
            this.items.addAll(Arrays.asList(items));
        }
        else {
            this.items.add(item);
        }
    }
    
    public boolean addStateToName(final int playerIndex, final String pieceName, final Context context, final int state) {
        for (final GraphicsItem graphicsItem : this.items) {
            if (graphicsItem instanceof PieceAddStateToName && (((PieceAddStateToName)graphicsItem).roleType() == null || MetadataFunctions.getRealOwner(context, ((PieceAddStateToName)graphicsItem).roleType()) == playerIndex) && (((PieceAddStateToName)graphicsItem).state() == null || ((PieceAddStateToName)graphicsItem).state() == state) && (((PieceAddStateToName)graphicsItem).piece() == null || ((PieceAddStateToName)graphicsItem).piece().equals(pieceName) || ((PieceAddStateToName)graphicsItem).piece().equals(StringRoutines.removeTrailingNumbers(pieceName)))) {
                return true;
            }
        }
        return false;
    }
    
    public ComponentStyleType componentStyle(final int playerIndex, final String pieceName, final Context context) {
        for (final GraphicsItem graphicsItem : this.items) {
            if (graphicsItem instanceof PieceStyle && (((PieceStyle)graphicsItem).roleType() == null || MetadataFunctions.getRealOwner(context, ((PieceStyle)graphicsItem).roleType()) == playerIndex) && (((PieceStyle)graphicsItem).pieceName() == null || ((PieceStyle)graphicsItem).pieceName().equals(pieceName) || ((PieceStyle)graphicsItem).pieceName().equals(StringRoutines.removeTrailingNumbers(pieceName)))) {
                return ((PieceStyle)graphicsItem).componentStyleType();
            }
        }
        return null;
    }
    
    public ValueLocationType displayPieceState(final int playerIndex, final String pieceName, final Context context) {
        for (final GraphicsItem graphicsItem : this.items) {
            if (graphicsItem instanceof ShowPieceState && (((ShowPieceState)graphicsItem).roleType() == null || MetadataFunctions.getRealOwner(context, ((ShowPieceState)graphicsItem).roleType()) == playerIndex) && (((ShowPieceState)graphicsItem).pieceName() == null || ((ShowPieceState)graphicsItem).pieceName().equals(pieceName) || ((ShowPieceState)graphicsItem).pieceName().equals(StringRoutines.removeTrailingNumbers(pieceName)))) {
                return ((ShowPieceState)graphicsItem).location();
            }
        }
        return ValueLocationType.None;
    }
    
    public ValueLocationType displayPieceValue(final int playerIndex, final String pieceName, final Context context) {
        for (final GraphicsItem graphicsItem : this.items) {
            if (graphicsItem instanceof ShowPieceValue && (((ShowPieceValue)graphicsItem).roleType() == null || MetadataFunctions.getRealOwner(context, ((ShowPieceValue)graphicsItem).roleType()) == playerIndex) && (((ShowPieceValue)graphicsItem).pieceName() == null || ((ShowPieceValue)graphicsItem).pieceName().equals(pieceName) || ((ShowPieceValue)graphicsItem).pieceName().equals(StringRoutines.removeTrailingNumbers(pieceName)))) {
                return ((ShowPieceValue)graphicsItem).location();
            }
        }
        return ValueLocationType.None;
    }
    
    public ArrayList<MetadataImageInfo> boardBackground(final Context context) {
        final ArrayList<MetadataImageInfo> allBackgrounds = new ArrayList<>();
        for (final GraphicsItem graphicsItem : this.items) {
            if (graphicsItem instanceof BoardBackground) {
                final Colour fillColourMeta = ((BoardBackground)graphicsItem).fillColour();
                final Color fillColour = (fillColourMeta == null) ? null : fillColourMeta.colour();
                final Colour edgeColourMeta = ((BoardBackground)graphicsItem).edgeColour();
                final Color edgeColour = (edgeColourMeta == null) ? null : edgeColourMeta.colour();
                if (((BoardBackground)graphicsItem).scale() >= 0.0f && ((BoardBackground)graphicsItem).scale() <= 100.0f) {
                    if (((BoardBackground)graphicsItem).rotation() >= 0 && ((BoardBackground)graphicsItem).rotation() <= 360) {
                        if (((BoardBackground)graphicsItem).offsetX() >= -1.0f && ((BoardBackground)graphicsItem).offsetX() <= 1.0f) {
                            if (((BoardBackground)graphicsItem).offsetY() >= -1.0f && ((BoardBackground)graphicsItem).offsetY() <= 1.0f) {
                                allBackgrounds.add(new MetadataImageInfo(-1, null, ((BoardBackground)graphicsItem).background(), ((BoardBackground)graphicsItem).scale(), fillColour, edgeColour, ((BoardBackground)graphicsItem).rotation(), ((BoardBackground)graphicsItem).offsetX(), ((BoardBackground)graphicsItem).offsetY()));
                            }
                            else {
                                this.addError("Offset Y for board background was equal to " + ((BoardBackground)graphicsItem).offsetY() + ", offset must be between -1 and 1");
                            }
                        }
                        else {
                            this.addError("Offset X for board background was equal to " + ((BoardBackground)graphicsItem).offsetX() + ", offset must be between -1 and 1");
                        }
                    }
                    else {
                        this.addError("Rotation for board background was equal to " + ((BoardBackground)graphicsItem).rotation() + ", rotation must be between 0 and 360");
                    }
                }
                else {
                    this.addError("Scale for board background was equal to " + ((BoardBackground)graphicsItem).scale() + ", scale must be between 0 and 100");
                }
            }
        }
        return allBackgrounds;
    }
    
    public ArrayList<MetadataImageInfo> boardForeground(final Context context) {
        final ArrayList<MetadataImageInfo> allForegrounds = new ArrayList<>();
        for (final GraphicsItem graphicsItem : this.items) {
            if (graphicsItem instanceof BoardForeground) {
                final Colour fillColourMeta = ((BoardForeground)graphicsItem).fillColour();
                final Color fillColour = (fillColourMeta == null) ? null : fillColourMeta.colour();
                final Colour edgeColourMeta = ((BoardForeground)graphicsItem).edgeColour();
                final Color edgeColour = (edgeColourMeta == null) ? null : edgeColourMeta.colour();
                if (((BoardForeground)graphicsItem).scale() >= 0.0f && ((BoardForeground)graphicsItem).scale() <= 100.0f) {
                    if (((BoardForeground)graphicsItem).rotation() >= 0 && ((BoardForeground)graphicsItem).rotation() <= 360) {
                        if (((BoardForeground)graphicsItem).offsetX() >= -1.0f && ((BoardForeground)graphicsItem).offsetX() <= 1.0f) {
                            if (((BoardForeground)graphicsItem).offsetY() >= -1.0f && ((BoardForeground)graphicsItem).offsetY() <= 1.0f) {
                                allForegrounds.add(new MetadataImageInfo(-1, null, ((BoardForeground)graphicsItem).foreground(), ((BoardForeground)graphicsItem).scale(), fillColour, edgeColour, ((BoardForeground)graphicsItem).rotation(), ((BoardForeground)graphicsItem).offsetX(), ((BoardForeground)graphicsItem).offsetY()));
                            }
                            else {
                                this.addError("Offset Y for board foreground was equal to " + ((BoardForeground)graphicsItem).offsetY() + ", offset must be between -1 and 1");
                            }
                        }
                        else {
                            this.addError("Offset X for board foreground was equal to " + ((BoardForeground)graphicsItem).offsetX() + ", offset must be between -1 and 1");
                        }
                    }
                    else {
                        this.addError("Rotation for board foreground was equal to " + ((BoardForeground)graphicsItem).rotation() + ", rotation must be between 0 and 360");
                    }
                }
                else {
                    this.addError("Scale for board foreground was equal to " + ((BoardForeground)graphicsItem).scale() + ", scale must be between 0 and 100");
                }
            }
        }
        return allForegrounds;
    }
    
    public MetadataImageInfo pieceBackground(final int playerIndex, final String pieceName, final Context context, final int state) {
        for (final GraphicsItem graphicsItem : this.items) {
            if (graphicsItem instanceof PieceBackground && (((PieceBackground)graphicsItem).roleType() == null || MetadataFunctions.getRealOwner(context, ((PieceBackground)graphicsItem).roleType()) == playerIndex) && (((PieceBackground)graphicsItem).state() == null || ((PieceBackground)graphicsItem).state() == state) && (((PieceBackground)graphicsItem).pieceName() == null || ((PieceBackground)graphicsItem).pieceName().equals(pieceName) || ((PieceBackground)graphicsItem).pieceName().equals(StringRoutines.removeTrailingNumbers(pieceName)))) {
                final Colour fillColourMeta = ((PieceBackground)graphicsItem).fillColour();
                final Color fillColour = (fillColourMeta == null) ? null : fillColourMeta.colour();
                final Colour edgeColourMeta = ((PieceBackground)graphicsItem).edgeColour();
                final Color edgeColour = (edgeColourMeta == null) ? null : edgeColourMeta.colour();
                if (((PieceBackground)graphicsItem).scale() >= 0.0f && ((PieceBackground)graphicsItem).scale() <= 100.0f) {
                    return new MetadataImageInfo(-1, null, ((PieceBackground)graphicsItem).background(), ((PieceBackground)graphicsItem).scale(), fillColour, edgeColour);
                }
                this.addError("Scale for background of piece " + pieceName + " was equal to " + ((PieceBackground)graphicsItem).scale() + ", scale must be between 0 and 100");
            }
        }
        return null;
    }
    
    public MetadataImageInfo pieceForeground(final int playerIndex, final String pieceName, final Context context, final int state) {
        for (final GraphicsItem graphicsItem : this.items) {
            if (graphicsItem instanceof PieceForeground && (((PieceForeground)graphicsItem).roleType() == null || MetadataFunctions.getRealOwner(context, ((PieceForeground)graphicsItem).roleType()) == playerIndex) && (((PieceForeground)graphicsItem).state() == null || ((PieceForeground)graphicsItem).state() == state) && (((PieceForeground)graphicsItem).pieceName() == null || ((PieceForeground)graphicsItem).pieceName().equals(pieceName) || ((PieceForeground)graphicsItem).pieceName().equals(StringRoutines.removeTrailingNumbers(pieceName)))) {
                final Colour fillColourMeta = ((PieceForeground)graphicsItem).fillColour();
                final Color fillColour = (fillColourMeta == null) ? null : fillColourMeta.colour();
                final Colour edgeColourMeta = ((PieceForeground)graphicsItem).edgeColour();
                final Color edgeColour = (edgeColourMeta == null) ? null : edgeColourMeta.colour();
                if (((PieceForeground)graphicsItem).scale() >= 0.0f && ((PieceForeground)graphicsItem).scale() <= 100.0f) {
                    return new MetadataImageInfo(-1, null, ((PieceForeground)graphicsItem).foreground(), ((PieceForeground)graphicsItem).scale(), fillColour, edgeColour);
                }
                this.addError("Scale for foreground of piece " + pieceName + " was equal to " + ((PieceForeground)graphicsItem).scale() + ", scale must be between 0 and 100");
            }
        }
        return null;
    }
    
    public Color pieceFillColour(final int playerIndex, final String pieceName, final Context context, final int state) {
        for (final GraphicsItem graphicsItem : this.items) {
            if (graphicsItem instanceof PieceColour && (((PieceColour)graphicsItem).roleType() == null || MetadataFunctions.getRealOwner(context, ((PieceColour)graphicsItem).roleType()) == playerIndex) && (((PieceColour)graphicsItem).state() == null || ((PieceColour)graphicsItem).state() == state) && (((PieceColour)graphicsItem).pieceName() == null || ((PieceColour)graphicsItem).pieceName().equals(pieceName) || ((PieceColour)graphicsItem).pieceName().equals(StringRoutines.removeTrailingNumbers(pieceName)))) {
                final Colour colourMeta = ((PieceColour)graphicsItem).fillColour();
                return (colourMeta == null) ? null : colourMeta.colour();
            }
        }
        return null;
    }
    
    public Color pieceSecondaryColour(final int playerIndex, final String pieceName, final Context context, final int state) {
        for (final GraphicsItem graphicsItem : this.items) {
            if (graphicsItem instanceof PieceColour && (((PieceColour)graphicsItem).roleType() == null || MetadataFunctions.getRealOwner(context, ((PieceColour)graphicsItem).roleType()) == playerIndex) && (((PieceColour)graphicsItem).state() == null || ((PieceColour)graphicsItem).state() == state) && (((PieceColour)graphicsItem).pieceName() == null || ((PieceColour)graphicsItem).pieceName().equals(pieceName) || ((PieceColour)graphicsItem).pieceName().equals(StringRoutines.removeTrailingNumbers(pieceName)))) {
                final Colour colourMeta = ((PieceColour)graphicsItem).secondaryColour();
                return (colourMeta == null) ? null : colourMeta.colour();
            }
        }
        return null;
    }
    
    public Color pieceEdgeColour(final int playerIndex, final String pieceName, final Context context, final int state) {
        for (final GraphicsItem graphicsItem : this.items) {
            if (graphicsItem instanceof PieceColour && (((PieceColour)graphicsItem).roleType() == null || MetadataFunctions.getRealOwner(context, ((PieceColour)graphicsItem).roleType()) == playerIndex) && (((PieceColour)graphicsItem).state() == null || ((PieceColour)graphicsItem).state() == state) && (((PieceColour)graphicsItem).pieceName() == null || ((PieceColour)graphicsItem).pieceName().equals(pieceName) || ((PieceColour)graphicsItem).pieceName().equals(StringRoutines.removeTrailingNumbers(pieceName)))) {
                final Colour colourMeta = ((PieceColour)graphicsItem).strokeColour();
                return (colourMeta == null) ? null : colourMeta.colour();
            }
        }
        return null;
    }
    
    public String[] pieceFamilies() {
        for (final GraphicsItem graphicsItem : this.items) {
            if (graphicsItem instanceof PieceFamilies) {
                return ((PieceFamilies)graphicsItem).pieceFamilies();
            }
        }
        return null;
    }
    
    public boolean pieceFlipHorizontal(final int playerIndex, final String pieceName, final Context context) {
        for (final GraphicsItem graphicsItem : this.items) {
            if (graphicsItem instanceof PieceReflect && (((PieceReflect)graphicsItem).roleType() == null || MetadataFunctions.getRealOwner(context, ((PieceReflect)graphicsItem).roleType()) == playerIndex) && (((PieceReflect)graphicsItem).pieceName() == null || ((PieceReflect)graphicsItem).pieceName().equals(pieceName) || ((PieceReflect)graphicsItem).pieceName().equals(StringRoutines.removeTrailingNumbers(pieceName))) && ((PieceReflect)graphicsItem).horizontal() != null) {
                return ((PieceReflect)graphicsItem).horizontal();
            }
        }
        return false;
    }
    
    public boolean pieceFlipVertical(final int playerIndex, final String pieceName, final Context context) {
        for (final GraphicsItem graphicsItem : this.items) {
            if (graphicsItem instanceof PieceReflect && (((PieceReflect)graphicsItem).roleType() == null || MetadataFunctions.getRealOwner(context, ((PieceReflect)graphicsItem).roleType()) == playerIndex) && (((PieceReflect)graphicsItem).pieceName() == null || ((PieceReflect)graphicsItem).pieceName().equals(pieceName) || ((PieceReflect)graphicsItem).pieceName().equals(StringRoutines.removeTrailingNumbers(pieceName))) && ((PieceReflect)graphicsItem).vertical() != null) {
                return ((PieceReflect)graphicsItem).vertical();
            }
        }
        return false;
    }
    
    public int pieceRotate(final int playerIndex, final String pieceName, final Context context) {
        for (final GraphicsItem graphicsItem : this.items) {
            if (graphicsItem instanceof PieceRotate && (((PieceRotate)graphicsItem).roleType() == null || MetadataFunctions.getRealOwner(context, ((PieceRotate)graphicsItem).roleType()) == playerIndex) && (((PieceRotate)graphicsItem).pieceName() == null || ((PieceRotate)graphicsItem).pieceName().equals(pieceName) || ((PieceRotate)graphicsItem).pieceName().equals(StringRoutines.removeTrailingNumbers(pieceName)))) {
                if (((PieceRotate)graphicsItem).degrees() >= 0 && ((PieceRotate)graphicsItem).degrees() <= 360) {
                    return ((PieceRotate)graphicsItem).degrees();
                }
                this.addError("Rotation for peice" + pieceName + "was equal to " + ((PieceRotate)graphicsItem).degrees() + ", rotation must be between 0 and 360");
            }
        }
        return 0;
    }
    
    public String pieceNameExtension(final int playerIndex, final String pieceName, final Context context, final int state) {
        for (final GraphicsItem graphicsItem : this.items) {
            if (graphicsItem instanceof PieceExtendName && (((PieceExtendName)graphicsItem).roleType() == null || MetadataFunctions.getRealOwner(context, ((PieceExtendName)graphicsItem).roleType()) == playerIndex) && (((PieceExtendName)graphicsItem).state() == null || ((PieceExtendName)graphicsItem).state() == state) && (((PieceExtendName)graphicsItem).piece() == null || ((PieceExtendName)graphicsItem).piece().equals(pieceName) || ((PieceExtendName)graphicsItem).piece().equals(StringRoutines.removeTrailingNumbers(pieceName)))) {
                return ((PieceExtendName)graphicsItem).nameExtension();
            }
        }
        return "";
    }
    
    public String pieceNameReplacement(final int playerIndex, final String pieceName, final Context context, final int state) {
        for (final GraphicsItem graphicsItem : this.items) {
            if (graphicsItem instanceof PieceRename && (((PieceRename)graphicsItem).roleType() == null || MetadataFunctions.getRealOwner(context, ((PieceRename)graphicsItem).roleType()) == playerIndex) && (((PieceRename)graphicsItem).state() == null || ((PieceRename)graphicsItem).state() == state) && (((PieceRename)graphicsItem).piece() == null || ((PieceRename)graphicsItem).piece().equals(pieceName) || ((PieceRename)graphicsItem).piece().equals(StringRoutines.removeTrailingNumbers(pieceName)))) {
                return ((PieceRename)graphicsItem).nameReplacement();
            }
        }
        return null;
    }
    
    public double pieceScale(final int playerIndex, final String pieceName, final Context context) {
        for (final GraphicsItem graphicsItem : this.items) {
            if (graphicsItem instanceof PieceScale && (((PieceScale)graphicsItem).roleType() == null || MetadataFunctions.getRealOwner(context, ((PieceScale)graphicsItem).roleType()) == playerIndex) && (((PieceScale)graphicsItem).pieceName() == null || ((PieceScale)graphicsItem).pieceName().equals(pieceName) || ((PieceScale)graphicsItem).pieceName().equals(StringRoutines.removeTrailingNumbers(pieceName)))) {
                if (((PieceScale)graphicsItem).scale() >= 0.0 && ((PieceScale)graphicsItem).scale() <= 100.0) {
                    return ((PieceScale)graphicsItem).scale();
                }
                this.addError("Scale for piece " + pieceName + " was equal to " + ((PieceScale)graphicsItem).scale() + ", scale must be between 0 and 100");
            }
        }
        return 0.9;
    }
    
    public boolean checkUsed(final int playerIndex, final String pieceName, final Context context) {
        for (final GraphicsItem graphicsItem : this.items) {
            if (graphicsItem instanceof ShowCheck && (((ShowCheck)graphicsItem).roleType() == null || MetadataFunctions.getRealOwner(context, ((ShowCheck)graphicsItem).roleType()) == playerIndex) && (((ShowCheck)graphicsItem).pieceName() == null || ((ShowCheck)graphicsItem).pieceName().equals(pieceName) || ((ShowCheck)graphicsItem).pieceName().equals(StringRoutines.removeTrailingNumbers(pieceName)))) {
                return true;
            }
        }
        return false;
    }
    
    public ArrayList<MetadataImageInfo> drawSymbol(final Context context) {
        final ArrayList<MetadataImageInfo> allSymbols = new ArrayList<>();
        for (final GraphicsItem graphicsItem : this.items) {
            if (graphicsItem instanceof ShowSymbol) {
                final Colour fillColourMeta = ((ShowSymbol)graphicsItem).fillColour();
                final Color fillColour = (fillColourMeta == null) ? null : fillColourMeta.colour();
                final Colour edgeColourMeta = ((ShowSymbol)graphicsItem).edgeColour();
                final Color edgeColour = (edgeColourMeta == null) ? null : edgeColourMeta.colour();
                final int rotation = ((ShowSymbol)graphicsItem).rotation();
                if (((ShowSymbol)graphicsItem).rotation() < 0 || ((ShowSymbol)graphicsItem).rotation() > 360) {
                    this.addError("Rotation for symbol" + ((ShowSymbol)graphicsItem).imageName() + "was equal to " + ((ShowSymbol)graphicsItem).rotation() + ", rotation must be between 0 and 360");
                }
                else if (((ShowSymbol)graphicsItem).sites() != null) {
                    for (final Integer site : ((ShowSymbol)graphicsItem).sites()) {
                        final SiteType graphElementType = ((ShowSymbol)graphicsItem).graphElementType();
                        if (context.game().board().topology().getGraphElements(graphElementType).size() > site) {
                            allSymbols.add(new MetadataImageInfo(site, graphElementType, ((ShowSymbol)graphicsItem).imageName(), ((ShowSymbol)graphicsItem).scale(), fillColour, edgeColour, rotation));
                        }
                        else {
                            this.addError("Failed to add symbol " + ((ShowSymbol)graphicsItem).imageName() + " at site " + site + " with graphElementType " + graphElementType);
                        }
                    }
                }
                else if (((ShowSymbol)graphicsItem).region() != null) {
                    for (final ArrayList<Integer> regionSites : MetadataFunctions.convertRegionToSiteArray(context, ((ShowSymbol)graphicsItem).region(), ((ShowSymbol)graphicsItem).roleType())) {
                        for (final Integer site : regionSites) {
                            final SiteType graphElementType = ((ShowSymbol)graphicsItem).graphElementType();
                            if (context.game().board().topology().getGraphElements(graphElementType).size() > site) {
                                allSymbols.add(new MetadataImageInfo(site, ((ShowSymbol)graphicsItem).graphElementType(), ((ShowSymbol)graphicsItem).imageName(), ((ShowSymbol)graphicsItem).scale(), fillColour, edgeColour, rotation));
                            }
                            else {
                                this.addError("Failed to add symbol " + ((ShowSymbol)graphicsItem).imageName() + " at region site " + site + " with graphElementType " + graphElementType);
                            }
                        }
                    }
                }
                else {
                    if (((ShowSymbol)graphicsItem).regionFunction() == null || !((ShowSymbol)graphicsItem).regionFunction().isStatic()) {
                        continue;
                    }
                    for (final int site2 : ((ShowSymbol)graphicsItem).regionFunction().eval(context).sites()) {
                        final SiteType graphElementType = ((ShowSymbol)graphicsItem).graphElementType();
                        if (context.game().board().topology().getGraphElements(graphElementType).size() > site2) {
                            allSymbols.add(new MetadataImageInfo(site2, ((ShowSymbol)graphicsItem).graphElementType(), ((ShowSymbol)graphicsItem).imageName(), ((ShowSymbol)graphicsItem).scale(), fillColour, edgeColour, rotation));
                        }
                        else {
                            this.addError("Failed to add symbol " + ((ShowSymbol)graphicsItem).imageName() + " at region site " + site2 + " with graphElementType " + graphElementType);
                        }
                    }
                }
            }
        }
        return allSymbols;
    }
    
    public ArrayList<MetadataImageInfo> drawLines(final Context context) {
        final ArrayList<MetadataImageInfo> allLines = new ArrayList<>();
        for (final GraphicsItem graphicsItem : this.items) {
            if (graphicsItem instanceof ShowLine) {
                final Colour colourMeta = ((ShowLine)graphicsItem).colour();
                final Color colour = (colourMeta == null) ? null : colourMeta.colour();
                if (((ShowLine)graphicsItem).lines() == null) {
                    continue;
                }
                for (final Integer[] line : ((ShowLine)graphicsItem).lines()) {
                    if (context.game().board().topology().vertices().size() > Math.max(line[0], line[1])) {
                        if (((ShowLine)graphicsItem).curve() != null && ((ShowLine)graphicsItem).curve().length == 4) {
                            if (((ShowLine)graphicsItem).curve()[0] >= 0.0 && ((ShowLine)graphicsItem).curve()[0] <= 1.0 && ((ShowLine)graphicsItem).curve()[1] >= 0.0 && ((ShowLine)graphicsItem).curve()[1] <= 1.0 && ((ShowLine)graphicsItem).curve()[1] >= 0.0 && ((ShowLine)graphicsItem).curve()[1] <= 1.0 && ((ShowLine)graphicsItem).curve()[1] >= 0.0 && ((ShowLine)graphicsItem).curve()[1] <= 1.0) {
                                allLines.add(new MetadataImageInfo(line, colour, ((ShowLine)graphicsItem).scale(), ((ShowLine)graphicsItem).curve()));
                            }
                            else {
                                this.addError("All control points for the B\u00e9zier curve between " + line[0] + " and " + line[1] + " must be between 0 and 1");
                            }
                        }
                        else {
                            this.addError("Exactly 4 values must be specified for the B\u00e9zier curve between " + line[0] + " and " + line[1]);
                        }
                    }
                    else {
                        this.addError("Failed to draw line between vertices " + line[0] + " and " + line[1]);
                    }
                }
            }
        }
        return allLines;
    }
    
    public Color boardColour(final BoardGraphicsType boardGraphicsType) {
        for (final GraphicsItem graphicsItem : this.items) {
            if (graphicsItem instanceof BoardColour && ((BoardColour)graphicsItem).boardGraphicsType() == boardGraphicsType) {
                final Colour colourMeta = ((BoardColour)graphicsItem).colour();
                return (colourMeta == null) ? null : colourMeta.colour();
            }
        }
        return null;
    }
    
    public boolean boardHidden() {
        for (final GraphicsItem graphicsItem : this.items) {
            if (graphicsItem instanceof NoBoard) {
                return ((NoBoard)graphicsItem).boardHidden();
            }
        }
        return false;
    }
    
    public boolean showRegionOwner() {
        for (final GraphicsItem graphicsItem : this.items) {
            if (graphicsItem instanceof ShowRegionOwner) {
                return ((ShowRegionOwner)graphicsItem).show();
            }
        }
        return false;
    }
    
    public ContainerStyleType boardStyle() {
        for (final GraphicsItem graphicsItem : this.items) {
            if (graphicsItem instanceof BoardStyle) {
                return ((BoardStyle)graphicsItem).containerStyleType();
            }
        }
        return null;
    }
    
    public boolean onlyEdges() {
        for (final GraphicsItem graphicsItem : this.items) {
            if (graphicsItem instanceof BoardStyle) {
                return ((BoardStyle)graphicsItem).onlyEdges();
            }
        }
        return false;
    }
    
    public boolean checkeredBoard() {
        for (final GraphicsItem graphicsItem : this.items) {
            if (graphicsItem instanceof BoardCheckered) {
                return ((BoardCheckered)graphicsItem).checkeredBoard();
            }
        }
        return false;
    }
    
    public ArrayList<ArrayList<MetadataImageInfo>> regionsToFill(final Context context, final SiteType graphElementType) {
        final ArrayList<ArrayList<MetadataImageInfo>> allRegions = new ArrayList<>();
        for (final GraphicsItem graphicsItem : this.items) {
            if (graphicsItem instanceof RegionColour && ((RegionColour)graphicsItem).graphElementType() == graphElementType) {
                if (((RegionColour)graphicsItem).sites() != null) {
                    for (final Integer site : ((RegionColour)graphicsItem).sites()) {
                        if (context.game().board().topology().getGraphElements(graphElementType).size() > site) {
                            final Colour colourolourMeta = ((RegionColour)graphicsItem).colour();
                            final Color colour = (colourolourMeta == null) ? null : colourolourMeta.colour();
                            allRegions.add(new ArrayList<>());
                            allRegions.get(allRegions.size() - 1).add(new MetadataImageInfo(site, graphElementType, ((RegionColour)graphicsItem).boardGraphicsType(), colour));
                        }
                        else {
                            this.addError("Failed to fill site " + site + " with graphElementType " + graphElementType);
                        }
                    }
                }
                else if (((RegionColour)graphicsItem).region() != null) {
                    final Colour colourolourMeta2 = ((RegionColour)graphicsItem).colour();
                    final Color colour2 = (colourolourMeta2 == null) ? null : colourolourMeta2.colour();
                    for (final ArrayList<Integer> regionSiteList : MetadataFunctions.convertRegionToSiteArray(context, ((RegionColour)graphicsItem).region(), ((RegionColour)graphicsItem).roleType())) {
                        allRegions.add(new ArrayList<>());
                        for (final int site2 : regionSiteList) {
                            if (context.game().board().topology().getGraphElements(graphElementType).size() > site2) {
                                allRegions.get(allRegions.size() - 1).add(new MetadataImageInfo(site2, graphElementType, ((RegionColour)graphicsItem).boardGraphicsType(), colour2));
                            }
                            else {
                                this.addError("Failed to fill region " + ((RegionColour)graphicsItem).region() + "at site " + site2 + " with graphElementType " + graphElementType);
                            }
                        }
                    }
                }
                else {
                    if (((RegionColour)graphicsItem).regionFunction() == null || !((RegionColour)graphicsItem).regionFunction().isStatic()) {
                        continue;
                    }
                    final Colour colourolourMeta2 = ((RegionColour)graphicsItem).colour();
                    final Color colour2 = (colourolourMeta2 == null) ? null : colourolourMeta2.colour();
                    allRegions.add(new ArrayList<>());
                    for (final int site2 : ((RegionColour)graphicsItem).regionFunction().eval(context).sites()) {
                        if (context.game().board().topology().getGraphElements(graphElementType).size() > site2) {
                            allRegions.get(allRegions.size() - 1).add(new MetadataImageInfo(site2, graphElementType, ((RegionColour)graphicsItem).boardGraphicsType(), colour2));
                        }
                        else {
                            this.addError("Failed to fill region " + ((RegionColour)graphicsItem).region() + "at site " + site2 + " with graphElementType " + graphElementType);
                        }
                    }
                }
            }
        }
        return allRegions;
    }
    
    public float boardThickness(final BoardGraphicsType boardGraphicsType) {
        for (final GraphicsItem graphicsItem : this.items) {
            if (graphicsItem instanceof BoardStyleThickness && ((BoardStyleThickness)graphicsItem).boardGraphicsType() == boardGraphicsType) {
                if (((BoardStyleThickness)graphicsItem).thickness() >= 0.0f && ((BoardStyleThickness)graphicsItem).thickness() <= 100.0f) {
                    return ((BoardStyleThickness)graphicsItem).thickness();
                }
                this.addError("Scale for board thickness " + boardGraphicsType.name() + " was equal to " + ((BoardStyleThickness)graphicsItem).thickness() + ", scale must be between 0 and 100");
            }
        }
        return 1.0f;
    }
    
    public boolean adversarialPuzzle() {
        for (final GraphicsItem graphicsItem : this.items) {
            if (graphicsItem instanceof AdversarialPuzzle) {
                return ((AdversarialPuzzle)graphicsItem).adversarialPuzzle();
            }
        }
        return false;
    }
    
    public WhenScoreType showScore() {
        for (final GraphicsItem graphicsItem : this.items) {
            if (graphicsItem instanceof ShowScore) {
                return ((ShowScore)graphicsItem).showScore();
            }
        }
        return WhenScoreType.Always;
    }
    
    public boolean noAnimation() {
        for (final GraphicsItem graphicsItem : this.items) {
            if (graphicsItem instanceof NoAnimation) {
                return ((NoAnimation)graphicsItem).noAnimation();
            }
        }
        return false;
    }
    
    public boolean noHandScale() {
        for (final GraphicsItem graphicsItem : this.items) {
            if (graphicsItem instanceof NoHandScale) {
                return ((NoHandScale)graphicsItem).noHandScale();
            }
        }
        return false;
    }
    
    public boolean noDicePips() {
        for (final GraphicsItem graphicsItem : this.items) {
            if (graphicsItem instanceof NoDicePips) {
                return ((NoDicePips)graphicsItem).noDicePips();
            }
        }
        return false;
    }
    
    public boolean noMaskedColour() {
        for (final GraphicsItem graphicsItem : this.items) {
            if (graphicsItem instanceof NoMaskedColour) {
                return ((NoMaskedColour)graphicsItem).noMaskedColour();
            }
        }
        return false;
    }
    
    public SuitType[] suitRanking() {
        for (final GraphicsItem graphicsItem : this.items) {
            if (graphicsItem instanceof SuitRanking) {
                return ((SuitRanking)graphicsItem).suitRanking();
            }
        }
        return null;
    }
    
    public boolean scalePiecesByValue() {
        for (final GraphicsItem graphicsItem : this.items) {
            if (graphicsItem instanceof PieceScaleByValue) {
                return ((PieceScaleByValue)graphicsItem).scalePiecesByValue();
            }
        }
        return false;
    }
    
    public double stackScale(final Container container, final Context context, final int site, final SiteType siteType, final int state) {
        for (final GraphicsItem graphicsItem : this.items) {
            if (graphicsItem instanceof StackType && (((StackType)graphicsItem).roleType() == null || MetadataFunctions.getRealOwner(context, ((StackType)graphicsItem).roleType()) == container.owner()) && (((StackType)graphicsItem).name() == null || ((StackType)graphicsItem).name().equals(container.name()) || ((StackType)graphicsItem).name().equals(StringRoutines.removeTrailingNumbers(container.name()))) && (((StackType)graphicsItem).index() == null || ((StackType)graphicsItem).index().equals(container.index())) && (((StackType)graphicsItem).sites() == null || Arrays.asList(((StackType)graphicsItem).sites()).contains(site)) && (((StackType)graphicsItem).graphElementType() == null || ((StackType) graphicsItem).graphElementType() == siteType) && (((StackType)graphicsItem).state() == null || ((StackType)graphicsItem).state().equals(state))) {
                if (((StackType)graphicsItem).scale() >= 0.0 && ((StackType)graphicsItem).scale() <= 100.0) {
                    return ((StackType)graphicsItem).scale();
                }
                this.addError("Stack scale for role " + ((StackType)graphicsItem).roleType() + " name " + ((StackType)graphicsItem).name() + " was equal to " + ((StackType)graphicsItem).scale() + ", scale must be between 0 and 100");
            }
        }
        return 1.0;
    }
    
    public PieceStackType stackType(final Container container, final Context context, final int site, final SiteType siteType, final int state) {
        for (final GraphicsItem graphicsItem : this.items) {
            if (graphicsItem instanceof StackType && (((StackType)graphicsItem).roleType() == null || MetadataFunctions.getRealOwner(context, ((StackType)graphicsItem).roleType()) == container.owner()) && (((StackType)graphicsItem).name() == null || ((StackType)graphicsItem).name().equals(container.name()) || ((StackType)graphicsItem).name().equals(StringRoutines.removeTrailingNumbers(container.name()))) && (((StackType)graphicsItem).index() == null || ((StackType)graphicsItem).index().equals(container.index())) && (((StackType)graphicsItem).sites() == null || Arrays.asList(((StackType)graphicsItem).sites()).contains(site)) && (((StackType)graphicsItem).graphElementType() == null || ((StackType) graphicsItem).graphElementType() == siteType) && (((StackType)graphicsItem).state() == null || ((StackType)graphicsItem).state().equals(state))) {
                return ((StackType)graphicsItem).stackType();
            }
        }
        return PieceStackType.Default;
    }
    
    public Color playerColour(final int playerIndex, final Context context) {
        for (final GraphicsItem graphicsItem : this.items) {
            if (graphicsItem instanceof PlayerColour && MetadataFunctions.getRealOwner(context, ((PlayerColour)graphicsItem).roleType()) == playerIndex) {
                final Colour colourMeta = ((PlayerColour)graphicsItem).colour();
                return (colourMeta == null) ? null : colourMeta.colour();
            }
        }
        return null;
    }
    
    public boolean sitesAsHoles() {
        for (final GraphicsItem graphicsItem : this.items) {
            if (graphicsItem instanceof ShowSitesAsHoles) {
                return ((ShowSitesAsHoles)graphicsItem).sitesAsHoles();
            }
        }
        return false;
    }
    
    public boolean showPlayerHoles() {
        for (final GraphicsItem graphicsItem : this.items) {
            if (graphicsItem instanceof ShowPlayerHoles) {
                return ((ShowPlayerHoles)graphicsItem).showPlayerHoles();
            }
        }
        return false;
    }
    
    public EdgeInfoGUI drawEdge(final EdgeType type, final RelationType relationType, final boolean connection) {
        for (final GraphicsItem graphicsItem : this.items) {
            if (graphicsItem instanceof ShowEdges && ((ShowEdges)graphicsItem).type().supersetOf(type) && ((ShowEdges)graphicsItem).relationType().supersetOf(relationType) && ((ShowEdges)graphicsItem).connection() == connection) {
                return new EdgeInfoGUI(((ShowEdges)graphicsItem).style(), ((ShowEdges)graphicsItem).colour().colour());
            }
        }
        return null;
    }
    
    public boolean showPits() {
        for (final GraphicsItem graphicsItem : this.items) {
            if (graphicsItem instanceof ShowPits) {
                return ((ShowPits)graphicsItem).showPits();
            }
        }
        return false;
    }
    
    public boolean showCost() {
        for (final GraphicsItem graphicsItem : this.items) {
            if (graphicsItem instanceof ShowCost) {
                return ((ShowCost)graphicsItem).showCost();
            }
        }
        return false;
    }
    
    public PuzzleHintType hintType() {
        for (final GraphicsItem graphicsItem : this.items) {
            if (graphicsItem instanceof HintType) {
                return ((HintType)graphicsItem).hintType();
            }
        }
        return PuzzleHintType.Default;
    }
    
    public boolean showEdgeDirections() {
        for (final GraphicsItem graphicsItem : this.items) {
            if (graphicsItem instanceof ShowEdgeDirections) {
                return ((ShowEdgeDirections)graphicsItem).showEdgeDirections();
            }
        }
        return false;
    }
    
    public boolean showPossibleMoves() {
        for (final GraphicsItem graphicsItem : this.items) {
            if (graphicsItem instanceof ShowPossibleMoves) {
                return ((ShowPossibleMoves)graphicsItem).showPossibleMoves();
            }
        }
        return false;
    }
    
    public boolean straightRingLines() {
        for (final GraphicsItem graphicsItem : this.items) {
            if (graphicsItem instanceof NoCurves) {
                return ((NoCurves)graphicsItem).straightRingLines();
            }
        }
        return false;
    }
    
    public ShapeType cellShape() {
        for (final GraphicsItem graphicsItem : this.items) {
            if (graphicsItem instanceof ShowSitesShape) {
                return ((ShowSitesShape)graphicsItem).shape();
            }
        }
        return null;
    }
    
    public ShapeType boardShape() {
        for (final GraphicsItem graphicsItem : this.items) {
            if (graphicsItem instanceof BoardShape) {
                return ((BoardShape)graphicsItem).shape();
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        final String open = (this.items.size() <= 1) ? "" : "{";
        final String close = (this.items.size() <= 1) ? "" : "}";
        sb.append("    (graphics " + open + "\n");
        for (final GraphicsItem item : this.items) {
            if (item != null) {
                sb.append("        " + item.toString());
            }
        }
        sb.append("    " + close + ")\n");
        return sb.toString();
    }
    
    private void addError(final String string) {
        this.errorReport = this.errorReport + "Error: " + string + "\n";
    }
    
    public String getErrorReport() {
        return this.errorReport;
    }
    
    public void setErrorReport(final String s) {
        this.errorReport = s;
    }
}
