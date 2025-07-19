import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.management.JMException;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.text.*;
import javax.swing.text.DefaultEditorKit.*;
import javax.swing.text.StyledEditorKit.AlignmentAction;
import javax.swing.text.StyledEditorKit.FontSizeAction;
import javax.swing.text.rtf.RTFEditorKit;
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

    /*
     * 0 = English
     * 1 = German
     */
    String[] languages = { "English", "Deutsch" };
    int currentLanguage = 0;

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
            if (component instanceof JLabel) {
                JLabel myLabel = (JLabel) component;
                myLabel.setText(guiLanguageDicitonary.get(myLabel.getName())[currentLanguage]);
            } else if (component instanceof JComboBox) {
                JComboBox<String> myBox = (JComboBox<String>) component;
                String[] options = myBox.getName().split(" ");
                myBox.removeAllItems();
                for (String string : options) {
                    myBox.addItem(guiLanguageDicitonary.get(string)[currentLanguage]);
                }
                formatPanel.add(myBox);
            } else if (component instanceof JButton && component.getName() != null) {
                JButton myButton = (JButton) component;
                System.out.println(myButton.getName());
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

            return fileManagingLayout;
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

            

            return formatManagingLayout;

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
        }
    };
}
