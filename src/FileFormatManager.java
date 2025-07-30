import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.management.JMException;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.text.*;
import javax.swing.text.DefaultEditorKit.*;
import javax.swing.text.StyledEditorKit.AlignmentAction;
import javax.swing.text.StyledEditorKit.FontFamilyAction;
import javax.swing.text.StyledEditorKit.FontSizeAction;
import javax.swing.text.rtf.RTFEditorKit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import java.util.List;

import java.awt.*;
import java.awt.event.*;

/*
Used to:
- Open files
- Generate Window
- Facilitate user interaction
- Save and format input
-   ...
*/
public class FileFormatManager {
    Frame frame;
    TextWindow textWindow;
    FormatingUI formatingUI;
    FileUI fileUI;
    private static final List<String> FONT_LIST = Arrays
            .asList(new String[] { "Arial", "Calibri", "Cambria", "Courier New", "Comic Sans MS", "Dialog", "Georgia",
                    "Helevetica", "Lucida Sans", "Monospaced", "Tahoma", "Times New Roman", "Verdana" });

    /*
     * 0 = English
     * 1 = German
     */
    String[] languages = { "English", "Deutsch" };
    int currentLanguage = 0;

    // undo and redo
    public Document editorPaneDocument;
    protected UndoHandler undoHandler = new UndoHandler();
    protected UndoManager undoManager = new UndoManager();
    private UndoAction undoAction = null;
    private RedoAction redoAction = null;

