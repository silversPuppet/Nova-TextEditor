import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit.AlignmentAction;


import java.awt.*;
import java.awt.event.*;

public class FormatingUI implements FileFormatManager.GUI{
    JPanel formatManagingLayout;

        public JPanel createPanel(Map<String, String[]> guiLanguageDicitonary, FileFormatManager fileFormatManager) {
            FileFormatManager.TextWindow textWindow = fileFormatManager.textWindow;
            formatManagingLayout = new JPanel();
            formatManagingLayout.setLayout(new BoxLayout(formatManagingLayout, BoxLayout.Y_AXIS));

            // Save Button Names in Array for easy language changes
            // TO DO FORMAT TO HEADING
            JLabel formatingLabel = new JLabel();
            formatingLabel.setName("lbl_formating_");
            formatingLabel.setText(guiLanguageDicitonary.get((String) formatingLabel.getName())[fileFormatManager.currentLanguage]);
            formatManagingLayout.add(formatingLabel);

            /*
             * Buttons
             */

            JButton cutButton = new JButton(new DefaultEditorKit.CutAction());
            cutButton.setName("btn_cut_");
            cutButton.setHideActionText(true);
            cutButton.setText(guiLanguageDicitonary.get((String) cutButton.getName())[fileFormatManager.currentLanguage]);
            formatManagingLayout.add(cutButton);

            JButton copyButton = new JButton(new DefaultEditorKit.CopyAction());
            copyButton.setName("btn_copy_");
            copyButton.setHideActionText(true);
            copyButton.setText(guiLanguageDicitonary.get((String) copyButton.getName())[fileFormatManager.currentLanguage]);
            formatManagingLayout.add(copyButton);

            JButton pasteButton = new JButton(new DefaultEditorKit.PasteAction());
            pasteButton.setHideActionText(true);
            pasteButton.setName("btn_paste_");
            pasteButton.setText(guiLanguageDicitonary.get((String) pasteButton.getName())[fileFormatManager.currentLanguage]);
            formatManagingLayout.add(pasteButton);

            JButton textColourFGButton = new JButton();
            textColourFGButton.setName("btn_textColour_");
            textColourFGButton.setHideActionText(true);
            textColourFGButton
                    .setText(guiLanguageDicitonary.get((String) textColourFGButton.getName())[fileFormatManager.currentLanguage]);
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
                    .setText(guiLanguageDicitonary.get((String) textColourBGButton.getName())[fileFormatManager.currentLanguage]);
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
            sliderLabel.setText(guiLanguageDicitonary.get((String) sliderLabel.getName())[fileFormatManager.currentLanguage]);
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
            boldBox.setText(guiLanguageDicitonary.get((String) boldBox.getName())[fileFormatManager.currentLanguage]);
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
            italicBox.setText(guiLanguageDicitonary.get((String) italicBox.getName())[fileFormatManager.currentLanguage]);
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
            underlinedBox.setText(guiLanguageDicitonary.get((String) underlinedBox.getName())[fileFormatManager.currentLanguage]);
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
            strikethroughBox.setText(guiLanguageDicitonary.get((String) strikethroughBox.getName())[fileFormatManager.currentLanguage]);
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
                textAlignComboBox.addItem(guiLanguageDicitonary.get(string)[fileFormatManager.currentLanguage]);
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

            JComboBox<String> fontsBox = new JComboBox<String>(fileFormatManager.getEditorFonts());
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

            JLabel counterLabel = new JLabel();
            counterLabel.setName("lbl_wordcounter_");
            counterLabel.setText(guiLanguageDicitonary.get((String) counterLabel.getName())[fileFormatManager.currentLanguage]);
            formatManagingLayout.add(counterLabel);

            JLabel wordCountLabel = new JLabel();
            wordCountLabel.setName("_dynamic_WordCounter_");
            wordCountLabel.setText("0");

            
            formatManagingLayout.add(wordCountLabel);

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

        

        public JPanel getJPanel() {
            return formatManagingLayout;
        }

}
