import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.util.Map;
import java.awt.event.*;


public class FileUI implements FileFormatManager.GUI{

    public JPanel createPanel(Map<String, String[]> guiLanguageDicitonary, FileFormatManager fileFormatManager) {
            JPanel fileManagingLayout = new JPanel();
            fileManagingLayout.setLayout(new BoxLayout(fileManagingLayout, BoxLayout.LINE_AXIS));

            JButton saveButton = new JButton();
            // Asign Key as name to retrive for language change
            saveButton.setName("btn_save_");
            saveButton.setText(guiLanguageDicitonary.get(saveButton.getName())[fileFormatManager.currentLanguage]);
            saveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    fileFormatManager.saveFile();
                }
            });

            fileManagingLayout.add(saveButton);

            JButton openButton = new JButton();
            openButton.setName("btn_open_");
            openButton.setText(guiLanguageDicitonary.get(openButton.getName())[fileFormatManager.currentLanguage]);
            openButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    fileFormatManager.openFile();
                }
            });

            fileManagingLayout.add(openButton);

            JButton quitButton = new JButton();
            quitButton.setName("btn_quit_");
            quitButton.setText(guiLanguageDicitonary.get(quitButton.getName())[fileFormatManager.currentLanguage]);
            quitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    fileFormatManager.quit();
                }
            });

            fileManagingLayout.add(quitButton);

            JButton languageButton = new JButton();
            languageButton.setName("btn_language_");
            languageButton.setText(guiLanguageDicitonary.get(languageButton.getName())[fileFormatManager.currentLanguage]);
            languageButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    JComboBox<String> jbox = new JComboBox<>(fileFormatManager.languages);

                    int result = JOptionPane.showConfirmDialog(
                            fileFormatManager.frame,
                            jbox,
                            "Select Language",
                            JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.PLAIN_MESSAGE);

                    if (result == JOptionPane.OK_OPTION) {

                        fileFormatManager.currentLanguage = jbox.getSelectedIndex();
                        fileFormatManager.updateLanguage();

                    }
                }
            });

            fileManagingLayout.add(languageButton);

            JButton undoButton = new JButton(fileFormatManager.undoAction);
            undoButton.setName("btn_undo_");
            undoButton.setText(guiLanguageDicitonary.get(undoButton.getName())[fileFormatManager.currentLanguage]);

            fileManagingLayout.add(undoButton);

            JButton redoButton = new JButton(fileFormatManager.redoAction);
            redoButton.setName("btn_redo_");
            redoButton.setText(guiLanguageDicitonary.get(redoButton.getName())[fileFormatManager.currentLanguage]);

            fileManagingLayout.add(redoButton);

            return fileManagingLayout;
        }
}