    public FileFormatManager() {
        frame = new Frame("Nova Text Editor");
        frame.setLayout(new BorderLayout());

        // TEMPORARY START WITH EMPTY TEXT WINDOW
        textWindow = newTextWindow();
        textWindow.setContentType("text/rtf");
        textWindow.setDocument(new DefaultStyledDocument());
        JScrollPane editorScrollPane = new JScrollPane(textWindow);
        frame.add(editorScrollPane, BorderLayout.CENTER);

        // Adding the File UI to the Window
        fileUI = new FileUI();
        JPanel fileUIPanel = fileUI.createPanel();
        frame.add(fileUIPanel, BorderLayout.NORTH);

        // Adding the Formating UI to the Window
        formatingUI = new FormatingUI();
        JPanel formatingPanel = formatingUI.createPanel();
        frame.add(formatingPanel, BorderLayout.WEST);

        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setResizable(true);
        frame.setVisible(true);

        // Using WindowListener for closing the window
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });
        editorPaneDocument = textWindow.getDocument();

        editorPaneDocument.addUndoableEditListener(undoHandler);

        KeyStroke undoKeystroke = KeyStroke.getKeyStroke(KeyEvent.VK_Z, Event.META_MASK);
        KeyStroke redoKeystroke = KeyStroke.getKeyStroke(KeyEvent.VK_Y, Event.META_MASK);

        undoAction = new UndoAction();
        textWindow.getInputMap().put(undoKeystroke, "undoKeystroke");
        textWindow.getActionMap().put("undoKeystroke", undoAction);

        redoAction = new RedoAction();
        textWindow.getInputMap().put(redoKeystroke, "redoKeystroke");
        textWindow.getActionMap().put("redoKeystroke", redoAction);

    }

    public static List<Component> getAllComponents(final Container c) {
        Component[] comps = c.getComponents();
        List<Component> compList = new ArrayList<Component>();
        for (Component comp : comps) {
            compList.add(comp);
            if (comp instanceof Container)
                compList.addAll(getAllComponents((Container) comp));
        }
        return compList;
    }

    public void updateLanguage() {
        JPanel formatPanel = formatingUI.getJPanel();

        List<Component> comps = getAllComponents(frame);
        for (Component component : comps) {
            if (component instanceof JCheckBox) {
                System.out.println("CHECKBOX");
                JCheckBox myCheck = (JCheckBox) component;
                myCheck.setText(guiLanguageDicitonary.get((String) myCheck.getName())[currentLanguage]);
            } else if (component instanceof JLabel && component.getName() != null) {
                System.out.println("JLabel " + component.getName() + " \n");
                JLabel myLabel = (JLabel) component;
                myLabel.setText(guiLanguageDicitonary.get(myLabel.getName())[currentLanguage]);
            } else if (component instanceof JComboBox) {
                System.out.println("Combo Box \n");
                JComboBox<String> myBox = (JComboBox<String>) component;
                String[] options = myBox.getName().split(" ");
                myBox.removeAllItems();
                for (String string : options) {
                    myBox.addItem(guiLanguageDicitonary.get(string)[currentLanguage]);
                }
                formatPanel.add(myBox);
            } else if (component instanceof JButton && component.getName() != null) {
                JButton myButton = (JButton) component;
                myButton.setText(guiLanguageDicitonary.get(myButton.getName())[currentLanguage]);
            }

        }
    }

    public void quit() {
        System.exit(0);
    }

    public void openFile() {
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setDialogTitle("Choose destination.");
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int option = jfc.showOpenDialog(frame);

        if (option == JFileChooser.APPROVE_OPTION) {
            File selectedFile = jfc.getSelectedFile();

            RTFEditorKit kit = new RTFEditorKit();
            try {
                textWindow.setContentType("text/rtf");
                InputStream inputStream = new FileInputStream(selectedFile);
                DefaultStyledDocument styledDocument = new DefaultStyledDocument(new StyleContext());
                kit.read(inputStream, styledDocument, 0);
                textWindow.setDocument(styledDocument);
            } catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(frame, "Error opening file: " + e.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (IOException | javax.swing.text.BadLocationException e) {
                JOptionPane.showMessageDialog(frame, "Error opening file: " + e.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        }
    }

    public void saveFile() {
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setDialogTitle("Choose destination.");
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int option = jfc.showSaveDialog(frame);

        if (option == JFileChooser.APPROVE_OPTION) {
            File selectedFile = jfc.getSelectedFile();
            // Ensure it has the .rtf extension
            if (!selectedFile.getName().toLowerCase().endsWith(".rtf")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".rtf");
            }

            StyledDocument doc = (StyledDocument) textWindow.getDocument();
            RTFEditorKit rtfKit = new RTFEditorKit();

            try (OutputStream out = new FileOutputStream(selectedFile)) {
                rtfKit.write(out, doc, 0, doc.getLength());
                JOptionPane.showMessageDialog(frame, "File saved successfully!");
            } catch (IOException | javax.swing.text.BadLocationException e) {
                JOptionPane.showMessageDialog(frame, "Error saving file: " + e.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Text Interface for writing and displaying formatted Text
    private class TextWindow extends JTextPane {
        public TextWindow(String[] initString, String[] initStyles) {

            StyledDocument doc = this.getStyledDocument();

            // Load the text pane with styled text.
            try {
                for (int i = 0; i < initString.length; i++) {
                    doc.insertString(doc.getLength(), initString[i],
                            doc.getStyle(initStyles[i]));
                }
            } catch (BadLocationException ble) {
                System.err.println("Couldn't insert initial text into text pane.");
            }

        }
    }

    public TextWindow newTextWindow() {
        String[] defaultText = {};
        String[] defaultStyle = {};
        TextWindow t = new TextWindow(defaultText, defaultStyle);
        t.setBackground(Color.white);
        return t;
    }

    // GUI for interacting with Files
    private class FileUI implements GUI {
        public JPanel createPanel() {
            JPanel fileManagingLayout = new JPanel();
            fileManagingLayout.setLayout(new BoxLayout(fileManagingLayout, BoxLayout.LINE_AXIS));

            JButton saveButton = new JButton();
            // Asign Key as name to retrive for language change
            saveButton.setName("btn_save_");
            saveButton.setText(guiLanguageDicitonary.get(saveButton.getName())[currentLanguage]);
            saveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    saveFile();
                }
            });

            fileManagingLayout.add(saveButton);

            JButton openButton = new JButton();
            openButton.setName("btn_open_");
            openButton.setText(guiLanguageDicitonary.get(openButton.getName())[currentLanguage]);
            openButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    openFile();
                }
            });

            fileManagingLayout.add(openButton);

            JButton quitButton = new JButton();
            quitButton.setName("btn_quit_");
            quitButton.setText(guiLanguageDicitonary.get(quitButton.getName())[currentLanguage]);
            quitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    quit();
                }
            });

            fileManagingLayout.add(quitButton);

            JButton languageButton = new JButton();
            languageButton.setName("btn_language_");
            languageButton.setText(guiLanguageDicitonary.get(languageButton.getName())[currentLanguage]);
            languageButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    JComboBox<String> jbox = new JComboBox<>(languages);

                    int result = JOptionPane.showConfirmDialog(
                            frame,
                            jbox,
                            "Select Language",
                            JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.PLAIN_MESSAGE);

                    if (result == JOptionPane.OK_OPTION) {

                        currentLanguage = jbox.getSelectedIndex();
                        updateLanguage();

                    }
                }
            });

            fileManagingLayout.add(languageButton);

            JButton undoButton = new JButton(undoAction);
            undoButton.setName("btn_undo_");
            undoButton.setText(guiLanguageDicitonary.get(undoButton.getName())[currentLanguage]);

            fileManagingLayout.add(undoButton);

            JButton redoButton = new JButton(redoAction);
            redoButton.setName("btn_redo_");
            redoButton.setText(guiLanguageDicitonary.get(redoButton.getName())[currentLanguage]);

            fileManagingLayout.add(redoButton);

            return fileManagingLayout;
        }
    }

    // java undo and redo action classes

    public class UndoHandler implements UndoableEditListener {

        /**
         * Messaged when the Document has created an edit, the edit is added to
         * <code>undoManager</code>, an instance of UndoManager.
         */
        public void undoableEditHappened(UndoableEditEvent e) {
            undoManager.addEdit(e.getEdit());
            undoAction.update();
            redoAction.update();
        }
    }

    public class UndoAction extends AbstractAction {
        public UndoAction() {
            super("Undo");
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {
            try {
                undoManager.undo();
            } catch (CannotUndoException ex) {
                // TODO deal with this
                // ex.printStackTrace();
            }
            update();
            redoAction.update();
        }

        protected void update() {
            if (undoManager.canUndo()) {
                setEnabled(true);
                putValue(Action.NAME, undoManager.getUndoPresentationName());
            } else {
                setEnabled(false);
                putValue(Action.NAME, "Undo");
            }
        }
    }

    public class RedoAction extends AbstractAction {
        public RedoAction() {
            super("Redo");
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {
            try {
                undoManager.redo();
            } catch (CannotRedoException ex) {
                // TODO deal with this
                ex.printStackTrace();
            }
            update();
            undoAction.update();
        }

        protected void update() {
            if (undoManager.canRedo()) {
                setEnabled(true);
                putValue(Action.NAME, undoManager.getRedoPresentationName());
            } else {
                setEnabled(false);
                putValue(Action.NAME, "Redo");
            }
        }
    }

    // GUI for interacting with the TextWindows Format
    private class FormatingUI implements GUI {
        JPanel formatManagingLayout;

        public JPanel createPanel() {
            formatManagingLayout = new JPanel();
            formatManagingLayout.setLayout(new BoxLayout(formatManagingLayout, BoxLayout.Y_AXIS));

            // Save Button Names in Array for easy language changes
            // TO DO FORMAT TO HEADING
            JLabel formatingLabel = new JLabel();
            formatingLabel.setName("lbl_formating_");
            formatingLabel.setText(guiLanguageDicitonary.get((String) formatingLabel.getName())[currentLanguage]);
            formatManagingLayout.add(formatingLabel);

            /*
             * Buttons
             */

            JButton cutButton = new JButton(new DefaultEditorKit.CutAction());
            cutButton.setName("btn_cut_");
            cutButton.setHideActionText(true);
            cutButton.setText(guiLanguageDicitonary.get((String) cutButton.getName())[currentLanguage]);
            formatManagingLayout.add(cutButton);

            JButton copyButton = new JButton(new DefaultEditorKit.CopyAction());
            copyButton.setName("btn_copy_");
            copyButton.setHideActionText(true);
            copyButton.setText(guiLanguageDicitonary.get((String) copyButton.getName())[currentLanguage]);
            formatManagingLayout.add(copyButton);

            JButton pasteButton = new JButton(new DefaultEditorKit.PasteAction());
            pasteButton.setHideActionText(true);
            pasteButton.setName("btn_paste_");
            pasteButton.setText(guiLanguageDicitonary.get((String) pasteButton.getName())[currentLanguage]);
            formatManagingLayout.add(pasteButton);

            JButton textColourFGButton = new JButton();
            textColourFGButton.setName("btn_textColour_");
            textColourFGButton.setHideActionText(true);
            textColourFGButton
                    .setText(guiLanguageDicitonary.get((String) textColourFGButton.getName())[currentLanguage]);
            textColourFGButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    Color newColour = JColorChooser.showDialog(textColourFGButton, "", new Color(0));
                    SimpleAttributeSet attributeSet = new SimpleAttributeSet();
                    StyleConstants.setForeground(attributeSet, newColour);
                    textWindow.setCharacterAttributes(attributeSet, false);
                }
            });
            formatManagingLayout.add(textColourFGButton);

            JButton textColourBGButton = new JButton();
            textColourBGButton.setName("btn_textColourBG_");
            textColourBGButton.setHideActionText(true);
            textColourBGButton
                    .setText(guiLanguageDicitonary.get((String) textColourBGButton.getName())[currentLanguage]);
            textColourBGButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    Color newColour = JColorChooser.showDialog(textColourBGButton, "", new Color(255));
                    SimpleAttributeSet attributeSet = new SimpleAttributeSet();
                    StyleConstants.setBackground(attributeSet, newColour);
                    textWindow.setCharacterAttributes(attributeSet, false);
                }
            });
            formatManagingLayout.add(textColourBGButton);

            /*
             * Sliders
             */

            int defaultTextSize = 12;

            JLabel sliderLabel = new JLabel();
            sliderLabel.setName("lbl_textSize_");
            sliderLabel.setText(guiLanguageDicitonary.get((String) sliderLabel.getName())[currentLanguage]);
            formatManagingLayout.add(sliderLabel);

            JLabel textSizeLabel = new JLabel();
            textSizeLabel.setText(Integer.toString(defaultTextSize));
            formatManagingLayout.add(textSizeLabel);

            JSlider textSizeSlider = new JSlider(1, 50, defaultTextSize);
            textSizeSlider.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    int newFontSize = textSizeSlider.getValue();
                    textSizeLabel.setText(Integer.toString(newFontSize));

                    StyledDocument doc = textWindow.getStyledDocument();
                    int start = textWindow.getSelectionStart();
                    int end = textWindow.getSelectionEnd();

                    if (start == end) {
                        // No selection, apply style to input attributes
                        SimpleAttributeSet attr = new SimpleAttributeSet();
                        StyleConstants.setFontSize(attr, newFontSize);
                        textWindow.setCharacterAttributes(attr, false);
                    } else {
                        // Apply font size to the selected text
                        MutableAttributeSet attr = new SimpleAttributeSet();
                        StyleConstants.setFontSize(attr, newFontSize);
                        doc.setCharacterAttributes(start, end - start, attr, false);
                    }
                }
            });

            formatManagingLayout.add(textSizeSlider);

            // Checkbox
            /*
             * TO-DO put Item Listener in own class (But it's like 10pm and I am tired)
             */
            JCheckBox boldBox = new JCheckBox();
            boldBox.setName("chk_bold_");
            boldBox.setText(guiLanguageDicitonary.get((String) boldBox.getName())[currentLanguage]);
            boldBox.addItemListener(new ItemListener() {
                StyledDocument doc = textWindow.getStyledDocument();
                int start = textWindow.getSelectionStart();
                int end = textWindow.getSelectionEnd();

                @Override
                public void itemStateChanged(ItemEvent event) {
                    if (start == end) {
                        // No selection, apply style to input attributes
                        SimpleAttributeSet attr = new SimpleAttributeSet();
                        StyleConstants.setBold(attr, event.getStateChange() == ItemEvent.SELECTED);
                        textWindow.setCharacterAttributes(attr, false);
                    } else {
                        // Apply font size to the selected text
                        MutableAttributeSet attr = new SimpleAttributeSet();
                        StyleConstants.setBold(attr, event.getStateChange() == ItemEvent.SELECTED);
                        doc.setCharacterAttributes(start, end - start, attr, false);
                    }
                }
            });
            formatManagingLayout.add(boldBox);

            JCheckBox italicBox = new JCheckBox();
            italicBox.setName("chk_italic_");
            italicBox.setText(guiLanguageDicitonary.get((String) italicBox.getName())[currentLanguage]);
            italicBox.addItemListener(new ItemListener() {
                StyledDocument doc = textWindow.getStyledDocument();
                int start = textWindow.getSelectionStart();
                int end = textWindow.getSelectionEnd();

                @Override
                public void itemStateChanged(ItemEvent event) {
                    if (start == end) {
                        // No selection, apply style to input attributes
                        SimpleAttributeSet attr = new SimpleAttributeSet();
                        StyleConstants.setItalic(attr, event.getStateChange() == ItemEvent.SELECTED);
                        textWindow.setCharacterAttributes(attr, false);
                    } else {
                        // Apply font size to the selected text
                        MutableAttributeSet attr = new SimpleAttributeSet();
                        StyleConstants.setItalic(attr, event.getStateChange() == ItemEvent.SELECTED);
                        doc.setCharacterAttributes(start, end - start, attr, false);
                    }
                }
            });
            formatManagingLayout.add(italicBox);

            JCheckBox underlinedBox = new JCheckBox();
            underlinedBox.setName("chk_underline_");
            underlinedBox.setText(guiLanguageDicitonary.get((String) underlinedBox.getName())[currentLanguage]);
            underlinedBox.addItemListener(new ItemListener() {
                StyledDocument doc = textWindow.getStyledDocument();
                int start = textWindow.getSelectionStart();
                int end = textWindow.getSelectionEnd();

                @Override
                public void itemStateChanged(ItemEvent event) {
                    if (start == end) {
                        // No selection, apply style to input attributes
                        SimpleAttributeSet attr = new SimpleAttributeSet();
                        StyleConstants.setUnderline(attr, event.getStateChange() == ItemEvent.SELECTED);
                        textWindow.setCharacterAttributes(attr, false);
                    } else {
                        // Apply font size to the selected text
                        MutableAttributeSet attr = new SimpleAttributeSet();
                        StyleConstants.setUnderline(attr, event.getStateChange() == ItemEvent.SELECTED);
                        doc.setCharacterAttributes(start, end - start, attr, false);
                    }
                }
            });
            formatManagingLayout.add(underlinedBox);

            JCheckBox strikethroughBox = new JCheckBox();
            strikethroughBox.setName("chk_strikethrough_");
            strikethroughBox.setText(guiLanguageDicitonary.get((String) strikethroughBox.getName())[currentLanguage]);
            strikethroughBox.addItemListener(new ItemListener() {
                StyledDocument doc = textWindow.getStyledDocument();
                int start = textWindow.getSelectionStart();
                int end = textWindow.getSelectionEnd();

                @Override
                public void itemStateChanged(ItemEvent event) {
                    if (start == end) {
                        // No selection, apply style to input attributes
                        SimpleAttributeSet attr = new SimpleAttributeSet();
                        StyleConstants.setStrikeThrough(attr, event.getStateChange() == ItemEvent.SELECTED);
                        textWindow.setCharacterAttributes(attr, false);
                    } else {
                        // Apply font size to the selected text
                        MutableAttributeSet attr = new SimpleAttributeSet();
                        StyleConstants.setStrikeThrough(attr, event.getStateChange() == ItemEvent.SELECTED);
                        doc.setCharacterAttributes(start, end - start, attr, false);
                    }
                }
            });
            formatManagingLayout.add(strikethroughBox);

            /*
             * Combo Box
             */

            JComboBox<String> textAlignComboBox = new JComboBox<String>();
            textAlignComboBox.setName("cbox_align_ cbox_align0_ cbox_align1_ cbox_align2_ cbox_align3_");
            String[] options = textAlignComboBox.getName().split(" ");
            for (String string : options) {
                textAlignComboBox.addItem(guiLanguageDicitonary.get(string)[currentLanguage]);
            }
            textAlignComboBox.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent event) {
                    if ((event.getStateChange() != ItemEvent.SELECTED) ||
                            (textAlignComboBox.getSelectedIndex() == 0)) {

                        return;
                    }

                    String alignmentStr = (String) event.getItem();
                    int newAlignment = textAlignComboBox.getSelectedIndex() - 1;
                    // New alignment is set based on these values defined in StyleConstants:
                    // ALIGN_LEFT 0, ALIGN_CENTER 1, ALIGN_RIGHT 2, ALIGN_JUSTIFIED 3
                    textAlignComboBox.setAction(new AlignmentAction(alignmentStr, newAlignment));
                    textAlignComboBox.setSelectedIndex(0); // initialize to (default) select

                }
            });

            formatManagingLayout.add(textAlignComboBox);

            JComboBox fontsBox = new JComboBox<String>(getEditorFonts());
            fontsBox.setSelectedItem(0);
            fontsBox.setRenderer(new FontFamilyBox(fontsBox));
            fontsBox.addItemListener(new ItemListener() {

                @Override
                public void itemStateChanged(ItemEvent e) {
                    StyledDocument doc = textWindow.getStyledDocument();
                    int start = textWindow.getSelectionStart();
                    int end = textWindow.getSelectionEnd();

                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        final String fontName = fontsBox.getSelectedItem().toString();

                        fontsBox.setFont(new Font(fontName, Font.PLAIN, 16));

                        if (start == end) {
                            // No selection, apply style to input attributes
                            SimpleAttributeSet attr = new SimpleAttributeSet();
                            StyleConstants.setFontFamily(attr, fontName);
                            textWindow.setCharacterAttributes(attr, false);
                        } else {
                            // Apply font size to the selected text
                            MutableAttributeSet attr = new SimpleAttributeSet();
                            StyleConstants.setFontFamily(attr, fontName);
                            doc.setCharacterAttributes(start, end - start, attr, false);
                        }
                    }
                }
            });
            fontsBox.setSelectedItem(0);
            fontsBox.getEditor().selectAll();

            formatManagingLayout.add(fontsBox);

            return formatManagingLayout;

        }

        // Took this from:
        // https://stackoverflow.com/questions/16461454/custom-font-for-jcombobox not
        // really sure how it works rn
        private class FontFamilyBox extends BasicComboBoxRenderer {

            private static final long serialVersionUID = 1L;
            private JComboBox<String> comboBox;
            final DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
            private int row;

            public FontFamilyBox(JComboBox<String> fontsBox) {
                comboBox = fontsBox;
            }

            private void manItemInCombo() {
                if (comboBox.getItemCount() > 0) {
                    final Object comp = comboBox.getUI().getAccessibleChild(comboBox, 0);
                    if ((comp instanceof JPopupMenu)) {
                        final JList list = new JList(comboBox.getModel());
                        final JPopupMenu popup = (JPopupMenu) comp;
                        final JScrollPane scrollPane = (JScrollPane) popup.getComponent(0);
                        final JViewport viewport = scrollPane.getViewport();
                        final Rectangle rect = popup.getVisibleRect();
                        final Point pt = viewport.getViewPosition();
                        row = list.locationToIndex(pt);
                    }
                }
            }

            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (list.getModel().getSize() > 0) {
                    manItemInCombo();
                }
                final JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, row,
                        isSelected, cellHasFocus);
                final Object fntObj = value;
                final String fontFamilyName = (String) fntObj;
                setFont(new Font(fontFamilyName, Font.PLAIN, 16));
                return this;
            }
        }

        private Vector<String> getEditorFonts() {

            String[] availableFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
            Vector<String> returnList = new Vector<>();

            for (String font : availableFonts) {

                if (FONT_LIST.contains(font)) {

                    returnList.add(font);
                }
            }

            return returnList;
        }

        public JPanel getJPanel() {
            return formatManagingLayout;
        }

    }

    /*
     * "Toolbox" for all UI's to follow certain appearances
     * For Example share styles between UIs:
     * - Buttons
     * - Dropdowns
     * - Sliders
     */
    interface GUI {
        public JPanel createPanel();

        public class DefaultButton extends BasicButtonUI {
            @Override
            public void paint(Graphics g, JComponent c) {
                AbstractButton b = (AbstractButton) c;
                ButtonModel model = b.getModel();
            }
        }
    }

    // Creates a Dictionary with Key-value Pairs
    // key en,de
    Map<String, String[]> guiLanguageDicitonary = new HashMap<>() {
        {
            // Type Name English, German
            put("btn_save_", new String[] { "SAVE", "SPEICHERN" });
            put("btn_open_", new String[] { "OPEN", "ÖFFNEN" });
            put("btn_quit_", new String[] { "QUIT", "SCHLIESEN" });
            put("btn_language_", new String[] { "LANGUAGE", "SPRACHE" });
            put("btn_undo_", new String[] {"UNDO", "ZURÜCK"});
            put("btn_redo_", new String[] {"REDO", "NOCHMALS"});

            put("lbl_formating_", new String[] { "Formating", "Formatieren" });
            put("btn_cut_", new String[] { "Cut", "Ausschneiden" });
            put("btn_copy_", new String[] { "Copy", "Kopieren" });
            put("btn_textColour_", new String[] { "Colour", "Farbe" });
            put("btn_textColourBG_", new String[] { "Text Background Colour", "Text Hintergrund Farbe" });
            put("btn_paste_", new String[] { "Paste", "Einfügen" });
            put("cbox_align_", new String[] { "Alignment", "Ausrichtung" });
            put("cbox_align0_", new String[] { "Align Left", "Linksbündig" });
            put("cbox_align1_", new String[] { "Align Center", "Zentriert" });
            put("cbox_align2_", new String[] { "Align Right", "Rechtsbündig" });
            put("cbox_align3_", new String[] { "Align Justified", "Blocksatz" });
            put("lbl_textSize_", new String[] { "Size", "Größe" });
            put("chk_bold_", new String[] { "Bold", "Fett" });
            put("chk_italic_", new String[] { "Italic", "Kursiv" });
            put("chk_underline_", new String[] { "Underline", "Unterstrichen" });
            put("chk_strikethrough_", new String[] { "Strikethrough", "Durchstreichen" });
        }
    };
}
