// 
// Decompiled by Procyon v0.5.36
// 

package app.display.dialogs;

import app.display.dialogs.util.DialogUtil;
import app.display.dialogs.util.JComboCheckBox;
import game.types.board.SiteType;
import game.util.directions.DirectionFacing;
import gnu.trove.iterator.TIntIterator;
import manager.Manager;
import manager.utils.ContextSnapshot;
import topology.Topology;
import topology.TopologyElement;
import util.SettingsVC;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.*;

public class DeveloperDialog extends JDialog
{
    private static final long serialVersionUID = 1L;
    private final int CELL_X = 27;
    private final int VERTEX_X = 228;
    private final int EDGE_X = 440;
    private final int GAP = 26;
    private final int INIT_Y = 60;
    private final int SIZE_COMBO_BOXES = 100;
    private final Map<String, Integer> indexPregen;
    
    public static void showDialog() {
        try {
            final DeveloperDialog dialog = new DeveloperDialog();
            DialogUtil.initialiseSingletonDialog(dialog, "Developer Settings", null);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void initMapIndexPregen() {
        this.indexPregen.put("Inner", 0);
        this.indexPregen.put("Outer", 1);
        this.indexPregen.put("Perimeter", 2);
        this.indexPregen.put("Center", 3);
        this.indexPregen.put("Major", 4);
        this.indexPregen.put("Minor", 5);
        this.indexPregen.put("Corners", 7);
        this.indexPregen.put("Corners Concave", 8);
        this.indexPregen.put("Corners Convex", 9);
        this.indexPregen.put("Top", 11);
        this.indexPregen.put("Bottom", 12);
        this.indexPregen.put("Left", 13);
        this.indexPregen.put("Right", 14);
        this.indexPregen.put("Phases", 16);
        this.indexPregen.put("Side", 18);
        this.indexPregen.put("Col", 20);
        this.indexPregen.put("Row", 21);
        this.indexPregen.put("Neighbours", 23);
        this.indexPregen.put("Radials", 24);
        this.indexPregen.put("Distance", 25);
        this.indexPregen.put("Axial", 20);
        this.indexPregen.put("Horizontal", 21);
        this.indexPregen.put("Vertical", 22);
        this.indexPregen.put("Angled", 23);
        this.indexPregen.put("Slash", 24);
        this.indexPregen.put("Slosh", 25);
    }
    
    public DeveloperDialog() {
        this.indexPregen = new HashMap<>();
        this.initMapIndexPregen();
        this.setBounds(100, 100, 1200, this.indexPregen.values().size() * 31);
        this.getContentPane().setLayout(new BorderLayout());
        final JPanel contentPanel = new JPanel();
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.getContentPane().add(contentPanel, "Center");
        contentPanel.setLayout(null);
        final JLabel lblNewLabel = new JLabel("Pregeneration visuals");
        lblNewLabel.setBounds(27, 11, 331, 15);
        contentPanel.add(lblNewLabel);
        final Topology topology = ContextSnapshot.getContext().board().topology();
        this.makeColumnCell(contentPanel, topology);
        this.makeColumnVertex(contentPanel, topology);
        this.makeColumnEdge(contentPanel, topology);
        makeColumnOther(contentPanel, topology);
    }
    
    public void makeColumnCell(final JPanel contentPanel, final Topology topology) {
        final JLabel label = new JLabel("Cells");
        label.setBounds(37, 37, 104, 15);
        contentPanel.add(label);
        final JCheckBox checkBox_Corners = this.checkBox(contentPanel, 27, 60, "Corners", SettingsVC.drawCornerCells);
        checkBox_Corners.addActionListener(e -> {
            SettingsVC.drawCornerCells = checkBox_Corners.isSelected();
            Manager.app.repaint();
        });
        final JCheckBox checkBox_Corners_Concave = this.checkBox(contentPanel, 27, 60, "Corners Concave", SettingsVC.drawCornerConcaveCells);
        checkBox_Corners_Concave.addActionListener(e -> {
            SettingsVC.drawCornerConcaveCells = checkBox_Corners_Concave.isSelected();
            Manager.app.repaint();
        });
        final JCheckBox checkBox_Corners_Convex = this.checkBox(contentPanel, 27, 60, "Corners Convex", SettingsVC.drawCornerConvexCells);
        checkBox_Corners_Convex.addActionListener(e -> {
            SettingsVC.drawCornerConvexCells = checkBox_Corners_Convex.isSelected();
            Manager.app.repaint();
        });
        final JCheckBox checkBox_Major = this.checkBox(contentPanel, 27, 60, "Major", SettingsVC.drawMajorCells);
        checkBox_Major.addActionListener(e -> {
            SettingsVC.drawMajorCells = checkBox_Major.isSelected();
            Manager.app.repaint();
        });
        final JCheckBox checkBox_Minor = this.checkBox(contentPanel, 27, 60, "Minor", SettingsVC.drawMinorCells);
        checkBox_Minor.addActionListener(e -> {
            SettingsVC.drawMinorCells = checkBox_Minor.isSelected();
            Manager.app.repaint();
        });
        final JCheckBox checkBox_Perimeter = this.checkBox(contentPanel, 27, 60, "Perimeter", SettingsVC.drawPerimeterCells);
        checkBox_Perimeter.addActionListener(e -> {
            SettingsVC.drawPerimeterCells = checkBox_Perimeter.isSelected();
            Manager.app.repaint();
        });
        final JCheckBox checkBox_Outer = this.checkBox(contentPanel, 27, 60, "Outer", SettingsVC.drawOuterCells);
        checkBox_Outer.addActionListener(e -> {
            SettingsVC.drawOuterCells = checkBox_Outer.isSelected();
            Manager.app.repaint();
        });
        final JCheckBox checkBox_Inner = this.checkBox(contentPanel, 27, 60, "Inner", SettingsVC.drawInnerCells);
        checkBox_Inner.addActionListener(e -> {
            SettingsVC.drawInnerCells = checkBox_Inner.isSelected();
            Manager.app.repaint();
        });
        final JCheckBox checkBox_Top = this.checkBox(contentPanel, 27, 60, "Top", SettingsVC.drawTopCells);
        checkBox_Top.addActionListener(e -> {
            SettingsVC.drawTopCells = checkBox_Top.isSelected();
            Manager.app.repaint();
        });
        final JCheckBox checkBox_Bottom = this.checkBox(contentPanel, 27, 60, "Bottom", SettingsVC.drawBottomCells);
        checkBox_Bottom.addActionListener(e -> {
            SettingsVC.drawBottomCells = checkBox_Bottom.isSelected();
            Manager.app.repaint();
        });
        final JCheckBox checkBox_Left = this.checkBox(contentPanel, 27, 60, "Left", SettingsVC.drawLeftCells);
        checkBox_Left.addActionListener(e -> {
            SettingsVC.drawLeftCells = checkBox_Left.isSelected();
            Manager.app.repaint();
        });
        final JCheckBox checkBox_Right = this.checkBox(contentPanel, 27, 60, "Right", SettingsVC.drawRightCells);
        checkBox_Right.addActionListener(e -> {
            SettingsVC.drawRightCells = checkBox_Right.isSelected();
            Manager.app.repaint();
        });
        final JCheckBox checkBox_Center = this.checkBox(contentPanel, 27, 60, "Center", SettingsVC.drawCenterCells);
        checkBox_Center.addActionListener(e -> {
            SettingsVC.drawCenterCells = checkBox_Center.isSelected();
            Manager.app.repaint();
        });
        final JCheckBox checkBox_Phases = this.checkBox(contentPanel, 27, 60, "Phases", SettingsVC.drawPhasesCells);
        checkBox_Phases.addActionListener(e -> {
            SettingsVC.drawPhasesCells = checkBox_Phases.isSelected();
            Manager.app.repaint();
        });
        final JComboCheckBox comboCheckBox_Column = this.comboCheckBox(contentPanel, 27, 60, "Col", SettingsVC.drawColumnsCells, topology.columns(SiteType.Cell));
        if (comboCheckBox_Column != null) {
            comboCheckBox_Column.addActionListener(e -> EventQueue.invokeLater(() -> {
                for (int i = 0; i < comboCheckBox_Column.getItemCount(); ++i) {
                    SettingsVC.drawColumnsCells.set(i, comboCheckBox_Column.getItemAt(i).isSelected());
                }
                Manager.app.repaint();
            }));
        }
        final JComboCheckBox comboCheckBox_Row = this.comboCheckBox(contentPanel, 27, 60, "Row", SettingsVC.drawRowsCells, topology.rows(SiteType.Cell));
        if (comboCheckBox_Row != null) {
            comboCheckBox_Row.addActionListener(e -> EventQueue.invokeLater(() -> {
                for (int i = 0; i < comboCheckBox_Row.getItemCount(); ++i) {
                    SettingsVC.drawRowsCells.set(i, comboCheckBox_Row.getItemAt(i).isSelected());
                }
                Manager.app.repaint();
            }));
        }
        if (topology.sides(SiteType.Cell).size() > 0) {
            final Vector<JCheckBox> v = new Vector<>();
            for (final DirectionFacing d : topology.sides(SiteType.Cell).keySet()) {
                SettingsVC.drawSideCells.put(d.uniqueName().toString(), false);
                final JCheckBox tempCheckBox = new JCheckBox(d.uniqueName().toString(), false);
                tempCheckBox.setSelected(SettingsVC.drawSideCells.get(d.uniqueName().toString()));
                v.add(tempCheckBox);
            }
            final JComboCheckBox directionLimitOptions = new JComboCheckBox(v);
            directionLimitOptions.addActionListener(e -> EventQueue.invokeLater(() -> {
                for (int i = 0; i < directionLimitOptions.getItemCount(); ++i) {
                    SettingsVC.drawSideCells.put(directionLimitOptions.getItemAt(i).getText(), directionLimitOptions.getItemAt(i).isSelected());
                }
                Manager.app.repaint();
            }));
            directionLimitOptions.setBounds(27, 60 + 26 * this.indexPregen.get("Side"), 100, 23);
            contentPanel.add(directionLimitOptions);
        }
        final JCheckBox checkBox_Neighbours = this.checkBox(contentPanel, 27, 60, "Neighbours", SettingsVC.drawNeighboursCells);
        checkBox_Neighbours.addActionListener(e -> SettingsVC.drawNeighboursCells = checkBox_Neighbours.isSelected());
        final JCheckBox checkBox_Radials = this.checkBox(contentPanel, 27, 60, "Radials", SettingsVC.drawRadialsCells);
        checkBox_Radials.addActionListener(e -> SettingsVC.drawRadialsCells = checkBox_Radials.isSelected());
        final JCheckBox checkBox_Distance = this.checkBox(contentPanel, 27, 60, "Distance", SettingsVC.drawDistanceCells);
        checkBox_Distance.addActionListener(e -> SettingsVC.drawDistanceCells = checkBox_Distance.isSelected());
    }
    
    public void makeColumnVertex(final JPanel contentPanel, final Topology topology) {
        final JLabel label = new JLabel("Vertices");
        label.setBounds(249, 37, 104, 15);
        contentPanel.add(label);
        final JCheckBox checkBox_Corners = this.checkBox(contentPanel, 228, 60, "Corners", SettingsVC.drawCornerVertices);
        checkBox_Corners.addActionListener(e -> {
            SettingsVC.drawCornerVertices = checkBox_Corners.isSelected();
            Manager.app.repaint();
        });
        final JCheckBox checkBox_Corners_Concave = this.checkBox(contentPanel, 228, 60, "Corners Concave", SettingsVC.drawCornerConcaveVertices);
        checkBox_Corners_Concave.addActionListener(e -> {
            SettingsVC.drawCornerConcaveVertices = checkBox_Corners_Concave.isSelected();
            Manager.app.repaint();
        });
        final JCheckBox checkBox_Corners_Convex = this.checkBox(contentPanel, 228, 60, "Corners Convex", SettingsVC.drawCornerConvexVertices);
        checkBox_Corners_Convex.addActionListener(e -> {
            SettingsVC.drawCornerConvexVertices = checkBox_Corners_Convex.isSelected();
            Manager.app.repaint();
        });
        final JCheckBox checkBox_Major = this.checkBox(contentPanel, 228, 60, "Major", SettingsVC.drawMajorVertices);
        checkBox_Major.addActionListener(e -> {
            SettingsVC.drawMajorVertices = checkBox_Major.isSelected();
            Manager.app.repaint();
        });
        final JCheckBox checkBox_Minor = this.checkBox(contentPanel, 228, 60, "Minor", SettingsVC.drawMinorVertices);
        checkBox_Minor.addActionListener(e -> {
            SettingsVC.drawMinorVertices = checkBox_Minor.isSelected();
            Manager.app.repaint();
        });
        final JCheckBox checkBox_Perimeter = this.checkBox(contentPanel, 228, 60, "Perimeter", SettingsVC.drawPerimeterVertices);
        checkBox_Perimeter.addActionListener(e -> {
            SettingsVC.drawPerimeterVertices = checkBox_Perimeter.isSelected();
            Manager.app.repaint();
        });
        final JCheckBox checkBox_Outer = this.checkBox(contentPanel, 228, 60, "Outer", SettingsVC.drawOuterVertices);
        checkBox_Outer.addActionListener(e -> {
            SettingsVC.drawOuterVertices = checkBox_Outer.isSelected();
            Manager.app.repaint();
        });
        final JCheckBox checkBox_Inner = this.checkBox(contentPanel, 228, 60, "Inner", SettingsVC.drawInnerVertices);
        checkBox_Inner.addActionListener(e -> {
            SettingsVC.drawInnerVertices = checkBox_Inner.isSelected();
            Manager.app.repaint();
        });
        final JCheckBox checkBox_Top = this.checkBox(contentPanel, 228, 60, "Top", SettingsVC.drawTopVertices);
        checkBox_Top.addActionListener(e -> {
            SettingsVC.drawTopVertices = checkBox_Top.isSelected();
            Manager.app.repaint();
        });
        final JCheckBox checkBox_Bottom = this.checkBox(contentPanel, 228, 60, "Bottom", SettingsVC.drawBottomVertices);
        checkBox_Bottom.addActionListener(e -> {
            SettingsVC.drawBottomVertices = checkBox_Bottom.isSelected();
            Manager.app.repaint();
        });
        final JCheckBox checkBox_Left = this.checkBox(contentPanel, 228, 60, "Left", SettingsVC.drawLeftVertices);
        checkBox_Left.addActionListener(e -> {
            SettingsVC.drawLeftVertices = checkBox_Left.isSelected();
            Manager.app.repaint();
        });
        final JCheckBox checkBox_Right = this.checkBox(contentPanel, 228, 60, "Right", SettingsVC.drawRightVertices);
        checkBox_Right.addActionListener(e -> {
            SettingsVC.drawRightVertices = checkBox_Right.isSelected();
            Manager.app.repaint();
        });
        final JCheckBox checkBox_Center = this.checkBox(contentPanel, 228, 60, "Center", SettingsVC.drawCenterVertices);
        checkBox_Center.addActionListener(e -> {
            SettingsVC.drawCenterVertices = checkBox_Center.isSelected();
            Manager.app.repaint();
        });
        final JCheckBox checkBox_Phases = this.checkBox(contentPanel, 228, 60, "Phases", SettingsVC.drawPhasesVertices);
        checkBox_Phases.addActionListener(e -> {
            SettingsVC.drawPhasesVertices = checkBox_Phases.isSelected();
            Manager.app.repaint();
        });
        final JComboCheckBox comboCheckBox_Column = this.comboCheckBox(contentPanel, 228, 60, "Col", SettingsVC.drawColumnsVertices, topology.columns(SiteType.Vertex));
        if (comboCheckBox_Column != null) {
            comboCheckBox_Column.addActionListener(e -> EventQueue.invokeLater(() -> {
                for (int i = 0; i < comboCheckBox_Column.getItemCount(); ++i) {
                    SettingsVC.drawColumnsVertices.set(i, comboCheckBox_Column.getItemAt(i).isSelected());
                }
                Manager.app.repaint();
            }));
        }
        final JComboCheckBox comboCheckBox_Row = this.comboCheckBox(contentPanel, 228, 60, "Row", SettingsVC.drawRowsVertices, topology.rows(SiteType.Vertex));
        if (comboCheckBox_Row != null) {
            comboCheckBox_Row.addActionListener(e -> EventQueue.invokeLater(() -> {
                for (int i = 0; i < comboCheckBox_Row.getItemCount(); ++i) {
                    SettingsVC.drawRowsVertices.set(i, comboCheckBox_Row.getItemAt(i).isSelected());
                }
                Manager.app.repaint();
            }));
        }
        if (topology.sides(SiteType.Vertex).size() > 0) {
            final Vector<JCheckBox> v = new Vector<>();
            for (final DirectionFacing d : topology.sides(SiteType.Vertex).keySet()) {
                SettingsVC.drawSideVertices.put(d.uniqueName().toString(), false);
                final JCheckBox tempCheckBox = new JCheckBox(d.uniqueName().toString(), false);
                tempCheckBox.setSelected(SettingsVC.drawSideVertices.get(d.uniqueName().toString()));
                v.add(tempCheckBox);
            }
            final JComboCheckBox directionLimitOptions = new JComboCheckBox(v);
            directionLimitOptions.addActionListener(e -> EventQueue.invokeLater(() -> {
                for (int i = 0; i < directionLimitOptions.getItemCount(); ++i) {
                    SettingsVC.drawSideVertices.put(directionLimitOptions.getItemAt(i).getText(), directionLimitOptions.getItemAt(i).isSelected());
                }
                Manager.app.repaint();
            }));
            directionLimitOptions.setBounds(228, 60 + 26 * this.indexPregen.get("Side"), 100, 23);
            contentPanel.add(directionLimitOptions);
        }
        final JCheckBox checkBox_Neighbours = this.checkBox(contentPanel, 228, 60, "Neighbours", SettingsVC.drawNeighboursVertices);
        checkBox_Neighbours.addActionListener(e -> SettingsVC.drawNeighboursVertices = checkBox_Neighbours.isSelected());
        final JCheckBox checkBox_Radials = this.checkBox(contentPanel, 228, 60, "Radials", SettingsVC.drawRadialsVertices);
        checkBox_Radials.addActionListener(e -> SettingsVC.drawRadialsVertices = checkBox_Radials.isSelected());
        final JCheckBox checkBox_Distance = this.checkBox(contentPanel, 228, 60, "Distance", SettingsVC.drawDistanceVertices);
        checkBox_Distance.addActionListener(e -> SettingsVC.drawDistanceVertices = checkBox_Distance.isSelected());
    }
    
    public void makeColumnEdge(final JPanel contentPanel, final Topology topology) {
        final JLabel label = new JLabel("Edges");
        label.setBounds(461, 37, 104, 15);
        contentPanel.add(label);
        final JCheckBox checkBox_Corners = this.checkBox(contentPanel, 440, 60, "Corners", SettingsVC.drawCornerEdges);
        checkBox_Corners.addActionListener(e -> {
            SettingsVC.drawCornerEdges = checkBox_Corners.isSelected();
            Manager.app.repaint();
        });
        final JCheckBox checkBox_Corners_Concave = this.checkBox(contentPanel, 440, 60, "Corners Concave", SettingsVC.drawCornerConcaveEdges);
        checkBox_Corners_Concave.addActionListener(e -> {
            SettingsVC.drawCornerConcaveEdges = checkBox_Corners_Concave.isSelected();
            Manager.app.repaint();
        });
        final JCheckBox checkBox_Corners_Convex = this.checkBox(contentPanel, 440, 60, "Corners Convex", SettingsVC.drawCornerConvexEdges);
        checkBox_Corners_Convex.addActionListener(e -> {
            SettingsVC.drawCornerConvexEdges = checkBox_Corners_Convex.isSelected();
            Manager.app.repaint();
        });
        final JCheckBox checkBox_Major = this.checkBox(contentPanel, 440, 60, "Major", SettingsVC.drawMajorEdges);
        checkBox_Major.addActionListener(e -> {
            SettingsVC.drawMajorEdges = checkBox_Major.isSelected();
            Manager.app.repaint();
        });
        final JCheckBox checkBox_Minor = this.checkBox(contentPanel, 440, 60, "Minor", SettingsVC.drawMinorEdges);
        checkBox_Minor.addActionListener(e -> {
            SettingsVC.drawMinorEdges = checkBox_Minor.isSelected();
            Manager.app.repaint();
        });
        final JCheckBox checkBox_Perimeter = this.checkBox(contentPanel, 440, 60, "Perimeter", SettingsVC.drawPerimeterEdges);
        checkBox_Perimeter.addActionListener(e -> {
            SettingsVC.drawPerimeterEdges = checkBox_Perimeter.isSelected();
            Manager.app.repaint();
        });
        final JCheckBox checkBox_Outer = this.checkBox(contentPanel, 440, 60, "Outer", SettingsVC.drawOuterEdges);
        checkBox_Outer.addActionListener(e -> {
            SettingsVC.drawOuterEdges = checkBox_Outer.isSelected();
            Manager.app.repaint();
        });
        final JCheckBox checkBox_Inner = this.checkBox(contentPanel, 440, 60, "Inner", SettingsVC.drawInnerEdges);
        checkBox_Inner.addActionListener(e -> {
            SettingsVC.drawInnerEdges = checkBox_Inner.isSelected();
            Manager.app.repaint();
        });
        final JCheckBox checkBox_Top = this.checkBox(contentPanel, 440, 60, "Top", SettingsVC.drawTopEdges);
        checkBox_Top.addActionListener(e -> {
            SettingsVC.drawTopEdges = checkBox_Top.isSelected();
            Manager.app.repaint();
        });
        final JCheckBox checkBox_Bottom = this.checkBox(contentPanel, 440, 60, "Bottom", SettingsVC.drawBottomEdges);
        checkBox_Bottom.addActionListener(e -> {
            SettingsVC.drawBottomEdges = checkBox_Bottom.isSelected();
            Manager.app.repaint();
        });
        final JCheckBox checkBox_Left = this.checkBox(contentPanel, 440, 60, "Left", SettingsVC.drawLeftEdges);
        checkBox_Left.addActionListener(e -> {
            SettingsVC.drawLeftEdges = checkBox_Left.isSelected();
            Manager.app.repaint();
        });
        final JCheckBox checkBox_Right = this.checkBox(contentPanel, 440, 60, "Right", SettingsVC.drawRightEdges);
        checkBox_Right.addActionListener(e -> {
            SettingsVC.drawRightEdges = checkBox_Right.isSelected();
            Manager.app.repaint();
        });
        final JCheckBox checkBox_Center = this.checkBox(contentPanel, 440, 60, "Center", SettingsVC.drawCentreEdges);
        checkBox_Center.addActionListener(e -> {
            SettingsVC.drawCentreEdges = checkBox_Center.isSelected();
            Manager.app.repaint();
        });
        final JCheckBox checkBox_Phases = this.checkBox(contentPanel, 440, 60, "Phases", SettingsVC.drawPhasesEdges);
        checkBox_Phases.addActionListener(e -> {
            SettingsVC.drawPhasesEdges = checkBox_Phases.isSelected();
            Manager.app.repaint();
        });
        final JCheckBox checkBox_Axial = this.checkBox(contentPanel, 440, 60, "Axial", SettingsVC.drawAxialEdges);
        checkBox_Axial.addActionListener(e -> {
            SettingsVC.drawAxialEdges = checkBox_Axial.isSelected();
            Manager.app.repaint();
        });
        final JCheckBox checkBox_Horizontal = this.checkBox(contentPanel, 440, 60, "Horizontal", SettingsVC.drawHorizontalEdges);
        checkBox_Horizontal.addActionListener(e -> {
            SettingsVC.drawHorizontalEdges = checkBox_Horizontal.isSelected();
            Manager.app.repaint();
        });
        final JCheckBox checkBox_Vertical = this.checkBox(contentPanel, 440, 60, "Vertical", SettingsVC.drawVerticalEdges);
        checkBox_Vertical.addActionListener(e -> {
            SettingsVC.drawVerticalEdges = checkBox_Vertical.isSelected();
            Manager.app.repaint();
        });
        final JCheckBox checkBox_Angled = this.checkBox(contentPanel, 440, 60, "Angled", SettingsVC.drawAngledEdges);
        checkBox_Angled.addActionListener(e -> {
            SettingsVC.drawAngledEdges = checkBox_Angled.isSelected();
            Manager.app.repaint();
        });
        final JCheckBox checkBox_Slash = this.checkBox(contentPanel, 440, 60, "Slash", SettingsVC.drawSlashEdges);
        checkBox_Slash.addActionListener(e -> {
            SettingsVC.drawSlashEdges = checkBox_Slash.isSelected();
            Manager.app.repaint();
        });
        final JCheckBox checkBox_Slosh = this.checkBox(contentPanel, 440, 60, "Slosh", SettingsVC.drawSloshEdges);
        checkBox_Slosh.addActionListener(e -> {
            SettingsVC.drawSloshEdges = checkBox_Slosh.isSelected();
            Manager.app.repaint();
        });
        if (topology.sides(SiteType.Edge).size() > 0) {
            final Vector<JCheckBox> v = new Vector<>();
            for (final DirectionFacing d : topology.sides(SiteType.Edge).keySet()) {
                SettingsVC.drawSideEdges.put(d.uniqueName().toString(), false);
                final JCheckBox tempCheckBox = new JCheckBox(d.uniqueName().toString(), false);
                tempCheckBox.setSelected(SettingsVC.drawSideEdges.get(d.uniqueName().toString()));
                v.add(tempCheckBox);
            }
            final JComboCheckBox directionLimitOptions = new JComboCheckBox(v);
            directionLimitOptions.addActionListener(e -> EventQueue.invokeLater(() -> {
                for (int i = 0; i < directionLimitOptions.getItemCount(); ++i) {
                    SettingsVC.drawSideEdges.put(directionLimitOptions.getItemAt(i).getText(), directionLimitOptions.getItemAt(i).isSelected());
                }
                Manager.app.repaint();
            }));
            directionLimitOptions.setBounds(440, 60 + 26 * this.indexPregen.get("Side"), 100, 23);
            contentPanel.add(directionLimitOptions);
        }
    }
    
    private static void makeColumnOther(final JPanel contentPanel, final Topology topology) {
        final JTextField textFieldMaximumNumberOfTurns = new JTextField();
        textFieldMaximumNumberOfTurns.setColumns(10);
        textFieldMaximumNumberOfTurns.setBounds(970, 495, 86, 20);
        contentPanel.add(textFieldMaximumNumberOfTurns);
        textFieldMaximumNumberOfTurns.setText("" + ContextSnapshot.getContext().game().getMaxMoveLimit());
        final JLabel lblNewLabel_1 = new JLabel("Maximum number of moves");
        lblNewLabel_1.setBounds(719, 498, 241, 14);
        contentPanel.add(lblNewLabel_1);
        final DocumentListener documentListenerMaxTurns = new DocumentListener() {
            @Override
            public void changedUpdate(final DocumentEvent documentEvent) {
                this.update(documentEvent);
            }
            
            @Override
            public void insertUpdate(final DocumentEvent documentEvent) {
                this.update(documentEvent);
            }
            
            @Override
            public void removeUpdate(final DocumentEvent documentEvent) {
                this.update(documentEvent);
            }
            
            private void update(final DocumentEvent documentEvent) {
                try {
                    ContextSnapshot.getContext().game().setMaxMoveLimit(Integer.parseInt(textFieldMaximumNumberOfTurns.getText()));
                }
                catch (Exception e) {
                    ContextSnapshot.getContext().game().setMaxMoveLimit(10000);
                }
                Manager.app.repaint();
            }
        };
        textFieldMaximumNumberOfTurns.getDocument().addDocumentListener(documentListenerMaxTurns);
        final JCheckBox chckbxFacesofvertex = new JCheckBox("Faces of Vertices");
        chckbxFacesofvertex.setSelected(SettingsVC.drawFacesOfVertices);
        chckbxFacesofvertex.setBounds(707, 60, 199, 23);
        contentPanel.add(chckbxFacesofvertex);
        chckbxFacesofvertex.addActionListener(e -> {
            SettingsVC.drawFacesOfVertices = chckbxFacesofvertex.isSelected();
            Manager.app.repaint();
        });
        final JCheckBox chckbxEdgesOfVertices = new JCheckBox("Edges of Vertices");
        chckbxEdgesOfVertices.setSelected(SettingsVC.drawEdgesOfVertices);
        chckbxEdgesOfVertices.setBounds(707, 86, 199, 23);
        contentPanel.add(chckbxEdgesOfVertices);
        chckbxEdgesOfVertices.addActionListener(e -> {
            SettingsVC.drawEdgesOfVertices = chckbxEdgesOfVertices.isSelected();
            Manager.app.repaint();
        });
        final JCheckBox chckbxVerticesOfFaces = new JCheckBox("Vertices of Faces");
        chckbxVerticesOfFaces.setSelected(SettingsVC.drawVerticesOfFaces);
        chckbxVerticesOfFaces.setBounds(707, 112, 199, 23);
        contentPanel.add(chckbxVerticesOfFaces);
        chckbxVerticesOfFaces.addActionListener(e -> {
            SettingsVC.drawVerticesOfFaces = chckbxVerticesOfFaces.isSelected();
            Manager.app.repaint();
        });
        final JCheckBox chckbxEdgesOfFaces = new JCheckBox("Edges of Faces");
        chckbxEdgesOfFaces.setSelected(SettingsVC.drawEdgesOfFaces);
        chckbxEdgesOfFaces.setBounds(707, 138, 199, 23);
        contentPanel.add(chckbxEdgesOfFaces);
        chckbxEdgesOfFaces.addActionListener(e -> {
            SettingsVC.drawEdgesOfFaces = chckbxEdgesOfFaces.isSelected();
            Manager.app.repaint();
        });
        final JCheckBox chckbxVerticesOfEdges = new JCheckBox("Vertices of Edges");
        chckbxVerticesOfEdges.setSelected(SettingsVC.drawVerticesOfEdges);
        chckbxVerticesOfEdges.setBounds(707, 165, 199, 23);
        contentPanel.add(chckbxVerticesOfEdges);
        chckbxVerticesOfEdges.addActionListener(e -> {
            SettingsVC.drawVerticesOfEdges = chckbxVerticesOfEdges.isSelected();
            Manager.app.repaint();
        });
        final JCheckBox chckbxFacesOfEdges = new JCheckBox("Faces of Edges");
        chckbxFacesOfEdges.setSelected(SettingsVC.drawFacesOfEdges);
        chckbxFacesOfEdges.setBounds(707, 191, 199, 23);
        contentPanel.add(chckbxFacesOfEdges);
        chckbxFacesOfEdges.addActionListener(e -> {
            SettingsVC.drawFacesOfEdges = chckbxFacesOfEdges.isSelected();
            Manager.app.repaint();
        });
        final JLabel lblPendingValue = new JLabel();
        lblPendingValue.setBounds(719, 546, 241, 15);
        contentPanel.add(lblPendingValue);
        String allPendingValues = "";
        final TIntIterator it = Manager.ref().context().state().pendingValues().iterator();
        while (it.hasNext()) {
            allPendingValues = allPendingValues + it.next() + ", ";
        }
        lblPendingValue.setText("Pending Values: " + allPendingValues);
        final JLabel lblCounterValue = new JLabel();
        lblCounterValue.setBounds(719, 584, 241, 15);
        contentPanel.add(lblCounterValue);
        lblCounterValue.setText("Counter Value: " + Manager.ref().context().state().counter());
        final JLabel lblTempValue = new JLabel();
        lblTempValue.setBounds(719, 624, 241, 15);
        contentPanel.add(lblTempValue);
        lblTempValue.setText("Temp Value: " + Manager.ref().context().state().temp());
    }
    
    public JCheckBox checkBox(final JPanel contentPanel, final int x, final int init_y, final String namePregen, final boolean settingSelected) {
        final JCheckBox checkBox = new JCheckBox(namePregen);
        checkBox.setBounds(x, init_y + this.indexPregen.get(namePregen) * 26, 199, 23);
        contentPanel.add(checkBox);
        checkBox.setSelected(settingSelected);
        return checkBox;
    }
    
    public JComboCheckBox comboCheckBox(final JPanel contentPanel, final int x, final int init_y, final String namePregen, final ArrayList<Boolean> settingSelected, final List<List<TopologyElement>> graphElements) {
        JComboCheckBox comboCheckBox = null;
        if (graphElements.size() > 0) {
            final Vector<JCheckBox> v = new Vector<>();
            for (int i = 0; i < graphElements.size(); ++i) {
                if (settingSelected.size() <= i) {
                    settingSelected.add(false);
                }
                final JCheckBox tempCheckBox = new JCheckBox(namePregen + " " + i, false);
                tempCheckBox.setSelected(settingSelected.get(i));
                v.add(tempCheckBox);
            }
            comboCheckBox = new JComboCheckBox(v);
            comboCheckBox.setBounds(x, init_y + 26 * this.indexPregen.get(namePregen), 100, 23);
            contentPanel.add(comboCheckBox);
        }
        return comboCheckBox;
    }
}
