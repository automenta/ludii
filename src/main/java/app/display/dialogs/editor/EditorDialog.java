// 
// Decompiled by Procyon v0.5.36
// 

package app.display.dialogs.editor;

import app.DesktopApp;
import app.display.dialogs.util.DialogUtil;
import app.game.GameSetupDesktop;
import app.utils.SettingsDesktop;
import graphics.ImageProcessing;
import language.parser.Parser;
import language.parser.SelectionType;
import language.parser.TokenRange;
import main.grammar.Description;
import main.grammar.Report;
import manager.Manager;
import manager.utils.ContextSnapshot;
import manager.utils.SettingsManager;
import util.SettingsVC;
import util.locations.FullLocation;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class EditorDialog extends JDialog
{
    private static final long serialVersionUID = -3636781014267129575L;
    private static final String TAB_STRING = "\t";
    private static final String TAB_REPLACEMENT = "    ";
    public static final int TIMERLENGTH = 500;
    private final JPanel contentPanel;
    private final List<UndoRecord> undoRecords;
    private int undoDescriptionsMarker;
    final EditorHelpData editorHelpData;
    final boolean useColouredText;
    final JTextPane textArea;
    final JLabel verifiedByParser;
    String pasteBuffer;
    SuggestionDialog suggestion;
    boolean trace;
    
    public static void createAndShowGUI(final boolean longDescription, final boolean textColoured, final boolean shortcutsActive) {
        try {
            final EditorDialog dialog = new EditorDialog(longDescription, textColoured, shortcutsActive);
            DialogUtil.initialiseSingletonDialog(dialog, "Editor", null);
            dialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(final WindowEvent e) {
                    DesktopApp.frame().setContentPane(DesktopApp.view());
                    DesktopApp.view().invalidate();
                    Manager.app.repaint();
                    SettingsVC.selectedLocation = new FullLocation(-1);
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private EditorDialog(final boolean longDescription, final boolean useColouredText, final boolean shortcutsActive) {
        super(null, ModalityType.DOCUMENT_MODAL);
        this.contentPanel = new JPanel();
        this.undoRecords = new ArrayList<>();
        this.undoDescriptionsMarker = 0;
        this.editorHelpData = EditorHelpData.get();
        this.pasteBuffer = "";
        this.suggestion = null;
        this.trace = true;
        this.useColouredText = useColouredText;
        this.setBounds(100, 100, 759, 885);
        this.getContentPane().setLayout(new BorderLayout());
        this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.getContentPane().add(this.contentPanel, "Center");
        this.contentPanel.setLayout(new BorderLayout(0, 0));
        this.textArea = createTextPane();
        final JPanel noWrapPanel = new JPanel(new BorderLayout());
        noWrapPanel.add(this.textArea);
        final JScrollPane scrollPane = new JScrollPane(noWrapPanel);
        this.contentPanel.add(scrollPane);
        (this.verifiedByParser = new JLabel()).setHorizontalAlignment(4);
        this.getContentPane().add(this.verifiedByParser, "North");
        final JPanel bottomPane = new JPanel();
        bottomPane.setLayout(new BoxLayout(bottomPane, 2));
        this.getContentPane().add(bottomPane, "South");
        final JPanel buttonPaneLeft = new JPanel();
        buttonPaneLeft.setLayout(new FlowLayout(0));
        bottomPane.add(buttonPaneLeft);
        final JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(2));
        bottomPane.add(buttonPane);
        this.addNewButton(buttonPaneLeft);
        this.addCompileButton(buttonPane);
        this.addSaveButton(buttonPane);
        this.addNewFileButton(DesktopApp.playerApp(), buttonPane);
        addCancelButton(buttonPane);
        final String gameDescription = getGameDescription(longDescription);
        this.setText(gameDescription.replace("\r", ""));
        if (useColouredText) {
            this.setTextUpdateMonitor();
        }
        if (shortcutsActive) {
            this.addUndoHandler();
        }
        this.addMouseListener();
    }
    
    private static JTextPane createTextPane() {
        final JTextPane jTextPane = new JTextPane();
        jTextPane.getDocument().putProperty("__EndOfLine__", "\n");
        jTextPane.setFocusTraversalKeysEnabled(false);
        jTextPane.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(final FocusEvent e) {
                jTextPane.getCaret().setVisible(true);
            }
            
            @Override
            public void focusLost(final FocusEvent e) {
                jTextPane.getCaret().setVisible(true);
            }
        });
        final StyledDocument styledDoc = jTextPane.getStyledDocument();
        if (styledDoc instanceof AbstractDocument) {
            final AbstractDocument doc = (AbstractDocument)styledDoc;
            doc.setDocumentFilter(new DocumentFilter() {
                @Override
                public void insertString(final FilterBypass fb, final int offset, final String text, final AttributeSet attrs) throws BadLocationException {
                    super.insertString(fb, offset, text.replace("\t", "    "), attrs);
                }
                
                @Override
                public void replace(final FilterBypass fb, final int offset, final int length, final String text, final AttributeSet attrs) throws BadLocationException {
                    if (text.equals("\t")) {
                        EditorDialog.indentRange(jTextPane);
                        return;
                    }
                    super.replace(fb, offset, length, text.replace("\t", "    "), attrs);
                }
            });
        }
        return jTextPane;
    }
    
    private void addUndoHandler() {
        this.undoRecords.add(new UndoRecord(this.textArea));
        final Timer undoRecordTimer = new Timer(500, arg0 -> EditorDialog.this.storeUndoText());
        undoRecordTimer.setRepeats(false);
        this.textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(final KeyEvent e) {
                EventQueue.invokeLater(() -> {
                    if (EditorDialog.this.trace) {
                        System.out.println(">>EVENT: textArea/keypressed");
                    }
                    undoRecordTimer.stop();
                    EditorDialog.this.storeUndoText();
                    switch (EditorActions.fromKeyEvent(e)) {
                        case DELETE_LINE: {
                            if (EditorDialog.this.trace) {
                                System.out.println(">>EVENT: textArea/keypressed delete line");
                            }
                            EditorDialog.this.storeUndoText();
                            EditorDialog.this.deleteLine();
                            break;
                        }
                        case REDO: {
                            if (EditorDialog.this.trace) {
                                System.out.println(">>EVENT: textArea/keypressed redo");
                            }
                            EditorDialog.this.storeUndoText();
                            EditorDialog.this.redo();
                            break;
                        }
                        case UNDO: {
                            if (EditorDialog.this.trace) {
                                System.out.println(">>EVENT: textArea/keypressed undo");
                            }
                            EditorDialog.this.storeUndoText();
                            EditorDialog.this.undo();
                            break;
                        }
                        case NO_ACTION: {
                            if (EditorDialog.this.trace) {
                                System.out.println(">>EVENT: textArea/keypressed ignored " + KeyEvent.getKeyText(e.getKeyChar()));
                                break;
                            }
                            break;
                        }
                        case COPY_SELECTION: {
                            EditorDialog.this.copySelection();
                            if (EditorDialog.this.trace) {
                                System.out.println(">>EVENT: textArea/keypressed copy selection " + EditorDialog.this.pasteBuffer);
                                break;
                            }
                            break;
                        }
                        case REMOVE_SELECTION: {
                            EditorDialog.this.storeUndoText();
                            EditorDialog.this.removeSelection();
                            if (EditorDialog.this.trace) {
                                System.out.println(">>EVENT: textArea/keypressed remove selection " + EditorDialog.this.pasteBuffer);
                                break;
                            }
                            break;
                        }
                        case PASTE_BUFFER: {
                            EditorDialog.this.storeUndoText();
                            EditorDialog.this.pasteBuffer();
                            if (EditorDialog.this.trace) {
                                System.out.println(">>EVENT: textArea/keypressed paste " + EditorDialog.this.pasteBuffer);
                                break;
                            }
                            break;
                        }
                        case AUTOSUGGEST: {
                            if (EditorDialog.this.trace) {
                                System.out.println(">>EVENT: textArea/keypressed autosuggest");
                            }
                            EditorDialog.this.storeUndoText();
                            EditorDialog.this.showAutosuggest(TextPaneUtils.cursorCoords(EditorDialog.this.textArea), true);
                            break;
                        }
                    }
                    undoRecordTimer.start();
                });
            }
            
            @Override
            public void keyTyped(final KeyEvent e) {
                if ((e.getModifiers() & 0x2) != 0x0) {
                    return;
                }
                if (e.getKeyChar() == '\uffff') {
                    return;
                }
                if (Character.isWhitespace(e.getKeyChar())) {
                    return;
                }
                if (EditorDialog.this.trace) {
                    System.out.println(">>EVENT: textArea/keytyped - maybe showing autosuggest");
                }
                EditorDialog.this.storeUndoText();
                EventQueue.invokeLater(() -> EditorDialog.this.showAutosuggest(TextPaneUtils.cursorCoords(EditorDialog.this.textArea), true));
            }
        });
    }
    
    void deleteLine() {
        final int newCaretPosition = TextPaneUtils.startOfCaretCurrentRow(this.textArea);
        final int caretRowNumber = TextPaneUtils.getCaretRowNumber(this.textArea);
        final String[] lines = this.textAreaFullDocument().split("\n");
        final StringBuilder newDesc = new StringBuilder();
        for (int s = 0; s < lines.length; ++s) {
            if (s != caretRowNumber - 1) {
                newDesc.append(lines[s]).append("\n");
            }
        }
        this.setText(newDesc.toString());
        this.textArea.setCaretPosition(newCaretPosition);
    }
    
    void storeUndoText() {
        if (this.undoRecords.get(this.undoDescriptionsMarker).ignoreChanges(this.textArea)) {
            return;
        }
        for (int i = this.undoRecords.size() - 1; i > this.undoDescriptionsMarker; --i) {
            this.undoRecords.remove(i);
        }
        ++this.undoDescriptionsMarker;
        this.undoRecords.add(new UndoRecord(this.textArea));
    }
    
    void redo() {
        if (this.undoDescriptionsMarker >= this.undoRecords.size() - 1) {
            return;
        }
        ++this.undoDescriptionsMarker;
        this.undoRecords.get(this.undoDescriptionsMarker).apply(this.textArea);
    }
    
    void undo() {
        if (this.undoDescriptionsMarker <= 0) {
            return;
        }
        --this.undoDescriptionsMarker;
        this.undoRecords.get(this.undoDescriptionsMarker).apply(this.textArea);
    }
    
    private void addMouseListener() {
        this.textArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                if (EditorDialog.this.trace) {
                    System.out.println(">>EVENT: textArea/mouseClicked");
                }
                if (SwingUtilities.isRightMouseButton(e)) {
                    if (EditorDialog.this.trace) {
                        System.out.println(">>EVENT: textArea/mouseClicked/right click");
                    }
                    EditorDialog.this.showAutosuggest(e.getPoint(), false);
                }
                else if (e.getClickCount() == 1) {
                    if (EditorDialog.this.trace) {
                        System.out.println(">>EVENT: textArea/mouseClicked/single click");
                    }
                    if (EditorDialog.this.suggestion != null) {
                        EditorDialog.this.suggestion.setVisible(false);
                    }
                }
                else if (e.getClickCount() == 2) {
                    if (EditorDialog.this.trace) {
                        System.out.println(">>EVENT: textArea/mouseClicked/double click");
                    }
                    final int caretPos = EditorDialog.this.textArea.getCaretPosition();
                    final TokenRange range = Parser.tokenScope(EditorDialog.this.textArea.getText(), caretPos, true, SelectionType.SELECTION);
                    if (range != null) {
                        EditorDialog.this.textArea.setSelectionStart(range.from());
                        EditorDialog.this.textArea.setSelectionEnd(range.to());
                    }
                }
            }
        });
    }
    
    void showAutosuggest(final Point point, final boolean usePartial) {
        if (this.suggestion != null) {
            this.suggestion.setVisible(false);
        }
        if (!SettingsManager.editorAutocomplete) {
            return;
        }
        System.out.println("### Showing Autosuggest");
        if (!usePartial) {
            final int posn = this.textArea.viewToModel(point);
            this.textArea.setCaretPosition(posn);
        }
        try {
            final Rectangle rect = this.textArea.modelToView(this.textArea.getCaretPosition());
            final Point screenPos = new Point(rect.x, rect.y);
            SwingUtilities.convertPointToScreen(screenPos, this.textArea);
            final Point point2 = screenPos;
            point2.y += rect.height;
            this.suggestion = new SuggestionDialog(this, screenPos, usePartial);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static String getGameDescription(final boolean longDescription) {
        String gameDescription = "";
        if (longDescription) {
            final String fullDescription = DesktopApp.matchDescriptionFull();
            gameDescription = ((fullDescription == null || fullDescription.isEmpty()) ? ContextSnapshot.getContext().game().description().expanded() : fullDescription);
        }
        else {
            final String shortDescription = DesktopApp.matchDescriptionShort();
            gameDescription = ((shortDescription == null || shortDescription.isEmpty()) ? ContextSnapshot.getContext().game().description().raw() : shortDescription);
        }
        return gameDescription;
    }
    
    private void setTextUpdateMonitor() {
        final Timer colourRecordTimer = new Timer(500, arg0 -> EditorDialog.this.setText(EditorDialog.this.textAreaFullDocument()));
        colourRecordTimer.setRepeats(false);
        this.textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(final KeyEvent e) {
                EventQueue.invokeLater(() -> {
                    colourRecordTimer.stop();
                    colourRecordTimer.start();
                });
            }
        });
    }
    
    final String textAreaFullDocument() {
        return this.textArea.getText();
    }
    
    final String documentForSave() {
        final String full = this.textAreaFullDocument();
        return full.replace("\n", System.lineSeparator());
    }
    
    private static JButton addButton(final JPanel buttonPane, final String label, final ActionListener listener) {
        final JButton button = new JButton(label);
        button.setActionCommand(label);
        button.addActionListener(listener);
        buttonPane.add(button);
        return button;
    }
    
    private static JButton addCancelButton(final JPanel buttonPane) {
        final ActionListener listener = e -> {
            final Component component = (Component)e.getSource();
            final JDialog dialog = (JDialog)SwingUtilities.getRoot(component);
            dialog.dispose();
        };
        return addButton(buttonPane, "Cancel", listener);
    }
    
    private JButton addNewFileButton(final DesktopApp app, final JPanel buttonPane) {
        final ActionListener listener = e -> EditorDialog.saveGameDescription(app, EditorDialog.this.documentForSave());
        return addButton(buttonPane, "Save new file", listener);
    }
    
    private JButton addSaveButton(final JPanel buttonPane) {
        final ActionListener listener = e -> {
            try (final PrintWriter out = new PrintWriter(Manager.savedLudName())) {
                out.println(EditorDialog.this.documentForSave());
                System.out.println(Manager.savedLudName() + " overridden");
            }
            catch (FileNotFoundException e2) {
                System.out.println("You cannot override a game description loaded from memory. Use the 'Save new file' option");
            }
        };
        return addButton(buttonPane, "Override existing file", listener);
    }
    
    private JButton addNewButton(final JPanel buttonPane) {
        final ActionListener listener = e -> EditorDialog.this.setText("(game <string> [<players>] [<mode>] [<equipment>] [<rules>])");
        final JButton newButton = addButton(buttonPane, "New", listener);
        return newButton;
    }
    
    private JButton addCompileButton(final JPanel buttonPane) {
        final ActionListener listener = e -> GameSetupDesktop.compileAndShowGame(EditorDialog.this.textAreaFullDocument(), false, true);
        final JButton okButton = addButton(buttonPane, "Compile", listener);
        this.getRootPane().setDefaultButton(okButton);
        return okButton;
    }
    
    void setText(final String gameDescription) {
        final int pos = this.textArea.getCaretPosition();
        final int selStart = this.textArea.getSelectionStart();
        final int selEnd = this.textArea.getSelectionEnd();
        if (this.useColouredText) {
            this.textArea.setText("");
            final String[] tokens = new LudiiTokeniser(gameDescription).getTokens();
            int bracketCount = 0;
            int curlyCount = 0;
            boolean inAngle = false;
            EditorTokenType lastTokenType = null;
            for (final String token : tokens) {
                final EditorTokenType ttype = LudiiTokeniser.typeForToken(token, inAngle, lastTokenType);
                switch (ttype) {
                    case OPEN_CURLY: {
                        appendToPane(this.textArea, token, EditorLookAndFeel.bracketColourByDepthAndType(ttype, curlyCount), ttype.isBold());
                        ++curlyCount;
                        break;
                    }
                    case OPEN_ROUND:
                    case OPEN_SQUARE: {
                        appendToPane(this.textArea, token, EditorLookAndFeel.bracketColourByDepthAndType(ttype, bracketCount), ttype.isBold());
                        ++bracketCount;
                        break;
                    }
                    case CLOSE_CURLY: {
                        --curlyCount;
                        appendToPane(this.textArea, token, EditorLookAndFeel.bracketColourByDepthAndType(ttype, curlyCount), ttype.isBold());
                        break;
                    }
                    case CLOSE_ROUND:
                    case CLOSE_SQUARE: {
                        --bracketCount;
                        appendToPane(this.textArea, token, EditorLookAndFeel.bracketColourByDepthAndType(ttype, bracketCount), ttype.isBold());
                        break;
                    }
                    case OPEN_ANGLE: {
                        inAngle = true;
                        appendToPane(this.textArea, token, ttype.fgColour(), ttype.isBold());
                        break;
                    }
                    case CLOSE_ANGLE: {
                        appendToPane(this.textArea, token, ttype.fgColour(), ttype.isBold());
                        inAngle = false;
                        break;
                    }
                    case WHITESPACE:
                    case INT:
                    case FLOAT:
                    case OTHER:
                    case STRING:
                    case LABEL:
                    case CLASS:
                    case ENUM:
                    case RULE: {
                        appendToPane(this.textArea, token, ttype.fgColour(), ttype.isBold());
                        break;
                    }
                }
                lastTokenType = ttype;
            }
        }
        else {
            this.textArea.setText(gameDescription);
        }
        this.textArea.setCaretPosition(pos);
        if (selStart != selEnd) {
            this.textArea.setSelectionStart(selStart);
            this.textArea.setSelectionEnd(selEnd);
        }
        this.checkParseState(gameDescription);
    }
    
    private final void checkParseState(final String gameStr) {
        try {
            final Description gameDescription = new Description(gameStr);
            final boolean success = Parser.parseTest(gameDescription, SettingsManager.userSelections, new Report(), false);
            this.wasVerifiedByParser(success);
        }
        catch (Exception e) {
            this.wasVerifiedByParser(false);
        }
    }
    
    private void wasVerifiedByParser(final boolean b) {
        final int r = 7;
        final Color markerColour = b ? Color.GREEN : Color.RED;
        final BufferedImage image = new BufferedImage(28, 21, 2);
        final Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        ImageProcessing.ballImage(g2d, 7, 7, 7, markerColour);
        final ImageIcon icon = new ImageIcon(image);
        this.verifiedByParser.setIcon(icon);
    }
    
    public static String saveGameDescription(final DesktopApp app, final String desc) {
        final int fcReturnVal = DesktopApp.saveGameFileChooser().showSaveDialog(DesktopApp.frame());
        if (fcReturnVal == 0) {
            File file = DesktopApp.saveGameFileChooser().getSelectedFile();
            final String filePath = file.getAbsolutePath();
            if (!filePath.endsWith(".lud")) {
                file = new File(filePath + ".lud");
            }
            try (final PrintWriter out = new PrintWriter(file.getAbsolutePath())) {
                out.println(desc);
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return filePath;
        }
        return null;
    }
    
    private static void appendToPane(final JTextPane tp, final String msg, final Color c, final boolean isBold) {
        final StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);
        aset = sc.addAttribute(aset, StyleConstants.Size, SettingsDesktop.editorFontSize);
        aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Arial");
        aset = sc.addAttribute(aset, StyleConstants.Bold, isBold);
        aset = sc.addAttribute(aset, StyleConstants.Alignment, 3);
        final int len = tp.getDocument().getLength();
        tp.setCaretPosition(len);
        tp.setCharacterAttributes(aset, false);
        tp.replaceSelection(msg);
    }
    
    final String getText() {
        return this.textArea.getText();
    }
    
    final int getCaretPosition() {
        return this.textArea.getCaretPosition();
    }
    
    final void applyBackspace() {
        final int pos = this.textArea.getCaretPosition();
        this.textArea.select(pos - 1, pos);
        this.textArea.replaceSelection("");
        this.setText(this.textArea.getText());
    }
    
    final void applyDelete() {
        final int pos = this.textArea.getCaretPosition();
        this.textArea.select(pos, pos + 1);
        this.textArea.replaceSelection("");
        this.setText(this.textArea.getText());
    }
    
    final void insertCharacter(final char keyChar) {
        final String keyVal = Character.toString(keyChar);
        TextPaneUtils.insertAtCaret(this.textArea, keyVal);
        this.setText(this.textArea.getText());
    }
    
    final void cursorLeft() {
        final int pos = this.textArea.getCaretPosition();
        if (pos > 0) {
            this.textArea.setCaretPosition(pos - 1);
        }
        System.out.println("LEFT");
    }
    
    final void cursorRight() {
        final int pos = this.textArea.getCaretPosition();
        if (pos < this.textArea.getText().length()) {
            this.textArea.setCaretPosition(pos + 1);
        }
        System.out.println("RIGHT");
    }
    
    final void replaceTokenScopeWith(final String substitution, final boolean isPartial) {
        try {
            final int caretPos = this.textArea.getCaretPosition();
            final TokenRange range = Parser.tokenScope(this.textArea.getText(), caretPos, isPartial, isPartial ? SelectionType.TYPING : SelectionType.CONTEXT);
            if (range == null) {
                System.out.println("No range available");
                return;
            }
            final String pre = this.textArea.getText(0, range.from());
            final String post = this.textArea.getText(range.to(), this.textArea.getText().length() - range.to());
            final String after = pre + substitution + post;
            this.setText(after);
            this.textArea.setCaretPosition(pre.length() + substitution.length());
        }
        catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    
    final void pasteBuffer() {
        if (!this.pasteBuffer.isEmpty()) {
            this.textArea.replaceSelection(this.pasteBuffer);
            this.setText(this.textArea.getText());
        }
    }
    
    final void removeSelection() {
        final int start = this.textArea.getSelectionStart();
        final int end = this.textArea.getSelectionEnd();
        if (start > end) {
            this.pasteBuffer = this.textArea.getSelectedText();
            this.textArea.replaceSelection("");
            this.setText(this.textArea.getText());
        }
    }
    
    final void copySelection() {
        final int start = this.textArea.getSelectionStart();
        final int end = this.textArea.getSelectionEnd();
        if (start > end) {
            this.pasteBuffer = this.textArea.getSelectedText();
        }
    }
    
    static final void indentRange(final JTextPane textArea) {
        System.out.println("### INDENT ###");
        final int start = textArea.getSelectionStart();
        final int end = textArea.getSelectionEnd();
        System.out.println("start: " + start + ", end: " + end);
        if (start < end) {
            final String test = textArea.getSelectedText();
            final String[] lines = test.split("\\R");
            final String fixed = "    " + String.join("\n    ", lines);
            textArea.replaceSelection(fixed);
            textArea.setSelectionStart(start);
            textArea.setSelectionEnd(start + fixed.length());
        }
    }
    
    void returnFocus() {
        this.textArea.requestFocus();
    }
    
    public String charsBeforeCursor() {
        if (this.textArea.getText().isEmpty()) {
            return "";
        }
        final int pos = this.textArea.getCaretPosition();
        int start = pos - 1;
        try {
            while (start > 0 && Character.isLetterOrDigit(this.textArea.getText(start - 1, 1).charAt(0))) {
                --start;
            }
            final String result = this.textArea.getText(Math.max(0, start), pos - start);
            System.out.println("charsBeforeCursor returning " + start + ":" + pos + ":" + result);
            return result;
        }
        catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
